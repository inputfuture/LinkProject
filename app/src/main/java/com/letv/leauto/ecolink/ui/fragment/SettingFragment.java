package com.letv.leauto.ecolink.ui.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leauto.link.lightcar.LogUtils;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.net.GetCallBack;
import com.letv.leauto.ecolink.net.GsonUtils;
import com.letv.leauto.ecolink.net.OkHttpRequest;
import com.letv.leauto.ecolink.net.PostCallBack;
import com.letv.leauto.ecolink.receiver.ScreenRotationUtil;
import com.letv.leauto.ecolink.service.ScreenRotationService;
import com.letv.leauto.ecolink.thincar.protocol.DeviceInfoNotifyHelp;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.view.EcoDialog;
import com.letv.leauto.ecolink.umeng.AnalyzeManager;
import com.letv.leauto.ecolink.userinfo.ILoginOnActivityResult;
import com.letv.leauto.ecolink.userinfo.LoginManager;
import com.letv.leauto.ecolink.utils.AppCacheConfig;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.NoNetDialog;
import com.letv.leauto.ecolink.utils.SpUtils;
import com.letv.leauto.ecolink.utils.VehicleConst;
import com.letv.leauto.favorcar.contract.LoginContract;
import com.letv.loginsdk.activity.PersonalInfoActivity;
import com.letv.loginsdk.bean.DataHull;
import com.letv.loginsdk.bean.PersonalInfoBean;
import com.letv.loginsdk.bean.UserBean;
import com.letv.loginsdk.db.PreferencesManager;
import com.letv.loginsdk.network.task.GetResponseTask;
import com.letv.loginsdk.network.volley.VolleyRequest;
import com.letv.loginsdk.network.volley.VolleyResponse;
import com.letv.loginsdk.network.volley.toolbox.SimpleResponse;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

//import com.letv.leauto.ecolink.ui.FMSettingActivity;

public class SettingFragment extends BaseFragment implements View.OnClickListener, ILoginOnActivityResult {
    @Bind(R.id.iv_person_icon)
    ImageView iv_person_icon;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.tv_login)
    TextView tv_login;
    @Bind(R.id.lyt_keypad)
    RelativeLayout lyt_keypad;
    @Bind(R.id.tv_keypad_state)
    TextView tv_keypad_state;
    @Bind(R.id.lyt_fm)
    RelativeLayout lyt_fm;
    @Bind(R.id.lyt_music)
    RelativeLayout lyt_music;
    @Bind(R.id.lyt_navi)
    RelativeLayout lyt_navi;
    /*@Bind(R.id.lyt_cusser)
    RelativeLayout lyt_cusser;*/
    @Bind(R.id.about)
    RelativeLayout mAbout;

    @Bind(R.id.lr_cache)
    RelativeLayout ryt_cache;
    @Bind(R.id.tv_cache)
    TextView tv_cache;

    @Bind(R.id.lyt_barrage)
    RelativeLayout lyt_barrage;
    @Bind(R.id.lyt_tts)
    RelativeLayout ttsSetting;
    @Bind(R.id.lyt_bright)
    RelativeLayout screenSetting;
    @Bind(R.id.lyt_layout_display)
    RelativeLayout layout_display;

//    @Bind(R.id.tv_version_code)
//    TextView tv_version_code;

    @Bind(R.id.iv_barrage_select)
    ImageView iv_barrage_select;
    @Bind(R.id.iv_tts_select)
    ImageView iv_tts;
    @Bind(R.id.iv_openlight_select)
    ImageView iv_screen;
    @Bind(R.id.iv_layout_display)
    ImageView iv_layout_display;


//    @Bind(R.id.lyt_version)
//    RelativeLayout lyt_version;

    //    @Bind(R.id.iv_version)
//    ImageView iv_Version;
    @Bind(R.id.tv_title)
    TextView MyVehicleCard;

    private String cache;

    private final static String TAG = "SettingFragment";
    private HomeActivity homeActivity;
    private int typeId = R.id.lyt_bright;
    private Context mContext;
    private LocalBroadcastManager mLocalBroadcasetManager;
    public final static String ACCOUNT_TYPE = "com.letv";
    public static final String AUTH_TOKEN_TYPE_LETV = "tokenTypeLetv";
    /**
     * 轻车机连接成功时发送此广播
     */
    public static final String ACTION_THINCAR_CONNECTED = "com.letv.leauto.ecolink.action.THINCAR_CONNECTED";
    /**
     * 轻车机断开连接时发送此广播
     */
    public static final String ACTION_THINCAR_DISCONNECTED = "com.letv.leauto.ecolink.action.THINCAR_DISCONNECTED";

    public static SettingFragment getInstance(Bundle bundle) {
        SettingFragment mFragment = new SettingFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
//        if (GlobalCfg.IS_POTRAIT) {
//            view = inflater.inflate(R.layout.setting_page, null);
//        } else {
//            view = inflater.inflate(R.layout.setting_page_l, null);
//        }
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.setting_page, null);
        } else {
            view = inflater.inflate(R.layout.setting_page_l, null);
        }
        ButterKnife.bind(this, view);
        mContext = getActivity();
        initView();
//        if(hasLetvAuthenticator(mContext)) {
//            if (isLogin(mContext)) {
//                SpUtils.putBoolean(mContext, "islogin", true);
//                getUserToken();
//                getLoginName(mContext);
//                showHadLoginView(true);
//            }else {
//                showHadLoginView(false);
//            }
//
//        }
//        if (LoginManager.isLogin(EcoApplication.getInstance())) {
        if (LoginContract.isLogin()) {
            checkUpdate();
            PreferencesManager instance = PreferencesManager.getInstance();
            final String ssoToken = instance.getSso_tk();
            showHadLoginView(true);

//            GetResponseTask.getGetResponseTaskInstance().judgeLoginTask(ssoToken,
//                    new SimpleResponse<JudgeLoginBean>() {
//                        @Override
//                        public void onNetworkResponse(VolleyRequest<JudgeLoginBean> request,
//                                                      JudgeLoginBean result, DataHull hull, VolleyResponse.NetworkResponseState state) {
//                            //在此通过 NetworkResponseState 进行判断
//                            if (state == VolleyResponse.NetworkResponseState.SUCCESS) {
//                                getToken(ssoToken);
//                                showHadLoginView(true);
//                            } else {
//                                SettingFragment.this.LogOut();
//                            }
//
//                        }
//                    });


        } else {
            showHadLoginView(false);
        }
        return view;
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        homeActivity = (HomeActivity) mContext;
        mLocalBroadcasetManager = LocalBroadcastManager.getInstance(mContext);

    }


    private void initView() {
        mIsCurrentConn = false;
        if (!GlobalCfg.isThincarConnect) {
            tv_keypad_state.setText(R.string.str_unconnect);
        } else {
            tv_keypad_state.setText(R.string.str_connect);
        }
        lyt_keypad.setOnClickListener(this);
        lyt_fm.setOnClickListener(this);
        lyt_music.setOnClickListener(this);
        lyt_navi.setOnClickListener(this);
        // lyt_cusser.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        lyt_barrage.setOnClickListener(this);
        ttsSetting.setOnClickListener(this);
        screenSetting.setOnClickListener(this);
        layout_display.setOnClickListener(this);
        // lyt_version.setOnClickListener(this);
        ryt_cache.setOnClickListener(this);
        // tv_version_code.setText(getVersion());
        MyVehicleCard.setOnClickListener(this);

        if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.BARRAGE_OPEN, true)) {
            iv_barrage_select.setImageResource(R.mipmap.switch_on);
        } else {
            iv_barrage_select
                    .setImageResource(R.mipmap.switch_off);
        }

        if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.TTS_OPEN, true)) {
            iv_tts.setImageResource(R.mipmap.switch_on);
        } else {
            iv_tts
                    .setImageResource(R.mipmap.switch_off);
        }

        if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.SCREEN_LIGHT_OPEN, true)) {
            iv_screen.setImageResource(R.mipmap.switch_on);
        } else {
            iv_screen.setImageResource(R.mipmap.switch_off);
        }

        if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, false)) {
            iv_layout_display.setImageResource(R.mipmap.switch_on);
        } else {
            iv_layout_display.setImageResource(R.mipmap.switch_off);
        }

        tv_cache.setText("0");
        new MyTask().execute();

    }

    @Override
    protected void notificationEvent(int keyCode) {
        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if (!((HomeActivity) mContext).isPopupWindowShow) {
                    homeActivity.onBackPressed();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (v.getId()) {
            case R.id.tv_title:
                doLogin();
                break;
            case R.id.lyt_keypad:
                if (GlobalCfg.isThincarConnect) {
                    //已连接
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFmFragment(), "SettingFmFragment").commitAllowingStateLoss();
                } else {
                    //未连接
                    /*try {
                        Intent bt_intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(bt_intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingThincarFragment(), "SettingThincarFragment").commitAllowingStateLoss();
                }
                break;
            case R.id.lyt_fm:
//                Intent fm_intent = new Intent(mContext, FMSettingActivity.class);
//                startActivity(fm_intent);
                //((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFmFragment(), "SettingFmFragment").commitAllowingStateLoss();
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingEcolinkFragment(), "SettingEcolinkFragment").commitAllowingStateLoss();
                break;
            case R.id.lyt_music:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingMusicFragment(), "SettingMusicFragment").commitAllowingStateLoss();
                break;
            case R.id.lyt_navi:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingNaviFragment(), "SettingNaviFragment").commitAllowingStateLoss();
                break;

            case R.id.lr_cache:
                SettingCacheFragment frg = new SettingCacheFragment();
                frg.setCacheSring(cache);
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, frg, SettingCacheFragment.TAG).commitAllowingStateLoss();

                break;
//            case R.id.lyt_cusser:
//                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingCustomSerFragment(),"SettingCustomSerFragment").commitAllowingStateLoss();
//                break;
            case R.id.about:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingAboutFragment(), "SettingAboutFragment").commitAllowingStateLoss();
                break;
            case R.id.lyt_barrage:

                if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.BARRAGE_OPEN, true)) {
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.BARRAGE_OPEN, false);
                    iv_barrage_select
                            .setImageResource(R.mipmap.switch_off);
                } else {
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.BARRAGE_OPEN, true);
                    iv_barrage_select
                            .setImageResource(R.mipmap.switch_on);
                }
                break;
            case R.id.lyt_tts:

                if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.TTS_OPEN, true)) {
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.TTS_OPEN, false);
                    iv_tts
                            .setImageResource(R.mipmap.switch_off);
                } else {
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.TTS_OPEN, true);
                    iv_tts
                            .setImageResource(R.mipmap.switch_on);
                }
                break;
            case R.id.lyt_bright:

                //统计搜索关键字
                typeId = R.id.lyt_bright;
                HashMap<String, String> map = new HashMap<String, String>();
                if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.SCREEN_LIGHT_OPEN, true)) {
//                    showDialog("确定关闭屏幕常亮?");
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_LIGHT_OPEN, false);
                    iv_screen
                            .setImageResource(R.mipmap.switch_off);
                    homeActivity.closeLight();
                    LetvReportUtils.reportMessage(false);
                    map.put(AnalyzeManager.SetPara.SCREEN_OPEN, "close");
                } else {
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_LIGHT_OPEN, true);
                    iv_screen
                            .setImageResource(R.mipmap.switch_on);
                    LetvReportUtils.reportMessage(true);
                    //常亮
                    homeActivity.openLight();
//                    showDialog("确定打开屏幕常亮?");
                    map.put(AnalyzeManager.SetPara.SCREEN_OPEN, "open");
                }
                map.put(AnalyzeManager.SetPara.DEVICE_ID, DeviceUtils.getDeviceId(mContext));
                MobclickAgent.onEvent(mContext, AnalyzeManager.Event.SETTING, map);
                break;
            case R.id.lyt_layout_display:
                //车机连上横竖屏切换禁用
                if (GlobalCfg.IS_CAR_CONNECT) {
                    return;
                }
                if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, false)) {
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, false);
                    iv_layout_display.setImageResource(R.mipmap.switch_off);
                    GlobalCfg.IS_POTRAIT = false;
                } else {
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_POTRAIT_OPEN, true);
                    iv_layout_display.setImageResource(R.mipmap.switch_on);
                    GlobalCfg.IS_POTRAIT = true;
                    ScreenRotationUtil.stopLandService(mContext);

                }
                reStart();
                break;
            case R.id.lyt_version:


                break;
            default:
                break;
        }
    }

    @OnClick(R.id.tv_login)
    void loginOrOut() {
        if (tv_login.getText().equals("登录")) {
            doLogin();
        } else {
            LogOut();
        }
    }

    public void showDialog(String title) {
        EcoDialog dialog = new EcoDialog(mContext, R.style.Dialog, title);
        dialog.setListener(new EcoDialog.ICallDialogCallBack() {
            @Override
            public void onConfirmClick(EcoDialog currentDialog) {
                if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.SCREEN_LIGHT_OPEN, true)) {
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_LIGHT_OPEN, false);
                    iv_screen
                            .setImageResource(R.mipmap.switch_off);
                    homeActivity.closeLight();
                    LetvReportUtils.reportMessage(false);
                } else {
                    CacheUtils.getInstance(mContext).putBoolean(SettingCfg.SCREEN_LIGHT_OPEN, true);
                    iv_screen
                            .setImageResource(R.mipmap.switch_on);
                    //常亮
                    homeActivity.openLight();
                    LetvReportUtils.reportMessage(true);
                }
                currentDialog.dismiss();
            }

            @Override
            public void onCancelClick(EcoDialog currentDialog) {
                currentDialog.dismiss();
            }

        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingFragment");
        registerBroadcast();
        if (mGaiaLink != null && !mGaiaLink.isConnected()) {
            GlobalCfg.isThincarConnect = false;
            tv_keypad_state.setText(R.string.str_unconnect);
        } else {
            GlobalCfg.isThincarConnect = true;
            tv_keypad_state.setText(R.string.str_connect);
        }
        if (LoginContract.isLogin()) {
            showHadLoginView(true);
        } else {
            showHadLoginView(false);
        }
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingFragment.ACTION_THINCAR_CONNECTED);
        filter.addAction(SettingFragment.ACTION_THINCAR_DISCONNECTED);
        if (mConnectStateReceiver != null && mLocalBroadcasetManager != null) {
            mLocalBroadcasetManager.registerReceiver(mConnectStateReceiver, filter);
        }
    }

    private BroadcastReceiver mConnectStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SettingFragment.ACTION_THINCAR_CONNECTED)) {
                tv_keypad_state.setText(R.string.str_connect);
            } else if (action.equals(SettingFragment.ACTION_THINCAR_DISCONNECTED)) {
                tv_keypad_state.setText(R.string.str_unconnect);
            }
        }
    };

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd("SettingFragment");
        if (mLocalBroadcasetManager != null) {
            mLocalBroadcasetManager.unregisterReceiver(mConnectStateReceiver);
        }
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
        if (mConnectStateReceiver != null) {
            mConnectStateReceiver = null;
        }
        if (mLocalBroadcasetManager != null) {
            mLocalBroadcasetManager = null;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.04";
        }
    }

    /**
     * 重新启动应用
     */
    public void reStart() {
        EcoApplication.mIsRestart = true;
//        Intent launch = mContext.getPackageManager()
//                .getLaunchIntentForPackage(mContext
//                        .getPackageName());
//        launch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////        if (HomeActivity.isThinCar) {
////            launch.setAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
////        }
//        mContext.startActivity(launch);
     getActivity().finish();
    }


    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            tv_cache.setText("0.0KB");
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected Void doInBackground(Void... params) {
            try {
                cache = AppCacheConfig.getTotalCacheSize(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            tv_cache.setText(cache);
        }
    }

    //onPostExecute方法用于在执行完后台任务后更新UI,显示结果


    @OnClick(R.id.iv_person_icon)
    void startLogin() {
        doLogin();
    }

    /**
     * 查看用户详细信息
     */
    private void goUserDetails() {
        String uid = LoginManager.getUid(mContext);
        String ssoTk = LoginManager.getSsoTk(mContext);

        if ((!TextUtils.isEmpty(uid)) && (!TextUtils.isEmpty(ssoTk))) {
            PersonalInfoActivity.lunch(getActivity(), uid, ssoTk);
        }
    }

    @Override
    public void onLogOut() {
        System.out.println("onLogOut－－退出登录");
        LoginManager.logout(mContext);
        Message message = new Message();
        message.what = 0;
        handler.sendMessage(message);
    }

    @OnClick(R.id.tv_name)
    void myCrad() {
        doLogin();
    }

    public void LogOut() {
        LoginManager.logout(mContext);
        System.out.println("loginname+++LogOut");
//        if(hasLetvAuthenticator(mContext)){
//            startAPP("com.letv.android.account");//乐视手机单点登录退出方式，打开乐视手机账号管理
//        }else {
        //  new LoginSdkLogout().logout(mContext);
        // LoginManager.logout(mContext);
        //  }
        Message message = new Message();
        message.what = 0;
        handler.sendMessage(message);
        DeviceInfoNotifyHelp.getInstance().notifyUserLoginOut();

    }

    public void startAPP(String appPackageName) {
        try {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(appPackageName);
            startActivity(intent);
        } catch (Exception e) {

        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showHadLoginView(false);
                    break;
                case 1:
                    showHadLoginView(true);
                    break;
                case 2:
//                    ToastUtil.showShort(mContext,"与服务器连接错误");
//                    Toast.makeText(mContext,"与服务器连接错误",Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    public interface LoginCallBack {
        void onSuccess(UserBean userBean);

        void onFailer();
    }

    private void showHadLoginView(Boolean loginFlag) {
        if (loginFlag) {
            System.out.println("ccy showHadLoginView(true)");
//            String headPicUrl = LoginManager.getHeadPicUrl(mContext);
//            String userNickName = LoginManager.getNickname(mContext);
            String headPicUrl = LoginContract.getHeadPicUrl();
            String userNickName = LoginContract.getNickname();
            if (getActivity() != null) {
                Glide.with(getActivity()).load(headPicUrl).into(iv_person_icon);
            }
            tv_name.setVisibility(View.VISIBLE);
            tv_name.setText(userNickName);
            tv_login.setText("退出");
        } else {
            iv_person_icon.setImageResource(R.mipmap.set_person_icon);
            tv_name.setText("");
            tv_name.setVisibility(View.GONE);
            tv_login.setText("登录");
        }

    }

    private void checkUpdate() {
        GetResponseTask.getGetResponseTaskInstance().getUserInfoByUid(LoginManager.getUid(mContext), new SimpleResponse<PersonalInfoBean>() {
            @Override
            public void onCacheResponse(VolleyRequest<PersonalInfoBean> request, PersonalInfoBean result, DataHull hull, VolleyResponse.CacheResponseState state) {
                if (state == VolleyResponse.CacheResponseState.SUCCESS) {
                    String nickName = result.getNickname();
                    String headPicUrl = result.getPicture200x200();
//                    if (!nickName.equals(LoginManager.getNickname(mContext))) {
//                        LoginManager.setNickName(mContext, nickName);
//                    }
//
//                    if (!headPicUrl.equals(LoginManager.getHeadPicUrl(mContext))) {
//                        LoginManager.setHeadPicUrl(mContext, headPicUrl);
//                    }
                    if (!nickName.equals(LoginContract.getNickname())) {
                        LoginContract.setNickName(nickName);
                    }

                    if (!headPicUrl.equals(LoginContract.getHeadPicUrl())) {
                        LoginContract.setHeadPicUrl(headPicUrl);
                    }
                }
            }
        });
    }

    private void getToken(String ssToken) {
        //乐视集团SDK登录成功，用ssotoken登录易车卡，获取易车卡的token
        HashMap<String, String> bodys = new HashMap<>();
        bodys.put("sso_tk", ssToken);
        String params = GsonUtils.toJson(bodys);
        OkHttpRequest.postJson("login", VehicleConst.qauthUrl + "v1/leSso", null, params, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("ccy post请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) {
                System.out.println("ccy getToken onResponse");
//                Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get("credential");
                    VehicleConst.testToken = "Bearer " + jsonObject1.getString("access_token");
                    SpUtils.putString(mContext, "token", "Bearer " + jsonObject1.getString("access_token"));
                    SpUtils.putString(mContext, "refresh_token", jsonObject1.getString("refresh_token"));
                    SpUtils.putString(mContext, "mobile", jsonObject.getString("mobile"));
                    SpUtils.putString(mContext, "id", jsonObject.getString("id"));
                    getOwner("Bearer " + jsonObject1.getString("access_token"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
                System.out.println("ccy post请求出错" + errorCode);
            }
        });
    }

    private void getOwner(String token) {
        //登录成功，获取用户唯一ID
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", token);
        OkHttpRequest.get("owner", VehicleConst.qauthUrl + "v1/id", headers, new GetCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("get请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) {
                System.out.println("get请求成功");
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    SpUtils.putString(mContext, "owner", jsonObject.getString("id"));
                    System.out.println(jsonObject.getString("id"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response) {

            }
        });
    }

    public static void refeshToken(final Context context, final Handler myHandler) {
        //401错误时为token失效，调用此方法，用refresh_token刷新
        //刷新成功重新加载，刷新失败重新登录
        HashMap<String, String> bodys = new HashMap<String, String>();
        bodys.put("refresh_token", SpUtils.getString(context, "refresh_token", ""));
        String params = GsonUtils.toJson(bodys);
        OkHttpRequest.postJson("login", VehicleConst.qauthUrl + "v1/refresh", null, params, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("refresh请求失败");
                Message message = new Message();
                message.what = 2;
                myHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) {
                System.out.println("refresh请求成功");
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    VehicleConst.testToken = "Bearer " + jsonObject.getString("access_token");
                    SpUtils.putString(context, "token", "Bearer " + jsonObject.getString("access_token"));
                    SpUtils.putString(context, "refresh_token", jsonObject.getString("refresh_token"));
                    SpUtils.putBoolean(context, "islogin", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 3;
                myHandler.sendMessage(message);

            }

            @Override
            public void onError(int errorCode) {
                System.out.println("refresh请求出错" + errorCode);
                Message message = new Message();
                message.what = 2;
                myHandler.sendMessage(message);
            }

        });
    }

    public boolean hasLetvAuthenticator(Context context) {
        //判断是否为乐视手机
        AuthenticatorDescription[] allTypes = AccountManager.get(context).getAuthenticatorTypes();
        for (AuthenticatorDescription authenticatorType : allTypes) {
            if (ACCOUNT_TYPE.equals(authenticatorType.type)) {
                return true;

            }
        }
        return false;
    }

    public boolean isLogin(Context context) {
        //判断乐视手机账号是否登录
        AccountManager am = AccountManager.get(context);
        boolean isLogin = false;
        final Account[] accounts = am.getAccountsByType(ACCOUNT_TYPE);
        if (accounts != null && accounts.length > 0) {
            return true;
        }
        return isLogin;
    }

    public String getLoginName(Context context) {

        //乐视手机单点登录获取用户名以及头像等
        AccountManager am = AccountManager.get(context);
        final Account[] accountList = am.getAccountsByType(ACCOUNT_TYPE);
        String loginName = "";
        if (accountList != null && accountList.length > 0) {
            loginName = accountList[0].name;
            System.out.println("loginname+++" + accountList[0].toString());
            System.out.println("loginname+++" + loginName);
            SpUtils.putBoolean(mContext, "islogin", true);
        }
        SpUtils.putString(mContext, "userName", loginName);
        return loginName;
    }

//    public String getCurrentAreaShortName(PreferencesManager instance) {
//
//        return instance.getCurrentAreaShortName();
//    }
//
//    public String getCurrentAreaImageUrl(PreferencesManager instance) {
//
//        return instance.getCurrentAreaImageUrl();
//    }

    private UserBean mUserBean;// 登录返回的bean类

    public void doLogin() {
        //登录入口
//        if(hasLetvAuthenticator(mContext)){
//            if(!isLogin(mContext)){
//                final AccountManager am = AccountManager.get(mContext);
//                Bundle options = new Bundle();
//                options.putBoolean("loginFinish",true);
//                AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
//                    @Override
//                    public void run(AccountManagerFuture<Bundle> future) {
//                        Account[] accountList = am.getAccountsByType(ACCOUNT_TYPE);
//                        String loginName = "";
//                        if (accountList != null && accountList.length > 0) {
//                            loginName = accountList[0].name;
//                            System.out.println("loginname+++"+accountList[0].toString());
//                            System.out.println("loginname+++"+loginName);
//                            SpUtils.putBoolean(mContext, "islogin", true);
//                            getUserToken();
//                            getLoginName(mContext);
//                        }
//                    }
//                };
//                am.addAccount(ACCOUNT_TYPE, AUTH_TOKEN_TYPE_LETV, null, options, getActivity(),
//                        callback, null);
//            }else{
//                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new VehicleAddFragment(), "VehicleAddFragment").commit();
//            }
//        }else {
//            if (!LoginManager.isLogin(mContext)) {
        if (GlobalCfg.hasNet) {
            //网络正常
//            if (LoginManager.isLogin(EcoApplication.getInstance())) {
            if (LoginContract.isLogin()) {
                LogOut();
//                VehicleConst.testToken = SpUtils.getString(mContext, "token", "");
//                System.out.println("ccy login");
//                System.out.println("loginname+++" + VehicleConst.testToken);
//                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new VehicleAddFragment(), "VehicleAddFragment").commit();
            } else {
                LoginManager.login(getActivity(), new LoginManager.LoginCallBack() {
                    @Override
                    public void onSuccess(UserBean userBean) {
                        System.out.println("ccy onSuccess!");
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        getToken(userBean.getSsoTK());
                    }

                    @Override
                    public void onFailer() {
                        System.out.println("ccy onFailer!");
                    }
                });
            }
        } else {
            //无网络弹dialog
            NoNetDialog.show(mContext);
        }

//        }
    }

    public void initCard() {
        String ssToken = SpUtils.getString(mContext, "ssoTk", "");
        System.out.println(ssToken);
    }

    public void getUserToken() {
        //乐视手机单点登录成功获取token，用此token登录易车卡
        AccountManager am = AccountManager.get(mContext);
        Account account = am.getAccountsByType(ACCOUNT_TYPE)[0];
        AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    if (future.getResult() != null) {
                        SpUtils.putString(mContext, "ssoTk", future.getResult().getString(AccountManager.KEY_AUTHTOKEN));
                        getToken(future.getResult().getString(AccountManager.KEY_AUTHTOKEN));
                    }
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        am.getAuthToken(account, AUTH_TOKEN_TYPE_LETV, null, getActivity(), callback, new Handler());
    }

    private void contact() {
        //name：yangwei
        //mail:myjobcome@163.com
        //phone:15528213316
        //I feel honored to code with you!
    }
}

