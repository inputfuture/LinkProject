package com.letv.leauto.ecolink.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;

import java.text.DecimalFormat;

import static android.content.Context.WINDOW_SERVICE;

//常用单位转换的辅助类
public class DensityUtils {
    private DensityUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }
    /**
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager= (WindowManager) context.getSystemService(WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        return width;
    }


    /**
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager= (WindowManager) context.getSystemService(WINDOW_SERVICE);
        int height = windowManager.getDefaultDisplay().getHeight();
        return height;
    }
    public static String convertSec2Min(long sec) {
        String result = "";
        long min = sec / 60;
        long hour = 0;
        if (min < 60) {
            result = min + EcoApplication.getInstance().getString(R.string.minute);
        } else {
            hour = min / 60;
            min = min % 60;
            result = hour + EcoApplication.getInstance().getString(R.string.hour) + min + EcoApplication.getInstance().getString(R.string.minute);
        }
        return result;

    }

    public static String convertSec2MinSp(int sec) {
        String result = "";
        int min = sec / 60;
        int hour = 0;
        if (min < 60) {
            result = min + "";
        } else {
            hour = min / 60;
            min = min % 60;
            result = hour + ":" + min;
        }
        return result;

    }


    public static String convertMeter2KM(float meter) {
        if(meter >= 1000) {
            double dis = 0;
            dis = Math.round(meter / 100d) / 10d;
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.

            String distanceString = decimalFormat.format(dis);//format 返回的是字符串
            return distanceString + "公里";
        }
        else {
            long dis = 0;
            dis = Math.round(meter);
            return dis+"米";
        }
    }

    public static String convertMeter2KM_2(float meter) {
        double dis = 0;
        dis = Math.round(meter / 100d) / 10d;
        DecimalFormat decimalFormat = new DecimalFormat("#0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String distanceString = decimalFormat.format(dis);//format 返回的是字符串
        return distanceString;
    }

    public static String getMusicCachePath() {
        return Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/LeAuto/Music/";
    }


    public static String convertMeter2KMNoUnit(float meter) {
        double dis = 0;
        dis = Math.round(meter / 10d) / 100d;
        DecimalFormat decimalFormat = new DecimalFormat("#0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String distanceString = decimalFormat.format(dis);//format 返回的是字符串
        return distanceString;
    }
}
