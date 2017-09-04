package com.letv.leauto.ecolink.ui.leradio_interface.data;

import com.letv.mobile.http.model.LetvHttpBaseModel;
import com.letv.voicehelp.utils.Trace;

import org.json.JSONObject;

/**
 * Created by Han on 16/7/15.
 */
public class MusicDetailModel extends LeRadioBaseModel {


    public String playUrl;
    public int duration;
    public int playType;



    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public static MusicDetailModel parse(JSONObject object) {
        if (object == null) {
            return null;
        }
        MusicDetailModel detail = new MusicDetailModel();
        try {

            if (object.has(LeRadioBaseModel.MEDIA_DETAIL_DURATION)) {
                detail.duration = object.getInt(LeRadioBaseModel.MEDIA_DETAIL_DURATION);
            }

            if (object.has(LeRadioBaseModel.MEDIA_DETAIL_PLAYURL)) {
                detail.playUrl = object.getString(LeRadioBaseModel.MEDIA_DETAIL_PLAYURL);
            }

            if (object.has(LeRadioBaseModel.MEDIA_DETAIL_PLAYURL_TYPE)) {
                detail.playType = object.getInt(LeRadioBaseModel.MEDIA_DETAIL_PLAYURL_TYPE);
            }

        } catch (Exception e) {
            Trace.Debug("--->e: " + e.getMessage());
        }
        return detail;
    }
    @Override
    public String toString() {
        return "MusicDetailModel{" +
                ", playUrl='" + playUrl + '\'' +
                ", duration=" + duration +
                '}';
    }
}
