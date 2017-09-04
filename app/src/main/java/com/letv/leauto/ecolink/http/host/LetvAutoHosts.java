package com.letv.leauto.ecolink.http.host;

/**
 * Created by liweiwei on 16/2/23.
 */
public class LetvAutoHosts {
    public static final String HOST_URL = "http://leting.leauto.com/";         //生产环境
    public static final String BASE_URL = HOST_URL + "action";//HOST_URL+"api/v2/live/list"  ;;
    public static final String LIVE_DETAIL_URL = HOST_URL + "action";//HOST_URL+"api/v2/live/detail"  ;
    /**
     * LeRadio直播详情接口
     */
    public static final String LERADIO_LIVE_DETAIL =HOST_URL + "proxymid";
    // public static final String HOST_URL = "http://118.26.57.31:8080/";  //测试环境
    public static final String GUESS_LIKE = "http://recommend.vehicle.letv.com/leting/apis/v0/query?";
    public static final String TOKEN_ID = "fc8e42eb8fd1474d8f040c18f442190b";
    public static final String VOICE_BSN_URL = "http://api.bosonnlp.com/ner/analysis";
    public static final String WEATHER_URL = "http://api.hdtv.letv.com/iptv/api/box/newWeatherinfo.json?city=";
//    public static final String TRAFFIC_URL = "http://traffic.leautolink.com/api/licenseNo/findLimitLicenseNoByCity.do?city=";
    public static final  String TRAFFIC_URL="http://traffic.leautolink.com/api/licenseNo/findLimitLicenseNoByCity.do?city=";
}