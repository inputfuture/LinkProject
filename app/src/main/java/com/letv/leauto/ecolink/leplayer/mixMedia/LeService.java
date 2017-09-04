package com.letv.leauto.ecolink.leplayer.mixMedia;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.cfg.SettingMusicCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.leplayer.model.LTStatus;
import com.letv.leauto.ecolink.leplayer.model.OnStatusChangedListener;
import com.letv.leauto.ecolink.leplayer.model.PlayItem;
import com.letv.leauto.ecolink.thincar.protocol.LeRadioSendHelp;
import com.letv.leauto.ecolink.thincar.protocol.VoiceAssistantHelp;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicDetailModel;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.leauto.ecolink.utils.Utils;
import com.letv.leauto.ecolink.utils.VolumeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class LeService extends Service implements IMListener {
    private static final String TAG = "zhao111";
    private static final int MSG_STATUS_TICK = 99;
    private static final int MSG_STREAM_VOLUME = 98;
    private static final int MSG_RETRY_MEDIA = 97;
    private static final int MSG_NEXT_MEDIA = 96;
    private static final int PLAY_MUSIC = 100;

    private final LocalBinder binder = new LocalBinder();
    private ArrayList<OnStatusChangedListener> statusListener;
    private IMPlayer player,xPlayer, yunPlayer;
    private int Play_Mode = LePlayer.MODE_ORDER;

    private volatile ArrayList<PlayItem> playList;
    private int Current_Index = 0;
    private boolean userPlaying = true;    //是否用户设置服务为播放状态
    private boolean isLoop = true;
    private boolean isUserChangeVolume;
    private int lastSongIndex;
    private int defVolume = -5;
    private int retryNum = 0;
    private LTStatus status;

    public static volatile boolean isFromuser;


    private IndexChangeListener mIndexChangeListener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_STATUS_TICK: {
                    this.removeMessages(MSG_STATUS_TICK);
                    getMediaStatus();
                }
                break;
                case MSG_STREAM_VOLUME: {
                    if (!isUserChangeVolume) {
                        defVolume = VolumeUtils.getStreamVolume(LeService.this, AudioManager.STREAM_MUSIC);
                    }
                    isUserChangeVolume = false;
                }
                break;
                case MSG_RETRY_MEDIA:
                    playCurrent();
                    break;
                case MSG_NEXT_MEDIA:
                    playNext();
                    break;
                case PLAY_MUSIC:
                    Trace.Debug("#####playCurrent:" + PLAY_MUSIC);
                    playCurrentMusic();
                    break;
                //点击item获取Url的地址
                case MessageTypeCfg.MSG_GET_MUSIC_URL:
                    MusicDetailModel model = (MusicDetailModel) msg.obj;
                    if (model != null && model.playUrl != null) {
                        Trace.Debug("#####MSG_GET_MUSIC_URL:" + model.playUrl);
                        getPlayList().get(Current_Index).setUrl(model.playUrl);
                        setPlayer(getPlayList().get(Current_Index));
                    }
                    break;
            }
        }
    };
    public synchronized void onDuckBegin() {
        getPlayer().onDuckBegin();
    }


    public synchronized void onDuckEnd() {
        getPlayer().onDuckEnd();
    }

    public void setIndexChangeListener(IndexChangeListener indexChangeListener) {
        mIndexChangeListener = indexChangeListener;
    }


    public LeService() {
        lastSongIndex = -1;

    }

    Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;

    }

    public void decCurrentIndex() {
        Current_Index--;
    }

    public void remove(MediaDetail mediaDetail) {
        Iterator<PlayItem> it = playList.iterator();
        while (it.hasNext()) {
            PlayItem item = (PlayItem) it.next();
            if (item.getUrl().equals(mediaDetail.SOURCE_URL)) {
               // MediaOperation.getInstance().deleteLTItem(item);
                it.remove();
                break;
            }
        }
        Current_Index--;
    }

    public class LocalBinder extends Binder {
        public LeService getService() {
            return LeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        Play_Mode = CacheUtils.getInstance(getApplicationContext()).getInt(SettingCfg.PALY_MODE, 1);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        CacheUtils.getInstance(getApplicationContext()).putInt(SettingCfg.PALY_MODE, 1);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onPrepared(IMPlayer player) {
        handler.removeMessages(MSG_RETRY_MEDIA);
        handler.removeMessages(MSG_NEXT_MEDIA);
        if (this.statusListener != null) {
            for (OnStatusChangedListener listener : this.statusListener) {
                if (listener != null) {
                    listener.onPrepared();
                }
            }
//            this.statusListener.onPrepared();
        }
        retryNum = 0;
        Trace.Debug("#####AMediaPlayer onPrepared: " + this.Current_Index);
    }

    @Override
    public boolean onError(IMPlayer player, int what, int extra) {
        Trace.Debug("####AMediaPlayer onError: index=" + Current_Index + ", retryNum=" + retryNum + ",player=" + player);
        if (!userPlaying) {
            return true;
        }
        Trace.Debug("####### onError:retryNum="+retryNum);
        if (retryNum > 3 && this.statusListener != null) {
           // if (!handler.hasMessages(MSG_NEXT_MEDIA)) {
                if (this.statusListener != null) {
                    Trace.Debug("####### onError:this.statusListener="+this.statusListener.size());
                    for (OnStatusChangedListener listener : this.statusListener) {
                        if (listener != null ) {
                            listener.onError(what, extra);
                        }
                    }
                }

//                this.statusListener.onError(what, extra);
               // handler.sendEmptyMessageDelayed(MSG_NEXT_MEDIA, 5000);
          //  }
            retryNum = 0;
        } else {

            if (!handler.hasMessages(MSG_RETRY_MEDIA)) {
                retryNum++;
                Trace.Debug("####### onError:this.statusListener="+this.statusListener.size());
                if(this.statusListener.size()>1){
                    for(int i=0;i<this.statusListener.size()-1;i++){
                        this.statusListener.remove(i);
                    }
                }
                handler.sendEmptyMessageDelayed(MSG_RETRY_MEDIA, 500);
            }
        }
        return true;
    }

    @Override
    public void onCompletion(IMPlayer player) {
        isFromuser = true;
        Trace.Debug("####oncomPlete");
        LTStatus currentStatus = getCurrentStatus();
        if (currentStatus.currentItem != null) {
            MediaOperation mediaOperation = MediaOperation.getInstance();
            currentStatus.progress = 0;
            //mediaOperation.insertLTItem(currentStatus.currentItem);
        }
        if (getPlayList().size() <= 1) {
            seekTo(0);
            startPlay();
            return;
        }

        switch (Play_Mode) {
            case LePlayer.MODE_SINGLE: {
                if (isLoop) {
                    seekTo(0);
                    this.playCurrent();
                    //startPlay();
                } else {

                    this.userPlaying = false;
                    LeRadioSendHelp.getInstance().notifyPlayStatus(userPlaying,Current_Index);
                }
            }
            break;
            case LePlayer.MODE_ORDER: {
                int listSize = this.getPlayList().size();
                if (listSize <= 0) break;
                if (Current_Index < listSize) {
                    this.playNext();
                } else {
                    this.userPlaying = false;
                    LeRadioSendHelp.getInstance().notifyPlayStatus(userPlaying,Current_Index);
                }
            }
            break;
            case LePlayer.MODE_RANDOM: {
                int listSize = this.getPlayList().size();
                if (listSize <= 0) break;
                Current_Index = (int) (Math.random() * listSize);
                this.playCurrent();
            }
            break;
        }
        Trace.Debug("#####AMediaPlayer onCompletion: index=" + Current_Index);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        defVolume = VolumeUtils.getStreamVolume(this, AudioManager.STREAM_MUSIC);
    }

    public long startTime = 0;
    public long endTime = 0;
    public String mAUDIO_ID = "";
    public String mType = "";

    @Override
    public void onDestroy() {
        while (handler.hasMessages(MSG_STATUS_TICK)) {
            handler.removeMessages(MSG_STATUS_TICK);
        }
        endTime = System.currentTimeMillis();
        if (startTime != 0 && mAUDIO_ID != null && mAUDIO_ID.length() > 0) {
            if (SortType.SORT_LOCAL.equals(mType) || SortType.SORT_DOWNLOAD.equals(mType) || SortType.SORT_FAVOR.equals(mType) || SortType.SORT_KUWO_LOCAL.equals(mType) || SortType.SORT_LE_RADIO_LOCAL.equals(mType) || SortType.SORT_LOCAL_ALL.equals(mType)) {
                LetvReportUtils.reportAudioPlayEnd((endTime - startTime) / 1000 + "s", "false", mAUDIO_ID);
            } else {
                LetvReportUtils.reportAudioPlayEnd((endTime - startTime) / 1000 + "s", "true", mAUDIO_ID);
            }

        }

        userPlaying = false;
        LeRadioSendHelp.getInstance().notifyPlayStatus(userPlaying,Current_Index);
        getPlayer().leRelease();
        super.onDestroy();
    }

    private synchronized IMPlayer getPlayer() {
        if (player == null) {
            if (yunPlayer == null) {
                yunPlayer = new LeYunPlayer(this, this);
                yunPlayer.leSetActived(true);
            }
            this.player = yunPlayer;
        }
        return player;
    }

    private void getMediaStatus() {
        long position = 0;
        long duration = 0;
        if (this.getPlayer().getPlaying()) {
            position = this.getPlayer().getLePosition();
            duration = this.getPlayer().getLeDuration();
            duration = duration > 432000000 ? 0 : duration;
            position = position > duration ? duration : position;

            if (this.statusListener != null) {
                for (OnStatusChangedListener listener : this.statusListener) {
                    if (listener != null) {
                        listener.onProgressChanged(position, duration);
                    }
                }
            }
//            this.statusListener.onProgressChanged(position, duration);
        }
        handler.sendEmptyMessageDelayed(MSG_STATUS_TICK, 1000);
    }

    private void playCurrent() {
        VoiceAssistantHelp.getInstance().stopVoiceWhenPlalyMusic();
        if (mIndexChangeListener != null) {
            mIndexChangeListener.onChange(Current_Index);
        }
        handler.removeMessages(PLAY_MUSIC);
//        handler.sendEmptyMessageDelayed(PLAY_MUSIC,0);
        handler.sendEmptyMessage(PLAY_MUSIC);

    }

    private void playCurrentPause() {
        if (mIndexChangeListener != null) {
            mIndexChangeListener.onChange(Current_Index);
        }
        playCurrentMusic();
        startPlay();

    }

    private synchronized void playCurrentMusic() {
        PlayItem item = getPlayList().get(Current_Index);
        if (LePlayer.mPreLtItem != null) {
            if (LePlayer.mPreLtItem.currentItem != null) {
                Trace.Debug("#### insert progress " + LePlayer.mPreLtItem.toString());
                MediaOperation mediaOperation = MediaOperation.getInstance();
                //TODO
                // PlayItem item = getPlayList().get(Current_Index);
                MediaOperation.ProgressStatus status = mediaOperation.getLTITem(item);
                LePlayer.mPreLtItem.duration = status.duration;
                LePlayer.mPreLtItem.progress = status.progress;
                if (LePlayer.mPreLtItem.duration == LePlayer.mPreLtItem.progress) {
                    LePlayer.mPreLtItem.progress = 0;
                }
                item.setProgress(status.progress);
                item.setDuration(status.duration);
                //TODO
                //  changeToLocalSource(item);
            }

            try {
                //TODO
                // PlayItem item = this.getPlayList().get(Current_Index);
                Trace.Debug("#### item " + item);
          /*  if (item!=null&&item.getSource()!=null&&item.getSource().equals(SodrtType.SORT_LE_RADIO)&& TextUtils.isEmpty(item.getUrl())){
                handler.removeMessages(MessageTypeCfg.MSG_GET_MUSIC_URL);
                stopPlay();
                Trace.Debug("#####getUrl");
//                DataUtil.getInstance().getMusicUrlData(handler,item.getId(),item.getPlayType());
                DetailLoader loader = new DetailLoader(this, handler);
                loader.load((item.getId()), item.getPlayType());
            }else {
                setPlayer(item);
            }*/
                changeToLocalSource(item);
                setPlayer(item);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setPlayer(final PlayItem item) {
        userPlaying = true;
        boolean isLoacalSaved = false;
        if (item != null && item.getUrl() != null) {
            isLoacalSaved = Utils.IsLocalExiste(item.getUrl());
        }
        if (item.isXiaMiItem()) {
            if (yunPlayer != null) {
                yunPlayer.leStop();
                yunPlayer.leSetActived(false);
            }

            if (xPlayer == null) {
                xPlayer = new XmPlayer(this, this);
                xPlayer.leSetActived(true);
            }
            Trace.Debug("####XmPlayer");
            this.player = xPlayer;

        } else {

            if (xPlayer != null) {
                xPlayer.leStop();
                xPlayer.leSetActived(false);
            }
            if (yunPlayer == null) {
                yunPlayer = new LeYunPlayer(this, this);
                yunPlayer.leSetActived(true);
            }
            Trace.Debug("####yunPlayer");
            this.player = yunPlayer;
        }

        Trace.Debug("####playCurrent: " + this.player.getClass().getSimpleName() + item.getTitle());
        Trace.Debug("####playCurrent:item= " + item);
        if (item.isLocalItem() && isLoacalSaved) {
            setPlayerActive(item);
        } else {
            if (NetUtils.isConnected(getApplicationContext())) {
                if (NetUtils.isWifi(getApplicationContext())) {
                    Trace.Debug("####isWifi connect");
                    setPlayerActive(item);

                } else {
                    Trace.Debug("####isFromuser= " + isFromuser);
                    if (isFromuser) {
                        if (CacheUtils.getInstance(getApplicationContext()).getBoolean(SettingMusicCfg.USER_MOBILE_NET_PLAY, false)) {
                            Trace.Debug("  #### setPlayer:USER_MOBILE_NET_PLAY is true");
                            setPlayerActive(item);
                        } else {
                            isEffective = false;
                            EcoApplication.LeGlob.getPlayer().stopPlay();
                            for (OnStatusChangedListener listener : this.statusListener) {
                                if (listener != null) {
                                    listener.onProgressChanged(0, 0);
                                }
                            }
                            //如果是同一首歌就不弹对话框
                            /*if (isTheSame(item)) {
                                Trace.Debug("  #### setPlayer:is the same item:" + mLTItem.equals(item) + ",mLTItem=" + mLTItem + ",item=" + item);
                                isEffective = false;
                                setPlayerActive(item);
                            } else */{
                                NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(activity, R.string.mobile_play, R.string.ok, R.string.cancel, true);
                                networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                                    @Override
                                    public void onConfirm(boolean checked) {
                                        if (checked){
                                            CacheUtils.getInstance(getApplicationContext()).putBoolean(SettingMusicCfg.USER_MOBILE_NET_PLAY, true);
                                            activity.sendBroadcast(new Intent(SettingCfg.BROADCAST_2G_PLAY_SWICH));}

                                        setPlayerActive(item);
                                        mLTItem = item;
                                        Trace.Debug("### setPlayerActive");
                                    }

                                    @Override
                                    public void onCancel() {
                                        HomeActivity.isStoped=true;
                                        return;

                                    }
                                });
                                networkConfirmDialog.setCancelable(false);
                                networkConfirmDialog.show();
                            }
                        }
                    } else {
                        Trace.Debug("  #### setPlayer:isFromuser = "+isFromuser);
                        setPlayerActive(item);
                        Trace.Debug("### setPlayerActive");
                    }

                }
            } else {
                NetUtils.showNoNetDialog(activity);
            }
        }
    }


    private static volatile boolean isEffective = true;
    private PlayItem mLTItem;//判断是否是同一首音乐

    private void setPlayerActive(PlayItem item) {
        Trace.Debug("  #### player get name" + player);
        this.player.leSetActived(true);
//      EcoApplication.LeGlob.getqPlayer().pasuseByUser();

        isEffective = true;
        this.player.setLeMedia(item);

        if (mIsPreOrNext && playList.size() == 1) {
            this.player.leSeekTo(0);
            if (this.statusListener != null) {
                for (OnStatusChangedListener listener : this.statusListener) {
                    if (listener != null) {
                        listener.onProgressChanged(0, 0);
                    }
                }
            }

        } else {
            if (isTheSame(item)) {
               // this.player.leSeekTo(item.getProgress());
                mIsPreOrNext = false;
                return;
            } else {
                this.player.leSeekTo(0);
                mIsPreOrNext = true;
                mLTItem = item;
            }

        }
        if (this.statusListener != null && Current_Index != this.lastSongIndex) {
            this.lastSongIndex = Current_Index;
            for (OnStatusChangedListener listener : this.statusListener) {
                if (listener != null) {
                    listener.onSongChanged(null, this.lastSongIndex);
                }
            }
            LetvReportUtils.reportMessagesPlay(item.getCpName());
            endTime = System.currentTimeMillis();
            if (startTime != 0 && mAUDIO_ID != null && mAUDIO_ID.length() > 0) {
                if (SortType.SORT_LOCAL.equals(mType) || SortType.SORT_DOWNLOAD.equals(mType) || SortType.SORT_FAVOR.equals(mType) || SortType.SORT_KUWO_LOCAL.equals(item.getSource()) || SortType.SORT_LOCAL_NEW.equals(item.getSource()) || SortType.SORT_LE_RADIO_LOCAL.equals(item.getSource()) || SortType.SORT_LOCAL_ALL.equals(item.getSource())) {
                    LetvReportUtils.reportAudioPlayEnd((endTime - startTime) / 1000 + "s", "false", mAUDIO_ID);
                } else {
                    LetvReportUtils.reportAudioPlayEnd((endTime - startTime) / 1000 + "s", "true", mAUDIO_ID);
                }

            }

            startTime = endTime;
            mAUDIO_ID = item.getId();
            mType = item.getPlayType();
            Trace.Debug("##### playCurrent:mType " + mType);

//                this.statusListener.onSongChanged(null, this.lastSongIndex);
        }
        EcoApplication.LeGlob.getPlayer().startPlay();
        Trace.Debug("##### playCurrent: " + this.player.getClass().getSimpleName() + item.getId() + item.getTitle());
    }

    public void playNext() {
        mIsPreOrNext = true;
        int listSize = this.getPlayList().size();
        switch (Play_Mode) {
            case LePlayer.MODE_RANDOM:
                if (listSize <= 0) break;
                Current_Index = (int) (Math.random() * listSize);
                break;
            default:
                Current_Index++;
                if (listSize <= 0) {
                    return;
                }
                if (Current_Index < 0) {
                    Current_Index = (Current_Index + listSize * 10) % listSize;
                } else {
                    Current_Index = Current_Index % listSize;
                }
                retryNum = 0;
                Trace.Debug( "playNext: " + this.Current_Index);
                break;
        }

        this.playCurrent();
        while (handler.hasMessages(MSG_STATUS_TICK)) {
            handler.removeMessages(MSG_STATUS_TICK);
        }
        handler.sendEmptyMessage(MSG_STATUS_TICK);
    }

    private boolean mIsPreOrNext;

    public void playPrev() {
        mIsPreOrNext = true;
        int listSize = this.getPlayList().size();
        switch (Play_Mode) {
            case LePlayer.MODE_RANDOM:
                if (listSize <= 0) break;
                Current_Index = (int) (Math.random() * listSize);
                break;
            default:
                Current_Index--;
                if (listSize <= 0) {
                    return;
                }
                if (Current_Index < 0) {
                    Current_Index = (Current_Index + listSize * 10) % listSize;
                } else {
                    Current_Index = Current_Index % listSize;
                }
                retryNum = 0;
                break;
        }

        this.playCurrent();
        while (handler.hasMessages(MSG_STATUS_TICK)) {
            handler.removeMessages(MSG_STATUS_TICK);
        }
        handler.sendEmptyMessage(MSG_STATUS_TICK);
    }

    public void playList(int index) {
        userPlaying = true;
        ArrayList<PlayItem> playlist = this.getPlayList();
        if (playlist!=null && playlist.size()>0 && playlist.size() > index &&  Current_Index != -1) {
            Current_Index = index;
        } else {
            ///TODO
           // Current_Index = this.getPlayList().size();
            return;
        }

        LeRadioSendHelp.getInstance().notifyPlayStatus(userPlaying,Current_Index);
        this.playCurrent();
    }

    public void playListPause(int index) {
        userPlaying = true;
        ArrayList<PlayItem> playlist = this.getPlayList();
        if (playlist!=null && playlist.size()>0 && playlist.size() > index &&  Current_Index != -1) {
            Current_Index = index;
        } else {
            //Current_Index = this.getPlayList().size();
            return;
        }

        LeRadioSendHelp.getInstance().notifyPlayStatus(userPlaying,Current_Index);
        this.playCurrentPause();
    }

    public void seekTo(int mSec) {
        getPlayer().leSeekTo(mSec);
    }
	
	public void setPcmOpen(boolean isOpen) {
        getPlayer().setVolumePcmOpen(isOpen);
    }
	
    public void forwordOrRewind(boolean isForword) {
        getCurrentStatus();
        if (isForword) {
            if ((status.progress + 30000) >= status.duration) {
                playNext();
            } else {
                getPlayer().leSeekTo(status.progress + 30000);
            }
        } else {
            if ((status.progress - 30000) > 0) {
                getPlayer().leSeekTo(status.progress - 30000);
            } else {
                getPlayer().leSeekTo(0);
            }
        }
    }

    public void startPlay() {
//        EcoApplication.LeGlob.getqPlayer().pasuseByUser();
        final PlayItem item;
        ArrayList<PlayItem> playlist = this.getPlayList();
        if (playlist!=null && playlist.size()>0 && playlist.size() > Current_Index &&  Current_Index != -1) {
            item = this.getPlayList().get(Current_Index);
        } else {
            return;
        }
        Trace.Debug("  #### startPlay:isEffective= " + isEffective);
        if (!isEffective) {
            if (NetUtils.isConnected(getApplicationContext())) {
                if (NetUtils.isWifi(getApplicationContext())){
                    setPlayerActive(item);
                Trace.Debug("### setPlayerActive");
                    userPlaying = true;
                }else if (CacheUtils.getInstance(getApplicationContext()).getBoolean(SettingMusicCfg.USER_MOBILE_NET_PLAY, false)){
                    setPlayerActive(item);
                    Trace.Debug("### setPlayerActive");
                    userPlaying = true;
                }

            }
        } else {
            userPlaying = true;
            getPlayer().lePlay();
        }

        LeRadioSendHelp.getInstance().notifyPlayStatus(userPlaying,Current_Index);
    }

    public void pausePlay() {
        userPlaying = false;
        handler.removeMessages(MSG_NEXT_MEDIA);
        handler.removeMessages(MSG_RETRY_MEDIA);
//        getPlayer().leStop();对瓦解调用来说，实际上是暂停
//        handler.removeMessages(PLAY_MUSIC);
        getPlayer().lePause();

        LeRadioSendHelp.getInstance().notifyPlayStatus(userPlaying,Current_Index);
    }

    public synchronized void stopPlay() {
        userPlaying = false;
        handler.removeMessages(MSG_NEXT_MEDIA);
        handler.removeMessages(MSG_RETRY_MEDIA);
//        handler.removeMessages(PLAY_MUSIC);
        getPlayer().leStop();

        LeRadioSendHelp.getInstance().notifyPlayStatus(userPlaying,Current_Index);
    }




    public synchronized ArrayList<PlayItem> getPlayList() {
        if (this.playList == null) {
            this.playList = new ArrayList<PlayItem>();
        }
        return this.playList;
    }

    public synchronized LTStatus getCurrentStatus() {
        status = new LTStatus();
        status.playList = this.playList;
        if (this.playList != null && this.Current_Index >= 0 && this.Current_Index < this.playList.size()) {
            status.currentItem = this.playList.get(this.Current_Index);
        }
        status.currentIndex = this.Current_Index;
        if (this.getPlayer() != null) {
            status.duration = this.getPlayer().getLeDuration();
            status.progress = this.getPlayer().getLePosition();
            status.isPlaying = this.getPlayer().getPlaying();
//            if (status.currentItem != null) {
//                status.currentItem.setDuration(status.duration);
//                status.currentItem.setProgress(status.progress);
//            }
        }
        return status;
    }

    public void setPlayMode(int playMode) {
        this.Play_Mode = playMode;
    }

    public void setPlayLoop(boolean loop) {
        this.isLoop = loop;
    }

    public interface IndexChangeListener {
        void onChange(int index);
    }

    public void setOnStatusChangedListener(ArrayList<OnStatusChangedListener> listener) {
        this.statusListener = listener;
        if (this.statusListener != null) {
            handler.removeMessages(MSG_STATUS_TICK);
            handler.sendEmptyMessageDelayed(MSG_STATUS_TICK, 1000);
        }
    }

    private boolean isTheSame(PlayItem newItem) {
        Trace.Debug("##### newItem: " + newItem + ",mLTItem=" + mLTItem);
        if (newItem != null && mLTItem != null && isValueEffect(newItem.getUrl()) && isValueEffect(mLTItem.getUrl())) {
            if (newItem != null && mLTItem != null && newItem.getUrl().equals(mLTItem.getUrl())) {
                return true;
            } else {
                return false;
            }
        } else if (newItem != null && mLTItem != null && isValueEffect(newItem.getXmid()) && isValueEffect(mLTItem.getXmid())) {
            if (newItem.getXmid().equals(mLTItem.getXmid())) {
                return true;
            } else {
                return false;
            }
        } else {
            if (newItem != null && mLTItem != null && (newItem.getPlayType() != null && newItem.getPlayType().equals(mLTItem.getPlayType())) && (newItem.getId() != null && newItem.getId().equals(mLTItem.getId()))) {
                return true;
            } else {
                return false;
            }
        }

    }

    private boolean isValueEffect(String s) {
        if (s != null && !s.trim().equals("")) {
            return true;
        } else {
            return false;
        }

    }

    private boolean changeToLocalSource(PlayItem item) {
        if (item.isLeradioItem() && MediaOperation.getInstance().isDownLoadMusic(item.getId())) {
            String url = DeviceUtils.getMusicCachePath() + item.getTitle() + ".mp3";
            File file = new File(url);
            if (file.exists()) {
                item.setUrl(url);
                return true;
            }

        }
        return false;
    }

}
