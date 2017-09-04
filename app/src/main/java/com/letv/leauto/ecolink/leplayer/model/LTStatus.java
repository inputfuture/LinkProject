package com.letv.leauto.ecolink.leplayer.model;

import java.util.ArrayList;

/**
 * Created by zhaochao on 2015/7/21.
 */
public class LTStatus {
    public ArrayList<PlayItem> playList;
    public PlayItem currentItem;
    public String url;
    public int currentIndex;
    public long progress;
    public long duration;
    public boolean isPlaying=false;

    @Override
    public String toString() {
        return "LTStatus{" +
                "currentItem=" + currentItem +
                ", url='" + url + '\'' +
                ", currentIndex=" + currentIndex +
                ", progress=" + progress +
                ", duration=" + duration +
                ", isPlaying=" + isPlaying +
                '}';
    }
}
