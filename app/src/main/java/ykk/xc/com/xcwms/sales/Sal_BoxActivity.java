package ykk.xc.com.xcwms.sales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import ykk.xc.com.xcwms.basics.Cust_DialogActivity;
import ykk.xc.com.xcwms.basics.DeliveryWay_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.AssistInfo;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.MaterialBinningRecord;
import ykk.xc.com.xcwms.model.sal.BoxBarCode;
import ykk.xc.com.xcwms.model.sal.SalOrder;
import ykk.xc.com.xcwms.sales.adapter.Sal_BoxAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;

public class Sal_BoxActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lin_tab1)
    LinearLayout linTab1;
    @BindView(R.id.lin_tab2)
    LinearLayout linTab2;
    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
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
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_del)
    Button btnDel;
    @BindView(R.id.btn_unSave)
    Button btnUnSave;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.rb_type1)
    RadioButton rbType1;
    @BindView(R.id.rb_type2)
    RadioButton rbType2;

    
    private Sal_BoxActivity context = this;
    private static final int SEL_CUST = 11, SEL_DELI = 12, SEL_NUM = 13;
    private static final int SUCC1 = 201, UNSUCC1 = 501, SAVE = 202, UNSAVE = 502, DELETE = 203, UNDELETE = 503, MODIFY = 204, UNMODIFY = 504;
    private static final int CODE1 = 1, CODE2 = 2, CODE60 = 60;
    private Customer customer; // 客户
    private BoxBarCode boxBarCode; // 箱码表
    private Sal_BoxAdapter mAdapter;
    private char dataType = '1'; // 1：无源单，2：来源单
    private String strBoxBarcode, strMtlBarcode, strMtlBarcode_del; // 对应的条码号
    private List<MaterialBinningRecord> listMtl = new ArrayList<>();
    private char curViewFlag = '1'; // 1：箱子，2：物料
    private DecimalFormat df = new DecimalFormat("#.####");
    private int curPos; // 当前行
    private View curRadio; // 当前来源View
    private AssistInfo assist; // 辅助资料--发货方式
    private AlertDialog delDialog;
    private EditText etMtlCode2;
    private CheckBox checkClose;
    private boolean isCloseDelDialog = true; // 是否关闭删除的Dialog
    private char status = '0'; // 箱子状态
    private char binningType = '1'; // 1.单色装，2.混色装
    private OkHttpClient okHttpClient = new OkHttpClient();

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_BoxActivity> mActivity;

        public MyHandler(Sal_BoxActivity activity) {
            mActivity = new WeakReference<Sal_BoxActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_BoxActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 扫码成功后进入
                        switch (m.curViewFlag) {
                            case '1': // 箱码扫码   返回
                                m.boxBarCode = JsonUtil.strToObject((String) msg.obj, BoxBarCode.class);
                                m.linTab1.setEnabled(false);
                                m.linTab2.setEnabled(false);
                                m.getBox();

                                break;
                            case '2': // 物料扫码   返回
                                BarCodeTable barCodeTable = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);

                                m.getBarCodeTableAfter(); // 禁用一些控件
                                // 无源单和来源的分支
                                if(barCodeTable.getRelationBillId() == null || barCodeTable.getRelationBillId() == 0) {
                                    m.getBarCodeTable(barCodeTable);
                                } else {
                                    m.getBarCodeTable2(barCodeTable);
                                }

                                break;
                            case '3': // 删除物料扫码     返回
                                BarCodeTable barCodeTable2 = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.getBarCodeTable_delete(barCodeTable2);

                                break;
                        }

                        break;
                    case UNSUCC1:
                        m.mHandler.sendEmptyMessageDelayed(CODE60, 200);
                        Comm.showWarnDialog(m.context, "很抱歉，没能找到数据！");


                        break;
                    case SAVE: // 扫描后的保存 成功
                        m.status = '1';
                        m.listMtl.clear();
                        List<MaterialBinningRecord> list = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                        m.listMtl.addAll(list);
                        m.tvCount.setText("物料数量："+m.listMtl.size());
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSAVE: // 扫描后的保存 失败
                        m.mHandler.sendEmptyMessageDelayed(CODE60, 200);
                        m.mAdapter.notifyDataSetChanged();
                        Log.e("执行保存...","服务器繁忙，请稍候再试");
//                        Comm.showWarnDialog(m.context, "很抱歉，没能找到数据！");


                        break;
                    case DELETE: // 删除 成功
                        m.listMtl.clear();
                        List<MaterialBinningRecord> list2 = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                        m.listMtl.addAll(list2);
                        m.mAdapter.notifyDataSetChanged();
                        // 如果点击了关闭窗口的复选框
                        m.setCloseDelDialog();

                        break;
                    case UNDELETE: // 删除 失败
                        String str = (String) msg.obj;
                        if(str != null) {
                            String size = JsonUtil.strToString((String) msg.obj);
                            if(m.parseInt(size) == 0) {
                                m.listMtl.clear();
                                m.initDataSon(true);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#000000'>未开箱</font>"));
                                m.tvCount.setText("物料数量：0");
                            }
                        }
                        m.setCloseDelDialog();
                        m.mAdapter.notifyDataSetChanged();
                        Log.e("执行删除...","服务器繁忙，请稍候再试");
//                        Comm.showWarnDialog(m.context, "服务器繁忙，请稍候再试！");

                        break;
                    case MODIFY: // 修改 成功
                        switch (m.status) {
                            case '1': // 开箱
                                m.setEnables(m.etMtlCode,R.drawable.back_style_blue,true);
                                m.setFocusable(m.etMtlCode);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                                break;
                            case '2': // 封箱
                                m.setEnables(m.etMtlCode,R.drawable.back_style_gray3,false);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));

                                break;
                        }

                        break;
                    case UNMODIFY: // 修改 失败
                        Comm.showWarnDialog(m.context,"服务器忙，请稍候操作！");

                        break;
                    case CODE1: // 清空数据
                        m.etMtlCode.setText("");
                        m.strMtlBarcode = "";

                        break;
                    case CODE2: // Dialog默认得到焦点，隐藏软键盘
                        m.hideSoftInputMode(m.etMtlCode2);
                        m.setFocusable(m.etMtlCode2);

                        break;
                    case CODE60: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 箱码扫码
                                m.setTexts(m.etBoxCode, m.strBoxBarcode);
                                break;
                            case '2': // 物料扫码
                                m.setTexts(m.etMtlCode, m.strMtlBarcode);
                                break;
                            case '3': // 删除扫码
                                m.setTexts(m.etMtlCode2, m.strMtlBarcode_del);
                                break;
                        }

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.sal_box;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Sal_BoxAdapter(context, listMtl);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Sal_BoxAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, MaterialBinningRecord entity, int position) {
                Log.e("num", "行：" + position);
//                curPos = position;
//                showInputDialog("数量", String.valueOf(entity.getNumber()), "0", SEL_NUM);
            }
        });
    }

    @Override
    public void initData() {
        initDataSon(false);
        hideSoftInputMode(etBoxCode);
        hideSoftInputMode(etMtlCode);
        curRadio = viewRadio1;
    }

    /**
     *
     */
    private void initDataSon(boolean enable) {
        if(enable) {
            setEnables(etMtlCode, R.drawable.back_style_blue, true);
            setEnables(tvCustSel, R.drawable.back_style_blue, true);
            setEnables(tvDeliverSel, R.drawable.back_style_blue, true);
        } else {
            setEnables(etMtlCode, R.drawable.back_style_gray3, false);
            setEnables(tvCustSel, R.drawable.back_style_gray3, false);
            setEnables(tvDeliverSel, R.drawable.back_style_gray3, false);
        }
    }

    @OnClick({R.id.lin_tab1, R.id.lin_tab2, R.id.btn_close, R.id.tv_custSel, R.id.tv_deliverSel, R.id.btn_clone, R.id.btn_del, R.id.btn_unSave, R.id.btn_save, R.id.rb_type1, R.id.rb_type2})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.lin_tab1:
                dataType = '1';
                tabSelected(viewRadio1);
                tvTitle.setText("产品装箱-无源单");

                break;
            case R.id.lin_tab2:
                dataType = '2';
                tabSelected(viewRadio2);
                tvTitle.setText("产品装箱-有源单");

                break;
            case R.id.tv_custSel: // 选择客户
                showForResult(Cust_DialogActivity.class, SEL_CUST, null);

                break;
            case R.id.tv_deliverSel: // 发货方式
                showForResult(DeliveryWay_DialogActivity.class, SEL_DELI, null);

                break;
            case R.id.btn_del: // 删除
                if(listMtl != null && listMtl.size() > 0) {
                    deleteRowDialog();
                } else {
                    Comm.showWarnDialog(context,"箱子中没有物料，不能删除！");
                }

                break;
            case R.id.btn_unSave: // 解封
                if(boxBarCode == null) {
                    Comm.showWarnDialog(context,"请先扫描箱码！");
                    return;
                }
                status = '1';
                run_modifyStatus();

                break;
            case R.id.btn_save: // 封箱保存
                if(boxBarCode == null) {
                    Comm.showWarnDialog(context,"请先扫描箱码！");
                    return;
                }
                if(listMtl == null || listMtl.size() == 0) {
                    Comm.showWarnDialog(context,"箱子里还没有物料不能封箱！");
                    return;
                }
                status = '2';
                run_modifyStatus();

                break;
            case R.id.btn_clone: // 清空
                reset();

                break;
            case R.id.rb_type1: // 装箱类型--单装
                binningType = '1';
                clickRadioChange();

                break;
            case R.id.rb_type2: // 装箱类型--混装
                binningType = '2';
                clickRadioChange();

                break;
        }
    }

    /**
     * 重置
     */
    private void reset() {
        tabSelected(viewRadio1);
        tvTitle.setText("产品装箱-无源单");
        linTab1.setEnabled(true);
        linTab2.setEnabled(true);
        etBoxCode.setText("");
        boxBarCode = null;
        tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
        tvBoxName.setText("");
        tvBoxSize.setText("");
        tvBoxLength.setText("");
        tvBoxWidth.setText("");
        tvBoxAltitude.setText("");
        tvBoxVolume.setText("");
        etMtlCode.setText("");
//        tvCustSel.setText("");
        setEnables(tvCustSel, R.drawable.back_style_blue, true);
//        customer = null;
//        tvDeliverSel.setText("");
        setEnables(tvDeliverSel, R.drawable.back_style_blue, true);
        tvCount.setText("物料数量：0");
        rbType1.setEnabled(true);
        rbType2.setEnabled(true);
        rbType1.setChecked(true);

        dataType = '1';
        curViewFlag = '1';

        listMtl.clear();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (listMtl == null || listMtl.size() == 0) {
            Comm.showWarnDialog(context, "请先插入行！");
            return false;
        }

        return true;
    }

    /**
     * 点击装箱类型后改变的
     */
    private void clickRadioChange() {
        rbType1.setTextColor(Color.parseColor(rbType1.isChecked() ? "#FFFFFF" : "#666666"));
        rbType2.setTextColor(Color.parseColor(rbType2.isChecked() ? "#FFFFFF" : "#666666"));
    }

    /**
     * 删除行的dialog
     */
    private void deleteRowDialog() {
        View v = context.getLayoutInflater().inflate(R.layout.sal_box_item_del_dialog, null);
        delDialog = new AlertDialog.Builder(context).setView(v).create();
        // 初始化id
        etMtlCode2 = (EditText) v.findViewById(R.id.et_mtlCode2);
        Button btnClose = (Button) v.findViewById(R.id.btn_close);
        checkClose = v.findViewById(R.id.check_close);
        checkClose.setChecked(isCloseDelDialog);
        mHandler.sendEmptyMessageDelayed(CODE2,200);

        // 关闭
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClose.setChecked(true);
                setCloseDelDialog();
            }
        });
        etMtlCode2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    String mtlCode = getValues(etMtlCode2).trim();
                    if (isKeyDownEnter(mtlCode, event, keyCode)) {
                        if (strMtlBarcode_del != null && strMtlBarcode_del.length() > 0) {
                            String tmp = mtlCode.replaceFirst(strMtlBarcode_del, "");
                            strMtlBarcode_del = tmp.replace("\n", "");
                        } else {
                            strMtlBarcode_del = mtlCode.replace("\n", "");
                        }
                        curViewFlag = '3';
                        // 执行查询方法
                        run_smGetDatas();
                    }
                }

                return false;
            }
        });

        Window window = delDialog.getWindow();
        delDialog.setCancelable(false);
        delDialog.show();
        window.setGravity(Gravity.CENTER);
    }

    /**
     * 删除扫码的时候是否关闭窗口
     */
    private void setCloseDelDialog() {
        // 如果点击了关闭窗口的复选框
        if(checkClose.isChecked()) {
            setFocusable(etMtlCode);
            etMtlCode2.setOnKeyListener(null);
            etMtlCode2 = null;
            delDialog.dismiss();
            delDialog = null;
            isCloseDelDialog = true;
        } else {
            isCloseDelDialog = false;
            mHandler.sendEmptyMessageDelayed(CODE60, 200);
        }
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (v.getId()) {
                    case R.id.et_boxCode: // 箱码
                        if(event.getAction() == KeyEvent.ACTION_DOWN) {
                            String boxCode = getValues(etBoxCode).trim();
                            if (isKeyDownEnter(boxCode, event, keyCode)) {
                                if (strBoxBarcode != null && strBoxBarcode.length() > 0) {
                                    String tmp = boxCode.replaceFirst(strBoxBarcode, "");
                                    strBoxBarcode = tmp.replace("\n", "");
                                } else {
                                    strBoxBarcode = boxCode.replace("\n", "");
                                }
                                curViewFlag = '1';
                                // 执行查询方法
                                run_smGetDatas();
                            }
                        }

                        break;
                    case R.id.et_mtlCode: // 物料
                        if(event.getAction() == KeyEvent.ACTION_DOWN) {
                            String mtlCode = getValues(etMtlCode).trim();
                            if (!smMtlBefore()) {
                                mHandler.sendEmptyMessageDelayed(CODE1,200);
                                return false;
                            }
                            if (isKeyDownEnter(mtlCode, event, keyCode)) {
                                if (strMtlBarcode != null && strMtlBarcode.length() > 0) {
                                    String tmp = mtlCode.replaceFirst(strMtlBarcode, "");
                                    strMtlBarcode = tmp.replace("\n", "");
                                } else {
                                    strMtlBarcode = mtlCode.replace("\n", "");
                                }
                                curViewFlag = '2';
                                // 执行查询方法
                                run_smGetDatas();
                            }
                        }

                        break;
                }
                return false;
            }
        };
        etBoxCode.setOnKeyListener(keyListener);
        etMtlCode.setOnKeyListener(keyListener);

    }

    /**
     * 扫码物料之前的判断
     */
    private boolean smMtlBefore() {
        if (customer == null) {
            Comm.showWarnDialog(context,"请选择客户！");
            return false;
        }
        if (assist == null) {
            Comm.showWarnDialog(context,"请选择发货方式！");
            return false;
        }
        return true;
    }

    /**
     * 是否按了回车键
     */
    private boolean isKeyDownEnter(String val, KeyEvent event, int keyCode) {
        if (val.length() > 0 && event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            return true;
        }
        return false;
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
            case SEL_NUM: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        listMtl.get(curPos).setNumber(parseDouble(value));
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /**
     * 扫描（箱码）返回
     */
    private void getBox() {
        if(boxBarCode != null) {
            listMtl.clear();
            setTexts(etBoxCode, boxBarCode.getBox().getBoxName());
            strBoxBarcode = boxBarCode.getBox().getBoxName();
            // 把箱子里的物料显示出来
            if(boxBarCode.getMtlBinningRecord() != null && boxBarCode.getMtlBinningRecord().size() > 0) {
                tvCount.setText("物料数量："+boxBarCode.getMtlBinningRecord().size());
                listMtl.addAll(boxBarCode.getMtlBinningRecord());

                MaterialBinningRecord mtlbr = boxBarCode.getMtlBinningRecord().get(0);
                // 固定当前是无源单还是有源单
                if(mtlbr.getRelationBillId() == null || mtlbr.getRelationBillId() == 0) {
                    tabSelected(viewRadio1);
                } else {
                    tabSelected(viewRadio2);
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
                binningType = mtlbr.getBinningType();
                // 自动选中对应类型
                if(binningType == '1') rbType1.setChecked(true);
                else rbType2.setChecked(true);
                clickRadioChange();

                rbType1.setEnabled(false);
                rbType2.setEnabled(false);

            } else {
                initDataSon(true);
                tvCount.setText("物料数量：0");
            }
            int status = boxBarCode.getStatus();
            if(status == 0) {
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                setEnables(etMtlCode, R.drawable.back_style_blue, true);
                setFocusable(etMtlCode);
            } else if(status == 1) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                setEnables(etMtlCode, R.drawable.back_style_blue, true);
                setFocusable(etMtlCode);
            } else if(status == 2) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
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
     * （条码表 无源单）返回的值
     */
    private void getBarCodeTable(BarCodeTable barCodeTable) {
        if (barCodeTable != null) {
            setTexts(etMtlCode, barCodeTable.getMaterialNumber());
            strMtlBarcode = barCodeTable.getMaterialNumber();
            int size = listMtl.size();
            tvCount.setText("物料数量："+size);

            MaterialBinningRecord tmpMtl = null;
            // 已装箱的物料
            if(size > 0) {
                // 相同的物料就+1，否则为1
                for(int i=0; i<size; i++) {
                    MaterialBinningRecord forMtl = listMtl.get(i);
                    if(barCodeTable.getMaterialId().equals(forMtl.getMaterialId())){
                        if((forMtl.getNumber()+1) <= barCodeTable.getMtlPack().getNumber()) {
                            tmpMtl = forMtl;

                            break;
                        } else {
                            Comm.showWarnDialog(context,"该物料已经装满");

                            return;
                        }
                    } else {
                        if(binningType == '1') {
                            Comm.showWarnDialog(context,"单色装只能装一种物料！");
                            return;
                        }
                    }
                }
            }
            if(tmpMtl == null) {
                tmpMtl = new MaterialBinningRecord();
                tmpMtl.setId(0);
                tmpMtl.setBoxBarCodeId(boxBarCode.getId());
                tmpMtl.setMaterialId(barCodeTable.getMaterialId());
                tmpMtl.setRelationBillId(barCodeTable.getRelationBillId());
                tmpMtl.setRelationBillNumber(barCodeTable.getRelationBillNumber());
                tmpMtl.setCustomerId(customer.getFcustId());
                if(assist != null) {
                    tmpMtl.setDeliveryWay(assist.getfName());
                }
                tmpMtl.setPackageWorkType(2);
                tmpMtl.setBinningType(binningType);

            }
            tmpMtl.setNumber(1);
            // 把对象转成json字符串
            String strJson = JsonUtil.objectToString(tmpMtl);
            // 添加到箱子并返回箱子的中的物料列表
            run_save(strJson);
        }
    }

    /**
     * （条码表 有源单）返回的值
     */
    private void getBarCodeTable2(BarCodeTable barCodeTable) {
        if (barCodeTable != null) {
            setTexts(etMtlCode, barCodeTable.getMaterialNumber());
            strMtlBarcode = barCodeTable.getMaterialNumber();
            int size = listMtl.size();
            tvCount.setText("物料数量："+size);

            MaterialBinningRecord tmpMtl = null;
            SalOrder salOrder = null;
            boolean orderIsnull = false; // 销售订单是否为空
            // 得到销售订单
            if(barCodeTable.getRelationObj() != null && barCodeTable.getRelationObj().length() > 0) {
                salOrder = JsonUtil.stringToObject(barCodeTable.getRelationObj(), SalOrder.class);
            }
            orderIsnull = salOrder == null;
            // 已装箱的物料
            if(size > 0) {


                MaterialBinningRecord mtl2 = listMtl.get(0);
                if(orderIsnull && mtl2.getCustomerId() != salOrder.getCustId()) {
                    Comm.showWarnDialog(context, "客户不一致，不能装箱！");
                    return;
                }
                if(orderIsnull && mtl2.getDeliveryWay().length() > 0 && mtl2.getDeliveryWay() != mtl2.getDeliveryWay()) {
                    Comm.showWarnDialog(context, "发货方式不一致，不能装箱！");
                    return;
                }
                // 相同的物料就+1，否则为1
                for(int i=0; i<size; i++) {
                    MaterialBinningRecord forMtl = listMtl.get(i);
                    if(orderIsnull && salOrder.getMtlId() == forMtl.getMaterialId()) {
                        if((forMtl.getNumber()+1) <= barCodeTable.getMtlPack().getNumber()) {
                            tmpMtl = forMtl;

                            break;
                        } else {
                            Comm.showWarnDialog(context,"该物料已经装满");

                            return;
                        }
                    } else {
                        if(binningType == '1') {
                            Comm.showWarnDialog(context,"单色装只能装一种物料！");
                            return;
                        }
                    }
                }
            }
            if(tmpMtl == null) {
                tmpMtl = new MaterialBinningRecord();
                tmpMtl.setId(0);
                tmpMtl.setBoxBarCodeId(boxBarCode.getId());
                tmpMtl.setMaterialId(barCodeTable.getMaterialId());
                tmpMtl.setRelationBillId(barCodeTable.getRelationBillId());
                tmpMtl.setRelationBillNumber(barCodeTable.getRelationBillNumber());
                tmpMtl.setCustomerId(salOrder.getCustId());
                if(assist != null) {
                    tmpMtl.setDeliveryWay(assist.getfName());
                }
                tmpMtl.setPackageWorkType(2);
                tmpMtl.setBinningType(binningType);
            }
            tmpMtl.setNumber(1);
            // 把对象转成json字符串
            String strJson = JsonUtil.objectToString(tmpMtl);
            run_save(strJson);
        }
    }

    /**
     * （条码表）删除返回的值
     */
    private void getBarCodeTable_delete(BarCodeTable barCodeTable) {
        if (barCodeTable != null) {
            setTexts(etMtlCode, barCodeTable.getMaterialNumber());
            strMtlBarcode = barCodeTable.getMaterialNumber();

            int size = listMtl.size();
            MaterialBinningRecord tmpMtl = null;
            // 已装箱的物料
            if(size > 0) {
                // 相同的物料就+1，否则为1
                for(int i=0; i<size; i++) {
                    MaterialBinningRecord forMtl = listMtl.get(i);
                    if(barCodeTable.getMaterialId().equals(forMtl.getMaterialId())){
                        tmpMtl = forMtl;

                        break;
                    }
                }
            }
            if(tmpMtl == null) {
                Comm.showWarnDialog(context,"扫描物料与箱子不匹配");
                return;
            }
            // 把对象转成json字符串
            String strJson = JsonUtil.objectToString(tmpMtl);
            // 添加到箱子并返回箱子的中的物料列表
            run_delete(strJson);
        }
    }

    /**
     * 扫描物料之后的控制
     */
    private void getBarCodeTableAfter() {
        setEnables(tvCustSel,R.drawable.back_style_gray3,false);
        setEnables(tvDeliverSel,R.drawable.back_style_gray3,false);
        rbType1.setEnabled(false);
        rbType2.setEnabled(false);
        tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        switch (curViewFlag) {
            case '1': // 箱码
                mUrl = Consts.getURL("boxBarCode/findBarcode");
                barcode = strBoxBarcode;
                break;
            case '2': // 物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarCodeTable2ByParam");
                barcode = strMtlBarcode;
                break;
            case '3': // 删除物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarCodeTable2ByParam");
                barcode = strMtlBarcode_del;
                break;
        }
        String boxId = boxBarCode != null ? String.valueOf(boxBarCode.getBoxId()) : "";
        String strCaseId = ""; // 方案id
        if(curViewFlag == '2') {
            if(dataType == '1')  strCaseId = "11"; // 无源单

            else strCaseId = "32,33"; // 有源单（销售订单、发货通知单）
        }
        FormBody formBody = new FormBody.Builder()
                .add("boxId", boxId)
                .add("barcode", barcode)
                .add("strCaseId", strCaseId)
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
                Log.e("run_smGetDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 保存的方法
     */
    private void run_save(String json) {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("materialBinningRecord/save");
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
                Log.e("run_delete --> onResponse", result);
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
