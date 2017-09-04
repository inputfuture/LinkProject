package com.letv.leauto.ecolink.cfg;

import com.zhy.http.okhttp.callback.StringCallback;

/**
 * Created by liweiwei1 on 2015/12/17.
 */
public class Constant {
    //仅仅定义一次，教学
    public static final String IS_FIRST_TIME_MUSIC = "IS_FIRST_TIME_MUSIC";
    public static final String IS_FIRST_TIME_ALBUM = "IS_FIRST_TIME_ALBUM";
    public static final String IS_FIRST_TIME_MAP = "IS_FIRST_TIME_MAP";

    //下载最大容量和保存最长天数
    public final static long MAXIMUM_CAPACITY = 500 * 1024 * 1024/* 500*1024*1024*/;//500M
    public final static long MAXIMUM_TIME = 7 * 24 * 60 * 60 * 1000;//7天
    public static final String ACTION_LISTEN = "ACTION_LISTEN";
    public static final String ACTION_FAVOR = "ACTION_FAVOR";

    public static final String GAODE_MAP_PACKAGE_NAME = "com.autonavi.minimap";
    public static final String BAIDU_MAP_PACKAGE_NAME = "com.baidu.BaiduMap";
    public static final String LE_VIDEO_PACKAGE_NAME = "com.letv.android.client";
    public static final String WEIXIN_PACKAGE_NAME = "com.tencent.mm";

    //跳转标记
    public static final String TAG_MAIN = "main";
    public static final String TAG_MAP = "map";
    public static final String TAG_LERADIO = "leradio";
    public static final String TAG_CALL = "call";
    public static final String TAG_SETTING = "set";
    public static final String TAG_KUWO = "kuwo";
    public static final String TAG_EASY_STOP = "easy_stop";
    public static final String TAG_CHOOSE_APP = "choose_app";
    public static final String TAG_LOCAL = "leradio_local";
    public static final String TAG_MUSIC_PLAY="menu_paly";
    public static final String TAG_QPLAY="qplay";

	public static final String TAG_LE_VIDEO = "levideo";
	public static final String TAG_WEIXIN = "weixin";
    public static final String LOCAL_APP_ID = "ecolink";
    public static final String TAG_GAODE_MAP= "gaode";
    public static final String TAG_BAIDU_MAP= "baidu";
    public static final String TAG_FAVOR_CAR= "favorcar";

    //Fragment name
    public static final String FRG_MAIN = "MAIN";
    public static final String FRG_MAP = "MAP";
    public static final String FRG_CALL = "CALL";
    public static final String FRG_EASY_STOP = "EASY_STOP";
    public static final String FRG_LOCAL_MUSIC = "LOCAL_MUSIC";

    //leKey
    public static final int KEYCODE_DPAD_LEFT = 4;
    public static final int KEYCODE_DPAD_LEFT_LONG = 12;
    public static final int KEYCODE_DPAD_RIGHT = 2;
    public static final int KEYCODE_DPAD_RIGHT_LONG = 10;
    public static final int KEYCODE_PAUSE_PALY = 6;
    public static final int KEYCODE_BUTTON_1_LONG = 14;
    public static final int KEYCODE_VOICE = 5;
    public static final int KEYCODE_DPAD_UP_LONG = 11;
    public static final int KEYCODE_DPAD_UP = 3;
    public static final int KEYCODE_DPAD_DOWN_LONG = 9;
    public static final int KEYCODE_DPAD_DOWN = 1;
    public static final int KEYCODE_DPAD_BACK = 0;
    public static final int KEYCODE_DPAD_CENTER = 7;
    public static final int KEYCODE_DPAD_CENTER_LONG = 15;
    /**
     * Created by lww on 9/7/15.
     */
    public static class SpConstant {
        public static String HISTORY_SEARCHKEY = "history_search_key";
        public static String HOME_ADDR = "home_address";
        public static String WORK_ADDR = "work_address";
    }

    public static class Radio {
        public static String LAST_TYPE = "last_type";
        public static String LAST_ALBUM = "last_album";
        public static String LAST_ALBUM_NAME = "last_album_name";
        public static String LAST_IMG_URL = "last_img_url";
        public static String LAST_MUSIC_URL = "last_music_url";
        public static String LAST_MUSIC_LOCAL_URL = "last_music_local_url";
        public static String LAST_POSITION = "last_position";
        public static String LAST_SORT_ID = "last_sort_id";
        public static String LAST_VOICE_KEY_WORD = "last_voice_key_word";
        public static String LAST_MUSIC_DISPLAY = "last_music_display";
        public static String LAST_MUSIC_STATE = "last_music_state";
        public static String NONET = "YES";
        public static String KUWOJSON = "KUWOJSON";
        public static String KUWOINDES = "KUWOINDES";
        public static String TOSTRING = "TOSTRING";
        public static String AUDIO_ID = "AUDIO_ID";//每首歌的id
        public static String ALBUM_ID = "ALBUM_ID";//专辑的id
        public static String KUWO_BILL_ID = "KUWO_BILL_ID";//酷我歌单的id
        public static String MUSIC_NAME = "MUSIC_NAME";//歌曲的名字
        public static String AUDIO_AUTHOR = "AUDIO_AUTHOR";//歌曲的作者
        public static String AUDIO_DETAIL = "AUDIO_DETAIL";//歌曲的详情

    }


    public static class Call {
        public static String LAST_CALL = "last_call";
        public static String IS_CALL_IN = "is_call_in";
    }

    //存储天气信息
    public static String WEATHER_INFO = "weather_info";
    public static String TRAFFIC_INFO = "traffic_info";
    public static String TRAFFIC_INFO_TWO = "traffic_info_two";

    public static String SUNRISE="sunrise";
    public static  String SUNSET="sunset";

    //存储是否是最近的按钮搜索界面
    public static String RECENT_MAP_INFO = "recent_map_info";


    public static final String  ISCUTCAR="是瘦机车";


    public static class HomeMenu{
        public static final String NAVI="navi";
        public static final String LERAIDO="leradio";
        public static final String LOCAL_MUCIC="local_music";
        public static final String SET="set";
        public static final String PHONE="phone";

        public static final String WECHAT="com.tencent.mm";
        public static final String LEVEDIO="com.letv.android.client";
        public static final String LIVE="com.letv.android.letvlive";
        public static final String GAODE="com.autonavi.minimap";
        public static final String BAIDU="com.baidu.BaiduMap";
        public static final String ADD="add";
        public static final String QPLAY="qplay";
        public static final String FAVORCAR = "com.letv.leauto.favorcar";



    }

    public static final int CLOSE_VOICE = 0x1000;//拉起导航
    public static final int SHOW_LIVE = 0x1001;//拉起live
    public static final int SHOW_COVER = 0x1002;//显示主页面的cover view
    public static final int HIDE_COVER = 0x1003; //隐藏cover

    public static final int HIDE_MAP_TOPVIEW = 0x1004;
    public static final int SHOW_MAP_TOPVIEW = 0x1005;
    public static final int NOTIFY_MAINACT_SHOWMAP = 0x1006;
    public static final int NOTITY_NAVI_NIGHT = 0x1007;//通知导航进入夜间模式
    public static final int NOTITY_NAVI_LIGHT = 0x1008;//通知导航进入白天间模式

    public static final int MAP_HALF_SCREEN = 0x1009;//地图和导航进入半屏
    public static final int MAP_RESTORE_SCREEN = 0x1010;//地图和导航恢复到全屏

    public static final int HIDE_BOTTOM_BAR = 0x1011;//隐藏应用底部导航栏
    public static final int SHOW_BOTTOM_BAR = 0x1012;//显示应用底部导航栏

    public static final int POI_SELECT_HOME_ADDRESS = 0x1013;//选择家的地址
    public static final int POI_SELECT_WORK_ADDRESS = 0x1014;//选择工作的地址

    public static final int NOTIFY_CURRENT_PAGE = 0x1015;//通知车机当前页面

    public static final int SHOW_DEBUG_VIEW = 0x1016;//显示调试界面
    public static final int HIDE_DEBUG_VIEW = 0x1017;//隐藏调试界面


    //丰田致炫的行车规制
    public static final int DRIVING = 0x1018; //车辆在行驶中
    public static final int NO_DRIVE = 0x1019; //车辆没有行驶
}
