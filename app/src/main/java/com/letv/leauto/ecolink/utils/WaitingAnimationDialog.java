package com.letv.leauto.ecolink.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;


import com.letv.leauto.ecolink.R;

import java.lang.ref.WeakReference;

/**
 * Created by fuqinqin on 2016/9/10.
 */
public class WaitingAnimationDialog {
    private static WeakReference<Dialog> mWeakDialog;
    private static WeakReference<Context> mWeakContext;

    public static void show(Context context) {
        if(mWeakContext != null && mWeakContext.get() != null && mWeakContext.get()==context
                && mWeakDialog != null && mWeakDialog.get() != null ){
            if(!mWeakDialog.get().isShowing()){
                mWeakDialog.get().show();
            }
        }else{
            OnKeyListener keyListener = new OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                {
                    if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_SEARCH)
                    {
                        return true;
                    }
                    return false;
                }
            };

            Dialog dialog = new Dialog(context,R.style.NobackDialog);
            dialog.setOnKeyListener(keyListener);
            dialog.setCanceledOnTouchOutside(false);

            if(((Activity)context).isFinishing()) return;
            dialog.show();
            dialog.setContentView(R.layout.widget_waitting_dialog);
            mWeakDialog = new WeakReference<Dialog>(dialog);
            mWeakContext = new WeakReference<Context>(context);
        }
    }

    public static boolean isShow(){
        return mWeakDialog != null;
    }

    public static void close() {
        if(mWeakDialog!=null && mWeakDialog.get() != null && mWeakDialog.get().isShowing()) {
            mWeakDialog.get().dismiss();
        }
    }
}
