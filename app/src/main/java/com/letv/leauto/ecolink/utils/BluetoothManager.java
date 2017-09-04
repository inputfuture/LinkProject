package com.letv.leauto.ecolink.utils;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by Administrator on 2016/8/15.
 */
public class BluetoothManager {
    /**
     * 当前 Android 设备的 bluetooth 是否已经开启
     *
     * @return true：Bluetooth 已经开启 false：Bluetooth 未开启
     */
    public static boolean isBluetoothEnabled()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter .getDefaultAdapter();
        if (bluetoothAdapter != null)
        {
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }
}
