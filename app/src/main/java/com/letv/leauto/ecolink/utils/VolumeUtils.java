package com.letv.leauto.ecolink.utils;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by zhaochao on 2015/9/23.
 */
public class VolumeUtils {
    public static void LogVolume(Context ctx, int streamType, int volume) {

        AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        //通话音量
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        Trace.Debug("VIOCE_CALL", "max:" + max + "mCurrentIndex:" + current);
        //系统音量
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        Trace.Debug("SYSTEM", "max:" + max + "mCurrentIndex:" + current);
        //铃声音量
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        Trace.Debug("RING", "max:" + max + "mCurrentIndex:" + current);
        //音乐音量
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Trace.Debug("MUSIC", "max:" + max + "mCurrentIndex:" + current);
        //提示声音音量
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        Trace.Debug("ALARM", "max:" + max + "mCurrentIndex:" + current);
    }

    public static void setMaxVolume(Context ctx, int streamType) {
        AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(streamType);
        mAudioManager.setStreamVolume(streamType, maxVolume, 0); //tempVolume:音量绝对值
    }

    public static void setTtsVolume(Context ctx, int streamType, int flag) {
        AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(streamType);
        mAudioManager.setStreamVolume(streamType, maxVolume, flag); //tempVolume:音量绝对值
    }

    ///返回调整之前的音量
    public static int downMusicVolume(Context ctx, int streamType, int volume) {
        int currentVolume = 0;
        AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = mAudioManager.getStreamVolume(streamType);
        mAudioManager.setStreamVolume(streamType, volume < 0 ? (currentVolume * 2 / 3) : volume, 0);
        return currentVolume;
    }

    public static int getStreamVolume(Context ctx, int streamType) {
        AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        return mAudioManager.getStreamVolume(streamType);
    }

    public static void setStreamVolume(Context ctx, int streamType, int volume) {
        AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = mAudioManager.getStreamMaxVolume(streamType);
        mAudioManager.setStreamVolume(streamType, volume < 0 ? (currentVolume * 2 / 3) : volume, 0);
    }

}
