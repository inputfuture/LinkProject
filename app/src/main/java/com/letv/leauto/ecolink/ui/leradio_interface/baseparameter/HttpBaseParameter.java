package com.letv.leauto.ecolink.ui.leradio_interface.baseparameter;

import com.letv.mobile.core.utils.ContextProvider;
import com.letv.mobile.core.utils.DeviceUtils;
import com.letv.mobile.core.utils.LanguageCodeUtil;
import com.letv.mobile.core.utils.SystemUtil;
import com.letv.mobile.http.parameter.LetvBaseParameter;

import java.util.HashMap;

//最基本的参数
public class HttpBaseParameter extends HashMap<String, String> {

    private static final long serialVersionUID = -6696431583924895751L;
    private static final String COMMON_KEY_PCODE = "pcode";// 产品编码
    private static final String COMMON_KEY_DEV_KEY = "devKey";// 客户端设备唯一id
    private static final String COMMON_KEY_DEV_ID = "devId";// 客户端设备唯一id,
                                                            // 能获取到IMEI取IMEI，获取不到取mac
    private static final String COMMON_KEY_TERMINAL_SERIES = "terminalSeries";// 设备型号
    private static final String COMMON_KEY_APP_VERSION = "appVersion";// 版本号
    private static final String COMMON_KEY_UID = "uid";// 用户ID
    private static final String COMMON_KEY_TOKEN = "token";// 用户ID
    private static final String COMMON_KEY_BS_CHANNEL = "bsChannel";// 渠道号
    private static final String COMMON_KEY_TERMINAL_APPLICATION = "terminalApplication";// 应用
    private static final String COMMON_KEY_TERMINAL_BRAND = "terminalBrand";
    private static final String COMMON_KEY_LANGCODE = "langcode";
    private static final String COMMON_KEY_WCODE = "wcode";
    private static final String COMMON_KEY_MAC = "mac";
    protected static final String COMMON_KEY_SALE_AREA = "salesArea";

    // 延用之前的pcode
    private static final String PCODE = "160110000";

    private static String devKey;
    private static String devId;
    private static String terminalSeries;
    private static String appVersion;
    private static String bsChannel;
    private static String terminalAppliction;
    private static String terminalBrand;
    // TODO(zhaoxinyang): init below
    private static String langcode;// 语言码
    private static String wcode; // 区域码
    private static String mac;
    private static String salesArea;

    static {
        // terminalAppliction = "letv_lemusic_app_default_rse";
        terminalAppliction = "leautolink";
        bsChannel = "eui_letv";
        appVersion = SystemUtil.getVersionName(ContextProvider.getApplicationContext());
        devId = DeviceUtils.getDeviceId();
        // devKey = DeviceUtils.getDeviceKey();
        terminalSeries = DeviceUtils.getTerminalSeries();
        terminalBrand = "letv";
        mac = SystemUtil.getMacAddress();
        langcode = LanguageCodeUtil.getLanguageCode();
        wcode = "cn";
        salesArea = DeviceUtils.getSalesArea();
    }

    public HttpBaseParameter combineParams() {
//        if (LoginModel.isLogin()) {
//            this.put(COMMON_KEY_UID, LoginModel.getUID());
//            this.put(COMMON_KEY_TOKEN, LoginModel.getToken());
//        }
//        this.put(COMMON_KEY_PCODE, PCODE);
//        this.put(COMMON_KEY_DEV_KEY, devKey);
//        this.put(COMMON_KEY_DEV_ID, devId);
//        this.put(COMMON_KEY_TERMINAL_SERIES, terminalSeries);
//        this.put(COMMON_KEY_BS_CHANNEL, bsChannel);
//        this.put(COMMON_KEY_APP_VERSION, appVersion);
//        this.put(COMMON_KEY_TERMINAL_APPLICATION, terminalAppliction);
//        this.put(COMMON_KEY_TERMINAL_BRAND, terminalBrand);
//        this.put(COMMON_KEY_LANGCODE, langcode);
//        this.put(COMMON_KEY_WCODE, wcode);
//        this.put(COMMON_KEY_MAC, mac);
//        this.put(COMMON_KEY_SALE_AREA, salesArea);
        return this;
    }

    public String toJSONString() {
        return null;
    }

}
