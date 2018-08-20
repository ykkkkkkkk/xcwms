package ykk.xc.com.xcwms.purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
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
import ykk.xc.com.xcwms.basics.Material_ListActivity;
import ykk.xc.com.xcwms.basics.Organization_DialogActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.basics.Supplier_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseFragment;
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
import ykk.xc.com.xcwms.model.pur.PurReceiveOrder;
import ykk.xc.com.xcwms.model.pur.PurReceiveOrder;
import ykk.xc.com.xcwms.purchase.adapter.Pur_InFragment3Adapter;
import ykk.xc.com.xcwms.purchase.adapter.Pur_InFragment3Adapter;
import ykk.xc.com.xcwms.util.JsonUtil;

public class Pur_InFragment3 extends BaseFragment {

    @BindView(R.id.tv_supplierSel)
    TextView tvSupplierSel;
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
    @BindView(R.id.et_mtlNo)
    EditText etMtlNo;
    @BindView(R.id.btn_selMtl)
    Button btnSelMtl;
    @BindView(R.id.et_sourceNo)
    EditText etSourceNo;
    @BindView(R.id.btn_sourceNo)
    Button btnSourceNo;
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
    @BindView(R.id.tv_purMan)
    TextView tvPurMan;

    private Pur_InFragment3 context = this;
    private static final int SEL_ORDER = 10, SEL_SUPPLIER = 11, SEL_STOCK = 12, SEL_STOCKP = 13, SEL_DEPT = 14, SEL_ORG = 15, SEL_ORG2 = 16, SEL_MTL = 17, SEL_STOCK2 = 18, SEL_STOCKP2 = 19;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int CLEAR1 = 1, CLEAR2 = 2, NUM_RESULT = 50, RESET = 60;
    private Supplier supplier; // 供应商
    private Material mtl;
    private Stock stock, stock2; // 仓库
    private StockPosition stockP, stockP2; // 库位
    private Department department; // 部门
    private Organization receiveOrg, purOrg; // 组织
    private Pur_InFragment3Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private List<PurReceiveOrder> sourceList = new ArrayList<>(); // 当前选择单据行数据
    private String stockBarcode, stockPBarcode, deptBarcode, mtlBarcode, sourceBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：部门， 4：收料订单， 5：物料
    private int curPos; // 当前行
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private Activity mContext;
    private Pur_InFragmentsActivity parent;

    // 消息处理
    private Pur_InFragment3.MyHandler mHandler = new Pur_InFragment3.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_InFragment3> mActivity;

        public MyHandler(Pur_InFragment3 activity) {
            mActivity = new WeakReference<Pur_InFragment3>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_InFragment3 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset('0');

                        m.checkDatas.clear();
                        m.getBarCodeTableAfterEnable(true);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext,"保存成功");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

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
                            case '4': // 采购订单
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                // 扫码成功后，判断必填项是否已经输入了值
                                // 禁用部分控件
//                                if(!m.getBarCodeTableAfterSon2(bt)) return;
                                m.parent.isChange = true;
                                m.getBarCodeTableAfter_recOrder(bt);


                                break;
                            case '5': // 物料
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.mtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
                                bt.setMtl(m.mtl);
                                m.getBarCodeTableAfterEnable(false);
                                m.getMaterialAfter(bt);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(RESET, 200);
                        Comm.showWarnDialog(m.mContext,"很抱歉，没能找到数据！");

                        break;
                    case RESET: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                m.setTexts(m.etWhName, m.stockBarcode);
                                break;
                            case '2': // 库位
                                m.setTexts(m.etWhPos, m.stockPBarcode);
                                break;
                            case '3': // 部门
                                m.setTexts(m.etDeptName, m.deptBarcode);
                                break;
                            case '4': // 收料订单
                                m.setTexts(m.etSourceNo, m.sourceBarcode);
                                break;
                            case '5': // 物料
                                m.setTexts(m.etMtlNo, m.mtlBarcode);
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
                                    Comm.showWarnDialog(m.mContext,"第" + (i + 1) + "行已入库，不能重复操作！");
                                    return;
                                }
                            }
                        }

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                    case CLEAR1: // 清空数据（来源单）
                        m.etSourceNo.setText("");
                        m.sourceBarcode = null;

                        break;
                    case CLEAR2: // 清空数据（物料）
                        m.etMtlNo.setText("");
                        m.mtlBarcode = null;

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_in_fragment3, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Pur_InFragmentsActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_InFragment3Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Pur_InFragment3Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", NUM_RESULT);
            }

            @Override
            public void onClick_selStock(View v, ScanningRecord2 entity, int position) {
                Log.e("selStock", "行：" + position);
                curPos = position;

                showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
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
        hideSoftInputMode(mContext, etMtlNo);
        hideSoftInputMode(mContext, etWhName);
        hideSoftInputMode(mContext, etWhPos);
        hideSoftInputMode(mContext, etDeptName);
        hideSoftInputMode(mContext, etSourceNo);
        getUserInfo();
        setFocusable(etMtlNo); // 物料代码获取焦点
    }

    @OnClick({R.id.tv_supplierSel, R.id.btn_sourceNo, R.id.btn_selMtl, R.id.btn_whName, R.id.btn_whPos, R.id.btn_save, R.id.btn_clone,
            R.id.tv_orderTypeSel, R.id.tv_receiveOrg, R.id.tv_purOrg, R.id.tv_purMan, R.id.btn_deptName})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_orderTypeSel: // 订单类型

                break;
            case R.id.btn_maker_code: // 打印条码界面
//                show(PrintBarcodeActivity.class, null);

                break;
            case R.id.tv_supplierSel: // 选择供应商
                showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, null);

                break;
            case R.id.btn_sourceNo: // 选择来源单号
                if (!selectSourceBefore()) {
                    return;
                }
                bundle = new Bundle();
                bundle.putSerializable("supplier", supplier);
                bundle.putSerializable("sourceList", (Serializable) sourceList);
                showForResult(Pur_SelReceiveOrderActivity.class, SEL_ORDER, bundle);

                break;
            case R.id.btn_whName: // 选择仓库
                isStockLong = false;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
            case R.id.btn_whPos: // 选择库位
                if (stock == null) {
                    Comm.showWarnDialog(mContext,"请先选择仓库！");
                    return;
                }
                bundle = new Bundle();
//                bundle.putInt("areaId", stockA.getId());
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);

                break;
            case R.id.btn_selMtl: // 选择物料
                if (checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "请选择或扫描来源单！");
                    return;
                }
                showForResult(Material_ListActivity.class, SEL_MTL, null);

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
                Comm.showDateDialog(mContext, view, 0);
                break;
            case R.id.tv_purMan: // 选择业务员

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(mContext.getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                run_findMatIsExistList2();
//                run_addScanningRecord();

                break;
            case R.id.btn_clone: // 重置
                hideKeyboard(mContext.getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
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
     * 选择来源单之前的判断
     */
    private boolean selectSourceBefore() {
        if (supplier == null) {
            Comm.showWarnDialog(mContext,"请选择供应商！");
            return false;
        }
        if (stock == null) {
            Comm.showWarnDialog(mContext,"请选择仓库！");
            return false;
        }
        if (stock.isStorageLocation() && stockP == null) {
            Comm.showWarnDialog(mContext,"请选择库位！");
            return false;
        }
        return true;
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext,"请先插入行！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
            if (sr2.getStockqty() > sr2.getFqty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）不能大于（应收数）！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_whName, R.id.et_whPos, R.id.et_mtlNo, R.id.et_deptName, R.id.et_sourceNo})
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
                // 按下事件
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_whName: // 仓库
                            String whName = getValues(etWhName).trim();
                            if (isKeyDownEnter(whName, keyCode)) {
                                if (stockBarcode != null && stockBarcode.length() > 0) {
                                    if(stockBarcode.equals(whName)) {
                                        stockBarcode = whName;
                                    } else {
                                        String tmp = whName.replaceFirst(stockBarcode, "");
                                        stockBarcode = tmp.replace("\n", "");
                                    }
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
                            if (isKeyDownEnter(whPos, keyCode)) {
                                if (stockPBarcode != null && stockPBarcode.length() > 0) {
                                    if(stockBarcode.equals(whPos)) {
                                        stockPBarcode = whPos;
                                    } else {
                                        String tmp = whPos.replaceFirst(stockPBarcode, "");
                                        stockPBarcode = tmp.replace("\n", "");
                                    }
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
                            if (isKeyDownEnter(deptName, keyCode)) {
                                if (deptBarcode != null && deptBarcode.length() > 0) {
                                    if(deptBarcode.equals(deptName)) {
                                        deptBarcode = deptName;
                                    } else {
                                        String tmp = deptName.replaceFirst(deptBarcode, "");
                                        deptBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    deptBarcode = deptName.replace("\n", "");
                                }
                                curViewFlag = '3';
                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_sourceNo: // 来源单号
                            String sourceNo = getValues(etSourceNo).trim();
                            if (!selectSourceBefore()) { // 扫码之前的判断
                                mHandler.sendEmptyMessageDelayed(CLEAR1, 200);
                                return false;
                            }
                            if (isKeyDownEnter(sourceNo, keyCode)) {
                                if (sourceBarcode != null && sourceBarcode.length() > 0) {
                                    String tmp = sourceNo.replaceFirst(sourceBarcode, "");
                                    sourceBarcode = tmp.replace("\n", "");
                                } else {
                                    sourceBarcode = sourceNo.replace("\n", "");
                                }
                                curViewFlag = '4';
                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_mtlNo: // 物料
                            String matNo = getValues(etMtlNo).trim();
                            if (checkDatas.size() == 0) { // 扫码之前的判断
                                Comm.showWarnDialog(mContext, "请选择或扫描来源单！");
                                mHandler.sendEmptyMessageDelayed(CLEAR2, 200);
                                return false;
                            }
                            if (isKeyDownEnter(matNo, keyCode)) {
                                if (mtlBarcode != null && mtlBarcode.length() > 0) {
                                    if(mtlBarcode.equals(matNo)) {
                                        mtlBarcode = matNo;
                                    } else {
                                        String tmp = matNo.replaceFirst(mtlBarcode, "");
                                        mtlBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    mtlBarcode = matNo.replace("\n", "");
                                }
                                curViewFlag = '5';
                                // 执行查询方法
                                run_smGetDatas();
                            }
                            break;
                    }
                }
                return false;
            }
        };
        etWhName.setOnKeyListener(keyListener);
        etWhPos.setOnKeyListener(keyListener);
        etMtlNo.setOnKeyListener(keyListener);
        etSourceNo.setOnKeyListener(keyListener);
    }

    /**
     * 是否按了回车键
     */
    private boolean isKeyDownEnter(String val, int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (val.length() == 0) {
                Comm.showWarnDialog(mContext, "请扫码条码！");
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 0：重置全部，1：重置物料部分
     *
     * @param flag
     */
    private void reset(char flag) {
        // 清空物料信息
        etSourceNo.setText(""); // 来源单
        etMtlNo.setText(""); // 物料代码

        setEnables(tvSupplierSel, R.drawable.back_style_blue,true);
        setEnables(tvReceiveOrg, R.drawable.back_style_blue,true);
        setEnables(tvPurOrg, R.drawable.back_style_blue,true);
        stock2 = null;
        stockP2 = null;
        sourceList.clear();
        parent.isChange = false;
    }

    private void resetSon() {
        getBarCodeTableAfterEnable(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvSupplierSel.setText("");
        etWhName.setText("");
        etWhPos.setText("");
        etDeptName.setText("");
        tvReceiveOrg.setText("");
        tvPurOrg.setText("");
        supplier = null;
        mtl = null;
        stock = null;
        stockP = null;
        department = null;
        receiveOrg = null;
        purOrg = null;
        curViewFlag = '1';
        stockBarcode = null;
        stockPBarcode = null;
        mtlBarcode = null;
        sourceBarcode = null;
        setFocusable(etWhName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_SUPPLIER: //查询供应商	返回
                if (resultCode == Activity.RESULT_OK) {
                    supplier = (Supplier) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_SUPPLIER", supplier.getfName());
                    if (supplier != null) {
                        tvSupplierSel.setText(supplier.getfName());
                    }
                }

                break;
            case SEL_ORDER: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        List<PurReceiveOrder> list = (List<PurReceiveOrder>) bundle.getSerializable("checkDatas");
                        sourceList.addAll(list);
                        parent.isChange = true;
                        getSourceAfter(list);
                    }
                }

                break;
            case SEL_MTL: //查询物料	返回
                if (resultCode == Activity.RESULT_OK) {
                    mtl = (Material) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_MTL", mtl.getfName());
                    getMaterialAfter(null);
                }

                break;
            case SEL_STOCK: //查询仓库	返回
                if (resultCode == Activity.RESULT_OK) {
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
                if (resultCode == Activity.RESULT_OK) {
                    stockP = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP", stockP.getFname());
                    getStockPAfter();
                }

                break;
            case SEL_STOCK2: //行事件选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock2 = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCK2", stock2.getfName());
                    // 启用了库位管理
                    if (stock2.isStorageLocation()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("stockId", stock2.getfStockid());
                        showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, bundle);
                    } else {
                        ScanningRecord2 sr2 = checkDatas.get(curPos);
                        sr2.setStockId(stock2.getfStockid());
                        sr2.setStock(stock2);
                        sr2.setStockFnumber(stock2.getfNumber());
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    ScanningRecord2 sr2 = checkDatas.get(curPos);
                    sr2.setStockId(stock2.getfStockid());
                    sr2.setStock(stock2);
                    sr2.setStockFnumber(stock2.getfNumber());

                    sr2.setStockPositionId(stockP2.getId());
                    sr2.setStockPName(stockP2.getFname());
                    mAdapter.notifyDataSetChanged();
                }

                break;
            case SEL_ORG: //查询收料组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    receiveOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG", receiveOrg.getName());
                    getOrgAfter();
                }

                break;
            case SEL_ORG2: //查询采购组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    purOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG2", purOrg.getName());
                    getOrg2After();
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    getDeptAfter();
                }

                break;
            case NUM_RESULT: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setStockqty(num);
                        checkDatas.get(curPos).setPoFmustqty(num);
//                        checkDatas.get(curPos).setFqty(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /**
     * 选择来源单返回
     */
    private void getSourceAfter(List<PurReceiveOrder> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            PurReceiveOrder p = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            sr2.setType(1);
            sr2.setSourceFinterId(p.getfId());
            sr2.setSourceFnumber(p.getFbillno());
            sr2.setFitemId(p.getMtl().getfMaterialId());
            sr2.setMtl(p.getMtl());
            sr2.setMtlFnumber(p.getMtl().getfNumber());
            sr2.setUnitFnumber(p.getMtl().getUnit().getUnitNumber());
            sr2.setPoFid(p.getfId());
            sr2.setPoFbillno(p.getFbillno());
            sr2.setPoFmustqty(p.getUsableFqty());

//            sr2.setBatchno(p.getBct().getBatchCode());
//            sr2.setSequenceNo(p.getBct().getSnCode());
            sr2.setFqty(p.getUsableFqty());

            // 是否启用物料的序列号,如果启用了，则数量为1
            if (p.getMtl().getIsSnManager() == 1) {
                sr2.setStockqty(1);
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
            sr2.setSupplierId(p.getSupplierId());
            sr2.setSupplierName(p.getSupplierName());
            sr2.setSupplierFnumber(supplier.getfNumber());
            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
            }
            // 收料组织
            if(receiveOrg == null) receiveOrg = new Organization();
            receiveOrg.setFpkId(p.getRecOrgId());
            receiveOrg.setNumber(p.getRecOrgNumber());
            receiveOrg.setName(p.getRecOrgName());

            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
            tvReceiveOrg.setText(receiveOrg.getName());
            sr2.setReceiveOrgFnumber(receiveOrg.getNumber());
            // 采购组织
            if(purOrg == null) purOrg = new Organization();
            purOrg.setFpkId(p.getRecOrgId());
            purOrg.setNumber(p.getRecOrgNumber());
            purOrg.setName(p.getRecOrgName());

            setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
            tvPurOrg.setText(purOrg.getName());
            sr2.setPurOrgFnumber(purOrg.getNumber());

            checkDatas.add(sr2);
        }
        setFocusable(etMtlNo); // 物料代码获取焦点

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择（物料）返回的值
     */
    private void getMaterialAfter(BarCodeTable bt) {
        if(bt != null) {
            setTexts(etMtlNo, mtlBarcode);
        }
        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            // 如果扫码相同
            if (parseInt(mtl.getfMaterialId()) == parseInt(sr2.getMtl().getfMaterialId())) {
                isFlag = true;

                // 未启用序列号
                if (sr2.getMtl().getIsSnManager() == 0) {
                    // 如果应收数大于实收数
                    if (sr2.getFqty() > sr2.getStockqty()) {
                        // 如果扫的是物料包装条码，就显示个数
                        double number = 0;
                        if(bt != null) {
                            number = bt.getMaterialCalculateNumber();
                        }
                        if(number > 0) {
                            sr2.setStockqty(number+sr2.getStockqty());
                        } else {
                            sr2.setStockqty(sr2.getStockqty() + 1);
                        }
                    } else {
                        // 数量已满
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行！，实收数不能大于应收数！");
                        return;
                    }

                } else { // 启用序列号
                    sr2.setStockqty(1);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(mContext, "扫的物料在订单不存在！");
        }
    }

    /**
     * 得到条码表的数据，禁用部分控件
     */
    private void getBarCodeTableAfterEnable(boolean isEnable) {
        if(isEnable) {
            setEnables(tvSupplierSel, R.drawable.back_style_blue,true);
            setEnables(tvReceiveOrg, R.drawable.back_style_blue, true);
            setEnables(tvPurOrg, R.drawable.back_style_blue, true);
        } else {
            setEnables(tvSupplierSel, R.drawable.back_style_gray3,false);
            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
            setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
        }
    }

    /**
     * 得到物料后进行加减数量
     */
    private void getBarCodeTableAfterExec(BarCodeTable bt) {
        int size = checkDatas.size();
        boolean isFlag = false; // 扫描的物料是否有效
        // 判断重复
        if(size > 0) {
            for (int i = 0; i < size; i++) {
                ScanningRecord2 sr2 = checkDatas.get(i);
                // 如果扫码相同
                if(parseInt(sr2.getMtl().getfMaterialId()) == parseInt(bt.getMaterialId()) && sr2.getMtl().getIsSnManager() == 0) { // 未启用序列号
                    isFlag = true;
                    // 实收数大于应收数,就看其他行有没有相同的物料
                    if(sr2.getStockqty()+1 > sr2.getFqty()) {
                        continue;
                    }
                    sr2.setStockqty(sr2.getStockqty() + 1);
                    sr2.setPoFmustqty(sr2.getStockqty() + 1);

                } else if(parseInt(sr2.getMtl().getfMaterialId()) == parseInt(bt.getMaterialId())) { // 启用序列号
                    isFlag = true;
                    if(sr2.getStockqty() == 1) { // 启用了序列号，如果已经扫了一次，就提示
                        Comm.showWarnDialog(mContext, "第"+(i+1)+"物料已启用序列号，数量只能为1！");
                        break;
                    }
                    sr2.setStockqty(1);
                    sr2.setPoFmustqty(1);
                }
            }
            if(!isFlag) {
                Comm.showWarnDialog(mContext, "本次扫描物料与订单中的行不匹配，请检查！");
                return;
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 来源订单 判断数据
     */
    private boolean getBarCodeTableAfterSon2(BarCodeTable bt) {
        // 得到采购订单
        PurReceiveOrder p = JsonUtil.stringToObject(bt.getRelationObj(), PurReceiveOrder.class);
        int size = sourceList.size();
        for (int i = 0; i < size; i++) {
            PurReceiveOrder p2 = sourceList.get(i);
            // 是否有相同的行，就提示
            if (p.getfId() == p2.getfId() && p.getMtlId() == p2.getMtlId() && p.getEntryId() == p2.getEntryId()) {
                Comm.showWarnDialog(mContext, "第"+(i+1)+"行！，已有相同的数据！");
                return false;
            }
        }
        return true;
    }

    /**
     * 得到条码表的数据 （物料）
     */
    private void getBarCodeTableAfter_mtl(BarCodeTable barCodeTable) {
        setTexts(etMtlNo, mtlBarcode);

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
    private void getBarCodeTableAfter_recOrder(BarCodeTable bt) {
        setTexts(etSourceNo, sourceBarcode);
        // 得到采购订单
        PurReceiveOrder recOrder = JsonUtil.stringToObject(bt.getRelationObj(), PurReceiveOrder.class);
        int size = sourceList.size();
        for (int i = 0; i < size; i++) {
            PurReceiveOrder p2 = sourceList.get(i);
            // 是否有相同的行，就提示
            if (recOrder.getfId() == p2.getfId() && recOrder.getMtlId() == p2.getMtlId() && recOrder.getEntryId() == p2.getEntryId()) {
                Comm.showWarnDialog(mContext, "第"+(i+1)+"行！，已有相同的数据！");
                return;
            }
        }
//        getSourceAfter(null);
        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setSourceFinterId(bt.getRelationBillId());
        sr2.setSourceFnumber(bt.getRelationBillNumber());
        sr2.setFitemId(bt.getMaterialId());
        sr2.setStockId(stock.getfStockid());
        sr2.setStock(stock);
        sr2.setStockFnumber(stock.getfNumber());
        sr2.setStockPositionId(stockP.getId());
        sr2.setStockPName(stockP.getFname());
        sr2.setReceiveOrgFnumber(recOrder.getRecOrgNumber());
        sr2.setPurOrgFnumber(recOrder.getPurOrgNumber());
        if(supplier == null) supplier = new Supplier();
        supplier.setFsupplierid(recOrder.getSupplierId());
        supplier.setfNumber(recOrder.getSupplierNumber());
        supplier.setfName(recOrder.getSupplierName());
        setEnables(tvSupplierSel, R.drawable.back_style_gray3, false);
        tvSupplierSel.setText(recOrder.getSupplierName());
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

        sr2.setMtl(bt.getMtl());
        sr2.setMtlFnumber(bt.getMtl().getfNumber());
        sr2.setUnitFnumber(bt.getMtl().getUnit().getUnitNumber());
        Material mtl = bt.getMtl();
        if(mtl.getIsBatchManager() > 0) {
            sr2.setBatchno(bt.getBatchCode());
        }
        if(mtl.getIsSnManager() > 0) {
            sr2.setSequenceNo(bt.getSnCode());
        }
        if (department != null) {
            sr2.setEmpId(department.getFitemID());
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }
        sr2.setFqty(recOrder.getUsableFqty());
        sr2.setStockqty(0);
        sr2.setPoFid(recOrder.getfId());
        sr2.setEntryId(recOrder.getEntryId());
        sr2.setPoFbillno(recOrder.getFbillno());
        sr2.setPoFmustqty(recOrder.getUsableFqty());
        sr2.setBarcode(bt.getBarcode());

        checkDatas.add(sr2);
        sourceList.add(recOrder);
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
            setFocusable(etMtlNo);
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
            record.setFdate("");
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
            case '4': // 采购订单
                mUrl = Consts.getURL("barCodeTable/findBarcode3ByParam");
                barcode = sourceBarcode;
                caseId = 31;
                break;
            case '5': // 物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                // caseId = 11; // 因为这里有物料包装或者物料的码所以不能指定caseId
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("caseId", caseId > 0 ? String.valueOf(caseId) : "")
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
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }
}