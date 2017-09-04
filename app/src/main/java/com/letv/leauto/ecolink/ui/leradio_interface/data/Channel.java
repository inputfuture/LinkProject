package com.letv.leauto.ecolink.ui.leradio_interface.data;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/31.
 */
public class Channel extends LeRadioBaseModel{
//    pageId	对应频道的cms pageId
//    name		频道名称
//    mzcId		频道cid（cms配置在副标题中）
//    pic	 	cms配置的旧移动端图片
//    pic1	 	 cms配置的图片1
//    pic2	 	cms配置的图片2
//    dataUrl	 String	 频道首页数据地址
//    cmsId     cms对应的频道id,用于数据上报
//    locked    条目被锁定，1锁定（cms配置中的简介字段，配置为1，表示该频道锁定）
//    skipType  /跳转类型,2跳转到导航标签页，3跳转到H5，4跳转到特殊页面（包括4k\2k\3D\1080P\DTS\DB）
//    type	 	 频道跳转类型
//    url	 	 填充的cms简介字段，应该没什么用
    public String pageId;
    public String name;
    public String mzcId;
    public String type;
    public String pic;
    public String pic1;
    public String pic2;
    public String url;
    public String dataUrl;
    public String cmsID;
    public String skipType;

    public Channel(String pageId, String name, String mzcId, String type, String dataUrl, String cmsID, String skipType) {
        this.pageId = pageId;
        this.name = name;
        this.mzcId = mzcId;
        this.type = type;
        this.dataUrl = dataUrl;
        this.cmsID = cmsID;
        this.skipType = skipType;
    }

    public Channel() {

    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMzcId() {
        return mzcId;
    }

    public void setMzcId(String mzcId) {
        this.mzcId = mzcId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getCmsID() {
        return cmsID;
    }

    public void setCmsID(String cmsID) {
        this.cmsID = cmsID;
    }

    public String getSkipType() {
        return skipType;
    }

    public void setSkipType(String skipType) {
        this.skipType = skipType;
    }

    /**
     *
     */
    public static Channel parse(JSONObject object) {
        if (object == null) {
            return null;
        }
        Channel channel = new Channel();
        try {
            if (object.has(MEDIA_CHANNEL_NAME)) {
                channel.name = object.getString(MEDIA_CHANNEL_NAME);
            }

            if (object.has(MEDIA_CHANNEL_CMSID)) {
                channel.cmsID = object.getString(MEDIA_CHANNEL_CMSID);
            }

            if (object.has(MEDIA_CHANNEL_DATAURL)) {
                channel.dataUrl = object.getString(MEDIA_CHANNEL_DATAURL);
            }

            if (object.has(MEDIA_CHANNEL_PAGEID)) {
                channel.pageId = object.getString(MEDIA_CHANNEL_PAGEID);
            }

            if (object.has(MEDIA_CHANNEL_SKIPTYPE)) {
                channel.skipType = object.getString(MEDIA_CHANNEL_SKIPTYPE);
            }
        } catch (Exception e) {

        }
        return channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (pageId != null ? !pageId.equals(channel.pageId) : channel.pageId != null) return false;
        if (name != null ? !name.equals(channel.name) : channel.name != null) return false;
        if (mzcId != null ? !mzcId.equals(channel.mzcId) : channel.mzcId != null) return false;
        if (type != null ? !type.equals(channel.type) : channel.type != null) return false;
        if (dataUrl != null ? !dataUrl.equals(channel.dataUrl) : channel.dataUrl != null)
            return false;
        if (cmsID != null ? !cmsID.equals(channel.cmsID) : channel.cmsID != null) return false;
        return skipType != null ? skipType.equals(channel.skipType) : channel.skipType == null;

    }

    @Override
    public int hashCode() {
        int result = pageId != null ? pageId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (mzcId != null ? mzcId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (dataUrl != null ? dataUrl.hashCode() : 0);
        result = 31 * result + (cmsID != null ? cmsID.hashCode() : 0);
        result = 31 * result + (skipType != null ? skipType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "pageId='" + pageId + '\'' +
                ", name='" + name + '\'' +
                ", mzcId='" + mzcId + '\'' +
                ", type='" + type + '\'' +
                ", pic='" + pic + '\'' +
                ", pic1='" + pic1 + '\'' +
                ", pic2='" + pic2 + '\'' +
                ", url='" + url + '\'' +
                ", dataUrl='" + dataUrl + '\'' +
                ", cmsID='" + cmsID + '\'' +
                ", skipType='" + skipType + '\'' +
                '}';
    }
}
