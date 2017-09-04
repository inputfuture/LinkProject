package com.letv.leauto.ecolink.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.baidu.tts.client.SpeechSynthesizeBag;

import com.leautolink.multivoiceengins.ErrorInfo;
import com.letv.dispatcherlib.engine.ATTSListener;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.leplayer.common.LePlayerCommon;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.utils.TelephonyUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.voicehelp.utils.LeVoiceEngineUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liweiwei on 16/3/16.
 */
public class EcoTTSController extends ATTSListener {
    private String tag = EcoTTSController.class.getSimpleName();
    private static EcoTTSController mInstance = null;
    //    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";

    private Context mContext;
    public volatile boolean   mSpeechFinished=true;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    LeVoiceEngineUtils.speakTTSWithListener((String)msg.obj,EcoTTSController.this);
                    break;
            }
        }
    };
    /**
     * 获取TTS处理的单例实例
     *
     * @return
     */
    public static EcoTTSController getInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        synchronized (EcoTTSController.class) {
            if (mInstance == null) {
                mInstance = new EcoTTSController(context);
            }
        }
        return mInstance;
    }

    EcoTTSController(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    public void init() {
        //  initialEnv();
//        initialTts();
//        initVoiceTTS();
        mLePlayer=EcoApplication.LeGlob.getPlayer();
    }

    private LePlayer mLePlayer;

    /*private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }*/

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = mContext.getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    private void initialTts() {
//        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
//        this.mSpeechSynthesizer.setContext(mContext);
//        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
//       /* // 文本模型文件路径 (离线引擎使用)
//        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
//                + TEXT_MODEL_NAME);
//        // 声学模型文件路径 (离线引擎使用)
//        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
//                + SPEECH_FEMALE_MODEL_NAME);
//        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
//        //this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
//        //      + LICENSE_FILE_NAME);
//        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
//        this.mSpeechSynthesizer.setAppId("7702439");*/
//        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
//        this.mSpeechSynthesizer.setApiKey("mfL7FYdsGpGE670qoQxS5QO1", "306b977f3e471617df6a1018bf999477");
//        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
//        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
//        // 设置Mix模式的合成策略
//        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_NETWORK);
//        // 授权检测接口(可以不使用，只是验证授权是否成功)
//       /* AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
//        if (authInfo.isSuccess()) {
//            Log.i(tag, "auth success");
//        } else {
//            String errorMsg = authInfo.getTtsError().getDetailMessage();
//            Log.e(tag, "auth failed errorMsg=" + errorMsg);
//        }*/
//        // 初始化tts
//        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME,"9");
//        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
//
//        mSpeechSynthesizer.initTts(TtsMode.ONLINE);
//        // 加载离线英文资源（提供离线英文合成功能）
//        /*int result =
//                mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
//                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
//        Log.e(tag, "loadEnglishModel result=" + result);*/
//    }


    /**
     * 单句
     *
     * @param text
     */
    public void speak(String text) {
//        Boolean ttsOpen = CacheUtils.getInstance(mContext).getBoolean(SettingCfg.TTS_OPEN, true);
//        //tts关闭或者文字为空时  不播放tts
//        if (text == null || !ttsOpen) {
//            return;
//        }
        if (HomeActivity.isPopupWindowShow){
            return;
        }
        if (TelephonyUtil.getInstance(mContext).isTelephonyCalling())
            return;
//        if (mLePlayer!=null&&mLePlayer.getCurrentStatus()!=null&&mLePlayer.getCurrentStatus().currentItem!=null&&mLePlayer.getCurrentStatus().isPlaying){
//            // mLePlayer.stopPlay();
//            mLePlayer.onDuckBegin();
//            Trace.Debug("#####stop");
//        }

//
//        QPlayer qPlayer=EcoApplication.LeGlob.getqPlayer();
//        if (qPlayer!=null) {
//            qPlayer.pause();
//
//        }
//        int result = this.mSpeechSynthesizer.speak(text);
        TTSAudioFocusManager.getInstance(mContext).requestDuckFocus();
        LeVoiceEngineUtils.cancelSTT();
        LeVoiceEngineUtils.speakTTSWithListener(text,this);
 //       LeVoiceEngineUtils.setVolumeTTS(8);
//        LeVoiceEngineUtils.speakTTSWithListener(text,this);
        //        Message msg = mHandler.obtainMessage(1);
        //        msg.obj = text;
        //        mHandler.sendMessageDelayed(msg, 1000);


//        if (result < 0) {
//            Log.e(tag, "error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
//        }
    }

    public void pause() {

        LeVoiceEngineUtils.stopTTS();
        mSpeechFinished=true;
    }

    public void resume() {
        mSpeechFinished=false;
        LeVoiceEngineUtils.releaseTTS();

    }

    public void stop() {
        LeVoiceEngineUtils.stopTTS();
        mSpeechFinished=true;
        onSpeechFinish(null);
        //【Android】【兼容性-坚果】【电台】【必现】退出导航后，电台声音暂停 tts没播放完
        Intent broadcast = new Intent(LePlayerCommon.BROADCAST_ACTION_TTS);
        broadcast.putExtra(LePlayerCommon.BROADCAST_EXTRA_TTS, LePlayerCommon.BROADCAST_EXTRA_TTS_END);
    }
    //
//    public void batchSpeak(String[] array) {
//
//
//        Boolean ttsOpen = CacheUtils.getInstance(mContext).getBoolean(SettingCfg.TTS_OPEN, true);
//
//        if (!ttsOpen) {
//            return;
//        }
//        if (TelephonyUtil.getInstance(mContext).isTelephonyCalling())
//            return;
//        if (mLePlayer!=null&&mLePlayer.getCurrentStatus()!=null&&mLePlayer.getCurrentStatus().currentItem!=null&&mLePlayer.getCurrentStatus().isPlaying){
//            mLePlayer.stopPlay();
//            Trace.Debug("#####stop");
//        }
//
//        List<SpeechSynthesizeBag> bags = new ArrayList<SpeechSynthesizeBag>();
//        for (int i = 0; i < array.length; i++) {
//            bags.add(getSpeechSynthesizeBag(array[i], i + ""));
//        }
//        int result = this.voiceTTS.batchSpeak(bags);
//        if (result < 0) {
//            Log.e(tag, "error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
//        }
//    }

    private SpeechSynthesizeBag getSpeechSynthesizeBag(String text, String utteranceId) {
        SpeechSynthesizeBag speechSynthesizeBag = new SpeechSynthesizeBag();
        speechSynthesizeBag.setText(text);
        speechSynthesizeBag.setUtteranceId(utteranceId);
        return speechSynthesizeBag;
    }

    @Override
    public boolean onSynthesizeStart(String s) {
        return true;
    }

    @Override
    public boolean onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        return true;
    }

    @Override
    public boolean onSynthesizeFinish(String s) {
        return true;
    }

    @Override
    public boolean onSpeechStart(String s) {
        mSpeechFinished=false;

        Intent broadcast = new Intent(LePlayerCommon.BROADCAST_ACTION_TTS);
        broadcast.putExtra(LePlayerCommon.BROADCAST_EXTRA_TTS, LePlayerCommon.BROADCAST_EXTRA_TTS_BEGIN);
        EcoApplication.instance.sendBroadcast(broadcast);
        return true;
    }

    @Override
    public boolean onSpeechProgressChanged(String s, int i) {
        if (mLePlayer!=null&&mLePlayer.getCurrentStatus()!=null&&mLePlayer.getCurrentStatus().currentItem!=null&&mLePlayer.getCurrentStatus().isPlaying){
//            mLePlayer.stopPlay();
            Trace.Debug("#####stop");
        }
//        QPlayer qPlayer=EcoApplication.LeGlob.getqPlayer();
//        if (qPlayer!=null) {
//            qPlayer.pause();
//
//        }
        return true;
    }

    @Override
    public boolean onSpeechFinish(String s) {
        Trace.Debug("***** onSpeechFinish");
//        if(!PlayPCM.stopByUser){
//            QPlayer qPlayer=EcoApplication.LeGlob.getqPlayer();
//            if (qPlayer!=null&& !PlayPCM.stopByUser&&qPlayer.getcurrentMediaInfos()!=null) {
//                    qPlayer.play();
//            }
//        }
        mSpeechFinished=true;
        Intent broadcast = new Intent(LePlayerCommon.BROADCAST_ACTION_TTS);
        broadcast.putExtra(LePlayerCommon.BROADCAST_EXTRA_TTS, LePlayerCommon.BROADCAST_EXTRA_TTS_END);
        EcoApplication.instance.sendBroadcast(broadcast);
//        if (mLePlayer!=null&& mLePlayer.getCurrentStatus()!=null&&mLePlayer.getCurrentStatus().currentItem!=null&&!BaseActivity.isVoice&&!BaseActivity.isStoped){
////            mLePlayer.startPlay();
//            mLePlayer.onDuckEnd();
//        }
        TTSAudioFocusManager.getInstance(mContext).abandonFocus();
        return true;
    }

    @Override
    public boolean onError(String s, ErrorInfo errorInfo) {
        mSpeechFinished=true;
        Intent broadcast = new Intent(LePlayerCommon.BROADCAST_ACTION_TTS);
        broadcast.putExtra(LePlayerCommon.BROADCAST_EXTRA_TTS, LePlayerCommon.BROADCAST_EXTRA_TTS_END);
        EcoApplication.instance.sendBroadcast(broadcast);
        if (mLePlayer!=null&& mLePlayer.getCurrentStatus()!=null&&mLePlayer.getCurrentStatus().currentItem!=null&&!BaseActivity.isVoice&&!BaseActivity.isStoped){
            Trace.Debug("####start");
            mLePlayer.startPlay();
            mLePlayer.onDuckEnd();
        }
        return true;
    }
}
