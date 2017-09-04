package com.letv.leauto.ecolink.json;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.database.field.SortIDConfig;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liweiwei on 16/2/25.
 */
public class GuessAlbumParse {

    public static LeObject<LeAlbumInfo> parseGuessAlbumResp(String response) {
        LeObject<LeAlbumInfo> leObject = new LeObject<LeAlbumInfo>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.optJSONArray("results");
            if (results != null && results.length() > 0) {
                leObject.success = true;
                leObject.tag = "GUESS_LIKE";
                leObject.list = getGuessAlbumList(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leObject;
    }



    public static ArrayList<LeAlbumInfo> getGuessAlbumList(JSONArray jsonArray) {
        ArrayList<LeAlbumInfo> albumInfos = new ArrayList<LeAlbumInfo>();
        String cacheString = "[";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject content = jsonArray.optJSONObject(i).optJSONObject("content");
            String albumId = jsonArray.optJSONObject(i).optString("id");
            if (content != null) {
                LeAlbumInfo info = new LeAlbumInfo();
                info.SRC_IMG_URL = "";
                info.SOURCE_CP_ID = content.optString("source_cp_id");
                info.NAME = content.optString("class_name");
                info.SRC_IMG_URL = content.optString("source_img_url");
                info.IMG_URL = content.optString("img_url");
                info.ALBUM_TYPE_ID = "";
                info.SORT_ID = SortIDConfig.GUESS_LIKING;
                info.ALBUM_ID = albumId;
                info.ORDER = "0";
                JSONObject first_media = content.optJSONObject("first_media");
                if (first_media != null) {
                    info.DISPLAY_NAME = first_media.optString("media_name");
                }
                cacheString += info.toString();
                if (i < jsonArray.length() - 1) {
                    cacheString += ",";
                }
                albumInfos.add(info);
            }
        }
        cacheString = cacheString + "]";
        EcoApplication.LeGlob.getCache().putString(DataUtil.ALBUM_LIST + "_" + SortIDConfig.GUESS_LIKING, cacheString);
        return  albumInfos;
    }
}
