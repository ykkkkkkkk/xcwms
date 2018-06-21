package ykk.xc.com.xcwms.purchase.adapter;

import android.app.Activity;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.model.pur.PurPoOrder;
import ykk.xc.com.xcwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Pur_OrderSearchAdapter extends BaseArrayRecyclerAdapter<PurPoOrder> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<PurPoOrder> datas;

    public Pur_OrderSearchAdapter(Activity context, List<PurPoOrder> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.pur_order_search_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, PurPoOrder entity, final int pos) {
        // 初始化id
        TextView tv1 = holder.obtainView(R.id.tv1);
        TextView tv2 = holder.obtainView(R.id.tv2);
        TextView tv3 = holder.obtainView(R.id.tv3);
        TextView tv4 = holder.obtainView(R.id.tv4);
        TextView tv5 = holder.obtainView(R.id.tv5);
        TextView tv6 = holder.obtainView(R.id.tv6);
        TextView tv7 = holder.obtainView(R.id.tv7);
        TextView tv8 = holder.obtainView(R.id.tv8);
        TextView tv9 = holder.obtainView(R.id.tv9);
        // 赋值
        tv1.setText(String.valueOf(pos + 1));
        tv2.setText(entity.getFbillno());
        tv3.setText(entity.getSupplierName());
        tv4.setText(entity.getPurPerson()+"\n"+entity.getPurOrgName());
        tv5.setText(entity.getDeptName());
        tv6.setText(entity.getPoFdate());
        tv7.setText(entity.getMtlFnumber()+"\n"+entity.getMtlFname()+"\n"+entity.getMtlType());
        tv8.setText(entity.getPoFdate()+""+entity.getUnitFname()+"\n"+entity.getPoFstockinqty());
        tv9.setText(entity.getPurOrgName());
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(PurPoOrder entity, int position);
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
