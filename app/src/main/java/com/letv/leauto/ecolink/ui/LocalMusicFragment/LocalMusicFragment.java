package com.letv.leauto.ecolink.ui.LocalMusicFragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.csr.gaia.library.Gaia;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.page.LocalAllMusicPage;
import com.letv.leauto.ecolink.ui.page.LocalAlbumPage;
import com.letv.leauto.ecolink.ui.view.MusicStateManager;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/5.
 */
public class LocalMusicFragment extends BaseFragment implements ViewPager.OnPageChangeListener,View.OnClickListener{


    @Bind(R.id.vp_viewPager)
    ViewPager mViewPager;
    @Bind(R.id.music_state_icon)
    ImageView mMusicStateButton;
    private HomeActivity homeActivity;
    @Bind(R.id.local_all_btn)
    TextView mLocalAllBtn;
    @Bind(R.id.local_album_btn)
    TextView mLocalAlbumBtn;

    private ArrayList<String> mTitleList; //tab名称列表
    private ArrayList<BasePage> mPages;
    private LocalAlbumPage musicList_page;
    private LocalAllMusicPage allMusicPage;
    private  LocalPagerAdapter mAdapter;
    private int mPageNum;
    private ArrayList<TextView> mTextViewList;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.local_musicfragment_p, null);
        } else {
            view = inflater.inflate(R.layout.local_musicfragment, null);
        }
        ButterKnife.bind(this, view);
        initFrament(view);
        homeActivity = (HomeActivity)mContext;
        MusicStateManager.getInstance().init(getActivity(),mMusicStateButton);
        return view;
    }

    private void initFrament(View view) {
        mPages =new ArrayList<BasePage>();
        mTitleList =new ArrayList<String>();
        mTitleList.add(mContext.getString(R.string.str_all_songs));
        mTitleList.add("歌单");

        mTextViewList=new ArrayList<>();
        mTextViewList.add(mLocalAllBtn);
        mTextViewList.add(mLocalAlbumBtn);

        allMusicPage=new LocalAllMusicPage(mContext);
        musicList_page=new LocalAlbumPage(mContext);
        if (mAutoPlay){
            allMusicPage.setAutoPlay(true);
            mAutoPlay=false;
        }
        mPages.add(allMusicPage);
        mPages.add(musicList_page);

        mAdapter =new LocalPagerAdapter(mContext, mPages, mTitleList);
        mPages.get(0).initData();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mLocalAlbumBtn.setOnClickListener(this);
        mLocalAllBtn.setOnClickListener(this);
        selectPage(0);
//        vpViewPager.setOffscreenPageLimit(0);
    }

    private void selectPage(int position) {
        mViewPager.setCurrentItem(position);
        for (int i = 0; i < mTextViewList.size(); i++) {
            TextView textView=mTextViewList.get(i);
            if (i==position){
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                textView.setBackgroundResource(R.drawable.radiobutton_bg);
                textView.setTextColor(mContext.getResources().getColor(
                        R.color.white));
                TextPaint bill = textView.getPaint();
                bill.setFakeBoldText(true);
            }else {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                textView.setBackgroundResource(R.color.transparent);
                textView.setTextColor(mContext.getResources().getColor(
                        R.color.transparent_60));
                TextPaint my = textView.getPaint();
                my.setFakeBoldText(false);
            }
        }


    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        BasePage page = mPages.get(position);
        page.initData();
        selectPage(position);

    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onResume(){
        if(mContext==null)
            return;
        super.onResume();
        if(mPages != null) {
            BasePage page = mPages.get(mPageNum);
            if(page != null) {
                page.onResume();
            }
        }
        MusicStateManager.getInstance().init(getActivity(),mMusicStateButton);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mGaiaLink.isConnected()) {
            mGaiaLink.cancelNotification(Gaia.EventId.CHARGER_CONNECTION);
            mGaiaLink = null;
            Trace.Debug("cancelNotification");
        }
    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    homeActivity.onBackPressed();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.local_all_btn:
               selectPage(0);

                break;
            case R.id.local_album_btn:
               selectPage(1);
                break;

        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            if (musicList_page!=null){
                musicList_page.initData();
            }
        }
        super.onHiddenChanged(hidden);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPages != null) {
            for (BasePage mPage : mPages) {
                mPage.destory();
            }
        }
    }
private boolean mAutoPlay;
    public void setAutoPlay(boolean autoPlay) {
        mAutoPlay=autoPlay;

    }

    public void autoPlay() {
        if (allMusicPage!=null){
            allMusicPage.autoPlay();
            mAutoPlay=false;
        }
    }


}
