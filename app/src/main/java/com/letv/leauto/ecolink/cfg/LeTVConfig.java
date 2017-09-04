/**
 * Copyright 2013 LeTV Technology Co. Ltd., Inc. All rights reserved.
 *
 * @Author : xiaqing
 * @Description :
 */

package com.letv.leauto.ecolink.cfg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.letv.mobile.core.utils.ContextProvider;
import com.letv.mobile.core.utils.FileUtils;

import java.io.File;

/**
 * TODO(xiaqing): We should consider the devices with pluggable SD card.
 * This class provide a common configure information for base project.
 * @author xiaqing
 */
public final class LeTVConfig {
    // TODO(qingxia): Maybe we should use this definition later.
    // private static final String COMPANY_PATH = "/letv/";
    // Define the base directory by package name.
    private static String sPackageName = "letv";
    // Global working base path
    private static String sWorkingPath = null;
    // Path information
    // save log path config
    public static final String LETV_FILE_LOGGER_PATH = "/log/";
    public static final String LETV_ERROR_PATH = "/errorLog/";
    // download path
    private static final String LETV_DOWNLOAD_PATH = "/download/";
    // Global download path
    private static final String GLOBAL_DOWNLOAD_PATH = "/ledown/";
    // player log path
    private static final String LETV_PLAYER_LOG_PATH = "/player_log/";
    // image cache path
    public static final String IMAGE_CACHE_DIR = "/.image/";
    // NOTE(qingxia): In letv devices, we should put a empty .nomedia file to
    // base application directory to prevent the media server scanning this
    // directory.
    private static final String LETV_NOT_CHECK_MEDIA_FILE = "/.nomedia";
    // 统一缓存路径
    private static final String CACHE_OF_EUI = ".CacheOfEUI/";
    // 统一log路径
    private static final String LOG_OF_EUI = ".LogOfEUI/";
    // The backup folder for the log files, stay in the same directory with the log folders
    // Log备份文件夹名称,与error log和player log在同一目录下
    private static final String LETV_BACKUP_LOG_PATH = "/backup/";

    /**
     * @param packageName
     */
    public static void init(String packageName) {
//        if (Utils.isStringEmpty(packageName)) {
//            return;
//        }
        sPackageName = packageName;

        initAllDir();
    }

    /**
     * init all dir if not exits
     */
    private static void initAllDir() {
        // NOTE(qingxia): In letv devices, we should put a empty .nomedia file
        // to base application directory to prevent the media server scanning
        // this directory.
        FileUtils.createFile(LeTVConfig.getNoMediaFilePath());

        // Initialize all working directory.
        String[] allDir = new String[] { getImageCachePath(), getErrorLogPath(),
                getPlayerLogPath(), getDownloadPath() };
        for (String dirPath : allDir) {
            FileUtils.createDir(dirPath);
        }

        FileUtils.createFile(LeTVConfig.getDownloadNoMediaFilePath());
    }

    /**
     * get image cache path
     * @return if sdcard doesn't exist ,return null. otherwise return
     *         sdcard Path + /letv/.image/
     */
    public static String getImageCachePath() {
        return getGlobalWorkingPath() + CACHE_OF_EUI + sPackageName
                + IMAGE_CACHE_DIR;
    }

    public static String getErrorLogPath() {
        return getGlobalWorkingPath() + LOG_OF_EUI + sPackageName
                + LETV_ERROR_PATH;
    }

    private static String getNoMediaFilePath() {
        return getGlobalWorkingPath() + CACHE_OF_EUI + sPackageName
                + LETV_NOT_CHECK_MEDIA_FILE;
    }

    /**
     * 获取下载目录ledown隐藏文件路径
     * @return
     */
    public static String getDownloadNoMediaFilePath() {
        return getDownloadPath() + LETV_NOT_CHECK_MEDIA_FILE;
    }

    public static String getAppRoot() {
        return getGlobalWorkingPath() + CACHE_OF_EUI + sPackageName;
    }

    public static String getDownloadPath() {
        // TODO 目前放在全局目录下 目的是让其他App(本地视频App)搜索到，若不需要被其他app搜索到可使用 sAppName
        // +GLOBAL_DOWNLOAD_PATH
        // 乐视视频保留下载视频下载路径，不做缓存路径修改
        return getGlobalWorkingPath() + GLOBAL_DOWNLOAD_PATH;
    }

    public static String getPlayerLogPath() {
        return getGlobalWorkingPath() + LOG_OF_EUI + sPackageName
                + LETV_PLAYER_LOG_PATH;
    }

    /**
     * 获取Backup文件夹的绝对路径
     * @return the absolute path of the Backup folder
     */
    public static String getBackupLogPath() {
        return getGlobalWorkingPath() + LOG_OF_EUI + sPackageName
                + LETV_BACKUP_LOG_PATH;
    }

    /**
     * Get and initialize global application working path.
     * @return
     */
    public static String getGlobalWorkingPath() {
        if (sWorkingPath == null) {
            String str = getSDPath();
            if (str == null) {
                return getNoSdCardPath();
            }

            // Initialize base working directory.
            File sdDir = new File(str);
            if (sdDir.canWrite()) {
                sWorkingPath = str;
            } else {
                return getNoSdCardPath();
            }
        }

        // TODO(qingxia): May be we should add company name later.
        // return sWorkingPath + COMPANY_PATH;
        return sWorkingPath;
    }

    /**
     * Get global sd card path
     * @return
     */
    public static String getSDPath() {
        File sdDir = null;
        if (Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState())) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir == null ? null : sdDir.toString() + "/";
    }

    @SuppressLint("SdCardPath")
    public static String getNoSdCardPath() {
        return ContextProvider.getApplicationContext().getFilesDir().toString()
                + File.separator;
    }

    private static class ImgCacheFileHolder {
        private static final File imgCacheSDCardFile = new File(
                getImageCachePath());

        private static final File imgCacheMemoryFile = new File(
                getNoSdCardPath() + IMAGE_CACHE_DIR);
    }

    /**
     * @return a File for img cache path in sdcard. The File is single instance
     */
    public static File getImgCacheSDCardFileInstance() {
        return ImgCacheFileHolder.imgCacheSDCardFile;
    }

    /**
     * @return a File for img cache path in memory.The File is single instance
     */
    public static File getImgCacheMemoryFileInstance() {
        return ImgCacheFileHolder.imgCacheMemoryFile;
    }

    public static String getPackageName() {
        return sPackageName;
    }

    /**
     * get external download dir
     * @param context
     * @return
     */
    public static File getExternalDownloadDir(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        boolean isSDCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (!isSDCardExist) {
            return null;
        }
        final File externalCacheDir = new File(getAppRoot(),
                LETV_DOWNLOAD_PATH);
        if (!externalCacheDir.exists()) {
            externalCacheDir.mkdirs();
        }
        return externalCacheDir;
    }

    /**
     * get external download file
     * @param context
     * @param name
     * @return
     */
    public static File getExternalDownloadFile(Context context, String name) {
        File dir = getExternalDownloadDir(context);
        if (dir == null) {
            return null;
        }
        return new File(getExternalDownloadDir(context), name);
    }

    /**
     * Returns the absolute path to the cache file on the external filesystem
     * @param context
     *            Global information about an application environment
     * @param name
     *            File name
     * @return Returns the path of the cache file on external storage. Returns
     *         null if
     *         external storage is not currently mounted.
     */
    public static File getExternalCacheFile(Context context, String name) {
        File dir = getExternalCacheDir(context);
        if (dir == null) {
            return null;
        }
        return new File(getExternalCacheDir(context), name);
    }

    /**
     * Returns the absolute path to the cache file on the filesystem. These
     * files will be
     * ones that get deleted first when the device runs on storage.
     * @param context
     *            Global information about an application environment
     * @param name
     *            File name
     * @return Returns the absolute path to the application specific cache
     *         directory on the
     *         filesystem. Returns null if external storage is not currently
     *         mounted.
     */
    public static File getInternalCacheFile(Context context, String name) {
        File dir = context.getCacheDir();
        if (dir == null) {
            return null;
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(context.getCacheDir(), name);
    }

    /**
     * Returns a boolean indicating whether this file can be found on cache
     * @param context
     * @param fileName
     * @return
     */
    public static boolean isFileExistInCache(Context context, String fileName) {
        File file = getExternalCacheFile(context, fileName);
        if (file != null && file.exists()) {
            return true;
        }
        file = getInternalCacheFile(context, fileName);
        return file != null && file.exists();
    }

    /**
     * Returns the absolute path to the directory on the external filesystem
     * (that is
     * somewhere on {@link Environment#getExternalStorageDirectory()
     * Environment.getExternalStorageDirectory()} where the application can
     * place cache
     * files it owns.
     * <p/>
     * This is like {@link Context#getCacheDir()
     * Context.getCacheDir()} in that these files will be deleted when the
     * application is uninstalled.
     * <p/>
     * <b>API level 8+</b>:
     * {@link Context#getExternalCacheDir()
     * Context.getExternalCacheDir()}
     * @param context
     *            Global information about an application environment
     * @return Returns the path of the directory holding application cache files
     *         on external
     *         storage. Returns null if external storage is not currently
     *         mounted so it
     *         could not ensure the path exists; you will need to call this
     *         method again
     *         when it is available.
     * @see Context#getCacheDir
     */
    public static File getExternalCacheDir(Context context) {
        boolean isSDCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (!isSDCardExist) {
            return null;
        }
        final File externalCacheDir = new File(getAppRoot(), "/download/");
        if (!externalCacheDir.exists()) {
            externalCacheDir.mkdirs();
        }
        return externalCacheDir;
    }

    private LeTVConfig() {
    }
}