package com.letv.auto.keypad.util;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luzhiyong on 15-2-11.
 */
public class AndroidUtils {

    public final static String ACCOUNT_TYPE = "com.letv";

    private static Map<String, String> mUserAgentHeader;

    /**
     * 初始化 user-agent
     *
     * @param context
     * @param msg
     */
    public static void initUserAgentHeader(final Context context, String msg) {
        StringBuilder sb = new StringBuilder();

        sb.append("Loner/");
        sb.append(Build.MODEL);
        sb.append("(");
        sb.append(Build.DISPLAY);
        sb.append(";");
        sb.append("letvauto ");
        sb.append(getAppVersionName(context));
        sb.append(";");
        sb.append(msg == null ? "" : msg);
        sb.append(")");

        mUserAgentHeader = new HashMap<String, String>();
        mUserAgentHeader.put("User-Agent", sb.toString());
    }

    public static final Map<String, String> getUserAgentHeader() {
        return mUserAgentHeader;
    }

    public static final String getUserAgent() {
        return mUserAgentHeader.get("User-Agent");
    }

    public static final String getAppVersionName(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignore) {
        }

        return "";
    }

    public static final int getAppVersionCode(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException ignore) {
        }

        return -1;
    }

    public static boolean hasPermanentKey() {
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (hasBackKey && hasHomeKey) {
            return false;
        } else {
            return true;
        }
    }

    public static ComponentName getTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null) {
            return tasks.get(0).topActivity;
        }
        return null;
    }

    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    public static boolean hasLetvAuthenticator(Context context) {
        AuthenticatorDescription[] allTypes = AccountManager.get(context).getAuthenticatorTypes();

        for (AuthenticatorDescription authenticatorType : allTypes) {
            if (ACCOUNT_TYPE.equals(authenticatorType.type)) {
                return true;
            }
        }
        return false;
    }

    private AndroidUtils() {}
}
