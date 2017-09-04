package com.letv.leauto.ecolink.cfg;

/**
 * Created by liweiwei on 16/2/24.
 */
public class MessageTypeCfg {
    public static final int MSG_FROM_LOCAL = 0x111;
    public static final int MSG_FROM_VOICE = 0x112;
    public static final int MSG_FROM_RECENT = 0x113;
    public static final int MSG_TOPITEMS_OBTAINED = 0x01;
    public static final int MSG_SUBITEMS_OBTAINED = 0x02;
    public static final int MSG_MEDIALST_OBTAINED = 0x03;
    public static final int MSG_GETDATA_COMPLETED = 0x04;
    public static final int MSG_REFRESH_COMPLETED = 0x05;
    public static final int MSG_CPLIST_OBTAINED = 0x06;
    public static final int MSG_SEARCH_BY_VOICE_OBTAINED = 0x07;
    public static final int MSG_GET_WEATHER = 0x08;
    public static final int MSG_GET_WEATHER_FAIL = 0x081;
    public static final int MSG_GET_TRAFFIC = 0x09;
    public static final int MSG_GET_TRAFFIC_FAIL= 0x091;
    public static final int MSG_GET_MUSIC = 0x10;
    public static final int MSG_GET_KWUMUSIC = 0x11;
    public static final int MSG_LIVE_MEDIALST_OBTAINED = 0x12;


    public static final int MSG_INIT_LOCATION = 0x60;

    public static final int MSG_GETDATA_FAILED = 0x98;
    public static final int MSG_GETDATA_EXCEPTION = 0x99;
    public static final int MSG_VOICE_SEARCH=0X97;
    public static final int MSG_NODATA_GET = 0x96;

    public static final int MSG_CAR_CONNECT = 0x21;
    public static final int MSG_CAR_UNCONNECT = 0x22;
    public static final int MSG_STOP_MUSIC = 0x23;
    public static final int MSG_START_VOICEIN = 0x24;
    public static final int MSG_STOP_VOICEIN = 0x25;
    public static final int RECENT_CONTACT = 0x26;

    public static final int MSG_ROUND_GUIDE = 0x44;
    public static final int MSG_MAIN = 0x45;
    public static final int MSG_MUSIC = 0x46;
    public static final int MSG_PHONE = 0x47;

    //没有通讯录权限
    public static final int MSG_NO_CONNECTS_PERMISSION = 0x010;
    //    QU
//搜索推荐列表
    public static final int MSG_GET_KUWO_RECOMMEND = 0x33;
    //酷我搜索
    public static final int MSG_GET_KUWO_SEARCH = 0x34;
    //酷我搜索歌单
    public static final int MSG_GET_KUWO_SEARCH_LIST = 0x35;
    //搜索电台歌曲,根据列表id获取列表内容
    public static final int MSG_GET_KUWO_ID_SEARCH = 0x36;
    //搜索歌曲链接,获取歌曲图片,获取歌词
    public static final int MSG_GET_KUWO_URL_SEARCH = 0x37;
    //获取歌曲图片,
    public static final int MSG_GET_KUWO_PIC_URL = 0x38;
    public static final int MSG_GET_FROM_GETKUWOUTILS = 0x39;
    public static final int MSG_GET_KUWO_DATA_FAILE = 0x40;
    //搜索电台列表
    public static final int MSG_GET_KUWO_RADIO_LIST = 0x41;
    public static final int MUSICSTROP = 0x42;
    public static final int MUSICSTART = 0x43;
    public static final int MUSICINDEX = 0x45;

    public static final int MSG_CHANNEL = 0x50;
    public static final int MSG_HOME_PAGE = 0x51;
    public static final int MSG_GET_VIDEOLIST = 0x52;
    public static final int MSG_GET_AUDIOLIST = 0x53;
    public static final int MSG_GET_MUSIC_URL = 0x55;

    public static final int NO_MORE_DATA = 0X56;/*下拉刷新没有更多数据*/
}
