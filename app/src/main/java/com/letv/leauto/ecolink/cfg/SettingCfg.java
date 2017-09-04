package com.letv.leauto.ecolink.cfg;

/**
 * Created by liweiwei on 16/3/18.
 */
public class SettingCfg {
    public static final String DOWNLOAD_OPEN = "download_open";
    public static final String BARRAGE_OPEN = "barrage_open";
    public static final String TTS_OPEN = "tts_open";
    public static final String SCREEN_LIGHT_OPEN = "screen_light_open";
    public static final String SCREEN_POTRAIT_OPEN = "screen_potrait_open";
    public static final String DATA_DOWNLOAD_OPEN="data_download";
    public static final String PALY_MODE="play_mode";
    public static final String SHOW_NET_DIALOG="net_hint";
    public static final String LastPostion="LastPostion";//存储最后一次的音乐界面是哪个

    public static final String NAVI_SELECT_KYE="NAVI_SELECT_KYE";//0: 视车联 1：高德导航 2：百度导航
    public static final String NAVI_MAP_MODE="NAVI_MAP_MODE"; //0: 日间 1：夜间 2：自动
    public static final String NAVI_TRAFFIC_ON_OFF = "NAVI_TRAFFIC_ON_OFF"; //0: 开启 1：关闭
    public static final String NAVI_SPEAKER = "NAVI_SPEAKER";//0:静音 1：播报
    public static final String NAVI_SPEAKER_CONTENT = "NAVI_SPEAKER_CONTENT"; //0X01电子眼 0X02 前方路况 0X04 导航信息

    public static final String NAVI_ONGOING = "NAVI_ONGOING";
    public static final String NAVI_END_ADDRESS = "NAVI_END_ADDRESS";

    public static final String Land="screen_land";/*强制横屏是否打开*/
    /*
    0: 200 M
    1: 300 M
    2: 500 M
    3: 800 M
    4: 1024 M
     */
    public static final String CACHE_LEVEL="CACHE_LEVEL";
    public static final String BROADCAST_DOWNlOAD_SWICH = "mobile_download_swich";/*使用2g播放广播*/
    public static final String BROADCAST_2G_PLAY_SWICH = "mobile_play_swich";/*使用2g播放广播*/
    public static final String USE_MOBLIE_NET_PLAY = "user_mobile_net_play";
    public static final String USE_MOBILE_DOWNLOAD_MAP = "use_mobile_download_map";

    public static final String TRAFFIC_OPEN_MAP = "traffic_open_map";
    public static final String MAP_DOWNLOAD = "map_download";
    public static final String QPLAY_MODE="qPlay_mode";
    public static final String QPLAY_FIRST_LOGIN = "qPlay_first_login";
    /** 标记车机是不是第一次连接 */
    public static final String IS_FIRST_CONNECT = "is_first_connect";
    public static final String NAVI_SCALE_OPEN = "navi_scale_open";

    public static float FONT_SCALE=1.0f;
}
