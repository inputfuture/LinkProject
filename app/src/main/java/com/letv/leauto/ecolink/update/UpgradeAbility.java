package com.letv.leauto.ecolink.update;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.WindowManager;
import android.widget.Toast;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.Trace;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;


public class UpgradeAbility {
    private static final String TAG = "UpgradeAbility";

    public static final String DO_NOT_MIND_UPGRADE_VERSION = "do_not_mind_upgrade_version";
    public static final String NOT_MIND_UPGRADE_VERSION = "not_mind_upgrade_version";
    public static final String DOWNLOADED_RELEASE_NOTES = "downloaded_release_notes";
    public static final String DOWNLOADED_VERSION = "downloaded_version";
    private static final String TEST_URL = "http://115.182.94.28/iptv/api/apk/getUpgradeProfile";
    private static final String TRUE_URL = "http://ota.scloud.letv.com/api/v1/apk/upgradeProfile";
    private static final int CONNECTION_TIMEOUT = 20000;
    private static final String ENCODEING = "utf-8";
    private static final int UPDATE_NOT = 0;
    private static final int UPDATE_OPTIONAL = 1;
    private static final int UPDATE_FORCE = 2;
    private static final int NETWORK_DISCONNECTED_BACKGROUND = 1001;
    private static final int NETWORK_DISCONNECTED_FOREGROUND = 1002;
    private boolean isChecking = false;
    private String packageName;
    private String packageVersion;
    private String deviceType;
    private String deviceModel;
    private String deviceResolution;
    private String deviceMac;
    private IUpgradeAbility listener;
    public UpdateData updateInfo;
    private Context mContext;
    private CustomAlertDialog newUpdateDialog;
    private CustomProgressDialog downLoadDialog;
    private DownLoadApk downLoadApk;
    private static final long REQUEST_INTERVAL = 1000*60*60*6;
    private static final String LAST_APP_CHECKED_TIME ="lastAppCheckedTime";
    public static final String SERVER_APP_VERSION ="serverAppVersion";

    public UpgradeAbility(Context mContext) {
        this.mContext = mContext;
    }
    public void checkAppUpdateInterval(){
        CacheUtils cacheUtils = CacheUtils.getInstance(mContext);
        Long time = cacheUtils.getLong(LAST_APP_CHECKED_TIME, 0);
        if((System.currentTimeMillis()-time)>REQUEST_INTERVAL && isWifiConnected() ){
            Trace.Info(TAG, "checkAppUpdateInterval ");
            checkUpgrade(false, true);
        }
    }
    private Handler mHander = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_NOT:
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.is_new),
                            Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.hasNewVersion(false);
                    }
                    isChecking = false;
                    break;
                case NETWORK_DISCONNECTED_BACKGROUND:
                    // Do not show Toast when checking upgrade, remove by renkai,
                    // 2014-6-10
                    // Toast.makeText(mContext, R.string.network_failed,
                    // Toast.LENGTH_SHORT).show();
                    break;
                case NETWORK_DISCONNECTED_FOREGROUND:
                    CacheUtils cacheUtils = CacheUtils.getInstance(mContext);
                    String version = cacheUtils.getString(UpgradeAbility.DOWNLOADED_VERSION, "");
                    String releaseNotes = cacheUtils.getString(UpgradeAbility.DOWNLOADED_RELEASE_NOTES, "");
                    if (localApkExists(version) && isGreaterVersion(version)) {
                        showOptionalUpgradeDialog(version, releaseNotes);
                    }
                    else {
//                        Toast.makeText(mContext, "无网络",
//                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    public void checkUpgrade(final boolean from_setting,boolean background) {
        //if (isNetworkConnected()) {
        exectiveChecking(from_setting, background);
        //}
    }

    private void exectiveChecking(final boolean from_setting, final boolean background) {
        if (isChecking) {
            return;
        }
        isChecking = true;
        new Thread() {
            public void run() {
                getDeviceInfo();
                if(!isNetworkConnected()){
                    int what = background?NETWORK_DISCONNECTED_BACKGROUND:NETWORK_DISCONNECTED_FOREGROUND;
                    mHander.sendEmptyMessage(what);
                    isChecking = false;
                    return;
                }
                byte[] data = getUpdateMessage();
                if (data == null) {
                    int what = background?NETWORK_DISCONNECTED_BACKGROUND:NETWORK_DISCONNECTED_FOREGROUND;
                    mHander.sendEmptyMessage(what);
                    isChecking = false;
                    return;
                }
                CacheUtils cacheUtils = CacheUtils.getInstance(mContext);
                cacheUtils.putLong(LAST_APP_CHECKED_TIME, System.currentTimeMillis());
                // if (intent != null) {
                // this.from_setting = intent
                // .getBooleanExtra("from_setting", false);
                // } else {
                // from_setting = false;
                // }
                updateInfo = analysisJSON(data);
                if (updateInfo == null) {
                    updateInfo = analysisXML(data);
                }

                if (updateInfo == null) {
                    // stopSelf();
                    isChecking = false;
                    return;
                }
                cacheUtils.putString(SERVER_APP_VERSION, updateInfo.getVersion());
//                EventBus.getDefault().post(new GetServerAppVersionEvent());

                // background download apk
                if(background){
                    if (!localApkExists(updateInfo.getVersion()) && updateInfo.getUrl()!=null && !updateInfo.getUrl().equalsIgnoreCase("")) {
                        downloadApk(true);
                    }
                    else{
                        Trace.Error(TAG, "not download");
                    }
                    isChecking = false;
                    return;
                }

                if (UPDATE_OPTIONAL == updateInfo.getCommand()) {
                    // showOptionalUpdateDialog();
                    mHander.post(new Runnable() {

                        @Override
                        public void run() {
                            SharedPreferences sf = mContext
                                    .getApplicationContext()
                                    .getSharedPreferences(
                                            UpgradeAbility.DO_NOT_MIND_UPGRADE_VERSION,
                                            Activity.MODE_PRIVATE);
                            if (!from_setting
                                    && updateInfo
                                    .getVersion()
                                    .equals(sf
                                            .getString(
                                                    UpgradeAbility.NOT_MIND_UPGRADE_VERSION,
                                                    ""))) {

                            } else {
                                showOptionalUpgradeDialog(updateInfo.getVersion(), updateInfo.getNote());
                            }
                        }
                    });
                } else if (UPDATE_FORCE == updateInfo.getCommand()) {
                    mHander.post(new Runnable() {

                        @Override
                        public void run() {
                            showOptionalUpgradeDialog(updateInfo.getVersion(), updateInfo.getNote());
                        }
                    });
                } else if (UPDATE_NOT == updateInfo.getCommand()) {
                    if (from_setting) {
                        mHander.sendEmptyMessage(UPDATE_NOT);
                    }
                }
                isChecking = false;
            }
        }.start();
    }

    private void getDeviceInfo() {
        packageName = mContext.getPackageName();
        packageVersion = getPackageVersion();
        deviceType = getDeviceType();
        deviceModel = getDeviceModel();
        deviceResolution = getDeviceResolution();
        deviceMac =getDeviceMac();
//        deviceMac ="c825e1775c3c";
    }

    private byte[] getUpdateMessage() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("package", packageName);
        params.put("version", packageVersion);
        params.put("type", deviceType);
        params.put("model", deviceModel);
        // params.put("resolution", deviceResolution);
        params.put("mac", deviceMac);
        String url = getUrlWithParams(TRUE_URL, params);
        byte[] data = null;
        try {
            URL httpurl=new URL(url);
            HttpURLConnection httpURLConnection= (HttpURLConnection) httpurl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            httpURLConnection.connect();
            InputStream inputStream=httpURLConnection.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            inputStream.close();
            data = os.toByteArray();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        HttpGet request = new HttpGet(url);

//        HttpResponse response = null;
//        byte[] data = null;
//        try {
//            HttpClient client = new DefaultHttpClient();
//            client.getParams()
//                    .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
//                            CONNECTION_TIMEOUT);
//            response = client.execute(request);
//            if (response != null
//                    && response.getStatusLine().getStatusCode() == 200) {
//                HttpEntity entity = response.getEntity();
//                int length = (int) entity.getContentLength();
//                InputStream is = entity.getContent();
//                if (length > 0) {
//                    data = new byte[length];
//                    is.read(data);
//                } else {
//                    ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
//                    byte[] buffer = new byte[1024];
//                    int len = 0;
//                    while ((len = is.read(buffer, 0, buffer.length)) != -1) {
//                        os.write(buffer, 0, len);
//                    }
//                    os.flush();
//                    is.close();
//                    data = os.toByteArray();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return data;
    }

    private UpdateData analysisJSON(byte[] data) {
        UpdateData info = null;
        String json = null;
        try {
            json = new String(data, "utf-8");
            Trace.Error("======",json);
            JSONTokener jsonParser = new JSONTokener(json);
            JSONObject jsonObject = (JSONObject) jsonParser.nextValue();

            info = new UpdateData();
            info.setCommand(jsonObject.getInt("update"));
            info.setVersion(jsonObject.getString("version"));
            info.setNote(jsonObject.getString("note"));
            info.setUrl(jsonObject.getString("url"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    private UpdateData analysisXML(byte[] data) {
        UpdateData info = null;
        InputStream stream = new ByteArrayInputStream(data);
        String s = null;
        try {
            s = new String(data, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(stream, ENCODEING);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName().toLowerCase();
                        if (name.equals("apkupgraderesponse")) {
                            info = new UpdateData();
                        } else {
                            String text = parser.nextText();
                            if (text != null) {
                                text = text.trim();
                                if (name.equals("update")) {
                                    info.setCommand(Integer.parseInt(text));
                                } else if (name.equals("version")) {
                                    info.setVersion(parser.nextText());
                                } else if (name.equals("note")) {
                                    info.setNote(parser.nextText());
                                } else if (name.equals("url")) {
                                    info.setUrl(parser.nextText());
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return info;
    }

    /** start UpdateDialogActivity */
    // private void showOptionalUpdateDialog() {
    // Intent intent = new Intent(mContext, UpdateDialogActivity.class);
    // intent.putExtra("updateInfo", updateInfo);
    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // mContext.startActivity(intent);
    // }

    private boolean localApkExists(String versions){
        String path = SdCardUtils.getSDCardRootPath(mContext) + "/";
        File apk = new File(path + DownloadUtils.getDownLoadApkName(versions));
        return apk.exists();
    }
    private boolean isGreaterVersion(String version){
        boolean ret = false;
        try {
            Float fv = Float.parseFloat(version);
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            return fv>info.versionCode;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }
    /**
     * show Optionnal Dialog before Download
     */
    private void showOptionalUpgradeDialog(final String version, final String releaseNotes) {
        int msgRid = R.string.upgrade_message_from_server;
        if (localApkExists(version)) {
            msgRid = R.string.upgrade_message_from_local;
        }
        String msg = mContext.getString(msgRid);
        CustomDialog customDialog=new CustomDialog(mContext, R.style.Dialog);
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void positiveClick() {
                LetvReportUtils.reportDownloadEvent(mContext, updateInfo.getVersion()+"",/* UpdateConfig.getChannel(mContext)*/"letv");
                LetvReportUtils.reportUpgradeEvent(mContext, updateInfo.getVersion()+"", true, null);
                if (localApkExists(version)) {
                    DownloadUtils.installApk(mContext, SdCardUtils.getSDCardRootPath(mContext) + "/"
                            + DownloadUtils.getDownLoadApkName(version));
                } else {
                    showDownLoadDialog();
                }

            }

            @Override
            public void negativeClick() {
                LetvReportUtils.reportUpgradeEvent(mContext, updateInfo.getVersion()+"", false, null);

            }

            @Override
            public void notUpdateChecked(boolean check) {
                if (check) {
                    SharedPreferences sf = mContext.getApplicationContext()
                            .getSharedPreferences(UpgradeAbility.DO_NOT_MIND_UPGRADE_VERSION,
                                    Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putString(UpgradeAbility.NOT_MIND_UPGRADE_VERSION,
                            version);
                    editor.apply();
                } else {
                    SharedPreferences sf = mContext.getApplicationContext()
                            .getSharedPreferences(UpgradeAbility.DO_NOT_MIND_UPGRADE_VERSION,
                                    Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putString(UpgradeAbility.NOT_MIND_UPGRADE_VERSION,
                            null);
                    editor.apply();

                }

            }
        });
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
        customDialog.setTitle(mContext.getString(R.string.upgrade_title));
        customDialog.setMessage(updateInfo.getNote());

    }
    /**
     * show download Dialog
     */
    private void showDownLoadDialog() {


//        downLoadDialog=new CustomProgressDialog(mContext, R.style.Dialog);
//        downLoadDialog.setClickListener(new CustomProgressDialog.ClickListener() {
//            @Override
//            public void positiveClick() {
//
//            }
//
//            @Override
//            public void negativeClick() {
//                if(downLoadApk!=null)
//                    downLoadApk.setStopDownload(true);
//            }
//        });
//
////        downLoadDialog.setCanceledOnTouchOutside(false);
//        downLoadDialog.show();
//        downLoadDialog.setTitle(mContext.getString(R.string.download_update));
//        downLoadDialog.setMessageView(mContext.getString(R.string.downloading_message));
        downloadApk(false);
    }
    private void downloadApk(boolean background){
        if(!background || (background && isWifiConnected())) {
            downLoadApk = new DownLoadApk(mContext, myUpgradeDialogHandler, background);
            downLoadApk.setUpdateData(updateInfo);
            downLoadApk.downloadApk();
        }
    }

    private String getPackageVersion() {
        String versionName = null;
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            versionName = info.versionCode + "";
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private String getDeviceType() {
        return "3rd";

        // if (Build.VERSION.SDK_INT < 11) {
        // return "phone";
        // } else if (Build.VERSION.SDK_INT < 14) {
        // return "pad";
        // }
        //
        // TelephonyManager tm = (TelephonyManager)
        // mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        // String deviceID = tm.getDeviceId();
        // if (deviceID != null) {
        // return "phone";
        // }
        //
        // DisplayMetrics dm = new DisplayMetrics();
        // ((WindowManager)
        // mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay()
        // .getMetrics(dm);
        // double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2.0)
        // + Math.pow(dm.heightPixels, 2.0));
        // double screenSize = cf / (160 * dm.density);
        // if (screenSize <= 6.5f) {
        // return "phone";
        // } else if (screenSize <= 12f) {
        // return "pad";
        // } else {
        // return "tv";
        // }
    }

    private String getDeviceModel() {
        return "ui";
    }

    private String getDeviceResolution() {
        DisplayMetrics dm = new DisplayMetrics();
//		if (Build.VERSION.SDK_INT < 17) {
        ((WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(dm);
        // } else {
        // ((WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE))
        // .getDefaultDisplay().getRealMetrics(dm);
        // }
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return width + "x" + height;
    }

    private String getDeviceMac() {
        String mac= NetworkUtil.getMACAddress("wlan0");
        if (mac != null) {
            return mac.replaceAll(":", "");
        }
        if(!mac.equalsIgnoreCase("")){
            return mac;
        }

        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(mContext.TELEPHONY_SERVICE);
        String deviceID = tm.getDeviceId();
        if (deviceID != null && !deviceID.startsWith("000000000000000")) {
            return deviceID;
        }

        String androidID = Secure.getString(mContext.getContentResolver(),
                Secure.ANDROID_ID);
        if (androidID != null)
            return androidID;
        else
            return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 检测网络是否可用
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null) {
            return network.isAvailable();
        }
        return false;
    }
    private boolean isWifiConnected(){
        boolean isWifiConn = false;
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String extra = mWifi.getExtraInfo();
        if( mWifi!=null && mWifi.isConnected() &&extra!=null){
            isWifiConn = true;
        }
        return isWifiConn;
    }

    private String getUrlWithParams(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url);
        try {
            builder.append('?');
            Set<Entry<String, String>> entry = params.entrySet();
            for (Entry<String, String> param : entry) {
                builder.append(URLEncoder.encode(param.getKey(), ENCODEING));
                builder.append('=');
                builder.append(URLEncoder.encode(param.getValue(), ENCODEING));
                builder.append('&');
            }
            builder.deleteCharAt(builder.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    //
    // @Override
    // public IBinder onBind(Intent intent) {
    // return null;
    // }

    public void setListener(IUpgradeAbility listener) {
        this.listener = listener;
    }

    public interface IUpgradeAbility {
        public void hasNewVersion(boolean hasNewVersion);
    }

    public final static int PROGRESS_SET_MAX = 0;
    public final static int PROGRESS_SET_PROGRESS = 1;
    public final static int UPDATE_FAIL_SERVER = 2;
    public final static int UPDATE_FAIL_CLIENT = 3;
    public final static int CURRENT_DIALOG_DISMISS = 100;
    private Handler myUpgradeDialogHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS_SET_MAX:
                    break;
                case PROGRESS_SET_PROGRESS:
                    if(downLoadDialog != null){
                        int percentage = (int) (100.0f * msg.arg1 / msg.arg2);
                        downLoadDialog.setProgress(percentage);
                    }
                    // textPercentage.setText(percentage + "%");
                    break;
                case UPDATE_FAIL_SERVER:
                    Toast.makeText(mContext, "服务器出错，更新失败，请稍后再试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_FAIL_CLIENT:
                    Toast.makeText(mContext, "更新失败，请检查网络连接是否正常",
                            Toast.LENGTH_SHORT).show();
                    break;
                case CURRENT_DIALOG_DISMISS:
                    if(downLoadDialog != null){
                        downLoadDialog.dismiss();
                    }
                default:
                    break;
            }
        }
    };
    public static boolean hasNewApp(Context ctx){

        CacheUtils cacheUtils = CacheUtils.getInstance(ctx);
        int version = 0;
        try{
            version=Integer.parseInt(cacheUtils.getString(UpgradeAbility.SERVER_APP_VERSION, "0"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        PackageInfo pi= null;
        try {
            pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if(pi!=null){
            return pi.versionCode<version;
        }
        return false;
    }
}
