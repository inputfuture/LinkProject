package com.letv.leauto.ecolink.ui.leradio_interface.data;

/**
 * Created by Han on 16/7/16.
 */
//就是音乐详情的接口
public class VideoListModel {

    private int categoryId;
    private String albumId;
    private String mediaId;
    private String mediaType;
    private String singer;
    private String episode;
    private String img;
    private int page;
    private String name;
    private String subName;
    private int duration;
    private String xiamiId;
    private String sourceName;

    private int download;

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAuthor(String author) {
        this.singer = author;
    }

    public String getAuthor() {
        return this.singer;
    }

    public void setDetailType(String type) {
        mediaType = type;
    }

    public String getDetailType() {
        return mediaType;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDetailId() {
        return mediaId;
    }

    public void setDetailId(String videoId) {
        this.mediaId = videoId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setXiamiId(String xiamiId) {
        this.xiamiId = xiamiId;
    }

    public String getXiamiId() {
        return this.xiamiId;
    }

    public void setSourceName(String source) {
        this.sourceName = source;
    }

    public String getSourceName() {
        return this.sourceName;
    }
}
