package com.letv.leauto.ecolink.ui.page;

import static com.letv.mobile.core.utils.ContextProvider.getApplicationContext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.cfg.SettingMusicCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.LeCPDic;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.download.DownloadEngine;
import com.letv.leauto.ecolink.download.DownloadManager;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.json.CpParse;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.leplayer.model.LTStatus;
import com.letv.leauto.ecolink.leplayer.model.OnStatusChangedListener;
import com.letv.leauto.ecolink.manager.MusicDownloadManager;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.view.EcoSeekBar;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.Constants;
import com.letv.leauto.ecolink.utils.CpIdUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.TelephonyUtil;
import com.letv.leauto.ecolink.utils.TimeUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 15/12/8.
 * 此界面用于展示
 * 1 最近播放记录  Recent  type
 * 2 音乐播放界面 列表
 */
public class MusicPlayPage extends BasePage implements View.OnClickListener, LePlayer.MusicStateListener, OnStatusChangedListener {
    private static final String TAG = "MusicPlayPage";
    private static final int STOP = 0X49;
    private static final int START = 0X50;
    private static final int PRE = 0X94;
    private static final int NEXT = 0X93;
    private static final int REFRESH_DELETE = 0x95;
    private static long lastClickTime = 0;
    private DownloadManager mDownloadManager;
    @Bind(R.id.media_img)
    ImageView mMediaImgview;

    @Bind(R.id.media_mode)
    ImageView mModeView;
    @Bind(R.id.download)
    ImageView mDownloadView;
    @Bind(R.id.media_name)
    TextView mMediaNameTv;
    @Bind(R.id.tv_detail_name)
    TextView mMediaDetailTv;
    @Bind(R.id.media_author)
    TextView mMediaAuthorView;
    @Bind(R.id.source_id)
    TextView mSourceIdView;
    @Bind(R.id.media_process)
    TextView mProcessView;
    @Bind(R.id.media_dua)
    TextView mDurationView;

    @Bind(R.id.pre_media)
    ImageView mPreView;
    @Bind(R.id.next_media)
    ImageView mNextView;
    @Bind(R.id.start_pause)
    ImageView mStartPauseView;
    @Bind(R.id.sbPosition)
    EcoSeekBar mSeekbar;
    //直播顶部显示的文字布局
    @Bind(R.id.ll_top_live)
    LinearLayout mTopLiveLayout;
    @Bind(R.id.tv_zhibo)
    TextView mLiveTv;
    //进度条的布局
    @Bind(R.id.ll_video_progress)
    LinearLayout mProgressLayout;


    private String mSortType, mSortId;
    private LeAlbumInfo mAlbumInfo;
    private ArrayList<MediaDetail> mediaList;
    public int mCurrentIndex = 0;
    private String last_IMG_URL;
    private static final int MSG_AUTONEXT = 10;
    public static final int MSG_HIDE_NETSHADDOW = 21;
    //播放总时长
    private long LeDuration = 0;
    private static boolean isVr = false;
    //用于最后一次搜索语音,退出后重新进入,不再弹出列表界面
    public static boolean mIsStoped = false;
    private HomeActivity homeActivity;
    public int downloadPicPositon = -1;//下载音乐图片的角标
    private String url = "";//音乐图片的url
    private boolean mSeekbarHasFocus = false;
    private boolean mIndexChanged = false;
    private int mIsDownLoad;
    private final int NOT_DOWN_LOAD = 0;
    private final int DOWN_LOADING = 1;
    private final int DOWN_LOADED = 2;
    private final int CANNOT_DOWN_LOAD = 3;
    private Glide mGlide;
    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_HIDE_NETSHADDOW:
                    break;
                case MSG_AUTONEXT:
//                    //每次变化都传一次
                    mediaList = lePlayer.getPlayerList();
                    mCurrentIndex = lePlayer.getIndex();
                    Trace.Debug("####MSG_AUTONEXT");
                    mIndexChanged = true;
                    mIsDownLoad = NOT_DOWN_LOAD;

                    refreshView();

                    break;
                case STOP:
                    mStartPauseView.setImageResource(R.mipmap.music_pause);
                    break;
                case START:
                    mStartPauseView.setImageResource(R.mipmap.music_play);
                    break;
                //请求酷我的图片
                case MessageTypeCfg.MSG_GET_KUWO_PIC_URL:
                    downloadPicPositon++;
                    if (msg.obj != null) {
                        url = (String) msg.obj;
                    }
                    if (mediaList.size() == 0) {
                        return;
                    }
                    mediaList.get(downloadPicPositon).IMG_URL = url;
                    if (downloadPicPositon == mCurrentIndex) {
                        Trace.Debug("#### MSG_GET_KUWO_PIC_URL");
                        refreshView();
                    }
                    if (downloadPicPositon == mediaList.size() - 1) {
                        downloadPicPositon = -1;
                        Trace.Debug("#### MSG_GET_KUWO_PIC_URL");
                        refreshView();
                    }
                    break;
                case NEXT:
                    mNextView.setEnabled(true);
                    break;
                case PRE:
                    mPreView.setEnabled(true);
                    break;
                case REFRESH_DELETE:
                    //修改bug3651
                    if (mCurrentIndex < mediaList.size()) {
                        updateDeleteStateView(mediaList.get(mCurrentIndex));
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private DownloadEngine.DownloadListener mListener = new DownloadEngine.DownloadListener() {
        @Override
        public void onSuccess(MediaDetail task) {
            if (task != null && isTheSame(task)) {
                mIsDownLoad = DOWN_LOADED;
                mHandler.sendEmptyMessage(REFRESH_DELETE);
            }
        }

        @Override
        public void onStart(MediaDetail task) {

        }

        @Override
        public void onPause(MediaDetail task) {

        }

        @Override
        public void onFail(MediaDetail task, int code) {
            if (task != null && isTheSame(task) && code != 0) {
                mIsDownLoad = NOT_DOWN_LOAD;
                mHandler.sendEmptyMessage(REFRESH_DELETE);
            }
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
    };


    public MusicPlayPage(Context context, boolean isVr, LeAlbumInfo leAlbumInfo, ArrayList<MediaDetail> mediaDetails, int index) {
        super(context);
        mAlbumInfo = leAlbumInfo;
        mediaList = mediaDetails;
        this.isVr = isVr;
        mCurrentIndex = index;
        mGlide = Glide.get(context);
    }


    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_music, null);
        } else {
            view = inflater.inflate(R.layout.fragment_music_1, null);
        }

        homeActivity = (HomeActivity) ct;
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void initData() {
        lePlayer.setOnStatusChangedListener(this);
        lePlayer.setMusicStateListener(this);
        setMusicPalyMode();
        noNetView();
        if (!isVr && CacheUtils.getInstance(ct).getBoolean(Constant.IS_FIRST_TIME_MUSIC, true)) {
            CacheUtils.getInstance(ct).putBoolean(Constant.IS_FIRST_TIME_MUSIC, false);
        }
        if (lePlayer.getCurrentStatus() != null && lePlayer.getCurrentStatus().currentItem != null) {

            if (BaseActivity.isStoped) {
                Trace.Debug("#####2");
                lePlayer.stopPlay();
                mStartPauseView.setImageResource(R.mipmap.music_pause);

            } else {
                Trace.Debug("#####1");
                lePlayer.startPlay();
                mStartPauseView.setImageResource(R.mipmap.music_play);
            }
        } else {
            Trace.Debug("#####mediaList:"+mediaList);
            lePlayer.setAlbumInfo(mAlbumInfo);
            lePlayer.setPlayerList(mediaList);
            if (BaseActivity.isStoped) {
                Trace.Debug("#####4");
                lePlayer.playList(mCurrentIndex);
                lePlayer.stopPlay();
                mStartPauseView.setImageResource(R.mipmap.music_pause);

            } else {
                Trace.Debug("#####3");
                if (mCurrentIndex >= 0 && mCurrentIndex < mediaList.size()) {
                } else {
                    mCurrentIndex = 0;
                }
                Trace.Debug("#####mCurrentIndex=" + mCurrentIndex);
                lePlayer.playList(mCurrentIndex);
                mStartPauseView.setImageResource(R.mipmap.music_play);

            }

        }



        mDownloadManager = DownloadManager.getInstance();
        mDownloadManager.registerListener(mListener);

        setLisnerEnable();
        Trace.Debug("####initData");
        refreshView();

    }

    /**
     * D
     * 打开控件监听事件
     */
    private void setLisnerEnable() {
        setSeekbarView(true);
        mModeView.setOnClickListener(this);
        mDownloadView.setOnClickListener(this);
        mPreView.setOnClickListener(this);
        mNextView.setOnClickListener(this);
        mStartPauseView.setOnClickListener(this);

    }

    /**
     * 切换歌曲时更新界面
     */
    private void refreshView() {
        Trace.Debug("####refreshView");
        setImgView(null);
        if (mediaList.isEmpty() || mCurrentIndex < 0 || mCurrentIndex >= mediaList.size()) {
            if (lePlayer.getCurrentStatus() != null && lePlayer.getCurrentStatus().currentItem != null) {
                mMediaNameTv.setText(lePlayer.getCurrentStatus().currentItem.getTitle());
            }

            return;
        }

        if (lePlayer.getCurrentStatus() != null && lePlayer.getCurrentStatus().isPlaying) {
            mStartPauseView.setImageResource(R.mipmap.music_play);
        } else {
            mStartPauseView.setImageResource(R.mipmap.music_pause);
        }
        if (mediaList.size() == 0) {
            return;
        }
        MediaDetail mediaDetail = mediaList.get(mCurrentIndex);

        //修改bug3678
        LTStatus ltStatus = lePlayer.getCurrentStatus();
        if (ltStatus != null) {
            mSeekbar.setMax((int) (ltStatus.duration / 1000));
            mSeekbar.setProgress((int) (ltStatus.progress / 1000));
            setProgress(ltStatus.progress, ltStatus.duration);
        } else {
            return;
        }
        mDownloadView.setEnabled(true);
        mSortType = mAlbumInfo.TYPE;
        Trace.Debug("####mSortType=" + mSortType);
        if (SortType.SORT_KUWO.equals(mSortType)) {
            mModeView.setClickable(true);

            mNextView.setClickable(true);
            mPreView.setClickable(true);

            if (TextUtils.isEmpty(mediaDetail.SOURCE_URL)) {
                mDownloadView.setEnabled(false);
                mDownloadView.setImageResource(R.mipmap.download_icon_gray);
                mIsDownLoad = CANNOT_DOWN_LOAD;

            } else {
                updateDeleteStateView(mediaDetail);
            }

        } else if (SortType.SORT_LIVE.equals(mSortType) || (mediaDetail.LE_SOURCE_VID != null && mediaDetail.LE_SOURCE_MID != null &&
                !mediaDetail.LE_SOURCE_MID.equals("") && !mediaDetail.LE_SOURCE_VID.equals(""))) {
            mModeView.setVisibility(View.VISIBLE);
            Trace.Debug("### media name" + mediaDetail.LE_SOURCE_VID + ",mediaDetail.XIA_MI_ID=" + mediaDetail.XIA_MI_ID);
//            mModeView.setClickable(false);
//            mDownloadView.setClickable(false);
            mNextView.setClickable(true);
            mPreView.setClickable(true);
            mDownloadView.setEnabled(false);
            mDownloadView.setImageResource(R.mipmap.download_icon_gray);
            mModeView.setImageResource(R.mipmap.music_mode_loops_grey);
            mModeView.setEnabled(false);
//            mModeView.setImageResource(R.mipmap.);

        } else if ((SortType.SORT_DOWNLOAD.equals(mSortType) || SortType.SORT_LOCAL.equals(mSortType) || SortType.SORT_KUWO_LOCAL.equals(mSortType) || SortType.SORT_LOCAL_ALL.equals(mSortType) || SortType.SORT_LE_RADIO_LOCAL.equals(mSortType))) {
            mModeView.setClickable(true);
            mDownloadView.setClickable(true);
            mNextView.setClickable(true);
            mPreView.setClickable(true);
            Trace.Debug("####mediaDetail.SOURCE_URL=" + mediaDetail.SOURCE_URL);
            if (mediaDetail.SOURCE_URL == null || mediaDetail.SOURCE_URL.isEmpty()) {
                mIsDownLoad = NOT_DOWN_LOAD;
                mDownloadView.setImageResource(R.mipmap.download_icon);
                mDownloadView.setEnabled(true);
            } else {
                File file = new File(mediaDetail.SOURCE_URL);
                if (file.exists()) {
                    mediaDetail.setFileIfExist(true);
                }
                Trace.Debug("####mediaDetail.fileIfExist=" + mediaDetail.fileIfExist);
                if (mediaDetail.fileIfExist) {
                    mIsDownLoad = DOWN_LOADED;
                    mDownloadView.setImageResource(R.mipmap.delete_icon);
                    mDownloadView.setEnabled(true);
                } else {
                    mIsDownLoad = NOT_DOWN_LOAD;
                    mDownloadView.setImageResource(R.mipmap.download_icon);
                    mDownloadView.setEnabled(true);
                }
            }

        } else {

            mModeView.setClickable(true);
            mDownloadView.setClickable(true);
            mNextView.setClickable(true);
            mPreView.setClickable(true);
            Trace.Debug("####else####mediaDetail.SOURCE_URL=" + mediaDetail.SOURCE_URL);
            if (mediaDetail.SOURCE_URL != null && !mediaDetail.SOURCE_URL.trim().equals("") && mediaDetail.SOURCE_URL.startsWith("/")) {
                File file = new File(mediaDetail.SOURCE_URL);
                if (file.exists()) {
                    mediaDetail.setFileIfExist(true);
                }
                Trace.Debug("####else####mediaDetail.fileIfExist=" + mediaDetail.fileIfExist);
                if (mediaDetail.fileIfExist) {
                    mIsDownLoad = DOWN_LOADED;
                    mDownloadView.setImageResource(R.mipmap.delete_icon);
                    mDownloadView.setEnabled(true);
                } else {
                    mIsDownLoad = NOT_DOWN_LOAD;
                    mDownloadView.setImageResource(R.mipmap.download_icon);
                    mDownloadView.setEnabled(true);
                }
            } else {
                Trace.Debug("####else####mediaDetail.getPlayType()=" + mediaDetail.getPlayType());
                if (mediaDetail.getPlayType() == null || mediaDetail.getPlayType().isEmpty()) {
                    mediaDetail.setPlayType(Constants.TYPE_AUDIO);
                }

                if (mediaDetail.getPlayType().equals(Constants.TYPE_VIDEO)) {

                    mIsDownLoad = CANNOT_DOWN_LOAD;
                    mDownloadView.setImageResource(R.mipmap.download_icon_gray);
                    mDownloadView.setEnabled(false);

                } else if (mediaDetail.getPlayType().equals(Constants.TYPE_AUDIO)) {
                    Trace.Debug("####TYPE_AUDIO=" + mediaDetail.getSourceName()+"，mediaDetail.DOWNLOAD_FLAG="+mediaDetail.DOWNLOAD_FLAG);
                    if (mediaDetail.getSourceName() != null && (mediaDetail.getSourceName().contains("虾米") || mediaDetail.getSourceName().contains("Xiami"))/*|| mediaDetail.DOWNLOAD_FLAG==0*/) {
                        mIsDownLoad = CANNOT_DOWN_LOAD;
                        mDownloadView.setImageResource(R.mipmap.download_icon_gray);
                        mDownloadView.setEnabled(false);
                    } else {
                        Trace.Debug("####updateDeleteStateView");
                        updateDeleteStateView(mediaDetail);
                    }
                }
            }

        }
        if (mediaList.size() == 0) {
            return;
        }
        //修改bug620,20160601,begin
        Trace.Debug("###current media name" + mediaList.get(mCurrentIndex).NAME);
        mMediaNameTv.setText(mediaList.get(mCurrentIndex).NAME);

        if (mediaList.get(mCurrentIndex).AUTHOR != null) {
            if (mediaList.get(mCurrentIndex).AUTHOR.contains(ct.getString(R.string.str_unkhow_author)) || mediaList.get(mCurrentIndex).AUTHOR.contains(ct.getString(R.string.str_unkhow))
                    || "".equals(mediaList.get(mCurrentIndex).AUTHOR)) {
                mMediaAuthorView.setText(R.string.str_anonymouse);
            } else {
                mMediaAuthorView.setText(mediaList.get(mCurrentIndex).AUTHOR);
            }
        } else {
            mMediaAuthorView.setText(R.string.str_anonymouse);
            mMediaDetailTv.setText(mediaList.get(mCurrentIndex).ALBUM);
        }
        String cpName = null;
        if(mediaList.get(mCurrentIndex).LE_SOURCE_MID!=null && mediaList.get(mCurrentIndex).LE_SOURCE_VID!=null&&!mediaList.get(mCurrentIndex).LE_SOURCE_MID.isEmpty()
                && !mediaList.get(mCurrentIndex).LE_SOURCE_VID.isEmpty()){
            cpName = getCpName(/*mAlbumInfo.SOURCE_CP_ID*/mediaList.get(mCurrentIndex).SOURCE_CP_ID);
        }else {
            cpName = mediaList.get(mCurrentIndex).getSourceName();
        }
        Trace.Debug("####cpName" + cpName +"，mediaList.get(mCurrentIndex).getSourceName()="+mediaList.get(mCurrentIndex).getSourceName());
        if (cpName == null||cpName.isEmpty()) {
            mSourceIdView.setText("");
        } else if (cpName != null && (cpName.contains("虾米") || cpName.contains("Xiami"))) {
            mSourceIdView.setText("");
        } else {
            mSourceIdView.setText("合作媒体：" + cpName);
        }
        if (SortType.SORT_KUWO.equals(mAlbumInfo.TYPE)) {
            mTopLiveLayout.setVisibility(View.GONE);
            mProgressLayout.setVisibility(View.VISIBLE);
        } else if (mediaList.get(mCurrentIndex).END_TIME != null && mediaList.get(mCurrentIndex).START_TIME != null && (!SortType.SORT_LOCAL.equals(mSortType) && null != cpName) && (cpName.contains("直播") || cpName.contains("乐视车联"))) {
            mTopLiveLayout.setVisibility(View.VISIBLE);
            if (mediaList.get(mCurrentIndex).START_TIME < System.currentTimeMillis() / 1000 && System.currentTimeMillis() / 1000 < mediaList.get(mCurrentIndex).END_TIME) {
                mLiveTv.setText(R.string.str_live_doing);
            } else {
                mLiveTv.setText(R.string.str_live_not_start);
            }
            mProgressLayout.setVisibility(View.GONE);
        } else {
            if((mediaList.get(mCurrentIndex).END_TIME == null && mediaList.get(mCurrentIndex).START_TIME == null) && (!SortType.SORT_LOCAL.equals(mSortType) && null != cpName) && (cpName.contains("直播") || cpName.contains("乐视车联"))){
                mTopLiveLayout.setVisibility(View.VISIBLE);
                mLiveTv.setText("直播中");
                mProgressLayout.setVisibility(View.GONE);
            }else {
                mTopLiveLayout.setVisibility(View.GONE);
                mProgressLayout.setVisibility(View.VISIBLE);
            }
        }
        if (null != mediaList.get(mCurrentIndex).IMG_URL && !"".equals(mediaList.get(mCurrentIndex).IMG_URL)) {
            setImgView(mediaList.get(mCurrentIndex).getRealImgUrl());
        } else {
            setImgView(mAlbumInfo.getRealImgUrl());
        }
        switch (LePlayer.TYPE) {
            case 1:
                lePlayer.OPEN_LERADIO = true;
                HomeActivity.mLastMusicLayout = Constant.TAG_LERADIO;
                break;
            case 2:
                lePlayer.OPEN_KUWO = true;
                HomeActivity.mLastMusicLayout = Constant.TAG_KUWO;
                break;
            case 3:
                lePlayer.OPEN_LOCAL = true;
                HomeActivity.mLastMusicLayout = Constant.TAG_LOCAL;
                break;


        }

    }

    @Override
    public void musicStart() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(START);
        }
    }

    @Override
    public void musicStop() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(STOP);
        }
    }

    @Override
    public void musicIndex(int index) {
        Trace.Debug("#####index =" + index);
        mCurrentIndex = index;
        if (mHandler!=null){
            mHandler.sendEmptyMessage(MSG_AUTONEXT);}
        Trace.Error("===ddd", "musicPlayPage");
    }

    /**
     * 获取cp源
     *
     * @param cp_id
     */

    private String getCpName(String cp_id) {
        String cp_name = "";
        String json = "";
        Trace.Debug("MusicFragment", "cp_id:" + cp_id);
        if (isVr) {
            Trace.Debug("MusicFragment", "cp_id=" + cp_id);
            cp_name = CpIdUtils.CpId2CpName(cp_id);
            if (cp_name.contains("虾米")||cp_name.contains("Xiami")) {
                mSourceIdView.setText("");
            } else {
                mSourceIdView.setText(ct.getString(R.string.str_copration_media) + cp_name);
            }
        } else {
            json = EcoApplication.LeGlob.getCache().getString(DataUtil.CP_LIST, null);

            Trace.Debug("MusicFragment", "CP_LIST:json=" + json);
            //空指针判断
            if (cp_id == null || json == null) {
                return null;
            }
            try {

                ArrayList<LeCPDic> list = CpParse.getCPList(new JSONArray(json));
                Trace.Debug("MusicFragment", "list=" + list);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        LeCPDic dic = list.get(i);
                        Trace.Debug("MusicFragment", "dic=" + dic);
                        if (dic.SOURCE_CP_ID.equals(cp_id)) {
                            Trace.Debug("MusicFragment", "dic.NAME=" + dic.NAME);
                            cp_name = dic.NAME;
                            if(cp_name==null||cp_name.isEmpty()){
                                mSourceIdView.setText("");
                            }else {
                                mSourceIdView.setText("合作媒体：" + cp_name);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        Trace.Debug("MusicFragment", "CpName=" + cp_name);
        return cp_name;
    }

    /**
     * 设置音乐图片
     *
     * @param img_url
     */
    private void setImgView(String img_url) {
        last_IMG_URL = img_url;

        mGlide.with(EcoApplication.instance).load(img_url)
                .asBitmap()
                .skipMemoryCache(true)
                .placeholder(R.mipmap.ic_defult)
                .error(R.mipmap.ic_defult)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mMediaImgview);

    }

    /**
     * 获取专辑列表
     */
    private void getMediaList() {
        if (SortType.SORT_LOCAL.equals(mSortType) || SortType.SORT_DOWNLOAD.equals(mSortType) || SortType.SORT_FAVOR.equals(mSortType)) {
            DataUtil.getInstance().getMediaListFromDB(mHandler, mSortType, mAlbumInfo.ALBUM_ID, "media");
        } else if (SortType.SORT_RECENT.equals(mSortType)) {
            Trace.Debug("####getMediaList####SORT_RECENT--> mAlbumInfo=" + mAlbumInfo);
            DataUtil.getInstance().getMediaList(mHandler, "tag", mSortId, mAlbumInfo);
        } else {
            Trace.Debug("####getMediaList#### mAlbumInfo=" + mAlbumInfo);
            DataUtil.getInstance().getMediaList(mHandler, "tag", mSortId, mAlbumInfo);
        }
    }

    /**
     * 无网络时直接切换到AlbumList
     */
    private void replaceAlbumFragment() {
        //homeActivity.changeToKuwo();
        homeActivity.changeToLocal();
    }


    private void setSeekbarView(boolean show) {
        if (show) {
            mSeekbar.setVisibility(View.VISIBLE);
//            if (GlobalCfg.IS_DONGFEN) {
//                mSeekbar.setThumb(ct.getResources().getDrawable(R.mipmap.music_progress_dongfeng));
//            } else {
//                mSeekbar.setThumb(ct.getResources().getDrawable(R.drawable.music_progress_selector));
//            }
            mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                boolean userTouch = false;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        if (LeDuration != 0) {
                            if ((progress * 1000) >= LeDuration) {
                                progress = (int) (LeDuration * 90 / 100);
                            }
                            if (Math.abs(LeDuration - progress * 1000) < 1000) {
                                if (lePlayer.getCurrentStatus().currentItem != null) {
                                    MediaOperation mediaOperation = MediaOperation.getInstance();
                                    LTStatus ltStatus = lePlayer.getCurrentStatus();
                                    ltStatus.progress = 0;
                                }
                            }
                            lePlayer.seekTo(progress * 1000);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mSeekbarHasFocus = true;

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mSeekbarHasFocus = false;
//                    int progress = seekBar.getProgress();
//                    if (LeDuration != 0) {
//                        if ((progress * 1000) >= LeDuration) {
//                            progress = LeDuration * 95 / 100;
//                        }
//                        lePlayer.seekTo(progress * 1000);
//                    }
                }

            });

            mSeekbar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mSeekbarHasFocus = hasFocus;

                }
            });


        } else {
            mSeekbar.setVisibility(View.GONE);
        }


    }

    public void setProgress(long progress, long duration) {
        if (duration > 28800000) {
            return;
        }
        long max = duration / 1000;
        long pos = progress / 1000;
        if (pos > max)
            return;
        mProcessView.setText(TimeUtils.secToTime(pos + ""));
        if (TimeUtils.secToTime(max + "").equals("00:00")) {
            mDurationView.setText("");
        } else {
            mDurationView.setText(TimeUtils.secToTime(max + ""));
        }

    }

    @Override
    public void onProgressChanged(long progress, long duration) {
        LeDuration = duration;
        if (!mSeekbarHasFocus) {
            if (duration == 0) {
                mSeekbar.setProgress(0);
            } else {
                setProgress(progress, duration);

                if (mSeekbar != null && mSeekbar.getVisibility() == View.VISIBLE) {
                    mSeekbar.setMax((int) (duration / 1000));
                    mSeekbar.setProgress((int) (progress / 1000));
                }
            }
        }
    }


    @Override
    public void onSongChanged(String url, int position) {
    }

    @Override
    public void onVolumeChanged(float leftVolume, float rightVolume) {
    }

    @Override
    public void onError(int errCode, int extra) {
        Trace.Debug("####### onerror");
        if (mHandler!=null && errCode == 97) {
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_NETSHADDOW, 3000);
            ttsHandlerController.speak(ct.getString(R.string.str_paly_erro));
            Trace.Debug("#####stop");
            NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(ct, R.string.str_paly_err_title, R.string.str_paly_back_abum, R.string.str_paly_next);
            networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                @Override
                public void onConfirm(boolean checked) {
                    homeActivity.ChangeToLeradio();
                }

                @Override
                public void onCancel() {
                    mIsStoped = false;
                    lePlayer.playNext(false);
                }
            });
            networkConfirmDialog.show();
            lePlayer.stopPlay();
        }
    }

    @Override
    public void onPrepared() {

    }


    @Override
    public void onClick(View v) {
        if (mediaList.size() == 0 && v.getId() != R.id.start_pause) {
            return;
        }
        switch (v.getId()) {

            case R.id.media_mode:
                changeMusicPalyMode();
                break;

            case R.id.download:
                if (mediaList.size() == 0) {
                    return;
                }

                if (SortType.SORT_KUWO.equals(mAlbumInfo.TYPE)) {
                    final MediaDetail mediaDetail = mediaList.get(mCurrentIndex);

                    if (mIsDownLoad == DOWN_LOADED) {
                        NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(ct, R.string.delete_download, R.string.ok, R.string.cancel);
                        networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                            @Override
                            public void onConfirm(boolean checked) {
                                DownloadManager downloadManager = DownloadManager.getInstance();
                                mediaDetail.TYPE = SortType.SORT_KUWO_LOCAL;
                                downloadManager.remove(mediaDetail);
                                mIsDownLoad = NOT_DOWN_LOAD;
                                mDownloadView.setImageResource(R.mipmap.download_icon);
                                ToastUtil.showShort(ct, ct.getString(R.string.delete_success));
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                        networkConfirmDialog.show();

                    } else {

                        if (NetUtils.isConnected(ct)) {
                            if (NetUtils.isWifi(ct)) {
                                downLoadKuwo(mediaDetail);
                            } else {
                                if (CacheUtils.getInstance(ct).getBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, false)) {
                                    downLoadKuwo(mediaDetail);
                                } else {
                                    NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(ct, R.string.mobile_download, R.string.ok, R.string.cancel, true);
                                    networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                                        @Override
                                        public void onConfirm(boolean checked) {
                                            downLoadKuwo(mediaDetail);
                                            CacheUtils.getInstance(ct).putBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, true);
                                            ct.sendBroadcast(new Intent(SettingCfg.BROADCAST_DOWNlOAD_SWICH));

                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    });
                                    networkConfirmDialog.show();
                                }

                            }

                        } else {
                            NetUtils.showNoNetDialog(ct);

                        }
                    }

                } else if (SortType.SORT_DOWNLOAD.equals(mSortType) || SortType.SORT_LOCAL.equals(mSortType) || SortType.SORT_LOCAL_NEW.equals(mSortType) ||
                        SortType.SORT_KUWO_LOCAL.equals(mSortType) || SortType.SORT_LOCAL_ALL.equals(mSortType) || SortType.SORT_LE_RADIO_LOCAL.equals(mSortType)) {
                    final MediaDetail mediaDetail = mediaList.get(mCurrentIndex);
                    Trace.Debug("#### path " + mediaDetail.toString());

                    NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(ct, R.string.delete_download, R.string.ok, R.string.cancel);
                    networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                        @Override
                        public void onConfirm(boolean checked) {
                            deleteFile(mediaDetail);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    networkConfirmDialog.show();

                } else {
                    if (mediaList.size() == 0) {
                        return;
                    }

                    final MediaDetail mediaDetail = mediaList.get(mCurrentIndex);

                    if (mIsDownLoad == DOWN_LOADED) {
                        NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(ct, R.string.delete_download, R.string.ok, R.string.cancel);
                        networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                            @Override
                            public void onConfirm(boolean checked) {
                                DownloadManager downloadManager = DownloadManager.getInstance();
                                mediaDetail.TYPE = SortType.SORT_LE_RADIO_LOCAL;
                                downloadManager.remove(mediaDetail);
                                mIsDownLoad = NOT_DOWN_LOAD;
                                mDownloadView.setImageResource(R.mipmap.download_icon);

                                if (mediaDetail.SOURCE_URL == null || mediaDetail.SOURCE_URL.trim().equals("")) {
                                    return;
                                }

                                File file = new File(mediaDetail.SOURCE_URL);
                                if (file.exists()) {
                                    file.delete();
                                }
                                mediaDetail.setFileIfExist(false);
                                ToastUtil.showShort(ct, "删除成功");
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                        networkConfirmDialog.show();

                    } else {
                        if (NetUtils.isConnected(ct)) {
                            if (NetUtils.isWifi(ct)) {
                                downLoadLeradio(mediaDetail);
                            } else {
                                if (CacheUtils.getInstance(ct).getBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, false)) {
                                    downLoadLeradio(mediaDetail);
                                } else {
                                    NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(ct, R.string.mobile_download, R.string.ok, R.string.cancel, true);
                                    networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                                        @Override
                                        public void onConfirm(boolean checked) {
                                            downLoadLeradio(mediaDetail);

                                            mIsDownLoad = DOWN_LOADING;
                                            mDownloadView.setImageResource(R.mipmap.delete_icon_gray);
                                            CacheUtils.getInstance(ct).putBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, true);
                                            ct.sendBroadcast(new Intent(SettingCfg.BROADCAST_DOWNlOAD_SWICH));

                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    });
                                    networkConfirmDialog.show();
                                }

                            }

                        } else {
                            NetUtils.showNoNetDialog(ct);

                        }

                    }


                }
                break;

            case R.id.pre_media:
                if (!isClickAble()) {
                    return;
                }
                mIsStoped = false;
                lePlayer.playPrev();
                break;
            case R.id.next_media:
                if (!isClickAble()) {
                    return;
                }
                mIsStoped = false;
                lePlayer.playNext(false);
                break;
            case R.id.start_pause:
                if (!isClickAble()) {
                    return;
                }
                startOrPause();
                break;
            case R.id.bt_local_see:
                Trace.Debug("MusicFragment", "onActivityCreated   onclick");
                CacheUtils.getInstance(ct).putString(Constant.Radio.NONET, "MusicFragment");
                replaceAlbumFragment();

                break;
            case R.id.bt_refresh:
                Trace.Debug("MusicFragment", "onActivityCreated   onclick");
                if (NetUtils.isConnected(ct)) {
                    getMediaList();

                }
                break;
            default:
                break;
        }
    }


    /*
     * Prevent repeated clicks
     */
    public static boolean isClickAble() {
        long timeInterval = System.currentTimeMillis() - lastClickTime;
        Trace.Debug(TAG, "isClickAble time : " + timeInterval);
        if (timeInterval < 1000) {
            if (timeInterval < 0) {
                lastClickTime = System.currentTimeMillis();
            }
            return false;
        }
        lastClickTime = System.currentTimeMillis();
        return true;
    }

    private void downLoadKuwo(MediaDetail mediaDetail) {
        ToastUtil.show(ct, ct.getString(R.string.str_add_to_download));
        com.letv.leauto.ecolink.download.DownloadManager downloadManager = com.letv.leauto.ecolink.download.DownloadManager.getInstance();
        downloadManager.add(mediaDetail, SortType.SORT_KUWO_LOCAL);

    }

    private void downLoadLeradio(MediaDetail mediaDetail) {
        mDownloadManager = com.letv.leauto.ecolink.download.DownloadManager.getInstance();
        mDownloadManager.add(mediaDetail, SortType.SORT_LE_RADIO_LOCAL);
        mDownloadView.setImageResource(R.mipmap.delete_icon_gray);
        mIsDownLoad = DOWN_LOADING;
        LetvReportUtils.reportDownloadMusic(mediaDetail.ALBUM_ID, mediaDetail);
        ToastUtil.show(ct, ct.getString(R.string.str_add_to_download));
    }

    private void setMusicPalyMode() {
        int mode = CacheUtils.getInstance(ct).getInt(SettingCfg.PALY_MODE, 1);
        setMediaModeView(mode);
    }

    private void changeMusicPalyMode() {
        int mode = CacheUtils.getInstance(ct).getInt(SettingCfg.PALY_MODE, 1);
        switch (mode) {
            case 1:/*顺序播放*/
                mode = 2;
                break;
            case 2:/*单曲循环*/
                mode = 3;
                break;
            case 3: /*随机播放*/
                mode = 1;
                break;
            default:
                mode = 1;
                break;
        }
        setMediaModeView(mode);
        CacheUtils.getInstance(ct).putInt(SettingCfg.PALY_MODE, mode);
        lePlayer.setPlayMode(mode);

    }

    public void setMode(int mode){
        CacheUtils.getInstance(ct).putInt(SettingCfg.PALY_MODE, mode);
        lePlayer.setPlayMode(mode);
        setMediaModeView(mode);


    }

    private void setMediaModeView(int mode) {
        switch (mode) {
            case LePlayer.MODE_ORDER:
                mModeView.setImageResource(R.mipmap.music_mode_order);
                break;
            case LePlayer.MODE_SINGLE:
                mModeView.setImageResource(R.mipmap.music_mode_loops);
                break;
            case LePlayer.MODE_RANDOM:
                mModeView.setImageResource(R.mipmap.music_mode_random);
                break;
            default:
                mModeView.setImageResource(R.mipmap.music_mode_order);
                break;

        }
    }


    /**
     * 暂停播放
     */
    private void startOrPause() {
        if (lePlayer!=null && lePlayer.getCurrentStatus()!=null && lePlayer.getCurrentStatus().isPlaying) {
            mIsStoped = true;
            HomeActivity.isStoped = true;
            Trace.Debug("#####stop");
            lePlayer.stopPlay();
            mStartPauseView.setImageResource(R.mipmap.music_pause);
        } else {

            if (TelephonyUtil.getInstance(ct).isTelephonyCalling())
                return;
            mIsStoped = false;
            //lePlayer.startPlay();
            Trace.Debug("####start");
            mStartPauseView.setImageResource(R.mipmap.music_play);
            if (NetUtils.isConnected(getApplicationContext())) {
                if (NetUtils.isWifi(getApplicationContext())) {
                    lePlayer.startPlay();

                } else {
                    if (CacheUtils.getInstance(getApplicationContext()).getBoolean(SettingMusicCfg.USER_MOBILE_NET_PLAY, false)) {
                        lePlayer.startPlay();
                    } else {
                        NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(ct, R.string.mobile_play, R.string.ok, R.string.cancel, true);
                        networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                            @Override
                            public void onConfirm(boolean checked) {
                                CacheUtils.getInstance(getApplicationContext()).putBoolean(SettingMusicCfg.USER_MOBILE_NET_PLAY, true);
                                ct.sendBroadcast(new Intent(SettingCfg.BROADCAST_2G_PLAY_SWICH));
                                lePlayer.startPlay();
                            }

                            @Override
                            public void onCancel() {
                                return;

                            }
                        });
                        networkConfirmDialog.setCancelable(false);
                        networkConfirmDialog.show();
                    }
                }
            }
        }
    }

    private void deleteFile(MediaDetail mediaDetail) {
        if (mediaDetail.SOURCE_URL == null || (mediaDetail.SOURCE_URL != null && mediaDetail.SOURCE_URL.trim().equals(""))) {
            return;
        }

        File file = new File(mediaDetail.SOURCE_URL);
        if (file.exists()) {
            file.delete();
        }

        MusicDownloadManager.deleteFile(mediaDetail.SOURCE_URL);
        mediaDetail.setFileIfExist(false);
        MediaOperation.getInstance().deleteMediaDetailbyAudioId(mediaDetail.TYPE, mediaDetail.AUDIO_ID);
        mIsDownLoad = CANNOT_DOWN_LOAD;
        mDownloadView.setEnabled(false);
        mDownloadView.setImageResource(R.mipmap.delete_icon_gray);
        mediaList.remove(mediaDetail);
        lePlayer.remove(mediaDetail);
        ToastUtil.showShort(ct, "删除成功");
        if (mediaList.size() == 0) {
            mIsStoped = true;
            lePlayer.stopPlay();
        } else {
            mIsStoped = false;
            lePlayer.playNext(true);
        }


    }

    /**
     * 无网络遮罩
     */
    private void noNetView() {
        if (!isNetConnect && !SortType.SORT_LOCAL.equals(mSortType) && !SortType.SORT_DOWNLOAD.equals(mSortType)) {

        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setDongfenBar() {
        mSeekbar.invalidate();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mSeekbar.getLayoutParams();
        if (GlobalCfg.IS_DONGFEN) {
            mSeekbar.setThumb(ct.getResources().getDrawable(R.mipmap.music_progress_custom));
        } else {
            mSeekbar.setThumb(ct.getResources().getDrawable(R.drawable.music_progress_selector));

        }
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mSeekbar.setLayoutParams(layoutParams);
    }

    private void updateDeleteStateView(MediaDetail mediaDetail) {
        Trace.Debug("####updateDeleteStateView####mediaDetail.AUDIO_ID=" + mediaDetail.AUDIO_ID + ",mIsDownLoad=" + mIsDownLoad);
        if (MediaOperation.getInstance().isDownLoadMusic(SortType.SORT_LE_RADIO_LOCAL, mediaDetail)) {
            mDownloadView.setImageResource(R.mipmap.delete_icon);
            mIsDownLoad = DOWN_LOADED;
            mDownloadView.setEnabled(true);
            File file = new File(mediaDetail.getFile());
            if (file.exists()) {
                mediaDetail.fileIfExist = true;
            }
        } else if (TextUtils.isEmpty(mediaDetail.AUDIO_ID)) {
            mDownloadView.setImageResource(R.mipmap.download_icon_gray);
            mIsDownLoad = CANNOT_DOWN_LOAD;
            mDownloadView.setEnabled(false);
        } else {
            if (mIsDownLoad == DOWN_LOADING) {
                mDownloadView.setImageResource(R.mipmap.delete_icon_gray);
                mDownloadView.setEnabled(false);
            } else if (mIsDownLoad == DOWN_LOADED) {
                mDownloadView.setImageResource(R.mipmap.delete_icon);
                mDownloadView.setEnabled(true);
            } else if (mIsDownLoad == NOT_DOWN_LOAD) {
                mDownloadView.setImageResource(R.mipmap.download_icon);
                mDownloadView.setEnabled(true);
            } else if (mIsDownLoad == CANNOT_DOWN_LOAD) {
                mDownloadView.setImageResource(R.mipmap.download_icon_gray);
                mDownloadView.setEnabled(false);
            }
        }
    }

    protected void onDestroy() {
        mDownloadManager.unregisterListener(mListener);
    }

    private boolean isTheSame(MediaDetail newOne) {
        if (mCurrentIndex >= mediaList.size()) {
            return false;
        }
        MediaDetail current = mediaList.get(mCurrentIndex);
        if (newOne == null && current != null) {
            return false;
        } else if (newOne != null && current == null) {
            return false;

        } else {
            if (newOne.AUDIO_ID.equals(current.AUDIO_ID)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void destory() {
        super.destory();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
        mGlide.with(EcoApplication.instance).onDestroy();
    }
}
