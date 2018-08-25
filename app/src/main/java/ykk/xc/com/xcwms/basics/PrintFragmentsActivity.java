package ykk.xc.com.xcwms.basics;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tscdll.TSCActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.pur.ProdOrder;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.interfaces.IFragmentKeyeventListener;
import ykk.xc.com.xcwms.util.MyViewPager;
import ykk.xc.com.xcwms.util.adapter.BaseFragmentAdapter;

public class PrintFragmentsActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    MyViewPager viewPager;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_2)
    TextView tv2;
    private PrintFragmentsActivity context = this;
    private TextView curText;
    private IFragmentKeyeventListener fragment2Listener;
    private TSCActivity printUtils = new TSCActivity();
    private BluetoothAdapter mBluetoothAdapter;
    private AlertDialog alertDialog; // 已配对蓝牙列表dialog
    private boolean isConnected; // 判断是否连接蓝牙设备
    private String barcode; // 打印的条码
    private StringBuilder printContent = new StringBuilder(); // 打印的文字
    private int tabFlag;

    @Override
    public int setLayoutResID() {
        return R.layout.ab_print_fragments;
    }

    @Override
    public void initData() {

        curText = tv1;
        List<Fragment> listFragment = new ArrayList<Fragment>();
//        Bundle bundle2 = new Bundle();
//        bundle2.putSerializable("customer", customer);
//        fragment1.setArguments(bundle2); // 传参数
//        fragment2.setArguments(bundle2); // 传参数
        PrintFragment1 fragment1 = new PrintFragment1();
        PrintFragment2 fragment2 = new PrintFragment2();

        listFragment.add(fragment1);
        listFragment.add(fragment2);
//        viewPager.setScanScroll(false); // 禁止左右滑动
        //ViewPager设置适配器
        viewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), listFragment));
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(0);

        //ViewPager页面切换监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabSelected(tv1);
                        viewPager.setCurrentItem(0, false);
                        break;
                    case 1:
                        tabSelected(tv2);
                        viewPager.setCurrentItem(1, false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 获取所有已经绑定的蓝牙设备
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 注册蓝牙广播
        registerReceiver(mReceiver, makeFilters());
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(TextView tv) {
        curText.setText(getValues(curText).replace("✔",""));
        curText.setTextColor(Color.parseColor("#666666" +""));
        tv.setText(getValues(tv)+"✔");
        tv.setTextColor(Color.parseColor("#FF3300"));
        curText = tv;
    }

    @OnClick({R.id.btn_close, R.id.tv_1, R.id.tv_2})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                context.finish();

                break;
            case R.id.tv_1: // 物料
                tabSelected(tv1);
                viewPager.setCurrentItem(0, false);

                break;
            case R.id.tv_2: // 扫码打印
                tabSelected(tv2);
                viewPager.setCurrentItem(1, false);

                break;
        }
    }

    /**
     * Fragment打印回调
     */
    public void setFragmentPrint(int flag, String result) {
        tabFlag = flag;
        if(tabFlag != flag) {
            isConnected = false;
        }
        String date = Comm.getSysDate(7);
        Material mtl = JsonUtil.stringToObject(result, Material.class);
        printContent.append("");

        // 去打印
        connectBluetoothBefore();
    }
    public void setFragmentPrint2(int flag, String result) {
        tabFlag = flag;
        if(tabFlag != flag) {
            isConnected = false;
        }
        String date = Comm.getSysDate(7);
        BarCodeTable bt = JsonUtil.strToObject(result, BarCodeTable.class);
        switch (bt.getCaseId()) {
            case 34: // 生产任务单
                ProdOrder prodOrder = JsonUtil.stringToObject(bt.getRelationObj(), ProdOrder.class);

                int beginXPos = 22; // 开始横向位置
                int beginYPos = 22; // 开始纵向位置
                int rowHigthSum = 0; // 纵向高度的叠加
                int rowSpacing = 30; // 每行之间的距离

                printContent.append("TEXT "+beginXPos+","+beginYPos+",\"TSS24.BF2\",0,1,2,\"客户名称："+isNULLS(prodOrder.getCustName())+" \n");
                rowHigthSum = beginYPos+50;
                printContent.append("TEXT "+beginXPos+","+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"联系人："+isNULLS(prodOrder.getReceivePerson())+" \n");
                printContent.append("TEXT 260,"+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"日期："+date+" \n");
                rowHigthSum = rowHigthSum + rowSpacing;
                printContent.append("TEXT "+beginXPos+","+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"电话："+isNULLS(prodOrder.getReceiveTel())+" \n");
                printContent.append("TEXT 260,"+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"生产序号："+isNULLS(prodOrder.getProdSeqNumber())+" \n");
                rowHigthSum = rowHigthSum + rowSpacing;
                printContent.append("TEXT "+beginXPos+","+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"收货地址："+isNULLS(prodOrder.getReceiveAddress())+" \n");
                rowHigthSum = rowHigthSum + rowSpacing;
                printContent.append("TEXT "+beginXPos+","+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"订单号："+prodOrder.getFbillno()+" \n");
                rowHigthSum = rowHigthSum + rowSpacing;
                printContent.append("TEXT "+beginXPos+","+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"产品名称："+isNULLS(prodOrder.getMtlFname())+" \n");
                rowHigthSum = rowHigthSum + rowSpacing;
                printContent.append("TEXT "+beginXPos+","+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"产品规格："+isNULLS(prodOrder.getMtlSize())+" \n");
                rowHigthSum = rowHigthSum + rowSpacing;
                printContent.append("TEXT "+beginXPos+","+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"备注："+isNULLS(prodOrder.getRemarks())+" \n");
                rowHigthSum = 310;
                printContent.append("TEXT "+beginXPos+","+rowHigthSum+",\"TSS24.BF2\",0,1,1,\"条码： \n");
                barcode = bt.getBarcode();

                break;
        }
        // 去打印
        connectBluetoothBefore();
    }

    /**
     * 打印条码的方法
     * flag 0:默认； 1：显示标题，日期，条码
     */
    private void print() {
        printUtils.clearbuffer();
        printUtils.setup(74, 48, 4, 5, 0, 2, 0);

//        switch (flag) {
//            case '1': // 打印其他格式
//                String titleName = "物料条码";
//                String date = Comm.getSysDate(7);
//                s = "TEXT 90,20,TSS24.BF2,0,1,2,"" + titleName + " \n" +
//                        "TEXT 20,80,TSS24.BF2,0,1,1,"操作日期:" + date + " \n";
//
//                break;
//            default: // 打印默认格式
//                s = "TEXT 90,5,TSS24.BF2,0,1,2,"人工牙种植体 \n" +
//                        "TEXT 20,50,TSS24.BF2,0,1,1,"生产厂商:ykk \n" +
//                        "TEXT 20,75,TSS24.BF2,0,1,1,"型——号:38*10mm \n" +
//                        "TEXT 20,100,TSS24.BF2,0,1,1,"生产日期:2018-05-03 \n" +
//                        "TEXT 20,125,TSS24.BF2,0,1,1,"失效日期:2020-05-03 \n" +
//                        "TEXT 20,150,TSS24.BF2,0,1,1,"批——号:201805003 \n";
//                break;
//        }
        byte b[] = new byte[0];
        try {
//            b = s.getBytes("GBK");
            b = printContent.toString().getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        printUtils.sendcommand(b); // 打印字体
        // 条码和字体的行间距：28
        switch (tabFlag) { // 不同的页面打印不同的格式
            case 0:
                printUtils.barcode(25, 115, "39", 40, 1, 0, 1, 3, barcode);
                break;
            case 1:
                printUtils.barcode(100, 20, "39", 60, 1, 0, 1, 1, barcode);
                break;
        }
//        printUtils.barcode(25, 178, "39", 40, 1, 0, 1, 3, "123456789");
        printUtils.printlabel(1, 1);
    }

    public void setFragmentKeyeventListener(IFragmentKeyeventListener fragmentKeyeventListener) {
        this.fragment2Listener = fragmentKeyeventListener;
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
            print();
        } else {
            pair();
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 第二个Fragment 才监听删除键
        if (viewPager.getCurrentItem() == 1 && fragment2Listener!=null){
            boolean isBool = fragment2Listener.onFragmentKeyEvent(event);
            if(!isBool) {
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
