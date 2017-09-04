package com.letv.leauto.ecolink.utils;

/**
 *
 */
public class Constants {
    //传值
    public static String PID="pid";
    public static String PLAY_TYPE="playType";
    public static String CHANNEL_FOCUS="ChannelFocus";

    //share preference 相关
    public  final static String DEFAULT_NAME = "com.letv.android.lefm.preferences";
    public  final static String PREF_LAST_RECORD = DEFAULT_NAME + "_last_record";

    public  final static String MEDIA_TYPE = "media_type";
    public  final static String PLAY_LOOP_ORDER = "play_loop_order";
    public  final static String LAST_PLAY_INDEX = "last_play_index";

    //播放类型
    public  final static int MEDIA_TYPE_FM = 0;
    public  final static int MEDIA_TYPE_MEDIA = 1;
    public  final static int MEDIA_TYPE_LIVE = 2;
    // QueueItem bundle key
    public  final static String MEDIA_INFO_ALBUM_ID = "pid";
    public  final static String MEDIA_INFO_MEDIA_ID = "mediaid";
    public  final static String MEDIA_INFO_PLAY_TYPE = "playtype";
    public  final static String MEDIA_INFO_DURATION = "duration";
    public  final static String MEDIA_INFO_ALBUM_NAME = "albumName";
    public  final static String MEDIA_INFO_MEDIA_TYPE = "mediaType";
    public  final static String MEDIA_INFO_SOURCE_NAME = "sourceName";
    public  final static String MEDIA_INFO_INDEX = "index";
    //自定义PlaybackManager动作
    public static final String CUSTOM_ACTION_THUMBS_UP = "com.letv.android.lefm.THUMBS_UP";
    public static final String CUSTOM_ACTION_QUIT = "com.letv.android.lefm.QUIT";
    public static final String CUSTOM_ACTION_CHANGE_ORDER = "com.letv.android.lefm.CHANGE_ORDER";
    public static final String CUSTOM_ACTION_PLAY_ALBUM_ITEM_VIDEO = "com.letv.android.lefm.PLAY_ALBUM_ITEM_VIDEO";
    public static final String CUSTOM_ACTION_PLAY_ALBUM_ITEM_AUDIO = "com.letv.android.lefm.PLAY_ALBUM_ITEM_AUDIO";
    public static final String CUSTOM_ACTION_PLAY_ALBUM_ITEM_TOPIC = "com.letv.android.lefm.PLAY_ALBUM_ITEM_TOPIC";

    public static final String CUSTOM_ACTION_JUMP_TO_INDEX = "com.letv.android.lefm.JUMP_TO_INDEX";

    public static final String ALBUM_ITEM_KEY = "channelFocus";
    public static final String JUMP_INDEX_KEY = "jumpToIndex";

    //专辑类型
    public final static String MEDIA_PLAY_TYPE_VIDEOLIST = "1";
    public final static String MEDIA_PLAY_TYPE_VIDEO = "2";
    public final static String MEDIA_PLAY_TYPE_TOPICLIST = "4";
    public final static String MEDIA_PLAY_TYPE_AUDIOLIST = "10";
    public final static String MEDIA_PLAY_TYPE_AUDIO = "7";

    //播放顺序
    public final static int ORDER_LIST_REPEAT = 0;
    public final static int ORDER_SINGLE_REPEAT = 1;
    public final static int ORDER_RANDOM_PLAY = 2;
    public static final String CHANGE_ORDER_KEY = "changeOrder";
    //HomeActivity 初始化相关
    public final static String CONTENT_LIST = "content";
    public final static String CONTENT_POSITION = "position";
    public final static String CONTENT_REPORT_ID = "reportid";
    public final static String ORDER_ID_KEY = "service_order_id";

    //曲目类型
    public final static String TYPE_AUDIO = "0";
    public final static String TYPE_VIDEO = "1";

    //大数据上报参数
    public static final String APP_AGNES_NAME = "YIDAO_RSE_Leting";

    public static final String PAGE_ID = "pageId";
    public static final String PAGE_UUID = "page_uuid";
    public static final String ORDER_ID = "orderId";
    public static final String ORDER_TYPE = "orderType";
    public static final String TOTAL_TIME = "totalTime";
    public static final String RADIO_ID = "radioId";
    public static final String CHANNEL_ID = "channelId";
    public static final String BUTTON_NAME = "buttonName";
    public static final String AUDIO_ID = "audioId";

    public static final String WIDGET_ID_BUTTON_BACK = "1.b";
    public static final String WIDGET_ID_HOMEPAGE = "1";
    public static final String WIDGET_ID_PLAY_MUSIC = "P.1";
    public static final String WIDGET_ID_MUSIC_DETAIL = "p.2";
    public static final String WIDGET_ID_MUSIC_LIST = "L.1";

    public static final String WIDGET_EVENT_TYPE_CLICK = "click";
    public static final String WIDGET_EVENT_TYPE_EXPOSE = "expose";
    public static final String WIDGET_EVENT_TYPE_MOVE = "move";
    public static final String APP_EVENT_TYPE_EXIT = "exit";
    public static final String APP_EVENT_TYPE_EXIT_FROM_HOME = "exitHome";

    public static final String HOMEPAGE_ID = "1.0";
    //订单类型 -- 易道模式
    public static final String TYPE_YIDAO = "0";


    public static final int PLAY_TYPE_CDE = 0;
    public static final int PLAY_TYPE_EXT = 1;

}
