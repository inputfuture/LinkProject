package com.letv.leauto.ecolink.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.SortInfoAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.http.leradio.ChannelLoader;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.ChannelListModel;
import com.letv.leauto.ecolink.ui.leradio_interface.response.ChannelResponse;
import com.letv.leauto.ecolink.ui.page.LiveAlbumPage;
import com.letv.leauto.ecolink.ui.page.MyAlbumPage;
import com.letv.leauto.ecolink.ui.page.RadioAlbumPage;
import com.letv.leauto.ecolink.ui.view.LeRadioViewpager;
import com.letv.leauto.ecolink.ui.view.MusicStateManager;
import com.letv.leauto.ecolink.ui.view.SlideTablayout;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.SpUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * leradio 主界面
 */
public class LeRadioAlumFragment extends BaseFragment implements View.OnClickListener,ViewPager.OnPageChangeListener ,AlbumEidtFragment.SortChangeListener{

    private static final String HAS_NEW_CHANNELS = "has_new_channels";
    @Bind(R.id.album_strip)
    SlideTablayout mSlideTablayout;
    @Bind(R.id.album_pager)
    LeRadioViewpager mViewPager;
    @Bind(R.id.music_state_icon)
    ImageView mMusicStateButton;
    @Bind(R.id.edit_album_button)
    ImageView mEditAlbumButton;
    private ArrayList<BasePage> pages = new ArrayList<BasePage>();
    private ArrayList<LeAlbumInfo> albumInfos = new ArrayList<LeAlbumInfo>();
    private SortInfoAdapter mSortInfoAdapter;
//    private boolean isSortCached;
//    private String lastAlbumTag;
    private boolean isConnect;
    private String lastSortTag;
    private static final String TAG = LeRadioAlumFragment.class.getSimpleName();
    private HomeActivity homeActivity;
    private int mCurrentPage;
    MyAlbumPage myAlbumPage;
    ArrayList<Channel> mChannels = new ArrayList<Channel>();
    private boolean mIsWidget;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                case MessageCfg.MSG_HOME_PAGE:
//                    List<ChannelBlock> list= (List<ChannelBlock>) msg.obj;
//                    updateChannel( list);
//                    break;
                case MessageTypeCfg.MSG_CHANNEL:
                    List<Channel> loadedChns =  (List<Channel>) msg.obj;
                    if (loadedChns != null){

                        int size = loadedChns.size() >= 6 ? 6 : loadedChns.size();
                        ArrayList<Channel> channels = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            channels.add(loadedChns.get(i));
                            MediaOperation.getInstance().insertChannel(loadedChns.get(i));
                        }
                        appendLocal(channels);
                        updateChannel(channels);
                        pages.get(0).initData();
                        SpUtils.putBoolean(mContext, HAS_NEW_CHANNELS, true);
                    }

                    break;
            }
        }
    };


    public static LeRadioAlumFragment getInstance(Bundle bundle) {
        LeRadioAlumFragment mFragment = new LeRadioAlumFragment();
        Trace.Debug("##### getInstance");
        mFragment.setArguments(bundle);
        return mFragment;
    }


    @Override
    protected View initView(LayoutInflater inflater) {
        Trace.Debug("##### initView");
        View view = initOrietantion(inflater);
        ButterKnife.bind(this, view);
        homeActivity = (HomeActivity)mContext;
        mSlideTablayout.setViewPagerListener(this);
        mEditAlbumButton.setOnClickListener(this);
        MusicStateManager.getInstance().init(getActivity(), mMusicStateButton);
        return view;
    }

    /**
     * 横竖屏初始化
     *
     * @param inflater
     * @return
     */
    private View initOrietantion(LayoutInflater inflater) {
        View view;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (GlobalCfg.IS_POTRAIT) {

            view = inflater.inflate(R.layout.fragment_leradio_album, null);

        } else {

            view = inflater.inflate(R.layout.fragment_leradio_album_1, null);

        }

        return view;
    }



    @Override
    protected void initData(Bundle savedInstanceState) {
        Trace.Debug("##### initData");
        isConnect = NetUtils.isConnected(mContext);
        boolean noclean = SpUtils.getBoolean(mContext, HAS_NEW_CHANNELS, false);
        if (!noclean) {
            MediaOperation.getInstance().removeChannelAll();
        }
        //提示用户用的手机流量
        if (isConnect && GlobalCfg.IS_MOBILE_NET) {
            if (!NetUtils.isWifi(mContext)) {
                Toast.makeText(mContext, R.string.net_mobile_msg, Toast.LENGTH_LONG).show();
                GlobalCfg.IS_MOBILE_NET = false;
            }
        }
        this.lastSortTag += 1;
        ArrayList<Channel> channels =  MediaOperation.getInstance().getChannelsList();
        if (channels == null || channels.size() <= 0) {
            channels = new ArrayList<Channel>();
            ChannelLoader loader = new ChannelLoader(mContext, handler);
            loader.load();
        }
        appendLocal(channels);
        updateChannel(channels);
        pages.get(0).initData();
    }

    private void appendLocal(List<Channel> channels) {
        channels.add(new Channel("1003573514", "我的", "", "1", "http://d.itv.letv.com/mobile/channel/data.json?pageid=1003573514", "14021172", "2"));
//        MediaOperation.getInstance().insertChannel(new Channel("1003573514","我的","","1","http://d.itv.letv.com/mobile/channel/data.json?pageid=1003573514","14021172","2"));
    }


    private void updateChannel(List<Channel> list) {
        mChannels.clear();
        mChannels.addAll(list);
        pages.clear();
        for (int i = 0; i < mChannels.size(); i++) {
            if (mChannels.get(i).getName().equals("直播")) {
                LiveAlbumPage liveAlbumPage = new LiveAlbumPage(mContext, mChannels.get(i));
                pages.add(liveAlbumPage);
            }  else if (mChannels.get(i).getName().equals("我的")) {
               myAlbumPage = new MyAlbumPage(mContext);
                pages.add(myAlbumPage);
            }
            else {
                RadioAlbumPage radioAlbumPage = new RadioAlbumPage(mContext, mChannels.get(i));
                pages.add(radioAlbumPage);
//
            }
        }
//        pages.get(0).initData();
        if (mSortInfoAdapter==null){
            mSortInfoAdapter=new SortInfoAdapter(mContext,pages,mChannels);
        }else{
            mSortInfoAdapter.notifyDataSetChanged();

        }
        mViewPager.setAdapter(mSortInfoAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mSlideTablayout.setAnchorLayoutId(R.layout.anchor_layout);
        mSlideTablayout.setAnchorViewId(R.id.anchor);
        if (GlobalCfg.IS_POTRAIT) {
            mSlideTablayout.setTabLayoutId(R.layout.album_title_layout);
        } else {
            mSlideTablayout.setTabLayoutId(R.layout.album_title_layout_1);
        }
        mSlideTablayout.setTabViewId(R.id.title_id);
        mSlideTablayout.setTabTitleHightColor(R.color.white);
        mSlideTablayout.setTabTitleColor(R.color.transparent_60);
        mSlideTablayout.setViewPager(mViewPager);
        if (mIsWidget){
            pages.get(1).initData();
            mViewPager.setCurrentItem(1);
            mIsWidget=false;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {

        if (myAlbumPage!=null){
            myAlbumPage.initData();
        }
        if(!hidden){
            MusicStateManager.getInstance().init(getActivity(),mMusicStateButton);
        }
        super.onHiddenChanged(hidden);
    }


    public void widgetToFavor(){
        if (mViewPager!=null){
            mViewPager.setCurrentItem(1);
        }else{
            mIsWidget=true;

        }
    }
    @Override
    public void onResume() {
        Trace.Debug("##### onresume");
        super.onResume();
        if(mCurrentPage==1) {
            mViewPager.setCurrentItem(1);
            myAlbumPage.initData();
        }
        MobclickAgent.onPageStart("LeRadioAlumFragment");
        MusicStateManager.getInstance().init(getActivity(),mMusicStateButton);
    }

    public void refreshView(Context context){
        FragmentManager manager = ((HomeActivity) context).getSupportFragmentManager();
        Fragment albumlistFragment= manager.findFragmentByTag("AlbumListFragment");
        if(albumlistFragment != null){
            AlbumListFragment fragment = (AlbumListFragment)albumlistFragment;
            fragment.onRefreshView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LeRadioAlumFragment");

    }


    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*homeActivity.changeToHome();*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_state_icon:
                if (lePlayer!=null&&lePlayer.getCurrentStatus()!=null&&lePlayer.getCurrentStatus().currentItem!=null&&lePlayer.getCurrentStatus().isPlaying){
                    homeActivity.changeToPlayMusic();
                }
                break;
            case R.id.edit_album_button:
                AlbumEidtFragment  albumEidtFragment= new AlbumEidtFragment();
                FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                Fragment leRadioAlumFragment= manager.findFragmentByTag("LeRadioAlumFragment");
                FragmentTransaction transaction= manager.beginTransaction();
                if (leRadioAlumFragment!=null){
                    transaction.hide(leRadioAlumFragment);
                }
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right,R.anim.in_from_right, R.anim.out_to_right).add(R.id.music_frame, albumEidtFragment, "AlbumEidtFragment").commitAllowingStateLoss();
                albumEidtFragment.setSortChangeListener(this);
                break;

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage=position;
        this.homeActivity.albumSelectPosition = mCurrentPage;
        BasePage page = pages.get(position);
        page.initData();
//        LeSortInfo leSortInfo=sortInfos.get(position);
//        EnvStatus.Sort_Id = leSortInfo.PAGE_ID;
//        EnvStatus.Sort_Type = leSortInfo.TYPE;
//        EnvStatus.Album_Id = DataUtil.ALBUM_LIST + "_" + leSortInfo.PAGE_ID;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    @Override
    public void sortChange(ArrayList<Channel> channels) {
        if (channels.size() < mChannels.size()){
            mCurrentPage = 0;
        }
        appendLocal(channels);
        updateChannel(channels);
        pages.get(mCurrentPage).initData();
        mViewPager.setCurrentItem(mCurrentPage);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pages!=null){
            for (BasePage page : pages) {
                page.destory();
            }
            pages.clear();
            pages = null;
        }
    }
}
