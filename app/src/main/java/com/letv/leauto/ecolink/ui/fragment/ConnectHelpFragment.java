package com.letv.leauto.ecolink.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.page.CommonPagerAdapter;
import com.letv.leauto.ecolink.ui.page.NewHelpPage;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 2017/2/14.
 */
public class ConnectHelpFragment extends BaseFragment {
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.dot)
    LinearLayout mDotLayout;
    private ArrayList<BasePage> mPages;
    private CommonPagerAdapter mPagerAdapter;
    @Bind(R.id.iv_back)
    ImageView mBackView;
    private ImageView[] dots;
    private int mCurIndex;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_help_connect, null);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
    private void initView(){
        mPages=new ArrayList<>();
        mPages.add(new NewHelpPage(mContext,0));
        mPages.add(new NewHelpPage(mContext,1));
        mPagerAdapter=new CommonPagerAdapter(mContext,mPages,null);
        mViewPager.setAdapter(mPagerAdapter);
        mPages.get(0).initData();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPages.get(position).initData();
                setCurrentDot(position);
                mCurIndex=position;


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new NewHelpFragment()).commitAllowingStateLoss();
            }
        });
        for (int i = 0; i < mPages.size(); i++) {
            mDotLayout.addView(View.inflate(mContext, R.layout.advertisement_board_dot, null));
        }
        initDots(mDotLayout);

    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new NewHelpFragment()).commitAllowingStateLoss();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void initDots(LinearLayout ll_dot) {
        dots = new ImageView[mPages.size()];

        // 循环取得小点图片
        for (int i = 0; i < mPages.size(); i++) {
            dots[i] = (ImageView) ll_dot.getChildAt(i);
            dots[i].setEnabled(true);// 都设为灰色
        }

        mCurIndex = 0;
        dots[mCurIndex].setEnabled(false);// 设置为选中状态
    }
    private void setCurrentDot(int position) {
        if (position < 0 || position > mPages.size() - 1
                || mCurIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[mCurIndex].setEnabled(true);

        mCurIndex = position;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPages!=null){
            for (BasePage page :mPages){
                page.destory();
            }
            mPages.clear();
        }
    }
}
