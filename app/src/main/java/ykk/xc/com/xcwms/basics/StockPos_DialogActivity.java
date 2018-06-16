package ykk.xc.com.xcwms.basics;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import ykk.xc.com.xcwms.basics.adapter.StockPos_DialogAdapter;
import ykk.xc.com.xcwms.comm.BaseDialogActivity;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.comm.OnItemClickListener2;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.util.JsonUtil;

/**
 * 选择库位dialog
 */
public class StockPos_DialogActivity extends BaseDialogActivity {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;
    private StockPos_DialogActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 501;
    private List<StockPosition> listDatas = new ArrayList<>();
    private StockPos_DialogAdapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int areaId; // 库区id

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<StockPos_DialogActivity> mActivity;

        public MyHandler(StockPos_DialogActivity activity) {
            mActivity = new WeakReference<StockPos_DialogActivity>(activity);
        }

        public void handleMessage(Message msg) {
            StockPos_DialogActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                    case SUCC1: // 成功
                        List<StockPosition> list = JsonUtil.strToList((String) msg.obj, StockPosition.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.mAdapter.notifyDataSetChanged();
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_stock_pos_dialog;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new StockPos_DialogAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener2() {
            @Override
            public void onItemClick(View view, int pos) {
                StockPosition supplier = listDatas.get(pos);
                Intent intent = new Intent();
                intent.putExtra("obj", supplier);
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });
    }

    @Override
    public void initData() {
        bundle();
        run_okhttpDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            areaId = bundle.getInt("areaId");
        }
    }

    // 监听事件
    @OnClick({R.id.btn_close, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search:
                listDatas.clear();
                run_okhttpDatas();

                break;
        }
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("findStockPositionListByParam");
        FormBody formBody = new FormBody.Builder()
                .add("fNumberAndName", getValues(etSearch).trim())
                .add("areaId", String.valueOf(areaId))
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
                Log.e("StockPos_DialogActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
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
