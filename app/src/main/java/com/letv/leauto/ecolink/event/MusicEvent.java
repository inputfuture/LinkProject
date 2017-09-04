package com.letv.leauto.ecolink.event;

import com.letv.voicehelp.model.MediaDetail;

import java.util.ArrayList;

/**
 * Created by lixinlei on 16/11/29.
 */

public class MusicEvent {
    public static final int OPEN_MUSIC = 884;
    public static final int EXIT_MUSIC = 401;
    public static final int PLAY_LIST_MUSIC = 374;
    public static final int PLAY_MUSIC_STR = 375;
    public static final int PLAY_NEXT=376;
    public static final int PLAY_PRE=377;
    public static final int PAUSE=378;
    public static final int START=379;
    public static final int PLAY_MODE = 380;
    public static final int PLAY_LOCAL = 381;


    private int type;
    private String mediaJson;
    private ArrayList<MediaDetail> mediaDetails;
    private int mode=1;

    public MusicEvent(int type) {
        this.type = type;
    }
    public MusicEvent(int type,int mode) {
        this.type = type;
        this.mode=mode;
    }

    public MusicEvent(int type, ArrayList<MediaDetail> mediaDetails) {
        this.type = type;
        this.mediaDetails = mediaDetails;
    }

    public int getType() {
        return type;
    }

    public ArrayList<MediaDetail> getMediaDetails() {
        return mediaDetails;
    }

    public MusicEvent(int type, String mediaJson) {
        this.type = type;
        this.mediaJson = mediaJson;
    }
    public String getMediaJson() {
        return mediaJson;
    }

    public int  getPlayMode() {
        return mode;

    }
}
