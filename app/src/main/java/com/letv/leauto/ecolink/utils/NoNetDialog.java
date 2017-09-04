package com.letv.leauto.ecolink.utils;

import android.content.Context;
import android.content.Intent;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;

/**
 * Created by chenchunyu on 16/12/26.
 */
public class NoNetDialog {

    public static void show(final Context context){
        Trace.Debug("****** 无网络弹窗");
        final NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(context, R.string.no_net_message,R.string.go_setting,R.string.i_know);
        networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
            @Override
            public void onConfirm(boolean checked) {
                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                context.startActivity(intent);
            }

            @Override
            public void onCancel() {
                networkConfirmDialog.dismiss();
            }

        });
        networkConfirmDialog.show();
    }
}
