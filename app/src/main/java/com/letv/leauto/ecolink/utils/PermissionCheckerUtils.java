package com.letv.leauto.ecolink.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.ui.view.EcoDialog;

/**
 * Created by Vaan on 2017/4/26.
 */

public class PermissionCheckerUtils {

    Context mContext;

    public PermissionCheckerUtils(Context context) {
        mContext = context;
    }

    @SuppressLint("NewApi")
    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;
        int targetSdkVersion = mContext.getApplicationInfo().targetSdkVersion;
        if (targetSdkVersion >= Build.VERSION_CODES.M) {
            // targetSdkVersion >= Android M, we can
            // use Context#checkSelfPermission
            result = mContext.checkSelfPermission(permission)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // targetSdkVersion < Android M, we have to use PermissionCheckerUtils
            result = PermissionChecker.checkSelfPermission(mContext, permission)
                    == PermissionChecker.PERMISSION_GRANTED;
        }

        return result;
    }

    public boolean isSmsCan() {
        return selfPermissionGranted(Manifest.permission.READ_CONTACTS);
    }

    public boolean isAudioGranted() {
        return selfPermissionGranted(Manifest.permission.RECORD_AUDIO);
    }

    public boolean isCameraGranted(){
        return selfPermissionGranted(Manifest.permission.CAMERA);
    }

    public boolean isLocationGranted(){
        return selfPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public boolean isPhoneGranted(){
        return selfPermissionGranted(Manifest.permission.CALL_PHONE);
    }

    public boolean isStorageWriteGranted(){
        return selfPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public boolean isStorageReadGranted(){
        return selfPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    /**
     * open android settings screen for your app.
     */
    private static void openSettingsScreen(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + context.getPackageName());
        intent.setData(uri);
        context.startActivity(intent);
    }

    public  void showDialog(final Context context) {
        EcoDialog dialog = new EcoDialog(context, R.style.Dialog, "权限不够，去设置？");
        dialog.setListener(new EcoDialog.ICallDialogCallBack() {
            @Override
            public void onConfirmClick(EcoDialog currentDialog) {
                openSettingsScreen(context);
                currentDialog.dismiss();
            }

            @Override
            public void onCancelClick(EcoDialog currentDialog) {
                currentDialog.dismiss();
            }

        });
        dialog.show();
    }
}
