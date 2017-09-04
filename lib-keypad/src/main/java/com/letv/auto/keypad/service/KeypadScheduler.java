package com.letv.auto.keypad.service;

import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import android.util.Pair;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.letv.auto.keypad.util.LetvLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ZhangHaoyi on 15-2-10.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class KeypadScheduler {

    static final private String TAG = "KeyEventScheduler";

    static final private boolean DBG = LetvLog.DEBUG;

    // External Actions
    static final private int ACTION_ENABLE_BLUETOOTH = 0x01;
    static final private int ACTION_DISABLE_BLUETOOTH = 0x02;
    static final private int ACTION_CONNECT = 0x03;
    static final private int ACTION_DISCONNECT = 0x04;

    // Internal Actions
    static final private int ACTION_IND_EVENT = 0x1000; // 4096
    static final private int ACTION_REQ_EVENT = 0x1001; // 4097
    static final private int ACTION_DEFER_CLOSE = 0x1002; // 4098
    static final private int ACTION_CANCEL_CONNECT = 0x1003; // 4099
    static final private int ACTION_SWITCH_MODE = 0x1004; // 4100
    static final private int ACTION_RECONNECT = 0x1005; // 4101
    static final private int ACTION_SCAN_FOUND = 0x1006;
    static final private int ACTION_SCAN_COMPLETE = 0x1007;

    // Event types for General Internal Event Message
    static final private int EVENT_TYPE_NONE = 0x00;

    // Event types for Internal ACTION_IND_EVENT Message
    static final private int CONN_STATE_CHANGE = 0x01;
    static final private int DISCOVERY_RESULT = 0x02;
    static final private int DATA_AVAILABLE = 0x03;
    static final private int WRITE_DESCRIPTOR_FINISH = 0x04;

    // Event types for Internal ACTION_REQ_EVENT Message
    static final private int DISCOVERY_SERVICE = 0x01;
    static final private int START_READ_CHARAC = 0x02;
    static final private int STOP_READ_CHARAC = 0x03;
    static final private int SET_CHARAC_NOTIFY = 0x04;
    static final private int START_INIT_GATT = 0x05;

    static final private int DEVICE_KEYCODE_1 = 0x37;
    static final private int DEVICE_KEYCODE_2 = 0x38;
    static final private int DEVICE_KEYCODE_3 = 0x47;
    static final private int DEVICE_KEYCODE_4 = 0x48;
    static final private int DEVICE_KEYCODE_5 = 0x57;
    static final private int DEVICE_KEYCODE_6 = 0x58;
    static final private int DEVICE_KEYCODE_7 = 0x67;
    static final private int DEVICE_KEYCODE_8 = 0x68;

    static final public int CUSTOM_KEY = DEVICE_KEYCODE_3;
    static final public int MODE_KEY = DEVICE_KEYCODE_5;

    static final private boolean AUTO_CONNECT = false;

    private static final int KEY_UPDOWN_MASK = (1 << 6);
    private static final int KEY_INDEX_MASK = 0x3F;

    private static final int KEYEVENT_LONG_PRESSED = KeyEventManager.KEYEVENT_LONG_PRESSED;
    private static final int KEYEVENT_LONG_PRESSED_UP = KeyEventManager.KEYEVENT_LONG_PRESSED_UP;

    private static final int DISCONNECT_TIMEOUT = 60000;

    private static final UUID KEYPAD_SERVICE_UUID = UUID.fromString("0000fef5-0000-1000-8000-00805f9b34fb");
    private static final UUID KEYPAD_CHARAC_UUID = UUID.fromString("5f78df94-798c-46f5-990a-b3eb6a065c88");

    private static final UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private static final UUID BATTERY_CHARAC_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    private SmHandler mSmHandler;

    private KeyEventThread mKeyEventThread;

    private volatile int mBatteryLevel = 0;

    private List<Pair<UUID,UUID>> mAvailableUuids = new ArrayList<Pair<UUID,UUID>>();

    private IState mDisconnected;
    private IState mConnecting;
    private IState mConnected;

    private IKeyEventMode mNormalMode;
    private IKeyEventMode mCallMode;

    private int mFlags = 0;

    private boolean mIsDeferClose;

    private KeypadService mService;

    private BluetoothAdapter mBluetoothAdapter;

    private KeypadScanner mKeypadScanner;


    private List<BtGattCallback> mGattCallbacks = new ArrayList<BtGattCallback>();

    private BluetoothGatt mBluetoothGatt;

    private KeypadScanner.ScannerCallback mScannerCallback = new KeypadScanner.ScannerCallback() {
        @Override
        public boolean onHandle(int status, BluetoothDevice device) {
            broadcastScanResult(status,device);
            return true;
        }
    };

    private interface IKeyEventMode {
        boolean handle(byte[] data);
    }

    private class NormalMode implements IKeyEventMode {

        private int mLastKeyCode = 0;
        private boolean mLongPressed = false;

        Map<Integer, Integer> mKeyEventMapping = new HashMap<Integer, Integer>();

        NormalMode() {
            mKeyEventMapping.put(DEVICE_KEYCODE_4, KeyEvent.KEYCODE_DPAD_UP);
            mKeyEventMapping.put(DEVICE_KEYCODE_6, KeyEvent.KEYCODE_DPAD_DOWN);
            mKeyEventMapping.put(DEVICE_KEYCODE_8, KeyEvent.KEYCODE_DPAD_LEFT);
            mKeyEventMapping.put(DEVICE_KEYCODE_1, KeyEvent.KEYCODE_DPAD_RIGHT);
            mKeyEventMapping.put(DEVICE_KEYCODE_7, KeyEvent.KEYCODE_DPAD_CENTER);
            mKeyEventMapping.put(DEVICE_KEYCODE_2, KeyEvent.KEYCODE_BACK);
            mKeyEventMapping.put(DEVICE_KEYCODE_5, KeyEvent.KEYCODE_BUTTON_1); // Mode Key
            mKeyEventMapping.put(DEVICE_KEYCODE_3, KeyEvent.KEYCODE_BUTTON_2); // Custom Key
        }

        private boolean isSpecialKey(byte[] data) {
            int keyInfo = data[0];
            int keyCode = data[1];
            switch (keyCode) {
                //case DEVICE_KEYCODE_5:
               // case DEVICE_KEYCODE_3:
//                    return true;
                default:
                    return false;
            }
        }

        private boolean isOkOrBackKey(byte[] data) {
            int keyInfo = data[0];
            int keyCode = data[1];
            if ((DEVICE_KEYCODE_7 == keyCode) || (DEVICE_KEYCODE_2 == keyCode)) {
                return true;
            } else {
                return false;
            }
        }

        private void broadcastSpecialKeyPressed(int keyCode,int keyAttrs) {
            Intent intent = new Intent(KeyEventManager.ACTION_SPECIAL_KEY_PRESSED);
            intent.putExtra(KeyEventManager.EXTRA_KEYCODE, keyCode);
            intent.putExtra(KeyEventManager.EXTRA_ATTRIBUTE,keyAttrs);
            mService.sendBroadcast(intent);
        }

        private void broadcastOkOrBackKeyPressed(int keyCode,int keyAttrs) {
            Intent intent;
            if (DEVICE_KEYCODE_7 == keyCode) {
                intent = new Intent(KeyEventManager.ACTION_OK_KEY_PRESSED);
            } else {
                intent = new Intent(KeyEventManager.ACTION_BACK_KEY_PRESSED);
            }
            intent.putExtra(KeyEventManager.EXTRA_KEYCODE, keyCode);
            intent.putExtra(KeyEventManager.EXTRA_ATTRIBUTE,keyAttrs);
            mService.sendBroadcast(intent);
        }

        private KeyEvent convertDataToKeyEvent(int keyCode, int action, int index) {
            int flags = 0;
            Integer realKeyCode = mKeyEventMapping.get(keyCode);
            if (realKeyCode == null) {
                LetvLog.w(TAG, "Not Found Relevant KeyCode(" + keyCode + ")");
                return null;
            }

            if (realKeyCode == mLastKeyCode && action == KeyEvent.ACTION_DOWN && index > 0) {
                flags = KeyEvent.FLAG_LONG_PRESS;
            }
            return new KeyEvent(0, 0, action, realKeyCode, index, 0,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, flags);
        }

        @Override
        public boolean handle(byte[] data) {
            if (data != null && data.length > 1) {
                int keyCode = data[1];
                int keyAction = (data[0] & KEY_UPDOWN_MASK) != 0 ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP;
                int keyIndex = data[0] & KEY_INDEX_MASK;
                int keyAttr = 0;

                if (!isSpecialKey(data)) {
                    mLongPressed = false;
                    KeyEvent keyEvent = convertDataToKeyEvent(keyCode, keyAction, keyIndex);
                    if (keyEvent != null) {
                        if (DBG) {
                            LetvLog.d(TAG, "add keyEvent:" + keyEvent.toString());
                        }
                        mKeyEventThread.addKeyEvent(keyEvent);
                    }
                }

                if(isSpecialKey(data) || isOkOrBackKey(data)) {
                    if (keyAction == KeyEvent.ACTION_DOWN) {
                        if (mLastKeyCode == keyCode && !mLongPressed && keyIndex > 0) {
                            keyAttr = KEYEVENT_LONG_PRESSED;
                            mLongPressed = true;
                        } else {
                            mLastKeyCode = keyCode;
                            return true;
                        }
                    } else {
                        if (mLongPressed) {
                            mLongPressed = false;
                            keyAttr = KEYEVENT_LONG_PRESSED | KEYEVENT_LONG_PRESSED_UP;
                        }
                    }
                    if (isOkOrBackKey(data)) {
                        broadcastOkOrBackKeyPressed(keyCode, keyAttr);
                    } else {
                        broadcastSpecialKeyPressed(keyCode, keyAttr);
                    }
                }

                mLastKeyCode = keyCode;
                return true;
            }
            return false;
        }
    }

    private class CallMode implements IKeyEventMode {

        AudioManager mAudioManager;
        TelephonyManager mTelephonyManager;

        private void adjustVolume(int direction) {
            if (mAudioManager != null) {
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, direction, 0);
            }
        }

        private void rejectCall(){
            Method getITelephonyMethod;
            try {
                getITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony", (Class[]) null);
                getITelephonyMethod.setAccessible(true);
                Object telephonyObj = getITelephonyMethod.invoke(mTelephonyManager, (Object[]) null);
                telephonyObj.getClass().getMethod("endCall", new Class[] { }).invoke(telephonyObj, new Object[] {});
            } catch (SecurityException e) {
                LetvLog.w(TAG, "rejectCall Error! THROW EXCEPTION:" + e.getMessage());
            } catch (NoSuchMethodException e) {
                LetvLog.w(TAG, "rejectCall Error! THROW EXCEPTION:" + e.getMessage());
            } catch (IllegalArgumentException e) {
                LetvLog.w(TAG, "rejectCall Error! THROW EXCEPTION:" + e.getMessage());
            } catch (IllegalAccessException e) {
                LetvLog.w(TAG, "rejectCall Error! THROW EXCEPTION:" + e.getMessage());
            } catch (InvocationTargetException e) {
                LetvLog.w(TAG, "rejectCall Error! THROW EXCEPTION:" + e.getMessage());
            }
        }

        private void switchSpeaker() {
            if (mAudioManager != null) {
                if (mAudioManager.isSpeakerphoneOn()) {
                    mAudioManager.setSpeakerphoneOn(false);
                } else {
                    mAudioManager.setSpeakerphoneOn(true);
                }
            }
        }

        private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                Message msg;
                if (DBG) {
                    LetvLog.d(TAG, "into onCallStateChanged:" + state + " incomingNumber:" + incomingNumber);
                }
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        delFlags(KeyEventManager.KEYPAD_FLAGS_SPEACKER);
                        msg = mSmHandler.obtainMessage(ACTION_SWITCH_MODE);
                        msg.obj = mNormalMode;
                        mSmHandler.removeMessages(ACTION_SWITCH_MODE);
                        mSmHandler.sendMessageAtFrontOfQueue(msg);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (checkFlags(KeyEventManager.KEYPAD_FLAGS_SPEACKER)) {
                            mSmHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mTelephonyManager != null && mAudioManager != null
                                            && mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK
                                            && !mAudioManager.isSpeakerphoneOn()) {
                                        mAudioManager.setSpeakerphoneOn(true);
                                    }

                                }
                            }, 2500);
                        }
                        // no break
                    case TelephonyManager.CALL_STATE_RINGING:
                        msg = mSmHandler.obtainMessage(ACTION_SWITCH_MODE);
                        msg.obj = mCallMode;
                        mSmHandler.removeMessages(ACTION_SWITCH_MODE);
                        mSmHandler.sendMessageAtFrontOfQueue(msg);
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        private void answerCall() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                answerCallLater21();
            } else {
                answerCallEarlier21();
            }
        }
        private void answerCallLater21() {
            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
            mKeyEventThread.addKeyEvent(event);
            event = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
            mKeyEventThread.addKeyEvent(event);
        }

        private void answerCallEarlier21(){
            Method getITelephonyMethod;
            try {
                getITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony", (Class[]) null);
                getITelephonyMethod.setAccessible(true);
                TelephonyManager mTelephonyManager = (TelephonyManager) mService.getSystemService(Context.TELEPHONY_SERVICE);
                Object mITelephony =  getITelephonyMethod.invoke(mTelephonyManager, (Object[]) null);
                mITelephony.getClass().getMethod("answerRingingCall", new Class[] { }).invoke(mITelephony, new Object[] {});
            } catch (InvocationTargetException e) {
                try {
                    LetvLog.w(TAG, "for version 4.1 or larger");
                    Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
                    intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
                    mService.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
                } catch (Exception e2) {
                    LetvLog.e(TAG, "another exception :(" + e2.getMessage() + ")");
                    Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
                    meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                    mService.sendOrderedBroadcast(meidaButtonIntent, null);
                }
            } catch (Exception e) {
                LetvLog.w(TAG, "answerCall Error! THROW EXCEPTION:" + e.getMessage());
            }
        }

        CallMode() {
            mTelephonyManager = (TelephonyManager) mService.getSystemService(Context.TELEPHONY_SERVICE);
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            mAudioManager = (AudioManager) mService.getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public boolean handle(byte[] data) {
            int keyInfo = data[0];
            int keyCode = data[1];
            if ((keyInfo & KEY_UPDOWN_MASK) != 0) {
                return true;
            }
            switch (keyCode) {
                case DEVICE_KEYCODE_4: //Up
                    adjustVolume(AudioManager.ADJUST_RAISE);
                    break;
                case DEVICE_KEYCODE_6: //Down
                    adjustVolume(AudioManager.ADJUST_LOWER);
                    break;
                case DEVICE_KEYCODE_2: //Back
                    rejectCall();
                    break;
                case DEVICE_KEYCODE_7: //Center
                    int callState = mTelephonyManager.getCallState();
                    if (callState == TelephonyManager.CALL_STATE_OFFHOOK) {
                        switchSpeaker();
                    } else if (callState == TelephonyManager.CALL_STATE_RINGING) {
                        addFlags(KeyEventManager.KEYPAD_FLAGS_SPEACKER);
                        answerCall();
                    }
                    break;
            }
            return false;
        }
    }

    private class KeyEventThread extends Thread {
        boolean mIsRunning = false;
        boolean mIsStop = false;
        List<KeyEvent> mKeyEventQueue = new ArrayList<KeyEvent>();
        Instrumentation mInstrumentation = new Instrumentation();

        synchronized public void addKeyEvent(KeyEvent keyEvent) {
            mKeyEventQueue.add(keyEvent);
            this.notify();
        }

        synchronized public void delKeyEvent(KeyEvent keyEvent) {
            mKeyEventQueue.remove(keyEvent);
        }

        synchronized public void startThread() {
            mIsStop = false;
            mKeyEventQueue.clear();
            if (!mIsRunning) {
                mIsRunning = true;
                super.start();
            }
            notify();
        }

        synchronized public void stopThread() {
            mIsStop = true;
        }

        synchronized public void quitThread() {
            mIsRunning = false;
            notify();
        }

        @Override
        public void run() {
            if (DBG) {
                LetvLog.d(TAG, "KeyEvent Thread running");
            }
            List<KeyEvent> cacheQueue = new ArrayList<>();
            while (mIsRunning) {
                synchronized (this) {
                    while (mIsStop || mKeyEventQueue.isEmpty()) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            LetvLog.w(TAG, e.getMessage());
                        }
                    }
                    if (DBG) {
                        LetvLog.d(TAG, "KeyEvent Process:" + mKeyEventQueue.size() + " " + cacheQueue.size());
                    }
                    cacheQueue.addAll(mKeyEventQueue);
                    mKeyEventQueue.clear();
                }
                for (KeyEvent keyEvent : cacheQueue) {
                    try {
                        mInstrumentation.sendKeySync(keyEvent);
                    } catch (SecurityException exp) {
                        LetvLog.w(TAG, "sendKeySync EXCEPTION:" + exp.getMessage());
                    }
                    if (DBG) {
                        LetvLog.d(TAG, "SENDKEY:" + keyEvent.toString());
                    }
                }
                cacheQueue.clear();
            }
        }
    }

    InternalEvent obtainInternalEvent(int type, BluetoothGatt gatt, Object objectValue) {
        InternalEvent event = new InternalEvent(type);
        event.gatt = gatt;
        event.valueObject = objectValue;
        return event;
    }

    void requestInternalEvent(InternalEvent event, long timeout) {
        Message msg = mSmHandler.obtainMessage(ACTION_REQ_EVENT);
        msg.obj = event;
        mSmHandler.sendMessageDelayed(msg, timeout);
    }

    void indicateInternalEvent(InternalEvent event, long timeout) {
        Message msg = mSmHandler.obtainMessage(ACTION_IND_EVENT);
        msg.obj = event;
        mSmHandler.sendMessageDelayed(msg, timeout);
    }

    private class InternalEvent {
        int type;
        int valueInt1 = 0;
        int valueInt2 = 0;
        int valueInt3 = 0;
        int valueInt4 = 0;
        Object valueObject = null;
        BluetoothGatt gatt = null;

        InternalEvent(int type) {
            this.type = type;
        }
    }

    private void broadcastEvent(int event, Bundle bundle) {
        synchronized (mGattCallbacks) {
            for (BtGattCallback callback : mGattCallbacks) {
                callback.onGattReceive(event, bundle);
            }
        }
    }

    private void broadcastEvent(Intent intent) {
        Bundle bundle = intent.getExtras();
        String action = intent.getAction();
        if (action.equals(KeyEventManager.ACTION_CONNECTION_STATE_CHANGED)) {
            broadcastEvent(KeyEventManager.EVT_CONN_STAT_CHANGE, bundle);
        }
        mService.sendBroadcast(intent);
    }

    private void broadcastScanResult(int resultCode, BluetoothDevice device) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, device);
        bundle.putInt(KeyEventManager.EXTRA_SCAN_RESULT, resultCode);
        broadcastEvent(KeyEventManager.EVT_SCAN_RESULT, bundle);

        Intent intent = new Intent();
        if (resultCode==KeyEventManager.SCAN_FOUND) {
            intent.setAction(KeyEventManager.ACTION_SCAN_FOUND);
            intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        } else {
            intent.setAction(KeyEventManager.ACTION_SCAN_COMPLETE);
        }
        mService.sendBroadcast(intent);
    }

    private void broadcastConnStateChange(int newState, BluetoothDevice device) {
        Intent intent = new Intent(KeyEventManager.ACTION_CONNECTION_STATE_CHANGED);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        intent.putExtra(BluetoothProfile.EXTRA_STATE, newState);
        broadcastEvent(intent);
    }

    private void broadcastBatteryChange() {
        Intent intent = new Intent(KeyEventManager.ACTION_BATTERY_LEVEL_CHANGED);
        intent.putExtra(KeyEventManager.EXTRA_BATTERY_LEVEL,mBatteryLevel);
        broadcastEvent(intent);
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (DBG) {
                LetvLog.d(TAG, "onConnectionStateChange: device:" + gatt.getDevice().getAddress() +
                        " status=" + status + " newState" + newState + " gattHash" + gatt.hashCode());
            }
            if (status != BluetoothGatt.GATT_SUCCESS) {
                LetvLog.w(TAG, "connection state change error:" + status);
            }
            InternalEvent event = obtainInternalEvent(CONN_STATE_CHANGE, gatt, null);
            event.valueInt1 = status;
            event.valueInt2 = newState;
            indicateInternalEvent(event, 0);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (DBG) {
                LetvLog.d(TAG, "onServicesDiscovered: device:" + gatt.getDevice().getAddress() +
                        " status=" + status);
            }
            InternalEvent event = obtainInternalEvent(DISCOVERY_RESULT, gatt, null);
            event.valueInt1 = status;
            indicateInternalEvent(event, 0);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (DBG) {
                LetvLog.d(TAG, "onCharacteristicRead: device:" + gatt.getDevice().getAddress() +
                        " charac=" + characteristic.toString() + " gattHash" + gatt.hashCode());
            }
            InternalEvent event = obtainInternalEvent(DATA_AVAILABLE, gatt, characteristic);
            event.valueInt1 = status;
            indicateInternalEvent(event, 0);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (DBG) {
                LetvLog.d(TAG, "onCharacteristicChanged: device:" + gatt.getDevice().getAddress() +
                        " charac=" + characteristic.toString());
            }
            onCharacteristicRead(gatt, characteristic, BluetoothGatt.GATT_SUCCESS);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (DBG) {
                LetvLog.d(TAG, "onDescriptorWrite: device:" + gatt.getDevice().getAddress() +
                        " descriptor=" + descriptor.toString());
            }
            InternalEvent event = obtainInternalEvent(WRITE_DESCRIPTOR_FINISH, gatt, descriptor.getCharacteristic());
            indicateInternalEvent(event, 0);
        }
    };

    private interface IState {

        public String getName();

        public void enter();

        public void exit();

        public void processMessage(Message msg);
    }

    protected class SmHandler extends Handler {

        static final private int SM_HANDLER_INIT = 0x01;

        private Message mMsg;
        private IState mOrigState;
        private IState mDestState;
        private boolean mIsInitComplete = false;

        private ArrayList<Message> mDeferredMessages = new ArrayList<Message>();

        public SmHandler(Looper looper, IState initState) {
            super(looper);
            mOrigState = initState;
            initialize();
        }

        public void initialize() {
            if (!mIsInitComplete) {
                removeMessages(SM_HANDLER_INIT);
                Message msg = obtainMessage(SM_HANDLER_INIT);
                msg.sendToTarget();
            }
        }

        protected synchronized int getConnectionState() {
            IState retState = mDestState;
            if (retState == null)
                retState = mOrigState;
            if (retState == mConnected) {
                return BluetoothProfile.STATE_CONNECTED;
            } else if (retState == mConnecting) {
                return BluetoothProfile.STATE_CONNECTING;
            } else {
                return BluetoothProfile.STATE_DISCONNECTED;
            }
        }

        protected synchronized void transitionTo(IState state) {
            mDestState = state;
            if (DBG) {
                LetvLog.d(TAG, "transitionTo:" + state.getName());
            }
        }

        protected Message getCurrentMessage() {
            return mMsg;
        }

        protected void deferMessage(Message msg) {
            Message newMsg = obtainMessage();
            newMsg.copyFrom(msg);
            mDeferredMessages.add(newMsg);
        }

        protected void invokeExit(IState state) {
            state.exit();
        }

        protected void invokeEnter(IState state) {
            state.enter();
        }

        protected void processMessage(Message msg) {
            mOrigState.processMessage(msg);
        }

        protected synchronized void performTransitions() {
            if (mDestState != null && !mDestState.equals(mOrigState)) {
                invokeExit(mOrigState);
                mOrigState = mDestState;
                invokeEnter(mOrigState);

                for (int idx = mDeferredMessages.size() - 1; idx >= 0; --idx) {
                    Message msg = mDeferredMessages.get(idx);
                    sendMessageAtFrontOfQueue(msg);
                }
                mDeferredMessages.clear();
                mDestState = null;
            }
        }

        @Override
        public void handleMessage(Message msg) {
            mMsg = msg;
            if (DBG) {
                LetvLog.d(TAG, "SmHandler.handleMessage " + mIsInitComplete +
                        " Orig=" + mOrigState.getName() +
                        " Dest=" + (mDestState != null ? mDestState.getName() : "None") +
                        " What=" + mMsg.what);
            }
            if (mIsInitComplete)
                processMessage(msg);
            else if (msg.what == SM_HANDLER_INIT) {
                mIsInitComplete = true;
                invokeEnter(mOrigState);
            }
            performTransitions();
        }
    }

    Message getCurrentMessage() {
        return mSmHandler.getCurrentMessage();
    }

    boolean checkGattValid(BluetoothGatt gatt) {
        return mBluetoothGatt != null && mBluetoothGatt.equals(gatt);
    }

    boolean checkDeviceValid(BluetoothDevice device) {
        return mBluetoothGatt != null && mBluetoothGatt.getDevice().equals(device);
    }

    private boolean connectGatt(BluetoothDevice device) {
        mBluetoothGatt = device.connectGatt(mService, AUTO_CONNECT, mGattCallback);
        if (DBG) {
            LetvLog.d(TAG, "connectGatt hash(" +
                    (mBluetoothGatt != null ? mBluetoothGatt.hashCode() : "NULL") + ")");
        }
        return mBluetoothGatt != null;
    }

    private void disconnectGatt() {
        mBluetoothGatt.disconnect();
    }

    private void deferClose() {
        mIsDeferClose = true;
        mSmHandler.sendEmptyMessageDelayed(ACTION_DEFER_CLOSE, DISCONNECT_TIMEOUT);
    }

    private void cancelDeferClose() {
        mIsDeferClose = false;
        mSmHandler.removeMessages(ACTION_DEFER_CLOSE);
    }

    private boolean isDeferClose() {
        return mIsDeferClose;
    }

    private void closeGatt(long delay) {
        if (isDeferClose()) {
            cancelDeferClose();
        }
        final BluetoothGatt droppedGatt = mBluetoothGatt;
        mBluetoothGatt = null;
        if (delay <= 0) {
            droppedGatt.close();
        } else {
            mSmHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    droppedGatt.close();
                }
            }, delay);
        }
    }

    private boolean disconnectGatt(BluetoothDevice device) {
        if (checkDeviceValid(device)) {
            disconnectGatt();
            return true;
        }
        return false;
    }

    private void disconnect(boolean reconnect) {
        if (mBluetoothGatt != null) {
            disconnectGatt();
            if (!reconnect) {
                closeGatt(2000);
            }
        }
        transitionTo(mDisconnected);
    }



    private class DisconnectedState implements IState {

        static final String STATE_NAME = "Disconnected";

        @Override
        public String getName() {
            return STATE_NAME;
        }

        @Override
        public void enter() {
            if (DBG) {
                LetvLog.d(TAG, "ENTER DISCONNECTED: " + getCurrentMessage().what);
            }
            if (mBluetoothGatt == null) {
                if (mKeyEventThread != null && AUTO_CONNECT) {
                    mKeyEventThread.quitThread();
                    mKeyEventThread = null;
                }
            } else {
                BluetoothDevice lastDevice = mBluetoothGatt.getDevice();
                if (!isEnabled()) {
                    closeGatt(1000);
                } else if (!AUTO_CONNECT) {
                    closeGatt(1000);
                    connectDevice(lastDevice, 500);
                }
                broadcastConnStateChange(BluetoothProfile.STATE_DISCONNECTED,lastDevice);
            }
        }

        @Override
        public void exit() {
            if (DBG) {
                LetvLog.d(TAG, "EXIT DISCONNECTED: " + getCurrentMessage().what);
            }
        }

        @Override
        public void processMessage(Message msg) {
            InternalEvent event;
            BluetoothDevice device;
            switch (msg.what) {
                case ACTION_ENABLE_BLUETOOTH:
                    break;
                case ACTION_DISABLE_BLUETOOTH:
                    if (AUTO_CONNECT) {
                        if (mBluetoothGatt != null) {
                            closeGatt(0);
                        }
                    } else {
                        mSmHandler.removeMessages(ACTION_CONNECT);
                    }
                    break;
                case ACTION_CONNECT:
                    device = (BluetoothDevice) msg.obj;
                    if (!checkDeviceValid(device)) {
                        if (mBluetoothGatt != null) {
                            closeGatt(0);
                        }
                        if (connectGatt(device)) {
                            transitionTo(mConnecting);
                        } else {
                            LetvLog.w(TAG, "connectGatt Error " + device.getName());
                        }
                    }
                    break;
                case ACTION_DISCONNECT:
                    device = (BluetoothDevice) msg.obj;
                    if (checkDeviceValid(device)) {
                        closeGatt(0);
                    }
                    break;
                case ACTION_IND_EVENT:
                    event = (InternalEvent) msg.obj;
                    switch (event.type) {
                        case CONN_STATE_CHANGE:
                            int status = event.valueInt1;
                            int newState = event.valueInt2;
                            if (status == BluetoothGatt.GATT_SUCCESS &&
                                    newState == BluetoothProfile.STATE_CONNECTED) {
                                LetvLog.d(TAG, "mBluetoothGatt=" + (mBluetoothGatt != null ? mBluetoothGatt.hashCode() : "null") +
                                        " gatt=" + (event.gatt != null ? event.gatt.hashCode() : "null"));
                                if (event.gatt != null && checkGattValid(event.gatt)) {
                                    deferMessage(msg);
                                    transitionTo(mConnecting);
                                }
                            }
                            break;
                    }
                    break;
                case ACTION_DEFER_CLOSE:
                    if (mBluetoothGatt != null) {
                        closeGatt(0);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class ConnectingState implements IState {

        static final String STATE_NAME = "Connecting";

        static final int CONNECT_TIMEOUT = 180000;
        static final int RECONNECT_PERIOD = 1500;
        static final int DETECT_TIMEOUT = 10000;

        private IntentFilter mScannerFilter;

        private BluetoothDevice mDetectDevice;

        private BroadcastReceiver mScannerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Bundle bundle = intent.getExtras();
                BluetoothDevice device = null;
                int event = -1;
                if (action.equals(KeyEventManager.ACTION_SCAN_FOUND)) {
                    event = ACTION_SCAN_FOUND;
                    device = bundle.getParcelable(BluetoothDevice.EXTRA_DEVICE);
                } else if (action.equals(KeyEventManager.ACTION_SCAN_COMPLETE)) {
                    event = ACTION_SCAN_COMPLETE;
                }
                if (event != -1) {
                    Message msg = mSmHandler.obtainMessage(event);
                    msg.obj = device;
                    msg.sendToTarget();
                }
            }
        };

        private BluetoothGattCharacteristic mCurrentCharacteristic;

        private List<BluetoothGattCharacteristic> mAvailableCharacteristic = new ArrayList<BluetoothGattCharacteristic>();

        BluetoothGattCharacteristic getGattCharacteristic(UUID serviceUuid, UUID characUuid) {
            BluetoothGattService service = mBluetoothGatt.getService(serviceUuid);
            if (service != null) {
                if (DBG) {
                    LetvLog.d(TAG, "Found Service");
                }
                return service.getCharacteristic(characUuid);
            }
            LetvLog.w(TAG, "Not Found Relevant SERVICE");
            return null;
        }

        private boolean constructAvailableCharacteristics() {
            if (mBluetoothGatt == null) {
                LetvLog.w(TAG, "current gatt object is invalid");
                return false;
            }
            BluetoothGattCharacteristic characteristic;
            for (Pair<UUID, UUID> uuidPair : mAvailableUuids) {
                characteristic = getGattCharacteristic(uuidPair.first, uuidPair.second);
                if (characteristic == null) {
                    LetvLog.w(TAG, "Not found relevant characteristic(" + uuidPair.first.toString()
                            + ", " + uuidPair.second.toString() + ")");
                    return false;
                }
                mAvailableCharacteristic.add(characteristic);
            }
            return true;
        }

        private BluetoothGattCharacteristic nextAvailableCharacteristic() {
            if (mAvailableCharacteristic.size() > 0) {
                return mAvailableCharacteristic.remove(0);
            } else {
                return null;
            }
        }

        private boolean checkCharacteristicValid(BluetoothGattCharacteristic characteristic) {
            if (mCurrentCharacteristic != null && characteristic != null
                    && mCurrentCharacteristic.getUuid().equals(characteristic.getUuid())) {
                return true;
            }
            return false;
        }

        boolean reconnect(long delay) {
            if (mBluetoothGatt != null) {
                BluetoothDevice device = mBluetoothGatt.getDevice();
                disconnectGatt();
                mSmHandler.removeMessages(ACTION_RECONNECT);
                Message msg = mSmHandler.obtainMessage(ACTION_RECONNECT);
                msg.obj = device;
                return mSmHandler.sendMessageDelayed(msg, delay);
            }
            disconnect(false);
            return false;
        }

        boolean startDetect(BluetoothDevice device) {
            mDetectDevice = device;
            return mKeypadScanner.startScan(DETECT_TIMEOUT);
        }

        boolean stopDetect() {
            mDetectDevice = null;
            return mKeypadScanner.stopScan();
        }

        public ConnectingState() {
            mScannerFilter = new IntentFilter();
            mScannerFilter.addAction(KeyEventManager.ACTION_SCAN_FOUND);
            mScannerFilter.addAction(KeyEventManager.ACTION_SCAN_COMPLETE);
        }

        @Override
        public String getName() {
            return STATE_NAME;
        }

        @Override
        public void enter() {
            if (DBG) {
                LetvLog.d(TAG, "ENTER CONNECTING: " + getCurrentMessage().what);
            }
            mCurrentCharacteristic = null;
            mAvailableCharacteristic.clear();
            mService.registerReceiver(mScannerReceiver, mScannerFilter);
            mSmHandler.sendEmptyMessageDelayed(ACTION_CANCEL_CONNECT, CONNECT_TIMEOUT);
        }

        @Override
        public void exit() {
            mSmHandler.removeMessages(ACTION_CANCEL_CONNECT);
            if (DBG) {
                LetvLog.d(TAG, "EXIT CONNECTING: " + getCurrentMessage().what);
            }
            mService.unregisterReceiver(mScannerReceiver);
            stopDetect();
        }

        boolean discoverService() {
            return mBluetoothGatt.discoverServices();
        }

        @Override
        public void processMessage(Message msg) {
            InternalEvent event;
            BluetoothDevice device;
            switch (msg.what) {
                case ACTION_DISCONNECT:
                    device = (BluetoothDevice) msg.obj;
                    if (checkDeviceValid(device)) {
                        disconnect(false);
                    }
                    break;
                case ACTION_CONNECT:
                    device = (BluetoothDevice) msg.obj;
                    if (!checkDeviceValid(device)) {
                        deferMessage(msg);
                        disconnect(false);
                    }
                    break;
                case ACTION_RECONNECT:
                    device = (BluetoothDevice) msg.obj;
                    if (mBluetoothGatt != null) {
                        closeGatt(0);
                    }
                    if (!startDetect(device)) {
                        disconnect(false);
                    }
                    break;
                case ACTION_SCAN_FOUND:
                    device = (BluetoothDevice) msg.obj;
                    LetvLog.d(TAG,"SCAN_FOUND: " + device.getName());
                    if (device != null && device.equals(mDetectDevice)) {
                        if (mBluetoothGatt != null) {
                            closeGatt(0);
                        }
                        if(!connectGatt(device)) {
                            LetvLog.w(TAG,"RE-CONNECT ERROR! transitionTo DISCONNECTED!");
                            disconnect(false);
                        }
                        stopDetect();
                    }
                    break;
                case ACTION_SCAN_COMPLETE:
                    LetvLog.d(TAG, "SCAN_COMPLETE: " + (mDetectDevice != null ? mDetectDevice.getAddress() : "null"));
                    if (mDetectDevice != null) {
                        if (startDetect(mDetectDevice)) {
                            break;
                        }
                        disconnect(false);
                    }
                    break;
                case ACTION_CANCEL_CONNECT:
                    disconnect(false);
                    break;
                case ACTION_REQ_EVENT:
                    event = (InternalEvent)msg.obj;
                    if (!checkGattValid(event.gatt)) {
                        LetvLog.w(TAG, "ACTION_REQ_EVENT: GATT IS INVALID!(" + checkGattValid(event.gatt) + "," +
                                (event.gatt != null ? event.gatt.getDevice().getName() : "null") +
                                ") transitionTo \"DISCONNECTED\"");
                        break;
                    }
                    switch (event.type){
                        case SET_CHARAC_NOTIFY:
                            if (mCurrentCharacteristic != null) {
                                mBluetoothGatt.setCharacteristicNotification(mCurrentCharacteristic, true);
                                List<BluetoothGattDescriptor> ml = mCurrentCharacteristic.getDescriptors();
                                for (BluetoothGattDescriptor descriptor : ml) {
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    mBluetoothGatt.writeDescriptor(descriptor);
                                }
                            } else {
                                reconnect(RECONNECT_PERIOD);
                            }
                            break;
                        case START_INIT_GATT:
                            mCurrentCharacteristic = nextAvailableCharacteristic();
                            if (mCurrentCharacteristic != null) {
                                mBluetoothGatt.readCharacteristic(mCurrentCharacteristic);
                            } else {
                                reconnect(RECONNECT_PERIOD);
                            }
                            break;
                    }
                    break;
                case ACTION_IND_EVENT:
                    event = (InternalEvent) msg.obj;
                    if (!checkGattValid(event.gatt)) {
                        LetvLog.w(TAG, "ACTION_REQ_EVENT: DISCOVERY_RESULT IS INVALID!(" +
                                checkGattValid(event.gatt) + "," +
                                (event.gatt != null ? event.gatt.getDevice().getName() : "null") +
                                ") transitionTo \"DISCONNECTED\"");
                        break;
                    }
                    switch (event.type) {
                        case CONN_STATE_CHANGE:
                            int status = event.valueInt1;
                            int newState = event.valueInt2;
                            if (status != BluetoothGatt.GATT_SUCCESS ||
                                    newState == BluetoothProfile.STATE_DISCONNECTED) {
                                reconnect(RECONNECT_PERIOD);
                            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                                if (!discoverService()) {
                                    LetvLog.w(TAG, "DISCOVER-SERVICE ERROR:The remote service discovery has been started");
                                }
                            }
                            break;
                        case DISCOVERY_RESULT:
                            constructAvailableCharacteristics();
                            requestInternalEvent(obtainInternalEvent(START_INIT_GATT, mBluetoothGatt, null), 0);
                            break;
                        case WRITE_DESCRIPTOR_FINISH:
                            LetvLog.d(TAG,"into WRITE_DESCRIPTOR_FINISH");
                            BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) event.valueObject;
                            if (!checkCharacteristicValid(characteristic)) {
                                LetvLog.w(TAG, "current characteristic is no match");
                                break;
                            }
                            mCurrentCharacteristic = nextAvailableCharacteristic();
                            if (mCurrentCharacteristic != null) {
                                mBluetoothGatt.readCharacteristic(mCurrentCharacteristic);
                            } else {
                                transitionTo(mConnected);
                            }
                            break;
                        case DATA_AVAILABLE:
                            BluetoothGattCharacteristic charac = (BluetoothGattCharacteristic) event.valueObject;
                            byte[] data = charac.getValue();
                            if (data == null || data.length == 0) {
                                LetvLog.w(TAG,"Invalid Data");
                                break;
                            }
                            if (DBG) {
                                final StringBuilder stringBuilder = new StringBuilder(data.length);
                                for (byte byteChar : data)
                                    stringBuilder.append(String.format("%02X ", byteChar));
                                LetvLog.d(TAG, ">>>>>Data(" + data.length+ "):" + stringBuilder.toString());
                            }
                            if (!checkCharacteristicValid(charac)) {
                                LetvLog.w(TAG, "Invalid Characteristic! IGNORE!");
                                break;
                            }
                            if(charac.getUuid().equals(BATTERY_CHARAC_UUID)) {
                                mBatteryLevel = data[0];
                                broadcastBatteryChange();
                            }
                            requestInternalEvent(obtainInternalEvent(SET_CHARAC_NOTIFY, mBluetoothGatt, null), 0);
                            break;
                        default:
                            break;
                    }
                    break;
                case ACTION_DISABLE_BLUETOOTH:
                case ACTION_DEFER_CLOSE:
                    deferMessage(msg);
                    transitionTo(mDisconnected);
                    break;
                default:
                    break;
            }
        }
    }

    private class ConnectedState implements IState {

        static final String STATE_NAME = "Connected";

        private IKeyEventMode mCurrentMode;

        ConnectedState(){
            mCurrentMode = mNormalMode;
        }

        @Override
        public String getName() {
            return STATE_NAME;
        }

        @Override
        public void enter() {
            if (DBG) {
                LetvLog.d(TAG, "ENTER CONNECTED: " + getCurrentMessage().what);
            }
            if (mKeyEventThread == null) {
                mKeyEventThread = new KeyEventThread();
            }
            mKeyEventThread.startThread();
            if (AUTO_CONNECT) {
                cancelDeferClose();
            }
            if (mBluetoothGatt != null) {
                broadcastConnStateChange(BluetoothProfile.STATE_CONNECTED, mBluetoothGatt.getDevice());
            }
        }

        @Override
        public void exit() {
            if (DBG) {
                LetvLog.d(TAG, "EXIT CONNECTED: " + getCurrentMessage().what);
            }
            if (mKeyEventThread != null) {
                mKeyEventThread.stopThread();
            }
        }

        @Override
        public void processMessage(Message msg) {
            BluetoothDevice device;
            InternalEvent event;
            switch (msg.what) {
                case ACTION_DISABLE_BLUETOOTH:
                    disconnect(false);
                    break;
                case ACTION_DISCONNECT:
                    device = (BluetoothDevice) msg.obj;
                    if (checkDeviceValid(device)) {
                        disconnect(false);
                    } else {
                        LetvLog.w(TAG, "device is Not Valid" + device.getName());
                    }
                    break;
                case ACTION_CONNECT:
                    device = (BluetoothDevice) msg.obj;
                    if (!checkDeviceValid(device)) {
                        deferMessage(msg);
                        disconnect(false);
                    }
                    break;
                case ACTION_SWITCH_MODE:
                    if ((msg.obj instanceof IKeyEventMode) && mCurrentMode != msg.obj) {
                        mCurrentMode = (IKeyEventMode) msg.obj;
                    }
                    break;
                case ACTION_IND_EVENT:
                    event = (InternalEvent) msg.obj;
                    if (DBG) {
                        LetvLog.d(TAG, "into ACTION_IND_EVENT:" + event.type);
                    }
                    int status = event.valueInt1;
                    if (!checkGattValid(event.gatt) || status != BluetoothGatt.GATT_SUCCESS) {
                        LetvLog.w(TAG, "ACTION_IND_EVENT: GATT ERROR(" + status + "," +
                                checkGattValid(event.gatt) + "," +
                                (event.gatt != null ? event.gatt.getDevice().getName() : "null") +
                                ") transitionTo \"DISCONNECTED\"");
                        disconnect(true);
                        break;
                    }
                    switch (event.type) {
                        case DATA_AVAILABLE:
                            BluetoothGattCharacteristic charac = (BluetoothGattCharacteristic) event.valueObject;
                            byte[] data = charac.getValue();
                            if (DBG) {
                                final StringBuilder stringBuilder = new StringBuilder(data.length);
                                for (byte byteChar : data)
                                    stringBuilder.append(String.format("%02X ", byteChar));
                                LetvLog.d(TAG, ">>>>>Data(" + data.length+ "):" + stringBuilder.toString());
                            }
                            if(charac.getUuid().equals(KEYPAD_CHARAC_UUID)) {
                                if (mCurrentMode != null) {
                                    mCurrentMode.handle(data);
                                }
                            } else if (charac.getUuid().equals(BATTERY_CHARAC_UUID)) {
                                if (data != null && data.length > 0) {
                                    mBatteryLevel = data[0];
                                    broadcastBatteryChange();
                                }
                            }
                            break;
                        case CONN_STATE_CHANGE:
                            int newState = event.valueInt2;
                            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                if (AUTO_CONNECT) {
                                    deferClose();
                                }
                                transitionTo(mDisconnected);
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public final void transitionTo(IState state) {
        mSmHandler.transitionTo(state);
    }

    public void deferMessage(Message msg) {
        mSmHandler.deferMessage(msg);
    }

    private boolean isEnabled() {
        if (mBluetoothAdapter!=null && mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) return true;
        return false;
    }

    public KeypadScheduler(KeypadService service) {
        mService = service;
        mBluetoothAdapter = mService.getBluetoothAdapter();

        // Initialize Mode
        mCallMode = new CallMode();
        mNormalMode = new NormalMode();

        // Initialize State
        mDisconnected = new DisconnectedState();
        mConnecting = new ConnectingState();
        mConnected = new ConnectedState();
        mSmHandler = new SmHandler(Looper.getMainLooper(), mDisconnected);

        // Initialize Scanner
        mKeypadScanner = new KeypadScanner(mService);

        // Initialize Available Characteristic UUID
        mAvailableUuids.add(new Pair<UUID, UUID>(KEYPAD_SERVICE_UUID, KEYPAD_CHARAC_UUID));
        mAvailableUuids.add(new Pair<UUID, UUID>(BATTERY_SERVICE_UUID, BATTERY_CHARAC_UUID));
    }

    protected void enableBluetooth() {
        mSmHandler.sendEmptyMessage(ACTION_ENABLE_BLUETOOTH);
    }

    protected void disableBluetooth() {
        mSmHandler.sendEmptyMessage(ACTION_DISABLE_BLUETOOTH);
    }

    public boolean scanLeDevice(boolean enable) {
        if (enable) {
            return mKeypadScanner.startScan(0);
        } else {
            return mKeypadScanner.stopScan();
        }
    }

    public boolean connectDevice(BluetoothDevice device, long delay) {
        Message msg = mSmHandler.obtainMessage(ACTION_CONNECT);
        msg.obj = device;
        return mSmHandler.sendMessageDelayed(msg, delay);
    }

    public boolean disconnectDevice(BluetoothDevice device) {
        Message msg = mSmHandler.obtainMessage(ACTION_DISCONNECT);
        msg.obj = device;
        msg.sendToTarget();
        return true;
    }

    public int getConnectionState() {
        return mSmHandler.getConnectionState();
    }

    public int getBatteryLevel() {
        return mBatteryLevel;
    }

    public void registerGattCallback(BtGattCallback callback) {
        synchronized (mGattCallbacks) {
            if (!mGattCallbacks.contains(callback)) {
                mGattCallbacks.add(callback);
            }
        }
    }

    public void unregisterGattCallback(BtGattCallback callback) {
        synchronized (mGattCallbacks) {
            mGattCallbacks.remove(callback);
        }
    }

    synchronized public void addFlags(int flags) {
        mFlags |= flags;
    }

    synchronized public void delFlags(int flags) {
        mFlags &= ~flags;
    }

    synchronized public boolean checkFlags(int flags) {
        return (mFlags & flags) != 0;
    }
}