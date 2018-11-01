package ykk.xc.com.xcwms.purchase;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.basics.PrintMainActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.BoxBarCode;
import ykk.xc.com.xcwms.model.MaterialBinningRecord;
import ykk.xc.com.xcwms.model.pur.ProdOrder;
import ykk.xc.com.xcwms.model.sal.PickingList;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.MyViewPager;
import ykk.xc.com.xcwms.util.adapter.BaseFragmentAdapter;
import ykk.xc.com.xcwms.util.blueTooth.BluetoothDeviceListDialog;
import ykk.xc.com.xcwms.util.blueTooth.Constant;
import ykk.xc.com.xcwms.util.blueTooth.DeviceConnFactoryManager;
import ykk.xc.com.xcwms.util.blueTooth.ThreadPool;
import ykk.xc.com.xcwms.util.blueTooth.Utils;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ykk.xc.com.xcwms.util.blueTooth.Constant.MESSAGE_UPDATE_PARAMETER;
import static ykk.xc.com.xcwms.util.blueTooth.DeviceConnFactoryManager.CONN_STATE_FAILED;


public class Pur_ProdBoxMainActivity extends BaseActivity {

    @BindView(R.id.viewRadio1)
    View radio1;
    @BindView(R.id.viewRadio2)
    View radio2;
    @BindView(R.id.lin_tab1)
    LinearLayout linTab1;
    @BindView(R.id.lin_tab2)
    LinearLayout linTab2;
    @BindView(R.id.viewPager)
    MyViewPager viewPager;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_connState)
    TextView tvConnState;

    private Pur_ProdBoxMainActivity context = this;
    private View curRadio;
    private int id = 0; // 设备id
    private ThreadPool threadPool;
    private boolean isConnected; // 蓝牙是否连接标识
    private int tabFlag;
    private static final int CONN_STATE_DISCONN = 0x007; // 连接状态断开
    private static final int PRINTER_COMMAND_ERROR = 0x008; // 使用打印机指令错误
    private static final int CONN_PRINTER = 0x12;
    private List<MaterialBinningRecord> mbrList;
    private BoxBarCode boxBarCode;
    private DecimalFormat df = new DecimalFormat("#.######");
//    private Customer customer; // 客户

    @Override
    public int setLayoutResID() {
        return R.layout.pur_prod_box_main;
    }

    @Override
    public void initData() {
//        Bundle bundle = context.getIntent().getExtras();
//        if (bundle != null) {
//            customer = (Customer) bundle.getSerializable("customer");
//        }

        curRadio = radio1;
        List<Fragment> listFragment = new ArrayList<Fragment>();
//        Bundle bundle2 = new Bundle();
//        bundle2.putSerializable("customer", customer);
//        fragment1.setArguments(bundle2); // 传参数
        Pur_ProdBoxFragment1 fragment1 = new Pur_ProdBoxFragment1();
        Pur_ProdBoxFragment2 fragment2 = new Pur_ProdBoxFragment2();

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
                        tabSelected(radio1);
                        tvTitle.setText("生产装箱-非批量");
                        viewPager.setCurrentItem(0, false);

                        break;
                    case 1:
                        tabSelected(radio2);
                        tvTitle.setText("生产装箱-批量");
                        viewPager.setCurrentItem(1, false);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
//            customer = bundle.getParcelable("customer");
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    @OnClick({R.id.btn_close, R.id.btn_print, R.id.lin_tab1, R.id.lin_tab2})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                context.finish();

                break;
            case R.id.btn_print: // 打印
                show(PrintMainActivity.class,null);

                break;
            case R.id.lin_tab1:
                tabSelected(radio1);
                tvTitle.setText("生产装箱-非批量");
                viewPager.setCurrentItem(0, false);

                break;
            case R.id.lin_tab2:
                tabSelected(radio2);
                tvTitle.setText("生产装箱-批量");
                viewPager.setCurrentItem(1, false);

                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_DEVICE_DETACHED);
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 当选择蓝牙的时候按了返回键
            if(data == null) return;
            switch (requestCode) {
                /*蓝牙连接*/
                case Constant.BLUETOOTH_REQUEST_CODE: {
                    /*获取蓝牙mac地址*/
                    String macAddress = data.getStringExtra(BluetoothDeviceListDialog.EXTRA_DEVICE_ADDRESS);
                    //初始化话DeviceConnFactoryManager
                    new DeviceConnFactoryManager.Build()
                            .setId(id)
                            //设置连接方式
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                            //设置连接的蓝牙mac地址
                            .setMacAddress(macAddress)
                            .build();
                    //打开端口
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                    break;
                }
            }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish();
        }
        return false;
    }

    /**
     * Fragment打印回调
     */
    public void setFragmentPrint1(int flag, List<MaterialBinningRecord> materialBinningRecords, BoxBarCode boxBarCode) {
        tabFlag = flag;
        if(tabFlag != flag) {
//            isConnected = false;
        }
        String date = Comm.getSysDate(7);
        mbrList = materialBinningRecords;
        context.boxBarCode = boxBarCode;

        if(isConnected) {
            sendLabel();
        } else {
            // 打开蓝牙配对页面
            startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
        }
    }

    /**
     * 设置生产订单打码格式（大标签）
     * @param tsc
     */
    private void setPrintFormat(LabelCommand tsc) {
        int beginXPos = 20; // 开始横向位置
        int beginYPos = 20; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离
        String date = Comm.getSysDate(7);

        MaterialBinningRecord mbr = mbrList.get(0);
        ProdOrder prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(), ProdOrder.class);
        // 绘制箱子条码
        rowHigthSum = beginYPos + 20;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"箱码： \n");
        tsc.add1DBarcode(115, rowHigthSum-20, LabelCommand.BARCODETYPE.CODE39, 75, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, boxBarCode.getBarCode());
        rowHigthSum = beginYPos + 106;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物流公司："+isNULLS(prodOrder.getDeliveryCompanyName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"客户名称："+isNULLS(mbr.getCustomer().getCustomerName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单编号："+isNULLS(mbr.getSalOrderNo())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单日期："+isNULLS(prodOrder.getProdFdate()).substring(0,10)+" \n");
        rowHigthSum = rowHigthSum + 30;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"------------------------------------------------- \n");
        for(int i=0; i<mbrList.size(); i++) {
            MaterialBinningRecord mbr2 = mbrList.get(i);
            ProdOrder prodOrder2 = JsonUtil.stringToObject(mbr2.getRelationObj(), ProdOrder.class);
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料编码："+isNULLS(prodOrder2.getMtlFnumber())+" \n");
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料名称："+isNULLS(prodOrder2.getMtlFname())+" \n");
            String leaf = isNULLS(prodOrder2.getLeaf());
            String leaf2 = isNULLS(prodOrder2.getLeaf1());
            leaf2 = leaf2.length() > 0 ? "/"+leaf2 : "";
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"面料："+(leaf+leaf2)+" \n");
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"数量："+df.format(mbr2.getNumber())+" \n");
            tsc.addText(200, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"宽："+isNULLS(prodOrder2.getWidth())+" \n");
            tsc.addText(360, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"高："+isNULLS(prodOrder2.getHigh())+" \n");
            rowHigthSum = rowHigthSum + 30;
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"------------------------------------------------- \n");
        }
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(300, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"打印日期："+date+" \n");
    }

    /**
     * 发送标签
     */
    private void sendLabel() {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        int initHigt = 50;
        int high = mbrList.size() * 20;
        int sumHigh = initHigt + high;
        tsc.addSize(78, sumHigh);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addGap(10);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
        // 设置原点坐标
        tsc.addReference(0, 0);
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON);
        // 清除打印缓冲区
        tsc.addCls();
        // 绘制简体中文
        // --------------- 大标签打印 ------------------
        setPrintFormat(tsc);
//        tsc.addText(20, 250, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,"【物料条码】");
//        tsc.addText(20, 300, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料名称："+);
//        // 绘制一维条码
//        tsc.add1DBarcode(20, 250, LabelCommand.BARCODETYPE.CODE128, 80, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "SMARNET");
        // 打印标签
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响

        tsc.addSound(2, 100);
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand();
        // 发送数据
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            return;
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(datas);
        // 保存之后清空页面
//        saveAfter();
    }

    /**
     * 发送标签
     */
    private void sendLabel2() {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        int height = mbrList.size() * 40;
        height = height > 60 ? height : 60;
        tsc.addSize(76, height);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addGap(10);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
        // 设置原点坐标
        tsc.addReference(0, 0);
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON);
        // 清除打印缓冲区
        tsc.addCls();
        // 绘制简体中文
        // --------------- 大标签打印 ------------------
        setPrintFormat(tsc);
//        tsc.addText(20, 250, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,"【物料条码】");
//        tsc.addText(20, 300, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料名称："+);
//        // 绘制一维条码
//        tsc.add1DBarcode(20, 250, LabelCommand.BARCODETYPE.CODE128, 80, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "SMARNET");
        // 打印标签
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响

        tsc.addSound(2, 100);
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand();
        // 发送数据
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            return;
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(datas);
        // 保存之后清空页面
//        saveAfter();
    }

    /**
     * 蓝牙监听广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                // 蓝牙连接断开广播
                case ACTION_USB_DEVICE_DETACHED:
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
                    break;
                case DeviceConnFactoryManager.ACTION_CONN_STATE:
                    int state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1);
                    int deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1);
                    switch (state) {
                        case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:
                            if (id == deviceId) {
                                tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                                tvConnState.setTextColor(Color.parseColor("#666666")); // 未连接-灰色
                                isConnected = false;
                            }
                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
                            tvConnState.setText(getString(R.string.str_conn_state_connecting));
                            tvConnState.setTextColor(Color.parseColor("#6a5acd")); // 连接中-紫色
                            isConnected = false;

                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
//                            tvConnState.setText(getString(R.string.str_conn_state_connected) + "\n" + getConnDeviceInfo());
                            tvConnState.setText(getString(R.string.str_conn_state_connected));
                            tvConnState.setTextColor(Color.parseColor("#008800")); // 已连接-绿色
                            sendLabel();
                            isConnected = true;

                            break;
                        case CONN_STATE_FAILED:
                            Utils.toast(context, getString(R.string.str_conn_fail));
                            tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                            tvConnState.setTextColor(Color.parseColor("#666666")); // 未连接-灰色
                            isConnected = false;

                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_STATE_DISCONN:
                    if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].closePort(id);
                    }
                    break;
                case PRINTER_COMMAND_ERROR:
                    Utils.toast(context, getString(R.string.str_choice_printer_command));
                    break;
                case CONN_PRINTER:
                    Utils.toast(context, getString(R.string.str_cann_printer));
                    break;
                case MESSAGE_UPDATE_PARAMETER:
                    String strIp = msg.getData().getString("Ip");
                    String strPort = msg.getData().getString("Port");
                    //初始化端口信息
                    new DeviceConnFactoryManager.Build()
                            //设置端口连接方式
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.WIFI)
                            //设置端口IP地址
                            .setIp(strIp)
                            //设置端口ID（主要用于连接多设备）
                            .setId(id)
                            //设置连接的热点端口号
                            .setPort(Integer.parseInt(strPort))
                            .build();
                    threadPool = ThreadPool.getInstantiation();
                    threadPool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeviceConnFactoryManager.closeAllPort();
        if (threadPool != null) {
            threadPool.stopThreadPool();
        }
    }

}
