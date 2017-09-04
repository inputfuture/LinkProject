package com.letv.leauto.ecolink.leplayer.mixMedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.http.leradio.DetailLoader;
import com.letv.leauto.ecolink.http.leradio.LiveDetailLoader;
import com.letv.leauto.ecolink.leplayer.common.LePlayerCommon;
import com.letv.leauto.ecolink.leplayer.model.PlayItem;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicDetailModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicListItem;
import com.letv.leauto.ecolink.utils.Constants;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.leauto.ecolink.utils.Utils;
import com.letvcloud.cmf.MediaPlayer;
import com.letvcloud.cmf.MediaSource;

import java.io.IOException;

/**
 * File description
 * Created by @author${shimeng}  on @date14/5/22.
 */

public class LeYunPlayer implements IMPlayer {
    private static final String TAG = "LeYunPlayer";
    private MediaPlayer aPlayer;
    private MediaSource mMediaSource;
    private boolean isPlaying;
    private boolean isActived;
    private boolean isPrepared;
    private IMListener iMListener;
    private PlayItem song;
    Context mContext;
    private int mState = STATE_IDLE;
    private long mProgress = 0;
    private String videoType = "1";
    private int tagNum;
    private int mNetworkType;
    private static final int MSG_EXIT_APP = 0;
    private static final int MSG_CDE_READY = 1;
    private static final int MSG_NETWORK_READY = 2;

    private NetworkBroadcastReceive mNetworkReceive;
    private CmfBroadcastReceive mCmfReceive;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_EXIT_APP:
                    // 停止cde
                    if (LeYunPlayer.this.aPlayer != null) {
                        LeYunPlayer.this.aPlayer.release();
                    }
                    break;

                case MSG_CDE_READY:
                    if (LeYunPlayer.this.aPlayer != null) {
                        return;
                    }
                    lePlay();
                    break;

                case MSG_NETWORK_READY:
                    if (LeYunPlayer.this.aPlayer != null) {
                        return;
                    }
                    lePlay();
                    break;

                case IMPlayer.MSG_MEDIA_PLAYER_PREPARED:
                    Trace.Debug(TAG, "recieve MSG_MEDIA_PLAYER_PREPARED message:");
                    if (isActived && iMListener != null) {
                        iMListener.onPrepared(LeYunPlayer.this);
                    }
                    break;
                case IMPlayer.MSG_MEDIA_PLAYER_ERROR:
                    Trace.Debug(TAG, "recieve MSG_MEDIA_PLAYER_ERROR message:");
                    int errCode = msg.arg1;
                    if (isActived && iMListener != null) {
                        iMListener.onError(LeYunPlayer.this, 97, 1);
                    }
                    break;
                case IMPlayer.MSG_MEDIA_PLAYER_COMPLETED:
                    Trace.Debug(TAG, "recieve MSG_MEDIA_PLAYER_COMPLETED message:");
                    mState = STATE_STOP;
                    if (isActived && iMListener != null) {
                        iMListener.onCompletion(LeYunPlayer.this);
                    }
                    break;
                case MessageTypeCfg.MSG_GET_MUSIC_URL:
                    MusicDetailModel detail = (MusicDetailModel) msg.obj;
                    Trace.Debug(TAG, "url:" + detail.playUrl);
                    if (detail.playUrl != null) {
                        //shimeng add for bug1084,20160324
                        if (null != song) {
                            aPlayer.reset();
                            if (song.isLiveItem()) {
                                mMediaSource.setType(MediaSource.TYPE_LIVE).setEncrypt(
                                        true).setTransfer(
                                        true).setOverLoadProtect(false);
                            } else {
                                if (detail.playType == Constants.PLAY_TYPE_CDE) {
                                    mMediaSource.setType(MediaSource.TYPE_VOD).setEncrypt(
                                            false).setTransfer(false).setOverLoadProtect(true);
                                    if (song.getPlayType() == null || (song.getPlayType() != null && !song.getPlayType().equals(videoType))) {
                                        mMediaSource.setOther("mediatype=mp4");
                                    }
                                } else {
                                    mMediaSource.setType(MediaSource.TYPE_VOD).setEncrypt(
                                            false).setTransfer(false).setOverLoadProtect(true);
                                }
                            }
                            //url = "http://c204.duotin.com/M02/06/52/wKgB7FUrekSAfmmmADOkioxl37Y650.mp3";
                            mMediaSource.setSource(detail.playUrl);
                            aPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                            aPlayer.setParameter(MediaPlayer.PARAMETER_LECPLAYER_USE_AUDIO_TRACK, 1, 0);
                            aPlayer.setCachePreSize(1500);
                            aPlayer.setCacheWatermark(3000, 500);
                            aPlayer.setCacheMaxSize(3000);
                            aPlayer.setParameter(400, 512, 0);
                            try {
                                aPlayer.setDataSource(mContext, mMediaSource);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Trace.Error(TAG, "e:" + e.getStackTrace() );
                            }
                            Trace.Debug(TAG, "song url:" + detail.playUrl );
                            song.setUrl(detail.playUrl);
                            LePlayerPcm.lePlayer_ready(aPlayer, mContext);
                            aPlayer.prepareAsync();
                            isPlaying = true;
                        } else {
                            Trace.Debug(TAG, "song is null");
                        }
                    } else {
                        Trace.Debug(TAG, "the url is null and the id is :" + song);
                    }
                    break;
                case MessageTypeCfg.MSG_NODATA_GET:
                    if (isActived && iMListener != null) {
                        iMListener.onError(LeYunPlayer.this, 97, 1);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public LeYunPlayer(Context context, IMListener imListener) {
        mContext = context;
        this.iMListener = imListener;
        //this.initPlayer();
        this.registerReceiver();
    }

    public void onDuckBegin() {
        Trace.Debug("onDuckBegin");
        if (aPlayer != null) {
            aPlayer.setVolume(0.5f, 0.5f);
        }
    }

    public void onDuckEnd() {
        Trace.Debug("onDuckEnd");
        if (aPlayer != null) {
            aPlayer.setVolume(1, 1);
        }
    }
    private void registerReceiver() {
        //add for cde connection and disconnection ,20160408,begin
        this.mCmfReceive = new LeYunPlayer.CmfBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LePlayerCommon.CMF_SERVER_CONNECTION_ACTION);
        intentFilter.addAction(LePlayerCommon.CMF_SERVER_DISCONNECTION_ACTION);
        mContext.registerReceiver(this.mCmfReceive, intentFilter);
        //add for cde connection and disconnection ,20160408,end
        this.mNetworkReceive = new LeYunPlayer.NetworkBroadcastReceive();
        mContext.registerReceiver(this.mNetworkReceive,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void setLeMedia(PlayItem song) {
        this.mMediaSource = new MediaSource();
        Trace.Debug(TAG,"#####setLeMedia");
        try {
            this.initPlayer();
            if (isSameSong(this.song, song)) {
                aPlayer.start();
                Trace.Debug(TAG, "isSameSong:" + song.getUrl());

            } else {
                //How to process case:
                //  1. If we start, but hasn't get setOnPreparedListener.
                //
                if (aPlayer != null
                        && aPlayer.getPlayerState() != MediaPlayer.PLAYER_STATE_STOPED) {
                    aPlayer.stop();
                    aPlayer.reset();
                    aPlayer.seekTo(0);
                    Trace.Debug(TAG, "stop&reset---");
                } else {
                    return;
                }
                this.isPlaying = false;
                isPrepared = false;
                mState = STATE_PREPARED;
                handler.removeMessages(MessageTypeCfg.MSG_GET_MUSIC_URL);

                this.song = song;
                if (song.isLocalItem() && Utils.IsLocalExiste(song.getUrl())) {
                    Trace.Debug(TAG, "LocalItem---");
                    Message message = Message.obtain();
                    message.what = MessageTypeCfg.MSG_GET_MUSIC_URL;
                    MusicDetailModel model = new MusicDetailModel();
                    model.playUrl = song.getUrl();
                    model.playType = Constants.PLAY_TYPE_EXT;
                    message.obj = model;
                    handler.sendMessage(message);
                } else if (song.isLiveItem()) {
                    Trace.Debug(TAG, "LiveItem---");
                    tagNum++;
                    LiveDetailLoader loader = new LiveDetailLoader(mContext, handler);
                    loader.load(tagNum, song.getPlayType(), song);
                } else {
                    if (song != null && (song.getPlayType() == null
                            || song.getPlayType().isEmpty())) {
                        song.setPlayType("0");
                    }
                    if(song != null && !(song.getUrl() == null
                            || song.getUrl().isEmpty())){
                        Message message = Message.obtain();
                        message.what = MessageTypeCfg.MSG_GET_MUSIC_URL;
                        MusicDetailModel model = new MusicDetailModel();
                        model.playUrl = song.getUrl();
                        model.playType = Constants.PLAY_TYPE_CDE;
                        message.obj = model;
                        handler.sendMessage(message);
                    }else {
                        DetailLoader loader = new DetailLoader(mContext, handler);
                        loader.load(song.getId(), song.getPlayType());
                    }
                }
            }
            ///this.isPlaying = true;       //can not set as true, if song is null.

        } catch (Exception e) {
            Trace.Error(TAG , e.getLocalizedMessage());
        }
    }

    private int getPlayerState(MediaPlayer mediaPlayer) {
        int state = mediaPlayer.getPlayerState();
        return state;
    }

    @Override
    public void lePlay() {
        isPlaying = true;
        if (this.aPlayer != null && !aPlayer.isPlaying()) {
            if (getPlayerState(aPlayer) != aPlayer.PLAYER_STATE_PLAYING) {
                Trace.Debug(TAG, "lePlay--start");
                aPlayer.start();
            }
        }
    }

    @Override
    public void lePause() {
        isPlaying = false;
        if (this.aPlayer != null && aPlayer.isPlaying()) {
            Trace.Debug(TAG, "lePlay--pause");
            aPlayer.pause();
        }
    }

    @Override
    public void leStop() {
        isPlaying = false;
        if (this.aPlayer != null) {
            if (getPlayerState(aPlayer) != aPlayer.PLAYER_STATE_STOPED) {
                aPlayer.stop();
            }
            aPlayer.reset();
            Trace.Debug(TAG, "lePlay--stop&&reset");
            aPlayer.release();
            aPlayer = null;
            this.song = null;
        }
    }

    /**
     * 设置pcm输出
     *
     * @param isOpen 当为true时，要打开pcm输出，声音从pcm输出，为false则关闭pcm输出，声音就从手机输出
     */
    @Override
    public void setVolumePcmOpen(boolean isOpen) {
        if (this.aPlayer != null) {
            if (isOpen) {
                aPlayer.setVolume(0, 0);
            } else {
                aPlayer.setVolume(1, 1);
            }
        }
    }

    @Override
    public void leSeekTo(long mSec) {
        if (this.aPlayer != null) {
            aPlayer.seekTo((int) mSec);
        }
        mProgress = mSec;
    }

    @Override
    public void leSetActived(boolean isActived) {
        this.isActived = isActived;
    }

    @Override
    public void leRelease() {
        if (this.aPlayer != null) {
            aPlayer.release();
        }
    }

    @Override
    public long getLePosition() {
        if (this.aPlayer != null) {
            return aPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getLeDuration() {
        if (this.aPlayer != null) {
            return aPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public boolean getPlaying() {
        return this.isPlaying;
    }

    private boolean isSameSong(PlayItem song1, PlayItem song2) {
        if (song1 == null && song2 == null) {
            return true;
        }
        if (song1 != null && song2 != null) {
            if (song1.getId().equals(song2.getId()) && song1.getPlayType().equals(
                    song2.getPlayType())) {
                return isPrepared;
            }
        }
        return false;
    }


    private void initPlayer() {
        /*不是乐视资源的网络 蜻蜓，考拉，除了乐视和虾米之外的网络播放的*/
        if (this.aPlayer == null) {
            this.aPlayer = MediaPlayer.create(
                    MediaPlayer.DECODER_TYPE_LEC_AUTO);  //(MediaPlayer.DECODER_TYPE_SYS_HARD);

            if (this.aPlayer == null) {
                return;
            }
            mState = STATE_PREPARED;
            this.aPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                   @Override
                                                   public void onPrepared(MediaPlayer mp) {
                                                       isPrepared = true;
                                                       handler.removeMessages(IMPlayer
                                                               .MSG_MEDIA_PLAYER_ERROR);
                                                       handler.removeMessages(IMPlayer
                                                               .MSG_MEDIA_PLAYER_COMPLETED);
                                                       handler.removeMessages(IMPlayer
                                                               .MSG_MEDIA_PLAYER_PREPARED);
                                                       handler.sendEmptyMessage(IMPlayer
                                                               .MSG_MEDIA_PLAYER_PREPARED);
                                                       if (isPlaying) {
                                                           Trace.Error(TAG, "start");
                                                           mp.start();
                                                           mp.seekTo((int) mProgress);
                                                           mState = STATE_START;
                                                       }
                                                       // int precache = aPlayer
                                                       // .getCacheDuration();
                                                       Trace.Error(TAG,
                                                               "onPrepared:precache="
                                                                           /*+precache*/ + "," +
                                                                       (song == null ? "None"
                                                                               : song.toString()));
                                                   }
                                               }

            );
            this.aPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, String extra) {
                    isPrepared = false;
                    mState = STATE_STOP;
                    Trace.Debug(TAG, "onError:what=" + what + ",extra=" + extra);
                    Trace.Debug(TAG, "onError:" + (song == null ? "None" : song.toString()));
                    handler.removeMessages(MSG_MEDIA_PLAYER_ERROR);
                    Message message = Message.obtain();
                    message.what = MSG_MEDIA_PLAYER_ERROR;
                    message.arg1 = what;
                    handler.sendMessage(message);
                    return true;
                }
            });

            this.aPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()

                                                 {
                                                     @Override
                                                     public void onCompletion(MediaPlayer mp) {
                                                         Trace.Error(TAG, " onCompletion:"
                                                                 + (song == null ? "None"
                                                                 : song.toString()));
                                                         handler.removeMessages(IMPlayer
                                                                 .MSG_MEDIA_PLAYER_COMPLETED);
                                                         handler.sendEmptyMessageDelayed(IMPlayer
                                                                 .MSG_MEDIA_PLAYER_COMPLETED, 500);

                                                     }
                                                 }

            );
        }
    }

    @Override
    public int getState() {
        return mState;
    }

    //add for cde connection and disconnection ,20160408,begin
    private class CmfBroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LePlayerCommon.CMF_SERVER_CONNECTION_ACTION)) {
                boolean cdeReady = intent.getBooleanExtra(LePlayerCommon.EXTRA_CMF_CDE_READY,
                        false);
                boolean linkShellReady = intent.getBooleanExtra(
                        LePlayerCommon.EXTRA_CMF_LINKSHELL_READY, false);
                Trace.Debug(
                        TAG , "cdeReady:" + cdeReady + ",linkShellReady=" + linkShellReady);
                if (cdeReady) {
                    Trace.Debug(TAG, "cdeReady:" + cdeReady);
                    lePlay();
                }
            } else if (intent.getAction().equals(LePlayerCommon.CMF_SERVER_DISCONNECTION_ACTION)) {
                int erroCode = intent.getIntExtra(LePlayerCommon.EXTRA_CMF_ERROR_CODE, 0);
                Trace.Debug(TAG, "erroCode:" + erroCode);
                leStop();
            }
        }
    }

    private class NetworkBroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                LeYunPlayer.this.handleNetwork();
            }
        }
    }

    private boolean mIsCdeReady = false;
    private boolean mIsSurfaceReady = false;

    private void handleNetwork() {
        int networkType = NetUtils.getNetworkType(mContext);
        if (this.mNetworkType == networkType) {
            return;
        }

        this.mNetworkType = networkType;
        if (this.mNetworkType == 0) {
            this.stop();
            Trace.Debug(TAG, "网络连接失败,停止播放.");
            return;
        }

        if (!this.mIsSurfaceReady || !this.mIsCdeReady) {
            return;
        }
        this.handler.sendEmptyMessage(MSG_NETWORK_READY);
        Trace.Debug(TAG, "网络已连接上,即将播放.");
    }

    private void unregisterReceiver() {
        if (this.mCmfReceive != null && this.mCmfReceive.isInitialStickyBroadcast()) {
            mContext.unregisterReceiver(this.mCmfReceive);
            this.mCmfReceive = null;
        }
        if (this.mNetworkReceive != null && this.mNetworkReceive.isInitialStickyBroadcast()) {
            mContext.unregisterReceiver(this.mNetworkReceive);
            this.mNetworkReceive = null;
        }
    }

    public void stop() {
        Trace.Debug(TAG, "PlayVideoActivity onDestroy()");
        leStop();
        this.stopCdeAndExitApp();
        this.unregisterReceiver();
    }

    private void stopCdeAndExitApp() {
        new Thread() {
            @Override
            public void run() {
                LeYunPlayer.this.handler.sendEmptyMessage(MSG_EXIT_APP);
            }
        }.start();
    }
}
