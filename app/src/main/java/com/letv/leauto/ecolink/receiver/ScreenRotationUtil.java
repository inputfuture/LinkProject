package com.letv.leauto.ecolink.receiver;

import android.content.Context;
import android.content.Intent;

import com.leauto.sdk.NotifyCommand;
import com.leauto.sdk.SdkManager;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.service.HomeKeyService;
import com.letv.leauto.ecolink.service.ScreenRotationService;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by why on 2016/11/24.
 */
public class ScreenRotationUtil {
    public static void startLandService(Context mContext,String packageName){
        CacheUtils.getInstance(mContext).putBoolean(SettingCfg.Land, true);
        Intent intetn = new Intent(mContext, ScreenRotationService.class);
        intetn.putExtra(ScreenRotationService.ROTATION, ScreenRotationService.COMMAND_LANDSCAPE);
        mContext.startService(intetn);

        Intent serviceIntent = new Intent(mContext, HomeKeyService.class);
        HomeKeyService.appPackgeName=packageName;
        HomeKeyService.isRun=true;
        mContext.startService(serviceIntent);


    }
    public static void stopLandService(Context mContext){
        Intent intent = new Intent(mContext, ScreenRotationService.class);
        intent.putExtra(ScreenRotationService.ROTATION, ScreenRotationService.COMMAND_PORTRAIT);
        mContext.startService(intent);
        CacheUtils.getInstance(mContext).putBoolean(SettingCfg.Land,false);
        Trace.Debug("###stopLandService");



    }



    public static void commandShowPop(Context ct) {
        SdkManager.getInstance(ct).notifyCommand(
                NotifyCommand.COMMAND_SHOW_POP, 0, 0);
    }

    /**
     * 关闭悬浮按钮
     */
    public static void commandHidePop(Context ct) {
        SdkManager.getInstance(ct).notifyCommand(
                NotifyCommand.COMMAND_HIDE_POP, 0, 0);
    }
}
