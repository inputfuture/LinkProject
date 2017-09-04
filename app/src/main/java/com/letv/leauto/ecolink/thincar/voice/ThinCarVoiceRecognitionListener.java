package com.letv.leauto.ecolink.thincar.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import com.leauto.link.lightcar.PcmToWavUtil;
import com.leauto.link.lightcar.voiceassistant.VoiceCommandDefine;

import com.leautolink.multivoiceengins.ErrorInfo;
import com.leautolink.multivoiceengins.EventInfo;
import com.leautolink.multivoiceengins.STTResult;
import com.leautolink.multivoiceengins.engine.stt.stream.VoiceInputStream;
import com.leautolink.multivoiceengins.utils.Logger;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.leplayer.common.LePlayerCommon;
import com.letv.leauto.ecolink.thincar.protocol.VoiceAssistantHelp;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.voicehelp.listener.RecognitionListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/2/28.
 */

public class ThinCarVoiceRecognitionListener implements RecognitionListener {
    private static final String TAG = "RecognitionListener";
    private Context mContext;

    public ThinCarVoiceRecognitionListener(Context context) {
        mContext = context;
    }

    @Override
    public boolean onReadyForSpeech(Bundle bundle) {
        Trace.Error(TAG,"onReadyForSpeech");
        EcoApplication.getInstance().getMhandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startVoiceRecord();
            }
        },100);

        return false;
    }

    @Override
    public boolean onBeginningOfSpeech() {
        Trace.Error(TAG,"onBeginningOfSpeech");
        VoiceAssistantHelp.getInstance().detectVoiceInput();
        return false;
    }

    @Override
    public boolean onRmsChanged(float v) {
        return false;
    }

    @Override
    public boolean onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Trace.Error(TAG,"onPartialResults nbest:"+nbest);
        if (nbest.size() > 0) {
            String tempText = Arrays.toString(nbest.toArray(new String[0])).replace("[", "").replace("]", "").replace(" ", "");
            VoiceAssistantHelp.getInstance().sendDisplayTextInfo(tempText);
        }

        return false;
    }

    @Override
    public boolean onBufferReceived(byte[] bytes) {
        return false;
    }

    @Override
    public boolean onEndOfSpeech() {
        Trace.Error(TAG,"onEndOfSpeech");
        VoiceInputStream.getStream().clear();
        VoiceAssistantHelp.getInstance().detectNoVoiceInput();
       VoiceInputStream.getStream().setListening(false);
        return false;
    }

    @Override
    public boolean onSTTEvent(EventInfo eventInfo) {
        Trace.Error(TAG,"onSTTEvent eventInfo:"+eventInfo);
        return false;
    }

    /**
     * 识别成功但是解析不了后的回调
     * @param sttResult
     * @return
     */
    @Override
    public boolean onSTTFailed(STTResult sttResult) {
        Trace.Error(TAG,"onSTonSTTFailed:");
        VoiceInputStream.getStream().clear();
        stopVoiceRecord();
        return false;
    }

    /**
     * 是识别出错
     * @param errorInfo
     * @return
     */
    @Override
    public boolean onSTTError(ErrorInfo errorInfo) {
        Trace.Error(TAG,"onSTTError errorInfo:"+errorInfo);
        VoiceInputStream.getStream().clear();
        if (GlobalCfg.isVoiceDebugOpen) {
            new PcmToWavUtil().converToWav();
        }
        return false;
    }

    @Override
    public void voiceShouldShowText(String text) {
        VoiceInputStream.getStream().clear();
        Trace.Error(TAG,"voiceShouldShowTe text:" + text);
        VoiceAssistantHelp.getInstance().sendDisplayTextInfo(text);
    }

    /**
     * 识别成功并正确返回
     * @param text
     */
    @Override
    public void onResult(String text) {
        Trace.Error(TAG,"onResult text:" + text);
        HomeActivity activity = (HomeActivity) mContext;
        activity.resumeActivityNeeded();
        VoiceAssistantHelp.getInstance().sendDisplayTextInfo(text);

        stopVoiceRecord();
        startVoiceSearch();

        if (GlobalCfg.isVoiceDebugOpen) {
            new PcmToWavUtil().converToWav();
        }
    }

    @Override
    public void ttsStart() {
        stopVoiceRecord();
    }

    /**
     * 有多条显示记录时，需要让车机进入全屏
     */
    @Override
    public void showListView() {
        Trace.Error(TAG, "showListView");
        VoiceAssistantHelp.getInstance().sendRedirectVoiceCommand(VoiceCommandDefine.RedirectToPage.VOICE_CMD_REDIRECT,
                VoiceCommandDefine.RedirectToPage.RedirectToPageParam.VOICE_PARAM_VOICEASSIST);
        VoiceAssistantHelp.getInstance().sendVoiceTriggeValue(VoiceAssistantHelp.DISNABLE_VOICE_KEY);
        if (!((HomeActivity)mContext).isPopupWindowShow()) {
            Intent broadcast = new Intent();
            broadcast.setAction(LePlayerCommon.BROADCAST_ACTION_VOICERECORD);
            broadcast.putExtra(LePlayerCommon.BROADCAST_EXTRA_VOICERECORD, LePlayerCommon.BROADCAST_EXTRA_VOICERECORD_BEGIN);
            EcoApplication.instance.sendBroadcast(broadcast);
        }
    }

    @Override
    public void onClickBack(boolean value) {
        HomeActivity activity = (HomeActivity) mContext;
        activity.voiceWindowBackClick();
    }

    private void startVoiceRecord() {
        Trace.Info(TAG, "startVoiceRecord start record");
        VoiceAssistantHelp.getInstance().startVoiceRecord();
    }

    public void stopVoiceRecord() {
        Logger.d("LXL","stopVoiceRecord");
        VoiceAssistantHelp.getInstance().stopVoiceRecord();
        VoiceInputStream.getStream().setListening(false);
    }

    public void startVoiceSearch() {
        Intent broadcast = new Intent();
        broadcast.setAction(LePlayerCommon.BROADCAST_ACTION_VOICERECORD);
        broadcast.putExtra(LePlayerCommon.BROADCAST_EXTRA_VOICERECORD, LePlayerCommon.BROADCAST_EXTRA_VOICERECORD_BEGIN);
        EcoApplication.instance.sendBroadcast(broadcast);

        VoiceAssistantHelp.getInstance().stopVoiceAssistant();
    }
}
