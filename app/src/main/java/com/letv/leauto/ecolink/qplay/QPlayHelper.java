package com.letv.leauto.ecolink.qplay;

import android.content.Context;

import com.tencent.qplayauto.device.QPlayAutoSongListItem;

/**
 * Created by why on 2017/2/21.
 */

public class QPlayHelper {
    private static  QPlayHelper mInstance;
    private Context mContext;
    //音乐播放页面歌曲的父ID
    protected String RequstID;
    //当前播放音乐在音乐列表的索引
    protected int PlayIndex = -1;
    //当前播放页面所播放歌曲的item
    protected QPlayAutoSongListItem currSong;
    //当前音乐列表页面音乐的来源名
    protected String SourceStr;
    //当前音乐列表页面音乐列表的item
    protected QPlayAutoSongListItem songItem;
    private QPlayHelper(Context context){
        mContext=context.getApplicationContext();
    }

    public static QPlayHelper getInstance(Context context) {
        synchronized (QPlayHelper.class){
            if (mInstance==null){
                synchronized (QPlayHelper.class){
                    mInstance=new QPlayHelper(context);

                }
            }
        }
        return mInstance;
    }

    public void setSongItem(QPlayAutoSongListItem songItem) {
        this.songItem = songItem;
    }

    public void setRequstID(String requstID) {
        RequstID = requstID;
    }

    public void setSourceStr(String sourceStr) {
        SourceStr = sourceStr;
    }

    public QPlayAutoSongListItem getSongItem() {
        return songItem;
    }

    public String getRequstID() {
        return RequstID;
    }

    public String getSourceStr() {
        return SourceStr;
    }

    public int getPlayIndex() {
        return PlayIndex;
    }

    public void setPlayIndex(int playIndex) {
        PlayIndex = playIndex;
    }

    public QPlayAutoSongListItem getCurrSong() {
        return currSong;
    }

    public void setCurrSong(QPlayAutoSongListItem currSong) {
        this.currSong = currSong;
    }
}
