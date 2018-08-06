package ykk.xc.com.xcwms.purchase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnLongClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.basics.Dept_DialogActivity;
import ykk.xc.com.xcwms.basics.Organization_DialogActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.basics.Supplier_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Organization;
import ykk.xc.com.xcwms.model.ScanningRecord;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.model.Supplier;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.pur.PurOrder;
import ykk.xc.com.xcwms.model.pur.PurReceiveOrder;
import ykk.xc.com.xcwms.purchase.adapter.Pur_InAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;

public class Pur_InActivity extends BaseActivity {

    @BindView(R.id.lin_tabs)
    LinearLayout linTabs;
    @BindView(R.id.lin_tab1)
    LinearLayout linTab1;
    @BindView(R.id.lin_tab2)
    LinearLayout linTab2;
    @BindView(R.id.lin_tab3)
    LinearLayout linTab3;
    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.viewRadio3)
    View viewRadio3;
    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.btn_maker_code)
    Button btnMakerCode;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.tv_smName)
    TextView tvSmName;
    @BindView(R.id.et_whName)
    EditText etWhName;
    @BindView(R.id.btn_whName)
    Button btnWhName;
    @BindView(R.id.et_whPos)
    EditText etWhPos;
    @BindView(R.id.btn_whPos)
    Button btnWhPos;
    @BindView(R.id.et_deptName)
    EditText etDeptName;
    @BindView(R.id.btn_deptName)
    Button btnDeptName;
    @BindView(R.id.et_matNo)
    EditText etMatNo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_clone)
    Button btnClone;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.tv_orderTypeSel)
    TextView tvOrderTypeSel;
    @BindView(R.id.tv_operationTypeSel)
    TextView tvOperationTypeSel;
    @BindView(R.id.tv_receiveOrg)
    TextView tvReceiveOrg;
    @BindView(R.id.tv_purOrg)
    TextView tvPurOrg;
    @BindView(R.id.tv_purDate)
    TextView tvPurDate;
    @BindView(R.id.tv_purMan)
    TextView tvPurMan;

    private Pur_InActivity context = this;
    private static final int SEL_SUPPLIER = 10, SEL_STOCK = 11, SEL_STOCKP = 12, SEL_DEPT = 13, SEL_ORG = 14, SEL_ORG2 = 15;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int CODE1 = 1, CODE2 = 2, CODE20 = 20;
    private Supplier supplier; // 供应商
    private Material mtl;
    private Stock stock; // 仓库
    private StockPosition stockP; // 库位
    private Department department; // 部门
    private Organization receiveOrg, purOrg; // 组织
    private Pur_InAdapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String stockBarcode, stockPBarcode, deptBarcode, mtlBarcode, purOrderBarcode, recOrderBarcode; // 对应的条码号
    private char dataType = '1'; // 1：物料扫码，2：采购订单，3：收料订单
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：部门， 4：物料，5：采购订单，6：收料订单
    private int curPos; // 当前行
    private View curRadio; // 当前扫码的 View
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;

    // 消息处理
    private Pur_InActivity.MyHandler mHandler = new Pur_InActivity.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_InActivity> mActivity;

        public MyHandler(Pur_InActivity activity) {
            mActivity = new WeakReference<Pur_InActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_InActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset('0');

                        m.checkDatas.clear();
                        m.getBarCodeTableAfter(true);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.context,"保存成功");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.context,"服务器繁忙，请稍候再试！");

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = null;
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.stock = JsonUtil.stringToObject(bt.getRelationObj(), Stock.class);
                                m.getStockAfter();

                                break;
                            case '2': // 库位
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.stockP = JsonUtil.stringToObject(bt.getRelationObj(), StockPosition.class);
                                m.getStockPAfter();

                                break;
                            case '3': // 部门
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.department = JsonUtil.stringToObject(bt.getRelationObj(), Department.class);
                                m.getDeptAfter();

                                break;
                            case '4': // 物料
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.mtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
                                bt.setMtl(m.mtl);
                                // 扫码成功后，判断必填项是否已经输入了值
                                if(!m.getMtlAfter(bt)) return;
                                // 禁用部分控件
                                m.getBarCodeTableAfter(false);
                                if(!m.getBarCodeTableAfterSon(bt)) return;
                                m.getBarCodeTableAfter_mtl(bt);

                                break;
                            case '5': // 采购订单
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                // 扫码成功后，判断必填项是否已经输入了值
                                if(!m.getMtlAfter(bt)) return;
                                // 禁用部分控件
                                m.getBarCodeTableAfter(false);
                                if(!m.getBarCodeTableAfterSon2(bt)) return;
                                m.getBarCodeTableAfter_purOrder(bt);

                                break;
                            case '6': // 收料订单
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                // 扫码成功后，判断必填项是否已经输入了值
                                if(!m.getMtlAfter(bt)) return;
                                // 禁用部分控件
                                m.getBarCodeTableAfter(false);
                                if(!m.getBarCodeTableAfterSon2(bt)) return;
                                m.getBarCodeTableAfter_recOrder(bt);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(CODE20, 200);
                        Comm.showWarnDialog(m.context,"很抱歉，没能找到数据！");

                        break;
                    case CODE20: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                m.setTexts(m.etWhName, m.stockBarcode);
                                break;
                            case '2': // 库位
                                m.setTexts(m.etWhPos, m.stockPBarcode);
                                break;
                            case '3': // 部门
                                m.setTexts(m.etMatNo, m.deptBarcode);
                                break;
                            case '4': // 物料
                                m.setTexts(m.etMatNo, m.mtlBarcode);
                                break;
                            case '5': // 采购订单
                                m.setTexts(m.etMatNo, m.purOrderBarcode);
                                break;
                            case '6': // 收料订单
                                m.setTexts(m.etMatNo, m.recOrderBarcode);
                                break;
                        }

                        break;
                    case SUCC3: // 判断是否存在返回
                        String strBarcode = JsonUtil.strToString((String) msg.obj);
                        String[] barcodeArr = strBarcode.split(",");
                        for (int i = 0, len = barcodeArr.length; i < len; i++) {
                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
                                // 判断扫码表和当前扫的码对比是否一样
                                if (barcodeArr[i].equals(m.checkDatas.get(j).getBarcode())) {
                                    Comm.showWarnDialog(m.context,"第" + (i + 1) + "行已入库，不能重复操作！");
                                    return;
                                }
                            }
                        }

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                    case CODE1: // 清空数据
                        m.etMatNo.setText("");
                        m.mtlBarcode = null;
                        m.purOrderBarcode = null;
                        m.recOrderBarcode = null;

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.pur_in;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Pur_InAdapter(context, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Pur_InAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", CODE2);
            }

            @Override
            public void onClick_del(ScanningRecord2 entity, int position) {
                Log.e("del", "行：" + position);
                checkDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(etMatNo);
        hideSoftInputMode(etWhName);
        hideSoftInputMode(etWhPos);
        hideSoftInputMode(etDeptName);
        getUserInfo();
        curRadio = viewRadio1;
        tvPurDate.setText(Comm.getSysDate(7));
        setFocusable(etMatNo); // 物料代码获取焦点
    }

    @OnClick({R.id.btn_close, R.id.btn_maker_code, R.id.lin_tab1, R.id.lin_tab2, R.id.lin_tab3, R.id.tv_custSel, R.id.btn_whName, R.id.btn_whPos, R.id.btn_save, R.id.btn_clone,
            R.id.tv_orderTypeSel, R.id.tv_receiveOrg, R.id.tv_purOrg, R.id.tv_purDate, R.id.tv_purMan, R.id.btn_deptName})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.lin_tab1:
                dataType = '1';
                tabSelected(viewRadio1);
                tvSmName.setText("物料");
                resetSon2();

                break;
            case R.id.lin_tab2:
                dataType = '2';
                tabSelected(viewRadio2);
                tvSmName.setText("采购订单");
                resetSon2();

                break;
            case R.id.lin_tab3:
                dataType = '3';
                tabSelected(viewRadio3);
                tvSmName.setText("收料订单");
                resetSon2();

                break;
            case R.id.tv_orderTypeSel: // 订单类型

                break;
            case R.id.btn_maker_code: // 打印条码界面
//                show(PrintBarcodeActivity.class, null);

                break;
            case R.id.tv_custSel: // 选择供应商
                showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, null);

                break;
            case R.id.btn_whName: // 选择仓库
                isStockLong = false;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
            case R.id.btn_whPos: // 选择库位
                if (stock == null) {
                    Comm.showWarnDialog(context,"请先选择仓库！");
                    return;
                }
                bundle = new Bundle();
//                bundle.putInt("areaId", stockA.getId());
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);

                break;
            case R.id.btn_deptName: // 选择部门
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

                break;
            case R.id.tv_receiveOrg: // 收料组织
                showForResult(Organization_DialogActivity.class, SEL_ORG, null);

                break;
            case R.id.tv_purOrg: // 采购组织
                showForResult(Organization_DialogActivity.class, SEL_ORG2, null);

                break;
            case R.id.tv_purDate: // 入库日期
                Comm.showDateDialog(context, view, 0);
                break;
            case R.id.tv_purMan: // 选择业务员

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                run_findMatIsExistList2();
//                run_addScanningRecord();

                break;
            case R.id.btn_clone: // 重置
                hideKeyboard(getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(context);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetSon();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    resetSon();
                }

                break;
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    /**
     * 选择来源单之前的判断
     */
    private boolean smBefore() {
        if(dataType == '1' && supplier == null) {
            Comm.showWarnDialog(context, "请选择供应商！");
            return false;
        }
        if (stock == null) {
            Comm.showWarnDialog(context,"请选择仓库！");
            return false;
        }
        if (stock.isStorageLocation() && stockP == null) {
            Comm.showWarnDialog(context,"请选择库位！");
            return false;
        }
        if(dataType == '1' && receiveOrg == null) {
            Comm.showWarnDialog(context,"请选择收料组织！");
            return false;
        }
        if(dataType == '1' && purOrg == null) {
            Comm.showWarnDialog(context,"请选择采购组织！");
            return false;
        }
        return true;
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(context,"请先插入行！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
//            if (sr2.getMtl().getIsBatchManager() == 1 && sr2.getBatchno().length() == 0) {
//                Comm.showWarnDialog(context,"第" + (i + 1) + "行请输入（批号）！");
//                return false;
//            }
//            if (sr2.getMtl().getIsSnManager() == 1 && sr2.getSequenceNo().length() == 0) {
//                Comm.showWarnDialog(context,"第" + (i + 1) + "行请输入（序列号）！");
//                return false;
//            }
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
            if (sr2.getStockqty() > sr2.getFqty()) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（实收数）不能大于（应收数）！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_whName, R.id.et_whPos, R.id.et_matNo, R.id.et_deptName})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
    }

    @OnLongClick({R.id.btn_whName})
    public boolean onViewLongClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_whName: // 长按选择仓库
                isStockLong = true;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
        }
        return true;
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_whName: // 仓库
                            String whName = getValues(etWhName).trim();
                            if (isKeyDownEnter(whName, event, keyCode)) {
                                if (stockBarcode != null && stockBarcode.length() > 0) {
                                    String tmp = whName.replaceFirst(stockBarcode, "");
                                    stockBarcode = tmp.replace("\n", "");
                                } else {
                                    stockBarcode = whName.replace("\n", "");
                                }
                                curViewFlag = '1';
                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_whPos: // 库位
                            String whPos = getValues(etWhPos).trim();
                            if (isKeyDownEnter(whPos, event, keyCode)) {
                                if (stockPBarcode != null && stockPBarcode.length() > 0) {
                                    String tmp = whPos.replaceFirst(stockPBarcode, "");
                                    stockPBarcode = tmp.replace("\n", "");
                                } else {
                                    stockPBarcode = whPos.replace("\n", "");
                                }
                                curViewFlag = '2';
                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_deptName: // 部门
                            String deptName = getValues(etDeptName).trim();
                            if (isKeyDownEnter(deptName, event, keyCode)) {
                                if (deptBarcode != null && deptBarcode.length() > 0) {
                                    String tmp = deptName.replaceFirst(deptBarcode, "");
                                    deptBarcode = tmp.replace("\n", "");
                                } else {
                                    deptBarcode = deptName.replace("\n", "");
                                }
                                curViewFlag = '3';
                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_matNo: // 物料
                            String matNo = getValues(etMatNo).trim();
//                            if (!smBefore()) { // 扫码之前的判断
//                                mHandler.sendEmptyMessageDelayed(CODE1, 200);
//                                return false;
//                            }
                            if (isKeyDownEnter(matNo, event, keyCode)) {
                                switch (dataType){
                                    case '1': // 物料
                                        isKeyDownEnterSon(matNo, mtlBarcode, '4');

                                        break;
                                    case '2': // 采购订单
                                        isKeyDownEnterSon(matNo, purOrderBarcode, '5');

                                        break;
                                    case '3': // 收料订单
                                        isKeyDownEnterSon(matNo, recOrderBarcode, '6');

                                        break;
                                }
//                                // 执行查询方法
//                                run_smGetDatas();
                            }
                            break;
                    }
                }
                return false;
            }
        };
        etWhName.setOnKeyListener(keyListener);
        etWhPos.setOnKeyListener(keyListener);
        etMatNo.setOnKeyListener(keyListener);
    }

    /**
     * 是否按了回车键
     */
    private boolean isKeyDownEnter(String val, KeyEvent event, int keyCode) {
        if (val.length() > 0 && event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            return true;
        }
        return false;
    }
    private void isKeyDownEnterSon(String matNo, String barcode, char viewFlag) {
        if (barcode != null && barcode.length() > 0) {
            String tmp = matNo.replaceFirst(barcode, "");
            barcode = tmp.replace("\n", "");
        } else {
            barcode = matNo.replace("\n", "");
        }
        curViewFlag = viewFlag;
        switch (viewFlag) {
            case '4': // 物料
                mtlBarcode = barcode;
                break;
            case '5': // 采购订单
                purOrderBarcode = barcode;
                break;
            case '6': // 收料订单
                recOrderBarcode = barcode;
                break;
        }

        // 执行查询方法
        run_smGetDatas();
    }

    /**
     * 0：重置全部，1：重置物料部分
     *
     * @param flag
     */
    private void reset(char flag) {
        // 清空物料信息
        etMatNo.setText(""); // 物料代码

        setEnables(tvCustSel, R.drawable.back_style_blue,true);
        setEnables(tvReceiveOrg, R.drawable.back_style_blue,true);
        setEnables(tvPurOrg, R.drawable.back_style_blue,true);
    }

    private void resetSon() {
        getBarCodeTableAfter(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvCustSel.setText("");
        etWhName.setText("");
        etWhPos.setText("");
        etDeptName.setText("");
        tvReceiveOrg.setText("");
        tvPurOrg.setText("");
        supplier = null;
        stock = null;
        stockP = null;
        department = null;
        receiveOrg = null;
        purOrg = null;
        dataType = '1';
        curViewFlag = '1';
        stockBarcode = null;
        stockPBarcode = null;
        mtlBarcode = null;
        purOrderBarcode = null;
        recOrderBarcode = null;
        tvPurDate.setText(Comm.getSysDate(7));
    }
    private void resetSon2() {
        etMatNo.setText("");
        mtlBarcode = null;
        purOrderBarcode = null;
        recOrderBarcode = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_SUPPLIER: //查询供应商	返回
                if (resultCode == RESULT_OK) {
                    supplier = (Supplier) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_SUPPLIER", supplier.getfName());
                    if (supplier != null) {
                        tvCustSel.setText(supplier.getfName());
                    }
                }

                break;
            case SEL_STOCK: //查询仓库	返回
                if (resultCode == RESULT_OK) {
                    Stock stock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCK", stock.getfName());
                    if (this.stock != null && stock != null && stock.getId() == this.stock.getId()) {
                        // 长按了，并且启用了库区管理
                        if (isStockLong && stock.isStorageLocation()) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("stockId", stock.getfStockid());
                            showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);
                        }
                        return;
                    }
                    this.stock = stock;
                    getStockAfter();
                }

                break;
            case SEL_STOCKP: //查询库位	返回
                if (resultCode == RESULT_OK) {
                    stockP = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP", stockP.getFname());
                    getStockPAfter();
                }

                break;
            case SEL_ORG: //查询收料组织   	返回
                if (resultCode == RESULT_OK) {
                    receiveOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG", receiveOrg.getName());
                    getOrgAfter();
                }

                break;
            case SEL_ORG2: //查询采购组织   	返回
                if (resultCode == RESULT_OK) {
                    purOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG2", purOrg.getName());
                    getOrg2After();
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    getDeptAfter();
                }

                break;
            case CODE2: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setStockqty(num);
                        checkDatas.get(curPos).setFqty(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /**
     * 得到物料数据之后，判断库位是否为空
     */
    private boolean getMtlAfter(BarCodeTable barCodeTable) {
        Material mtl = barCodeTable.getMtl();
        if(mtl.getStockPos() != null && mtl.getStockPos().getStockId() > 0) {
            stock = mtl.getStock();
            stockP = mtl.getStockPos();
            setTexts(etWhName, stock.getfName());
            setTexts(etWhPos, stockP.getFname());
            stockBarcode = stock.getfName();
            stockPBarcode = stockP.getFname();
        } else {
            switch (dataType) {
                case '1': // 物料
                    setTexts(etMatNo, mtlBarcode);
                    break;
                case '2': // 采购订单
                    setTexts(etMatNo, purOrderBarcode);
                    break;
                case '3': // 收料订单
                    setTexts(etMatNo, recOrderBarcode);
                    break;
            }
            return smBefore();
        }
        return true;
    }

    /**
     * 得到条码表的数据，禁用部分控件
     */
    private void getBarCodeTableAfter(boolean isEnable) {
        linTab1.setEnabled(isEnable);
        linTab2.setEnabled(isEnable);
        linTab3.setEnabled(isEnable);
        if(isEnable) {
            setEnables(tvCustSel, R.drawable.back_style_blue,true);
            setEnables(tvReceiveOrg, R.drawable.back_style_blue, true);
            setEnables(tvPurOrg, R.drawable.back_style_blue, true);
            linTabs.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            setEnables(tvCustSel, R.drawable.back_style_gray3,false);
            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
            setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
            linTabs.setBackgroundColor(Color.parseColor("#EAEAEA"));
        }
    }

    /**
     * 物料判断
     */
    private boolean getBarCodeTableAfterSon(BarCodeTable bt) {
        int size = checkDatas.size();
        // 判断重复
        if(size > 0) {
            for (int i = 0; i < size; i++) {
                ScanningRecord2 sr2 = checkDatas.get(i);
                // 如果扫码相同
                if (mtlBarcode.equals(sr2.getBarcode())) {
                    if(sr2.getMtl().getIsSnManager() == 0) { // 未启用序列号
                        // 应收数量大于实收数量
                        sr2.setFqty(sr2.getStockqty() + 1);
                        sr2.setStockqty(sr2.getStockqty() + 1);
                        sr2.setPoFmustqty(sr2.getStockqty() + 1);
                        mAdapter.notifyDataSetChanged();
                    } else { // 启用序列号
                        Comm.showWarnDialog(context, "第"+(i+1)+"行！，已有相同的数据！");
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 来源订单 判断数据
     */
    private boolean getBarCodeTableAfterSon2(BarCodeTable bt) {
        int size = checkDatas.size();
        String tmpBarcode = null;
        // 判断供应商是否一样
        if(size > 0) {
            ScanningRecord2 sr2 = checkDatas.get(0);
            int sourceId = 0;

            switch (dataType) {
                case '2': // 采购订单
                    PurOrder purOrder = JsonUtil.stringToObject(bt.getRelationObj(), PurOrder.class);
                    sourceId = purOrder.getSupplierId();
                    tmpBarcode = purOrderBarcode;
                    break;
                case '3': // 收料订单
                    PurReceiveOrder recOrder = JsonUtil.stringToObject(bt.getRelationObj(), PurReceiveOrder.class);
                    sourceId = recOrder.getSupplierId();
                    tmpBarcode = recOrderBarcode;
                    break;

            }
            if(sr2.getSupplierId() != sourceId) {
                Comm.showWarnDialog(context, "供应商不一致，不能入库！");
                return false;
            }
        }

        for (int i = 0; i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);

            // 如果扫码相同
            if (tmpBarcode.equals(sr2.getBarcode())) {
                // 未启用序列号
                if (sr2.getMtl().getIsSnManager() == 0) {
                    if (sr2.getFqty() > sr2.getStockqty()) {
                        // 没有启用序列号，并且应发数量大于实发数量
                        sr2.setStockqty(sr2.getStockqty() + 1);
                        mAdapter.notifyDataSetChanged();
                        setTexts(etMatNo, tmpBarcode);
                        return false;
                    } else {
                        // 数量已满
                        Comm.showWarnDialog(context, "第" + (i + 1) + "行！，实收数不能大于应收数！");
                        return false;
                    }
                } else {
                    // 启用序列号
                    Comm.showWarnDialog(context, "第"+(i+1)+"行！，已有相同的数据！");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 得到条码表的数据 （物料）
     */
    private void getBarCodeTableAfter_mtl(BarCodeTable barCodeTable) {
        setTexts(etMatNo, mtlBarcode);

        ScanningRecord2 sr2 = new ScanningRecord2();
//        sr2.setSourceFinterId(barCodeTable.getRelationBillId());
//        sr2.setSourceFnumber(barCodeTable.getRelationBillNumber());
        sr2.setFitemId(barCodeTable.getMaterialId());
//        sr2.setSupplierId(supplier.getFsupplierid());
//        sr2.setSupplierName(supplier.getfName());
//        sr2.setSupplierFnumber(supplier.getfNumber());
        sr2.setStockId(stock.getfStockid());
        sr2.setStock(stock);
        sr2.setStockFnumber(stock.getfNumber());
//        sr2.setStockAreaId(stockA.getId());
//        sr2.setStockAName(stockA.getFname());
        sr2.setStockPositionId(stockP.getId());
        sr2.setStockPName(stockP.getFname());

        if(supplier != null) {
            sr2.setSupplierId(supplier.getFsupplierid());
            sr2.setSupplierName(supplier.getfName());
            sr2.setSupplierFnumber(supplier.getfNumber());
        }
        if (stock != null) {
            sr2.setStockId(stock.getfStockid());
            sr2.setStock(stock);
            sr2.setStockFnumber(stock.getfNumber());
        }
        if (stockP != null) {
            sr2.setStockPositionId(stockP.getId());
            sr2.setStockPName(stockP.getFname());
        }
        if (department != null) {
            sr2.setEmpId(department.getFitemID()); // 部门
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }
        // 收料组织
        if(receiveOrg != null) {
            sr2.setReceiveOrgFnumber(receiveOrg.getNumber());
//            tvReceiveOrg.setText(receiveOrg.getName());
            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
        }
        // 采购组织
        if(purOrg != null) {
            sr2.setPurOrgFnumber(purOrg.getNumber());
            tvPurOrg.setText(purOrg.getName());
            setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
        }

        sr2.setMtl(barCodeTable.getMtl());
        sr2.setMtlFnumber(barCodeTable.getMtl().getfNumber());
        sr2.setUnitFnumber(barCodeTable.getMtl().getUnit().getUnitNumber());
        Material mtl = barCodeTable.getMtl();
        if(mtl.getIsBatchManager() > 0) {
            sr2.setBatchno(barCodeTable.getBatchCode());
        }
        if(mtl.getIsSnManager() > 0) {
            sr2.setSequenceNo(barCodeTable.getSnCode());
        }
        if (department != null) {
            sr2.setEmpId(department.getFitemID());
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }

        sr2.setFqty(1);
        sr2.setStockqty(1);
        sr2.setPoFid(0);
        sr2.setEntryId(0);
        sr2.setPoFbillno("");
        sr2.setPoFmustqty(1);
        sr2.setBarcode(barCodeTable.getBarcode());

        checkDatas.add(sr2);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 得到条码表的数据 （采购订单）
     */
    private void getBarCodeTableAfter_purOrder(BarCodeTable barCodeTable) {
        setTexts(etMatNo, purOrderBarcode);

        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setSourceFinterId(barCodeTable.getRelationBillId());
        sr2.setSourceFnumber(barCodeTable.getRelationBillNumber());
        sr2.setFitemId(barCodeTable.getMaterialId());
        sr2.setStockId(stock.getfStockid());
        sr2.setStock(stock);
        sr2.setStockFnumber(stock.getfNumber());
        sr2.setStockPositionId(stockP.getId());
        sr2.setStockPName(stockP.getFname());
        // 得到采购订单
        PurOrder purOrder = JsonUtil.stringToObject(barCodeTable.getRelationObj(), PurOrder.class);
        sr2.setReceiveOrgFnumber(purOrder.getReceiveOrgNumber());
        sr2.setPurOrgFnumber(purOrder.getPurOrgNumber());
        if(supplier == null) supplier = new Supplier();
        supplier.setFsupplierid(purOrder.getSupplierId());
        supplier.setfNumber(purOrder.getSupplierNumber());
        supplier.setfName(purOrder.getSupplierName());
        setEnables(tvCustSel, R.drawable.back_style_gray3, false);
        tvCustSel.setText(purOrder.getSupplierName());
        sr2.setSupplierId(supplier.getFsupplierid());
        sr2.setSupplierName(supplier.getfName());
        sr2.setSupplierFnumber(supplier.getfNumber());
        // 收料组织
        if(receiveOrg == null) receiveOrg = new Organization();
        receiveOrg.setFpkId(purOrder.getReceiveOrgId());
        receiveOrg.setNumber(purOrder.getReceiveOrgNumber());
        receiveOrg.setName(purOrder.getReceiveOrgName());

        setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
        tvReceiveOrg.setText(receiveOrg.getName());
        // 采购组织
        if(purOrg == null) purOrg = new Organization();
        purOrg.setFpkId(purOrder.getPurOrgId());
        purOrg.setNumber(purOrder.getPurOrgNumber());
        purOrg.setName(purOrder.getPurOrgName());

        setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
        tvPurOrg.setText(purOrg.getName());

        sr2.setMtl(barCodeTable.getMtl());
        sr2.setMtlFnumber(barCodeTable.getMtl().getfNumber());
        sr2.setUnitFnumber(barCodeTable.getMtl().getUnit().getUnitNumber());
        Material mtl = barCodeTable.getMtl();
        if(mtl.getIsBatchManager() > 0) {
            sr2.setBatchno(barCodeTable.getBatchCode());
        }
        if(mtl.getIsSnManager() > 0) {
            sr2.setSequenceNo(barCodeTable.getSnCode());
        }
        if (department != null) {
            sr2.setEmpId(department.getFitemID());
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }
        sr2.setFqty(purOrder.getPoFqty());
        sr2.setStockqty(1);
        sr2.setPoFid(purOrder.getfId());
        sr2.setEntryId(purOrder.getEntryId());
        sr2.setPoFbillno(purOrder.getFbillno());
        sr2.setPoFmustqty(purOrder.getPoFqty());
        sr2.setBarcode(barCodeTable.getBarcode());

        checkDatas.add(sr2);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 得到条码表的数据 （收料订单）
     */
    private void getBarCodeTableAfter_recOrder(BarCodeTable barCodeTable) {
        setTexts(etMatNo, recOrderBarcode);

        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setSourceFinterId(barCodeTable.getRelationBillId());
        sr2.setSourceFnumber(barCodeTable.getRelationBillNumber());
        sr2.setFitemId(barCodeTable.getMaterialId());
        sr2.setStockId(stock.getfStockid());
        sr2.setStock(stock);
        sr2.setStockFnumber(stock.getfNumber());
        sr2.setStockPositionId(stockP.getId());
        sr2.setStockPName(stockP.getFname());
        // 得到收料订单
        PurReceiveOrder recOrder = JsonUtil.stringToObject(barCodeTable.getRelationObj(), PurReceiveOrder.class);
        sr2.setReceiveOrgFnumber(recOrder.getRecOrgNumber());
        sr2.setPurOrgFnumber(recOrder.getPurOrgNumber());
        if(supplier == null) supplier = new Supplier();
        supplier.setFsupplierid(recOrder.getSupplierId());
        supplier.setfNumber(recOrder.getSupplierNumber());
        supplier.setfName(recOrder.getSupplierName());
        setEnables(tvCustSel, R.drawable.back_style_gray3, false);
        tvCustSel.setText(recOrder.getSupplierName());
        sr2.setSupplierId(supplier.getFsupplierid());
        sr2.setSupplierName(supplier.getfName());
        sr2.setSupplierFnumber(supplier.getfNumber());
        // 收料组织
        if(receiveOrg == null) receiveOrg = new Organization();
        receiveOrg.setFpkId(recOrder.getRecOrgId());
        receiveOrg.setNumber(recOrder.getRecOrgNumber());
        receiveOrg.setName(recOrder.getRecOrgName());

        setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
        tvReceiveOrg.setText(receiveOrg.getName());
        // 采购组织
        if(purOrg == null) purOrg = new Organization();
        purOrg.setFpkId(recOrder.getPurOrgId());
        purOrg.setNumber(recOrder.getPurOrgNumber());
        purOrg.setName(recOrder.getPurOrgName());

        setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
        tvPurOrg.setText(purOrg.getName());

        sr2.setMtl(barCodeTable.getMtl());
        sr2.setMtlFnumber(barCodeTable.getMtl().getfNumber());
        sr2.setUnitFnumber(barCodeTable.getMtl().getUnit().getUnitNumber());
        Material mtl = barCodeTable.getMtl();
        if(mtl.getIsBatchManager() > 0) {
            sr2.setBatchno(barCodeTable.getBatchCode());
        }
        if(mtl.getIsSnManager() > 0) {
            sr2.setSequenceNo(barCodeTable.getSnCode());
        }
        if (department != null) {
            sr2.setEmpId(department.getFitemID());
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }
        sr2.setFqty(recOrder.getRecFqty());
        sr2.setStockqty(1);
        sr2.setPoFid(recOrder.getfId());
        sr2.setEntryId(recOrder.getEntryId());
        sr2.setPoFbillno(recOrder.getFbillno());
        sr2.setPoFmustqty(recOrder.getRecFqty());
        sr2.setBarcode(barCodeTable.getBarcode());

        checkDatas.add(sr2);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择（仓库）返回的值
     */
    private void getStockAfter() {
        if (stock != null) {
            setTexts(etWhName, stock.getfName());
            stockBarcode = stock.getfName();
            stockP = null;
            etWhPos.setText("");
            // 启用库位
            if (stock.isStorageLocation()) {
                setEnables(etWhPos, R.drawable.back_style_blue4, true);
                setEnables(btnWhPos, R.drawable.btn_blue3_selector, true);
            } else {
                stockP = null;
                etWhPos.setText("");
                setEnables(etWhPos, R.drawable.back_style_gray5, false);
                setEnables(btnWhPos, R.drawable.back_style_gray6, false);
            }
            // 长按了，并且启用了库位管理
            if (isStockLong && stock.isStorageLocation()) {
                Bundle bundle = new Bundle();
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);
            }
        }
    }

    /**
     * 选择（库位）返回的值
     */
    private void getStockPAfter() {
        if (stockP != null) {
            setTexts(etWhPos, stockP.getFname());
            stockPBarcode = stockP.getFname();
            setFocusable(etMatNo);
        }
    }

    /**
     * 选择（部门）返回的值
     */
    private void getDeptAfter() {
        if (department != null) {
            setTexts(etDeptName, department.getDepartmentName());
            deptBarcode = department.getDepartmentName();
        }
    }

    /**
     * 选择（收料组织）返回的值
     */
    private void getOrgAfter() {
        if (receiveOrg != null) {
            tvReceiveOrg.setText(receiveOrg.getName());
        }
    }

    /**
     * 选择（采购组织）返回的值
     */
    private void getOrg2After() {
        if (purOrg != null) {
            tvPurOrg.setText(purOrg.getName());
        }
    }

    /**
     * 保存方法
     */
    private void run_addScanningRecord() {
        showLoadDialog("保存中...");
        getUserInfo();

        List<ScanningRecord> list = new ArrayList<>();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            ScanningRecord record = new ScanningRecord();
            // type: 1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库
            record.setType(1);
            record.setSourceK3Id(sr2.getSourceFinterId());
            record.setSourceFnumber(sr2.getSourceFnumber());
            record.setMtlK3Id(sr2.getFitemId());
            record.setMtlFnumber(sr2.getMtlFnumber());
            record.setUnitFnumber(sr2.getUnitFnumber());
            record.setStockK3Id(sr2.getStockId());
            record.setStockFnumber(sr2.getStockFnumber());
            record.setStockAreaId(sr2.getStockAreaId());
            record.setStockPositionId(sr2.getStockPositionId());
            record.setSupplierK3Id(sr2.getSupplierId());
            record.setSupplierFnumber(sr2.getSupplierFnumber());
            record.setReceiveOrgFnumber(sr2.getReceiveOrgFnumber());
            record.setPurOrgFnumber(sr2.getPurOrgFnumber());
            record.setCustomerK3Id(0);
            record.setPoFid(sr2.getPoFid());
            record.setEntryId(sr2.getEntryId());
            record.setPoFbillno(sr2.getPoFbillno());
            record.setPoFmustqty(sr2.getPoFmustqty());

            if (department != null) {
                record.setDepartmentK3Id(department.getFitemID());
                record.setDepartmentFnumber(department.getDepartmentNumber());
            }
            record.setPdaRowno((i+1));
            record.setBatchNo(sr2.getBatchno());
            record.setSequenceNo(sr2.getSequenceNo());
            record.setBarcode(sr2.getBarcode());
            record.setFqty(sr2.getStockqty());
            record.setFdate(getValues(tvPurDate));
            record.setPdaNo("");
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());

            list.add(record);
        }

        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = Consts.getURL("addScanningRecord");
        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
//                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Log.e("run_addScanningRecord --> onResponse", result);
                mHandler.sendEmptyMessage(SUCC1);
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        int caseId = 0;
        switch (curViewFlag) {
            case '1':
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockBarcode;
                isStockLong = false;
                caseId = 12;
                break;
            case '2':
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockPBarcode;
                caseId = 14;
                break;
            case '3':
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockPBarcode;
                caseId = 15;
                break;
            case '4': // 物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                caseId = 11;
                break;
            case '5': // 采购订单
                mUrl = Consts.getURL("barCodeTable/findBarcode3ByParam");
                barcode = purOrderBarcode;
                caseId = 31;
                break;
            case '6': // 收料订单
                mUrl = Consts.getURL("barCodeTable/findBarcode3ByParam");
                barcode = recOrderBarcode;
                caseId = 36;
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("caseId", String.valueOf(caseId))
                .add("barcode", barcode)
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC2);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
                Log.e("run_smGetDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 判断表中存在该物料
     */
    private void run_findMatIsExistList2() {
        showLoadDialog("加载中...");
        StringBuilder strBarcode = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if(isNULLS(sr2.getBarcode()).length() > 0) {
                if((i+1) == size) strBarcode.append(sr2.getBarcode());
                else strBarcode.append(sr2.getBarcode() + ",");
            }
        }
        String mUrl = Consts.getURL("findMatIsExistList2");
        FormBody formBody = new FormBody.Builder()
                .add("orderType", "CG") // 单据类型CG代表采购订单，XS销售订单,生产PD
                .add("strBarcode", strBarcode.toString())
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC3);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
                Log.e("run_findMatIsExistList2 --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) {
            user = showObjectToXml(User.class, getResStr(R.string.saveUser));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeHandler(mHandler);
            context.finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        closeHandler(mHandler);
        super.onDestroy();
    }
}
