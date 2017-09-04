package com.letv.leauto.ecolink.ui.leradio_interface.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * videoAlbums、audioAlbums、audios列表下的item
 * 这三个类型统一用这一个类描述，有些字段只对某个类型有意义
 * Created by kevin on 2016/12/22.
 */
public class SearchItemModel extends LeRadioBaseModel {

    /* 类型：videoAlbums、audioAlbums、audios 三种 */
    public String mTag;

    public String name;

    /* 专辑Id */
    public String albumId;

    /* 评分 */
    public String rating;

    /* 专辑时长 - 音频 */
    public int duration;

    /* 主演  - 视频 */
    public List<String> starring;

    /* 导演 - 视频 */
    public List<String> directory;

    /* 缩略图 */
    public String images;

    /* 演唱者 - 音频 */
    public String singer;

    /* 音频来源 - 音频 */
    public String sourceName;

    /* 音频来源ID - 音频 */
    public String sourceId;

    /* 音频Id - 音频 */
    public String mediaId;

    /* 类型 */
    public String mediaType;


    public SearchItemModel() {
        starring = new ArrayList<>();
        directory = new ArrayList<>();
    }

    /**
     * 解析单个item
     * @param object Json中具体的item对象
     * @return
     */
    public static SearchItemModel parse(JSONObject object) {
        if (object == null) {
            return null;
        }

        SearchItemModel item = new SearchItemModel();

        try {
            if (object.has(MEDIA_DETAIL_NAME)) {
                item.name = object.getString(MEDIA_DETAIL_NAME);
            }

            if (object.has(MEDIA_SEARCH_ALBUMID)) {
                item.albumId = object.getString(MEDIA_SEARCH_ALBUMID);
            }
            if (object.has(MEDIA_SEARCH_RATING)) {
                item.rating = object.getString(MEDIA_SEARCH_RATING);
            }

            if (object.has(MEDIA_SEARCH_DURATION)) {
                item.duration = object.getInt(MEDIA_SEARCH_DURATION);
            }
            if (object.has(MEDIA_SEARCH_STARRING)) {
                JSONArray star = object.getJSONArray(MEDIA_SEARCH_DURATION);
                for (int j = 0; j < star.length(); j++) {
                    item.starring.add(star.getString(j));
                }
            }

            if (object.has(MEDIA_SEARCH_DIRECTORY)) {
                JSONArray star = object.getJSONArray(MEDIA_SEARCH_DIRECTORY);
                for (int j = 0; j < star.length(); j++) {
                    item.directory.add(star.getString(j));
                }
            }

//            if (object.has(MEDIA_SEARCH_IMAGES)) {
//                item.images = object.getString(MEDIA_SEARCH_IMAGES);
//            }

            if (object.has(MEDIA_SEARCH_SINGER)) {
                item.singer = object.getString(MEDIA_SEARCH_SINGER);
            }

            if (object.has(MEDIA_SEARCH_IMAGES)) {
                item.images = object.getString(MEDIA_SEARCH_IMAGES);
            }

            if (object.has(MEDIA_SEARCH_SOURCE)) {
                JSONObject source = object.getJSONObject(MEDIA_SEARCH_SOURCE);
                if (source != null) {
                    if (source.has(MEDIA_SEARCH_SOURCENAME)) {
                        item.sourceName = source.getString(MEDIA_SEARCH_SOURCENAME);
                    }

                    if (source.has(MEDIA_SEARCH_SOURCEID)) {
                        item.sourceId = source.getString(MEDIA_SEARCH_SOURCEID);
                    }

                }
            }

            if (object.has(MEDIA_SEARCH_MEDIAID)) {
                item.images = object.getString(MEDIA_SEARCH_MEDIAID);
            }

            if (object.has(MEDIA_SEARCH_MEDIATYPE)) {
                item.mediaType = object.getString(MEDIA_SEARCH_MEDIATYPE);
            }

        } catch (Exception e) {

        }
        return item;
    }
}
