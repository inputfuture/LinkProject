package com.letv.leauto.ecolink.leplayer;

import android.os.Handler;
import android.os.Message;

/**
 * Created by zhaochao on 2015/8/11.
 */
public abstract class LesongCallback extends Handler {
    public abstract void onResponse(int tagNum, String urlSrc);
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 0:
                onResponse(msg.arg1, (String)msg.obj);
                break;
            case -1:
                onResponse(msg.arg1, null);
                break;
            default:
                onResponse(msg.arg1, null);
                break;
        }
    }
}
