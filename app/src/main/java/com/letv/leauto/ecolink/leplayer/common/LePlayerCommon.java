package com.letv.leauto.ecolink.leplayer.common;

/**
 * Created by liweiwei1 on 2015/12/15.
 */
public class LePlayerCommon {
//    public static final String BROADCAST_ACTION = "com.leauto.leting";
//    public static final String EXTRA_SMG_SEARCHRESULTKEY = "searchresult";
//    public static final String EXTRA_SMG_SEARCHRE_AROUND = "search_around";
//    public static final String EXTRA_SMG_SEARCHRESULTVALUE = "102";
//    public static final String EXTRA_SMG_BACKPRESSED = "103";

    public static final String BROADCAST_ACTION_TTS = "con.leauto.leting.action_tts";
    public static final String BROADCAST_EXTRA_TTS = "con.leauto.leting.extra_tts";
    public static final int BROADCAST_EXTRA_TTS_BEGIN = 501;
    public static final int BROADCAST_EXTRA_TTS_END = 502;

    //shimeng add for cmf server connection broadcast action,20160408,begin
    public static final String CMF_SERVER_CONNECTION_ACTION = "com.leauto.cmf_connection";
    public static final String CMF_SERVER_DISCONNECTION_ACTION = "com.leauto.cmf_disconnection";
    public static final String EXTRA_CMF_CDE_READY = "cdeReady";
    public static final String EXTRA_CMF_LINKSHELL_READY = "linkShellReady";
    public static final String EXTRA_CMF_ERROR_CODE = "errorCode";
    //shimeng add for cmf server connection broadcast action,20160408,end

    public static final String BROADCAST_ACTION_VOICERECORD = "con.leauto.leting.action_voice";
    public static final String BROADCAST_EXTRA_VOICERECORD = "con.leauto.leting.extra_voice";
    public static final int BROADCAST_EXTRA_VOICERECORD_BEGIN = 503;
    public static final int BROADCAST_EXTRA_VOICERECORD_END = 504;
    public static final int BROADCAST_EXTRA_VOICERECORD_QUIT = 505;
}
