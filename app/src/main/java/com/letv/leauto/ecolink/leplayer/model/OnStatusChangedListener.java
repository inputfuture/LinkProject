package com.letv.leauto.ecolink.leplayer.model;

/**
 * Created by zhaochao on 2015/6/9.
 */
public interface OnStatusChangedListener {
//    public void onConnected(ArrayList<LTItem> playList, int mCurrentIndex, int progress, int duration, boolean isPlaying);
    public void onProgressChanged(long progress, long duration);
    public void onVolumeChanged(float leftVolume, float rightVolume);
    public void onSongChanged(String url, int position);
    public void onError(int errCode, int extra);
    public void onPrepared();
}
