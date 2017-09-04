package com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig;

import android.os.Build;

import com.letv.leauto.ecolink.BuildConfig;

/**
 * Created by anfengyang on 16/5/16.
 */
public final class GlobalHttpPathConfig {

    /**
     * 测试服务器地址
     */
    public static final String QA_BASE_URL = "http://test-radio.leautolink.com/api/v2/";
    //public static final String QA_BASE_URL = "http://10.75.42.245:8080/xserver-api-mobile/api/v2/";//channel/data.json?pageid=1003436801&page=1";

    /**
     * 正式环境服务器地址
     */
    public static final String REAL_BASE_URL = "http://radio.leautolink.com/api/v2/";

    /* xserver */
    public static final String XSERVER_MOBILE = "xserver-api-mobile/";

    /* 开关，用于选择测试服务器或者线上服务器 */
    public static final String BASE_URL = BuildConfig.SERVER_DEBUG ? QA_BASE_URL : REAL_BASE_URL;

    /**
     * 获取栏目接口
     */
    public static final String GET_CHANNEL =  "channel/all.json";

    /**
     * 每个栏目下的专辑列表的接口
     */
    public static final String HOME_DYNAMIC_HOMEPAGE = BASE_URL + "channel/data.json";
    /**
    * get topic data
    */
    public static final String PATH_TOPIC_DETAIL = "subject/content/get.json";

    /**
     * get audio data
     */
    public static final String GET_MUSIC_LIST = "audio/play/list.json";//获取音频列表信息
    public static final String  GET_MUSIC_DETAIL = "audio/play/get.json";//获取播放地址,音视频通用

    /**
     * get video data
     */
    public static final String GET_VIDEO_LIST = "video/albumInfo/get.json";//获取视频列表信息


    /**
     * LeRadio万象搜索接口
     */
    public static final String LERADIO_SEARCH = BASE_URL + "query/queryData.json";

}
