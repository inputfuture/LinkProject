package com.letv.leauto.ecolink.manager;

import android.content.Context;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;

import java.util.HashMap;

/**
 * Created by duran on 15-1-12.
 */
public class WeatherIconManager {

    private static HashMap<String, Integer> sWeatherIconMap = new HashMap<String, Integer>();


    private static WeatherIconManager sInstance = null;
    private Context mContext;

    public static WeatherIconManager getInstance() {
        if (sInstance == null) {
            sInstance = new WeatherIconManager();
        }
        return sInstance;
    }

    private WeatherIconManager() {
        mContext = EcoApplication.instance;
    }

    public void init() {
        initWeatherRes();
    }

    private void initWeatherRes() {
        sWeatherIconMap.clear();
        sWeatherIconMap.put(mContext.getString(R.string.weather_sunny), R.mipmap.weather_qing);
        sWeatherIconMap.put(mContext.getString(R.string.weather_sunny_night), R.mipmap.weather_qing_night);
        sWeatherIconMap.put(mContext.getString(R.string.weather_cloudy), R.mipmap.weather_duoyun);
        sWeatherIconMap.put(mContext.getString(R.string.weather_cloudy_night), R.mipmap.weather_duoyun_night);
        sWeatherIconMap.put(mContext.getString(R.string.weather_shade), R.mipmap.weather_yin);
        sWeatherIconMap.put(mContext.getString(R.string.weather_hail), R.mipmap.weather_bingbao);
        sWeatherIconMap.put(mContext.getString(R.string.weather_thunderstorm), R.mipmap.weather_leizhenyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_thunderstorm_with_hail), R.mipmap.weather_leizhenyubingbao);
        sWeatherIconMap.put(mContext.getString(R.string.weather_shower), R.mipmap.weather_zhenyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_shower_night), R.mipmap.weather_zhenyu_night);
        sWeatherIconMap.put(mContext.getString(R.string.weather_sleet), R.mipmap.weather_yujiaxue);
        sWeatherIconMap.put(mContext.getString(R.string.weather_light_rain), R.mipmap.weather_xiaoyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_rain), R.mipmap.weather_zhongyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_heavy_rain), R.mipmap.weather_dayu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_rain_storm), R.mipmap.weather_baoyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_light_rain2), R.mipmap.weather_xiaoyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_rain2), R.mipmap.weather_zhongyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_heavy_rain2), R.mipmap.weather_dayu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_rain_storm2), R.mipmap.weather_baoyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_rain_storm3), R.mipmap.weather_baoyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_rain_storm4), R.mipmap.weather_baoyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_rain_storm5), R.mipmap.weather_baoyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_snow_shower), R.mipmap.weather_zhenxue);
        sWeatherIconMap.put(mContext.getString(R.string.weather_snow_shower_night), R.mipmap.weather_zhenxue_night);
        sWeatherIconMap.put(mContext.getString(R.string.weather_light_snow), R.mipmap.weather_xiaoxue);
        sWeatherIconMap.put(mContext.getString(R.string.weather_snow), R.mipmap.weather_zhongxue);
        sWeatherIconMap.put(mContext.getString(R.string.weather_heavy_snow), R.mipmap.weather_daxue);
        sWeatherIconMap.put(mContext.getString(R.string.weather_blizzard), R.mipmap.weather_baoxue);
        sWeatherIconMap.put(mContext.getString(R.string.weather_light_snow2), R.mipmap.weather_xiaoxue);
        sWeatherIconMap.put(mContext.getString(R.string.weather_snow2), R.mipmap.weather_zhongxue);
        sWeatherIconMap.put(mContext.getString(R.string.weather_heavy_snow2), R.mipmap.weather_daxue);
        sWeatherIconMap.put(mContext.getString(R.string.weather_fog), R.mipmap.weather_wu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_sleet2), R.mipmap.weather_dongyu);
        sWeatherIconMap.put(mContext.getString(R.string.weather_sandstorm), R.mipmap.weather_shachenbao);
        sWeatherIconMap.put(mContext.getString(R.string.weather_heavy_sandstorm), R.mipmap.weather_shachenbao);
        sWeatherIconMap.put(mContext.getString(R.string.weather_sand), R.mipmap.weather_yangsha);
        sWeatherIconMap.put(mContext.getString(R.string.weather_dust), R.mipmap.weather_yangsha);
        sWeatherIconMap.put(mContext.getString(R.string.weather_haze), R.mipmap.weather_wumai);
        sWeatherIconMap.put(mContext.getString(R.string.weather_unknown), R.mipmap.weather_qing);

    }
    public int getWeatherIcon(String weather) {
        if(sWeatherIconMap!=null&& sWeatherIconMap.get(weather)!=null){
            return sWeatherIconMap.get(weather);
        }else {
            return 0;
        }

    }
}
