package com.letv.leauto.ecolink.http;

import android.content.Context;
import android.text.TextUtils;

import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.mobile.core.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin on 2016/12/21.
 */
public class CommonHeader extends HashMap<String, String> {

    private static final String TAG = CommonHeader.class.getSimpleName();
    /* APP版本 */
    private static final String HEADER_APPVERSION = "ver";
    /*Mac地址*/
    private static final String HEADER_MAC = "mac";
    /* 平台 */
    private static final String HEADER_TERMINAL = "platform";
    /* 设备ID */
    private static final String HEADER_DEVID = "did";
    /*设备IMEI号*/
    private static final String HEADER_IMEI = "imei";

    /*app*/
    private static final String HEADER_APP = "appid";


    private Context mContext;

    public CommonHeader(Context ctx) {
        mContext = ctx;
        String appVersion = SystemUtil.getVersionName(mContext);
        this.put(HEADER_APPVERSION, appVersion);
        this.put(HEADER_DEVID, DeviceUtils.getDeviceId(mContext));
        this.put(HEADER_IMEI, DeviceUtils.getDeviceId(mContext));
        this.put(HEADER_TERMINAL, "android");
//        this.put(HEADER_MAC, SystemUtil.getMacAddress(mContext));
        this.put(HEADER_APP, "leecolink_car");
    }

    public void addHeader(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        this.put(key, value);
    }

}
