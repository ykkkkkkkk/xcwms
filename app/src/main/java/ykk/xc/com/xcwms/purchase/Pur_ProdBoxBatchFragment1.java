package ykk.xc.com.xcwms.purchase;


import android.app.Activity;
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
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.basics.Box_DialogActivity;
import ykk.xc.com.xcwms.basics.Cust_DialogActivity;
import ykk.xc.com.xcwms.basics.Dept_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.Box;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.model.MaterialBinningRecord;
import ykk.xc.com.xcwms.model.SecurityCode;
import ykk.xc.com.xcwms.model.pur.ProdOrder;
import ykk.xc.com.xcwms.purchase.adapter.Pur_ProdBoxBatchFragment1Adapter;
import ykk.xc.com.xcwms.sales.adapter.Sal_SelOrderAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;

import static android.app.Activity.RESULT_OK;

public class Pur_ProdBoxBatchFragment1 extends BaseFragment {

    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_sourceNo)
    TextView tvSourceNo;
    @BindView(R.id.tv_mtl)
    TextView tvMtl;
    @BindView(R.id.tv_batch)
    TextView tvBatch;
    @BindView(R.id.tv_boxSel)
    TextView tvBoxSel;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.et_boxCode)
    EditText etBoxCode;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_countInfo)
    TextView tvCountInfo; // 合计的信息  记录箱数，件数

    public Pur_ProdBoxBatchFragment1() {
    }

    private Pur_ProdBoxBatchFragment1 mFragment = this;
    private static final int SEL_DEPT = 10, SEL_ORDER = 11, SEL_BOX = 12, SEL_CUST = 13;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501;
    private static final int CODE1 = 1, CODE2 = 2, CODE60 = 60;
    private Department department; // 生产车间
    private ProdOrder prodOrder; // 生产订单
    private Box box; // 包装箱
    private SecurityCode securityCode; // 包装箱条码
    private Customer customer; // 客户
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Pur_ProdBoxBatchFragment1Adapter mAdapter;
    private List<MaterialBinningRecord> listDatas = new ArrayList<>();
    private Activity mContext;
    private String strBarcode; // 防伪码

    // 消息处理
    private MyHandler mHandler = new MyHandler(mFragment);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_ProdBoxBatchFragment1> mFrag;

        public MyHandler(Pur_ProdBoxBatchFragment1 activity) {
            mFrag = new WeakReference<Pur_ProdBoxBatchFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_ProdBoxBatchFragment1 m = mFrag.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        m.securityCode = JsonUtil.strToObject((String) msg.obj, SecurityCode.class);
                        m.setTexts(m.etBoxCode, m.securityCode.getSecurityQrCode());
                        m.strBarcode = m.securityCode.getSecurityQrCode();

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.mHandler.sendEmptyMessageDelayed(CODE60, 200);
                        Comm.showWarnDialog(m.mContext, "很抱歉，没能找到数据！");

                        break;
                    case CODE1: // 清空数据
                        m.etBoxCode.setText("");
                        m.strBarcode = "";

                        break;
                    case CODE60: // 没有得到数据，就把回车的去掉，恢复正常数据
                        m.setTexts(m.etBoxCode, m.strBarcode);

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_prod_box_batch_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_ProdBoxBatchFragment1Adapter(mContext, listDatas);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        bundle();
    }

    private void bundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
        }
    }

    @OnClick({R.id.tv_deptSel, R.id.tv_sourceNo, R.id.tv_boxSel, R.id.tv_custSel, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_deptSel: // 选择生产车间
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

                break;
            case R.id.tv_sourceNo: // 选择来源单
                if (department == null) {
                    Comm.showWarnDialog(mContext,"请选择生产车间！");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("fbillno", getValues(tvSourceNo));
                bundle.putSerializable("department", department);
                showForResult(Pur_SelProdOrderActivity.class, SEL_ORDER, bundle);

                break;
            case R.id.tv_box: // 选择包装箱
                showForResult(Box_DialogActivity.class, SEL_BOX, null);

                break;
            case R.id.tv_custSel: // 选择客户
                showForResult(Cust_DialogActivity.class, SEL_CUST, null);

                break;
            case R.id.btn_save: // 封箱


                break;
        }
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (v.getId()) {
                    case R.id.et_mtlCode: // 物料
                        if(event.getAction() == KeyEvent.ACTION_DOWN) {
                            String mtlCode = getValues(etBoxCode).trim();
                            if(!smBefore()) {
                                mHandler.sendEmptyMessageDelayed(CODE1,200);
                                return false;
                            }
                            if (isKeyDownEnter(mtlCode, event, keyCode)) {
                                if (strBarcode != null && strBarcode.length() > 0) {
                                    String tmp = mtlCode.replaceFirst(strBarcode, "");
                                    strBarcode = tmp.replace("\n", "");
                                } else {
                                    strBarcode = mtlCode.replace("\n", "");
                                }
                                // 执行查询方法
                                run_smGetDatas();
                            }
                        }

                        break;
                }
                return false;
            }
        };
        etBoxCode.setOnKeyListener(keyListener);
    }

    /**
     * 扫码防伪码之前的判断
     */
    private boolean smBefore() {
        if (department == null) {
            Comm.showWarnDialog(mContext,"请选择生产车间！");
            return false;
        }
        if (prodOrder == null) {
            Comm.showWarnDialog(mContext,"请选择生产订单！");
            return false;
        }
        if (customer == null) {
            Comm.showWarnDialog(mContext,"请选择客户！");
            return false;
        }
        if (box == null) {
            Comm.showWarnDialog(mContext,"请选择包装箱！");
            return false;
        }
        return true;
    }


    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("barCodeTable/findBarcodeByParam");
        String strCaseId = ""; // 方案id
        FormBody formBody = new FormBody.Builder()
                .add("barcode", getValues(etBoxCode))
                .add("strCaseId", strCaseId)
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("run_smGetDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
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
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("findProdOrderList");
        FormBody formBody = new FormBody.Builder()
                .add("custId", String.valueOf(customer.getFcustId()))
//                .add("limit", "10")
//                .add("pageSize", "100")
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("Sal_SelOrderActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DEPT: // 查询部门	    返回
                if (resultCode == RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                }

                break;
            case SEL_ORDER: // 查询生产订单    返回
                if (resultCode == RESULT_OK) {
                    Bundle bundle  = data.getExtras();
                    if(bundle != null) {
                        prodOrder = (ProdOrder) bundle.getSerializable("obj");
                        Log.e("onActivityResult --> SEL_ORDER", prodOrder.getFbillno());
                        tvSourceNo.setText(prodOrder.getFbillno());
                        tvMtl.setText(prodOrder.getMtlFname());

                        String batch = prodOrder.getBatchCode();
                        if(batch != null && batch.length() > 0) {
                            tvBatch.setText("批号："+batch);
                            tvBatch.setVisibility(View.VISIBLE);
                        } else {
                            tvBatch.setVisibility(View.GONE);
                        }
                    }
                }

                break;
            case SEL_BOX: // 查询包装箱      返回
                if (resultCode == RESULT_OK) {
                    box = (Box) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_BOX", box.getBoxName());
                    tvBoxSel.setText(box.getBoxName());
                }

                break;
            case SEL_CUST: //查询部门	返回
                if (resultCode == RESULT_OK) {
                    customer = (Customer) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_CUST", customer.getCustomerName());
                    tvCustSel.setText(customer.getCustomerName());
                }

                break;
        }
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

}
