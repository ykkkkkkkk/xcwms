package ykk.xc.com.xcwms.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
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
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.Supplier;
import ykk.xc.com.xcwms.model.pur.PurPoOrder;
import ykk.xc.com.xcwms.purchase.adapter.Sel_PurOrderAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.xrecyclerview.XRecyclerView;

public class Sel_PurOrderActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.tv_custInfo)
    TextView tvCustInfo;
    @BindView(R.id.cbAll)
    CheckBox cbAll;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private Sel_PurOrderActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private Supplier supplier; // 供应商
    private OkHttpClient okHttpClient = new OkHttpClient();
    private FormBody formBody = null;
    private Sel_PurOrderAdapter mAdapter;
    private List<PurPoOrder> listDatas;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Sel_PurOrderActivity> mActivity;

        public MyHandler(Sel_PurOrderActivity activity) {
            mActivity = new WeakReference<Sel_PurOrderActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Sel_PurOrderActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        m.listDatas = JsonUtil.strToList2((String) msg.obj, PurPoOrder.class);
//                        m.listDatas = m.gGson().fromJson((String) msg.obj, new TypeToken<List<PO_list>>(){}.getType());

                        m.updateUI();

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.sel_pur_order;
    }

    @Override
    public void initData() {
        bundle();
        run_okhttpDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            supplier = (Supplier) bundle.getSerializable("supplier");
            tvCustInfo.setText("供应商：" + supplier.getfName());
        }
    }


    @OnClick({R.id.btn_close, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_confirm: // 确认
                if(listDatas == null || listDatas.size() == 0) {
                    toasts("请选择数据在确认！");
                    return;
                }
                List<PurPoOrder> list = new ArrayList<>();
                for(int i = 0, size = listDatas.size(); i<size; i++) {
                    PurPoOrder p = listDatas.get(i);
                    if(p.getIsCheck() == 1) {
                        list.add(p);
                    }
                }
                if(list.size() == 0) {
                    toasts("请勾选数据行！");
                    return;
                }
//                String result = JsonUtil.objectToString(list);
                Bundle bundle = new Bundle();
                bundle.putSerializable("checkDatas", (Serializable) list);
                setResults(context, bundle);
                context.finish();

                break;
        }
    }

    @OnCheckedChanged(R.id.cbAll)
    public void onViewChecked(CompoundButton buttonView, boolean isChecked) {
        if (listDatas == null) {
            return;
        }
        if (isChecked) {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                PurPoOrder p = listDatas.get(i);
                p.setIsCheck(1);
            }
        } else {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                PurPoOrder p = listDatas.get(i);
                p.setIsCheck(0);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("findPurPoOrderList");
        FormBody formBody = new FormBody.Builder()
//                .add("fbillno", getValues(etFbillno).trim())
                .add("supplierId", String.valueOf(supplier.getFsupplierid()))
//                .add("supplierName", supplier.)
//                .add("mtlFnumber", getValues(etMtlFnumber).trim())
//                .add("mtlFname", getValues(etMtlFname).trim())
//                .add("poFdateBeg", getValues(tvBegDate))
//                .add("poFdateEnd", getValues(tvEndDate))
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
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("Ware_Pur_OrderActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 更新UI
     */
    private void updateUI() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        xRecyclerView.setLayoutManager(layoutManager);
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Sel_PurOrderAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view
    }


    @Override
    public void onRefresh() {
//        isRefresh = true;
//        isLoadMore = false;
//        page = 1;
//        initData();
    }

    @Override
    public void onLoadMore() {
//        isRefresh = false;
//        isLoadMore = true;
//        page += 1;
//        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case SEL_CUST: //查询供应商	返回
//                if (resultCode == RESULT_OK) {
//                    supplier = data.getParcelableExtra("obj");
//                    Log.e("onActivityResult --> SEL_CUST", supplier.getFname());
//                    if (supplier != null) {
//                        setTexts(etCustSel, supplier.getFname());
//                    }
//                }
//
//                break;
//        }
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
