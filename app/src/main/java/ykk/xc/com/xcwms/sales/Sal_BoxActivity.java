package ykk.xc.com.xcwms.sales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.basics.Cust_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
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
    
    private Sal_BoxActivity context = this;
    private static final int SEL_CUST = 11;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int CODE1 = 1, CODE2 = 2, CODE3 = 3, CODE20 = 20;
    private Customer customer; // 客户
    private BoxBarCode boxBarCode; // 箱码表
    private Sal_BoxAdapter mAdapter;
    private char dataType = '1'; // 1：无源单，2：来源单
    private String strBoxBarcode, strMtlBarcode; // 对应的条码号
    private List<MaterialBinningRecord> listMtl = new ArrayList<>();
    private char curViewFlag = '1'; // 1：箱子，2：物料
    private int curPos; // 当前行
    private DecimalFormat df = new DecimalFormat("#.####");
    private View curRadio; // 当前来源View
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
                    case SUCC1:
//                        m.reset('0');

//                        Comm.showWarnDialog(m.context, "保存成功√");
//                        m.listMtl.clear();
//                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.context, "服务器繁忙，请稍候再试！");

                        break;
                    case SUCC2: // 扫码成功后进入
                        switch (m.curViewFlag) {
                            case '1':
                                m.boxBarCode = JsonUtil.strToObject((String) msg.obj, BoxBarCode.class);
                                m.getBoxBarcode();

                                break;
                            case '2':
                                BarCodeTable barCodeTable = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.setEnables(m.tvCustSel,R.drawable.back_style_gray3,false);
                                m.curRadio.setEnabled(false);

                                if(barCodeTable.getRelationBillId() == null || barCodeTable.getRelationBillId() == 0)
                                    m.getBarCodeTable(barCodeTable);
                                else m.getBarCodeTable2(barCodeTable);


                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(CODE20, 200);
                        Comm.showWarnDialog(m.context, "很抱歉，没能找到数据！");


                        break;
                    case SUCC3: // 扫描后的保存 成功
                        m.listMtl.clear();
                        List<MaterialBinningRecord> list = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                        m.listMtl.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC3: // 扫描后的保存 失败
                        Comm.showWarnDialog(m.context, "很抱歉，没能找到数据！");


                        break;
                    case CODE20: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1':
                                m.setTexts(m.etBoxCode, m.strBoxBarcode);
                                break;
                            case '2':
                                m.setTexts(m.etMtlCode, m.strMtlBarcode);
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
//                showInputDialog("数量", String.valueOf(entity.getNumber()), "0", CODE3);
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(etBoxCode);
        hideSoftInputMode(etMtlCode);
        curRadio = viewRadio1;
    }

    @OnClick({R.id.lin_tab1, R.id.lin_tab2, R.id.btn_close, R.id.tv_custSel, R.id.btn_del, R.id.btn_unSave, R.id.btn_save})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.lin_tab1:
                tabSelected(viewRadio1);
                tvTitle.setText("产品装箱-无源单");

                break;
            case R.id.lin_tab2:
                tabSelected(viewRadio2);
                tvTitle.setText("产品装箱-有源单");

                break;
            case R.id.tv_custSel: // 选择客户
                showForResult(Cust_DialogActivity.class, SEL_CUST, null);

                break;
            case R.id.btn_del: // 删除
//                addRow();

                break;
            case R.id.btn_unSave: // 解封
//                hideKeyboard(getCurrentFocus());
//                if (!saveBefore()) {
//                    return;
//                }
//                run_findMatIsExistList();
//                run_addScanningRecord();

                break;
            case R.id.btn_save: // 封箱保存
                hideKeyboard(getCurrentFocus());
                if (!saveBefore()) {
                    return;
                }
//                run_findMatIsExistList();
//                run_addScanningRecord();

                break;
            case R.id.btn_reset: // 重置
                hideKeyboard(getCurrentFocus());
                if (listMtl != null && listMtl.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(context);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            resetSon();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();

                } else {
//                    resetSon();
                }

                break;
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

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (v.getId()) {
                    case R.id.et_boxCode: // 箱码
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

                        break;
                    case R.id.et_mtlCode: // 物料
                        String mtlCode = getValues(etMtlCode).trim();
                        if(!smMtlBefore()) {
                            etMtlCode.setText("");
                            return true;
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
//        if (stock == null) {
//            Comm.showWarnDialog(context,"请选择选择发货方式！");
//            return false;
//        }
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
            case CODE3: // 数量
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
    private void getBoxBarcode() {
        if(boxBarCode != null) {
            listMtl.clear();
            setTexts(etBoxCode, boxBarCode.getBox().getBoxName());
            strBoxBarcode = boxBarCode.getBox().getBoxName();
            // 把箱子里的物料显示出来
            if(boxBarCode.getMtlBinningRecord() != null && boxBarCode.getMtlBinningRecord().size() > 0) {
                tvCount.setText("物料数量："+boxBarCode.getMtlBinningRecord().size());
                listMtl.addAll(boxBarCode.getMtlBinningRecord());
            } else {
                tvCount.setText("物料数量：0");
            }
            int status = boxBarCode.getStatus();
            if(status == 0) {
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
            } else if(status == 1) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
            } else if(status == 2) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
            }
            etMtlCode.setEnabled(true);
            setFocusable(etMtlCode);

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
            MaterialBinningRecord tmpMtl = null;
            // 已装箱的物料
            if(size > 0) {
                MaterialBinningRecord mtl2 = listMtl.get(0);

                // 相同的物料就+1，否则为1
                for(int i=0; i<size; i++) {
                    MaterialBinningRecord forMtl = listMtl.get(i);
                    if(barCodeTable.getMaterialId() == forMtl.getMaterialId() && (forMtl.getNumber()+1) <= barCodeTable.getMtlPack().getNumber()) {
                        tmpMtl = forMtl;
                        tmpMtl.setNumber(forMtl.getNumber()+1);

                        break;
                    } else if(barCodeTable.getMaterialId() == forMtl.getMaterialId() && (forMtl.getNumber()+1) > barCodeTable.getMtlPack().getNumber()) {
                        Comm.showWarnDialog(context,"该物料已经只能装"+barCodeTable.getMtlPack().getNumber()+"个");

                        return;
                    }
                }
            }
            if(tmpMtl == null) {
                tmpMtl = new MaterialBinningRecord();
                tmpMtl.setId(0);
                tmpMtl.setBoxBarCodeId(boxBarCode.getId());
                tmpMtl.setMaterialId(barCodeTable.getMaterialId());
                tmpMtl.setNumber(1);
                tmpMtl.setRelationBillId(barCodeTable.getRelationBillId());
                tmpMtl.setRelationBillNumber(barCodeTable.getRelationBillNumber());
                tmpMtl.setCustomerId(customer.getFcustId());
//                tmpMtl.setExpressType(1);
                tmpMtl.setPackageWorkType(2);

            } else {
                tmpMtl.setNumber(tmpMtl.getNumber()+1);
            }
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
            MaterialBinningRecord tmpMtl = null;
            SalOrder salOrder = null;
            // 得到销售订单
            if(barCodeTable.getRelationObj() != null && barCodeTable.getRelationObj().length() > 0) {
                salOrder = JsonUtil.stringToObject(barCodeTable.getRelationObj(), SalOrder.class);
            }
            // 已装箱的物料
            if(size > 0) {
                MaterialBinningRecord mtl2 = listMtl.get(0);
                if(salOrder != null && mtl2.getCustomerId() != salOrder.getCustId()) {
                    Comm.showWarnDialog(context, "客户不一致，不能装箱！");
                    return;
                }
//                if(salOrder != null && mtl2.getExpressType() != mtl.getExpressType()) {
//                    Comm.showWarnDialog(context, "交货方式不一致，不能装箱！");
//                    return;
//                }
                // 相同的物料就+1，否则为1
                for(int i=0; i<size; i++) {
                    MaterialBinningRecord forMtl = listMtl.get(i);
                    if(salOrder != null && salOrder.getMtlId() == forMtl.getMaterialId() && (forMtl.getNumber()+1) <= barCodeTable.getMtlPack().getNumber()) {
                        tmpMtl = forMtl;

                        break;
                    } else if(salOrder != null && salOrder.getMtlId() == forMtl.getMaterialId() && (forMtl.getNumber()+1) > barCodeTable.getMtlPack().getNumber()) {
                        Comm.showWarnDialog(context,"该物料已经只能装"+barCodeTable.getMtlPack().getNumber()+"个");

                        return;
                    }
                }
            }
            if(tmpMtl == null) {
                tmpMtl = new MaterialBinningRecord();
                tmpMtl.setId(0);
                tmpMtl.setBoxBarCodeId(boxBarCode.getId());
                tmpMtl.setMaterialId(barCodeTable.getMaterialId());
                tmpMtl.setNumber(1);
                tmpMtl.setRelationBillId(barCodeTable.getRelationBillId());
                tmpMtl.setRelationBillNumber(barCodeTable.getRelationBillNumber());
                tmpMtl.setCustomerId(salOrder.getCustId());
//                tmpMtl.setExpressType(); // 发货方式
                tmpMtl.setPackageWorkType(2);
            } else {
                tmpMtl.setNumber(tmpMtl.getNumber()+1);
            }
            // 把对象转成json字符串
            String strJson = JsonUtil.objectToString(tmpMtl);
            run_save(strJson);
        }
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
                mUrl = Consts.getURL("findBoxBarCodeByBarCode");
                barcode = strBoxBarcode;
                break;
            case '2': // 物料
                mUrl = Consts.getURL("barCodeTable/findBarCodeTable2ByParam");
                barcode = strMtlBarcode;
                break;
        }
        String boxId = boxBarCode != null ? String.valueOf(boxBarCode.getBoxId()) : "";
        FormBody formBody = new FormBody.Builder()
                .add("boxId", boxId)
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
                mHandler.sendEmptyMessage(UNSUCC2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC2);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
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
                mHandler.sendEmptyMessage(UNSUCC3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC3);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
                Log.e("run_save --> onResponse", result);
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
