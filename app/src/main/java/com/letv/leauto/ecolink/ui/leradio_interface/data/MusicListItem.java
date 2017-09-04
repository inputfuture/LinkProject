package com.letv.leauto.ecolink.ui.leradio_interface.data;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Han on 16/7/15.
 */
public class MusicListItem implements Serializable {


    /* 标题 */
    public String name;
    /* 专辑id（音/视频） */
    public String albumId;

    /* 副标题 */
    public String subName;

    /* 专辑分类id */
    public String categoryId;

    /* 专辑下详情id */
    public String mediaId;

    /* 专辑下详情类型 */
    public String mediaType;

    /* 作者 */
    public String author;

    /* 集数 */
    public String episode;

    /* 图片 */
    public String img;

    /* 音频时长 */
    public String duration;


    /* 虾米id（音频使用） */
    public String sourceId;

    /* 音频来源（音频使用） */
    public String sourceName;

    /* 页码 */
    public int page;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String id) {
        albumId = id;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getmediaId() {
        return mediaId;
    }

    public void setmediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getmediaType() {
        return this.mediaType;
    }

    public void setmediaType(String type) {
        this.mediaType = mediaType;
    }

    public String getsinger() {
        return this.author;
    }

    public void setsinger(String singer) {
        this.author = singer;
    }

    public String getEpisode() {
        return this.episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }


    public static MusicListItem parse(JSONObject object) {

        if (object == null) {
            return null;
        }
        MusicListItem item = new MusicListItem();
        try {

            if (object.has(LeRadioBaseModel.MEDIA_LIST_NAME)) {
                item.name = object.getString(LeRadioBaseModel.MEDIA_LIST_NAME);
            }

            if (object.has(LeRadioBaseModel.MEDIA_LIST_ALBUM_ID)) {
                item.albumId = object.getString(LeRadioBaseModel.MEDIA_LIST_ALBUM_ID);
            }

            if (object.has(LeRadioBaseModel.MEDIA_LIST_SUBNAME)) {
                item.subName = object.getString(LeRadioBaseModel.MEDIA_LIST_SUBNAME);
            }

            if (object.has(LeRadioBaseModel.MEDIA_LIST_CATEGORY_ID)) {
                item.categoryId = object.getString(LeRadioBaseModel.MEDIA_LIST_CATEGORY_ID);
            }


            if (object.has(LeRadioBaseModel.MEDIA_LIST_MEDIAID)) {
                item.mediaId = object.getString(LeRadioBaseModel.MEDIA_LIST_MEDIAID);
            }

            if (object.has(LeRadioBaseModel.MEDIA_LIST_MEDIATYPE)) {
                item.mediaType = object.getString(LeRadioBaseModel.MEDIA_LIST_MEDIATYPE);
            }

            if (object.has(LeRadioBaseModel.MEDIA_LIST_AUTHOR)) {
                item.author = object.getString(LeRadioBaseModel.MEDIA_LIST_AUTHOR);
            }

            if (object.has(LeRadioBaseModel.MEDIA_LIST_EPISODE)) {
                item.episode = object.getString(LeRadioBaseModel.MEDIA_LIST_EPISODE);
            }

            if (object.has(LeRadioBaseModel.MEDIA_LIST_IMG)) {
                item.img = object.getString(LeRadioBaseModel.MEDIA_LIST_IMG);
            }

            if (object.has(LeRadioBaseModel.MEDIA_LIST_DURATION)) {
                item.duration = object.getString(LeRadioBaseModel.MEDIA_LIST_DURATION);
            }

            if (object.has(LeRadioBaseModel.MEDIA_SEARCH_SOURCE)) {
                JSONObject source = object.getJSONObject(LeRadioBaseModel.MEDIA_SEARCH_SOURCE);
                if (source.has(LeRadioBaseModel.MEDIA_LIST_SOURCEID)) {
                    item.sourceId = source.getString(LeRadioBaseModel.MEDIA_LIST_SOURCEID);
                }

                if (source.has(LeRadioBaseModel.MEDIA_LIST_SOURCENAME)) {
                    item.sourceName = source.getString(LeRadioBaseModel.MEDIA_LIST_SOURCENAME);
                }
            }

            if (object.has(LeRadioBaseModel.MEDIA_LIST_PAGE)) {
                item.page = object.getInt(LeRadioBaseModel.MEDIA_LIST_PAGE);
            }

        } catch (Exception e) {

        }
        return item;
    }
}
