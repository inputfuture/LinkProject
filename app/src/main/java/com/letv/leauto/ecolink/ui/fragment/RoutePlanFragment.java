package com.letv.leauto.ecolink.ui.fragment;

import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyTrafficStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.leauto.link.lightcar.ThinCarDefine;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.event.CloseVoiceEvent;
import com.letv.leauto.ecolink.event.LinkCarConnectStatusObservable;
import com.letv.leauto.ecolink.lemap.navi.Utils;
import com.letv.leauto.ecolink.lemap.navi.routebean.StrategyBean;
import com.letv.leauto.ecolink.manager.LocationManager;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.LocationBaseFragment;
import com.letv.leauto.ecolink.ui.dialog.StrategyChooseDialog;
import com.letv.leauto.ecolink.ui.view.EcoRouteOverLay;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liweiwei1 on 2015/12/22.
 */
public class RoutePlanFragment extends LocationBaseFragment implements
        View.OnClickListener ,AMapNaviListener,AMap.OnMapClickListener,AMap.OnMapLongClickListener,
        AMap.OnMarkerClickListener,GeocodeSearch.OnGeocodeSearchListener,AMap.OnInfoWindowClickListener,AMap.InfoWindowAdapter,AMap.OnCameraChangeListener, Observer{

    public static final String ROUTEPLAN_START_ADDRESS = "ROUTEPLAN_START_ADDRESS";
    public static final String ROUTEPLAN_END_ADDRESS = "ROUTEPLAN_END_ADDRESS";
    public static final String LAUNCH_FRAGMENT = "LAUNCH_FRAGMENT";
    public static final String EASY_STOP = "EASY_STOP";
    public static final String MAP = "MAP";
    public static String ROUTEPLAN_DRIVE_MODE = "ROUTEPLAN_DRIVE_MODE";

    private static final int MSG_AUTO_NAVI = 0x01;

    private HomeActivity activity;
    AMap mAMap;
    @Bind(R.id.map)
    TextureMapView mapView;
    @Bind(R.id.iv_back)
    RelativeLayout iv_back;
    @Bind(R.id.rl_start_nav)
    RelativeLayout mStartNaviBtn;
    @Bind(R.id.tv_timer)
    TextView mTimeTextview;

    @Bind(R.id.route_strategy_one)
    TextView mRouteTextStrategyOne;
    @Bind(R.id.route_strategy_two)
    TextView mRouteTextStrategyTwo;
    @Bind(R.id.route_strategy_three)
    TextView mRouteTextStrategyThree;

    @Bind(R.id.route_linelayout_one)
    LinearLayout mRouteLineLayoutOne;
    @Bind(R.id.route_linelayout_two)
    LinearLayout mRouteLinelayoutTwo;
    @Bind(R.id.route_linelayout_three)
    LinearLayout mRouteLineLayoutThree;

    @Bind(R.id.route_distance_one)
    TextView mRouteTextDistanceOne;
    @Bind(R.id.route_distance_two)
    TextView mRouteTextDistanceTwo;
    @Bind(R.id.route_distance_three)
    TextView mRouteTextDistanceThree;

    @Bind(R.id.route_time_one)
    TextView mRouteTextTimeOne;
    @Bind(R.id.route_time_two)
    TextView mRouteTextTimeTwo;
    @Bind(R.id.route_time_three)
    TextView mRouteTextTimeThree;
    @Bind(R.id.emute)
    CheckBox mEmuteCheck;

    @Bind(R.id.iv_line_1)
    ImageView mDiver1;
    @Bind(R.id.iv_line_2)
    ImageView mDiver2;
    private boolean mEmuteIsOpen;
    String enAddr = null;
    String myAddr =null;

    Bundle savedInstanceState;
    @Bind(R.id.strategy_layout)
    LinearLayout mRouteLayout;
    @Bind(R.id.wait_view)
    ProgressBar mWaitBar;
    @Bind(R.id.traffic)
    ImageView mTrafficView;
    @Bind(R.id.location)
    ImageView mLocationBtn;
    @Bind(R.id.strategy_set)
    ImageView mStrategySetBtn;

    @Bind(R.id.zoom_in)
    ImageView mZoomInBtn;

    @Bind(R.id.zoom_out)
    ImageView mZoomOutBtn;

    @Bind(R.id.end_point)
    TextView mEndPoint;





    private int mTrafficOpened;
    private NaviLatLng startPoint = null;
    private NaviLatLng endPoint = null;
    public String duration;
    public String type;

    private static final float ROUTE_UNSELECTED_TRANSPARENCY = 0F;
    private static final float ROUTE_SELECTED_TRANSPARENCY = 1F;
    /**
     * 导航对象(单例)
     */
    private AMapNavi mAMapNavi;


    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    /**
     * 保存当前算好的路线
     */
    private SparseArray<EcoRouteOverLay> routeOverlays = new SparseArray<EcoRouteOverLay>();
    private SparseArray<EcoRouteOverLay> transRouteOverlays=new SparseArray<>();
    /*
            * strategyFlag转换出来的值都对应PathPlanningStrategy常量，用户也可以直接传入PathPlanningStrategy常量进行算路。
            * 如:mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList,PathPlanningStrategy.DRIVING_DEFAULT);
            */
    int strategyFlag = 0;
    private StrategyBean mStrategyBean;
    private List <LatLng> mSourthWestLatLngs=new ArrayList<>();
    private List<LatLng> mNorthEastLatLngs=new ArrayList<>();


    GeocodeSearch mGeocodeSearch;
    private LatLng mChooseLatLng;
    private Marker mAddPointMaker;
    private ArrayList<String> mWayPointStrings=new ArrayList<>();
    private float currentZoom=14;
    private float maxZoom=18;
    private float minZoom=3;
    private AMapLocation mCurLocation;

    private boolean congestion, cost, hightspeed, avoidhightspeed;
    private boolean mIsNewCalculate;


    public static RoutePlanFragment getInstance(Bundle bundle) {

        RoutePlanFragment mFragment = new RoutePlanFragment();
        mFragment.setArguments(bundle);

        return mFragment;
    }



    private int timeOutSeconds;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_AUTO_NAVI:
                    timeOutSeconds++;
                    if (timeOutSeconds < 10) {
                        if (mTimeTextview != null) {
                            mTimeTextview.setText(String.valueOf(10 - timeOutSeconds)+"s");
                        }
                        this.sendEmptyMessageDelayed(MSG_AUTO_NAVI, 1000);
                    } else {
                        this.removeMessages(MSG_AUTO_NAVI);
                        timeOutSeconds = 0;
                        replaceFragmentByNavi();
                    }
                    break;


            }
        }
    };

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_route_plan, null);
        } else {
            view = inflater.inflate(R.layout.fragment_route_plan_l, null);

        }
        MapCfg.routFragmentOpen=true;
        ButterKnife.bind(this, view);
        mTrafficOpened=CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_TRAFFIC_ON_OFF,1);
        if (isNetConnect) {

            initView();
            initMap();
            initPointData();
            initNavi();
        } else {
            ToastUtil.show(mContext, R.string.msg_no_net);
        }
        ((HomeActivity) mContext).isNavigating = true;
        if(activity == null) {
            activity = (HomeActivity) mContext;
            activity.showTitleBar(true);
        }
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        if(((HomeActivity)mContext).isDriving()){
            //车辆处于行驶中，需要隐藏按钮
            toyotaRule(true);
        }else{
            toyotaRule(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEvent(Integer i) {
        switch(i) {
            case Constant.DRIVING:
                toyotaRule(true);
                break;
            case Constant.NO_DRIVE:
                toyotaRule(false);
                break;
        }
    }

    private void initView() {
        if (mTrafficOpened==0){
            mTrafficView.setImageResource(R.mipmap.traffic_open_day);

        }else {
            mTrafficView.setImageResource(R.mipmap.traffic_close_day);
        }

        mRouteLayout.setVisibility(View.INVISIBLE);
        mWaitBar.setVisibility(View.VISIBLE);
        mStartNaviBtn.setOnClickListener(this);
        mStartNaviBtn.setEnabled(false);
        iv_back.setOnClickListener(this);
        mTrafficView.setOnClickListener(this);
        mRouteLineLayoutOne.setOnClickListener(this);
        mRouteLinelayoutTwo.setOnClickListener(this);
        mRouteLineLayoutThree.setOnClickListener(this);
        mZoomOutBtn.setOnClickListener(this);
        mZoomInBtn.setOnClickListener(this);
        mStrategySetBtn.setOnClickListener(this);
        mLocationBtn.setOnClickListener(this);

        mTimeTextview.setText("9s");
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 初始化AMap对象
     */
    private void initMap() {
        if (mAMap == null) {
            mAMap = mapView.getMap();
            mapView.onCreate(savedInstanceState);
            MyTrafficStyle myTrafficStyle=new MyTrafficStyle();
            myTrafficStyle.setSlowColor(mContext.getResources().getColor(R.color.route_slow_color));
            myTrafficStyle.setSmoothColor(mContext.getResources().getColor(R.color.route_smooth_color));
            myTrafficStyle.setCongestedColor(mContext.getResources().getColor(R.color.route_congested_color));
            myTrafficStyle.setSeriousCongestedColor(mContext.getResources().getColor(R.color.route_serious_congested_color));
            mAMap.setMyTrafficStyle(myTrafficStyle);
            mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    if (mTrafficOpened==0){
                        mAMap.setTrafficEnabled(true);
                    }else {
                        mAMap.setTrafficEnabled(false);
                    }
                    mAMap.setMapTextZIndex(3);
                    mAMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
                        @Override
                        public void onTouch(MotionEvent motionEvent) {
                            Trace.Debug("*******  map touch");
                            removeTimeCount();
                        }
                    });


                }
            });

            mAMap.getUiSettings().setZoomControlsEnabled(false);
            mAMap.setOnMapClickListener(this);
            mAMap.setOnMapLongClickListener(this);
            mAMap.setOnMarkerClickListener(this);
            mGeocodeSearch = new GeocodeSearch(mContext);
            mGeocodeSearch.setOnGeocodeSearchListener(this);
            mAMap.setOnInfoWindowClickListener(this);
            mAMap.setInfoWindowAdapter(this);


            locali_do();
        }

        /**
         * 互联车机地图夜间模式
         */
        getEcoApplication().getObservable().addObserver(this);
        if(HomeActivity.isThinCar){
            setMapType(AMap.MAP_TYPE_NIGHT);
        }

    }
    private void initPointData() {
        myAddr = getArguments().getString(ROUTEPLAN_START_ADDRESS);

        if (myAddr!=null){
            String[] stStrs = myAddr.split(",");
            if (stStrs.length > 0) {
                startPoint = new NaviLatLng(Double.parseDouble(stStrs[1]), Double.parseDouble(stStrs[2]));
            }
        }else{
            myAddr=EcoApplication.getInstance().getAddress()+","+EcoApplication.getInstance().getLatitude()+","+EcoApplication.getInstance().getLongitude();
            String[] stStrs = myAddr.split(",");
            if (stStrs.length > 0) {
                startPoint = new NaviLatLng(EcoApplication.getInstance().getLatitude(),EcoApplication.getInstance().getLongitude());
            }
        }

        enAddr = getArguments().getString(ROUTEPLAN_END_ADDRESS);
        CacheUtils.getInstance(mContext).putString(SettingCfg.NAVI_END_ADDRESS, enAddr);
        if (enAddr != null) {
            String[] stStrsEnd = enAddr.split(",");
            if (stStrsEnd.length > 0) {
                endPoint = new NaviLatLng(Double.parseDouble(stStrsEnd[1]), Double.parseDouble(stStrsEnd[2]));
                mEndPoint.setText(stStrsEnd[0]);
            }
        }
    }
    /**
     * 导航初始化
     */
    private void initNavi() {
        congestion=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_CONGESTION,false);
        cost=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_COST,false);
        avoidhightspeed=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_HIGHSPEED,false);
        hightspeed=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_PRIORITY_HIGHSPEED,false);
        mStrategyBean = new StrategyBean(congestion, cost, hightspeed, avoidhightspeed);
        startList.add(startPoint);
        endList.add(endPoint);
        mAMapNavi = AMapNavi.getInstance(mContext.getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        calculateDriveRoute();
    }
    /**
     * 驾车路径规划计算
     */
    private void calculateDriveRoute() {
        mHandler.removeMessages(MSG_AUTO_NAVI);
        mTimeTextview.setText("");
        try {
            strategyFlag = mAMapNavi.strategyConvert(mStrategyBean.isCongestion(), mStrategyBean.isCost(), mStrategyBean.isAvoidhightspeed(), mStrategyBean.isHightspeed(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(){
            @Override
            public void run() {
                mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag);
            }
        }.start();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        EventBus.getDefault().register(this);
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RoutePlanFragment");
        mapView.onResume();
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RoutePlanFragment");
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Trace.Debug("##### ondestory");
        EventBus.getDefault().unregister(this);
        getEcoApplication().getObservable().deleteObserver(this);

        cleanRouteOverlay();
        mapView.onDestroy();
        MapCfg.routFragmentOpen=false;
        mAMapNavi.removeAMapNaviListener(this);
        mAMapNavi=null;
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (mAMapNavi != null) {
//            mAMapNavi.destroy();
//            mamana
//        }
    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    ((HomeActivity) mContext).isNavigating = false;
                    if (EASY_STOP.equals(getArguments().getString(LAUNCH_FRAGMENT))) {
                        replaceFragmentByEasyStop();
                    } else {
                        replaceFragmentByMap();
                    }
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
//                handler.removeMessages(MSG_AUTO_NAVI); //ccy add  2016.2.25

                ((HomeActivity) mContext).isNavigating = false;
                if(EASY_STOP.equals(getArguments().getString(LAUNCH_FRAGMENT))){
                    replaceFragmentByEasyStop();
                }else{
                    replaceFragmentByMap();
                }
                break;
            case R.id.route_linelayout_one:
                mHandler.removeMessages(MSG_AUTO_NAVI);
                mTimeTextview.setText("");

                setMapToCenter();
                focuseRouteLine(true, false, false);
                break;
            case R.id.route_linelayout_two:
                mHandler.removeMessages(MSG_AUTO_NAVI);
                mTimeTextview.setText("");
                focuseRouteLine(false, true, false);

                setMapToCenter();
                break;
            case R.id.route_linelayout_three:
                mHandler.removeMessages(MSG_AUTO_NAVI);
                mTimeTextview.setText("");
                focuseRouteLine(false, false, true);

                setMapToCenter();
                break;
            case R.id.rl_start_nav:
                replaceFragmentByNavi();

                break;
            case R.id.traffic:
                if (mTrafficOpened==0){
                    mTrafficOpened=1;
                    mTrafficView.setImageResource(R.mipmap.traffic_close_day);
                    mAMap.setTrafficEnabled(false);
                }else {
                    mTrafficOpened=0;
                    mTrafficView.setImageResource(R.mipmap.traffic_open_day);
                    mAMap.setTrafficEnabled(true);
                }
                CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_TRAFFIC_ON_OFF,mTrafficOpened);

                break;
            case R.id.location:
                mAMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurLocation.getLatitude(),mCurLocation.getLongitude())));
                break;
            case R.id.zoom_out:
                currentZoom=mAMap.getCameraPosition().zoom;
                if(currentZoom<maxZoom) {
                    currentZoom = currentZoom + 1;
                    mAMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom));
//                    mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLocation.getLatitude(),mCurLocation.getLongitude()), currentZoom));
                }

                break;
            case R.id.zoom_in:
                currentZoom=mAMap.getCameraPosition().zoom;
                if(currentZoom>minZoom){
                    currentZoom = currentZoom - 1;
                    mAMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom));
//                    mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLocation.getLatitude(),mCurLocation.getLongitude()), currentZoom));
                }
                break;
            case R.id.strategy_set:
                StrategyChooseDialog chooseDialog=new StrategyChooseDialog(mContext,R.style.Dialog);
                chooseDialog.setStrategyChangeListener(new StrategyChooseDialog.StrategyChangeListener() {
                    @Override
                    public void getCurrentStrategy(StrategyBean bean) {
                        if (bean.equals(mStrategyBean)){
                            return;
                        }else {
                            mStrategyBean=bean;
                            mIsNewCalculate=true;
                            calculateDriveRoute();
                        }
                    }
                });
                chooseDialog.show();

                break;

            default:
                break;
        }
    }

    private void setMapToCenter() {
        LatLng sourthWest = getSourthWestLatlng();
        LatLng northEast = getNorthEastLatlng();
        if (sourthWest != null && northEast != null) {
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(sourthWest, northEast), 150));
        }
    }


    private void replaceFragmentByMap() {
        Bundle nBundle = new Bundle();
        MapFragment secondFragment = MapFragment.getInstance(nBundle);

        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment,MapFragment.class.getSimpleName()).commitAllowingStateLoss();
    }
    private void replaceFragmentByEasyStop(){
        Bundle nBundle = new Bundle();
        EasyStopFragment secondFragment = EasyStopFragment.getInstance(nBundle);
        ((HomeActivity) mContext).setEasyStopFragment(secondFragment);
        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.easy_stop_frame, secondFragment).commitAllowingStateLoss();

    }

    private void replaceFragmentByNavi() {
        EventBus.getDefault().post(new CloseVoiceEvent());
        mHandler.removeMessages(MSG_AUTO_NAVI);
        Bundle nBundle = new Bundle();
        nBundle.putBoolean(NaviFragment.EMUTE,mEmuteCheck.isChecked());
        nBundle.putStringArrayList(NaviFragment.WAY_POINT,mWayPointStrings);
        NaviFragment secondFragment = NaviFragment.getInstance(nBundle);
        secondFragment.setArguments(nBundle);
        LetvReportUtils.reportNavigationStart(myAddr,enAddr,duration,type);
        if(null!=mContext) {
            Fragment navi = ((HomeActivity) mContext).getSupportFragmentManager().findFragmentByTag(NaviFragment.class.getSimpleName());
            if(navi != null){
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().remove(navi).commitAllowingStateLoss();
            }
            if(EASY_STOP.equals(getArguments().getString(LAUNCH_FRAGMENT))){
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.easy_stop_frame, secondFragment,NaviFragment.class.getSimpleName()).commitAllowingStateLoss();
            }else{
                if (HomeActivity.isThinCar) {
                    HomeActivity.isNotifyCar=false;
                }

                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment,NaviFragment.class.getSimpleName()).commitAllowingStateLoss();
            }

        }

    }



    private void locali_do() {
        double la = EcoApplication.getInstance().getLatitude();
        double lo =  EcoApplication.getInstance().getLongitude();
        currentZoom=14;
        if(mAMap != null) {
            mAMap.clear();
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(la, lo), currentZoom));
        }
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
        if (!mIsNewCalculate){
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_NAVI,1000);
        }
        cleanRouteOverlay();
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        for (int i = 0; i < ints.length; i++) {
            AMapNaviPath path = paths.get(ints[i]);
            if (path != null) {
                drawRoutes(ints[i], path);
            }
        }
        setRouteLineTag(paths, ints);
        mStartNaviBtn.setEnabled(true);
        mWaitBar.setVisibility(View.GONE);
        mRouteLayout.setVisibility(View.VISIBLE);
        LatLng centerLatLng= mAMap.getCameraPosition().target;
        Trace.Debug("#### centerlatlng "+centerLatLng.longitude+"  "+centerLatLng.longitude);
        setMapToCenter();
//        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mAMap.getCameraPosition().target,mAMap.getCameraPosition().zoom-0.5f));
        if (ints.length==1){
            mRouteLineLayoutOne.setVisibility(View.VISIBLE);

            if (GlobalCfg.IS_POTRAIT){
                mRouteLineLayoutOne.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) mRouteTextStrategyOne.getLayoutParams();
                layoutParams.leftMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_20dp);
                layoutParams.rightMargin=0;
                layoutParams.topMargin=0;
                layoutParams.bottomMargin=0;
                mRouteTextStrategyOne.setLayoutParams(layoutParams);

                LinearLayout.LayoutParams layoutParams2= (LinearLayout.LayoutParams) mRouteTextTimeOne.getLayoutParams();
                layoutParams2.leftMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_20dp);
                layoutParams2.rightMargin=0;
                layoutParams2.topMargin=0;
                layoutParams2.bottomMargin=0;
                mRouteTextTimeOne.setLayoutParams(layoutParams2);
                LinearLayout.LayoutParams layoutParams3= (LinearLayout.LayoutParams) mRouteTextDistanceOne.getLayoutParams();
                layoutParams3.leftMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_20dp);
                layoutParams3.rightMargin=0;
                layoutParams3.topMargin=0;
                layoutParams3.bottomMargin=0;
                mRouteTextDistanceOne.setLayoutParams(layoutParams3);
                mRouteTextStrategyOne.setVisibility(View.GONE);

                mRouteLineLayoutOne.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                mRouteLinelayoutTwo.setVisibility(View.GONE);
                mRouteLineLayoutThree.setVisibility(View.GONE);

                mDiver1.setVisibility(View.GONE);
                mDiver2.setVisibility(View.GONE);
            }else{
                mRouteLinelayoutTwo.setVisibility(View.INVISIBLE);
                mRouteLineLayoutThree.setVisibility(View.INVISIBLE);
                mDiver1.setVisibility(View.INVISIBLE);
                mDiver2.setVisibility(View.INVISIBLE);}

        }else if (ints.length==2){
            mRouteLineLayoutOne.setVisibility(View.VISIBLE);
            mRouteLinelayoutTwo.setVisibility(View.VISIBLE);
            mDiver1.setVisibility(View.VISIBLE);
            if (GlobalCfg.IS_POTRAIT){
                mRouteLineLayoutOne.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) mRouteTextStrategyOne.getLayoutParams();
                layoutParams.leftMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams.rightMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams.topMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams.bottomMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                mRouteTextStrategyOne.setLayoutParams(layoutParams);

                LinearLayout.LayoutParams layoutParams2= (LinearLayout.LayoutParams) mRouteTextTimeOne.getLayoutParams();
                layoutParams2.leftMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams2.rightMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams2.topMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams2.bottomMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                mRouteTextTimeOne.setLayoutParams(layoutParams2);
                LinearLayout.LayoutParams layoutParams3= (LinearLayout.LayoutParams) mRouteTextDistanceOne.getLayoutParams();
                layoutParams3.leftMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams3.rightMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams3.topMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams3.bottomMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                mRouteTextDistanceOne.setLayoutParams(layoutParams3);


                mRouteTextStrategyOne.setVisibility(View.VISIBLE);
                mRouteLineLayoutOne.setGravity(Gravity.CENTER);
                mRouteLineLayoutThree.setVisibility(View.GONE);
                mDiver2.setVisibility(View.GONE);
            }else{
                mRouteLineLayoutThree.setVisibility(View.INVISIBLE);
                mDiver2.setVisibility(View.INVISIBLE);
            }


        }else if (ints.length==3){
            if (GlobalCfg.IS_POTRAIT){
                mRouteLineLayoutOne.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) mRouteTextStrategyOne.getLayoutParams();
                layoutParams.leftMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams.rightMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams.topMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams.bottomMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                mRouteTextStrategyOne.setLayoutParams(layoutParams);

                LinearLayout.LayoutParams layoutParams2= (LinearLayout.LayoutParams) mRouteTextTimeOne.getLayoutParams();
                layoutParams2.leftMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams2.rightMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams2.topMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams2.bottomMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                mRouteTextTimeOne.setLayoutParams(layoutParams2);
                LinearLayout.LayoutParams layoutParams3= (LinearLayout.LayoutParams) mRouteTextDistanceOne.getLayoutParams();
                layoutParams3.leftMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams3.rightMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams3.topMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                layoutParams3.bottomMargin=mContext.getResources().getDimensionPixelOffset(R.dimen.size_3dp);
                mRouteTextDistanceOne.setLayoutParams(layoutParams3);
                mRouteLineLayoutOne.setOrientation(LinearLayout.VERTICAL);
                mRouteLineLayoutOne.setGravity(Gravity.CENTER);
                mRouteTextStrategyOne.setVisibility(View.VISIBLE);

            }
            mRouteLineLayoutOne.setVisibility(View.VISIBLE);
            mRouteLinelayoutTwo.setVisibility(View.VISIBLE);
            mRouteLineLayoutThree.setVisibility(View.VISIBLE);
            mDiver1.setVisibility(View.VISIBLE);
            mDiver2.setVisibility(View.VISIBLE);

        }
    }

    private LatLng getSourthWestLatlng() {
        if (mSourthWestLatLngs.size()<=0){
            return null;
        }

        double lat=mSourthWestLatLngs.get(0).latitude;
        double lng=mSourthWestLatLngs.get(0).longitude;
        for (int i = 0; i < mSourthWestLatLngs.size(); i++) {
            double curlat=mSourthWestLatLngs.get(i).latitude;
            if (curlat<lat){
                lat=curlat;
            }
            double curLng=mSourthWestLatLngs.get(i).longitude;
            if (curLng<lng){
                lng=curLng;
            }


        }
        return new LatLng(lat,lng);
    }
    private LatLng getNorthEastLatlng() {
        if (mNorthEastLatLngs.size()<=0){
            return null;
        }
        double lat=mNorthEastLatLngs.get(0).latitude;
        double lng=mNorthEastLatLngs.get(0).longitude;
        for (int i = 0; i < mNorthEastLatLngs.size(); i++) {
            double curlat=mNorthEastLatLngs.get(i).latitude;
            if (curlat>lat){
                lat=curlat;
            }
            double curLng=mNorthEastLatLngs.get(i).longitude;
            if (curLng>lng){
                lng=curLng;
            }
        }
        return new LatLng(lat,lng);
    }


    /**
     * 绘制路径规划结果
     *
     * @param routeId 路径规划线路ID
     * @param path    AMapNaviPath
     */

    private void drawRoutes(int routeId, AMapNaviPath path) {
        mAMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        mNorthEastLatLngs.add(path.getBoundsForPath().northeast);
        mSourthWestLatLngs.add(path.getBoundsForPath().southwest);
        path.getWayPoint();
        EcoRouteOverLay transRouteOverLay = new EcoRouteOverLay(mAMap, path, mContext);
        transRouteOverLay.setWayPointStrings(mWayPointStrings);
        RouteOverlayOptions  transRouteOverlayOptions=new RouteOverlayOptions();
        transRouteOverlayOptions.setArrowOnTrafficRoute(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_aolr).getBitmap());
        transRouteOverlayOptions.setNormalRoute(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_transparent).getBitmap());
        transRouteOverlayOptions.setUnknownTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_no_transpararent).getBitmap());
        transRouteOverlayOptions.setSmoothTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_green_transparent).getBitmap());
        transRouteOverlayOptions.setSlowTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_slow_transparent).getBitmap());
        transRouteOverlayOptions.setJamTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_bad_transparent).getBitmap());
        transRouteOverlayOptions.setVeryJamTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_grayred_transparent).getBitmap());
        transRouteOverlayOptions.setLineWidth(60f);
        transRouteOverLay.setRouteOverlayOptions(transRouteOverlayOptions);
        transRouteOverLay.setTrafficLine(true);
        transRouteOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.startpoint1));
        transRouteOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.endpoint1));
        transRouteOverLay.addToMap();

        transRouteOverlays.put(routeId, transRouteOverLay);
        EcoRouteOverLay routeOverLay = new EcoRouteOverLay(mAMap, path, mContext);
        routeOverLay.setWayPointStrings(mWayPointStrings);
        RouteOverlayOptions  routeOverlayOptions=new RouteOverlayOptions();
        routeOverlayOptions.setArrowOnTrafficRoute(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_aolr).getBitmap());
        routeOverlayOptions.setNormalRoute(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_transparent).getBitmap());
        routeOverlayOptions.setUnknownTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_no).getBitmap());
        routeOverlayOptions.setSmoothTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_green).getBitmap());
        routeOverlayOptions.setSlowTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_slow).getBitmap());
        routeOverlayOptions.setJamTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_bad).getBitmap());
        routeOverlayOptions.setVeryJamTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_grayred).getBitmap());
        routeOverlayOptions.setLineWidth(60f);
        routeOverLay.setRouteOverlayOptions(routeOverlayOptions);
        routeOverLay.setTrafficLine(true);
        routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.startpoint1));
        routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.endpoint1));
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);


    }




    /**
     * 路线tag选中设置
     *
     * @param lineOne
     * @param lineTwo
     * @param lineThree
     */
    private void focuseRouteLine(boolean lineOne, boolean lineTwo, boolean lineThree) {
        if (lineOne){
            setLinelayoutTwo(lineTwo);
            setLinelayoutThree(lineThree);
            setLinelayoutOne(lineOne);
        }else if (lineTwo){

            setLinelayoutOne(lineOne);
            setLinelayoutThree(lineThree);
            setLinelayoutTwo(lineTwo);

        }else{
            setLinelayoutOne(lineOne);
            setLinelayoutTwo(lineTwo);
            setLinelayoutThree(lineThree);
        }


    }



    private void cleanRouteOverlay() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            int key = routeOverlays.keyAt(i);
            EcoRouteOverLay overlay = routeOverlays.get(key);
            overlay.removeFromMap();
            overlay.destroy();
        }
        routeOverlays.clear();
        for (int i = 0; i < transRouteOverlays.size(); i++) {
            int key = transRouteOverlays.keyAt(i);
            EcoRouteOverLay overlay = transRouteOverlays.get(key);
            overlay.removeFromMap();
            overlay.destroy();
        }
        transRouteOverlays.clear();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mAddPointMaker!=null){
            mAddPointMaker.remove();
            mAddPointMaker=null;
        }


        int key=getRouteClickIndex(latLng);
        switch (key){
            case 0:
                mHandler.removeMessages(MSG_AUTO_NAVI);
                focuseRouteLine(true, false, false);
                break;
            case 1:
                mHandler.removeMessages(MSG_AUTO_NAVI);
                focuseRouteLine(false, true, false);
                break;
            case 2:
                mHandler.removeMessages(MSG_AUTO_NAVI);
                focuseRouteLine(false, false, true);
                break;
        }

    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mAddPointMaker!=null){
            mAddPointMaker.remove();
            mAddPointMaker=null;
        }
        mChooseLatLng=latLng;
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latLng.latitude,latLng.longitude), 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mGeocodeSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求



    }


    @Override
    public boolean onMarkerClick(Marker marker) {


        return false;
    }




    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public View getInfoWindow(final Marker marker) {
        if (marker.getTitle().equals("-1")){
            View view=LayoutInflater.from(mContext).inflate(R.layout.way_point_add,null);
            TextView textView= (TextView) view.findViewById(R.id.point_name);
            ImageView imageView= (ImageView) view.findViewById(R.id.point_add);
            textView.setText(marker.getSnippet());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (wayList.size()>=3){
                        ToastUtil.show(mContext,"途经点最多设置三个");
                        return;

                    }
                    LatLng latLng=marker.getPosition();
                    wayList.add(new NaviLatLng(latLng.latitude,latLng.longitude));
                    mAddPointMaker.remove();
                    mAddPointMaker.destroy();
                    mAddPointMaker=null;
                    mWayPointStrings.add(marker.getSnippet());
                    mIsNewCalculate=true;
                    calculateDriveRoute();

                }
            });
            return view;
        }else if (marker.getTitle().equals("0")||marker.getTitle().equals("1")||marker.getTitle().equals("2")){
            View view=LayoutInflater.from(mContext).inflate(R.layout.way_point_delete,null);
            TextView textView= (TextView) view.findViewById(R.id.point_name);
            ImageView imageView= (ImageView) view.findViewById(R.id.point_delete);
            textView.setText(marker.getSnippet());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LatLng latLng=marker.getPosition();
                    wayList.remove(new NaviLatLng(latLng.latitude,latLng.longitude));
                    mWayPointStrings.remove(Integer.valueOf(marker.getTitle()));
                    mIsNewCalculate=true;
                    calculateDriveRoute();
                }
            });
            return view;
        }

        return null;
    }



    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int code) {
        if (code == 1000) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                RegeocodeAddress address = regeocodeResult.getRegeocodeAddress();
                Trace.Debug("### "+address.getFormatAddress());
                mAddPointMaker= mAMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi))
                        .position(mChooseLatLng) .title("-1")
                        .snippet(address.getFormatAddress())
                        .draggable(true));
                mAddPointMaker.showInfoWindow();
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mChooseLatLng));
            } else {
                ToastUtil.show(mContext, R.string.str_no_result);
            }
        } else if (code == 27) {
            ToastUtil.show(mContext,R.string.net_erro_toast);
        } else if (code == 32) {
            ToastUtil.show(mContext, R.string.str_key_erro);
            //mLocationDesTextView.setText("key无效");
        } else {
            ToastUtil.show(mContext,R.string.net_erro_toast);
            //mLocationDesTextView.setText(rCode);
        }


    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    private int getRouteClickIndex(LatLng latLng) {
        int x= (int) (mapView.getX()+1);
        int y= (int) (mapView.getY()+1);
        Point point=new Point(x,y);
        Point point1=new Point(x,y+80);
        LatLng latLng1 = mAMap.getProjection().fromScreenLocation(point);
        LatLng latLng2=mAMap.getProjection().fromScreenLocation(point1);
        int distance= (int) (AMapUtils.calculateLineDistance(latLng1,latLng2));

        int index=-1;
        for (int i = 0; i < routeOverlays.size(); i++) {
            int key = routeOverlays.keyAt(i);

            EcoRouteOverLay overlay = routeOverlays.get(key);
            List<NaviLatLng> naviLatLngs=overlay.getAMapNaviPath().getCoordList();

            for (NaviLatLng lng : naviLatLngs) {
//                if (Math.abs(lng.getLatitude()-latLng.latitude)<0.0008f&&Math.abs(lng.getLongitude()-latLng.longitude)<0.0008f){
//                    index=i;
//                    break;
//                }
//

                if (AMapUtils.calculateLineDistance(latLng,new LatLng(lng.getLatitude(),lng.getLongitude()))<distance){
                    index=i;
                    break;
                }

            }
        }
        return index;
    }

    /**
     * @param paths 多路线回调路线
     * @param ints  多路线回调路线ID
     */
    private void setRouteLineTag(HashMap<Integer, AMapNaviPath> paths, int[] ints) {
        if (ints.length < 1) {
            visiableRouteLine(false, false, false);
            return;
        }
        int indexOne = 0;
        String stragegyTagOne = Utils.getStrategyDes(paths, ints, indexOne, mStrategyBean);
        setLinelayoutOneContent(ints[indexOne], stragegyTagOne);
        if (ints.length == 1) {
            visiableRouteLine(true, false, false);
            focuseRouteLine(true, false, false);
            return;
        }

        int indexTwo = 1;
        String stragegyTagTwo = Utils.getStrategyDes(paths, ints, indexTwo, mStrategyBean);
        setLinelayoutTwoContent(ints[indexTwo], stragegyTagTwo);
        if (ints.length == 2) {
            visiableRouteLine(true, true, false);
            focuseRouteLine(true, false, false);
            return;
        }

        int indexThree = 2;
        String stragegyTagThree = Utils.getStrategyDes(paths, ints, indexThree, mStrategyBean);
        setLinelayoutThreeContent(ints[indexThree], stragegyTagThree);
        if (ints.length >= 3) {
            visiableRouteLine(true, true, true);
            focuseRouteLine(true, false, false);
        }

    }

    private void visiableRouteLine(boolean lineOne, boolean lineTwo, boolean lineThree) {
        setLinelayoutOneVisiable(lineOne);
        setLinelayoutTwoVisiable(lineTwo);
        setLinelayoutThreeVisiable(lineThree);
    }

    private void setLinelayoutOneVisiable(boolean visiable) {
        if (visiable) {
            mRouteLineLayoutOne.setVisibility(View.VISIBLE);
        } else {
            mRouteLineLayoutOne.setVisibility(View.INVISIBLE);
        }
    }

    private void setLinelayoutTwoVisiable(boolean visiable) {
        if (visiable) {
            mRouteLinelayoutTwo.setVisibility(View.VISIBLE);
        } else {
            mRouteLinelayoutTwo.setVisibility(View.INVISIBLE);
        }
    }

    private void setLinelayoutThreeVisiable(boolean visiable) {
        if (visiable) {
            mRouteLineLayoutThree.setVisibility(View.VISIBLE);
        } else {
            mRouteLineLayoutThree.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置第一条线路Tab 内容
     *
     * @param routeID  路线ID
     * @param strategy 策略标签
     */
    private void setLinelayoutOneContent(int routeID, String strategy) {
        mRouteLineLayoutOne.setTag(routeID);
        EcoRouteOverLay overlay = routeOverlays.get(routeID);
        AMapNaviPath path = overlay.getAMapNaviPath();
        mRouteTextStrategyOne.setText(strategy);
        String timeDes = Utils.getFriendlyTime(path.getAllTime());
        mRouteTextTimeOne.setText(timeDes);
        String disDes = Utils.getFriendlyDistance(path.getAllLength());
        mRouteTextDistanceOne.setText(disDes);
    }

    /**
     * 设置第二条路线Tab 内容
     *
     * @param routeID  路线ID
     * @param strategy 策略标签
     */
    private void setLinelayoutTwoContent(int routeID, String strategy) {
        mRouteLinelayoutTwo.setTag(routeID);
        EcoRouteOverLay overlay = routeOverlays.get(routeID);
        AMapNaviPath path = overlay.getAMapNaviPath();
        mRouteTextStrategyTwo.setText(strategy);
        String timeDes = Utils.getFriendlyTime(path.getAllTime());
        mRouteTextTimeTwo.setText(timeDes);
        String disDes = Utils.getFriendlyDistance(path.getAllLength());
        mRouteTextDistanceTwo.setText(disDes);
    }

    /**
     * 设置第三条路线Tab 内容
     *
     * @param routeID  路线ID
     * @param strategy 策略标签
     */
    private void setLinelayoutThreeContent(int routeID, String strategy) {
        mRouteLineLayoutThree.setTag(routeID);
        EcoRouteOverLay overlay = routeOverlays.get(routeID);
        AMapNaviPath path = overlay.getAMapNaviPath();
        mRouteTextStrategyThree.setText(strategy);
        String timeDes = Utils.getFriendlyTime(path.getAllTime());
        mRouteTextTimeThree.setText(timeDes);
        String disDes = Utils.getFriendlyDistance(path.getAllLength());
        mRouteTextDistanceThree.setText(disDes);
    }

    /**
     * 第一条路线是否focus
     *
     * @param focus focus为true 突出颜色显示，标示为选中状态，为false则标示非选中状态
     */
    private void setLinelayoutOne(boolean focus) {
        if (mRouteLineLayoutOne.getVisibility() != View.VISIBLE) {
            return;
        }
        try {
            int routeID = (int) mRouteLineLayoutOne.getTag();
            EcoRouteOverLay overlay = routeOverlays.get(routeID);
            EcoRouteOverLay transRouteOverLay=transRouteOverlays.get(routeID);
            if (focus) {
                mAMapNavi.selectRouteId(routeID);

//                focusOverLay(overlay);
                overlay.setTransparency(ROUTE_SELECTED_TRANSPARENCY);
                transRouteOverLay.setTransparency(ROUTE_UNSELECTED_TRANSPARENCY);
//                overlay.removeFromMap();
//                overlay.addToMap();
                overlay.setZindex(2);
                mRouteLineLayoutOne.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_10));
                mRouteTextStrategyOne.setTextColor(mContext.getResources().getColor(R.color.white));
                mRouteTextTimeOne.setTextColor(mContext.getResources().getColor(R.color.white));
                mRouteTextDistanceOne.setTextColor(mContext.getResources().getColor(R.color.white));
            } else {
                overlay.setTransparency(ROUTE_UNSELECTED_TRANSPARENCY);
                transRouteOverLay.setTransparency(ROUTE_SELECTED_TRANSPARENCY);
                transRouteOverLay.setZindex(0);
//                unFocusOverLay(overlay);
                overlay.setTransparency(ROUTE_UNSELECTED_TRANSPARENCY);
                mRouteLineLayoutOne.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_5));
                mRouteTextStrategyOne.setTextColor(mContext.getResources().getColor(R.color.half_white));
                mRouteTextDistanceOne.setTextColor(mContext.getResources().getColor(R.color.half_white));
                mRouteTextTimeOne.setTextColor(mContext.getResources().getColor(R.color.half_white));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 第二条路线是否focus
     *
     * @param focus focus为true 突出颜色显示，标示为选中状态，为false则标示非选中状态
     */
    private void setLinelayoutTwo(boolean focus) {
        if (mRouteLinelayoutTwo.getVisibility() != View.VISIBLE) {
            return;
        }
        try {
            int routeID = (int) mRouteLinelayoutTwo.getTag();
            EcoRouteOverLay overlay = routeOverlays.get(routeID);
            EcoRouteOverLay transRouteOverLay=transRouteOverlays.get(routeID);

            if (focus) {
                mRouteLinelayoutTwo.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_10));
                mRouteTextStrategyTwo.setTextColor(mContext.getResources().getColor(R.color.white));
                mRouteTextTimeTwo.setTextColor(mContext.getResources().getColor(R.color.white));
                mRouteTextDistanceTwo.setTextColor(mContext.getResources().getColor(R.color.white));
                mAMapNavi.selectRouteId(routeID);
//                focusOverLay(overlay);
//                overlay.removeFromMap();
                overlay.setZindex(2);

                overlay.setTransparency(ROUTE_SELECTED_TRANSPARENCY);
                transRouteOverLay.setTransparency(ROUTE_UNSELECTED_TRANSPARENCY);
//                overlay.setTrafficLine(true);

            } else {
                mRouteLinelayoutTwo.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_5));
                mRouteTextStrategyTwo.setTextColor(mContext.getResources().getColor(R.color.half_white));
                mRouteTextDistanceTwo.setTextColor(mContext.getResources().getColor(R.color.half_white));
                mRouteTextTimeTwo.setTextColor(mContext.getResources().getColor(R.color.half_white));
//                unFocusOverLay(overlay);
                transRouteOverLay.setZindex(0);
                overlay.setTransparency(ROUTE_UNSELECTED_TRANSPARENCY);
                transRouteOverLay.setTransparency(ROUTE_SELECTED_TRANSPARENCY);
//                overlay.setTrafficLine(false);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第三条路线是否focus
     *
     * @param focus focus为true 突出颜色显示，标示为选中状态，为false则标示非选中状态
     */
    private void setLinelayoutThree(boolean focus) {
        if (mRouteLineLayoutThree.getVisibility() != View.VISIBLE) {
            return;
        }
        try {
            int routeID = (int) mRouteLineLayoutThree.getTag();
            EcoRouteOverLay overlay = routeOverlays.get(routeID);
            EcoRouteOverLay transRouteOverLay=transRouteOverlays.get(routeID);
            if (overlay == null) {
                return;
            }
            if (focus) {
                mRouteLineLayoutThree.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_10));
                mRouteTextStrategyThree.setTextColor(mContext.getResources().getColor(R.color.white));
                mRouteTextTimeThree.setTextColor(mContext.getResources().getColor(R.color.white));
                mRouteTextDistanceThree.setTextColor(mContext.getResources().getColor(R.color.white));
                mAMapNavi.selectRouteId(routeID);
                overlay.setZindex(2);
//                overlay.removeFromMap();
//                focusOverLay(overlay);
                overlay.setTransparency(ROUTE_SELECTED_TRANSPARENCY);
                transRouteOverLay.setTransparency(ROUTE_UNSELECTED_TRANSPARENCY);

            } else {
                mRouteLineLayoutThree.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_5));
                mRouteTextStrategyThree.setTextColor(mContext.getResources().getColor(R.color.half_white));
                mRouteTextDistanceThree.setTextColor(mContext.getResources().getColor(R.color.half_white));
                mRouteTextTimeThree.setTextColor(mContext.getResources().getColor(R.color.half_white));
                overlay.setTransparency(ROUTE_UNSELECTED_TRANSPARENCY);
                transRouteOverLay.setTransparency(ROUTE_SELECTED_TRANSPARENCY);
                transRouteOverLay.setZindex(0);
//                overlay.setTrafficLine(false);

//                unFocusOverLay(overlay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        super.onLocationChanged(aMapLocation);
        mCurLocation=aMapLocation;
    }

    /**
     * 单路径算路成功回调
     */

    @Override
    public void onCalculateRouteSuccess() {

    }




    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }




    @Override
    public void onCalculateRouteFailure(int i) {
        Toast.makeText(mContext.getApplicationContext(),"错误码"+i,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }


    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(currentZoom >= maxZoom){
            mZoomOutBtn.setImageResource(R.mipmap.map_zoom_out_grey);
        }
        else if(currentZoom <= minZoom){
            mZoomInBtn.setImageResource(R.mipmap.map_zoom_in_grey);
        }
        else{
            mZoomOutBtn.setImageResource(R.mipmap.map_zoom_out);
            mZoomInBtn.setImageResource(R.mipmap.map_zoom_in);
        }

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        currentZoom= cameraPosition.zoom;

    }

    public void removeTimeCount() {
        mTimeTextview.setText("");
        mHandler.removeMessages(MSG_AUTO_NAVI);
    }


//    @Override
//    public void onPlayRing(int i) {
//
//    }
@Override
public void update(Observable observable, Object data) {
    if((Integer)data == LinkCarConnectStatusObservable.CONNECT){
        setMapType(AMap.MAP_TYPE_NIGHT);
    }else{
        setMapType(AMap.MAP_TYPE_NORMAL);
    }
}

    /**
     * 设置地图类型
     * @param mapType
     */
    public void setMapType(final int mapType){
        if(mHandler!=null){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mAMap!=null){
                        mAMap.setMapType(mapType);
                    }
                }
            }, 1500);
        }
    }

    public void setZoomOut(boolean b) {
        if (mAMap!=null){
            if (b){
                mAMap.animateCamera(CameraUpdateFactory.zoomOut());
            }else {
                mAMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        }
    }

    /**
     *
     * 丰田致炫的走行规制
     *   行驶过程中，地图页面按钮不能触控
     * @param tag true表示在行驶中, false表示非行驶
     */
    private void toyotaRule(boolean tag){
        if(tag){
            mLocationBtn.setVisibility(View.GONE);
            mStrategySetBtn.setVisibility(View.GONE);
            mTrafficView.setVisibility(View.GONE);
            mZoomInBtn.setVisibility(View.GONE);
            mZoomOutBtn.setVisibility(View.GONE);
        }else{
            mLocationBtn.setVisibility(View.VISIBLE);
            mStrategySetBtn.setVisibility(View.VISIBLE);
            mTrafficView.setVisibility(View.VISIBLE);
            mZoomInBtn.setVisibility(View.VISIBLE);
            mZoomOutBtn.setVisibility(View.VISIBLE);
        }
    }
//    @Override
//    public void onPlayRing(int i) {
//
//    }
}
