package com.letv.leauto.ecolink.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.letv.leauto.ecolink.cfg.EnvStatus;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.tracker2.agnes.Agnes;
import com.letv.tracker2.agnes.App;
import com.letv.tracker2.agnes.Event;
import com.letv.tracker2.agnes.Widget;
import com.letv.tracker2.enums.EventType;
import com.letv.tracker2.enums.Key;
import com.letv.tracker2.msg.bean.Version;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

//import com.letv.tracker.msg.bean.Version;

/**
 *
 */
public class LetvReportUtils {
    private static Map<String, String> page_uuld = new HashMap<>();
    public static String userId = "";
    private static Context mContext;
    private static int mMajor = -1;
    private static int mMinor = -1;
    private static int mPatch = -1;

    public static final String APP_NAME = "Ecolink_android";//格式00:00:00
    public static final String UID = "uid";
    public static final String DID = "DID";
    public static final String CPID = "CPID";
    public static final String SONGID = "SONGID";
    public static final String ALBUMID = "albumId";
    public static final String SORTID = "SORTID";
    public static final String ACTION = "ACTION";
    public static final String ISLOVE = "ISLOVE";
    public static final String ALGORITHMID = "ALGORITHMID";
    public static final String PLAYTIME = "PLAYTIME";//格式00:00:00
    public static final String MEDIATIME = "MEDIATIME";//格式00:00:00
    public static final String sourceId = "sourceId";
    public static final String tagId = "tagId";
    public static final String songId = "songId";


    static String[] SOURCE_CP_ID_FILTER = {
            // "cc431a6a-3590-11e5-b07a-fa163e6f7961"/*cms发布到乐听库的虾米音乐*/,
            "c9e0c736-3590-11e5-b07a-fa163e6f7961"/*乐视体育点播*/,
            "b2aaef5e-3f24-11e5-b43b-fa163e6f7961"/*乐视音乐点播*/,
    };

    public static void init(Context context) {
        mContext = context;
        try {
            Agnes.getInstance().setContext(context);
        }catch (Exception e){
            Trace.Error("======", "LetvReportUtils init fail ：e=" + e);
        }
        Agnes.getInstance().getConfig().enableLog();
    }


    /**
     * report run or ready其中Event、Play、MusicPlay都必须显示调用如下方法上报，由调用者自行选择上报时机。
     */
    public static void reportMessages(String albumId, MediaDetail detail, String action) {

        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = null;
        if (action.equals("book")) {
            event = app.createEvent(EventType.Book);
        } else {
            event = app.createEvent(EventType.Unbook);
        }
        event.addProp(ALBUMID, albumId);
        event.addProp(sourceId, detail.SOURCE_CP_ID);
        event.addProp(tagId, EnvStatus.Sort_Id);
        Trace.Info("LetvReportUtils", ",userId = " + userId);
        agnes.report(event);

    }
    public static void reportDownloadMusic(String albumId, MediaDetail detail) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("downloadAudio");
        event.addProp(ALBUMID, albumId);
        event.addProp(sourceId, detail.SOURCE_CP_ID);
        event.addProp(tagId, EnvStatus.Sort_Id);
        event.addProp(songId, detail.NAME);
        agnes.report(event);
    }
    public static void reportMessagesPlay(String cpId) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent(EventType.Play);
//        event.addProp("cpId", cpId);
        event.addProp("sourceId", cpId);
        event.addProp(tagId, EnvStatus.Sort_Id);
        Trace.Info("LetvReportUtils", ",电台播放页 ");
        agnes.report(event);

    }



    public static void reportJumpAppEvent(String result) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("search");
        event.addProp("result", result);
        Trace.Info("LetvReportUtils", ",语音搜索页 ");
        agnes.report(event);
    }

    public static void reportMapSearchEvent() {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("mapSearch");
        agnes.report(event);
    }

    public static void reportJumpHelpAppEvent(String fromName) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Widget widget = app.createWidget(fromName);
        Event event = widget.createEvent(EventType.Expose);
        event.addProp(Key.From, fromName);
        if (fromName.equals("4.1.2")) {
            event.addProp(Key.WidgetName, "乐键帮助页");
        } else if (fromName.equals("4.1.3")) {
            event.addProp(Key.WidgetName, "新手帮助页");
        }
        Trace.Info("LetvReportUtils", ",fromName= " + fromName);
        agnes.report(event);
    }

    public static void reportMessage(boolean selected) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event;
        if (selected) {
            event = app.createEvent("select");
        } else {
            event = app.createEvent("unselect");
        }
        Trace.Info("LetvReportUtils", ",设置");
        agnes.report(event);
    }

    public static void reportVoiceSearch(String searchTerm, String type) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("voiceInput");
        event.addProp("searchTerm", searchTerm);
        event.addProp("searchType", type);
//        event.addProp("type", type);
        agnes.report(event);
    }


    public static void reportUpgradeEvent(Context context, String version, boolean result, String failCause) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent(EventType.Upgrade);
        event.addProp("version", version);
        event.addProp("result", result + "");
//        event.addProp("upgradeSN", version);
//        if(result) {
//            event.setResult(EventResult.Success);
//        }else {
//            event.setResult(EventResult.Failed);
//            event.addProp("failCause", failCause);
//        }
        Trace.Info("LetvReportUtils", ",个人中心:version=" + version);
        agnes.report(event);
    }

    public static void reportConnectStart(String phone_brand, String phone_brand_model, String phone_os, String phone_os_version
            , String province, String city, String OBU_screen_width, String OBU_screen_height, String OBU_os, String OBU_os_version
            , String OBU_id, String OBU_brand) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("connect_start");
        event.addProp("phone_brand", phone_brand);
        event.addProp("phone_brand_model", phone_brand_model);
        event.addProp("phone_os", phone_os);
        event.addProp("phone_os_version", phone_os_version);
        event.addProp("province", province);
        event.addProp("city", city);
        event.addProp("OBU_screen_width", OBU_screen_width);
        event.addProp("OBU_screen_height", OBU_screen_height);
        event.addProp("OBU_os", OBU_os);
        event.addProp("OBU_os_version", OBU_os_version);
//        event.addProp("phone_id", phone_id);
//        event.addProp("ip", ip);
        event.addProp("OBU_id", OBU_id);
        event.addProp("OBU_brand", OBU_brand);
//        event.addProp("OBU_MAC", OBU_MAC);
        agnes.report(event);
    }

    public static void reportConnectEnd() {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("connect_end");
        agnes.report(event);
    }

    public static void reportAudioPlayEnd(String audio_play_duration, String online, String audio_item_id) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("audio_play_end");
        event.addProp("audio_play_duration", audio_play_duration);
        event.addProp("online", online);
        event.addProp("audio_item_id", audio_item_id);
        agnes.report(event);
    }

    public static void reportConnectStartGps(String longitude, String latitude) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("connect_start_gps");
        event.addProp("longitude", longitude);
        event.addProp("latitude", latitude);
        agnes.report(event);
    }

    public static void reportConnectEndGps(String longitude, String latitude) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("connect_end_gps");
        event.addProp("longitude", longitude);
        event.addProp("latitude", latitude);
        agnes.report(event);
    }

    public static void reportClick(String page_id) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent(EventType.Click);
        event.addProp("page_id", page_id);
        agnes.report(event);
    }

    public static void reportNavigationStart(String navigation_beginning, String navigation_destination, String navigation_expect_time, String navigation_type) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("navigation_start");
        event.addProp("navigation_beginning", navigation_beginning);
        event.addProp("navigation_destination", navigation_destination);
        event.addProp("navigation_expect_time", navigation_expect_time);
        event.addProp("navigation_type", navigation_type);
        agnes.report(event);
    }

    public static void reportNavigationEnd(String navigation_actual_time, String navigation_destination_stop, String navigation_id, String to_background_usedtime) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("navigation_end");
        event.addProp("navigation_actual_time", navigation_actual_time);
        event.addProp("navigation_destination_stop", navigation_destination_stop);
        event.addProp("navigation_id", navigation_id);
        event.addProp("to_background_usedtime", to_background_usedtime);
        agnes.report(event);
    }

    public static void reportDownloadEvent(Context context, String version, String channleId) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent(EventType.Download);
        event.addProp("version", version);
        event.addProp("channleId", channleId);
        agnes.report(event);
    }

    public static void reportLoginEvent( String userId, String action) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event;
        if (action.equalsIgnoreCase("login")) {
            event = app.createEvent(action);
            event.addProp("sourceName", "button");
            event.addProp(UID, userId);
            Trace.Info("LetvReportUtils", ",登陆页");
            agnes.report(event);
        } else if (action.equalsIgnoreCase("register")) {
            event = app.createEvent(action);
            event.addProp("sourceName", "tabbar");
            event.addProp(UID, userId);
            Trace.Info("LetvReportUtils", ",登陆页:userId=" + userId);
            agnes.report(event);
        }
    }

    public static int getMajorVer() {
        if (mMajor >= 0) {
            return mMajor;
        }
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            String[] digits = versionName.split("\\.");
            if (digits.length >= 1) {
                mMajor = Integer.parseInt(digits[0]);
            } else {
                mMajor = 0;
            }
        } catch (Exception e) {
            mMajor = 0;
            e.printStackTrace();
        }
        return mMajor;
    }

    public static int getMinorVer() {
        if (mMinor >= 0) {
            return mMinor;
        }
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            String[] digits = versionName.split("\\.");
            if (digits.length >= 2) {
                mMinor = Integer.parseInt(digits[1]);
            } else {
                mMinor = 0;
            }
        } catch (Exception e) {
            mMinor = 0;
            e.printStackTrace();
        }
        return mMinor;
    }

    public static int getPatchVer() {
        if (mPatch >= 0) {
            return mPatch;
        }
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            String[] digits = versionName.split("\\.");
            if (digits.length >= 3) {
                mPatch = Integer.parseInt(digits[2]);
            } else {
                mPatch = 0;
            }
        } catch (Exception e) {
            mPatch = 0;
            e.printStackTrace();
        }
        return mPatch;
    }

    private static void setAppVersion(App app) {
        Version appVersion = app.getVersion();
        appVersion.setVersion(getMajorVer(), getMinorVer(), getPatchVer());
    }

    public static void recordAppStart() {
//        Agnes agnes = Agnes.getInstance();
//        App app = agnes.getApp(APP_NAME);
//        setAppVersion(app);
//        app.run();
//        Agnes.getInstance().report(app);
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event e = app.createEvent("run");
        Agnes.getInstance().report(e);
    }

    public static void recordActivityStart(String activityId) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Long currantTime = getCurrantTime();
        String page_uuld_str = activityId + "_" + currantTime;
        page_uuld.put(activityId, page_uuld_str);
        Event e = app.createEvent(EventType.acStart);
        e.addProp("activityId", activityId);
        e.addProp("page_uuid", page_uuld_str);
        Agnes.getInstance().report(e);
    }

    Long currantTime = getCurrantTime();

    public static void recordActivityEnd(String activityId) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event e = app.createEvent(EventType.acEnd);
        e.addProp("activityId", activityId);
        if (page_uuld.get(activityId) != null) {
            e.addProp("page_uuid", page_uuld.get(activityId));
            Agnes.getInstance().report(e);
            page_uuld.remove(activityId);
        }
    }

    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString =
                    byteArrayToHexString(md.digest(resultString.getBytes()));
        } catch (Exception ex) {
        }
        return resultString;
    }

    public static Long getCurrantTime() {
        Calendar c = Calendar.getInstance();
        Long currantTime = c.getTimeInMillis();
        return currantTime;
    }

    /**
     * 转换字节数组16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String getDid(Context context) {
        String did = getIMEI(context) + getIMSI(context) + getDeviceName() + getBrandName() + getMacAddress(context);
        return MD5Encode(did);
    }

    public static String getIMEI(Context context) {
        try {
            String deviceId = ((TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE)).getDeviceId();

            if (TextUtils.isEmpty(deviceId)) {
                return "";
            } else {
                return deviceId.replace(" ", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getIMSI(Context context) {
        if (context == null) {
            return "";
        }

        String subscriberId = null;
        try {
            subscriberId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();

            if (null == subscriberId || subscriberId.length() <= 0) {
                subscriberId = generate_DeviceId(context);
            } else {
                subscriberId.replace(" ", "");
                if (TextUtils.isEmpty(subscriberId)) {
                    subscriberId = generate_DeviceId(context);
                }
            }

            return subscriberId;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return subscriberId;
        }
    }

    public static String generate_DeviceId(Context context) {
        String str = getIMEI(context) + getDeviceName() + getBrandName() + getMacAddress(context);

        return MD5Encode(str);
    }

    public static String getDeviceName() {
        return ensureStringValidate(android.os.Build.MODEL);
    }

    public static String getBrandName() {
        String brand = ensureStringValidate(android.os.Build.BRAND);

        if (TextUtils.isEmpty(brand)) {
            return "";
        }

        return getData(brand);
    }

    public static String getMacAddress(Context context) {
        WifiInfo wifiInfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .getConnectionInfo();

        if (wifiInfo != null) {
            return ensureStringValidate(wifiInfo.getMacAddress());
        }

        return "";
    }

    public static String getData(String data) {
        if (TextUtils.isEmpty(data)) {
            return "-";
        }

        return data.replace(" ", "_");
    }

    public static String ensureStringValidate(String str) {
        return str == null ? "" : str;
    }


}