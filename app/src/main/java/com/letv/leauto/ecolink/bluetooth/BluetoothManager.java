package com.letv.leauto.ecolink.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by Administrator on 2016/10/25.
 */
public class BluetoothManager {

    private static final String TAG = "BluetoothManager";
    private static final String PIN_CODE = "0000";
    private Context mContext;

    private static BluetoothManager ourInstance = new BluetoothManager();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mCurrentBluetoothDevice;

    public static BluetoothManager getInstance() {
        return ourInstance;
    }

    private BluetoothManager() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void autoConnectBluetooth(Context context, String macStr) {
        mContext = context;
        registerBlueToothBroadcast();

        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

//        //搜索蓝牙
//        if (!mBluetoothAdapter.isDiscovering()) {
//            mBluetoothAdapter.startDiscovery();
//        }

        mCurrentBluetoothDevice = mBluetoothAdapter.getRemoteDevice(macStr);
        if (mCurrentBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
            startPairing();
        }
    }

    private void registerBlueToothBroadcast() {
        Toast.makeText(mContext, "BluetoothDevice is registerBlueToothBroadcast", Toast.LENGTH_SHORT).show();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        //filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.setPriority(1000);
        mContext.registerReceiver(mBluetoothReceiver, filter);
    }

    private void unregisterBlueToothBroadcast() {
        try {
            mContext.unregisterReceiver(mBluetoothReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接蓝牙设备
     */
    private void autoConnect() {
        new Thread() {
            @Override
            public void run() {
                Trace.Debug(TAG, "connect autoConnect .....");
                /**使用A2DP协议连接设备*/
                mBluetoothAdapter.getProfileProxy(mContext, mProfileServiceListener, BluetoothProfile.A2DP);
                /**使用HEADSET协议连接设备*/
                mBluetoothAdapter.getProfileProxy(mContext, mProfileServiceListener, BluetoothProfile.HEADSET);
            }
        }.start();


        unregisterBlueToothBroadcast();
    }

    private void startPairing() {
        try {
            Trace.Debug(TAG, "connect startPairing .....");
            BlueToothUtil.setPin(mCurrentBluetoothDevice.getClass(), mCurrentBluetoothDevice, PIN_CODE);
            //通过工具类BlueToothUtil,调用createBond方法
            BlueToothUtil.createBond(mCurrentBluetoothDevice.getClass(), mCurrentBluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice btDevice = null;
            // 从Intent中获取设备对象
            btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (isTarget(btDevice)) {
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    Trace.Debug(TAG, "connect ACTION_FOUND state:"+btDevice.getBondState());
//                    //没有配对
//                    if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
//                        startPairing();
//                    }
                } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    Trace.Debug(TAG, "connect ACTION_BOND_STATE_CHANGED state:"+btDevice.getBondState());
                    //配对完成
                    if (btDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        autoConnect();
                    }
                } else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                    Trace.Debug(TAG, "connect ACTION_PAIRING_REQUEST state:"+btDevice.getBondState());
                    try {
                        //确认配对
                        BlueToothUtil.setPairingConfirmation(btDevice.getClass(), btDevice, true);
                        Trace.Debug(TAG, "isOrderedBroadcast:" + isOrderedBroadcast() + ",isInitialStickyBroadcast:" + isInitialStickyBroadcast());
                        //终止有序广播
                        Toast.makeText(context, "BluetoothDevice PAIRING_REQUESTD", Toast.LENGTH_SHORT).show();
                        //如果没有将广播终止，取消弹出配对框。
                        abortBroadcast();
                        //调用setPin方法进行配对
                        boolean ret = BlueToothUtil.setPin(btDevice.getClass(), btDevice, PIN_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private boolean isTarget(BluetoothDevice device) {
        if (mCurrentBluetoothDevice == null || device == null || device.getAddress() == null) {
            return false;
        }

        Trace.Debug(TAG, "connect isTarget device:"+device.getName());
        boolean found = device.getAddress().equals(mCurrentBluetoothDevice.getAddress());
        return found;
    }

    /**
     * 连接蓝牙设备（通过监听蓝牙协议的服务，在连接服务的时候使用BluetoothA2dp协议）
     */
    private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceDisconnected(int profile) {

        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            try {
                if (profile == BluetoothProfile.HEADSET) {
                    BluetoothHeadset bh = (BluetoothHeadset) proxy;
                    if (bh.getConnectionState(mCurrentBluetoothDevice) != BluetoothProfile.STATE_CONNECTED) {
                        bh.getClass()
                                .getMethod("connect", BluetoothDevice.class)
                                .invoke(bh, mCurrentBluetoothDevice);
                    }
                    Trace.Debug(TAG, "connect HEADSET ongoing");
                } else if (profile == BluetoothProfile.A2DP) {
                    //使用A2DP的协议连接蓝牙设备（使用了反射技术调用连接的方法
                    BluetoothA2dp a2dp = (BluetoothA2dp) proxy;
                    if (a2dp.getConnectionState(mCurrentBluetoothDevice) != BluetoothProfile.STATE_CONNECTED) {
                        a2dp.getClass()
                                .getMethod("connect", BluetoothDevice.class)
                                .invoke(a2dp, mCurrentBluetoothDevice);
                        Trace.Debug(TAG, "connect A2DP ongoing");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}