package com.letv.leauto.ecolink.json;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.database.model.LeCPDic;
import com.letv.leauto.ecolink.database.model.LeSortInfo;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liweiwei on 16/3/2.
 */
public class CpParse {
    public static LeObject<LeCPDic> parseCpResp(String response) {
        LeObject<LeCPDic> leObject = new LeObject<LeCPDic>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            leObject.tag = jsonObject.optString("tag");
            int status = jsonObject.optInt("status", 0);
            if (status == 1) {
                JSONObject data = jsonObject.optJSONObject("data");
                if (data != null) {
                    leObject.success = true;
                    leObject.list = getCPList(data.optJSONArray("root"));
                    if (leObject.list != null && leObject.list.size() > 0) {
                        EcoApplication.LeGlob.getCache().putString(DataUtil.CP_LIST, data.optJSONArray("root").toString());
                    }
                }
            }
        } catch (Exception e) {

        }
        return leObject;
    }
    public static ArrayList<LeCPDic> getCPList(JSONArray jsonArray) {
        ArrayList<LeCPDic> subItems = new ArrayList<LeCPDic>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    LeCPDic item = new LeCPDic();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    item.SOURCE_CP_ID = jsonObject.optString("SOURCE_CP_ID");
                    //item.CREATE_TIME_BAK = jsonObject.optString("CREATE_TIME_BAK");
                    item.NAME = jsonObject.optString("NAME");
                    item.ALIAS_NAME = jsonObject.optString("ALIAS_NAME");
                   /* item.CREATE_TIME = jsonObject.optString("CREATE_TIME");
                    item.DESCRIPTION = jsonObject.optString("DESCRIPTION");
                    item.MEDIA_CHANNEL_ID = jsonObject.optString("MEDIA_CHANNEL_ID");
                    item.MEDIA_SOURCE_ID = jsonObject.optString("MEDIA_SOURCE_ID");
                    item.UPDATE_TIME = jsonObject.optString("UPDATE_TIME");
                    item.STATUS = jsonObject.optInt("STATUS");
                    item.PHONE = jsonObject.optString("PHONE");
                    item.API = jsonObject.optString("API");
                    item.LINKMAN = jsonObject.optString("LINKMAN");
                    item.CREATE_USER = jsonObject.optString("CREATE_USER");
                    item.UPDATE_USER = jsonObject.optString("UPDATE_USER");
                    item.UPDATE_TIME_BAK = jsonObject.optString("UPDATE_TIME_BAK");
                    item.MODULE_KEY = jsonObject.optString("MODULE_KEY");*/
                    subItems.add(item);
                } catch (Exception e) {

                }
            }
        }
        return subItems;
    }
}
