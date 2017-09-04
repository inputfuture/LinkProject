package com.letv.leauto.ecolink.ui.view;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.leauto.link.lightcar.ScreenRecordActivity;
import com.leauto.link.lightcar.ScreenRecorderManager;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.receiver.ScreenBroadcastReceiver;
import com.letv.leauto.ecolink.thincar.ThinCarIAOACallback;
import com.letv.leauto.ecolink.ui.base.BaseActivity;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 这个NotifyAppStateActivity专用于瘦车机判断当前应用是否进入后台
 * 并发消息给瘦车机
 *
 * Created by Administrator on 2016/12/15.
 */
public abstract class  NotifyAppStateActivity extends BaseActivity {
    Handler mHandler = new Handler();

    protected int pageIndex;
    protected ThinCarIAOACallback mThinCarIAOACallback;
    private ScreenBroadcastReceiver mScreenReceiver;
    private UsbStateReceiver mUsbStateReceiver;
    protected boolean isAoaRecordSuccess = false;

    /**
     * 这个广播用来监测HOME键
     */
    private final BroadcastReceiver mHomeKeyReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                LogUtils.i("NotifyAppStateActivity","reason:"+reason);
                if (reason != null
                        && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    GlobalCfg.isAppHomeState = true;
                    DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_HOME_PRESSED,0,0);
                    if (GlobalCfg.IS_THIRD_APP_STATE) {
                        Intent intent1 = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                        context.getApplicationContext().startActivity(intent1);
                    }
                }
            }
        }
    };

    private class UsbStateReceiver extends  BroadcastReceiver {

        /**
         * 监听USB连接状态
         */
        public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
        public static final String USB_CONNECTED = "connected";
        public static final String USB_FUNCTION_ADB = "adb";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.i("UsbStateReceiver", "action:" + action);
            if (GlobalCfg.isChooseAppState) {
                return;
            }

            if (ACTION_USB_STATE.equals(action)) {
                boolean connect = intent.getBooleanExtra(USB_CONNECTED, false);
                boolean adb = intent.getBooleanExtra(USB_FUNCTION_ADB, false);
                boolean accessory = intent.getBooleanExtra("accessory", false);
                if (!adb) {
                    if (connect) {
                        if (!isAoaRecordSuccess) {
                            startRecordActivity(true);
                        }
                        isAoaRecordSuccess = false;
                    } else {
                        if (mThinCarIAOACallback != null) {
                            mThinCarIAOACallback.onAoaConnectStateChange(ThinCarDefine.ConnectState.STATE_DISCONNECT);
                        }
                    }
                } else {
                    if (connect) {
                        startRecordActivity(false);
                    }
                }
            }
        }

        private void startRecordActivity(boolean isThincar) {
            Intent intent = new Intent(mContext, ScreenRecordActivity.class);
            if (isThincar) {
                intent.setAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
            } else {
                intent.setAction(ScreenRecordActivity.NORMAL_START_ACTIVITY_ACTION);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScreenRecorderManager.getScreenRecorderManager(mContext).resumeThincarScreenData();

        GlobalCfg.isAppHomeState = false;
//        if (!GlobalCfg.isScreenOff) {
            GlobalCfg.isAppBackground = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_FORGROUND);
                }
            },300);

//        }

        if(GlobalCfg.IS_THIRD_APP_STATE) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (GlobalCfg.isCarResumed) {
                        DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_CHANGE_HALF_MODE_PARAM,0,0);
                    } else {/** 不是车机点击让应用来到前台，有可能是应用按返回键回来的*/
                        notifyCurrentPageIndex();
                    }
                    GlobalCfg.isCarResumed = false;
                    GlobalCfg.IS_THIRD_APP_STATE = false;
                }
            },300);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isCurrentAppBackground(this)) {
            GlobalCfg.isAppBackground = true;
            if (!GlobalCfg.IS_THIRD_APP_STATE) {
                DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_BACKGROUND);
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        registerBroadcast();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        unRegisterBroadcast();
    }

    private void unRegisterBroadcast(){
        try {
            unregisterReceiver(mHomeKeyReceiver);
            unregisterReceiver(mScreenReceiver);
            unregisterReceiver(mUsbStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerBroadcast() {
        /** 监听HOME */
        IntentFilter homeFilter = new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyReceiver, homeFilter);

        /** 监听锁屏 */
        mScreenReceiver=new ScreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);

        /** 监听usb状态*/
        mUsbStateReceiver = new UsbStateReceiver();
        IntentFilter usbFilter = new IntentFilter(UsbStateReceiver.ACTION_USB_STATE);
        filter.setPriority(1000);
        registerReceiver(mUsbStateReceiver, usbFilter);
    }

    /**
     * 判断当前应用是否进入后台
     * @param context
     * @return
     */
    public boolean isCurrentAppBackground(Context context) {
        boolean result = false;
        try {
            Field processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            List<ActivityManager.RunningAppProcessInfo> list = ((ActivityManager) (context
                    .getSystemService(Context.ACTIVITY_SERVICE))).getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo:list) {
                if (processInfo.processName.equals(context.getPackageName()) &&
                        processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {

        }

        return result;
    }

    public void setCurrentPageIndex(int index ) {
        /**
         * 如果当前command作用于地图界面才去更新地图的全屏，半屏，
         * 其它的值过来，暂时忽略。
         */
        pageIndex = index;
    }

    public void notifyCurrentPageIndex() {
        DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,pageIndex,0);
        if (pageIndex == ThinCarDefine.PageIndexDefine.HALF_MAP_PAGE) {
            DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_CHANGE_HALF_MODE_PARAM,0,0);
        } else if (pageIndex == ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE){
            DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_CHANGE_FULL_MODE_PARAM,0,0);
        }
    }

    public boolean isActionForNavi() {
        return mThinCarIAOACallback.isActionForNavi();
    }

    public int getCurrentPid(Context context) {
        int pid = 0;
        try {
            Field processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            List<ActivityManager.RunningAppProcessInfo> list = ((ActivityManager) (context
                    .getSystemService(Context.ACTIVITY_SERVICE))).getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo:list) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    pid = processInfo.pid;
                    break;
                }
            }
        } catch (Exception e) {

        }

        return pid;
    }
}