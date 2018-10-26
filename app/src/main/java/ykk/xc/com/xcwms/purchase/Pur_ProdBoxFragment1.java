package ykk.xc.com.xcwms.purchase;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.solidfire.gson.JsonObject;

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
import ykk.xc.com.xcwms.basics.Box_DialogActivity;
import ykk.xc.com.xcwms.basics.Cust_DialogActivity;
import ykk.xc.com.xcwms.basics.DeliveryWay_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseFragment;
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
import ykk.xc.com.xcwms.model.pur.ProdOrder;
import ykk.xc.com.xcwms.purchase.adapter.Pur_ProdBoxFragment1Adapter;
import ykk.xc.com.xcwms.util.JsonUtil;
/**
 * 生产装箱--无批次
 */
public class Pur_ProdBoxFragment1 extends BaseFragment {

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
    @BindView(R.id.et_prodOrderCode)
    EditText etProdOrderCode;
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
//    @BindView(R.id.btn_save)
//    Button btnSave;
    @BindView(R.id.btn_end)
    Button btnEnd;

    public Pur_ProdBoxFragment1() {}

    private Pur_ProdBoxFragment1 mFragment = this;
    private Pur_ProdBoxMainActivity parent;
    private Activity mContext;
    private static final int SEL_CUST = 11, SEL_DELI = 12, SEL_BOX = 13, SEL_NUM = 14, RESET = 15;
    private static final int SUCC1 = 201, UNSUCC1 = 501, SAVE = 202, UNSAVE = 502, DELETE = 203, UNDELETE = 503, MODIFY = 204, UNMODIFY = 504, MODIFY2 = 205, UNMODIFY2 = 505, MODIFY3 = 206, UNMODIFY3 = 506, MODIFY_NUM = 207, UNMODIFY_NUM = 507;
    private static final int CODE1 = 1, CODE2 = 2;
    private Customer customer; // 客户
    private Box box; // 箱子表
    private BoxBarCode boxBarCode; // 箱码表
    private Pur_ProdBoxFragment1Adapter mAdapter;
    private String boxBarcode, prodOrderBarcode, mtlBarcode, mtlBarcode_del; // 对应的条码号
    private List<MaterialBinningRecord> checkDatas = new ArrayList<>();
    private char curViewFlag = '1'; // 1：箱子，2：物料
    private DecimalFormat df = new DecimalFormat("#.####");
    private int curPos; // 当前行
    private AssistInfo assist; // 辅助资料--生产方式
    private AlertDialog delDialog;
    private EditText etMtlCode2;
    private CheckBox checkClose;
    private boolean isCloseDelDialog = true; // 是否关闭删除的Dialog
    private char status = '0'; // 箱子状态（0：创建，1：开箱，2：封箱）
    private char binningType = '2'; // 1.单色装，2.混色装
    private User user;
    private OkHttpClient okHttpClient = new OkHttpClient();

    // 消息处理
    private Pur_ProdBoxFragment1.MyHandler mHandler = new Pur_ProdBoxFragment1.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_ProdBoxFragment1> mActivity;

        public MyHandler(Pur_ProdBoxFragment1 activity) {
            mActivity = new WeakReference<Pur_ProdBoxFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_ProdBoxFragment1 m = mActivity.get();
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
                            case '2': // 生产订单扫码   返回
                                List<ProdOrder> list = JsonUtil.strToList((String) msg.obj, ProdOrder.class);
//                                BarCodeTable bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
//                                if(!bt.getIsLocalCust()) {
//                                    Comm.showWarnDialog(m.mContext,"客户信息未同步，请前往PC端同步！");
//                                    return;
//                                }
                                // 是否启用序列号，是否装过箱
//                                if(barCodeTable.getMtl() != null && barCodeTable.getMbr() != null && barCodeTable.getMtl().getIsSnManager() > 0 && barCodeTable.getMbr().getId() > 0) {
//                                    m.setTexts(m.etMtlCode, m.mtlBarcode);
//                                    Comm.showWarnDialog(m.mContext,"该物料启用了序列号，不能重复装箱！");
//                                    return;
//                                }
//                                m.getBarCodeTableAfter(); // 禁用一些控件
//                                m.getBarCodeTable2(barCodeTable);
                                m.getProdOrderAfter(list);

                                break;
                            case '3': // 物料扫码     返回
//                                BarCodeTable barCodeTable2 = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
//                                m.getBarCodeTable_delete(barCodeTable2);
                                BarCodeTable bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.getMtlAfter(bt);

                                break;
                            case '4': // 删除物料扫码     返回
                                BarCodeTable barCodeTable2 = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.getBarCodeTable_delete(barCodeTable2);

                                break;
                        }

                        break;
                    case UNSUCC1:
                        m.mHandler.sendEmptyMessageDelayed(RESET, 200);
                        Comm.showWarnDialog(m.mContext, "很抱歉，没能找到数据！");


                        break;
                    case SAVE: // 扫描后的保存 成功
                        m.status = '1';
                        m.checkDatas.clear();
                        List<MaterialBinningRecord> listMbr = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                        m.checkDatas.addAll(listMbr);
                        m.btnEnd.setVisibility(View.VISIBLE);
//                        double sum = 0;
//                        for(int i = 0, size = m.checkDatas.size(); i<size; i++) {
//                            sum += m.checkDatas.get(i).getNumber();
//                        }
//                        m.tvCount.setText("数量："+m.df.format(sum));
                        m.mAdapter.notifyDataSetChanged();
//                        m.btnSave.setVisibility(View.GONE);
                        Comm.showWarnDialog(m.mContext,"保存成功✔");

                        break;
                    case UNSAVE: // 扫描后的保存 失败
                        m.mHandler.sendEmptyMessageDelayed(RESET, 200);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext,"保存到装箱失败，请检查！");

                        break;
                    case DELETE: // 删除 成功
                        m.checkDatas.clear();
                        List<MaterialBinningRecord> list2 = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                        m.checkDatas.addAll(list2);
                        m.mAdapter.notifyDataSetChanged();
                        // 如果点击了关闭窗口的复选框
                        m.setCloseDelDialog();

                        break;
                    case UNDELETE: // 删除 失败
                        JsonObject jsonObj = JsonUtil.strToObject((String) msg.obj, JsonObject.class);
                        if(jsonObj.has("errMsg")) {
                            String errMsg = jsonObj.get("errMsg").getAsString();
                            Comm.showWarnDialog(m.mContext, errMsg);
                            return;
                        }
                        if(jsonObj.has("listSize")) {
                            int size = jsonObj.get("listSize").getAsInt();
                            if(size == 0) {
                                m.tvCustSel.setText("客户：");
//                                m.setEnables(m.tvCustSel, R.drawable.back_style_gray3, false);
                                m.customer = null;
                                m.checkDatas.clear();
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#000000'>未开箱</font>"));
                                m.tvCount.setText("数量：0");
                            }
                        }
                        m.setCloseDelDialog();
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case MODIFY: // 修改状态（开箱或封箱） 成功
                        String count = JsonUtil.strToString((String) msg.obj);
//                        m.btnSave.setVisibility(View.VISIBLE);
                        switch (m.status) {
                            case '1': // 开箱
                                m.setEnables(m.etMtlCode,R.drawable.back_style_blue,true);
                                m.setFocusable(m.etMtlCode);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                                m.btnEnd.setText("封箱");
                                break;
                            case '2': // 封箱
                                m.setEnables(m.etMtlCode,R.drawable.back_style_gray3,false);
                                m.setEnables(m.tvDeliverSel,R.drawable.back_style_gray3,false);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
                                if(m.parseInt(count) > 0) {
                                    Comm.showWarnDialog(m.mContext,"当前客户还有"+count+"个物料没有装箱，请注意！");
                                }
                                m.btnEnd.setText("开箱");
                                break;
                        }
                        // 去打印
                        List<MaterialBinningRecord> list = new ArrayList<>();
                        for(int i=0; i<m.checkDatas.size(); i++) {
                            MaterialBinningRecord mbr = m.checkDatas.get(i);
                            if(mbr.getNumber() > 0) list.add(mbr);
                        }
                        m.parent.setFragmentPrint1(0, list, m.boxBarCode);

                        break;
                    case UNMODIFY: // 修改状态（开箱或封箱） 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case MODIFY2: // 修改生产方式 成功
                        // 更新所有的交货方式
                        for(int i = 0, size = m.checkDatas.size(); i<size; i++) {
                            MaterialBinningRecord mbr = m.checkDatas.get(i);
                            mbr.setDeliveryWay(m.getValues(m.tvDeliverSel));
                        }
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNMODIFY2: // 修改生产方式 失败
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
                    case MODIFY_NUM: // 修改数量  成功
                        double number = (double) msg.obj;
                        m.checkDatas.get(m.curPos).setNumber(number);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNMODIFY_NUM: // 修改数量 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case CODE1: // 清空数据
                        m.etProdOrderCode.setText("");
                        m.prodOrderBarcode = "";

                        break;
                    case CODE2: // Dialog默认得到焦点，隐藏软键盘
                        m.hideSoftInputMode(m.mContext, m.etMtlCode2);
                        m.setFocusable(m.etMtlCode2);

                        break;
                    case RESET: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 箱码扫码
                                m.setTexts(m.etBoxCode, m.boxBarcode);
                                break;
                            case '2': // 生产订单扫码
                                m.setTexts(m.etProdOrderCode, m.prodOrderBarcode);
                                break;
                            case '3': // 物料扫码
                                m.setTexts(m.etMtlCode, m.mtlBarcode);
                                break;
                            case '4': // 删除扫码
                                m.setTexts(m.etMtlCode2, m.mtlBarcode_del);
                                break;
                        }

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_prod_box_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Pur_ProdBoxMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_ProdBoxFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setCallBack(new Pur_ProdBoxFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, MaterialBinningRecord entity, int position) {
                Log.e("num", "行：" + position);
                if(status == 2) return;

                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getNumber()), "0.0", SEL_NUM);
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etBoxCode);
        hideSoftInputMode(mContext, etProdOrderCode);
        hideSoftInputMode(mContext, etMtlCode);
        getUserInfo();
    }

    @OnClick({R.id.btn_boxConfirm, R.id.tv_custSel, R.id.tv_deliverSel, R.id.btn_clone, R.id.btn_del, R.id.btn_save, R.id.btn_end, R.id.btn_print, R.id.tv_box})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_custSel: // 选择客户
                showForResult(Cust_DialogActivity.class, SEL_CUST, null);

                break;
            case R.id.tv_deliverSel: // 生产方式
                showForResult(DeliveryWay_DialogActivity.class, SEL_DELI, null);

                break;
            case R.id.tv_box: // 选择箱子
                showForResult(Box_DialogActivity.class, SEL_BOX, null);

                break;
            case R.id.btn_boxConfirm: // 确认箱子
                if(getValues(tvBox).length() == 0) {
                    Comm.showWarnDialog(mContext,"请选择箱子！");
                    return;
                }
                run_modifyBoxIdByBarcode();

                break;
            case R.id.btn_del: // 删除
                if(status == '2') {
                    Comm.showWarnDialog(mContext,"已经封箱，不能删除，请开箱操作！");
                    return;
                }
                if(checkDatas != null && checkDatas.size() > 0) {
                    deleteRowDialog();
                } else {
                    Comm.showWarnDialog(mContext,"箱子中没有物料，不能删除！");
                }

                break;
            case R.id.btn_save: // 保存
                if(boxBarCode == null) {
                    Comm.showWarnDialog(mContext,"请先扫描箱码！");
                    return;
                }
                status = '1';

                List<MaterialBinningRecord> list = new ArrayList<>();
                for(int i=0; i<checkDatas.size(); i++) {
                    MaterialBinningRecord mbr = checkDatas.get(i);
                    if(mbr.getNumber() > 0) list.add(mbr);
                }
                if(list.size() == 0) {
                    Comm.showWarnDialog(mContext,"请扫物料条码！");
                    return;
                }
                // 把对象转成json字符串
                String strJson = JsonUtil.objectToString(list);
                run_save(strJson);
//                run_modifyStatus();

                break;
            case R.id.btn_end: // 封箱保存
                if(boxBarCode == null) {
                    Comm.showWarnDialog(mContext,"请先扫描箱码！");
                    return;
                }
                if(checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext,"箱子里还没有物料不能封箱！");
                    return;
                }
                if(getValues(tvDeliverSel).length() == 0) {
                    setEnables(tvDeliverSel, R.drawable.back_style_blue, true);
                    Comm.showWarnDialog(mContext,"请选择发货方式！");
                    return;
                }
                if(status == '1') status = '2';
                else status = '1';

                run_modifyStatus();

                break;
            case R.id.btn_print: // 打印
                if(boxBarCode == null) {
                    Comm.showWarnDialog(mContext,"请先扫描箱码！");
                    return;
                }
                if(checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext,"箱子里还没有物料不能打印！");
                    return;
                }
                if(status != '2') {
                    Comm.showWarnDialog(mContext,"请先封箱，然后打印！");
                    return;
                }
                parent.setFragmentPrint1(0, checkDatas, boxBarCode);

                break;
            case R.id.btn_clone: // 新装
                reset();

                break;
        }
    }

    /**
     * 重置
     */
    private void reset() {
        btnEnd.setVisibility(View.GONE);
        etBoxCode.setText("");
        etProdOrderCode.setText("");
        etMtlCode.setText("");
        boxBarCode = null;
        prodOrderBarcode = null;
        mtlBarcode = null;
        mtlBarcode_del = null;
        tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
        tvBoxName.setText("");
        tvBoxSize.setText("");
        tvBoxLength.setText("");
        tvBoxWidth.setText("");
        tvBoxAltitude.setText("");
        tvBoxVolume.setText("");
        tvCustSel.setText("客户：");
//        setEnables(tvCustSel, R.drawable.back_style_gray3, false);
        customer = null;
//        setEnables(tvCustSel, R.drawable.back_style_blue, true);
//        tvDeliverSel.setText("");
//        setEnables(tvDeliverSel, R.drawable.back_style_blue, true);
        tvCount.setText("数量：0");

        curViewFlag = '1';

        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        setFocusable(etBoxCode);
    }

    /**
     * 删除行的dialog
     */
    private void deleteRowDialog() {
        View v = mContext.getLayoutInflater().inflate(R.layout.sal_box_item_del_dialog, null);
        delDialog = new AlertDialog.Builder(mContext).setView(v).create();
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
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String mtlCode = getValues(etMtlCode2).trim();
                if (mtlBarcode_del != null && mtlBarcode_del.length() > 0) {
                    if(mtlBarcode_del.equals(mtlCode)) {
                        mtlBarcode_del = mtlCode;
                    } else {
                        String tmp = mtlCode.replaceFirst(mtlBarcode_del, "");
                        mtlBarcode_del = tmp.replace("\n", "");
                    }
                } else {
                    mtlBarcode_del = mtlCode.replace("\n", "");
                }
                curViewFlag = '4';
                mHandler.sendEmptyMessageDelayed(RESET, 200);
                // 执行查询方法
                run_smGetDatas();
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
        }
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
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
                            mHandler.sendEmptyMessageDelayed(RESET, 200);
                            // 执行查询方法
                            run_smGetDatas();

                            break;
                        case R.id.et_prodOrderCode: // 生产订单
                            String fbillCode = getValues(etProdOrderCode).trim();
                            if (prodOrderBarcode != null && prodOrderBarcode.length() > 0) {
                                if (prodOrderBarcode.equals(fbillCode)) {
                                    prodOrderBarcode = fbillCode;
                                } else {
                                    String tmp = fbillCode.replaceFirst(prodOrderBarcode, "");
                                    prodOrderBarcode = tmp.replace("\n", "");
                                }
                            } else {
                                prodOrderBarcode = fbillCode.replace("\n", "");
                            }
                            curViewFlag = '2';
                            mHandler.sendEmptyMessageDelayed(RESET, 200);

                            if(prodOrderBarcode.length() == 0) {
                                Comm.showWarnDialog(mContext,"请对准条码！");
                                return false;
                            }
                            if (smMtlBefore()) {
                                // 执行查询方法
                                run_smGetDatas();
                            }

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
                            curViewFlag = '3';
                            mHandler.sendEmptyMessageDelayed(RESET, 200);

                            if (checkDatas.size() == 0) {
                                Comm.showWarnDialog(mContext,"请扫描生产订单！");
                                mHandler.sendEmptyMessageDelayed(RESET, 200);
                                return false;
                            }
                            // 执行查询方法
                            run_smGetDatas();

                            break;
                    }
                }
                return false;
            }
        };
        etBoxCode.setOnKeyListener(keyListener);
        etProdOrderCode.setOnKeyListener(keyListener);
        etMtlCode.setOnKeyListener(keyListener);

    }

    /**
     * 扫码物料之前的判断
     */
    private boolean smMtlBefore() {
//        if (customer == null) {
//            Comm.showWarnDialog(mContext,"请选择客户！");
//            return false;
//        }

        if(boxBarCode == null) {
            Comm.showWarnDialog(mContext, "请扫箱码！");
            return false;
        }
        if (assist == null) {
            Comm.showWarnDialog(mContext,"请选择发货方式！");
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_CUST: //查询客户	返回
                if (resultCode == Activity.RESULT_OK) {
                    customer = (Customer) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_CUST", customer.getCustomerName());
                    if (customer != null) {
                        tvCustSel.setText("客户："+customer.getCustomerName());
                    }
                }

                break;
            case SEL_DELI: //查询生产方式	返回
                if (resultCode == Activity.RESULT_OK) {
                    assist = (AssistInfo) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DELI", assist.getfName());
                    if (assist != null) {
                        tvDeliverSel.setText(assist.getfName());
                    }
                }

                break;
            case SEL_BOX: //查询箱子	返回
                if (resultCode == Activity.RESULT_OK) {
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
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double number = parseDouble(value);
                        MaterialBinningRecord mbr = checkDatas.get(curPos);
                        if(number > mbr.getRelationBillFQTY()) {
                            Comm.showWarnDialog(mContext,"第"+(curPos+1)+"行装箱数不能大于订单数！");
                            return;
                        }
                        int id = mbr.getId();
                        run_modifyNumber2(id, number);
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
            checkDatas.clear();
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
                Comm.showWarnDialog(mContext,"请选择包装箱！");
                return;
            }

            // 把箱子里的物料显示出来
            if(boxBarCode.getMtlBinningRecord() != null && boxBarCode.getMtlBinningRecord().size() > 0) {
                MaterialBinningRecord mtlbr = boxBarCode.getMtlBinningRecord().get(0);

                if(mtlbr.getCaseId() != 34) {
                    etBoxCode.setText("");
                    boxBarcode = null;
                    setFocusable(etBoxCode);
                    Comm.showWarnDialog(mContext,"该箱子已经装了其他类型物料，请扫描未使用的箱码！");
                    return;
                }
                checkDatas.addAll(boxBarCode.getMtlBinningRecord());
                double sum = 0;
                for(int i = 0, size = checkDatas.size(); i<size; i++) {
                    sum += checkDatas.get(i).getNumber();
                }
                tvCount.setText("数量："+df.format(sum));

                // 显示当前的客户
                if(customer == null) customer = new Customer();
                customer.setFcustId(mtlbr.getCustomerId());
                customer.setCustomerCode(mtlbr.getCustomerNumber());
                customer.setCustomerName(mtlbr.getCustomer().getCustomerName());
                tvCustSel.setText("客户："+mtlbr.getCustomer().getCustomerName());
                // 显示交货方式
                if(assist == null) assist = new AssistInfo();
                assist.setfName(mtlbr.getDeliveryWay());
                tvDeliverSel.setText(mtlbr.getDeliveryWay());

            } else {
                tvCount.setText("数量：0");
            }
            btnEnd.setText("封箱");
            btnEnd.setVisibility(View.GONE);
            int status = boxBarCode.getStatus();
            if(status == 0) {
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                setFocusable(etProdOrderCode);
                this.status = '0';
            } else if(status == 1) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                setFocusable(etProdOrderCode);
                btnEnd.setVisibility(View.VISIBLE);
                this.status = '1';
            } else if(status == 2) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
                btnEnd.setText("开箱");
                btnEnd.setVisibility(View.VISIBLE);
                this.status = '2';
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
     * （条码表 有源单）返回的值
     */
    private void getBarCodeTable2(BarCodeTable bt) {
        if (bt != null) {
            int size = checkDatas.size();
            getUserInfo();

            MaterialBinningRecord mbr = null;
            ProdOrder prodOrder = null;
            boolean prodOrderNotnull = false; // 销售订单是否为空

            MaterialBinningRecord mbr1 = null;
            if(size > 0) {
                mbr1 = checkDatas.get(0);
            }

            // 得到生产订单
            if(bt.getRelationObj() != null && bt.getRelationObj().length() > 0) {
                // 判断来源单是否所属同一类型，销售订单，或者生产通知单
                if(mbr1 != null && mbr1.getCaseId() != bt.getCaseId()) { // 是否为相同订单
                    Comm.showWarnDialog(mContext, "当前订单类型不一致，请检查！" +
                            "（已装箱为："+(mbr1.getCaseId() == 34 ? "生产订单" : "其它订单")+"，" +
                            "当前扫码为："+(bt.getCaseId() == 34 ? "生产订单" : "其它订单")+"）");
                    return;
                }

                //  得到对象    生产订单
                prodOrder = JsonUtil.stringToObject(bt.getRelationObj(), ProdOrder.class);
            }
            // 对象是否为空
            prodOrderNotnull = prodOrder != null;

            // 带上客户和生产方式
            if(getValues(tvCustSel).length() == 0) {
                if(customer == null) customer = new Customer();
                customer.setFcustId(prodOrder.getCustId());
                customer.setCustomerCode(prodOrder.getCustNumber());
                customer.setCustomerName(prodOrder.getCustName());
                tvCustSel.setText("客户："+customer.getCustomerName());
            }

            // 如果来源单的的客户为空，就提示选择客户
            if(prodOrderNotnull && prodOrder.getCustId() == 0 && getValues(tvCustSel).length() == 0) {
//                setEnables(tvCustSel, R.drawable.back_style_blue, true);
                Comm.showWarnDialog(mContext, "请选择客户！");
                return;
            }
            // 判断客户和生产方式是否一致
            if(prodOrderNotnull && mbr1 != null && mbr1.getCustomerId() > 0 && mbr1.getCustomerId() != prodOrder.getCustId()) {
                Comm.showWarnDialog(mContext, "客户不一致，不能装箱！");
                return;
            }

            // 已装箱的物料
            if(size > 0) {
                // 相同的物料就+1，否则为1
                for(int i=0; i<size; i++) {
                    MaterialBinningRecord forMtl = checkDatas.get(i);

                    // 判断物料是否装满，单装或者混装
                    if(prodOrderNotnull && bt.getRelationBillId() == forMtl.getRelationBillId() && bt.getEntryId() == forMtl.getEntryId()) {
                        mbr = forMtl;
                        if(forMtl.getMtl().getIsSnManager() == 0) {
                            if((mbr.getNumber()+1) > mbr.getRelationBillFQTY()) {
                                Comm.showWarnDialog(mContext,"第"+(i+1)+"行装箱数不能大于订单数！");
                                return;
                            }
                        }
                    }  else {
                        if(binningType == '1') {
                            Comm.showWarnDialog(mContext,"单装只能装一种物料！");
                            return;
                        }
                    }

                }
            }
            if(mbr == null) {
                mbr = new MaterialBinningRecord();
                mbr.setId(0);
                mbr.setBoxBarCodeId(boxBarCode.getId());
                mbr.setMaterialId(bt.getMaterialId());
                mbr.setRelationBillId(bt.getRelationBillId());
                mbr.setRelationBillNumber(bt.getRelationBillNumber());
                mbr.setCustomerId(customer.getFcustId());
                mbr.setCustomerNumber(customer.getCustomerCode());
                if(assist != null) {
                    mbr.setDeliveryWay(assist.getfName());
                }
                mbr.setPackageWorkType(2);
                mbr.setBinningType(binningType);
                mbr.setCaseId(bt.getCaseId());
            }

            mbr.setFbillType(1);
            mbr.setBarcodeSource('1');
            mbr.setNumber(1);
            mbr.setRelationBillFQTY(prodOrder.getProdFqty());
            mbr.setEntryId(prodOrder.getEntryId());
            mbr.setBarcode(bt.getBarcode());
            // 启用了批次号
            if(mbr.getMtl() != null && mbr.getMtl().getIsBatchManager() == 1) {
                mbr.setBatchCode(bt.getBatchCode());
            }
            // 启用了序列号
            if(mbr.getMtl() != null && mbr.getMtl().getIsSnManager() == 1) {
                mbr.setSnCode(bt.getSnCode());
            }
            mbr.setCreateUserId(user.getId());
            mbr.setCreateUserName(user.getUsername());
            mbr.setModifyUserId(user.getId());
            mbr.setModifyUserName(user.getUsername());
            mbr.setSalOrderNo(prodOrder.getSalOrderNo());
            mbr.setSalOrderNoEntryId(prodOrder.getSalOrderEntryId());

            // 把对象转成json字符串
            String strJson = JsonUtil.objectToString(mbr);
            run_save(strJson);
        }
    }

    /**
     * 得到生产订单的数据
     */
    private void getProdOrderAfter(List<ProdOrder> list) {
        checkDatas.clear(); // 清空
        int size = list.size();
        for(int i=0; i<size; i++) {
            MaterialBinningRecord mbr = new MaterialBinningRecord();
            ProdOrder prodOrder = list.get(i);
            mbr.setId(0);
            mbr.setFbillType(1); // 单据类型（1：生产装箱，2：销售装箱，3：装箱单）
            mbr.setBoxBarCodeId(boxBarCode.getId());
            mbr.setMtl(prodOrder.getMtl());
            mbr.setMaterialId(prodOrder.getMtlId());
            mbr.setRelationBillId(prodOrder.getfId());
            mbr.setRelationBillNumber(prodOrder.getFbillno());
            if(customer == null) {
                customer = new Customer();
                customer.setFcustId(prodOrder.getCustId());
                customer.setCustomerCode(prodOrder.getCustNumber());
                customer.setCustomerName(prodOrder.getCustName());
            }
            mbr.setCustomerId(customer.getFcustId());
            mbr.setCustomerNumber(customer.getCustomerCode());
            mbr.setCustomer(customer);
            if(assist != null) {
                mbr.setDeliveryWay(assist.getfName());
            }
            mbr.setPackageWorkType(2);
            mbr.setBinningType(binningType);
            mbr.setCaseId(34);
            mbr.setBarcodeSource('1');
            mbr.setNumber(0);
            mbr.setRelationBillFQTY(prodOrder.getProdFqty());
            mbr.setEntryId(prodOrder.getEntryId());
            mbr.setBarcode("");
            // 启用了批次号，在扫物料中加入
//            if(mbr.getMtl() != null && mbr.getMtl().getIsBatchManager() == 1) {
//                mbr.setBatchCode(bt.getBatchCode());
//            }
            // 启用了序列号
//            if(mbr.getMtl() != null && mbr.getMtl().getIsSnManager() == 1) {
//                mbr.setSnCode(bt.getSnCode());
//            }
            mbr.setCreateUserId(user.getId());
            mbr.setCreateUserName(user.getUsername());
            mbr.setModifyUserId(user.getId());
            mbr.setModifyUserName(user.getUsername());
            mbr.setSalOrderNo(prodOrder.getSalOrderNo());
            mbr.setSalOrderNoEntryId(prodOrder.getSalOrderEntryId());
            
            // 物料是否启用序列号
            if(prodOrder.getMtl().getIsSnManager() == 1) {
                mbr.setListBarcode(new ArrayList<String>());
            }
            mbr.setStrBarcodes("");
            mbr.setRelationObj(JsonUtil.objectToString(prodOrder));

            checkDatas.add(mbr);
        }

        tvCustSel.setText("客户："+customer.getCustomerName());
        tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
        setFocusable(etMtlCode);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 得到扫码物料 数据
     */
    private void getMtlAfter(BarCodeTable bt) {
        Material tmpMtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);

        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            MaterialBinningRecord mbr = checkDatas.get(i);
            Material mtl = mbr.getMtl();
            // 如果扫码相同
            if (bt.getMaterialId() == mtl.getfMaterialId()) {
                isFlag = true;

                double fqty = 1;
                // 计量单位数量
                if(tmpMtl.getCalculateFqty() > 0) fqty = tmpMtl.getCalculateFqty();
                // 未启用序列号
                if (tmpMtl.getIsSnManager() == 0) {
                    // 生产数大于装箱数
                    if (mbr.getRelationBillFQTY() > mbr.getNumber()) {
                        // 如果扫的是物料包装条码，就显示个数
                        double number = 0;
                        if(bt != null) number = bt.getMaterialCalculateNumber();

                        if(number > 0) {
                            mbr.setNumber(mbr.getNumber() + (number*fqty));
                        } else {
                            mbr.setNumber(mbr.getNumber() + fqty);
                        }
                        mbr.setBatchCode(bt.getBatchCode());
                        mbr.setSnCode(bt.getSnCode());

                        // 启用了最小包装
                    } else if(mtl.getMtlPack() != null && mtl.getMtlPack().getIsMinNumberPack() == 1) {
                        if(mtl.getMtlPack().getIsMinNumberPack() == 1) {
                            // 如果装箱数小于订单数，就加数量
                            if(mbr.getNumber() < mbr.getRelationBillFQTY()) {
                                mbr.setNumber(mbr.getNumber() + fqty);
                            } else {
                                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已经达到最小包装生产数量！");
                                return;
                            }
                        }

                    } else if ((mtl.getMtlPack() == null || mtl.getMtlPack().getIsMinNumberPack() == 0) && mbr.getNumber() > mbr.getRelationBillFQTY()) {
                        // 数量已满
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，（装箱数）不能大于（订单数）！");
                        return;
                    }
                } else {
                    if (mbr.getNumber() == mbr.getRelationBillFQTY()) {
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已装完！");
                        return;
                    }
                    List<String> list = mbr.getListBarcode();
                    if(list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(mContext,"该物料条码已在装箱行中，请扫描未使用过的条码！");
                        return;
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
                        if((k+1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k)+",");
                    }
                    mbr.setBatchCode(bt.getBatchCode());
                    mbr.setSnCode(bt.getSnCode());
                    mbr.setListBarcode(list);
                    mbr.setStrBarcodes(sb.toString());
                    mbr.setNumber(mbr.getNumber() + 1);
                }
                // 汇总数量
                double sum = 0;
                for(int j = 0, sizeJ = checkDatas.size(); j<sizeJ; j++) {
                    sum += checkDatas.get(j).getNumber();
                }
                tvCount.setText("数量："+ df.format(sum));
                mAdapter.notifyDataSetChanged();
                isPickingEnd();
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(mContext, "该物料与订单不匹配！");
        }
    }

    /**
     * 是否已经捡完货
     */
    private void isPickingEnd() {
        int size = checkDatas.size();
        int count = 0; // 计数器
        for(int i=0; i<size; i++) {
            MaterialBinningRecord mbr = checkDatas.get(i);
            if(mbr.getNumber() >= mbr.getRelationBillFQTY()) {
                count += 1;
            }
        }
        if(count == size) {
            toasts("已经装完货了，请保存！");
        }
    }

    /**
     * （条码表）删除返回的值
     */
    private void getBarCodeTable_delete(BarCodeTable bt) {
        if (bt != null) {
            int size = checkDatas.size();
            MaterialBinningRecord mbr = null;
            // 已装箱的物料
            if(size > 0) {
                // 相同的物料就+1，否则为1
                for(int i=0; i<size; i++) {
                    MaterialBinningRecord forMtl = checkDatas.get(i);
                    if(bt.getRelationBillNumber().equals(forMtl.getRelationBillNumber()) && bt.getEntryId() == forMtl.getEntryId()){
                        mbr = forMtl;

                        break;
                    }
                }
            }
            if(mbr == null) {
                Comm.showWarnDialog(mContext,"扫描物料不在该箱子中！");
                return;
            }
            // 把对象转成json字符串
            String strJson = JsonUtil.objectToString(mbr);
            // 添加到箱子并返回箱子的中的物料列表
            run_delete(strJson, bt.getBarcode());
        }
    }

    /**
     * 扫描物料之后的控制
     */
    private void getBarCodeTableAfter() {
//        setEnables(tvCustSel,R.drawable.back_style_gray3,false);
        setEnables(tvDeliverSel,R.drawable.back_style_gray3,false);
        tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = "34"; // 方案id    （有源单（生产订单）
        switch (curViewFlag) {
            case '1': // 箱码
                mUrl = Consts.getURL("boxBarCode/findBarcode");
                barcode = boxBarcode;
                break;
            case '2': // 生产订单扫码
                mUrl = Consts.getURL("prodOrder/findBarcode");
                barcode = prodOrderBarcode;
                strCaseId = "";
                break;
            case '3': // 物料扫码
//                mUrl = Consts.getURL("barCodeTable/findBarcode2ByParam");
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                strCaseId = "11,21,34";
                break;
            case '4': // 删除物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarcode2ByParam");
                barcode = mtlBarcode_del;
                break;
        }
        String boxId = boxBarCode != null ? String.valueOf(boxBarCode.getBoxId()) : "";
        FormBody formBody = new FormBody.Builder()
                .add("boxId", boxId)
                .add("barcode", barcode)
                .add("strCaseId", strCaseId)
                .add("sourceType","7") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
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
        String mUrl = Consts.getURL("materialBinningRecord/save2");
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
    private void run_delete(String json, String barcode) {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("materialBinningRecord/delete");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("strJson", json)
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
     * 开箱或者封箱（查询客户还有多少订单没有装箱）
     */
    private void run_modifyStatus() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("boxBarCode/modifyStatus2");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(boxBarCode.getId()))
                .add("status", String.valueOf(status))
                .add("customerNumber", String.valueOf(customer.getCustomerCode()))
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
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

}
