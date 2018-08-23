package ykk.xc.com.xcwms.basics;

import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;

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
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.util.JsonUtil;

/**
 * ykk
 * 打印条码界面
 */
public class SaoMaPrintActivity extends BaseActivity {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_selectType)
    TextView tvSelectType;

    private SaoMaPrintActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 501, NORMAL = 10;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int caseId = 34; // （34：生产订单）
    private String barcode; // 对应的条码号

    // 消息处理
    final MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<SaoMaPrintActivity> mActivity;

        public MyHandler(SaoMaPrintActivity activity) {
            mActivity = new WeakReference<SaoMaPrintActivity>(activity);
        }

        public void handleMessage(Message msg) {
            SaoMaPrintActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        m.toasts("打印完毕√");

                        break;
                    case UNSUCC1: // 数据加载失败！
                        Comm.showWarnDialog(m.context,"服务器繁忙，请稍候再试！");

                        break;
                    case NORMAL: // 输入框矫正
                        m.setTexts(m.etCode, m.barcode);
                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_sm_print;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        hideSoftInputMode(etCode);
    }

    @OnClick({R.id.btn_close, R.id.tv_selectType})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.tv_selectType: // 选择打印的表
                pop_selectType(v);
                popWindow.showAsDropDown(v);

                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 按了删除键，回退键
        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // 按下事件
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_code: // 物料
                            String code = getValues(etCode).trim();
                            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                if (code.length() == 0) {
                                    toasts("请扫码条码！");
                                    return false;
                                }
                                if (barcode != null && barcode.length() > 0) {
                                    if(barcode.equals(code)) {
                                        barcode = code;
                                    } else {
                                        String tmp = code.replaceFirst(barcode, "");
                                        barcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    barcode = code.replace("\n", "");
                                }
                                mHandler.sendEmptyMessageDelayed(NORMAL, 200);
                                // 执行查询方法
                                run_print();
                            }
                            break;
                    }
                }
                return false;
            }
        };
        etCode.setOnKeyListener(keyListener);
    }

    /**
     * 创建PopupWindow 【 查询来源类型 】
     */
    private PopupWindow popWindow;

    @SuppressWarnings("deprecation")
    private void pop_selectType(View v) {
        if (null != popWindow) {//不为空就隐藏
            popWindow.dismiss();
            return;
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        final View popV = getLayoutInflater().inflate(R.layout.ab_sm_print_type, null);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindow = new PopupWindow(popV, v.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow.setAnimationStyle(R.style.AnimationFade);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setOutsideTouchable(true);
        popWindow.setFocusable(true);

        // 点击其他地方消失
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tmpId = 0;
                switch (v.getId()) {
                    case R.id.btn1:// 物料表
                        tmpId = v.getId();
                        caseId = 34;

                        break;
                }
                popWindow.dismiss();
                tvSelectType.setText("打印类型--"+getValues((Button) popV.findViewById(tmpId)));
            }
        };
        popV.findViewById(R.id.btn1).setOnClickListener(click);
    }

    /**
     * 得到条码号
     */
    private void run_print() {
        showLoadDialog("打印连接中...");
        String mUrl = Consts.getURL("appPrint");
        // 条码号
        FormBody formBody = new FormBody.Builder()
                .add("caseId", String.valueOf(caseId))
                .add("barcode", barcode)
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
                mHandler.sendEmptyMessageDelayed(UNSUCC1, 1000);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("SaoMaPrintActivity --> run_print", result);
                mHandler.sendMessageDelayed(msg, 1000);
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

}
