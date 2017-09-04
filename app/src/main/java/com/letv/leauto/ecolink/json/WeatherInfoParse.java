package com.letv.leauto.ecolink.json;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by liweiwei on 16/3/28.
 */
public class WeatherInfoParse {
    public static WeatherInfo parseWeatherInfo(String origin_result) {
        WeatherInfo result = new WeatherInfo();
        try {
            JSONObject originObject = new JSONObject(origin_result);
            result.city = originObject.optString("city");
            result.sunrise = originObject.optString("sunrise_1");
            result.sunset = originObject.optString("sunset_1");

            JSONObject environment = originObject.optJSONObject("environment");
            result.quality = environment.optString("quality");
            result.pm25 = environment.optString("pm25");

            JSONObject resultObject = originObject.optJSONObject("forecast");
            JSONArray forecastList = resultObject.optJSONArray("weather");

            if (forecastList.length() > 0) {
                JSONObject detail = forecastList.optJSONObject(0);

                String high = detail.optString("high");
                String low = detail.optString("low");
                result.temp = low.replace(EcoApplication.instance.getString(R.string.weather_cold), "").replace("℃", " ") + "~" + high.replace(EcoApplication.instance.getString(R.string.weather_hot), "");
                JSONObject day = detail.optJSONObject("day");

                String dayType = day.optString("type");

                JSONObject night = detail.optJSONObject("night");
                String nightType = night.optString("type");
                result.weather = dayType;

                //数据返回正确结果后再存储
//                CacheUtils.getInstance(EcoApplication.getInstance()).putString(Constant.WEATHER_INFO, origin_result);
//                CacheUtils.getInstance(EcoApplication.getInstance()).putString(Constant.TRAFFIC_INFO_TWO, /*result.pm25 + " " + */result.quality);

            }

        } catch (Exception e) {

        }
        return result;
    }
}
