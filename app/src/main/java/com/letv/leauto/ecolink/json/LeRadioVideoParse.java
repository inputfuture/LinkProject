package com.letv.leauto.ecolink.json;

import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.ui.leradio_interface.data.PositiveSeries;
import com.letv.leauto.ecolink.ui.leradio_interface.data.VideoListModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.VideoListResponseModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kevin on 2016/12/19.
 */
public class LeRadioVideoParse {

    public static final String VIDEO_LIST_PAGE = "page";
    public static final String VIDEO_LIST_PAGESIZE = "pageSize";
    public static final String VIDEO_LIST_PAGESERIES = "positiveSeries";


    /*标题*/
    public static final String VIDEO_INFO_NAME = "name";

    /*副标题*/
    public static final String VIDEO_INFO_SUBNAME = "subName";

    /*专辑 id*/
    public static final String VIDEO_INFO_ALBUMID = "albumId";

    /*专辑分类id*/
    public static final String VIDEO_INFO_CATEGORYID = "categoryId";

    /*专辑下详情id*/
    public static final String VIDEO_INFO_DETAILID = "detailId";

    /*专辑下详情类型*/
    public static final String VIDEO_INFO_DETAILTYPE = "detailType";

    /*作者*/
    public static final String VIDEO_INFO_AUTHOR = "author";

    /*集数*/
    public static final String VIDEO_INFO_EPISODE = "episode";

    /*剧集海报*/
    public static final String VIDEO_INFO_IMAGE = "img";

    /*时长*/
    public static final String VIDEO_INFO_DURATION = "duration";

    /*所在页码*/
    public static final String VIDEO_INFO_PAGE = "page";


    public static List<MediaDetail> parse(JSONObject json) {
        try {
            JSONObject series = json.getJSONObject(VIDEO_LIST_PAGESERIES);
            VideoListResponseModel list = new VideoListResponseModel();
            list.setPage(series.getInt(VIDEO_LIST_PAGE));
            list.setPageSize(series.getInt(VIDEO_LIST_PAGESIZE));
            JSONArray array = series.getJSONArray(VIDEO_LIST_PAGESERIES);

            for (int i=0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                VideoListModel model = new VideoListModel();
                model.setDetailId(object.getString(VIDEO_INFO_DETAILID));
                model.setCategoryId(object.getInt(VIDEO_INFO_CATEGORYID));

            }
        } catch (Exception e) {

        }

        return null;
    }
}
