package com.letv.leauto.ecolink.json;

import android.os.Message;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by liweiwei on 16/4/25.
 */
public class TrafficRestrictionParse {

    public static String parseTrafficInfo(String response) {
        StringBuffer res = new StringBuffer();
        try {
            JSONObject result = new JSONObject(response);
            String resultCode = result.getString("code");
            if (resultCode != null && resultCode.equals("200")) {
                String obj = result.optString("obj");
                if (obj != null && obj.length() > 0) {
                    //获取今日限号数据
                    if (obj.contains(",")){
                        String sourceStr = obj;
                        String[] sourceStrArray  = sourceStr.split(",");
                        for (int i = 0; i < sourceStrArray.length; i++) {
                            if (i==sourceStrArray.length-1){
                                res.append("|"+sourceStrArray[i]);
                            }else {
                                res.append(sourceStrArray[i]);
                            }
                        }

                    }else if (obj != null&&obj.contains(EcoApplication.instance.getString(R.string.str_unlimit))){

                        res.append(EcoApplication.instance.getString(R.string.str_unlimit_today));
                    }else {
                        res.append(obj);
                    }
                }else if (obj == null ||(obj!=null&& obj.length()==0)){
                    res.append(EcoApplication.instance.getString(R.string.str_unlimit_city));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}
