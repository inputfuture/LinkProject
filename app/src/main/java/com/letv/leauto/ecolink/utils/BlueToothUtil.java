package com.letv.leauto.ecolink.utils;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.google.gson.Gson;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.protocol.DataSendManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/9/8.
 */
public class BlueToothUtil {

    public static final String CUUID="00001101-0000-1000-8000-00805f9b34fb";
    private static final String TAG = "BlueToothUtil";
    private static BluetoothA2dp a2dp;

//    public static boolean isContent(BluetoothAdapter adapter,String mac) throws IOException {
//        BluetoothDevice dev = adapter.getRemoteDevice(mac);
//        ParcelUuid[] uuid = dev.getUuids();
//        BluetoothSocket record =  dev.createRfcommSocketToServiceRecord(UUID.fromString(uuid[0].toString()));
//        return  record.isConnected();
//
//    }

    public  static void blueToothConnectStatus(final Context context, final BluetoothAdapter adapter, final String mac) {
        if (adapter == null) {
            LogUtils.i(TAG,"blueToothConnectStatus adapter is null,return do nothing!");
            return;
        }

        int headth = adapter.getProfileConnectionState(BluetoothProfile.HEALTH);
        int a2dp = adapter.getProfileConnectionState(BluetoothProfile.A2DP);
        int headset = adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        int flag = -1;
        if (a2dp == BluetoothProfile.STATE_CONNECTED) {
            flag = a2dp;
        } else if (headset == BluetoothProfile.STATE_CONNECTED) {
            flag = headset;
        } else if (headth == BluetoothProfile.STATE_CONNECTED) {
            flag = headth;
        }

        if (flag != -1) {//有设备连接
            adapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    List<BluetoothDevice> devs = proxy.getConnectedDevices();
                    for (BluetoothDevice device : devs) {
                        if (device.getAddress().equals(mac)) {//连接为指定车机
                            Trace.Debug("TAG","蓝牙连接为指定车机");
                            return;
                        }else{
                            //连接非指定车机
                            try {
                                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(CUUID));
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sendMoblieBlueToothInfo(adapter,context);
                        }
                    }
                }
                @Override
                public void onServiceDisconnected(int profile) {
                }
            }, flag);
        }else {//无设备连接
            sendMoblieBlueToothInfo(adapter,context);
        }
    }



    private static void sendMoblieBlueToothInfo(BluetoothAdapter adapter,Context context){
        if (adapter.getState() == BluetoothAdapter.STATE_OFF) {
            adapter.enable();
        }
        Map map = new HashMap();
        map.put("PHONE_BT_NAME", adapter.getName());
        map.put("address", adapter.getAddress());
        Gson gson=new Gson();
        String json = gson.toJson(map);
        DataSendManager.getInstance().sendMsgToCar(0x300,0x17,0,json.getBytes());
    }

}
