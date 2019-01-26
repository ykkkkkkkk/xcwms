package ykk.xc.com.xcwms.basics;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Box;
import ykk.xc.com.xcwms.model.BoxBarCode;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.MaterialBinningRecord;
import ykk.xc.com.xcwms.model.pur.ProdOrder;
import ykk.xc.com.xcwms.model.sal.DeliOrder;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.MyViewPager;
import ykk.xc.com.xcwms.util.adapter.BaseFragmentAdapter;
import ykk.xc.com.xcwms.util.blueTooth.BluetoothDeviceListDialog;
import ykk.xc.com.xcwms.util.blueTooth.Constant;
import ykk.xc.com.xcwms.util.blueTooth.DeviceConnFactoryManager;
import ykk.xc.com.xcwms.util.blueTooth.ThreadPool;
import ykk.xc.com.xcwms.util.blueTooth.Utils;
import ykk.xc.com.xcwms.util.interfaces.IFragmentKeyeventListener;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ykk.xc.com.xcwms.util.blueTooth.Constant.MESSAGE_UPDATE_PARAMETER;
import static ykk.xc.com.xcwms.util.blueTooth.DeviceConnFactoryManager.CONN_STATE_FAILED;

public class PrintMainActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    MyViewPager viewPager;
    @BindView(R.id.tv_connState)
    TextView tvConnState;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_2)
    TextView tv2;
    @BindView(R.id.tv_3)
    TextView tv3;
    @BindView(R.id.tv_4)
    TextView tv4;
    private PrintMainActivity context = this;
    private static final String TAG = "PrintMainActivity";
    private TextView curText;
    private IFragmentKeyeventListener fragment2Listener;
    private List<String> barcodeList = new ArrayList<>(); // 打印的条码
    private String barcode; // 打印的条码
    private boolean isConnected; // 蓝牙是否连接标识
    private int tabFlag;
    private int id = 0; // 设备id
    private ThreadPool threadPool;
    private BarCodeTable bt;
    private Map<String, String> map4;
    private List<ProdOrder> prodOrderList = new ArrayList<>();
    private List<MaterialBinningRecord> mbrList = new ArrayList<>();
    private List<BoxBarCode> boxBarCodeList = null;
    private BoxBarCode boxBarCode;
    private int printCount; // 打印次数
    private DecimalFormat df = new DecimalFormat("#.####");
    private static final int CONN_STATE_DISCONN = 0x007; // 连接状态断开
    private static final int PRINTER_COMMAND_ERROR = 0x008; // 使用打印机指令错误
    private static final int CONN_PRINTER = 0x12;

    @Override
    public int setLayoutResID() {
        return R.layout.ab_print_main;
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
        PrintFragment3 fragment3 = new PrintFragment3();
        PrintFragment4 fragment4 = new PrintFragment4();

        listFragment.add(fragment1);
        listFragment.add(fragment2);
        listFragment.add(fragment3);
        listFragment.add(fragment4);
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
                        tabChange(tv1,0);
                        break;
                    case 1:
                        tabChange(tv2,1);
                        break;
                    case 2:
                        tabChange(tv3,2);
                        break;
                    case 3:
                        tabChange(tv4,3);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 延时跳入到界面2
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tabChange(tv2,1);
            }
        },200);
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
        }
    }

    @OnClick({R.id.btn_close, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                context.finish();

                break;
            case R.id.tv_1: // 物料
                tabChange(tv1,0);

                break;
            case R.id.tv_2: // 扫码打印
                tabChange(tv2,1);

                break;
            case R.id.tv_3: // 箱码打印
                tabChange(tv3,2);

                break;
            case R.id.tv_4: // 物料包装
                tabChange(tv4,3);

                break;
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(TextView tv) {
        if(curText.getId() == tv.getId()) {
            return;
        }
        curText.setText(getValues(curText).replace("✔",""));
        curText.setTextColor(Color.parseColor("#666666" +""));
        tv.setText(getValues(tv)+"✔");
        tv.setTextColor(Color.parseColor("#FF3300"));
        curText = tv;
    }

    private void tabChange(TextView tv, int page) {
        tabSelected(tv);
        viewPager.setCurrentItem(page, false);
    }

    /**
     * Fragment打印回调
     */
    public void setFragmentPrint(int flag, String result, int count) {
        prodOrderList.clear();

        tabFlag = flag;
        printCount = count;
        if(tabFlag != flag) {
//            isConnected = false;
        }
        bt = JsonUtil.stringToObject(result, BarCodeTable.class);
        barcode = bt.getBarcode();

        if(isConnected) {
            for(int i=0; i<count; i++) {
                sendLabel(0);
            }

        } else {
            // 打开蓝牙配对页面
            startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
        }
    }

    /**
     * 生产订单的 大标，小标
     * @param flag
     * @param result
     */
    public void setFragmentPrint2(int flag, String result) {
        tabFlag = flag;
        if(tabFlag != flag) {
//            isConnected = false;
        }
        // 清空list
        prodOrderList.clear();
        barcodeList.clear();

        List<BarCodeTable> barCodeTables = JsonUtil.strToList(result, BarCodeTable.class);

        for(int i=0; i<barCodeTables.size(); i++) {
            BarCodeTable bt = barCodeTables.get(i);
            switch (bt.getCaseId()) {
                case 34: // 生产任务单
                    ProdOrder p = JsonUtil.stringToObject(bt.getRelationObj(), ProdOrder.class);
                    prodOrderList.add(p);
                    barcodeList.add(bt.getBarcode());

                    break;
            }
        }
        if(isConnected) {
            for(int i = 0; i< prodOrderList.size(); i++) {
                sendLabel(i);
            }
        } else {
            // 打开蓝牙配对页面
            startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
        }
    }

    /**
     * 生产装箱清单
     * @param flag
     * @param result
     */
    public void setFragmentPrint2B(int flag, String result) {
        tabFlag = flag;
        if(tabFlag != flag) {
//            isConnected = false;
        }
        // 清空list
        mbrList.clear();

        boxBarCode = JsonUtil.strToObject(result, BoxBarCode.class);
        if(boxBarCode.getStatus() != 2) {
            Comm.showWarnDialog(context, "扫码的箱子未封箱，不能打印！");
            return;
        }

        // 把箱子里的物料显示出来
        if(boxBarCode.getMtlBinningRecord() == null || boxBarCode.getMtlBinningRecord().size() == 0) {
            Comm.showWarnDialog(context, "扫码的箱子中没有物料！");
            return;
        }
        mbrList.clear();
        List<MaterialBinningRecord> tmpMbrList = boxBarCode.getMtlBinningRecord();
        mbrList.addAll(tmpMbrList);

        if(isConnected) {
            setProdBoxListPrint();
        } else {
            // 打开蓝牙配对页面
            startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
        }
    }

    /**
     * 复核装箱清单
     * @param flag
     * @param result
     */
    public void setFragmentPrint2C(int flag, String result) {
        tabFlag = flag;
        if(tabFlag != flag) {
//            isConnected = false;
        }
        // 清空list
        mbrList.clear();

        BoxBarCode boxBarCode = JsonUtil.strToObject(result, BoxBarCode.class);
        if(boxBarCode.getStatus() != 2) {
            Comm.showWarnDialog(context, "扫码的箱子未封箱，不能打印！");
            return;
        }

        // 把箱子里的物料显示出来
        if(boxBarCode.getMtlBinningRecord() == null || boxBarCode.getMtlBinningRecord().size() == 0) {
            Comm.showWarnDialog(context, "扫码的箱子中没有物料！");
            return;
        }
        mbrList.clear();
        List<MaterialBinningRecord> tmpMbrList = boxBarCode.getMtlBinningRecord();
        mbrList.addAll(tmpMbrList);

        if(isConnected) {
            setDeliBoxListPrint();
        } else {
            // 打开蓝牙配对页面
            startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
        }
    }

    public void setFragmentPrint3(int flag, List<BoxBarCode> boxBarCodeList) {
        tabFlag = flag;
        if(tabFlag != flag) {
//            isConnected = false;
        }
        // 清空list
        if(this.boxBarCodeList == null) this.boxBarCodeList = new ArrayList<>();
        else this.boxBarCodeList.clear();

        this.boxBarCodeList.addAll(boxBarCodeList);

        if(isConnected) {
            for(int i = 0; i< this.boxBarCodeList.size(); i++) {
                sendLabel(i);
            }
        } else {
            // 打开蓝牙配对页面
            startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
        }
    }

    /**
     * Fragment4打印回调
     */
    public void setFragmentPrint4(int flag, String result, int count) {
        prodOrderList.clear();

        tabFlag = flag;
        printCount = count;
        if(tabFlag != flag) {
//            isConnected = false;
        }
        map4 = JsonUtil.stringToObject(result, Map.class);
        barcode = map4.get("barCode");

        if(isConnected) {
            for(int i=0; i<count; i++) {
                sendLabel(6);
            }

        } else {
            // 打开蓝牙配对页面
            startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
        }
    }

    public void setFragmentKeyeventListener(IFragmentKeyeventListener fragmentKeyeventListener) {
        this.fragment2Listener = fragmentKeyeventListener;
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

    /**
     * 发送标签
     *
     */
    void sendLabel(int count) {
//        LabelCommand tsc = new LabelCommand();
//        // 设置标签尺寸，按照实际尺寸设置
////        tsc.addSize(78, 60); // 有间隙的纸
//        tsc.addSize(80, 26); // 连纸
//        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
//        tsc.addGap(10);
//        // 设置打印方向
//        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
//        // 开启带Response的打印，用于连续打印
//        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
//        // 设置原点坐标
//        tsc.addReference(0, 0);
//        // 撕纸模式开启
//        tsc.addTear(EscCommand.ENABLE.ON);
//        // 清除打印缓冲区
//        tsc.addCls();
            // 绘制简体中文
        switch (tabFlag) {
            case 0: // 物料条码
                setMtlFormat();

                break;
            case 1: // 生产订单--大标签
                setDeliOrderFormat(count);

                break;
            case 2: // 生产订单--小标签
                setDeliOrderFormat2(count);

                break;
            case 3: // 箱码打印条码
                setBoxBarCodeFormat(count);

                break;
            case 6: // 物料包装--大标签
                setMtlBoxFormat();

                break;
            case 7: // 物料包装--小标签
                setMtlBoxFormat2();

                break;
        }

        // 打印标签end
//        tsc.addPrint(1, 1);
//        // 打印标签后 蜂鸣器响
//
//        tsc.addSound(2, 100);
//        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
//        Vector<Byte> datas = tsc.getCommand();
//        // 发送数据
//        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
//            return;
//        }
//        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(datas);
    }

    /**
     * 设置物料打码格式
     */
    private void setMtlFormat() {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(80, 26); // 有间隙的纸
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addGap(0);
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
        // --------------- 打印区-------------Begin
        tsc.addText(10, 20, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"编码："+bt.getMaterialNumber());
        tsc.addText(350, 20, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"规格："+bt.getMaterialSize());
        String tmpName = bt.getMaterialName();
        int tmpLen = tmpName.length();
        String mtlName1 = null;
        String mtlName2 = null;
        if(tmpName.length() <= 27) {
            tsc.addText(10, 50, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"名称："+tmpName);
        } else {
            mtlName1 = tmpName.substring(0, 27);
            mtlName2 = tmpName.substring(27, tmpLen);
            tsc.addText(10, 50, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"名称："+mtlName1);
            tsc.addText(80, 80, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+mtlName2);
        }

        // 绘制一维条码
        tsc.add1DBarcode(10, 110, LabelCommand.BARCODETYPE.CODE39, 60, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, barcode);
        tsc.addText(480, 130, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+Comm.getSysDate(9));

        // --------------- 打印区-------------End
        // 打印标签end
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
    }

    /**
     * 设置生产订单打码格式（大标签）
     */
    private void setDeliOrderFormat(int count) {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(78, 60); // 有间隙的纸
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
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 26; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离
        String date = Comm.getSysDate(9);

        ProdOrder prodOrder = prodOrderList.get(count);

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,""+isNULLS(prodOrder.getDeliveryCompanyName())+" \n");
        tsc.add1DBarcode(300, beginYPos, LabelCommand.BARCODETYPE.CODE39, 50, LabelCommand.READABEL.DISABLE, LabelCommand.ROTATION.ROTATION_0, 2, 5, prodOrder.getFbillno());
        rowHigthSum = beginYPos + 60;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"客户名称："+isNULLS(prodOrder.getCustName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"生产单号："+prodOrder.getFbillno()+" \n");
        tsc.addText(280, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单号："+prodOrder.getSalOrderNo()+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"生产序号："+isNULLS(prodOrder.getProdSeqNumber())+" \n");
        tsc.addText(280, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"日期："+date+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"产品名称："+isNULLS(prodOrder.getMtlFname())+" \n");

        String width = isNULLS(prodOrder.getWidth());
        String high = isNULLS(prodOrder.getHigh());
        String strWidthHigh = "";
        width = width.length() > 0 ? "宽："+width : "";
        high = high.length() > 0 ? "高："+high : "";
        strWidthHigh = width + "  *" + high;
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"产品规格："+strWidthHigh+" \n");

        String leaf = isNULLS(prodOrder.getLeaf());
        String leaf2 = isNULLS(prodOrder.getLeaf1());
        String strTmp = "";
        if (leaf.length() > 0 && leaf2.length() > 0) strTmp = leaf + " , " + leaf2;
        else if (leaf.length() > 0) strTmp = leaf;
        else if (leaf2.length() > 0) strTmp = leaf2;
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "布料/叶片：" + strTmp + " \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"联系人："+isNULLS(prodOrder.getReceivePerson())+" \n");
        tsc.addText(280, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"电话："+isNULLS(prodOrder.getReceiveTel())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"收货地址："+isNULLS(prodOrder.getReceiveAddress())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"备注："+isNULLS(prodOrder.getRemarks())+" \n");
        rowHigthSum = rowHigthSum + 51;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"条码： \n");
        // 绘制一维条码
        tsc.add1DBarcode(115, rowHigthSum-20, LabelCommand.BARCODETYPE.CODE39, 75, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, barcodeList.get(count));

        // --------------- 打印区-------------End
        // 打印标签end
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
    }
    /**
     * 设置生产订单打码格式（小标签）
     */
    private void setDeliOrderFormat2(int count) {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(78, 60); // 有间隙的纸
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
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 26; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 31; // 每行之间的距离
        String date = Comm.getSysDate(9);

//        for(int i=0; i<prodOrderList.size(); i++) {
        ProdOrder prodOrder = prodOrderList.get(count);

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "客户名称：" + isNULLS(prodOrder.getCustName()) + " \n");
        rowHigthSum = beginYPos + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "订单号：" + isNULLS(prodOrder.getSalOrderNo()) + " \n");
        tsc.addText(260, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"日期："+date+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "产品名称：" + isNULLS(prodOrder.getMtlFname()) + " \n");

        String leaf = isNULLS(prodOrder.getLeaf());
        String leaf2 = isNULLS(prodOrder.getLeaf1());
        String strTmp = "";
        if (leaf.length() > 0 && leaf2.length() > 0) strTmp = leaf + " , " + leaf2;
        else if (leaf.length() > 0) strTmp = leaf;
        else if (leaf2.length() > 0) strTmp = leaf2;

        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "布料/叶片：" + strTmp + " \n");
        rowHigthSum = rowHigthSum + rowSpacing;

        String width = isNULLS(prodOrder.getWidth());
        String high = isNULLS(prodOrder.getHigh());
        String strWidthHigh = "";
        width = width.length() > 0 ? "宽："+width : "";
        high = high.length() > 0 ? "高："+high : "";
        strWidthHigh = width + "  *" + high;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "产品规格：" + strWidthHigh + " \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "备注：" + isNULLS(prodOrder.getRemarks()) + " \n");

        // --------------- 打印区-------------End
        // 打印标签end
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
    }

    /**
     * 设置箱码打印格式
     */
    private void setBoxBarCodeFormat(int count) {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(78, 60); // 有间隙的纸
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
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 40; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 50; // 每行之间的距离

        BoxBarCode boxBarCode = boxBarCodeList.get(count);
        Box box = boxBarCode.getBox();

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,"包装物名称："+isNULLS(box.getBoxName())+" \n");
        rowHigthSum = rowHigthSum + 100;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,"包装物规格："+isNULLS(box.getBoxSize())+" \n");
        rowHigthSum = rowHigthSum + 100;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,"条码： \n");
        // 绘制一维条码
        tsc.add1DBarcode(140, rowHigthSum-20, LabelCommand.BARCODETYPE.CODE39, 75, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, boxBarCode.getBarCode());

        // --------------- 打印区-------------End
        // 打印标签end
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
    }

    /**
     * 设置生产装箱清单打印格式
     */
    private void setProdBoxListPrint() {
        setProdBoxListFormat1();
        // 绘制箱子条码
        for(int i=0, size = mbrList.size(); i<size; i++) {
            setProdBoxListFormat2(i);
        }
        setProdBoxListFormat3();
    }

    /**
     * 打印头部1
     */
    private void setProdBoxListFormat1() {
        LabelCommand tsc = new LabelCommand();
        setTscBegin(tsc,0,0, false);
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 12; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离

        MaterialBinningRecord mbr = mbrList.get(0);
        ProdOrder prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(), ProdOrder.class);
        // 绘制箱子条码
        rowHigthSum = beginYPos + 18;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"箱码： \n");
        tsc.add1DBarcode(115, rowHigthSum-18, LabelCommand.BARCODETYPE.CODE39, 65, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, boxBarCode.getBarCode());
        rowHigthSum = beginYPos + 96;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物流公司："+isNULLS(prodOrder.getDeliveryCompanyName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"客户名称："+isNULLS(mbr.getCustomer().getCustomerName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单编号："+isNULLS(mbr.getSalOrderNo())+" \n");
        tsc.addText(280, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单日期："+isNULLS(prodOrder.getProdFdate()).substring(0,10)+" \n");

        // --------------- 打印区-------------End
        setTscEnd(tsc);
    }

    /**
     * 打印物料信息2
     */
    private void setProdBoxListFormat2(int pos) {
        LabelCommand tsc = new LabelCommand();
        setTscBegin(tsc,0,0, false);
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 0; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离

        MaterialBinningRecord mbr = mbrList.get(pos);
        ProdOrder prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(), ProdOrder.class);
        Material  mtl = mbr.getMtl();

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"------------------------------------------------- \n");
        rowHigthSum = beginYPos + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料编码："+isNULLS(prodOrder.getMtlFnumber())+" \n");
        tsc.addText(320, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"旧编码："+isNULLS(mtl.getOldNumber())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"名称："+isNULLS(prodOrder.getMtlFname())+" \n");

        String leaf = isNULLS(prodOrder.getLeaf());
        String leaf2 = isNULLS(prodOrder.getLeaf1());
        String strTmp = "";
        if (leaf.length() > 0 && leaf2.length() > 0) strTmp = leaf + " , " + leaf2;
        else if (leaf.length() > 0) strTmp = leaf;
        else if (leaf2.length() > 0) strTmp = leaf2;
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"面料："+strTmp+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"数量："+df.format(mbr.getNumber())+" \n");
        tsc.addText(200, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"宽："+isNULLS(prodOrder.getWidth())+" \n");
        tsc.addText(360, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"高："+isNULLS(prodOrder.getHigh())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"备注："+isNULLS(prodOrder.getRemarks())+" \n");

        // --------------- 打印区-------------End
        setTscEnd(tsc);
    }

    /**
     * 打印日期
     */
    private void setProdBoxListFormat3() {
        LabelCommand tsc = new LabelCommand();
        setTscBegin(tsc,0,0, false);
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 0; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离
        String date = Comm.getSysDate(9);
        MaterialBinningRecord mbr = mbrList.get(0);
        ProdOrder prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(), ProdOrder.class);

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"------------------------------------------------- \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"联系人："+isNULLS(prodOrder.getReceivePerson())+" \n");
        tsc.addText(230, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"电话："+isNULLS(prodOrder.getReceiveTel())+" \n");
        tsc.addText(500, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+date+" \n");

        rowHigthSum = rowHigthSum + rowSpacing;
        String tmpAddress = isNULLS(prodOrder.getReceiveAddress());
        int tmpLen = tmpAddress.length();
        String addres1 = null;
        String addres2 = null;
        if(tmpAddress.length() <= 22) {
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"地址："+tmpAddress);
        } else {
            addres1 = tmpAddress.substring(0, 22);
            addres2 = tmpAddress.substring(22, tmpLen);
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"地址："+addres1);
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(80, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+addres2);
        }

        // --------------- 打印区-------------End
        setTscEnd(tsc);
    }

    /**
     * 设置复核单装箱清单打印格式
     */
    private void setDeliBoxListPrint() {
        setDeliBoxListFormat1();
        // 绘制箱子条码
        for(int i=0, size = mbrList.size(); i<size; i++) {
            setDeliBoxListFormat2(i);
        }
        setDeliBoxListFormat3();
    }

    /**
     * 打印头部1
     */
    private void setDeliBoxListFormat1() {
        LabelCommand tsc = new LabelCommand();
        setTscBegin(tsc,0,0, false);
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 12; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离

        MaterialBinningRecord mbr = mbrList.get(0);
        DeliOrder deliOrder = JsonUtil.stringToObject(mbr.getRelationObj(), DeliOrder.class);
        // 绘制箱子条码
        rowHigthSum = beginYPos + 20;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"箱码： \n");
        tsc.add1DBarcode(115, rowHigthSum-20, LabelCommand.BARCODETYPE.CODE39, 75, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, boxBarCode.getBarCode());
        rowHigthSum = beginYPos + 106;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物流公司："+isNULLS(deliOrder.getDeliveryCompanyName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"客户名称："+isNULLS(deliOrder.getCustName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单编号："+isNULLS(deliOrder.getSalOrderNo())+" \n");
        tsc.addText(280, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单日期："+isNULLS(deliOrder.getDeliDate()).substring(0,10)+" \n");

        // --------------- 打印区-------------End
        setTscEnd(tsc);
    }

    /**
     * 打印物料信息2
     */
    private void setDeliBoxListFormat2(int pos) {
        LabelCommand tsc = new LabelCommand();
        setTscBegin(tsc,0,0, false);
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 0; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离

        MaterialBinningRecord mbr = mbrList.get(pos);
        Material mtl = mbr.getMtl();
        DeliOrder deliOrder = JsonUtil.stringToObject(mbr.getRelationObj(), DeliOrder.class);

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"------------------------------------------------- \n");
        rowHigthSum = beginYPos + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料编码："+isNULLS(deliOrder.getMtlFnumber())+" \n");
        tsc.addText(320, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"旧编码："+isNULLS(mtl.getOldNumber())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"名称："+isNULLS(deliOrder.getMtlFname())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"数量："+df.format(mbr.getNumber())+" \n");
        tsc.addText(260, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"单位："+isNULLS(deliOrder.getMtlUnitName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"备注："+isNULLS(deliOrder.getEntryRemark())+" \n");

        // --------------- 打印区-------------End
        setTscEnd(tsc);
    }

    /**
     * 打印日期
     */
    private void setDeliBoxListFormat3() {
        LabelCommand tsc = new LabelCommand();
        setTscBegin(tsc,0,0, false);
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 0; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离
        String date = Comm.getSysDate(9);
        MaterialBinningRecord mbr = mbrList.get(0);
        DeliOrder deliOrder = JsonUtil.stringToObject(mbr.getRelationObj(), DeliOrder.class);

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"------------------------------------------------- \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"联系人："+isNULLS(deliOrder.getFconsignee())+" \n");
        tsc.addText(230, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"电话："+isNULLS(deliOrder.getFreceivetel())+" \n");
        tsc.addText(500, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+date+" \n");

        rowHigthSum = rowHigthSum + rowSpacing;
        String tmpAddress = isNULLS(deliOrder.getFreceive());
        int tmpLen = tmpAddress.length();
        String addres1 = null;
        String addres2 = null;
        if(tmpAddress.length() <= 22) {
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"地址："+tmpAddress);
        } else {
            addres1 = tmpAddress.substring(0, 22);
            addres2 = tmpAddress.substring(22, tmpLen);
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"地址："+addres1);
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(80, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+addres2);
        }

        // --------------- 打印区-------------End
        setTscEnd(tsc);
    }

    /**
     * 设置物料包装打码格式（大标签）
     */
    private void setMtlBoxFormat() {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(78, 60); // 有间隙的纸
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
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 38; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 35; // 每行之间的距离

        tsc.addText(210, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,"物料包装");
        rowHigthSum = rowHigthSum + rowSpacing+65;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"编码："+ map4.get("MaterialNumber"));
        rowHigthSum = rowHigthSum + rowSpacing;
        String tmpName = map4.get("MaterialName");
        int tmpLen = tmpName.length();
        String mtlName1 = null;
        String mtlName2 = null;
        if(tmpName.length() <= 27) {
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"名称："+tmpName);
            rowHigthSum = rowHigthSum + rowSpacing;
        } else {
            mtlName1 = tmpName.substring(0, 27);
            mtlName2 = tmpName.substring(27, tmpLen);
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"名称："+mtlName1);
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(80, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+mtlName2);
        }
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"包装名称："+map4.get("MaterialPackName"));
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"包装规格："+map4.get("MaterialPackSize"));
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"包装数量："+map4.get("MaterialCalculateNumber"));
        tsc.addText(320, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"日期："+map4.get("PrintDate"));
        rowHigthSum = rowHigthSum + rowSpacing+5;
        // 绘制一维条码
        tsc.add1DBarcode(30, rowHigthSum, LabelCommand.BARCODETYPE.CODE39, 80, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, barcode);
        // --------------- 打印区-------------End
        // 打印标签end
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
    }

    /**
     * 设置物料包装打码格式（小标签）
     */
    private void setMtlBoxFormat2() {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(80, 26); // 有间隙的纸
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addGap(0);
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
        // --------------- 打印区-------------Begin
        tsc.addText(10, 20, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"包装编码："+map4.get("MaterialNumber"));
        tsc.addText(350, 20, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"规格："+map4.get("MaterialPackSize"));
        String tmpName = map4.get("MaterialName");
        int tmpLen = tmpName.length();
        String mtlName1 = null;
        String mtlName2 = null;
        if(tmpName.length() <= 27) {
            tsc.addText(10, 50, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"名称："+tmpName);
        } else {
            mtlName1 = tmpName.substring(0, 27);
            mtlName2 = tmpName.substring(27, tmpLen);
            tsc.addText(10, 50, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"名称："+mtlName1);
            tsc.addText(80, 80, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+mtlName2);
        }

        // 绘制一维条码
        tsc.add1DBarcode(10, 110, LabelCommand.BARCODETYPE.CODE39, 60, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, barcode);
        tsc.addText(480, 130, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+Comm.getSysDate(9));

        // --------------- 打印区-------------End
        // 打印标签end
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
    }

    /**
     * 打印前段配置
     * @param tsc
     */
    private void setTscBegin(LabelCommand tsc, int width, int height, boolean isAddGap) {
        // 设置标签尺寸，按照实际尺寸设置
        if(width > 0) tsc.addSize(width, height);
        else tsc.addSize(78, 26); // 这个为生产装箱单打印的
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
//        tsc.addGap(10);
        if(isAddGap) tsc.addGap(10);
        else tsc.addGap(0);
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
    }
    /**
     * 打印后段配置
     * @param tsc
     */
    private void setTscEnd(LabelCommand tsc) {
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

                            switch (tabFlag) {
                                case 0: // 物料条码
                                    for(int i=0; i<printCount; i++) {
                                        sendLabel(0);
                                    }

                                    break;
                                case 1: // 生产订单--大标签
                                    if (prodOrderList.size() > 0) {
                                        for (int i = 0; i < prodOrderList.size(); i++) {
                                            sendLabel(i);
                                        }
                                    } else sendLabel(0);

                                    break;
                                case 2: // 生产订单--小标签
                                    if (prodOrderList.size() > 0) {
                                        for (int i = 0; i < prodOrderList.size(); i++) {
                                            sendLabel(i);
                                        }
                                    } else sendLabel(0);

                                    break;
                                case 3: // 箱码打印条码
                                    if (boxBarCodeList.size() > 0) {
                                        for (int i = 0; i < boxBarCodeList.size(); i++) {
                                            sendLabel(i);
                                        }
                                    } else sendLabel(0);

                                    break;
                                case 4: // 生产装箱清单打印条码
                                    setProdBoxListPrint();

                                    break;
                                case 5: // 复核装箱清单打印条码
                                    setDeliBoxListPrint();

                                    break;
                                case 6: // 物料包装打印条码
                                    for(int i=0; i<printCount; i++) {
                                        sendLabel(6);
                                    }

                                    break;
                                case 7: // 物料包装打印条码
                                    for(int i=0; i<printCount; i++) {
                                        sendLabel(7);
                                    }

                                    break;
                            }

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
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_DEVICE_DETACHED);
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy()");
        DeviceConnFactoryManager.closeAllPort();
        if (threadPool != null) {
            threadPool.stopThreadPool();
        }
    }

    private String getConnDeviceInfo() {
        String str = "";
        DeviceConnFactoryManager deviceConnFactoryManager = DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id];
        if (deviceConnFactoryManager != null
                && deviceConnFactoryManager.getConnState()) {
            if ("USB".equals(deviceConnFactoryManager.getConnMethod().toString())) {
                str += "USB\n";
                str += "USB Name: " + deviceConnFactoryManager.usbDevice().getDeviceName();
            } else if ("WIFI".equals(deviceConnFactoryManager.getConnMethod().toString())) {
                str += "WIFI\n";
                str += "IP: " + deviceConnFactoryManager.getIp() + "\t";
                str += "Port: " + deviceConnFactoryManager.getPort();
            } else if ("BLUETOOTH".equals(deviceConnFactoryManager.getConnMethod().toString())) {
                str += "BLUETOOTH\n";
                str += "MacAddress: " + deviceConnFactoryManager.getMacAddress();
//                deviceConnFactoryManager.get
            } else if ("SERIAL_PORT".equals(deviceConnFactoryManager.getConnMethod().toString())) {
                str += "SERIAL_PORT\n";
                str += "Path: " + deviceConnFactoryManager.getSerialPortPath() + "\t";
                str += "Baudrate: " + deviceConnFactoryManager.getBaudrate();
            }
        }
        return str;
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

}
