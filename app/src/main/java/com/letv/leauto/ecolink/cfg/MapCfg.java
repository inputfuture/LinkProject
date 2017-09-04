package com.letv.leauto.ecolink.cfg;

/**
 * Created by liweiwei on 16/3/24.
 */
public class MapCfg {
    public static final int ATM = 4;
    public static final int GAS = 3;
    public static final int WC = 2;
    public static final int MAINTENCE = 1;
    public static final String MAPMODE = "mapmode";
    public static final String POI_LOCATION = "poi_location";
    public static String city = "beijing";
    public static String location = "";
    public static final int MAPMODE_CUSTOME=0; /*普通的地图模式*/
    public static final int MAPMODE_ADD=1; /*添加公司或者家的地址模式*/
    public static final int MAPMODE_POI=2; /*在地图上显示点的位置模式*/



    public static final String SEARCH_TYPE = "search_type";
    //第一次launch 导航需要显示帧动画: 0 显示; 1 不显示
    public static String MAP_LAUNCHED = "map_launched";


    //搜索类型,1:从地图主页,2:Point家或公司,3:设置界面,4:语音搜索
    public static final int SEARCH_TYPE_MAP = 0;
    public static final int SEARCH_TYPE_ADD = 1;
    public static final int SEARCH_TYPE_SET = 2;
    public static final int SEARCH_TYPE_VOICE = 3;
    public static final int SEARCH_TYPE_THINCAR = 4;//来自车机端的搜索请求
    public static boolean mNaAciFragmentIsNaVi=false;
    public static boolean mNaAciFragmentIsBackground=false;
    public static long mStartTime=0;
    public static int mToTalTime=0;

    public static boolean mapfragmentOpen=false;
    public static boolean routFragmentOpen=false;
    public static boolean naviFragmentOpen=false;
}
