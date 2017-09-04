package com.letv.leauto.ecolink.thincar.protocol;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.sdk.NotifyCommand;
import com.leauto.sdk.SdkManager;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.receiver.ScreenRotationUtil;
import com.letv.leauto.ecolink.thincar.module.ThincarQuickSearchEvent;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.ui.fragment.NaviFragment;
import com.letv.leauto.ecolink.utils.EcoActivityManager;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Utils;
import com.letv.voicehelp.eventbus.EventBusHelper;

/**
 * 这个类处理车机方面传过来的请求
 *
 * Created by Administrator on 2016/11/18.
 */
public class CarProtocolProcess {

    private static CarProtocolProcess sInstance;

    private CarProtocolProcess() {

    }

    public static CarProtocolProcess getInstance() {
        if (sInstance == null) {
            sInstance = new CarProtocolProcess();
        }

        return sInstance;
    }

    public void parseNotifyEvent(int event, String data, final Context context) {
        HomeActivity activity = (HomeActivity)context;
        NaviFragment fragment = NaviFragment.getThis();
        switch (event) {
            case ThinCarDefine.ProtocolNotifyValue.LAUNCH_THIRD_APP:
                String[] array = data.split(" ");
                if (array.length >= 2) {
                    startApp(array[0], array[1], context);
                }
                break;
            case ThinCarDefine.ProtocolNotifyValue.APP_REQUEST_PIC:
                ThirdAppMsgHelp.getInstance().sendAppIcon(data, context);
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_ALBUM_INFO:
                String[] strs = data.split(" ");
                if (strs.length >= 2) {
                    LeRadioSendHelp.getInstance().requestAlbumInfo(strs[0], strs[1]);
                }
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_SONG_IMAGE:
                LeRadioSendHelp.getInstance().requestImageById(data);
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_PLAYER_ACTION:
                String[] str = data.split(" ");
                if (str.length >= 2) {
                    LeRadioSendHelp.getInstance().requestPlayerAction(str[0], str[1]);
                }
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUESET_USER_INFO:
                DeviceInfoNotifyHelp.getInstance().requestAccountInfo();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_PHONE_INFO:
                DeviceInfoNotifyHelp.getInstance().requesetPhoneBattery();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_WELCOME_INFO:
                DeviceInfoNotifyHelp.getInstance().requestWelcomInfo();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_START_VOICE:
                VoiceAssistantHelp.getInstance().onStartVoiceAssistant();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_START_RECORD:
                VoiceAssistantHelp.getInstance().onStartVoiceRecord();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_DETECT_VOICE:
                VoiceAssistantHelp.getInstance().detectVoiceInput();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_DETECT_NOVOICE:
                VoiceAssistantHelp.getInstance().detectNoVoiceInput();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_STOP_RECORD:
                VoiceAssistantHelp.getInstance().stopVoiceRecord();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_STOP_VOICE:
                VoiceAssistantHelp.getInstance().requestStopVoiceRecognize();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_NAVI_BAR_INFO:
                NaviBarSendHelp.getInstance().requestNaviBarInfo();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_IS_IN_NAVI:
                NaviBarSendHelp.getInstance().sendIsInNaving();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_START_NAVI:
                NaviBarSendHelp.getInstance().requestStartNavi(data);
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_STOP_NAVI:
                NaviBarSendHelp.getInstance().requestStopNavi();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_START_PREVIEW:
                if (fragment != null) {
                    fragment.startPreview();
                }
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_STOP_PREVIEW:
                if (fragment != null) {
                    fragment.stopPreview();
                }
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_QUICK_SEARCH:
                EventBusHelper.post(new ThincarQuickSearchEvent(data));
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_ALL_SONG_INFO:
                LeRadioSendHelp.getInstance().requestAlbumList();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_ALL_APP_INFO:
                ThirdAppMsgHelp.getInstance().requestAllAppInfo(context);
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_PHONE_BOOK:
                BlueToothHelp.getInstance().requestPhoneBook(context);
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_CALL_HISTORY:
                BlueToothHelp.getInstance().requestCallHistory(data, context);
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_HUD_ACTION:
                NaviInfoSendHelp.getInstance().requestHudAction();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_BLUE_CONNECT:
                String[] blueData = data.split(" ");
                if (blueData.length == 2) {
                    BlueToothHelp.getInstance().requestAutoConnect(context,blueData[0],blueData[1]);
                } else if (blueData.length == 1) {
                    BlueToothHelp.getInstance().requestAutoConnect(context,blueData[0],"");
                }
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_BLUE_INFO:
                BlueToothHelp.getInstance().requestBlueToothInfo();
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_SETTING_ADDRESS:
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_SETTING_HOME:
                EventBusHelper.post(Constant.POI_SELECT_HOME_ADDRESS);
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_SETTING_WORK:
                EventBusHelper.post(Constant.POI_SELECT_WORK_ADDRESS);
                break;
            case ThinCarDefine.ProtocolNotifyValue.REQUEST_PLAYER_STATUS:
                LeRadioSendHelp.getInstance().requestPlayerStatus();
                break;
        }
    }

    //启动三方应用
    private void startApp(String packageName, String activityName, Context context) {
        GlobalCfg.IS_THIRD_APP_STATE = true;
        GlobalCfg.isCarResumed = false;

        if (GlobalCfg.isScreenOff) {
            return;
        }

        if (packageName.equals(Constant.LOCAL_APP_ID)) {
            launchLocalApp(activityName, context);
        } else if (packageName.equals(Constant.HomeMenu.FAVORCAR)) {
            HomeActivity activity = (HomeActivity)context;
            activity.changeToFavorCar();
        } else {
            //下面启动通过添加功能添加的应用
            try {
                if (!GlobalCfg.isScreenOff) {
                    if (GlobalCfg.IS_THIRD_APP_STATE) {
                        Intent intent1 = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                        context.getApplicationContext().startActivity(intent1);
                    }

                    Intent intent = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);

                    if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                        ScreenRotationUtil.startLandService(context,packageName);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(context, packageName + "is not installed!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    //启动首页8个图标对应用页面
    private void launchLocalApp(String activityName, Context context) {
        Context appContext = context.getApplicationContext();
        if (activityName.equals(Constant.TAG_LE_VIDEO)) {//启动Live
            startLeVedio(appContext);
        } else if (activityName.equals(Constant.TAG_WEIXIN)) {//启动微信
            startWChartApp(appContext);
        } else if (activityName.equals(Constant.TAG_GAODE_MAP)) {
            Utils.startGaoDeApp(appContext);
        } else if (activityName.equals(Constant.TAG_BAIDU_MAP)) {
            Utils.startBaidu(appContext);
        } else {
            GlobalCfg.IS_THIRD_APP_STATE = false;
            changeToPage(activityName, context);
        }
    }

    /**
     * 打开乐视视频
     */
    public void startLeVedio(Context context) {
        try {
            BaseActivity.isStoped=true;

            EcoApplication.LeGlob.getPlayer().stopPlay();
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(Constant.LE_VIDEO_PACKAGE_NAME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            commandShowPop(context);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 车机链接时打开悬浮按钮
     */
    private void commandShowPop(Context context) {
        SdkManager.getInstance(context).notifyCommand(
                NotifyCommand.COMMAND_SHOW_POP, 0, 0);
    }

    public void startWChartApp(Context context) {
        try {
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
            if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                ScreenRotationUtil.startLandService(context,cmp.getPackageName());
            }

        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "您的手机未安装微信", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }






    public void changeToPage(String activityName, Context context) {
//        ToastUtil.show(context.getApplicationContext(),activityName+EcoActivityManager.create().getTopActivityName(context));
        if (EcoActivityManager.create().topActivity().getClass().getName().equals(HomeActivity.class.getName())) {
            HomeActivity activity = (HomeActivity) context;
            if (activity.isDestroyed()) {
                return;
            }

            switch (activityName) {
                case Constant.TAG_MAP:
                    activity.changeToNavi();
                    break;
                case Constant.TAG_MAIN:

                    activity.changeToHome();
                    break;
                case Constant.TAG_LERADIO:
                    activity.setCurrentPageIndex(ThinCarDefine.PageIndexDefine.LERADIO_PAGE);
                    activity.ChangeToLeradio();
                    break;
                case Constant.TAG_CALL:
                    activity.changeToPhone();
                    break;

                case Constant.TAG_EASY_STOP:
//                if (isNavigating && !isInEasyStop) {
//                    showExitNaviDialog(8);
//                } else {
//                    ChangeButton(8);
//                }
                    break;
                case Constant.TAG_SETTING:
                    activity.changeToSetting();
                    break;
                case Constant.TAG_LOCAL:
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    activity.setCurrentPageIndex(ThinCarDefine.PageIndexDefine.LOCAL_PAGE);
                    activity.changeToLocal();
                    break;
                case Constant.TAG_QPLAY:
                    activity.changeToQPlay();
                    break;
                default:
                    break;
            }
        }else{
            Activity topactivity = EcoActivityManager.create().topActivity();
            if (topactivity != null) {
                topactivity.finish();
            }
        }

    }
}
