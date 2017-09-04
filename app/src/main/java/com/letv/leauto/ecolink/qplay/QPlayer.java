package com.letv.leauto.ecolink.qplay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.IBinder;

import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.utils.Trace;
import com.tencent.qplayauto.device.QPlayAutoArguments;
import com.tencent.qplayauto.device.QPlayAutoSongListItem;

import java.util.List;

/**
 * Created by why on 2017/2/27.
 */

public class QPlayer implements ServiceConnection ,PlayPCM.IndexChangeListener {
    private boolean isBinded;
    private PlayPCM service;
    Context context;
    private List<QPlayAutoSongListItem> mPlayList;
    private int mIndex;
    private MusicStateListener musicStateListener;
    private AudioManager mAudioManager;


    public QPlayer(Context context) {
        this.context=context;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((PlayPCM.LocalBinder) binder).getService();
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        service.setmIndexChangeListener(this);
        isBinded = true;

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
        isBinded = false;

    }

    public synchronized void openServiceIfNeed() {

        if (this.context != null) {
            Intent service = new Intent(this.context.getApplicationContext(), PlayPCM.class);
            this.context.startService(service);
            if (this.isBinded) {
                this.context.unbindService(this);
                this.isBinded = false;
            }
            Trace.Debug("##### openServiceIfNeed");
            this.context.bindService(service, this, Context.BIND_AUTO_CREATE);
        }
    }

    public void play(){
        requestAudioFocus();
        service.startPlay();
        if (musicStateListener!=null){
            musicStateListener.musicStart();
        }
    }
    public void  pause(){
        service.pausePlay();
//        mAudioManager.abandonAudioFocus(audioFocusChangeListener);
        if (musicStateListener!=null){
            musicStateListener.musicStop();
        }

    }
    public void pasuseByUser(){

        service.stopByUser=true;
        pause();
        Trace.Debug("******stop");
    }
    public void  setPlayList(List<QPlayAutoSongListItem> playList){
        mPlayList=playList;
        service.getPlayList().clear();
        service.getPlayList().addAll(playList);
    }


    public void playIndex(int index){
        mIndex=index;
        requestAudioFocus();
        this.service.playList(index);

        if (musicStateListener!=null){
            musicStateListener.musicStart();
        }

    }

    public void playPre(){
        requestAudioFocus();
        service.playPre();
        if (musicStateListener!=null){
            musicStateListener.musicStart();
        }
    }



    public void playNext(){
        service.playNext();
        requestAudioFocus();
        if (musicStateListener!=null){
            musicStateListener.musicStart();
        }
    }
    public void releasePlay(){
        service.releasePlay();
    }

    public void stop(){
        service.stopPlay();
    }


    public void setHandler(Handler handler){
        service.setHandler(handler);
    }


//    public Handler getHandler(){
////        return service.PlayHandler;
//
//    }


    public int getPlayState(){
        return  service.getPlayState();
    }

    public int getTotalTimes(){
        return  service.getTotalTimes();}

    public int getPlayPosition(){
        return service.getPlayPosition();
    }


    public int startConnect(){
        return service.connect();
    }

    public boolean isPlay() {
        if (getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
            return true;
        }else{
            return false;
        }
//        return service.IsPlay();
    }

    public QPlayAutoArguments.ResponseMediaInfos getcurrentMediaInfos(){
        return service.getCurrentPlaySongInfo();
    }

    @Override
    public void onChange(int index) {
        mIndex=index;
        if (musicStateListener!=null){
            musicStateListener.musicIndex(index);
        }

    }

    public List<QPlayAutoSongListItem> getPlayList() {
        return mPlayList;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setPlayMode(int mode) {
        service.setPlayMode(mode);
    }

    public interface MusicStateListener {
        void musicStart();

        void musicStop();

        void musicIndex(int index);
    }

    public void setMusicStateListener(MusicStateListener musicStateListener) {
        this.musicStateListener = musicStateListener;
    }
    public boolean requestAudioFocus() {
//        int result = mAudioManager.requestAudioFocus(audioFocusChangeListener,
//                AudioManager.STREAM_MUSIC,
//                AudioManager.AUDIOFOCUS_GAIN);
//        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//
//            System.out.println("liweiwei....requestAudioFocus = ");
//            // lePlayer.stopPlay();
//            return false;
//        } else {
//            return true;
//        }
        return false;
    }
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                pause();
                Trace.Debug("******stop");

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                pause();
                Trace.Debug("******stop");

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (!BaseActivity.isVoice && !PlayPCM.stopByUser) {
                    play();
                }


            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                requestAudioFocus();




            }

        }

    };
}
