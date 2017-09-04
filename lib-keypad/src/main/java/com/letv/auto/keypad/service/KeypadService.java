package com.letv.auto.keypad.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;


//import com.letv.auto.bdvoice.tts.TtsService;
import com.letv.auto.keypad.R;
import com.letv.auto.keypad.activity.ResultActivity;
import com.letv.auto.keypad.interfaces.StopServicesJobs;
import com.letv.auto.keypad.util.LetvLog;
import com.letv.auto.keypad.util.LetvSettings;


/**
 * Created by ZhangHaoyi on 15-2-10.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class KeypadService extends Service {

    static final private boolean DBG = true;

    static final private String TAG = "KeyEventService";

    private static final String KEYEVENT_SHARED_PREFERENCES = "KeyEventExternalData";

    private static final String AUTO_CONNECT_DEVICE = "AutoConnectDevice";

    private static final String DEFAULT_DEVICE_VALUE = "00:00:00:00:00";

    private SharedPreferences mPreferences;

    private SharedPreferences.Editor mEditor;

    private IBinder mBinder = new LocalBinder();

    private BluetoothAdapter mBluetoothAdapter;

    private KeypadScheduler mScheduler;

    private NotificationManager mNotificationManager;

    private boolean mHasLeFeature = false;

    private BtGattCallback mBtGattCallback = new BtGattCallback() {
        @Override
        public void onGattReceive(int event, Bundle bundle) {
            switch(event) {
                case KeyEventManager.EVT_CONN_STAT_CHANGE:
                    int curState = bundle.getInt(BluetoothProfile.EXTRA_STATE);
                    if (curState == BluetoothProfile.STATE_CONNECTED) {
                        keypadConnected();
                    } else {
                        keypadDisconnected();
                    }
                    break;
            }
        }
    };

    class KeyEventBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int curtStat = bundle.getInt(BluetoothAdapter.EXTRA_STATE);
                if (curtStat == BluetoothAdapter.STATE_ON) {
                    autoConnectDevice(2000);
                } else if ( curtStat == BluetoothAdapter.STATE_OFF ) {
                    mScheduler.disableBluetooth();
                }
            }
        }
    }

    private BroadcastReceiver mReceiver = new KeyEventBroadcastReceiver();

    class LocalBinder extends Binder {
        KeypadService getService() {
            return KeypadService.this;
        }
    }

    private void playConnectionStateTTS(boolean isConnected) {
        int resId = R.string.keypad_connected;
        if (!isConnected) {
            resId = R.string.keypad_disconnected;
        }
      /*  TtsService.getInstance().
                playKeyPadTTS(getString(resId), TtsService.TTS_TYPE_NORMAL, null);*/
    }

    private void notifyKeypadConnected() {
        if (mNotificationManager == null) {
            LetvLog.w(TAG, "NOTIFY ERROR: mNotificationManager is NULL");
        }
        // First: fetch relevant text string
        final String tickerText = getString(R.string.keypad_conn_success);
        final String contentTitle = getString(R.string.keypad_status);
        final String contentText = getString(R.string.keypad_connected);

        // Second: create pending intent
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        //intent.setClassName("com.letv.auto", "com.letv.auto.home.ui.AutoHomeActivity");
        intent.setClass(KeypadService.this, ResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(KeypadService.this, 0, intent, 0);

        // Finally: create Notification object
//        Notification noti = new Notification.Builder(KeypadService.this)
//                .setContentTitle(contentTitle)
//                .setContentText(contentText)
//                .setSmallIcon(R.drawable.notification_ic_keypad)
//                .setTicker(tickerText)
//                .setOngoing(true)
//                .setContentIntent(pi)
//                .build();
//        mNotificationManager.notify(R.drawable.notification_ic_keypad, noti);
    }

    private void cancelKeypadConnected() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(R.drawable.notification_ic_keypad);
        } else {
            LetvLog.w(TAG,"CANCEL ERROR: mNotificationManager is NULL");
        }
    }

    private void autoConnectDevice(long delay) {
        String value = mPreferences.getString(AUTO_CONNECT_DEVICE, DEFAULT_DEVICE_VALUE);
        if (value.equals(DEFAULT_DEVICE_VALUE)) {
            LetvLog.d(TAG, "Not Record AutoConnectDevice");
            return;
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(value);
        if (device == null) {
            LetvLog.w(TAG, "Can not get Remote Device(" + value + ")");
            return;
        }
        connectDevice(device, delay);
    }

    private void keypadConnected() {
        notifyKeypadConnected();
        playConnectionStateTTS(true);
        LetvSettings.setKeypadConnectionState(KeypadService.this, true);
    }

    private void keypadDisconnected() {
        keypadCleanup();
        playConnectionStateTTS(false);
    }

    private void keypadCleanup() {
        cancelKeypadConnected();
        LetvSettings.setKeypadConnectionState(KeypadService.this, false);
    }

    private void initService() {
        mPreferences = getSharedPreferences(KEYEVENT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        //Initialize Receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        //Initialize KeyEventScheduler
        mScheduler = new KeypadScheduler(this);

        //Initialize Notification
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Initialize Gatt Callback
        mScheduler.registerGattCallback(mBtGattCallback);

        //Clean Job
        keypadCleanup();

        // Auto Connect Device
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            autoConnectDevice(500);
        }
    }

    @Override
    public void onCreate() {
        if(DBG) {
            LetvLog.d(TAG,"into onCreate:" + (long)this.hashCode() );
        }
        StopServicesJobs.addServiceNameToListWhenStart(KeypadService.class.getName());
        mHasLeFeature = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mHasLeFeature) {
            initService();
        } else {
            LetvLog.w(TAG,"Current System Not Support Low-Energy Bluetooth");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(DBG) {
            LetvLog.d(TAG,"into onStartCommand:" + (long)this.hashCode() );
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DBG) {
            LetvLog.d(TAG, "into onDestroy");
        }
        if (mScheduler != null) {
            mScheduler.unregisterGattCallback(mBtGattCallback);
            mScheduler = null;
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        keypadCleanup();
        super.onDestroy();
        StopServicesJobs.removeServiceNameToListWhenDestory(KeypadService.class.getName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    protected BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public boolean scanLeDevice( boolean enable ) {
        return mScheduler.scanLeDevice(enable);
    }

    private boolean connectDevice(BluetoothDevice device ,long defer) {
        return mScheduler.connectDevice(device, defer);
    }

    public boolean connectDevice(BluetoothDevice device) {
        return connectDevice(device,0);
    }

    public boolean disconnectDevice( BluetoothDevice device ) {
        return mScheduler.disconnectDevice(device);
    }

    public int getConnectionState() {
        return mScheduler.getConnectionState();
    }

    public int getBatteryLevel() {
        return mScheduler.getBatteryLevel();
    }

    public void registerGattCallback( BtGattCallback callback ) {
        mScheduler.registerGattCallback(callback);
    }

    public void unregisterGattCallback(BtGattCallback callback) {
        mScheduler.unregisterGattCallback(callback);
    }

    public void setAutoConnectDevice(BluetoothDevice device) {
        if (device != null) {
            mEditor.putString(AUTO_CONNECT_DEVICE, device.getAddress());
            mEditor.commit();
        }
    }

    public String getAutoConnectDeivceAddress() {
        String address = mPreferences.getString(AUTO_CONNECT_DEVICE, DEFAULT_DEVICE_VALUE);
        if (address.equals(DEFAULT_DEVICE_VALUE)) {
            return null;
        }
        return address;
    }

    public void addFlags(int flags) {
        mScheduler.addFlags(flags);
    }

    public void delFlags(int flags) {
        mScheduler.delFlags(flags);
    }

    public boolean checkFlags(int flags){
        return mScheduler.checkFlags(flags);
    }
}
