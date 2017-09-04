package com.letv.leauto.ecolink.leplayer.mixMedia;

import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.letv.leauto.ecolink.BuildConfig;
import com.letv.leauto.ecolink.leplayer.model.LTItem;
import com.letv.leauto.ecolink.leplayer.model.PlayItem;
import com.letv.leauto.ecolink.leplayer.xiami.OnlineSong;
import com.letv.leauto.ecolink.leplayer.xiami.RequestSongTask;
import com.letv.leauto.ecolink.utils.Trace;

import com.xiami.audio.MediaPlayer;
import com.xiami.audio.exceptions.InitPlayerThreadException;
import com.xiami.sdk.XiamiSDK;


import java.io.IOException;
import java.util.HashMap;


public class XmPlayer implements IMPlayer {

    private final String XiamiKey = "e267c0799ca0fe10d5f7642767ad310b";
    private final String XiamiSecret = "f425cddfceac83cd52cfe9e37d8e3680";
    private Context ctx;
    private MediaPlayer xmPlayer;
    private IMListener iMListener;
    private OnlineSong xmSong;
    private PlayItem song;
    private boolean callbackOk = false;
    private boolean isPlaying;
    private boolean isActived;
    private int hasErrors;//xiami播放错误时，一定会发生onCompleted，这个值用于记录onCompleted是否是error引起的。
    private int mState = STATE_IDLE;
    private long mProgress = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (xmSong != null && xmPlayer!=null) {
                        try {
                            xmPlayer.setDataSource(xmSong.getListenFile());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        callbackOk = true;
                    }
                    break;
                case 1:
                    if(xmPlayer!=null) {
                        xmPlayer.pause();
                    }
                    break;
                case MSG_MEDIA_PLAYER_PREPARED:
                    if(isActived && iMListener!=null) {
                        iMListener.onPrepared(XmPlayer.this);
                    }
                    break;
                case MSG_MEDIA_PLAYER_COMPLETED:
                    callbackOk = false;
                    if(isActived && iMListener!=null) {
                        iMListener.onCompletion(XmPlayer.this);
                    }
                    break;
                case MSG_MEDIA_PLAYER_ERROR:
                    callbackOk = false;
                    int errCode = msg.arg1;
                    if(isActived && iMListener!=null) {
                        XmPlayer.this.song = null;
                        iMListener.onError(XmPlayer.this, 97, 1);
                    }
                default:
                    break;
            }
        }
    };


    public XmPlayer(Context context, IMListener imListener)
    {
        this.ctx = context;
        this.iMListener = imListener;
        XiamiSDK.init(this.ctx, XiamiKey, XiamiSecret);
        if(BuildConfig.DEBUG) {
            XiamiSDK.enableLog(true);
        }
        this.initPlayer();
    }
    @Override
    public void onDuckBegin() {
        this.xmPlayer.setVolume(0.1f, 0.1f);
    }

    @Override
    public void onDuckEnd() {
        this.xmPlayer.setVolume(1f, 1f);
    }
    private void initPlayer() {
        if(xmPlayer==null) {
            xmPlayer = new MediaPlayer();
            this.xmPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    handler.removeMessages(MSG_MEDIA_PLAYER_ERROR);
                    handler.removeMessages(MSG_MEDIA_PLAYER_COMPLETED);
                    handler.removeMessages(MSG_MEDIA_PLAYER_PREPARED);
                    handler.sendEmptyMessage(MSG_MEDIA_PLAYER_PREPARED);
                    hasErrors = 0;
                    if(isPlaying) {
                        try {
                            xmPlayer.start();
                           // xmPlayer.seekTo(mProgress);
                        } catch (InitPlayerThreadException e) {
                            e.printStackTrace();
                        }
                    }
                    Trace.Debug("XPlayer", "onPrepared:" + xmSong.getSongId() + xmSong.getSongName());
                }
            });
            this.xmPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer player,int i, int i1) {
                    Trace.Debug("XPlayer", "onError:" + xmSong.getSongId() + xmSong.getSongName());
                    Trace.Debug("XPlayer", "onError:i=" +i+",i1="+i1);
                    hasErrors ++;
                    handler.removeMessages(MSG_MEDIA_PLAYER_ERROR);
                    Message message = Message.obtain();
                    message.what = MSG_MEDIA_PLAYER_ERROR;
                    message.arg1 = i;
                    handler.sendMessageDelayed(message, 500);
                    return true;
                }
            });
            this.xmPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    Trace.Debug("XPlayer","#####XPlayer onCompletion:" + xmSong.getSongId() + xmSong.getSongName());
                    if(hasErrors==0) {
                        handler.removeMessages(MSG_MEDIA_PLAYER_COMPLETED);
                        handler.sendEmptyMessageDelayed(MSG_MEDIA_PLAYER_COMPLETED, 500);
                    }
                }
            });
        }
    }

    @Override
    public void setLeMedia(PlayItem song) {
        try {
            this.initPlayer();
            xmPlayer.reset();
            if(!callbackOk || !isSameSong(this.song, song)) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("song_id", song.getXmid());
                params.put("quality", "l");
                new FindSongByIdTask(ctx).execute(params);
                callbackOk = false;
            } else {
                xmPlayer.start();
            }
            this.hasErrors = 0;
            this.isPlaying = true;
            this.song = song;
        }catch (Exception e){
            Trace.Error("XPlayer", e.getLocalizedMessage());
        }
    }

    @Override
    public void lePlay() {
        isPlaying = true;
        if(this.xmPlayer!=null) {
            try {
                xmPlayer.start();
            } catch (InitPlayerThreadException e) {
                e.printStackTrace();
                handler.removeMessages(MSG_MEDIA_PLAYER_ERROR);
                handler.sendEmptyMessageDelayed(MSG_MEDIA_PLAYER_ERROR, 500);
            }
        }
    }

    @Override
    public void lePause() {
        isPlaying = false;
        if(this.xmPlayer!=null) {
            xmPlayer.pause();
        }
    }

    @Override
    public void leStop() {
        isPlaying = false;
        if(this.xmPlayer!=null) {
            xmPlayer.release();
            xmPlayer = null;
            this.song = null;
        }
    }
    /**
     * 设置pcm输出，但虾米播放器不支持，所以不实现
     * @param isOpen 当为true时，要打开pcm输出，声音从pcm输出，为false则关闭pcm输出，声音就从手机输出
     */
    @Override
    public void setVolumePcmOpen(boolean isOpen) {

    }

    @Override
    public void leSeekTo(long mSec) {
        if(this.xmPlayer!=null) {
            long sec = (long)mSec ;
            try {
                xmPlayer.seekTo(sec);
            } catch (InitPlayerThreadException e) {
                e.printStackTrace();
            }
        }
        mProgress = mSec;
    }

    @Override
    public void leSetActived(boolean isActived) {
        this.isActived = isActived;
    }

    @Override
    public void leRelease() {
        if(this.xmPlayer!=null) {
            xmPlayer.release();
        }
    }

    @Override
    public long getLePosition() {
        if(this.xmPlayer!=null) {
            return xmPlayer.getCurrentPositionMS();
        }
        return 0;
    }

    @Override
    public long getLeDuration() {
        if(this.xmPlayer!=null) {
            return xmPlayer.getDurationMS();
        }
        return 0;
    }

    @Override
    public boolean getPlaying()
    {
        return this.isPlaying;
    }

    private boolean isSameSong(PlayItem song1, PlayItem song2) {
        if(song1==null && song2==null) {
            return true;
        }
        if(song1!=null && song2!=null) {
            if(song1.getXmid()!=null && song1.getXmid().equals(song2.getXmid())) {
                return callbackOk;
            }
        }
        return false;
    }

    class FindSongByIdTask extends RequestSongTask {

        public FindSongByIdTask(Context context) {
            super(context);
        }

        @Override
        public void postInBackground(OnlineSong onlineSong) {
        }

        @Override
        protected void onPostExecute(OnlineSong onlineSong) {
            super.onPostExecute(onlineSong);
            if (onlineSong != null) {
                xmSong = onlineSong;
                handler.sendEmptyMessage(0);

            } else {
                handler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    public int getState() {
        return mState;
    }
}
