package ykk.xc.com.xcwms.basics;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import ykk.xc.com.xcwms.basics.adapter.ProductBarcode1Adapter;
import ykk.xc.com.xcwms.basics.adapter.ProductBarcode2Adapter;
import ykk.xc.com.xcwms.basics.adapter.ProductBarcode3Adapter;
import ykk.xc.com.xcwms.basics.adapter.ProductBarcode4Adapter;
import ykk.xc.com.xcwms.basics.adapter.ProductBarcode5Adapter;
import ykk.xc.com.xcwms.basics.adapter.ProductBarcode6Adapter;
import ykk.xc.com.xcwms.basics.adapter.ProductBarcode7Adapter;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.model.Mtl;
import ykk.xc.com.xcwms.model.pur.PoList;
import ykk.xc.com.xcwms.model.pur.SeOrder;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockArea;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.TSC2Activity;
import ykk.xc.com.xcwms.util.basehelper.BaseArrayRecyclerAdapter;
import ykk.xc.com.xcwms.util.xrecyclerview.XRecyclerView;

/**
 * ykk
 * 打印条码界面
 */
public class PrintBarcodeActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.tv_selectType)
    TextView tvSelectType;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.lin_1)
    LinearLayout lin1;
    @BindView(R.id.lin_2)
    LinearLayout lin2;
    @BindView(R.id.lin_3)
    LinearLayout lin3;
    @BindView(R.id.lin_4)
    LinearLayout lin4;
    @BindView(R.id.lin_5)
    LinearLayout lin5;
    @BindView(R.id.lin_6)
    LinearLayout lin6;
    @BindView(R.id.lin_7)
    LinearLayout lin7;

    private PrintBarcodeActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 501, FINDCODE = 101, UNFINDCODE = 301;
    private TSC2Activity printUtils = new TSC2Activity();
    private LinearLayout lin_cur; // 当前表头对象
    private BluetoothAdapter mBluetoothAdapter;
    private AlertDialog alertDialog; // 已配对蓝牙列表dialog
    private boolean isConnected; // 判断是否连接蓝牙设备
    private OkHttpClient okHttpClient = new OkHttpClient();
    private FormBody formBody = null;
    private char selectType = '1'; // 记录打印的是那个表
    private List<Mtl> list1 = new ArrayList<>();
    private List<Stock> list2;
    private List<StockArea> list3;
    private List<StockPosition> list4;
    private List<Department> list5;
    private List<PoList> list6;
    private List<SeOrder> list7;
    private ProductBarcode1Adapter mAdapter1; // 物料
    private ProductBarcode2Adapter mAdapter2; // 仓库
    private ProductBarcode3Adapter mAdapter3; // 库区
    private ProductBarcode4Adapter mAdapter4; // 库位
    private ProductBarcode5Adapter mAdapter5; // 部门
    private ProductBarcode6Adapter mAdapter6; // 采购
    private ProductBarcode7Adapter mAdapter7; // 销售
    private BaseArrayRecyclerAdapter curAdapter; // 记录当前的adapter
    private String barcode; // 保存条码的
    private int curPos; // 当前行

    // 消息处理
    final MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<PrintBarcodeActivity> mActivity;

        public MyHandler(PrintBarcodeActivity activity) {
            mActivity = new WeakReference<PrintBarcodeActivity>(activity);
        }

        public void handleMessage(Message msg) {
            PrintBarcodeActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        String json = (String) msg.obj;
                        m.getJson_findDatas(json);
                        m.updateUI();

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                    case FINDCODE: // 得到barcode
                        String barcode = JsonUtil.strToString((String) msg.obj);
                        m.getBarcode_findBarcode(barcode);

                        break;
                    case UNFINDCODE: // 没有得到barcode
                        m.toasts("抱歉，服务器繁忙，请稍后再试！");

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_print_code;
    }

    @Override
    public void initView() {
        // 获取所有已经绑定的蓝牙设备
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerReceiver(mReceiver, makeFilters());

        lin_cur = lin1;
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter1 = new ProductBarcode1Adapter(context, list1);
        xRecyclerView.setAdapter(mAdapter1);
        curAdapter = mAdapter1;
        xRecyclerView.setLoadingListener(context);
        xRecyclerView.setPullRefreshEnabled(false); // 上拉加载禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 下拉刷新禁用
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.btn_close, R.id.tv_selectType, R.id.btn_search})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_close: // 关闭
                unregisterReceiver(mReceiver);
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.tv_selectType: // 选择打印的表
                pop_selectType(v);
                popWindow.showAsDropDown(v);

                break;
            case R.id.btn_search: // 查询数据
                run_findDatas();

                break;
        }
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
        final View popV = getLayoutInflater().inflate(R.layout.ab_print_code_tablename, null);
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
                char tmpSelectType = '1';
                switch (v.getId()) {
                    case R.id.btn1:// 物料表
                        tmpId = v.getId();
                        tmpSelectType = '1';
                        setSelectType(lin1);

                        break;
                    case R.id.btn2:// 仓库表
                        tmpId = v.getId();
                        tmpSelectType = '2';
                        setSelectType(lin2);

                        break;

                    case R.id.btn3:// 库区表
                        tmpId = v.getId();
                        tmpSelectType = '3';
                        setSelectType(lin3);

                        break;
                    case R.id.btn4:// 库区表
                        tmpId = v.getId();
                        tmpSelectType = '4';
                        setSelectType(lin4);

                        break;
                    case R.id.btn5:// 部门表
                        tmpId = v.getId();
                        tmpSelectType = '5';
                        setSelectType(lin5);

                        break;
                    case R.id.btn6:// 采购表
                        tmpId = v.getId();
                        tmpSelectType = '6';
                        setSelectType(lin6);

                        break;
                    case R.id.btn7:// 销售表
                        tmpId = v.getId();
                        tmpSelectType = '7';
                        setSelectType(lin7);

                        break;
                }
                popWindow.dismiss();
                if(selectType == tmpSelectType) { // 两次点击的是一样的
                    return;
                }
                if(curAdapter != null) { // 清空数据
                    curAdapter.clearData();
                }
                selectType = tmpSelectType;
                tvSelectType.setText(getValues((Button) popV.findViewById(tmpId)) + "-生成条码");
            }
        };
        popV.findViewById(R.id.btn1).setOnClickListener(click);
        popV.findViewById(R.id.btn2).setOnClickListener(click);
        popV.findViewById(R.id.btn3).setOnClickListener(click);
        popV.findViewById(R.id.btn4).setOnClickListener(click);
        popV.findViewById(R.id.btn5).setOnClickListener(click);
        popV.findViewById(R.id.btn6).setOnClickListener(click);
        popV.findViewById(R.id.btn7).setOnClickListener(click);
    }

    /**
     * 选择了类型，数据跟着变
     */
    private void setSelectType(LinearLayout lin) {
        if (lin_cur != null) {
            lin_cur.setVisibility(View.GONE);
        }
        lin_cur = lin;
        lin.setVisibility(View.VISIBLE);
    }

    /**
     * 清空list
     */
    private void clearList() {
        switch (selectType) {
            case '1':
                if (list1 != null && list1.size() > 0) {
                    list1.clear();
                }
                break;
            case '2':
                if (list2 != null && list2.size() > 0) {
                    list2.clear();
                }
                break;
            case '3':
                if (list3 != null && list3.size() > 0) {
                    list3.clear();
                }
                break;
            case '4':
                if (list4 != null && list4.size() > 0) {
                    list4.clear();
                }
                break;
            case '5':
                if (list5 != null && list5.size() > 0) {
                    list5.clear();
                }
                break;
            case '6':
                if (list6 != null && list6.size() > 0) {
                    list6.clear();
                }
                break;
            case '7':
                if (list7 != null && list7.size() > 0) {
                    list7.clear();
                }
                break;
        }
    }

    /**
     * 通过okhttp加载数据
     * 仓库信息，库区，库位，部门，物料
     */
    private void run_findDatas() {
        showLoadDialog("加载中...");
        String mUrl = null;
        switch (selectType) {
            case '1':
                mUrl = Consts.getURL("findMtlListByParam");
                break;
            case '2':
                mUrl = Consts.getURL("findStockListByParam");
                break;
            case '3':
                mUrl = Consts.getURL("findStockAreaListByParam");
                break;
            case '4':
                mUrl = Consts.getURL("findStockPositionListByParam");
                break;
            case '5':
                mUrl = Consts.getURL("findDepartmentListByParam");
                break;
            case '6':
                mUrl = Consts.getURL("findPoListList2ByParam");
                break;
            case '7':
                mUrl = Consts.getURL("findSeOrderList2ByParam");
                break;
            default: // 默认物料url
                mUrl = Consts.getURL("findMtlListByParam");
                break;
        }
        clearList();
        String searchName = getValues(etSearch).trim();
        FormBody formBody = new FormBody.Builder()
                .add("fname", searchName) // (1--5)用
                .add("poNumber", searchName) // 6 用
                .add("seroderNmber", searchName) // 7 用
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
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("PrintBarcodeActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 得到数据
     */
    private void getJson_findDatas(String json) {
        switch (selectType) {
            case '1':
                list1 = JsonUtil.strToList2(json, Mtl.class);
                break;
            case '2':
                list2 = JsonUtil.strToList2(json, Stock.class);
                break;
            case '3':
                list3 = JsonUtil.strToList2(json, StockArea.class);
                break;
            case '4':
                list4 = JsonUtil.strToList2(json, StockPosition.class);
                break;
            case '5':
                list5 = JsonUtil.strToList2(json, Department.class);
                break;
            case '6':
                list6 = JsonUtil.strToList2(json, PoList.class);
                break;
            case '7':
                list7 = JsonUtil.strToList2(json, SeOrder.class);
                break;
        }
    }

    /**
     * 更新UI
     */
    private void updateUI() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        xRecyclerView.setLayoutManager(layoutManager);
        if (mAdapter1 == null || mAdapter2 == null || mAdapter3 == null || mAdapter4 == null || mAdapter5 == null || mAdapter6 == null || mAdapter7 == null) {
//
////            xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
////            xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
//
            switch (selectType) {
                case '1':
                    mAdapter1 = new ProductBarcode1Adapter(context, list1);
                    xRecyclerView.setAdapter(mAdapter1);
                    curAdapter = mAdapter1;
                    break;
                case '2':
                    mAdapter2 = new ProductBarcode2Adapter(context, list2);
                    xRecyclerView.setAdapter(mAdapter2);
                    curAdapter = mAdapter2;
                    break;
                case '3':
                    mAdapter3 = new ProductBarcode3Adapter(context, list3);
                    xRecyclerView.setAdapter(mAdapter3);
                    curAdapter = mAdapter3;
                    break;
                case '4':
                    mAdapter4 = new ProductBarcode4Adapter(context, list4);
                    xRecyclerView.setAdapter(mAdapter4);
                    curAdapter = mAdapter4;
                    break;
                case '5':
                    mAdapter5 = new ProductBarcode5Adapter(context, list5);
                    xRecyclerView.setAdapter(mAdapter5);
                    curAdapter = mAdapter5;
                    break;
                case '6':
                    mAdapter6 = new ProductBarcode6Adapter(context, list6);
                    xRecyclerView.setAdapter(mAdapter6);
                    curAdapter = mAdapter6;
                    break;
                case '7':
                    mAdapter7 = new ProductBarcode7Adapter(context, list7);
                    xRecyclerView.setAdapter(mAdapter7);
                    curAdapter = mAdapter7;
                    break;
            }
//            xRecyclerView.setLoadingListener(context);
            setAdapterListener();
//
//            xRecyclerView.setPullRefreshEnabled(false); // 上拉加载禁用
//            xRecyclerView.setLoadingMoreEnabled(false); // 下拉刷新禁用
//
//        } else {
            switch (selectType) {
                case '1':
                    mAdapter1.notifyDataSetChanged();
                    curAdapter = mAdapter1;
                    break;
                case '2':
                    mAdapter2.notifyDataSetChanged();
                    curAdapter = mAdapter2;
                    break;
                case '3':
                    mAdapter3.notifyDataSetChanged();
                    curAdapter = mAdapter3;
                    break;
                case '4':
                    mAdapter4.notifyDataSetChanged();
                    curAdapter = mAdapter4;
                    break;
                case '5':
                    mAdapter5.notifyDataSetChanged();
                    curAdapter = mAdapter5;
                    break;
                case '6':
                    mAdapter6.notifyDataSetChanged();
                    curAdapter = mAdapter6;
                    break;
                case '7':
                    mAdapter7.notifyDataSetChanged();
                    curAdapter = mAdapter7;
                    break;
            }

        }
    }

    /**
     * 适配器中定义的事件
     */
    private void setAdapterListener() {
        switch (selectType) {
            case '1':
                mAdapter1.setCallBack(new ProductBarcode1Adapter.MyCallBack() {
                    @Override
                    public void onPrint(Mtl e, int pos) {
                        Log.e("setListener-111", e.getFname());
                        setAdapterListenerSon(e.getBarcode(), "mtl", pos, e.getId());
                    }
                });
                break;
            case '2':
                mAdapter2.setCallBack(new ProductBarcode2Adapter.MyCallBack() {
                    @Override
                    public void onPrint(Stock e, int pos) {
                        Log.e("setListener-222", e.getFname());
                        setAdapterListenerSon(e.getBarcode(), "stock", pos, e.getId());
                    }
                });
                break;
            case '3':
                mAdapter3.setCallBack(new ProductBarcode3Adapter.MyCallBack() {
                    @Override
                    public void onPrint(StockArea e, int pos) {
                        Log.e("setListener-333", e.getFname());
                        setAdapterListenerSon(e.getBarcode(), "stockArea", pos, e.getId());
                    }
                });
                break;
            case '4':
                mAdapter4.setCallBack(new ProductBarcode4Adapter.MyCallBack() {
                    @Override
                    public void onPrint(StockPosition e, int pos) {
                        Log.e("setListener-444", e.getFname());
                        setAdapterListenerSon(e.getBarcode(), "stockPosition", pos, e.getId());
                    }
                });
                break;
            case '5':
                mAdapter5.setCallBack(new ProductBarcode5Adapter.MyCallBack() {
                    @Override
                    public void onPrint(Department e, int pos) {
                        Log.e("setListener-555", e.getFname());
                        setAdapterListenerSon(e.getBarcode(), "department", pos, e.getId());
                    }
                });
                break;
            case '6':
                mAdapter6.setCallBack(new ProductBarcode6Adapter.MyCallBack() {
                    @Override
                    public void onPrint(PoList e, int pos) {
                        Log.e("setListener-666", e.getPoNumber());
                        setAdapterListenerSon(e.getBarcode(), "poList", pos, e.getPoId());
                    }
                });
                break;
            case '7':
                mAdapter7.setCallBack(new ProductBarcode7Adapter.MyCallBack() {
                    @Override
                    public void onPrint(SeOrder e, int pos) {
                        Log.e("setListener-777", e.getSeroderNmber());
                        setAdapterListenerSon(e.getBarcode(), "seOrder", pos, e.getSeorderId());
                    }
                });
                break;
        }
    }
    private void setAdapterListenerSon(String barcode, String tableName, int position, int id) {
        context.barcode = isNULL2(barcode, "123456789");
        if(barcode.length() == 13) {
            connectBluetoothBefore();
        } else {
            curPos = position;
            run_findBarcode(tableName, String.valueOf(id));
        }
    }


    /**
     * 得到条码号
     */
    private void run_findBarcode(String tableName, String id) {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("findBarcode");
        // 条码号
        String barcode = Comm.randBarcode(id);
        FormBody formBody = new FormBody.Builder()
                .add("tableName", tableName)
                .add("id", id)
                .add("barcode", barcode)
                .add("retValue", "") // 这个是返回值
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
                mHandler.sendEmptyMessage(UNFINDCODE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNFINDCODE);
                    return;
                }
                Message msg = mHandler.obtainMessage(FINDCODE, result);
                Log.e("PrintBarcodeActivity --> run_findBarcode", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 得到返回的条码号
     */
    private void getBarcode_findBarcode(String barcode) {
        context.barcode = barcode;
        int position = curPos;
        switch (selectType) {
            case '1':
                list1.get(position).setBarcode(barcode);
                break;
            case '2':
                list2.get(position).setBarcode(barcode);
                break;
            case '3':
                list3.get(position).setBarcode(barcode);
                break;
            case '4':
                list4.get(position).setBarcode(barcode);
                break;
            case '5':
                list5.get(position).setBarcode(barcode);
                break;
            case '6':
                list6.get(position).setBarcode(barcode);
                break;
            case '7':
                list7.get(position).setBarcode(barcode);
                break;
        }
        curAdapter.notifyDataSetChanged();
        connectBluetoothBefore();
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

    /**
     * 连接蓝牙前的判断
     */
    private void connectBluetoothBefore() {
        //获取蓝牙适配器实例。如果设备不支持蓝牙则返回null
        if (mBluetoothAdapter == null) {
            toasts("设备不支持蓝牙！");
            return;
        }
        // 判断蓝牙是否开启
        if (!mBluetoothAdapter.isEnabled()) {
            // 蓝牙未开启，打开蓝牙
            openBluetooth();
            return;
        }
        // 判断状态为连接
        if (isConnected) {
            printContent('1');
        } else {
            pair();
        }
//        if(isConnect()) {
//            printContent('1');
//        } else {
//            pair();
//        }
    }

    private boolean isConnect() {
        try {
            return printUtils.btSocket.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 打开蓝牙
     */
    private void openBluetooth() {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enabler);
        }
    }

    /**
     * 得到已配对的列表进行配对
     */
    private void pair() {
        alertDialog = null;
        View v = context.getLayoutInflater().inflate(R.layout.bluetooth_oklist, null);
        alertDialog = new AlertDialog.Builder(context).setView(v).create();
        // 初始化id
        Button btn_close = (Button) v.findViewById(R.id.btn_close);
        LinearLayout lin_oklist = v.findViewById(R.id.lin_oklist);
        if (lin_oklist.getChildCount() > 0) { // 每次都清空子View
            lin_oklist.removeAllViews();
        }

        // 单击事件
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = null;
                switch (v.getId()) {
                    case R.id.btn_close: // 关闭
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        break;
                }
            }
        };
        btn_close.setOnClickListener(click);
        // 得到已配对的蓝牙设备
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice blueDevice : devices) {
                String str = blueDevice.getName() + " : (" + blueDevice.getAddress() + ")";
                addView(lin_oklist, str); // 添加到布局
            }

            Window window = alertDialog.getWindow();
            alertDialog.setCancelable(false);
            alertDialog.show();
            window.setGravity(Gravity.CENTER);
        } else {
            toasts("请先配对蓝牙打印机！");
        }
    }

    /**
     * 添加到LinearLayout
     */
    private void addView(LinearLayout lin, String twoMenuName) {
        View v = LayoutInflater.from(context).inflate(R.layout.bluetooth_oklist_item, null);
        final TextView tv_item = (TextView) v.findViewById(R.id.tv_item);
        tv_item.setText(twoMenuName);
        // 设置单击事件
        tv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击就配对，然后关闭这个dialog
                String item = getValues(tv_item);
                // 截取的格式为：名称:(20:20:20:20:20),只截取括号里的
                String address = item.substring(item.indexOf("(") + 1, item.indexOf(")"));
                printUtils.openport(address);

                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
        ViewGroup parent = (ViewGroup) tv_item.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        // 添加到容器中
        lin.addView(tv_item);
    }

    /**
     * 打印条码的方法
     * flag 0:默认； 1：显示标题，日期，条码
     */
    private void printContent(char flag) {
        printUtils.clearbuffer();
        printUtils.setup(40, 30, 4, 5, 0, 2, 0);
        String s = null;
        switch (flag) {
            case '1': // 打印其他格式
                String titleName = "物料条码";
                switch (selectType) {
                    case '1':
                        titleName = "物料条码";
                        break;
                    case '2':
                        titleName = "仓库条码";
                        break;
                    case '3':
                        titleName = "库区条码";
                        break;
                    case '4':
                        titleName = "库位条码";
                        break;
                    case '5':
                        titleName = "部门条码";
                        break;
                    case '6':
                        titleName = "采购条码";
                        break;
                    case '7':
                        titleName = "销售条码";
                        break;
                }
                String date = Comm.getSysDate(7);
                s = "TEXT 90,20,\"TSS24.BF2\",0,1,2,\"" + titleName + " \n" +
                        "TEXT 20,80,\"TSS24.BF2\",0,1,1,\"操作日期:" + date + " \n";

                break;
            default: // 打印默认格式
                s = "TEXT 90,5,\"TSS24.BF2\",0,1,2,\"人工牙种植体 \n" +
                        "TEXT 20,50,\"TSS24.BF2\",0,1,1,\"生产厂商:ykk \n" +
                        "TEXT 20,75,\"TSS24.BF2\",0,1,1,\"型——号:38*10mm \n" +
                        "TEXT 20,100,\"TSS24.BF2\",0,1,1,\"生产日期:2018-05-03 \n" +
                        "TEXT 20,125,\"TSS24.BF2\",0,1,1,\"失效日期:2020-05-03 \n" +
                        "TEXT 20,150,\"TSS24.BF2\",0,1,1,\"批——号:201805003 \n";
                break;
        }

        byte b[] = new byte[0];
        try {
            b = s.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        printUtils.sendcommand(b); // 打印字体
        // 条码和字体的行间距：28
        switch (flag) {
            case '1':
                printUtils.barcode(25, 115, "39", 40, 1, 0, 1, 3, barcode);
                break;
            default:
                printUtils.barcode(25, 178, "39", 40, 1, 0, 1, 3, barcode);
                break;
        }
//        printUtils.barcode(25, 178, "39", 40, 1, 0, 1, 3, "123456789");
        printUtils.printlabel(1, 1);
    }

    /**
     * 广播监听蓝牙状态
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context2, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED: // 已连接
                    toasts("已连接蓝牙设备√");
                    isConnected = true;
                    connectBluetoothBefore();

                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:// 断开连接
                    //蓝牙连接被切断
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = device.getName();
                    toasts(name + "的连接已断开！");
                    isConnected = false;

                    break;
            }
        }
    };

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            unregisterReceiver(mReceiver);
            closeHandler(mHandler);
            printUtils.closeport();
            context.finish();
        }
        return false;
    }

}
