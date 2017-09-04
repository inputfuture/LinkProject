package com.letv.leauto.ecolink.umeng;

/**
 * Created by liweiwei on 16/4/27.
 */
public class AnalyzeManager {

    //事件
    public static class Event {
        public static String MUSIC_PLAY = "music_play";
        public static String MUSIC_FAVOR = "music_favor";
        public static String MAP = "map";
        public static String SETTING = "setting";
        public static String VOICE = "voice";
    }

    //音乐参数
    public static class MusicPara {
        public static String AUDIO_ID = "audio_id";
        public static String ALBUM_ID = "album_id";
        public static String MUSIC_CP = "music_cp";
        public static String MUSIC_TYPE = "music_type";
        public static String MUSIC_NAME = "music_name";
    }

    //地图参数
    public static class MapPara {
        public static String KEY_WORD = "key_word";
        public static String DEVICE_ID = "device_id";

    }

    //设置参数
    public static class SetPara {
        public static String SCREEN_OPEN = "screen_open";
        public static String DEVICE_ID = "device_id";
        public static String SCREEN_DISPLAY = "screen_display";
    }

    //语音参数
    public static class VoicePara {
        public static String KEY_WORD = "key_word";
        public static String DEVICE_ID = "device_id";
    }

}
