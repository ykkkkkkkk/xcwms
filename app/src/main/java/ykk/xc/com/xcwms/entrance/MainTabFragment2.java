package ykk.xc.com.xcwms.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.produce.Prod_BoxMainActivity;
import ykk.xc.com.xcwms.produce.Prod_InMainActivity;
import ykk.xc.com.xcwms.produce.Prod_ProcedureBindingActivity;
import ykk.xc.com.xcwms.produce.Prod_ProcedureReportActivity;
import ykk.xc.com.xcwms.produce.Prod_ProcessSearchActivity;

public class MainTabFragment2 extends BaseFragment {

    public MainTabFragment2() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item2, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 生产装箱
                show(Prod_BoxMainActivity.class, null);

                break;
            case R.id.relative2: // 生产入库
                show(Prod_InMainActivity.class,null);

                break;
            case R.id.relative3: // 工艺查看
                show(Prod_ProcessSearchActivity.class,null);

                break;
            case R.id.relative4: // 工序汇报
                show(Prod_ProcedureReportActivity.class,null);

                break;
            case R.id.relative5: // 工序绑定
                show(Prod_ProcedureBindingActivity.class,null);

                break;
        }
    }
}
