package com.letv.leauto.ecolink.ui.leradio_interface.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anfengyang on 16/5/16.
 */
public class MediaInfoModel implements Parcelable {
    private String author;
    private String type;


    //media 类型 0-广播  1-点播  2-直播



    private int mediaType;
    //广播参数
    private String letvOriginId;
    private String favoriteId;

    private String desc;
    private int collectionId;
    private NowPlayingProgramme nowPlayingProgramme = null;
    //点播参数
    private int duration;
    private int pid;
    private String albumName;
    private String playType;
    private String sourceName;

    //通用参数
    //mediaId
    private String mediaId;
    private String name;
    private String logoUrl;
    private String playUrl;
    public MediaInfoModel(){

    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getLetvOriginId() {
        return letvOriginId;
    }

    public void setLetvOriginId(String letvOriginId) {
        this.letvOriginId = letvOriginId;
    }

    public String getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public NowPlayingProgramme getNowPlayingProgramme() {
        return nowPlayingProgramme;
    }

    public void setNowPlayingProgramme(NowPlayingProgramme nowPlayingProgramme) {
        this.nowPlayingProgramme = nowPlayingProgramme;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MediaInfoModel{" +
                "mediaType=" + mediaType +
                ", letvOriginId='" + letvOriginId + '\'' +
                ", favoriteId='" + favoriteId + '\'' +
                ", desc='" + desc + '\'' +
                ", collectionId=" + collectionId +
                ", nowPlayingProgramme=" + nowPlayingProgramme +
                ", duration=" + duration +
                ", pid=" + pid +
                ", albumName='" + albumName + '\'' +
                ", playType='" + playType + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", name='" + name + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", playUrl='" + playUrl + '\'' +
                '}';
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String playType) {
        this.playType = playType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mediaType);
        dest.writeString(letvOriginId);
        dest.writeString(favoriteId);
        dest.writeString(playUrl);
        dest.writeString(desc);
        dest.writeInt(collectionId);
        dest.writeInt(duration);
        dest.writeInt(pid);
        dest.writeString(albumName);
        dest.writeString(mediaId);
        dest.writeString(name);
        dest.writeString(logoUrl);
        dest.writeString(playType);
        dest.writeString(sourceName);
    }

    public MediaInfoModel(Parcel in) {
        mediaType = in.readInt();
        letvOriginId = in.readString();
        favoriteId = in.readString();
        playUrl = in.readString();
        desc = in.readString();
        collectionId = in.readInt();
        duration = in.readInt();
        pid = in.readInt();
        albumName = in.readString();
        mediaId = in.readString();
        name = in.readString();
        logoUrl = in.readString();
        playType = in.readString();
        sourceName = in.readString();
    }

    public static final Creator<MediaInfoModel> CREATOR = new Creator<MediaInfoModel>() {
        @Override
        public MediaInfoModel createFromParcel(Parcel in) {
            return new MediaInfoModel(in);
        }

        @Override
        public MediaInfoModel[] newArray(int size) {
            return new MediaInfoModel[size];
        }
    };


}
