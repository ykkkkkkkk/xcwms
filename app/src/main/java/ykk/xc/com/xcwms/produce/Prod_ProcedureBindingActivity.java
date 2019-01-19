package ykk.xc.com.xcwms.produce;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Procedure;
import ykk.xc.com.xcwms.model.SchedulTeam;
import ykk.xc.com.xcwms.model.SchedulTeamEntry;
import ykk.xc.com.xcwms.model.Staff;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.ValuationPayroll;
import ykk.xc.com.xcwms.model.ValuationType;
import ykk.xc.com.xcwms.model.pur.ProdOrder;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.LogUtil;

public class Prod_ProcedureBindingActivity extends BaseActivity {

    @BindView(R.id.tv_localMac)
    TextView tvLocalMac;
    @BindView(R.id.tv_process)
    TextView tvProcess;
    @BindView(R.id.tv_okProcess)
    TextView tvOkProcess;

    private Prod_ProcedureBindingActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int SEL_ORDER = 10, CODE1 = 11;
    private User user;
    private int procedureId; // 工序id
    private OkHttpClient okHttpClient = new OkHttpClient();
    private DecimalFormat df = new DecimalFormat("#.######");
    private boolean isBack; // 是否返回数据

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_ProcedureBindingActivity> mActivity;

        public MyHandler(Prod_ProcedureBindingActivity activity) {
            mActivity = new WeakReference<Prod_ProcedureBindingActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_ProcedureBindingActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 扫码成功后进入
                        boolean isBool = m.popDatasB != null;
                        m.popDatasB = JsonUtil.strToList((String)msg.obj, Procedure.class);
                        Procedure procedure = m.popDatasB.get(0);
                        if(isBool) m.popupWindow_B();
                        else {
//                                    Procedure pd = m.popDatasB.get(0);
//                                    m.procedureId = pd.getId();
//                                    m.tvProcess.setText(pd.getProcedureName());
                        }
                        m.run_findProcedureName();

                        break;
                    case UNSUCC1:
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        Comm.showWarnDialog(m.context, errMsg);
                        m.run_findProcedureName();

                        break;
                    case SUCC2: // 成功
                        m.toasts("工序绑定成功✔");
                        m.tvOkProcess.setText(m.getValues(m.tvProcess));
                        m.tvProcess.setText("");
                        m.procedureId = 0;
                        if(m.isBack) {
                            m.setResults(m.context);
                            m.context.finish();
                        }

                        break;
                    case UNSUCC2: // 数据加载失败！
                        m.toasts("服务器繁忙，请稍后再试！");

                        break;
                    case SUCC3: // 查询绑定工序名称 成功
                        String name = JsonUtil.strToString((String) msg.obj);
                        m.tvOkProcess.setText(name);

                        break;
                    case UNSUCC3: // 查询绑定工序名称 失败！

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_procedure_binding;
    }

    @Override
    public void initView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String mac = Comm.getAddressMac(context);
                tvLocalMac.setText(mac);
                // 保存mac地址
                SharedPreferences spf = spf(getResStr(R.string.saveOther));
                spf.edit().putString("localhostMac", mac).commit();
            }
        });
    }

    @Override
    public void initData() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            isBack = bundle.getBoolean("isBack");
        }
        getUserInfo();
        run_itemList();
    }

    @OnClick({R.id.btn_close, R.id.tv_localMac, R.id.tv_process, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.tv_process: // 工序
                if(popDatasB == null || popDatasB.size() == 0) {
                    run_itemList();
                } else {
                    popupWindow_B();
                    popWindowB.showAsDropDown(tvProcess);
                }

                break;
            case R.id.btn_save: // 保存
                if(procedureId == 0) {
                    Comm.showWarnDialog(context,"请选择工序！");
                    return;
                }
                run_save();

                break;
        }
    }

    @Override
    public void setListener() {
    }

    /**
     * 创建PopupWindowB 【查询工序列表】
     */
    private PopupWindow popWindowB;
    private ListAdapter2 adapterB;
    private List<Procedure> popDatasB;
    private void popupWindow_B() {
        if (null != popWindowB) {// 不为空就隐藏
            popWindowB.dismiss();
            return;
        }
//        btnSave.setVisibility(View.GONE);
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popup_list, null);
        final ListView listView = (ListView) popView.findViewById(R.id.listView);

        if (adapterB != null) {
            adapterB.notifyDataSetChanged();
        } else {
            adapterB = new ListAdapter2(context, popDatasB);
            listView.setAdapter(adapterB);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Procedure pd = popDatasB.get(position);
                    procedureId = pd.getId();
                    tvProcess.setText(pd.getProcedureName());

                    popWindowB.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowB = new PopupWindow(popView, tvProcess.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowB.setBackgroundDrawable(new BitmapDrawable());
        popWindowB.setOutsideTouchable(true);
        popWindowB.setFocusable(true);
//        popWindowB.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                btnSave.setVisibility(View.VISIBLE);
//            }
//        });
    }
    /**
     * 工序 适配器
     */
    private class ListAdapter2 extends BaseAdapter {

        private Activity activity;
        private List<Procedure> datas;

        public ListAdapter2(Activity activity, List<Procedure> datas) {
            this.activity = activity;
            this.datas = datas;
        }

        @Override
        public int getCount() {
            if(datas == null) {
                return 0;
            }
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            if(datas == null) {
                return null;
            }
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder holder = null;
            if(v == null) {
                holder = new ViewHolder();
                v = activity.getLayoutInflater().inflate(R.layout.popup_list_item, null);
                holder.tv_name = (TextView) v.findViewById(R.id.tv_name);

                v.setTag(holder);
            }else holder = (ViewHolder) v.getTag();

            holder.tv_name.setText(datas.get(position).getProcedureName());

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;

        }
    }

    /**
     * 查询计件类型
     */
    private void run_itemList() {
        showLoadDialog("加载中...");
        String mUrl = getURL("procedure/findProcedureByParam_app2");
        FormBody formBody = new FormBody.Builder()
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
                LogUtil.e("run_itemList --> onResponse", result);
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
     * 保存
     */
    private void run_save() {
        showLoadDialog("保存中...");
        String mUrl = getURL("procedureBinding/save_app");
        FormBody formBody = new FormBody.Builder()
                .add("mobileMac", getValues(tvLocalMac))
                .add("procedureId", String.valueOf(procedureId))
                .add("userId", String.valueOf(user.getId()))
                .add("userName", user.getUsername())
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

    /**
     * 查询绑定的工序名称
     */
    private void run_findProcedureName() {
        showLoadDialog("加载中...");
        String mUrl = getURL("procedureBinding/findProcedureName");
        FormBody formBody = new FormBody.Builder()
                .add("mobileMac", getValues(tvLocalMac))
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
                LogUtil.e("run_findProcedureName --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC3, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
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
