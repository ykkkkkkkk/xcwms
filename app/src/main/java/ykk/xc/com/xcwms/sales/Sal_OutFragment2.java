package ykk.xc.com.xcwms.sales;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import ykk.xc.com.xcwms.basics.Staff_DialogActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.model.EnumDict;
import ykk.xc.com.xcwms.model.ExpressCompany;
import ykk.xc.com.xcwms.model.MaterialBinningRecord;
import ykk.xc.com.xcwms.model.Organization;
import ykk.xc.com.xcwms.model.ScanningRecord;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.model.ScanningRecordTok3;
import ykk.xc.com.xcwms.model.ShrinkOrder;
import ykk.xc.com.xcwms.model.Staff;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.pur.ProdOrder;
import ykk.xc.com.xcwms.model.sal.DeliOrder;
import ykk.xc.com.xcwms.model.sal.SalOrder;
import ykk.xc.com.xcwms.sales.adapter.Sal_OutFragment2Adapter;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.interfaces.IFragmentExec;

/**
 * 扫箱码 出库
 */
public class Sal_OutFragment2 extends BaseFragment implements IFragmentExec {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
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
    @BindView(R.id.tv_stockStaff)
    TextView tvStockStaff;
    @BindView(R.id.tv_expressCompany)
    TextView tvExpressCompany;
    @BindView(R.id.et_expressNo)
    EditText etExpressNo;
    @BindView(R.id.lin_top)
    LinearLayout linTop;

    private Sal_OutFragment2 context = this;
    private static final int SEL_ORDER = 10, SEL_DEPT = 11, SEL_ORG = 12, SEL_ORG2 = 13, SEL_EXPRESS = 14,RESET = 15, SEL_STOCK2 = 16, SEL_STOCKP2 = 17, SEL_STAFF = 18, PAD_SM = 19;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503, SUCC4 = 204, UNSUCC4 = 504;
    private static final int SETFOCUS = 1, CODE2 = 2;
    private Customer cust; // 客户
    private Stock stock, stock2; // 仓库
    private StockPosition stockP, stockP2; // 库位
    private Staff stockStaff; // 仓管员
    private Department department; // 部门
    private Organization receiveOrg, salOrg; // 组织
    private ExpressCompany expressCompany; // 物料公司
    private Sal_OutFragment2Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String boxBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：箱码
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private Activity mContext;
    private Sal_OutMainActivity parent;
    private int caseId; // 当前单据的方案id
    private Map<String,Boolean> mapBox = new HashMap<String,Boolean>(); // 记录箱码
    private List<MaterialBinningRecord> mbrList = null; // 保存箱子里的物料
    private List<DeliOrder> deliOrderList = new ArrayList<>(); // 保存发货通知单的
    private String k3Number; // 记录传递到k3返回的单号
    private List<Map<String, Object>> listMaps = new ArrayList<>(); // 记录多少个箱子和箱子里的物料
    private boolean isTextChange; // 是否进入TextChange事件

    // 消息处理
    private Sal_OutFragment2.MyHandler mHandler = new Sal_OutFragment2.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_OutFragment2> mActivity;

        public MyHandler(Sal_OutFragment2 activity) {
            mActivity = new WeakReference<Sal_OutFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_OutFragment2 m = mActivity.get();
            String errMsg = null;
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
//                        m.reset('0');
//
//                        m.checkDatas.clear();
//                        m.getBarCodeTableBefore(true);
//                        m.mAdapter.notifyDataSetChanged();
//                        m.caseId = 0;
//                        m.mapBox.clear();
//                        m.deliOrderList.clear();
                        m.btnSave.setVisibility(View.GONE);
                        Comm.showWarnDialog(m.mContext,"保存成功，请点击“审核按钮”！");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                    case PASS: // 审核成功 返回
                        m.k3Number = null;
                        m.btnSave.setVisibility(View.VISIBLE);
                        m.reset('0');

                        m.checkDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        m.caseId = 0;
                        m.mapBox.clear();
                        m.deliOrderList.clear();
//                        Comm.showWarnDialog(m.mContext,"审核成功✔");
                        m.toasts("审核成功✔");
                        m.parent.setFragment2Print(2, m.listMaps);

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = null;
                        switch (m.curViewFlag) {
                            case '1': // 装箱单
                                m.mbrList = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                                MaterialBinningRecord mbr = m.mbrList.get(0);
                                // 通过mapBox来清空箱子记录的list
                                if(m.mapBox.size() == 0)  m.listMaps.clear();

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
                                    // 记录箱子和物料条数
                                    Map<String, Object> mapDatas = new HashMap<>();
                                    mapDatas.put("caseId", 34);
                                    mapDatas.put("barcode", m.boxBarcode);
                                    mapDatas.put("list", m.mbrList);
                                    m.listMaps.add(mapDatas);

                                    m.run_findDeliOrderByProdOrder(orderNo.toString(), orderEntryId.toString());
                                    return;
                                }

                                if(m.isAlikeCust(mbr)) return;
                                if(m.caseId > 0 && m.caseId != mbr.getCaseId()) {
                                    Comm.showWarnDialog(m.mContext,"扫码的箱码单据和当前行的单据不一致！例:(行数据是复核装箱单，你扫了生产装箱单！)");
                                    return;
                                }
                                m.caseId = mbr.getCaseId();

                                // 记录箱子和物料条数
                                Map<String, Object> mapDatas = new HashMap<>();
                                mapDatas.put("caseId", 37);
                                mapDatas.put("barcode", m.boxBarcode);
                                mapDatas.put("list", m.mbrList);
                                m.listMaps.add(mapDatas);

                                switch (m.caseId) {
                                    case 32: // 销售装箱
                                        m.getSourceAfter(m.mbrList);
                                        break;
                                    case 34: // 生产装箱
//                                        m.getSourceAfter2(m.mbrList);
                                        break;
                                    case 37: // 发货通知单，复核单装箱
                                        MaterialBinningRecord tmpMbr = m.mbrList.get(0);
                                        DeliOrder deliOrder = JsonUtil.stringToObject(tmpMbr.getRelationObj(), DeliOrder.class);
                                        String exitType = m.isNULLS(deliOrder.getExitType());
                                        if(!exitType.equals("销售出库")) {
                                            Comm.showWarnDialog(m.mContext,"该销售订单的出库类型不是销售出库，不能操作");
                                            return;
                                        }
                                        // 把这个箱码保存到map中
                                        m.mapBox.put(m.boxBarcode, true);
                                        m.getSourceAfter3(m.mbrList);
                                        break;
                                }

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(RESET, 200);
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(Comm.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器超时，请重试！";
                        }
                        Comm.showWarnDialog(m.mContext, errMsg);

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
                    case SUCC4: // 查询发货通知单
                        List<DeliOrder> list2 = JsonUtil.strToList((String) msg.obj, DeliOrder.class);
                        DeliOrder deliOrderP = list2.get(0);
                        String exitType = m.isNULLS(deliOrderP.getExitType());
                        if(!exitType.equals("销售出库")) {
                            Comm.showWarnDialog(m.mContext,"该销售订单的出库类型不是销售出库，不能操作");
                            return;
                        }
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
                            if(!deliOrderP.getFbillno().equals(m.deliOrderList.get(0).getFbillno())) {
                                Comm.showWarnDialog(m.mContext,"本次扫描的的订单和行里的订单不匹配！");
                                return;
                            }
                        }
                        m.deliOrderList.clear();
                        m.deliOrderList.addAll(list2);

                        // 把这个箱码保存到map中
                        m.mapBox.put(m.boxBarcode, true);
                        m.getSourceAfter2();

                        break;
                    case UNSUCC4: // 查询发货通知单 失败
                        String strError = JsonUtil.strToString((String)msg.obj);
                        Comm.showWarnDialog(m.mContext,strError);

                        break;
                    case RESET: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 仓库
//                                m.setTexts(m.etStock, m.stockBarcode);
                                break;
                            case '2': // 库位
//                                m.setTexts(m.etStockPos, m.stockPBarcode);
                                break;
//                            case '3': // 销售装箱单
//                                m.setTexts(m.etBoxCode, m.boxBarcode);
//                                break;
//                            case '9': // 运单号
//                                m.setTexts(m.etExpressNo, m.expressNoBarcode);
//                                break;
                        }

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etBoxCode);

                        break;
                    case PAD_SM: // pad扫码
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '1': // 装箱单
                                etName = m.getValues(m.etBoxCode);
                                if (m.boxBarcode != null && m.boxBarcode.length() > 0) {
                                    if(m.boxBarcode.equals(etName)) {
                                        m.boxBarcode = etName;
                                    } else m.boxBarcode = etName.replaceFirst(m.boxBarcode, "");

                                } else m.boxBarcode = etName;
                                m.setTexts(m.etBoxCode, m.boxBarcode);
                                // 执行查询方法
                                m.run_smGetDatas(m.boxBarcode);

                                break;
                        }

                        break;
                }
            }
        }
    }

    @Override
    public void onFragmenExec() {
        listMaps.clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (Sal_OutMainActivity) context;
        parent.setFragmentExec(this);
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sal_out_fragment2, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
//        parent = (Sal_OutMainActivity) mContext;

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
        hideSoftInputMode(mContext, etBoxCode);
        hideSoftInputMode(mContext, etExpressNo);
        getUserInfo();

        // 得到默认仓库的值
        defaultStockVal = getXmlValues(spf(getResStr(R.string.saveSystemSet)), EnumDict.STOCKANDPOSTIONTDEFAULTSOURCEOFVALUE.name()).charAt(0);
        if(defaultStockVal == '2') {

            if(user.getStock() != null) {
                stock = user.getStock();
                saveObjectToXml(stock, "strStock", getResStr(R.string.saveUser));
            }

            if(user.getStockPos() != null) {
                stockP = user.getStockPos();
                saveObjectToXml(stockP, "strStockPos", getResStr(R.string.saveUser));
            }
        } else {
            stock = showObjectByXml(Stock.class, "strStock", getResStr(R.string.saveUser));
            stockP = showObjectByXml(StockPosition.class, "strStockPos", getResStr(R.string.saveUser));
        }
        if(user.getStaff() != null) {
            stockStaff = user.getStaff();
        } else {
            stockStaff = showObjectByXml(Staff.class, "strStockStaff", getResStr(R.string.saveUser));
        }
        department = showObjectByXml(Department.class, "strDepartment", getResStr(R.string.saveUser));
        // 赋值
        if(stockStaff != null) tvStockStaff.setText(stockStaff.getName());
        if(department != null) tvDeptSel.setText(department.getDepartmentName());

        tvSalDate.setText(Comm.getSysDate(7));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() { setFocusable(etBoxCode); // 物料代码获取焦点
                }
            },200);
        }
    }

    @OnClick({R.id.btn_save, R.id.btn_pass, R.id.btn_clone, R.id.tv_orderTypeSel, R.id.tv_receiveOrg, R.id.tv_salOrg, R.id.tv_salDate,
              R.id.tv_deptSel, R.id.tv_stockStaff, R.id.lin_rowTitle, R.id.tv_expressCompany})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_orderTypeSel: // 订单类型


                break;
            case R.id.tv_deptSel: // 选择部门
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

                break;
            case R.id.tv_receiveOrg: // 发货组织
//                showForResult(Organization_DialogActivity.class, SEL_ORG, null);

                break;
            case R.id.tv_salOrg: // 销售组织
//                showForResult(Organization_DialogActivity.class, SEL_ORG2, null);

                break;
            case R.id.tv_salDate: // 出库日期
                Comm.showDateDialog(mContext, view, 0);
                break;
            case R.id.tv_stockStaff: // 选择仓管员
                bundle = new Bundle();
                bundle.putInt("isload", 0);
                showForResult(Staff_DialogActivity.class, SEL_STAFF, bundle);

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(mContext.getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                run_findInStockSum();
//                run_addScanningRecord();

                break;
            case R.id.btn_pass: // 审核
                if(k3Number == null) {
                    Comm.showWarnDialog(mContext,"请先保存，然后审核！");
                    return;
                }
                run_submitAndPass();

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
            ScanningRecordTok3 srToK3 = sr2.getSrTok3();

            // 仓管员
            if(stockStaff != null) srToK3.setStockStaffNumber(stockStaff.getNumber());
            
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，（实发数）必须大于0！");
                return false;
            }
            if (sr2.getStockId() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，请选择（仓库）！");
                return false;
            }
            if ((sr2.getMtl().getMtlPack() == null || sr2.getMtl().getMtlPack().getIsMinNumberPack() == 0) && sr2.getStockqty() > sr2.getFqty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，（实发数）不能大于（应发数）！");
                return false;
            }
            if ((sr2.getMtl().getMtlPack() == null || sr2.getMtl().getMtlPack().getIsMinNumberPack() == 0) && sr2.getStockqty() < sr2.getFqty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，（实发数）必须等于（应发数）！");
                return false;
            }

        }
        return true;
    }

    @OnFocusChange({R.id.et_boxCode})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
    }

    @Override
    public void setListener() {
        // 箱码
        etBoxCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                curViewFlag = '1';
//                boxBarcode = s.toString();
//                // 执行查询方法
//                run_smGetDatas(boxBarcode);

                if(!isTextChange) {
                    if (baseIsPad) {
                        isTextChange = true;
                        mHandler.sendEmptyMessageDelayed(PAD_SM,600);
                    } else {
                        boxBarcode = s.toString();
                        // 执行查询方法
                        run_smGetDatas(boxBarcode);
                    }
                }
            }
        });

        etExpressNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) parent.isKeyboard = true;
            else parent.isKeyboard = false;
            }
        });
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
        tvExpressCompany.setText("");
        etExpressNo.setText("");
        expressCompany = null;
        linTop.setVisibility(View.VISIBLE);
    }

    private void resetSon() {
        if(user.getStaff() != null) {
            stockStaff = user.getStaff();
            tvStockStaff.setText(stockStaff.getName());
        }
        listMaps.clear();
        k3Number = null;
        btnSave.setVisibility(View.VISIBLE);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvReceiveOrg.setText("");
        tvSalOrg.setText("");
        stock = null;
        stockP = null;
        department = null;
        receiveOrg = null;
        salOrg = null;
        curViewFlag = '1';
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
                        stockAllFill(false);
                        saveObjectToXml(stock2, "strStock", getResStr(R.string.saveUser));

                    }
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    stockAllFill(true);
                    saveObjectToXml(stock2, "strStock", getResStr(R.string.saveUser));
                    saveObjectToXml(stockP2, "strStockPos", getResStr(R.string.saveUser));

                }

                break;
            case SEL_ORG: //查询出库组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    receiveOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG", receiveOrg.getName());
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
            case SEL_STAFF: // 仓管员	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockStaff = (Staff) data.getSerializableExtra("staff");
                    tvStockStaff.setText(stockStaff.getName());
                    saveObjectToXml(stockStaff, "strStockStaff", getResStr(R.string.saveUser));
                }
                break;
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS,300);
    }

    /**
     * 部门数据全部填充
     */
    private void stockAllFill(boolean inStockPosData) {
        int size = checkDatas.size();
        boolean isBool = false;
        for(int i=0; i<size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if(sr2.getStockId() > 0) {
                isBool = true;
                break;
            }
        }
        if(isBool) {
            ScanningRecord2 sr2 = checkDatas.get(curPos);
            sr2.setStockId(stock2.getfStockid());
            sr2.setStockFnumber(stock2.getfNumber());
            sr2.setStockName(stock2.getfName());
            sr2.setStock(stock2);
            if(inStockPosData) {
                sr2.setStockPos(stockP2);
                sr2.setStockPositionId(stockP2.getId());
                sr2.setStockPName(stockP2.getFname());
            }
        } else { // 全部都为空的时候，选择任意全部填充
            for (int i = 0; i < size; i++) {
                ScanningRecord2 sr2 = checkDatas.get(i);
                sr2.setStockId(stock2.getfStockid());
                sr2.setStockFnumber(stock2.getfNumber());
                sr2.setStockName(stock2.getfName());
                sr2.setStock(stock2);
                if(inStockPosData) {
                    sr2.setStockPos(stockP2);
                    sr2.setStockPositionId(stockP2.getId());
                    sr2.setStockPName(stockP2.getFname());
                }
            }
        }
        mAdapter.notifyDataSetChanged();
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
     * 选择来源单返回（销售装箱）
     */
    private void getSourceAfter(List<MaterialBinningRecord> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            MaterialBinningRecord mbr = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            sr2.setSourceK3Id(mbr.getRelationBillId());
            sr2.setSourceFnumber(mbr.getRelationBillNumber());
            sr2.setMtlId(mbr.getMaterialId());
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
                sr2.setSourceK3Id(deliOrder.getfId());
                sr2.setSourceFnumber(deliOrder.getFbillno());
                sr2.setMtlId(deliOrder.getMtlId());
                sr2.setMtl(deliOrder.getMtl());
                //
                sr2.setMtlFnumber(deliOrder.getMtlFnumber());
                sr2.setUnitFnumber(deliOrder.getMtl().getUnit().getUnitNumber());
                sr2.setPoFid(deliOrder.getfId());
                sr2.setPoFbillno(deliOrder.getFbillno());
                sr2.setSalOrderNo(deliOrder.getSalOrderNo());
                sr2.setSalOrderNoEntryId(deliOrder.getSalOrderEntryId());
                sr2.setFprice(deliOrder.getFprice());
//            sr2.setBatchno(deliOrder.getBatchCode());
//            sr2.setSequenceNo(deliOrder.getSnCode());
//            sr2.setBarcode(deliOrder.getBarcode());

                sr2.setStockId(stock.getfStockid());
                sr2.setStock(stock);
                sr2.setStockFnumber(stock.getfNumber());
                sr2.setStockPos(stockP);
                sr2.setStockPositionId(stockP.getId());
                sr2.setStockPName(stockP.getFname());

                if (deliOrder.getStock() != null) {
                    sr2.setStockId(deliOrder.getStockId());
                    sr2.setStockFnumber(deliOrder.getStockNumber());
                    sr2.setStockName(deliOrder.getStockName());
                }
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
                tvReceiveOrg.setText(receiveOrg.getName());
                sr2.setReceiveOrgFnumber(receiveOrg.getNumber());

                if (salOrg == null) salOrg = new Organization();
                if(deliOrder.getSalOrgId() > 0) {
                    salOrg.setFpkId(deliOrder.getSalOrgId());
                    salOrg.setNumber(deliOrder.getSalOrgNumber());
                    salOrg.setName(deliOrder.getSalOrgName());
                } else {
                    salOrg.setFpkId(deliOrder.getDeliOrgId());
                    salOrg.setNumber(deliOrder.getDeliOrgNumber());
                    salOrg.setName(deliOrder.getDeliOrgName());
                }

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
                String deliveryCompanyId = isNULLS(deliOrder.getDeliveryCompanyId());
                String deliveryCompanyNumber = isNULLS(deliOrder.getDeliveryCompanyNumber());
                String deliveryCompanyName = isNULLS(deliOrder.getDeliveryCompanyName());
                if(expressCompany == null) expressCompany = new ExpressCompany();
                expressCompany.setUniquenessId(deliveryCompanyId);
                expressCompany.setExpressNumber(deliveryCompanyNumber);
                expressCompany.setExpressName(deliveryCompanyName);

                sr2.setLeafNumber(deliOrder.getLeaf());
                sr2.setLeafNumber2(deliOrder.getLeaf1());
                sr2.setCoveQty(deliOrder.getCoveQty());

                ScanningRecordTok3 srTok3 = new ScanningRecordTok3();
                srTok3.setSaleDeptNumber(deliOrder.getSaleDeptNumber());
                srTok3.setCustomerService(deliOrder.getCustomerService());
                srTok3.setFreceive(deliOrder.getFreceive());
                srTok3.setFreceivetel(deliOrder.getFreceivetel());
                srTok3.setFconsignee(deliOrder.getFconsignee());
                srTok3.setCarrierNumber(deliOrder.getCarrierNumber());
                srTok3.setSalerNumber(deliOrder.getSalerNumber());
                srTok3.setDeliverWayNumber(deliOrder.getDeliverWayNumber());
                srTok3.setDeliveryCompanyNumber(deliOrder.getDeliveryCompanyNumber());
                srTok3.setExitTypeNumber(deliOrder.getExitTypeNumber());
                srTok3.setFpaezArea(deliOrder.getEntryArea());
                srTok3.setFpaezWidth(deliOrder.getWidth());
                srTok3.setFpaezHigh(deliOrder.getHigh());
                srTok3.setFpaezBeizhu(deliOrder.getSummary());
                srTok3.setFentryNote(deliOrder.getEntryRemark());
                srTok3.setFboxAmount(mapBox.size());
                sr2.setSrTok3(srTok3);

                checkDatas.add(sr2);
            }
        }
        int size2 = mbrList.size();
        for(int i=0; i<size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            for(int j=0; j<size2; j++) {
                MaterialBinningRecord mbr = mbrList.get(j);
//                if(mbr.getMaterialId() == sr2.getMtl().getfMaterialId()) {
                if(mbr.getSalOrderNo().equals(sr2.getSalOrderNo()) && mbr.getSalOrderNoEntryId() == sr2.getSalOrderNoEntryId()) {
                    sr2.setSourceId(mbr.getId());
                    sr2.setStockqty(sr2.getStockqty()+mbr.getNumber());
                    break;
                }
            }
        }
        tvExpressCompany.setText(expressCompany.getExpressName());
        setFocusable(etBoxCode); // 物料代码获取焦点

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择来源单返回（发货订单，复核单装箱）
     */
    private void getSourceAfter3(List<MaterialBinningRecord> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            MaterialBinningRecord mbr = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            sr2.setSourceId(mbr.getId());
            sr2.setSourceK3Id(mbr.getRelationBillId());
            sr2.setSourceFnumber(mbr.getRelationBillNumber());
            sr2.setMtlId(mbr.getMaterialId());
            sr2.setMtl(mbr.getMtl());
            sr2.setMtlFnumber(mbr.getMtl().getfNumber());
            sr2.setUnitFnumber(mbr.getMtl().getUnit().getUnitNumber());
            sr2.setPoFid(mbr.getRelationBillId());
            sr2.setPoFbillno(mbr.getRelationBillNumber());
            sr2.setBatchno(mbr.getBatchCode());
            sr2.setSequenceNo(mbr.getSnCode());
            sr2.setBarcode(mbr.getBarcode());
            sr2.setStockId(stock.getfStockid());
            sr2.setStock(stock);
            sr2.setStockFnumber(stock.getfNumber());
            sr2.setStockPos(stockP);
            sr2.setStockPositionId(stockP.getId());
            sr2.setStockPName(stockP.getFname());

            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
            }
            // 得到发货订单
            DeliOrder deliOrder = JsonUtil.stringToObject(mbr.getRelationObj(), DeliOrder.class);
            // 仓库
            if (deliOrder.getStock() != null) {
                sr2.setStockId(deliOrder.getStockId());
                sr2.setStockFnumber(deliOrder.getStockNumber());
                sr2.setStockName(deliOrder.getStockName());
            }
            sr2.setEntryId(deliOrder.getEntryId());
            sr2.setFqty(mbr.getRelationBillFQTY());
            sr2.setPoFmustqty(mbr.getRelationBillFQTY());
            sr2.setStockqty(mbr.getNumber());
            sr2.setFprice(deliOrder.getFprice());
            // 发货组织
            if(deliOrder.getDeliOrgId() > 0) {
                if(receiveOrg == null) receiveOrg = new Organization();
                receiveOrg.setFpkId(deliOrder.getDeliOrgId());
                receiveOrg.setNumber(deliOrder.getDeliOrgNumber());
                receiveOrg.setName(deliOrder.getDeliOrgName());
                tvReceiveOrg.setText(receiveOrg.getName());
                sr2.setReceiveOrgFnumber(receiveOrg.getNumber());
            }

            if(salOrg == null) salOrg = new Organization();
            if(deliOrder.getSalOrgId() > 0) {
                // 销售组织
                salOrg.setFpkId(deliOrder.getSalOrgId());
                salOrg.setNumber(deliOrder.getSalOrgNumber());
                salOrg.setName(deliOrder.getSalOrgName());
            } else {
                salOrg.setFpkId(deliOrder.getDeliOrgId());
                salOrg.setNumber(deliOrder.getDeliOrgNumber());
                salOrg.setName(deliOrder.getDeliOrgName());
            }
            setEnables(tvSalOrg, R.drawable.back_style_gray3, false);
            tvSalOrg.setText(salOrg.getName());
            sr2.setPurOrgFnumber(salOrg.getNumber());

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
            String deliveryCompanyId = isNULLS(deliOrder.getDeliveryCompanyId());
            String deliveryCompanyNumber = isNULLS(deliOrder.getDeliveryCompanyNumber());
            String deliveryCompanyName = isNULLS(deliOrder.getDeliveryCompanyName());
            if(expressCompany == null) expressCompany = new ExpressCompany();
            expressCompany.setUniquenessId(deliveryCompanyId);
            expressCompany.setExpressNumber(deliveryCompanyNumber);
            expressCompany.setExpressName(deliveryCompanyName);

            sr2.setLeafNumber(deliOrder.getLeaf());
            sr2.setLeafNumber2(deliOrder.getLeaf1());
            sr2.setCoveQty(deliOrder.getCoveQty());

            ScanningRecordTok3 srTok3 = new ScanningRecordTok3();
            srTok3.setSaleDeptNumber(deliOrder.getSaleDeptNumber());
            srTok3.setCustomerService(deliOrder.getCustomerService());
            srTok3.setFreceive(deliOrder.getFreceive());
            srTok3.setFreceivetel(deliOrder.getFreceivetel());
            srTok3.setFconsignee(deliOrder.getFconsignee());
            srTok3.setCarrierNumber(deliOrder.getCarrierNumber());
            srTok3.setSalerNumber(deliOrder.getSalerNumber());
            srTok3.setDeliverWayNumber(deliOrder.getDeliverWayNumber());
            srTok3.setDeliveryCompanyNumber(deliOrder.getDeliveryCompanyNumber());
            srTok3.setExitTypeNumber(deliOrder.getExitTypeNumber());
            srTok3.setFpaezArea(deliOrder.getEntryArea());
            srTok3.setFpaezWidth(deliOrder.getWidth());
            srTok3.setFpaezHigh(deliOrder.getHigh());
            srTok3.setFpaezBeizhu(deliOrder.getSummary());
            srTok3.setFentryNote(deliOrder.getEntryRemark());
            srTok3.setFboxAmount(mapBox.size());
            sr2.setSrTok3(srTok3);

            checkDatas.add(sr2);
        }
        tvExpressCompany.setText(expressCompany.getExpressName());
        setFocusable(etBoxCode); // 物料代码获取焦点

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择（部门）返回的值
     */
    private void getDeptAfter() {
        if (department != null) {
            tvDeptSel.setText(department.getDepartmentName());
            saveObjectToXml(department, "strDepartment", getResStr(R.string.saveUser));
        }
    }

    /**
     * 选择（收料组织）返回的值
     */
    private void getOrgAfter() {
        if (receiveOrg != null) {
            tvReceiveOrg.setText(receiveOrg.getName());
            if(salOrg == null) {
                try {
                    salOrg = Comm.deepCopy(receiveOrg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvSalOrg.setText(salOrg.getName());
            }
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
            record.setSourceId(sr2.getSourceId());
            record.setSourceK3Id(sr2.getSourceK3Id());
            record.setSourceFnumber(sr2.getSourceFnumber());
            record.setMtlK3Id(sr2.getMtlId());
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
            record.setFprice(sr2.getFprice());

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
            record.setSalOrderNo(sr2.getSalOrderNo());
            if(expressCompany != null) {
                record.setExpressNumber(expressCompany.getExpressNumber());
            }
            record.setKdAccount(user.getKdAccount());
            record.setKdAccountPassword(user.getKdAccountPassword());
            record.setSrTok3(sr2.getSrTok3());
            record.setLeafNumber(sr2.getLeafNumber());
            record.setLeafNumber2(sr2.getLeafNumber2());
            record.setCoveQty(sr2.getCoveQty());

            list.add(record);
        }

        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("addScanningRecord");
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
                Log.e("run_addScanningRecord --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas(String val) {
        isTextChange = false;
        if(val.length() == 0) {
            Comm.showWarnDialog(mContext,"请对准条码！");
            return;
        }
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        switch (curViewFlag) {
            case '1': // 箱子扫码
                mUrl = getURL("materialBinningRecord/findList3ByParam");
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
                Log.e("run_smGetDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC2, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
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
        String mUrl = getURL("scanningRecord/findInStockSum");
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
                Log.e("run_findInStockSum --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC3);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_findDeliOrderByProdOrder(String strOrderNo, String strOrderEntryId) {
        showLoadDialog("加载中...");
        String mUrl = mUrl = getURL("findDeliOrderByProdOrder");;
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
                Log.e("run_smGetDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC4, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC4, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 提交并审核
     */
    private void run_submitAndPass() {
        showLoadDialog("正在审核...");
        String mUrl = getURL("scanningRecord/submitAndPass");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("fbillNo", k3Number)
                .add("type", "2")
                .add("kdAccount", user.getKdAccount())
                .add("kdAccountPassword", user.getKdAccountPassword())
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
                mHandler.sendEmptyMessage(UNPASS);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                Log.e("run_submitAndPass --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNPASS, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(PASS, result);
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
