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
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.Supplier;
import ykk.xc.com.xcwms.model.pur.PurOrder;
import ykk.xc.com.xcwms.model.pur.PurReceiveOrder;
import ykk.xc.com.xcwms.model.sal.PickingList;
import ykk.xc.com.xcwms.purchase.adapter.Pur_SelOrderAdapter;
import ykk.xc.com.xcwms.purchase.adapter.Pur_SelReceiveOrderAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.basehelper.BaseRecyclerAdapter;
import ykk.xc.com.xcwms.util.xrecyclerview.XRecyclerView;

public class Pur_SelReceiveOrderActivity extends BaseActivity implements XRecyclerView.LoadingListener {

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

    private Pur_SelReceiveOrderActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private Supplier supplier; // 供应商
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Pur_SelReceiveOrderAdapter mAdapter;
    private List<PurReceiveOrder> listDatas = new ArrayList<>();
    private List<PurReceiveOrder> sourceList; // 上个界面传来的数据列表
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Pur_SelReceiveOrderActivity> mActivity;

        public MyHandler(Pur_SelReceiveOrderActivity activity) {
            mActivity = new WeakReference<Pur_SelReceiveOrderActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_SelReceiveOrderActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<PurReceiveOrder> list = JsonUtil.strToList2((String) msg.obj, PurReceiveOrder.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }
                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新开启
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC1: // 数据加载失败！
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        m.toasts(errMsg);

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.pur_sel_receive_order;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Pur_SelReceiveOrderAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                PurReceiveOrder recOrder = listDatas.get(pos-1);
                int size = listDatas.size();
                String fbillno = recOrder.getFbillno();
                for(int i=0; i<size; i++) {
                    listDatas.get(i).setIsCheck(0);
                }
                for(int i=0; i<size; i++) {
                    PurReceiveOrder recOrder2 = listDatas.get(i);
                    if(fbillno.equals(recOrder2.getFbillno())) {
                        recOrder2.setIsCheck(1);
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        bundle();
        initLoadDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            supplier = (Supplier) bundle.getSerializable("supplier");
            sourceList = (List<PurReceiveOrder>) bundle.getSerializable("sourceList");
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
                List<PurReceiveOrder> list = new ArrayList<>();
                for(int i = 0, size = listDatas.size(); i<size; i++) {
                    PurReceiveOrder p = listDatas.get(i);

                    int batch = p.getMtl().getIsBatchManager();
                    int snNo = p.getMtl().getIsSnManager();
                    // 选中了行
                    if(p.getIsCheck() == 1) {
                        if(sourceList != null) {
                            for (int j = 0; j < sourceList.size(); j++) {
                                PurReceiveOrder purOrder2 = sourceList.get(j);
                                // 如果已经选择了相同的行，就提示
                                if (p.getfId() == purOrder2.getfId() && p.getMtlId() == purOrder2.getMtlId() && p.getEntryId() == purOrder2.getEntryId()) {
                                    Comm.showWarnDialog(context, "第" + (i + 1) + "行已经在入库的列表中，不能重复选择！");
                                    return;
                                }
                            }
                        }
                        // 启用了批次货序列号，如果还没生码，就提示
//                        if(p.getBct() == null) {
//                            Comm.showWarnDialog(context,"第"+(i+1)+"行没有生码，请到PC端“条码管理-条码生成”选择对应单据进行生码，生码后，请刷新数据！");
//                            return;
//                        }

//                    if((batch > 0 && isNULLS(p.getBct().getBatchCode()).length() == 0) || (snNo > 0 && isNULLS(p.getBct().getSnCode()).length() == 0)) {
//                        Comm.showWarnDialog(context,"第"+(i+1)+"行没有生码，请到PC端“条码管理-条码生成”选择对应单据进行生码，生码后，请刷新数据！");
//                        return;
//                    }

                        list.add(p);
                    }
                }
                if(list.size() == 0) {
                    toasts("请勾选数据行！");
                    return;
                }
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
                PurReceiveOrder p = listDatas.get(i);
                p.setIsCheck(1);
            }
        } else {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                PurReceiveOrder p = listDatas.get(i);
                p.setIsCheck(0);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("findPurReceiveOrderList");
        FormBody formBody = new FormBody.Builder()
//                .add("fbillno", getValues(etFbillno).trim())
                .add("supplierId", String.valueOf(supplier.getFsupplierid()))
                .add("isDefaultStock", "1") // 查询默认仓库和库位
                .add("limit", String.valueOf(limit))
                .add("pageSize", "30")
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
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Log.e("Pur_SelReceiveOrderActivity --> onResponse", result);
                Message msg = mHandler.obtainMessage(SUCC1, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        isLoadMore = false;
        initLoadDatas();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        isLoadMore = true;
        limit += 1;
        run_okhttpDatas();
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
