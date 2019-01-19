package ykk.xc.com.xcwms.produce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Collective;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.MaterialProcedureTime;
import ykk.xc.com.xcwms.model.MaterialProcedureTimeEntry;
import ykk.xc.com.xcwms.model.Procedure;
import ykk.xc.com.xcwms.model.Staff;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.ValuationPayroll;
import ykk.xc.com.xcwms.model.ValuationType;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.LogUtil;

public class Prod_ProcedureReportActivity extends BaseActivity {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.tv_valuationType)
    TextView tvValuationType;
    @BindView(R.id.tv_collTitle)
    TextView tvCollTitle;
    @BindView(R.id.tv_coll)
    TextView tvColl;
    @BindView(R.id.tv_process)
    TextView tvProcess;
    @BindView(R.id.tv_staff)
    TextView tvStaff;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.et_staffCode)
    EditText etStaffCode;
    @BindView(R.id.tv_mtlNumber)
    TextView tvMtlNumber;
    @BindView(R.id.tv_mtlName)
    TextView tvMtlName;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.relative_Info)
    RelativeLayout relativeInfo;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv_remark)
    TextView tvRemark;

    private Prod_ProcedureReportActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, BIND = 1;
    private static final int SEL_ORDER = 10, CODE1 = 11, SCAN = 12;
    private Material mtl;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String mtlBarcode, staffBarcode; // 对应的条码号
    private DecimalFormat df = new DecimalFormat("#.######");
    private char curViewFlag = '1'; // 1：物料，2：员工
    private User user;
    private Staff staff;
    private Collective collective; // 集体
    private Procedure procedure; // 工序表
    private MaterialProcedureTimeEntry mptEntry; // 工序工时entry表
    private boolean isTextChange; // 是否为平板电脑
    private String mobileMac; // 本地Mac地址
    private boolean notGetFosus; // 不能得到焦点


    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_ProcedureReportActivity> mActivity;

        public MyHandler(Prod_ProcedureReportActivity activity) {
            mActivity = new WeakReference<Prod_ProcedureReportActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_ProcedureReportActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1: // 默认工序 返回
                        m.procedure = JsonUtil.strToObject((String)msg.obj, Procedure.class);
                        ValuationType vt = m.procedure.getValuationType();
                        m.tvProcess.setText(m.procedure.getProcedureName());
                        if(vt.getDescription().indexOf("集体") > -1) {
                            m.tvCollTitle.setVisibility(View.VISIBLE);
                            m.tvColl.setVisibility(View.VISIBLE);
                        } else {
                            m.tvCollTitle.setVisibility(View.GONE);
                            m.tvColl.setVisibility(View.GONE);
                        }
                        m.tvValuationType.setText(vt.getDescription());
                        m.collective = m.procedure.getCollective();
                        if(m.collective != null)
                            m.tvColl.setText(m.collective.getCollectiveName());
                        else m.tvColl.setText("");

                        m.setEnables(m.etMtlCode, R.drawable.back_style_blue, true);
                        m.setEnables(m.etStaffCode, R.drawable.back_style_blue, true);
                        if(!m.notGetFosus) m.setFocusable(m.etMtlCode);
                        m.notGetFosus = false;

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        Comm.showWarnDialog(m.context, errMsg);
                        m.procedure = null;
                        m.tvProcess.setText("");
                        m.tvValuationType.setText("");
                        m.tvColl.setText("");
                        m.setEnables(m.etMtlCode, R.drawable.back_style_gray3, false);
                        m.setEnables(m.etStaffCode, R.drawable.back_style_gray3, false);

                        break;
                    case SUCC2: // 成功
                        Comm.showWarnDialog(m.context,"保存成功");
                        m.mptEntry = null;
                        m.tvMtlNumber.setText("物料编号：");
                        m.tvMtlName.setText("物料名称：");
                        m.tvStaff.setText(Html.fromHtml("员工：<font color='#000000'>"+m.staff.getName()+"</font>"));
                        m.etMtlCode.setText("");
                        m.etStaffCode.setText("");
                        m.setFocusable(m.etMtlCode);

                        break;
                    case UNSUCC2: // 数据加载失败！
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case SUCC3: // 物料扫码   成功
                        switch (m.curViewFlag) {
                            case '1': // 物料
                                m.mptEntry = JsonUtil.strToObject((String)msg.obj, MaterialProcedureTimeEntry.class);
                                MaterialProcedureTime mpt = m.mptEntry.getMaterialProcedureTime();
                                Material mtl = mpt.getMaterial();
                                m.tvMtlNumber.setText(Html.fromHtml("物料编号：<font color='#000000'>"+mtl.getfNumber()+"</font>"));
                                m.tvMtlName.setText(Html.fromHtml("物料名称：<font color='#000000'>"+mtl.getfName()+"</font>"));
                                m.setFocusable(m.etStaffCode);

                                break;
                            case '2': // 员工
                                BarCodeTable bt = JsonUtil.strToObject((String)msg.obj, BarCodeTable.class);
                                Staff staff = JsonUtil.stringToObject(bt.getRelationObj(), Staff.class);
                                m.staff = staff;
                                m.tvStaff.setText(Html.fromHtml("员工：<font color='#000000'>"+staff.getName()+"</font>"));
                                m.notGetFosus = true;
                                // 重新检测当前工序工序集体下是否为这个人
                                m.run_findBindingProcedure();

                                break;
                        }

                        break;
                    case UNSUCC3: // 数据加载失败！
                        switch (m.curViewFlag) {
                            case '1': // 物料
                                m.mptEntry = null;
                                m.tvMtlNumber.setText("物料编号：");
                                m.tvMtlName.setText("物料名称：");

                                break;
                            case '2': // 员工
                                m.staff = null;
                                m.tvStaff.setText("员工：");

                                break;
                        }
                        String errMsg2 = JsonUtil.strToString((String) msg.obj);
                        Comm.showWarnDialog(m.context, errMsg2);

                        break;
                    case SCAN: // pad扫码
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '1': // 物料
                                etName = m.getValues(m.etMtlCode);
                                if (m.mtlBarcode != null && m.mtlBarcode.length() > 0) {
                                    if(m.mtlBarcode.equals(etName)) {
                                        m.mtlBarcode = etName;
                                    } else m.mtlBarcode = etName.replaceFirst(m.mtlBarcode, "");

                                } else m.mtlBarcode = etName;
                                m.setTexts(m.etMtlCode, m.mtlBarcode);

                                break;
                            case '2': // 员工
                                etName = m.getValues(m.etStaffCode);
                                if (m.staffBarcode != null && m.staffBarcode.length() > 0) {
                                    if(m.staffBarcode.equals(etName)) {
                                        m.staffBarcode = etName;
                                    } else m.staffBarcode = etName.replaceFirst(m.staffBarcode, "");

                                } else m.staffBarcode = etName;
                                m.setTexts(m.etStaffCode, m.staffBarcode);

                                break;
                        }
                        m.run_smGetDatas();

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_procedure_report;
    }

    @Override
    public void initView() {
        mobileMac = Comm.getAddressMac(context);
    }

    @Override
    public void initData() {
        hideSoftInputMode(etMtlCode);
        hideSoftInputMode(etStaffCode);
        getUserInfo();
        staff = user.getStaff();
        if(staff != null) {
            tvStaff.setText(Html.fromHtml("员工：<font color='#000000'>" + staff.getName() + "</font>"));
        }
        run_findBindingProcedure();
    }

    @OnClick({R.id.btn_close, R.id.btn_bind, R.id.tv_num, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_bind: // 计件类型
                Bundle bundle = new Bundle();
                bundle.putBoolean("isBack", true);
                showForResult(Prod_ProcedureBindingActivity.class, BIND, bundle);

                break;
            case R.id.tv_num: // 选择数量
//                showInputDialog("数量", getValues(tvNum), "0", CODE1);

                break;
            case R.id.btn_save: // 保存
                if(getValues(tvProcess).length() == 0) {
                    Comm.showWarnDialog(context,"请绑定工序！");
                    return;
                }
                ValuationType vt = procedure.getValuationType();
                if(vt.getDescription().indexOf("集体") > -1 && getValues(tvColl).length() == 0) {
                    Comm.showWarnDialog(context,"请在PC端维护集体下的人员和对应工序！");
                    return;
                }
                if(mptEntry == null) {
                    Comm.showWarnDialog(context,"请扫描有效的物料条码！");
                    return;
                }
                if(staff == null) {
                    Comm.showWarnDialog(context,"请扫描有效的员工条码！");
                    return;
                }

//                if(parseInt(getValues(tvNum)) == 0) {
//                    Comm.showWarnDialog(context,"请选择数量！");
//                    return;
//                }
                ValuationPayroll vp = new ValuationPayroll();
                vp.setId(0);
                if(getValues(tvValuationType).indexOf("集体") > -1) {
                    vp.setCollectiveId(collective.getId());
                    vp.setStaffId(0);
                    vp.setDeptId(0);
                } else {
                    vp.setCollectiveId(0);
                    vp.setStaffId(staff.getStaffId());
                    vp.setDeptId(parseInt(staff.getStaffPostDept()));
                }

                MaterialProcedureTime mpt = mptEntry.getMaterialProcedureTime();
                vp.setfMaterialId(mpt.getfMaterialId());
                vp.setProcessflowId(mpt.getProcessflowId());
                vp.setProcedureId(procedure.getId());
                vp.setValuationTypeId(vt.getId());
                vp.setTotalNumber(parseInt(getValues(tvNum)));
                vp.setJobTime(mptEntry.getWorkTime());
                vp.setBarCode(mtlBarcode);
                vp.setSequent(mptEntry.getSeqNo());

                vp.setCreaterId(user.getId());
                vp.setCreaterName(user.getUsername());
                vp.setValState(1);
                vp.setCreateWay(1);

                run_save(vp);

                break;
        }
    }

    @Override
    public void setListener() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusable(etGetFocus);
                switch (v.getId()) {
                    case R.id.et_mtlCode: // 物料
                        curViewFlag = '1';
                        setFocusable(etMtlCode);
                        break;
                    case R.id.et_staffCode: // 员工
                        curViewFlag = '2';
                        setFocusable(etStaffCode);
                        break;
                }
            }
        };
        etMtlCode.setOnClickListener(click);
        etStaffCode.setOnClickListener(click);

        // 物料
        etMtlCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '1';

                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SCAN,600);
                }
            }
        });

        // 物料
        etStaffCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '2';

                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SCAN,600);
                }
            }
        });
    }

    /**
     * 查询绑定的工序
     */
    private void run_findBindingProcedure() {
        isTextChange = false;
        showLoadDialog("加载中...");
        String mUrl = getURL("procedure/findBindingProcedure");
        FormBody formBody = new FormBody.Builder()
                .add("mobileMac", mobileMac)
                .add("staffId", staff != null ? String.valueOf(staff.getStaffId()) : "")
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
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_findBindingProcedure --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
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
    private void run_smGetDatas() {
        isTextChange = false;
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        switch (curViewFlag) {
            case '1': // 物料扫码
                mUrl = getURL("materialProcedureTime/findMptEntryByPortion_app");
                barcode = mtlBarcode;
                strCaseId = "";
                break;
            case '2': // 员工扫码
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = staffBarcode;
                strCaseId = "16";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("procedureId", String.valueOf(procedure.getId())) // 物料条件
                .add("strCaseId", strCaseId) // 员工条件
                .add("barcode", barcode) // 都有
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
                    Message msg = mHandler.obtainMessage(UNSUCC3, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
                Log.e("run_smGetDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 保存
     */
    private void run_save(ValuationPayroll vp) {
        showLoadDialog("保存中...");
        String mUrl = getURL("valuationPayroll/insertList");
        String mJson = JsonUtil.objectToString(vp);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
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
                LogUtil.e("run_save --> onResponse", result);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BIND: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        run_findBindingProcedure();
                    }
                }

                break;
            case CODE1: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        tvNum.setText(df.format(num));
                    }
                }

                break;

        }
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) user = showUserByXml();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean isNext = Comm.smKeyIsValid(context, event);
        return isNext ? super.dispatchKeyEvent(event) : false;
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
