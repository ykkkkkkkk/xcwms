package ykk.xc.com.xcwms.basics.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.model.BarCodeTable;
import ykk.xc.com.xcwms.util.basehelper.BaseArrayRecyclerAdapter;

/**
 * 物料包装适配器
 */
public class PrintFragment4Adapter extends BaseArrayRecyclerAdapter<BarCodeTable> {
//
    private Activity context;
    private MyCallBack callBack;

    public PrintFragment4Adapter(Activity context, List<BarCodeTable> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_print_fragment4_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final BarCodeTable entity, final int pos) {
            // 初始化id
            TextView tv_row = holder.obtainView(R.id.tv_row);
            TextView tv_fnumber = holder.obtainView(R.id.tv_fnumber);
            TextView tv_fname = holder.obtainView(R.id.tv_fname);
            TextView tv_fModel = holder.obtainView(R.id.tv_fModel);
            TextView tv_print = holder.obtainView(R.id.tv_print);
            // 赋值
            tv_row.setText(String.valueOf(pos + 1));
            tv_fnumber.setText(entity.getMaterialNumber());
            tv_fname.setText(entity.getMaterialName());
            tv_fModel.setText(entity.getMaterialSize());

            View.OnClickListener click = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.tv_print: // 打印
                            if(callBack != null) {
                                callBack.onPrint(entity, pos);
                            }

                            break;
                    }
                }
            };
            tv_print.setOnClickListener(click);

            // 长按打印多次
            tv_print.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(callBack != null) {
                        callBack.onPrint2(entity, pos);
                    }
                    return true;
                }
            });
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onPrint(BarCodeTable entity, int position);
        void onPrint2(BarCodeTable entity, int position);
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
