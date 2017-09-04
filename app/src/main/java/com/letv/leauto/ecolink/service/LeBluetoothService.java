package com.letv.leauto.ecolink.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.letv.leauto.ecolink.receiver.BluetoothReceiver;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by shimeng on 14/3/15.
 */
public class LeBluetoothService extends Service{
    private final IBinder binder = new BluetoothBinder();
    private final String TAG = "LeBluetoothService";
    AudioManager audioManager;

    ComponentName mRemoteControlClientReceiverComponent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       Trace.Info(TAG, "onBind ---->" + intent);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
       Trace.Info(TAG, "onUnbind");
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
       Trace.Info(TAG, "onDestroy ---->");
        super.onDestroy();
//取消注册
        audioManager.unregisterMediaButtonEventReceiver(mRemoteControlClientReceiverComponent);


    }
	@Override
	    public void onCreate() {
       Trace.Info(TAG, "onCreate ---->");
        super.onCreate();

        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
//注册接收的Receiver,只有BluetoothReceiver能够接收到了，它是出于栈顶的。
        mRemoteControlClientReceiverComponent = new ComponentName(this, BluetoothReceiver.class);
//注册MediaButton
        audioManager.registerMediaButtonEventReceiver(mRemoteControlClientReceiverComponent);
        // AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        //shimeng add for bug 944 ,20160312,begin
        //AudioManager注册一个MediaButton对象
       // ComponentName bluetoothReceiver = new ComponentName(getPackageName(),BluetoothReceiver.class.getName());
        //只有China_MBReceiver能够接收到了，它是出于栈顶的。
        //不过，在模拟上检测不到这个效果，因为这个广播是我们发送的，流程不是我们在上面介绍的。
        // audioManager.registerMediaButtonEventReceiver(bluetoothReceiver);
    }
    public class BluetoothBinder extends Binder{
        public LeBluetoothService getService(){
           Trace.Info(TAG, "getService enter");
            return LeBluetoothService.this;
        }
    }

}
