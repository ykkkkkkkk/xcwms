package ykk.xc.com.xcwms.sales;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import ykk.xc.com.xcwms.basics.Batch_DialogActivity;
import ykk.xc.com.xcwms.basics.Cust_DialogActivity;
import ykk.xc.com.xcwms.basics.Material_ListActivity;
import ykk.xc.com.xcwms.basics.Organization_DialogActivity;
import ykk.xc.com.xcwms.basics.PrintBarcodeActivity;
import ykk.xc.com.xcwms.basics.Sequence_DialogActivity;
import ykk.xc.com.xcwms.basics.StockArea_DialogActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Organization;
import ykk.xc.com.xcwms.model.ScanningRecord;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockArea;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.sal.SalOrder;
import ykk.xc.com.xcwms.sales.adapter.Sal_OutAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;

public class Sal_OutActivity extends BaseActivity {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.btn_maker_code)
    Button btnMakerCode;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.et_whName)
    EditText etWhName;
    @BindView(R.id.btn_whName)
    Button btnWhName;
    @BindView(R.id.et_whArea)
    EditText etWhArea;
    @BindView(R.id.btn_whArea)
    Button btnWhArea;
    @BindView(R.id.et_whPos)
    EditText etWhPos;
    @BindView(R.id.btn_whPos)
    Button btnWhPos;
    @BindView(R.id.et_matNo)
    EditText etMatNo;
    @BindView(R.id.tv_matName)
    TextView tvMatName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.et_num)
    EditText etNum;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.et_sourceNo)
    EditText etSourceNo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_selMat)
    Button btnSelMat;
    @BindView(R.id.btn_sourceNo)
    Button btnSourceNo;
    @BindView(R.id.tv_batchNo)
    TextView tvBatchNo;
    @BindView(R.id.tv_sequenceNo)
    TextView tvSequenceNo;
    @BindView(R.id.btn_clone)
    Button btnClone;
    @BindView(R.id.tv_orderTypeSel)
    TextView tvOrderTypeSel;
    @BindView(R.id.tv_salDate)
    TextView tvSalDate;
    @BindView(R.id.tv_inventoryOrg)
    TextView tvInventoryOrg;
    @BindView(R.id.tv_salOrg)
    TextView tvSalOrg;

    private Sal_OutActivity context = this;
    private static final int SEL_ORDER = 10, SEL_CUST = 11, SEL_STOCK = 12, SEL_STOCKA = 13, SEL_STOCKP = 14, SEL_DEPT = 15, SEL_MAT = 16, SEL_BATCH = 17, SEL_SEQUENCE = 18, SEL_ORG = 19, SEL_ORG2 = 20;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int CODE1 = 1, CODE2 = 2, CODE4 = 4, CODE20 = 20;
    private Customer customer; // 客户
    private Stock stock; // 仓库
    private StockArea stockA; // 库区
    private StockPosition stockP; // 库位
    private Organization salOrg, inventoryOrg; // 销售组织，库存组织
    private Material mtl; // 物料
    private Sal_OutAdapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private char dataType = '1'; // 1：来源单，2：无源单
    private String stockBarcode, stockABarcode, stockPBarcode, matBarcode, sourceBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库区， 3：库位， 4：物料
    private int curPos; // 当前行
    private boolean isStockLong, isStockALong; // 判断选择（仓库，库区）是否长按了

    private OkHttpClient okHttpClient = new OkHttpClient();

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_OutActivity> mActivity;

        public MyHandler(Sal_OutActivity activity) {
            mActivity = new WeakReference<Sal_OutActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_OutActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset('0');

                        m.showWarnDialog("保存成功√");
                        m.checkDatas.clear();
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC1:
                        m.showWarnDialog("服务器繁忙，请稍候再试！");

                        break;
                    case SUCC2: // 扫码成功后进入
                        switch (m.curViewFlag) {
                            case '1':
                                m.stock = JsonUtil.strToObject((String) msg.obj, Stock.class);
                                m.getStockAfter();

                                break;
                            case '2':
                                m.stockA = JsonUtil.strToObject((String) msg.obj, StockArea.class);
                                m.getStockAAfter();

                                break;
                            case '3':
                                m.stockP = JsonUtil.strToObject((String) msg.obj, StockPosition.class);
                                m.getStockPAfter();

                                break;
                            case '4':
                                m.mtl = JsonUtil.strToObject((String) msg.obj, Material.class);
                                m.setTexts(m.etMatNo, m.mtl.getfNumber());
                                m.matBarcode = m.mtl.getfNumber();
                                if (m.dataType == '1') { // 来源单
                                    if (m.mtl.getIsSnManager() == 1) {
                                        m.showWarnDialog("该物料已启用序列号，数量为1");
                                        return;
                                    }
                                    for (int i = 0, size = m.checkDatas.size(); i < size; i++) {
                                        ScanningRecord2 sr2 = m.checkDatas.get(i);
                                        if (m.mtl.getfMaterialId() == sr2.getMtl().getfMaterialId() && sr2.getMtl().getIsSnManager() == 0 && sr2.getFqty() > sr2.getStockqty()) {
                                            // 没有启用序列号，并且应发数量大于实发数量
                                            sr2.setStockqty(sr2.getStockqty() + 1);
                                            break;
                                        }
                                    }

                                } else { // 无源单
                                    m.addRowSon("", "", -1);
                                }
                                m.mAdapter.notifyDataSetChanged();

                                break;
                            case '5':
                                List<SalOrder> list = JsonUtil.strToList2((String) msg.obj, SalOrder.class);
                                m.getSourceAfter(list, true);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(CODE20, 200);
                        m.showWarnDialog("很抱歉，没能找到数据！");


                        break;
                    case CODE20: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1':
                                m.setTexts(m.etWhName, m.stockBarcode);
                                break;
                            case '2':
                                m.setTexts(m.etWhArea, m.stockABarcode);
                                break;
                            case '3':
                                m.setTexts(m.etWhPos, m.stockPBarcode);
                                break;
                            case '4':
                                m.setTexts(m.etMatNo, m.matBarcode);
                                break;
                            case '5':
                                m.setTexts(m.etSourceNo, m.sourceBarcode);
                                break;
                        }

                        break;
                    case SUCC3: // 判断是否存在返回
                        String strId = JsonUtil.strToString((String) msg.obj);
                        String[] idArr = strId.split(",");
                        for (int i = 0, len = idArr.length; i < len; i++) {
                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
                                if (m.parseInt(idArr[i]) == m.checkDatas.get(j).getMtl().getfMaterialId()) {
                                    m.showWarnDialog("第" + (i + 1) + "行物料已扫过！");
                                    return;
                                }
                            }
                        }


                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.sal_out;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Sal_OutAdapter(context, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Sal_OutAdapter.MyCallBack() {
            @Override
            public void onClick_batch_top(View v, ScanningRecord2 entity, int position) {
                Log.e("batch", "行：" + position);
                curPos = position;
                if (entity.getMtl().getIsSnManager() == 1) { // 选择批号
                    Bundle bundle = new Bundle();
                    bundle.putInt("fitemId", entity.getMtl().getfMaterialId());
                    bundle.putInt("stockId", entity.getStock().getfStockid());
                    showForResult(Batch_DialogActivity.class, CODE1, bundle);
                }
            }

            @Override
            public void onClick_batch_long(View v, ScanningRecord2 entity, int position) {
                Log.e("sequence", "行：" + position);
                curPos = position;
                if (entity.getMtl().getIsSnManager() == 1) { // 选择序列号
                    showForResult(Sequence_DialogActivity.class, CODE2, null);
                }
            }

            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", CODE4);
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
        hideSoftInputMode(etWhArea);
        hideSoftInputMode(etWhPos);
        hideSoftInputMode(etSourceNo);
        tvSalDate.setText(Comm.getSysDate(7));
    }

    @OnClick({R.id.btn_close, R.id.btn_sourceNo, R.id.btn_maker_code, R.id.tv_custSel, R.id.btn_whName, R.id.tv_batchNo, R.id.tv_sequenceNo, R.id.btn_whArea, R.id.btn_whPos,
            R.id.btn_selMat, R.id.btn_add, R.id.btn_save, R.id.btn_clone, R.id.tv_orderTypeSel, R.id.tv_salDate, R.id.tv_inventoryOrg, R.id.tv_salOrg})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.tv_orderTypeSel: // 订单类型


                break;
            case R.id.tv_salDate: // 选择日期
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.btn_maker_code: // 打印条码界面
                show(PrintBarcodeActivity.class, null);

                break;
            case R.id.btn_sourceNo: // 选择来源单号
                if (!selectSourceBefore()) {
                    return;
                }
                bundle = new Bundle();
                bundle.putSerializable("customer", customer);
//                showForResult(Sal_SelOrderActivity.class, SEL_ORDER, bundle);
                showForResult(Sal_SelSourceFragmentActivity.class, SEL_ORDER, bundle);

                break;
            case R.id.tv_custSel: // 选择客户
                showForResult(Cust_DialogActivity.class, SEL_CUST, null);

                break;
            case R.id.btn_whName: // 选择仓库
                isStockLong = false;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
            case R.id.btn_whArea: // 选择库区
                if (stock == null) {
                    showWarnDialog("请先选择仓库！");
                    return;
                }
                isStockALong = false;
                bundle = new Bundle();
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockArea_DialogActivity.class, SEL_STOCKA, bundle);

                break;
            case R.id.btn_whPos: // 选择库位
                if (stockA == null) {
                    showWarnDialog("请先选择库区！");
                    return;
                }
                bundle = new Bundle();
                bundle.putInt("areaId", stockA.getId());
                showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);

                break;
            case R.id.tv_inventoryOrg: // 库存组织
                showForResult(Organization_DialogActivity.class, SEL_ORG, null);

                break;
            case R.id.tv_salOrg: // 销售组织
                showForResult(Organization_DialogActivity.class, SEL_ORG2, null);

                break;
            case R.id.btn_selMat: // 选择物料
                if (!selectSourceBefore()) {
                    return;
                }
                showForResult(Material_ListActivity.class, SEL_MAT, null);

                break;
            case R.id.tv_batchNo: // 选择批号
                if (stock == null) {
                    showWarnDialog("请选择仓库！");
                    return;
                }
                if (mtl == null) {
                    showWarnDialog("请选择物料！");
                    return;
                }
                bundle = new Bundle();
                bundle.putInt("fitemId", mtl.getfMaterialId());
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(Batch_DialogActivity.class, SEL_BATCH, bundle);

                break;
            case R.id.tv_sequenceNo: // 选择序列号
                if (mtl == null) {
                    showWarnDialog("请选择物料！");
                    return;
                }
                showForResult(Sequence_DialogActivity.class, SEL_SEQUENCE, null);

                break;
            case R.id.btn_add:
                addRow();

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                run_findMatIsExistList();
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
        if (customer == null) {
            showWarnDialog("请选择客户！");
            return false;
        }
        if (stock == null) {
            showWarnDialog("请选择仓库！");
            return false;
        }
        if (stock.isReservoirArea() && stockA == null) {
            showWarnDialog("请选择库区！");
            return false;
        }
        if (stock.isStorageLocation() && stockP == null) {
            showWarnDialog("请选择库位！");
            return false;
        }
        return true;
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            showWarnDialog("请先插入行！");
            return false;
        }
        if(inventoryOrg == null) {
            showWarnDialog("请选择库存组织！");
            return false;
        }
        if(salOrg == null) {
            showWarnDialog("请选择销售组织！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if (sr2.getMtl().getIsBatchManager() == 1 && sr2.getBatchno().length() == 0) {
                showWarnDialog("第" + (i + 1) + "行请输入（批号）！");
                return false;
            }
            if (sr2.getMtl().getIsSnManager() == 1 && sr2.getSequenceNo().length() == 0) {
                showWarnDialog("第" + (i + 1) + "行请输入（序列号）！");
                return false;
            }
            if (sr2.getStockqty() == 0) {
                showWarnDialog("第" + (i + 1) + "行（实发数）必须大于0！");
                return false;
            }
            if (sr2.getStockqty() > sr2.getFqty()) {
                showWarnDialog("第" + (i + 1) + "行（实发数）不能大于（应发数）！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_whName, R.id.et_whArea, R.id.et_whPos, R.id.et_matNo, R.id.et_sourceNo})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
    }

    @OnLongClick({R.id.btn_whName, R.id.btn_whArea})
    public boolean onViewLongClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_whName: // 长按选择仓库
                isStockLong = true;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
            case R.id.btn_whArea: // 长按选择库区
                if (stock == null) {
                    showWarnDialog("请先选择仓库！");
                    return true;
                }
                isStockALong = true;
                bundle = new Bundle();
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockArea_DialogActivity.class, SEL_STOCKA, bundle);

                break;
        }
        return true;
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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
                    case R.id.et_whArea: // 库区
                        String whArea = getValues(etWhArea).trim();
                        if (isKeyDownEnter(whArea, event, keyCode)) {
                            if (stockABarcode != null && stockABarcode.length() > 0) {
                                String tmp = whArea.replaceFirst(stockABarcode, "");
                                stockABarcode = tmp.replace("\n", "");
                            } else {
                                stockABarcode = whArea.replace("\n", "");
                            }
                            curViewFlag = '2';
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
                            curViewFlag = '3';
                            // 执行查询方法
                            run_smGetDatas();
                        }

                        break;
                    case R.id.et_matNo: // 物料
                        String matNo = getValues(etMatNo).trim();
                        if (!selectSourceBefore()) { // 扫码之前的判断
                            etMatNo.setText("");
                            return false;
                        }
                        if (isKeyDownEnter(matNo, event, keyCode)) {
                            if (matBarcode != null && matBarcode.length() > 0) {
                                String tmp = matNo.replaceFirst(matBarcode, "");
                                matBarcode = tmp.replace("\n", "");
                            } else {
                                matBarcode = matNo.replace("\n", "");
                            }
                            curViewFlag = '4';
                            // 执行查询方法
                            run_smGetDatas();
                        }

                        break;
                    case R.id.et_sourceNo: // 来源单号
                        String sourceNo = getValues(etSourceNo).trim();
                        if (!selectSourceBefore()) { // 扫码之前的判断
                            etSourceNo.setText("");
                            return false;
                        }
                        if (isKeyDownEnter(sourceNo, event, keyCode)) {
                            if (sourceBarcode != null && sourceBarcode.length() > 0) {
                                String tmp = sourceNo.replaceFirst(sourceBarcode, "");
                                sourceBarcode = tmp.replace("\n", "");
                            } else {
                                sourceBarcode = sourceNo.replace("\n", "");
                            }
                            curViewFlag = '5';
                            // 执行查询方法
                            run_smGetDatas();
                        }

                        break;
                }
                return false;
            }
        };
        etWhName.setOnKeyListener(keyListener);
        etWhArea.setOnKeyListener(keyListener);
        etWhPos.setOnKeyListener(keyListener);
        etMatNo.setOnKeyListener(keyListener);
        etSourceNo.setOnKeyListener(keyListener);
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

    /**
     * 0：重置全部，1：重置物料部分
     *
     * @param flag
     */
    private void reset(char flag) {
        // 清空物料信息
        mtl = null;
        etSourceNo.setText(""); // 来源单号
        etMatNo.setText(""); // 物料代码
        tvMatName.setText(""); // 物料名称
        tvType.setText(""); // 规格
        etNum.setText(""); // 数量

        setEnables(tvInventoryOrg, R.drawable.back_style_blue, true);
        setEnables(tvSalOrg, R.drawable.back_style_blue, true);
        setEnables(etNum, R.drawable.back_style_blue, true);
        tvBatchNo.setText(""); // 批号
        tvSequenceNo.setText(""); // 序列号
        setEnables(tvBatchNo, R.drawable.back_style_blue, true);
        setEnables(tvSequenceNo, R.drawable.back_style_blue, true);

        if (flag == '0') {
            setEnables(btnSelMat, R.drawable.btn_blue3_selector, true);
            setEnables(btnAdd, R.drawable.btn_blue_selector, true);
            setEnables(etSourceNo, R.drawable.back_style_blue4, true);
            setEnables(btnSourceNo, R.drawable.btn_blue3_selector, true);
        }
    }

    private void resetSon() {
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvCustSel.setText("");
        etWhName.setText("");
        etWhArea.setText("");
        etWhPos.setText("");
        tvInventoryOrg.setText("");
        tvSalOrg.setText("");
        customer = null;
        stock = null;
        stockA = null;
        stockP = null;
        inventoryOrg = null;
        salOrg = null;
        dataType = '1';
        curViewFlag = '1';
        tvSalDate.setText(Comm.getSysDate(7));
    }


    /**
     * 添加行
     */
    private void addRow() {
        if (customer == null) {
            showWarnDialog("请选择客户！");
            return;
        }
        if (stock == null) {
            showWarnDialog("请选择仓库！");
            return;
        }
        if (stock.isStorageLocation() && stockA == null) {
            showWarnDialog("请选择库区！");
            return;
        }
        if (stock.isStorageLocation() && stockP == null) {
            showWarnDialog("请选择库位！");
            return;
        }
        if(inventoryOrg == null) { // 库存组织
            showWarnDialog("请选择库存组织！");
            return;
        }
        if(salOrg == null) { // 销售组织
            showWarnDialog("请选择销售组织！");
            return;
        }
        if (mtl == null) {
            showWarnDialog("请选择物料！");
            return;
        }
        double num = parseDouble(getValues(etNum).trim());
        if (num == 0) {
            showWarnDialog("请输入数量！");
            return;
        }
        String batch = getValues(tvBatchNo).trim();
        if (mtl.getIsSnManager() == 1 && batch.length() == 0) {
            showWarnDialog("该物料启用了批号，请选择批号！");
            return;
        }
        String seqNo = getValues(tvSequenceNo).trim();
        if (mtl.getIsSnManager() == 1 && seqNo.length() == 0) {
            showWarnDialog("该物料启用了序列号，请选择序列号！");
            return;
        }
        // 隐藏键盘
        hideKeyboard(getCurrentFocus());

        ScanningRecord2 sr2 = new ScanningRecord2();
//        sr2.setSource_finterID(1);
        sr2.setCustomerId(customer.getFcustId());
        sr2.setCustomerName(customer.getCustomerName());
        sr2.setStockId(stock.getId());
        sr2.setStockPName(stock.getfName());
        sr2.setStockAreaId(stockA.getId());
        sr2.setStockAName(stockA.getFname());
        sr2.setStockPositionId(stockP.getId());
        sr2.setStockPName(stockP.getFname());
        sr2.setFitemId(mtl.getfMaterialId());
        sr2.setMtl(mtl);
        sr2.setBatchno(batch);
        sr2.setEmpId(0);
        sr2.setFqty(num);
        sr2.setStockqty(num); //

//        boolean isAlike = false; // 是否存在重复数据，存在加1
//        for(int j=0, sizej=checkDatas.size(); j<sizej; j++) {
//            ScanningRecord2 sr2a = checkDatas.get(j);
//            if(mtl.getId() == sr2a.getFitemId()){
//                sr2a.setFqty(sr2a.getFqty()+num);
//                sr2a.setStockqty(sr2a.getStockqty()+num);
//                isAlike = true;
//                break;
//            }
//        }
//        if(!isAlike) {
//            checkDatas.add(sr2);
//        }
        addRowSon(batch, seqNo, num);

        reset('1');
        mAdapter.notifyDataSetChanged();
    }

    private void addRowSon(String batchNo, String seqNo, double num) {
        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setCustomerId(customer.getFcustId());
        sr2.setCustomerName(customer.getCustomerName());
        sr2.setStockId(stock.getfStockid());
        sr2.setStock(stock);
        sr2.setStockAreaId(stockA.getId());
        sr2.setStockAName(stockA.getFname());
        sr2.setStockPositionId(stockP.getId());
        sr2.setStockPName(stockP.getFname());
        sr2.setFitemId(mtl.getfMaterialId());
        sr2.setReceiveOrgFnumber(inventoryOrg.getNumber());
        sr2.setPurOrgFnumber(salOrg.getNumber());
        sr2.setMtl(mtl);
        sr2.setMtlFnumber(mtl.getfNumber());
        sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
        sr2.setBatchno(batchNo);
        sr2.setSequenceNo(seqNo);
        double fqty = num == -1 ? 1 : num;
        sr2.setFqty(fqty);
        sr2.setStockqty(fqty);
        sr2.setPoFid(0);
        sr2.setPoFbillno("");
        sr2.setPoFmustqty(fqty);

        boolean isAlike = false; // 是否存在重复数据，存在加1
        for (int j = 0, sizej = checkDatas.size(); j < sizej; j++) {
            ScanningRecord2 sr2a = checkDatas.get(j);
            if (mtl.getfMaterialId() == sr2a.getFitemId() && mtl.getIsSnManager() == 0) { // 没有启用序列化
                sr2a.setFqty(sr2a.getFqty() + num);
                sr2a.setStockqty(sr2a.getStockqty() + num);
                isAlike = true;
                break;
            }
        }
        if (!isAlike) {
            checkDatas.add(sr2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_CUST: //查询客户	返回
                if (resultCode == RESULT_OK) {
                    customer = (Customer) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_CUST", customer.getCustomerName());
                    if (customer != null) {
                        tvCustSel.setText(customer.getCustomerName());
                    }
                }

                break;
            case SEL_ORDER: // 查询订单返回
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        List<SalOrder> list = (List<SalOrder>) bundle.getSerializable("checkDatas");
                        getSourceAfter(list, false);
                    }
                }

                break;
            case SEL_STOCK: //查询仓库	返回
                if (resultCode == RESULT_OK) {
                    Stock stock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCK", stock.getfName());
                    if (this.stock != null && stock != null && stock.getId() == this.stock.getId()) {
//                         长按了，并且启用了库区管理
                        if (isStockLong && stock.isReservoirArea()) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("stockId", stock.getfStockid());
                            showForResult(StockArea_DialogActivity.class, SEL_STOCKA, bundle);
                        }
                        return;
                    }
                    this.stock = stock;
                    getStockAfter();
                }

                break;
            case SEL_STOCKA: //查询库区	返回
                if (resultCode == RESULT_OK) {
                    StockArea stockA = (StockArea) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKA", stockA.getFname());
                    if (this.stockA != null && stockA != null && stockA.getId() == this.stockA.getId()) {
//                         长按了，并且启用了库位管理
                        if (isStockALong && stock.isStorageLocation()) {
                            isStockALong = true;
                            Bundle bundle = new Bundle();
                            bundle.putInt("areaId", stockA.getId());
                            showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);
                        }
                        return;
                    }
                    this.stockA = stockA;
                    getStockAAfter();
                }

                break;
            case SEL_STOCKP: //查询库位	返回
                if (resultCode == RESULT_OK) {
                    stockP = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP", stockP.getFname());
                    getStockPAfter();
                }

                break;
            case SEL_ORG: //查询库存组织   	返回
                if (resultCode == RESULT_OK) {
                    inventoryOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG", inventoryOrg.getName());
                    getOrganizationAfter();
                }

                break;
            case SEL_ORG2: //查询销售组织   	返回
                if (resultCode == RESULT_OK) {
                    salOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG2", salOrg.getName());
                    getOrganization2After();
                }

                break;
            case SEL_MAT: //查询物料	返回
                if (resultCode == RESULT_OK) {
                    mtl = (Material) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_MAT", mtl.getfName());
                    getMtlAfter();
                }

                break;
            case SEL_BATCH: //查询批号  返回
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String result = bundle.getString("resultValue");
                        tvBatchNo.setText(result);
                    }
                }

                break;
            case SEL_SEQUENCE: //查询序列号  返回
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String result = bundle.getString("resultValue");
                        tvSequenceNo.setText(result);
                    }
                }

                break;
            case CODE1: // 批号
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        checkDatas.get(curPos).setBatchno(value);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case CODE2: // 序列号
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        checkDatas.get(curPos).setSequenceNo(value);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case CODE4: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        checkDatas.get(curPos).setStockqty(parseDouble(value));
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /**
     * 选择来源单返回
     */
    private void getSourceAfter(List<SalOrder> list, boolean isSaoma) {
        for (int i = 0, size = list.size(); i < size; i++) {
            SalOrder s = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            sr2.setType(2);
            sr2.setSourceFinterId(s.getfId());
            sr2.setSourceFnumber(s.getFbillno());
            sr2.setFitemId(s.getMtl().getfMaterialId());
            sr2.setMtl(s.getMtl());
            sr2.setMtlFnumber(s.getMtl().getfNumber());
            sr2.setUnitFnumber(s.getMtl().getUnit().getUnitNumber());
            sr2.setPoFbillno(s.getFbillno());
            sr2.setPoFmustqty(s.getSalFqty()-s.getSalFstockoutqty());

            sr2.setBatchno(""); // 批号默认为空
            sr2.setSequenceNo(""); // 批号默认为空
            sr2.setFqty(s.getSalFqty()-s.getSalFstockoutqty());
//             是否启用物料的序列号,如果启用了，则数量为1
            if (s.getMtl().getIsSnManager() == 1) {
                sr2.setStockqty(1);
            }
            if (stock != null) {
                sr2.setStockId(stock.getId());
                sr2.setStock(stock);
                sr2.setStockFnumber(stock.getfNumber());
            }
            if (stockA != null) {
                sr2.setStockAreaId(stockA.getId());
                sr2.setStockAName(stockA.getFname());
            }
            if (stockP != null) {
                sr2.setStockPositionId(stockP.getId());
                sr2.setStockPName(stockP.getFname());
            }
            sr2.setCustomerId(s.getCustId());
            sr2.setCustomerName(s.getCustName());

            inventoryOrg = s.getInventoryOrg();
            if(inventoryOrg != null) { // 库存组织
                setEnables(tvInventoryOrg, R.drawable.back_style_gray3, false);
                tvInventoryOrg.setText(inventoryOrg.getName());
                sr2.setReceiveOrgFnumber(inventoryOrg.getNumber());
            }
            salOrg = s.getSalOrg();
            if(salOrg != null) { // 销售组织
                setEnables(tvSalOrg, R.drawable.back_style_gray3, false);
                tvSalOrg.setText(salOrg.getName());
                sr2.setPurOrgFnumber(salOrg.getNumber());
            }

            boolean isAlike = false; // 是否存在重复数据，存在加1
            for (int j = 0, sizej = checkDatas.size(); j < sizej; j++) {
                ScanningRecord2 sr2a = checkDatas.get(j);
                if (s.getfId() == sr2a.getSourceFinterId() && s.getMtl().getId() == sr2a.getMtl().getId()) {
                                    if(sr2a.getStockqty() < sr2a.getFqty()) { // 未装満，加1
                                        sr2a.setStockqty(sr2a.getStockqty()+1);
                    isAlike = true;
                    break;
                                    }
                }
            }
            if (!isAlike) {
                checkDatas.add(sr2);
            }
        }

        if(dataType == '0') dataType = '1';

        if (isSaoma) { // 只有扫码的时候才显示单号
            String seroderNmber = list.get(0).getFbillno();
            setTexts(etSourceNo, seroderNmber);
            sourceBarcode = seroderNmber;
        }
        setEnables(btnSelMat, R.drawable.back_style_gray6, false);
        setEnables(btnAdd, R.drawable.back_style_gray3, false);
        setFocusable(etMatNo); // 物料代码获取焦点

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择（仓库）返回的值
     */
    private void getStockAfter() {
        if (stock != null) {
            setTexts(etWhName, stock.getfName());
            stockBarcode = stock.getfName();
            stockA = null;
            etWhArea.setText("");
            stockP = null;
            etWhPos.setText("");
//             启用库区
            if (stock.isReservoirArea()) {
                setEnables(etWhArea, R.drawable.back_style_blue4, true);
                setEnables(btnWhArea, R.drawable.btn_blue3_selector, true);

            } else {
                stockA = null;
                etWhArea.setText("");
                setEnables(etWhArea, R.drawable.back_style_gray5, false);
                setEnables(btnWhArea, R.drawable.back_style_gray6, false);
            }
//             启用库位
            if (stock.isStorageLocation()) {
                setEnables(etWhPos, R.drawable.back_style_blue4, true);
                setEnables(btnWhPos, R.drawable.btn_blue3_selector, true);
            } else {
                stockP = null;
                etWhPos.setText("");
                setEnables(etWhPos, R.drawable.back_style_gray5, false);
                setEnables(btnWhPos, R.drawable.back_style_gray6, false);
            }
//             长按了，并且启用了库区管理
            if (isStockLong && stock.isReservoirArea()) {
                isStockALong = true;
                Bundle bundle = new Bundle();
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockArea_DialogActivity.class, SEL_STOCKA, bundle);
            }
        }
    }

    /**
     * 选择（库区）返回的值
     */
    private void getStockAAfter() {
        if (stockA != null) {
            setTexts(etWhArea, stockA.getFname());
            stockABarcode = stockA.getFname();
            stockP = null;
            etWhPos.setText("");
//             长按了，并且启用了库位管理
            if (isStockALong && stock.isStorageLocation()) {
                Bundle bundle = new Bundle();
                bundle.putInt("areaId", stockA.getId());
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
        }
    }

    /**
     * 选择（库存组织）返回的值
     */
    private void getOrganizationAfter() {
        if (inventoryOrg != null) {
            tvInventoryOrg.setText(inventoryOrg.getName());
        }
    }

    /**
     * 选择（销售组织）返回的值
     */
    private void getOrganization2After() {
        if (salOrg != null) {
            tvSalOrg.setText(salOrg.getName());
        }
    }

    /**
     * 选择（物料）返回的值
     */
    private void getMtlAfter() {
        if (mtl != null) {
            setTexts(etMatNo, mtl.getfNumber());
            matBarcode = mtl.getfNumber();
            tvMatName.setText(mtl.getfName());
            tvType.setText(mtl.getMaterialSize());
            setTexts(etNum, mtl.getIsSnManager() == 1 ? "1" : "");
            dataType = '2';
            setEnables(etSourceNo, R.drawable.back_style_gray5, false);
            setEnables(btnSourceNo, R.drawable.back_style_gray6, false);
//             物料是否需要输入批号
            if (mtl.getIsSnManager() == 1) {
                setEnables(tvBatchNo, R.drawable.back_style_blue, true);
            } else {
                tvBatchNo.setText("");
                setEnables(tvBatchNo, R.drawable.back_style_gray3, false);
            }
//             是否启用物料的序列号,如果启用了，则数量为1
            if (mtl.getIsSnManager() == 1) {
                setEnables(tvSequenceNo, R.drawable.back_style_blue, true);
                etNum.setText("1");
                setEnables(etNum, R.drawable.back_style_gray3, false);
            } else {
                tvSequenceNo.setText("");
                setEnables(tvSequenceNo, R.drawable.back_style_gray3, false);
                etNum.setText("");
                setEnables(etNum, R.drawable.back_style_blue, true);
            }
        }
    }

    /**
     * 保存方法
     */
    private void run_addScanningRecord() {
        showLoadDialog("保存中...");

        List<ScanningRecord> list = new ArrayList<>();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            ScanningRecord record = new ScanningRecord();
            record.setId(sr2.getPoFid());
//          type: 1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库
            record.setType(2);
            record.setSourceK3Id(sr2.getSourceFinterId());
            record.setSourceFnumber(sr2.getSourceFnumber());
            record.setMtlK3Id(sr2.getFitemId());
            record.setMtlFnumber(sr2.getMtlFnumber());
            record.setUnitFnumber(sr2.getUnitFnumber());
            record.setStockK3Id(sr2.getStockId());
            record.setStockAreaId(sr2.getStockAreaId());
            record.setStockPositionId(sr2.getStockPositionId());
            record.setCustomerK3Id(customer.getFcustId());
            record.setFqty(sr2.getStockqty());
            record.setFdate(Comm.getSysDate(0));
            record.setReceiveOrgFnumber(sr2.getReceiveOrgFnumber());
            record.setPurOrgFnumber(sr2.getPurOrgFnumber());
            record.setPoFid(sr2.getPoFid());
            record.setPoFbillno(sr2.getPoFbillno());
            record.setPoFmustqty(sr2.getPoFmustqty());
            record.setPdaRowno((i+1));
            record.setBatchNo(sr2.getBatchno());
            record.setSequenceNo(sr2.getSequenceNo());
            record.setFqty(sr2.getStockqty());
            record.setFdate(getValues(tvSalDate));
            record.setPdaNo("");
//             得到用户对象
            User user = showObjectToXml(User.class, getResStr(R.string.saveUser));
            record.setOperationId(user.getId());

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
                .post(body)
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
        switch (curViewFlag) {
            case '1':
                mUrl = Consts.getURL("findStockByBarcode");
                barcode = stockBarcode;
                isStockLong = false;
                break;
            case '2':
                mUrl = Consts.getURL("findStockAreaByBarcode");
                barcode = stockABarcode;
                isStockALong = false;
                break;
            case '3':
                mUrl = Consts.getURL("findStockPositionByBarcode");
                barcode = stockPBarcode;
                break;
            case '4':
                mUrl = Consts.getURL("findMtlByBarcode");
                barcode = matBarcode;
                break;
            case '5':
                mUrl = Consts.getURL("findSeOrderListByParam");
                barcode = sourceBarcode;
                break;
        }
        FormBody formBody = new FormBody.Builder()
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
    private void run_findMatIsExistList() {
        showLoadDialog("加载中...");
        StringBuilder strFiterIds = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            strFiterIds.append(sr2.getMtl().getfMaterialId() + ",");
        }
        String mUrl = Consts.getURL("findMatIsExistList");
        FormBody formBody = new FormBody.Builder()
                .add("orderType", "XS") // 单据类型CG代表采购订单，XS销售订单
                .add("strFitemIds", strFiterIds.toString())
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
                Log.e("run_findMatIsExistList --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
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
