package ykk.xc.com.xcwms.entrance;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

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
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.AppInfo;
import ykk.xc.com.xcwms.purchase.Pur_InMainActivity;
import ykk.xc.com.xcwms.purchase.Pur_OrderSearchActivity;
import ykk.xc.com.xcwms.util.IDownloadContract;
import ykk.xc.com.xcwms.util.IDownloadPresenter;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.UpdateManager;

import static android.os.Process.killProcess;

public class MainTabFragment1 extends BaseFragment {


    @BindView(R.id.tab1)
    TextView tab1;
    @BindView(R.id.relative1)
    RelativeLayout relative1;
    @BindView(R.id.tab2)
    TextView tab2;
    @BindView(R.id.relative2)
    RelativeLayout relative2;
    @BindView(R.id.tab3)
    TextView tab3;
    @BindView(R.id.relative3)
    RelativeLayout relative3;
    @BindView(R.id.lin_tab)
    LinearLayout linTab;

    private Activity mContext;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private OkHttpClient okHttpClient = new OkHttpClient();

    // 消息处理
    final MainTabFragment1.MyHandler mHandler = new MainTabFragment1.MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainTabFragment1> mActivity;

        public MyHandler(MainTabFragment1 activity) {
            mActivity = new WeakReference<MainTabFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            MainTabFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                    case SUCC1: // 得到更新信息
                        AppInfo appInfo = JsonUtil.strToObject((String) msg.obj, AppInfo.class);
                        if (m.getAppVersionCode(m.mContext) != appInfo.getAppVersion()) {
                            m.showNoticeDialog(appInfo.getAppRemark());
                        }

                        break;
                    case UNSUCC1: // 得到更新信息失败

                        break;
                }
            }
        }
    }
    
    public MainTabFragment1() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item1, container, false);
    }

    @Override
    public void initData() {
        mContext = getActivity();
        // 查询是否更新数据
        run_findAppInfo();
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 采购订单
                show(Pur_OrderSearchActivity.class, null);

                break;
            case R.id.relative2: // 采购入库
                show(Pur_InMainActivity.class, null);

                break;
            case R.id.relative3: // 生产入库
//                show(Prod_InActivity.class,null);

                break;
            case R.id.relative4: // 生产装箱
//                show(Pur_ProdBoxMainActivity.class, null);

                break;
        }
    }


    /**
     * 提示下载框
     */
    private void showNoticeDialog(String remark) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("更新版本").setMessage(remark)
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new UpdateManager(mContext).checkUpdateInfo();
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
                .create();// 创建
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();// 显示
    }

    /**
     * 得到本机的版本信息
     */
    private int getAppVersionCode(Context context) {
        PackageManager pack;
        PackageInfo info;
        // String versionName = "";
        try {
            pack = context.getPackageManager();
            info = pack.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
            // versionName = info.versionName;
        } catch (Exception e) {
            Log.e("getAppVersionName(Context context)：", e.toString());
        }
        return 0;
    }

    /**
     * 获取服务端的App信息
     */
    private void run_findAppInfo() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("findAppInfo");
        ;
        FormBody formBody = new FormBody.Builder()
//                .add("limit", "10")
//                .add("pageSize", "100")
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("MainTabFragment4 --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

}
