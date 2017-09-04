package com.letv.leauto.ecolink.ui.base;

import static com.letv.leauto.ecolink.BuildConfig.BUILD_TYPE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.leauto.link.lightcar.LogUtils;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.receiver.ScreenRotationUtil;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.qplay.PlayPCM;
import com.letv.leauto.ecolink.qplay.QPlayer;
import com.letv.leauto.ecolink.receiver.ScreenBroadcastReceiver;
import com.letv.leauto.ecolink.service.ScreenRotationService;
import com.letv.leauto.ecolink.thincar.protocol.LeRadioSendHelp;
import com.letv.leauto.ecolink.thincar.protocol.ThirdAppMsgHelp;
import com.letv.leauto.ecolink.thincar.protocol.VoiceAssistantHelp;
import com.letv.leauto.ecolink.ui.DisclaimerActivity;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.EcoActivityManager;
import com.letv.leauto.ecolink.utils.Trace;
import java.util.List;


public abstract class BaseActivity extends FragmentActivity implements
        OnClickListener {
    private  final int NOTIFY_CANCEL = 0x91;
    protected Context mContext;
//    public QPlayer qPlayer;
    public static boolean isVoice=false;
    public static boolean isStoped=false;/*用于标记切换音源之后是否恢复播放，只有在 点击pre next pause 下才强制停止，不在恢复*/
    private static final String TAG = "BaseActivity";
    private static Activity mDisclaimerActivity;
    public  NetworkConfirmDialog mNoNetDialog;
    private IntentFilter mNetIntentFilter;
    private NetReceiver mNetReceiver;
    private TelephonyManager telephonyManager;
    private OnePhoneStateListener mPhoneStateListener;

    private Handler mNotifyHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(1);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawableResource(R.drawable.bg_land);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setWindowAnimations(R.style.window_anim_style);
        mContext = this;
        initOrientation();
        initView();
        mPhoneStateListener = new OnePhoneStateListener();
        telephonyManager = (TelephonyManager)mContext.getSystemService(
                mContext.TELEPHONY_SERVICE);
        telephonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        mNetIntentFilter=new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mNetReceiver=new NetReceiver();
        mContext.registerReceiver(mNetReceiver,mNetIntentFilter);

        if (this instanceof DisclaimerActivity) {
            mDisclaimerActivity = this;
        }
        EcoActivityManager.create().addActivity(this);
    }

    private void initOrientation() {
        if (CacheUtils.getInstance(this).getBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, false)) {
            GlobalCfg.IS_POTRAIT = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            GlobalCfg.IS_POTRAIT = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }


   private static class OnePhoneStateListener extends PhoneStateListener{

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            LePlayer lePlayer=EcoApplication.LeGlob.getPlayer();
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    Trace.Info(TAG, "###[Listener]等待接电话:"+incomingNumber);
                    EcoApplication.LeGlob.getTtsController().stop();
                    lePlayer.stopPlay();
//
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Trace.Info(TAG, "####[Listener]挂断电话:"+incomingNumber);
                    if (!TextUtils.isEmpty(incomingNumber)&&!BaseActivity.isVoice&&!BaseActivity.isStoped&&lePlayer!=null&&lePlayer.getCurrentStatus()!=null&&lePlayer.getCurrentStatus().currentItem!=null){
                        Trace.Debug("####start");
                        lePlayer.startPlay();

                    }
//
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Trace.Info(TAG, "####[Listener]通话中:"+incomingNumber);
                    EcoApplication.LeGlob.getTtsController().stop();
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
//        LetvReportUtils.recordActivityStart(this.getClass().getSimpleName());
        super.onResume();
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        mNotifyHandler.removeMessages(NOTIFY_CANCEL);
        mNotifyHandler.sendEmptyMessage(NOTIFY_CANCEL);
        ScreenRotationUtil.stopLandService(mContext);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//        LetvReportUtils.recordActivityEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ActivityManager manager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            if(!runningTaskInfos.get(0).topActivity.getPackageName().equals(getPackageName())) {
                //        获取NotificationManager实例
                NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //        构造Notification.Builder 对象
                NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext);

                //        设置Notification图标
                builder.setSmallIcon(R.mipmap.ic_launcher);

                builder.setTicker("乐视车联");
                //        设置通知的题目
                builder.setContentTitle("乐视车联");
                //        设置通知的内容
                builder.setContentText("乐视车联在后台运行");
//                builder.setContentInfo(Info);
                //        设置通知可以被自动取消
                builder.setAutoCancel(true);
                //        设置通知栏显示的Notification按时间排序
                builder.setWhen(System.currentTimeMillis());
                //        设置其他物理属性，包括通知提示音、震动、屏幕下方LED灯闪烁
//                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));//这里设置一个本地文件为提示音
//                builder.setVibrate(new long[]{1000,1000,1000,1000});
                builder.setLights(Color.RED,0,1);
                Notification notification=builder.build();//notify(int id,notification对象);id用来标示每个notification
                notificationManager.notify(1,notification);
                mNotifyHandler.sendEmptyMessageDelayed(NOTIFY_CANCEL,5000);

            }
            Trace.Debug("#####notifycation");

        }

    }

    protected void showNoNetDialog(){
        Trace.Debug("****** 无网络弹窗");
        if (mNoNetDialog==null){
            mNoNetDialog=new NetworkConfirmDialog(this, R.string.no_net_message,R.string.ok, R.string.i_know);
            mNoNetDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                @Override
                public void onConfirm(boolean checked) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                    mNoNetDialog=null;
                }

                @Override
                public void onCancel() {
                    mNoNetDialog=null;


                }
            });
            mNoNetDialog.setCancelable(false);
            mNoNetDialog.show();}else{
            if (!mNoNetDialog.isShowing()){
                mNoNetDialog.show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        getWindow().setBackgroundDrawable(null);
        ScreenRotationUtil.stopLandService(mContext);
        if (mNetReceiver!=null){
            mContext.unregisterReceiver(mNetReceiver);
            mNetReceiver=null;
        }
        if (mNotifyHandler!=null){
            mNotifyHandler.removeCallbacksAndMessages(null);
            mNotifyHandler=null;
        }
        mPhoneStateListener=null;
        TelephonyManager telephony = (TelephonyManager)mContext.getSystemService(
                mContext.TELEPHONY_SERVICE);
      telephony.listen(null,0);
      mDisclaimerActivity=null;
        super.onDestroy();
        EcoActivityManager.create().finishActivity(this);
    }





    protected abstract void initView();


    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
    }



    public void closeDisclaimerActivity() {
        if (mDisclaimerActivity != null) {
            mDisclaimerActivity.finish();
            mDisclaimerActivity = null;
        }
    }

    class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //Toast.makeText(context, intent.getAction(), 1).show();
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            if (activeInfo!=null){
                if (mNoNetDialog!=null){
                    mNoNetDialog.dismiss();
                    mNoNetDialog=null;
                }
            }
        }  //如果无网络连接activeInfo为null

    }

    protected boolean isThinCar(Intent intent) {
        String action = intent.getAction();
        LogUtils.i("HomeActivity","isThinCar action:"+action);
        if (!TextUtils.isEmpty(action) && UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
            return true;
        }

        return false;
    }

    public EcoApplication getEcoApplication(){
        return (EcoApplication) this.getApplication();
    }
}
