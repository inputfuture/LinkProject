package com.letv.leauto.ecolink.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

/**
 * Created by Jerome on 2017/7/13.
 * <p>
 * 早播报TTS之前request  AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK ,来让第三方music 音量降低
 * TTS播放完需要abandonFocus
 *
 * @return
 */

public class TTSAudioFocusManager implements OnAudioFocusChangeListener {

    private static TTSAudioFocusManager mInstance = null;
    private AudioManager mAudioManager;
    private Context mContext;


    public static TTSAudioFocusManager getInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        synchronized (TTSAudioFocusManager.class) {
            if (mInstance == null) {
                mInstance = new TTSAudioFocusManager(context);
            }
        }
        return mInstance;
    }

    private TTSAudioFocusManager(Context context) {
        this.mContext = context;
        mAudioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 请求获取音频焦点（在开始播报TTS之前需要调用）
     * <p>
     * AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK 让第三方降低音量
     *
     * @return
     */
    public boolean requestDuckFocus() {
        int result = mAudioManager
                .requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result;
    }

    /**
     * 释放音频焦点（在退出播放需要调用）
     *
     * @return
     */
    public boolean abandonFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager
                .abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
    }
}
