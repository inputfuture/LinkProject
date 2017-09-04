package com.letv.leauto.ecolink.ui.leradio_interface.data;

import com.letv.mobile.http.model.LetvHttpBaseModel;

import java.util.ArrayList;

public class TopicDetailModel extends LetvHttpBaseModel {

    // 专辑专题
    public static final String TOPIC_TYPE_ALBUM = "1";
    // 视频专题
    public static final String TOPIC_TYPE_VIDEO = "2";

    private String name;
    private String type;
    private String ctime;
    private String cid;
    private ArrayList<TopicItem> dataList;
    private String desc;

    public boolean isTypeAlbum() {
        return TOPIC_TYPE_ALBUM.equals(this.type);
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCtime() {
        return this.ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public ArrayList<TopicItem> getDataList() {
        return this.dataList;
    }

    public void setDataList(ArrayList<TopicItem> dataList) {
        this.dataList = dataList;
    }

    public static class TopicItem extends LetvHttpBaseModel {
        private String id;
        private String nameCn;
        private String picUrl;
        private String dataType;
        private String subTitle;
        private String download;
        private int duration;
        private int pid;
        private String sourceName;
        public boolean isTypeAlbum() {
            return TOPIC_TYPE_ALBUM.equals(this.dataType);
        }

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNameCn() {
            return this.nameCn;
        }

        public void setNameCn(String nameCn) {
            this.nameCn = nameCn;
        }

        public String getPicUrl() {
            return this.picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getDataType() {
            return this.dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getSubTitle() {
            return this.subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        @Override
        public String toString() {
            return "TopicItem [id=" + this.id + ", nameCn=" + this.nameCn
                    + ", picUrl=" + this.picUrl + ", dataType=" + this.dataType
                    + ", subTitle=" + this.subTitle + ", duration=" +this.duration+"]";
        }

        public String getDownload() {
            return this.download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }
    }
}
