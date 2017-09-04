package com.letv.leauto.ecolink.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * @author  wanghuayan
 * 强制横屏service
 *
 */
public class ScreenRotationService extends Service {

    // 强制横屏的command
    public static final int COMMAND_LANDSCAPE = 1;
    public static final int COMMAND_PORTRAIT = 0;
    public static final String ROTATION="rotation";

    private  View mWindowView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {


        WindowManager winowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int command = paramIntent.getIntExtra(ROTATION, 0);
        if (mWindowView != null) {
            winowManager.removeViewImmediate(mWindowView);
        }
        mWindowView = null;

        if (command == COMMAND_LANDSCAPE) {

            // 强制横屏
            Settings.System.putInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
            Settings.System. putInt(this.getContentResolver(),Settings.System. USER_ROTATION, Surface.ROTATION_90);
            WindowParams windowParams = new WindowParams(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mWindowView = new View(this);
            winowManager.addView(mWindowView, windowParams);
        } else {
            CacheUtils.getInstance(getApplicationContext()).putBoolean(SettingCfg.Land,false);
            Settings.System.putInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
            Settings.System. putInt(this.getContentResolver(),Settings.System. USER_ROTATION, Surface.ROTATION_0);
            // 取消强制横屏
            if (mWindowView != null) {
                winowManager.removeViewImmediate(mWindowView);
            }
            mWindowView = null;
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CacheUtils.getInstance(getApplicationContext()).putBoolean(SettingCfg.Land,false);
        WindowManager winowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Settings.System.putInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
        Settings.System. putInt(this.getContentResolver(),Settings.System. USER_ROTATION, Surface.ROTATION_0);
        if (mWindowView != null) {
            winowManager.removeViewImmediate(mWindowView);
        }
        mWindowView = null;
    }
    class WindowParams extends WindowManager.LayoutParams {
        public WindowParams(int paramInt) {
            super(0, 0, WindowManager.LayoutParams.TYPE_TOAST, 8, -3);
            this.gravity = 48;
            this.screenOrientation = paramInt;
        }
    }

}
