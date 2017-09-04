package com.letv.leauto.ecolink.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;


import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.ui.view.EcoDialog;


/**
 * Created by fuqinqin on 2016/9/9.
 */
public class PermissionUtils {

    public  interface permissionCB{
         void onCancelled(String permission);
        void onGoToSetting(String permission);
        void onSetSuccess(String permission);

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

    private static void showDialog(final Context context) {
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

     public static boolean checkPermission(Context context, String permission) {
         boolean result = false;
        if( ActivityCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED){
            showDialog(context);
        }
        else {

        }
        return result;
     }
}
