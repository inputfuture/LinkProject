package com.letv.leauto.ecolink.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.letv.leauto.ecolink.utils.ToastUtil;

import java.util.List;

public class HomeKeyService extends Service {
    private static final int BACK = 0x57;
    private Context mContext;
    public static String APP_PACKAGE_NAME="name";
    private volatile boolean flag=true;
    public static volatile boolean isRun=true;
    public static volatile String appPackgeName=null;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            ToastUtil.show(getApplicationContext(),"应用切换到后台");
        }
    };

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        final String name = getPackageName();


        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    if (isRun) {
                        String tmpName = getTopActivity(HomeKeyService.this);
//                        Log.d("why", "####tmpname=" + tmpName);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!tmpName.equals(name) && !tmpName.equals(appPackgeName)) {
                            Intent intetn = new Intent(mContext, ScreenRotationService.class);
                            intetn.setPackage(getPackageName());
                            intetn.putExtra(ScreenRotationService.ROTATION, 0);
                            startService(intetn);
//                            stopSelf();
                            flag=false;

                           handler.sendEmptyMessage(BACK);

                            break;
                        }
                    }else{
                        stopSelf();
                    }

                }
            }
        }.start();


        return Service.START_NOT_STICKY;
    }

    private String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            return (runningTaskInfos.get(0).topActivity.getPackageName()).toString();
        } else {
            return null;
        }
    }
}
