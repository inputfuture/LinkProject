package com.letv.leauto.ecolink.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;


/**
 * 下载的工具类
 *
 */
public class DownloadUtils {

    private static final String TAG = "DownloadUtils";

    public static String getDownLoadApkName(String version){
        return "Ecolink_"+version+".apk";
    }

    public static String getApkPath(){
        return Environment.getExternalStorageDirectory()+ File.separator + "Ecolink" + File.separator;
    }

    public static void installApk(Context context , String path){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
