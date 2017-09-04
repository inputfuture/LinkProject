package com.tencent.qplayauto.device;

/**
 * Created by agaochen on 2016/9/27.
 * QPlay车载类参数通用类
 */

public class QPlayAutoArguments {

    /**
     * 请求移动设备信息
     */
    public final static int REQUEST_DEVICE_INFOS = 1;
    /**
     * 请求歌曲列表
     */
    public final static int RESPONSE_PLAY_LIST = 2;
    public static class ResponsePlayList
    {
        public int count;
        public String parentID;
        public int pageIndex;
        public QPlayAutoSongListItem[] playList;

        @Override
        public String toString() {
            return "数量："+ count +"，父ID："+ parentID;
        }
    }

    /**
     * 读取专辑图数据
     */
    public final static int REQUEST_ALBUM = 3;
    public static class RequestAlbum
    {
        public String songID;
        public int packageIndex;
    }

    /**
     * 拉取歌词
     */
    public final static int REQUEST_LYRIC = 4;
    public static class RequestLyric
    {
        public int requestID;//因为是异步拉取歌，需要把这个存储起来
        public String songID;
        public int packageIndex;
        public int lyricType;
    }

    /**
     * 获取将要播放歌曲的详细信息
     */
    public final static int RESPONSE_MEDIA_INFOS = 5;
    public static class ResponseMediaInfos
    {
        public String songID;//歌曲ID
        public int PCMTotalLength;//PCM数据总长度
        public int frequency;//采样频率
        public int channel; // 声道
        public int bit; // 采样精度
        public int songDuration;// 歌曲播放总时间
    }

    /**
     * 读取PCM数据
     */
    public final static int REQUEST_PCM = 6;
    public static class RequestPCM
    {
        public String songID;
        public int packageIndex;
    }

    /**
     * 停止传输二进制数据
     */
    public final static int REQUEST_STOP_SEND_DATA = 7;
    public static class RequestStopSendData
    {
        public String songID;
        public int dataType;
    }

    /**
     * 收到播放命令
     */
    public final static int REQUEST_DEVICE_PLAY_PLAY = 8;

    /**
     * 收到暂停命令
     */
    public final static int REQUEST_DEVICE_PLAY_PAUSE = 9;

    /**
     * 收到播放上一首命令
     */
    public final static int REQUEST_DEVICE_PLAY_PRE = 10;

    /**
     * 收到播放下一首命令
     */
    public final static int REQUEST_DEVICE_PLAY_NEXT = 11;

    /**
     * 收到停止命令
     */
    public final static int REQUEST_DEVICE_PLAY_STOP =12;

    /**
     * 收到网络状态命令
     */
    public final static int REQUEST_NETWORK_STATE = 13;

    /**
     * 收到搜索命令
     */
    public final static int RESPONSE_SEARCH = 14;
    public static class ResponseSearch
    {
        public String key;
        public int pageFlag;
        public QPlayAutoSongListItem[] searchList;
    }
    /**
     * 收到断开命令
     */
    public final static int REQUEST_DISCONNECT = 15;
    /**
     * 收到注册播放消息命令
     */
    public final static int REQUEST_REGISTER_PLAY_STATE = 16;
    /**
     * 收到注销播放消息命令
     */
    public final static int REQUEST_UNREGISTER_PLAY_STATE = 17;
    /**
     * 收到读取播放状态命令
     */
    public final static int REQUEST_PLAY_STATE = 18;
    /**
     * 收到读取当前播放歌曲信息命令
     */
    public final static int REQUEST_PLAY_INFOS = 19;
    /**
     * 收到播放歌曲信息
     */
    public final static int REQUEST_PLAY_SONG = 20;
    public static class RequestPlaySong
    {
        public String songID;
        public String parentID;
        int type;
        public String name;
        public String artist;
        public String album;
    }

    public final static int RESPONSE_MOBILE_DEVICE_INFOS = 21;

    public final static int REQUEST_ERROR = 22;
    public static class CommandError
    {
        public String Command;
        public int ErrorNo;
    }
    public final static int RESPONSE_ERROR = 23;

}
