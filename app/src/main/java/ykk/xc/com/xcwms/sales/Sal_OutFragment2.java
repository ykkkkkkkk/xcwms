package ykk.xc.com.xcwms.sales;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ykk.xc.com.xcwms.basics.Express_DialogActivity;
import ykk.xc.com.xcwms.basics.Organization_DialogActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.CombineSalOrderEntry;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.model.EnumDict;
import ykk.xc.com.xcwms.model.ExpressCompany;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.MaterialBinningRecord;
import ykk.xc.com.xcwms.model.Organization;
import ykk.xc.com.xcwms.model.ScanningRecord;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.model.ShrinkOrder;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.pur.ProdOrder;
import ykk.xc.com.xcwms.model.sal.DeliOrder;
import ykk.xc.com.xcwms.model.sal.SalOrder;
import ykk.xc.com.xcwms.sales.adapter.Sal_OutFragment2Adapter;
import ykk.xc.com.xcwms.util.JsonUtil;
/**
 * 扫箱码 出库
 */
public class Sal_OutFragment2 extends BaseFragment {

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
    @BindView(R.id.et_boxCode)
    EditText etBoxCode;
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
    @BindView(R.id.tv_expressCompany)
    TextView tvExpressCompany;
    @BindView(R.id.et_expressNo)
    EditText etExpressNo;
    @BindView(R.id.lin_top)
    LinearLayout linTop;

    private Sal_OutFragment2 context = this;
    private static final int SEL_ORDER = 10, SEL_STOCK = 11, SEL_STOCKP = 12, SEL_DEPT = 13, SEL_ORG = 14, SEL_ORG2 = 15, SEL_EXPRESS = 16,RESET = 17;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, SUCC3B = 203, UNSUCC3B = 503, SUCC4 = 204, UNSUCC4 = 504;
    private static final int CODE1 = 1, CODE2 = 2;
    private Customer cust; // 客户
    private Stock stock; // 仓库
    private StockPosition stockP; // 库位
    private Department department; // 部门
    private Organization receiveOrg, salOrg; // 组织
    private ExpressCompany expressCompany; // 物料公司
    private Sal_OutFragment2Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String stockBarcode, stockPBarcode, deptBarcode, boxBarcode, expressNoBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos; // 当前行
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private Activity mContext;
    private Sal_OutMainActivity parent;
    private int caseId; // 当前单据的方案id
    private Map<String,Boolean> mapBox = new HashMap<String,Boolean>();
    private List<MaterialBinningRecord> mbrList = null; // 保存箱子里的物料
    private List<DeliOrder> deliOrderList = new ArrayList<>(); // 保存发货通知单的

    // 消息处理
    private Sal_OutFragment2.MyHandler mHandler = new Sal_OutFragment2.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_OutFragment2> mActivity;

        public MyHandler(Sal_OutFragment2 activity) {
            mActivity = new WeakReference<Sal_OutFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_OutFragment2 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset('0');

                        m.checkDatas.clear();
                        m.getBarCodeTableBefore(true);
                        m.mAdapter.notifyDataSetChanged();
                        m.caseId = 0;
                        m.mapBox.clear();
                        m.deliOrderList.clear();
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
                            case '3': // 装箱单
                                m.mbrList = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                                MaterialBinningRecord mbr = m.mbrList.get(0);

                                if(m.mapBox.containsKey(m.boxBarcode)) {
                                    Comm.showWarnDialog(m.mContext,"一个箱子只能扫一次！");
                                    return;
                                }
                                // 判断是否做了发货通知单
                                if(mbr.getCaseId() == 34) {
                                    StringBuffer orderNo = new StringBuffer();
                                    StringBuffer orderEntryId = new StringBuffer();
                                    for (int i = 0, size = m.mbrList.size(); i < size; i++) {
                                        MaterialBinningRecord mbr2 = m.mbrList.get(i);
                                        ProdOrder prodOrder = JsonUtil.stringToObject(mbr2.getRelationObj(), ProdOrder.class);
                                        if ((i + 1) == size) {
                                            orderNo.append(prodOrder.getFbillno());
                                            orderEntryId.append(prodOrder.getEntryId());
                                        } else {
                                            orderNo.append(prodOrder.getFbillno() + ",");
                                            orderEntryId.append(prodOrder.getEntryId() + ",");
                                        }
                                    }
                                    m.run_findDeliOrderByProdOrder(orderNo.toString(), orderEntryId.toString());
                                    return;
                                }

                                if(m.isAlikeCust(mbr)) return;
                                if(m.caseId > 0 && m.caseId != mbr.getCaseId()) {
                                    Comm.showWarnDialog(m.mContext,"扫码的箱码单据和当前行的单据不一致！例:(行数据是复核装箱单，你扫了生产装箱单！)");
                                    return;
                                }
                                m.caseId = mbr.getCaseId();

                                m.getBarCodeTableBefore(false);
                                switch (m.caseId) {
                                    case 32: // 销售装箱
                                        m.getSourceAfter(m.mbrList);
                                        break;
                                    case 34: // 生产装箱
//                                        m.getSourceAfter2(m.mbrList);
                                        break;
                                    case 37: // 发货通知单，复核单装箱
                                        m.getSourceAfter3(m.mbrList);
                                        break;
                                }
                                // 把这个箱码保存到map中
                                m.mapBox.put(m.boxBarcode, true);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(RESET, 200);
                        Comm.showWarnDialog(m.mContext,"很抱歉，没能找到数据！");

                        break;
                    case SUCC3: // 判断是否存在返回
                        List<ShrinkOrder> list = JsonUtil.strToList((String) msg.obj, ShrinkOrder.class);
                        for (int i = 0, len = list.size(); i < len; i++) {
                            ShrinkOrder so = list.get(i);
                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
                                ScanningRecord2 sr2 = m.checkDatas.get(j);
                                // 比对订单号和分录id
                                if (so.getFbillno().equals(sr2.getPoFbillno()) && so.getEntryId() == sr2.getEntryId()) {
                                    if((so.getFqty()+sr2.getStockqty()) > sr2.getFqty()) {
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已出库数“"+so.getFqty()+"”，当前超出数“"+(so.getFqty()+sr2.getStockqty() - sr2.getFqty())+"”！");
                                        return;
                                    } else if(so.getFqty() == sr2.getFqty()) {
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已全部出库，不能重复操作！");
                                        return;
                                    }
                                }
                            }
                        }
                        m.run_addScanningRecord();

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
//                    case SUCC3B: // 判断是否存在返回
//                        String result2 = (String) msg.obj;
//                        String strBarcode2 = JsonUtil.strToString(result2);
//                        if(m.isNULLS(strBarcode2).length() > 0) m.isRepeatSave(strBarcode2);
//                        List<CombineSalOrderEntry> list = JsonUtil.strToList(result2, CombineSalOrderEntry.class);
//                        int count = 0; // 统计
//                        if(list != null && list.size() > 0) {
//                            int size = list.size();
//                            for(int i=0; i<size; i++) {
//                                CombineSalOrderEntry parent = list.get(i);
//                                for(int j=0, size2=m.checkDatas.size(); j<size2; j++) {
//                                    ScanningRecord2 son = m.checkDatas.get(j);
//                                    if(parent.getfId() == son.getPoFid() && parent.getEntryId() == son.getEntryId()) {
//                                        count += 1;
//                                        break;
//                                    }
//                                }
//                            }
////                            if(size > count) {
////                                Comm.showWarnDialog(m.mContext,"当前操作的数据与发货单不一致，请检查数据！");
////                            }
//                        }
//
//                        break;
//                    case UNSUCC3B: // 判断是否存在返回
//                        m.run_addScanningRecord();
//
//                        break;
                    case SUCC4: // 查询发货通知单
                        List<DeliOrder> list2 = JsonUtil.strToList((String) msg.obj, DeliOrder.class);
                        boolean isBool = false;
                        String fbillNo = null;
                        int size = list2.size();
                        for(int i=0; i<size; i++) {
                            DeliOrder deli = list2.get(i);
                            if(fbillNo != null && !fbillNo.equals(deli.getFbillno())) {
                                isBool = true;
                            }
                            fbillNo = deli.getFbillno();
                        }
                        if(isBool) {
                            Comm.showWarnDialog(m.mContext,"该箱物料存在"+(size)+"个发货通知单，不允许操作！");
                            return;
                        }
                        if(m.mbrList.size() > list2.size()) {
                            Comm.showWarnDialog(m.mContext,"装箱物与发货通知单不匹配，请检查！");
                            return;
                        }
                        if(m.deliOrderList.size() > 0) {
                            DeliOrder deliOrder2 = list2.get(0);
                            if(!deliOrder2.getFbillno().equals(m.deliOrderList.get(0).getFbillno())) {
                                Comm.showWarnDialog(m.mContext,"本次扫描的的订单和行里的订单不匹配！");
                                return;
                            }
                        }
                        m.deliOrderList.clear();
                        m.deliOrderList.addAll(list2);

                        m.getBarCodeTableBefore(false);
                        m.getSourceAfter2();
                        // 把这个箱码保存到map中
                        m.mapBox.put(m.boxBarcode, true);

                        break;
                    case UNSUCC4: // 查询发货通知单 失败
                        String strError = JsonUtil.strToString((String)msg.obj);
                        Comm.showWarnDialog(m.mContext,strError);

                        break;
                    case CODE1: // 清空数据
                        m.etBoxCode.setText("");
                        m.boxBarcode = "";

                        break;
                    case RESET: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                m.setTexts(m.etStock, m.stockBarcode);
                                break;
                            case '2': // 库位
                                m.setTexts(m.etStockPos, m.stockPBarcode);
                                break;
                            case '3': // 销售装箱单
                                m.setTexts(m.etBoxCode, m.boxBarcode);
                                break;
                            case '9': // 运单号
                                m.setTexts(m.etExpressNo, m.expressNoBarcode);
                                break;
                        }

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
        return inflater.inflate(R.layout.sal_out_fragment2, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Sal_OutMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Sal_OutFragment2Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Sal_OutFragment2Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
//                curPos = position;
//                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", CODE2);
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
        hideSoftInputMode(mContext, etBoxCode);
        hideSoftInputMode(mContext, etStock);
        hideSoftInputMode(mContext, etStockPos);
        hideSoftInputMode(mContext, etExpressNo);
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
                setFocusable(etBoxCode); // 物料代码获取焦点
            }
        },800);
//        setFocusable(etBoxCode); // 物料代码获取焦点
    }

    @OnClick({R.id.btn_stock, R.id.btn_stockPos, R.id.btn_save, R.id.btn_clone,
            R.id.tv_orderTypeSel, R.id.tv_receiveOrg, R.id.tv_salOrg, R.id.tv_salDate, R.id.tv_salMan, R.id.lin_rowTitle, R.id.tv_expressCompany})
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
                run_findInStockSum();
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
            case R.id.tv_expressCompany: // 选择物料公司
                showForResult(Express_DialogActivity.class, SEL_EXPRESS, null);

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
        String express = getValues(tvExpressCompany);
        String expressNo = getValues(etExpressNo).trim();
        if(express.length() == 0 && expressNo.length() > 0) {
            Comm.showWarnDialog(mContext,"请选择物料公司！");
            return false;
        }
        if(express.length() > 0 && expressNo.length() == 0) {
            Comm.showWarnDialog(mContext,"请输入运单号！");
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
            if ((sr2.getMtl().getMtlPack() == null || sr2.getMtl().getMtlPack().getIsMinNumberPack() == 0) && sr2.getStockqty() > sr2.getFqty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实发数）不能大于（应发数）！");
                return false;
            }
            if ((sr2.getMtl().getMtlPack() == null || sr2.getMtl().getMtlPack().getIsMinNumberPack() == 0) && sr2.getStockqty() < sr2.getFqty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实发数）必须等于（应发数）！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_stock, R.id.et_stockPos, R.id.et_boxCode})
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
                        case R.id.et_boxCode: // 物料
                            String boxCode = getValues(etBoxCode).trim();
                            if (isKeyDownEnter(boxCode, keyCode)) {
                                if (!smBefore()) { // 扫码之前的判断
                                    mHandler.sendEmptyMessageDelayed(CODE1, 200);
                                    return false;
                                }
                                if (boxBarcode != null && boxBarcode.length() > 0) {
                                    if (boxBarcode.equals(boxCode)) {
                                        boxBarcode = boxCode;
                                    } else {
                                        String tmp = boxCode.replaceFirst(boxBarcode, "");
                                        boxBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    boxBarcode = boxCode.replace("\n", "");
                                }
                                mHandler.sendEmptyMessage(RESET);
                                curViewFlag = '3';
                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_expressNo: // 运单号
                            String expressNo = getValues(etExpressNo).trim();
                            if (isKeyDownEnter(expressNo, keyCode)) {
                                if (expressNoBarcode != null && expressNoBarcode.length() > 0) {
                                    if (expressNoBarcode.equals(expressNo)) {
                                        expressNoBarcode = expressNo;
                                    } else {
                                        String tmp = expressNo.replaceFirst(expressNoBarcode, "");
                                        expressNoBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    expressNoBarcode = expressNo.replace("\n", "");
                                }
                                mHandler.sendEmptyMessage(RESET);
                                curViewFlag = '9';
                            }

                            break;
                    }
                }
                return false;
            }
        };
        etStock.setOnKeyListener(keyListener);
        etStockPos.setOnKeyListener(keyListener);
        etBoxCode.setOnKeyListener(keyListener);
        etExpressNo.setOnKeyListener(keyListener);

        etExpressNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) parent.isKeyboard = true;
            else parent.isKeyboard = false;
            }
        });
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
        etBoxCode.setText(""); // 物料代码
        tvCustSel.setText("客户：");
        cust = null;
        setEnables(tvReceiveOrg, R.drawable.back_style_blue, true);
        setEnables(tvSalOrg, R.drawable.back_style_blue, true);
        tvExpressCompany.setText("");
        etExpressNo.setText("");
        expressCompany = null;
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
        boxBarcode = null;
        tvSalDate.setText(Comm.getSysDate(7));
    }
    private void resetSon2() {
        etBoxCode.setText("");
        boxBarcode = null;
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
            case SEL_EXPRESS: //查询物料公司	返回
                if (resultCode == Activity.RESULT_OK) {
                    expressCompany = (ExpressCompany) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_EXPRESS", expressCompany.getExpressName());
                    tvExpressCompany.setText(expressCompany.getExpressName());
                    setTexts(etExpressNo, getValues(etExpressNo));
                    setFocusable(etExpressNo);
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
     * 判断是相同的客户
     */
    private boolean isAlikeCust(MaterialBinningRecord mbr) {
        switch (mbr.getCaseId()) {
            case 32: // 销售装箱
                SalOrder s = JsonUtil.stringToObject(mbr.getRelationObj(), SalOrder.class);
                if(cust != null && !cust.getCustomerCode().equals(s.getCustNumber())){
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
            case 37: // 发货通知单，复核单装箱
                DeliOrder deli = JsonUtil.stringToObject(mbr.getRelationObj(), DeliOrder.class);
                if(cust != null && !cust.getCustomerCode().equals(deli.getCustNumber())){
                    Comm.showWarnDialog(mContext, "客户不同，不能操作，请检查！");
                    return true;
                }
                break;

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
     * 选择来源单返回（销售装箱）
     */
    private void getSourceAfter(List<MaterialBinningRecord> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            MaterialBinningRecord mbr = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            sr2.setSourceFinterId(mbr.getRelationBillId());
            sr2.setSourceFnumber(mbr.getRelationBillNumber());
            sr2.setFitemId(mbr.getMaterialId());
            sr2.setMtl(mbr.getMtl());
            sr2.setMtlFnumber(mbr.getMtl().getfNumber());
            sr2.setUnitFnumber(mbr.getMtl().getUnit().getUnitNumber());
            sr2.setPoFid(mbr.getRelationBillId());
            sr2.setPoFbillno(mbr.getRelationBillNumber());
            sr2.setBatchno(mbr.getBatchCode());
            sr2.setSequenceNo(mbr.getSnCode());
            sr2.setBarcode(mbr.getBarcode());

            if (stock != null) {
                sr2.setStockId(stock.getfStockid());
                sr2.setStock(stock);
                sr2.setStockFnumber(stock.getfNumber());
            }
            if (stockP != null) {
                sr2.setStockPositionId(stockP.getId());
                sr2.setStockPName(stockP.getFname());
            }
//            sr2.setSupplierId(mbr.getSupplierId());
//            sr2.setSupplierName(mbr.getSupplierName());
//            sr2.setSupplierFnumber(supplier.getfNumber());
            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
            }
            // 得到销售订单
            SalOrder salOrder = JsonUtil.stringToObject(mbr.getRelationObj(), SalOrder.class);
            sr2.setEntryId(salOrder.getEntryId());
            sr2.setFqty(mbr.getRelationBillFQTY());
            sr2.setPoFmustqty(mbr.getRelationBillFQTY());
            sr2.setStockqty(mbr.getNumber());
            // 发货组织
            if(salOrder.getInventoryOrgId() > 0) {
                if(receiveOrg == null) receiveOrg = new Organization();
                receiveOrg.setFpkId(salOrder.getInventoryOrgId());
                receiveOrg.setNumber(salOrder.getInventoryOrgNumber());
                receiveOrg.setName(salOrder.getInventoryOrgName());
                setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
                tvReceiveOrg.setText(receiveOrg.getName());
                sr2.setReceiveOrgFnumber(receiveOrg.getNumber());
            }

            // 销售组织
            if(salOrder.getSalOrgId() > 0) {
                if(salOrg == null) salOrg = new Organization();
                salOrg.setFpkId(salOrder.getSalOrgId());
                salOrg.setNumber(salOrder.getSalOrgNumber());
                salOrg.setName(salOrder.getSalOrgName());

                setEnables(tvSalOrg, R.drawable.back_style_gray3, false);
                tvSalOrg.setText(salOrg.getName());
                sr2.setPurOrgFnumber(salOrg.getNumber());
            }
            sr2.setCustomerId(salOrder.getCustId());
            sr2.setCustomerName(salOrder.getCustName());
            sr2.setCustFnumber(salOrder.getCustNumber());
            if(cust == null) {
                cust = new Customer();
                cust.setFcustId(salOrder.getCustId());
                cust.setCustomerCode(salOrder.getCustNumber());
                cust.setCustomerName(salOrder.getCustName());

                tvCustSel.setText("客户："+salOrder.getCustName());
            }
            checkDatas.add(sr2);
        }
        setFocusable(etBoxCode); // 物料代码获取焦点

        mAdapter.notifyDataSetChanged();
    }
    /**
     * 选择来源单返回（生产装箱）
     */
    private void getSourceAfter2() {
        int size = deliOrderList.size();
        if(checkDatas.size() == 0) {
            for (int i = 0; i < size; i++) {
                DeliOrder deliOrder = deliOrderList.get(i);
                ScanningRecord2 sr2 = new ScanningRecord2();
                sr2.setSourceFinterId(deliOrder.getfId());
                sr2.setSourceFnumber(deliOrder.getFbillno());
                sr2.setFitemId(deliOrder.getMtlId());
                sr2.setMtl(deliOrder.getMtl());
                sr2.setMtlFnumber(deliOrder.getMtlFnumber());
                sr2.setUnitFnumber(deliOrder.getMtl().getUnit().getUnitNumber());
                sr2.setPoFid(deliOrder.getfId());
                sr2.setPoFbillno(deliOrder.getFbillno());
//            sr2.setBatchno(deliOrder.getBatchCode());
//            sr2.setSequenceNo(deliOrder.getSnCode());
//            sr2.setBarcode(deliOrder.getBarcode());

                if (stock != null) {
                    sr2.setStockId(stock.getfStockid());
                    sr2.setStock(stock);
                    sr2.setStockFnumber(stock.getfNumber());
                }
                if (stockP != null) {
                    sr2.setStockPositionId(stockP.getId());
                    sr2.setStockPName(stockP.getFname());
                }
//            sr2.setSupplierId(mbr.getSupplierId());
//            sr2.setSupplierName(mbr.getSupplierName());
//            sr2.setSupplierFnumber(supplier.getfNumber());
                if (department != null) {
                    sr2.setEmpId(department.getFitemID()); // 部门
                    sr2.setDepartmentFnumber(department.getDepartmentNumber());
                }
                sr2.setEntryId(deliOrder.getEntryId());
                sr2.setFqty(deliOrder.getDeliFqty());
                sr2.setPoFmustqty(deliOrder.getDeliFqty());
//            sr2.setStockqty(deliOrder.getNumber());
                // 发货组织
                if (receiveOrg == null) receiveOrg = new Organization();
                receiveOrg.setFpkId(deliOrder.getDeliOrgId());
                receiveOrg.setNumber(deliOrder.getDeliOrgNumber());
                receiveOrg.setName(deliOrder.getDeliOrgName());
                setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
                tvReceiveOrg.setText(receiveOrg.getName());
                sr2.setReceiveOrgFnumber(receiveOrg.getNumber());

                if (salOrg == null) salOrg = new Organization();
                salOrg.setFpkId(deliOrder.getDeliOrgId());
                salOrg.setNumber(deliOrder.getDeliOrgNumber());
                salOrg.setName(deliOrder.getDeliOrgName());

                setEnables(tvSalOrg, R.drawable.back_style_gray3, false);
                tvSalOrg.setText(salOrg.getName());
                sr2.setPurOrgFnumber(salOrg.getNumber());

                sr2.setCustomerId(deliOrder.getCustId());
                sr2.setCustomerName(deliOrder.getCustName());
                sr2.setCustFnumber(deliOrder.getCustNumber());
                if (cust == null) {
                    cust = new Customer();
                    cust.setFcustId(deliOrder.getCustId());
                    cust.setCustomerCode(deliOrder.getCustNumber());
                    cust.setCustomerName(deliOrder.getCustName());

                    tvCustSel.setText("客户：" + deliOrder.getCustName());
                }
                sr2.setSourceType('7');
//            sr2.setTempId(ism.getId());
//            sr2.setRelationObj(JsonUtil.objectToString(ism));
                sr2.setFsrcBillTypeId("SAL_DELIVERYNOTICE");
                sr2.setfRuleId("SAL_DELIVERYNOTICE-SAL_OUTSTOCK");
                sr2.setFsTableName("T_SAL_DELIVERYNOTICEENTRY");
                checkDatas.add(sr2);
            }
        }
        int size2 = mbrList.size();
        for(int i=0; i<size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            for(int j=0; j<size2; j++) {
                MaterialBinningRecord mbr = mbrList.get(j);
                if(mbr.getMaterialId() == sr2.getMtl().getfMaterialId()) {
                    sr2.setStockqty(sr2.getStockqty()+mbr.getNumber());
                    break;
                }
            }
        }
        setFocusable(etBoxCode); // 物料代码获取焦点

        mAdapter.notifyDataSetChanged();
    }
    // old
//    private void getSourceAfter2(List<MaterialBinningRecord> list) {
//        for (int i = 0, size = list.size(); i < size; i++) {
//            MaterialBinningRecord mbr = list.get(i);
//            ScanningRecord2 sr2 = new ScanningRecord2();
//            sr2.setSourceFinterId(mbr.getRelationBillId());
//            sr2.setSourceFnumber(mbr.getRelationBillNumber());
//            sr2.setFitemId(mbr.getMaterialId());
//            sr2.setMtl(mbr.getMtl());
//            sr2.setMtlFnumber(mbr.getMtl().getfNumber());
//            sr2.setUnitFnumber(mbr.getMtl().getUnit().getUnitNumber());
//            sr2.setPoFid(mbr.getRelationBillId());
//            sr2.setPoFbillno(mbr.getRelationBillNumber());
//            sr2.setBatchno(mbr.getBatchCode());
//            sr2.setSequenceNo(mbr.getSnCode());
//            sr2.setBarcode(mbr.getBarcode());
//
//            if (stock != null) {
//                sr2.setStockId(stock.getfStockid());
//                sr2.setStock(stock);
//                sr2.setStockFnumber(stock.getfNumber());
//            }
//            if (stockP != null) {
//                sr2.setStockPositionId(stockP.getId());
//                sr2.setStockPName(stockP.getFname());
//            }
////            sr2.setSupplierId(mbr.getSupplierId());
////            sr2.setSupplierName(mbr.getSupplierName());
////            sr2.setSupplierFnumber(supplier.getfNumber());
//            if (department != null) {
//                sr2.setEmpId(department.getFitemID()); // 部门
//                sr2.setDepartmentFnumber(department.getDepartmentNumber());
//            }
//            // 得到生产订单
//            ProdOrder prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(), ProdOrder.class);
//            sr2.setEntryId(prodOrder.getEntryId());
//            sr2.setFqty(mbr.getRelationBillFQTY());
//            sr2.setPoFmustqty(mbr.getRelationBillFQTY());
//            sr2.setStockqty(mbr.getNumber());
//            // 发货组织
//            if(receiveOrg == null) receiveOrg = new Organization();
//            receiveOrg.setFpkId(prodOrder.getProdOrgId());
//            receiveOrg.setNumber(prodOrder.getProdOrgNumber());
//            receiveOrg.setName(prodOrder.getProdOrgName());
//            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
//            tvReceiveOrg.setText(receiveOrg.getName());
//            sr2.setReceiveOrgFnumber(receiveOrg.getNumber());
//
//            if(salOrg == null) salOrg = new Organization();
//            salOrg.setFpkId(prodOrder.getProdOrgId());
//            salOrg.setNumber(prodOrder.getProdOrgNumber());
//            salOrg.setName(prodOrder.getProdOrgName());
//
//            setEnables(tvSalOrg, R.drawable.back_style_gray3, false);
//            tvSalOrg.setText(salOrg.getName());
//            sr2.setPurOrgFnumber(salOrg.getNumber());
//
//            sr2.setCustomerId(prodOrder.getCustId());
//            sr2.setCustomerName(prodOrder.getCustName());
//            sr2.setCustFnumber(prodOrder.getCustNumber());
//            if(cust == null) {
//                cust = new Customer();
//                cust.setFcustId(prodOrder.getCustId());
//                cust.setCustomerCode(prodOrder.getCustNumber());
//                cust.setCustomerName(prodOrder.getCustName());
//
//                tvCustSel.setText("客户："+prodOrder.getCustName());
//            }
//            sr2.setSourceType('7');
////            sr2.setTempId(ism.getId());
////            sr2.setRelationObj(JsonUtil.objectToString(ism));
//            sr2.setFsrcBillTypeId("PRD_MO");
//            sr2.setfRuleId("SAL_SALEORDER-SAL_OUTSTOCK");
//            sr2.setFsTableName("T_PRD_MOENTRY");
//            checkDatas.add(sr2);
//        }
//        setFocusable(etBoxCode); // 物料代码获取焦点
//
//        mAdapter.notifyDataSetChanged();
//    }
    /**
     * 选择来源单返回（发货订单，复核单装箱）
     */
    private void getSourceAfter3(List<MaterialBinningRecord> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            MaterialBinningRecord mbr = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            sr2.setSourceFinterId(mbr.getRelationBillId());
            sr2.setSourceFnumber(mbr.getRelationBillNumber());
            sr2.setFitemId(mbr.getMaterialId());
            sr2.setMtl(mbr.getMtl());
            sr2.setMtlFnumber(mbr.getMtl().getfNumber());
            sr2.setUnitFnumber(mbr.getMtl().getUnit().getUnitNumber());
            sr2.setPoFid(mbr.getRelationBillId());
            sr2.setPoFbillno(mbr.getRelationBillNumber());
            sr2.setBatchno(mbr.getBatchCode());
            sr2.setSequenceNo(mbr.getSnCode());
            sr2.setBarcode(mbr.getBarcode());

            if (stock != null) {
                sr2.setStockId(stock.getfStockid());
                sr2.setStock(stock);
                sr2.setStockFnumber(stock.getfNumber());
            }
            if (stockP != null) {
                sr2.setStockPositionId(stockP.getId());
                sr2.setStockPName(stockP.getFname());
            }
//            sr2.setSupplierId(mbr.getSupplierId());
//            sr2.setSupplierName(mbr.getSupplierName());
//            sr2.setSupplierFnumber(supplier.getfNumber());
            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
            }
            // 得到发货订单
            DeliOrder deliOrder = JsonUtil.stringToObject(mbr.getRelationObj(), DeliOrder.class);
            sr2.setEntryId(deliOrder.getEntryId());
            sr2.setFqty(mbr.getRelationBillFQTY());
            sr2.setPoFmustqty(mbr.getRelationBillFQTY());
            sr2.setStockqty(mbr.getNumber());
            // 发货组织
            if(deliOrder.getDeliOrgId() > 0) {
                if(receiveOrg == null) receiveOrg = new Organization();
                receiveOrg.setFpkId(deliOrder.getDeliOrgId());
                receiveOrg.setNumber(deliOrder.getDeliOrgNumber());
                receiveOrg.setName(deliOrder.getDeliOrgName());
                setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
                tvReceiveOrg.setText(receiveOrg.getName());
                sr2.setReceiveOrgFnumber(receiveOrg.getNumber());

                // 销售组织
                if(salOrg == null) salOrg = new Organization();
                salOrg.setFpkId(deliOrder.getDeliOrgId());
                salOrg.setNumber(deliOrder.getDeliOrgNumber());
                salOrg.setName(deliOrder.getDeliOrgName());

                setEnables(tvSalOrg, R.drawable.back_style_gray3, false);
                tvSalOrg.setText(salOrg.getName());
                sr2.setPurOrgFnumber(salOrg.getNumber());
            }
            sr2.setCustomerId(deliOrder.getCustId());
            sr2.setCustomerName(deliOrder.getCustName());
            sr2.setCustFnumber(deliOrder.getCustNumber());
            if(cust == null) {
                cust = new Customer();
                cust.setFcustId(deliOrder.getCustId());
                cust.setCustomerCode(deliOrder.getCustNumber());
                cust.setCustomerName(deliOrder.getCustName());

                tvCustSel.setText("客户："+deliOrder.getCustName());
            }
            sr2.setSourceType('9');
//            sr2.setTempId(ism.getId());
//            sr2.setRelationObj(JsonUtil.objectToString(ism));
            sr2.setFsrcBillTypeId("SAL_DELIVERYNOTICE");
            sr2.setfRuleId("SAL_DELIVERYNOTICE-SAL_OUTSTOCK");
            sr2.setFsTableName("T_SAL_DELIVERYNOTICEENTRY");
            checkDatas.add(sr2);
        }
        setFocusable(etBoxCode); // 物料代码获取焦点

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
            setFocusable(etBoxCode);
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
            record.setSourceType(sr2.getSourceType());
//            record.setTempId(sr2.getTempId());
//            record.setRelationObj(sr2.getRelationObj());
            record.setFsrcBillTypeId(sr2.getFsrcBillTypeId());
            record.setfRuleId(sr2.getfRuleId());
            record.setFsTableName(sr2.getFsTableName());
            record.setFcarriageNo(getValues(etExpressNo).trim());
            if(expressCompany != null) {
                record.setExpressNumber(expressCompany.getExpressNumber());
            }

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
            case '3': // 箱子扫码
                mUrl = Consts.getURL("materialBinningRecord/findList3ByParam");
                barcode = boxBarcode;
                strCaseId = "";
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
    private void run_findInStockSum() {
        showLoadDialog("加载中...");
        StringBuilder strFbillno = new StringBuilder();
        StringBuilder strEntryId = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if((i+1) == size) {
                strFbillno.append(sr2.getPoFbillno());
                strEntryId.append(sr2.getEntryId());
            } else {
                strFbillno.append(sr2.getPoFbillno() + ",");
                strEntryId.append(sr2.getEntryId() + ",");
            }
        }
        String mUrl = Consts.getURL("scanningRecord/findInStockSum");
        FormBody formBody = new FormBody.Builder()
                .add("fbillType", "5") // fbillType  1：采购订单入库，2：收料任务单入库，3：生产订单入库，4：销售订单出库，5：发货通知单出库
                .add("strFbillno", strFbillno.toString())
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
                Log.e("run_findInStockSum --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_findDeliOrderByProdOrder(String strOrderNo, String strOrderEntryId) {
        showLoadDialog("加载中...");
        String mUrl = mUrl = Consts.getURL("findDeliOrderByProdOrder");;
        FormBody formBody = new FormBody.Builder()
                .add("strOrderNo", strOrderNo)
                .add("strOrderEntryId", strOrderEntryId)
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
                mHandler.sendEmptyMessage(UNSUCC4);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC4, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC4, result);
                Log.e("run_smGetDatas --> onResponse", result);
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