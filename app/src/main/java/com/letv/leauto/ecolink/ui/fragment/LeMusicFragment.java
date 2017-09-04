package com.letv.leauto.ecolink.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.AlbumListAdapter;
import com.letv.leauto.ecolink.adapter.SortInfoAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.EnvStatus;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.LeSortInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.http.leradio.AudioListLoader;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.json.AlbumParse;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.leradio_interface.data.PositiveSeries;
import com.letv.leauto.ecolink.ui.page.MusicListPage;
import com.letv.leauto.ecolink.ui.page.MusicPlayPage;
import com.letv.leauto.ecolink.ui.view.MusicStateManager;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.LogUtil;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 2016/8/9.
 * 代表音乐播放界面的fragment  包含musicplaypage  musiclistpage musicliricpage
 */
public class LeMusicFragment extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {
//    public static final String REFRESH = "refresh";
//    private static boolean isVr;
    @Bind(R.id.pager)
    ViewPager mViewpager;
    @Bind(R.id.ll_dot)
    LinearLayout ll_dot;//获取轮播图片的点的parent，用于动态添加要显示的点

    @Bind(R.id.wait_view)
    ImageView mWaitView;
    @Bind(R.id.music_content)
    RelativeLayout mMusicContet;
    @Bind(R.id.connect_view)
    RelativeLayout mConnectView;
    @Bind(R.id.bt_refresh)
    TextView mRefreshview;
    @Bind(R.id.bt_local_see)
    TextView mLocalview;
    @Bind(R.id.albumlist)
    ImageView album_list;
//    @Bind(R.id.radio_type)
//    ImageView mRadioType; //用来表示音源从哪里切入 乐听，酷我等
    private ArrayList<BasePage> mPages = new ArrayList<BasePage>();
    private MusicPlayPage musicPlayPage;
    private MusicListPage musicListPage;
    private LeSortInfo mLeSortInfo;
    private HomeActivity mHomeActivity;
    private boolean mIsVr;
    private CacheUtils mCacheUtil;
    private LeAlbumInfo albumInfo;
    private int mCurrentIndex;
    // 底部小点图片
    private ImageView[] dots;
    // 记录当前选中位置
    private int dotCurrentIndex;
    private ArrayList<MediaDetail> mediaList;
    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessageTypeCfg.MSG_MEDIALST_OBTAINED:
                    LeObject<MediaDetail> result = (LeObject<MediaDetail>) msg.obj;
                    if (result != null && result.list != null && result.list.size() > 0) {
                        mWaitView.setVisibility(View.GONE);
                        mConnectView.setVisibility(View.GONE);
                        mediaList = result.list;
                        Trace.Debug("#### albumInfo=" + albumInfo + "  ##medialist=" + mediaList.size() + " index=" + mCurrentIndex);
                        musicPlayPage = new MusicPlayPage(mContext, mIsVr, albumInfo, mediaList, mCurrentIndex);
                        musicListPage = new MusicListPage(mContext, albumInfo, mediaList, mCurrentIndex);
                        mPages.add(musicListPage);
                        mPages.add(musicPlayPage);
                        addDot(mPages);
                        mViewpager.setAdapter(new SortInfoAdapter(mContext, mPages));
                        mViewpager.setOffscreenPageLimit(2);
                        mPages.get(1).initData();
                        mViewpager.setCurrentItem(1);
                    } else {
                        ToastUtil.show(mContext, R.string.null_data_toast);
                    }

                    break;
                case MessageTypeCfg.MSG_SEARCH_BY_VOICE_OBTAINED:
                    LeObject<MediaDetail> voiceList = (LeObject<MediaDetail>) msg.obj;
                    if (voiceList != null && voiceList.list.size() > 0) {

                    } else {
                        ToastUtil.show(mContext, R.string.null_data_toast);
                    }
                    break;

                //酷我专辑数据
                case MessageTypeCfg.MSG_GET_FROM_GETKUWOUTILS:
                    mediaList = new ArrayList<>();
                    mediaList.addAll((List<MediaDetail>) msg.obj);
                    if (mediaList.size() != 0) {
                        //判断argAudioId在专辑中的角标
                        String argAudioId = mCacheUtil.getString(Constant.Radio.AUDIO_ID, null);
                        for (int i = 0; i < mediaList.size(); i++) {
                            if (mediaList.get(i).AUDIO_ID.equals(argAudioId)) {
                                mCurrentIndex = i;
                            }
                        }
                        mWaitView.setVisibility(View.GONE);
                        mConnectView.setVisibility(View.GONE);
                        musicPlayPage = new MusicPlayPage(mContext, mIsVr, albumInfo, mediaList, mCurrentIndex);
                        musicListPage = new MusicListPage(mContext, albumInfo, mediaList, mCurrentIndex);
                        mPages.clear();
                        mPages.add(musicListPage);
                        mPages.add(musicPlayPage);
                        addDot(mPages);
                        mViewpager.setAdapter(new SortInfoAdapter(mContext, mPages));
                        mPages.get(1).initData();
                        mViewpager.setOffscreenPageLimit(2);
                        mViewpager.setCurrentItem(1);

                    } else {
                        Trace.Error("=lemusicFragment=", "没有数据");
                    }
                    break;
                //酷我搜索
                case MessageTypeCfg.MSG_GET_KUWO_URL_SEARCH:
                    mediaList = new ArrayList<>();
                    String url = null;
                    if (msg.obj != null) {
                        url = (String) msg.obj;
                    }
                    mWaitView.setVisibility(View.GONE);
                    mConnectView.setVisibility(View.GONE);
                    MediaDetail mediaDetail = new MediaDetail();
                    mediaDetail.NAME = mCacheUtil.getString(Constant.Radio.MUSIC_NAME, null);
                    mediaDetail.AUTHOR = mCacheUtil.getString(Constant.Radio.AUDIO_AUTHOR, null);
                    mediaDetail.TYPE = SortType.SORT_KUWO;
                    mediaDetail.ALBUM = mCacheUtil.getString(Constant.Radio.AUDIO_DETAIL, null);
                    mediaDetail.AUDIO_ID = mCacheUtil.getString(Constant.Radio.AUDIO_ID, null);
                    mediaDetail.SOURCE_URL = url;
                    mediaList.add(mediaDetail);
                    mCurrentIndex = 0;
                    musicPlayPage = new MusicPlayPage(mContext, mIsVr, albumInfo, mediaList, mCurrentIndex);
                    musicListPage = new MusicListPage(mContext, albumInfo, mediaList, mCurrentIndex);
                    mPages.clear();
                    mPages.add(musicListPage);
                    mPages.add(musicPlayPage);
                    addDot(mPages);
                    mViewpager.setAdapter(new SortInfoAdapter(mContext, mPages));
                    mPages.get(1).initData();
                    mViewpager.setOffscreenPageLimit(2);
                    mViewpager.setCurrentItem(1);
                    break;
                //本地数据的缓存
                case MessageTypeCfg.MSG_FROM_RECENT:
                case MessageTypeCfg.MSG_FROM_LOCAL:
                    LeObject<MediaDetail> leObject = (LeObject<MediaDetail>) msg.obj;
                    if (leObject != null && leObject.list.size() > 0) {
                        mediaList = new ArrayList<>();
                        mediaList.addAll(leObject.list);
                        //判断上次歌曲在专辑中的角标
                        String argMUSIC_NAME = mCacheUtil.getString(Constant.Radio.MUSIC_NAME, null);
                        String argMUSIC_AUTHOR = mCacheUtil.getString(Constant.Radio.AUDIO_AUTHOR, null);
                        for (int i = 0; i < mediaList.size(); i++) {
                            if (mediaList.get(i).NAME.equals(argMUSIC_NAME)/*&&mediaList.get(i).AUTHOR.equals(argMUSIC_AUTHOR)*/) {
                                mCurrentIndex = i;
                            }
                        }
                        Trace.Debug("####MSG_FROM_LOCAL##medialist=" + mediaList.size() + " index=" + mCurrentIndex);
                        mWaitView.setVisibility(View.GONE);
                        mConnectView.setVisibility(View.GONE);
                        musicPlayPage = new MusicPlayPage(mContext, mIsVr, albumInfo, mediaList, mCurrentIndex);
                        musicListPage = new MusicListPage(mContext, albumInfo, mediaList, mCurrentIndex);
                        mPages.clear();
                        mPages.add(musicListPage);
                        mPages.add(musicPlayPage);
                        addDot(mPages);
                        mViewpager.setAdapter(new SortInfoAdapter(mContext, mPages));
                        mViewpager.setOffscreenPageLimit(2);
                        mViewpager.setCurrentItem(1);
                        mPages.get(1).initData();

                    } else {
                        ToastUtil.show(mContext, "空数据!");
                    }
                    break;
                case MessageTypeCfg.MSG_FROM_VOICE:
                    LeObject<MediaDetail> voiceLastList = (LeObject<MediaDetail>) msg.obj;
                    if (voiceLastList != null && voiceLastList.list != null && voiceLastList.list.size() > 0) {
                        mWaitView.setVisibility(View.GONE);
                        mConnectView.setVisibility(View.GONE);
                        mediaList = voiceLastList.list;
                        if(lePlayer!=null){
                            mCurrentIndex = lePlayer.getIndex();
                        }
                        Trace.Debug("######medialist=" + mediaList.size() + " index=" + mCurrentIndex);
                        musicPlayPage = new MusicPlayPage(mContext, mIsVr, albumInfo, mediaList, mCurrentIndex);
                        musicListPage = new MusicListPage(mContext, albumInfo, mediaList, mCurrentIndex);
                        mPages.clear();
                        mPages.add(musicListPage);
                        mPages.add(musicPlayPage);
                        addDot(mPages);
                        mViewpager.setAdapter(new SortInfoAdapter(mContext, mPages));
                        mViewpager.setOffscreenPageLimit(2);
                        mViewpager.setCurrentItem(1);
                        mPages.get(1).initData();
                    } else {
                        ToastUtil.show(mContext, R.string.null_data_toast);
                    }
                    break;
                case MessageTypeCfg.MSG_SUBITEMS_OBTAINED: {

                    LeObject<LeAlbumInfo> albumInfoLeObject = (LeObject<LeAlbumInfo>) msg.obj;

                    updateAlbumInfo(albumInfoLeObject.list);
                }
                break;
                case MessageTypeCfg.MSG_LIVE_MEDIALST_OBTAINED:
                    mediaList = (ArrayList<MediaDetail>) msg.obj;
                    if (mediaList != null && mediaList.size() > 0) {
                        Trace.Debug("#### albumInfo=" + albumInfo + "  ##medialist=" + mediaList.size() + " index=" + mCurrentIndex);
                        musicPlayPage = new MusicPlayPage(mContext, mIsVr, albumInfo, mediaList, mCurrentIndex);
                        musicListPage = new MusicListPage(mContext, albumInfo, mediaList, mCurrentIndex);
                        mPages.clear();
                        mPages.add(musicListPage);
                        mPages.add(musicPlayPage);
                        mWaitView.setVisibility(View.GONE);
                        mConnectView.setVisibility(View.GONE);
                        addDot(mPages);
                        mViewpager.setAdapter(new SortInfoAdapter(mContext, mPages));
                        mViewpager.setOffscreenPageLimit(2);
                        mPages.get(1).initData();
                        mViewpager.setCurrentItem(1);
                    } else {
                        ToastUtil.show(mContext, R.string.null_data_toast);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private void updateAlbumInfo(ArrayList<LeAlbumInfo> list) {
        Trace.Debug("#### updateAlbumInfo=" + list);
        if (list != null && list.size() > 0) {
            Trace.Debug("#### requestLiveMedialist=" + list);
            DataUtil.getInstance().requestLiveMedialist(mHandler, "tag", list, null);
        }
    }

    public void addDot(ArrayList<BasePage> pages) {
        ll_dot.removeAllViews();
        for (int i = 0; i < pages.size(); i++) {

            ll_dot.addView(View.inflate(mContext, R.layout.advertisement_board_dot, null));
        }
        initDots(pages, ll_dot);
    }

    private void setCurrentDot(ArrayList<BasePage> pages, int position) {
        if (position < 0 || position > pages.size() - 1
                || dotCurrentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[dotCurrentIndex].setEnabled(true);

        dotCurrentIndex = position;
    }

    private void initDots(ArrayList<BasePage> pages, LinearLayout ll_dot) {
        dots = new ImageView[pages.size()];

        // 循环取得小点图片
        for (int i = 0; i < pages.size(); i++) {
            dots[i] = (ImageView) ll_dot.getChildAt(i);
            dots[i].setEnabled(true);// 都设为灰色
        }

        dotCurrentIndex = 1;
        dots[dotCurrentIndex].setEnabled(false);// 设置为选中状态
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = initOrietantion(inflater);
        ButterKnife.bind(this, view);
        album_list.setOnClickListener(this);
        mRefreshview.setOnClickListener(this);
        mLocalview.setOnClickListener(this);
        mViewpager.addOnPageChangeListener(this);
        mHomeActivity = (HomeActivity) mContext;
       // MusicStateManager.getInstance().init(getActivity(), mRadioType);


        return view;
    }

    @Override
    public void onResume() {
        Trace.Debug("##### onResume");
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();
//        if (mGaiaLink.isConnected()) {
//            mGaiaLink.cancelNotification(Gaia.EventId.CHARGER_CONNECTION);
//            mGaiaLink = null;
//            Trace.Debug("cancelNotification");
//        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        //无数据,无网络时可以点击
        refreshPages(mContext);

    }

    public void refreshPages(Context context) {
        Trace.Debug("#### refreshPages");

        if (mContext == null) {
            mContext = context;
        }

        /**解决瘦车机空指针异常*/
        if (ll_dot == null) {
            initView(LayoutInflater.from(mContext));
        }

        CacheUtils cacheUtil = CacheUtils.getInstance(mContext);
        String lastType = cacheUtil.getString(Constant.Radio.LAST_TYPE, null);

        if (lePlayer == null) {
            lePlayer = EcoApplication.LeGlob.getPlayer();
            lePlayer.openServiceIfNeed();
        }
        albumInfo = lePlayer.getLeAlbumInfo();
        if (albumInfo == null) {
            Trace.Debug("#### albumInfo=" + albumInfo + " lastType=" + lastType);
            albumInfo = getAlbumFromCache();
        }
        Trace.Debug("#### albumInfo=" + albumInfo + "  ##medialist=" + mediaList);
        Trace.Debug("#### index=" + mCurrentIndex);
        if ((lastType != null && lastType.equals(SortType.SORT_VOICE)||SortType.SORT_VOICE.equals(albumInfo.TYPE))) {
            String lastMediaList = cacheUtil.getString(Constant.Radio.TOSTRING, null);//
            Trace.Debug("#### lastMediaList=" + lastMediaList);
            DataUtil.getInstance().getMediaListFromJson(mHandler,lastMediaList,"media");
        } else if (albumInfo != null && albumInfo.TYPE != null && (SortType.SORT_KUWO_LOCAL.equals(albumInfo.TYPE) || SortType.SORT_LOCAL_NEW.equals(albumInfo.TYPE)
                || SortType.SORT_LOCAL.equals(albumInfo.TYPE) || SortType.SORT_LOCAL_ALL.equals(albumInfo.TYPE) ||
                SortType.SORT_LE_RADIO_LOCAL.equals(albumInfo.TYPE))) {
            Trace.Debug("####SORT_LOCAL_NEW#### albumInfo=" + albumInfo + "  ##albumInfo.SORT_ID=" + albumInfo.SORT_ID);
            getMediaList();
        } else {
            isNetConnect = NetUtils.isConnected(context);
            if (isNetConnect) {
                if (mConnectView != null) {
                    mConnectView.setVisibility(View.GONE);
                }
                getMediaList();
            } else {
                //mConnectView.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 横竖屏初始化
     *
     * @param inflater
     * @return
     */
    private View initOrietantion(LayoutInflater inflater) {
        final View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_le_music, null);
        } else {
            view = inflater.inflate(R.layout.fragment_le_music, null);
        }
        return view;
    }

    private LeAlbumInfo getAlbumFromCache() {
        mCacheUtil = CacheUtils.getInstance(mContext);
        LeAlbumInfo info = new LeAlbumInfo();
        String lastAlbum = mCacheUtil.getString(Constant.Radio.LAST_ALBUM, null);
        Trace.Debug("####getAlbumFromCache#### lastAlbum=" + lastAlbum);
        try {
            ArrayList<LeAlbumInfo> list = AlbumParse.getAlbumList(new JSONArray("[" + lastAlbum + "]"));
            Trace.Debug("####getAlbumFromCache#### list=" + list);
            if (list != null && list.size() > 0) {
                info = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Trace.Debug("####getAlbumFromCache#### info=" + info);
        int lastPositon = mCacheUtil.getInt(Constant.Radio.LAST_POSITION, -1);
        Trace.Debug("####getAlbumFromCache#### lastPositon=" + lastPositon);
        if (lastPositon != -1) {
            EnvStatus.Sort_Id = mCacheUtil.getString(Constant.Radio.LAST_SORT_ID, null);
            Trace.Debug("####getAlbumFromCache#### Sort_Id=" + EnvStatus.Sort_Id);
            mCurrentIndex = lastPositon;
            //qu 酷我// TODO: 2016/8/17
            info.SORT_ID = mCacheUtil.getString(Constant.Radio.LAST_SORT_ID, null);
            info.ALBUM_ID = mCacheUtil.getString(Constant.Radio.ALBUM_ID, null);
            info.KUWO_BILL_ID = mCacheUtil.getString(Constant.Radio.KUWO_BILL_ID, null);
        }
        return info;
    }


    /**
     * 获取专辑列表
     */
    private void getMediaList() {
        if (lePlayer.getPlayerList() != null && albumInfo.TYPE != null && albumInfo.TYPE.equals(SortType.SORT_KUWO)) {
            mediaList = lePlayer.getPlayerList();
            mCurrentIndex = lePlayer.getIndex();
            musicPlayPage = new MusicPlayPage(mContext, mIsVr, albumInfo, mediaList, mCurrentIndex);
            musicListPage = new MusicListPage(mContext, albumInfo, mediaList, mCurrentIndex);
            mPages.clear();
            mPages.add(musicListPage);
            mPages.add(musicPlayPage);
            addDot(mPages);
            mViewpager.setAdapter(new SortInfoAdapter(mContext, mPages));
            mPages.get(1).initData();
            mViewpager.setOffscreenPageLimit(2);
            mViewpager.setCurrentItem(1);
        } else if (lePlayer.getPlayerList() != null) {
            mediaList = lePlayer.getPlayerList();
            mCurrentIndex = lePlayer.getIndex();
            Trace.Debug("####222 albumInfo=" + albumInfo + "  ##medialist=" + mediaList.size() + " index=" + mCurrentIndex);
            musicListPage = new MusicListPage(mContext, albumInfo, mediaList, mCurrentIndex);
            musicPlayPage = new MusicPlayPage(mContext, mIsVr, albumInfo, mediaList, mCurrentIndex);
            mPages.clear();
            mPages.add(musicListPage);
            mPages.add(musicPlayPage);
            addDot(mPages);
            mViewpager.setAdapter(new SortInfoAdapter(mContext, mPages));
            mPages.get(1).initData();
            mViewpager.setOffscreenPageLimit(2);
            mViewpager.setCurrentItem(1);

        } else {
           if (SortType.SORT_KUWO_LOCAL.equals(albumInfo.TYPE) || SortType.SORT_LOCAL_NEW.equals(albumInfo.TYPE) || SortType.SORT_LOCAL.equals(albumInfo.TYPE) || SortType.SORT_LOCAL_ALL.equals(albumInfo.TYPE) || SortType.SORT_LE_RADIO_LOCAL.equals(albumInfo.TYPE)) {
                Trace.Debug("####SORT_LOCAL#### albumInfo=" + albumInfo + "  ##albumInfo.SORT_ID=" + albumInfo.SORT_ID);
                DataUtil.getInstance().getMediaListFromDB(mHandler, albumInfo.TYPE, "all_local", "media");
            } else if (albumInfo.TYPE != null && SortType.SORT_LOCAL.equals(albumInfo.TYPE) || SortType.SORT_FAVOR.equals(albumInfo.TYPE)) {
                DataUtil.getInstance().getMediaListFromDB(mHandler, albumInfo.TYPE, albumInfo.ALBUM_ID, "media");
            } else if (albumInfo.TYPE != null && SortType.SORT_RECENT.equals(albumInfo.TYPE)) {
                Trace.Debug("####SORT_RECENT#### albumInfo=" + albumInfo + "  ##albumInfo.SORT_ID=" + albumInfo.SORT_ID);
                //DataUtil.getInstance().getMediaList(mHandler, "tag", albumInfo.SORT_ID, albumInfo);
                DataUtil.getInstance().getMediaListFromDB(mHandler, albumInfo.TYPE, albumInfo.ALBUM_ID, "media");
            } else if (albumInfo.PAGE_ID.equals(SortType.SORT_LIVE) || albumInfo.NAME.contains(SortType.SORT_LIVE)) {
                Trace.Debug("####TYPE#### albumInfo=" + albumInfo + "  ##albumInfo.SORT_ID=" + albumInfo.PAGE_ID);
                //DataUtil.getInstance().getAlbumList(mHandler, "", "2400");
                DataUtil.getInstance().getMediaList(mHandler, "tag", SortType.SORT_LIVE, albumInfo);
            } else {
                Trace.Debug("####else#### albumInfo=" + albumInfo + "  ##albumInfo.SORT_ID=" + albumInfo.SORT_ID);
                //DataUtil.getInstance().getMediaList(mHandler, "tag", albumInfo.SORT_ID, albumInfo);
//               AudioListLoader loader = new AudioListLoader(mContext, handler, albumInfo);
//               loader.load(1);
               CacheUtils cacheUtil = CacheUtils.getInstance(mContext);
               String lastMediaList = cacheUtil.getString(Constant.Radio.TOSTRING, null);//
               Trace.Debug("#### lastMediaList=" + lastMediaList);
               DataUtil.getInstance().getMediaListFromLeradioJson(mHandler,lastMediaList,"media");
            }
        }
    }


    public static LeMusicFragment getInstance(Bundle bundle, boolean b) {
        LeMusicFragment mFragment = new LeMusicFragment();
        mFragment.setArguments(bundle);
        //isVr = b;
        return mFragment;
    }

    public void setDongfenBar() {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPages.get(position).initData();
        setCurrentDot(mPages, position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Trace.Debug("###### hide ==" + hidden);
    }
    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                    manager.popBackStack("LeMusicFragment", 0);
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.*//*setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right,R.anim.in_from_right, R.anim.out_to_right).*//*hide(this).commitAllowingStateLoss();
//                mHomeActivity.ChangeButton(2);
                    mHomeActivity.removeMusicPlayPage();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.albumlist:
                FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                manager.popBackStack("LeMusicFragment", 0);
                FragmentTransaction transaction = manager.beginTransaction();
                transaction./*setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right,R.anim.in_from_right, R.anim.out_to_right).*/hide(this).commitAllowingStateLoss();
//                mHomeActivity.ChangeButton(2);
                mHomeActivity.removeMusicPlayPage();
                break;
            case R.id.bt_refresh:
                refreshPages(mContext);
                break;
            case R.id.bt_local_see:
                Trace.Debug("MusicFragment", "onActivityCreated   onclick");
                CacheUtils.getInstance(mHomeActivity).putString(Constant.Radio.NONET, "MusicFragment");
                replaceAlbumFragment();

                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
        if (mPages!=null){
            for (BasePage mPage : mPages) {
                mPage.destory();
            }
            mPages.clear();
        }
        if (mediaList!=null){
            mediaList.clear();
        }

    }

    /**
     * 无网络时直接切换到AlbumList
     */
    private void replaceAlbumFragment() {
        //homeActivity.changeToKuwo();
        mHomeActivity.changeToLocal();
    }

    public void setMode(int mode) {
      if (musicPlayPage!=null){
          musicPlayPage.setMode(mode);
      }
    }
}
