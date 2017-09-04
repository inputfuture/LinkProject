package com.letv.auto.keypad.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;


import com.letv.auto.keypad.util.LetvLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangHaoyi on 15-2-15.
 */
public class KeyEventManager {

    static final public String TAG = "KeyEventManager";

    // External Broadcast Events
    static final public int EVT_SCAN_RESULT     = 0x01;
    static final public int EVT_CONN_STAT_CHANGE= 0x02;

    /* Detailed Scan Result Code */
    public static final int SCAN_COMPLETE       = 0x00;
    public static final int SCAN_FOUND          = 0x01;
    public static final int SCAN_FAILED         = 0x02;
    public static final int SCAN_NOTSUPPORT     = 0x03;

    public static final int MGR_SUCCESS_RESULT = 0x00;
    public static final int MGR_ERROR_GENERAL = 0x01;
    public static final int MGR_ERROR_NOTSUPPORT = 0x02;
    public static final int MGR_ERROR_NOTLISTENER = 0x03;

    public static final int MGR_NOT_INITIALIZE = 0x01;
    public static final int MGR_NOT_SUPPORT = 0x02;
    public static final int MGR_SUCCESS_CONNECTED = 0x03;
    public static final int MGR_DISCONNECTED = 0x04;

    public static final int KEYPAD_FLAGS_SPEACKER = 1;

    // EXTRA
    static final public String EXTRA_SCAN_RESULT = "com.letv.auto.service.extra.SCAN_RESULT";
    static final public String EXTRA_CONN_STATE  = "com.letv.auto.service.extra.CONN_STATE";

    public static final String ACTION_CONNECTION_STATE_CHANGED =
            "com.letv.auto.keypad.action.CONNECTION_STATE_CHANGED";

    public static final String ACTION_SPECIAL_KEY_PRESSED =
            "com.letv.auto.keypad.action.SPECIAL_KEY_PRESSED";

    public static final String ACTION_OK_KEY_PRESSED =
            "com.letv.auto.keypad.action.OK_KEY_PRESSED";

    public static final String ACTION_BACK_KEY_PRESSED =
            "com.letv.auto.keypad.action.BACK_KEY_PRESSED";

    public static final String ACTION_BATTERY_LEVEL_CHANGED =
            "com.letv.auto.keypad.action.BATTERY_LEVEL_CHANGED";

    public static final String ACTION_SCAN_FOUND =
            "com.letv.auto.keypad.action.SCAN_FOUND";

    public static final String ACTION_SCAN_COMPLETE =
            "com.letv.auto.keypad.action.SCAN_COMPLETE";

    public static final String EXTRA_KEYCODE = "com.letv.auto.keypad.extra.KEYCODE";

    public static final String EXTRA_ATTRIBUTE = "com.letv.auto.keypad.extra.ATTRIBUTE";

    public static final String EXTRA_BATTERY_LEVEL = "com.letv.auto.keypad.extra.BATTERY_LEVEL";

    public static final int KEYCODE_CUSTOM = KeypadScheduler.CUSTOM_KEY;
    public static final int KEYCODE_MODE = KeypadScheduler.MODE_KEY;

    public static final int KEYEVENT_LONG_PRESSED = 1;
    public static final int KEYEVENT_LONG_PRESSED_UP = 1 << 1;

    static private int sLeFeature = MGR_NOT_INITIALIZE;

    private BluetoothAdapter mBluetoothAdapter;
    private KeypadService mService = null;
    private BtGattCallback mGattCallback;

    static private List<ManagerListener> sManagerListenerList = new ArrayList<ManagerListener>();
    static private Object sKeyMgrLock = new Object();
    static private KeyEventManager sKeyEventMgr = null;

    public interface ManagerListener {
        public void onManagerCreated( KeyEventManager mgr );
        public void onManagerDestroy();
        public void onManagerError( int errCode );
    }

    static private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            LetvLog.w(TAG, "into onServiceConnected");
            KeypadService service = ((KeypadService.LocalBinder) binder).getService();
            synchronized (sKeyMgrLock) {
                sLeFeature = MGR_SUCCESS_CONNECTED;
                sKeyEventMgr = new KeyEventManager(service);
                for (ManagerListener listener : sManagerListenerList) {
                    listener.onManagerCreated(sKeyEventMgr);
                }
            }
            LetvLog.w(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            synchronized (sManagerListenerList) {
                sLeFeature = MGR_DISCONNECTED;
                for (ManagerListener listener : sManagerListenerList) {
                    listener.onManagerDestroy();
                }
                sKeyEventMgr = null;
            }
        }
    };

    private KeyEventManager( KeypadService service ) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mService = service;
    }

    private boolean isEnabled() {
        if (mBluetoothAdapter!=null && mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) return true;
        return false;
    }

    private boolean isValidDevice(BluetoothDevice device) {
        if (device == null) return false;
        if (mBluetoothAdapter!=null && mBluetoothAdapter.checkBluetoothAddress(device.getAddress())) return true;
        return false;
    }

    private boolean isSupportLe() {
        return sLeFeature == MGR_SUCCESS_CONNECTED;
    }

    static public void init(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            final Intent intent = new Intent(context, KeypadService.class);
            context.startService(intent);
            context.bindService(intent, mConnection, 0);
        } else {
            sLeFeature = MGR_NOT_SUPPORT;
            LetvLog.w(TAG, "Current System is Not Support Low-Energy Bluetooth");
        }
    }

    static public void release(Context context) {
        if (sLeFeature == MGR_SUCCESS_CONNECTED) {
            final Intent intent = new Intent(context, KeypadService.class);
            context.stopService(intent);
        }
    }

    public boolean scanDevice( boolean enable ) {
        if (mService != null && isEnabled() &&
                isSupportLe()) {
            return mService.scanLeDevice(enable);
        }
        return false;
    }

    public boolean connect( BluetoothDevice device ) {
        if (mService != null && isEnabled() &&
                isSupportLe() && isValidDevice(device)) {
            return mService.connectDevice(device);
        }
        return false;
    }

    public boolean disconnect(BluetoothDevice device) {
        if (mService != null && isEnabled() &&
                isSupportLe() && isValidDevice(device)) {
            return mService.disconnectDevice(device);
        }
        return false;
    }

    public int getConnectionState() {
        if (mService != null && isEnabled() && isSupportLe() ) {
            return mService.getConnectionState();
        }
        return BluetoothProfile.STATE_DISCONNECTED;
    }

    public int getBatteryLevel() {
        if (mService != null && isEnabled() && isSupportLe() ) {
            return mService.getBatteryLevel();
        }
        return -1;
    }

    public boolean registerGattCallback(BtGattCallback callback) {
        if (mService != null && isSupportLe()) {
            mService.registerGattCallback(callback);
            return true;
        }
        return false;
    }

    public boolean unregisterGattCallback( BtGattCallback callback ) {
        if( mService != null && isSupportLe() ) {
            mGattCallback = callback;
            mService.unregisterGattCallback(callback);
            return true;
        }
        return false;
    }

    public boolean close( ) {
        if( mService != null && isSupportLe() ) {
            mService.unbindService(mConnection);
            mService.unregisterGattCallback(mGattCallback);
            return true;
        }
        return false;
    }

    public void setAutoConnectDevice(BluetoothDevice device) {
        if (mService != null && isEnabled() &&
                isSupportLe() && isValidDevice(device)) {
            mService.setAutoConnectDevice(device);
        }
    }

    public String getAutoConnectDeviceAddress() {
        if (mService != null && isEnabled() &&
                isSupportLe() ) {
            return mService.getAutoConnectDeivceAddress();
        }
        return null;
    }

    public void addFlags(int flags) {
        mService.addFlags(flags);
    }

    public void delFlags(int flags) {
        mService.delFlags(flags);
    }

    public boolean checkFlags(int flags){
        return mService.checkFlags(flags);
    }

    static public void getKeyEventManager( Context context , ManagerListener listener ) {
        int errorCode = MGR_ERROR_GENERAL;
        synchronized (sKeyMgrLock) {
            if(listener == null) {
                errorCode = MGR_ERROR_NOTLISTENER;
            } else {
                switch (sLeFeature) {
                    case MGR_NOT_INITIALIZE:
                        sManagerListenerList.add(listener);
                        break;
                    case MGR_NOT_SUPPORT:
                        errorCode = MGR_ERROR_NOTSUPPORT;
                        break;
                    case MGR_SUCCESS_CONNECTED:
                        if(sKeyEventMgr!=null){
                            listener.onManagerCreated(sKeyEventMgr);
                            return;
                        }
                        break;
                }
            }
            listener.onManagerError(errorCode);
        }
    }

    static public KeyEventManager getKeyEventManager(Context context) {
        synchronized (sKeyMgrLock) {
            return sKeyEventMgr;
        }
    }
}
