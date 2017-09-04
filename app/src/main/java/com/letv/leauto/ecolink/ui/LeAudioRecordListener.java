package com.letv.leauto.ecolink.ui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.leauto.sdk.data.OnAudioRecordListener;

/**
 * Created by Administrator on 2016/11/7.
 */
public class LeAudioRecordListener implements OnAudioRecordListener {
    Context mContext;
    public LeAudioRecordListener(Context context) {
        mContext = context;
    }
    @Override
    public void onAudioRecordData(final byte[] bytes) {
        LogUtils.i("LeAudioRecordListener", "onAudioRecordData receiverd!");
        Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,"onAudioRecordData size:" + bytes.length,Toast.LENGTH_SHORT).show();
            }
        });

        DataSendManager.getInstance().writePcmToFile(bytes, 0, bytes.length);
    }
}
