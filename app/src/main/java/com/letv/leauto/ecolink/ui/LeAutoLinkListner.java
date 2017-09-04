package com.letv.leauto.ecolink.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.leauto.link.lightcar.CarDataParseUtil;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.leauto.sdk.SdkManager;
import com.leauto.sdk.data.CarNaviRemoteDataListener;
import com.leauto.sdk.data.DeviceInfo;
import com.leauto.sdk.data.KeyboardRemoteControlListener;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.leplayer.common.LePlayerCommon;
import com.letv.leauto.ecolink.service.ScreenRotationService;
import com.letv.leauto.ecolink.thincar.ThinCarIAOACallback;
import com.letv.leauto.ecolink.ui.fragment.MainFragment;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.Trace;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

/**
 * Created by Jerome Liu on 2015/11/5.
 */
public class LeAutoLinkListner implements CarNaviRemoteDataListener,KeyboardRemoteControlListener{
    private static final String TAG = "LeAutoLinkListner";
    /*连接车机的监听  车机拔出 插入的消息   发广播 类似 和其他模块交互*/
    private int eSend2PhoneEvent_start_voice_input = 1;//开始语音识别
    private int eSend2PhoneEvent_stop_or_quit_voice_input = 2;//停止/取消语音识别
    private int eSend2PhoneEvent_quit_voice_screen = 22;//取消语音识别
    //手机端发送给车机端的消息：
    private int E_Internal_Event_MiCarApp_Speech_Recognition_Result = 0x601;//参数1:成功2 失败

    //瘦机车导航按钮


    private Context mContext;
    private Toast mToast;
    private Handler mainHandler;
    private ThinCarIAOACallback mIAOACallback;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mToast.setText((String) msg.obj);
            mToast.show();
        }
    };

    public LeAutoLinkListner(Context context, Handler handler,ThinCarIAOACallback callback) {
        mContext = context.getApplicationContext();
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        mainHandler = handler;
        mIAOACallback = callback;
    }

    /**
     * @param
     */
    private void showToast(String str) {
        mHandler.sendMessage(Message.obtain(null, 0, str));
    }

    @Override
    public void onDongFengControl(int key) {
                    /* *收到东风车机发来的方控事件
                    * key=1开始语音识别
                    * key=2 停止语音识别
                    */
//        showToast("onDongFengControl:key="+key);
        EcoApplication.instance.setShowScreen(key);
        Intent broadcast=new Intent();
        switch (key) {
            case 1://开始语音识别
               broadcast.setAction(LePlayerCommon.BROADCAST_ACTION_VOICERECORD);
                broadcast.putExtra(LePlayerCommon.BROADCAST_EXTRA_VOICERECORD, LePlayerCommon.BROADCAST_EXTRA_VOICERECORD_BEGIN);
                EcoApplication.instance.sendBroadcast(broadcast);

                break;
            case 2:;//停止/取消语音识别
                mainHandler.sendEmptyMessage(MessageTypeCfg.MSG_START_VOICEIN);
                broadcast.setAction(LePlayerCommon.BROADCAST_ACTION_VOICERECORD);
                broadcast.putExtra(LePlayerCommon.BROADCAST_EXTRA_VOICERECORD, LePlayerCommon.BROADCAST_EXTRA_VOICERECORD_END);
                EcoApplication.instance.sendBroadcast(broadcast);

                break;
            case 3://地图规划
                mainHandler.sendEmptyMessage(MessageTypeCfg.MSG_ROUND_GUIDE);
            case 4://主页
                mainHandler.sendEmptyMessage(MessageTypeCfg.MSG_MAIN);
            case 5://电台
                mainHandler.sendEmptyMessage(MessageTypeCfg.MSG_MUSIC);
            case 6://电话
                mainHandler.sendEmptyMessage(MessageTypeCfg.MSG_PHONE);

            default:
                break;
        }


//                         else if (eSend2PhoneEvent_quit_voice_screen == key) {
//                            mainHandler.sendEmptyMessage(MessageTypeCfg.MSG_START_VOICEIN);
//                            Intent broadcast = new Intent(LePlayerCommon.BROADCAST_ACTION_VOICERECORD);
//                            broadcast.putExtra(LePlayerCommon.BROADCAST_EXTRA_VOICERECORD, LePlayerCommon.BROADCAST_EXTRA_VOICERECORD_END);
//                            EcoApplication.instance.sendBroadcast(broadcast);
//                        }
    }
    @Override
    public void NotifyConnectStatus(int state) {

        if (SdkManager.LINK_CONNECTED == state) {
            GlobalCfg.IS_CAR_CONNECT = true;

            DataSendManager.getInstance().notifyCarConnect();
            LetvReportUtils.reportConnectStartGps(EcoApplication.getInstance().getLongitude()+"",EcoApplication.getInstance().getLatitude()+"");
//            Config.isConnectedVehicle=true;
        }
        if (SdkManager.LINK_DISCONNECTED == state) {
//            Config.isConnectedVehicle=false;
            DataSendManager.getInstance().notifyCarDisConnect();
            mIAOACallback.onAdbConnectStateChange(ThinCarDefine.ConnectState.STATE_DISCONNECT);
            Settings.System.putInt(mContext.getContentResolver(),"show_touches", 0);
            LetvReportUtils.reportConnectEnd();
            LetvReportUtils.reportConnectEndGps(EcoApplication.getInstance().getLongitude()+"",EcoApplication.getInstance().getLatitude()+"");

            Intent intetn = new Intent(mContext, ScreenRotationService.class);
            intetn.putExtra(ScreenRotationService.ROTATION, ScreenRotationService.COMMAND_PORTRAIT);
            mContext.startService(intetn);


//            showToast("断开1111");
            GlobalCfg.IS_CAR_CONNECT = false;
            //隐藏车厂的logo
            GlobalCfg.CAR_FACTORY_NAME = null;
            GlobalCfg.IS_DONGFEN=false;

            mainHandler.sendEmptyMessage(MessageTypeCfg.MSG_CAR_UNCONNECT);
            Intent intent1=new Intent();
            intent1.setAction(MainFragment.HIDE);
            mContext.sendBroadcast(intent1);
//                if (GlobalCfg.IS_POTRAIT) {
//                    showToast("断开切换为竖屏");
//                    GlobalCfg.IS_POTRAIT = true;
//                    //如果此刻程序还在运行则直接切换到竖屏
//                    if (DeviceUtils.isApplicationBroughtToFront(mContext)) {
//                       // reStart();
//                    } else {
//                        //否则记住当时的状态
//                        GlobalCfg.IS_APPLICATION_FRONT = false;
//                    }
//                }
        }

    }

    /**
     * 玉林SDK新增接口和上层进行交互
     * @param command
     * @param parasm
     * @param params1
     */
    @Override
    public void remoteDataListener(int command, int parasm, int params1) {
        LogUtils.i(TAG, "remoteDataListener command : " + command);
    }

    @Override
    public void remoteDataListener(byte[] bytes, int i) {
        ByteBuffer bf = ByteBuffer.wrap(bytes,0,bytes.length);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        byte header1 = bf.get();
        byte header2 = bf.get();
        short appid = bf.getShort();

        CarDataParseUtil.parseCarData(appid, bytes, mIAOACallback);
    }

    Boolean leAutoIsPotrait = false;
    /**
     * deviceInfo 的回调
     */
    @Override
    public void onDeviceInfo(DeviceInfo deviceInfo) {
        if ( GlobalCfg.IS_CAR_CONNECT&&deviceInfo!=null){
            String os = null;
            switch (deviceInfo.getOsType()){
                case DeviceInfo.ANDROID:
                    os="Android";
                    break;
                case DeviceInfo.Linux:
                    os="Linux";
                    break;
                case DeviceInfo.QNX:
                    os="QNX";
                    break;
                case DeviceInfo.WINCE:
                    os="WINCE";
                    break;
            }
//            event.addProp("phone_id", phone_id);
//            event.addProp("ip", ip);
//            event.addProp("OBU_id", OBU_id);
//            event.addProp("OBU_MAC", OBU_MAC);
            LetvReportUtils.reportConnectStart(android.os.Build.BRAND,android.os.Build.MODEL,/*android.os.Build.USER*/"Android",android.os.Build.VERSION.RELEASE,EcoApplication.getInstance().getProvince(),EcoApplication.getInstance().getCity()
                    ,deviceInfo.getScreenWidth()+"",deviceInfo.getScreenHeight()+"",os,deviceInfo.getOsVersion()
                    ,deviceInfo.getDeviceId(),deviceInfo.getOem());
        }
        //获得model信息
        String  model=deviceInfo.getModel();
        if(null!=model && model.equals("OxFF")){
            CacheUtils.getInstance(mContext).putBoolean(Constant.ISCUTCAR,true);
        }
        //显示对应车厂的logo
//        showToast(deviceInfo.getManufacturer());
        GlobalCfg.CAR_FACTORY_NAME = deviceInfo.getManufacturer();
        if (deviceInfo.getManufacturer().toLowerCase().contains("dongfeng")){
            GlobalCfg.IS_DONGFEN=true;
        }else{
            GlobalCfg.IS_DONGFEN=false;
        }

        //((HomeActivity) mContext).showLogo();


        mainHandler.sendEmptyMessage(MessageTypeCfg.MSG_CAR_CONNECT);
        Intent intent1=new Intent();
        intent1.setAction(MainFragment.SHOW);
        mContext.sendBroadcast(intent1);

        int height = deviceInfo.getScreenHeight();
        int width = deviceInfo.getScreenWidth();
        if (height > width) {
            leAutoIsPotrait = true;
            GlobalCfg.CAR_IS_lAND=false;
        } else {;
            GlobalCfg.CAR_IS_lAND=true;
            leAutoIsPotrait = false;
        }
        /**
         * 默认是竖屏的时候,如果是横屏,则重启应用;默认是横屏的时候,如果是竖屏,则重启应用.
         */
        if (leAutoIsPotrait && !GlobalCfg.IS_POTRAIT) {
//            showToast("切换到竖屏!");
            CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, true);
            reStart();
        } else if (!leAutoIsPotrait && !GlobalCfg.IS_POTRAIT) {
            CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, false);
//            showToast(deviceInfo.getManufacturer() + "保持!");
        } else if (leAutoIsPotrait && GlobalCfg.IS_POTRAIT) {
            CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, true);
//            showToast(deviceInfo.getManufacturer() + "保持!");
        } else {
//            showToast("切换到横屏!");
            CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, false);
            reStart();
        }
        Settings.System.putInt(mContext.getContentResolver(),"show_touches", 1);
       
    /**
     *
     * ANDROID = 1;
     * WINCE = 2;
     * Linux = 3;
     * QNX = 4;
     */
    Trace.Error("EcoLink","车机OS："+deviceInfo.getOsType());
}

    public boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 重新启动应用
     */
    public void reStart() {
        EcoApplication.mIsRestart=true;
        Intent launch = mContext.getPackageManager()
                .getLaunchIntentForPackage(mContext
                        .getPackageName());
        launch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(launch);
    }

    private void dealWithBlueInfo(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            LogUtils.i("TAG", "车机返回的信息：" + jsonObject.toString());

            String name = jsonObject.optString("AVN_BT_NAME");

            LogUtils.i("TAG", "车机返回名称：" + name);
            int ADDR_0 = jsonObject.optInt("AVN_BT_ADDR_0");
            int ADDR_1 = jsonObject.optInt("AVN_BT_ADDR_1");
            int ADDR_2 = jsonObject.optInt("AVN_BT_ADDR_2");
            int ADDR_3 = jsonObject.optInt("AVN_BT_ADDR_3");
            int ADDR_4 = jsonObject.optInt("AVN_BT_ADDR_4");
            int ADDR_5 = jsonObject.optInt("AVN_BT_ADDR_5");

            String s5= Integer.toHexString(ADDR_5);
            String s4= Integer.toHexString(ADDR_4);
            String s3= Integer.toHexString(ADDR_3);
            String s2= Integer.toHexString(ADDR_2);
            String s1= Integer.toHexString(ADDR_1);
            String s0= Integer.toHexString(ADDR_0);

            StringBuffer buffer=new StringBuffer();
            buffer.append(s5);
            buffer.append(":"+s4);
            buffer.append(":"+s3);
            buffer.append(":"+s2);
            buffer.append(":"+s1);
            buffer.append(":"+s0);

            LogUtils.i("TAG",buffer.toString().toUpperCase());

            mIAOACallback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.NOTIFY_BLUETOOTH_MAC, buffer.toString().toUpperCase());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
