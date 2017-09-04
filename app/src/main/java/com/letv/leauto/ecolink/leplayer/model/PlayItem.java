package com.letv.leauto.ecolink.leplayer.model;

import com.letv.leauto.ecolink.utils.Constants;

/**
 * Created by fuqinqin on 2016/12/19.
 */
public class PlayItem {
    private String url="";
    private String id="";
    private String xmid="";
    private String source = "";
    private String cpName = "";
    private String title="";
    private String playType="";
    private long progress = 0;
    private long duration = 0;
    private long playTime = 0;

    private String imageUrl = "";
    private String author = "";

    private String cpid = "";
    private String mid = "";
    private String vid = "";


    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String type) {
        this.playType = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public long getProgress() {
        return progress;
    }

    public long getDuration() {
        return duration;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public String getXmid() {
        return xmid;
    }

    public String getCpName() {
        return cpName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setXmid(String xmid) {
        this.xmid = xmid;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public String getCpid() {
        return cpid;
    }

    public String getMid() {
        return mid;
    }

    public String getVid() {
        return vid;
    }

    public void setCpid(String cpid) {
        this.cpid = cpid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isXiaMiItem(){
        if(xmid != null && !xmid.trim().equals("")){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isLocalItem(){
        if(url != null && !url.trim().equals("") && url.startsWith("/")){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isLeradioItem(){
        if(playType != null && !playType.trim().equals("") && !id.trim().equals("")){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isLiveItem(){
        if(mid != null && !mid.trim().equals("") && cpid != null && !cpid.trim().equals("")){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (xmid != null ? xmid.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (cpName != null ? cpName.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (playType != null ? playType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PlayItem{" +
                "url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", xmid='" + xmid + '\'' +
                ", vid='" + vid + '\'' +
                ", mid='" + mid + '\'' +
                ", cpid='" + cpid + '\'' +
                ", playType='" + playType + '\'' +
                ", source='" + source + '\'' +
                ", cpName='" + cpName + '\'' +
                ", title='" + title + '\'' +
                ", progress='" + progress + '\'' +
                ", duration='" + duration + '\'' +
                ", playTime='" + playTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayItem that = (PlayItem) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (xmid != null ? !xmid.equals(that.xmid) : that.xmid != null)
            return false;
        if (source != null ? !source.equals(that.source) : that.source != null)
            return false;
        if (cpName != null ? !cpName.equals(that.cpName) : that.cpName != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null)
            return false;
        if (playType != null ? !playType.equals(that.playType) : that.playType != null) return false;
        return true;

    }

}


