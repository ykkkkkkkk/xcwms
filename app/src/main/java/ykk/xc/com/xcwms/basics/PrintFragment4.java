package ykk.xc.com.xcwms.basics;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
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
import ykk.xc.com.xcwms.basics.adapter.PrintFragment4Adapter;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.LogUtil;
import ykk.xc.com.xcwms.util.xrecyclerview.XRecyclerView;

public class PrintFragment4 extends BaseFragment implements XRecyclerView.LoadingListener {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.tv_print_type)
    TextView tvPrintType;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private PrintFragment4 context = this;
    private List<Material> listDatas = new ArrayList<>();
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private PrintFragment4Adapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Activity mContext;
    private PrintMainActivity parent;
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private char printType = '1'; // （1：小标签，2：大标签）
    private boolean isLoad = true; // 是否加载了数据

    // 消息处理
    final PrintFragment4.MyHandler mHandler = new PrintFragment4.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<PrintFragment4> mActivity;

        public MyHandler(PrintFragment4 activity) {
            mActivity = new WeakReference<PrintFragment4>(activity);
        }

        public void handleMessage(Message msg) {
            PrintFragment4 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        String json = (String) msg.obj;
                        List<Material> list = JsonUtil.strToList2(json, Material.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新禁用
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

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
        return inflater.inflate(R.layout.ab_print_fragment4, container, false);
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if(isLoad && isVisibleToUser) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    initLoadDatas();
//                    isLoad = false;
//                }
//            }, 200);
//        }
//    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (PrintMainActivity) mContext;

        xRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PrintFragment4Adapter(mContext, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setCallBack(new PrintFragment4Adapter.MyCallBack() {
            @Override
            public void onPrint(Material e, int pos) {
            Log.e("onPrint1", e.getfName());
            // 打印
//            connectBluetoothBefore();
            String result = JsonUtil.objectToString(e);
            parent.setFragmentPrint(0, result);
            }
        });
    }

    @Override
    public void initData() {
        mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initLoadDatas();
                }
            }, 200);
    }

    @OnClick({R.id.btn_search, R.id.tv_print_type})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_search: // 查询数据
                initLoadDatas();

                break;
            case R.id.tv_print_type: // 打印类型（小标和大标）
                if(printType == '1') {
                    tvPrintType.setText("大标打印");
                    printType = '2';
                } else {
                    tvPrintType.setText("小标打印");
                    printType = '1';
                }

                break;
        }
    }

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
    }

    /**
     * 通过okhttp加载数据
     * 仓库信息，库区，库位，部门，物料
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("material/findMaterialListByParam3");
        String searchName = getValues(etSearch).trim();
        FormBody formBody = new FormBody.Builder()
                .add("fNumberAndName", searchName)
                .add("limit", String.valueOf(limit))
                .add("pageSize", "30")
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(request);

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("PrintFragment1 --> onResponse", result);
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

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
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mContext.unregisterReceiver(mReceiver);
    }
}
