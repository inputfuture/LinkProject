package com.letv.leauto.ecolink.receiver;

/**
 * Created for bug944 by shimeng on 14/3/12.
 */

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;
import java.util.List;

public class BluetoothReceiver extends BroadcastReceiver  {
    public LePlayer lePlayer;
    private static String TAG = "BluetoothReceiver" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Trace.Error("==BluetoothReceiver==","BluetoothReceiver");
        String intentAction = intent.getAction() ;
        //获得KeyEvent对象
        int action = 0;
        KeyEvent keyEvent = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        lePlayer = EcoApplication.LeGlob.getPlayer();
        lePlayer.openServiceIfNeed();
        ArrayList<MediaDetail> playerList = lePlayer.getPlayerList();


        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(keyEvent!=null) {
            Trace.Debug(TAG, "Action ---->" + intentAction + "  KeyEvent----->" + keyEvent.toString());
            action = keyEvent.getAction();
        }
        boolean isLeActiveBackground = isApplicationBroughtToBackground(context);
        Trace.Debug(TAG, "isLeActiveBackground ---->" + isLeActiveBackground );

        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isScreenLock = mKeyguardManager.inKeyguardRestrictedInputMode();
        Trace.Debug(TAG, "flag ---->" + isScreenLock );



        if (/*(isLeActiveBackground || isScreenLock)&&*/ Intent.ACTION_MEDIA_BUTTON.equals(intentAction) && action == KeyEvent.ACTION_DOWN ) {
            //获得按键字节码
            int keyCode = keyEvent.getKeyCode();
            int key = 0;
            switch (keyCode) {
                //左方向键
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                    Toast.makeText(context.getApplicationContext(),"方向左键",Toast.LENGTH_SHORT).show();
                    if (lePlayer!=null&&playerList != null && playerList.size() > 0) {
                        lePlayer.playPrev();
                    }
                    break;
                //右方向键
                case KeyEvent.KEYCODE_MEDIA_NEXT:
//                    Toast.makeText(context.getApplicationContext(),"方向右键",Toast.LENGTH_SHORT).show();
                    if (lePlayer!=null&&playerList != null && playerList.size() > 0) {
                        lePlayer.playNext(false);
                    }
                    break;
                //开始播放/停止
                case KeyEvent.KEYCODE_MEDIA_PLAY:
//                    Toast.makeText(context.getApplicationContext(),"方向播放",Toast.LENGTH_SHORT).show();
                    if (lePlayer!=null&&playerList != null && playerList.size() > 0) {
                        if (null != lePlayer.getCurrentStatus()) {
                            /** 瘦车机连接，收到播放按键直接开始播放 */
                            if (HomeActivity.isThinCar) {
                                startPlay(context);
                                return;
                            }
//                            if (lePlayer.getCurrentStatus().isPlaying == true) {
//                                stopPlay(context);
//                            } else {
                            startPlay(context);
//                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
//                    Toast.makeText(context.getApplicationContext(),"方向停止",Toast.LENGTH_SHORT).show();
                    if (lePlayer!=null&&playerList != null && playerList.size() > 0) {
                        if (null != lePlayer.getCurrentStatus()) {
                            /** 瘦车机连接，收到暂停按键直接停止播放 */
                            if (HomeActivity.isThinCar) {
                                stopPlay(context);
                                return;
                            }

//                            if (lePlayer.getCurrentStatus().isPlaying == true) {
                            stopPlay(context);
//                            } else {
//                                startPlay(context);
//                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:{

//                    Toast.makeText(context.getApplicationContext(),"播放或者停止",Toast.LENGTH_SHORT).show();
                    if (lePlayer!=null&&playerList != null && playerList.size() > 0) {
                        if (null != lePlayer.getCurrentStatus()) {
                            if (lePlayer.getCurrentStatus().isPlaying == true) {
                                stopPlay(context);
                            } else {
                                startPlay(context);
                            }
                        }
                    }



                }
                break;

                default:
                    break;
            }
            Trace.Debug(TAG, "key ---->" + key + "  keyCode----->" + keyCode);
        }else if(Intent.ACTION_SCREEN_OFF.equals(intentAction)){
            Trace.Debug(TAG, "intentAction ---->" + intentAction );
        }
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    == BluetoothAdapter.STATE_OFF){
                lePlayer.stopPlay();
                BaseActivity.isStoped=true;
                Trace.Debug("#####stop");
            }
        }

    }
    /**
     *判断当前应用程序处于前台还是后台
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            Trace.Debug(TAG, "context.getPackageName() ---->" + context.getPackageName() + "  topActivity.getPackageName()----->" + topActivity.getPackageName());
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
    private void stopPlay(Context context) {
        Intent intent1=new Intent();
        intent1.setAction("STOP_PLAY");
        context.getApplicationContext().sendBroadcast(intent1);
        lePlayer.stopPlay();
        BaseActivity.isStoped=true;
        Trace.Debug("#####stop");
    }



    private void startPlay(Context context) {
        Intent intent2=new Intent();
        intent2.setAction("START_PLAY");
        context.getApplicationContext().sendBroadcast(intent2);
        lePlayer.startPlay();
        Trace.Debug("####start");
    }
}