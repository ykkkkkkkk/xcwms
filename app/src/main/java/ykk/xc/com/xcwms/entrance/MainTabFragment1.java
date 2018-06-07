package ykk.xc.com.xcwms.entrance;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.purchase.Pur_InActivity;
import ykk.xc.com.xcwms.util.LoadingDialog;

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
    Unbinder unbinder;

    public MainTabFragment1() {
    }

    private LoadingDialog mLoadDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.aa_main_item1, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1:
                mLoadDialog = new LoadingDialog(getActivity(), "连接服务器...", true);
                break;
            case R.id.relative2:
//                show(getActivity(), Ware_Pur_InActivity.class, null);
                Intent intent = new Intent(getContext(), Pur_InActivity.class);
                startActivity(intent);
                break;
            case R.id.relative3:
                mLoadDialog = new LoadingDialog(getActivity(), "连接服务器...", true);
                break;
        }
    }
}
