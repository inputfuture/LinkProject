package com.letv.leauto.ecolink.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.mobile.core.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liweiwei1 on 2015/12/17.
 */
public class DeviceUtils {
    private static String mMusicPath = null;

    /**
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * @return
     */
    public static int getScreenHeigt(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        return metric.heightPixels;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        //获取手机devices id
        String devicesId = tm.getDeviceId();
        //如果devices id 获取不到，则获取imsi号；
        if(devicesId==null){
            devicesId = tm.getSubscriberId();
        }
        //如果imsi号也获取不到，则返回空格，因为okhttp请求时参数不能填空。
        if(devicesId==null){
            devicesId = " ";
        }
        return devicesId;
    }


    public static String getMusicCachePath() {
        if(mMusicPath == null) {
            mMusicPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()+"/Ecolink/Music/";
        }
        if (!FileUtils.isFileExist(mMusicPath)) {
            FileUtils.createSDDir(mMusicPath);
        }
        return mMusicPath;
    }
    public static String getKuwoMusicCachePath() {
        return Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Ecolink/KuWoMusic/";
    }

    public static String getApkCachePath() {
        return Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Android/data/com.letv.leauto.ecolink/files/Download/";
    }

    /**
     * 弹出键盘
     *
     * @param mContext
     */
    public static void popKeyBoard(Context mContext) {
        InputMethodManager imm =
                (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 键盘关闭
     *
     * @param mContext
     */
    public static void dropKeyBoard(Context mContext, View view) {
        //键盘弹下
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }


    /**
     * 判断当前应用程序是否处于前台
     */
    public static boolean isApplicationBroughtToFront(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return false;
            }
        }
        return true;
    }

    public static String getDeviceName() {
        String devicesNmae = android.os.Build.MANUFACTURER;
        Trace.Info("", "getDeviceName=" + devicesNmae);
        return devicesNmae;
    }

    public static String getDeviceModel() {
        String devicesModel = android.os.Build.MODEL;
        Trace.Info("", "devicesModel=" + devicesModel);
        return devicesModel;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemSDKVersion() {
        return android.os.Build.VERSION.SDK;
    }

    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /* 检查手机上是否安装了指定的软件
    * @param context
    * @param packageName：应用包名
    * @return
            */
    public static boolean isAvilible(Context context, String packageName){
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    public static void  putNaviEndToCache(Context context,String enAddr){
        
        String res = CacheUtils.getInstance(context).getString(Constant.SpConstant.HISTORY_SEARCHKEY, null);
        if (res == null) {
            res = enAddr;
        } else {
            if (!res.contains(enAddr)) {
                res = res + ";" + enAddr;
            } else {
                int index = res.indexOf(enAddr);
                StringBuilder sb = new StringBuilder();
                if (index == 0) {
                    if (!res.equals(enAddr)) {
                        sb.append(res.substring(enAddr.length() + 1));
                        sb.append(";" + enAddr);
                    } else {
                        sb.append(enAddr);
                    }
                } else {
                    if ((index + enAddr.length()) == res.length()) {
                        sb.append(res.substring(0, index));
                        sb.append(enAddr);
                    } else {
                        sb.append(res.substring(0, index));
                        sb.append(res.substring(index + enAddr.length() + 1));
                        sb.append(";" + enAddr);
                    }
                }
                res = sb.toString();
            }
        }
        CacheUtils.getInstance(context).putString(Constant.SpConstant.HISTORY_SEARCHKEY, res);

    }

    //获取屏幕亮度值
    public static int getScreenBrightness(Activity activity) {
        int value = 0;
        ContentResolver cr = activity.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {

        }
        return value;
    }
    //设置屏幕亮度
    public static void setScreenBrightness(Activity activity, int value) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.screenBrightness = value / 255f;
        activity.getWindow().setAttributes(params);
    }

    /**
     * 获取系统睡觉时间
     * @param activity
     */
    public static int getScreenOffTime(Activity activity) {
        ContentResolver cr = activity.getContentResolver();
        int result = 10*60*1000;
        try {
            result = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 设置系统睡觉时间
     * @param activity
     */
    public static void setScreenOffTime(Activity activity,int time) {
        ContentResolver cr = activity.getContentResolver();
        try {
            Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
