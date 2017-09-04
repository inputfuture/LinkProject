package com.letv.leauto.ecolink;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.baidu.mapapi.SDKInitializer;
import com.leautolink.multivoiceengins.IServer;
import com.leautolink.multivoiceengins.engine.Config;
import com.leautolink.multivoiceengins.http.base.HttpHelper;
import com.letv.dispatcherlib.manager.LeVoiceEngineManager;
import com.letv.dispatcherlib.manager.init.BaiduInitParams;
import com.letv.dispatcherlib.manager.listener.InitListener;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.database.manager.LeDBHelper;
import com.letv.leauto.ecolink.event.CallEvent;
import com.letv.leauto.ecolink.event.HomeEvent;
import com.letv.leauto.ecolink.event.LinkCarConnectStatusObservable;
import com.letv.leauto.ecolink.event.MusicEvent;
import com.letv.leauto.ecolink.event.NavEvent;
import com.letv.leauto.ecolink.leplayer.common.LePlayerCommon;
import com.letv.leauto.ecolink.net.GsonUtils;
import com.letv.leauto.ecolink.net.OkHttpRequest;
import com.letv.leauto.ecolink.receiver.BluetoothReceiver;
import com.letv.leauto.ecolink.thincar.ThinCarIAOACallback;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.GeneralUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.PermissionCheckerUtils;
import com.letv.leauto.ecolink.utils.SpUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.leauto.favorcar.FavorLibraryApp;
import com.letv.loginsdk.LetvLoginSdkManager;
import com.letv.mobile.core.utils.ContextProvider;
import com.letv.mobile.http.HttpGlobalConfig;
import com.letv.voicehelp.agencies.AgenciesManager;
import com.letv.voicehelp.eventbus.EventBusHelper;
import com.letv.voicehelp.manger.LeVoiceManager;
import com.letv.voicehelp.manger.call.Contact;
import com.letv.voicehelp.manger.call.LeVoiceCallManger;
import com.letv.voicehelp.manger.command.LeVoiceCommandManager;
import com.letv.voicehelp.manger.music.LeVoiceMusicManager;
import com.letv.voicehelp.manger.nav.LeVoiceNavManager;
import com.letv.voicehelp.manger.nav.SearchPoi;
import com.letv.voicehelp.model.MediaDetail;
import com.letv.voicehelp.utils.LeVoiceEngineUtils;
import com.letvcloud.cmf.CmfHelper;
import com.letvcloud.cmf.utils.DeviceUtils;
import com.letvcloud.cmf.utils.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.List;

//import com.leautolink.lemultivoiceengineslibrary.manager.init.BaiduInitParams;

public class EcoApplication extends MultiDexApplication {

    public static EcoApplication instance;
    public static LeGlob LeGlob;
    public static boolean mIsRestart;/*标志是否是横竖屏切换导致的重新启动*/
    public static boolean mIsRestarting = false;
    private SQLiteDatabase globalDb;
    public static ArrayList<String> logList = new ArrayList<String>();

    public static boolean isLocation = false;
    public int ShowScreen;//显示页面status
    private String city = null;
    private String adCode = null;
    private String province = null;
    private String sunrise = null;
    private String sunset = null;
    private double latitude;
    private double longitude;


    public static boolean isAoaRestart = false;
    public static boolean isAdbRestart = false;

    private boolean debugTag=BuildConfig.DEBUG;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    private AudioManager audioManager;
    private ComponentName mRemoteControlClientReceiverComponent;
    public static Typeface typeFace;
    private AMapLocation currentLoaction;
    public String address;

    public static boolean firstBoot = true;
    /*****************************集团登录SDK*********************************/
    /**
     * 平台标示,测试使用,发布时要换成自己的plat
     */
    //public String platName = "car_recorder";
    public String platName = "leauto";
    /**
     * 从QQ开发平台上申请到的APP ID和APP KEY
     */
    public static String QQ_APP_ID = "1105409216";
    //    public static String QQ_APP_ID = "1105168737";
    public static String QQ_APP_KEY = "AK8An2dkUsQhRZEs";
//    public static String QQ_APP_KEY = "mq9Uq2NBVStG42xL";
    /**
     * 从新浪开发平台上申请到的App Key和应用的回调页
     */
    public static String SINA_APP_KEY = "1934408605";
    public static String SINA_APP_SECRET = "b0b21c661b5c73fca2449ecd8824cfd0";
    public static String REDIRECT_URL = "http://ecolink.leautolink.com/";// 应用的回调页
    /**
     * 从微信开发平台上申请到的APP ID和AppSecret
     */
//    public static String WX_APP_ID = "wx05009e5a71c72eba";
    public static String WX_APP_ID = "wxe96e7888e16b06bb";
    //    public static String WX_APP_SECRET = "1e02922db9a922ea6b30332376d555bd";
    public static String WX_APP_SECRET = "b0b21c661b5c73fca2449ecd8824cfd0";


    public static String SuperId_appID = "4b8cb88cdf84e069e0bd1da2";
    public static String SuperId_appsecret = "95dd5b0344e6c012ed5340ee";
    public static String SuperId_appSignToken = "jX5pM5MpaMOI9c2GNAwZZN1qmnaI2y0W";

    public static String GOOGLE_SERVER_CLIENT_ID = "40664878722-3qmrg4gclkfuhede1lga4e1672ik6ad2.apps.googleusercontent.com";

    public LinkCarConnectStatusObservable mObservable = new LinkCarConnectStatusObservable();

    public LinkCarConnectStatusObservable getObservable(){
        return mObservable;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private Handler mhandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

        }
    };



    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        //注册接收的Receiver,只有BluetoothReceiver能够接收到了，它是出于栈顶的。
        mRemoteControlClientReceiverComponent = new ComponentName(this, BluetoothReceiver.class);
        //注册MediaButton
        audioManager.registerMediaButtonEventReceiver(mRemoteControlClientReceiverComponent);
//        if (debugTag){
//            CrashHandler crashHandler = CrashHandler.getInstance();
//            crashHandler.init(getApplicationContext());
//        }
        OkHttpRequest.newInstance(this);
        GsonUtils.newInstance();
        SpUtils.getInstance(this);
        SDKInitializer.initialize(this);
        String processName = EcoApplication.getProcessName(this,
                android.os.Process.myPid());
        //        //初始化集团登录SDK
//            LetvLoginSdkManager.initSDK(this, platName, false, true, true, true, false, true);
//        new LetvLoginSdkManager().initThirdLogin(QQ_APP_ID, QQ_APP_KEY, SINA_APP_KEY, REDIRECT_URL, WX_APP_ID, WX_APP_SECRET);
//        new LetvLoginSdkManager().showPersonInfo(true);

        // SDK初始化，需要传入一个常驻的context 一个平台标识 一个开关变量（用于控制log日志是否输出） 一个开关变量（用于控制手机注册后登录成功是否显示）
        // 一个开关变量（用于控制帐号密码登录，短信验证码登录，第三方登录成功是否显示） 平台标识找西蒙去协调分配
        //一个开关变量（用于控制个人信息页面是否有退出登录功能）    一个开关变量（用于控制是否添加第三方登录功能）
        //一个开关变量（用于控制是否由SDK内部封装方法来调起个人信息页面，默认为true）
        LetvLoginSdkManager.initSDK(this, platName, true, true, true, true, true, true);
        //new LetvLoginSdkManager().initThirdLoginSwitch(true, true, true, true, true, true, true);
        // 需要传入第三方登录需要的一些参数（从QQ开发平台上申请到的APP ID和APP KEY，从新浪开发平台上申请到的App
        // Key和应用的回调页，从微信开发平台上申请到的APP ID和AppSecret）
        new LetvLoginSdkManager().initThirdLogin(QQ_APP_ID, QQ_APP_KEY, SINA_APP_KEY, REDIRECT_URL,
                WX_APP_ID, WX_APP_SECRET, SuperId_appID, SuperId_appsecret, SuperId_appSignToken, GOOGLE_SERVER_CLIENT_ID);
        new LetvLoginSdkManager().initThirdLoginSwitch(false, true, true, true, false, false, false);
        // 注册乐视账户成功时自动登录后是否显示个人信息界面（true:显示 false：不显示）
        new LetvLoginSdkManager().showPersonInfo(true);

//        Log.e("==进程名称==", "进程名称="+processName);
        if (processName != null) {
            boolean defaultProcess = processName
                    .equals("com.letv.leauto.ecolink");
            if (defaultProcess) {
                Trace.Debug("==进程名称==", "进程名称=" + processName);
                //必要的初始化资源操作
                /*CrashReport.initCrashReport(getApplicationContext(), "b83f950ebe", false);*/
                instance = this;
                LeGlob = new LeGlob(this.getApplicationContext());
                //埋点
                LetvReportUtils.init(this);
                initcmf();
                //初始化语音助手
                initLeVoiceEngines();
                new Thread(){
                    @Override
                    public void run() {
                        initVoice();
                    }
                }.start();
            }

            /** 录屏进程crash同时上传到服务端 */
            if (processName.endsWith(":screen_record")) {
                Log.i("EcoApplication","onCreate processName:" + processName);
                CrashReport.initCrashReport(getApplicationContext(), "900025153", false);
            }
        }
        initInterface();
        //爱车初始化
        FavorLibraryApp.setParameters(QQ_APP_ID,QQ_APP_KEY,SINA_APP_KEY,REDIRECT_URL,WX_APP_ID,WX_APP_SECRET);
        FavorLibraryApp.onCreate(this);
        initIOVClound();

        //腾讯X5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  null);

        //Bugly热更新初始化,提供的升级dialog的自定义UI布局
        Beta.upgradeDialogLayoutId = R.layout.upgrade_dialog;
        //设置Wifi下自动下载
        Beta.autoDownloadOnWifi = true;
        //第三个参数true表示打开debug模式，false表示关闭调试模式
        Bugly.init(this,"b83f950ebe",false);
        ContentResolver cr = getApplicationContext().getContentResolver();
        try {
            int value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
            ThinCarIAOACallback.DEFAULT_BRIGHT_VALUE=value;
        } catch (Settings.SettingNotFoundException e) {

        }
//        initializeRepository();

    }

    /**
     * 初始化乐车云sdk
     */
    private void initIOVClound(){
//        IOVCloudRepository.Builder builder = IOVCloudRepository.newBuilder(this)
//                .baseUrl("http://id-bj.ffauto.us");
//        IOVCloudRepository.getInstance().initializeRepository(this, builder, new IOVCloudRepository.OnInitializeRepositoryListener() {
//            @Override
//            public void onInitializeRepositoryCompleted(FFResult result) {
//                if (result.getError() == FFError.NONE) {
//                    Trace.Debug("IOVCloud","Initialized IOV Cloud Repository successfully.");
//                }else{
//                    Trace.Debug("IOVCloud", result.getErrorDescription());
//                }
//            }
//        });
    }

    private void initInterface() {
        ContextProvider.initIfNotInited(getApplicationContext());
        HttpGlobalConfig.init(GlobalHttpPathConfig.BASE_URL,
                GlobalHttpPathConfig.BASE_URL, "http://dc.letv.com/",
                null,
                null, true,
                true, false);

    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }


    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getAdCode() {
        return adCode;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Beta.installTinker();
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    private void sendCmfServerBroadvcast(boolean cdeReady, boolean linkShellReady) {
        Intent intent = new Intent();
        intent.setAction(LePlayerCommon.CMF_SERVER_CONNECTION_ACTION);   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
        intent.putExtra(LePlayerCommon.EXTRA_CMF_CDE_READY, cdeReady);
        intent.putExtra(LePlayerCommon.EXTRA_CMF_LINKSHELL_READY, linkShellReady);
        sendBroadcast(intent);      //发送广播
    }

    private void sendCmfServerDisConnectedBroadvcast(int errorCode) {
        Intent intent = new Intent();
        intent.setAction(LePlayerCommon.CMF_SERVER_DISCONNECTION_ACTION);   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
        intent.putExtra(LePlayerCommon.EXTRA_CMF_ERROR_CODE, errorCode);
        sendBroadcast(intent);      //发送广播
    }

    private void initcmf() {
        boolean isMainProcess = this.getPackageName().equals(DeviceUtils.getProcessName(this, android.os.Process.myPid()));
        if (isMainProcess) {
            Logger.e("init", "init params" + GeneralUtils.getInitPlayerParams(this));
            CmfHelper.init(this, GeneralUtils.getInitPlayerParams(this));
            // 若只是使用播放器, 不使用LinkShell和CDE, 则不需要后面的操作
            final CmfHelper cmfHelper = CmfHelper.getInstance();
            if(!debugTag){
//                Logger.setOutputToLogcat(false);
                Logger.setLogLevel(0);
            }
            cmfHelper.setOnStartStatusChangeListener(new CmfHelper.OnStartStatusChangeListener() {
                @Override
                public void onLinkShellStartComplete(int statusCode) {
                    //boolean linkShellReady = cmfHelper.linkShellReady();
                }

                @Override
                public void onCdeStartComplete(int i) {
                    boolean linkShellReady = cmfHelper.linkShellReady();
                    boolean cdeReady = cmfHelper.cdeReady();
                    sendCmfServerBroadvcast(cdeReady, linkShellReady);
                }

                @Override
                public void onMediaServiceDisconnected() {
                    Logger.w("", "onServerDisconnected. service disconnected. error code(%s)", 0);
                    sendCmfServerDisConnectedBroadvcast(0);
                }
            });
            cmfHelper.start();
        }
    }

    public static synchronized EcoApplication getInstance() {
        if (null == instance) {
            instance = new EcoApplication();
        }
        return instance;
    }


    public static SQLiteDatabase getModeDb(int mode) {
        return instance.getDb();
    }

    private synchronized SQLiteDatabase getDb() {
        PermissionCheckerUtils permissionChecker = new PermissionCheckerUtils(this.getApplicationContext());
        if (globalDb == null) {
            if(permissionChecker.isStorageWriteGranted() && permissionChecker.isStorageReadGranted()) {
                globalDb = LeDBHelper.getInstance(
                        this.getApplicationContext()).getWritableDatabase();
            }else{
                permissionChecker.showDialog(this.getApplicationContext());
            }
        }
        return globalDb;
    }

//    public synchronized static void WriteLog(String text) {
//        try {
//            logList.add(System.currentTimeMillis() + ": " + text);
//            if (logList.size() < 20) {
//                return;
//            }
//            FileOutputStream fout = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DCIM/leting.txt", true);
//            for (int i = 0; i < logList.size(); i++) {
//                byte[] bytes = (logList.get(i) + "\r\n").getBytes("utf-8");
//                fout.write(bytes);
//            }
//            logList.clear();
//            fout.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public int getShowScreen() {
        return ShowScreen;
    }

    public void setShowScreen(int showScreen) {
        ShowScreen = showScreen;
    }


    public void setCurrentLoaction(AMapLocation currentLoaction) {
        this.currentLoaction = currentLoaction;
    }

    public AMapLocation getCurrentLoaction() {
        return currentLoaction;
    }


    private void initVoice() {
        LeVoiceManager.getInstance().init(this);
        LeVoiceCallManger.getInstance().setmCallListener(new LeVoiceCallManger.CallListener() {
            @Override
            public boolean acceptIncoming() {
                return false;
            }

            @Override
            public int getStatus() {
                return CALL_STATUS_IDLE;
            }

            @Override
            public boolean hangupCall() {
                return false;
            }

            @Override
            public boolean call(final Contact con) {
//                ToastUtil.show(EcoApplication.this,"拨打电话 : " + con.getNumber());
                Trace.Debug("######拨打电话 : " + con.getNumber());
//                Looper.prepare();
//                ToastUtil.show(EcoApplication.this,"拨打电话 : " + con.getNumber());
//              Looper.prepare();
                LetvReportUtils.reportVoiceSearch(con.getKeyWord(),"电话");
                LetvReportUtils.reportJumpAppEvent("true");
                EventBusHelper.post(new CallEvent(con.getNumber()));
                return false;
            }

            @Override
            public boolean call(final String number) {
                Trace.Debug("#####拨打电话 : " + number);
                LetvReportUtils.reportVoiceSearch(number,"电话");
                LetvReportUtils.reportJumpAppEvent("true");
//                Looper.prepare();
//                ToastUtil.show(EcoApplication.this,"拨打电话 : " + number);
//                Looper.loop();

                EventBusHelper.post(new CallEvent(number));
                return false;
            }

            @Override
            public boolean rejectIncoming() {
                return false;
            }
        });

        LeVoiceNavManager.getInstance().setmNavListener(new LeVoiceNavManager.NavListener() {
            @Override
            public void openNav() {
                EventBusHelper.post(new NavEvent(null, NavEvent.OPEN_NAV));
            }

            @Override
            public void startNav() {
                
            }

            @Override
            public void exitNav() {
                EventBusHelper.post(new NavEvent(null, NavEvent.EXIT_NAV));
            }

            @Override
            public boolean isInNav() {
                return MapCfg.mNaAciFragmentIsNaVi;
            }

            @Override
            public boolean isMapOpen() {
                return MapCfg.mapfragmentOpen||MapCfg.routFragmentOpen||MapCfg.naviFragmentOpen;

            }

            @Override
            public void navToAddress(final SearchPoi poi) {
                LetvReportUtils.reportVoiceSearch(poi.getKeyWord(),"地图");
                LetvReportUtils.reportJumpAppEvent("true");
                EventBusHelper.post(new NavEvent(poi, NavEvent.GO_TO_NAV));
            }

            @Override
            public boolean strategy(final int strategy) {
                EventBusHelper.post(new NavEvent(strategy, NavEvent.NEW_PATH));
                return false;
            }

            @Override
            public String getHomeAddress() {
                return  CacheUtils.getInstance(EcoApplication.this).getString(Constant.SpConstant.HOME_ADDR, null);
            }

            @Override
            public void goHome() {

                EventBusHelper.post(new NavEvent(null, NavEvent.GO_HOME));
            }

            @Override
            public void setHomeAddress(SearchPoi poi) {

                String address = poi.getAddrname() + "," + poi.getLatitude() + "," + poi.getLongitude();
                CacheUtils.getInstance(EcoApplication.this).putString(Constant.SpConstant.HOME_ADDR, address);
            }

            @Override
            public String getWorkAddress() {
                return  CacheUtils.getInstance(EcoApplication.this).getString(Constant.SpConstant.WORK_ADDR, null);
            }

            @Override
            public void goWork() {

                EventBusHelper.post(new NavEvent(null, NavEvent.GO_WORK));
            }

            @Override
            public void setWorkAddress(SearchPoi poi) {
//                com.letv.voicehelp.utils.Trace.Debug("设置公司的地址");
                String address = poi.getAddrname() + "," + poi.getLatitude() + "," + poi.getLongitude();
                CacheUtils.getInstance(EcoApplication.this).putString(Constant.SpConstant.WORK_ADDR, address);
            }

            @Override
            public void previewWholeCourse() {
                Trace.Debug("navi","预览全程");
                EventBusHelper.post(new NavEvent(null, NavEvent.PREVIEW));

            }

            @Override
            public void switchNavSound(boolean open) {
                Trace.Debug("navi","switchNavSound="+open);
                if (open) {
                    EventBusHelper.post(new NavEvent(null, NavEvent.OPEN_SOUND));
                }else {
                    EventBusHelper.post(new NavEvent(null, NavEvent.CLOSE_SOUND));
                }
            }
//            head_forward 车头朝上  ,north_forward 正北朝上
            @Override
            public void switchMapOrientation(String s) {
                if (s!=null){
                    if (NavEvent.CAR_UP_STRING.equals(s)){
                        EventBusHelper.post(new NavEvent(null, NavEvent.CAR_UP));
                    }else if (NavEvent.NORTH_UP_STRING.equals(s)){
                        EventBusHelper.post(new NavEvent(null, NavEvent.NORTH_UP));
                    }
                    Trace.Debug("navi","map oriention="+s);
                }

            }

            @Override
            public String getLeftTime() {
                return  CacheUtils.getInstance(EcoApplication.this).getString(GlobalCfg.LEFT_TIME, null);
            }

            @Override
            public String getLeftDistance() {
                return  CacheUtils.getInstance(EcoApplication.this).getString(GlobalCfg.LEFT_DISTANCE, null);
            }
            @Override
            public void mapZoomIn(){
                EventBusHelper.post(new NavEvent(null, NavEvent.ZOOM_IN));
            }

            @Override
            public void mapZoomOut(){
                EventBusHelper.post(new NavEvent(null, NavEvent.ZOOM_OUT));
            }
        });

        LeVoiceMusicManager.getInstance().setmMusicListener(new LeVoiceMusicManager.MusicListener() {
            @Override
            public void exit() {
                EventBusHelper.post(new MusicEvent(MusicEvent.EXIT_MUSIC));
            }

            @Override
            public boolean isPlaying() {
                return (LeGlob.getPlayer()!=null&&LeGlob.getPlayer().getCurrentStatus()!=null&&LeGlob.getPlayer().getCurrentStatus().currentItem!=null&&LeGlob.getPlayer().getCurrentStatus().isPlaying);
            }

            @Override
            public void next() {
                EventBusHelper.post(new MusicEvent(MusicEvent.PLAY_NEXT));
            }

            @Override
            public void pre() {
                EventBusHelper.post(new MusicEvent(MusicEvent.PLAY_PRE));
            }

            @Override
            public void pause() {
                EventBusHelper.post(new MusicEvent(MusicEvent.PAUSE));
            }

            @Override
            public void play() {
                EventBusHelper.post(new MusicEvent(MusicEvent.START));
                Trace.Debug("*******");
            }

            @Override
            public void playMusic(MediaDetail mediaDetail) {
                Trace.Debug("*******");

            }

            @Override
            public void playMusicByString(String respone) {
                Trace.Debug("*******");
                LetvReportUtils.reportVoiceSearch(respone,"电台");
                LetvReportUtils.reportJumpAppEvent("true");
                com.letv.voicehelp.utils.Trace.Debug("最新的音乐搜索结果 : " + respone);
                EventBusHelper.post(new MusicEvent(MusicEvent.PLAY_MUSIC_STR, respone));
            }

            @Override
            public void playMusic(final ArrayList<MediaDetail> mediaDetails) {
                EventBusHelper.post(new MusicEvent(MusicEvent.PLAY_LIST_MUSIC, mediaDetails));
            }

            @Override
            public void playLocalMusic() {
                Trace.Debug("*******");
                EventBusHelper.post(new MusicEvent(MusicEvent.PLAY_LOCAL));

            }

            @Override
            public void playRandom() {
                Trace.Debug("*******");
                EventBusHelper.post(new MusicEvent(MusicEvent.PLAY_MODE,3));
            }

            @Override
            public void playModel(int model) {
                Trace.Debug("*******");
                switch (model){
                    case 0://单曲

                        EventBusHelper.post(new MusicEvent(MusicEvent.PLAY_MODE,2));
                        break;
                    case 1://随机

                        EventBusHelper.post(new MusicEvent(MusicEvent.PLAY_MODE,3));
                        break;
                    case 2://

                        EventBusHelper.post(new MusicEvent(MusicEvent.PLAY_MODE,1));
                        break;
                }


            }

            /**
             * 打开音乐
             */
            @Override
            public void openMusic() {
                EventBusHelper.post(new MusicEvent(MusicEvent.OPEN_MUSIC));
            }

//            @Override
//            public void playAfterPause() {
//
//            }


        });

        LeVoiceCommandManager.getInstance().setCommonCMDListener(new LeVoiceCommandManager.CommonCMDListener() {
            @Override
            public void home() {
                EventBusHelper.post(new HomeEvent());

            }
        });
    }

    private void initLeVoiceEngines() {
        HttpHelper.init(true, this);
        LeVoiceEngineManager.setInitListener(new InitListener() {
            @Override
            public void onServiceConnected(IServer iServer) {
                Trace.Debug("####### 语音助手初始化成功 ");
//                Toast.makeText(getApplicationContext(), "语音助手初始化成功", Toast.LENGTH_LONG).show();
                LeVoiceEngineUtils.setMixModeTTS(Config.TTS_MIX_HIGH_SPEED_SYNTHESIZE);
                Config.appId = Config.FROM_HULIAN;
                AgenciesManager.getInstance().init();
            }

            @Override
            public void onServiceDisconnected() {
                Trace.Debug("####### 语音助手初始化失败 ");

            }

            @Override
            public void onUnbindService() {
                Trace.Debug("####### 语音助手解绑 ");

            }

            @Override
            public void onServiceConnectedFailed(String msg) {

            }
        });

        BaiduInitParams baiduInitParams = new BaiduInitParams.BaiduInitParamsBuilder()
                .useTts(true)//使用百度的TTS
                .useStt(true)//使用百度STT
                .useWakeUp(false)//不使用百度WakeUp
                .ttsAppId("9090495")//在百度开放平台上申请的本应用的TTS 的 AppId，注意：如果不填写，那么会用默认的，但是离线TTS就会出问题，请务必填写自己应用的相关信息
                .ttsApiKey("UITY1UDogTKriVkE8GHKsIPj")//在百度开放平台上申请的本应用的TTS 的 ApiKey
                .ttsSecretKey("9ade287c0351bf1890f334e24bdba501")//在百度开放平台上申请的本应用的TTS 的 SecretKey
                .build();
        LeVoiceEngineManager.bindService(getApplicationContext(), baiduInitParams);
    }

    public Handler getMhandler() {
        return mhandler;
    }



//    public void initializeRepository(){
//
//
//        IOVCloudRepository.Builder builder = IOVCloudRepository.newBuilder(this)
//                .baseUrl("http://mq-bj.ffauto.us:9000");
//        IOVCloudRepository.getInstance().initializeRepository(this, builder, new IOVCloudRepository.OnInitializeRepositoryListener() {
//            @Override
//            public void onInitializeRepositoryCompleted(FFResult ffResult) {
//                FFUtils.logging(IOVCloudRepository.getInstance().getVersion());
//                if (ffResult.getError() == FFError.NONE) {
//                    FFUtils.logging("initializeRepository: Successfully, isUserLoggedIn():" + IOVCloudRepository.getInstance().isUserLoggedIn());
//                    if(IOVCloudRepository.getInstance().isUserLoggedIn()){
//                        FFUtils.logging("You are logged in as user:" + IOVCloudRepository.getInstance().getCurrentUser().getUsername());
//                    }
//                }else if(ffResult.getError() == FFError.IOVCLOUD_SERVICE_CURRENTLY_NOT_AVAILABLE){
//                    FFUtils.logging("initializeRepository:" + ffResult.getUserFriendlyDescription());
//                }
//                FFUtils.logging("SDKStatus: " + IOVCloudRepository.getInstance().getSdkStatus());
//
//            }
//        });
//    }


}
