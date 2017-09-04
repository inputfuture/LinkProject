package com.letv.leauto.ecolink.json;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.voicehelp.utils.Trace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liweiwei on 16/2/25.
 */
public class AlbumParse {

    public static LeObject<LeAlbumInfo> parseAlbumResp(String response,String sort_id) {
        LeObject<LeAlbumInfo> leObject = new LeObject<LeAlbumInfo>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            leObject.tag = jsonObject.optString("tag");
            int status = jsonObject.optInt("status", 0);
            if (status == 1) {
                JSONObject data = jsonObject.optJSONObject("data");
                if (data != null) {
                    leObject.success = true;
                    leObject.list = getAlbumList(data.optJSONArray("root"));
                    if (leObject.list != null && leObject.list.size() > 0) {
                        EcoApplication.LeGlob.getCache().putString(DataUtil.ALBUM_LIST + "_" + sort_id, data.optJSONArray("root").toString());
                    }
                }
            }
        } catch (Exception e) {

        }
        return leObject;
    }

    public static ArrayList<LeAlbumInfo> getAlbumList(JSONArray jsonArray) {
        ArrayList<LeAlbumInfo> albumInfos = new ArrayList<LeAlbumInfo>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    LeAlbumInfo item = new LeAlbumInfo();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    item.IMG_URL = jsonObject.optString("IMG_URL");
                    item.SRC_IMG_URL = jsonObject.optString("SRC_IMG_URL");
                    item.SORT_ID = jsonObject.optString("PAGE_ID");
                    item.ALBUM_TYPE_ID = jsonObject.optString("ALBUM_TYPE_ID");
                    item.ALBUM_ID = jsonObject.optString("ALBUM_ID");
                    item.DESCRIPTION = jsonObject.optString("DESCRIPTION");
                    item.NAME = jsonObject.optString("NAME");
                    item.SOURCE_CP_ID = jsonObject.optString("SOURCE_CP_ID");
                    if (item.NAME != null) {
                        item.NAME = item.NAME.replace("", "");
                    }
                    item.channelType="直播";
                    item.DISPLAY_NAME = jsonObject.optString("DISPLAY_NAME");
                    item.DISPLAY_SOURCE_URL = jsonObject.optString("DISPLAY_SOURCE_URL");
                    item.DISPLAY_LE_SOURCE_VID = jsonObject.optString("DISPLAY_LE_SOURCE_VID");
                    item.DISPLAY_LE_SOURCE_MID = jsonObject.optString("DISPLAY_LE_SOURCE_MID");
                    item.DISPLAY_ID = jsonObject.optString("DISPLAY_ID");
                    item.DISPLAY_IMG_URL = jsonObject.optString("DISPLAY_IMG_URL");
                    item.CREATE_TIME = jsonObject.optString("CREATE_TIME");
                    item.PLAYCOUNT = jsonObject.optString("PLAYCOUNT");
                    item.ORDER = jsonObject.optString("ORDER");
                    item.TYPE = jsonObject.optString("TYPE");
                    albumInfos.add(item);
                } catch (Exception e) {

                }
            }
        }
        return albumInfos;
    }

    public static LeObject<LeAlbumInfo> getLeAlbumInfoList(String response, Channel argChannels) {
        LeObject<LeAlbumInfo> leObject = new LeObject<LeAlbumInfo>();
        ArrayList<LeAlbumInfo> albumInfos = new ArrayList<LeAlbumInfo>();
        try {
            JSONObject jsonObject = new JSONObject(response);
           JSONArray jsonArray=  jsonObject.getJSONObject("data").getJSONArray("albums");

           for (int i=0;i<jsonArray.length();i++){

               JSONObject jsonObject1=jsonArray.getJSONObject(i);
               LeAlbumInfo leAlbumInfo = getAlbumInfoFromJson(jsonObject1);
               if (leAlbumInfo != null) {
                   leAlbumInfo.channelType = argChannels.name;
                   albumInfos.add(leAlbumInfo);
               }
           }
            leObject.list=albumInfos;
        } catch (Exception e) {
            Trace.Debug("--->exception: " + e.getMessage());
        }
        return leObject;
    }

    public static LeAlbumInfo getAlbumInfoFromJson(JSONObject json) {

        if (json == null) {
            return null;
        }

        LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
        try {
            if (json.has("name")) {
                leAlbumInfo.NAME = json.getString("name");
            }
            if (json.has("albumId")) {
                leAlbumInfo.ALBUM_ID = json.getInt("albumId") + "";
            }
            if (json.has("rating")) {
                leAlbumInfo.RATING = json.getString("rating");
            }
            if (json.has("image")) {
                leAlbumInfo.IMG_URL = json.getString("image");
            }
            if (json.has("author")) {
                leAlbumInfo.AUTHER = json.getString("author");
            }
            if (json.has("directory")) {
                leAlbumInfo.DIRECTORY = json.getString("directory");
            }

            if (json.has("mediaType")) {
                leAlbumInfo.ALBUM_TYPE_ID = json.getString("mediaType");
            }
            if (json.has("source")) {
                JSONObject jsonObject = json.getJSONObject("source");
                if (jsonObject.has("sourceName")) {
                    leAlbumInfo.SOURCENAME = jsonObject.getString("sourceName");
                }
                if (jsonObject.has("sourceId")) {
                    leAlbumInfo.SOURCEID = jsonObject.getString("sourceId");
                }
            }
            if (json.has("pageid")) {
                leAlbumInfo.PAGE_ID = json.getString("pageid");
            }
        }catch (Exception e) {

        }
        return leAlbumInfo;
    }
}
