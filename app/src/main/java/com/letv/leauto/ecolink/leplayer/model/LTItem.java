package com.letv.leauto.ecolink.leplayer.model;

import com.letv.leauto.ecolink.http.host.LetvAutoHosts;

/**
 * Created by zhaochao on 2015/6/9.
 */
public class LTItem {
    private String url="";
    private String mid="";
    private String vid="";
    private String format="";
    private String cpid="";
    private String title="";
    private String type="";
    private int position=0;
    private String audio_id="";

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String playType) {
        this.playType = playType;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    private String playType;
    private String mediaId;

    public String getAudio_id() {
        return audio_id;
    }

    public void setAudio_id(String audio_id) {
        this.audio_id = audio_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCpid() {
        return cpid;
    }

    public void setCpid(String srcName) {
        this.cpid = srcName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getRealUrl()
    {
        String url = null;
        if(this.url==null || this.url.length()<=0) {
            url = this.url;
        } else if(this.url.startsWith("http://")) {
            url = this.url;
        } else if(this.url.startsWith("/data")) {
            url = LetvAutoHosts.HOST_URL + "img" + this.url.substring(5);
        } else {
            url = LetvAutoHosts.HOST_URL + "img/uploadfile/" + this.url;
        }
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LTItem ltItem = (LTItem) o;

        if (position != ltItem.position) return false;
        if (url != null ? !url.equals(ltItem.url) : ltItem.url != null) return false;
        if (mid != null ? !mid.equals(ltItem.mid) : ltItem.mid != null) return false;
        if (vid != null ? !vid.equals(ltItem.vid) : ltItem.vid != null) return false;
        if (format != null ? !format.equals(ltItem.format) : ltItem.format != null) return false;
        if (cpid != null ? !cpid.equals(ltItem.cpid) : ltItem.cpid != null) return false;
        if (title != null ? !title.equals(ltItem.title) : ltItem.title != null) return false;
        return type != null ? type.equals(ltItem.type) : ltItem.type == null;

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (mid != null ? mid.hashCode() : 0);
        result = 31 * result + (vid != null ? vid.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (cpid != null ? cpid.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + position;
        return result;
    }

    @Override
    public String toString() {
        return "LTItem{" +
                "url='" + url + '\'' +
                ", mid='" + mid + '\'' +
                ", vid='" + vid + '\'' +
                ", format='" + format + '\'' +
                ", cpid='" + cpid + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", position=" + position +
                ", audio_id='" + audio_id + '\'' +
                '}';
    }
}
