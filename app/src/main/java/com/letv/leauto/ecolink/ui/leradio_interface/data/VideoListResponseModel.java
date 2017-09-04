package com.letv.leauto.ecolink.ui.leradio_interface.data;

import com.letv.mobile.http.model.LetvHttpBaseModel;

import java.util.Arrays;

/**
 * Created by Han on 16/7/16.
 */
public class VideoListResponseModel extends LetvHttpBaseModel {
    private int dataType;
    private int type;
    private String albumId;
    private int categoryId;
    private String name;
    private int positive;
    private int albumTypeId;
    private int end;
    private String img;

    private int episodes;
    private int varietyShow;
    private int seriesStyle;
    private int seriesShow;

    private int mPage;
    private int mPageSize;

    public void setPage(int page) {
        mPage = page;
    }

    public void setPageSize(int size) {
        mPageSize = size;
    }

    public int getVarietyShow() {
        return varietyShow;
    }

    public void setVarietyShow(int varietyShow) {
        this.varietyShow = varietyShow;
    }

    public int getSeriesStyle() {
        return seriesStyle;
    }

    public void setSeriesStyle(int seriesStyle) {
        this.seriesStyle = seriesStyle;
    }

    public int getSeriesShow() {
        return seriesShow;
    }

    public void setSeriesShow(int seriesShow) {
        this.seriesShow = seriesShow;
    }

    private String nowEpisode;
    private VideoListModel[] albums;
    private VideoListModel[] nearlyVideos;
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPositive() {
        return positive;
    }

    public void setPositive(int positive) {
        this.positive = positive;
    }

    public int getAlbumTypeId() {
        return albumTypeId;
    }

    public void setAlbumTypeId(int albumTypeId) {
        this.albumTypeId = albumTypeId;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public String getNowEpisode() {
        return nowEpisode;
    }

    public void setNowEpisode(String nowEpisode) {
        this.nowEpisode = nowEpisode;
    }

    @Override
    public String toString() {
        return "VideoListResponseModel{" +
                "dataType=" + dataType +
                ", type=" + type +
                ", albumId='" + albumId + '\'' +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", positive=" + positive +
                ", albumTypeId=" + albumTypeId +
                ", end=" + end +
                ", episodes=" + episodes +
                ", nowEpisode='" + nowEpisode + '\'' +
                ", positiveSeries=" + Arrays.toString(albums) +
                ", nearlyVideos=" + Arrays.toString(nearlyVideos) +
                '}';
    }

    public VideoListModel[] getAlbums() {
        return albums;
    }

    public void setAlbums(VideoListModel[] albums) {
        this.albums = albums;
    }

    public VideoListModel[] getNearlyVideos() {
        return nearlyVideos;
    }

    public void setNearlyVideos(VideoListModel[] nearlyVideos) {
        this.nearlyVideos = nearlyVideos;
    }
}
