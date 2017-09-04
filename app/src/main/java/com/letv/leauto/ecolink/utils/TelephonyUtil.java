package com.letv.leauto.ecolink.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by why on 2016/7/5.
 */
public class TelephonyUtil {
    private Context mContext;
    private static TelephonyUtil mInstance;

    public TelephonyUtil(Context context) {
        mContext = context.getApplicationContext();
    }

    public static TelephonyUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (TelephonyUtil.class) {
                if (mInstance == null) {
                    mInstance = new TelephonyUtil(context);
                }
            }
        }
        return mInstance;
    }

    public boolean isTelephonyCalling() {
        boolean calling = false;
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (TelephonyManager.CALL_STATE_OFFHOOK == telephonyManager.getCallState() || TelephonyManager.CALL_STATE_RINGING == telephonyManager.getCallState()) {
            calling = true;
        }
        return calling;
    }
}
