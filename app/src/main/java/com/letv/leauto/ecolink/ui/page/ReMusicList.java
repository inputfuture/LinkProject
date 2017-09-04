package com.letv.leauto.ecolink.ui.page;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ReMusicList implements Serializable {
    long playlistid;
    private String name;
    private String pic;
    private int count;

    public long getPlaylistid() {
        return playlistid;
    }

    public void setPlaylistid(long playlistid) {
        this.playlistid = playlistid;
    }

    @Override
    public String toString() {
        return "ReMusicList{" +
                "playlistid=" + playlistid +
                ", name='" + name + '\'' +
                ", pic='" + pic + '\'' +
                ", count=" + count +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
