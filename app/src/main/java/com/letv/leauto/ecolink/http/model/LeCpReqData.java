package com.letv.leauto.ecolink.http.model;

import com.letv.leauto.ecolink.http.host.LetvAutoHosts;
import com.letv.leauto.ecolink.leplayer.model.PlayItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liweiwei on 16/2/23.
 */
public class LeCpReqData {

    private PlayItem item;
    private int tagNum;
    private  Boolean isWifi;

    public static LeCpReqData create(PlayItem song,int tagNum,Boolean isWifi) {
        LeCpReqData data = new LeCpReqData();
        data.item = song;
        data.isWifi = isWifi;
        return data;
    }
    public String toLeCpRequestBody()
    {
        try {

            JSONObject data = new JSONObject();
            data.put("LE_SOURCE_MID", this.item.getMid());
            data.put("LE_SOURCE_VID", this.item.getVid());
            data.put("SOURCE_CP_ID", this.item.getCpid());
            data.put("IS_WIFI", this.isWifi?"y":"n");
            JSONObject requsetJson = new JSONObject();
            requsetJson.put("modulename", "dyn_media");
            requsetJson.put("tag", String.valueOf(this.tagNum));
            requsetJson.put("operation", "query");
            requsetJson.put("data", data);
            requsetJson.put("tokenid", LetvAutoHosts.TOKEN_ID);
            return  "parameter=" + requsetJson.toString() + "&tokenid=" + LetvAutoHosts.TOKEN_ID;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


}
