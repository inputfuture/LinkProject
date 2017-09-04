package com.letv.leauto.ecolink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.leplayer.model.LTStatus;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by shimeng on 14/3/29.
 */
public class SmsReceiver extends BroadcastReceiver {
    /*听音乐的时候接入短信，歌曲停止 ，接着放*/

    public static final String TAG = "SmsReceiver";
    public LePlayer player;
    LTStatus mPlayStatus;
    private static final int MSG_STATUS_DELAY_CHECK = 0x01;
    private static final int MSG_STREAM_VOLUME = 0x02;

    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_STATUS_DELAY_CHECK: {
                    LTStatus cplayStatus = player.getCurrentStatus();
                   Trace.Info(TAG, "MSG_STATUS_DELAY_CHECK ---->cplayStatus.isPlaying=" + cplayStatus.isPlaying);
                    handler.removeMessages(MSG_STATUS_DELAY_CHECK);
                    if (!cplayStatus.isPlaying && mPlayStatus.isPlaying&&!BaseActivity.isVoice&&!BaseActivity.isStoped) {
                        player.startPlay();
                        Trace.Debug("####start");
                    } else if (cplayStatus.isPlaying && mPlayStatus.isPlaying) {
                       Trace.Info(TAG, "do nothing");
                        handler.sendEmptyMessageDelayed(MSG_STATUS_DELAY_CHECK, 500);
                    } else {
                        handler.sendEmptyMessageDelayed(MSG_STATUS_DELAY_CHECK, 500);
                    }
                }
                break;
            }
        }
    };


    @Override
    public void onReceive(Context context, Intent intent) {
        player = EcoApplication.LeGlob.getPlayer();
        player.openServiceIfNeed();
       Trace.Info(TAG, "Action ---->" + intent.getAction());
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            mPlayStatus = player.getCurrentStatus();
        }
    }

}
