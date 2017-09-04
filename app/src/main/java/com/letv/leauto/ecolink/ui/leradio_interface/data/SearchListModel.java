package com.letv.leauto.ecolink.ui.leradio_interface.data;

import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.json.AlbumParse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析LeRadio搜索接口返回结果
 * Created by kevin on 2016/12/23.
 */
public class SearchListModel extends LeRadioBaseModel {

    public List<LeAlbumInfo> mAlbums;

    public List<MusicDetailModel> mAudios;

    public SearchListModel() {
        mAlbums = new ArrayList<>();
        mAudios = new ArrayList<>();
    }

    /**
     * 把后台返回的搜索数据解析成一个list，每个成员是一个SearchItemModel
     * @param object 后台返回JSON的data
     * @return
     */
    public static SearchListModel parse(JSONObject object) {
        if (object == null) {
            return null;
        }

        SearchListModel list = new SearchListModel();
        try {
            if (object.has(MEDIA_SEARCH_VIDEOALBUMS)) {
                JSONArray vAlbum = object.getJSONArray(MEDIA_SEARCH_VIDEOALBUMS);
                for (int i = 0; i < vAlbum.length(); i++) {
                    LeAlbumInfo info = AlbumParse.getAlbumInfoFromJson(vAlbum.getJSONObject(i));
                    if (info != null) {
                        list.mAlbums.add(info);
                    }
                }

            }

            if (object.has(MEDIA_SEARCH_AUDIOALBUMS)) {
                JSONArray aAlbum = object.getJSONArray(MEDIA_SEARCH_AUDIOALBUMS);

                for (int i = 0; i < aAlbum.length(); i++) {
                    LeAlbumInfo info = AlbumParse.getAlbumInfoFromJson(aAlbum.getJSONObject(i));
                    if (info != null) {
                        list.mAlbums.add(info);
                    }
                }

            }

            if (object.has(MEDIA_SEARCH_AUDIOS)) {
                JSONArray aAudios = object.getJSONArray(MEDIA_SEARCH_AUDIOS);

                for (int i = 0; i < aAudios.length(); i++) {

                    MusicDetailModel detail = MusicDetailModel.parse(aAudios.getJSONObject(i));
                    if (detail != null) {
                        list.mAudios.add(detail);
                    }
                }

            }
        } catch (Exception e) {

        }
        return list;
    }
}
