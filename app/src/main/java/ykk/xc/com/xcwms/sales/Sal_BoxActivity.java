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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
import ykk.xc.com.xcwms.basics.Batch_DialogActivity;
import ykk.xc.com.xcwms.basics.Cust_DialogActivity;
import ykk.xc.com.xcwms.basics.Sequence_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.model.sal.SalOrder;
import ykk.xc.com.xcwms.sales.adapter.Sal_BoxAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;

public class Sal_BoxActivity extends BaseActivity {


    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.btn_reset)
    Button btnReset;
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
    private static final int CODE1 = 1, CODE2 = 2, CODE4 = 4, CODE20 = 20;
    private Customer customer; // 客户
    private Sal_BoxAdapter mAdapter;
    private char dataType = '1'; // 1：来源单，2：无源单
    private String boxBarcode, mtlBarcode; // 对应的条码号
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private char curViewFlag = '1'; // 1：箱子，2：物料
    private int curPos; // 当前行

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
//                        m.checkDatas.clear();
//                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.context, "服务器繁忙，请稍候再试！");

                        break;
                    case SUCC2: // 扫码成功后进入
                        switch (m.curViewFlag) {
                            case '1':
//                                m.stock = JsonUtil.strToObject((String) msg.obj, Stock.class);
//                                m.getStockAfter();

                                break;
                            case '2':
//                                m.mtl = JsonUtil.strToObject((String) msg.obj, Material.class);
//                                m.setTexts(m.etMatNo, m.mtl.getfNumber());
//                                m.matBarcode = m.mtl.getfNumber();
//                                if (m.dataType == '1') { // 来源单
//                                    if (m.mtl.getIsSnManager() == 1) {
//                                        Comm.showWarnDialog(m.context, "该物料已启用序列号，数量为1");
//                                        return;
//                                    }
//                                    for (int i = 0, size = m.checkDatas.size(); i < size; i++) {
//                                        ScanningRecord2 sr2 = m.checkDatas.get(i);
//                                        if (m.mtl.getfMaterialId() == sr2.getMtl().getfMaterialId() && sr2.getMtl().getIsSnManager() == 0 && sr2.getFqty() > sr2.getStockqty()) {
//                                            // 没有启用序列号，并且应发数量大于实发数量
//                                            sr2.setStockqty(sr2.getStockqty() + 1);
//                                            break;
//                                        }
//                                    }
//
//                                } else { // 无源单
//                                    m.addRowSon("", "", -1);
//                                }
                                m.mAdapter.notifyDataSetChanged();

                                break;
                            case '5':
                                List<SalOrder> list = JsonUtil.strToList2((String) msg.obj, SalOrder.class);
//                                m.getSourceAfter(list, true);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(CODE20, 200);
                        Comm.showWarnDialog(m.context, "很抱歉，没能找到数据！");


                        break;
                    case CODE20: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1':
                                m.setTexts(m.etBoxCode, m.boxBarcode);
                                break;
                            case '2':
                                m.setTexts(m.etMtlCode, m.mtlBarcode);
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
        mAdapter = new Sal_BoxAdapter(context, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Sal_BoxAdapter.MyCallBack() {
            @Override
            public void onClick_batch_top(View v, ScanningRecord2 entity, int position) {
                Log.e("batch", "行：" + position);
                curPos = position;
                if (entity.getMtl().getIsSnManager() == 1) { // 选择批号
                    Bundle bundle = new Bundle();
                    bundle.putInt("fitemId", entity.getMtl().getfMaterialId());
                    bundle.putInt("stockId", entity.getStock().getfStockid());
                    showForResult(Batch_DialogActivity.class, CODE1, bundle);
                }
            }

            @Override
            public void onClick_batch_long(View v, ScanningRecord2 entity, int position) {
                Log.e("sequence", "行：" + position);
                curPos = position;
                if (entity.getMtl().getIsSnManager() == 1) { // 选择序列号
                    showForResult(Sequence_DialogActivity.class, CODE2, null);
                }
            }

            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", CODE4);
            }

            @Override
            public void onClick_del(ScanningRecord2 entity, int position) {
                Log.e("del", "行：" + position);
                checkDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(etBoxCode);
        hideSoftInputMode(etMtlCode);
    }

    @OnClick({R.id.btn_close, R.id.tv_custSel, R.id.btn_del, R.id.btn_unSave, R.id.btn_save})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

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
                if (checkDatas != null && checkDatas.size() > 0) {
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
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(context, "请先插入行！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if (sr2.getMtl().getIsBatchManager() == 1 && sr2.getBatchno().length() == 0) {
                Comm.showWarnDialog(context, "第" + (i + 1) + "行请输入（批号）！");
                return false;
            }
            if (sr2.getMtl().getIsSnManager() == 1 && sr2.getSequenceNo().length() == 0) {
                Comm.showWarnDialog(context, "第" + (i + 1) + "行请输入（序列号）！");
                return false;
            }
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(context, "第" + (i + 1) + "行（实发数）必须大于0！");
                return false;
            }
            if (sr2.getStockqty() > sr2.getFqty()) {
                Comm.showWarnDialog(context, "第" + (i + 1) + "行（实发数）不能大于（应发数）！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_whName, R.id.et_whArea, R.id.et_whPos, R.id.et_matNo, R.id.et_sourceNo})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
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
                            if (boxBarcode != null && boxBarcode.length() > 0) {
                                String tmp = boxCode.replaceFirst(boxBarcode, "");
                                boxBarcode = tmp.replace("\n", "");
                            } else {
                                boxBarcode = boxCode.replace("\n", "");
                            }
                            curViewFlag = '1';
                            // 执行查询方法
                            run_smGetDatas();
                        }

                        break;
                    case R.id.et_mtlCode: // 物料
                        String mtlCode = getValues(etMtlCode).trim();
                        if (isKeyDownEnter(mtlCode, event, keyCode)) {
                            if (mtlBarcode != null && mtlBarcode.length() > 0) {
                                String tmp = mtlCode.replaceFirst(mtlBarcode, "");
                                mtlBarcode = tmp.replace("\n", "");
                            } else {
                                mtlBarcode = mtlCode.replace("\n", "");
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
            case CODE4: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        checkDatas.get(curPos).setStockqty(parseDouble(value));
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /**
     * 选择（物料）返回的值
     */
    private void getMtlAfter() {
//        if (mtl != null) {
//            setTexts(etMatNo, mtl.getfNumber());
//            matBarcode = mtl.getfNumber();
//            tvMatName.setText(mtl.getfName());
//            tvType.setText(mtl.getMaterialSize());
//            setTexts(etNum, mtl.getIsSnManager() == 1 ? "1" : "");
//            dataType = '2';
//            setEnables(etSourceNo, R.drawable.back_style_gray5, false);
//            setEnables(btnSourceNo, R.drawable.back_style_gray6, false);
////             物料是否需要输入批号
//            if (mtl.getIsSnManager() == 1) {
//                setEnables(tvBatchNo, R.drawable.back_style_blue, true);
//            } else {
//                tvBatchNo.setText("");
//                setEnables(tvBatchNo, R.drawable.back_style_gray3, false);
//            }
////             是否启用物料的序列号,如果启用了，则数量为1
//            if (mtl.getIsSnManager() == 1) {
//                setEnables(tvSequenceNo, R.drawable.back_style_blue, true);
//                etNum.setText("1");
//                setEnables(etNum, R.drawable.back_style_gray3, false);
//            } else {
//                tvSequenceNo.setText("");
//                setEnables(tvSequenceNo, R.drawable.back_style_gray3, false);
//                etNum.setText("");
//                setEnables(etNum, R.drawable.back_style_blue, true);
//            }
//        }
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
                barcode = boxBarcode;
                break;
            case '2': // 物料
                mUrl = Consts.getURL("barCodeTable/findBarCodeTable2ByParam");
                barcode = mtlBarcode;
                break;
        }
        FormBody formBody = new FormBody.Builder()
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
