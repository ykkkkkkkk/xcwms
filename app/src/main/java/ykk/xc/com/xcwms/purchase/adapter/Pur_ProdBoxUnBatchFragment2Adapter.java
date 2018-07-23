package ykk.xc.com.xcwms.purchase.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.model.MaterialBinningRecord;
import ykk.xc.com.xcwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Pur_ProdBoxUnBatchFragment2Adapter extends BaseArrayRecyclerAdapter<MaterialBinningRecord> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Pur_ProdBoxUnBatchFragment2Adapter(Activity context, List<MaterialBinningRecord> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.pur_prod_box_unbatch_fragment2_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final MaterialBinningRecord entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mats = holder.obtainView(R.id.tv_mats);
        TextView tv_deliWay = holder.obtainView(R.id.tv_deliWay);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        TextView tv_custName = holder.obtainView(R.id.tv_custName);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mats.setText(entity.getMaterial().getfNumber()+"\n"+entity.getMaterial().getfName());
        String deliWay = Comm.isNULLS(entity.getDeliveryWay());
        tv_deliWay.setText(deliWay);
        // 是否启用批次管理和序列号管理
        tv_num.setText(df.format(entity.getNumber()));
        if(entity.getMaterial().getIsSnManager() == 1) {
            tv_num.setBackgroundResource(R.drawable.back_style_gray2a);
            tv_num.setEnabled(false);
        } else {
            tv_num.setBackgroundResource(R.drawable.back_style_blue2);
            tv_num.setEnabled(true);
        }
        tv_custName.setText(entity.getCustomer().getCustomerName());

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_num: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                }
            }
        };
        tv_num.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, MaterialBinningRecord entity, int position);
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
