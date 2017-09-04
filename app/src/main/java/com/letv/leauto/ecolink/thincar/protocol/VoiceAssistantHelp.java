package com.letv.leauto.ecolink.thincar.protocol;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.leauto.link.lightcar.voiceassistant.ConstantCmd;
import com.leautolink.multivoiceengins.engine.stt.stream.VoiceInputStream;
import com.letv.dispatcherlib.engine.ATTSListener;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.controller.TTSAudioFocusManager;
import com.letv.leauto.ecolink.thincar.voice.ThinCarAirControlListener;
import com.letv.leauto.ecolink.thincar.voice.ThinCarAppListener;
import com.letv.leauto.ecolink.thincar.voice.ThinCarRadioListener;
import com.letv.leauto.ecolink.thincar.voice.ThinCarVoiceRecognitionListener;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.voicehelp.manger.air.LeVoiceAirControlManager;
import com.letv.voicehelp.manger.app.LeVoiceAppManager;
import com.letv.voicehelp.manger.radio.LeVoiceRadioManager;
import com.letv.voicehelp.utils.LeVoiceEngineUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/18.
 * 函数名称规范：
 *  所有从车机端接收到通知，处理函数都以on为前缀；如onstartVoiceAssistant
 *  车机端与手机端的语音交互：
 *      只有开启语音助手是车机发起的
 *      中间过程中所有命令都是由手机发给车机
 */
public class VoiceAssistantHelp {
    private static final String TAG = "VoiceAssistantHelp";

    private Context mContext;
    private ThinCarVoiceRecognitionListener mVoiceRecognitionListener;
    private ThinCarAirControlListener mAirControlListener;
    private ThinCarRadioListener mRadioListener;
    private ThinCarAppListener mAppListener;

    /** 禁止车机硬件触发语音识别*/
    public static final int DISNABLE_VOICE_KEY = 0;
    /** 使能车机硬件触发语音识别*/;
    public static final int ENABLE_VOICE_KEY = 1;

    private boolean isRadioPlaying = false;

    private static VoiceAssistantHelp ourInstance = new VoiceAssistantHelp();

    public static VoiceAssistantHelp getInstance() {
        return ourInstance;
    }

    public void initVoiceAssistant(Context context) {
        if (context instanceof  HomeActivity) {
            mContext = context;
        }

        mVoiceRecognitionListener = new ThinCarVoiceRecognitionListener(mContext);
        mAirControlListener = new ThinCarAirControlListener(context);
        mRadioListener = new ThinCarRadioListener();
        mAppListener = new ThinCarAppListener(context);

        LeVoiceAirControlManager.getInstance().setmAirControlListener(mAirControlListener);
        LeVoiceRadioManager.getInstance().setmRadioListener(mRadioListener);
        LeVoiceAppManager.getInstance().setAppListener(mAppListener);
    }

    private VoiceAssistantHelp() {
    }

    /**
     * 车机通知手机启动语音助手
     *  手机端开始播报TTS，同时把TTS文字内容回传给车机
     */
    public void onStartVoiceAssistant() {
        LogUtils.i(TAG, "onStartVoiceAssistant");
        stopVoiceRecord();

        ((HomeActivity)mContext).onStartVoiceAssistant();
        ((HomeActivity)mContext).resumeActivityNeeded();
        ((HomeActivity)mContext).PopWindowDismiss();
        LeVoiceEngineUtils.stopTTS();
        LeVoiceEngineUtils.stopSTT();
        LeVoiceEngineUtils.cancelSTT();

        ((HomeActivity)mContext).isPopupWindowShow = true;
        String text = mContext.getString(R.string.voice_speech_amhere);
        startTTS(text);
        TTSAudioFocusManager.getInstance(mContext).requestDuckFocus();
        LeVoiceEngineUtils.speakTTSWithListener(text, new ATTSListener() {
            @Override
            public boolean onSpeechFinish(String id) {
                TTSAudioFocusManager.getInstance(mContext).abandonFocus();
                ((HomeActivity)mContext).resumeActivityNeeded();
                return false;
            }

            @Override
            public boolean onError(String id, com.leautolink.multivoiceengins.ErrorInfo errorInfo) {
                TTSAudioFocusManager.getInstance(mContext).abandonFocus();
                ((HomeActivity)mContext).resumeActivityNeeded();
                return false;
            }
        });
    }

    /**
     * 退出语音助手
     */
    public void stopVoiceAssistant() {
        LogUtils.i(TAG, "stopVoiceAssistant");
        ((HomeActivity)mContext).isPopupWindowShow = false;
        JSONObject object = ConstantCmd.buildNotify(ConstantCmd.METHOD_STOP_VOICE_ASSISTANT, null);

        sendToCar(object);
    }

    /**
     * 手机控制车机开始录音
     */
    public void startVoiceRecord() {
        LogUtils.i(TAG, "--->StartVoiceRecord");
        JSONObject object = ConstantCmd.buildNotify(ConstantCmd.METHOD_START_VOICE_RECORD, null);
        sendToCar(object);
        VoiceInputStream.getStream().setListening(true);
    }

    /**
     *  结束录音
     */
    public void stopVoiceRecord() {
        LogUtils.i(TAG, "stopRecord");
        JSONObject object = ConstantCmd.buildNotify(ConstantCmd.METHOD_STOP_VOICE_RECORD, null);

        sendToCar(object);
    }

    /**
     * 开始播报tts
     * @param tts
     */
    public void startTTS(String tts) {
        LogUtils.i(TAG, "startTTS");
        Map<String, Object> map = new HashMap<>();
        map.put(ConstantCmd.PARAM_TEXT, tts);
        JSONObject object = ConstantCmd.buildNotify(ConstantCmd.METHOD_START_TTS, map);
        sendToCar(object);
    }

    /**
     * 检测到有人开始说话
     */
    public void detectVoiceInput() {
        LogUtils.i(TAG, "detectVoiceInput");
        JSONObject object = ConstantCmd.buildNotify(ConstantCmd.METHOD_DETECT_VOICE_INPUT, null);

        sendToCar(object);
    }

    /**
     *  检测到说话结束
     */
    public void detectNoVoiceInput() {
        LogUtils.i(TAG, "detectNoVoiceInput");
        JSONObject object = ConstantCmd.buildNotify(ConstantCmd.METHOD_DETECT_VOICE_NO, null);

        sendToCar(object);
    }

    /**
     * 开始搜索
     * @return
     */
    public void startSearching() {
        LogUtils.i(TAG, "startSearching");
        JSONObject object = ConstantCmd.buildNotify(ConstantCmd.METHOD_START_SEARCHING, null);

        sendToCar(object);
    }

    /**
     * 车机通知手机开始录音
     */
    public void onStartVoiceRecord() {
        LogUtils.i(TAG, "startVoiceRecord");
        VoiceInputStream.getStream().clear();
        VoiceInputStream.getStream().setListening(true);
    }

    /**
     * 调用接口解析传过来的音频数据
     */
    public void onParseVoiceData(byte[] data, int length) {
        VoiceInputStream stream = VoiceInputStream.getStream();
        stream.writeVoice(data);
    }

    /**
     * 发送解析过的语音对应文本给车机
     * @param str 要显示的文本内容
     */
    public void sendDisplayTextInfo(String str) {
        Map<String, Object> map = new HashMap<>();
        map.put(ConstantCmd.PARAM_TEXT, str);
        JSONObject object = ConstantCmd.buildNotify(ConstantCmd.METHOD_DISPLAY_TEXTINFO, map);

        sendToCar(object);
    }

    /**
     * 通用控制指令
     * @param command   命令
     */
    public void sendCommonVoiceCommand(String command) {
        Map<String, Object> content = new HashMap<>();
        content.put(ConstantCmd.PARAM_COMMAND, command);
        content.put(ConstantCmd.PARAM_PARAMETER, "");
        JSONObject object = ConstantCmd.buildRequest(ConstantCmd.METHOD_VOICE_COMMAND, content);

        sendToCar(object);

        /** 退出语音助手*/
        stopVoiceAssistant();
    }

    /**
     * 跳转界面控制指令
     * @param command   命令
     * @param parameter 参数
     */
    public void sendRedirectVoiceCommand(String command, String parameter) {
        Map<String, Object> content = new HashMap<>();
        content.put(ConstantCmd.PARAM_COMMAND, command);
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("page",parameter);
        content.put(ConstantCmd.PARAM_PARAMETER, paraMap);
        JSONObject object = ConstantCmd.buildRequest(ConstantCmd.METHOD_VOICE_COMMAND, content);

        sendToCar(object);
    }

    /**
     * 改变频道控制指令
     * @param command   命令
     * @param parameter 参数
     */
    public void sendChangeChannelVoiceCommand(String command, String parameter) {
        Map<String, Object> content = new HashMap<>();
        content.put(ConstantCmd.PARAM_COMMAND, command);
        content.put(ConstantCmd.PARAM_PARAMETER, parameter);
        JSONObject object = ConstantCmd.buildRequest(ConstantCmd.METHOD_VOICE_COMMAND, content);

        sendToCar(object);
        /** 退出语音助手*/
        stopVoiceAssistant();
    }

    /**
     * 设置频道控制指令
     * @param command   命令
     */
    public void sendSetChannelVoiceCommand(String command, String channel,float rate) {
        Map<String, Object> content = new HashMap<>();
        content.put(ConstantCmd.PARAM_COMMAND, command);
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("channel",channel);
        paraMap.put("rate",rate);
        content.put(ConstantCmd.PARAM_PARAMETER, paraMap);
        JSONObject object = ConstantCmd.buildRequest(ConstantCmd.METHOD_VOICE_COMMAND, content);

        sendToCar(object);

        /** 退出语音助手*/
        stopVoiceAssistant();
    }

    /**
     * 设置空调温度控制指令
     * @param command   命令
     */
    public void sendSetACTempVoiceCommand(String command, int temp) {
        Map<String, Object> content = new HashMap<>();
        content.put(ConstantCmd.PARAM_COMMAND, command);
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("temperature",temp);
        content.put(ConstantCmd.PARAM_PARAMETER, paraMap);
        JSONObject object = ConstantCmd.buildRequest(ConstantCmd.METHOD_VOICE_COMMAND, content);

        sendToCar(object);

        /** 退出语音助手*/
        stopVoiceAssistant();
    }

    /**
     * 发送语音相关的Request、Notify、Response到车机
     * @param object
     */
    private void sendToCar(JSONObject object) {
        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.VOICE_ASSISTANT_APPID,object);
    }

    public ThinCarVoiceRecognitionListener getVoiceRecognitionListener() {
        return mVoiceRecognitionListener;
    }

    public void setRadioPlayState(boolean value) {
        isRadioPlaying = value;
    }

    public boolean isRadioPlaying() {
        return isRadioPlaying;
    }

    /**
     * 车机通知手机结语音识别
     */
    public void requestStopVoiceRecognize() {
        LeVoiceEngineUtils.stopTTS();
        LeVoiceEngineUtils.stopSTT();
        LeVoiceEngineUtils.cancelSTT();

        HomeActivity activity = (HomeActivity) mContext;
        activity.isPopupWindowShow = false;
        activity.resetPlayerState();
    }

    public void stopVoiceWhenPlalyMusic() {
        stopVoiceAssistant();
        requestStopVoiceRecognize();
    }

    public void onDestroy() {
        mContext = null;
    }

    /**
     *  控制车机要不要显示语音识别图标
     *  @param value 1 enable,0 diseanble
     */
    public void sendVoiceTriggeValue(int value) {
        LogUtils.i(TAG, "detectNoVoiceInput");
        Map<String, Object> map = new HashMap<>();
        map.put(ConstantCmd.PARAM_COMMAND, value);
        JSONObject object = ConstantCmd.buildNotify("VoiceTrigge", map);

        sendToCar(object);
    }
}
