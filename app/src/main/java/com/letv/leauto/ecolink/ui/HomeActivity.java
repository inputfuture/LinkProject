package com.letv.leauto.ecolink.ui;

import static com.letv.leauto.ecolink.ui.fragment.RoutePlanFragment.LAUNCH_FRAGMENT;

import android.app.ActivityManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.navi.AMapNavi;
import com.amap.api.services.route.RouteSearch;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.MsgHeader;
import com.leauto.link.lightcar.ScreenRecordActivity;
import com.leauto.link.lightcar.ScreenRecorderManager;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.pcm.PcmDataManager;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.leauto.link.lightcar.service.ReceiveDataService;
import com.leauto.sdk.SdkManager;
import com.leautolink.multivoiceengins.engine.Config;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.SeconPageAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.EnvStatus;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.cfg.VoiceCfg;
import com.letv.leauto.ecolink.controller.EcoTTSController;
import com.letv.leauto.ecolink.database.field.SortIDConfig;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.Contact;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.event.CallEvent;
import com.letv.leauto.ecolink.event.CloseVoiceEvent;
import com.letv.leauto.ecolink.event.HomeEvent;
import com.letv.leauto.ecolink.event.MusicEvent;
import com.letv.leauto.ecolink.event.MusicVoiceEvent;
import com.letv.leauto.ecolink.event.NavEvent;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.lemap.PoiHistoryManager;
import com.letv.leauto.ecolink.lemap.entity.SearchPoi;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.leplayer.common.LePlayerCommon;
import com.letv.leauto.ecolink.leplayer.model.LTStatus;
import com.letv.leauto.ecolink.manager.WeatherIconManager;
import com.letv.leauto.ecolink.qplay.QPlayMainFragment;
import com.letv.leauto.ecolink.receiver.NetChangeBroadCaster;
import com.letv.leauto.ecolink.receiver.ScreenRotationUtil;
import com.letv.leauto.ecolink.thincar.LeThincarInfoInterface;
import com.letv.leauto.ecolink.thincar.PhoneInfoMonitor;
import com.letv.leauto.ecolink.thincar.ThinCarIAOACallback;
import com.letv.leauto.ecolink.thincar.module.ThincarQuickSearchEvent;
import com.letv.leauto.ecolink.thincar.ota.DownType;
import com.letv.leauto.ecolink.thincar.ota.OtaEntity;
import com.letv.leauto.ecolink.thincar.ota.OtaMessageDialog;
import com.letv.leauto.ecolink.thincar.ota.OtaUtils;
import com.letv.leauto.ecolink.thincar.ota.ThinCarDBImpl;
import com.letv.leauto.ecolink.thincar.protocol.DeviceInfoNotifyHelp;
import com.letv.leauto.ecolink.thincar.protocol.LeRadioSendHelp;
import com.letv.leauto.ecolink.thincar.protocol.NaviBarSendHelp;
import com.letv.leauto.ecolink.thincar.protocol.ThirdAppMsgHelp;
import com.letv.leauto.ecolink.thincar.protocol.VoiceAssistantHelp;
import com.letv.leauto.ecolink.ui.LocalMusicFragment.LocalMusicFragment;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.ui.fragment.CallFragment;
import com.letv.leauto.ecolink.ui.fragment.EasyStopFragment;
import com.letv.leauto.ecolink.ui.fragment.KeySearchFragment;
import com.letv.leauto.ecolink.ui.fragment.LeMusicFragment;
import com.letv.leauto.ecolink.ui.fragment.LeRadioAlumFragment;
import com.letv.leauto.ecolink.ui.fragment.MainFragment;
import com.letv.leauto.ecolink.ui.fragment.MapFragment;
import com.letv.leauto.ecolink.ui.fragment.NaviFragment;
import com.letv.leauto.ecolink.ui.fragment.RoutePlanFragment;
import com.letv.leauto.ecolink.ui.fragment.SettingFragment;
import com.letv.leauto.ecolink.ui.page.HomeCommonPage;
import com.letv.leauto.ecolink.ui.view.DeleteDataDialog;
import com.letv.leauto.ecolink.ui.view.EcoDialog;
import com.letv.leauto.ecolink.ui.view.StateTitleActivity;
import com.letv.leauto.ecolink.update.UpgradeAbility;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.ContextProvider;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.EcoActivityManager;
import com.letv.leauto.ecolink.utils.HashUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.NetworkUtil;
import com.letv.leauto.ecolink.utils.NoNetDialog;
import com.letv.leauto.ecolink.utils.PackageUtil;
import com.letv.leauto.ecolink.utils.PermissionCheckerUtils;
import com.letv.leauto.ecolink.utils.TelephonyUtil;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.leauto.favorcar.FavorLibraryApp;
import com.letv.leauto.favorcar.contract.InitYContract;
import com.letv.leauto.favorcar.exInterface.LocationInfo;
import com.letv.loginsdk.constant.LoginConstant;
import com.letv.voicehelp.LeVoicePopupWindow;
import com.letv.voicehelp.eventbus.EventBusHelper;
import com.letv.voicehelp.manger.nav.LocationManager;
import com.letv.voicehelp.utils.LeVoiceEngineUtils;
import com.letvcloud.cmf.CmfHelper;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class HomeActivity extends StateTitleActivity implements DeleteDataDialog.DeleteDataInterface, NetChangeBroadCaster.netEventHandler {

    public static final int VOICE_RESULT_CODE = 0x901;
    private static final int START = 0X92;
    private static final int STOP = 0X93;

    public static boolean isPopupWindowShow = false;

    @Bind(R.id.main_frame)
    public FrameLayout frameLayout_home;

    @Bind(R.id.rl_main)
    public RelativeLayout mRlMain;
    @Bind(R.id.kuwo)
    public FrameLayout kuwo;
    @Bind(R.id.music_play)
    public FrameLayout music_play;
    @Bind(R.id.call_frame)
    public FrameLayout frameLayout_phone;
    @Bind(R.id.local_music_frame)
    public FrameLayout local_music_frame;
    @Bind(R.id.map_frame)
    public FrameLayout frameLayout_map;
    @Bind(R.id.music_frame)
    public FrameLayout frameLayout_music;
    @Bind(R.id.setting_frame)
    public FrameLayout frameLayout_set;
    @Bind(R.id.easy_stop_frame)
    public FrameLayout frameLayout_easy_stop;
    @Bind(R.id.qplay_container)
    FrameLayout frameLayout_q_play;
    @Bind(R.id.iv_voice)
    public ImageButton iv_voice;
    @Bind(R.id.iv_map)
    public ImageButton iv_map;
    @Bind(R.id.iv_music)
    public ImageButton iv_music;
    @Bind(R.id.iv_home)
    public ImageButton iv_home;
    @Bind(R.id.iv_phone_book)
    public ImageButton iv_phone_book;
    @Bind(R.id.ll_title)
    LinearLayout m_ll_title;
    @Bind(R.id.rl_bottombar)
    LinearLayout mBottomBar;
    @Bind(R.id.debug_memory_tv)
    TextView mDebugMemoryText;
    @Bind(R.id.debug_current_cpu_tv)
    TextView mDebugCurrentCPUText;

    public FragmentManager fragmentManager;
    private MainFragment mainFragment;
    private CallFragment callFragment;
    private MapFragment mapFragment;
    private EasyStopFragment mEasyStopFragment;
    private LeRadioAlumFragment leRadioAlumFragment;
    private LocalMusicFragment localMusicFragment;
    private SettingFragment settingFragment;
    private LeMusicFragment musicFragment;
    private QPlayMainFragment qPlayMainFragment;
    private LeAlbumInfo leAlbumInfo;
    private ArrayList<Contact> contactList;
    private ArrayList<Contact> conList;
    public boolean isNavigating = false;
    public boolean isInMapFragment = false;
    public boolean isInEasyStop = false;


    MyBroadcastReceiver mBroadcastReceiver;
    private boolean mIsCalling; //当前是否处于通话中
    private boolean mNeedRememberLastNavi = true;

    public String lastVisibilyLayout = "";
    public static String mLastMusicLayout = "";
    public  int albumSelectPosition = 0;

    private ScreenRecorderManager recoderManager;
    public static boolean isThinCar;
    public static boolean isThinCarMain = false;//确定map图片是否在主页面
    public static boolean isNotifyCar = true;
    public static boolean isVoiceActivty = false;

    private DataSendManager mDataSendManager;
    private LeVoicePopupWindow popupWindow;
    //地图是否半屏显示的tag，半屏的时候不显示dialog
    public boolean mMapIsHalf = false;
    private LePlayer lePlayer;

    private int mPid;

    private LeThincarInfoInterface mLeThincarInfoInterface;

    private ReceiveDataService mReceiveDataService;

    Handler mUpdateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            updatePhoneInfo();
        }
    };


    OtaMessageDialog otaMessageDialog;

    private boolean isDriving = false; // 车辆正在行驶中

    public boolean isDriving() {
        return isDriving;
    }

    public void setDriving(boolean driving) {
        isDriving = driving;
    }

    @Override
    public void delete() {
        unInitLeAuto();
        CmfHelper.getInstance().stop(true);
        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_ONGOING, 0);
        if (lePlayer != null && lePlayer.getCurrentStatus() != null) {
            LTStatus currentStatus = lePlayer.getCurrentStatus();
            if (currentStatus.currentItem != null) {
                MediaOperation mediaOperation = MediaOperation.getInstance();
                currentStatus.currentItem.setProgress(lePlayer.getCurrentStatus().progress);
                mediaOperation.insertLTItem(currentStatus.currentItem);

            }
        }

       // DataSendManager.getInstance().notifyRecordExit();
        EcoActivityManager.create().AppExit();
//        System.exit(0);
    }

    // Return current index to Adapter
    public int getCurrentPagerIdx() {
        return albumSelectPosition;
    }

    @Override
    public void onNetChange() {
        ContextProvider.init(this);
        if (NetworkUtil.getNetworkType() == NetworkUtil.NETWORK_TYPE_NONE) {
            GlobalCfg.hasNet = false;

            mDataSendManager.sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_NO_NETWORK);
            try {
                if(!mMapIsHalf){
                    NoNetDialog.show(mContext);
                }
            } catch (Exception e) {
                System.out.println("dialog show faild!");
            }
        } else {
            GlobalCfg.hasNet = true;
            DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_HAS_NETWORK);
        }
    }

    public void startNaviForThinCar(String naviType) {
        if (mapFragment != null) {
            mapFragment.startNaviFromThincar(naviType);
        }
    }

    public void stopNaviForThinCar() {
        NaviFragment fragment = NaviFragment.getThis();
        if (fragment != null) {
            fragment.stopNaviFromThincar();
        }
    }

    public void requestQickSearch(String target) {
        Bundle nBundle = new Bundle();
        nBundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_THINCAR);
        nBundle.putString(KeySearchFragment.TCHINCAR_SEARCH_KEY, target);
        nBundle.putBoolean(KeySearchFragment.IS_THINCAR_QUICEK_SEARCH,true);
        KeySearchFragment secondFragment = KeySearchFragment.getInstance(nBundle);
        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment).commitAllowingStateLoss();
    }

    public static class MyBroadcastReceiver extends BroadcastReceiver {
        WeakReference<HomeActivity> ref;


        public MyBroadcastReceiver(HomeActivity homeActivity) {
            this.ref = new  WeakReference<HomeActivity>(homeActivity);
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LePlayerCommon.BROADCAST_ACTION_VOICERECORD)) {
                int msgCode = intent.getIntExtra(LePlayerCommon.BROADCAST_EXTRA_VOICERECORD, 0);
                switch (msgCode) {
                    case LePlayerCommon.BROADCAST_EXTRA_VOICERECORD_BEGIN:
                        //语音搜索
                        if (ref==null){
                            return;
                        }
                        HomeActivity homeActivity=ref.get();
                        if (homeActivity!=null)
                        homeActivity.startVoiceSearch();
                        break;

                    case LePlayerCommon.BROADCAST_EXTRA_VOICERECORD_END:
                        break;
                }
            }
        }
    }

    public void startVoiceSearch() {
        if(!new PermissionCheckerUtils(mContext).isAudioGranted()){
            ToastUtil.show(mContext, "麦克风权限未打开");
            return;
        }

        if (NetUtils.isConnected(mContext)) {
            mIsCalling = TelephonyUtil.getInstance(mContext).isTelephonyCalling();
            if (mIsCalling) {
                ToastUtil.showShort(mContext, mContext.getString(R.string.main_calling_not_support_voice));
                return;
            }

//            LetvReportUtils.reportJumpAppEvent();
//            if (!NaviTTSController.getInstance(mContext).isSpeechFinished()){
//                Toast.makeText(mContext,"您正在导航中！",Toast.LENGTH_SHORT).show();
//                return;
//            }
            //关闭TTS
            if (!isThinCar) {
                LeVoiceEngineUtils.stopTTS();
            }
            //关闭音乐
            if (lePlayer != null && lePlayer.getCurrentStatus() != null){
                shouldPlayContinue = lePlayer.getCurrentStatus().isPlaying;
            }
            Trace.Debug("####shouldPlayContinue  " + shouldPlayContinue);
            Trace.Debug("#####stop");
            FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
            NaviFragment fragment = (NaviFragment) manager.findFragmentByTag("NaviFragment");
//            if (fragment != null) {
//                fragment.stopSpeakNavi();
//            }
//            reportJumpAppEvent(mContext.getClass().getName());

//            Intent intent;
//            if (GlobalCfg.IS_POTRAIT) {
//                intent = new Intent(mContext, VoiceActivity.class);
//            } else {
//                intent = new Intent(mContext, VoiceActivity1.class);
//            }
//            startActivityForResult(intent, VOICE_RESULT_CODE);
            if (popupWindow == null) {
//                popupWindow = new VoicePopupWindow(HomeActivity.this, VoiceTTSController.getInstance(this));
                popupWindow = new LeVoicePopupWindow(HomeActivity.this);
                popUpWindowHideSystemNaviBar();
            }
            isPopupWindowShow = true;
            LetvReportUtils.recordActivityStart("VoiceActivity");
//
//            QPlayer qPlayer=EcoApplication.LeGlob.getqPlayer();
//            qPlayer.pause();

            View view = findViewById(R.id.rl_main);
            LeVoicePopupWindow.MyDissmissListener listener = new LeVoicePopupWindow.MyDissmissListener() {

                @Override
                public void destory() {
                    resetPlayerStateAfterVoiceDismiss();
                }
            };


            if (isThinCar) {
                popupWindow.setmRecognitionListener(VoiceAssistantHelp.getInstance().getVoiceRecognitionListener());
            } else {
                popupWindow.setmRecognitionListener(null);
            }
            if (!popupWindow.isShowing()) {
                popupWindow.show(view, listener);
            }
        } else {
            NetUtils.showNoNetDialog(mContext);
        }
    }

    public void PopWindowDismiss() {

        if (popupWindow != null) {

            isPopupWindowShow = false;
            popupWindow.dismiss();
            if (lePlayer != null && lePlayer.getCurrentStatus() != null && lePlayer.getCurrentStatus().currentItem != null && !BaseActivity.isVoice && !BaseActivity.isStoped) {
                Trace.Debug("####start");
                LetvReportUtils.recordActivityEnd("VoiceActivity");
            }
        }
    }
    //    public static String sHA1(Context context) {
//        try {
//            PackageInfo info = context.getPackageManager().getPackageInfo(
//                    context.getPackageName(), PackageManager.GET_SIGNATURES);
//            byte[] cert = info.signatures[0].toByteArray();
//            MessageDigest md = MessageDigest.getInstance("SHA1");
//            byte[] publicKey = md.digest(cert);
//            StringBuffer hexString = new StringBuffer();
//            for (int i = 0; i < publicKey.length; i++) {
//                String appendString = Integer.toHexString(0xFF & publicKey[i])
//                        .toUpperCase(Locale.US);
//                if (appendString.length() == 1)
//                    hexString.append("0");
//                hexString.append(appendString);
//                hexString.append(":");
//            }
//            String result = hexString.toString();
//            return result.substring(0, result.length()-1);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.i("HomeActivity", "onCreate ");
        mDataSendManager =  DataSendManager.getInstance();
        mDataSendManager.initDataSendManager(this.getApplicationContext());
        Intent intent = this.getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals(ScreenRecordActivity.AOA_START_ACTIVITY_ACTION)){
                isAoaRecordSuccess = true;
            }
        }

        initThincarDataSend();
        setLePlayer();
        super.onCreate(savedInstanceState);


        NaviBarSendHelp.getInstance().initNaviBar(mContext);
        EventBusHelper.register(this);

        MobclickAgent.openActivityDurationTrack(false);
        //设置窗口为透明
        if (!EcoApplication.mIsRestart) {
            UpgradeAbility upgradeAbility = new UpgradeAbility(mContext);
            upgradeAbility.checkUpgrade(false, false);


//            checkOtaUpdate();
        }
        //初始化天气icon
        WeatherIconManager.getInstance().init();

        //数据埋点
        LetvReportUtils.recordAppStart();
        if (!NetUtils.isConnected(mContext)) {
            showNoNetDialog();
            GlobalCfg.hasNet = false;
        }

        processExtraData();
        if (NetUtils.isConnected(mContext) && mNeedRememberLastNavi) {
            showNaviDialog();
        }
        NetChangeBroadCaster.mListeners.add(this);

        setOnSystemUiVisibilityChangeListener();

        mPid = getCurrentPid(this);

        mLeThincarInfoInterface = new LeThincarInfoInterface();
        FavorLibraryApp.setCarInfo(mLeThincarInfoInterface);
    }
    long startTime;
     boolean first=true;
    private void checkOtaUpdate() {
        first=true;

        if (!isThinCar){

        ThinCarDBImpl db = ThinCarDBImpl.getInstance(mContext);
        List<OtaEntity> _otaentity = db.getOtaEntityFromDB();
        if (-_otaentity.size()==0){
            return;
        }
        final OtaEntity entity = _otaentity.get(0);
        File file = new File(entity.getFilePath(), entity.getFileName());
        if (file.exists()&&file.length()==entity.getPkgSize()){

        }else {
            startTime =System.currentTimeMillis();
            otaMessageDialog=new OtaMessageDialog(mContext, R.style.Dialog,0);
            otaMessageDialog.setUpdateClickListener(new OtaMessageDialog.UpdateClickListener() {
                @Override
                public void onClick() {
                    if (otaMessageDialog!=null){
                        otaMessageDialog.setlayoutByType(1);}
                    startTime=System.currentTimeMillis();

                    OkHttpUtils.get().url(entity.getDownUrl()).build().execute(new FileCallBack(entity.getFilePath(), entity.getFileName()) {
                        @Override
                        public void inProgress(float progress, long total , int id) {


                            long endTime=System.currentTimeMillis();
                            if ((endTime-startTime)>1000){
                                int finalProgress= (int) (100*progress);
                                if (otaMessageDialog!=null&&otaMessageDialog.isShowing()){
                                    otaMessageDialog.setProgress(finalProgress);
                                }
                                startTime=endTime;
                                Trace.Debug("thincar"," inprogress="+finalProgress);
                                entity.setDownStatus(DownType.DOWN_DOWNING);
                                entity.setProgress(finalProgress);
                                ThinCarDBImpl db = ThinCarDBImpl.getInstance(mContext);
                                db.updataOtaEntity(entity);
                                if(HomeActivity.isThinCar){
                                    OtaUtils.sendOtaUpdata(entity, mContext,first);
                                    first=false;
                                }


                            }
                        }

                        @Override
                        public void onError(Call call, Exception e, int code) {

                        }

                        @Override
                        public void onResponse(File response,int code) {
                            LogUtils.i("TAG", "File: " + response.toString());
                            if (otaMessageDialog!=null&&otaMessageDialog.isShowing()){

                            otaMessageDialog.setlayoutByType(2);}
                            try {
                                String md5 = HashUtils.getMd5ByFile(response);
                                if (md5.equals(entity.getMd5())) {
                                    entity.setDownStatus(DownType.DOWN_SUCCESS);
                                    entity.setProgress(100);
                                    ThinCarDBImpl db = ThinCarDBImpl.getInstance(mContext);
                                    db.updataOtaEntity(entity);
                                }
                                if(HomeActivity.isThinCar){
                                    OtaUtils.sendOtaUpdata(entity, mContext,first);
                                    first=false;
                                }
                                LogUtils.i("TAG", "下载数据库内容: " + entity.toString());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            otaMessageDialog.show();
            otaMessageDialog.setOtaEntity(entity);

        }}


    }

    private void setLePlayer() {
        if(EcoApplication.LeGlob!=null) {
            lePlayer = EcoApplication.LeGlob.getPlayer();
            lePlayer.openServiceIfNeed();
//            qPlayer=EcoApplication.LeGlob.getqPlayer();
//            qPlayer.openServiceIfNeed();
        }
    }

    private void initThinCar() {
        Trace.Debug("HomeActivity", "initThinCar");
        VoiceAssistantHelp.getInstance().initVoiceAssistant(this);

        mThinCarIAOACallback = new ThinCarIAOACallback(this);
        /** 表示从aoa过来且连接好了 */
       String action = this.getIntent().getAction();
        LogUtils.i("HomeActivity", "initThinCar action:" + action);
        if (action != null) {
            if (action.equals(ScreenRecordActivity.AOA_START_ACTIVITY_ACTION)) {
                if (EcoApplication.mIsRestart) {
                    changeToNavi();
                }
                mThinCarIAOACallback.onAoaConnectStateChange(ThinCarDefine.ConnectState.STATE_CONNECT);
            } else if (action.equals(ThinCarIAOACallback.ADB_RESTART_ACTIVITY_ACTION)) {
                if (EcoApplication.mIsRestart) {
                    changeToNavi();
                }
                mThinCarIAOACallback.onAdbConnectStateChange(ThinCarDefine.ConnectState.STATE_CONNECT);
            }
        }

        EcoApplication.mIsRestart = false;
       this.getIntent().setAction(Intent.ACTION_MAIN);

        Intent intent = new Intent(this, ReceiveDataService.class);
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                LogUtils.e("HomeActivity","initThinCar onServiceConnected");
                mReceiveDataService =  ((ReceiveDataService.ReceiveDataBinder) iBinder).getService();
                mReceiveDataService.setIAOACallback(mThinCarIAOACallback);
                /** 初始化车机SDK */
                initLeAuto();
                DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_READY_REC_EVENT_PARAM);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        this.bindService(intent,conn, Service.BIND_AUTO_CREATE);
    }

    private LeAutoLinkListner mLeAutoLinkListner;
    private LeAudioRecordListener mLeAudioRecordListener;

    /**
     * 初始化车机SDK竖屏
     */
    public void initLeAuto() {
        LogUtils.e("HomeActivity","initLeAuto");
        mLeAutoLinkListner = new LeAutoLinkListner(this, mHandler, mThinCarIAOACallback);
        mLeAudioRecordListener = new LeAudioRecordListener(this);
        //mOnAudioRecordListener = new OnAudioRecordListener();
        SdkManager.getInstance(this).initSdk(mLeAutoLinkListner);
        SdkManager.getInstance(this).setKeyboardRemoteListener(mLeAutoLinkListner);
        SdkManager.getInstance(this).setCarAudioRecordListener(mLeAudioRecordListener);
    }

    /**
     * 程序退出时需要释放车机SDK资源
     */
    public void unInitLeAuto() {
        SdkManager.getInstance(this).unInitSdk();
    }




    public void showTitleBar(boolean show) {
        if (show) {
            m_ll_title.setVisibility(View.VISIBLE);
        } else {
            m_ll_title.setVisibility(View.GONE);
        }
    }

    public void showLogo() {
        if (mainFragment != null) {
            mainFragment.showLogo();
        }
        FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
        LeMusicFragment fragment = (LeMusicFragment) manager.findFragmentByTag("LeMusicFragment");
        if (fragment != null) {
            fragment.setDongfenBar();
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    /**
     * 处理widget传过来的参数
     */
    private void processExtraData() {
        Intent intent = getIntent();
        String type = intent.getStringExtra(GlobalCfg.WIDGETYEPE);
        if (type != null) {
            if (type.equals(GlobalCfg.FAVOR)) {
                widgetToFavor();
            } else if (type.equals(GlobalCfg.LOCAL)) {
                widgetToLocal();

            } else if (type.equals(GlobalCfg.HOME)) {
                widgetToHome();

            } else if (type.equals(GlobalCfg.COMPANY)) {
                widgetToLComPany();

            } else if (type.equals(GlobalCfg.MUSIC)) {
                widgetToMusic();
            }
        }

    }

    private void widgetToMusic() {
        CacheUtils cacheUtil = CacheUtils.getInstance(mContext);
        String lastAlbum = cacheUtil.getString(Constant.Radio.LAST_ALBUM, null);
        if(lastAlbum!=null) {
            changeToPlayMusic();
        }else{
            addMusicFragment();
        }
    }

    private void widgetToLComPany() {
        Trace.Debug("##### widgetToLComPany");
        widgetToNavi(false);
    }

    private void widgetToHome() {

        widgetToNavi(true);

    }

    private void widgetToLocal() {
        Trace.Debug("##### widgetToLocal");
        setNavibar();
        naviBackground();
        mLastMusicLayout = Constant.TAG_LOCAL;
        CacheUtils.getInstance(this).putString(SettingCfg.LastPostion, Constant.TAG_LOCAL);
        lastVisibilyLayout = Constant.TAG_LOCAL;
        local_music_frame.setVisibility(View.VISIBLE);
        iv_music.setImageResource(R.mipmap.button_radio_sel);

        if (null == localMusicFragment) {
            localMusicFragment = new LocalMusicFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.local_music_frame, localMusicFragment).commitAllowingStateLoss();
        } else {
            localMusicFragment.onResume();
        }

    }

    private void widgetToNavi(boolean isHome) {


        if (!NetUtils.isConnected(mContext)) {
            ToastUtil.show(mContext, R.string.net_no);
            return;
        }

        Trace.Debug("#### change to navi");
        naviForeground();
        setNavibar();


        lastVisibilyLayout = Constant.TAG_MAP;
        frameLayout_map.setVisibility(View.VISIBLE);
        iv_map.setImageResource(R.mipmap.button_map_sel);
        if (getFragmentByTag(NaviFragment.class.getSimpleName()) != null) {
            showTitleBar(false);

        }
        String endDress;
        if (isHome) {
            endDress = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HOME_ADDR, null);
        } else {
            endDress = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.WORK_ADDR, null);
        }
        if (endDress == null) {

            Bundle nBundle = new Bundle();
            nBundle.putBoolean(MapFragment.MAPMODE, true);
            nBundle.putBoolean(MapFragment.IS_HOME_ADDRESS, isHome);
            nBundle.putBoolean(MapFragment.IS_WIDGET,true);


            ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().
                    replace(R.id.map_frame, MapFragment.getInstance(nBundle),MapFragment.class.getSimpleName()).commitAllowingStateLoss();
        } else {
            Bundle nBundle = new Bundle();
            nBundle.putBoolean(MapFragment.IS_HOME_ADDRESS, isHome);
            nBundle.putBoolean(MapFragment.IS_WIDGET, true);
            ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().
                    replace(R.id.map_frame, MapFragment.getInstance(nBundle),MapFragment.class.getSimpleName()).commitAllowingStateLoss();

        }

    }

    private void widgetToFavor() {
        Trace.Debug("##### widgetToFavor");
        setNavibar();
        naviBackground();
        mLastMusicLayout = Constant.TAG_LERADIO;
        CacheUtils.getInstance(this).putString(SettingCfg.LastPostion, Constant.TAG_LERADIO);
        lastVisibilyLayout = Constant.TAG_LERADIO;
        frameLayout_music.setVisibility(View.VISIBLE);
        iv_music.setImageResource(R.mipmap.button_radio_sel);
        if (leRadioAlumFragment == null) {
            leRadioAlumFragment = new LeRadioAlumFragment();
            fragmentManager.beginTransaction().replace(R.id.music_frame, leRadioAlumFragment, "LeRadioAlumFragment").commitAllowingStateLoss();
        }
        leRadioAlumFragment.widgetToFavor();
    }

    public void hideLogo() {
        if (mainFragment != null) {
            mainFragment.hideLogo();
        }
        FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
        LeMusicFragment fragment = (LeMusicFragment) manager.findFragmentByTag("LeMusicFragment");
        if (fragment != null) {
            fragment.setDongfenBar();

        }

    }


    @Override
    protected void initView() {
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        iv_map.setOnClickListener(this);
        iv_music.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        iv_phone_book.setOnClickListener(this);
        iv_voice.setOnClickListener(this);


        // lePlayer.setIconStateListener(this);
        if (lePlayer != null) {
            lePlayer.openServiceIfNeed();
            lePlayer.setActivity(this);
        }

        //是否需要显示Logo
        showLogo();
        mainFragment = new MainFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame, mainFragment, Constant.FRG_MAIN).commitAllowingStateLoss();
        changeToHome();
//        checkStatement();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }


    @Override
    public void onClick(View v) {
        //更新界面
        switch (v.getId()) {
            case R.id.iv_home:
                //点击主页按钮时,重新加载数据
                LetvReportUtils.reportClick("a91f5e5c");
                showTitleBar(true);
                changeToHome();
            {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //设置动画时间
                scaleAnimation.setDuration(500);
                iv_home.startAnimation(scaleAnimation);
            }


            break;
            case R.id.iv_phone_book: {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //设置动画时间
                scaleAnimation.setDuration(500);
                iv_phone_book.startAnimation(scaleAnimation);
            }
            changeToPhone();
            LetvReportUtils.reportClick("bdd1ab46");

            break;
            case R.id.iv_map: {
                LetvReportUtils.reportClick("af137bb9");
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //设置动画时间
                scaleAnimation.setDuration(500);
                iv_map.startAnimation(scaleAnimation);
            }
            if (isNavigating && isInEasyStop) {

                showExitNaviDialog(true);
            } else {

                changeToNavi();
            }


            break;
            case R.id.iv_music: {
                LetvReportUtils.reportClick("014fa18e");
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //设置动画时间
                scaleAnimation.setDuration(500);
                iv_music.startAnimation(scaleAnimation);
                if (!iv_music.getDrawable().getConstantState().equals(getResources().getDrawable(R.mipmap.button_radio_sel).getConstantState())) {
                    addMusicFragment();
                }
            }


            break;
            case R.id.iv_voice: {
                LetvReportUtils.reportClick("7c4a5268");
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //设置动画时间
                scaleAnimation.setDuration(500);
                iv_voice.startAnimation(scaleAnimation);
            }
            //语音搜索
            startVoiceSearch();
            break;
            default:
                break;
        }
    }
    //给大数据埋点添加,20160511,石孟,begin
//    public static void reportJumpAppEvent(String name) {
//        LetvReportUtils.reportJumpAppEvent(name);
//    }

    //给大数据埋点添加,20160511,石孟,end
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case VOICE_RESULT_CODE:
                String domin = data.getStringExtra(VoiceCfg.RESULT_DOMAIN);
                switch (domin) {
                    case VoiceCfg.DOMAIN_MUSIC:
                        mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.NOTIFY_FROM_PHONE_PARAM, ThinCarDefine.ProtocolToCarAction.LAUNCH_LE_RADION_ACTION, 0);
                        changeToPlayMusic();
                        break;
                    case VoiceCfg.DOMAIN_MAP:
                        mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.NOTIFY_FROM_PHONE_PARAM, ThinCarDefine.ProtocolToCarAction.LAUNCH_NAVI_ACTION, 0);//通知车机到导航界面
                        if (isNavigating) {
                            /*final Intent data1 = data;
                            EcoDialog dialog = new EcoDialog(mContext, R.style.Dialog, "确定退出现在的导航?");
                            dialog.setListener(new EcoDialog.ICallDialogCallBack() {
                                @Override
                                public void onConfirmClick(EcoDialog currentDialog) {
                                    ((HomeActivity) mContext).isNavigating = false;
                                    currentDialog.dismiss();
                                    RoutePlanFragment.removeSelf();
                                    NaviFragment.removeSelf();
                                    ChangeButton(7);
                                    Bundle nBundle = new Bundle();
                                    nBundle.putString(RoutePlanFragment.ROUTEPLAN_START_ADDRESS, data1.getStringExtra(RoutePlanFragment.ROUTEPLAN_START_ADDRESS));
                                    nBundle.putString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS, data1.getStringExtra(RoutePlanFragment.ROUTEPLAN_END_ADDRESS));
                                    RoutePlanFragment secondFragment = RoutePlanFragment.getInstance(nBundle);
                                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment).commitAllowingStateLoss();
                                    isInMapFragment = false;
                                    isInEasyStop = false;
                                }

                                @Override
                                public void onCancelClick(EcoDialog currentDialog) {
                                    currentDialog.dismiss();
                                }

                            });
                            dialog.show();*/
                            ((HomeActivity) mContext).isNavigating = false;

                            changeToNaviAfterVoice();
                            Bundle nBundle = new Bundle();
                            nBundle.putString(RoutePlanFragment.ROUTEPLAN_START_ADDRESS, data.getStringExtra(RoutePlanFragment.ROUTEPLAN_START_ADDRESS));
                            nBundle.putString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS, data.getStringExtra(RoutePlanFragment.ROUTEPLAN_END_ADDRESS));
                            AMapNavi.getInstance(mContext).destroy();
                            RoutePlanFragment secondFragment = RoutePlanFragment.getInstance(nBundle);
                            ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment,RoutePlanFragment.class.getSimpleName()).commitAllowingStateLoss();
                            isInMapFragment = false;
                            isInEasyStop = false;
                        } else {
                            changeToNaviAfterVoice();
                            Bundle nBundle = new Bundle();
                            AMapNavi.getInstance(mContext).destroy();
                            nBundle.putString(RoutePlanFragment.ROUTEPLAN_START_ADDRESS, data.getStringExtra(RoutePlanFragment.ROUTEPLAN_START_ADDRESS));
                            nBundle.putString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS, data.getStringExtra(RoutePlanFragment.ROUTEPLAN_END_ADDRESS));
                            RoutePlanFragment secondFragment = RoutePlanFragment.getInstance(nBundle);
                            ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment,RoutePlanFragment.class.getSimpleName()).commitAllowingStateLoss();
                            isInMapFragment = false;
                            isInEasyStop = false;
                        }

                        break;
                }
                break;
            case LoginConstant.LOGOUTFROMPERSONINFO:
                if (settingFragment != null) {
                    settingFragment.onLogOut();
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showExitNaviDialog(final boolean map) {
        EcoDialog dialog = new EcoDialog(mContext, R.style.Dialog, "确定退出现在的导航?");
        dialog.setListener(new EcoDialog.ICallDialogCallBack() {
            @Override
            public void onConfirmClick(EcoDialog currentDialog) {
                ((HomeActivity) mContext).isNavigating = false;
                currentDialog.dismiss();
                Fragment fragment=getFragmentById(R.id.map_frame);
                if (fragment!=null){
                    fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
                if (map) {
                    changeToNavi();
                } else {
                    changeToEasy();
                }
            }

            @Override
            public void onCancelClick(EcoDialog currentDialog) {
                currentDialog.dismiss();
            }

        });
        dialog.show();
    }




    public static Boolean shouldPlayContinue = true;

    /**
     * 语音返回之后是否需要继续播放
     */


    /**
     * 根据意图打开地图相关的页面,地点或者附近的银行等
     *
     * @param intention
     * @param keyword
     */
    private void openMapByIntention(String intention, String keyword) {
        changeToNavi();
        Bundle bundle = new Bundle();
        mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.NOTIFY_FROM_PHONE_PARAM, ThinCarDefine.ProtocolToCarAction.LAUNCH_NAVI_ACTION, 0);//通知车机到导航界面
        bundle.putString(KeySearchFragment.VOICE_SEARCH_KEY, keyword);
        switch (intention) {
            case "route":
            case "poi":
                bundle.putBoolean(KeySearchFragment.SEARCH_TYPE_NEARBY, false);
                break;
            case "nearby":
                bundle.putBoolean(KeySearchFragment.SEARCH_TYPE_NEARBY, true);
                break;
        }
        bundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_VOICE);
        KeySearchFragment fragment = KeySearchFragment.getInstance(bundle);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.map_frame, fragment, "KeySearchFragment").commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("HomeActivity", "onResume mIsRestart:" + EcoApplication.mIsRestart);
        MobclickAgent.onResume(this);

        Intent intent = this.getIntent();
        turnEcolinkDirection(intent);

        //连上车机条件下，app在前台自动隐藏虚拟按键
        if(isThinCar){
            hideSystemNavigationBar();
        }

        LetvReportUtils.recordActivityStart(this.getClass().getSimpleName());
        resetScreenOpenState();
        mBroadcastReceiver = new MyBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LePlayerCommon.BROADCAST_ACTION_VOICERECORD);
        intentFilter.addAction(LePlayerCommon.BROADCAST_ACTION_TTS);
        registerReceiver(mBroadcastReceiver, intentFilter);
        MapCfg.mNaAciFragmentIsBackground = false;
        if (MapCfg.mNaAciFragmentIsNaVi) {
            //暂停计时
            MapCfg.mToTalTime += (System.currentTimeMillis() - MapCfg.mStartTime);

        }
        if (getFragmentByTag(NaviFragment.class.getSimpleName())== null && getFragmentByTag(RoutePlanFragment.class.getSimpleName()) == null) {
            showTitleBar(true);
        }
    }

    private void resetScreenOpenState() {
        if (CacheUtils.getInstance(EcoApplication.getInstance()).getBoolean(SettingCfg.SCREEN_LIGHT_OPEN, true)) {
            //常亮
            openLight();
        } else {
            //关闭常亮
            closeLight();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Trace.Debug("  ####### HomeActivity  pause");
        LetvReportUtils.recordActivityEnd(this.getClass().getSimpleName());

        try {
            if (mBroadcastReceiver != null) {
                unregisterReceiver(mBroadcastReceiver);
                mBroadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MobclickAgent.onPause(this);
        MapCfg.mNaAciFragmentIsBackground = true;
        MobclickAgent.onPause(this);
        if (MapCfg.mNaAciFragmentIsNaVi) {
            //开始计时
            MapCfg.mStartTime = System.currentTimeMillis();
        }
        setNaviTTLWarning();
        if (popupWindow != null) {
            popupWindow.myDismiss(false);
        }
        sendWidgetBroadvcast();
    }
    private void sendWidgetBroadvcast() {
        Intent intent = new Intent();
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
        int[] wigetIds = {0x01};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, wigetIds);
        sendBroadcast(intent);      //发送广播
        Trace.Debug("  ####### sendWidgetBroadvcast");
    }
    private void setNaviTTLWarning() {
        if (getFragmentByTag(NaviFragment.class.getSimpleName()) != null) {
            if (!DeviceUtils.isApplicationBroughtToFront(mContext)) {
                if (BaseActivity.isVoice)
                    return;
                if (!TelephonyUtil.getInstance(mContext).isTelephonyCalling()) {
                    EcoTTSController.getInstance(mContext).stop();
                    EcoTTSController.getInstance(mContext).speak("乐视车联持续为您导航");
                }
            }
        }
    }

    @Override
    protected void onStop() {
        LogUtils.i("HomeActivity", "onStop");
        super.onStop();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        showDefaultPage();
    }

    @Override
    protected void onDestroy() {
        if (!EcoApplication.mIsRestart) {
            releaseWhenDestroyed();
        }
        super.onDestroy();
        EventBusHelper.unregister(this);

        LogUtils.i("HomeActivity", "onDestroy mIsRestart:" + EcoApplication.mIsRestart);
      if (EcoApplication.mIsRestart){
          Intent launch = this.getPackageManager().getLaunchIntentForPackage(this
                            .getPackageName());
            launch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
          if (EcoApplication.isAoaRestart) {
              launch.setAction(ScreenRecordActivity.AOA_START_ACTIVITY_ACTION);
          } else if (EcoApplication.isAdbRestart) {
              launch.setAction(ThinCarIAOACallback.ADB_RESTART_ACTIVITY_ACTION);
          }
            startActivity(launch);
        }

        GlobalCfg.isChooseAppState = false;
        EcoApplication.isAoaRestart = false;
        EcoApplication.isAdbRestart = false;
        GlobalCfg.isVoiceDebugOpen = false;
        GlobalCfg.isMemoryDebugOpen = false;
    }

    private void releaseWhenDestroyed() {

        //unbindService(this);
        //取消常亮
        closeLight();
        if (mThinCarIAOACallback != null) {
            mThinCarIAOACallback = null;
        }

        if (mDataSendManager != null && !EcoApplication.mIsRestarting) {
            /** 连接调整横屏转竖屏走这，不发应用退出消息，进程被杀死，发消息 */
            mDataSendManager.sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_EXIT);
        }

        EcoApplication.mIsRestarting = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeEvent(HomeEvent homeEvent){
        LetvReportUtils.reportClick("a91f5e5c");
        changeToHome();


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMusicEvent(MusicEvent event) {
        switch (event.getType()) {
            case MusicEvent.EXIT_MUSIC:
                exitMusic();
                break;
            case MusicEvent.OPEN_MUSIC:
                openMusic();
                mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                        ThinCarDefine.PageIndexDefine.LERADIO_PAGE,0);
                setCurrentPageIndex(ThinCarDefine.PageIndexDefine.LERADIO_PAGE);
                break;
            case MusicEvent.PLAY_LIST_MUSIC:
                palyMusicList(event.getMediaDetails());
                mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                        ThinCarDefine.PageIndexDefine.LERADIO_PAGE,0);
                setCurrentPageIndex(ThinCarDefine.PageIndexDefine.LERADIO_PAGE);
                break;
            case MusicEvent.PLAY_MUSIC_STR:
                palyMusicByVoice(event.getMediaJson());
                mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                        ThinCarDefine.PageIndexDefine.LERADIO_PAGE,0);
                setCurrentPageIndex(ThinCarDefine.PageIndexDefine.LERADIO_PAGE);
                break;
            case MusicEvent.PAUSE:
                lePlayer.stopPlayByUser();
                break;
            case MusicEvent.START:
                isStoped=false;
                lePlayer.startPlay();
                break;
            case MusicEvent.PLAY_NEXT:
                openNext(true);
                break;
            case MusicEvent.PLAY_PRE:
                openNext(false);
                break;
            case MusicEvent.PLAY_MODE:
                CacheUtils.getInstance(mContext).putInt(SettingCfg.PALY_MODE, event.getPlayMode());
                lePlayer.setPlayMode(event.getPlayMode());
                if (musicFragment!=null){
                    musicFragment.setMode(event.getPlayMode());

                }
                playRadomMusic();
                //lePlayer.playPrev();
                break;
            case MusicEvent.PLAY_LOCAL:
                Trace.Debug("##### widgetToLocal");
                setNavibar();
                naviBackground();
                mLastMusicLayout = Constant.TAG_LOCAL;
                CacheUtils.getInstance(this).putString(SettingCfg.LastPostion, Constant.TAG_LOCAL);
                lastVisibilyLayout = Constant.TAG_LOCAL;
                local_music_frame.setVisibility(View.VISIBLE);
                iv_music.setImageResource(R.mipmap.button_radio_sel);

                if (null == localMusicFragment) {
                    localMusicFragment = new LocalMusicFragment();
                    localMusicFragment.setAutoPlay(true);
                    fragmentManager.beginTransaction()
                            .replace(R.id.local_music_frame, localMusicFragment).commitAllowingStateLoss();

                } else {
                    localMusicFragment.onResume();
                    localMusicFragment.autoPlay();
                }

                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMusicVoiceEvent(MusicVoiceEvent event) {
        voice2palyMusic(event.getMediaDetails());
    }

    private void exitMusic() {
        shouldPlayContinue = false;
        BaseActivity.isStoped = true;
        lePlayer.stopPlay();
        removeMusicPlayPage();
        if (isThinCar) {
            changeToNavi();
        } else {
            changeToHome();
        }
        ttsSpeakWithSpeaked("已为您关闭音乐");
    }

    private void openMusic() {
        Trace.Debug("#####tts onLinstenSong ");
//        addMusicFragment();
        isStoped=false;
        if (lePlayer.getPlayerList() != null && lePlayer.getPlayerList().size() > 0) {
            popupWindow.myDismiss(false);
            Trace.Debug("#####tts lePlayer.getPlayerList() ");
            lePlayer.startPlay();
            changeToPlayMusic();

        } else {

            CacheUtils cacheUtil = CacheUtils.getInstance(mContext);
            String lastAlbum = cacheUtil.getString(Constant.Radio.LAST_ALBUM, null);
            Trace.Debug("#####tts lastAlbum= "+lastAlbum);
            if(lastAlbum!=null) {
                changeToPlayMusic();
            }else{
                addMusicFragment();
            }

            ttsSpeakWithSpeaked("已为您打开音乐");
        }
    }

    private void playRadomMusic() {
        Trace.Debug("#####tts onLinstenSong ");
//        addMusicFragment();
        isStoped=false;
        if (lePlayer.getPlayerList() != null && lePlayer.getPlayerList().size() > 0) {
            popupWindow.myDismiss(false);
            lePlayer.startPlay();
            changeToPlayMusic();
        } else {

            CacheUtils cacheUtil = CacheUtils.getInstance(mContext);
            String lastAlbum = cacheUtil.getString(Constant.Radio.LAST_ALBUM, null);
            if(lastAlbum!=null) {
                changeToPlayMusic();
            }else{
                addMusicFragment();
            }

            ttsSpeakWithSpeaked("已为您打开音乐界面");
        }
    }

    private void openNext(boolean next){

//        addMusicFragment();
        if (lePlayer.getPlayerList() != null && lePlayer.getPlayerList().size() > 0) {
            popupWindow.myDismiss(false);
            if (next){
                lePlayer.playNext(false);
            }else {
                lePlayer.playPrev();
            }
        } else {

            CacheUtils cacheUtil = CacheUtils.getInstance(mContext);
            String lastAlbum = cacheUtil.getString(Constant.Radio.LAST_ALBUM, null);
            if(lastAlbum!=null) {
                changeToPlayMusic();
            }else{
                addMusicFragment();
            }

            ttsSpeakWithSpeaked("已为您打开音乐");
        }
    }



    //播放语音搜索到的曲目接口
    private void voice2palyMusic(ArrayList<MediaDetail> mediaList) {
        if (mediaList != null) {
            GlobalCfg.MUSIC_TYPE = Constant.TAG_LERADIO;
            lePlayer.TYPE = 1;
            lePlayer.setPlayerList(mediaList);
            LeAlbumInfo leAlbumInfo = new LeAlbumInfo();
            leAlbumInfo.TYPE = SortType.SORT_VOICE;
            leAlbumInfo.NAME = mediaList.get(0).NAME;
            lePlayer.setAlbumInfo(leAlbumInfo);
            LeRadioSendHelp.getInstance().setLocalRadioAlbum(leAlbumInfo);
            EnvStatus.Sort_Id = SortIDConfig.VOICE_RECOGNIZE;
            lePlayer.playList(0);
            changeToPlayMusic();
        }
    }

    private void palyMusicList(ArrayList<com.letv.voicehelp.model.MediaDetail> mediaDetails) {
        ArrayList<MediaDetail> mDs = new ArrayList<>();
        for (int i = 0; i < mediaDetails.size(); i++) {
            MediaDetail md = new MediaDetail();
            com.letv.voicehelp.model.MediaDetail helpMd = mediaDetails.get(i);
            md.IMG_URL = helpMd.IMG_URL;
            md.SOURCE_CP_ID = helpMd.SOURCE_CP_ID;
            md.SOURCE_URL = helpMd.SOURCE_URL;
            md.NAME = helpMd.NAME;
            md.AUDIO_ID = helpMd.AUDIO_ID;
            md.ALBUM_ID = helpMd.ALBUM_ID;

            md.START_TIME = helpMd.START_TIME;
            md.END_TIME = helpMd.END_TIME;

            md.AUTHOR = helpMd.AUTHOR;
            md.CREATE_TIME = helpMd.CREATE_TIME;
            md.LE_SOURCE_MID = helpMd.LE_SOURCE_MID;
            md.LE_SOURCE_VID = helpMd.LE_SOURCE_VID;
            mDs.add(md);
        }
        if (mediaDetails != null) {
            GlobalCfg.MUSIC_TYPE = Constant.TAG_LERADIO;
            lePlayer.TYPE = 1;
            lePlayer.setPlayerList(mDs);
            LeAlbumInfo leAlbumInfo = new LeAlbumInfo();
            leAlbumInfo.ALBUM_ID = mediaDetails.get(0).ALBUM_ID;
            leAlbumInfo.TYPE = SortType.SORT_VOICE;
            leAlbumInfo.NAME = mediaDetails.get(0).getKeyWord();
            lePlayer.setAlbumInfo(leAlbumInfo);
            EnvStatus.Sort_Id = SortIDConfig.VOICE_RECOGNIZE;
            lePlayer.playList(0);
            changeToPlayMusic();
        }
    }

    public void palyMusicByVoice(String serchJson) {
        Trace.Debug("#####tts serchJson: " + serchJson);
        DataUtil.getInstance().getPlayList(serchJson);
    }

    public void palyAbumsMusicByVoice(String serchJson) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(CallEvent event) {
//        if (android.os.Build.MODEL.contains("vivo") || android.os.Build.MODEL.contains("coolpad")) {
//            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + event.getNumber()));
//            mContext.startActivity(intent);
//        } else {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + event.getNumber()));
        mContext.startActivity(intent);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNavEvent(NavEvent event) {
        switch (event.getType()) {
            case NavEvent.OPEN_NAV:
                ttsSpeakWithSpeaked("导航已开启");
                changeToNavi();
                if (isThinCar) {
                    /** 发消息给车机通知进行全屏导航 */
                    mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                            ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE,0);
                    setCurrentPageIndex(ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE);
                }
                break;
            case NavEvent.EXIT_NAV:
                if (isThinCar) {
                    NaviFragment fragment = NaviFragment.getThis();
                    if (fragment != null) {
                        fragment.stopNaviFromThincarVoice();
                    }
                    /** 发消息给车机通知进行全屏导航 */
                    mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                            ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE,0);
                    setCurrentPageIndex(ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE);
                } else {
                    exitNav();
//                    ttsSpeakWithSpeaked("已为您退出导航");
                }
                break;
            case NavEvent.GO_TO_NAV:
                Trace.Debug("我要去导航~~~~~~~~~~~~");
                PoiHistoryManager.getInstance(mContext).saveSearchPoi(new SearchPoi(event.getPoi().getLatitude()+"",event.getPoi().getLongitude()+"",event.getPoi().getAddrname()));
                String enAddr = event.getPoi().getAddrname() + "," + event.getPoi().getLatitude() + "," + event.getPoi().getLongitude();
                startNav(enAddr, RouteSearch.DrivingSaveMoneyAvoidCongestion);
                break;
            case NavEvent.NEW_PATH:
                NaviFragment navi = (NaviFragment) getSupportFragmentManager().findFragmentByTag("NaviFragment");

                if (navi != null) {
                    navi.reCalculateRoute(event.getStrategy());
//                    String endAddress = navi.getEndAddress();
//                    startNav(endAddress, event.getStrategy());
                    if (popupWindow.isShowing()) {
                        popupWindow.myDismiss(false);
                    }
                } else {
                    ttsSpeak("你还没有进行导航");
                }
                break;
            case NavEvent.GO_HOME:
                goHome();
                break;
            case NavEvent.GO_WORK:
                goWork();
                break;
            case NavEvent.CLOSE_SOUND:
                setNaviVoice(false);
                break;
            case NavEvent.OPEN_SOUND:
                setNaviVoice(true);
                break;
            case NavEvent.PREVIEW:
                setNaviPreview();
                break;
            case NavEvent.CAR_UP:
                setNaviOritentionMode(false);
                break;
            case NavEvent.NORTH_UP:
                setNaviOritentionMode(true);

                break;
            case NavEvent.ZOOM_IN:
                setNaviZoomOut(false);
                break;
            case NavEvent.ZOOM_OUT:
                setNaviZoomOut(true);
                break;
        }
    }

    private void setNaviZoomOut(boolean b) {
        NaviFragment naviFragment= (NaviFragment) getFragmentByTag(NaviFragment.class.getSimpleName());
        MapFragment mapFragment= (MapFragment) getFragmentByTag(MapFragment.class.getSimpleName());
        RoutePlanFragment routePlanFragment= (RoutePlanFragment) getFragmentByTag(RoutePlanFragment.class.getSimpleName());
        if (null==naviFragment&&null==mapFragment&&null==routePlanFragment){
            return;
        }
        naviForeground();
        setNavibar();
        lastVisibilyLayout = Constant.TAG_MAP;
        frameLayout_map.setVisibility(View.VISIBLE);
        iv_map.setImageResource(R.mipmap.button_map_sel);
        if (naviFragment!=null){
            naviFragment.setZoomOut(b);}
        if (mapFragment!=null){
            mapFragment.setZoomOut(b);
        }
        if (routePlanFragment!=null){
            routePlanFragment.setZoomOut(b);
        }
    }

    private void setNaviOritentionMode(boolean northUp) {
        NaviFragment naviFragment= (NaviFragment) getFragmentByTag(NaviFragment.class.getSimpleName());
        if (null==naviFragment){
            return;
        }
        naviForeground();
        setNavibar();
        lastVisibilyLayout = Constant.TAG_MAP;
        frameLayout_map.setVisibility(View.VISIBLE);
        iv_map.setImageResource(R.mipmap.button_map_sel);
        naviFragment.setOritentionMode(northUp);
    }

    private void setNaviPreview() {
        NaviFragment naviFragment= (NaviFragment) getFragmentByTag(NaviFragment.class.getSimpleName());
        if (null==naviFragment){
            return;
        }
        naviForeground();
        setNavibar();
        lastVisibilyLayout = Constant.TAG_MAP;
        frameLayout_map.setVisibility(View.VISIBLE);
        iv_map.setImageResource(R.mipmap.button_map_sel);
        naviFragment.startPreview();

    }

    private void setNaviVoice(boolean open) {
        if (open){
            CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_SPEAKER, 1);
        }else {
            CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_SPEAKER, 0);
            EcoTTSController.getInstance(mContext).stop();
        }
//        NaviFragment naviFragment= (NaviFragment) getFragmentByTag(NaviFragment.class.getSimpleName());
//        if (null==naviFragment){
//            return;
//        }
//        naviForeground();
//        setNavibar();
//        lastVisibilyLayout = Constant.TAG_MAP;
//        frameLayout_map.setVisibility(View.VISIBLE);
//        iv_map.setImageResource(R.mipmap.button_map_sel);
//        naviFragment.setSoundOpend(open);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeVoicePopupWindow(CloseVoiceEvent event) {
        if (popupWindow != null) {
            popupWindow.myDismiss(false);
        }
    }

    private void goWork() {
        String homeAddress = CacheUtils.getInstance(this).getString(Constant.SpConstant.WORK_ADDR, "");
        if (!TextUtils.isEmpty(homeAddress)) {
            startNav(homeAddress, RouteSearch.DrivingSaveMoneyAvoidCongestion);
        }
    }

    private void goHome() {
        String homeAddress = CacheUtils.getInstance(this).getString(Constant.SpConstant.HOME_ADDR, "");
        if (!TextUtils.isEmpty(homeAddress)) {
            startNav(homeAddress, RouteSearch.DrivingSaveMoneyAvoidCongestion);
        }
    }

    public void ttsSpeak(String text) {
//        VoiceTTSController.getInstance(this).speak(text);
        popupWindow.saySomethingOutText(text);
    }

    public void ttsSpeakWithSpeaked(String text) {
//        VoiceTTSController.getInstance(this).speak(text);
        popupWindow.saySomethingOutText(text/*, new VoiceTTSController.VoiceTTSOneListener("Main") {
            @Override
            public boolean onError(String id, SpeechError speechError) {
                if (popupWindow.isShowing()) {
                    popupWindow.myDismiss(false);
                }
                return true;
            }

            @Override
            public boolean onSpeechFinish(String id) {
                if (popupWindow.isShowing()) {
                    popupWindow.myDismiss(false);
                }
                return true;
            }
        }*/);
    }

    private void exitNav() {
        Fragment naviFragment= getFragmentByTag(NaviFragment.class.getSimpleName());
        if (naviFragment!=null){
            fragmentManager.beginTransaction().remove(naviFragment).commitAllowingStateLoss();}

        Fragment routeFragment=getFragmentByTag(RoutePlanFragment.class.getSimpleName());
        if (routeFragment!=null){
            fragmentManager.beginTransaction().remove(routeFragment).commitAllowingStateLoss();
        }
        changeToHome();

    }

    private void startNav(String endAddr, int drice_mode) {
        int which=CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_SELECT_KYE, 0);
        switch (which){
            case  0:
                /** 发消息给车机通知进行全屏导航 */
                mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                        ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE,0);
                setCurrentPageIndex(ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE);
                changeToNaviAfterVoice();
                AMapLocation aMapLocation = LocationManager.getInstance().getaMapLocation();
                if (aMapLocation != null) {
                    EcoApplication.isLocation = true;
                    String myAddr = aMapLocation.getAddress() + "," + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude();
                    if (endAddr != null) {
                        Bundle nBundle = new Bundle();
                        nBundle.putString(RoutePlanFragment.ROUTEPLAN_START_ADDRESS, myAddr);
                        nBundle.putString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS, endAddr);
                        AMapNavi.getInstance(mContext).destroy();
                        RoutePlanFragment secondFragment = RoutePlanFragment.getInstance(nBundle);
                        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment,RoutePlanFragment.class.getSimpleName()).commitAllowingStateLoss();
                    }
                } else {
                    ttsSpeak("定位失败");
                }
                break;
            case 1:

                startGaoDeMap(endAddr);

                break;
            case 2:

                startBaiDuMap(endAddr);
                break;
        }


    }

    @Override
    public void onBackPressed() {
//        if (!mainFragment.onBackPressed()) {
        /** 瘦车机连接状态下，忽略返回事件 */
        if (isThinCar) {
            return;
        }

        if (lastVisibilyLayout == Constant.TAG_MAIN) {
            if (HomeCommonPage.mCanDelete) {
                HomeCommonPage.mCanDelete = false;
                SeconPageAdapter.isDelete = false;
                mainFragment.refreshAllPage();
                return;
            }
            DataSendManager.getInstance().notifyRecordExit();
            showDialog();
        } else {
            if (lastVisibilyLayout == Constant.TAG_MAP && (fragmentManager.findFragmentByTag(NaviFragment.class.getSimpleName()) != null || getFragmentByTag(RoutePlanFragment.class.getSimpleName()) != null)) {
                DeleteDataDialog dialog = new DeleteDataDialog(this, "NaviFragment");
                dialog.setListener(new DeleteDataDialog.ICallDialogCallBack() {
                    @Override
                    public void onConfirmClick(DeleteDataDialog currentDialog) {
                        if (fragmentManager.findFragmentByTag(NaviFragment.class.getSimpleName())!= null) {
                            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(NaviFragment.class.getSimpleName())).commitAllowingStateLoss();
                        }

                        if (getFragmentByTag(RoutePlanFragment.class.getSimpleName()) != null) {
                            fragmentManager.beginTransaction().remove(getFragmentByTag(RoutePlanFragment.class.getSimpleName())).commitAllowingStateLoss();
                        }

                        if (mapFragment != null) {
                            fragmentManager.beginTransaction().remove(mapFragment).commitAllowingStateLoss();
                            mapFragment = null;
                        }

                        if (mEasyStopFragment != null) {
                            fragmentManager.beginTransaction().remove(mEasyStopFragment).commitAllowingStateLoss();
                            mEasyStopFragment = null;
                        }

                        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_ONGOING, 0);
                        changeToHome();
                    }

                    @Override
                    public void onCancelClick(DeleteDataDialog currentDialog) {

                    }

                });
                dialog.show();
            } else {
                changeToHome();
            }
        }

    }

    public void showDialog() {
        DeleteDataDialog deleteDataDialog = new DeleteDataDialog(this, "HomeActivity");
        deleteDataDialog.setInterface(this);
        deleteDataDialog.show();
    }

    public void openLight() {
        //常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void closeLight() {
        //关闭常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void setMapFragment(MapFragment fragment) {
        this.mapFragment = fragment;
    }

    public void setEasyStopFragment(EasyStopFragment fragment) {
        this.mEasyStopFragment = fragment;
    }


    private void setNavibar() {
        frameLayout_home.setVisibility(View.GONE);
        frameLayout_phone.setVisibility(View.GONE);
        frameLayout_map.setVisibility(View.GONE);
        frameLayout_music.setVisibility(View.GONE);
        frameLayout_set.setVisibility(View.GONE);
        frameLayout_easy_stop.setVisibility(View.GONE);
        music_play.setVisibility(View.GONE);
        local_music_frame.setVisibility(View.GONE);
        frameLayout_q_play.setVisibility(View.GONE);
        kuwo.setVisibility(View.GONE);
        iv_home.setImageResource(R.mipmap.button_home);
        iv_phone_book.setImageResource(R.mipmap.button_phone);
        iv_map.setImageResource(R.mipmap.button_map);
        iv_music.setImageResource(R.mipmap.button_radio);
        showTitleBar(true);

    }


    public void ChangeToLeradio() {
        Trace.Debug("MusicFragment", "ChangeToLeradio");
        setNavibar();
        naviBackground();
        frameLayout_music.setVisibility(View.VISIBLE);
        iv_music.setImageResource(R.mipmap.button_radio_sel);

        if (lePlayer.OPEN_LERADIO && lePlayer.leMediaDetails.size() > 0) {
            lePlayer.TYPE = 1;
            lePlayer.setAlbumInfo(lePlayer.LE_ALBUMINFO);
            ArrayList<MediaDetail> mediaDetails = new ArrayList<>();
            mediaDetails.addAll(lePlayer.leMediaDetails);
            lePlayer.setPlayerList(mediaDetails);
            if (mLastMusicLayout != Constant.TAG_LERADIO) {
                mLastMusicLayout = Constant.TAG_LERADIO;
                CacheUtils.getInstance(this).putString(SettingCfg.LastPostion, Constant.TAG_LERADIO);
                lastVisibilyLayout = Constant.TAG_LERADIO;

                if (BaseActivity.isStoped) {

                } else {
                    lePlayer.playList(lePlayer.LE_INDEX);
                }
            }

            changeToPlayMusic();
        } else {
            mLastMusicLayout = Constant.TAG_LERADIO;
            CacheUtils.getInstance(this).putString(SettingCfg.LastPostion, Constant.TAG_LERADIO);
            lastVisibilyLayout = Constant.TAG_LERADIO;
            if (leRadioAlumFragment == null) {
                leRadioAlumFragment = new LeRadioAlumFragment();
                fragmentManager.beginTransaction().replace(R.id.music_frame, leRadioAlumFragment, "LeRadioAlumFragment").commitAllowingStateLoss();
            } else {
                leRadioAlumFragment.refreshView(this);
            }
        }

    }

    public void changeToNavi() {
        Trace.Debug("#### change to navi");
        naviForeground();
        setNavibar();
        lastVisibilyLayout = Constant.TAG_MAP;
        frameLayout_map.setVisibility(View.VISIBLE);
        iv_map.setImageResource(R.mipmap.button_map_sel);
        if (!isDestroyed()&&getFragmentByTag(NaviFragment.class.getSimpleName()) != null) {
            showTitleBar(false);
            Trace.Debug("#### 1111");
        }
        Fragment fragment = fragmentManager.findFragmentById(R.id.map_frame);
        if (fragment == null) {
            if (isDestroyed()){
                return;
            }
            mapFragment = new MapFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.map_frame, mapFragment,MapFragment.class.getSimpleName()).commitAllowingStateLoss();

        }

    }

    public void changeToNaviAfterVoice() {
        setNavibar();
        naviForeground();
        if (mEasyStopFragment != null) {
            fragmentManager.beginTransaction().remove(mEasyStopFragment).commitAllowingStateLoss();
            mEasyStopFragment = null;
        }
        if (mapFragment != null) {
            fragmentManager.beginTransaction().remove(mapFragment).commitAllowingStateLoss();
            mapFragment = null;
        }
        lastVisibilyLayout = Constant.TAG_MAP;
        frameLayout_map.setVisibility(View.VISIBLE);
        iv_map.setImageResource(R.mipmap.button_map_sel);

    }

    public void changeToLocal() {
        setNavibar();
        naviBackground();
        local_music_frame.setVisibility(View.VISIBLE);
        iv_music.setImageResource(R.mipmap.button_radio_sel);
        if (lePlayer.OPEN_LOCAL && lePlayer.localMediaDetails.size() > 0) {
            lePlayer.TYPE = 3;
            lePlayer.setAlbumInfo(lePlayer.LOCAL_ALBUMINFO);

            ArrayList<MediaDetail> mediaDetails = new ArrayList<>();
            mediaDetails.addAll(lePlayer.localMediaDetails);
            lePlayer.setPlayerList(mediaDetails);
            if (mLastMusicLayout != Constant.TAG_LOCAL) {
                mLastMusicLayout = Constant.TAG_LOCAL;
                CacheUtils.getInstance(this).putString(SettingCfg.LastPostion, Constant.TAG_LOCAL);
                lastVisibilyLayout = Constant.TAG_LOCAL;
                if (BaseActivity.isStoped) {
                } else {
                    lePlayer.playList(lePlayer.LOCAL_INDEX);
                }
            }
            changeToPlayMusic();
        } else {
            mLastMusicLayout = Constant.TAG_LOCAL;
            CacheUtils.getInstance(this).putString(SettingCfg.LastPostion, Constant.TAG_LOCAL);
            lastVisibilyLayout = Constant.TAG_LOCAL;
            if (null == localMusicFragment) {
                localMusicFragment = new LocalMusicFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.local_music_frame, localMusicFragment, "LocalMusicFragment").commitAllowingStateLoss();
            } else {
                localMusicFragment.onResume();
            }
        }
    }



    public void changeToPhone() {
        setNavibar();
        naviBackground();
        lastVisibilyLayout = Constant.TAG_CALL;
        frameLayout_phone.setVisibility(View.VISIBLE);
        iv_phone_book.setImageResource(R.mipmap.button_phone_sel);
        if (null == callFragment) {
            callFragment = new CallFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.call_frame, callFragment, "CallFragment").commitAllowingStateLoss();
        }
    }

    public void changeToSetting() {
        setNavibar();
        naviBackground();
        lastVisibilyLayout = Constant.TAG_SETTING;
        frameLayout_set.setVisibility(View.VISIBLE);
        if (null == settingFragment) {
            settingFragment = new SettingFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.setting_frame, settingFragment).commitAllowingStateLoss();
        }
    }
    public void changeToFavorCar(){
        InitYContract.init(mContext, GlobalCfg.IS_POTRAIT);
        InitYContract.startFavorCar();
    }

    public void changeToPlayMusic() {
        setNavibar();
        naviBackground();
        music_play.setVisibility(View.VISIBLE);
        iv_music.setImageResource(R.mipmap.button_radio_sel);

        FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
        Bundle nBundle = new Bundle();
        Trace.Debug("#### musicFragment="+musicFragment);
        if (musicFragment == null) {
            musicFragment = LeMusicFragment.getInstance(nBundle, false);
            if(! manager.isDestroyed()) {
                manager.beginTransaction().replace(R.id.music_play, musicFragment,
                        "LeMusicFragment").commitAllowingStateLoss();
            }
        } else {
            if(! manager.isDestroyed()) {
                manager.beginTransaction().show(musicFragment).commitAllowingStateLoss();
            }
            musicFragment.refreshPages(this);
        }
    }

    public void changeToHome() {
        if (isDestroyed()){
            return;
        }
        setNavibar();
        naviBackground();
        lastVisibilyLayout = Constant.TAG_MAIN;
        frameLayout_home.setVisibility(View.VISIBLE);
        iv_home.setImageResource(R.mipmap.button_home_sel);
        FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            manager.beginTransaction().replace(R.id.main_frame, mainFragment)
                    .commitAllowingStateLoss();
        } else {
            manager.beginTransaction().show(mainFragment).commitAllowingStateLoss();
        }
        if (HomeCommonPage.mCanDelete) {
            HomeCommonPage.mCanDelete = false;
            SeconPageAdapter.isDelete = false;
            mainFragment.refreshPages();
        }
        showTitleBar(true);

    }

    public void changeToEasy() {
        setNavibar();
        naviForeground();
        lastVisibilyLayout = Constant.TAG_EASY_STOP;
        frameLayout_easy_stop.setVisibility(View.VISIBLE);
        if (mapFragment != null) {
            fragmentManager.beginTransaction().remove(mapFragment).commitAllowingStateLoss();
            mapFragment = null;
        }
        if (null == mEasyStopFragment) {
            mEasyStopFragment = new EasyStopFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.easy_stop_frame, mEasyStopFragment, Constant.FRG_EASY_STOP).commitAllowingStateLoss();
        } else {
        }

    }
    public void changeToQPlay(){
        setNavibar();
        naviBackground();
        lastVisibilyLayout = Constant.TAG_QPLAY;
        frameLayout_q_play.setVisibility(View.VISIBLE);
        if (null == qPlayMainFragment) {
            qPlayMainFragment = new QPlayMainFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.qplay_container, qPlayMainFragment,QPlayMainFragment.class.getSimpleName()).commitAllowingStateLoss();
        }

    }

    private void naviForeground() {
        if (MapCfg.mNaAciFragmentIsNaVi) {

            MapCfg.mNaAciFragmentIsBackground = false;
            //暂停计时
            MapCfg.mToTalTime += (System.currentTimeMillis() - MapCfg.mStartTime);

        }

    }

    private void naviBackground() {
        if (MapCfg.mNaAciFragmentIsNaVi) {

            MapCfg.mNaAciFragmentIsBackground = true;
            //开始计时
            MapCfg.mStartTime = System.currentTimeMillis();

        }

    }


    public void addMusicFragment() {
        frameLayout_home.setVisibility(View.GONE);
        frameLayout_phone.setVisibility(View.GONE);
        frameLayout_map.setVisibility(View.GONE);
        frameLayout_music.setVisibility(View.GONE);
        frameLayout_set.setVisibility(View.GONE);
        frameLayout_easy_stop.setVisibility(View.GONE);
        //frameLayout_choose_app.setVisibility(View.GONE);
        music_play.setVisibility(View.GONE);
        local_music_frame.setVisibility(View.GONE);
        kuwo.setVisibility(View.GONE);

        //mChooseAppLayout.setVisibility(View.GONE);
        iv_home.setImageResource(R.mipmap.button_home);
        iv_phone_book.setImageResource(R.mipmap.button_phone);
        iv_map.setImageResource(R.mipmap.button_map);

        Trace.Debug("MusicFragment", "musicFragment=" + musicFragment);
        //如果Fagment已经在后台运行,直接显示,不进行任何更新
        if (musicFragment == null) {
            if (CacheUtils.getInstance(this).getString(SettingCfg.LastPostion, null) != null && CacheUtils.getInstance(this).getString(SettingCfg.LastPostion, null).equals(Constant.TAG_LERADIO)) {
                ChangeToLeradio();
            } else if (CacheUtils.getInstance(this).getString(SettingCfg.LastPostion, null) != null && CacheUtils.getInstance(this).getString(SettingCfg.LastPostion, null).equals(Constant.TAG_LOCAL)) {
                changeToLocal();
            } else {
                ChangeToLeradio();
            }
        } else {
            if (LePlayer.TYPE == 0) {
                changeToPlayMusic();
            } else if (mLastMusicLayout.equals(Constant.TAG_LERADIO)) {
                ChangeToLeradio();
            } else if (mLastMusicLayout.equals(Constant.TAG_LOCAL)) {
                changeToLocal();

            } else {
                ChangeToLeradio();
            }
//            FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
//            manager.beginTransaction().show( musicFragment).commitAllowingStateLoss();
//            music_play.setVisibility(View.VISIBLE);
        }
        iv_music.setImageResource(R.mipmap.button_radio_sel);
    }

    public void removeMusicPlayPage() {
        frameLayout_home.setVisibility(View.GONE);
        frameLayout_phone.setVisibility(View.GONE);
        frameLayout_map.setVisibility(View.GONE);
        frameLayout_music.setVisibility(View.GONE);
        frameLayout_set.setVisibility(View.GONE);
        frameLayout_easy_stop.setVisibility(View.GONE);
        //frameLayout_choose_app.setVisibility(View.GONE);
        music_play.setVisibility(View.GONE);
        local_music_frame.setVisibility(View.GONE);
        kuwo.setVisibility(View.GONE);
        //mChooseAppLayout.setVisibility(View.GONE);
        iv_home.setImageResource(R.mipmap.button_home);
        iv_phone_book.setImageResource(R.mipmap.button_phone);
        iv_map.setImageResource(R.mipmap.button_map);

        if (lePlayer.TYPE == 1) {
            lePlayer.OPEN_LERADIO = false;
            ChangeToLeradio();

        } else if (lePlayer.TYPE == 3) {
            lePlayer.OPEN_LOCAL = false;
            changeToLocal();
        } else {
            if (lastVisibilyLayout.equals(Constant.TAG_MAIN)) {
                changeToHome();
            } else if (lastVisibilyLayout.equals(Constant.TAG_MAP)) {
                changeToNavi();

            } else if (lastVisibilyLayout.equals(Constant.TAG_LERADIO)) {
                ChangeToLeradio();

            } else if (lastVisibilyLayout.equals(Constant.TAG_CALL)) {
                changeToPhone();

            } else if (lastVisibilyLayout.equals(Constant.TAG_SETTING)) {
                changeToSetting();

            }else if (lastVisibilyLayout.equals(Constant.TAG_EASY_STOP)) {
                changeToEasy();

            } else if (lastVisibilyLayout.equals(Constant.TAG_LOCAL)) {
                changeToLocal();

            } else {
                changeToHome();
            }
        }


    }

    protected void hadleMessages(Message msg) {
        switch (msg.what) {
            case MessageTypeCfg.MSG_CAR_CONNECT:
                showLogo();
                break;
            case MessageTypeCfg.MSG_CAR_UNCONNECT:
                hideLogo();
                break;

            case MessageTypeCfg.MSG_ROUND_GUIDE:
                if (isNavigating && !isInMapFragment) {
                    Toast.makeText(mContext, R.string.main_is_tingjiandan_navi, Toast.LENGTH_SHORT).show();
                } else {
                    changeToNavi();
                }
                break;
            case MessageTypeCfg.MSG_MAIN:
                changeToHome();
                break;
            case MessageTypeCfg.MSG_MUSIC:
                ChangeToLeradio();
                break;
            case MessageTypeCfg.MSG_PHONE:
                changeToPhone();
                break;
            case UPDATE_TIME:
                initTime();
                NaviFragment naviFragment= (NaviFragment) getFragmentByTag(NaviFragment.class.getSimpleName());
                if (naviFragment!=null){
                    naviFragment.setAutoMapMode();
                }

                break;
            case STOP:
                break;
            case START:
                break;
        }

    }

    public MapFragment getMapFragment() {
        return mapFragment;
    }

    public int getRlMainHeight() {
        return mRlMain.getHeight();
    }

    public void NotifyMapView() {
        changeToNavi();
        isThinCarMain = true;
    }


    public void NotifyMapToOri() {
        changeToNavi();
        isThinCarMain = false;

        if (GlobalCfg.IS_POTRAIT) {
            EventBus.getDefault().post(Constant.SHOW_MAP_TOPVIEW);
        }
    }

    /**
     * 通知瘦车机连接状态
     * @param value
     */
    public void notifyThinCarState(boolean value) {
        isThinCar = value;
        Config.isConnectedVehicle=value;
        if (otaMessageDialog!=null){
            if (otaMessageDialog.isShowing()){
                otaMessageDialog.dismiss();

            }
            otaMessageDialog=null;
        }

        mLeThincarInfoInterface.setThinCarState(value);
        //lePlayer.setPcmOpen(isThinCar);
//        try {
        LeVoiceEngineUtils.setConnectedVehicle(value);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEvent(Integer i) {
        switch (i) {
            case Constant.NOTIFY_MAINACT_SHOWMAP:
                NotifyMapToOri();
                break;
            case Constant.SHOW_BOTTOM_BAR:
                mBottomBar.setVisibility(View.VISIBLE);
                showSystemNavigationBar();
                break;
            case Constant.HIDE_BOTTOM_BAR:
                mBottomBar.setVisibility(View.INVISIBLE);
                hideSystemNavigationBar();
                break;
            case Constant.NOTIFY_CURRENT_PAGE:
                notifyCurrentPageIndex();
                break;
            case Constant.SHOW_DEBUG_VIEW:
                GlobalCfg.isMemoryDebugOpen = true;
                mDebugMemoryText.setVisibility(View.VISIBLE);
                mDebugCurrentCPUText.setVisibility(View.VISIBLE);
                updatePhoneInfo();
                break;
            case Constant.HIDE_DEBUG_VIEW:
                GlobalCfg.isMemoryDebugOpen = false;
                mDebugMemoryText.setVisibility(View.GONE);
                mDebugCurrentCPUText.setVisibility(View.GONE);
                break;
        }

    }

    /**
     * 监听系统UI（主要是虚拟按键）的显示和隐藏
     */
    private void setOnSystemUiVisibilityChangeListener(){
        this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
//                if(mBottomBar.getVisibility()==View.INVISIBLE){
                    hideSystemNavigationBar();
//                }
            }
        });
    }

    /**
     * 隐藏虚拟按键
     */
    private void hideSystemNavigationBar() {
        if(isThinCar){
            if(checkDeviceHasNavigationBar(this)){
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                    View view = this.getWindow().getDecorView();
                    view.setSystemUiVisibility(View.GONE);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    View decorView = getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
        }
    }


    /**
     * popupwindow 隐藏导航栏
     */
    private void popUpWindowHideSystemNaviBar(){
        if(isThinCar){
            if(popupWindow!=null){
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                    View view = popupWindow.getContentView();
                    view.setSystemUiVisibility(View.GONE);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    View decorView = popupWindow.getContentView();
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
        }
    }

    /**
     * 显示虚拟按键
     */
    private void showSystemNavigationBar(){
        if(checkDeviceHasNavigationBar(this)){
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                View view = this.getWindow().getDecorView();
                view.setSystemUiVisibility(View.VISIBLE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }


    public void onStartVoiceAssistant() {
        if (popupWindow == null) {
            popupWindow = new LeVoicePopupWindow(HomeActivity.this);
        }

        /**停止导航语音播报*/
//        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_SPEAKER,0);

        if (isThinCar) {
            popUpWindowHideSystemNaviBar();
            popupWindow.setmRecognitionListener(VoiceAssistantHelp.getInstance().getVoiceRecognitionListener());
            /**popupWindow不支持此接口，暂时去掉**/
            popupWindow.setThinCarDissmissListener(new LeVoicePopupWindow.MyDissmissListener() {
                @Override
                public void destory() {
                    if (lePlayer != null && lePlayer.getCurrentStatus() != null && lePlayer.getCurrentStatus().currentItem != null && !BaseActivity.isVoice && !BaseActivity.isStoped) {
                        Trace.Debug("####start");
                        LetvReportUtils.recordActivityEnd("VoiceActivity");
                        lePlayer.startPlay();
                    }

                    notifyCurrentPageIndex();

                    /**开始导航语音播报*/
//                    CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_SPEAKER,1);
                    if (isThinCar) {
                        VoiceAssistantHelp.getInstance().getVoiceRecognitionListener().stopVoiceRecord();
                        VoiceAssistantHelp.getInstance().stopVoiceAssistant();
                    }
                }
            });
        }
    }

    private void showNaviDialog() {
        int isNaving = CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_ONGOING, 0);
        final String endAddr = CacheUtils.getInstance(mContext).getString(SettingCfg.NAVI_END_ADDRESS, "");
        if (isNaving == 0 || endAddr.equals("")) {
            return;
        }

        DeleteDataDialog dialog = new DeleteDataDialog(this, "continueNaving");
        dialog.setListener(new DeleteDataDialog.ICallDialogCallBack() {
            @Override
            public void onConfirmClick(DeleteDataDialog currentDialog) {
                //如果是语音搜索则不做任何操作
                frameLayout_home.setVisibility(View.GONE);
                frameLayout_phone.setVisibility(View.GONE);
                frameLayout_map.setVisibility(View.GONE);
                frameLayout_music.setVisibility(View.GONE);
                frameLayout_set.setVisibility(View.GONE);
                frameLayout_easy_stop.setVisibility(View.GONE);
                music_play.setVisibility(View.GONE);
                local_music_frame.setVisibility(View.GONE);
                kuwo.setVisibility(View.GONE);
                iv_home.setImageResource(R.mipmap.button_home);
                iv_phone_book.setImageResource(R.mipmap.button_phone);
                iv_map.setImageResource(R.mipmap.button_map);
                iv_music.setImageResource(R.mipmap.button_radio);

                lastVisibilyLayout = Constant.TAG_MAP;
                frameLayout_map.setVisibility(View.VISIBLE);
                iv_map.setImageResource(R.mipmap.button_map_sel);

                ((HomeActivity) mContext).isNavigating = true;
                ((HomeActivity) mContext).isInMapFragment = true;
                ((HomeActivity) mContext).isInEasyStop = false;

                Bundle nBundle = new Bundle();
                nBundle.putString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS, endAddr);
                nBundle.putString(LAUNCH_FRAGMENT, RoutePlanFragment.MAP);
                AMapNavi.getInstance(mContext).destroy();

                /** 发消息给车机通知进行全屏导航 */
                mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                        ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE,0);
                setCurrentPageIndex(ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE);

                RoutePlanFragment secondFragment = RoutePlanFragment.getInstance(nBundle);
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment,RoutePlanFragment.class.getSimpleName()).commitAllowingStateLoss();
                ((HomeActivity) mContext).getSupportFragmentManager().executePendingTransactions();
            }

            @Override
            public void onCancelClick(DeleteDataDialog currentDialog) {
                CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_ONGOING, 0);
            }
        });

        dialog.show();
    }


    private Fragment getFragmentByTag(String  tag){
        if (fragmentManager!=null){
            return fragmentManager.findFragmentByTag(tag);
        }else{
            return null;
        }
    }


    private Fragment getFragmentById(int id){
        if (fragmentManager!=null){
            return fragmentManager.findFragmentById(R.id.map_frame);
        }else{
            return null;
        }
    }

    private void startBaiDuMap(String endAdress) {
        endAdress=EcoApplication.getInstance().getAddress()+","+EcoApplication.getInstance().getLatitude()+","+EcoApplication.getInstance().getLongitude();
        String[] stStrs = endAdress.split(",");
        if (stStrs.length <= 0) {
            return;
        }
        Intent intent;
        if(PackageUtil.ApkIsInstall(mContext,"com.baidu.BaiduMap")){
            notifyCurrentThirdAppPage();
            try {
                com.baidu.mapapi.model.LatLng sourceLatLng= new com.baidu.mapapi.model.LatLng(Double.parseDouble(stStrs[1]),Double.parseDouble(stStrs[2]));
                CoordinateConverter converter  = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.COMMON);
// sourceLatLng待转换坐标
                converter.coord(sourceLatLng);
                com.baidu.mapapi.model.LatLng desLatLng = converter.convert();
                intent = Intent.getIntent("intent://map/direction?" +
                        //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
                        "destination=latlng:"+ desLatLng.latitude +","+ desLatLng.longitude +"|name:"+stStrs[0]+        //终点
                        "&mode=driving" +          //导航路线方式
//                        "region="+ city +
                        "&src=ecolink#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                //intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&src=thirdapp.navi.yourCompanyName.yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                ScreenRotationUtil.commandShowPop(mContext);
                if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                    ScreenRotationUtil.startLandService(mContext,"com.baidu.BaiduMap");
                }
                mContext.startActivity(intent); //启动调用
            } catch (URISyntaxException e) {
                Trace.Error("intent", e.getMessage());
            }
        }else{//未安装
            //market为路径，id为包名
            //显示手机上所有的market商店
            ToastUtil.show(mContext,R.string.str_install_baidu_map);
//            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }


    }

    private void startGaoDeMap(String endAdress) {
        endAdress=EcoApplication.getInstance().getAddress()+","+EcoApplication.getInstance().getLatitude()+","+EcoApplication.getInstance().getLongitude();
        String[] stStrs = endAdress.split(",");
        if (stStrs.length <= 0) {
            return;
        }
        if(PackageUtil.ApkIsInstall(mContext,"com.autonavi.minimap")){
            notifyCurrentThirdAppPage();
            try{
                Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=ecolink&poiname="+stStrs[0]+"&lat=" + stStrs[1] + "&lon=" + stStrs[2] + "&dev=0&style=2");
                mContext.startActivity(intent);
                ScreenRotationUtil.commandShowPop(mContext);
                if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                    ScreenRotationUtil.startLandService(mContext,"com.autonavi.minimap");

                }
            } catch (URISyntaxException e)
            {e.printStackTrace(); }
        }else{
            ToastUtil.show(mContext,R.string.str_install_gaode_map);
//            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }

    private void resetPlayerStateAfterVoiceDismiss() {
        isPopupWindowShow = false;
        VoiceAssistantHelp.getInstance().sendVoiceTriggeValue(VoiceAssistantHelp.ENABLE_VOICE_KEY);
        if (lePlayer != null && lePlayer.getCurrentStatus() != null && lePlayer.getCurrentStatus().currentItem != null && !BaseActivity.isVoice && !BaseActivity.isStoped) {
            Trace.Debug("####start");
            LetvReportUtils.recordActivityEnd("VoiceActivity");
            if (!isThinCar || !VoiceAssistantHelp.getInstance().isRadioPlaying()) {
                lePlayer.startPlay();
            }
        }

//        if(!PlayPCM.stopByUser){
//            QPlayer qPlayer=EcoApplication.LeGlob.getqPlayer();
//            if (qPlayer!=null&& !PlayPCM.stopByUser&&qPlayer.getcurrentMediaInfos()!=null) {
//                qPlayer.play();
//            }
//        }

        if (isThinCar) {
            CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_SPEAKER,1);
            VoiceAssistantHelp.getInstance().getVoiceRecognitionListener().stopVoiceRecord();
            VoiceAssistantHelp.getInstance().stopVoiceAssistant();
        }
        VoiceAssistantHelp.getInstance().setRadioPlayState(false);
    }

    public void resumeActivityNeeded() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
    }

    public void voiceWindowBackClick() {
        notifyCurrentPageIndex();
    }

    public void hideQPlay() {
        if (lastVisibilyLayout == Constant.TAG_QPLAY) {
            frameLayout_q_play.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isPopupWindowShow() {
        boolean isShow = false;
        if (popupWindow != null) {
            isShow = popupWindow.isShowing();
        }
        return isShow;
    }

    private void updatePhoneInfo() {
        mDebugCurrentCPUText.setText("当前应用占用CPU:" + PhoneInfoMonitor.getCurrentPackageCPU(mPid));
        mDebugMemoryText.setText("当前应用占用内存:" + PhoneInfoMonitor.getMemory() + "MB");

        if (GlobalCfg.isMemoryDebugOpen) {
            mUpdateHandler.sendEmptyMessageDelayed(1,1000);
        }
    }

    /**
     * Fix LIGHTAVN-1619
     * leradio播放情况下，瘦车机语音识别按钮两点再次，leradio恢复播放
     * 其它情况慎调用此接口
     */
    public void resetPlayerState() {
        if (lePlayer != null && lePlayer.getCurrentStatus() != null
                && lePlayer.getCurrentStatus().currentItem != null && !BaseActivity.isVoice && !BaseActivity.isStoped) {
            lePlayer.startPlay();
        }
    }

    public void setCurrentLocationInfo(AMapLocation curMapLocation) {
        LocationInfo info = new LocationInfo();
        info.setLatitude(curMapLocation.getLatitude());
        info.setLongtitude(curMapLocation.getLongitude());
        info.setCityName(curMapLocation.getCity());
        mLeThincarInfoInterface.setLocatinInfo(info);
    }

    public void setCarVINcode(String code) {
        mLeThincarInfoInterface.setCarVinCode(code);
    }

    private void notifyCurrentThirdAppPage() {
        GlobalCfg.IS_THIRD_APP_STATE = true;
        GlobalCfg.isCarResumed = false;
        DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                ThinCarDefine.PageIndexDefine.THIRAD_APP_PAGE, 0);
    }

    private void turnEcolinkDirection(Intent intent) {
        int turnDirection = 0;
        if (intent != null) {
            /** 判断是否需要调整为竖屏 */
            turnDirection = intent.getIntExtra(ScreenRecordActivity.TURN_CAR_DIRECTION,0);
            String action = intent.getAction();
            if (action != null && action.equals(ScreenRecordActivity.AOA_START_ACTIVITY_ACTION)){
                isAoaRecordSuccess = true;
            }
        }

        boolean neeRestart = false;
        switch (turnDirection) {
            case ScreenRecordActivity.TURN_CAR_PORTRAIT:
                /** 需要调整为竖屏*/
                if (!GlobalCfg.IS_POTRAIT) {
                    CacheUtils.getInstance(this).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, true);
                    GlobalCfg.IS_POTRAIT = true;
                    ScreenRotationUtil.stopLandService(this);
                    neeRestart = true;
                }
                break;
            case ScreenRecordActivity.TURN_CAR_LAND:
                /** 需要调整为横屏*/
                if (GlobalCfg.IS_POTRAIT) {
                    CacheUtils.getInstance(this).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, false);
                    GlobalCfg.IS_POTRAIT = false;
                    neeRestart = true;
                }
                break;
        }

        if (neeRestart) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EcoApplication.isAoaRestart = true;
                    HomeActivity.isThinCar = false;
                    restartActivity();
                }
            }, 1500);
        } else {
            initThinCar();
        }
    }

    private void restartActivity () {
        LogUtils.i("HomeActivity", "restartActivity !!!!");
        EcoApplication.mIsRestart = true;
        EcoApplication.mIsRestarting = true;
        this.finish();
    }

    private void initThincarDataSend() {
        LeRadioSendHelp.getInstance().initLeRadioSendHelp(this.getApplicationContext());
        DeviceInfoNotifyHelp.getInstance().initDeviceInfo(this.getApplicationContext());
        ThirdAppMsgHelp.getInstance().initThirdAppMsgHelp(this.getApplicationContext());
        PcmDataManager.getInstance().initPcmDataManager(this.getApplicationContext());
    }

    /**
     * 有些情况下通讯未建立，从这里获取事件进行处理
     */
    private void showDefaultPage() {
        if (HomeActivity.isThinCar && mReceiveDataService != null && mThinCarIAOACallback != null) {
            int command = mReceiveDataService.getCommandEvenHeader();
            MsgHeader header = mReceiveDataService.getNaviEvenHeader();
            if (command != ReceiveDataService.DEFAULT_COMMEAN_VALUE) {
                mThinCarIAOACallback.onCommand(command,0);
            }

            if (header != null) {
                mThinCarIAOACallback.onNaviEvent(header.startx, header.starty, header.width, header.height);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onThincarQuickSearchEvent(ThincarQuickSearchEvent event) {
        requestQickSearch(event.getSearchTarget());
    }
}