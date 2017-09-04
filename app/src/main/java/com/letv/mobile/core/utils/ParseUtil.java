package com.letv.mobile.core.utils;

/**
 * 强制转换
 * @author baiwenlong
 */
public class ParseUtil {

    public static int parseInt(String strValue, int defValue) {
        if (!StringUtils.equalsNull(strValue)) {
            try {
                return Integer.parseInt(strValue);
            } catch (Exception e) {
                // ignore
                // e.printStackTrace();
            }
        }
        return defValue;
    }

    public static long parseLong(String strValue, long defValue) {
        if (!StringUtils.equalsNull(strValue)) {
            try {
                return Long.parseLong(strValue);
            } catch (Exception e) {
                // ignore
                // e.printStackTrace();
            }
        }
        return defValue;
    }

    public static float parseFloat(String strValue, float defValue) {
        if (!StringUtils.equalsNull(strValue)) {
            try {
                return Float.parseFloat(strValue);
            } catch (Exception e) {
                // ignore
                // e.printStackTrace();
            }
        }
        return defValue;
    }

    public static double parseDouble(String strValue, float defValue) {
        if (!StringUtils.equalsNull(strValue)) {
            try {
                return Double.parseDouble(strValue);
            } catch (Exception e) {
                // ignore
                // e.printStackTrace();
            }
        }
        return defValue;
    }
}
