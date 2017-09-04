package com.letv.leauto.ecolink.cfg;

/**
 * Created by liweiwei on 16/3/3.
 */
public class GlobalCfg {
    public static final String WIDGETYEPE = "widget_type";
    //全局定义横竖屏
    public static Boolean IS_POTRAIT = false;
    //如果用户在断开横屏车机USB之前  点击home键  记住拔线时的状态  用于再次点击进入时重启进入竖屏
    public static Boolean IS_APPLICATION_FRONT = true;
    //手机数据网络判断
    public static Boolean IS_MOBILE_NET = true;
    //显示车场logo
    public static String CAR_FACTORY_NAME = null;
    //手机数据网络判断
    public static Boolean IS_CAR_CONNECT = false;

    //判断是否为电动车
    public static Boolean IS_ELECTRIC_CAR = false;

    public static boolean CAR_IS_lAND=true;

    public static boolean IS_DONGFEN=false;

    public static  boolean IS_VOICE=false; /*当前是否在语音播报界面*/
    public static String MUSIC_TYPE="";

    public static String  FAVOR ="favor";
    public static String  LOCAL ="local";
    public static String  HOME ="home";
    public static String  COMPANY ="company";

    public static String MUSIC ="music";

    public static boolean coupon = true;//洗车券
    public static String userId = "";
    public static String brandName ="";
    public static String name = "";
    public static String url = "";
    public static String sSerialId = "";
    public static int pChecked = 0;//省份默认选中位置
    public static int cChecked = 0;//城市默认选中位置


    public static int EXPENDSIZE=40;
    public static int DEFAULT_SIZE=2;

    /** 判断是否三方app 起动中*/
    public static Boolean IS_THIRD_APP_STATE = false;
    public static boolean hasNet = true;

    public static boolean QQ_CONNECT;
    /** 保存手机是否处理锁屏状态中*/
    public static boolean isScreenOff = false;
    /** 保存应用是否处于后台*/
    public static boolean isAppBackground = false;
    /** 是否是车机事件让应用回到前台*/
    public static boolean isCarResumed = false;
    /** 保存应用是否处于HOME状态*/
    public static boolean isAppHomeState = false;
    public static boolean mNeedPlayAnim = true;

    public static String LEFT_TIME = "left_time";
    public static String LEFT_DISTANCE = "left_distance";

    /** 语音调试是否打开 */
    public static boolean isVoiceDebugOpen = false;
    /** 应用内存调试是否打 */
    public static boolean isMemoryDebugOpen = false;

    /** 轻车机是否已经连接*/
    public static boolean isThincarConnect = false;

    //是否是通过车机拾音
    public static boolean isSttStreamSrcUsb = false;

    /** 是否处于添加三方app状态 */
    public static boolean isChooseAppState = false;
}
