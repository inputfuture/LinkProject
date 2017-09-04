package com.letv.leauto.ecolink.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

//import org.apache.http.util.ByteArrayBuffer;
//import org.apache.http.util.EncodingUtils;

//跟网络相关的工具类
public class NetUtils {

    private static final String TAG = "NetUtils";

    public static final int NET_TYPE_WIFI = 1;
    public static final int NET_TYPE_MOBILE = 2;

    public static final int NET_STATE_DISABLED = 1;
    public static final int NET_STATE_ENABLED = 2;
    public static final int NET_STATE_CONNECTED = 3;
    public static final int NET_STATE_DISCONNECTED = 4;


    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        return info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    /**
     * 获取网路类型
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        NetworkInfo info = null;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = (connectivityManager != null) ? connectivityManager.getActiveNetworkInfo() : null;
        } catch (Exception e) {
            Trace.Error(TAG, "[NetUtils.getNetworkType] " + e.toString());
        }

        // 0:not use; 1:Ethernet; 2:Mobile; 3:Wifi
        int networkType = 0;
        String networkName = "no network";
        if (info != null && info.isAvailable()) {
            networkName = info.getTypeName();

            switch (info.getType()) {
                case ConnectivityManager.TYPE_ETHERNET:
                    networkType = 1;
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    networkType = 3;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    networkType = 2;
                    break;
                default:
                    networkType = 0;
                    break;
            }
        }
        Trace.Debug(TAG, "detects the mCurrentIndex network: " + networkName + ",type: " + networkType);

        return networkType;
    }

    public static String getWifiIp(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            if (ipAddress == 0) {
                return null;
            }
            return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
        } catch (Exception e) {
            Trace.Error(TAG, "[CommonUtil.getWifiIp] " + e.toString());
        }

        return "";
    }

    public static String getEthernetIp() {
        try {
            for (Enumeration<?> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface item = (NetworkInterface) e.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = item.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            Trace.Error(TAG,  e.getMessage());
        }

        return "";
    }
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<?> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface item = (NetworkInterface) e.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = item.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            Trace.Error(TAG,  e.getMessage());
        }

        return "";
    }

//    public static boolean isAvailable() {
//        String myString="";
//        try {
//            URL url = new URL("http://www.baidu.com/index.html");
//            URLConnection urlCon = url.openConnection();
//            urlCon.setConnectTimeout(1500);
//            InputStream is = urlCon.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
//            // 用ByteArrayBuffer缓存
//            ByteArrayBuffer baf = new ByteArrayBuffer(50);
//            int current = 0;
//            while ((current = bis.read()) != -1) {
//                baf.append((byte) current);
//            }
//
//            myString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
//            bis.close();
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            return false;
//        }
//
//        if(myString.indexOf("www.baidu.com")>-1){
//            return true;
//        }else{
//            return false;
//        }
//    }


    public static void showNoNetDialog(final Context context){
        Trace.Debug("****** 无网络弹窗");
        NetworkConfirmDialog networkConfirmDialog=new NetworkConfirmDialog(context, R.string.no_net_message,R.string.ok, R.string.i_know);
        networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
            @Override
            public void onConfirm(boolean checked) {
                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                context.startActivity(intent);
            }

            @Override
            public void onCancel() {


            }
        });
        networkConfirmDialog.setCancelable(false);
        networkConfirmDialog.show();
    }
}
