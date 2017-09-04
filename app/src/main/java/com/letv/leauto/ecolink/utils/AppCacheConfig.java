package com.letv.leauto.ecolink.utils;

import android.content.Context;
import android.os.Environment;

import com.letv.leauto.ecolink.cfg.SettingCfg;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by fuqinqin on 2016/9/7.
 */
public class AppCacheConfig {

    public static String getTotalCacheSize(Context context) throws Exception {
        String s = context.getCacheDir().getAbsolutePath();

        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
            String folder2 = context.getExternalCacheDir().getAbsolutePath();
        }
        return getFormatSize(cacheSize);
    }

    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            String result = null;
            if (kiloByte == 0) {
                result = "0KB";
            } else {
                result = size + "Byte";
            }
            return result;
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    //return M unit
    public static long getCacheConfigLimtedSize(Context context) {
        int level = CacheUtils.getInstance(context).getInt(SettingCfg.CACHE_LEVEL, 0);
        long size = 200;
        switch (level) {
            case 0:
                size = 200;
                break;
            case 1:
                size = 300;
                break;
            case 2:
                size = 500;
                break;
            case 3:
                size = 800;
                break;
            case 4:
                size = 1024;
                break;
            default:
                break;
        }
        return size;

    }
}

