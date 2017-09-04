package com.letv.leauto.ecolink.leplayer.model;

/**
 * Created by zhaochao on 2015/6/9.
 */
public interface ILePlayer {
//    public void setStatusListener(OnStatusChangedListener listener);
    public void setSong(PlayItem song);
    public void resumeSong();
    public void pauseSong();
    public void seekTo(int mSec);
    public void setVolume(float leftVolume, float rightVolume);
    public String getDataSource();
    public int getDuration();
    public int getPosition();
    public boolean isPlaying();

    public void setMediaListener(IMediaListener listener);
    public void setCallbackEnable(boolean enable);
}
