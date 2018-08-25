package ykk.xc.com.xcwms.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.purchase.Prod_InActivity;
import ykk.xc.com.xcwms.purchase.Pur_InFragmentsActivity;
import ykk.xc.com.xcwms.purchase.Pur_OrderSearchActivity;
import ykk.xc.com.xcwms.purchase.Pur_ProdBoxFragmentActivity;

public class MainTabFragment1 extends BaseFragment {


    @BindView(R.id.tab1)
    TextView tab1;
    @BindView(R.id.relative1)
    RelativeLayout relative1;
    @BindView(R.id.tab2)
    TextView tab2;
    @BindView(R.id.relative2)
    RelativeLayout relative2;
    @BindView(R.id.tab3)
    TextView tab3;
    @BindView(R.id.relative3)
    RelativeLayout relative3;
    @BindView(R.id.lin_tab)
    LinearLayout linTab;

    public MainTabFragment1() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item1, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 采购订单
                show(Pur_OrderSearchActivity.class, null);

                break;
            case R.id.relative2: // 采购入库
//                show(Pur_InActivity.class, null);
                show(Pur_InFragmentsActivity.class, null);

                break;
            case R.id.relative3: // 生产入库
                show(Prod_InActivity.class,null);

                break;
            case R.id.relative4: // 生产装箱
                show(Pur_ProdBoxFragmentActivity.class, null);

                break;
        }
    }
}
