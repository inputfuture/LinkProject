package com.letv.leauto.ecolink.ui.page;

import java.io.Serializable;

/**
 * Created by qu on 2016/8/1.
 */
public class ReWtiteMusic implements Serializable {
    private long id;
    private String name;
    private String album;
    private String artist;
    private String playUrl;

    @Override
    public String toString() {
        return "ReWtiteMusic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", during=" + during +
                '}';
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    private String imgUrl;
    private int during;

    public ReWtiteMusic() {

    }

    public int getDuring() {
        return this.during;
    }

    public void setDuring(int var1) {
        this.during = var1;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long var1) {
        this.id = var1;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String var1) {
        this.name = var1;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String var1) {
        this.album = var1;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String var1) {
        this.artist = var1;
    }

}
