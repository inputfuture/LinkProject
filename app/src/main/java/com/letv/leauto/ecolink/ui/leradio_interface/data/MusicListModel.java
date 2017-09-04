package com.letv.leauto.ecolink.ui.leradio_interface.data;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.mobile.http.model.LetvHttpBaseModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Han on 16/7/15.
 */
public class MusicListModel extends LeRadioBaseModel {

    private List<MusicListItem> audios;

    public MusicListModel() {
        audios = new ArrayList<>();
    }

    public List<MusicListItem> getAudios() {
        return audios;
    }

    public void setAudios(List<MusicListItem> audios) {
        this.audios = audios;
    }

    /**
     * 通过json解析音视频列表
     * @param object
     */
    public static MusicListModel parse(JSONObject object) {

        JSONArray album;
        MusicListModel list = new MusicListModel();
        try {
            if (object == null || !object.has(MEDIA_LIST_ALMBU)) {
                return null;
            }

            album = object.getJSONArray(MEDIA_LIST_ALMBU);
            //add for save lastAlbum for music wiget
            if (album != null) {
                EcoApplication.LeGlob.getCache().putString(DataUtil.MEDIA_LIST, album.toString());
            }
            for (int i = 0; i < album.length(); i++) {
                JSONObject obj = album.getJSONObject(i);
                MusicListItem item = MusicListItem.parse(obj);
                if (item != null) {
                    list.getAudios().add(item);
                }

            }
        } catch (Exception e) {
            Trace.Debug("e=" + e);
        }

        return list;
    }

//
//    public void setAudios(MusicListItem[] audios) {
//        this.audios = audios;
//    }
}
