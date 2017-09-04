package com.letv.leauto.ecolink.thincar.voice;

import android.util.Log;

import com.leauto.link.lightcar.voiceassistant.VoiceCommandDefine;
import com.leauto.link.lightcar.voiceassistant.VoiceQueryStatusDefine;
import com.leauto.link.lightcar.voiceassistant.VoiceStatusManager;
import com.letv.leauto.ecolink.thincar.protocol.VoiceAssistantHelp;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.voicehelp.manger.radio.LeVoiceRadioManager;
import com.letv.voicehelp.manger.radio.RadioInfo;

/**
 * Created by Administrator on 2017/2/27.
 */

public class ThinCarRadioListener implements LeVoiceRadioManager.RadioListener {
    private static final String TAG = "RadioListener";

    private static final int fmMin = 76;
    private static final int fmMax = 108;

    private static final int amMin = 525;
    private static final int amMax = 1607;

    private static final String AM_MODE = "am";
    private static final String FM_MODE = "fm";

    private RadioInfo mRadioInfo = new RadioInfo(fmMax,fmMin,amMax,amMin);

    @Override
    public RadioInfo getRadioInfo() {
        return mRadioInfo;
    }

    @Override
    public void open() {
        Trace.Info(TAG,"open");
        VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_OPEN_RADIO);
        VoiceAssistantHelp.getInstance().setRadioPlayState(true);
    }

    @Override
    public void close() {
        Trace.Info(TAG,"close");
        VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_CLOSE_RADIO);
        VoiceAssistantHelp.getInstance().setRadioPlayState(false);
    }

    @Override
    public void autoSearch() {
        Trace.Info(TAG,"autoSearch");
        VoiceAssistantHelp.getInstance().sendCommonVoiceCommand(VoiceCommandDefine.VOICE_CMD_AUDO_SEEK);
        VoiceAssistantHelp.getInstance().setRadioPlayState(true);
    }

    @Override
    public void pre() {
        Trace.Info(TAG,"pre");
        VoiceAssistantHelp.getInstance().sendChangeChannelVoiceCommand(VoiceCommandDefine.ChangeRadioChannel.VOICE_CMD_CHANGE_RADIO_CHANNEL,
                VoiceCommandDefine.ChangeRadioChannel.ChangeRadioChannelParam.VOICE_PARAM_LAST);
        VoiceAssistantHelp.getInstance().setRadioPlayState(true);
    }

    @Override
    public void next() {
        Trace.Info(TAG,"next");
        VoiceAssistantHelp.getInstance().sendChangeChannelVoiceCommand(VoiceCommandDefine.ChangeRadioChannel.VOICE_CMD_CHANGE_RADIO_CHANNEL,
                VoiceCommandDefine.ChangeRadioChannel.ChangeRadioChannelParam.VOICE_PARAM_NEXT);
        VoiceAssistantHelp.getInstance().setRadioPlayState(true);
    }

    @Override
    public void switchModulationSystem(String mode) {
        Trace.Info(TAG,"switchModulationSystem mode:" + mode);
        if (mode.equals(AM_MODE)) {
            playByChannel(mode,925+"");
        } else if (mode.equals(FM_MODE)) {
            playByChannel(mode,88+"");
        }
    }

    @Override
    public void playByChannel(String mode, String channel) {
        Trace.Info(TAG,"playByChannel mode:" + mode + "  channel:" + channel);
        try {
            float rate = Float.parseFloat(channel);
            String modeTemp = VoiceCommandDefine.SetRadioChannel.SetRadioChannelParam.VOICE_PARAM_AM;
            if (mode.equals(AM_MODE)) {
                modeTemp = VoiceCommandDefine.SetRadioChannel.SetRadioChannelParam.VOICE_PARAM_AM;
            } else if (mode.equals(FM_MODE)) {
                modeTemp = VoiceCommandDefine.SetRadioChannel.SetRadioChannelParam.VOICE_PARAM_FM;
            }
            VoiceAssistantHelp.getInstance().sendSetChannelVoiceCommand(VoiceCommandDefine.SetRadioChannel.VOICE_CMD_SET_RADIO_CHANNEL,
                    modeTemp,rate);
            VoiceAssistantHelp.getInstance().setRadioPlayState(true);
        } catch (Exception e) {
        }

    }

    @Override
    public boolean radioIsOpen() {
        boolean isRadioOpen = false;
        int value = VoiceStatusManager.getInstance().getCurrentRadioStatus();
        switch (value) {
            case VoiceQueryStatusDefine.RadioStatus.RADIO_CLOSE:
                isRadioOpen = false;
                break;
            case VoiceQueryStatusDefine.RadioStatus.RADIO_OPEN:
                isRadioOpen = true;
                break;
        }
        return isRadioOpen;
    }
}
