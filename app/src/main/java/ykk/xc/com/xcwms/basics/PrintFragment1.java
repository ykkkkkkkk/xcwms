package ykk.xc.com.xcwms.basics;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
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
import ykk.xc.com.xcwms.basics.adapter.PrintFragment1Adapter;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.xrecyclerview.XRecyclerView;

public class PrintFragment1 extends BaseFragment implements XRecyclerView.LoadingListener {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.tv_print_type)
    TextView tvPrintType;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private PrintFragment1 context = this;
//    private TSCActivity printUtils = new TSCActivity();
//    private BluetoothAdapter mBluetoothAdapter;
//    private AlertDialog alertDialog; // 已配对蓝牙列表dialog
//    private boolean isConnected; // 判断是否连接蓝牙设备
    private List<Material> listDatas = new ArrayList<>();
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private PrintFragment1Adapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Activity mContext;
    private PrintFragmentsActivity parent;
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private String barcode; // 保存条码的
    private char printType = '1'; // （1：小标签，2：大标签）
    private boolean isRegister = false; // 是否注册了广播

    // 消息处理
    final PrintFragment1.MyHandler mHandler = new PrintFragment1.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<PrintFragment1> mActivity;

        public MyHandler(PrintFragment1 activity) {
            mActivity = new WeakReference<PrintFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            PrintFragment1 m = mActivity.get();
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
        return inflater.inflate(R.layout.ab_print_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (PrintFragmentsActivity) mContext;
        // 获取所有已经绑定的蓝牙设备
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(!isRegister) {
//            // 注册蓝牙广播
//            mContext.registerReceiver(mReceiver, makeFilters());
//            isRegister = true;
//        }

        xRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PrintFragment1Adapter(mContext, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        mAdapter.setCallBack(new PrintFragment1Adapter.MyCallBack() {
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
        initLoadDatas();
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
        String mUrl = Consts.getURL("findMaterialListByParam");
        String searchName = getValues(etSearch).trim();
        FormBody formBody = new FormBody.Builder()
                .add("fNumberAndName", getValues(etSearch).trim())
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
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("PrintFragment1 --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

//    /**
//     * 连接蓝牙前的判断
//     */
//    private void connectBluetoothBefore() {
//        //获取蓝牙适配器实例。如果设备不支持蓝牙则返回null
//        if (mBluetoothAdapter == null) {
//            toasts("设备不支持蓝牙！");
//            return;
//        }
//        // 判断蓝牙是否开启
//        if (!mBluetoothAdapter.isEnabled()) {
//            // 蓝牙未开启，打开蓝牙
//            openBluetooth();
//            return;
//        }
//        // 判断状态为连接
//        if (isConnected) {
//            printContent();
//        } else {
//            pair();
//        }
//    }
//
//    /**
//     * 打开蓝牙
//     */
//    private void openBluetooth() {
//        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
//            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivity(enabler);
//        }
//    }
//
//    /**
//     * 得到已配对的列表进行配对
//     */
//    private void pair() {
//        alertDialog = null;
//        View v = context.getLayoutInflater().inflate(R.layout.bluetooth_oklist, null);
//        alertDialog = new AlertDialog.Builder(mContext).setView(v).create();
//        // 初始化id
//        Button btn_close = (Button) v.findViewById(R.id.btn_close);
//        LinearLayout lin_oklist = v.findViewById(R.id.lin_oklist);
//        if (lin_oklist.getChildCount() > 0) { // 每次都清空子View
//            lin_oklist.removeAllViews();
//        }
//
//        // 单击事件
//        View.OnClickListener click = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = null;
//                switch (v.getId()) {
//                    case R.id.btn_close: // 关闭
//                        if (alertDialog != null && alertDialog.isShowing()) {
//                            alertDialog.dismiss();
//                        }
//                        break;
//                }
//            }
//        };
//        btn_close.setOnClickListener(click);
//        // 得到已配对的蓝牙设备
//        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
//        if (devices.size() > 0) {
//            for (BluetoothDevice blueDevice : devices) {
//                String str = blueDevice.getName() + " : (" + blueDevice.getAddress() + ")";
//                addView(lin_oklist, str); // 添加到布局
//            }
//
//            Window window = alertDialog.getWindow();
//            alertDialog.setCancelable(false);
//            alertDialog.show();
//            window.setGravity(Gravity.CENTER);
//        } else {
//            toasts("请先配对蓝牙打印机！");
//        }
//    }
//
//    /**
//     * 添加到LinearLayout
//     */
//    private void addView(LinearLayout lin, String twoMenuName) {
//        View v = LayoutInflater.from(mContext).inflate(R.layout.bluetooth_oklist_item, null);
//        final TextView tv_item = (TextView) v.findViewById(R.id.tv_item);
//        tv_item.setText(twoMenuName);
//        // 设置单击事件
//        tv_item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 点击就配对，然后关闭这个dialog
//                String item = getValues(tv_item);
//                // 截取的格式为：名称:(20:20:20:20:20),只截取括号里的
//                String address = item.substring(item.indexOf("(") + 1, item.indexOf(")"));
//                printUtils.openport(address);
//
//                if (alertDialog != null && alertDialog.isShowing()) {
//                    alertDialog.dismiss();
//                }
//            }
//        });
//        ViewGroup parent = (ViewGroup) tv_item.getParent();
//        if (parent != null) {
//            parent.removeAllViews();
//        }
//        // 添加到容器中
//        lin.addView(tv_item);
//    }
//
//    /**
//     * 打印条码的方法
//     * flag 0:默认； 1：显示标题，日期，条码
//     */
//    private void printContent() {
//        printUtils.clearbuffer();
//        printUtils.setup(40, 30, 4, 5, 0, 2, 0);
//        String s = null;
//        switch (printType) {
//            case '1': // 打印小标签
//                String date = Comm.getSysDate(7);
//                s = "TEXT 90,20,\"TSS24.BF2\",0,1,2,\"abc \n" +
//                        "TEXT 20,80,\"TSS24.BF2\",0,1,1,\"操作日期:" + date + " \n";
//
//                break;
//            case '2': // 打印大标签
//                s = "TEXT 90,5,\"TSS24.BF2\",0,1,2,\"人工牙种植体 \n" +
//                        "TEXT 20,50,\"TSS24.BF2\",0,1,1,\"生产厂商:ykk \n" +
//                        "TEXT 20,75,\"TSS24.BF2\",0,1,1,\"型——号:38*10mm \n" +
//                        "TEXT 20,100,\"TSS24.BF2\",0,1,1,\"生产日期:2018-05-03 \n" +
//                        "TEXT 20,125,\"TSS24.BF2\",0,1,1,\"失效日期:2020-05-03 \n" +
//                        "TEXT 20,150,\"TSS24.BF2\",0,1,1,\"批——号:201805003 \n";
//                break;
//        }
//
//        byte b[] = new byte[0];
//        try {
//            b = s.getBytes("GBK");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        printUtils.sendcommand(b); // 打印字体
//        // 条码和字体的行间距：28
//        switch (printType) {
//            case '1':
//                printUtils.barcode(25, 115, "39", 40, 1, 0, 1, 3, barcode);
//                break;
//            case '2':
//                printUtils.barcode(25, 178, "39", 40, 1, 0, 1, 3, barcode);
//                break;
//        }
////        printUtils.barcode(25, 178, "39", 40, 1, 0, 1, 3, "123456789");
//        printUtils.printlabel(1, 1);
//    }

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

//    /**
//     * 广播监听蓝牙状态
//     */
//    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context2, Intent intent) {
//            String action = intent.getAction();
//            switch (action) {
//                case BluetoothDevice.ACTION_ACL_CONNECTED: // 已连接
//                    toasts("已连接蓝牙设备√");
//                    isConnected = true;
//                    connectBluetoothBefore();
//
//                    break;
//                case BluetoothDevice.ACTION_ACL_DISCONNECTED:// 断开连接
//                    //蓝牙连接被切断
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    String name = device.getName();
//                    toasts(name + "的连接已断开！");
//                    isConnected = false;
//
//                    break;
//            }
//        }
//    };

    /**
     * 蓝牙监听需要添加的Action
     */
    private IntentFilter makeFilters() {
        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.openBluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
//        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        intentFilter.addAction("android.openBluetooth.BluetoothAdapter.STATE_OFF");
//        intentFilter.addAction("android.openBluetooth.BluetoothAdapter.STATE_ON");
        return intentFilter;
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
