package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.SortInfoAdapter;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeSortInfo;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wanghuayan on 16/7/26.
 */
public class MyAlbumPage extends BasePage implements
        ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.vp_viewPager)
    LinearLayout mViewpager;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
//    private ArrayList<BasePage> mPages; //定义要装fragment的列表
//    private ArrayList<String> listTitle; //tab名称列表
//    private SortInfoAdapter mSortinfoAdapter;
//    private ArrayList<LeSortInfo> sorts;
    private RelativeLayout.LayoutParams pLayoutParams;

    private RecentListPage mRecentListPage;
    private FavorAlbumPage mFavorAlbumPage;
    private BasePage mCurrentPage;

    public MyAlbumPage(Context context) {
        super(context);
    }


    @Override
    public void destory() {
        super.destory();
        if (mRecentListPage != null) {
            mRecentListPage.destory();
        }
        if (mFavorAlbumPage != null) {
            mFavorAlbumPage.destory();
        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_my_album, null);
        ButterKnife.bind(this, view);
        pLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
//        sorts = new ArrayList<LeSortInfo>();
////        sorts.add(new LeSortInfo("猜你喜欢", SortType.SORT_GUESS_LIKING, "8999"));
//        sorts.add(new LeSortInfo("收藏", SortType.SORT_FAVOR, "9003"));
//        sorts.add(new LeSortInfo("历史", SortType.SORT_RECENT, "9004"));
        initPage(view);

        selectPage(0);
        mFavorAlbumPage = new FavorAlbumPage(ct,new LeSortInfo(ct.getString(R.string.main_nav_collection), SortType.SORT_FAVOR, "9003"),mScrollView );
        mFavorAlbumPage.initData();
        mViewpager.removeAllViews();
        mViewpager.addView(mFavorAlbumPage.getContentView(),pLayoutParams);
        mCurrentPage=mFavorAlbumPage;
//        mViewpager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mScrollView.smoothScrollTo(0,0);
//                mViewpager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });
        return view;
    }

    @Override
    public void initData() {
        Trace.Debug("##### initdate");
        if (mCurrentPage!=null){
            mCurrentPage.initData();
        }
        mFavorAlbumPage = new FavorAlbumPage(ct,new LeSortInfo("收藏", SortType.SORT_FAVOR, "9003"),mScrollView );
        mFavorAlbumPage.initData();
    }

    private void initPage(View view) {
//        mPages = new ArrayList<BasePage>();
//        for (int i = 0; i < sorts.size(); i++) {
//            if (sorts.get(i).TYPE.equals(SortType.SORT_RECENT)) {
//                RecentListPage musicListPage = new RecentListPage(ct, sorts.get(i));
//                mPages.add(musicListPage);
//            } else {
//                FavorAlbumPage radioAlbumPage = new FavorAlbumPage(ct, sorts.get(i));
//                mPages.add(radioAlbumPage);
//            }
//        }
//        mPages.get(0).initData();
        radioGroup.setOnCheckedChangeListener(this);
//        mViewpager.removeAllViews();
//        mViewpager.addView(mPages.get(0).getContentView(),pLayoutParams);
        mScrollView.smoothScrollTo(0,0);
        //这句话市重点getChildFragmentManager()
//        mSortinfoAdapter = new SortInfoAdapter(ct, mPages, sorts);
//        mViewpager.setAdapter(mSortinfoAdapter);

    }

    private void selectPage(int position) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton child = (RadioButton) radioGroup.getChildAt(i);
            child.setChecked(false);
            child.setTextColor(ct.getResources().getColor(
                    R.color.transparent_60));
            TextPaint tp = child.getPaint();
            tp.setFakeBoldText(false);
        }
//        mViewpager.setCurrentItem(position);

        RadioButton select = (RadioButton) radioGroup.getChildAt(position);
        select.setChecked(true);
        select.setTextColor(ct.getResources().getColor(
                R.color.white));
        TextPaint tp = select.getPaint();
        tp.setFakeBoldText(true);

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Trace.Debug("##### oncheck");
        switch (checkedId) {
//            case R.id.guss_like:
//                selectPage(0);
//                break;
            case R.id.favorite:
                selectPage(0);
                mFavorAlbumPage = new FavorAlbumPage(ct,new LeSortInfo(ct.getString(R.string.main_nav_collection), SortType.SORT_FAVOR, "9003"),mScrollView );
                mFavorAlbumPage.initData();
                mViewpager.removeAllViews();
                mViewpager.addView(mFavorAlbumPage.getContentView(),pLayoutParams);
                mCurrentPage=mFavorAlbumPage;


                break;
            case R.id.recent:
                selectPage(1);
                mRecentListPage = new RecentListPage(ct,new LeSortInfo(ct.getString(R.string.str_history), SortType.SORT_RECENT, "9004") ,mScrollView);
                mRecentListPage.initData();
                mViewpager.removeAllViews();
                mViewpager.addView(mRecentListPage.getContentView(),pLayoutParams);
                mCurrentPage=mRecentListPage;
                break;
        }
        mViewpager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mScrollView.smoothScrollTo(0,0);
                mViewpager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
