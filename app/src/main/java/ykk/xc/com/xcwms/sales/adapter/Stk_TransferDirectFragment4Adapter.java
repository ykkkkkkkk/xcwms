package ykk.xc.com.xcwms.sales.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Stk_TransferDirectFragment4Adapter extends BaseArrayRecyclerAdapter<ScanningRecord2> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Stk_TransferDirectFragment4Adapter(Activity context, List<ScanningRecord2> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.stk_transferdirect_fragment4_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final ScanningRecord2 entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mats = holder.obtainView(R.id.tv_mats);
        TextView tv_batch_seqNo = holder.obtainView(R.id.tv_batch_seqNo);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        TextView tv_stockAP = holder.obtainView(R.id.tv_stockAP);
        TextView tv_stockAP2 = holder.obtainView(R.id.tv_stockAP2);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mats.setText(entity.getMtl().getfNumber()+"\n"+entity.getMtl().getfName()+"\n"+entity.getMtl().getMaterialSize());
        // 是否启用序列号
        if(entity.getMtl().getIsSnManager() == 1) {
            tv_nums.setEnabled(false);
            tv_nums.setBackgroundResource(R.drawable.back_style_gray3b);
        } else {
            tv_nums.setEnabled(true);
            tv_nums.setBackgroundResource(R.drawable.back_style_blue2);
        }
        String batchNo = Comm.isNULLS(entity.getBatchno());
        batchNo = batchNo.length() == 0 ? "无" : batchNo;
        String seqNo = Comm.isNULLS(entity.getSequenceNo());
        seqNo = seqNo.length() == 0 ? "无" : seqNo;
        tv_batch_seqNo.setText(batchNo+"\n"+seqNo);
        double stockqty = entity.getStockqty();
//        tv_nums.setText(Html.fromHtml(df.format(entity.getFqty())+"/<font color='#FF4400'>"+entity.getCoveQty()+"</font><br><font color='#009900'>"+df.format(stockqty)+"</font>"));
        tv_nums.setText(Html.fromHtml(df.format(entity.getUsableFqty())+"<br><font color='#009900'>"+df.format(stockqty)+"</font>"));
        // 调出仓库
        if(entity.getStockPos() != null) {
            tv_stockAP.setText(entity.getStockName()+"\n"+entity.getStockPos().getFnumber());
        } else if(entity.getStock() != null) {
            tv_stockAP.setText(entity.getStockName());
        } else {
            tv_stockAP.setText("");
        }
        // 调入仓库
        if(entity.getStockPos2() != null) {
            tv_stockAP2.setText(entity.getStock2().getfName()+"\n"+entity.getStockPos2().getFnumber());
        } else if(entity.getStock2() != null) {
            tv_stockAP2.setText(entity.getStock2().getfName());
        } else {
            tv_stockAP2.setText("");
        }

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_nums: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                    case R.id.tv_stockAP: // 选择调出仓库
                        if(callBack != null) {
                            callBack.onClick_selStock(v, entity, pos,false);
                        }

                        break;
                    case R.id.tv_stockAP2: // 选择调入仓库
                        if(callBack != null) {
                            callBack.onClick_selStock(v, entity, pos,true);
                        }

                        break;
                }
            }
        };
        tv_nums.setOnClickListener(click);
        tv_stockAP.setOnClickListener(click);
        tv_stockAP2.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, ScanningRecord2 entity, int position);
        void onClick_selStock(View v, ScanningRecord2 entity, int position, boolean isInStock);
        void onClick_del(ScanningRecord2 entity, int position);
    }

    /*之下的方法都是为了方便操作，并不是必须的*/

    //在指定位置插入，原位置的向后移动一格
//    public boolean addItem(int position, String msg) {
//        if (position < datas.size() && position >= 0) {
//            datas.add(position, msg);
//            notifyItemInserted(position);
//            return true;
//        }
//        return false;
//    }
//
//    //去除指定位置的子项
//    public boolean removeItem(int position) {
//        if (position < datas.size() && position >= 0) {
//            datas.remove(position);
//            notifyItemRemoved(position);
//            return true;
//        }
//        return false;
//    }

    //清空显示数据
//    public void clearAll() {
//        datas.clear();
//        notifyDataSetChanged();
//    }


}
