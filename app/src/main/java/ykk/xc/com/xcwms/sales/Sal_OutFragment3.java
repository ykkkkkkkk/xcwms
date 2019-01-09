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

import butterknife.BindView;
import butterknife.OnClick;
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
import ykk.xc.com.xcwms.basics.Staff_DialogActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.model.ExpressCompany;
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
import ykk.xc.com.xcwms.model.sal.DeliOrder;
import ykk.xc.com.xcwms.model.sal.PickingList;
import ykk.xc.com.xcwms.sales.adapter.Sal_OutFragment3Adapter;
import ykk.xc.com.xcwms.util.JsonUtil;

/**
 * 扫箱码 出库
 */
public class Sal_OutFragment3 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.tv_pickingListSel)
    TextView tvPickingListSel;
    @BindView(R.id.et_deliCode)
    EditText etDeliCode;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_receiveOrg)
    TextView tvReceiveOrg;
    @BindView(R.id.tv_salOrg)
    TextView tvSalOrg;
    @BindView(R.id.tv_stockStaff)
    TextView tvStockStaff;
    @BindView(R.id.tv_expressCompany)
    TextView tvExpressCompany;
    @BindView(R.id.et_expressNo)
    EditText etExpressNo;
    @BindView(R.id.lin_top)
    LinearLayout linTop;
    @BindView(R.id.btn_save)
    Button btnSave;

    private Sal_OutFragment3 context = this;
    private static final int SEL_PICKINGLIST = 10, SEL_DEPT = 11, SEL_ORG = 12, SEL_ORG2 = 13, SEL_EXPRESS = 14, SEL_STOCK2 = 15, SEL_STOCKP2 = 16, SEL_STAFF = 17, PAD_SM = 18;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int CODE1 = 1, UPDATE = 2, UPDATE_NULL = 3;
    private Stock stock2; // 仓库
    private StockPosition stockP2; // 库位
    private Department department; // 部门
    private Staff stockStaff; // 仓管员
    private Organization receiveOrg, salOrg; // 组织
    private ExpressCompany expressCompany; // 物料公司
    private Sal_OutFragment3Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String barcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：箱码
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private Activity mContext;
    private Sal_OutMainActivity parent;
    private String k3Number; // 记录传递到k3返回的单号
    private boolean isTextChange; // 是否进入TextChange事件

    // 消息处理
    private Sal_OutFragment3.MyHandler mHandler = new Sal_OutFragment3.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_OutFragment3> mActivity;

        public MyHandler(Sal_OutFragment3 activity) {
            mActivity = new WeakReference<Sal_OutFragment3>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_OutFragment3 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
//                        m.reset();
                        m.btnSave.setVisibility(View.GONE);
                        // 反写k3number 到PickingList表
                        m.run_modifyK3number();
                        Comm.showWarnDialog(m.mContext,"保存成功，请点击“审核按钮”！");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                    case PASS: // 审核成功 返回
                        m.k3Number = null;
                        m.btnSave.setVisibility(View.VISIBLE);
                        m.reset();
                        Comm.showWarnDialog(m.mContext,"审核成功✔");

                        break;
                    case UNPASS: // 审核失败 返回
                        String errMsg = JsonUtil.strToString((String)msg.obj);
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 发货通知单 扫码
                        switch (m.curViewFlag) {
                            case '1': // 拣货单
                                List<PickingList> list2 = JsonUtil.strToList((String) msg.obj, PickingList.class);
                                PickingList p = list2.get(0);
//                                m.tvPickingListSel.setText(p.getFbillno());
                                m.tvCustSel.setText(p.getCustName());
                                m.getPickingList(list2);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        String errMsg2 = m.isNULLS((String) msg.obj);
                        if(errMsg2.length() > 0) {
                            String message = JsonUtil.strToString(errMsg2);
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
                    case PAD_SM: // pad扫码
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '1': // 发货单
                                etName = m.getValues(m.etDeliCode);
                                if (m.barcode != null && m.barcode.length() > 0) {
                                    if(m.barcode.equals(etName)) {
                                        m.barcode = etName;
                                    } else m.barcode = etName.replaceFirst(m.barcode, "");

                                } else m.barcode = etName;
                                m.setTexts(m.etDeliCode, m.barcode);
                                // 执行查询方法
                                m.run_smGetDatas(m.barcode);

                                break;
                        }

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sal_out_fragment3, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Sal_OutMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Sal_OutFragment3Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Sal_OutFragment3Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
//                curPos = position;
//                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", CODE1);
            }

            @Override
            public void onClick_selStock(View v, ScanningRecord2 entity, int position) {
                Log.e("selStock", "行：" + position);
                curPos = position;

                showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
            }

        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etDeliCode);
        hideSoftInputMode(mContext, etExpressNo);
        getUserInfo();

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
                public void run() { setFocusable(etDeliCode); // 物料代码获取焦点
                }
            },200);
        }
    }

    @OnClick({R.id.tv_pickingListSel, R.id.btn_save, R.id.btn_pass, R.id.btn_clone, R.id.tv_orderTypeSel, R.id.tv_receiveOrg, R.id.tv_salOrg,
            R.id.tv_deptSel, R.id.tv_stockStaff, R.id.lin_rowTitle, R.id.tv_expressCompany})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_orderTypeSel: // 订单类型


                break;
            case R.id.tv_pickingListSel: // 选择拣货单
                bundle = new Bundle();
                bundle.putString("pickingType","2");
                showForResult(Sal_SelPickingListActivity.class, SEL_PICKINGLIST, bundle);

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
                            reset();
                            k3Number = null;
                            btnSave.setVisibility(View.VISIBLE);
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    reset();
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
//        if(receiveOrg == null) {
//            Comm.showWarnDialog(mContext,"请选择发货组织！");
//            return false;
//        }
//        if(salOrg == null) {
//            Comm.showWarnDialog(mContext,"请选择销售组织！");
//            return false;
//        }
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

            // 员工
            if(stockStaff != null) srToK3.setStockStaffNumber(stockStaff.getNumber());
            if (sr2.getStockId() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，请选择（仓库）！");
                return false;
            }
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实发数）必须大于0！");
                return false;
            }
//            if (sr2.getStockqty() > sr2.getFqty()) {
//                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实发数）不能大于（应发数）！");
//                return false;
//            }
//            if (sr2.getStockqty() < sr2.getFqty()) {
//                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实发数）必须等于（应发数）！");
//                return false;
//            }
            if(sr2.getStockqty() > 0) {
                list.add(sr2);
            }
        }
        checkDatas.clear();
        checkDatas.addAll(list);

        return true;
    }

    @Override
    public void setListener() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusable(etGetFocus);
                switch (v.getId()) {
                    case R.id.et_deliCode: // 发货单
                        curViewFlag = '1';
                        setFocusable(etDeliCode);
                        break;
                    case R.id.et_expressNo: // 运单号
                        setFocusable(etExpressNo);
                        break;
                }
            }
        };
        etDeliCode.setOnClickListener(click);
        etExpressNo.setOnClickListener(click);

        // 发货单号
        etDeliCode.addTextChangedListener(new TextWatcher() {
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
                        barcode = s.toString();
                        // 执行查询方法
                        run_smGetDatas(barcode);
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
     */
    private void reset() {
        linTop.setVisibility(View.VISIBLE);
        tvPickingListSel.setText("");
        tvCustSel.setText("客户：");
        tvExpressCompany.setText("");
        etExpressNo.setText("");
        expressCompany = null;
        tvReceiveOrg.setText("");
        tvSalOrg.setText("");
        department = null;
        receiveOrg = null;
        salOrg = null;
        curViewFlag = '1';

        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
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
        switch (curViewFlag) {
            case '1': // 发货单扫码
                mUrl = getURL("pickingList/findBarcode");
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("barcode", barcode)
                .add("pickingType","2")
                .add("isUpload", "1")
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_PICKINGLIST: //查询拣货单	返回
                if (resultCode == Activity.RESULT_OK) {
                    List<PickingList> list = (List<PickingList>) data.getSerializableExtra("checkDatas");

                    PickingList p = list.get(0);
                    tvPickingListSel.setText(p.getFbillno());
                    tvCustSel.setText(p.getCustName());
                    getPickingList(list);
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
            case CODE1: // 数量
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
            case SEL_STAFF: // 采购员	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockStaff = (Staff) data.getSerializableExtra("staff");
                    tvStockStaff.setText(stockStaff.getName());
                    saveObjectToXml(stockStaff, "strStockStaff", getResStr(R.string.saveUser));
                }
                break;
        }
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
     * 得到拣货单列表
     */
    private void getPickingList(List<PickingList> list) {
        // 判断扫描重复单据
        if(checkDatas.size() > 0) {
            Comm.showWarnDialog(mContext,"请先保存当前行数据！");
            return;
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            PickingList pl = list.get(i);
            DeliOrder deliOrder = pl.getDeliOrder();
            ScanningRecord2 sr2 = new ScanningRecord2();
            Material mtl = pl.getMtl();

            sr2.setSourceId(pl.getId());
            sr2.setSourceK3Id(pl.getId());
            sr2.setSourceFnumber(pl.getPickingListNo());
//            sr2.setSourceK3Id(pl.getfId());
//            sr2.setSourceFnumber(pl.getFbillno());
            sr2.setMtlId(pl.getMtlId());
            sr2.setMtl(mtl);
            sr2.setMtlFnumber(mtl.getfNumber());
            sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
            sr2.setPoFid(pl.getfId());
            sr2.setSalOrderNo(deliOrder.getSalOrderNo());
            sr2.setPoFbillno(pl.getFbillno());
            sr2.setFprice(pl.getFprice());
//            sr2.setBatchno(pl.getBatchCode());
//            sr2.setSequenceNo(pl.getSnCode());
            sr2.setBarcode(pl.getBarcode());
            sr2.setStockId(pl.getStockId());
            sr2.setStockFnumber(pl.getStockNumber());
            sr2.setStockName(pl.getStockName());
            sr2.setStockPositionId(pl.getStockPositionId());
            sr2.setStockPName(pl.getStockPositionName());
            StockPosition stockP = new StockPosition();
            stockP.setfStockPositionId(pl.getStockPositionId());
            stockP.setFnumber(pl.getStockPositionNumber());
            stockP.setFname(pl.getStockPositionName());
            sr2.setStockPos(stockP);

            sr2.setEntryId(pl.getEntryId());
            sr2.setFqty(pl.getDeliFqty());
            sr2.setPoFmustqty(pl.getDeliFqty());
            sr2.setUsableFqty(pl.getPickingListNum());
            sr2.setStockqty(pl.getPickingListNum());
            // 发货组织
            if(receiveOrg == null) receiveOrg = new Organization();
            receiveOrg.setFpkId(pl.getDeliOrgId());
            receiveOrg.setNumber(pl.getDeliOrgNumber());
            receiveOrg.setName(pl.getDeliOrgName());
            tvReceiveOrg.setText(receiveOrg.getName());
            sr2.setReceiveOrgFnumber(receiveOrg.getNumber());

            // 销售组织
            if(salOrg == null) salOrg = new Organization();
            if(pl.getSalOrgId() > 0) {
                salOrg.setFpkId(pl.getSalOrgId());
                salOrg.setNumber(pl.getSalOrgNumber());
                salOrg.setName(pl.getSalOrgName());
            } else {
                salOrg.setFpkId(pl.getDeliOrgId());
                salOrg.setNumber(pl.getDeliOrgNumber());
                salOrg.setName(pl.getDeliOrgName());
            }

            tvSalOrg.setText(salOrg.getName());
            sr2.setPurOrgFnumber(salOrg.getNumber());
            sr2.setCustomerId(pl.getCustId());
            sr2.setCustomerName(pl.getCustName());
            sr2.setCustFnumber(pl.getCustNumber());
            tvCustSel.setText("客户："+pl.getCustName());
            sr2.setSourceType('6');
//            sr2.setTempId(ism.getId());
//            sr2.setRelationObj(JsonUtil.objectToString(ism));
            sr2.setFsrcBillTypeId("SAL_DELIVERYNOTICE");
            sr2.setfRuleId("SAL_DELIVERYNOTICE-SAL_OUTSTOCK");
            sr2.setFsTableName("T_SAL_DELIVERYNOTICEENTRY");
            String deliveryCompanyId = isNULLS(pl.getDeliveryCompanyId());
            String deliveryCompanyNumber = isNULLS(pl.getDeliveryCompanyNumber());
            String deliveryCompanyName = isNULLS(pl.getDeliveryCompanyName());
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
            srTok3.setFboxAmount(0); // 箱数
            srTok3.setFlhDbdj(deliOrder.getFlhDbdj());
            srTok3.setFlhDbj(deliOrder.getFlhDbj());
            sr2.setSrTok3(srTok3);

            checkDatas.add(sr2);
        }
        PickingList pl = list.get(0);
        DeliOrder deliOrder = pl.getDeliOrder();
        Staff stockStaff = deliOrder.getStockStaff();
        if(stockStaff != null) {
            context.stockStaff = deliOrder.getStockStaff();
            tvStockStaff.setText(stockStaff.getName());
        }
        tvExpressCompany.setText(expressCompany.getExpressName());
        mAdapter.notifyDataSetChanged();
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
            record.setFdate("");
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
     * 传入k3成功，反写k3单号的数据
     */
    private void run_modifyK3number() {
        showLoadDialog("更新中...");
        ScanningRecord2 sr2 = checkDatas.get(0);
        String mUrl = getURL("pickingList/modifyK3number");
        FormBody formBody = new FormBody.Builder()
                .add("pickingListNo", sr2.getSourceFnumber())
                .add("k3number", k3Number)
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
                mHandler.sendEmptyMessage(UPDATE_NULL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UPDATE_NULL);
                    return;
                }
                Message msg = mHandler.obtainMessage(UPDATE, result);
                Log.e("run_modifyK3number --> onResponse", result);
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
