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
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import ykk.xc.com.xcwms.basics.Staff_DialogActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.model.EnumDict;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Organization;
import ykk.xc.com.xcwms.model.ScanningRecord;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.model.ScanningRecordTok3;
import ykk.xc.com.xcwms.model.ShrinkOrder;
import ykk.xc.com.xcwms.model.Staff;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.sal.PickingList;
import ykk.xc.com.xcwms.model.stock.StkTransferApp;
import ykk.xc.com.xcwms.sales.adapter.Stk_TransferDirectFragment4Adapter;
import ykk.xc.com.xcwms.sales.adapter.Stk_TransferDirectFragment4Adapter;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.LogUtil;
import ykk.xc.com.xcwms.util.interfaces.IFragmentExec;

/**
 * (调拨申请单)到（直接调拨单）
 */
public class Stk_TransferDirectFragment4 extends BaseFragment implements IFragmentExec {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.et_sourceNo)
    EditText etSourceNo;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_receiveOrg)
    TextView tvReceiveOrg;
    @BindView(R.id.tv_salOrg)
    TextView tvSalOrg;
    @BindView(R.id.tv_stockStaff)
    TextView tvStockStaff;
    @BindView(R.id.lin_top)
    LinearLayout linTop;

    private Stk_TransferDirectFragment4 context = this;
    private static final int SEL_ORDER = 10, SEL_DEPT = 11, SEL_ORG = 12, SEL_ORG2 = 13, SEL_STOCK2 = 15, SEL_STOCKP2 = 16, SEL_STAFF = 17, PAD_SM = 18;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int SETFOCUS = 1, CODE2 = 2;
    private Stock stock, stock2; // 仓库
    private StockPosition stockP, stockP2; // 库位
    private Staff stockStaff; // 仓管员
    private Department department; // 部门
    private Organization inOrg, outOrg; // 组织
    private Stk_TransferDirectFragment4Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String sourceBarcode, mtlBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：箱码
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private Activity mContext;
    private Stk_TransferDirectMainActivity parent;
    private int caseId; // 当前单据的方案id
    private String k3Number; // 记录传递到k3返回的单号
    private boolean isTextChange; // 是否进入TextChange事件
    private boolean isInStock; // 是否为调入仓库，否则为调出仓库

    // 消息处理
    private Stk_TransferDirectFragment4.MyHandler mHandler = new Stk_TransferDirectFragment4.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Stk_TransferDirectFragment4> mActivity;

        public MyHandler(Stk_TransferDirectFragment4 activity) {
            mActivity = new WeakReference<Stk_TransferDirectFragment4>(activity);
        }

        public void handleMessage(Message msg) {
            Stk_TransferDirectFragment4 m = mActivity.get();
            String errMsg = null;
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
                        m.reset('0');
//
                        m.checkDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        m.caseId = 0;
                        Comm.showWarnDialog(m.mContext,"保存成功✔");
//                        m.parent.setFragment2Print(2, m.listMaps);
//                        m.btnSave.setVisibility(View.GONE);
//                        Comm.showWarnDialog(m.mContext,"保存成功，请点击“审核按钮”！");

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
//                        Comm.showWarnDialog(m.mContext,"审核成功✔");
                        m.toasts("审核成功✔");
//                        m.parent.setFragment2Print(2, m.listMaps);

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        switch (m.curViewFlag) {
                            case '1': // 申请单
                                List<StkTransferApp> list = JsonUtil.strToList((String) msg.obj, StkTransferApp.class);
                                m.getSourceAfter(list);

                                break;
                            case '2': // 物料
                                BarCodeTable bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.getMtlAfter(bt);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(Comm.isNULLS(errMsg).length() == 0) {
                            errMsg = "很抱歉，没有找到数据！";
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
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etMtlCode);

                        break;
                    case PAD_SM: // pad扫码
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '1': // 申请单
                                etName = m.getValues(m.etSourceNo);
                                if (m.sourceBarcode != null && m.sourceBarcode.length() > 0) {
                                    if(m.sourceBarcode.equals(etName)) {
                                        m.sourceBarcode = etName;
                                    } else m.sourceBarcode = etName.replaceFirst(m.sourceBarcode, "");

                                } else m.sourceBarcode = etName;
                                m.setTexts(m.etSourceNo, m.sourceBarcode);
                                // 执行查询方法
                                m.run_smGetDatas(m.sourceBarcode);

                                break;
                            case '2': // 物料
                                etName = m.getValues(m.etMtlCode);
                                if (m.mtlBarcode != null && m.mtlBarcode.length() > 0) {
                                    if(m.mtlBarcode.equals(etName)) {
                                        m.mtlBarcode = etName;
                                    } else m.mtlBarcode = etName.replaceFirst(m.mtlBarcode, "");

                                } else m.mtlBarcode = etName;
                                m.setTexts(m.etMtlCode, m.mtlBarcode);
                                // 执行查询方法
                                m.run_smGetDatas(m.mtlBarcode);

                                break;
                        }

                        break;
                }
            }
        }
    }

    @Override
    public void onFragmenExec() {
//        listMaps.clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (Stk_TransferDirectMainActivity) context;
        parent.setFragmentExec(this);
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.stk_transferdirect_fragment4, container, false);
    }

    @Override
    public void initView() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build();
        }
        mContext = getActivity();
//        parent = (Stk_TransferDirectMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Stk_TransferDirectFragment4Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Stk_TransferDirectFragment4Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                LogUtil.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", CODE2);
            }

            @Override
            public void onClick_selStock(View v, ScanningRecord2 entity, int position, boolean isInStock) {
                LogUtil.e("selStock", "行：" + position);
                curPos = position;
                context.isInStock = isInStock;
                showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
            }

            @Override
            public void onClick_del(ScanningRecord2 entity, int position) {
                LogUtil.e("del", "行：" + position);
                checkDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext,etSourceNo);
        hideSoftInputMode(mContext, etMtlCode);
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

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() { setFocusable(etSourceNo); // 物料代码获取焦点
                }
            },200);
        }
    }

    @OnClick({R.id.btn_save, R.id.btn_pass, R.id.btn_clone, R.id.tv_deptSel, R.id.tv_stockStaff, R.id.lin_rowTitle})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
//            case R.id.tv_sourceSel: // 选择调拨申请单
//                showForResult(Stk_SelTransferOrderActivity.class, SEL_ORDER, bundle);
//
//                break;
            case R.id.tv_deptSel: // 选择部门
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

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
        if(inOrg == null) {
            Comm.showWarnDialog(mContext,"请选择发货组织！");
            return false;
        }
        if(outOrg == null) {
            Comm.showWarnDialog(mContext,"请选择销售组织！");
            return false;
        }

        // 判断是否输入了数量
        boolean isNull = false;
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 pl = checkDatas.get(i);
            if (pl.getStockqty() > 0) isNull = true;
        }
        if(!isNull) {
            Comm.showWarnDialog(mContext,"当前行中，至少有一行（拣货数）必须大于0！");
            return false;
        }

        List<ScanningRecord2> list = new ArrayList<>();
        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            ScanningRecordTok3 srToK3 = sr2.getSrTok3();

            // 仓管员
            if(stockStaff != null) srToK3.setStockStaffNumber(stockStaff.getNumber());
            
//            if (sr2.getStockqty() == 0) {
//                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，（实发数）必须大于0！");
//                return false;
//            }
            if (sr2.getStockId() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，请选择（调出仓库）！");
                return false;
            }
            if (sr2.getStock2() == null) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，请选择（调入仓库）！");
                return false;
            }
            if(sr2.getStockId() == sr2.getStock2().getfStockid()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，（调出仓库）和（调入仓库）不能一致！");
                return false;
            }
            if (sr2.getStockqty() > 0) {
                list.add(sr2);
            }
//            if ((sr2.getMtl().getMtlPack() == null || sr2.getMtl().getMtlPack().getIsMinNumberPack() == 0) && sr2.getStockqty() > sr2.getFqty()) {
//                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，（实发数）不能大于（应发数）！");
//                return false;
//            }
//            if ((sr2.getMtl().getMtlPack() == null || sr2.getMtl().getMtlPack().getIsMinNumberPack() == 0) && sr2.getStockqty() < sr2.getFqty()) {
//                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，（实发数）必须等于（应发数）！");
//                return false;
//            }

        }
        checkDatas.clear();
        checkDatas.addAll(list);

        return true;
    }

    @OnFocusChange({R.id.et_mtlCode})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
    }

    @Override
    public void setListener() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusable(etGetFocus);
                switch (v.getId()) {
                    case R.id.et_sourceNo: // 申请单
                        curViewFlag = '1';
                        setFocusable(etSourceNo);
                        break;
                    case R.id.et_mtlCode: // 物料
                        curViewFlag = '2';
                        setFocusable(etMtlCode);
                        break;
                }
            }
        };
        etSourceNo.setOnClickListener(click);
        etMtlCode.setOnClickListener(click);

        // 申请单
        etSourceNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '1';

                if(!isTextChange) {
                    if (baseIsPad) {
                        isTextChange = true;
                        mHandler.sendEmptyMessageDelayed(PAD_SM,600);
                    } else {
                        sourceBarcode = s.toString();
                        // 执行查询方法
                        run_smGetDatas(sourceBarcode);
                    }
                }
            }
        });

        // 物料
        etMtlCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '2';
                if(checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext,"请先选择调拨申请单！");
                    return;
                }
//                mtlBarcode = s.toString();
//                // 执行查询方法
//                run_smGetDatas(mtlBarcode);

                if(!isTextChange) {
                    if (baseIsPad) {
                        isTextChange = true;
                        mHandler.sendEmptyMessageDelayed(PAD_SM,600);
                    } else {
                        mtlBarcode = s.toString();
                        // 执行查询方法
                        run_smGetDatas(mtlBarcode);
                    }
                }
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
        etSourceNo.setText(""); // 物料代码
        etMtlCode.setText(""); // 物料代码
        linTop.setVisibility(View.VISIBLE);
    }

    private void resetSon() {
        if(user.getStaff() != null) {
            stockStaff = user.getStaff();
            tvStockStaff.setText(stockStaff.getName());
        }
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
        inOrg = null;
        outOrg = null;
        curViewFlag = '1';
        sourceBarcode = null;
        mtlBarcode = null;
    }
    private void resetSon2() {
        etMtlCode.setText("");
        mtlBarcode = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_ORDER: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        List<StkTransferApp> list = (List<StkTransferApp>) bundle.getSerializable("checkDatas");
//                        sourceList.addAll(list);
                        getSourceAfter(list);
                    }
                }

                break;
            case SEL_STOCK2: //行事件选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock2 = (Stock) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_STOCK2", stock2.getfName());
                    // 启用了库位管理
                    if (stock2.isStorageLocation()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("stockId", stock2.getfStockid());
                        showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, bundle);
                    } else {
                        if(isInStock) stockAllFill2(false);
                        else {
                            stockAllFill(false);
                            saveObjectToXml(stock2, "strStock", getResStr(R.string.saveUser));
                        }
                    }
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    if(isInStock) stockAllFill2(true);
                    else {
                        stockAllFill(true);
                        saveObjectToXml(stock2, "strStock", getResStr(R.string.saveUser));
                        saveObjectToXml(stockP2, "strStockPos", getResStr(R.string.saveUser));
                    }
                }

                break;
            case SEL_ORG: //查询出库组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    inOrg = (Organization) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_ORG", inOrg.getName());
                    getOrgAfter();
                }

                break;
            case SEL_ORG2: //查询生产组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    outOrg = (Organization) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_ORG2", outOrg.getName());
                    getOrg2After();
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
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
     * 仓库数据全部填充（调出仓库）
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
     * 仓库数据全部填充（调入仓库）
     */
    private void stockAllFill2(boolean inStockPosData) {
        int size = checkDatas.size();
        boolean isBool = false;
        for(int i=0; i<size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if(sr2.getStock2() != null) {
                isBool = true;
                break;
            }
        }
        if(isBool) {
            ScanningRecord2 sr2 = checkDatas.get(curPos);
            sr2.setStock2(stock2);
            if(inStockPosData) {
                sr2.setStockPos2(stockP2);
            }
        } else { // 全部都为空的时候，选择任意全部填充
            for (int i = 0; i < size; i++) {
                ScanningRecord2 sr2 = checkDatas.get(i);
                sr2.setStock2(stock2);
                if(inStockPosData) {
                    sr2.setStockPos2(stockP2);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择来源单返回（调拨申请单）
     */
    private void getSourceAfter(List<StkTransferApp> list) {
        // 判断扫描重复单据
        if(checkDatas.size() > 0) {
            Comm.showWarnDialog(mContext,"请先保存当前行数据！");
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            StkTransferApp stk = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            Material mtl = stk.getMtl();

            sr2.setSourceK3Id(stk.getfId());
            sr2.setSourceFnumber(stk.getfBillNo());
            sr2.setMtlId(stk.getfMaterialId());
            sr2.setMtl(mtl);
            //
            sr2.setMtlFnumber(stk.getfMaterialNumber());
            sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
            sr2.setPoFid(stk.getfId());
            sr2.setPoFbillno(stk.getfBillNo());
            sr2.setSalOrderNo("");
            sr2.setSalOrderNoEntryId(0);
            sr2.setFprice(0);
//            sr2.setBatchno(stk.getBatchCode());
//            sr2.setSequenceNo(stk.getSnCode());
//            sr2.setBarcode(stk.getBarcode());

            // 调拨申请单的调出仓库
            if (stk.getfStockId() > 0) {
                Stock stock = new Stock();
                stock.setfStockid(stk.getfStockId());
                stock.setfNumber(stk.getfStockNumber());
                stock.setfName(stk.getfStockName());
                sr2.setStock(stock);
                sr2.setStockId(stk.getfStockId());
                sr2.setStockFnumber(stk.getfStockNumber());
                sr2.setStockName(stk.getfStockName());
                sr2.setStockPos(null);
                sr2.setStockPositionId(0);
                sr2.setStockPName("");
            } else if(mtl.getStock() != null) {
                // 物料默认的仓库仓位
                setStockInfo(sr2, mtl.getStock());
                setStockPosInfo(sr2, mtl.getStockPos());
            } else {
                // 默认操作员的仓库仓位
                setStockInfo(sr2, stock);
                setStockPosInfo(sr2, stockP);
            }
            // 调拨申请单的调入仓库
            sr2.setStock2(stk.getfStockIn());

            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
            }
            sr2.setEntryId(stk.getfEntryId());
            sr2.setFqty(stk.getFqty());
            sr2.setPoFmustqty(stk.getFqty());
            sr2.setUsableFqty(stk.getUsableFqty());
//            sr2.setStockqty(stk.getNumber());
            // 发货组织
            if (inOrg == null) inOrg = new Organization();
            inOrg.setFpkId(stk.getfStockOrgInId());
            inOrg.setNumber(stk.getfStockOrgInNumber());
            inOrg.setName(stk.getfStockOrgInName());
            tvReceiveOrg.setText(inOrg.getName());
            sr2.setReceiveOrgFnumber(inOrg.getNumber());

            if (outOrg == null) outOrg = new Organization();
            if(stk.getfStockOrgId() > 0) {
                outOrg.setFpkId(stk.getfStockOrgId());
                outOrg.setNumber(stk.getfStockOrgNumber());
                outOrg.setName(stk.getfStockOrgName());
            } else {
                outOrg.setFpkId(stk.getfStockOrgInId());
                outOrg.setNumber(stk.getfStockOrgInNumber());
                outOrg.setName(stk.getfStockOrgInName());
            }

            tvSalOrg.setText(outOrg.getName());
            sr2.setPurOrgFnumber(outOrg.getNumber());

            sr2.setCustomerId(0);
            sr2.setCustomerName("");
            sr2.setCustFnumber("");
//            if (cust == null) {
//                cust = new Customer();
//                cust.setFcustId(stk.getCustId());
//                cust.setCustomerCode(stk.getCustNumber());
//                cust.setCustomerName(stk.getCustName());
//
//                tvCustSel.setText("客户：" + stk.getCustName());
//            }
            sr2.setSourceType('7');
//            sr2.setTempId(ism.getId());
//            sr2.setRelationObj(JsonUtil.objectToString(ism));
            sr2.setfRuleId("StkTransferApply-StkTransferDirect");
            sr2.setFsrcBillTypeId("STK_TRANSFERAPPLY");
            sr2.setFsTableName("T_STK_STKTRANSFERAPPENTRY");

//            String deliveryCompanyId = isNULLS(stk.getDeliveryCompanyId());
//            String deliveryCompanyNumber = isNULLS(stk.getDeliveryCompanyNumber());
//            String deliveryCompanyName = isNULLS(stk.getDeliveryCompanyName());
//            if(expressCompany == null) expressCompany = new ExpressCompany();
//            expressCompany.setUniquenessId(deliveryCompanyId);
//            expressCompany.setExpressNumber(deliveryCompanyNumber);
//            expressCompany.setExpressName(deliveryCompanyName);

//            sr2.setLeafNumber(stk.getLeaf());
//            sr2.setLeafNumber2(stk.getLeaf1());
//            sr2.setCoveQty(stk.getCoveQty());

            ScanningRecordTok3 srTok3 = new ScanningRecordTok3();
//            srTok3.setSaleDeptNumber(stk.getSaleDeptNumber());
//            srTok3.setCustomerService(stk.getCustomerService());
//            srTok3.setFreceive(stk.getFreceive());
//            srTok3.setFreceivetel(stk.getFreceivetel());
//            srTok3.setFconsignee(stk.getFconsignee());
//            srTok3.setCarrierNumber(stk.getCarrierNumber());
//            srTok3.setSalerNumber(stk.getSalerNumber());
//            srTok3.setSalerName(stk.getSalerName());
//            srTok3.setDeliverWayNumber(stk.getDeliverWayNumber());
//            srTok3.setDeliveryCompanyNumber(stk.getDeliveryCompanyNumber());
//            srTok3.setExitTypeNumber(stk.getExitTypeNumber());
//            srTok3.setFpaezArea(stk.getEntryArea());
//            srTok3.setFpaezWidth(stk.getWidth());
//            srTok3.setFpaezHigh(stk.getHigh());
//            srTok3.setFpaezBeizhu(stk.getSummary());
//            srTok3.setFentryNote(stk.getEntryRemark());
//            srTok3.setFboxAmount(0);
//            srTok3.setFlhDbdj(stk.getFlhDbdj());
//            srTok3.setFlhDbj(stk.getFlhDbj());
            sr2.setSrTok3(srTok3);

            checkDatas.add(sr2);
        }
        setFocusable(etMtlCode); // 物料代码获取焦点

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 来源订单 判断数据
     */
    private void getMtlAfter(BarCodeTable bt) {
        Material tmpMtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
        bt.setMtl(tmpMtl);
        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            Material mtl = sr2.getMtl();
            // 如果扫码相同
            if (bt.getMaterialId() == mtl.getfMaterialId()) {
                isFlag = true;

                double fqty = 1;
                // 计量单位数量
                if(tmpMtl.getCalculateFqty() > 0) fqty = tmpMtl.getCalculateFqty();
                // 未启用序列号
                if (tmpMtl.getIsSnManager() == 0) {
                    // 发货数大于拣货数
                    if (sr2.getUsableFqty() > sr2.getStockqty()) {
                        // 如果扫的是物料包装条码，就显示个数
                        double number = 0;
                        if(bt != null) number = bt.getMaterialCalculateNumber();

                        if(number > 0) {
                            sr2.setStockqty(sr2.getStockqty() + (number*fqty));
                        } else {
                            sr2.setStockqty(sr2.getStockqty() + fqty);
                        }

                        // 启用了最小包装
                    } else if(mtl.getMtlPack() != null && mtl.getMtlPack().getIsMinNumberPack() == 1) {
                        if(mtl.getMtlPack().getIsMinNumberPack() == 1) {
                            // 如果拣货数小于订单数，就加数量
                            if(sr2.getStockqty() < sr2.getUsableFqty()) {
                                sr2.setStockqty(sr2.getStockqty() + fqty);
                            } else {
                                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已经达到最小包装发货数量！");
                                return;
                            }
                        }

                    } else if ((mtl.getMtlPack() == null || mtl.getMtlPack().getIsMinNumberPack() == 0) && sr2.getStockqty() > sr2.getUsableFqty()) {
                        // 数量已满
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，（拣货数）不能大于（订单数）！");
                        return;
                    }
                } else {
                    if (sr2.getStockqty() == sr2.getUsableFqty()) {
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已捡完！");
                        return;
                    }
                    List<String> list = sr2.getListBarcode();
                    if(list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(mContext,"该物料条码已在拣货行中，请扫描未使用过的条码！");
                        return;
                    }
                    list.add(bt.getBarcode());
//                    // 拼接条码号，用逗号隔开
//                    StringBuilder sb = new StringBuilder();
//                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
//                        if((k+1) == sizeK) sb.append(list.get(k));
//                        else sb.append(list.get(k)+",");
//                    }
                    sr2.setListBarcode(list);
//                    sr2.setStrBarcodes(sb.toString());
                    sr2.setStockqty(sr2.getStockqty() + 1);
                }
                mAdapter.notifyDataSetChanged();
                isPickingEnd();
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(mContext, "该物料与订单不匹配！");
        }
    }

    /**
     * 是否已经捡完货
     */
    private void isPickingEnd() {
        int size = checkDatas.size();
        int count = 0; // 计数器
        for(int i=0; i<size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if(sr2.getStockqty() >= sr2.getUsableFqty()) {
                count += 1;
            }
        }
        if(count == size) {
            toasts("已经扫完货了，请保存！");
        }
    }

    /**
     * 设置仓库的值
     */
    private void setStockInfo(ScanningRecord2 sr2, Stock stock) {
        if(stock == null) {
            sr2.setStock(null);
            sr2.setStockId(0);
            sr2.setStockFnumber("");
            sr2.setStockName("");
        } else {
            sr2.setStock(stock);
            sr2.setStockId(stock.getfStockid());
            sr2.setStockFnumber(stock.getfNumber());
            sr2.setStockName(stock.getfName());
        }
    }

    /**
     * 设置库位的值
     */
    private void setStockPosInfo(ScanningRecord2 sr2, StockPosition stockP) {
        if(stockP == null) {
            sr2.setStockPos(null);
            sr2.setStockPositionId(0);
            sr2.setStockPName("");
        } else {
            sr2.setStockPos(stockP);
            sr2.setStockPositionId(stockP.getId());
            sr2.setStockPName(stockP.getFname());
        }
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
        if (inOrg != null) {
            tvReceiveOrg.setText(inOrg.getName());
            if(outOrg == null) {
                try {
                    outOrg = Comm.deepCopy(inOrg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvSalOrg.setText(outOrg.getName());
            }
        }
    }

    /**
     * 选择（采购组织）返回的值
     */
    private void getOrg2After() {
        if (outOrg != null) {
            tvSalOrg.setText(outOrg.getName());
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
            // type: 1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库 6、直接调拨
            record.setType(6);
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
//            record.setFcarriageNo(getValues(etExpressNo).trim());
            record.setSalOrderNo(sr2.getSalOrderNo());
//            if(expressCompany != null) {
//                record.setExpressNumber(expressCompany.getExpressNumber());
//            }
            record.setKdAccount(user.getKdAccount());
            record.setKdAccountPassword(user.getKdAccountPassword());
            record.setSrTok3(sr2.getSrTok3());
            record.setLeafNumber(sr2.getLeafNumber());
            record.setLeafNumber2(sr2.getLeafNumber2());
            record.setCoveQty(sr2.getCoveQty());
            record.setStock2(sr2.getStock2());
            record.setStockPos2(sr2.getStockPos2());

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
                LogUtil.e("run_addScanningRecord --> onResponse", result);
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
            case '1': // 申请单
                mUrl = getURL("stkTransferApp/findBarcode");
                barcode = sourceBarcode;
                strCaseId = "";
                break;
            case '2': // 物料
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
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
                LogUtil.e("run_smGetDatas --> onResponse", result);
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

                .add("fbillType", "7") // fbillType  1：采购订单入库，2：收料任务单入库，3：生产订单入库，4：销售订单出库，5：发货通知单出库，6：发货通知单下推调拨单，7：调拨申请单到直接调拨
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
                LogUtil.e("run_findInStockSum --> onResponse", result);
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
                LogUtil.e("run_submitAndPass --> onResponse", result);
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
