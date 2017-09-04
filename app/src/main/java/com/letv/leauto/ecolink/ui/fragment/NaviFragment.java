package com.letv.leauto.ecolink.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyTrafficStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AMapTrafficStatus;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.DirectionView;
import com.amap.api.navi.view.NextTurnTipView;
import com.amap.api.navi.view.ZoomInIntersectionView;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.routepoisearch.RoutePOIItem;
import com.amap.api.services.routepoisearch.RoutePOISearch;
import com.amap.api.services.routepoisearch.RoutePOISearch.RoutePOISearchType;
import com.amap.api.services.routepoisearch.RoutePOISearchQuery;
import com.amap.api.services.routepoisearch.RoutePOISearchResult;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.gps.GPSListenerImp;
import com.letv.leauto.ecolink.gps.GPSLocationManager;
import com.letv.leauto.ecolink.lemap.NaviTTSController;
import com.letv.leauto.ecolink.lemap.entity.SearchPoi;
import com.letv.leauto.ecolink.lemap.navi.Utils;
import com.letv.leauto.ecolink.lemap.navi.routebean.StrategyBean;
import com.letv.leauto.ecolink.thincar.ThincarGestureProcessor;
import com.letv.leauto.ecolink.thincar.protocol.NaviBarSendHelp;
import com.letv.leauto.ecolink.thincar.protocol.NaviInfoSendHelp;
import com.letv.leauto.ecolink.thincar.view.LandTrafficBarView;
import com.letv.leauto.ecolink.thincar.view.ThincarTurnView;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.NaviSettingDialog;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.dialog.NaviEndDialog;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.view.DeleteDataDialog;
import com.letv.leauto.ecolink.ui.view.EcoZoomButtonView;
import com.letv.leauto.ecolink.ui.view.MyTrafficBar;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.MyAnimationDrawable;
import com.letv.leauto.ecolink.utils.TimeUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.amap.api.services.routepoisearch.RoutePOISearch.RoutePOISearchType.TypeATM;
import static com.amap.api.services.routepoisearch.RoutePOISearch.RoutePOISearchType.TypeGasStation;
import static com.amap.api.services.routepoisearch.RoutePOISearch.RoutePOISearchType.TypeMaintenanceStation;
import static com.amap.api.services.routepoisearch.RoutePOISearch.RoutePOISearchType.TypeToilet;

/**
 * Created by liweiwei1 on 2015/12/23.
 */
public class NaviFragment extends BaseFragment implements AMapNaviListener, AMapNaviViewListener,
        View.OnClickListener, AMap.OnCameraChangeListener,RoutePOISearch.OnRoutePOISearchListener ,AMap.OnMarkerClickListener, Observer{

    //        private int[] customIconTypes = new int[] {R.mipmap.caricon,R.mipmap.sou1,R.mipmap.sou2,R.mipmap.sou3,R.mipmap.sou4,R.mipmap.sou5,R.mipmap.sou6,R.mipmap.sou7,R.mipmap.sou8,R.mipmap.sou9,R.mipmap.sou10,R.mipmap.sou11,R.mipmap.sou12,R.mipmap.sou13,R.mipmap.sou14,R.mipmap.sou15,R.mipmap.sou16};
    private int[] customIconTypes = {R.mipmap.caricon,
            R.mipmap.caricon, R.mipmap.sou2, R.mipmap.sou3,
            R.mipmap.sou4, R.mipmap.sou5, R.mipmap.sou6, R.mipmap.sou7,
            R.mipmap.sou8, R.mipmap.sou9, R.mipmap.sou10,
            R.mipmap.sou11, R.mipmap.sou12, R.mipmap.sou13,
            R.mipmap.sou14, R.mipmap.sou15, R.mipmap.sou16,
            R.mipmap.sou17, R.mipmap.sou18, R.mipmap.sou19,
    };
    public static final int CHANG_VIEW = 0;
    public static final int EXIT_PREVIEW = 1;
    public static final int LOCK = 2;
    public static final String  EMUTE="navi_emute";
    private static final int SPEED = 3;
    private static final int PARK_CLOSE = 4;
    public static final String WAY_POINT = "way_point";
    private static final int WAY_POINT_CLOSE = 5;
    private boolean mIsNorthUp;
    private boolean isDiaLogShow = false;//Dialog只弹一次

    private AMapNaviViewOptions mNaviViewOption;
    public static  final  int NAVI_SETTING_REQUST=0X11;

    private int mDayNightMode; /*导航模式 0白天1：夜间 2：自动*/
    private int mTrafficOnOff; /*实时路况 0开启，1关闭*/
    private static NaviFragment mThis;

    @Bind(R.id.navi_view)
    AMapNaviView mNaviView;
    @Bind(R.id.direction_view)
    DirectionView mDirectionView;//指北针
    @Bind(R.id.zoom_view)
    EcoZoomButtonView mZoomButtonView;//大小按钮
    @Bind(R.id.myTrafficBar)
    MyTrafficBar mTrafficBar;//光柱
    @Bind(R.id.setting_view)
    ImageView mSettingView;
    @Bind(R.id.preview_img)
    ImageView mNaviPreViewImg;
    @Bind(R.id.exit_navi_image)
    ImageView mExitNaviImage;
    @Bind(R.id.navi_speed_img)
    ImageView mSpeedImg;
    @Bind(R.id.navi_speed_text)
    TextView mSpeedTextView;
    @Bind(R.id.speed_unit)
    TextView mSpeedUnit;

    @Bind(R.id.gps_img)
    ImageView mGpsImageView;
    @Bind(R.id.gps_num)
    TextView mGpsNumTV;
    @Bind(R.id.next_turn_view)
    NextTurnTipView mNextTurnTipView;//下一路口
    @Bind(R.id.next_road_name)
    TextView mNextRoadName;
    @Bind(R.id.turn_rest_distance)
    TextView mNextTurnDistanceView;
    @Bind(R.id.turn_rest_unit)
    TextView mNextTurnRestUnit;


    @Bind(R.id.cross_gps_img)
    ImageView mGpsImageViewCross;
    @Bind(R.id.cross_gps_num)
    TextView mGpsNumTVCross;
    @Bind(R.id.cross_next_turn_view)
    NextTurnTipView mNextTurnTipViewCross;//下一路口
    @Bind(R.id.cross_next_road_name)
    TextView mNextRoadNameCross;
    @Bind(R.id.cross_turn_rest_distance)
    TextView mNextTurnDistanceViewCross;
    @Bind(R.id.cross_turn_rest_unit)
    TextView mNextTurnRestUnitCross;

    @Bind(R.id.common_road_layout)
    RelativeLayout mCommonRoadmessagelayout;
    @Bind(R.id.cross_road_layout)
    RelativeLayout mCrossRoadMessageLayout;
    @Bind(R.id.cross_view)
    ZoomInIntersectionView zoomInIntersectionView;
    @Bind(R.id.rest_predict_layout)
    RelativeLayout mRestPredictLayout;
    @Bind(R.id.rest_layout)
    RelativeLayout mRestLayout;
    @Bind(R.id.predict_layout)
    RelativeLayout mPredictLayout;
    @Bind(R.id.rest_time)
    TextView mRestTime;
    @Bind(R.id.rest_distance)
    TextView mRestDistance;
    @Bind(R.id.predict_time)
    TextView mPredictTime;
    @Bind(R.id.predict_distance)
    TextView mPredictDistance;
    @Bind(R.id.continue_navi)
    TextView mContinueNavi;

    @Bind(R.id.predict_title)
    TextView mPredictTitle;
    @Bind(R.id.rest_title)
    TextView mRestTitle;
    @Bind(R.id.traffic)
    ImageView mTrafficView;
    @Bind(R.id.parking_layout)
    RelativeLayout mParkLayout;

    @Bind(R.id.park1)
    TextView mPark1;
    @Bind(R.id.park2)
    TextView mPark2;
    @Bind(R.id.park3)
    TextView mPark3;
    @Bind(R.id.diver1)
    LinearLayout mDiver1;
    @Bind(R.id.diver2)
    LinearLayout mDiver2;
    @Bind(R.id.park_cancel_time)
    TextView mParkCancleTime;
    @Bind(R.id.parkname)
    TextView mParkName;
    @Bind(R.id.park_distance)
    TextView mParkDistance;
    @Bind(R.id.park_here)
    TextView mParkHereTextView;
    @Bind(R.id.cancel_park)
    LinearLayout mCancelParkLayout;
    @Bind(R.id.way_point_layout)
    RelativeLayout mWayPointLayout;
    @Bind(R.id.way_point_name)
    TextView mWayPointName;
    @Bind(R.id.way_point_distance)
    TextView mWayPointDistance;
    @Bind(R.id.way_point_setbutton)
    TextView mWayPointSetButton;

    RelativeLayout mPortBottomlayout;
    @Bind(R.id.test)
    Button mTestButton;
    @Bind(R.id.main_layout)
    RelativeLayout main_layout;

    private RelativeLayout mNaviLayout;
    private LinearLayout mNaviSpeedLayout;
    private ThincarTurnView halfTurnView;
    private LandTrafficBarView landTrafficBar;

    AMapNavi mNavi;
    NaviTTSController mTTsManager;
    Bundle savedInstanceState;

    private float maxZoom;
    private float minZoom;

    private float mCurrentZoom;
    boolean mIsPreView = false;
    /**
     * 到达目的地
     */
    public boolean mIsArrive;

    private View mThincarCover;
    private ImageView mConnectBackImage;
    private GPSLocationManager mGpsManager;
    private AMap mAmap;
    private AMapNaviPath mNaviPath;
    private List<AMapTrafficStatus> mTrafficStatuses;
    private boolean mIsLocked;
    private long mSpeedStartTime;
    private long mRetainDistance;
    private boolean mIsThincarHalfMode = false;
    private volatile int mLimitSpeed;
    private int halfMapHeight;
    private int backLayoutHeight;


    private boolean mShowRestLayout;
    private boolean congestion, cost, hightspeed, avoidhightspeed;

    private  int mStrategy;


    private float mTotalDistance,mTotalTime,mAverageSpeed,mHighestSpeed;

    private boolean mParkingShowed;
    private PoiItem mParkPoiItem;
    private  AMapNaviLocation mCurLocation;

    private List<String> mWayPointStrings;


    private int timeOutSeconds;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what){
                case CHANG_VIEW:
                    if(mShowRestLayout){
                        showPredictView();
                    }
                    else{
                        showRestView();
                    }
                    this.sendEmptyMessageDelayed(CHANG_VIEW, 5000);
                    break;

                case EXIT_PREVIEW:
                    setImageWhenStopPreview();
                    mNaviView.recoverLockMode();
                    NaviBarSendHelp.getInstance().notifyStopPreview();
                    mIsPreView = false;
                    break;
                case LOCK:
                    mZoomButtonView.setVisibility(View.GONE);
                    mDirectionView.setVisibility(View.GONE);

                    break;
                case SPEED:
//                    mSpeedTextView.setText("0KM");
                    break;
                case PARK_CLOSE:
                    timeOutSeconds++;
                    if (timeOutSeconds < 10) {
                        mParkCancleTime.setText(String.valueOf(10 - timeOutSeconds)+"s");

                        this.sendEmptyMessageDelayed(PARK_CLOSE, 1000);
                    } else {
                        this.removeMessages(PARK_CLOSE);
                        timeOutSeconds = 0;
                        mParkLayout.setVisibility(View.GONE);
                    }
                    break;
                case WAY_POINT_CLOSE:
                    setWayPointLayoutGone();
                    break;
            }

        }
    };
    private volatile boolean mEmuteIsOpen;
    private StrategyBean mStrategyBean;
    private RoutePoiOverlay mRoutePoiOverlay;
    private ArrayList<PoiItem> mCarParkItems;
    private List<NaviLatLng> mWayPoints;
    private NaviLatLng mStartPoint;
    private NaviLatLng mEndPoint;
    private WayPointOverLay mWayPointOverLay;
    private NaviLatLng mNewSetWayPoint;
    private NaviInfo mNaviInfo;
    private long mStartTime;
    private long mEndTime;
    private int mCrossBitmapType;
    private NaviEndDialog naviEndDialog;
    private boolean firstshow=true;
    private NaviSettingDialog naviSettingDialog;


    public static NaviFragment getInstance(Bundle bundle) {
        NaviFragment  mFragment = new NaviFragment();
        mThis = mFragment;
        mFragment.setArguments(bundle);
        return mFragment;
    }

    public static NaviFragment getThis(){
        return mThis;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
    private void showPredictView(){
        mShowRestLayout = false;
        mPredictLayout.setVisibility(View.VISIBLE);
        mRestLayout.setVisibility(View.GONE);

    }

    /**
     * 初始化剩余 和预估界面
     */
    private void showRestView(){
        mShowRestLayout = true;
        mPredictLayout.setVisibility(View.GONE);
        mRestLayout.setVisibility(View.VISIBLE);

    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;

        ((HomeActivity)getActivity()).showTitleBar(false);
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_navi_p, null);
            mPortBottomlayout= (RelativeLayout) view.findViewById(R.id.navi_port_bottom_layout);
            mNaviLayout = (RelativeLayout) view.findViewById(R.id.mNaviLayout);
            mNaviSpeedLayout = (LinearLayout) view.findViewById(R.id.navi_speed_layout);
            halfTurnView = (ThincarTurnView)view.findViewById(R.id.half_turnview);
            landTrafficBar = (LandTrafficBarView)view.findViewById(R.id.landTrafficBar);
        } else {
            view = inflater.inflate(R.layout.fragment_navi, null);
        }

        ButterKnife.bind(this, view);

        mTTsManager = NaviTTSController.getInstance(mContext);

        if (HomeActivity.isThinCar) {
//            mExitNaviImage.setVisibility(View.GONE);
            initThincarView();
        }


        setImageWhenStopPreview();
        startGPSListen();
//        mNextTurnTipView.setCustomIconTypes(mContext.getResources(),customIconTypes);
        if (halfTurnView!=null){
            halfTurnView.setCustomIconTypes(mContext.getResources(),customIconTypes);}
        showRestView();
        mHandler.sendEmptyMessageDelayed(CHANG_VIEW,5000);
        mIsNorthUp = false;
        mSettingView.setOnClickListener(this);
        mTrafficView.setOnClickListener(this);
        mZoomButtonView.getZoomInBtn().setOnClickListener(this);
        mZoomButtonView.getZoomOutBtn().setOnClickListener(this);
        mContinueNavi.setOnClickListener(this);
        mPark1.setOnClickListener(this);
        mPark2.setOnClickListener(this);
        mPark3.setOnClickListener(this);
        mWayPointSetButton.setOnClickListener(this);
        mParkHereTextView.setOnClickListener(this);
        mCancelParkLayout.setOnClickListener(this);
        mTestButton.setOnClickListener(this);
        mEmuteIsOpen=getArguments().getBoolean(EMUTE);
        mWayPointStrings=getArguments().getStringArrayList(WAY_POINT);
        mCrossRoadMessageLayout.setVisibility(View.GONE);
        mNaviPreViewImg.setVisibility(View.GONE);
        mNaviPreViewImg.setVisibility(View.GONE);
        mZoomButtonView.setVisibility(View.GONE);
        mDirectionView.setVisibility(View.GONE);
        mContinueNavi.setVisibility(View.GONE);
        mTrafficView.setVisibility(View.GONE);
        mSpeedImg.setImageResource(R.mipmap.limit_speed_blue);
        mSpeedTextView.setText("0");
        setUpMap();
        MapCfg.naviFragmentOpen=true;
        getEcoApplication().getObservable().addObserver(this);
        return view;
    }

    private void initThincarView() {
        halfMapHeight = (int)((double)ThinCarDefine.HALF_NAVI_CAR_HEIGHT * mPhoneCarRate);
        backLayoutHeight = ((HomeActivity)getActivity()).getRlMainHeight() - halfMapHeight;

        mThincarCover = LayoutInflater.from(mContext).inflate(R.layout.thincar_cover_layout, null);
        mConnectBackImage = (ImageView) mThincarCover.findViewById(R.id.thincar_anim_image);
    }


    private void setNaviViewOptions() {
        mNaviViewOption = mNaviView.getViewOptions();
        mNaviViewOption.setReCalculateRouteForYaw(true);//设置偏航时是否重新计算路径
        mNaviViewOption.setReCalculateRouteForTrafficJam(true);//前方拥堵时是否重新计算路径
        mNaviViewOption.setTrafficInfoUpdateEnabled(true);//设置交通播报是否打开
        mNaviViewOption.setCameraInfoUpdateEnabled(true);//设置摄像头播报是否打开
        mNaviViewOption.setScreenAlwaysBright(true);//设置导航状态下屏幕是否一直开启。
        mNaviViewOption.setMonitorCameraEnabled(true);//设置摄像头监控图标是否显示
//        mNaviViewOption.setMonitorCameraBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher));
        mNaviViewOption.setCrossDisplayShow(true);
        mNaviViewOption.setLayoutVisible(false);
        if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.NAVI_SCALE_OPEN,true)){
            mNaviViewOption.setAutoChangeZoom(true);
        }else {
            mNaviViewOption.setAutoChangeZoom(false);
        }
        mNaviViewOption.setAutoDrawRoute(true);
        mNaviViewOption.setTrafficBarEnabled(false);
        mNaviViewOption.setLockMapDelayed(10*1000);
        mNaviViewOption.setLeaderLineEnabled(mContext.getResources().getColor(R.color.leader_line));

        mNaviView.setLockZoom(17);
        RouteOverlayOptions routeOverlayOptions=mNaviViewOption.getRouteOverlayOptions();
        if (routeOverlayOptions==null){
            routeOverlayOptions=new RouteOverlayOptions();
        }

        routeOverlayOptions.setArrowOnTrafficRoute(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_aolr).getBitmap());
        routeOverlayOptions.setNormalRoute(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture).getBitmap());
        routeOverlayOptions.setUnknownTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_no).getBitmap());
        routeOverlayOptions.setSmoothTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_green).getBitmap());
        routeOverlayOptions.setSlowTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_slow).getBitmap());
        routeOverlayOptions.setJamTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_bad).getBitmap());
        routeOverlayOptions.setVeryJamTraffic(BitmapDescriptorFactory.fromResource(R.mipmap.navi_custtexture_grayred).getBitmap());
        routeOverlayOptions.setLineWidth(80f);

        mNaviViewOption.setRouteOverlayOptions(routeOverlayOptions);
        mNaviViewOption.setStartPointBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.startpoint1));
        mNaviViewOption.setEndPointBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.endpoint1));
        mNaviViewOption.setWayPointBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.transparent));

        mNaviViewOption.setPointToCenter(0.5, 0.66);
        mNaviView.setNaviMode(AMapNaviView.CAR_UP_MODE);
        mNaviViewOption.setTilt(0);

        Rect landRect=new Rect(0,mContext.getResources().getDimensionPixelSize(R.dimen.size_130dp),mContext.getResources().getDimensionPixelSize(R.dimen.size_220dp),DensityUtils.getScreenHeight(mContext)-mContext.getResources().getDimensionPixelSize(R.dimen.size_48dp));
        Rect portRect=new Rect(0,0,DensityUtils.getScreenWidth(mContext),mContext.getResources().getDimensionPixelSize(R.dimen.size_170dp));

        mNaviViewOption.setCrossLocation(landRect,portRect);


        mNaviView.setViewOptions(mNaviViewOption);
        mNaviView.setLazyNextTurnTipView(mNextTurnTipView);
        mNaviView.setLazyDirectionView(mDirectionView);
        mNaviView.setLazyZoomButtonView(mZoomButtonView);
        mNaviView.setLazyZoomInIntersectionView(zoomInIntersectionView);
        mNextTurnTipView.setDrawingCacheEnabled(true);
        mNextTurnTipViewCross.setDrawingCacheEnabled(true);
        mNaviView.getLazyDirectionView().setImageResource(R.mipmap.car_up_mode);
        mHandler.sendEmptyMessageDelayed(LOCK,10*1000);
        mDirectionView.setOnClickListener(this);


        mTrafficOnOff = CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_TRAFFIC_ON_OFF, 1);

//        mNaviView.setLazyTrafficBarView(mTrafficBar);



    }

    private void setUpMap() {
//        setMapCustomStyleFile(mContext);
        mNaviView.onCreate(savedInstanceState);
        mNaviView.setAMapNaviViewListener(this);
        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_ONGOING, 1);
        setNaviViewOptions();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        EventBus.getDefault().register(this);
        mStartTime = System.currentTimeMillis();
        MapCfg.mNaAciFragmentIsNaVi=true;
        MapCfg.mNaAciFragmentIsBackground=false;
    }

    public void stopNaviFromThincar() {
        HomeActivity activity = (HomeActivity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //如果当前设置dialog是show状态，需要cancel
                if(naviSettingDialog!=null && naviSettingDialog.isShowing()){
                    naviSettingDialog.cancel();
                    setNaviSettings();
                }
                if (!isDiaLogShow) {
                    showDialog();
                }
            }
        });
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
//        if (mIsNorthUp){
//            mDirectionView.setRotation(360-cameraPosition.bearing);
//        }

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Trace.Debug("##### finish ");
//        if (mIsPreView||mShowCross){
//
//        }else{
//            mNaviView.setLockZoom((int) cameraPosition.zoom);
//            mInitLockZoom=mNaviView.getLockZoom();
//            Trace.Debug("#### cross "+mInitLockZoom);
//
//        }


        mCurrentZoom = cameraPosition.zoom;

        if(mCurrentZoom >= maxZoom){
            mZoomButtonView.getZoomInBtn().setEnabled(false);

        }else{
            mZoomButtonView.getZoomInBtn().setEnabled(true);
        }
        if(mCurrentZoom <= minZoom){
            mZoomButtonView.getZoomOutBtn().setEnabled(false);

        }else{
            mZoomButtonView.getZoomOutBtn().setEnabled(true);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mNaviView.onResume();
        MobclickAgent.onPageStart("NaviFragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        if(naviSettingDialog!=null && naviSettingDialog.isShowing()){
            naviSettingDialog.cancel();
            setNaviSettings();
        }
        mNaviView.onPause();
        MobclickAgent.onPageEnd("NaviFragment");
//        if (!BaseActivity.isVoice) {
//            openGPS(mContext);
//        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mNaviView.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getEcoApplication().getObservable().deleteObserver(this);
        mThis = null;
        MapCfg.naviFragmentOpen=false;
        mNaviView.onDestroy();
        if(mNaviView.getMap() != null) {
            mNaviView.getMap().clear();
            mAmap = null;

        }
        if (mNavi!=null){
            mNavi.removeAMapNaviListener(this);
            mNavi.removeAMapNaviListener(mTTsManager);
            mNavi.stopNavi();
            mNavi.destroy();
            mNavi = null;
        }
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mTrafficStatuses != null) {
            mTrafficStatuses.clear();
        }
        if (mTrafficBar!=null){
            mTrafficBar.recycleResource();
            mTrafficBar = null;}

        if (landTrafficBar!=null){
            landTrafficBar.recycleResource();
            landTrafficBar = null;}
        if (mNextTurnTipViewCross != null) {
            mNextTurnTipViewCross.recycleResource();
            mNextTurnTipViewCross = null;
        }

        if (mNextTurnTipView!=null){
            mNextTurnTipView.recycleResource();
            mNextTurnTipView = null;
        }
        if (halfTurnView != null) {
            halfTurnView.recycleResource();
            halfTurnView = null;
        }

        mNaviPath = null;


        ((HomeActivity) mContext).isNavigating = false;
        ((HomeActivity) mContext).isInEasyStop = false;
        ((HomeActivity) mContext).isInMapFragment = false;

        if(mGpsManager != null) {
            mGpsManager.stop();
            mGpsManager = null;
        }
        mContext = null;
        removeListener();

        if (MapCfg.mNaAciFragmentIsNaVi){

            if (MapCfg.mNaAciFragmentIsBackground){
                //暂停计时
                MapCfg.mToTalTime+=(System.currentTimeMillis()- MapCfg.mStartTime);
            }
            LetvReportUtils.reportNavigationEnd((System.currentTimeMillis()- mStartTime)/1000+"s", mIsArrive +"", mStartTime +"",MapCfg.mToTalTime/1000+"s");
            MapCfg.mNaAciFragmentIsNaVi=false;
            MapCfg.mToTalTime=0;
            MapCfg.mStartTime=0;
        }

    }

    private void removeListener() {
        mSettingView.setOnClickListener(null);
        mZoomButtonView.getZoomInBtn().setOnClickListener(null);
        mZoomButtonView.getZoomOutBtn().setOnClickListener(null);
        mExitNaviImage.setOnClickListener(null);
        mNaviPreViewImg.setOnClickListener(null);
        mDirectionView.setOnClickListener(null);
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        mExitNaviImage.setOnClickListener(this);
        mNaviPreViewImg.setOnClickListener(this);
        mNaviPreViewImg.setImageResource(R.mipmap.navi_preview);

        if(((HomeActivity)mContext).isDriving()){
            //车辆处于行驶中，需要隐藏按钮
            toyotaRule(true);
        }else{
            toyotaRule(false);
        }
    }

    @Override
    public void onInitNaviFailure() {
        ToastUtil.show(mContext, R.string.str_navi_faild);
    }

    @Override
    public void onInitNaviSuccess() {
        Trace.Debug("#### 导航初始化成功");


    }

    @Override
    public void onStartNavi(int i) {

        Trace.Debug("###### onStartNavi i " + i);

    }

    @Override
    public void onTrafficStatusUpdate() {
        Trace.Debug("#####onTrafficStatusUpdate ");


    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        mCurLocation=aMapNaviLocation;
        Trace.Debug("#####onLocationChange ");

    }

    @Override
    public void onGetNavigationText(int i, String s) {
        Trace.Debug("####onGetNavigationText i " +i + s);
    }

    @Override
    public void onEndEmulatorNavi() {
        Trace.Debug("####onEndEmulatorNavi  " );
    }

    @Override
    public void onArriveDestination() {
        mIsArrive =true;
        mTotalDistance=mTotalDistance+mNaviPath.getAllLength();
        mTotalTime= (System.currentTimeMillis()-mStartTime)/1000/60;
        int averageSpeed= (int) ((mTotalDistance/1000)/(mTotalTime/60));
        if (MapCfg.mNaAciFragmentIsBackground){
            //暂停计时
            MapCfg.mToTalTime+=(System.currentTimeMillis()- MapCfg.mStartTime);
        }
        naviEndDialog=new NaviEndDialog(mContext,R.style.Dialog);
        naviEndDialog.setCloseListener(new NaviEndDialog.CloseListener() {
            @Override
            public void close() {
                naviEndDialog.dismiss();
                replaceFragmentByMap();

            }
        });
        naviEndDialog.show();
        naviEndDialog.setAllTime(mTotalTime+"");
        naviEndDialog.setDistance(Utils.getFriendlyDistance2((int) mTotalDistance));
        naviEndDialog.setAverageSpeed(averageSpeed+"");
        naviEndDialog.setHightSpeed(mHighestSpeed+"");
        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_ONGOING, 0);
        CacheUtils.getInstance(mContext).putString(SettingCfg.NAVI_END_ADDRESS, "");
        LetvReportUtils.reportNavigationEnd((System.currentTimeMillis()- mStartTime)/1000+"s", mIsArrive +"", mStartTime +"",MapCfg.mToTalTime/1000+"s");
        MapCfg.mNaAciFragmentIsNaVi=false;
        MapCfg.mToTalTime=0;
        MapCfg.mStartTime=0;
        mIsArrive =false;
        NaviBarSendHelp.getInstance().responseNotNaviingDirect();
        NaviBarSendHelp.getInstance().requestNaviBarInfo();
        if (halfTurnView!=null){
            halfTurnView.setVisibility(View.GONE);}
        Trace.Debug("##### onArriveDestination  ");
    }




    @Override
    public void onCalculateRouteSuccess() {
        if (mEmuteIsOpen){
            mNavi.startNavi(NaviType.EMULATOR);
        }else{
            mNavi.startNavi(NaviType.GPS);//GPSNaviMode
        }
        mNaviPath=mNavi.getNaviPath();
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        StringBuffer sb=new StringBuffer();
        switch(i){
            case 2:
                sb.append(mContext.getString(R.string.navi_net_timeout_or_fiald));
                break;
            case 6:
                sb.append(mContext.getString(R.string.navi_end_address_erro));
                break;
            case 11:
                sb.append(mContext.getString(R.string.navi_no_endaddress));
                break;
            case 10:
                sb.append(mContext.getString(R.string.navi_no_startaddress));

                break;
            case 12:
                sb.append(mContext.getString(R.string.navi_no_rout_point));
                break;
            case 4:
                sb.append(mContext.getString(R.string.navi_protocle_erro));
                break;
            case 3:
                sb.append(mContext.getString(R.string.navi_start_address_erro));

                break;
            case 16:
                sb.append(mContext.getString(R.string.navi_permissions));

                break;
            case 18:
                sb.append(mContext.getString(R.string.navi_request_parameter_illegal));

                break;
            case 13:
                sb.append(mContext.getString(R.string.navi_user_illegal));

                break;
            case 17:
                sb.append(mContext.getString(R.string.navi_request_exceeded));

                break;
            case 14:
                sb.append(mContext.getString(R.string.navi_request_unexit));

                break;
            case 15:
                sb.append(mContext.getString(R.string.navi_request_res_erro));

                break;
            case 1:
                sb.append(mContext.getString(R.string.navi_rute_sucess));

                break;
            case 19:
                sb.append(mContext.getString(R.string.navi_unkown_erro));

                break;
        }


        ToastUtil.show(mContext, sb);
        Trace.Debug("#####onCalculateRouteFailure: " + sb + ",code:" + i);

//        11-路径计算错误异常：终点没有找到道路
//        static int 	ERROR_NOROADFORSTARTPOINT
//        10-路径计算错误异常：起点没有找到道路
//        static int 	ERROR_NOROADFORWAYPOINT
//        12-路径计算错误异常：途径点没有找到道路
//        static int 	ERROR_PROTOCOL
//        4-路径计算错误异常：协议解析错误
//        static int 	ERROR_STARTPOINT
//        3-路径计算错误异常：起点错误
//        static int 	INSUFFICIENT_PRIVILEGES
//        16-无权限访问此服务
//        static int 	INVALID_PARAMS
//        18-请求参数非法
//        static int 	INVALID_USER_KEY
//        13-用户key非法或过期
//        static int 	OVER_QUOTA
//        17-请求超出配额
//        static int 	SERVICE_NOT_EXIST
//        14-请求服务不存在
//        static int 	SERVICE_RESPONSE_ERROR
//        15-请求服务响应错误
//        static int 	SUCCESS_ROUTE
//        1-路径计算成功
//        static int 	UNKNOWN_ERROR
//        19-未知错误


    }

    @Override
    public void onReCalculateRouteForYaw() {
        Trace.Debug("##### onReCalculateRouteForYaw");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        Trace.Debug("##### onReCalculateRouteForTrafficJam");
    }

    @Override
    public void onArrivedWayPoint(int i) {
        Trace.Debug("##### onArrivedWayPoint");
    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        Trace.Debug("##### onGpsOpenStatus");
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {
        Trace.Debug("##### onNaviInfoUpdated");
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {
        mLimitSpeed=aMapNaviCameraInfos[0].getCameraSpeed();
        Trace.Debug("#### ");

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        mNaviInfo=naviInfo;

        StringBuffer buffer = new StringBuffer();
        buffer.append(mContext.getString(R.string.navi_currant_road) + naviInfo.getCurrentRoadName());
        buffer.append(mContext.getString(R.string.navi_next_road) + naviInfo.getNextRoadName());
        buffer.append(mContext.getString(R.string.navi_residual_distance) + naviInfo.getPathRetainDistance());
        buffer.append(mContext.getString(R.string.navi_residual_time)+ naviInfo.getPathRetainTime());
        buffer.append(mContext.getString(R.string.navi_steering_icon) + naviInfo.getIconType());
        buffer.append(mContext.getString(R.string.navi_last_residual_distance) + naviInfo.getCurStepRetainDistance());
        Trace.Debug("#####onNaviInfoUpdate" + buffer.toString());




        GlobalCfg.LEFT_DISTANCE=DensityUtils.convertMeter2KM(naviInfo.getPathRetainDistance());
        GlobalCfg.LEFT_TIME=DensityUtils.convertSec2Min(naviInfo.getPathRetainTime());

        if (halfTurnView!=null){
            halfTurnView.setIconType(naviInfo.getIconType());}
        NaviInfoSendHelp.getInstance().sendHudInfoToCar(naviInfo);
        NaviBarSendHelp.getInstance().updateNaviInfo(naviInfo);
        mNextRoadName.setText(naviInfo.getNextRoadName());
        mNextRoadNameCross.setText(naviInfo.getNextRoadName());
        if (naviInfo.getCurrentSpeed()>=0){
            mSpeedTextView.setText(naviInfo.getCurrentSpeed()+"");}
        mRestTime.setText(DensityUtils.convertSec2Min(naviInfo.getPathRetainTime()));
        mPredictTime.setText(getCurrentTime(naviInfo.getPathRetainTime()));
        mRestDistance.setText(DensityUtils.convertMeter2KM(naviInfo.getPathRetainDistance()));
        if (naviInfo.getCurStepRetainDistance() > 1000) {
            mNextTurnDistanceView.setText(Utils.getFriendlyDistance2(naviInfo.getCurStepRetainDistance())+"");
            mNextTurnDistanceViewCross.setText(Utils.getFriendlyDistance2(naviInfo.getCurStepRetainDistance())+"");
            mNextTurnRestUnit.setText("公里后");
            mNextTurnRestUnitCross.setText("公里后");
            if (halfTurnView != null) {
                halfTurnView.updateText(DensityUtils.convertMeter2KM_2(naviInfo.getCurStepRetainDistance())," 公里");
            }
        } else {
            mNextTurnDistanceView.setText(naviInfo.getCurStepRetainDistance()+"");
            mNextTurnDistanceViewCross.setText(naviInfo.getCurStepRetainDistance()+"");
            mNextTurnRestUnit.setText("米后");
            mNextTurnRestUnitCross.setText("米后");
            if (halfTurnView != null) {
                halfTurnView.updateText(naviInfo.getCurStepRetainDistance() + ""," 米");
            }
        }


        if (naviInfo.getCurStepRetainDistance()==0){
            mNextTurnDistanceView.setText("");
            mNextTurnDistanceViewCross.setText("");
            mNextTurnRestUnit.setText("");
            mNextTurnRestUnitCross.setText("");
            if (halfTurnView != null) {
                halfTurnView.updateText("","");
            }
        }
        mTrafficStatuses=mNavi.getTrafficStatuses(mNaviPath.getAllLength()-naviInfo.getPathRetainDistance(),mNaviPath.getAllLength());
        mTrafficBar.update(mTrafficStatuses,mNaviPath.getAllLength(),naviInfo.getPathRetainDistance());
        if (landTrafficBar!=null){
            landTrafficBar.update(mTrafficStatuses,mNaviPath.getAllLength(),naviInfo.getPathRetainDistance());
        }
        if (naviInfo.getCurrentSpeed()>mHighestSpeed){
            mHighestSpeed=naviInfo.getCurrentSpeed();
        }

        /** 瘦车机半屏模式不显示竖光柱 */
        if (mIsLocked && !mIsThincarHalfMode){
            mTrafficBar.setVisibility(View.VISIBLE);
        }
        if (!mParkingShowed){
            if (naviInfo.getPathRetainDistance()<500){
                NaviLatLng endLatLng=mNaviPath.getEndPoint();
                LatLonPoint latLonPoint=new LatLonPoint(endLatLng.getLatitude(),endLatLng.getLongitude());
                doPoiSearch("停车场",latLonPoint,null);

            }
        }

    }


    private  void  doPoiSearch(String newText, LatLonPoint mCenterPoint, final RoutePOISearchType type){
        if (newText.length() > 0) {

            mParkingShowed=true;
            SearchPoi searchPoi=new SearchPoi("0","0",newText);
            searchPoi.setType(SearchPoi.SEARCH);

            if (mCenterPoint != null) {
                PoiSearch.Query mPoiQuery = new PoiSearch.Query(newText, "", null);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
                PoiSearch mPoiSearch = new PoiSearch(mContext, mPoiQuery);
                mPoiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() { /*** 附近的加油站/银行搜索*/
                @Override
                public void onPoiSearched(PoiResult result, int rCode) {

                    if (rCode == 1000) {
                        String queryString=result.getQuery().getQueryString();

                        if (result != null && result.getQuery() != null) {
                            if (queryString.equals("停车场")){
                                // 搜索poi的结果
                                // 取得搜索到的poiitems有多少页
                                mCarParkItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                                if (mCarParkItems != null && mCarParkItems.size() > 0) {
                                    mParkLayout.setVisibility(View.VISIBLE);
                                    mHandler.sendEmptyMessageDelayed(PARK_CLOSE,1000);
                                    if (mCarParkItems.size()==1){
                                        mPark1.setVisibility(View.VISIBLE);
                                        mPark2.setVisibility(View.GONE);
                                        mPark3.setVisibility(View.GONE);
                                        mDiver1.setVisibility(View.GONE);
                                        mDiver2.setVisibility(View.GONE);

                                    }else if (mCarParkItems.size()==2){
                                        mPark1.setVisibility(View.VISIBLE);
                                        mPark2.setVisibility(View.VISIBLE);
                                        mPark3.setVisibility(View.GONE);
                                        mDiver1.setVisibility(View.VISIBLE);
                                        mDiver2.setVisibility(View.GONE);

                                    }else {
                                        mPark1.setVisibility(View.VISIBLE);
                                        mPark2.setVisibility(View.VISIBLE);
                                        mPark3.setVisibility(View.VISIBLE);
                                        mDiver1.setVisibility(View.VISIBLE);
                                        mDiver2.setVisibility(View.VISIBLE);

                                    }
                                    focuseRouteLine(true,false,false);

                                } else {
                                    ToastUtil.show(mContext, getResources().getString(R.string.map_no_result));
                                }
                            }else {
                                ArrayList<PoiItem> poiItems=result.getPois();
                                ArrayList<RoutePOIItem> routePOIItems=new ArrayList<RoutePOIItem>();


                                if (poiItems != null && poiItems.size() > 0) {
                                    for (PoiItem poiItem : poiItems) {
                                        RoutePOIItem routePOIItem=new RoutePOIItem();
                                        routePOIItem.setTitle(poiItem.getTitle());
                                        routePOIItem.setPoint(poiItem.getLatLonPoint());
                                        routePOIItem.setDistance(poiItem.getDistance());
                                        routePOIItems.add(routePOIItem);
                                    }
                                    if (mRoutePoiOverlay != null) {
                                        mRoutePoiOverlay.removeFromMap();
                                    }
                                    mRoutePoiOverlay = new RoutePoiOverlay(mContext,mAmap, routePOIItems,type);
                                    mRoutePoiOverlay.addToMap();
                                    startPreview();
                                }



                            }

                        }

                    } else if (rCode == 27) {
                        Trace.Debug("#####"+ mContext.getString(R.string.map_error_network));
                    } else if (rCode == 32) {
                        Trace.Debug("#####"+  mContext.getString(R.string.map_error_network));
                    } else {
                        Trace.Debug("#####"+  mContext.getString(R.string.map_error_network));
                    }

                }

                    @Override
                    public void onPoiItemSearched(PoiItem poiItem, int i) {
                        String address = poiItem.getSnippet();
                    }
                });
                mPoiSearch.setBound(new PoiSearch.SearchBound(mCenterPoint, 3000, true));
                // 设置搜索区域为以lp点为圆心，其周围2000米范围
                mPoiSearch.searchPOIAsyn();// 异步搜索
            }
        }
    }

    private void focuseRouteLine(boolean lineOne, boolean lineTwo, boolean lineThree) {
        mHandler.removeMessages(PARK_CLOSE);
        mHandler.sendEmptyMessageDelayed(PARK_CLOSE,1000);
        mParkCancleTime.setText("10s");
        timeOutSeconds=0;
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
    /**
     * 第一条路线是否focus
     *
     * @param focus focus为true 突出颜色显示，标示为选中状态，为false则标示非选中状态
     */
    private void setLinelayoutOne(boolean focus) {
        if (mPark1.getVisibility() != View.VISIBLE) {
            return;
        }
        if (focus) {
            mParkPoiItem= mCarParkItems.get(0);
            mParkName.setText(mParkPoiItem.getTitle());
            LatLng startLatLng=new LatLng(mParkPoiItem.getLatLonPoint().getLatitude(),mParkPoiItem.getLatLonPoint().getLongitude());
            LatLng endLatLng=new LatLng(mNaviPath.getEndPoint().getLatitude(),mNaviPath.getEndPoint().getLongitude());
            mParkDistance.setText("距终点"+Utils.getDistance(startLatLng,endLatLng));
            mPark1.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            mPark1.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            mPark1.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            mPark1.setTextColor(mContext.getResources().getColor(R.color.black));

        }


    }


    /**
     * 第二条路线是否focus
     *
     * @param focus focus为true 突出颜色显示，标示为选中状态，为false则标示非选中状态
     */
    private void setLinelayoutTwo(boolean focus) {
        if (mPark2.getVisibility() != View.VISIBLE) {
            return;
        }

        if (focus) {
            mPark2.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            mPark2.setTextColor(mContext.getResources().getColor(R.color.white));
            mParkPoiItem= mCarParkItems.get(1);
            mParkName.setText(mParkPoiItem.getTitle());
            LatLng startLatLng=new LatLng(mParkPoiItem.getLatLonPoint().getLatitude(),mParkPoiItem.getLatLonPoint().getLongitude());
            LatLng endLatLng=new LatLng(mNaviPath.getEndPoint().getLatitude(),mNaviPath.getEndPoint().getLongitude());
            mParkDistance.setText("距终点"+Utils.getDistance(startLatLng,endLatLng));
        } else {
            mPark2.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            mPark2.setTextColor(mContext.getResources().getColor(R.color.black));

        }

    }

    /**
     * 第三条路线是否focus
     *
     * @param focus focus为true 突出颜色显示，标示为选中状态，为false则标示非选中状态
     */
    private void setLinelayoutThree(boolean focus) {
        if (mPark3.getVisibility() != View.VISIBLE)
            return;

        if (focus) {
            mParkPoiItem= mCarParkItems.get(2);
            mParkName.setText(mParkPoiItem.getTitle());

            LatLng startLatLng=new LatLng(mParkPoiItem.getLatLonPoint().getLatitude(),mParkPoiItem.getLatLonPoint().getLongitude());
            LatLng endLatLng=new LatLng(mNaviPath.getEndPoint().getLatitude(),mNaviPath.getEndPoint().getLongitude());
            mParkDistance.setText("距终点"+Utils.getDistance(startLatLng,endLatLng));
            mPark3.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            mPark3.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            mPark3.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            mPark3.setTextColor(mContext.getResources().getColor(R.color.black));

        }

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {


    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {


    }




    private static String getCurrentTime(int leaveSecond){
        long time=System.currentTimeMillis();
        time = time + leaveSecond*1000;
        final Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int hour=mCalendar.get(Calendar.HOUR_OF_DAY);
        int min =mCalendar.get(Calendar.MINUTE);
        StringBuffer sb = new StringBuffer();
        if(hour<10) {
            sb.append("0"+ hour);
        }
        else {;
            sb.append(hour+"");
        }
        sb.append(":");

        if(min <10){
            sb.append("0"+min );
        }
        else{
            sb.append(min +"");
        }

        return sb.toString();
    }
    private int mInitLockZoom;
    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        mCrossBitmapType=aMapNaviCross.getPicFormat();
        mCommonRoadmessagelayout.setVisibility(View.GONE);
        if (!mIsThincarHalfMode) {
            mCrossRoadMessageLayout.setVisibility(View.VISIBLE);
        }
        mNextTurnTipViewCross.setImageBitmap(mNextTurnTipView.getDrawingCache());
        mNaviView.setLazyNextTurnTipView(mNextTurnTipViewCross);


    }

    @Override
    public void hideCross() {
        mCommonRoadmessagelayout.setVisibility(View.VISIBLE);
        mCrossRoadMessageLayout.setVisibility(View.GONE);
        mNextTurnTipView.setImageBitmap(mNextTurnTipViewCross.getDrawingCache());
        mNaviView.setLazyNextTurnTipView(mNextTurnTipView);

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {
        Trace.Debug("######showLaneInfo");
    }

    @Override
    public void hideLaneInfo() {
        Trace.Debug("######hideLaneInfo");
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
        mNavi.selectRouteId(ints[ints.length-1]);
        if (mEmuteIsOpen){
            mNavi.startNavi(NaviType.EMULATOR);
        }else{
            mNavi.startNavi(NaviType.GPS);//GPSNaviMode
        }
        mNaviPath=mNavi.getNaviPath();
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

//    @Override
//    public void onPlayRing(int i) {
//
//    }


    private void replaceFragmentByMap() {

        //导航结束通知
        if (HomeActivity.isThinCar) {
            HomeActivity.isNotifyCar = true;//防止重复通知显示导航栏
            EventBus.getDefault().post(Constant.HIDE_BOTTOM_BAR);
            DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.CONTROL_LIGHT_AVN_PARAM, ThinCarDefine.ProtocolToCarAction.SHOW_BOTTOM_BAR, 0);

        } else {
            EventBus.getDefault().post(Constant.SHOW_BOTTOM_BAR);
        }


        Bundle nBundle = new Bundle();
        MapFragment secondFragment = MapFragment.getInstance(nBundle);
        if (getFragmentManager() != null) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.map_frame, secondFragment,MapFragment.class.getSimpleName());
            transaction.commitAllowingStateLoss();

        }
    }

    private void replaceFragmentByEasyStop(){
        Bundle nBundle = new Bundle();
        EasyStopFragment secondFragment = EasyStopFragment.getInstance(nBundle);
        if (getFragmentManager() != null) {
            ((HomeActivity) mContext).setEasyStopFragment(secondFragment);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.easy_stop_frame, secondFragment);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 界面右下角功能设置按钮的回调接口。
     */
    @Override
    public void onNaviSetting() {
        Trace.Debug( "#### onNaviSetting");
    }

    /**
     * 导航页面左下角返回按钮点击后弹出的『退出导航对话框』中选择『确定』后的回调接口。
     */
    @Override
    public void onNaviCancel() {
        Trace.Debug("##### 停止导航");
        ((HomeActivity) mContext).isNavigating = false;
        replaceFragmentByMap();
//        showDialog();
    }

    /**
     * @return
     * 导航页面左下角返回按钮的回调接口 false-由SDK主动弹出『退出导航』对话框，true-SDK不主动弹出『退出导航对话框』，由用户自定义
     */
    @Override
    public boolean onNaviBackClick() {
        /** 车机连接状态下，忽略返回键 */
        if (!HomeActivity.isThinCar) {
            showDialog();
        }
        return true;
    }

    @Override
    public void onNaviMapMode(int i) {
        Trace.Debug("######onNaviMapMode");
    }

    @Override
    public void onNaviTurnClick() {
        Trace.Debug("######onNaviTurnClick");
    }

    /**
     * 界面下一道路名称的点击回调。
     */
    @Override
    public void onNextRoadClick() {
        Trace.Debug("######onNextRoadClick");
    }

    /**
     * 界面全览按钮的点击回调
     */
    @Override
    public void onScanViewButtonClick() {
        Trace.Debug("####### onScanViewButtonClick");
    }

    /**
     * @param lock
     * 是否锁定地图的回调
     */
    @Override
    public void onLockMap(boolean lock) {
        mIsLocked=lock;
        Trace.Debug("###### onLockMap "+lock);
        mHandler.removeMessages(LOCK);

        //处于行驶模式中的时候忽略此功能
        if(((HomeActivity)mContext).isDriving()){
            return;
        }

        /** 瘦车机半屏模式下，忽略此事件*/
        if (mIsThincarHalfMode) {
            return;
        }

        if (lock){
            mNaviPreViewImg.setVisibility(View.GONE);
            mZoomButtonView.setVisibility(View.GONE);
            mDirectionView.setVisibility(View.GONE);
            mContinueNavi.setVisibility(View.GONE);
            mTrafficView.setVisibility(View.GONE);
            mTrafficBar.setVisibility(View.VISIBLE);
            mRestPredictLayout.setVisibility(View.VISIBLE);
            mSpeedImg.setVisibility(View.VISIBLE);
            mSpeedTextView.setVisibility(View.VISIBLE);
            mSpeedUnit.setVisibility(View.VISIBLE);




        }else{
            if (mCrossBitmapType!=1){
                mCrossRoadMessageLayout.setVisibility(View.GONE);
                mCommonRoadmessagelayout.setVisibility(View.VISIBLE);
            }
            mNaviPreViewImg.setVisibility(View.VISIBLE);
            mZoomButtonView.setVisibility(View.VISIBLE);
            mDirectionView.setVisibility(View.VISIBLE);
            mTrafficBar.setVisibility(View.GONE);
            mContinueNavi.setVisibility(View.VISIBLE);
            mRestPredictLayout.setVisibility(View.GONE);
            mTrafficView.setVisibility(View.VISIBLE);
            mSpeedImg.setVisibility(View.GONE);
            mSpeedUnit.setVisibility(View.GONE);
            mSpeedTextView.setVisibility(View.GONE);


        }
    }

    /**
     * 导航view加载完成回调
     */
    @Override
    public void onNaviViewLoaded() {
        mAmap = mNaviView.getMap();
        mThincarGestureProcessor = new ThincarGestureProcessor(mAmap,mPhoneCarRate);
        mNaviView.getMap().getUiSettings().setScaleControlsEnabled(true);
        setNaviSettings();

//        setNightForThincar();

//        mAmap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
//            @Override
//            public void onMapLoaded() {
//
//                setAutoMapMode();
//            }
//        });


        mAmap.setOnCameraChangeListener(this);
        mAmap.setOnMarkerClickListener(this);
        maxZoom = mAmap.getMaxZoomLevel();
        minZoom = mAmap.getMinZoomLevel();
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAmap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        mAmap.getUiSettings().setScaleControlsEnabled(true);
        mAmap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
        mAmap.getUiSettings().setLogoBottomMargin(-1000);


        mNavi = AMapNavi.getInstance(mContext.getApplicationContext());
        mNavi.addAMapNaviListener(this);
        mNavi.addAMapNaviListener(mTTsManager);
        mNavi.setEmulatorNaviSpeed(150);
        mInitLockZoom=mNaviView.getLockZoom();
        mNaviPath=mNavi.getNaviPath();
        mRetainDistance=mNaviPath.getAllLength();
        mTrafficStatuses=mNavi.getTrafficStatuses(0,mNaviPath.getAllLength());
        mTrafficBar.update(mTrafficStatuses,mNaviPath.getAllLength(),mNaviPath.getAllLength());
        congestion=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_CONGESTION,false);
        cost=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_COST,false);
        avoidhightspeed=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_HIGHSPEED,false);
        hightspeed=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_PRIORITY_HIGHSPEED,false);
        mStrategyBean = new StrategyBean(congestion, cost, hightspeed, avoidhightspeed);
        mStrategy=mNavi.strategyConvert(mStrategyBean.isCongestion(), mStrategyBean.isCost(), mStrategyBean.isAvoidhightspeed(), mStrategyBean.isHightspeed(), true);
        mStartPoint=mNavi.getNaviPath().getStartPoint();
        mEndPoint=mNavi.getNaviPath().getEndPoint();
        mWayPoints = mNaviPath.getWayPoint();
        if (mWayPoints!=null){
            Trace.Debug("#### point size="+mWayPoints.size());
            mWayPointOverLay =new WayPointOverLay(mContext,mAmap, mWayPoints);
            mWayPointOverLay.addToMap();}
        if (mEmuteIsOpen){
            mNavi.startNavi(NaviType.EMULATOR);
        }else{
            mNavi.startNavi(NaviType.GPS);//GPSNaviMode
        }

//        if (HomeActivity.isThinCar) {
//            setMapNightTime();
//        }
        Trace.Debug("##### 导航view加载完成");
    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                if(!((HomeActivity) mContext).isPopupWindowShow) {
                    onNaviBackClick();
                }
                break;
            case Constant.KEYCODE_DPAD_CENTER:
                if ( MapCfg.mNaAciFragmentIsNaVi){
                    LetvReportUtils.reportNavigationEnd((System.currentTimeMillis()- mStartTime)/1000+"s", mIsArrive +"", mStartTime +"",MapCfg.mToTalTime/1000+"s");
                    MapCfg.mNaAciFragmentIsNaVi=false;
                    MapCfg.mToTalTime=0;
                    MapCfg.mStartTime=0;
                }
                ((HomeActivity) mContext).isNavigating = false;
                CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_ONGOING, 0);
                mTTsManager.stop();
                if(RoutePlanFragment.EASY_STOP.equals(getArguments().getString(RoutePlanFragment.LAUNCH_FRAGMENT))){
                    replaceFragmentByEasyStop();
                }else{
                    replaceFragmentByMap();
                }
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_navi_image:
                showDialog();
                break;
            case R.id.preview_img:
                if (!mIsPreView) {
                    startPreview();
                } else {
                    stopPreview();
                }
                break;


            case R.id.mNaviLayout:
                //  Log.i(TAG, "onClick: ");
                break;
            case R.id.setting_view:
                naviSettingDialog=new NaviSettingDialog(mContext,R.style.Dialog);
                naviSettingDialog.setSettingChangeListener(new NaviSettingDialog.NaviSettingChangeListener() {
                    @Override
                    public void settingChange() {
                        setNaviSettings();
                    }
                });
                naviSettingDialog.setStrategyChangeListener(new NaviSettingDialog.StrategyChangeListener() {
                    @Override
                    public void getCurrentStrategy(StrategyBean bean, int type) {
                        if (bean.equals(mStrategyBean)){
                            switch (type){
                                case MapCfg.MAINTENCE:
                                    searchRoutePOI(TypeMaintenanceStation);

                                    break;
                                case MapCfg.WC:
                                    searchRoutePOI(TypeToilet);
                                    break;
                                case MapCfg.GAS:
                                    searchRoutePOI(TypeGasStation);
                                    break;
                                case MapCfg.ATM:
                                    searchRoutePOI(TypeATM);
                                    break;
                            }

                        }else{
                            mStrategyBean=bean;
                            int strategyFlag = mNavi.strategyConvert(mStrategyBean.isCongestion(), mStrategyBean.isCost(), mStrategyBean.isAvoidhightspeed(), mStrategyBean.isHightspeed(), false);
                            ArrayList<NaviLatLng> startNaviLatLngs=new ArrayList<>();
                            startNaviLatLngs.add(mStartPoint);
                            ArrayList<NaviLatLng> endNaviLatLngs = new ArrayList<>();
                            endNaviLatLngs.add(mEndPoint);
                            mNavi.calculateDriveRoute(startNaviLatLngs,endNaviLatLngs,mWayPoints,strategyFlag);
                        }

                    }


                });

                naviSettingDialog.show();
//                Intent intent = new Intent(activity, NaviSettingDialog.class);
//                startActivityForResult(intent,NAVI_SETTING_REQUST);
                break;
            case R.id.direction_view:
                CameraUpdate localCameraUpdate = CameraUpdateFactory.changeBearing(0.0F);
                mAmap.animateCamera(localCameraUpdate);


                if(mIsNorthUp){
                    setCarUpMode();
                }
                else {
                    setNorthUpMode();
                }
                break;
            case R.id.zoom_add:
                break;
            case R.id.zoom_reduce:
                break;
            case R.id.continue_navi:
                mNaviView.recoverLockMode();
                break;
            case R.id.traffic:
                if (mTrafficOnOff==0){
                    mTrafficOnOff=1;
                    mAmap.setTrafficEnabled(false);
                    mNaviViewOption.setTrafficLine(true);
                    if(mDayNightMode==0){
                        mTrafficView.setImageResource(R.mipmap.traffic_close_day);
                    }else if (mDayNightMode==1){
                        mTrafficView.setImageResource(R.mipmap.traffic_close_night);
                    }else{
                        if(TimeUtils.isDayTime(mContext)){
                            mTrafficView.setImageResource(R.mipmap.traffic_close_day);
                        }else{
                            mTrafficView.setImageResource(R.mipmap.traffic_close_night);

                        }

                    }

                }else{
                    mTrafficOnOff=0;
                    mAmap.setTrafficEnabled(true);
                    mNaviViewOption.setTrafficLine(true);
                    if(mDayNightMode==0){
                        mTrafficView.setImageResource(R.mipmap.traffic_open_day);
                    }else if (mDayNightMode==1){
                        mTrafficView.setImageResource(R.mipmap.traffic_open_night);
                    }else{
                        if(TimeUtils.isDayTime(mContext)){
                            mTrafficView.setImageResource(R.mipmap.traffic_open_day);
                        }else{
                            mTrafficView.setImageResource(R.mipmap.traffic_open_night);

                        }

                    }

                }
                CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_TRAFFIC_ON_OFF, mTrafficOnOff);
                break;

            case R.id.park1:
                focuseRouteLine(true, false, false);
                break;
            case R.id.park2:
                focuseRouteLine(false, true, false);
                break;
            case R.id.park3:
                focuseRouteLine(false, false, true);
                break;

            case R.id.cancel_park:
                mParkLayout.setVisibility(View.GONE);
                break;
            case R.id.park_here:
                naviToPark();
                break;
            case R.id.way_point_setbutton:
                setNewWaypoint();

                break;
            case R.id.test:

                mTotalDistance=mTotalDistance+mNaviPath.getAllLength();
                mTotalTime= System.currentTimeMillis()-mStartTime/1000/60;
                int averageSpeed= (int) ((mTotalDistance/1000)/mTotalTime);
                if (MapCfg.mNaAciFragmentIsBackground){
                    //暂停计时
                    MapCfg.mToTalTime+=(System.currentTimeMillis()- MapCfg.mStartTime);
                }


                naviEndDialog=new NaviEndDialog(mContext,R.style.Dialog);
                naviEndDialog.setCloseListener(new NaviEndDialog.CloseListener() {
                    @Override
                    public void close() {
                        naviEndDialog.dismiss();
                        replaceFragmentByMap();

                    }
                });
                naviEndDialog.show();
                naviEndDialog.setAllTime(mTotalTime+"");
                naviEndDialog.setDistance(Utils.getFriendlyDistance2((int) mTotalDistance));
                naviEndDialog.setAverageSpeed(averageSpeed+"");
                naviEndDialog.setHightSpeed(mHighestSpeed+"");
                break;
            default:
                break;
        }
    }

    private void naviToPark() {

        mStrategy=mNavi.strategyConvert(mStrategyBean.isCongestion(), mStrategyBean.isCost(), mStrategyBean.isAvoidhightspeed(), mStrategyBean.isHightspeed(), false);
        ArrayList<NaviLatLng> startList=new ArrayList();
        ArrayList<NaviLatLng> endList=new ArrayList<>();
        startList.add(mCurLocation.getCoord());
        endList.add(new NaviLatLng(mParkPoiItem.getLatLonPoint().getLatitude(),mParkPoiItem.getLatLonPoint().getLongitude()));
        mNavi.calculateDriveRoute(startList, endList, null, mStrategy);
        mParkLayout.setVisibility(View.GONE);
    }


    public void showDialog() {
        HomeActivity activity = (HomeActivity) mContext;
        DeleteDataDialog dialog = new DeleteDataDialog(activity, "NaviFragment");
        dialog.setListener(new DeleteDataDialog.ICallDialogCallBack() {
            @Override
            public void onConfirmClick(DeleteDataDialog currentDialog) {
                if ( MapCfg.mNaAciFragmentIsNaVi){
                    LetvReportUtils.reportNavigationEnd((System.currentTimeMillis()- mStartTime)/1000+"s", mIsArrive +"", mStartTime +"",MapCfg.mToTalTime/1000+"s");
                    MapCfg.mNaAciFragmentIsNaVi=false;
                    MapCfg.mToTalTime=0;
                    MapCfg.mStartTime=0;
                }
                ((HomeActivity) mContext).isNavigating = false;
                NaviBarSendHelp.getInstance().responseNotNaviingDirect();
                NaviBarSendHelp.getInstance().requestNaviBarInfo();
                CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_ONGOING, 0);
                mTTsManager.stop();
                if(RoutePlanFragment.EASY_STOP.equals(getArguments().getString(RoutePlanFragment.LAUNCH_FRAGMENT))){
                    replaceFragmentByEasyStop();
                }else{
                    replaceFragmentByMap();
                }
                //  currentDialog.dismiss();
            }

            @Override
            public void onCancelClick(DeleteDataDialog currentDialog) {
                //currentDialog.dismiss();
                isDiaLogShow = false;
            }

        });
        isDiaLogShow = true;
        dialog.show();
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public  final void openGPS(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gps){
            ToastUtil.show(context, "请打开GPS!");
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            ((Activity) mContext).overridePendingTransition(0, 0);
        }

    }

    private void setImageWhenPreView(){
        if(mDayNightMode==0){
            mNaviPreViewImg.setImageResource(R.mipmap.navi_exit_preview);
        }else if (mDayNightMode==1){
            mNaviPreViewImg.setImageResource(R.mipmap.navi_exit_preview_night);
        }else{
            if(TimeUtils.isDayTime(mContext)){
                mNaviPreViewImg.setImageResource(R.mipmap.navi_exit_preview);

            }else{
                mNaviPreViewImg.setImageResource(R.mipmap.navi_exit_preview_night);
            }

        }
    }

    private void setImageWhenStopPreview(){
        if(mDayNightMode==0){
            mNaviPreViewImg.setImageResource(R.mipmap.navi_preview);
        }else if (mDayNightMode==1){
            mNaviPreViewImg.setImageResource(R.mipmap.navi_preview_night);
        }else{
            if(TimeUtils.isDayTime(mContext)){
                mNaviPreViewImg.setImageResource(R.mipmap.navi_preview);

            }else{
                mNaviPreViewImg.setImageResource(R.mipmap.navi_preview_night);
            }

        }

    }

    public void notifyGesterEvent(int event, int x, int y, int parameter) {
        if (mThincarGestureProcessor != null) {
            mThincarGestureProcessor.notifyGesterEvent(event, x, y, parameter);
        }
    }

    public void switchWindowState(short x,short y,short width,short height) {
        if (height == ThinCarDefine.FULL_CAR_HEIGHT) {
            restoreScreen();
        } else {
            halfScreen();
        }
    }

    /**
     * 针对瘦车机进入半屏
     */
    private void halfScreen() {
        setNightForThincar();

        if (!GlobalCfg.IS_POTRAIT) {
            return;
        }
        if (mConnectBackImage == null) {
            initThincarView();
        }
        mIsThincarHalfMode = true;
        ((HomeActivity)getActivity()).mMapIsHalf = true;
        if (((HomeActivity)getActivity()).isActionForNavi()) {
            ((HomeActivity)getActivity()).setCurrentPageIndex(ThinCarDefine.PageIndexDefine.HALF_MAP_PAGE);
        }
        setViewState(View.GONE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                halfMapHeight);
        params.setMargins(0, (int)((double)ThinCarDefine.HALF_TOP_MARGIN *mPhoneCarRate) , 0, 0);
        if (mNaviLayout!=null){
            mNaviLayout.setLayoutParams(params);}

        addHalfTurnView();
        showConnectView();

        if (!GlobalCfg.IS_THIRD_APP_STATE) {
            DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_CHANGE_HALF_MODE_PARAM,0,0);
        }
    }

    private void setViewState(int state) {

        mRestPredictLayout.setVisibility(state);
        if (mContinueNavi.getVisibility()==View.VISIBLE){
            mRestPredictLayout.setVisibility(View.GONE);
        }
        mCommonRoadmessagelayout.setVisibility(state);
        if (mCrossRoadMessageLayout.getVisibility()==View.VISIBLE){
            mCommonRoadmessagelayout.setVisibility(View.GONE);
        }
        mSpeedImg.setVisibility(state);
        if (mNaviSpeedLayout!=null){
            mNaviSpeedLayout.setVisibility(state);}
        if (mPortBottomlayout!=null){
            mPortBottomlayout.setVisibility(state);}
    }

    /**
     * 恢复到原来状态
     */
    private void restoreScreen() {
        setNightForThincar();

        if (!GlobalCfg.IS_POTRAIT) {
            return;
        }

        mIsThincarHalfMode = false;
        ((HomeActivity)getActivity()).mMapIsHalf = false;
        if (((HomeActivity)getActivity()).isActionForNavi()) {
            ((HomeActivity)getActivity()).setCurrentPageIndex(ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE);
        }
        setViewState(View.VISIBLE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                (int)((double)ThinCarDefine.FULL_NAVI_CAR_HEIGHT * mPhoneCarRate));
        params.addRule(RelativeLayout.BELOW,R.id.common_road_layout);
        if (mNaviLayout!=null){
            mNaviLayout.setLayoutParams(params);}
        removeHalfTurnView();

        DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_CHANGE_FULL_MODE_PARAM,0,0);
    }

    private void showConnectView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , backLayoutHeight);
        params.addRule(RelativeLayout.BELOW, R.id.mNaviLayout);
        main_layout.removeView(mThincarCover);
        main_layout.addView(mThincarCover, params);

        if (GlobalCfg.mNeedPlayAnim) {
            MyAnimationDrawable.animateRawManuallyFromXML(R.drawable.car_connect_anim,
                    mConnectBackImage, new Runnable() {

                        @Override
                        public void run() {
                        }
                    }, new Runnable() {

                        @Override
                        public void run() {
                        }
                    });

            GlobalCfg.mNeedPlayAnim = false;
        }
    }

    private void addHalfTurnView() {
        // if (MapCfg.mNaAciFragmentIsNaVi) {
        if (halfTurnView!=null){
            halfTurnView.setVisibility(View.VISIBLE);}
        //}
        if (landTrafficBar!=null){
            landTrafficBar.setVisibility(View.VISIBLE);}
        mZoomButtonView.setVisibility(View.INVISIBLE);

        mSettingView.setVisibility(View.GONE);
        mDirectionView.setVisibility(View.GONE);
        mTrafficBar.setVisibility(View.GONE);
        mCrossRoadMessageLayout.setVisibility(View.GONE);
    }

    private void removeHalfTurnView() {
        if (halfTurnView!=null){
            halfTurnView.setVisibility(View.INVISIBLE);}
        if (landTrafficBar!=null){
            landTrafficBar.setVisibility(View.INVISIBLE);}
        if(!((HomeActivity)mContext).isDriving()){
            //车辆处于行驶中，不显示
            mZoomButtonView.setVisibility(View.VISIBLE);
            mSettingView.setVisibility(View.VISIBLE);
            mDirectionView.setVisibility(View.VISIBLE);
            mTrafficBar.setVisibility(View.VISIBLE);
        }

        if (mThincarCover != null) {
            main_layout.removeView(mThincarCover);
        }
    }

    public boolean isPreview() {
        return  mIsPreView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEvent(Integer i) {

        switch (i) {
            case Constant.SHOW_MAP_TOPVIEW:
                if (GlobalCfg.IS_POTRAIT && !HomeActivity.isThinCar) {
                    mRestPredictLayout.setVisibility(View.VISIBLE);

                }
                break;

            case Constant.HIDE_MAP_TOPVIEW:
                if (GlobalCfg.IS_POTRAIT && !HomeActivity.isThinCar) {
                    mRestPredictLayout.setVisibility(View.GONE);

                }
                break;
            case Constant.MAP_HALF_SCREEN:
                halfScreen();
                break;
            case Constant.MAP_RESTORE_SCREEN:
                GlobalCfg.mNeedPlayAnim = true;

                restoreScreen();
                break;
            case Constant.DRIVING:
                toyotaRule(true);
                break;
            case Constant.NO_DRIVE:
                toyotaRule(false);
                break;
        }
    }



    private void startGPSListen(){
        mGpsManager =  new GPSLocationManager((Activity)mContext);
        GPSListenerImp listener = new GPSListenerImp(mContext);
        listener.setImageView(mGpsImageView, mGpsNumTV);
        listener.setCrossImageView(mGpsImageViewCross,mGpsNumTVCross);
        mGpsManager.start(listener,true);
    }

    private void setNaviSettings(){
        mDayNightMode = CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_MAP_MODE, 2);
//        mTrafficOnOff = CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_TRAFFIC_ON_OFF, 1);
        if(mNaviViewOption != null) {
            boolean scaleOpen=CacheUtils.getInstance(mContext).getBoolean(SettingCfg.NAVI_SCALE_OPEN,true);
            if (scaleOpen){
                mNaviViewOption.setAutoChangeZoom(true);
            }else {
                mNaviViewOption.setAutoChangeZoom(false);
            }


//            if(mTrafficOnOff == 0){
////                mNaviViewOption.setTrafficLine(true);
//                mAmap.setTrafficEnabled(true);
//                mNaviViewOption.setTrafficLine(true);
//
////                mNaviViewOption.setFourCornersBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.custtexture_no));
//            }
//            else if(mTrafficOnOff == 1){
////                mNaviViewOption.setTrafficLine(false);
//                mAmap.setTrafficEnabled(false);
//                mNaviViewOption.setTrafficLine(true);
////                mNaviViewOption.setFourCornersBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.custtexture_no));
//            }
            if (mDayNightMode == 0) {
                setMapDaytime();
            } else if (mDayNightMode == 1) {
                setMapNightTime();
            } else if (mDayNightMode == 2) {
                if (HomeActivity.isThinCar){
                    setMapNightTime();
                }else{
                    setAutoMap();
                }
            }
            if(NaviSettingDialog.SpeakerContent.getInstance().hasElectricEyeMsg()){
                mNaviViewOption.setCameraInfoUpdateEnabled(true);
            }
            else {
                mNaviViewOption.setCameraInfoUpdateEnabled(false);
            }

            if(NaviSettingDialog.SpeakerContent.getInstance().hasTrafficMsg()){
                mNaviViewOption.setTrafficInfoUpdateEnabled(true);
            }
            else {
                mNaviViewOption.setTrafficInfoUpdateEnabled(false);
            }

            mNaviView.setViewOptions(mNaviViewOption);
        }

    }

    public void setAutoMapMode(){
        if(mDayNightMode == 2){
            if (HomeActivity.isThinCar){
                setMapNightTime();
            }else{
                setAutoMap();
            }
        }
    }

    private void setAutoMap(){
        if (TimeUtils.isDayTime(mContext)){
            setMapDaytime();

        }else{
            setMapNightTime();
        }
    }

    private void setMapNightTime() {
        Trace.Debug("##### setMapNightTime");
        mNaviViewOption.setNaviNight(true);
        if (mIsNorthUp){
            mDirectionView.setImageResource(R.mipmap.north_up_night);
        }else{
            mDirectionView.setImageResource(R.mipmap.car_up_night);
        }
        mZoomButtonView.getZoomInBtn().setImageResource(R.drawable.map_zoom_out_night_selector);
        mZoomButtonView.getZoomOutBtn().setImageResource(R.drawable.map_zoom_in_night_selector);
        mSettingView.setImageResource(R.mipmap.navi_setting_night);
        if (isPreview()){
            mNaviPreViewImg.setImageResource(R.mipmap.navi_exit_preview_night);
        }else{
            mNaviPreViewImg.setImageResource(R.mipmap.navi_preview_night);
        }
        if (GlobalCfg.IS_POTRAIT){
            mSettingView.setImageResource(R.mipmap.navi_setting_night_por);
        }else{
            mSettingView.setImageResource(R.mipmap.navi_setting_night);
        }
        mExitNaviImage.setImageResource(R.mipmap.navi_close_night);
        if (mPortBottomlayout!=null){
            mPortBottomlayout.setBackgroundResource(R.drawable.white_corner_bg);
            mContinueNavi.setTextColor(mContext.getResources().getColor(R.color.transparent_80));
            mRestDistance.setTextColor(mContext.getResources().getColor(R.color.transparent_80));
            mRestTime.setTextColor(mContext.getResources().getColor(R.color.transparent_80));
            mPredictTime.setTextColor(mContext.getResources().getColor(R.color.transparent_80));
            mPredictDistance.setTextColor(mContext.getResources().getColor(R.color.transparent_80));
            mPredictTitle.setTextColor(mContext.getResources().getColor(R.color.transparent_80));
            mRestTitle.setTextColor(mContext.getResources().getColor(R.color.transparent_80));
        }

        if (mAmap!=null){
            MyTrafficStyle myTrafficStyle=new MyTrafficStyle();
            myTrafficStyle.setSlowColor(mContext.getResources().getColor(R.color.night_slow_color));
            myTrafficStyle.setSmoothColor(mContext.getResources().getColor(R.color.night_smooth_color));
            myTrafficStyle.setCongestedColor(mContext.getResources().getColor(R.color.night_congested_color));
            myTrafficStyle.setSeriousCongestedColor(mContext.getResources().getColor(R.color.night_serious_congested_color));
            mAmap.setMyTrafficStyle(myTrafficStyle);


        }
        if(mTrafficOnOff == 0){
            mAmap.setTrafficEnabled(true);
            mNaviViewOption.setTrafficLine(true);
            mTrafficView.setImageResource(R.mipmap.traffic_open_night);
        }
        else if(mTrafficOnOff == 1){
            mAmap.setTrafficEnabled(false);
            mNaviViewOption.setTrafficLine(true);
            mTrafficView.setImageResource(R.mipmap.traffic_close_night);
        }
        if (mPortBottomlayout!=null){
            mPortBottomlayout.setBackgroundColor(mContext.getResources().getColor(R.color.navi_por_bottom));
        }
    }


    private void setMapDaytime() {
        Trace.Debug("##### setMapNightTime");
        mNaviViewOption.setNaviNight(false);
        if (mIsNorthUp){
            mDirectionView.setImageResource(R.mipmap.north_up_mode);
        }else{
            mDirectionView.setImageResource(R.mipmap.car_up_mode);
        }
        mZoomButtonView.getZoomInBtn().setImageResource(R.drawable.map_zoom_out_selector);
        mZoomButtonView.getZoomOutBtn().setImageResource(R.drawable.map_zoom_in_selector);
        if (GlobalCfg.IS_POTRAIT){
            mSettingView.setImageResource(R.mipmap.navi_start_setting);
        }else {
            mSettingView.setImageResource(R.mipmap.navi_start_setting_land);
        }
        if (isPreview()){
            mNaviPreViewImg.setImageResource(R.mipmap.navi_exit_preview);
        }else{
            mNaviPreViewImg.setImageResource(R.mipmap.navi_preview);
        }
        if (mPortBottomlayout!=null){
            mPortBottomlayout.setBackgroundResource(R.drawable.white_corner_bg);
            mContinueNavi.setTextColor(mContext.getResources().getColor(R.color.black_60));
            mRestDistance.setTextColor(mContext.getResources().getColor(R.color.black_60));
            mRestTime.setTextColor(mContext.getResources().getColor(R.color.black_60));
            mPredictTime.setTextColor(mContext.getResources().getColor(R.color.black_60));
            mPredictDistance.setTextColor(mContext.getResources().getColor(R.color.black_60));
            mPredictTitle.setTextColor(mContext.getResources().getColor(R.color.black_60));
            mRestTitle.setTextColor(mContext.getResources().getColor(R.color.black_60));
        }
        if (GlobalCfg.IS_POTRAIT){
            mExitNaviImage.setImageResource(R.mipmap.navi_close);
        }else {
            mExitNaviImage.setImageResource(R.mipmap.navi_close_day_land);
        }
        if (mAmap!=null){
            MyTrafficStyle myTrafficStyle=new MyTrafficStyle();
            myTrafficStyle.setCongestedColor(mContext.getResources().getColor(R.color.day_congested_color));
            myTrafficStyle.setSeriousCongestedColor(mContext.getResources().getColor(R.color.day_serious_congested_color));
            myTrafficStyle.setSmoothColor(mContext.getResources().getColor(R.color.day_smooth_color));
            myTrafficStyle.setSlowColor(mContext.getResources().getColor(R.color.day_slow_color));
            mAmap.setMyTrafficStyle(myTrafficStyle);

        }

        if(mTrafficOnOff == 0){
            mAmap.setTrafficEnabled(true);
            mNaviViewOption.setTrafficLine(true);
            mTrafficView.setImageResource(R.mipmap.traffic_open_day);
        }
        else if(mTrafficOnOff == 1){
            mAmap.setTrafficEnabled(false);
            mNaviViewOption.setTrafficLine(true);
            mTrafficView.setImageResource(R.mipmap.traffic_close_day);
        }


    }

    private void setMapCustomStyleFile(Context context) {
        String styleName = "style_json.json";
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String filePath = null;
        try {
            inputStream = context.getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            filePath = context.getFilesDir().getAbsolutePath();
            File file = new File(filePath + "/" + styleName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            outputStream.write(b);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();

                if (outputStream != null)
                    outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        aMap.setCustomMapStylePath(filePath + "/" + styleName);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Trace.Debug("### onActivityResult");
        switch (requestCode){
            case NAVI_SETTING_REQUST:
                setNaviSettings();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public String getEndAddress(){
        return getArguments().getString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS);
    }

    public void startPreview() {
        if(!mIsPreView) {
            mHandler.removeMessages(EXIT_PREVIEW);
            setImageWhenPreView();
            mNaviView.displayOverview();
            mNaviPath=mNavi.getNaviPath();
            mAmap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(mNaviPath.getBoundsForPath().southwest,mNaviPath.getBoundsForPath().northeast),250));
            mIsPreView = true;
            NaviBarSendHelp.getInstance().notifyStartPreview();
            mHandler.sendEmptyMessageDelayed(EXIT_PREVIEW, 10000);
        }
    }

    public void stopPreview() {
        setImageWhenStopPreview();
        mNaviView.recoverLockMode();
        mIsPreView = false;
        NaviBarSendHelp.getInstance().notifyStopPreview();
        mHandler.removeMessages(EXIT_PREVIEW);
    }

    public void stopNaviFromThincarVoice() {
        if ( MapCfg.mNaAciFragmentIsNaVi){
            LetvReportUtils.reportNavigationEnd((System.currentTimeMillis()- mStartTime)/1000+"s", mIsArrive +"", mStartTime +"",MapCfg.mToTalTime/1000+"s");
            MapCfg.mNaAciFragmentIsNaVi=false;
            MapCfg.mToTalTime=0;
            MapCfg.mStartTime=0;
        }
        ((HomeActivity) mContext).isNavigating = false;
        NaviBarSendHelp.getInstance().responseNotNaviingDirect();
        NaviBarSendHelp.getInstance().requestNaviBarInfo();
        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_ONGOING, 0);
        mTTsManager.stop();
        if(RoutePlanFragment.EASY_STOP.equals(getArguments().getString(RoutePlanFragment.LAUNCH_FRAGMENT))){
            replaceFragmentByEasyStop();
        }else{
            replaceFragmentByMap();
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        setNightForThincar();
        setThincarMapModeDelay();
    }
    public void reCalculateRoute(int strategy) {
        if (mNavi != null) {
            mNavi.reCalculateRoute(strategy);

        }
    }
    private void searchRoutePOI(RoutePOISearchType type) {
        if (mNaviPath!=null){
            NaviLatLng startNaviLatLng=mNaviPath.getStartPoint();
            NaviLatLng endNaviLatLng=mNaviPath.getEndPoint();
            RoutePOISearchQuery query = new RoutePOISearchQuery(new LatLonPoint(startNaviLatLng.getLatitude(),startNaviLatLng.getLongitude()),new LatLonPoint(endNaviLatLng.getLatitude(),endNaviLatLng.getLongitude()), mStrategy, type, 250);
            final RoutePOISearch search = new RoutePOISearch(mContext, query);
            search.setPoiSearchListener(this);
            search.searchRoutePOIAsyn();}
    }
    @Override
    public void onRoutePoiSearched(RoutePOISearchResult result, int errorCode) {
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if(result != null){
                final RoutePOISearchType type=result.getQuery().getSearchType();
                List<RoutePOIItem> items = result.getRoutePois();
                if (items != null && items.size() > 0) {
                    if (mRoutePoiOverlay != null) {
                        mRoutePoiOverlay.removeFromMap();
                    }
                    mRoutePoiOverlay = new RoutePoiOverlay(mContext,mAmap, items,result.getQuery().getSearchType());
                    mRoutePoiOverlay.addToMap();
                    startPreview();
                } else {
                    doRouteSearchNoResult(type);
                }
            }
        }else{
        }
    }
    private void setNightForThincar() {
        if (mAmap != null) {
            setThincarMapMode();
            mAmap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    setThincarMapMode();
                }
            });
        }
    }

    private void setThincarMapMode() {
//        if(mDayNightMode==2){
//            if(HomeActivity.isThinCar){
////                mAmap.setMapType(AMap.MAP_TYPE_NIGHT);
//                setMapNightTime();
//            }else{
////                mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
//                setAutoMap();
//            }
//        }
        setNaviSettings();
    }

    private void setThincarMapModeDelay() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if(mDayNightMode==2){
//                    if (HomeActivity.isThinCar) {
////                        mAmap.setMapType(AMap.MAP_TYPE_NIGHT);
//                        setMapNightTime();
//                    } else {
////                        mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
//                        setAutoMap();
//                    }
//                }
                setNaviSettings();
            }
        }, 2000);
    }
    private void doRouteSearchNoResult(final RoutePOISearchType type) {
        final NetworkConfirmDialog networkConfirmDialog=new NetworkConfirmDialog(mContext, R.string.route_search_no,R.string.cancel,R.string.search_near);
        networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
            @Override
            public void onConfirm(boolean checked) {

            }

            @Override
            public void onCancel() {
                if (mCurLocation==null){
                    ToastUtil.show(mContext,"网络不好，请稍后重试");
                    return;
                }
                LatLonPoint latLonPoint=new LatLonPoint(mCurLocation.getCoord().getLatitude(),mCurLocation.getCoord().getLongitude());

                switch (type){
                    case TypeATM:
                        doPoiSearch("ATM",latLonPoint,type);
                        break;
                    case TypeToilet:
                        doPoiSearch("卫生间",latLonPoint,type);
                        break;
                    case TypeGasStation:
                        doPoiSearch("加油站",latLonPoint,type);
                        break;
                    case TypeMaintenanceStation:
                        doPoiSearch("维修站",latLonPoint,type);
                        break;
                }


            }
        });
        networkConfirmDialog.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getSnippet().equals("0")) {
            mWayPoints.remove(0);
            ArrayList<NaviLatLng> startNaviLatLngs=new ArrayList<>();
            startNaviLatLngs.add(mStartPoint);
            ArrayList<NaviLatLng> endNaviLatLngs = new ArrayList<>();
            endNaviLatLngs.add(mEndPoint);
            mWayPointOverLay.removeFromMap();
//            mNavi.stopNavi();

            Trace.Debug("#### point size="+mWayPoints.size());
            mWayPointOverLay =new WayPointOverLay(mContext,mAmap, mWayPoints);
            mWayPointOverLay.addToMap();
            mStrategy=mNavi.strategyConvert(mStrategyBean.isCongestion(), mStrategyBean.isCost(), mStrategyBean.isAvoidhightspeed(), mStrategyBean.isHightspeed(), false);
            mTotalDistance=mTotalDistance+mNaviPath.getAllLength()-mNaviInfo.getPathRetainDistance();
            mNavi.calculateDriveRoute(startNaviLatLngs,endNaviLatLngs,mWayPoints,mStrategy);

//            mNavi.reCalculateRoute(mStrategy);




        } else if (marker.getSnippet().equals("1")) {
            mWayPoints.remove(1);
            ArrayList<NaviLatLng> startNaviLatLngs=new ArrayList<>();
            startNaviLatLngs.add(mStartPoint);
            ArrayList<NaviLatLng> endNaviLatLngs = new ArrayList<>();
            endNaviLatLngs.add(mEndPoint);
//            mNavi.stopNavi();
            mWayPointOverLay.removeFromMap();
            Trace.Debug("#### point size="+mWayPoints.size());
            mWayPointOverLay =new WayPointOverLay(mContext,mAmap, mWayPoints);
            mWayPointOverLay.addToMap();
            mStrategy=mNavi.strategyConvert(mStrategyBean.isCongestion(), mStrategyBean.isCost(), mStrategyBean.isAvoidhightspeed(), mStrategyBean.isHightspeed(), false);
            mTotalDistance=mTotalDistance+mNaviPath.getAllLength()-mNaviInfo.getPathRetainDistance();
            mNavi.calculateDriveRoute(startNaviLatLngs,endNaviLatLngs,mWayPoints,mStrategy);
//            mNavi.reCalculateRoute(mStrategy);



        } else if (marker.getSnippet().equals("2")) {
            mWayPoints.remove(2);
            ArrayList<NaviLatLng> startNaviLatLngs=new ArrayList<>();
            startNaviLatLngs.add(mStartPoint);
            ArrayList<NaviLatLng> endNaviLatLngs = new ArrayList<>();
            endNaviLatLngs.add(mEndPoint);
//            mNavi.stopNavi();
            mWayPointOverLay.removeFromMap();
            Trace.Debug("#### point size="+mWayPoints.size());
            mWayPointOverLay =new WayPointOverLay(mContext,mAmap, mWayPoints);
            mWayPointOverLay.addToMap();
            mStrategy=mNavi.strategyConvert(mStrategyBean.isCongestion(), mStrategyBean.isCost(), mStrategyBean.isAvoidhightspeed(), mStrategyBean.isHightspeed(), false);
            mTotalDistance=mTotalDistance+mNaviPath.getAllLength()-mNaviInfo.getPathRetainDistance();
            mNavi.calculateDriveRoute(startNaviLatLngs,endNaviLatLngs,mWayPoints,mStrategy);
//            mNavi.reCalculateRoute(mStrategy);


        } else {
            setWayPointLayout(marker);
        }


        return false;
    }

    private void setWayPointLayout(Marker marker) {
        RoutePOIItem routePOIItem = (RoutePOIItem) marker.getObject();
        if (routePOIItem != null) {
            mWayPointLayout.setVisibility(View.VISIBLE);
            mWayPointName.setText(routePOIItem.getTitle());
            mWayPointDistance.setText("距您"+Utils.getFriendlyDistance((int) routePOIItem.getDistance()) + "");
            mWayPointSetButton.setOnClickListener(this);
            LatLonPoint latLonPoint=routePOIItem.getPoint();
            mNewSetWayPoint=new NaviLatLng(latLonPoint.getLatitude(),latLonPoint.getLongitude());
            mWayPointLayout.setVisibility(View.VISIBLE);
            setWaypointLayoutVisible();
        }
    }

    private void setWaypointLayoutVisible() {
        mHandler.removeMessages(WAY_POINT_CLOSE,1000*10);
        mHandler.sendEmptyMessageDelayed(WAY_POINT_CLOSE,1000*10);
        if (GlobalCfg.IS_POTRAIT){
            if (mPortBottomlayout!=null){
//            RelativeLayout.LayoutParams  params= (RelativeLayout.LayoutParams) mPortBottomlayout.getLayoutParams();
//            params.addRule(RelativeLayout.ABOVE, R.id.way_point_layout);
//            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                mPortBottomlayout.setVisibility(View.INVISIBLE);}

        }else{
//            RelativeLayout.LayoutParams setParams= (RelativeLayout.LayoutParams) mSettingView.getLayoutParams();
//            setParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            setParams.addRule(RelativeLayout.ABOVE,R.id.way_point_layout);
//            mSettingView.setLayoutParams(setParams);
//            RelativeLayout.LayoutParams exitParams= (RelativeLayout.LayoutParams) mExitNaviImage.getLayoutParams();
//            exitParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            exitParams.addRule(RelativeLayout.ABOVE,R.id.way_point_layout);
//            mExitNaviImage.setLayoutParams(exitParams);

            mSettingView.setVisibility(View.INVISIBLE);
            mExitNaviImage.setVisibility(View.INVISIBLE);

        }
    }


    private void setNewWaypoint() {
        if (mWayPoints!=null){
            mWayPoints.clear();
        }else {
            mWayPoints=new ArrayList<>();
        }
        mWayPoints.add(mNewSetWayPoint);
        ArrayList<NaviLatLng> startNaviLatLngs=new ArrayList<>();
        startNaviLatLngs.add(mStartPoint);
        ArrayList<NaviLatLng> endNaviLatLngs = new ArrayList<>();
        endNaviLatLngs.add(mEndPoint);
        if (mWayPointOverLay!=null){
            mWayPointOverLay.removeFromMap();}
        if (mRoutePoiOverlay!=null){
            mRoutePoiOverlay.removeFromMap();}
//        mNavi.stopNavi();

        Trace.Debug("#### point size="+mWayPoints.size());
        mWayPointOverLay =new WayPointOverLay(mContext,mAmap, mWayPoints);
        mWayPointOverLay.addToMap();
        mStrategy=mNavi.strategyConvert(mStrategyBean.isCongestion(), mStrategyBean.isCost(), mStrategyBean.isAvoidhightspeed(), mStrategyBean.isHightspeed(), false);
        mTotalDistance=mTotalDistance+mNaviPath.getAllLength()-mNaviInfo.getPathRetainDistance();
        mNavi.calculateDriveRoute(startNaviLatLngs,endNaviLatLngs,mWayPoints,mStrategy);
        setWayPointLayoutGone();

    }

    private void setWayPointLayoutGone() {
        mWayPointLayout.setVisibility(View.GONE);
        if (GlobalCfg.IS_POTRAIT){
            if (mPortBottomlayout!=null){
//            RelativeLayout.LayoutParams  params= (RelativeLayout.LayoutParams) mPortBottomlayout.getLayoutParams();
//            params.removeRule(RelativeLayout.ABOVE);
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            mPortBottomlayout.setLayoutParams(params);
                mPortBottomlayout.setVisibility(View.VISIBLE);}

        }else{
//            RelativeLayout.LayoutParams setParams= (RelativeLayout.LayoutParams) mSettingView.getLayoutParams();
//            setParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            setParams.removeRule(RelativeLayout.ABOVE);
//            mSettingView.setLayoutParams(setParams);
//            RelativeLayout.LayoutParams exitParams= (RelativeLayout.LayoutParams) mExitNaviImage.getLayoutParams();
//            exitParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            exitParams.removeRule(RelativeLayout.ABOVE);
//            mExitNaviImage.setLayoutParams(exitParams);

            mSettingView.setVisibility(View.VISIBLE);
            mExitNaviImage.setVisibility(View.VISIBLE);

        }
    }





    private static class WayPointOverLay {
        private AMap mamap;
        private List<NaviLatLng> mNaviLatLngs;
        private ArrayList<Marker> wayMarkers = new ArrayList<Marker>();

        private List<String> mWayPointStrings;
        private int[] wayPointId = new int[]{R.mipmap.navi_passby1_delete, R.mipmap.navi_passby2_delete, R.mipmap.navi_passby3_delete};
        private Context mContext;

        public WayPointOverLay(Context context,AMap aMap, List<NaviLatLng> naviLatLngs) {
            mContext=context;
            mamap = aMap;
            mNaviLatLngs = naviLatLngs;


        }

        /**
         * 添加Marker到地图中。
         *
         * @since V2.1.0
         */
        public void addToMap() {

            if (mNaviLatLngs != null && mNaviLatLngs.size() > 0) {
                int wayPointSize = mNaviLatLngs.size();
                Marker wayPointMarker;
                if (wayPointSize == 1) {
                    LatLng wayLatLng = new LatLng(mNaviLatLngs.get(0).getLatitude(), mNaviLatLngs.get(0).getLongitude());


                    wayPointMarker = mamap.addMarker((new MarkerOptions()).position(wayLatLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.navi_passby_delete))).snippet("0"));
                    wayPointMarker.setInfoWindowEnable(false);
                    wayPointMarker.setClickable(true);
                    this.wayMarkers.add(wayPointMarker);
                } else {
                    for (int i = 0; i < mNaviLatLngs.size(); i++) {
                        NaviLatLng wayNaviLatLng = mNaviLatLngs.get(i);
                        LatLng wayLatLng = new LatLng(wayNaviLatLng.getLatitude(), wayNaviLatLng.getLongitude());


                        wayPointMarker = this.mamap.addMarker((new MarkerOptions()).position(wayLatLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), wayPointId[i]))).snippet(i+""));
                        wayPointMarker.setInfoWindowEnable(false);
                        wayPointMarker.setClickable(true);

                        this.wayMarkers.add(wayPointMarker);
                    }
                }

            }
        }

        public void removeFromMap() {
            for (Marker mark : wayMarkers) {
                mark.remove();
            }
        }

    }
    /**
     * 自定义PoiOverlay
     *
     */

    private static class RoutePoiOverlay {
        private AMap mamap;
        private List<RoutePOIItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
        private RoutePOISearchType mSearchType;
        private Context mContext;


        public RoutePoiOverlay(Context context,AMap amap, List<RoutePOIItem> pois, RoutePOISearchType searchType) {
            mContext=context;
            mamap = amap;
            mPois = pois;
            mSearchType=searchType;
        }

        /**
         * 添加Marker到地图中。
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mamap.addMarker(getMarkerOptions(i));
                switch (mSearchType){
                    case TypeATM:
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.navi_passby_atm)));
                        break;
                    case TypeToilet:
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.navi_passby_wc)));
                        break;
                    case  TypeGasStation:
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.navi_passby_gasstation)));
                        break;
                    case  TypeMaintenanceStation:
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.navi_passby_weixiudian)));
                        break;

                }
                RoutePOIItem item = mPois.get(i);
                marker.setClickable(true);
                marker.setObject(item);


                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getPoint().getLatitude(),
                        mPois.get(i).getPoint().getLongitude()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index)).infoWindowEnable(false);
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getDistance() + "米  " + mPois.get(index).getDuration() + "秒";
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public RoutePOIItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }
    }

    public void setSoundOpend(boolean open) {

        if (open){
            CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_SPEAKER, 1);
        }else {
            CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_SPEAKER, 0);
            mTTsManager.stop();
        }
    }

    public void setOritentionMode(boolean northUp) {
        if (northUp){
            setNorthUpMode();

        }else {
            setCarUpMode();
        }




    }

    private void setCarUpMode() {
        mIsNorthUp = false;
        mNaviViewOption.setPointToCenter(0.5, 0.66);
        if(mDayNightMode==0){
            mNaviView.getLazyDirectionView().setImageResource(R.mipmap.car_up_mode);
        }else if (mDayNightMode==1){
            mNaviView.getLazyDirectionView().setImageResource(R.mipmap.car_up_night);
        }else{
            if(TimeUtils.isDayTime(mContext)){
                mNaviView.getLazyDirectionView().setImageResource(R.mipmap.car_up_mode);
            }else{
                mNaviView.getLazyDirectionView().setImageResource(R.mipmap.car_up_night);

            }

        }

        mNaviView.setNaviMode(AMapNaviView.CAR_UP_MODE);
        mNaviView.setViewOptions(mNaviViewOption);
        mNaviView.requestLayout();
    }

    private void setNorthUpMode() {
        mIsNorthUp = true;
        mNaviViewOption.setPointToCenter(0.5,0.66);
        if(mDayNightMode==0){
            mNaviView.getLazyDirectionView().setImageResource(R.mipmap.north_up_mode);
        }else if (mDayNightMode==1){
            mNaviView.getLazyDirectionView().setImageResource(R.mipmap.north_up_night);
        }else{
            if(TimeUtils.isDayTime(mContext)){
                mNaviView.getLazyDirectionView().setImageResource(R.mipmap.north_up_mode);

            }else{
                mNaviView.getLazyDirectionView().setImageResource(R.mipmap.north_up_night);
            }

        }

        mNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);
        mNaviView.setViewOptions(mNaviViewOption);
        mNaviView.requestLayout();
    }

    public void setZoomOut(boolean b) {
        if (b){
            if (mAmap!=null){
                mAmap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        }else{
            if (mAmap!=null){
                mAmap.animateCamera(CameraUpdateFactory.zoomIn());
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
            mExitNaviImage.setVisibility(View.GONE);
            mSettingView.setVisibility(View.GONE);

            mZoomButtonView.setVisibility(View.GONE);
            mDirectionView.setVisibility(View.GONE);
            mTrafficBar.setVisibility(View.GONE);

        }else{
            mExitNaviImage.setVisibility(View.VISIBLE);
            mSettingView.setVisibility(View.VISIBLE);

            mZoomButtonView.setVisibility(View.VISIBLE);
            mDirectionView.setVisibility(View.VISIBLE);
            mTrafficBar.setVisibility(View.VISIBLE);
        }
    }
}