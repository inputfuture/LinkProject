package com.letv.mobile.core.utils;

import android.Manifest;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

/**
 * 设备工具类
 * @author LETV
 */
public class DeviceUtils {

//    private static Logger sLogger = new Logger("DeviceUtils");

    /**
     * 获取设备名称型号
     */
    public static String getTerminalSeries() {
        return LeSystemProperties.getProductModelName().replace(" ", "_") + "_"
                + LeSystemProperties.getVendorName();
    }

    private static final String LETV_BRAND_NAME = "Letv";
    private static final String LETV_BRAND_NAME_NEW = "LeEco";

    /** 是否第三方设备 */
    public static boolean isOtherDevice() {
        return !isLetvDevice();
    }

    /**
     * 判断是不是高通方案的机器
     */
    public static boolean isQualcommDevice() {
        return LeSystemProperties.PLATFORM_QCOM
                .equals(LeSystemProperties.getPlatformName());
    }

    /** 是否自有设备 */
    public static boolean isLetvDevice() {
        return LETV_BRAND_NAME.equalsIgnoreCase(android.os.Build.BRAND)
                || LETV_BRAND_NAME_NEW.equalsIgnoreCase(android.os.Build.BRAND);
    }

    /**
     * 加密
     * 获取设备ID，先取IMEI，获取不到取mac地址的MD5值作为设备id
     */
    public static String getDeviceId() {
        String imeiId = getDeviceIMEI();
        if (!StringUtils.equalsNull(imeiId)) {
            return imeiId;
        }

        String mac = SystemUtil.getMacAddress();
        if (StringUtils.equalsNull(mac)) {
            return "";
        }
        return MD5Util.MD5(mac);
    }

    /**
     * 用来区分视频内容是否在白名单中,在白名单内且在无版权地区可以进行播放
     * 按照：</br>
     * 1.如果能获取到imei，则拼接&devid=imeixxxxxxx（后面的xxx...是imei值，无冒号）</br>
     * 2.如果获取不到imei，则拼接&devid=macxxxxxxxx（后面的xxx...是mac地址，无冒号）</br>
     * @return 有Imei,&devid=imeixxxx;</br>
     *         没有imei有mac,&devid=macxxxx;</br>
     *         imei和mac都没有,&devid=;</br>
     */
    public static String getDevId() {
        String imei = DeviceUtils.getDeviceIMEI();
        StringBuilder sb = new StringBuilder();
        sb.append("&").append("devid=");// add "devid="
        if (!StringUtils.equalsNull(imei)) {
            sb.append("imei").append(imei);// add "imei983477347"
            return sb.toString();
        }

        String mac = SystemUtil.getMacAddress();
        if (StringUtils.equalsNull(mac)) {
            return sb.toString();
        }
        sb.append("mac");// add "mac"
        sb.append(mac);// add "mac989123829"
        return sb.toString();
    }

    /**
     * 获取设备IMEI值
     * @return
     */
    public static String getDeviceIMEI() {
        if (ContextProvider.getApplicationContext() == null) {
            return "";
        }

        String imei = getImeiFromEuiInterface();

        if (!StringUtils.equalsNull(imei)) {
            return imei;
        }

        // above android 6.0 we should check whether the app has the permission
        // to get imei

        if (PermissionUtil
                .checkSelfPermission(Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager telephonyManager = (TelephonyManager) ContextProvider
                    .getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();

            if (StringUtils.equalsNull(imei)) {
                return "";
            }
        }
        return imei;
    }

    private static String getImeiFromEuiInterface() {
        TelephonyManager telephonyManager = (TelephonyManager) ContextProvider
                .getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = null;
        try {
            Method method = TelephonyManager.class.getMethod("getLEUIDeviceId");
            method.setAccessible(true);
            imei = (String) method.invoke(telephonyManager);
        } catch (Exception e) {
            imei = telephonyManager.getDeviceId();
            e.printStackTrace();
        }
        return imei;
    }

    /**
     * 获取deviceKey即phoneKey
     * @return
     */
//    public static String getDeviceKey() {
//        IBinder binder = ServiceManager.getService("leuiphonebind");
//        if (binder == null) {
////            sLogger.d("leuiphonebind binder is null");
//            return null;
//        }
//        String bindKey = null;
//        try {
//            IPhoneBind phoneBind = IPhoneBind.Stub.asInterface(binder);
//            bindKey = phoneBind.getLeTVSNValue("leui_phone_bind_key");
//        } catch (Exception e) {
////            sLogger.e("getPhoneBind error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return bindKey;
//    }

    /**
     * 获取手机ptyp
     * @return
     */
    public static String getPType() {
        return SystemUtil.getSystemProperty("ro.product.customize");
    }

    /**
     * 获取销售区域
     * CN 中国，HK 中国香港，TW 中国台湾，US 美国，IN 印度，RU 俄罗斯
     * 当返回为""时，表示系统未获取到IMEI所对应的销售区域，或者该IMEI是无效的
     */
    public static String getSalesArea() {
        return SystemUtil.getSystemProperty("persist.sys.salesarea");
    }

    /**
     * 获取product model
     * @return product model
     */
    public static String getPMode(){
        return SystemUtil.getSystemProperty("ro.product.model");
    }
}
