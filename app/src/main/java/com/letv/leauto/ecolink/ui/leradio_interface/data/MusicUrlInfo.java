package com.letv.leauto.ecolink.ui.leradio_interface.data;

import com.letv.mobile.http.model.LetvHttpBaseModel;

public class MusicUrlInfo extends LetvHttpBaseModel {

    String id;
    String name;
    int duration;
    String playUrl;
    int pageNum;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    @Override
    public String toString() {
        return "MusicDetailModel{" +
                ", id='" + id + '\'' +
                ", pageNum=" + pageNum +
                ", name='" + name + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", duration=" + duration +
                '}';
    }
}
