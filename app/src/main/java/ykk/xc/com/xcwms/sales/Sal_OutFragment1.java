package ykk.xc.com.xcwms.sales;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import butterknife.OnTouch;
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
import ykk.xc.com.xcwms.basics.PrintMainActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.CombineSalOrderEntry;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.model.EnumDict;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.MaterialBinningRecord;
import ykk.xc.com.xcwms.model.Organization;
import ykk.xc.com.xcwms.model.ScanningRecord;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.pur.ProdOrder;
import ykk.xc.com.xcwms.model.sal.DeliOrder;
import ykk.xc.com.xcwms.model.sal.SalOrder;
import ykk.xc.com.xcwms.purchase.Prod_InMainActivity;
import ykk.xc.com.xcwms.sales.adapter.Sal_OutAdapter;
import ykk.xc.com.xcwms.sales.adapter.Sal_OutFragment1Adapter;
import ykk.xc.com.xcwms.util.JsonUtil;

/**
 * 销售订单出库
 */
public class Sal_OutFragment1 extends BaseFragment {

    @BindView(R.id.et_stock)
    EditText etStock;
    @BindView(R.id.btn_stock)
    Button btnStock;
    @BindView(R.id.et_stockPos)
    EditText etStockPos;
    @BindView(R.id.btn_stockPos)
    Button btnStockPos;
    @BindView(R.id.tv_deptName)
    TextView tvDeptName;
    @BindView(R.id.et_matNo)
    EditText etMatNo;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_receiveOrg)
    TextView tvReceiveOrg;
    @BindView(R.id.tv_salOrg)
    TextView tvSalOrg;
    @BindView(R.id.tv_salDate)
    TextView tvSalDate;
    @BindView(R.id.tv_salMan)
    TextView tvSalMan;
    @BindView(R.id.lin_top)
    LinearLayout linTop;

    private Sal_OutFragment1 context = this;
    private static final int SEL_ORDER = 10, SEL_STOCK = 11, SEL_STOCKP = 12, SEL_DEPT = 13, SEL_ORG = 14, SEL_ORG2 = 15;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, SUCC3B = 203, UNSUCC3B = 503;
    private static final int CODE1 = 1, CODE2 = 2, CODE20 = 20;
    private Customer cust; // 客户
    private Stock stock; // 仓库
    private StockPosition stockP; // 库位
    private Department department; // 部门
    private Organization receiveOrg, salOrg; // 组织
    private Sal_OutFragment1Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String stockBarcode, stockPBarcode, deptBarcode, mtlBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos; // 当前行
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private Activity mContext;
    private Sal_OutMainActivity parent;

    // 消息处理
    private Sal_OutFragment1.MyHandler mHandler = new Sal_OutFragment1.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_OutFragment1> mActivity;

        public MyHandler(Sal_OutFragment1 activity) {
            mActivity = new WeakReference<Sal_OutFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_OutFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset('0');

                        m.checkDatas.clear();
                        m.getBarCodeTableBefore(true);
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
                                m.stock = JsonUtil.strToObject((String) msg.obj, Stock.class);
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.stock = JsonUtil.stringToObject(bt.getRelationObj(), Stock.class);
                                m.getStockAfter();

                                break;
                            case '2': // 库位
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.stockP = JsonUtil.stringToObject(bt.getRelationObj(), StockPosition.class);
                                m.getStockPAfter();

                                break;
                            case '3': // 销售订单
                                BarCodeTable barCodeTable = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                if(!m.getMtlAfter(barCodeTable)) return;
                                if(m.isAlikeCust(barCodeTable, null, '1')) return;
                                m.getBarCodeTableBefore(false);
                                if(!m.getBarCodeTableBeforeSon(bt)) return;
                                m.getBarCodeTableAfter(barCodeTable);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(CODE20, 200);
                        Comm.showWarnDialog(m.mContext,"很抱歉，没能找到数据！");

                        break;
                    case CODE20: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                m.setTexts(m.etStock, m.stockBarcode);
                                break;
                            case '2': // 库位
                                m.setTexts(m.etStockPos, m.stockPBarcode);
                                break;
                            case '3': // 销售订单
                                m.setTexts(m.etMatNo, m.mtlBarcode);
                                break;
                        }

                        break;
                    case SUCC3: // 判断是否存在返回
                        String result = (String) msg.obj;
                        String strBarcode = JsonUtil.strToString(result);
                        if(m.isNULLS(strBarcode).length() > 0) m.isRepeatSave(strBarcode);

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                    case SUCC3B: // 判断是否存在返回
                        String result2 = (String) msg.obj;
                        String strBarcode2 = JsonUtil.strToString(result2);
                        if(m.isNULLS(strBarcode2).length() > 0) m.isRepeatSave(strBarcode2);
                        List<CombineSalOrderEntry> list = JsonUtil.strToList(result2, CombineSalOrderEntry.class);
                        int count = 0; // 统计
                        if(list != null && list.size() > 0) {
                            int size = list.size();
                            for(int i=0; i<size; i++) {
                                CombineSalOrderEntry parent = list.get(i);
                                for(int j=0, size2=m.checkDatas.size(); j<size2; j++) {
                                    ScanningRecord2 son = m.checkDatas.get(j);
                                    if(parent.getfId() == son.getPoFid() && parent.getEntryId() == son.getEntryId()) {
                                        count += 1;
                                        break;
                                    }
                                }
                            }
                            if(size > count) {
                                Comm.showWarnDialog(m.mContext,"当前操作的数据存在拼单发货，请检查拼单的数据！");
                            }
                        }

                        break;
                    case UNSUCC3B: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                    case CODE1: // 清空数据
                        m.etMatNo.setText("");
                        m.mtlBarcode = "";

                        break;
                }
            }
        }
    }

    /**
     * 判断是否有重复的保存
     * @param strBarcode
     * @return
     */
    private boolean isRepeatSave(String strBarcode) {
        String[] barcodeArr = strBarcode.split(",");
        boolean isNext = true; // 是否下一步
        for (int i = 0, len = barcodeArr.length; i < len; i++) {
            for (int j = 0, size = checkDatas.size(); j < size; j++) {
                ScanningRecord2 sr2 = checkDatas.get(j);
                Material mtl = sr2.getMtl();
                // 判断扫码表和当前扫的码对比是否一样
                if (mtl.getIsSnManager() == 1 && barcodeArr[i].equals(checkDatas.get(j).getBarcode())) {
                    Comm.showWarnDialog(mContext,"第" + (i + 1) + "行已出库，不能重复操作！");
                    isNext = false;
                    return false;
                }
            }
        }
        if(isNext) run_addScanningRecord();

        return true;
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sal_out_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Sal_OutMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Sal_OutFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Sal_OutFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0.0", CODE2);
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
        hideSoftInputMode(mContext, etMatNo);
        hideSoftInputMode(mContext, etStock);
        hideSoftInputMode(mContext, etStockPos);
        getUserInfo();

        // 得到默认仓库的值
        defaultStockVal = getXmlValues(spf(getResStr(R.string.saveSystemSet)), EnumDict.STOCKANDPOSTIONTDEFAULTSOURCEOFVALUE.name()).charAt(0);
        if(defaultStockVal == '2') {

            if(user.getStock() != null) {
                stock = user.getStock();
                setTexts(etStock, stock.getfName());
                stockBarcode = stock.getfName();
            }

            if(user.getStockPos() != null) {
                stockP = user.getStockPos();
                setTexts(etStockPos, stockP.getFnumber());
                stockPBarcode = stockP.getFnumber();
            }
        }

        tvSalDate.setText(Comm.getSysDate(7));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFocusable(etMatNo); // 物料代码获取焦点
            }
        },800);
//        setFocusable(etMatNo); // 物料代码获取焦点
    }

    @OnClick({R.id.btn_stock, R.id.btn_stockPos, R.id.btn_save, R.id.btn_clone,
            R.id.tv_orderTypeSel, R.id.tv_receiveOrg, R.id.tv_salOrg, R.id.tv_salDate, R.id.tv_salMan, R.id.lin_rowTitle})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_orderTypeSel: // 订单类型


                break;
            case R.id.btn_stock: // 选择仓库
                isStockLong = false;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
            case R.id.btn_stockPos: // 选择库位
                if (stock == null) {
                    Comm.showWarnDialog(mContext,"请先选择仓库！");
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
            case R.id.tv_receiveOrg: // 发货组织
                showForResult(Organization_DialogActivity.class, SEL_ORG, null);

                break;
            case R.id.tv_salOrg: // 销售组织
                showForResult(Organization_DialogActivity.class, SEL_ORG2, null);

                break;
            case R.id.tv_salDate: // 出库日期
                Comm.showDateDialog(mContext, view, 0);
                break;
            case R.id.tv_prodMan: // 选择业务员

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
            case R.id.lin_rowTitle: // 点击行标题头
                if(linTop.getVisibility() == View.VISIBLE) {
                    linTop.setVisibility(View.GONE);
                } else {
                    linTop.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    /**
     * 选择来源单之前的判断
     */
    private boolean smBefore() {
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
        if(receiveOrg == null) {
            Comm.showWarnDialog(mContext,"请选择发货组织！");
            return false;
        }
        if(salOrg == null) {
            Comm.showWarnDialog(mContext,"请选择销售组织！");
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
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实发数）必须大于0！");
                return false;
            }
            if (sr2.getStockqty() > sr2.getFqty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实发数）不能大于（应发数）！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_stock, R.id.et_stockPos, R.id.et_matNo})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
    }

    @OnLongClick({R.id.btn_stock})
    public boolean onViewLongClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_stock: // 长按选择仓库
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
                        case R.id.et_stock: // 仓库
                            String whName = getValues(etStock).trim();
                            if (isKeyDownEnter(whName, keyCode)) {
                                if (stockBarcode != null && stockBarcode.length() > 0) {
                                    if (stockBarcode.equals(whName)) {
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
                        case R.id.et_stockPos: // 库位
                            String whPos = getValues(etStockPos).trim();
                            if (isKeyDownEnter(whPos, keyCode)) {
                                if (stockPBarcode != null && stockPBarcode.length() > 0) {
                                    if (stockPBarcode.equals(whPos)) {
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
                        case R.id.et_matNo: // 物料
                            String matNo = getValues(etMatNo).trim();
                            if (isKeyDownEnter(matNo, keyCode)) {
                                if (mtlBarcode != null && mtlBarcode.length() > 0) {
                                    if (mtlBarcode.equals(matNo)) {
                                        mtlBarcode = matNo;
                                    } else {
                                        String tmp = matNo.replaceFirst(mtlBarcode, "");
                                        mtlBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    mtlBarcode = matNo.replace("\n", "");
                                }
                                curViewFlag = '3';
                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                    }
                }
                return false;
            }
        };
        etStock.setOnKeyListener(keyListener);
        etStockPos.setOnKeyListener(keyListener);
        etMatNo.setOnKeyListener(keyListener);
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
        etMatNo.setText(""); // 物料代码
        tvCustSel.setText("客户：");
        cust = null;
        setEnables(tvReceiveOrg, R.drawable.back_style_blue, true);
        setEnables(tvSalOrg, R.drawable.back_style_blue, true);
        linTop.setVisibility(View.VISIBLE);
    }

    private void resetSon() {
        getBarCodeTableBefore(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        etStock.setText("");
        etStockPos.setText("");
        tvReceiveOrg.setText("");
        tvSalOrg.setText("");
        stock = null;
        stockP = null;
        department = null;
        receiveOrg = null;
        salOrg = null;
        curViewFlag = '1';
        stockBarcode = null;
        stockPBarcode = null;
        mtlBarcode = null;
        tvSalDate.setText(Comm.getSysDate(7));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
            case SEL_ORG: //查询出库组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    receiveOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG", receiveOrg.getName());
                    if(salOrg == null) {
                        try {
                            salOrg = Comm.deepCopy(receiveOrg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tvSalOrg.setText(salOrg.getName());
                    }
                    getOrgAfter();
                }

                break;
            case SEL_ORG2: //查询生产组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    salOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG2", salOrg.getName());
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
            case CODE2: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setStockqty(num);
//                        checkDatas.get(curPos).setFqty(num);
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
        if(defaultStockVal == '1' && mtl.getStockPos() != null && mtl.getStockPos().getStockId() > 0) {
            stock = mtl.getStock();
            stockP = mtl.getStockPos();
            setTexts(etStock, stock.getfName());
            setTexts(etStockPos, stockP.getFname());
            stockBarcode = stock.getfName();
            stockPBarcode = stockP.getFname();
        } else {
            setTexts(etMatNo, mtlBarcode);
            return smBefore();
        }
        return true;
    }

    /**
     * 判断是相同的客户
     */
    private boolean isAlikeCust(BarCodeTable bct, MaterialBinningRecord mbr, char flag) {
        if(flag == '1') {
            SalOrder s = JsonUtil.stringToObject(bct.getRelationObj(), SalOrder.class);
            if(cust != null && !cust.getCustomerCode().equals(s.getCustNumber())){
                Comm.showWarnDialog(mContext, "客户不同，不能操作，请检查！");
                return true;
            }
        } if(flag == '2') {
            switch (mbr.getCaseId()) {
                case 32: // 销售装箱
                    SalOrder s = JsonUtil.stringToObject(mbr.getRelationObj(), SalOrder.class);
                    if(cust != null && !cust.getCustomerCode().equals(s.getCustNumber())){
                        Comm.showWarnDialog(mContext, "客户不同，不能操作，请检查！");
                        return true;
                    }
                    break;
                case 33: // 发货通知单，复核单装箱
                    DeliOrder deli = JsonUtil.stringToObject(mbr.getRelationObj(), DeliOrder.class);
                    if(cust != null && !cust.getCustomerCode().equals(deli.getCustNumber())){
                        Comm.showWarnDialog(mContext, "客户不同，不能操作，请检查！");
                        return true;
                    }
                    break;
                case 34: // 生产装箱
                    ProdOrder prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(), ProdOrder.class);
                    if(cust != null && !cust.getCustomerCode().equals(prodOrder.getCustNumber())){
                        Comm.showWarnDialog(mContext, "客户不同，不能操作，请检查！");
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    /**
     * 得到条码表的数据
     */
    private void getBarCodeTableBefore(boolean isEnable) {
        if(isEnable) {
            setEnables(tvReceiveOrg, R.drawable.back_style_blue, true);
            setEnables(tvSalOrg, R.drawable.back_style_blue, true);
        } else {
            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
            setEnables(tvSalOrg, R.drawable.back_style_gray3, false);
        }
    }

    /**
     * 来源订单 判断数据
     */
    private boolean getBarCodeTableBeforeSon(BarCodeTable bt) {
        int size = checkDatas.size();
        setTexts(etMatNo, mtlBarcode);
        if(size > 0) {
            for (int i = 0; i < size; i++) {
                ScanningRecord2 sr2 = checkDatas.get(i);
                Material mtl = sr2.getMtl();
                // 如果扫码相同
                if (mtlBarcode.equals(sr2.getBarcode())) {
                    double fqty = 1;
                    // 计量单位数量
                    if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();
                    // 未启用序列号
                    if (mtl.getIsSnManager() == 0) {
                        if (sr2.getFqty() > sr2.getStockqty()) {
                            // 没有启用序列号，并且应发数量大于实发数量
                            sr2.setStockqty(sr2.getStockqty() + fqty);
                            mAdapter.notifyDataSetChanged();
                            return false;
                        } else {
                            // 数量已满
                            Comm.showWarnDialog(mContext, "第" + (i + 1) + "行！，实发数不能大于应发数！");
                            return false;
                        }
                    } else {
                        // 启用序列号
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行！，已有相同的数据！");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 得到条码表的数据
     */
    private void getBarCodeTableAfter(BarCodeTable bt) {
        setTexts(etMatNo, mtlBarcode);
        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setSourceFinterId(bt.getRelationBillId());
        sr2.setSourceFnumber(bt.getRelationBillNumber());
        sr2.setFitemId(bt.getMaterialId());
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
        // 得到销售订单
        SalOrder salOrder = JsonUtil.stringToObject(bt.getRelationObj(), SalOrder.class);
        // 发货组织
        if(salOrder.getInventoryOrgId() > 0) {
            if(receiveOrg == null) receiveOrg = new Organization();
            receiveOrg.setFpkId(salOrder.getInventoryOrgId());
            receiveOrg.setNumber(salOrder.getInventoryOrgNumber());
            receiveOrg.setName(salOrder.getInventoryOrgName());
            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
            tvReceiveOrg.setText(receiveOrg.getName());
        }
        sr2.setReceiveOrgFnumber(salOrder.getInventoryOrgNumber());

        // 销售组织
        if(salOrder.getSalOrgId() > 0) {
            if(salOrg == null) salOrg = new Organization();
            salOrg.setFpkId(salOrder.getSalOrgId());
            salOrg.setNumber(salOrder.getSalOrgNumber());
            salOrg.setName(salOrder.getSalOrgName());

            setEnables(tvSalOrg, R.drawable.back_style_gray3, false);
            tvSalOrg.setText(salOrg.getName());
        }
        sr2.setPurOrgFnumber(salOrder.getSalOrgNumber());

        sr2.setCustomerId(salOrder.getCustId());
        sr2.setCustomerName(salOrder.getCustName());
        sr2.setCustFnumber(salOrder.getCustNumber());
        if(cust == null) cust = new Customer();
        cust.setFcustId(salOrder.getCustId());
        cust.setCustomerCode(salOrder.getCustNumber());
        cust.setCustomerName(salOrder.getCustName());

        tvCustSel.setText("客户："+salOrder.getCustName());
        Material mtl = bt.getMtl();
        sr2.setMtl(mtl);
        sr2.setMtlFnumber(mtl.getfNumber());
        sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
        sr2.setBatchno(bt.getBatchCode());
        sr2.setSequenceNo(bt.getSnCode());
        sr2.setBarcode(bt.getBarcode());
        if (department != null) {
            sr2.setEmpId(department.getFitemID());
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }
        sr2.setFqty(salOrder.getSalFqty()-salOrder.getSalFstockoutqty());
        double fqty = 1;
        // 计量单位数量
        if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();
        sr2.setStockqty(fqty);
        sr2.setPoFid(salOrder.getfId());
        sr2.setEntryId(salOrder.getEntryId());
        sr2.setPoFbillno(salOrder.getFbillno());
        sr2.setPoFmustqty(salOrder.getSalFqty()-salOrder.getSalFstockoutqty());

        checkDatas.add(sr2);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 选择（仓库）返回的值
     */
    private void getStockAfter() {
        if (stock != null) {
            setTexts(etStock, stock.getfName());
            stockBarcode = stock.getfName();
            stockP = null;
            etStockPos.setText("");
            // 启用库位
            if (stock.isStorageLocation()) {
                setEnables(etStockPos, R.drawable.back_style_blue4, true);
                setEnables(btnStockPos, R.drawable.btn_blue3_selector, true);
            } else {
                stockP = null;
                etStockPos.setText("");
                setEnables(etStockPos, R.drawable.back_style_gray5, false);
                setEnables(btnStockPos, R.drawable.back_style_gray6, false);
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
            setTexts(etStockPos, stockP.getFname());
            stockPBarcode = stockP.getFname();
            setFocusable(etMatNo);
        }
    }

    /**
     * 选择（部门）返回的值
     */
    private void getDeptAfter() {
        if (department != null) {
            tvDeptName.setText(department.getDepartmentName());
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
        if (salOrg != null) {
            tvSalOrg.setText(salOrg.getName());
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
            record.setType(2);
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
            record.setCustomerK3Id(sr2.getCustomerId());
            record.setCustFnumber(sr2.getCustFnumber());
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
            record.setFdate(getValues(tvSalDate));
            record.setPdaNo("");
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());
            record.setK3UserFnumber(user.getKdUserNumber());
            record.setSourceType('5');
//            record.setTempId(ism.getId());
//            record.setRelationObj(JsonUtil.objectToString(ism));
            record.setFsrcBillTypeId("SAL_SALEORDER");
            record.setfRuleId("SAL_SALEORDER-SAL_OUTSTOCK");
            record.setFsTableName("T_SAL_ORDERENTRY");

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
        String strCaseId = null;
        switch (curViewFlag) {
            case '1':
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockBarcode;
                isStockLong = false;
                strCaseId = "12";
                break;
            case '2':
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockPBarcode;
                strCaseId = "14";
                break;
            case '3': // 销售订单扫码
                mUrl = Consts.getURL("barCodeTable/findBarcode3ByParam");
                barcode = mtlBarcode;
                strCaseId = "32";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", strCaseId)
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
        StringBuilder strFid = new StringBuilder();
        StringBuilder strEntryId = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if(isNULLS(sr2.getBarcode()).length() > 0) {
                if((i+1) == size) {
                    strBarcode.append(sr2.getBarcode());
                    strFid.append(sr2.getPoFid());
                    strEntryId.append(sr2.getEntryId());

                } else {
                    strBarcode.append(sr2.getBarcode() + ",");
                    strFid.append(sr2.getPoFid() + ",");
                    strEntryId.append(sr2.getEntryId() + ",");
                }
            }
        }
        String mUrl = null;
        mUrl = Consts.getURL("findMatIsExistList3");
        FormBody formBody = new FormBody.Builder()
                .add("orderType", "XS") // 单据类型CG代表采购订单，XS销售订单,生产PD
                .add("strBarcode", strBarcode.toString())
                .add("strFid", strFid.toString())
                .add("strEntryId", strEntryId.toString())
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
                mHandler.sendEmptyMessage(UNSUCC3B);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC3B);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3B, result);
                Log.e("run_findMatIsExistList2 --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) user = showUserByXml();
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

}
