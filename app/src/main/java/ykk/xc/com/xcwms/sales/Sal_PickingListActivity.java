package ykk.xc.com.xcwms.sales;

import android.app.Activity;
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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.basics.DeliveryWay_DialogActivity;
import ykk.xc.com.xcwms.basics.PrintMainActivity;
import ykk.xc.com.xcwms.basics.StockPos_DialogActivity;
import ykk.xc.com.xcwms.basics.Stock_DialogActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.model.AssistInfo;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.model.Customer;
import ykk.xc.com.xcwms.model.EnumDict;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockPosition;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.model.sal.DeliOrder;
import ykk.xc.com.xcwms.model.sal.PickingList;
import ykk.xc.com.xcwms.sales.adapter.Sal_PickingListAdapter;
import ykk.xc.com.xcwms.util.JsonUtil;

/**
 * 拣货单界面
 */
public class Sal_PickingListActivity extends BaseActivity {

    @BindView(R.id.et_deliCode)
    EditText etDeliCode;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.tv_deliverSel)
    TextView tvDeliverSel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.rb_type1)
    RadioButton rbType1;
    @BindView(R.id.rb_type2)
    RadioButton rbType2;

    private Sal_PickingListActivity context = this;
    private static final int SEL_DELI = 11, SEL_STOCK2 = 12, SEL_STOCKP2 = 13;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int CODE1 = 1, CODE2 = 2, CODE20 = 20;
    private Customer cust; // 客户
    private Stock stock, stock2; // 仓库
    private StockPosition stockP, stockP2; // 库位
    private AssistInfo assist; // 辅助资料--发货方式
    private Sal_PickingListAdapter mAdapter;
    private List<PickingList> checkDatas = new ArrayList<>();
    private String stockBarcode, stockPBarcode, mtlBarcode, deliBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：发货订单, 4：物料
    private int curPos; // 当前行
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private char pickingType = '1'; // 拣货类型：1.拣货装箱，2.拣货装车

    // 消息处理
    private Sal_PickingListActivity.MyHandler mHandler = new Sal_PickingListActivity.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_PickingListActivity> mActivity;

        public MyHandler(Sal_PickingListActivity activity) {
            mActivity = new WeakReference<Sal_PickingListActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_PickingListActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset('0');

                        m.checkDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.context,"保存成功");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.context,"服务器繁忙，请稍候再试！");

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = null;
                        switch (m.curViewFlag) {
                            case '3': // 发货订单
                                List<DeliOrder> list = JsonUtil.strToList((String) msg.obj, DeliOrder.class);
                                Material tmpMtl = null;
                                for(int i=0, size=list.size(); i<size; i++) {
                                    Material mtl = list.get(i).getMtl();
                                    if(mtl.getStockPos() != null && mtl.getStockPos().getStockId() > 0) {
                                        tmpMtl = mtl;
                                        break;
                                    }
                                }
                                m.setTexts(m.etDeliCode, m.deliBarcode);
                                if(!m.smAfterCheck(tmpMtl)) return;
                                m.getDeliOrderAfter(list);

                                break;
                            case '4': // 物料
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.getMtlAfter(bt);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(CODE20, 200);
                        String errMsg = m.isNULLS((String) msg.obj);
                        if(errMsg.length() > 0) {
                            String message = JsonUtil.strToString(errMsg);
                            Comm.showWarnDialog(m.context, message);
                        } else {
                            Comm.showWarnDialog(m.context,"条码不存在，或者扫错了条码！");
                        }

                        break;
                    case CODE20: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
//                            case '1': // 仓库
//                                m.setTexts(m.etStock, m.stockBarcode);
//                                break;
//                            case '2': // 库位
//                                m.setTexts(m.etStockPos, m.stockPBarcode);
//                                break;
                            case '3': // 发货订单
                                m.setTexts(m.etDeliCode, m.deliBarcode);
                                break;
                            case '4': // 物料
                                m.setTexts(m.etMtlCode, m.mtlBarcode);
                                break;
                        }

                        break;
                    case SUCC3: // 判断是否存在返回
                        String strBarcode = JsonUtil.strToString((String) msg.obj);
                        String[] barcodeArr = strBarcode.split(",");
                        for (int i = 0, len = barcodeArr.length; i < len; i++) {
                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
                                // 判断扫码表和当前扫的码对比是否一样
                                if (barcodeArr[i].equals(m.checkDatas.get(j).getBarcode())) {
                                    Comm.showWarnDialog(m.context,"第" + (i + 1) + "行已拣货，不能重复操作！");
                                    return;
                                }
                            }
                        }

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                    case CODE1: // 清空数据
                        switch (m.curViewFlag) {
                            case '3': // 发货订单
                                m.etDeliCode.setText("");
                                m.deliBarcode = "";
                                break;
                            case '4': // 物料
                                m.etMtlCode.setText("");
                                m.mtlBarcode = "";
                                break;
                        }

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.sal_pickinglist;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Sal_PickingListAdapter(context, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Sal_PickingListAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, PickingList entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getPickingListNum()), "0.0", CODE2);
            }

            @Override
            public void onClick_selStock(View v, PickingList entity, int position) {
                Log.e("selStock", "行：" + position);
                curPos = position;

                showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
            }
        });

    }

    @Override
    public void initData() {
        hideSoftInputMode(etDeliCode);
        hideSoftInputMode(etMtlCode);
        getUserInfo();
        setFocusable(etDeliCode); // 物料代码获取焦点
//        setFocusable(etMtlCode); // 物料代码获取焦点

        // 得到默认仓库的值
        defaultStockVal = getXmlValues(spf(getResStr(R.string.saveSystemSet)), EnumDict.STOCKANDPOSTIONTDEFAULTSOURCEOFVALUE.name()).charAt(0);
        if(defaultStockVal == '2') {

            if(user.getStock() != null) {
                stock = user.getStock();
                stockBarcode = stock.getfName();
            }

            if(user.getStockPos() != null) {
                stockP = user.getStockPos();
                stockPBarcode = stockP.getFnumber();
            }
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_print, R.id.tv_deliverSel, R.id.btn_save, R.id.btn_clone, R.id.rb_type1, R.id.rb_type2})
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
            case R.id.tv_deliverSel: // 发货方式
                showForResult(DeliveryWay_DialogActivity.class, SEL_DELI, null);

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
//                run_findMatIsExistList();
                run_addScanningRecord();

                break;
            case R.id.btn_clone: // 重置
                hideKeyboard(getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(context);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetSon();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    resetSon();
                }

                break;
            case R.id.rb_type1: // 拣货类型--拣货装箱
                pickingType = '1';
                clickRadioChange();
                for(int i=0; i<checkDatas.size(); i++) {
                    checkDatas.get(i).setPickingType('1');
                }
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.rb_type2: // 拣货类型--拣货装车
                pickingType = '2';
                clickRadioChange();
                for(int i=0; i<checkDatas.size(); i++) {
                    checkDatas.get(i).setPickingType('2');
                }
                mAdapter.notifyDataSetChanged();

                break;
        }
    }

    /**
     * 点击装箱类型后改变的
     */
    private void clickRadioChange() {
        rbType1.setTextColor(Color.parseColor(rbType1.isChecked() ? "#FFFFFF" : "#666666"));
        rbType2.setTextColor(Color.parseColor(rbType2.isChecked() ? "#FFFFFF" : "#666666"));
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (assist == null) {
            Comm.showWarnDialog(context,"请选择发货方式！");
            return false;
        }
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(context,"请先插入行！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            PickingList pl = checkDatas.get(i);
            if (pl.getPickingListNum() == 0) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（拣货数）必须大于0！");
                return false;
            }
            if ((pl.getMtl().getMtlPack() == null || pl.getMtl().getMtlPack().getIsMinNumberPack() == 0) && pl.getPickingListNum() > pl.getDeliFremainoutqty()) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（拣货数）不能大于（订单数）！");
                return false;
            }
            // 启用批次号
            if(pl.getMtl().getIsBatchManager() > 0 && isNULLS(pl.getBatchNo()).length() == 0) {
                curPos = i;
                writeBatchDialog();
                return false;
            }
            if(pl.getStock() == null) {
                Comm.showWarnDialog(context,"第"+(i+1)+"行请选择仓库！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_deliCode, R.id.et_mtlCode})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
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
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_deliCode: // 来源单号
                            curViewFlag = '3';
                            String sourceNo = getValues(etDeliCode).trim();
//                            if (!smBefore('0')) { // 扫码之前的判断
//                                mHandler.sendEmptyMessageDelayed(CODE1, 200);
//                                return false;
//                            }
                            if (isKeyDownEnter(sourceNo, event, keyCode)) {
                                if (deliBarcode != null && deliBarcode.length() > 0) {
                                    if (deliBarcode.equals(sourceNo)) {
                                        deliBarcode = sourceNo;
                                    } else {
                                        String tmp = sourceNo.replaceFirst(deliBarcode, "");
                                        deliBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    deliBarcode = sourceNo.replace("\n", "");
                                }
                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_mtlCode: // 物料
                            curViewFlag = '4';
                            String deliCode = getValues(etDeliCode).trim();
                            String matNo = getValues(etMtlCode).trim();
                            if (deliCode.length() == 0) { // 扫码之前的判断
                                mHandler.sendEmptyMessageDelayed(CODE1, 200);
                                return false;
                            }
                            if (isKeyDownEnter(matNo, event, keyCode)) {
                                if (mtlBarcode != null && mtlBarcode.length() > 0) {
                                    if (mtlBarcode.equals(matNo)) {
                                        mtlBarcode = matNo;
                                    } else {
                                        String tmp = matNo.replaceFirst(mtlBarcode, "");
                                        mtlBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    mtlBarcode = matNo.replace("\n", "");
                                }

                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                    }
                }
                return false;
            }
        };
        etDeliCode.setOnKeyListener(keyListener);
        etMtlCode.setOnKeyListener(keyListener);
    }

    /**
     * 是否按了回车键
     */
    private boolean isKeyDownEnter(String val, KeyEvent event, int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (val.length() == 0) {
                Comm.showWarnDialog(context, "请扫码条码！");
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 0：重置全部，1：重置物料部分
     *
     * @param flag
     */
    private void reset(char flag) {
        // 清空物料信息
        etDeliCode.setText("");
        etMtlCode.setText(""); // 物料代码
        tvCustSel.setText("客户：");
        cust = null;
        deliBarcode = null;
        mtlBarcode = null;
        setFocusable(etDeliCode);

    }

    private void resetSon() {
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvCustSel.setText("客户：");
        tvDeliverSel.setText("");
        rbType1.setEnabled(true);
        rbType2.setEnabled(true);
        rbType1.setChecked(true);
        clickRadioChange();
        pickingType = '1';
        stock = null;
        stockP = null;
        assist = null;
        cust = null;
        curViewFlag = '1';
        stockBarcode = null;
        stockPBarcode = null;
        deliBarcode = null;
        mtlBarcode = null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DELI: //查询发货方式	返回
                if (resultCode == RESULT_OK) {
                    assist = (AssistInfo) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DELI", assist.getfName());
                    if (assist != null) {
                        tvDeliverSel.setText(assist.getfName());
                        if(checkDatas.size() > 0) {
                            int size = checkDatas.size();
                            for(int i=0; i<size; i++) {
                                PickingList pl = checkDatas.get(i);
                                pl.setDeliveryWay(assist.getfName());
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }

                break;
            case CODE2: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setPickingListNum(num);
//                        checkDatas.get(curPos).setDeliFremainoutqty(num);
                        mAdapter.notifyDataSetChanged();
                        isPickingEnd();
                    }
                }

                break;
            case SEL_STOCK2: //行事件选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock2 = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCK2", stock2.getfName());
                    // 启用了库位管理
                    if (stock2.isStorageLocation()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("stockId", stock2.getfStockid());
                        showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, bundle);
                    } else {
                        PickingList pk = checkDatas.get(curPos);
                        pk.setStockId(stock2.getfStockid());
                        pk.setStockNumber(stock2.getfNumber());
                        pk.setStockName(stock2.getfName());
                        pk.setStock(stock2);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    PickingList pk = checkDatas.get(curPos);
                    pk.setStockId(stock2.getfStockid());
                    pk.setStockNumber(stock2.getfNumber());
                    pk.setStockName(stock2.getfName());
                    pk.setStock(stock2);

                    pk.setStockPositionId(stockP2.getId());
                    pk.setStockPositionNumber(stockP2.getFnumber());
                    pk.setStockPositionName(stockP2.getFname());
                    pk.setStockPosition(stockP2);
                    mAdapter.notifyDataSetChanged();
                }

                break;
        }
    }

    /**
     * 得到物料数据之后，判断库位是否为空
     */
    private boolean smAfterCheck(Material mtl) {
        if(defaultStockVal == '1' && mtl != null && mtl.getStockPos() != null && mtl.getStockPos().getStockId() > 0) {
            stock = mtl.getStock();
            stockP = mtl.getStockPos();
        } else {
            setTexts(etDeliCode, deliBarcode);
        }
        return true;
    }

    /**
     * 来源订单 判断数据
     */
    private void getMtlAfter(BarCodeTable bt) {
        setTexts(etMtlCode, mtlBarcode);
        Material tmpMtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
        bt.setMtl(tmpMtl);
        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            PickingList pl = checkDatas.get(i);
            Material mtl = pl.getMtl();
            // 如果扫码相同
            if (bt.getMaterialId() == mtl.getfMaterialId()) {
                isFlag = true;

                double fqty = 1;
                // 计量单位数量
                if(tmpMtl.getCalculateFqty() > 0) fqty = tmpMtl.getCalculateFqty();
                // 未启用序列号
                if (tmpMtl.getIsSnManager() == 0) {
                    // 发货数大于拣货数
                    if (pl.getDeliFremainoutqty() > pl.getPickingListNum()) {
                        // 如果扫的是物料包装条码，就显示个数
                        double number = 0;
                        if(bt != null) number = bt.getMaterialCalculateNumber();

                        if(number > 0) {
                            pl.setPickingListNum(pl.getPickingListNum() + (number*fqty));
                        } else {
                            pl.setPickingListNum(pl.getPickingListNum() + fqty);
                        }
                        pl.setBatchNo(bt.getBatchCode());
                        pl.setSnNo(bt.getSnCode());

                    // 启用了最小包装
                    } else if(mtl.getMtlPack() != null && mtl.getMtlPack().getIsMinNumberPack() == 1) {
                        if(mtl.getMtlPack().getIsMinNumberPack() == 1) {
                            // 如果拣货数小于订单数，就加数量
                            if(pl.getPickingListNum() < pl.getDeliFremainoutqty()) {
                                pl.setPickingListNum(pl.getPickingListNum() + fqty);
                            } else {
                                Comm.showWarnDialog(context, "第" + (i + 1) + "行，已经达到最小包装发货数量！");
                                return;
                            }
                        }

                    } else if ((mtl.getMtlPack() == null || mtl.getMtlPack().getIsMinNumberPack() == 0) && pl.getPickingListNum() > pl.getDeliFremainoutqty()) {
                        // 数量已满
                        Comm.showWarnDialog(context, "第" + (i + 1) + "行，（拣货数）不能大于（订单数）！");
                        return;
                    }
                } else {
                    if (pl.getPickingListNum() == pl.getDeliFremainoutqty()) {
                        Comm.showWarnDialog(context, "第" + (i + 1) + "行，已捡完！");
                        return;
                    }
                    List<String> list = pl.getListBarcode();
                    if(list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(context,"该物料条码已在拣货行中，请扫描未使用过的条码！");
                        return;
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
                        if((k+1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k)+",");
                    }
                    pl.setBatchNo(bt.getBatchCode());
                    pl.setSnNo(bt.getSnCode());
                    pl.setListBarcode(list);
                    pl.setStrBarcodes(sb.toString());
                    pl.setPickingListNum(pl.getPickingListNum() + 1);
                }
                mAdapter.notifyDataSetChanged();
                isPickingEnd();
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(context, "该物料与订单不匹配！");
        }
    }

    /**
     * 是否已经捡完货
     */
    private void isPickingEnd() {
        int size = checkDatas.size();
        int count = 0; // 计数器
        for(int i=0; i<size; i++) {
            PickingList p = checkDatas.get(i);
            if(p.getPickingListNum() >= p.getDeliFremainoutqty()) {
                count += 1;
            }
        }
        if(count == size) {
            toasts("已经捡完货了，请保存！");
        }
    }

    /**
     * 得到发货订单的数据
     */
    private void getDeliOrderAfter(List<DeliOrder> list) {
        checkDatas.clear(); // 清空
        int size = list.size();
        for(int i=0; i<size; i++) {
            PickingList pl = new PickingList();
            DeliOrder deliOrder = list.get(i);

            pl.setfId(deliOrder.getfId());
            pl.setFbillno(deliOrder.getFbillno());
            pl.setDeliDate(deliOrder.getDeliDate());
            pl.setCustId(deliOrder.getCustId());
            pl.setCustNumber(deliOrder.getCustNumber());
            pl.setCustName(deliOrder.getCustName());
            pl.setDeliOrgId(deliOrder.getDeliOrgId());
            pl.setDeliOrgNumber(deliOrder.getDeliOrgNumber());
            pl.setDeliOrgName(deliOrder.getDeliOrgName());
            pl.setMtl(deliOrder.getMtl());
            pl.setMtlId(deliOrder.getMtlId());
            pl.setMtlFnumber(deliOrder.getMtlFnumber());
            pl.setMtlFname(deliOrder.getMtlFname());
            pl.setMtlUnitName(deliOrder.getMtlUnitName());
            if(stock != null) {
                pl.setStockId(stock.getfStockid());
                pl.setStockNumber(stock.getfNumber());
                pl.setStockName(stock.getfNumber());
                pl.setStock(stock);
            }
            if(stockP != null) {
                pl.setStockPositionId(stockP.getId());
                pl.setStockPositionNumber(stockP.getFnumber());
                pl.setStockPositionName(stockP.getFname());
                pl.setStockPosition(stockP);
            }
            pl.setDeliFqty(deliOrder.getDeliFqty());
            pl.setDeliFremainoutqty(deliOrder.getDeliFremainoutqty());
//            pl.setDeliveryWay(deliOrder.getDeliveryWay());
            pl.setEntryId(deliOrder.getEntryId());
//            pl.setBatchNo(bt.getBatchCode());
//            pl.setSnNo(bt.getSnCode());
//            pl.setBarcode(bt.getBarcode());
            // 显示客户
            if(cust == null) cust = new Customer();
            cust.setFcustId(deliOrder.getCustId());
            cust.setCustomerCode(deliOrder.getCustNumber());
            cust.setCustomerName(deliOrder.getCustName());
            tvCustSel.setText("客户："+deliOrder.getCustName());
            // 显示交货方式
            if(assist == null && isNULLS(deliOrder.getDeliveryWay()).length() > 0) {
                assist = new AssistInfo();
                assist.setfName(deliOrder.getDeliveryWay());
                tvDeliverSel.setText(deliOrder.getDeliveryWay());
                pl.setDeliveryWay(deliOrder.getDeliveryWay());

            } else if(assist != null) {
                tvDeliverSel.setText(assist.getfName());
                pl.setDeliveryWay(assist.getfName());
            }

            pl.setMtl(deliOrder.getMtl());
            pl.setPickingListNum(0);
            pl.setCreateUserId(user.getId());
            pl.setCreateUserName(user.getUsername());
            pl.setPickingType(pickingType);
            pl.setSalOrderNo(deliOrder.getSalOrderNo());
            pl.setSalOrderNoEntryId(deliOrder.getSalOrderEntryId());
            // 物料是否启用序列号
            if(deliOrder.getMtl().getIsSnManager() == 1) {
                pl.setListBarcode(new ArrayList<String>());
            }
            pl.setStrBarcodes("");

            checkDatas.add(pl);
        }

        setFocusable(etMtlCode);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 保存方法
     */
    private void run_addScanningRecord() {
        showLoadDialog("保存中...");
        getUserInfo();

        String mJson = JsonUtil.objectToString(checkDatas);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = Consts.getURL("pickingList/add");
        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
//                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
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
                Log.e("run_addScanningRecord --> onResponse", result);
                mHandler.sendEmptyMessage(SUCC1);
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        String isList = ""; // 是否根据单据查询全部
        switch (curViewFlag) {
            case '1': // 仓库
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockBarcode;
                isStockLong = false;
                strCaseId = "12";
                break;
            case '2': // 库位
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockPBarcode;
                strCaseId = "14";
                break;
            case '3': // 发货订单
                mUrl = Consts.getURL("deliverynotice/findBarcode");
                barcode = deliBarcode;
                strCaseId = "";
                break;
            case '4': // 物料
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                strCaseId = "11,21";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", strCaseId)
                .add("isList", String.valueOf(isList))
                .add("barcode", barcode)
                .add("isDefaultStock","1") // 查询默认仓库和库位
                .add("sourceType","6") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
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
                    Message msg = mHandler.obtainMessage(UNSUCC2, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
                Log.e("run_smGetDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 判断表中存在该物料
     */
    private void run_findMatIsExistList() {
        showLoadDialog("加载中...");
        StringBuilder strBarcode = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            PickingList sr2 = checkDatas.get(i);
            if(isNULLS(sr2.getBarcode()).length() > 0) {
                if((i+1) == size) strBarcode.append(sr2.getBarcode());
                else strBarcode.append(sr2.getBarcode() + ",");
            }
        }
        String mUrl = Consts.getURL("pickingList/findMatIsExistList");
        FormBody formBody = new FormBody.Builder()
                .add("orderType", "JH") // 单据类型（CG代表采购订单，XS销售订单,生产PD，JH拣货单）
                .add("strBarcode", strBarcode.toString())
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
                Log.e("run_findMatIsExistList --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 输入批次的dialog
     */
    private void writeBatchDialog() {
        View v = context.getLayoutInflater().inflate(R.layout.pur_sel_prod_order_item2, null);
        final AlertDialog batchDialog = new AlertDialog.Builder(context).setView(v).create();
        // 初始化id
        final EditText etBatch = (EditText) v.findViewById(R.id.et_batch);
        Button btnClose = (Button) v.findViewById(R.id.btn_close);
        Button btnConfirm = (Button) v.findViewById(R.id.btn_confirm);

        // 关闭
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                batchDialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String batch = getValues(etBatch).trim();
                if(batch.length() == 0) {
                    Comm.showWarnDialog(context,"请输入批次！");
                    return;
                }
                checkDatas.get(curPos).setBatchNo(batch);
                if(checkDatas.get(curPos).getMtl().getIsSnManager() > 0) {
                    checkDatas.get(curPos).setSnNo(batch+10000);
                }
                mAdapter.notifyDataSetChanged();
                hideKeyboard(v);
                batchDialog.dismiss();
            }
        });


        Window window = batchDialog.getWindow();
        batchDialog.setCancelable(false);
        batchDialog.show();
        window.setGravity(Gravity.CENTER);
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) {
            user = showUserByXml();
        }
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
