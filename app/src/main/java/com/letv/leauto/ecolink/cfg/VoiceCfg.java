package com.letv.leauto.ecolink.cfg;

/**
 * Created by liweiwei on 16/3/22.
 */
public class VoiceCfg {
    //开启语音请求
    public static final int START_VR = 0x100;

    //百度语音搜索传入参数字段
    public static final String EXTRA_NLU = "nlu";
    public static final String EXTRA_PROP = "prop";
    public static final String VOICE_RECOGNITION = "VOICE_RECOGNITION";
    public static final String EXTRA_SOUND_START = "sound_start";
    public static final String EXTRA_SOUND_END = "sound_end";
    public static final String EXTRA_SOUND_SUCCESS = "sound_success";
    public static final String EXTRA_SOUND_ERROR = "sound_error";
    public static final String EXTRA_SOUND_CANCEL = "sound_cancel";
    public static final String EXTRA_INFILE = "infile";

    //领域:music,map,contact,other
    public static final String  RESULT_DOMAIN = "result_domain";

    public static final String   DOMAIN_MUSIC = "music";
    public static final String   DOMAIN_MAP = "map";
    public static final String   DOMAIN_CONTACT= "contact";
    public static final String   DOMAIN_OTHER = "other";

    //某一领域的意图:music包括play;map包括nearBy
    public static final String  RESULT_INTENTION = "result_intention";
    public static final String  RESULT_KEYWORD = "result_keyword";

    public static final int MSG_GET_DATA_SUCCESS = 0x601;
    public static final int MSG_GET_DATA_FAILED = 0x602;


}
