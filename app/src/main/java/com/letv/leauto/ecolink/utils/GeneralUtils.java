
package com.letv.leauto.ecolink.utils;
import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.letvcloud.cmf.CmfHelper;
import com.letvcloud.cmf.utils.FileHelper;
import com.letvcloud.cmf.utils.StringUtils;

public class GeneralUtils {
    private static final String TAG = "GeneralUtils";
    private static String appId = "751"; // APP ID 每个应用单独分配，参考 http://wiki.letv.cn/pages/viewpage.action?pageId=37330964
    private static long port = 7000L;
    private boolean mIsCdeReady = false;
    private String mIp;
    private int mNetworkType;
    private String mCdeVersion;
    private String mRomVersion;
    private long mCdePort;
    CmfHelper cmfHelper;
    public static boolean isLetvStream(String originalUrl) {
        if (StringUtils.isEmpty(originalUrl)) {
            return false;
        }

        return originalUrl.contains("letv.com") || originalUrl.contains("letv.cn") || originalUrl.contains("video123456.com");
    }

    /**
     * 获取初始化播放器时传入的参数
     * @return
     */
    public static String getInitPlayerParams(Context context) {
        //for test demo,begin
        //int appId = 9;
        //int port = 26990;
        //for test demo,end
        // app_channel=unknown 应用发行渠道
        // test_upgrade=1 升级测试，正式上线时去掉
        // manual_upgrade=1 手动启动后台升级
        // start_after_upgrade=1 升级检测后在启动，开启后manual_upgrade参数无效
        // return "app_id=" + appId + "&port=" + port + "&app_channel=unknown";
        String logFile = context.getDir("datas", Context.MODE_PRIVATE).getAbsolutePath() + "/cde.log";
       // String logFile = FileHelper.getSdPath(context, "log").getAbsolutePath() + File.separator + "cde.log";
        return "app_id=" + appId + "&port=" + port + "&vod_urgent_size=3" + "&app_channel=unknown&log_dir=" + logFile;
    }

}
