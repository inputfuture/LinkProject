package com.letv.leauto.ecolink.http.model;

import android.util.Log;

import com.letv.leauto.ecolink.http.host.LetvAutoHosts;
import com.letv.leauto.ecolink.utils.MD5Util;
import com.letv.leauto.ecolink.utils.PinYinUtil;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by liweiwei on 16/4/25.
 */
public class TrafficReq {

    public static String create(String cityName) {
        String result = "";

        result = LetvAutoHosts.TRAFFIC_URL + cityName;

        Trace.Debug("TrafficReq ", result);
        return result;
    }
}
