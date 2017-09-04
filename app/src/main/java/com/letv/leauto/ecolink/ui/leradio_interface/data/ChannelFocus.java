package com.letv.leauto.ecolink.ui.leradio_interface.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ChannelFocus implements Parcelable {
    //目前用到的参数
    private String pid; // 专辑id
    private String pic; // 焦点图片
    private String name; // 名称



    private String cmsid; // cms唯一id
    private String vid; // 视频id
    private String zid; // 专题id
    private String at;
    private String episode; // 总集数
    private String nowEpisodes; // 当前更新集数
    private String isEnd; // 是否完结
    private PicAll picAll;// 图片
    private String pay; // 1:需要支付;0:免费（只有专辑有此属性）
    private String singer; // 歌手
    private String subTitle;// 副标题
    private String tag; // 盖章标签
    private String tm; // 过期时间戳
    private String type; // 影片来源标示：1-专辑,2-视频,3-明星,4-专题,5 外网影片
    private String liveCode; // 直播编号
    private String liveUrl; // 直播地址
    private String webUrl; // 外跳web地址
    private String webViewUrl; // 内嵌webview地址
    private String albumType; // 专辑类型 180001正片 180002片花 180003花絮 180004资讯 ...
    private String varietyShow; // 是否是栏目:1 - 是，0 - 否
    private String cname;// 频道名
    private String cid; // 频道入口
    private String dataUrl;// 跳转频道的dataUrl
    private String duration; // 视频时长
    private String videoType; // 视频类型 180001正片 180002片花 180003花絮 180004资讯 ...
    private String liveid; // 直播场次id
    private String homeImgUrl; // 主队图标
    private String guestImgUrl; // 客队图标
    private String id; // 直播id
    private String pageid; // 页面id
    private String director; // 导演
    private String score; // 评分
    private String updateTime; // 更新时间
    private String subCategory; // 影片类型
    private String playCount;
    private String commentCount;
    private String defaultStream; // 码流
    private String area; // 地区
    private String releaseDate; // 年代
    private String style; // 风格
    private String source;// 数据来源:1-cms;2-推荐;3-排行;4-搜索
    private String guest;
    private String issue;
    private String cornerLabel; // 角标类型，1、全网；2、付费；3、会员；4、独播；5、自制；6专题；7预告；
    private String recFragId;
    private String recReid;
    private String recArea;
    private String recBucket;
    private String mDisplayingPic;
    private String skipAppInfo; // 生态屏广告位 app跳转
    private String audioId;

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCmsid() {
        return this.cmsid;
    }

    public void setCmsid(String cmsid) {
        this.cmsid = cmsid;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getVid() {
        return this.vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getZid() {
        return this.zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String getAt() {
        return this.at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getEpisode() {
        return this.episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getNowEpisodes() {
        return this.nowEpisodes;
    }

    public void setNowEpisodes(String nowEpisodes) {
        this.nowEpisodes = nowEpisodes;
    }

    public String getIsEnd() {
        return this.isEnd;
    }

    public void setIsEnd(String isEnd) {
        this.isEnd = isEnd;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPay() {
        return this.pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getSinger() {
        return this.singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTm() {
        return this.tm;
    }

    public void setTm(String tm) {
        this.tm = tm;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLiveCode() {
        return this.liveCode;
    }

    public void setLiveCode(String liveCode) {
        this.liveCode = liveCode;
    }

    public String getLiveUrl() {
        return this.liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getWebUrl() {
        return this.webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getWebViewUrl() {
        return this.webViewUrl;
    }

    public void setWebViewUrl(String webViewUrl) {
        this.webViewUrl = webViewUrl;
    }

    public String getAlbumType() {
        return this.albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
    }

    public String getVarietyShow() {
        return this.varietyShow;
    }

    public void setVarietyShow(String varietyShow) {
        this.varietyShow = varietyShow;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVideoType() {
        return this.videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getLiveid() {
        return this.liveid;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public String getHomeImgUrl() {
        return this.homeImgUrl;
    }

    public void setHomeImgUrl(String homeImgUrl) {
        this.homeImgUrl = homeImgUrl;
    }

    public String getGuestImgUrl() {
        return this.guestImgUrl;
    }

    public void setGuestImgUrl(String guestImgUrl) {
        this.guestImgUrl = guestImgUrl;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPageid() {
        return this.pageid;
    }

    public void setPageid(String pageid) {
        this.pageid = pageid;
    }

    public String getDirector() {
        return this.director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSubCategory() {
        return this.subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getPlayCount() {
        return this.playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getCommentCount() {
        return this.commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return "ChannelFocus [cmsid=" + this.cmsid + ", pid=" + this.pid
                + ", vid=" + this.vid + ", zid=" + this.zid + ", at=" + this.at
                + ", episode=" + this.episode + ", nowEpisodes="
                + this.nowEpisodes + ", isEnd=" + this.isEnd + ", pic="
                + this.pic + ", name=" + this.name + ", pay=" + this.pay
                + ", singer=" + this.singer + ", subTitle=" + this.subTitle
                + ", tag=" + this.tag + ", tm=" + this.tm + ", type="
                + this.type + ", liveCode=" + this.liveCode + ", liveUrl="
                + this.liveUrl + ", webUrl=" + this.webUrl + ", webViewUrl="
                + this.webViewUrl + ", albumType=" + this.albumType
                + ", varietyShow=" + this.varietyShow + ", cid=" + this.cid
                + ", duration=" + this.duration + ", videoType="
                + this.videoType + ", liveid=" + this.liveid + ", homeImgUrl="
                + this.homeImgUrl + ", guestImgUrl=" + this.guestImgUrl
                + ", id=" + this.id + ", pageid=" + this.pageid
                + "]";
    }

    public String getDataUrl() {
        return this.dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getDefaultStream() {
        return this.defaultStream;
    }

    public void setDefaultStream(String defaultStream) {
        this.defaultStream = defaultStream;
    }

    public PicAll getPicAll() {
        return this.picAll;
    }

    public void setPicAll(PicAll picAll) {
        this.picAll = picAll;
    }

//    public String getArea() {
//        return this.area;
//    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCname() {
        return this.cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getGuest() {
        return this.guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getIssue() {
        return this.issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getRecFragId() {
        return this.recFragId;
    }

    public void setRecFragId(String recFragId) {
        this.recFragId = recFragId;
    }

    public String getRecReid() {
        return this.recReid;
    }

    public void setRecReid(String recReid) {
        this.recReid = recReid;
    }

    public String getRecArea() {
        return this.recArea;
    }

    public void setRecArea(String recArea) {
        this.recArea = recArea;
    }

    public String getRecBucket() {
        return this.recBucket;
    }

    public void setRecBucket(String recBucket) {
        this.recBucket = recBucket;
    }

    public String getCornerLabel() {
        return this.cornerLabel;
    }

    public void setCornerLabel(String cornerLabel) {
        this.cornerLabel = cornerLabel;
    }

    public String getDisplayingPic() {
        return this.mDisplayingPic;
    }

    public void setDisplayingPic(String pic) {
        this.mDisplayingPic = pic;
    }

    public String getSkipAppInfo() {
        return skipAppInfo;
    }

    public void setSkipAppInfo(String skipAppInfo) {
        this.skipAppInfo = skipAppInfo;
    }

    public String getAudioid() {
        return audioId;
    }

    public void setAudioid(String audioid) {
        this.audioId = audioid;
    }

    public ChannelFocus(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pid);
        dest.writeString(pic);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(audioId);
        dest.writeString(zid);
    }

    public static final Creator<ChannelFocus>  CREATOR= new Creator<ChannelFocus>(){

        @Override
        public ChannelFocus createFromParcel(Parcel source) {
            ChannelFocus focus = new ChannelFocus();
            focus.setPid(source.readString());
            focus.setPic(source.readString());
            focus.setName(source.readString());
            focus.setType(source.readString());
            focus.setAudioid(source.readString());
            focus.setZid(source.readString());
            return focus;
        }

        @Override
        public ChannelFocus[] newArray(int size) {
            return new ChannelFocus[size];
        }
    };
}
