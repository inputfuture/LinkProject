package com.letv.leauto.ecolink.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/10/19.
 */
public class BlueToothUtil {
    /**
     * 与设备配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    private static BluetoothDevice remoteDevice = null;

    //与设备配对
    public static boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        boolean returnValue = (boolean) createBondMethod.invoke(btDevice);

        return returnValue;
    }

    //与设备解除配对
    public static boolean removeBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        boolean returnValue = (boolean) removeBondMethod.invoke(btDevice);
        return returnValue;
    }

    public static boolean setPin(Class btClass, BluetoothDevice btDevice, String str) throws Exception {
        Method setPinMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
        boolean returnValue = (boolean) setPinMethod.invoke(btDevice, new Object[]{str.getBytes()});
        return returnValue;
    }

    //取消用户输入
    public static boolean cancelPairingUserInput(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method cancelPairingUserInput = btClass.getMethod("cancelPairingUserInput");
        boolean returnValue = (boolean) cancelPairingUserInput.invoke(btDevice);

        return returnValue;
    }

    //取消配对
    public static boolean cancelBondProcess(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method cancelBondProcess = btClass.getMethod("cancelBondProcess");
        boolean returnValue = (boolean) cancelBondProcess.invoke(btDevice);

        return returnValue;
    }

    static public void setPairingConfirmation(Class<?> btClass, BluetoothDevice device, boolean isConfirm) throws Exception {
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation", boolean.class);
        setPairingConfirmation.invoke(device, isConfirm);
    }

    public static ParcelUuid[] getUuids(BluetoothAdapter blueToothAdapter) {
        ParcelUuid[] uids = null;
        try {
            Method getUuidsMethod = blueToothAdapter.getClass().getMethod("getUuids");
            uids = (ParcelUuid[]) getUuidsMethod.invoke(blueToothAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uids;
    }

    static public void printAllInform(Class clsShow) {
        try {
            // 取得所有方法
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (; i < hideMethod.length; i++) {
                //Log.e("method name", hideMethod.getName() + ";and the i is:"
                //  + i);
            }
            // 取得所有常量
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++) {
                //Log.e("Field name", allFields.getName());
            }
        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}