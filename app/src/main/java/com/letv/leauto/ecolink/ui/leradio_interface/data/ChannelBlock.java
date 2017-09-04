package com.letv.leauto.ecolink.ui.leradio_interface.data;

import java.util.ArrayList;

public class ChannelBlock {
    private String name; // 模块名称
    private String blockSubName; // 模块副标题
    private String nativeSubjectStartTime; // 专题开始的时间
    private String cid; // 模块频道id
    private String reid; // 推荐模块的唯一id，用于数据上报
    private String rectCid; // 模块跳转，需要跳转的频道id
    private String rectCName;// 模块跳转，需要跳转的频道name
    private String rectPageId; // 模块跳转，需要跳转到的pageid(每个导航标签都有一个pageid)
    private String rectType; // 模块跳转类型 1: 跳转条件筛选 2: 跳转导航标签 3:跳H5
    private String rectUrl; // 模块跳转，需要跳转到H5的url
    private String rectVt;
    private String dataUrl;// 跳转频道的dataUrl

    private String rectSubCid; // 副标题跳转，需要跳转的频道id
    private String rectSubCName; // 副标题跳转，需要跳转的频道名称
    private String rectSubPageId; // 副标题跳转，需要跳转到的pageid
    private String rectSubType; // 副标题跳转，跳转类型 1: 跳转检索页 2:
    // 跳转导航标签（包括排行，首页，频道内检索）；3跳转到H5页面
    private String rectSubUrl; // 副标题跳转，配置的直接跳转的地址（H5页面地址）
    private String rectSubVt; // 副标题跳转，跳转的视频类型
    private String dataSubUrl; // 副标题跳转，跳转频道的URL

    private String recFragId;
    private String recReid;
    private String recArea;
    private String recBucket;
    private String recSrcType;
    private String style;//模块展现样式，客户端会根据这个字段，决定以何种样式展示模块数据
    private ArrayList<ChannelFocus> list; // 模块下的默认数据
    private String liveCount;//直播总场数

    public String getLiveCount() {
        return this.liveCount;
    }

    public void setLiveCount(String liveCount) {
        this.liveCount = liveCount;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getReid() {
        return this.reid;
    }

    public void setReid(String reid) {
        this.reid = reid;
    }

    public String getRectCid() {
        return this.rectCid;
    }

    public void setRectCid(String rectCid) {
        this.rectCid = rectCid;
    }

    public String getRectPageId() {
        return this.rectPageId;
    }

    public void setRectPageId(String rectPageId) {
        this.rectPageId = rectPageId;
    }

    public String getRectType() {
        return this.rectType;
    }

    public void setRectType(String rectType) {
        this.rectType = rectType;
    }

    public String getRectUrl() {
        return this.rectUrl;
    }

    public void setRectUrl(String rectUrl) {
        this.rectUrl = rectUrl;
    }

    public String getRectVt() {
        return this.rectVt;
    }

    public void setRectVt(String rectVt) {
        this.rectVt = rectVt;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public ArrayList<ChannelFocus> getList() {
        return this.list;
    }

    public void setList(ArrayList<ChannelFocus> list) {
        this.list = list;
    }

    public String getDataUrl() {
        return this.dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getRectCName() {
        return this.rectCName;
    }

    public void setRectCName(String rectCName) {
        this.rectCName = rectCName;
    }

    public String getBlockSubName() {
        return this.blockSubName;
    }

    public void setBlockSubName(String blockSubName) {
        this.blockSubName = blockSubName;
    }

    public String getRectSubCid() {
        return this.rectSubCid;
    }

    public void setRectSubCid(String rectSubCid) {
        this.rectSubCid = rectSubCid;
    }

    public String getRectSubCName() {
        return this.rectSubCName;
    }

    public void setRectSubCName(String rectSubCName) {
        this.rectSubCName = rectSubCName;
    }

    public String getRectSubPageId() {
        return this.rectSubPageId;
    }

    public void setRectSubPageId(String rectSubPageId) {
        this.rectSubPageId = rectSubPageId;
    }

    public String getRectSubType() {
        return this.rectSubType;
    }

    public void setRectSubType(String rectSubType) {
        this.rectSubType = rectSubType;
    }

    public String getRectSubUrl() {
        return this.rectSubUrl;
    }

    public void setRectSubUrl(String rectSubUrl) {
        this.rectSubUrl = rectSubUrl;
    }

    public String getRectSubVt() {
        return this.rectSubVt;
    }

    public void setRectSubVt(String rectSubVt) {
        this.rectSubVt = rectSubVt;
    }

    public String getDataSubUrl() {
        return this.dataSubUrl;
    }

    public void setDataSubUrl(String dataSubUrl) {
        this.dataSubUrl = dataSubUrl;
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

    public String getRecSrcType() {
        return this.recSrcType;
    }

    public void setRecSrcType(String recSrcType) {
        this.recSrcType = recSrcType;
    }

    public String getNativeSubjectStartTime() {
        return this.nativeSubjectStartTime;
    }

    public void setNativeSubjectStartTime(String nativeSubjectStartTime) {
        this.nativeSubjectStartTime = nativeSubjectStartTime;
    }


}
