package com.letv.leauto.ecolink.receiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.service.ScreenRotationService;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.voicehelp.eventbus.EventBusHelper;

/**
 * Created by why on 2016/8/11.
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {
    private String action = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
//            GlobalCfg.isScreenOff = false;
            /**
             * 屏幕变亮，判断当前是处于解锁状态还是未锁屏状态
             */
            if(!isScreenLock(context)){
                screenUnLock(context);
            }

//                mScreenStateListener.onScreenOn();
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            Intent intetn = new Intent(context, ScreenRotationService.class);
            intetn.putExtra(ScreenRotationService.ROTATION, ScreenRotationService.COMMAND_PORTRAIT);
            context.startService(intetn);

            GlobalCfg.isScreenOff = true;
            DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_SCREEN_LOCK);
//                mScreenStateListener.onScreenOff();
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁

            screenUnLock(context);
//                mScreenStateListener.onUserPresent();
        }
    }

    /**
     * 屏幕解锁了
     * @param context
     */
    private void screenUnLock(Context context){
        if (CacheUtils.getInstance(context).getBoolean(SettingCfg.Land,false)){
            Intent intetn = new Intent(context, ScreenRotationService.class);
            intetn.putExtra(ScreenRotationService.ROTATION, ScreenRotationService.COMMAND_LANDSCAPE);
            context.startService(intetn);
        }

            GlobalCfg.isScreenOff = false;

            /** 应用回到HOME界面，通知车机处于后台*/
            if (GlobalCfg.isAppHomeState) {
                DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_BACKGROUND);
            }

            /** 应用非三方app状态下，由于其它原因处于后台*/
            if (!GlobalCfg.IS_THIRD_APP_STATE && GlobalCfg.isAppBackground) {
                DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_BACKGROUND);
            }
        DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_SCREEN_UNLOCK);

        /** 应用处于三方app状态下，且没有处于后台，通知车机跳到相应界面 */
        if (GlobalCfg.IS_THIRD_APP_STATE && !GlobalCfg.isAppBackground) {
            EventBusHelper.post(Constant.NOTIFY_CURRENT_PAGE);
        }
    }

    /**
     * 判断手机是否锁屏，
     * @return false:没有锁屏
     *          true:锁屏
     *
     *          如果flag为true，表示有两种状态：a、屏幕是黑的  b、目前正处于解锁状态  。如果flag为false，表示目前未锁屏
     */
    public boolean isScreenLock(Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();

//        if (flag) {
//            flag = isCurrentAppScreenState(context);
//        }

        GlobalCfg.isScreenOff = flag;
        return flag;
    }
}