package com.letv.leauto.ecolink.json;

import com.letv.leauto.ecolink.database.model.ParkingDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by zhuyanbo on 16/8/23.
 */
public class ParkingParse {


    public static ArrayList<ParkingDetail> parseParkingResult(String result) {
        ArrayList<ParkingDetail> leMarkers = new ArrayList<ParkingDetail>();
        try {
            JSONObject jsonObject = new JSONObject(result);

            boolean isSuccess = jsonObject.optBoolean("isSuccess");
            if (isSuccess) {
                JSONArray data = jsonObject.optJSONArray("parkList");
                if (data != null) {
                    leMarkers = getMediaList(data);
//                    if (leMarkers != null && leMarkers.size() > 0) {
//                        EcoApplication.LeGlob.getCache().putString(DataUtil.MEDIA_LIST, data.optJSONArray("root").toString());
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leMarkers;
    }


    public static ArrayList<ParkingDetail> getMediaList(JSONArray jsonArray) {
        ArrayList<ParkingDetail> subItems = new ArrayList<ParkingDetail>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    ParkingDetail item = new ParkingDetail();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    item.resetCount = jsonObject.optInt("restCount");
                    item.count = jsonObject.optInt("count");
                    item.firstHour = jsonObject.optDouble("firstHour");
                    item.latitude = jsonObject.optDouble("latitude");
                    item.longtitude = jsonObject.optDouble("longitude");
                    item.address = jsonObject.optString("address");
                    item.parkName = jsonObject.optString("parkName");
                    item.parkId = jsonObject.optString("parkId");
                    item.isSelected = false;
                    if (i == 50) {
                        break;
                    }
                    subItems.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return subItems;
    }

}
