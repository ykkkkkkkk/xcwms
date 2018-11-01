package ykk.xc.com.xcwms.sales;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.basics.Box_DialogActivity;
import ykk.xc.com.xcwms.basics.Cust_DialogActivity;
import ykk.xc.com.xcwms.basics.DeliveryWay_DialogActivity;
import ykk.xc.com.xcwms.basics.PrintMainActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.AssistInfo;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Box;
import ykk.xc.com.xcwms.model.BoxBarCode;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.MaterialBinningRecord;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.sal.PickingList;
import ykk.xc.com.xcwms.sales.adapter.Sal_RecombinationAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.blueTooth.BluetoothDeviceListDialog;
import ykk.xc.com.xcwms.util.blueTooth.Constant;
import ykk.xc.com.xcwms.util.blueTooth.DeviceConnFactoryManager;
import ykk.xc.com.xcwms.util.blueTooth.ThreadPool;
import ykk.xc.com.xcwms.util.blueTooth.Utils;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ykk.xc.com.xcwms.util.blueTooth.Constant.MESSAGE_UPDATE_PARAMETER;
import static ykk.xc.com.xcwms.util.blueTooth.DeviceConnFactoryManager.CONN_STATE_FAILED;

public class Sal_RecombinationActivity extends BaseActivity {

    @BindView(R.id.lin_box)
    LinearLayout linBox;
    @BindView(R.id.tv_box)
    TextView tvBox;
    @BindView(R.id.et_boxCode)
    EditText etBoxCode;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_boxName)
    TextView tvBoxName;
    @BindView(R.id.tv_boxSize)
    TextView tvBoxSize;
    @BindView(R.id.tv_boxLength)
    TextView tvBoxLength;
    @BindView(R.id.tv_boxWidth)
    TextView tvBoxWidth;
    @BindView(R.id.tv_boxAltitude)
    TextView tvBoxAltitude;
    @BindView(R.id.tv_boxVolume)
    TextView tvBoxVolume;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.tv_deliverSel)
    TextView tvDeliverSel;
    @BindView(R.id.tv_pickingListSel)
    TextView tvPickingListSel;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_connState)
    TextView tvConnState;

    private Sal_RecombinationActivity context = this;
    private static final int SEL_CUST = 11, SEL_DELI = 12, SEL_BOX = 13, SEL_NUM = 14, SEL_PICKINGLIST = 15;
    private static final int SUCC1 = 201, UNSUCC1 = 501, SAVE = 202, UNSAVE = 502, DELETE = 203, UNDELETE = 503, MODIFY = 204, UNMODIFY = 504, MODIFY2 = 205, UNMODIFY2 = 505, MODIFY3 = 206, UNMODIFY3 = 506, MODIFY_NUM = 207, UNMODIFY_NUM = 507;
    private static final int CODE1 = 1, CODE2 = 2, CODE60 = 60;
    private Customer customer; // 客户
    private Box box; // 箱子表
    private BoxBarCode boxBarCode; // 箱码表
    private Sal_RecombinationAdapter mAdapter;
    private String boxBarcode, mtlBarcode; // 对应的条码号
    private List<MaterialBinningRecord> mbrList = new ArrayList<>();
    private List<PickingList> plList = new ArrayList<>();
    private char curViewFlag = '1'; // 1：箱子，2：物料
    private DecimalFormat df = new DecimalFormat("#.####");
    private int curPos; // 当前行
    private AssistInfo assist; // 辅助资料--发货方式
    private char status = '0'; // 箱子状态（0：创建，1：开箱，2：封箱）
    private User user;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int id = 0; // 设备id
    private ThreadPool threadPool;
    private boolean isConnected, isPair; // 蓝牙是否连接标识
    private static final int CONN_STATE_DISCONN = 0x007; // 连接状态断开
    private static final int PRINTER_COMMAND_ERROR = 0x008; // 使用打印机指令错误
    private static final int CONN_PRINTER = 0x12;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_RecombinationActivity> mActivity;

        public MyHandler(Sal_RecombinationActivity activity) {
            mActivity = new WeakReference<Sal_RecombinationActivity>(activity);
        }

        public void handleMessage(Message msg) {
            final Sal_RecombinationActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 扫码成功后进入
                        switch (m.curViewFlag) {
                            case '1': // 箱码扫码   返回
                                m.boxBarCode = JsonUtil.strToObject((String) msg.obj, BoxBarCode.class);
                                m.etMtlCode.setText("");
                                m.getBox();

                                break;
                            case '2': // 物料扫码   返回
                                BarCodeTable bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                Material mtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
                                bt.setMtl(mtl);
                                m.getMaterialAfter(bt);

                                break;
                        }

                        break;
                    case UNSUCC1:
                        m.mHandler.sendEmptyMessageDelayed(CODE60, 200);
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        if(errMsg.length() > 0) {
                            Comm.showWarnDialog(m.context, errMsg);
                        } else {
                            Comm.showWarnDialog(m.context,"条码不存在，或者扫错了条码！");
                        }

                        break;
                    case SAVE: // 保存 成功
//                        m.status = '1';
//                        m.mbrList.clear();
//                        m.plList.clear();
//                        m.reset();
//                        Comm.showWarnDialog(m.context,"保存成功√");
                        m.toasts("保存成功，现在打印装箱清单...");
                        if(m.isConnected) {
                            m.sendLabel();
                        } else {
                            // 打开蓝牙配对页面
                            m.startActivityForResult(new Intent(m.context, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
                        }

                        break;
                    case UNMODIFY2: // 修改发货方式 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case MODIFY3: // 修改条码贴到对应的箱子上  成功
                        m.linBox.setVisibility(View.GONE);
                        m.tvBox.setText("");
                        m.getBox();

                        break;
                    case UNMODIFY3: // 修改条码贴到对应的箱子上 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case CODE1: // 清空数据
                        m.etMtlCode.setText("");
                        m.mtlBarcode = "";

                        break;
                    case CODE2: // Dialog默认得到焦点，隐藏软键盘

                        break;
                    case CODE60: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 箱码扫码
                                m.setTexts(m.etBoxCode, m.boxBarcode);
                                break;
                            case '2': // 物料扫码
                                m.setTexts(m.etMtlCode, m.mtlBarcode);
                                break;
                        }

                        break;

                    // 蓝牙打印模块的
                    case CONN_STATE_DISCONN:
                        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[m.id] != null) {
                            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[m.id].closePort(m.id);
                        }
                        break;
                    case PRINTER_COMMAND_ERROR:
                        Utils.toast(m.context, m.getString(R.string.str_choice_printer_command));
                        break;
                    case CONN_PRINTER:
                        Utils.toast(m.context, m.getString(R.string.str_cann_printer));
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
                                .setId(m.id)
                                //设置连接的热点端口号
                                .setPort(Integer.parseInt(strPort))
                                .build();
                        m.threadPool = ThreadPool.getInstantiation();
                        m.threadPool.addTask(new Runnable() {
                            @Override
                            public void run() {
                                DeviceConnFactoryManager.getDeviceConnFactoryManagers()[m.id].openPort();
                            }
                        });
                        break;
                }
            }
        }
    }

    private void saveAfter() {
        status = '1';
        mbrList.clear();
        plList.clear();
        reset();
        Comm.showWarnDialog(context,"保存成功√");
    }

    @Override
    public int setLayoutResID() {
        return R.layout.sal_recombination;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Sal_RecombinationAdapter(context, mbrList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Sal_RecombinationAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, MaterialBinningRecord entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getNumber()), "0.0", SEL_NUM);
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(etBoxCode);
        hideSoftInputMode(etMtlCode);
        getUserInfo();

    }

    @OnClick({R.id.btn_close, R.id.btn_print, R.id.tv_custSel, R.id.btn_boxConfirm, R.id.tv_deliverSel, R.id.btn_clone, R.id.tv_pickingListSel, R.id.btn_save, R.id.tv_box})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_print: // 打印条码界面
                show(PrintMainActivity.class, null);

                break;
            case R.id.tv_custSel: // 选择客户
                showForResult(Cust_DialogActivity.class, SEL_CUST, null);

                break;
            case R.id.tv_deliverSel: // 发货方式
                showForResult(DeliveryWay_DialogActivity.class, SEL_DELI, null);

                break;
            case R.id.tv_box: // 选择箱子
                showForResult(Box_DialogActivity.class, SEL_BOX, null);

                break;
            case R.id.btn_boxConfirm: // 确认箱子
                if(getValues(tvBox).length() == 0) {
                    Comm.showWarnDialog(context,"请选择箱子！");
                    return;
                }
                run_modifyBoxIdByBarcode();

                break;
            case R.id.tv_pickingListSel: // 选择拣货单
                if(boxBarCode == null) {
                    Comm.showWarnDialog(context, "请扫箱码！");
                    return;
                }
                bundle = new Bundle();
                bundle.putString("pickingType","1");
                showForResult(Sal_SelPickingListActivity.class, SEL_PICKINGLIST, bundle);

                break;
            case R.id.btn_save: // 保存
                if(boxBarCode == null) {
                    Comm.showWarnDialog(context,"请先扫描箱码！");
                    return;
                }
                if(mbrList == null || mbrList.size() == 0) {
                    Comm.showWarnDialog(context,"箱子里还没有物料不能封箱！");
                    return;
                }
                status = '2';
                int flag = saveBefore();
                switch (flag) {
                    case 0: // 直接保存
                        // 把对象转成json字符串
                        String strJson = JsonUtil.objectToString(mbrList);
                        run_save(strJson);

                        break;
                    case -1: // 数据拦截提示
                        break;
                    default: // 数据保存询问提示
                        AlertDialog.Builder build = new AlertDialog.Builder(context);
                        build.setIcon(R.drawable.caution);
                        build.setTitle("系统提示");
                        build.setMessage("第"+(flag)+"行，复核数小于拣货数，是否继续保存？");
                        build.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 把对象转成json字符串
                                String strJson = JsonUtil.objectToString(mbrList);
                                run_save(strJson);
                            }
                        });
                        build.setNegativeButton("取消保存", null);
                        build.setCancelable(false);
                        build.show();

                        break;
                }

                break;
            case R.id.btn_clone: // 新装
                reset();

                break;
        }
    }

    @OnLongClick({R.id.btn_close})
    public boolean onViewLongClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 测试打印
                if(isConnected) {
                    sendLabel();
                } else {
                    // 打开蓝牙配对页面
                    startActivityForResult(new Intent(context, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
                }

                break;
        }
        return true;
    }

    /**
     * 选择保存之前的判断
     */
    private int saveBefore() {
        if (mbrList == null || mbrList.size() == 0) {
            Comm.showWarnDialog(context,"您还没有选择数据！");
            return -1;
        }

        // 检查数据
        for (int i = 0, size = mbrList.size(); i < size; i++) {
            MaterialBinningRecord mbr = mbrList.get(i);
            if (mbr.getNumber() == 0) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（复合数）必须大于0！");
                return -1;
            }
            if (mbr.getNumber() < mbr.getRelationBillFQTY()) { // 提示复核数量小于拣货单数量
                return (i+1);
            }
            if (mbr.getNumber() > mbr.getRelationBillFQTY()) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（复合数）不能大于（拣货数）！");
                return -1;
            }
        }
        return 0;
    }

    /**
     * 重置
     */
    private void reset() {
        etBoxCode.setText("");
        boxBarCode = null;
        boxBarcode = null;
        mtlBarcode = null;
        tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
        tvBoxName.setText("");
        tvBoxSize.setText("");
        tvBoxLength.setText("");
        tvBoxWidth.setText("");
        tvBoxAltitude.setText("");
        tvBoxVolume.setText("");
        etMtlCode.setText("");
        tvCustSel.setText("");
        tvPickingListSel.setText("");
        setEnables(tvPickingListSel, R.drawable.back_style_blue, true);
//        setEnables(tvCustSel, R.drawable.back_style_blue, true);
//        customer = null;
        tvDeliverSel.setText("");
//        setEnables(tvDeliverSel, R.drawable.back_style_blue, true);
        tvCount.setText("物料数量：0");

        curViewFlag = '1';

        mbrList.clear();
        mAdapter.notifyDataSetChanged();
        setFocusable(etBoxCode);
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
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_boxCode: // 箱码
                            String boxCode = getValues(etBoxCode).trim();
                            if (boxBarcode != null && boxBarcode.length() > 0) {
                                if (boxBarcode.equals(boxCode)) {
                                    boxBarcode = boxCode;
                                } else {
                                    String tmp = boxCode.replaceFirst(boxBarcode, "");
                                    boxBarcode = tmp.replace("\n", "");
                                }
                            } else {
                                boxBarcode = boxCode.replace("\n", "");
                            }
                            curViewFlag = '1';
                            // 执行查询方法
                            run_smGetDatas(boxBarcode);

                            break;
                        case R.id.et_mtlCode: // 物料
                            String mtlCode = getValues(etMtlCode).trim();
                            if (mtlBarcode != null && mtlBarcode.length() > 0) {
                                if (mtlBarcode.equals(mtlCode)) {
                                    mtlBarcode = mtlCode;
                                } else {
                                    String tmp = mtlCode.replaceFirst(mtlBarcode, "");
                                    mtlBarcode = tmp.replace("\n", "");
                                }
                            } else {
                                mtlBarcode = mtlCode.replace("\n", "");
                            }
                            curViewFlag = '2';
                            if (getValues(tvPickingListSel).length() == 0) {
                                mHandler.sendEmptyMessageDelayed(CODE1, 200);
                                Comm.showWarnDialog(context,"请选择拣货单！");
                                return false;
                            }
                            // 执行查询方法
                            run_smGetDatas(mtlBarcode);

                            break;
                    }
                }

                return false;
            }
        };
        etBoxCode.setOnKeyListener(keyListener);
        etMtlCode.setOnKeyListener(keyListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_CUST: //查询客户	返回
                if (resultCode == RESULT_OK) {
                    customer = (Customer) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_CUST", customer.getCustomerName());
                    if (customer != null) {
                        tvCustSel.setText(customer.getCustomerName());
                    }
                }

                break;
            case SEL_DELI: //查询发货方式	返回
                if (resultCode == RESULT_OK) {
                    assist = (AssistInfo) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DELI", assist.getfName());
                    if (assist != null) {
                        tvDeliverSel.setText(assist.getfName());
                    }
                }

                break;
            case SEL_BOX: //查询箱子	返回
                if (resultCode == RESULT_OK) {
                    box = (Box) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_BOX", box.getBoxName());
                    if (box != null) {
                        tvBox.setText(box.getBoxName());
                        boxBarCode.setBoxId(box.getId());
                        boxBarCode.setBox(box);
                    }
                }

                break;
            case SEL_NUM: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double number = parseDouble(value);
                        mbrList.get(curPos).setNumber(number);
                        mAdapter.notifyDataSetChanged();
                        isRecombinationEnd();
                    }
                }

                break;
            case SEL_PICKINGLIST: //查询拣货单	返回
                if (resultCode == RESULT_OK) {
                    mbrList.clear();
                    plList.clear();
                    List<PickingList> list = (List<PickingList>) data.getSerializableExtra("checkDatas");
                    plList.addAll(list);

                    PickingList p = list.get(0);
                    tvPickingListSel.setText(p.getFbillno());
                    tvCustSel.setText(p.getCustName());
                    tvDeliverSel.setText(p.getDeliveryWay());
                    tvCount.setText("物料数量："+list.size());
                    tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                    getPickingList(list);
                    setFocusable(etMtlCode);
                    mAdapter.notifyDataSetChanged();
                }

                break;
            /*蓝牙连接*/
            case Constant.BLUETOOTH_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    isPair = true;
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
                }
                if(!isPair) {
                    // 打开蓝牙配对页面
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivityForResult(new Intent(context, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
                        }
                    },500);

                }
                break;
            }
        }
    }

    /**
     * 扫描（箱码）返回
     */
    private void getBox() {
        if(boxBarCode != null) {
            mbrList.clear();
            setTexts(etBoxCode, boxBarCode.getBarCode());
            boxBarcode = boxBarCode.getBarCode();
            // 箱子为空提示选择
            if(boxBarCode.getBox() == null) {
                linBox.setVisibility(View.VISIBLE);
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                tvBoxName.setText("");
                tvBoxSize.setText("");
                tvBoxLength.setText("");
                tvBoxWidth.setText("");
                tvBoxAltitude.setText("");
                tvBoxVolume.setText("");
                Comm.showWarnDialog(context,"请选择包装箱！");
                return;
            }

            // 把箱子里的物料显示出来
            if(boxBarCode.getMtlBinningRecord() != null && boxBarCode.getMtlBinningRecord().size() > 0) {
                tvCount.setText("物料数量："+boxBarCode.getMtlBinningRecord().size());
//                mbrList.addAll(boxBarCode.getMtlBinningRecord());

                MaterialBinningRecord mtlbr = boxBarCode.getMtlBinningRecord().get(0);
                // 固定当前是无源单还是有源单
                if(mtlbr.getRelationBillId() == 0) {
//                    tabSelected(viewRadio1);
//                    dataType = '1';
                } else {
//                    tabSelected(viewRadio2);
////                    dataType = '2';
                }
                // 显示当前的客户
                if(customer == null) customer = new Customer();
                customer.setFcustId(mtlbr.getCustomerId());
                customer.setCustomerName(mtlbr.getCustomer().getCustomerName());
                tvCustSel.setText(mtlbr.getCustomer().getCustomerName());
                // 显示交货方式
                if(assist == null) assist = new AssistInfo();
                assist.setfName(mtlbr.getDeliveryWay());
                tvDeliverSel.setText(mtlbr.getDeliveryWay());

            } else {
                tvCount.setText("物料数量：0");
            }
            int status = boxBarCode.getStatus();
            if(status == 0) {
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                setEnables(tvPickingListSel, R.drawable.back_style_blue, true);
                setEnables(etMtlCode, R.drawable.back_style_blue, true);
//                setFocusable(etMtlCode);
            } else if(status == 1) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                setEnables(tvPickingListSel, R.drawable.back_style_blue, true);
                setEnables(etMtlCode, R.drawable.back_style_blue, true);
//                setFocusable(etMtlCode);
            } else if(status == 2) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
                setEnables(tvPickingListSel, R.drawable.back_style_gray3, false);
                setEnables(etMtlCode, R.drawable.back_style_gray3, false);
            }


            tvBoxName.setText(boxBarCode.getBox().getBoxName());
            tvBoxSize.setText(boxBarCode.getBox().getBoxSize());
            tvBoxLength.setText(df.format(boxBarCode.getBox().getLength()));
            tvBoxWidth.setText(df.format(boxBarCode.getBox().getWidth()));
            tvBoxAltitude.setText(df.format(boxBarCode.getBox().getAltitude()));
            tvBoxVolume.setText(df.format(boxBarCode.getBox().getVolume()));

            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 得到拣货单列表
     */
    private void getPickingList(List<PickingList> list) {
        for(int i=0, size=list.size(); i<size; i++) {
            PickingList pl = list.get(i);
            MaterialBinningRecord mbr = new MaterialBinningRecord();
            mbr.setId(0);
            mbr.setFbillType(3); // 单据类型（1：生产装箱，2：销售装箱，3：拣货单）
            mbr.setBoxBarCodeId(boxBarCode.getId());
            mbr.setMaterialId(pl.getMtlId());
            mbr.setMtl(pl.getMtl());
            mbr.setBarcode(pl.getBarcode());
            mbr.setBatchCode(pl.getBatchNo());
            mbr.setBarcodeSource('1');
            mbr.setSnCode(pl.getSnNo());
            mbr.setNumber(0);
            mbr.setRelationBillId(pl.getfId());
            mbr.setRelationBillNumber(pl.getFbillno());
            mbr.setCustomerId(pl.getCustId());
            mbr.setCustomerNumber(pl.getCustNumber());
            mbr.setDeliveryWay(pl.getDeliveryWay());
            mbr.setPackageWorkType(2);
            mbr.setBinningType('3');
            mbr.setCaseId(37);
            mbr.setRelationBillFQTY(pl.getPickingListNum());
            mbr.setEntryId(pl.getEntryId());
            mbr.setUsableFqty(pl.getUsableFqty());
            mbr.setCreateUserId(user.getId());
            mbr.setCreateUserName(user.getUsername());
            mbr.setModifyUserId(user.getId());
            mbr.setModifyUserName(user.getUsername());
            mbr.setSalOrderNo(pl.getSalOrderNo());
            mbr.setSalOrderNoEntryId(pl.getSalOrderNoEntryId());
            // 物料是否启用序列号
            if(pl.getMtl().getIsSnManager() == 1) {
                mbr.setListBarcode(new ArrayList<String>());
            }
            mbr.setStrBarcodes("");

            mbrList.add(mbr);
        }
    }

    /**
     * 选择（物料）返回的值
     */
    private void getMaterialAfter(BarCodeTable bt) {
        if(bt != null) {
            setTexts(etMtlCode, mtlBarcode);
        }
        Material tmpMtl = bt.getMtl();
        int size = mbrList.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            MaterialBinningRecord mbr = mbrList.get(i);
            // 如果扫码相同
            if (bt.getMaterialId() == mbr.getMaterialId()) {
                isFlag = true;

                double fqty = 1;
                // 计量单位数量
                if(tmpMtl.getCalculateFqty() > 0) fqty = tmpMtl.getCalculateFqty();
                // 未启用序列号
                if (tmpMtl.getIsSnManager() == 0) {
                    // 如果拣货数大于复核数
                    if (mbr.getRelationBillFQTY() > mbr.getNumber()) {
                        // 如果扫的是物料包装条码，就显示个数
                        double number = 0;
                        if(bt != null) number = bt.getMaterialCalculateNumber();

                        if(number > 0) {
                            mbr.setNumber(mbr.getNumber()+(number*fqty));
                        } else {
                            mbr.setNumber(mbr.getNumber() + fqty);
                        }
                    } else {
                        // 数量已满
                        Comm.showWarnDialog(context, "第" + (i + 1) + "行，复核数不能大于拣货数！");
                        return;
                    }

                } else { // 启用序列号
                    if (mbr.getRelationBillFQTY() == mbr.getNumber()) {
                        Comm.showWarnDialog(context, "第" + (i + 1) + "行，已复核完！");
                        return;
                    }
                    List<String> list = mbr.getListBarcode();
                    if(list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(context,"该物料条码存在复核行中，请扫描未使用过的条码！");
                        return;
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
                        if((k+1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k)+",");
                    }
                    mbr.setListBarcode(list);
                    mbr.setStrBarcodes(sb.toString());
                    mbr.setNumber(mbr.getNumber() + 1);

                }
                mAdapter.notifyDataSetChanged();
                isRecombinationEnd();
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(context, "扫的物料与订单不匹配！");
        }
    }

    /**
     * 是否已经捡完货
     */
    private void isRecombinationEnd() {
        int size = mbrList.size();
        int count = 0; // 计数器
        for(int i=0; i<size; i++) {
            MaterialBinningRecord p = mbrList.get(i);
            if(p.getNumber() >= p.getUsableFqty()) {
                count += 1;
            }
        }
        if(count == size) {
            toasts("全部复核完了，请保存！");
        }
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas(String val) {
        if(val.length() == 0) {
            Comm.showWarnDialog(context,"请对准条码！");
            return;
        }
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        switch (curViewFlag) {
            case '1': // 箱码
                mUrl = Consts.getURL("boxBarCode/findBarcode");
                barcode = boxBarcode;
                strCaseId = "";
                break;
            case '2': // 物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                strCaseId = "11,21";
                break;
        }
        String boxId = boxBarCode != null ? String.valueOf(boxBarCode.getBoxId()) : "";
        FormBody formBody = new FormBody.Builder()
                .add("boxId", boxId)
                .add("barcode", barcode)
                .add("strCaseId", strCaseId)
                .add("sourceType","9") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
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
                Log.e("run_smGetDatas --> onResponse", result);
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
     * 保存的方法
     */
    private void run_save(String json) {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("materialBinningRecord/save3");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("strJson", json)
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
                mHandler.sendEmptyMessage(UNSAVE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSAVE);
                    return;
                }
                Message msg = mHandler.obtainMessage(SAVE, result);
                Log.e("run_save --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 修改发货方式
     */
    private void run_modifyWay() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("materialBinningRecord/modifyDeliveryway");
        MaterialBinningRecord mtl = new MaterialBinningRecord();
        getUserInfo();

        FormBody formBody = new FormBody.Builder()
                .add("boxBarCodeId", String.valueOf(boxBarCode.getId()))
                .add("deliveryWay", getValues(tvDeliverSel))
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
                mHandler.sendEmptyMessage(UNMODIFY2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY2);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY2, result);
                Log.e("run_modifyWay --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 删除的方法
     */
    private void run_delete(String json) {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("materialBinningRecord/delete");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("strJson", json)
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
                mHandler.sendEmptyMessage(UNDELETE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNDELETE, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(DELETE, result);
                Log.e("run_delete --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 开箱或者封箱
     */
    private void run_modifyStatus() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("boxBarCode/modifyStatus");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(boxBarCode.getId()))
                .add("status", String.valueOf(status))
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
                mHandler.sendEmptyMessage(UNMODIFY);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY, result);
                Log.e("run_modifyStatus --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 修改对应的箱子
     */
    private void run_modifyBoxIdByBarcode() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("boxBarCode/modifyBoxIdByBarcode");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("boxId", String.valueOf(boxBarCode.getBoxId()))
                .add("barCode", boxBarCode.getBarCode())
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
                mHandler.sendEmptyMessage(UNMODIFY3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY3);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY3, result);
                Log.e("run_modifyBoxIdByBarcode --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 修改数量（不叠加）
     */
    private void run_modifyNumber2(int id, final double number) {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("materialBinningRecord/modifyNumber2");
        MaterialBinningRecord mtl = new MaterialBinningRecord();
        getUserInfo();

        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("number", String.valueOf(number))
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
                mHandler.sendEmptyMessage(UNMODIFY_NUM);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY_NUM);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY_NUM, number);
                Log.e("run_modifyNumber2 --> onResponse", result);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeHandler(mHandler);
            context.finish();
        }
        return false;
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

        PickingList pl = plList.get(0);
        // 绘制箱子条码
        rowHigthSum = beginYPos + 20;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"箱码： \n");
        tsc.add1DBarcode(115, rowHigthSum-20, LabelCommand.BARCODETYPE.CODE39, 75, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, boxBarCode.getBarCode());
        rowHigthSum = beginYPos + 106;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物流公司："+isNULLS(pl.getDeliveryCompanyName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"客户名称："+isNULLS(pl.getCustName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单编号："+isNULLS(pl.getSalOrderNo())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单日期："+isNULLS(pl.getDeliDate()).substring(0,10)+" \n");
        rowHigthSum = rowHigthSum + 30;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"------------------------------------------------- \n");
        for(int i=0; i<plList.size(); i++) {
            PickingList pl2 = plList.get(i);
            MaterialBinningRecord mbr2 = mbrList.get(i);
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料编码："+isNULLS(pl2.getMtlFnumber())+" \n");
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料名称："+isNULLS(pl2.getMtlFname())+" \n");
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"数量："+df.format(mbr2.getNumber())+" \n");
            tsc.addText(260, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"单位："+isNULLS(pl2.getMtlUnitName())+" \n");
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
        isPair = false;
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        int initHigt = 50;
        int high = plList.size() * 20;
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
        saveAfter();
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
        closeHandler(mHandler);
        super.onDestroy();
        DeviceConnFactoryManager.closeAllPort();
        if (threadPool != null) {
            threadPool.stopThreadPool();
        }
    }

}
