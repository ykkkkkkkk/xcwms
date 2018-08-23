package ykk.xc.com.xcwms.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.purchase.Prod_InActivity;
import ykk.xc.com.xcwms.purchase.Pur_ProdBoxFragmentActivity;

public class MainTabFragment2 extends BaseFragment {

    public MainTabFragment2() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item2, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 生产装箱
                show(Pur_ProdBoxFragmentActivity.class, null);

                break;
            case R.id.relative2: // 生产入库
                show(Prod_InActivity.class,null);

                break;
            case R.id.relative3: //

                break;
            case R.id.relative4: //

                break;
        }
    }
}
