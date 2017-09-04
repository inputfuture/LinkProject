package com.letv.leauto.ecolink.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.AlbumListAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.cfg.SettingMusicCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.download.DownloadEngine;
import com.letv.leauto.ecolink.http.leradio.AudioListLoader;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.library.PullToRefreshBase;
import com.letv.leauto.ecolink.library.PullToRefreshListView;
import com.letv.leauto.ecolink.manager.MusicDownloadManager;
import com.letv.leauto.ecolink.thincar.protocol.LeRadioSendHelp;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.PositiveSeries;
import com.letv.leauto.ecolink.ui.page.MusicPlayPage;
import com.letv.leauto.ecolink.ui.view.MusicStateManager;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.mobile.core.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 专辑音乐列表，管理界面，下载收藏
 * <p/>
 * TO-DO List: 1. 视频和音频加载、显示方式统一（目前状态：视频通过positive分页，音频没有分页标记）
 * 2. 视频管理的层级太深，需要扁平化
 */
public class AlbumListFragment extends BaseFragment implements View.OnClickListener, DownloadEngine.DownloadListener,
        AdapterView.OnItemClickListener, LePlayer.ListViewItemStateListener, PullToRefreshBase.OnRefreshListener2<ListView> {
    private static final int ICON = 0x44;
    @Bind(R.id.music_state_icon)
    ImageView mMusicStateButton;
    @Bind(R.id.album_list)
    PullToRefreshListView mListView;
    @Bind(R.id.back)
    ImageView mBackButton;
    @Bind(R.id.cb_select_all)
    ImageView cb_select_all;
    @Bind(R.id.album_name)
    TextView mAlbumNameView;
    @Bind(R.id.download)
    LinearLayout mDownloadView;
    @Bind(R.id.download_img)
    ImageView mDownloadImg;
    @Bind(R.id.download_txt)
    TextView mDownloadText;

    @Bind(R.id.favorite)
    LinearLayout mFavoriteView;
    @Bind(R.id.favorite_img)
    ImageView mFavorImage;
    @Bind(R.id.favorite_txt)
    TextView mFavorText;
    @Bind(R.id.order)
    LinearLayout mOrderView;
    @Bind(R.id.order_img)
    ImageView mOrderImage;
    @Bind(R.id.order_txt)
    TextView mOrderText;
    @Bind(R.id.selection)
    TextView mSelectionView;
    @Bind(R.id.all_select_text)
    TextView mAllSelectTextView;
    @Bind(R.id.all)
    LinearLayout mAllView;
    @Bind(R.id.complete)
    TextView mCompleteButton;
    @Bind(R.id.wait_view)
    ImageView mWaitView;
    @Bind(R.id.nodate)
    TextView mNoDateView;
    @Bind(R.id.connect_view)
    RelativeLayout mConnectView;
    @Bind(R.id.bt_refresh)
    TextView mRefreshView;
    @Bind(R.id.bt_local_see)
    TextView mLocalToseeView;

    @Bind(R.id.select_grid)
    GridView mSelectGrid;
    LeAlbumInfo mAlbumInfo;
    ArrayList<MediaDetail> mediaList = new ArrayList<>();/*用于标记从网络获取的所有音乐*/
    ArrayList<MediaDetail> mDownlodDetails = new ArrayList<>();/*用于标记展示的音乐*/
    ArrayList<MediaDetail> mShowMediaLists = new ArrayList<>();
    private boolean mIsDownLoad;
    private HomeActivity homeActivity;
    AlbumListAdapter musicListAdapter;
    private boolean mIsFavor;
    private int mIndex = 1;//当前刷新页面的索引
    private AudioListLoader mLoader;
    private SelectionAdapter mSelectionAdapter;

    private List<String> mSelectionStrings = new ArrayList<>();
    private List<PositiveSeries> mPositiveSeries;
    private String mSelectionString = "全部";


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //获取pagenum,并获取视频列表
                case MessageTypeCfg.MSG_GET_VIDEOLIST:

                    mListView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mNoDateView.setVisibility(View.GONE);
                    mConnectView.setVisibility(View.GONE);
                    mDownloadView.setEnabled(false);
                    mDownloadImg.setImageResource(R.mipmap.download_icon_gray);
                    mListView.onRefreshComplete();
                    mPositiveSeries = (List<PositiveSeries>) msg.obj;
                    mSelectionString = "全部";
                    if (mPositiveSeries != null && mPositiveSeries.size() > 0) {
                        mediaList.addAll(mPositiveSeries.get(0).getPositivieSeries());

                        mShowMediaLists.addAll(mediaList);
                        //mShowMediaLists.removeAll(mDownlodDetails);
                        mIndex++;
                        if (musicListAdapter == null) {
                            musicListAdapter = new AlbumListAdapter(mShowMediaLists, mDownlodDetails, getActivity(), lePlayer);
                            musicListAdapter.setDownLoadClickListener(new AlbumListAdapter.DownLoadClickListener() {
                                @Override
                                public void canClick(boolean canClick) {
                                    mDownloadView.setEnabled(canClick);

                                }
                            });
                            mListView.setAdapter(musicListAdapter);
                        } else {
                            musicListAdapter.notifyDataSetChanged();
                        }

                    } else {
                        ToastUtil.show(mContext, R.string.no_more_data);
                        mHandler.sendEmptyMessage(MessageTypeCfg.NO_MORE_DATA);
                    }

                    break;

                case MessageTypeCfg.MSG_GET_AUDIOLIST:
                    ArrayList<MediaDetail> mediaDetails = (ArrayList<MediaDetail>) msg.obj;
                    mListView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mNoDateView.setVisibility(View.GONE);
                    mConnectView.setVisibility(View.GONE);
                    mDownloadView.setEnabled(true);
                    mDownloadImg.setImageResource(R.mipmap.download_icon);
                    mListView.onRefreshComplete();

                    if (mediaDetails != null && mediaDetails.size() > 0) {
                        mIndex += 1;
                        Trace.Debug("###### MSG_GET_AUDIOLIST：mIndex=" + mIndex);
                        mediaList.addAll(mediaDetails);
                        mShowMediaLists.addAll(mediaDetails);
                        //mShowMediaLists.removeAll(mDownlodDetails);
                        mSelectionString = "全部";

                        if (musicListAdapter == null) {
                            musicListAdapter = new AlbumListAdapter(mShowMediaLists, mDownlodDetails, getActivity(), lePlayer);
                            mListView.setAdapter(musicListAdapter);
                        } else {
                            musicListAdapter.notifyDataSetChanged();
                        }
                        musicListAdapter.setDownLoadClickListener(new AlbumListAdapter.DownLoadClickListener() {
                            @Override
                            public void canClick(boolean canClick) {
                                mDownloadView.setEnabled(canClick);

                            }
                        });

                    } else {
                        ToastUtil.show(mContext, R.string.no_more_data);
                        mHandler.sendEmptyMessage(MessageTypeCfg.NO_MORE_DATA);
                    }
//                    mMediaInfoModels = ( ArrayList<MediaDetail>) msg.obj;
//                    if (mMediaInfoModels != null&&mMediaInfoModels.size()!=0) {
//                        LeAlbumInfo leAlbumInfo = new LeAlbumInfo();
//                        leAlbumInfo.TYPE = SortType.SORT_MUSIC;
//                        lePlayer.setAlbumInfo(leAlbumInfo);
//                        lePlayer.setPlayerList(mMediaInfoModels);
//                        initListData(mMediaInfoModels);
//                    }
                    break;
                case MessageTypeCfg.MUSICINDEX:
                    if (mediaList != null && mCurrentIndex!=-1 && mCurrentIndex < mediaList.size() && lePlayer.getPlayerList() != null && mCurrentIndex < lePlayer.getPlayerList().size()) {
                        if (lePlayer.getPlayerList().get(mCurrentIndex).NAME.equals(mediaList.get(mCurrentIndex).NAME)) {
                            if (musicListAdapter != null) {
                                musicListAdapter.notifyDataSetChanged();
                            }
                            mListView.setSelection(mCurrentIndex);
                            mListView.smoothScrollToPosition(mCurrentIndex);
                        }
                    }
                    break;
                case MessageTypeCfg.MSG_GETDATA_FAILED:
                    mListView.onRefreshComplete();
                    mListView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mNoDateView.setVisibility(View.VISIBLE);
                    mConnectView.setVisibility(View.GONE);
                    mNoDateView.setText(R.string.no_data_toast);
                    break;
                case MessageTypeCfg.MSG_GETDATA_EXCEPTION:
                    mListView.onRefreshComplete();
                    mListView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mConnectView.setVisibility(View.GONE);

                    mNoDateView.setVisibility(View.VISIBLE);
                    mNoDateView.setText("网络出现错误");
                    break;
                case MessageTypeCfg.NO_MORE_DATA:

                    mListView.onRefreshComplete();
//                    mListView.setReleaseLabel("没有更多数据");
            }
        }
    };

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = initOrietantion(inflater);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        homeActivity = (HomeActivity) mContext;
        mBackButton.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mListView.setOnRefreshListener(this);
        mDownloadView.setOnClickListener(this);
        mFavoriteView.setOnClickListener(this);
        mAllView.setOnClickListener(this);
        mCompleteButton.setOnClickListener(this);
        mRefreshView.setOnClickListener(this);
        mLocalToseeView.setOnClickListener(this);
        mSelectionView.setOnClickListener(this);
        mSelectGrid.setOnItemClickListener(new SelectionsListener());
        mOrderView.setOnClickListener(this);
        mWaitView.setVisibility(View.VISIBLE);
        if(!((HomeActivity) mContext).isDestroyed()) {
            Glide.with(mContext).load(R.drawable.loading_gif).into(mWaitView);
        }
        mNoDateView.setVisibility(View.GONE);
        mConnectView.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mDownloadView.getLayoutParams();
        layoutParams.leftMargin = 0;
        mDownloadView.setLayoutParams(layoutParams);
        mAlbumInfo = bundle.getParcelable(Constants.CHANNEL_FOCUS);
        mIsFavor = MediaOperation.getInstance().isCollectionAlbum(mAlbumInfo);
        setFavorView(mIsFavor);
        mLoader = new AudioListLoader(mContext, this.mHandler, mAlbumInfo);
        refreshView();
        MusicStateManager.getInstance().init(getActivity(), mMusicStateButton);
        return view;
    }

    private void refreshView() {
        if (musicListAdapter != null) {
            musicListAdapter.removeAll();
        }
        if (mAlbumInfo != null) {
            mAlbumNameView.setText(mAlbumInfo.NAME);
            if (NetUtils.isConnected(mContext)) {
                // DataUtil.getInstance().getMusicDetailListData(this.mHandler, mContext, mAlbumInfo, mAlbumInfo.ALBUM_TYPE_ID,null);
                mLoader.load(mIndex);
                mNoDateView.setVisibility(View.GONE);
                mConnectView.setVisibility(View.GONE);
            } else {
                mNoDateView.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                mWaitView.setVisibility(View.GONE);
                mConnectView.setVisibility(View.VISIBLE);

            }
            mDownlodDetails.clear();
            mDownlodDetails.addAll(MediaOperation.getInstance().getDownlodMusics(mAlbumInfo.ALBUM_ID, SortType.SORT_LE_RADIO_LOCAL));
            if (mDownlodDetails.size() > 0) {
                Trace.Debug("#####  sql  download size=" + mDownlodDetails.toString());
            } else {
                Trace.Debug("#####  sql  download is null");
            }


        }
    }


    private void setFavorView(boolean mIsFavor) {
        if (mIsFavor) {
            mFavorImage.setImageResource(R.mipmap.music_favor);
        } else {
            mFavorImage.setImageResource(R.mipmap.music_unfavor);
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        lePlayer.setListViewItemStateListener(this);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            MusicStateManager.getInstance().init(getActivity(), mMusicStateButton);
        }
        super.onHiddenChanged(hidden);
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
            view = inflater.inflate(R.layout.fragment_album_list, null);
        } else {
            view = inflater.inflate(R.layout.fragment_album_list_1, null);

        }
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!mIsDownLoad) {
//                    if (mediaList != null && mediaList.size() != 0) {
//                        if (position >= 1 && position-1 < mediaList.size()) {
//                            lePlayer.playList(position-1);
//
//                        }
//                        homeActivity.changeToPlayMusic();
//                    }
            if (mShowMediaLists != null && mShowMediaLists.size() > 0) {
                //lePlayer.saveCurrentItem();
                lePlayer.TYPE = 1;
                mAlbumInfo.TYPE = SortType.SORT_LE_RADIO;
                lePlayer.setPlayerList(mShowMediaLists);
                lePlayer.setAlbumInfo(mAlbumInfo);
                Channel channel = LeRadioSendHelp.getInstance().getCurrentChooseChannel();
                LeRadioSendHelp.getInstance().setCurrentPlayChannel(channel);
                if (position >= 1 && position - 1 < mShowMediaLists.size()) {
                    if (position - 1 == LePlayer.LE_INDEX) {
                        if (BaseActivity.isStoped) {
                            lePlayer.playListPause(position - 1);
                        } else {
                            lePlayer.playList(position - 1);
                        }

                    } else {
                        BaseActivity.isStoped = false;
                        MusicPlayPage.mIsStoped = false;
                        lePlayer.playList(position - 1);
                    }

                    GlobalCfg.MUSIC_TYPE = Constant.TAG_LERADIO;
                }
                homeActivity.changeToPlayMusic();
            }
        }
//


    }

    public void onRefreshView() {
        MusicStateManager.getInstance().init(getActivity(), mMusicStateButton);
    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);
        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                    Fragment leRadioAlumFragment = manager.findFragmentByTag("LeRadioAlumFragment");
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                    if (leRadioAlumFragment != null) {
                        Trace.Debug("######  leRadioAlumFragment=" + leRadioAlumFragment);
                        leRadioAlumFragment.onResume();
                        transaction.show(leRadioAlumFragment);
                    } else {
                        manager.beginTransaction()
                                .replace(R.id.music_frame, leRadioAlumFragment).commitAllowingStateLoss();
                    }
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
//                if (mIsDownLoad){
//                    mIsDownLoad=false;
//                    mAllView.setVisibility(View.GONE);
//                    mFavoriteView.setVisibility(View.VISIBLE);
//                    if (musicListAdapter!=null){
//                        musicListAdapter.setIsDownload(mIsDownLoad);
//                    }
//                }else{
                FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                Fragment leRadioAlumFragment = manager.findFragmentByTag("LeRadioAlumFragment");
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                if (leRadioAlumFragment != null) {
                    Trace.Debug("######  leRadioAlumFragment=" + leRadioAlumFragment);
                    leRadioAlumFragment.onResume();
                    transaction.show(leRadioAlumFragment);
                } else {
                    manager.beginTransaction()
                            .replace(R.id.music_frame, leRadioAlumFragment).commitAllowingStateLoss();
                }

                break;
            case R.id.download:
                if (mIsDownLoad) {
                    mDownloadText.setText(R.string.str_download);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mDownloadView.getLayoutParams();
                    layoutParams.leftMargin = 0;
                    mDownloadView.setLayoutParams(layoutParams);
                    if (NetUtils.isConnected(mContext)) {
                        if (NetUtils.isWifi(mContext)) {
                            downloadMedias();
//                            ToastUtil.show(mContext,"添加到下载队列");
                        } else {
                            if (CacheUtils.getInstance(mContext).getBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, false)) {
                                downloadMedias();
//                                ToastUtil.show(mContext,"添加到下载队列");


                            } else {
                                NetworkConfirmDialog dialog = new NetworkConfirmDialog(mContext, R.string.data_download_warn, R.string.continue_download, R.string.cancel, true);
                                dialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                                    @Override
                                    public void onConfirm(boolean checked) {
                                        downloadMedias();
                                        if (checked) {
                                            CacheUtils.getInstance(mContext).putBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, true);
                                        } else {
                                            CacheUtils.getInstance(mContext).putBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, false);
                                        }
                                        mContext.sendBroadcast(new Intent(SettingCfg.BROADCAST_DOWNlOAD_SWICH));
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                                dialog.show();
                            }
                        }
                    } else {
                        ToastUtil.show(mContext,R.string.connect_network_toast);
                        mIsDownLoad = false;
                        mDownlodDetails.clear();
                        mDownlodDetails.addAll(MediaOperation.getInstance().getDownlodMusics(mAlbumInfo.ALBUM_ID, SortType.SORT_LE_RADIO_LOCAL));
                        mAllView.setVisibility(View.GONE);
                        mCompleteButton.setVisibility(View.GONE);
                        mFavoriteView.setVisibility(View.VISIBLE);
                       // mOrderView.setVisibility(View.VISIBLE);
                        mSelectionView.setVisibility(View.VISIBLE);
                        if (musicListAdapter != null) {
                            musicListAdapter.setIsDownload(mIsDownLoad);
                        }

                    }


                } else {
                    if (musicListAdapter != null) {
                        mIsDownLoad = true;
                        mDownlodDetails.clear();
                        mDownlodDetails.addAll(MediaOperation.getInstance().getDownlodMusics(mAlbumInfo.ALBUM_ID, SortType.SORT_LE_RADIO_LOCAL));
                        mShowMediaLists.removeAll(mDownlodDetails);
                        Trace.Debug("######  size=" + mDownlodDetails.size());
                        musicListAdapter.setIsDownload(mIsDownLoad);
                        mCompleteButton.setVisibility(View.VISIBLE);
                        mDownloadText.setText(R.string.str_download_start);
                        mDownloadText.setEnabled(false);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mDownloadView.getLayoutParams();
                        layoutParams.leftMargin = (int) mContext.getResources().getDimension(R.dimen.size_24dp);
                        mDownloadView.setLayoutParams(layoutParams);
                        mAllView.setVisibility(View.VISIBLE);
                        mFavoriteView.setVisibility(View.GONE);
                        mSelectionView.setVisibility(View.GONE);
                        mOrderView.setVisibility(View.GONE);
                        if(mShowMediaLists.size()==0){
                            setAllSelectView(false);
                            mAllView.setClickable(false);
                        }else {
                            mAllView.setClickable(true);
                            musicListAdapter.setItemAllSelectListener(
                                    new AlbumListAdapter.ItemAllSelectListener() {

                            @Override
                            public void setIsAll(boolean isAll) {
                                setAllSelectView(isAll);
                            }
                        });
                        }
                    }

                }

//                if (mAlbumInfo!=null) {
//                    MusicDownloadFragment downloadFragment=new MusicDownloadFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelable("LeAlbumInfo", mAlbumInfo);
//                    downloadFragment.setArguments(bundle);
//                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().add(R.id.music_frame, downloadFragment, "MusicDownloadFragment").commitAllowingStateLoss();
//                }
                break;
            case R.id.favorite:
                if (mIsFavor) {
                    ttsHandlerController.stop();
                    ttsHandlerController.speak(mContext.getString(R.string.str_uncollection));
                    MediaOperation.getInstance().delelteAlbumInfo(SortType.SORT_FAVOR, mAlbumInfo);
                    if (mediaList != null && mediaList.size() > 0) {
                        LetvReportUtils.reportMessages(mAlbumInfo.ALBUM_ID, mediaList.get(0),
                                "unbook");
                    }
                    mIsFavor = false;
                } else {
                    ttsHandlerController.stop();
                    ttsHandlerController.speak(mContext.getString(R.string.str_collection_success));
                    MediaOperation.getInstance().insertAlbumInfo(SortType.SORT_FAVOR, mAlbumInfo);
                    if (mediaList != null && mediaList.size() > 0) {
                        LetvReportUtils.reportMessages(mAlbumInfo.ALBUM_ID, mediaList.get(0),
                                "book");
                    }
                    mIsFavor = true;
                }
                setFavorView(mIsFavor);

                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //设置动画时间
                scaleAnimation.setDuration(500);

                mFavorImage.startAnimation(scaleAnimation);


                break;
            case R.id.all:
                if (mIsDownLoad) {
                    if (musicListAdapter != null) {
                        boolean isAll = musicListAdapter.getIsAll();
                        musicListAdapter.setIsAllDownLoad(!isAll);
                        setAllSelectView(!isAll);
                    }
                }

                break;
            case R.id.complete:
                mDownloadText.setText(R.string.str_download);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mDownloadView.getLayoutParams();
                layoutParams.leftMargin = 0;
                mDownloadView.setLayoutParams(layoutParams);
                mIsDownLoad = false;
                mAllView.setVisibility(View.GONE);
                mCompleteButton.setVisibility(View.GONE);
                mFavoriteView.setVisibility(View.VISIBLE);
                mSelectionView.setVisibility(View.VISIBLE);

                mShowMediaLists.clear();
                mShowMediaLists.addAll(mediaList);
                if(musicListAdapter!=null) {
                    musicListAdapter.setIsDownload(mIsDownLoad);
                    musicListAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.bt_local_see:
                homeActivity.changeToLocal();
                break;
            case R.id.bt_refresh:
                refreshView();
                break;

            case R.id.selection:
                if (mSelectGrid.getVisibility() == View.VISIBLE) {
                    mSelectGrid.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mFavoriteView.setEnabled(true);
                    mDownloadText.setTextColor(mContext.getResources().getColor(R.color.transparent_80));
                    mFavorText.setTextColor(mContext.getResources().getColor(R.color.transparent_80));
                    if (mAlbumInfo.ALBUM_TYPE_ID.equals(Constants.MEDIA_PLAY_TYPE_AUDIOLIST)) {
                        mDownloadView.setEnabled(true);
                        mDownloadImg.setImageResource(R.mipmap.download_icon);
                    } else {
                        mDownloadView.setEnabled(false);
                        mDownloadImg.setImageResource(R.mipmap.download_icon_gray);


                    }
                    mDownloadImg.setImageResource(R.mipmap.download_icon);
                    setFavorView(mIsFavor);
                   // mOrderView.setEnabled(true);
                } else {
                    mListView.setVisibility(View.GONE);
                    mDownloadView.setEnabled(false);
                    mDownloadImg.setImageResource(R.mipmap.download_icon_gray);
                    mFavorImage.setImageResource(R.mipmap.music_favor_grey);
                    mDownloadText.setTextColor(mContext.getResources().getColor(R.color.transparent_10));
                    mFavorText.setTextColor(mContext.getResources().getColor(R.color.transparent_10));
                    mFavoriteView.setEnabled(false);
                    //mOrderView.setEnabled(false);
                    mSelectGrid.setVisibility(View.VISIBLE);
                    mSelectionStrings.clear();
                    int selctionCount = 0;
                    if(mediaList.size() %(20)==0) {
                        selctionCount = Math.round(mediaList.size() / 20 );
                    }else{
                        selctionCount = Math.round(mediaList.size() / 20 + 1);
                    }
                    for (int i = 0; i < selctionCount; i++) {
                        if ((i + 1) * 20 <= mediaList.size()) {
                            mSelectionStrings.add(i * 20 + 1 + "～" + (i + 1) * 20);
                        } else {
                            mSelectionStrings.add(i * 20 + 1 + "～" + mediaList.size());
                        }
                    }
                    mSelectionStrings.add("全部");
                    Trace.Debug("##### mSelectionStrings =" + mSelectionStrings.toString());
                    if (mSelectionAdapter == null) {
                        mSelectionAdapter = new SelectionAdapter(mSelectionStrings);
                        mSelectGrid.setAdapter(mSelectionAdapter);
                    } else {
                        mSelectionAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case R.id.order:
                if(mediaList != null && mediaList.size() > 1){
                    Collections.reverse(mediaList);
                }
                ArrayList<MediaDetail> mediaDetails = new ArrayList<>();
                mediaDetails.addAll(mShowMediaLists);
                mShowMediaLists.clear();
                for (int i = mediaDetails.size() - 1; i >= 0; i--) {
                    mShowMediaLists.add(mediaDetails.get(i));
                }
                if (musicListAdapter != null) {
                    musicListAdapter.notifyDataSetChanged();
                }

                break;


        }

    }


    private void setOrderView(boolean increase) {
        if (increase) {
            mOrderImage.setImageResource(R.mipmap.order);
        }

    }

    @Override
    public void onSuccess(MediaDetail task) {

    }

    @Override
    public void onStart(MediaDetail task) {

    }

    @Override
    public void onPause(MediaDetail task) {

    }

    @Override
    public void onFail(MediaDetail task, int code) {

    }

    @Override
    public void onProgress(MediaDetail task, float percent) {

    }

    @Override
    public void remove() {

    }

    @Override
    public void add() {

    }

    class SelectionsListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Trace.Debug("##### position =" + position + ",id="+id);
            if (position >= 0 && position < mSelectionStrings.size()) {
                if (position < mSelectionStrings.size() - 1) {
                    mShowMediaLists.clear();
                    if(position>=1){
                        mShowMediaLists.addAll(mediaList.subList(position * 20-1, (position + 1) * 20 - 1 < mediaList.size() ? (position + 1) * 20 - 1 : mediaList.size()));
                    }else {
                        mShowMediaLists.addAll(mediaList.subList(position * 20, (position + 1) * 20 - 1 < mediaList.size() ? (position + 1) * 20 - 1 : mediaList.size()));
                    }
                    if(musicListAdapter!=null) {
                        musicListAdapter.notifyDataSetChanged();

                    }
                    mListView.setVisibility(View.VISIBLE);
                    mSelectGrid.setVisibility(View.GONE);
                    mSelectionString = mSelectionStrings.get(position);
                    mSelectionAdapter.notifyDataSetChanged();

                } else {
                    mShowMediaLists.clear();
                    mShowMediaLists.addAll(mediaList);
                    if(musicListAdapter!=null) {
                        musicListAdapter.notifyDataSetChanged();
                    }
                    mListView.setVisibility(View.VISIBLE);
                    mSelectGrid.setVisibility(View.GONE);
                    mSelectionString = mSelectionStrings.get(position);
                    mSelectionAdapter.notifyDataSetChanged();
                }
                mFavoriteView.setEnabled(true);
                mDownloadView.setEnabled(true);
                mDownloadImg.setImageResource(R.mipmap.download_icon);
                setFavorView(mIsFavor);
                //mOrderView.setEnabled(true);
            }
        }
    }

    private void setAllSelectView(boolean all) {
        if (all){
            mAllSelectTextView.setText(R.string.all_checked);
            cb_select_all.setImageResource(R.mipmap.song_selected);
        }
        else{
            mAllSelectTextView.setText(R.string.all_checked);
            cb_select_all.setImageResource(R.mipmap.song_not_selected);
        }
    }

    private void downloadMedias() {

        final List<MediaDetail> nowMedials = new ArrayList<>();
        nowMedials.addAll(musicListAdapter.getNowDownLoadMedials());
        mIsDownLoad = false;
        mDownlodDetails.clear();
        mDownlodDetails.addAll(MediaOperation.getInstance().getDownlodMusics(mAlbumInfo.ALBUM_ID, SortType.SORT_LE_RADIO_LOCAL));
        mAllView.setVisibility(View.GONE);
        mCompleteButton.setVisibility(View.GONE);
        mFavoriteView.setVisibility(View.VISIBLE);
        if (musicListAdapter != null) {
            musicListAdapter.setIsDownload(mIsDownLoad);
        }
        if (nowMedials.size()>0){
            ToastUtil.show(mContext,R.string.str_add_to_download);
            Trace.Debug("###### nowMedle size"+nowMedials.size());
        }
        new Thread() {
            @Override
            public void run() {
                MusicDownloadManager.startDownloadLeradioWithServer(mHandler, mAlbumInfo, nowMedials);
            }
        }.start();

        musicListAdapter.notifyDataSetChanged();
    }

    @Override
    public void musicStart() {
    }

    @Override
    public void musicStop() {
    }

    public int mCurrentIndex;

    @Override
    public void musicIndex(int index) {
        mCurrentIndex = index;
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MessageTypeCfg.MUSICINDEX);
        }
    }

    @Override

    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        mediaList.clear();
        refreshView();

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        int page = 0;
        if (mediaList == null || mediaList.size() <= 0) {
            mIndex = 1;
            page = 1;
        } else {
            page = mIndex;
        }
        mLoader.load(page);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
        mediaList.clear();
        mDownlodDetails.clear();
        lePlayer.setListViewItemStateListener(null);
    }

    class SelectionAdapter extends BaseAdapter {
        private Context context;
        private List<String> mStrings;
        private LayoutInflater mInflater;


        public SelectionAdapter(List<String> mediaDetails) {
            this.mStrings = mediaDetails;
            mInflater = LayoutInflater.from(mContext);
        }


        @Override
        public int getCount() {
            return mStrings == null ? 0 : mStrings.size();
        }

        @Override
        public Object getItem(int position) {
            return mStrings == null ? null : mStrings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_selection, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.album_edit_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(mStrings.get(position));
            if (mStrings.get(position).equals(mSelectionString)) {
                viewHolder.textView.setBackgroundResource(R.drawable.album_eidt_hightlight_selector);
                viewHolder.textView.setTextColor(mContext.getResources().getColor(R.color.green_color));
            } else {
                viewHolder.textView.setBackgroundResource(R.drawable.album_eidt_custom_selector);
                viewHolder.textView.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
            }

            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }
}

