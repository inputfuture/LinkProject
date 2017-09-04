package com.letv.leauto.ecolink.leplayer.mixMedia;


import com.letv.leauto.ecolink.leplayer.model.LTItem;
import com.letv.leauto.ecolink.leplayer.model.PlayItem;

/**
 * Created by zhaochao on 2015/10/29.
 */
public interface IMPlayer {
    int MSG_MEDIA_PLAYER_PREPARED = 0x97;
    int MSG_MEDIA_PLAYER_ERROR = 0x99;
    int MSG_MEDIA_PLAYER_COMPLETED = 0x98;

    int STATE_IDLE = 0;
    int STATE_PREPARED = 1;
    int STATE_START = 2;
    int STATE_STOP = 3;

    void setVolumePcmOpen(boolean isOpen);
    void onDuckBegin();
    void onDuckEnd();
    void setLeMedia(PlayItem item);
    void lePlay();
    void lePause();
    void leStop();
    void leSeekTo(long milsec);
    void leSetActived(boolean isActived);
    void leRelease();
    long getLeDuration();
    long getLePosition();
    boolean getPlaying();
    int getState();
}
