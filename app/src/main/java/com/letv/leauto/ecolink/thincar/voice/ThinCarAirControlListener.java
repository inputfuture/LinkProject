package com.letv.leauto.ecolink.thincar.voice;

import android.content.Context;
import com.leauto.link.lightcar.voiceassistant.VoiceQueryStatusDefine;
import com.leauto.link.lightcar.voiceassistant.VoiceStatusManager;

import com.letv.dispatcherlib.config.Constant;
import com.letv.leauto.ecolink.thincar.protocol.VoiceAssistantHelp;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.voicehelp.manger.air.LeVoiceAirControlManager;
import com.letv.voicehelp.manger.air.LeVoiceAirControlManager.AirControlListener;
import com.leauto.link.lightcar.voiceassistant.VoiceCommandDefine;

/**
 * Created by Administrator on 2017/2/27.
 */

public class ThinCarAirControlListener implements AirControlListener {
    private static final String TAG = "AirControlListener";

    VoiceStatusManager mVoiceStatusManager;

    public ThinCarAirControlListener(Context context) {
        mVoiceStatusManager = VoiceStatusManager.getInstance();
    }

    /**
     * 获取当前空调温度
     */
    @Override
    public int getAirCurrentTem() {
        int currentTep = mVoiceStatusManager.getAirCurrentTem();
        Trace.Info(TAG,"getAirCurrentTem currentTep:" + currentTep);
        return currentTep;
    }


    /**
     * 调整空调温度
     *
     * @param mode
     */
    @Override
    public void changeTemOpt(String mode) {
        Trace.Info(TAG,"changeTemOpt mode:" + mode);
        if (mode.equals(Constant.VEHICLE_AC_TEM_OPT_HIGHER)) {
            VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_HIGHER_TEMP);
        } else if (mode.equals(Constant.VEHICLE_AC_TEM_OPT_LOWER)) {
            VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_LOWER_TEMP);
        }
    }

    /**
     * 制热
     */
    @Override
    public void warm() {
        Trace.Info(TAG,"warm");
        VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_HEAT_MODE);
    }

    /**
     * 制冷
     */
    @Override
    public void cold() {
        Trace.Info(TAG,"cold");
        VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_COOL_MODE);
    }

    /**
     * 设定空调模式
     *
     * @param mode
     */
    @Override
    public void setAcMode(String mode) {
        Trace.Info(TAG,"setAcMode mode:"  + mode);
        if (mode.equals(Constant.VEHICLE_AC_MODE_AUTO)) {
            VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_AUTO_MODE);
        }
    }

    /**
     * 设定温度
     *
     * @param tem 温度
     */
    @Override
    public void changeTem(String tem) {
        Trace.Info(TAG,"changeTem tem:"  + tem);
        try {
            int temp = Integer.parseInt(tem);
            VoiceAssistantHelp.getInstance().sendSetACTempVoiceCommand(VoiceCommandDefine.VOICE_CMD_SET_AC_TEM,temp);
        } catch (Exception e) {

        }
    }

    /**
     * 调整风速
     *
     * @param mode 模式
     */
    @Override
    public void changeWindSpeed(String mode) {
        Trace.Info(TAG,"changeWindSpeed mode:"  + mode);
        if (mode.equals(Constant.VEHICLE_AC_WIND_CAPACITY_HIGHER)) {
            VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_INCREASE_BLOW);
        } else if (mode.equals(Constant.VEHICLE_AC_WIND_CAPACITY_LOWER)) {
            VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_REDUCE_BLOW);
        } else if (mode.equals(Constant.VEHICLE_AC_WIND_CAPACITY_MAX)) {
            VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_MAX_WIND);
        } else if (mode.equals(Constant.VEHICLE_AC_WIND_CAPACITY_MIN)) {
            VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_MIN_WIND);
        }
    }

    /**
     * 调整空调内外循环
     *
     * @param mode
     */
    @Override
    public void changeInletAir(String mode) {
        Trace.Info(TAG,"changeInletAir mode:"  + mode);
        if (mode.equals(Constant.VEHICLE_AC_MODE_INLETAIR)){
            VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_INNER_LOOP);
        }else if (mode.equals(Constant.VEHICLE_AC_MODE_OUTLETAIR)){
            VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_OUTER_LOOP);
        }
    }

    /**
     * 调整风向
     *
     * @param mode
     */
    @Override
    public void changeWindDirection(String mode) {
        Trace.Info(TAG,"changeWindDirection mode:"  + mode);
         if (mode.equals("向"+Constant.VEHICLE_AC_WIND_DIRECTION_UP)) {//向上吹风
             VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_UPWARD_BLOW);
         } else if (mode.equals("向" + Constant.VEHICLE_AC_WIND_DIRECTION_DOWN)) {//向下吹风
             VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_DOWN_BLOW);
         } else if (mode.equals(Constant.VEHICLE_AC_WIND_DIRECTION_LEVEL)) {//水平吹风
             VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_HORIZONTAL_BLOW);
         }
    }

    /**
     * 打开空调
     *
     * @param
     */
    @Override
    public void open() {
        Trace.Info(TAG,"open");
        VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_OPEN_AC);
    }

    /**
     * 关闭空调
     *
     * @param
     */
    @Override
    public void close() {
        Trace.Info(TAG,"close");
        VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_CLOSE_AC);
    }

    /**
     * 获取当前内外循环模式
     */
    @Override
    public String getCurrentInOrOutMode() {
        String mode = Constant.VEHICLE_AC_MODE_INLETAIR;
        int value = mVoiceStatusManager.getCurrentInOrOutMode();
        switch (value) {
            case VoiceQueryStatusDefine.ACWindMode.AC_INNER_WIND:
                mode = Constant.VEHICLE_AC_MODE_INLETAIR;
                break;
            case VoiceQueryStatusDefine.ACWindMode.AC_OUTER_WIND:
                mode = Constant.VEHICLE_AC_MODE_OUTLETAIR;
                break;
        }
        return mode;
    }

    /**
     *  获取空调模式
     * @return
     */
    @Override
    public String getCurrenAcMode() {
        String mode = LeVoiceAirControlManager.AIR_AC_MODE__CLOD;
        int value = mVoiceStatusManager.getCurrenAcMode();
        switch (value) {
            case VoiceQueryStatusDefine.ACModeStatus.AC_COLD_MODE:
                mode = LeVoiceAirControlManager.AIR_AC_MODE__CLOD;
                break;
            case VoiceQueryStatusDefine.ACModeStatus.AC_HOT_MODE:
                mode = LeVoiceAirControlManager.AIR_AC_MODE__WARM;
                break;
            case VoiceQueryStatusDefine.ACModeStatus.AC_AUTO_MODE:
                mode = LeVoiceAirControlManager.AIR_AC_MODE__AUTO;
                break;
        }


        return mode;
    }

    /**
     * 空调是否打开
     * @return
     */
    @Override
    public boolean getAirIsOpen() {
        boolean isAirOpen = false;
        int value = mVoiceStatusManager.getCurrentAirStatus();
        switch (value) {
            case VoiceQueryStatusDefine.ACStatus.AC_OPEN:
                isAirOpen = true;
                break;
            case VoiceQueryStatusDefine.ACStatus.AC_CLOSE:
                isAirOpen = false;
                break;
        }
        return isAirOpen;
    }

    /**
     * 获取当前风向
     * @return
     */
    @Override
    public String getCurrentWindDirection() {
        String mode = "向" + Constant.VEHICLE_AC_WIND_DIRECTION_UP;
        int value = mVoiceStatusManager.getCurrentWindDirection();
        switch (value) {
            case VoiceQueryStatusDefine.ACWindDiretion.WIND_DIRECTION_HORIZONTAL:
                mode = Constant.VEHICLE_AC_WIND_DIRECTION_LEVEL;
                break;
            case VoiceQueryStatusDefine.ACWindDiretion.WIND_DIRECTION_DOWN:
                mode = "向" + Constant.VEHICLE_AC_WIND_DIRECTION_DOWN;
                break;
            case VoiceQueryStatusDefine.ACWindDiretion.WIND_DIRECTION_UP:
                mode = "向" + Constant.VEHICLE_AC_WIND_DIRECTION_UP;
                break;
        }
        return mode;
    }

    /**
     * 是否已达最大风速
     * @return
     */
    @Override
    public boolean isMaxWindSpeed() {
        return mVoiceStatusManager.isMaxWindSpeed();
    }

    /**
     * 是否已达最小风速
     * @return
     */
    @Override
    public boolean isMInWindSpeed() {
        return mVoiceStatusManager.isMinWindSpeed();
    }
}
