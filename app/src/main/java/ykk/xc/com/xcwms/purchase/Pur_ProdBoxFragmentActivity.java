package ykk.xc.com.xcwms.purchase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.basics.PrintFragmentsActivity;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.util.MyViewPager;


public class Pur_ProdBoxFragmentActivity extends BaseActivity {

    @BindView(R.id.viewRadio1)
    View radio1;
    @BindView(R.id.viewRadio2)
    View radio2;
    @BindView(R.id.lin_tab1)
    LinearLayout linTab1;
    @BindView(R.id.lin_tab2)
    LinearLayout linTab2;
    @BindView(R.id.viewPager)
    MyViewPager viewPager;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private Pur_ProdBoxFragmentActivity context = this;
    private View curRadio;
//    private Customer customer; // 客户

    @Override
    public int setLayoutResID() {
        return R.layout.pur_prod_box;
    }

    @Override
    public void initData() {
//        Bundle bundle = context.getIntent().getExtras();
//        if (bundle != null) {
//            customer = (Customer) bundle.getSerializable("customer");
//        }

        curRadio = radio1;
        List<Fragment> listFragment = new ArrayList<Fragment>();
//        Bundle bundle2 = new Bundle();
//        bundle2.putSerializable("customer", customer);
        Pur_ProdBoxBatchFragment1 fragment1 = new Pur_ProdBoxBatchFragment1();
//        fragment1.setArguments(bundle2); // 传参数
        Pur_ProdBoxUnBatchFragment2 fragment2 = new Pur_ProdBoxUnBatchFragment2();
//        fragment2.setArguments(bundle2); // 传参数

        listFragment.add(fragment2);
        listFragment.add(fragment1);
        viewPager.setScanScroll(false); // 禁止左右滑动
        //ViewPager设置适配器
        viewPager.setAdapter(new Pur_ProdBoxFragmentAdapter(getSupportFragmentManager(), listFragment));
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(0);


        //ViewPager页面切换监听
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position) {
//                    case 0:
//                        tabSelected(radio1);
//                        tvTitle.setText("生产装箱-批量");
//                        viewPager.setCurrentItem(0, false);
//                        break;
//                    case 1:
//                        tabSelected(radio2);
//                        tvTitle.setText("生产装箱-非批量");
//                        viewPager.setCurrentItem(1, false);
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
//            customer = bundle.getParcelable("customer");
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    @OnClick({R.id.btn_close, R.id.btn_print, R.id.lin_tab1, R.id.lin_tab2})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                context.finish();

                break;
            case R.id.btn_print: // 打印
                show(PrintFragmentsActivity.class,null);

                break;
            case R.id.lin_tab2:
                tabSelected(radio2);
                tvTitle.setText("生产装箱-批量");
                viewPager.setCurrentItem(1, false);

                break;
            case R.id.lin_tab1:
                tabSelected(radio1);
                tvTitle.setText("生产装箱-非批量");
                viewPager.setCurrentItem(0, false);

                break;
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish();
        }
        return false;
    }

}
