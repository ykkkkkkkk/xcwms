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
import java.text.DecimalFormat;
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
import ykk.xc.com.xcwms.basics.Material_ListActivity;
import ykk.xc.com.xcwms.basics.Staff_DialogActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.basics.Supplier_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.model.EnumDict;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.ScanningRecord;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.model.ScanningRecordTok3;
import ykk.xc.com.xcwms.model.ShrinkOrder;
import ykk.xc.com.xcwms.model.Staff;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.pur.PurReceiveOrder;
import ykk.xc.com.xcwms.model.sal.PickingList;
import ykk.xc.com.xcwms.purchase.adapter.Pur_InFragment3Adapter;
import ykk.xc.com.xcwms.util.JsonUtil;

public class Pur_InFragment3 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.tv_supplierSel)
    TextView tvSupplierSel;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
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
    @BindView(R.id.tv_stockStaff)
    TextView tvStockStaff;
    @BindView(R.id.tv_purStaff)
    TextView tvPurStaff;
    @BindView(R.id.lin_top)
    LinearLayout linTop;

    private Pur_InFragment3 context = this;
    private static final int SEL_ORDER = 10, SEL_SUPPLIER = 11, SEL_DEPT = 12, SEL_MTL = 13, SEL_STOCK2 = 14, SEL_STOCKP2 = 15, SEL_STAFF = 16, SEL_STAFF2 = 17, PAD_SM = 18;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int SETFOCUS = 1, NUM_RESULT = 50;
//    private Supplier supplier; // 供应商
//    private Material mtl;
    private Stock stock, stock2; // 仓库
    private StockPosition stockP, stockP2; // 库位
    private Staff stockStaff, purStaff; // 仓管员
    private Department department; // 部门
//    private Organization receiveOrg, purOrg; // 组织
    private Pur_InFragment3Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
//    private List<PurReceiveOrder> sourceList = new ArrayList<>(); // 当前选择单据行数据
    private String mtlBarcode, sourceBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：收料订单， 2：物料
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
    private Pur_InMainActivity parent;
    private char defaultStockVal; // 默认仓库的值
    private DecimalFormat df = new DecimalFormat("#.####");
    private String k3Number; // 记录传递到k3返回的单号
    private int orgId; // 组织id
    private boolean isTextChange; // 是否进入TextChange事件

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
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
//                        m.reset('0');
//
//                        m.checkDatas.clear();
//                        m.getBarCodeTableAfterEnable(true);
//                        m.mAdapter.notifyDataSetChanged();
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
                        Comm.showWarnDialog(m.mContext,"审核成功✔");

                        break;
                    case UNPASS: // 审核失败 返回
                        String strMsg = JsonUtil.strToString((String)msg.obj);
                        Comm.showWarnDialog(m.mContext, strMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = null;
                        Material mtl = null;
                        switch (m.curViewFlag) {
                            case '1': // 收料订单
                                List<PurReceiveOrder> listRecOrder = JsonUtil.strToList((String) msg.obj, PurReceiveOrder.class);
                                // 扫码成功后，判断必填项是否已经输入了值
                                boolean isBool = m.getSourceAfter(listRecOrder);
                                if(isBool) m.mHandler.sendEmptyMessageDelayed(SETFOCUS,300);

                                break;
                            case '2': // 物料
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                mtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
                                bt.setMtl(mtl);
                                m.getMaterialAfter(bt);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        String errMsg = m.isNULLS((String) msg.obj);
                        if(errMsg.length() > 0) {
                            String message = JsonUtil.strToString(errMsg);
                            Comm.showWarnDialog(m.mContext, message);
                        } else {
                            Comm.showWarnDialog(m.mContext,"条码不存在，或者扫错了条码！");
                        }

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
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已入库数“"+so.getFqty()+"”，当前超出数“"+(so.getFqty()+sr2.getStockqty() - sr2.getFqty())+"”！");
                                        return;
                                    } else if(so.getFqty() == sr2.getFqty()) {
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已全部入库，不能重复操作！");
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
                        m.setFocusable(m.etMtlNo);

                        break;
                    case PAD_SM: // pad扫码
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '1': // 收料订单
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
                            case '2': // 物料扫码
                                etName = m.getValues(m.etMtlNo);
                                if (m.mtlBarcode != null && m.mtlBarcode.length() > 0) {
                                    if(m.mtlBarcode.equals(etName)) {
                                        m.mtlBarcode = etName;
                                    } else m.mtlBarcode = etName.replaceFirst(m.mtlBarcode, "");

                                } else m.mtlBarcode = etName;
                                m.setTexts(m.etMtlNo, m.mtlBarcode);
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
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_in_fragment3, container, false);
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
        parent = (Pur_InMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_InFragment3Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Pur_InFragment3Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0.0", NUM_RESULT);
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
        hideSoftInputMode(mContext, etSourceNo);
        hideSoftInputMode(mContext, etMtlNo);
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
//        supplier = showObjectByXml(Supplier.class, "strSupplier", getResStr(R.string.saveUser));
        if(user.getStaff() != null) {
            stockStaff = user.getStaff();
            purStaff = user.getStaff();
        } else {
            stockStaff = showObjectByXml(Staff.class, "strStockStaff", getResStr(R.string.saveUser));
            purStaff = showObjectByXml(Staff.class, "strPurStaff", getResStr(R.string.saveUser));
        }
        department = showObjectByXml(Department.class, "strDepartment", getResStr(R.string.saveUser));
        // 赋值
//        if(supplier != null) tvSupplierSel.setText(supplier.getfName());
        if(stockStaff != null) tvStockStaff.setText(stockStaff.getName());
        if(purStaff != null) tvPurStaff.setText(purStaff.getName());
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

    @OnClick({R.id.tv_supplierSel, R.id.btn_sourceNo, R.id.btn_selMtl, R.id.btn_save, R.id.btn_pass, R.id.btn_clone,
            R.id.tv_orderTypeSel, R.id.tv_stockStaff, R.id.tv_purStaff, R.id.tv_deptSel, R.id.lin_rowTitle})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_orderTypeSel: // 订单类型

                break;
            case R.id.tv_supplierSel: // 选择供应商
                showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, null);

                break;
            case R.id.btn_sourceNo: // 选择来源单号
                bundle = new Bundle();
//                bundle.putSerializable("supplier", supplier);
//                bundle.putSerializable("sourceList", (Serializable) sourceList);
                showForResult(Pur_SelReceiveOrderActivity.class, SEL_ORDER, bundle);

                break;
            case R.id.btn_selMtl: // 选择物料
                if (checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "请选择或扫描来源单！");
                    return;
                }
                showForResult(Material_ListActivity.class, SEL_MTL, null);

                break;
            case R.id.tv_deptSel: // 选择部门
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

                break;
            case R.id.tv_purDate: // 入库日期
                Comm.showDateDialog(mContext, view, 0);
                break;
            case R.id.tv_purStaff: // 选择采购员
                bundle = new Bundle();
                bundle.putInt("isload", 0);
                showForResult(Staff_DialogActivity.class, SEL_STAFF, bundle);

                break;
            case R.id.tv_stockStaff: // 选择仓管员
                bundle = new Bundle();
                bundle.putInt("isload", 0);
                showForResult(Staff_DialogActivity.class, SEL_STAFF2, bundle);

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

        // 判断是否输入了数量
        boolean isNull = false;
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if (sr2.getStockqty() > 0) isNull = true;
        }
        if(!isNull) {
            Comm.showWarnDialog(mContext,"当前行中，至少有一行（实收数）必须大于0！");
            return false;
        }

        List<ScanningRecord2> list = new ArrayList<>();
        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            ScanningRecordTok3 srToK3 = sr2.getSrTok3();
            Material mtl = sr2.getMtl();

            String purDeptNumber = Comm.isNULLS(srToK3.getFpurchaseDeptNumber());
            String recDeptNumber = Comm.isNULLS(srToK3.getFstockDeptNumber());

            // 采购员，仓管员
            if(purStaff != null) srToK3.setFpurchaserNumber(purStaff.getNumber());
            if(stockStaff != null) srToK3.setStockStaffNumber(stockStaff.getNumber());
            // 部门
            if(department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
                if(purDeptNumber.length() == 0 ) srToK3.setFpurchaseDeptNumber(department.getDepartmentNumber());
                if(recDeptNumber.length() == 0 ) srToK3.setFstockDeptNumber(department.getDepartmentNumber());
            }

            if (sr2.getStockId() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，请选择（仓库）！");
                return false;
            }
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
            if (sr2.getStockqty() > sr2.getUsableFqty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）不能大于（应收数）！");
                return false;
            }
//            double fqty = sr2.getUsableFqty()*(1+mtl.getReceiveMaxScale()/100);
//            if (sr2.getStockqty() > fqty) {
            double fqty = sr2.getFqty()*(1+mtl.getReceiveMaxScale()/100);
            if ((sr2.getFqty() - sr2.getUsableFqty()) > 0 && sr2.getStockqty() > (fqty - (sr2.getFqty() - sr2.getUsableFqty()))) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）不能大于（应收数）"+(mtl.getReceiveMaxScale() > 0 ? "；最大上限为（"+df.format(fqty)+"）" : "")+"！");
                return false;
            }

            if(sr2.getStockqty() > 0) {
                list.add(sr2);
            }
        }
        checkDatas.clear();
        checkDatas.addAll(list);

        return true;
    }

    @OnFocusChange({R.id.et_mtlNo, R.id.et_sourceNo})
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
                    case R.id.et_sourceNo: // 来源单
                        curViewFlag = '1';
                        setFocusable(etSourceNo);
                        break;
                    case R.id.et_mtlNo: // 物料
                        curViewFlag = '2';
                        setFocusable(etMtlNo);
                        break;
                }
            }
        };
        etSourceNo.setOnClickListener(click);
        etMtlNo.setOnClickListener(click);

        // 来源单据
        etSourceNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '1';
//                sourceBarcode = s.toString();
//                // 执行查询方法
//                run_smGetDatas(sourceBarcode);

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
        etMtlNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '2';
                if (checkDatas.size() == 0) { // 扫码之前的判断
                    s.delete(0,s.length());
                    Comm.showWarnDialog(mContext, "请选择或扫描来源单！");
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
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
        etSourceNo.setText(""); // 来源单
        etMtlNo.setText(""); // 物料代码
        stock2 = null;
        stockP2 = null;
//        sourceList.clear();
        parent.isChange = false;
        setFocusable(etSourceNo);
    }

    private void resetSon() {
        orgId = 0;
        k3Number = null;
        btnSave.setVisibility(View.VISIBLE);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvSupplierSel.setText("");
        tvDeptSel.setText("");
        tvReceiveOrg.setText("");
        tvPurOrg.setText("");
//        supplier = null;
        stock = null;
        stockP = null;
        department = null;
        curViewFlag = '1';
        mtlBarcode = null;
        sourceBarcode = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_ORDER: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        List<PurReceiveOrder> list = (List<PurReceiveOrder>) bundle.getSerializable("checkDatas");
//                        sourceList.addAll(list);
                        getSourceAfter(list);
                    }
                }

                break;
            case SEL_MTL: //查询物料	返回
                if (resultCode == Activity.RESULT_OK) {
                    Material mtl = (Material) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_MTL", mtl.getfName());
                    getMaterialAfter(null);
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
//                        checkDatas.get(curPos).setPoFmustqty(num);
//                        checkDatas.get(curPos).setFqty(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_STAFF: // 采购员	返回
                if (resultCode == Activity.RESULT_OK) {
                    purStaff = (Staff) data.getSerializableExtra("staff");
                    tvPurStaff.setText(purStaff.getName());
                    saveObjectToXml(purStaff, "strPurStaff", getResStr(R.string.saveUser));
                }

                break;
            case SEL_STAFF2: // 仓管员	返回
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
     * 仓库数据全部填充
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
     * 选择来源单返回
     */
    private boolean getSourceAfter(List<PurReceiveOrder> list) {
        // 判断扫描重复单据
        if(checkDatas.size() > 0) {
            Comm.showWarnDialog(mContext,"请先保存当前行数据！");
            return false;
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            PurReceiveOrder recOrder = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            Material mtl = recOrder.getMtl();

            sr2.setType(1);
            sr2.setSourceK3Id(recOrder.getfId());
            sr2.setSourceFnumber(recOrder.getFbillno());
            sr2.setSupplierId(recOrder.getSupplierId());
            sr2.setSupplierFnumber(recOrder.getSupplierNumber());
            sr2.setSupplierName(recOrder.getSupplierName());
            sr2.setMtlId(mtl.getfMaterialId());
            sr2.setMtl(mtl);
            sr2.setMtlFnumber(recOrder.getMtl().getfNumber());
            sr2.setUnitFnumber(recOrder.getMtl().getUnit().getUnitNumber());
            sr2.setPoFid(recOrder.getfId());
            sr2.setEntryId(recOrder.getEntryId());
            sr2.setPoFbillno(recOrder.getFbillno());
            sr2.setFprice(recOrder.getFprice());
            sr2.setFqty(recOrder.getFactreceiveqty());
            sr2.setUsableFqty(recOrder.getUsableFqty());
            sr2.setPoFmustqty(recOrder.getFactreceiveqty());
            sr2.setStockqty(0);

            // 默认物料的仓库仓位
            Stock mtlStock = mtl.getStock();
            StockPosition mtlStockPos = mtl.getStockPos();
            if(mtlStock != null) {
                setStockInfo(sr2, mtlStock);
                setStockPosInfo(sr2, mtlStockPos);
            } else {
                // 默认操作员的仓库仓位
                setStockInfo(sr2, stock);
                setStockPosInfo(sr2, stockP);
            }
            // 收料组织
            tvReceiveOrg.setText(recOrder.getRecOrgName());
            sr2.setReceiveOrgFnumber(recOrder.getRecOrgNumber());
            // 采购组织
            tvPurOrg.setText(recOrder.getPurOrgName());
            sr2.setPurOrgFnumber(recOrder.getPurOrgNumber());
            orgId = recOrder.getRecOrgId();
            // 物料是否启用序列号
            if(mtl.getIsSnManager() == 1) {
                sr2.setListBarcode(new ArrayList<String>());
            }
            sr2.setStrBarcodes("");

            ScanningRecordTok3 srTok3 = new ScanningRecordTok3();
            srTok3.setFpaezCgDanhao(recOrder.getPurOrderNo());
            srTok3.setFpurchaseDeptNumber(recOrder.getPurDeptNumber());
            srTok3.setFstockDeptNumber(recOrder.getRecDeptNumber());
            srTok3.setFpurchaserNumber(recOrder.getPurStaffNumber());
            srTok3.setStockStaffNumber(recOrder.getRecStaffNumber());
            srTok3.setFamount(recOrder.getFamount());
            sr2.setSrTok3(srTok3);

            checkDatas.add(sr2);
        }
        PurReceiveOrder recOrder = list.get(0);
        Staff recStaff = recOrder.getRecStaff();
        if(recStaff != null) {
            stockStaff = recStaff;
            tvStockStaff.setText(stockStaff.getName());
        }
        Staff purUser = recOrder.getPurStaff();
        if(purUser != null) {
            purStaff = purUser;
            tvPurStaff.setText(purStaff.getName());
        }
        tvSupplierSel.setText(recOrder.getSupplierName());

        mAdapter.notifyDataSetChanged();

        parent.isChange = true;
        return true;
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
     * 选择（物料）返回的值
     */
    private void getMaterialAfter(BarCodeTable bt) {
        int size = checkDatas.size();
        Material tmpMtl = bt.getMtl();
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
                    // 如果应收数大于实收数
                    if (sr2.getUsableFqty() > sr2.getStockqty()) {
                        // 如果扫的是物料包装条码，就显示个数
                        double number = 0;
                        if(bt != null) number = bt.getMaterialCalculateNumber();

                        if(number > 0) {
                            sr2.setStockqty(sr2.getStockqty()+(number*fqty));
                        } else {
                            sr2.setStockqty(sr2.getStockqty() + fqty);
                        }
                    } else {
                        // 数量已满
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行！，实收数不能大于应收数！");
                        return;
                    }

                } else { // 启用序列号
                    if (sr2.getUsableFqty() == sr2.getStockqty()) {
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，扫码录数已完成！");
                        return;
                    }
                    List<String> list = sr2.getListBarcode();
                    if(list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(mContext,"该物料条码已在列表中，请扫描未使用过的条码！");
                        return;
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
                        if((k+1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k)+",");
                    }
                    sr2.setListBarcode(list);
                    sr2.setStrBarcodes(sb.toString());
                    sr2.setStockqty(sr2.getStockqty() + 1);
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
     * 选择（部门）返回的值
     */
    private void getDeptAfter() {
        if (department != null) {
            tvDeptSel.setText(department.getDepartmentName());
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
            record.setCustomerK3Id(0);
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
            record.setFdate("");
            record.setPdaNo("");
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());
            record.setK3UserFnumber(user.getKdUserNumber());
            record.setSourceType('3');
//            record.setTempId(ism.getId());
//            record.setRelationObj(JsonUtil.objectToString(ism));
            record.setFsrcBillTypeId("PUR_ReceiveBill");
            record.setfRuleId("PUR_ReceiveBill-STK_InStock");
            record.setFsTableName("T_PUR_ReceiveEntry");
            record.setListBarcode(sr2.getListBarcode());
            record.setStrBarcodes(sr2.getStrBarcodes());
            record.setKdAccount(user.getKdAccount());
            record.setKdAccountPassword(user.getKdAccountPassword());
            record.setSrTok3(sr2.getSrTok3());

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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Log.e("run_addScanningRecord --> onResponse", result);
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
            case '1': // 收料订单
                mUrl = getURL("purReceiveOrder/findBarcode");
                barcode = sourceBarcode;
                strCaseId = "36";
                break;
            case '2': // 物料扫码
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                strCaseId = "11,21";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", strCaseId)
                .add("barcode", barcode)
                .add("sourceType","2") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
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
                    Message msg = mHandler.obtainMessage(UNSUCC2, result);
                    mHandler.sendMessage(msg);

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
        String mUrl = getURL("scanningRecord/findInStockSum");
        FormBody formBody = new FormBody.Builder()
                .add("fbillType", "2") // fbillType  1：采购订单入库，2：收料任务单入库，3：生产订单入库，4：销售订单出库，5：发货通知单出库
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
     * 提交并审核
     */
    private void run_submitAndPass() {
        showLoadDialog("正在审核...");
        String mUrl = getURL("scanningRecord/submitAndPass");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("fbillNo", k3Number)
                .add("type", "1")
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
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNPASS, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(PASS, result);
                Log.e("run_submitAndPass --> onResponse", result);
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