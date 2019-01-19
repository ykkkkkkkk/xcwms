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
import ykk.xc.com.xcwms.sales.Sal_BoxActivity;
import ykk.xc.com.xcwms.sales.Sal_OrderSearchActivity;
import ykk.xc.com.xcwms.sales.Sal_OutActivity;
import ykk.xc.com.xcwms.sales.Sal_OutMainActivity;
import ykk.xc.com.xcwms.sales.Sal_PickingListActivity;
import ykk.xc.com.xcwms.sales.Sal_RecombinationActivity;
import ykk.xc.com.xcwms.sales.Stk_TransferDirectMainActivity;

public class MainTabFragment3 extends BaseFragment {


    @BindView(R.id.tvImg1)
    TextView tvImg1;
    @BindView(R.id.tab1)
    TextView tab1;
    @BindView(R.id.relative1)
    RelativeLayout relative1;
    @BindView(R.id.tvImg2)
    TextView tvImg2;
    @BindView(R.id.tab2)
    TextView tab2;
    @BindView(R.id.relative2)
    RelativeLayout relative2;
    @BindView(R.id.tvImg3)
    TextView tvImg3;
    @BindView(R.id.tab3)
    TextView tab3;
    @BindView(R.id.relative3)
    RelativeLayout relative3;
    @BindView(R.id.lin_tab)
    LinearLayout linTab;
    @BindView(R.id.tvImg4)
    TextView tvImg4;
    @BindView(R.id.tab4)
    TextView tab4;
    @BindView(R.id.relative4)
    RelativeLayout relative4;
    @BindView(R.id.tvImg5)
    TextView tvImg5;
    @BindView(R.id.tab5)
    TextView tab5;
    @BindView(R.id.relative5)
    RelativeLayout relative5;
    @BindView(R.id.tvImg6)
    TextView tvImg6;
    @BindView(R.id.tab6)
    TextView tab6;
    @BindView(R.id.relative6)
    RelativeLayout relative6;
    @BindView(R.id.lin_tab2)
    LinearLayout linTab2;

    public MainTabFragment3() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item3, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 销售订单
                show(Sal_OrderSearchActivity.class, null);

                break;
            case R.id.relative2: // 销售出库
                show(Sal_OutMainActivity.class, null);

                break;
            case R.id.relative3: // 直接调拨
                show(Stk_TransferDirectMainActivity.class, null);

                break;
            case R.id.relative4: // 生产领料
                show(Sal_PickingListActivity.class, null);

                break;
            case R.id.relative5: // 复核单
                show(Sal_RecombinationActivity.class, null);

                break;
            case R.id.relative6: // 销售装箱
                show(Sal_BoxActivity.class, null);

                break;
        }
    }
}
