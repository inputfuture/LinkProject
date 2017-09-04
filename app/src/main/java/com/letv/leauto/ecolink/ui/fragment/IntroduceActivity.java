package com.letv.leauto.ecolink.ui.fragment;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.page.CommonPagerAdapter;
import com.letv.leauto.ecolink.ui.page.IntroducePage;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 2017/3/7.
 */
public class IntroduceActivity extends BaseActivity {
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
    protected void initView() {
        setContentView(R.layout.activity_introduce);
        ButterKnife.bind(this);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        mPages=new ArrayList<>();
        mPages.add(new IntroducePage(mContext,0));
        mPages.add(new IntroducePage(mContext,1));
        mPages.add(new IntroducePage(mContext,2));
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
        for (int i = 0; i < mPages.size(); i++) {
            mDotLayout.addView(View.inflate(mContext, R.layout.advertisement_board_dot, null));
        }
        initDots(mDotLayout);



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
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPages!=null){
            for (BasePage mPage : mPages) {
                mPage.destory();
            }
            mPages.clear();
        }
    }
}
