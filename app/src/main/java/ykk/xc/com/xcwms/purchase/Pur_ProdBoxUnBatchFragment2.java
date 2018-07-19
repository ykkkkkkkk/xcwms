package ykk.xc.com.xcwms.purchase;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.sal.SalOrder;
import ykk.xc.com.xcwms.sales.adapter.Sal_SelOrderAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.xrecyclerview.XRecyclerView;

public class Pur_ProdBoxUnBatchFragment2 extends BaseFragment {

    public Pur_ProdBoxUnBatchFragment2() {}

    private Pur_ProdBoxUnBatchFragment2 mFragment = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private Customer customer; // 客户
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Sal_SelOrderAdapter mAdapter;
    private List<SalOrder> listDatas;
    private Activity mContext;

    // 消息处理
    private MyHandler mHandler = new MyHandler(mFragment);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_ProdBoxUnBatchFragment2> mFrag;

        public MyHandler(Pur_ProdBoxUnBatchFragment2 activity) {
            mFrag = new WeakReference<Pur_ProdBoxUnBatchFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_ProdBoxUnBatchFragment2 m = mFrag.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        m.listDatas = JsonUtil.strToList2((String) msg.obj, SalOrder.class);

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }

    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_prod_box_unbatch_fragment2, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
//        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
//        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//        mAdapter = new Sal_SelOrderAdapter(mContext, listDatas);
//        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        mContext = getActivity();
//        run_okhttpDatas();
    }


//    @OnClick({R.id.btn_confirm})
//    public void onViewClicked(View view) {
//        Bundle bundle = null;
//        switch (view.getId()) {
//            case R.id.btn_confirm: // 确认
//                if(listDatas == null || listDatas.size() == 0) {
//                    toasts("请选择数据在确认！");
//                    return;
//                }
//                List<SalOrder> list = new ArrayList<SalOrder>();
//                for(int i = 0, size = listDatas.size(); i<size; i++) {
//                    SalOrder p = listDatas.get(i);
//                    if(p.getIsCheck() == 1) {
//                        list.add(p);
//                    }
//                }
//                if(list.size() == 0) {
//                    toasts("请勾选数据行！");
//                    return;
//                }
//                bundle = new Bundle();
//                bundle.putChar("sourceType", '1'); // 1.销售订单，2.发货通知单
//                bundle.putSerializable("checkDatas", (Serializable)list);
//                setResults(bundle);
//                mContext.finish();
//
//                break;
//        }
//    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("findSalOrderList");
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
                if(!JsonUtil.isSuccess(result)) {
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
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

}
