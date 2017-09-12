package com.letv.leauto.ecolink.thincar;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.ScreenRecordActivity;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.IAOACallback;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.module.AVNInfo;
import com.leauto.link.lightcar.ota.OtaThincarUtils;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.event.LinkCarConnectStatusObservable;
import com.letv.leauto.ecolink.receiver.ScreenRotationUtil;
import com.letv.leauto.ecolink.service.PathService;
import com.letv.leauto.ecolink.thincar.ota.OtaUtils;
import com.letv.leauto.ecolink.thincar.protocol.CarProtocolProcess;
import com.letv.leauto.ecolink.thincar.protocol.DeviceInfoNotifyHelp;
import com.letv.leauto.ecolink.thincar.protocol.VoiceAssistantHelp;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.fragment.MapFragment;
import com.letv.leauto.ecolink.ui.fragment.NaviFragment;
import com.letv.leauto.ecolink.utils.BlueToothUtil;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.LogUtil;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.TimeUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.utils.Trace;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 跟瘦车机相关的交互放在这个类中
 * <p/>
 * Created by Administrator on 2016/12/15.
 */
public class ThinCarIAOACallback implements IAOACallback {
    private HomeActivity mContext;
    private Handler mHandler;
    private int brightness;
    private BluetoothAdapter adapter;
    private String carMac;

    public static int DEFAULT_BRIGHT_VALUE= 65;

    /**
     * 系统默认睡觉时间为30分钟
     */
    private int DEFAULT_SYSTEM_SCREENOFF_TIME = 30*60*1000;
    /** 定义15s待机暗值 */
    private int FIFTEN_SEC_SCREENOFF_TIME = 15*1000;


    /***
     *  这个变量用于记录车机传来全屏半屏事件，
     *  作用于哪个界面
     */
    private int mActionForPage;

    /**
     * 判断动画有没有播放过，用以控制在收到多次录屏请求时，
     * 忽略到后面的请求。
     */
    private boolean mHasAnimPlayed = false;

    private NaviEventRunnable mNaviEventRunnable;

    /**
     * jerome add code
     *
     * 此参数是判断互联连接上后，是否需要切换到导航, 默认是需要切换
     */
    private boolean mIsSwitchMap = true;

    /**
     * 保存系统设置的屏幕睡觉时间
     */
    private int mSystemScreenOffTime;

    public static final String ADB_RESTART_ACTIVITY_ACTION = "com.leauto.ecolink.adb.restart.activity";

    private Runnable mHalfScreenRunnable = new Runnable() {
        @Override
        public void run() {
            EventBus.getDefault().post(Constant.MAP_HALF_SCREEN);
        }
    };

    private Runnable mDarkScreenRunnable = new Runnable() {
        @Override
        public void run() {
            /** 屏幕变暗 */
            if (HomeActivity.isThinCar) {
                DeviceUtils.setScreenBrightness(mContext, 0);
            }
        }
    };

    public ThinCarIAOACallback(HomeActivity context) {
        mContext = context;
        mHandler = new Handler(context.getMainLooper());
        brightness = DeviceUtils.getScreenBrightness(mContext);
    }

    public void setContext(HomeActivity context) {
        mContext = context;
    }

    @Override
    public void onCommand(final int command, int params) {
        LogUtils.i("CallBack","onCommand command:" + command);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processCommandEvent(command);
            }
        });
        switch (command){
            case ThinCarDefine.ProtocolAppId.DRVIE_APPID:
                if(params==1){
                    EventBus.getDefault().post(Constant.DRIVING);
                    mContext.setDriving(true);
                }else{
                    EventBus.getDefault().post(Constant.NO_DRIVE);
                    mContext.setDriving(false);
                }
                break;
        }
    }

    @Override
    public void onPcmPath(String pcmPath) {

    }

    @Override
    public void onAoaAttach() {
        LogUtils.i("ThinCarIAOACallback","onAoaAttach isThinCar:" + HomeActivity.isThinCar);
        if (HomeActivity.isThinCar) {
            return;
        }
        mContext.notifyThinCarState(true);
        //互联车机连接上，发出通知
        mContext.getEcoApplication().getObservable().setLinkConnected(LinkCarConnectStatusObservable.CONNECT);
        mContext.closeDisclaimerActivity();
        EventBus.getDefault().post(Constant.HIDE_BOTTOM_BAR);

        mSystemScreenOffTime = DeviceUtils.getScreenOffTime(mContext);
        DeviceUtils.setScreenOffTime(mContext,DEFAULT_SYSTEM_SCREENOFF_TIME);

        startBluetooth();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /** get version info*/
                byte[] array = OtaThincarUtils.getVersion();
                Trace.Debug("thincar", "send getVersion data");
                /** send upgrade request*/

                DataSendManager.getInstance().sendDataDirect(array);
                DeviceInfoNotifyHelp.getInstance().notifyCurrentTime();
            }
        }, 1000);

        // showMapView();
        playWelcomeAnim();

        String home = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HOME_ADDR, null);
        String work = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.WORK_ADDR, null);
        if (home != null) {
            showDistanceInfoForThincart(home);
        } else {
            if (work != null) {
                showDistanceInfoForThincart(work);
            } else {
                LogUtils.i("TAG", "通知车机工作和家地址没有设定");
            }
        }
    }

    @Override
    public void onAoaDettach() {
        LogUtils.i("ThinCarIAOACallback","onAoaDettach");
        mIsSwitchMap = true;
        mHasAnimPlayed = false;
        GlobalCfg.isAppBackground = false;
        mContext.notifyThinCarState(false);

        if(mContext.isDriving()){
            EventBus.getDefault().post(Constant.NO_DRIVE);
            mContext.setDriving(false);
        }
        //互联车机断开，发出通知
        mContext.getEcoApplication().getObservable().setLinkConnected(LinkCarConnectStatusObservable.DIS_CONNECT);

        EventBus.getDefault().post(Constant.SHOW_BOTTOM_BAR);

        mHandler.removeCallbacks(mDarkScreenRunnable);

        if (mSystemScreenOffTime < FIFTEN_SEC_SCREENOFF_TIME) {
            mSystemScreenOffTime = FIFTEN_SEC_SCREENOFF_TIME;
        }
        DeviceUtils.setScreenOffTime(mContext,mSystemScreenOffTime);

        if (brightness == 0) {
            brightness = DEFAULT_BRIGHT_VALUE;
        }
        DeviceUtils.setScreenBrightness(mContext, brightness);

        //关闭路径信息service
        Intent intent = new Intent(mContext, PathService.class);
        intent.putExtra("loacal", PathService.EndService);
        mContext.startService(intent);

        EventBus.getDefault().post(Constant.MAP_RESTORE_SCREEN);
    }

    @Override
    public void NotifyEvent(final int event, final String data) {
        Trace.Debug("thincar","event="+event+"  data="+data);
        switch (event) {
            case ThinCarDefine.ProtocolNotifyValue.NOTIFY_BLUETOOTH_MAC://蓝牙地址

                if (data != null) {
                    carMac = data;
                    BlueToothUtil.blueToothConnectStatus(mContext, adapter, data);
                } else {
                    LogUtils.i("TAG", "接受车机mac地值为null");
                }
                break;

            case ThinCarDefine.ProtocolNotifyValue.NOTIFY_CAR_OTAINFO:
                if (data != null) {
                    //获得车机SN和版本去请求接口
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(data);
                        String model = jsonObject.optString("MODEL");
                        String carSN = jsonObject.optString("SN");
                        String version = jsonObject.optString("VERSION");
                        OtaUtils.checkOTA(mContext, carSN, model, version);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.show(mContext, "传输版本错误");
                }
                break;
            case ThinCarDefine.ProtocolNotifyValue.NOTIFY_CAR_UPDATE_INFO:
                if (data != null) {
                    //请求升级文件



                    try {
                        Trace.Debug("thincar"," 解析数据");
                        org.json.JSONObject object = new org.json.JSONObject(data);
                        String name = object.optString("NAME");
                        String version = object.optString("VERSION");
                        int start = object.optInt("START");
                        int count = object.optInt("COUNT");
                        OtaUtils.sendOtaZip(mContext, version, start, count);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.i("TAG", e.toString());
                    }
                } else {
                    ToastUtil.show(mContext, "传输版本错误");
                }
                break;
            case  ThinCarDefine.ProtocolNotifyValue.NOTIFY_CAR_UPDATE_ACCEPT:
                Trace.Debug("thincar","update accept");
                //如果是wifi环境，直接下载，发下载进入 非wifi环境，像车机发送确认弹窗
                if (data!=null){
                    try {
                        org.json.JSONObject jsonObject=new org.json.JSONObject(data);
                        int accept=jsonObject.optInt("ACCEPT");
                        if (accept==2) {
                            boolean isWifi=true;
                            if (isWifi){
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        OtaUtils.startOtaDownload(mContext);
                                    }
                                });
                            }else {
                                OtaUtils.sendNoWifiState(mContext);


                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                break;
            case ThinCarDefine.ProtocolNotifyValue.NOTIFY_OTA_NOWIFI_RSP:
                org.json.JSONObject jsonObject= null;
                try {
                    jsonObject = new org.json.JSONObject(data);
                    int accept = jsonObject.optInt("ACCEPT");
                    if (accept==2){
                        OtaUtils.startOtaDownload(mContext);}
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Trace.Debug("thincar ","no wifirespons");
                break;
            default:
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CarProtocolProcess.getInstance().parseNotifyEvent(event, data, mContext);
                    }
                });
                break;
        }
    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void onViceDataObtain(byte[] data, int len) {
        LogUtils.i("CallBack","onViceDataObtain len:" + len);
        VoiceAssistantHelp.getInstance().onParseVoiceData(data, len);
        if (GlobalCfg.isVoiceDebugOpen) {
            DataSendManager.getInstance().writePcmToFile(data, 0, data.length);
        }
    }

    @Override
    public void onNaviEvent(short x, short y, short width, short height) {
        LogUtils.i("CallBack","onNaviEvent height:" + height);
        if (mNaviEventRunnable != null) {
            mHandler.removeCallbacks(mNaviEventRunnable);
        }

        mNaviEventRunnable = new NaviEventRunnable(x,y,width,height);
        mHandler.postDelayed(mNaviEventRunnable,200);
    }

    @Override
    public void notifyGesterEvent(final int event, final int x, final int y, final int parameter) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NaviFragment naviFragment = NaviFragment.getThis();
                MapFragment mapFragment = mContext.getMapFragment();
                if (naviFragment != null && MapCfg.mNaAciFragmentIsNaVi) {
                    naviFragment.notifyGesterEvent(event, x, y, parameter);
                } else {
                    mapFragment.notifyGesterEvent(event, x, y, parameter);
                }
            }
        });
    }

    @Override
    public void onAVNInfo(AVNInfo info) {
//        IOVCloudUtil.registVehicle(info);
        LogUtils.e("ThinCarAOACallback", info.toString());
        mContext.setCarVINcode(info.getVin());
    }

    @Override
    public void onCANFileTransmit(String name, long size) {
        LogUtils.e("ThinCarAOACallback", "name="+name+";size="+size);
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_CAN_FILE_TRANSMIT);
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("response",0);
        map.put("Parameter", parameter);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(map);
        LogUtils.d("ThinCarAOACallback", jsonObject.toString());
        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.OTA_APPID,jsonObject);
    }

    @Override
    public void onCANDataPath(String s) {

    }

    private void startHomeActivity() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        mContext.startActivity(intent);
        EventBus.getDefault().post(Constant.CLOSE_VOICE);//机车点击导航按钮时，关闭语音
    }

    private void darkScreen() {
        long time = 0;
        if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.IS_FIRST_CONNECT,true)) {
            time = TimeUtils.ONE_MINUTE_TIEM;
        } else {
            time = TimeUtils.ONE_MINUTE_TIEM / 2;
        }
        mHandler.postDelayed(mDarkScreenRunnable, time);
    }

    //开启蓝牙
    private void startBluetooth() {
        if (adapter == null) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
    }

    private void showMapView() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.i("ThinCarIAOCallback","showMapView getMapFragment:"+mContext.getMapFragment());
                if(mIsSwitchMap && !mContext.isDestroyed()){
                    mContext.NotifyMapView();
                }
                if (mContext.getMapFragment() != null) {
                    mContext.getMapFragment().getMapScreenShot(new MapAnimation.GetMapShotFinish() {

                        @Override
                        public void onFinished() {
                            if (GlobalCfg.IS_CAR_CONNECT) {
                                playWelcomeAnim();
                            }
                        }
                    });
                }
                mContext.isThinCarMain = false;
            }
        }, 500);
    }

    private void showDistanceInfoForThincart(String address) {
        Intent intent = new Intent(mContext, PathService.class);
        intent.putExtra("loacal", PathService.startService);
        intent.putExtra("endPoint", address);
        mContext.startService(intent);
    }

    public void playWelcomeAnim() {
        mHasAnimPlayed = true;
        darkScreen();

        /** 动画播放，认为连接建立 */
        CacheUtils.getInstance(mContext).putBoolean(SettingCfg.IS_FIRST_CONNECT,false);

        sendLockEvent();
        sendNetStatus();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mContext.getMapFragment() != null) {
                    //mContext.getMapFragment().playAnim();
                }
            }
        }, 2000);
    }

    /**
     * 通知车机手机网络状态
     */
    private void sendNetStatus() {
        if(NetUtils.isConnected(mContext)) {
            DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_HAS_NETWORK);
        } else {
            DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_NO_NETWORK);
        }
    }

    private void sendLockEvent() {
        /** 连接时检测，如果屏幕锁定，则通知车机 */
        if (isScreenLock()) {
            DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_SCREEN_LOCK);
        }
    }

    public boolean getHasAnimPlayed() {
        return mHasAnimPlayed;
    }

    public boolean isActionForNavi() {
        return mActionForPage == ThinCarDefine.ProtocolFromCarAction.SHOW_NAVI;
    }

    /**
     * 判断手机是否锁屏，
     * @return false:没有锁屏
     *          true:锁屏
     */
    public boolean isScreenLock() {
        KeyguardManager mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();

        if (flag) {
            flag = isCurrentAppScreenState(mContext);
        }

        GlobalCfg.isScreenOff = flag;
        return flag;
    }

    public class NaviEventRunnable implements Runnable {
        private short mStartX;
        private short mStartY;
        private short mWidth;
        private short mHeight;

        public NaviEventRunnable(short startX, short startY, short width, short height) {
            mStartX = startX;
            mStartY = startY;
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void run() {
            if(mContext.isDestroyed()){
                return;
            }
            NaviFragment naviFragment = NaviFragment.getThis();
            MapFragment mapFragment = mContext.getMapFragment();
            mContext.resumeActivityNeeded();

            mHandler.removeCallbacks(mHalfScreenRunnable);

            GlobalCfg.isCarResumed = true;
            if (naviFragment != null && MapCfg.mNaAciFragmentIsNaVi) {
                naviFragment.switchWindowState(mStartX, mStartY, mWidth, mHeight);
            } else if (mapFragment != null && mapFragment.isVisible()) {
                mapFragment.switchMapWindowSize(mStartX, mStartY, mWidth, mHeight);
            } else if (mHeight == ThinCarDefine.HALF_NAVI_CAR_HEIGHT) {
                MapFragment tempFragment = MapFragment.getInstance(new Bundle());
                (mContext).setMapFragment(tempFragment);
                (mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.map_frame, tempFragment,MapFragment.class.getSimpleName()).commitAllowingStateLoss();
                mHandler.postDelayed(mHalfScreenRunnable, 300);
            }

            // GlobalCfg.IS_THIRD_APP_STATE = false;
        }
    }

    /**
     * 判断当前应用是否处于锁屏状态
     * @param context
     * @return
     */
    public static boolean isCurrentAppScreenState(Context context) {
        boolean result = false;
        try {
            Field processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            List<ActivityManager.RunningAppProcessInfo> list = ((ActivityManager) (context
                    .getSystemService(Context.ACTIVITY_SERVICE))).getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo:list) {
                if (processInfo.processName.equals(context.getPackageName()) &&
                        processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_TOP_SLEEPING) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {

        }

        return result;
    }

    @Override
    public void startScreenRecordActivity() {
        Intent intent = new Intent(mContext, ScreenRecordActivity.class);
        intent.setAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void onAoaConnectStateChange(final int state) {
        LogUtils.i("IAOACallback", "initThinCar state:" + state);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case ThinCarDefine.ConnectState.STATE_CONNECT:
                        onAoaAttach();
                        break;
                    case ThinCarDefine.ConnectState.STATE_DISCONNECT:
                        onAoaDettach();
                        break;
                }
            }
        });
    }

    @Override
    public void onAdbConnectStateChange(final int state) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case ThinCarDefine.ConnectState.STATE_CONNECT:
                        onAoaAttach();
                        break;
                    case ThinCarDefine.ConnectState.STATE_DISCONNECT:
                        onAoaDettach();
                        break;
                }
            }
        });
    }

    @Override
    public void postAdbDeviceInfo(int pinCode,int width, int height) {
        LogUtil.e("postAdbDeviceInfo","pinCode:" + pinCode + "width:" + width + "  height:" +height);
        boolean neeRestart = false;
        if (height > width) {
            /** 需要调整为竖屏*/
            if (!GlobalCfg.IS_POTRAIT) {
                CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, true);
                GlobalCfg.IS_POTRAIT = true;
                ScreenRotationUtil.stopLandService(mContext);
                neeRestart = true;
            }
        } else {
            /** 需要调整为横屏*/
            if (GlobalCfg.IS_POTRAIT) {
                CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, false);
                GlobalCfg.IS_POTRAIT = false;
                neeRestart = true;
            }
        }

        if (neeRestart) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adbReStart();
                }
            },1500);
        }
    }

    /**
     * 重新启动应用
     */
    private void adbReStart() {
        HomeActivity.isThinCar = false;
        EcoApplication.mIsRestart = true;
        EcoApplication.mIsRestarting = true;
        EcoApplication.isAdbRestart = true;
        Intent launch = mContext.getPackageManager()
                .getLaunchIntentForPackage(mContext
                        .getPackageName());
        launch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        launch.setAction(ADB_RESTART_ACTIVITY_ACTION);
        mContext.startActivity(launch);
    }

    private void processCommandEvent(int command) {
        mActionForPage = command;
        if (mContext.isDestroyed()) {
            return;
        }
        switch (command) {
            case ThinCarDefine.ProtocolFromCarAction.SHOW_NAVI:
                startHomeActivity();
                mContext.changeToNavi();
                mContext.NotifyMapToOri();
                break;
            case ThinCarDefine.ProtocolFromCarAction.SHOW_LIVE:
                mIsSwitchMap = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(Constant.SHOW_LIVE);
                    }
                }, 500);
                break;
            case ThinCarDefine.ProtocolFromCarAction.SHOW_LERADIO://音乐界面
//                mIsSwitchMap = false;
//                startHomeActivity();
//                mContext.ChangeToLeradio();

                GlobalCfg.IS_THIRD_APP_STATE = true;
                GlobalCfg.isCarResumed = false;

                String packageName = "com.hongfans.rearview";
                Intent intent = mContext.getApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(intent);

                if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                    ScreenRotationUtil.startLandService(mContext,packageName);
                }
                break;
            case ThinCarDefine.ProtocolFromCarAction.SHOW_SPEECH:
                mContext.startVoiceSearch();
                break;
            case ThinCarDefine.ProtocolFromCarAction.SHOW_LERADIO_LOCAL:
                mIsSwitchMap = false;
                startHomeActivity();
                mContext.changeToLocal();
                break;
        }
    }
}