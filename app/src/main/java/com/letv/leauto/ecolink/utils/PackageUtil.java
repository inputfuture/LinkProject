package com.letv.leauto.ecolink.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by why on 2016/8/19.
 */
public class PackageUtil {
    public static boolean ApkIsInstall(Context context, String  packageName){
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if(packageInfo ==null){
            return false;
        }else{
           return true;
        }

    }
}
