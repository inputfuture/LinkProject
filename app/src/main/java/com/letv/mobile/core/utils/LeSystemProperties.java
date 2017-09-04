package com.letv.mobile.core.utils;

import android.os.Build;

public class LeSystemProperties {

    public final static String PLATFORM_MTK = "mtk";
    public final static String PLATFORM_QCOM = "qcom";

    private final static String X1_PATTERN_REGEX = "^[Xx][0-9]{1}$";
    private final static String MAX1_PATTERN_REGEX = "^[Mm][Aa][Xx][0-9]{1}$";
    private final static String COMMON_PATTERN_REGEX = "^[Xx][0-9]{3}$";
    private final static String WHOLE_NETCOM_PATTERN_REGEX = "^[Xx][0-9]{3}[+]$";

    private final static String RO_VENDOR = "ro.product.customize";

    private final static String LE_1 = "Le 1";
    private final static String LE_PRO = "Le 1 Pro";
    private final static String LE_MAX = "Le Max";

    private final static String VENDOR_DEFAULT = "default";
    private final static String VENDOR_CT = "china-telecom";
    private final static String VENDOR_CMCC = "china-mobile";
    private final static String VENDOR_WN = "whole-netcom";
    private final static String VENDOR_OVERSEA = "oversea";

    //mtk products
    private final static String LE1 = "Le1";
    private final static String LE1_ = "Le1_";
    private final static String LE1S = "Le1s";

    //qcom products
    private final static String LE1PRO = "Le1Pro";
    private final static String LEMAX = "LeMax";
    private final static String LEMAXPRO = "LeMaxPro";
    private final static String LEMAX2 = "LeMax2";

    //common products
    private final static String LE2 = "Le2";

    /**
     * Read system property value from Build.device
     *
     * @return "x600"
     * "x608"
     * "x1"
     * "max1"
     * ""
     */
    public static String getDeviceName() {
        String deviceName = Build.DEVICE;
        if (null == deviceName) {
            return "";
        }
        return deviceName;
    }

    /**
     * Read system property value from ro.product.name
     */
    public static String getProductName() {
        String productName = Build.PRODUCT;
        if (null == productName) {
            return "";
        }
        return productName;
    }

    /**
     * Read system property value from ro.product.model
     */
    public static String getModelName() {
        String modelName = Build.MODEL;
        if (null == modelName) {
            return "";
        }
        return modelName;
    }

    /**
     * Get platform information
     *
     * @return "mtk" or "qcom" or "unknown"
     */
    public static String getPlatformName() {
        String platformValue = "";
        String productName = getProductName();
        if (null == productName || "".equals(productName)) {
            platformValue = "unknown";
        }
        if (productName.equals(LE1) || productName.startsWith(LE1_) || productName.startsWith
                (LE1S)) {
            platformValue = PLATFORM_MTK;
        } else if (productName.startsWith(LE1PRO) || productName.startsWith(LEMAX) ||
                productName.startsWith(LEMAXPRO) || productName.startsWith(LEMAX2)) {
            platformValue = PLATFORM_QCOM;
        } else if (productName.startsWith(LE2)) {
            String modelName = getModelName();
            if (modelName.startsWith("Le X62")) {
                platformValue = PLATFORM_MTK;
            } else if (modelName.startsWith("Le X52")) {
                platformValue = PLATFORM_QCOM;
            }
        }
        return platformValue;
    }

    /**
     * Read system property value from "ro.product.customize",
     * if null, adapting value from "ro.product.model"
     *
     * @return "default"
     * "china-mobile"
     * "china-telecom"
     * "oversea"
     * "whole-netcom"
     * ""
     */
    public static String getVendorName() {
        String vendorType = "";
        String platformName = SystemUtil.getSystemProperty(RO_VENDOR);
        if (null != platformName && !"".equals(platformName)) {
            vendorType = platformName;
        } else {
            vendorType = lookupOldVendorTable();
        }
        if (StringUtils.equalsNull(vendorType)) {
            return VENDOR_DEFAULT;
        }
        return vendorType;
    }

    /**
     * Read system property value from Build.MODEL
     * Warning: ro.product.model has changed as below:
     * X60* ----> Le 1
     * X80* ----> Le 1 Pro
     * X90* ----> Le Max
     *
     * @return "Le 1"
     * "Le 1 Pro"
     * "Le Max"
     * ""
     */
    public static String getProductModelName() {
        String productModel = Build.MODEL;
        // need convert "x60*/x80*/x90*" to "Le 1/Le 1 Pro/Le Max"
        if (productModel.matches(COMMON_PATTERN_REGEX)
                || productModel.matches(WHOLE_NETCOM_PATTERN_REGEX)) {

            if (productModel.charAt(1) == '6') {
                productModel = LE_1;
            } else if (productModel.charAt(1) == '8') {
                productModel = LE_PRO;
            } else if (productModel.charAt(1) == '9') {
                productModel = LE_MAX;
            }
        }
        if (StringUtils.equalsNull(productModel)) {
            return LE_1;
        }
        return productModel;
    }

    /**
     * convert value from Build.MODEL
     *
     * @return
     */
    private static String lookupOldVendorTable() {
        String vendorValue = "";
        String modelInfo = Build.MODEL;
        if (StringUtils.equalsNull(modelInfo)) {
            return VENDOR_DEFAULT;
        }
        if (modelInfo.matches(COMMON_PATTERN_REGEX)) {
            if (modelInfo.endsWith("0")) {
                vendorValue = VENDOR_DEFAULT;
            } else if (modelInfo.endsWith("6")) {
                vendorValue = VENDOR_CT;
            } else if (modelInfo.endsWith("8")) {
                vendorValue = VENDOR_CMCC;
            }
        } else if (modelInfo.matches(WHOLE_NETCOM_PATTERN_REGEX)) {
            if (modelInfo.endsWith("+")) {
                vendorValue = VENDOR_WN;
            }
        } else if (modelInfo.startsWith("Le")
                && (modelInfo.endsWith("Max") || modelInfo.endsWith("Pro"))) {
            vendorValue = VENDOR_OVERSEA;
        }

        return vendorValue;
    }

}