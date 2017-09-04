package com.letv.leauto.ecolink.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.MyTrafficStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.lemap.SensorEventHelper;
import com.letv.leauto.ecolink.lemap.entity.SearchPoi;
import com.letv.leauto.ecolink.lemap.navi.Utils;
import com.letv.leauto.ecolink.receiver.ScreenRotationUtil;
import com.letv.leauto.ecolink.thincar.MapAnimation;
import com.letv.leauto.ecolink.thincar.ThincarGestureProcessor;
import com.letv.leauto.ecolink.thincar.protocol.NaviBarSendHelp;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.LocationBaseFragment;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.view.BackView;
import com.letv.leauto.ecolink.ui.view.PressedImageView;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.MyAnimationDrawable;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.ThincarUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.leauto.ecolink.utils.WaitingAnimationDialog;
import com.letv.mobile.core.utils.PermissionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MapFragment extends LocationBaseFragment implements LocationSource, View.OnClickListener,
        AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnCameraChangeListener ,SensorEventHelper.AngleCallback,AMapNaviListener, Observer {
    public static final String IS_HOME_ADDRESS = "map_select_home";
    public static final String MAPMODE="MAPMODE";
    public static final String IS_WIDGET = "widget";
    //    public static String IS_HOME_ADDRESS = "IS_HOME_ADDRESS";
    @Bind(R.id.map)
    TextureMapView mapView;


    @Bind(R.id.map_search)
    LinearLayout mMapSearchLayout;
    @Bind(R.id.map_search_text)
    TextView mMapSearchTextView;

//    @Bind(R.id.map_content_layout)
//    RelativeLayout mapContentlayout;

    @Bind(R.id.poi_add_title)
    LinearLayout mPoiAddTitleLayout; //选定模式的搜索框
    @Bind(R.id.poi_add_search_text)
    TextView mPoiAddTextView; //选点模式的文字框
    @Bind(R.id.iv_back)
    BackView mBackView;



    private volatile int mSelectPoiMode;//是否是添加点的模式 添加公司地址，添加家的地址 0;普通的地图模式 1：添加公司或者家的地址 2：poi打点显示

    @Bind(R.id.map_downlod_layout)
    RelativeLayout mDownloadLayout;
    @Bind(R.id.map_downlod)
    ImageView mDownloadView;

    @Bind(R.id.geo_result_layout)
    LinearLayout mGeoResultlayout;

    @Bind(R.id.rlt_teach)
    RelativeLayout rlt_teach;
    @Bind(R.id.iv_teach_confirm)
    ImageView iv_teach_confirm;

    @Bind(R.id.geo_describe)
    TextView mGeoDescribe;
    @Bind(R.id.geo_build)
    TextView mGeoBuildings;
    @Bind(R.id.distance)
    TextView mDistanceView;

    @Bind(R.id.divider_line)
    View divider_line;

    @Bind(R.id.iv_add)
    ImageView mAddView;

    @Bind(R.id.iv_addzoom)
    PressedImageView addzoom;

    @Bind(R.id.iv_reducezoom)
    PressedImageView reducezoom;

    @Bind(R.id.iv_localization)
    ImageView mLocationView;

    @Bind(R.id.other_icon_layout)
    RelativeLayout other_icon_layout;
    @Bind(R.id.activity_navi)
    RelativeLayout main_layout;


    @Bind(R.id.traffic)
    ImageView mTrafficView;
    @Bind(R.id.tv_guide)
    TextView tv_guide;


    @Bind(R.id.poi_show_title)
    RelativeLayout mPoishowLayout;
    @Bind(R.id.poi_show_title_text)
    TextView mPoiShowTitleTextview;

    @Bind(R.id.poi_show_back)
    BackView mPoiShowBackView;
    @Bind(R.id.close_poi_show)
    ImageView mPoiCloseView;
    private boolean  mFromWidget;

    private AMap aMap;

    Bundle savedInstanceState;
    String myAddr = null;
    String enAddr = null;
    String homeAddr = null;
    String workAddr = null;
    //选择的图标
    private LatLonPoint mChosepoint;
    private GeocodeSearch geocoderSearch;
    private Marker mChosePointMarker;
    private String markAddr;//选择的地点信息
    private String mDesAdressName;
    private double mCurLatitude = 0;
    private double mCurLongitude = 0;
    private String mCurAddress, mCurDiscrebe;

    private float currentZoom=14;
    private float maxZoom = 18;
    private float minZoom = 3;
    private  double mDistinLatitude;
    private  double mDistinLongitude;
    private  String city = "北京";
    private int mTrafficOpened;
    private int mCarMode=0; //0表示正北模式  1表示 车头朝上模式， 2表示 定位模式
    private int mInitCarMode=0;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);


    private IntentFilter mapIntentfilter;
    private MapDownloadReceiver mapDownloadReceiver;
    NetworkConfirmDialog mLocationConfirmDialog;
    private static boolean mdismiss;

    private MapAnimation mMapAnimation;
    private View mThincarCover;
    private ImageView mConnectBackImage;
    private boolean mIsHalfMode;
    /** 点击位置全屏时是否应用显示 */
    private boolean mShouldPoilayoutRestore = false;

    private LatLng mChooseLatLng;



    private int halfMapHeight;
    private int backLayoutHeight;


    private AMapLocation mCurMapLocation;

    private boolean isHome;
    private Marker mCurPointMarker;
    private SensorEventHelper mSensorEventHelper;
    private Circle mCircle;
    private SearchPoi mSearchPoi;
    private  AMapNavi mNavi;


    public static MapFragment getInstance(Bundle bundle) {
        MapFragment mFragment = new MapFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }
    View view;
    @Override
    protected View initView(LayoutInflater inflater) {
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_map, null);
        } else {
            view = inflater.inflate(R.layout.fragment_map_l, null);
        }

        ButterKnife.bind(this, view);
        ((HomeActivity)getActivity()).setMapFragment(this);
        mSensorEventHelper=new SensorEventHelper(mContext);
        mSensorEventHelper.setAngleCallback(this);
        mTrafficOpened=CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_TRAFFIC_ON_OFF,1);
        MapCfg.mapfragmentOpen=true;
        ((HomeActivity)getActivity()).showTitleBar(true);
        mMapSearchTextView.setText(getResources().getString(R.string.click_start_seatch));
        mapView.onCreate(savedInstanceState);
        mPoiAddTitleLayout.setOnClickListener(this);
        mTrafficView.setOnClickListener(this);
        iv_teach_confirm.setClickable(true);
        iv_teach_confirm.setOnClickListener(this);
        mMapSearchLayout.setOnClickListener(this);
        mGeoResultlayout.setOnClickListener(this);
        addzoom.setOnClickListener(this);
        reducezoom.setOnClickListener(this);
        mLocationView.setOnClickListener(this);
        mDownloadView.setOnClickListener(this);
        mPoiCloseView.setOnClickListener(this);
        mPoiShowBackView.setOnClickListener(this);
        //初始化地理编码服务
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
        mBackView.setOnClickListener(this);
        mapIntentfilter=new IntentFilter(SettingCfg.MAP_DOWNLOAD);
        mapDownloadReceiver=new MapDownloadReceiver(this);
        mContext.registerReceiver(mapDownloadReceiver,mapIntentfilter);
        if (mTrafficOpened==0){
            mTrafficView.setImageResource(R.mipmap.traffic_open_day);

        }else {
            mTrafficView.setImageResource(R.mipmap.traffic_close_day);
        }

        ((HomeActivity)mContext).isInMapFragment = true;

        if (getArguments()!=null){
            mSelectPoiMode =getArguments().getInt(MapCfg.MAPMODE);
            isHome=getArguments().getBoolean(IS_HOME_ADDRESS);
            mFromWidget=getArguments().getBoolean(IS_WIDGET);
            mSearchPoi= (SearchPoi) getArguments().getSerializable(MapCfg.POI_LOCATION);


        }
        if (CacheUtils.getInstance(mContext).getBoolean(SettingCfg.MAP_DOWNLOAD,false)){
            mDownloadLayout.setVisibility(View.GONE);

        }else{
            mDownloadLayout.setVisibility(View.VISIBLE);
            if (!GlobalCfg.IS_POTRAIT){
                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mDownloadLayout.getLayoutParams();
                params.width= DensityUtils.getScreenHeight(mContext)-2*mContext.getResources().getDimensionPixelSize(R.dimen.size_12dp);
                mDownloadLayout.setLayoutParams(params);

            }

        }

        if (HomeActivity.isThinCar) {
            mDownloadLayout.setVisibility(View.GONE);

        }
        if (mSelectPoiMode==0){
            mGeoResultlayout.setVisibility(View.GONE);
            Trace.Debug("******* gone");
        }else {
            mGeoResultlayout.setVisibility(View.VISIBLE);
        }
        if (isNetConnect) {
            setUpMap();
        } else {

            EcoApplication.isLocation = false;
            showNoNetDialog();
            setUpMap();
        }

        mThincarGestureProcessor = new ThincarGestureProcessor(mapView.getMap(),mPhoneCarRate);
        if (HomeActivity.isThinCar) {
            initThinCarView();
        }

        /**
         * 互联车机地图夜间模式
         */
        getEcoApplication().getObservable().addObserver(this);
        if(HomeActivity.isThinCar){
//            setMapType(AMap.MAP_TYPE_NIGHT);
            setNightForThincar();
        }
        return view;
    }

    private void initThinCarView() {
        mMapAnimation = new MapAnimation(main_layout, mContext, mapView, other_icon_layout, mPhoneCarRate);
        halfMapHeight = (int)((double)ThinCarDefine.HALF_NAVI_CAR_HEIGHT * mPhoneCarRate);
        backLayoutHeight = ((HomeActivity)getActivity()).getRlMainHeight() - halfMapHeight;

        mThincarCover = LayoutInflater.from(mContext).inflate(R.layout.thincar_cover_layout,null);
        mConnectBackImage = (ImageView)mThincarCover.findViewById(R.id.thincar_anim_image);
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

    private void intTypeFace() {

    }

    public void playAnim() {
        Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMapAnimation.playReally();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        EventBus.getDefault().register(this);
        boolean locationPermission=PermissionUtil.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        Trace.Debug("####permission location"+locationPermission);
        if (!locationPermission){
            showLocationDialog();
        }


        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Trace.Debug("#####permission not gain");

        }



    }

    private void showLocationDialog() {
        if (mLocationConfirmDialog==null&&!mdismiss){
            mLocationConfirmDialog=new NetworkConfirmDialog(mContext, R.string.open_location_permission,R.string.ok,R.string.cancel);
            mLocationConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                @Override
                public void onConfirm(boolean checked) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    mContext.startActivity(intent);
                    mLocationConfirmDialog=null;

                }

                @Override
                public void onCancel() {
                    mdismiss=true;
                    mLocationConfirmDialog=null;
                }
            });
            mLocationConfirmDialog.setCancelable(false);
            mLocationConfirmDialog.show();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mSensorEventHelper.registerSensorListener();
        int firstLaunch = CacheUtils.getInstance(mContext).getInt(MapCfg.MAP_LAUNCHED, 0);
        if (firstLaunch == 0) {
            CacheUtils.getInstance(mContext).putInt(MapCfg.MAP_LAUNCHED, 1);
        }

    }

    public void getMapScreenShot(MapAnimation.GetMapShotFinish listener) {
        if(mMapAnimation!=null){
            mMapAnimation.getMapScreenShot(listener);
        }
        //hideViewWhenHalf();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        mSensorEventHelper.unRegisterSensorListener();
    }

    public void switchMapWindowSize(short x, short y, short width, short height) {
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) ((double) height * mPhoneCarRate));
//        params.setMargins(0, (int) y, 0, 0);
        mDownloadLayout.setVisibility(View.GONE);
        if (height == ThinCarDefine.FULL_CAR_HEIGHT) {
            restoreScreen();
        } else {
            halfScreen();
        }
//        if (mapContentlayout != null) {
//            mapContentlayout.setLayoutParams(params);
//        }
    }

    /**
     * 针对瘦车机进入半屏
     */
    public void halfScreen() {
        setNightForThincar();
        if (mConnectBackImage == null) {
            initThinCarView();
        }
        mIsHalfMode = true;
        ((HomeActivity)getActivity()).mMapIsHalf = true;
        if (((HomeActivity)getActivity()).isActionForNavi()) {
            ((HomeActivity)getActivity()).setCurrentPageIndex(ThinCarDefine.PageIndexDefine.HALF_MAP_PAGE);
        }
        hideViewWhenHalf();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                halfMapHeight);
        params.setMargins(0, ThinCarDefine.HALF_TOP_MARGIN, 0, 0);
        mapView.setLayoutParams(params);

        showConnectImage();

        if (!GlobalCfg.IS_THIRD_APP_STATE) {
            DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_CHANGE_HALF_MODE_PARAM,0,0);
        }
    }

    /**
     * 恢复到原来状态
     */
    public void restoreScreen() {
        setNightForThincar();
        mIsHalfMode = false;
        ((HomeActivity)getActivity()).mMapIsHalf = false;
        if (((HomeActivity)getActivity()).isActionForNavi()) {
            ((HomeActivity)getActivity()).setCurrentPageIndex(ThinCarDefine.PageIndexDefine.FULL_MAP_PAGE);
        }
        showViewWhenRestore();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) ((double) ThinCarDefine.FULL_CAR_HEIGHT * mPhoneCarRate));
        params.addRule(RelativeLayout.BELOW, R.id.title);
        mapView.setLayoutParams(params);

        DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_CHANGE_FULL_MODE_PARAM,0,0);
    }

    private void hideViewWhenHalf() {

        addzoom.setVisibility(View.GONE);
        reducezoom.setVisibility(View.GONE);
        mTrafficView.setVisibility(View.GONE);
        mLocationView.setVisibility(View.GONE);

        if (mGeoResultlayout.getVisibility() == View.VISIBLE) {
            mGeoResultlayout.setVisibility(View.INVISIBLE);
            if (mChosePointMarker != null) {
                mChosePointMarker.destroy();
            }

            if (mSelectPoiMode ==1){
                if (mCurPointMarker!=null){
                    mCurPointMarker.destroy();
                    mCurPointMarker=null;
                }
            }
            mShouldPoilayoutRestore = true;
        }

    }

    private void showConnectImage() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , backLayoutHeight);
        params.addRule(RelativeLayout.BELOW, R.id.map);
        main_layout.removeView(mThincarCover);
        main_layout.addView(mThincarCover,params);

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

    private void showViewWhenRestore() {

        if(!((HomeActivity)mContext).isDriving()){
            //车辆处于行驶中，按钮不显示
            addzoom.setVisibility(View.VISIBLE);
            reducezoom.setVisibility(View.VISIBLE);
            mTrafficView.setVisibility(View.VISIBLE);
            mLocationView.setVisibility(View.VISIBLE);
        }

        if (mShouldPoilayoutRestore) {
            mGeoResultlayout.setVisibility(View.VISIBLE);
            restorePointMarker();
            mShouldPoilayoutRestore = false;
        }

        if (mThincarCover != null) {
            main_layout.removeView(mThincarCover);
        }
    }

    public void showMapView() {
        if (mapView != null) {
            mapView.setVisibility(View.VISIBLE);
        }
    }

    public void notifyGesterEvent(int event, int x, int y, int parameter) {
        if (mThincarGestureProcessor != null) {
            mThincarGestureProcessor.notifyGesterEvent(event, x, y, parameter);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapCfg.mapfragmentOpen=false;
        if(mapDownloadReceiver!=null){
            mContext.unregisterReceiver(mapDownloadReceiver);
            mapDownloadReceiver=null;
        }
        ((HomeActivity)getActivity()).setMapFragment(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        getEcoApplication().getObservable().deleteObserver(this);
        EventBus.getDefault().unregister(this);
        mapView.onDestroy();
        mapView=null;
       // ((HomeActivity)mContext).setMapFragment(null);
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
            mlocationClient=null;

        }
        if (mSensorEventHelper != null) {
            mSensorEventHelper.removeCallBack();
            mSensorEventHelper=null;
        }

        removeListenr();
        mContext=null;


    }

    private void removeListenr() {
//        mPoiSearchLayout.setOnClickListener(null);
//        company_remind.setOnClickListener(null);
//        home_remind.setOnClickListener(null);
//        mTrafficView.setOnClickListener(null);
//        iv_teach_confirm.setOnClickListener(null);
//        geocoderSearch.setOnGeocodeSearchListener(null);
//
//        mBackView.setOnClickListener(null);
//        homelyt.setOnClickListener(null);
//        worklyt.setOnClickListener(null);
//        searchView.setOnClickListener(null);
//        homelyt.setOnLongClickListener(null);
//        worklyt.setOnLongClickListener(null);
//        mPoiContentlayout.setOnClickListener(null);
//        addzoom.setOnClickListener(null);
//        reducezoom.setOnClickListener(null);
//        mLocationView.setOnClickListener(null);
//        mDownloadView.setOnClickListener(null);
    }




    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        if (aMap == null) {
            aMap = mapView.getMap();
            // 此方法必须重写
        }

        // 自定义系统定位蓝点
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setLogoBottomMargin(-1000);
        MyTrafficStyle myTrafficStyle=new MyTrafficStyle();
        myTrafficStyle.setSmoothColor(mContext.getResources().getColor(R.color.smooth_color));
        myTrafficStyle.setCongestedColor(mContext.getResources().getColor(R.color.congested_color));
        myTrafficStyle.setSlowColor(mContext.getResources().getColor(R.color.slow_color));
        myTrafficStyle.setSeriousCongestedColor(mContext.getResources().getColor(R.color.serious_congested_color));
        aMap.setMyTrafficStyle(myTrafficStyle);

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                mNavi=AMapNavi.getInstance(mContext);
                if (mTrafficOpened==0){
                    aMap.setTrafficEnabled(true);
                }else{
                    aMap.setTrafficEnabled(false);
                }

//                setMapCustomStyleFile(mContext);
            }
        });

        setNightForThincar();

        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.radiusFillColor(getResources().getColor(R.color.transparent));
        myLocationStyle.strokeColor(getResources().getColor(R.color.transparent));

        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.transparent));
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(currentZoom));




        //设置定位园的颜色为透明色
        if (mSelectPoiMode ==1){
            tv_guide.setVisibility(View.GONE);
            mAddView.setImageResource(R.mipmap.map_ic_add1);

            mMapSearchLayout.setVisibility(View.GONE);
            mPoishowLayout.setVisibility(View.GONE);
            mPoiAddTitleLayout.setVisibility(View.VISIBLE);
            if (isHome){
                mPoiAddTextView.setHint(getString(R.string.map_search_home));
            }else {
                mPoiAddTextView.setText(getString(R.string.map_search_company));
            }
        }else if (mSelectPoiMode ==0){

            mMapSearchLayout.setVisibility(View.VISIBLE);
            mPoiAddTitleLayout.setVisibility(View.GONE);
            mPoishowLayout.setVisibility(View.GONE);
            tv_guide.setVisibility(View.GONE);
            mAddView.setImageResource(R.mipmap.location_to);
        }else if (mSelectPoiMode==2){
            mMapSearchLayout.setVisibility(View.GONE);
            mPoiAddTitleLayout.setVisibility(View.GONE);
            mPoishowLayout.setVisibility(View.VISIBLE);
            tv_guide.setVisibility(View.GONE);
            if (mSearchPoi != null) {
                mPoiShowTitleTextview.setText(mSearchPoi.getAddrname());
            }
            mAddView.setImageResource(R.mipmap.location_to);
        }

        aMap.setOnMapClickListener(this);//添加map点击
        aMap.setOnCameraChangeListener(this);
        if (GlobalCfg.IS_POTRAIT) {
            aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);

            if (((HomeActivity) getActivity()).isThinCarMain) {//竖屏地图在主页面
                aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng lng) {
                        EventBus.getDefault().post(Constant.NOTIFY_MAINACT_SHOWMAP);
                    }
                });//添加map点击
            }
        } else {
            aMap.setOnMapClickListener(this);
        }

        aMap.setTrafficEnabled(true);
    }


    private boolean mIsFirstLocation=true;
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null) {


            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                mCurMapLocation=aMapLocation;
                if (mLocationConfirmDialog!=null){
                    mLocationConfirmDialog.dismiss();
                    mLocationConfirmDialog=null;
                }
                mCurLongitude = aMapLocation.getLongitude();
                mCurLatitude = aMapLocation.getLatitude();
                mCurAddress =aMapLocation.getAddress();
                mCurDiscrebe = aMapLocation.getProvince()+aMapLocation.getCity()+aMapLocation.getDistrict();
                myAddr = aMapLocation.getAddress() + "," + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude();
                city = aMapLocation.getCity();
                EcoApplication.getInstance().setLatitude(aMapLocation.getLatitude());
                EcoApplication.getInstance().setLongitude(aMapLocation.getLongitude());

                if (mSelectPoiMode ==1){
                    if (mIsFirstLocation){
                        setMap2SelectMaker();
                        Trace.Debug("##### initzoom");
                        currentZoom=14;
                        mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLatitude,mCurLongitude), currentZoom));
                        mIsFirstLocation=false;
                        markAddr = mCurAddress + "," + mCurMapLocation.getLatitude() + "," + mCurMapLocation.getLongitude();
                        mGeoDescribe.setText(aMapLocation.getAddress());
                        mGeoBuildings.setText("我的位置");
                        mDistanceView.setText("");
                        mSearchPoi=null;

                    }
                    if(mFromWidget){
                        Trace.Debug("##### from widget");
                        replaceFragmentByPoiSearch(isHome);
                        mFromWidget = false;
                    }

                }else if (mSelectPoiMode ==0){
                    addCircle(aMapLocation);
                    setCurLocationMaker(aMapLocation);
//                        mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                    if (mIsFirstLocation){
                        Trace.Debug("##### initzoom");
                        currentZoom=14;
                        mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLatitude,mCurLongitude), currentZoom));
                        mIsFirstLocation=false;

                    }
                    if(mFromWidget){
                        Trace.Debug("##### from widget");
                        if (isHome){
                            enAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HOME_ADDR, "");
                        }else{
                            enAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.WORK_ADDR, "");
                        }
                        setLantiAndLongti(enAddr);
                        chooseMap(CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_SELECT_KYE,0));
                        mFromWidget = false;
                    }

                }else if (mSelectPoiMode==2){
                    if (mSearchPoi!=null){

                        addCircle(aMapLocation);
                        setCurLocationMaker(aMapLocation);
                        double latitude=Double.valueOf(mSearchPoi.getLatitude());
                        double longitude=Double.valueOf(mSearchPoi.getLongitude());
                        mChosepoint = new LatLonPoint(latitude, longitude);
                        mDistinLatitude = mChosepoint.getLatitude();
                        mDistinLongitude = mChosepoint.getLongitude();
                        mDesAdressName = mSearchPoi.getAddrname();

                        mChooseLatLng=new LatLng(latitude,longitude);
                        mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(mChooseLatLng, currentZoom));


                        if (mChosePointMarker!=null){
                            mChosePointMarker.remove();
                            mChosePointMarker=null;
                        }
                        mChosePointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi))
                                .position(new LatLng(latitude,longitude)));
                        mChosePointMarker.showInfoWindow();
                        mChosePointMarker.setToTop();

                        markAddr = mDesAdressName + "," + mChosepoint.getLatitude() + "," + mChosepoint.getLongitude();
                        if (mChosepoint!=null){
                            mGeoResultlayout.setVisibility(View.VISIBLE);
                            Trace.Debug("####visible");
                        }
                        enableGuide();
                        if (mSearchPoi.getAddrname() != null) {
                            mGeoBuildings.setText(mSearchPoi.getAddrname());
                        }

                        mDistanceView.setVisibility(View.VISIBLE);
                        mDistanceView.setText(String.format(mContext.getString(R.string.map_distance), Utils.getDistance(new LatLng(mCurLatitude,mCurLongitude),mChooseLatLng)));
                        if (mSearchPoi.getLatitude()!=null){
                            mGeoDescribe.setText(mSearchPoi.getDistrict());
                        }
                        mSearchPoi=null;

                    }

                }

                city = aMapLocation.getCity();
                EcoApplication.getInstance().setAdCode(aMapLocation.getAdCode());
                EcoApplication.isLocation = true;
                WaitingAnimationDialog.close();

            } else {
                if (aMapLocation.getErrorCode()==AMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION||aMapLocation.getErrorCode()==AMapLocation.ERROR_CODE_FAILURE_NOWIFIANDAP){
                    showLocationDialog();

                }
                Trace.Debug("######permission error code ="+aMapLocation.getErrorCode() +"  "+aMapLocation.getErrorInfo());
                EcoApplication.isLocation = false;
            }
        }


    }

    private void addCircle(AMapLocation aMapLocation) {
        if (mCircle==null){
            CircleOptions options = new CircleOptions();
            options.strokeWidth(2f);
            options.fillColor(FILL_COLOR);
            options.strokeColor(STROKE_COLOR);
            options.center(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
            options.radius(aMapLocation.getAccuracy());
            mCircle = aMap.addCircle(options);
        }else{
            mCircle.setCenter(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
            mCircle.setRadius(aMapLocation.getAccuracy());

        }
    }


    private void setSelect2MapMaker() {
        if (mChosePointMarker!=null){
            mChosePointMarker.destroy();
        }
        if (mCurPointMarker==null){
            mCurPointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked))
                    .position(new LatLng(mCurMapLocation.getLatitude(),mCurMapLocation.getLongitude())));

        }else{
            mCurPointMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked));
            mCurPointMarker.setRotateAngle(0);
            mCurPointMarker.setPosition(new LatLng(mCurMapLocation.getLatitude(),mCurMapLocation.getLongitude()));

        }
        addCircle(mCurMapLocation);
    }


    private void setMap2SelectMaker() {
        if (mChosePointMarker!=null){
            mChosePointMarker.destroy();
        }

        if (mCurPointMarker==null){
            mCurPointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi))
                    .position(new LatLng(mCurMapLocation.getLatitude(),mCurMapLocation.getLongitude())));
        }else{
            mCurPointMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.navi));
            mCurPointMarker.setPosition(new LatLng(mCurMapLocation.getLatitude(),mCurMapLocation.getLongitude()));
            mCurPointMarker.setRotateAngle(0);

        }
        if (mCircle!=null){
            mCircle.remove();
            mCircle=null;
        }


    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mContext.getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setNeedAddress(true);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            Trace.Debug("***** 开始定位");
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {

        if (mlocationClient != null) {
            Trace.Debug("#####permission stop location");
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }





    private void setLantiAndLongti(String address){
        String[] array = address.split(",");
        mDistinLatitude = Double.valueOf(array[1]).doubleValue();
        mDistinLongitude = Double.valueOf(array[2]).doubleValue();
    }
    /**
     * 直接点击搜索
     */
    private void replaceFragmentByKeySearch(boolean isAddMode) {
        if (isAddMode){
            Bundle nBundle = new Bundle();
            nBundle.putBoolean(MapFragment.IS_HOME_ADDRESS, isHome);
            nBundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_ADD);
            ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().
                    replace(R.id.map_frame, KeySearchFragment.getInstance(nBundle)).commitAllowingStateLoss();
        }else {
            Bundle nBundle = new Bundle();
            nBundle.putString(RoutePlanFragment.ROUTEPLAN_START_ADDRESS, myAddr);
            nBundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_MAP);
            KeySearchFragment secondFragment = KeySearchFragment.getInstance(nBundle);
            ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment).commitAllowingStateLoss();
        }
    }





    /**
     * 去路径规划页面
     */
    private void replaceFragmentByRoutePlan() {

        Bundle nBundle = new Bundle();
        nBundle.putString(RoutePlanFragment.ROUTEPLAN_START_ADDRESS, myAddr);
        nBundle.putString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS, enAddr);
        nBundle.putString(RoutePlanFragment.LAUNCH_FRAGMENT, RoutePlanFragment.MAP);
        AMapNavi.getInstance(mContext).destroy();
        RoutePlanFragment secondFragment = RoutePlanFragment.getInstance(nBundle);
        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment,RoutePlanFragment.class.getSimpleName()).commitAllowingStateLoss();
        ((HomeActivity) mContext).isNavigating = true;
        ((HomeActivity) mContext).isInMapFragment = true;
        ((HomeActivity) mContext).isInEasyStop = false;
    }

    /**
     * 添加家或者公司
     *
     * @param isHome
     */
    private void replaceFragmentByPoiSearch(Boolean isHome) {
        if (mCurMapLocation==null){
            return;
        }

        if (mChosePointMarker!=null){
            mChosePointMarker.remove();
            mChosePointMarker=null;
        }
        if (mCurPointMarker!=null){
            mCurPointMarker.remove();
            mCurPointMarker=null;
        }

        this.isHome=isHome;
        mSelectPoiMode =1;
        mChosepoint=null;

        mMapSearchLayout.setVisibility(View.GONE);
        mPoiAddTitleLayout.setVisibility(View.VISIBLE);
        mGeoResultlayout.setVisibility(View.VISIBLE);
        mAddView.setImageResource(R.mipmap.map_ic_add1);
        tv_guide.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(mCurAddress)){
            String building = mCurAddress.replace(mCurDiscrebe,"");
            if(building == null || building.length()<=1){
                building = mCurAddress;
            }
            mGeoBuildings.setText(building);
            mGeoDescribe.setText(mCurDiscrebe);
        }
        currentZoom=14;
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurMapLocation.getLatitude(),mCurMapLocation.getLongitude()),currentZoom));
        mGeoResultlayout.setVisibility(View.VISIBLE);
        markAddr = mCurAddress + "," + mCurMapLocation.getLatitude() + "," + mCurMapLocation.getLongitude();
        mChosepoint=new LatLonPoint(mCurMapLocation.getLatitude(),mCurMapLocation.getLongitude());
        enableGuide();
        mLocationView.setImageResource(R.mipmap.localization);

//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapView.getLayoutParams();
//        layoutParams.addRule(RelativeLayout.BELOW,R.id.poi_search_layout);
//        mapView.setLayoutParams(layoutParams);
        if (isHome){
            mPoiAddTextView.setHint(getString(R.string.map_search_home));
        }else{
            mPoiAddTextView.setHint(getString(R.string.map_search_company));
        }

        setMap2SelectMaker();

    }


    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    ((HomeActivity) mContext).changeToHome();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }
    @Override
    public void onClick(View v) {
        isNetConnect = NetUtils.isConnected(mContext);

        switch (v.getId()) {
            case R.id.map_downlod:
                CacheUtils.getInstance(mContext).putBoolean(SettingCfg.MAP_DOWNLOAD,true);
                mDownloadLayout.setVisibility(View.GONE);
                break;
            case R.id.close_poi_show:
            case R.id.iv_back:
                if (mSelectPoiMode ==1||mSelectPoiMode ==2){
                    mChosepoint=null;
                    mSelectPoiMode =0;
                    mLocationView.setImageResource(R.mipmap.north_up_mode);
                    mCarMode=0;
                    mInitCarMode=0;

                    mMapSearchLayout.setVisibility(View.VISIBLE);
                    mPoiAddTitleLayout.setVisibility(View.GONE);
                    mPoishowLayout.setVisibility(View.GONE);
                    mAddView.setImageResource(R.mipmap.location_to);
                    tv_guide.setVisibility(View.VISIBLE);
                    mGeoResultlayout.setVisibility(View.GONE);
                    Trace.Debug("******* gone");
//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapView.getLayoutParams();
//                layoutParams.addRule(RelativeLayout.BELOW,R.id.poi_title);
//                mapView.setLayoutParams(layoutParams);
                    currentZoom=14;
                    setSelect2MapMaker();
                    if(mCurLatitude == 0 && mCurLongitude == 0){
                        return;
                    }else{
                        mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLatitude,mCurLongitude), currentZoom));
                    }

                }
                break;

            case R.id.poi_show_back:
                replaceFragmentByKeySearch(false);
                LetvReportUtils.reportMapSearchEvent();
                break;
            case R.id.map_search:
                replaceFragmentByKeySearch(false);
                LetvReportUtils.reportMapSearchEvent();
                break;
            case R.id.iv_teach_confirm:
                rlt_teach.setVisibility(View.GONE);
                break;
            case R.id.iv_addzoom:
                currentZoom=aMap.getCameraPosition().zoom;
                if(currentZoom<maxZoom) {
                    currentZoom = currentZoom + 1;
//                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLatitude,mCurLongitude), currentZoom));
                    aMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom));
                }

                break;
            case R.id.iv_reducezoom:
                currentZoom=aMap.getCameraPosition().zoom;
                if(currentZoom>minZoom){
                    currentZoom = currentZoom - 1;
//                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLatitude,mCurLongitude), currentZoom));
                    aMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom));
                }

                break;
            case R.id.iv_localization:
                if (mCurMapLocation==null){
                    return;
                }
                if (mSelectPoiMode ==1){
                    setMap2SelectMaker();
                    mChosepoint=null;
                    currentZoom=14;
                    if(mCurLatitude == 0 && mCurLongitude == 0){
                        return;
                    }else{
                        mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLatitude,mCurLongitude), currentZoom));
                        mapView.getMap().moveCamera(CameraUpdateFactory.changeBearing(0));
                    }
                    if(!TextUtils.isEmpty(mCurAddress)){
                        String building = mCurAddress.replace(mCurDiscrebe,"");

                        if(building == null || building.length()<=1){
                            building = mCurAddress;
                        }
                        mGeoBuildings.setText(building);
                        mGeoDescribe.setText(mCurDiscrebe);
                    }
                    mGeoResultlayout.setVisibility(View.VISIBLE);
                    enableGuide();
                }else if (mSelectPoiMode==2){

                    if (mCarMode==2){
                        mCarMode=0;
                        mInitCarMode=0;
                        mLocationView.setImageResource(R.mipmap.north_up_mode);
                        mChosepoint=null;
                        setNorthUpMode();
                        currentZoom=14;
                        if(mCurLatitude == 0 && mCurLongitude == 0){
                            return;
                        }else{
                            mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLatitude,mCurLongitude), currentZoom));
                            mapView.getMap().moveCamera(CameraUpdateFactory.changeBearing(0));
                        }

                    }else if (mCarMode==0){
                        mCarMode=1;
                        mInitCarMode=1;
                        mLocationView.setImageResource(R.mipmap.car_up_mode);
                        setCarUpMode();
                    }else{
                        mCarMode=0;
                        mInitCarMode=0;
                        mLocationView.setImageResource(R.mipmap.north_up_mode);
                        setNorthUpMode();

                    }




                }else if (mSelectPoiMode==0){
                    if (mCarMode==2){
                        mCarMode=0;
                        mInitCarMode=0;
                        mLocationView.setImageResource(R.mipmap.north_up_mode);
                        mChosepoint=null;
                        setSelect2MapMaker();
                        currentZoom=14;
                        if(mCurLatitude == 0 && mCurLongitude == 0){
                            return;
                        }else{
                            mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurLatitude,mCurLongitude), currentZoom));
                            mapView.getMap().moveCamera(CameraUpdateFactory.changeBearing(0));
                        }
                        mGeoBuildings.setText("我的位置");
                        mGeoDescribe.setText(mCurMapLocation.getAddress());
                        mDistanceView.setText("");
                        mDistanceView.setVisibility(View.GONE);

                        mGeoResultlayout.setVisibility(View.VISIBLE);
                        disableGuide();
                    }else if (mCarMode==0){
                        mCarMode=1;
                        mInitCarMode=1;
                        mLocationView.setImageResource(R.mipmap.car_up_mode);
                        setCarUpMode();
                    }else{
                        mCarMode=0;
                        mInitCarMode=0;
                        mLocationView.setImageResource(R.mipmap.north_up_mode);
                        setNorthUpMode();

                    }
                }
                break;
            case R.id.geo_result_layout:
                if (mSelectPoiMode ==1){
                    saveAddressData(markAddr);
                    mSelectPoiMode =0;
                    mChosepoint=null;
                    currentZoom=14;
                    replaceFragmentByKeySearch(false);
                }else {
                        if (TextUtils.isEmpty(markAddr)) {
                            enAddr = myAddr;
                        } else {
                            enAddr = markAddr;
                        }

                        chooseMap(CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_SELECT_KYE,0));
                    }

                break;

            case R.id.traffic:
                if (mTrafficOpened==0){
                    mTrafficOpened=1;
                    mTrafficView.setImageResource(R.mipmap.traffic_close_day);
                    aMap.setTrafficEnabled(false);

                }else {
                    mTrafficOpened=0;
                    mTrafficView.setImageResource(R.mipmap.traffic_open_day);
                    aMap.setTrafficEnabled(true);

                }
                CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_TRAFFIC_ON_OFF,mTrafficOpened);

                break;
            case R.id.poi_add_title:
                replaceFragmentByKeySearch(true);
                break;
            default:
                break;
        }

    }




    final String[] mItems = {"ecolink","高德","百度"};



    private void chooseMap(int which){

        if (myAddr.equals(enAddr)){
            ToastUtil.show(mContext,"你已经在目的地");
            return;
        }
        switch (which){
            case  0:
                replaceFragmentByRoutePlan();

                break;
            case 1:

                startGaoDeMap();

                break;
            case 2:
                startBaiDuMap();
                break;
        }
    }



    private void saveAddressData(String data) {
        if (data != null && !data.equals("") && !data.equals("null")) {
            if (isHome) {
                CacheUtils.getInstance(mContext).putString(Constant.SpConstant.HOME_ADDR, data);
            } else {
                CacheUtils.getInstance(mContext).putString(Constant.SpConstant.WORK_ADDR, data);
            }
        } else {
            ToastUtil.show(mContext, "没有获取到地址，请检查网络");
            Trace.Debug("######  no address");
        }
    }


    private void startBaiDuMap() {
        Intent intent;
        if(DeviceUtils.isAvilible(mContext,"com.baidu.BaiduMap")){
            notifyCurrentThirdAppPage();
            try {
                com.baidu.mapapi.model.LatLng sourceLatLng= new com.baidu.mapapi.model.LatLng(mDistinLatitude,mDistinLongitude);
                CoordinateConverter converter  = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.COMMON);
// sourceLatLng待转换坐标
                converter.coord(sourceLatLng);
                com.baidu.mapapi.model.LatLng desLatLng = converter.convert();
                intent = Intent.getIntent("intent://map/direction?" +
                        //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
                        "destination=latlng:"+ desLatLng.latitude +","+ desLatLng.longitude +"|name:"+mDesAdressName+        //终点
                        "&mode=driving&" +          //导航路线方式
                        "region="+ city +           //
                        "&src=ecolink#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                //intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&src=thirdapp.navi.yourCompanyName.yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");


                mContext.startActivity(intent); //启动调用
                if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                    ScreenRotationUtil.startLandService(mContext,"com.baidu.BaiduMap");
                }
            } catch (URISyntaxException e) {
                Log.e("intent", e.getMessage());
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

    private void startGaoDeMap() {
        if(DeviceUtils.isAvilible(mContext,"com.autonavi.minimap")){
            notifyCurrentThirdAppPage();
            try{
                Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=ecolink&poiname="+mDesAdressName+"&lat=" + mDistinLatitude + "&lon=" + mDistinLongitude + "&dev=0&style=2");
                mContext.startActivity(intent);
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



    //map 点击图标
    @Override
    public void onMapClick(LatLng latLng) {
        if (mSelectPoiMode!=2){
        Trace.Debug("#### onmap click latlng="+latLng.toString());
        /** 瘦车机半屏模式忽略点击事件*/
        if (mIsHalfMode) {
            return;
        }

        mChooseLatLng = latLng;

        mChosepoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        mDistinLatitude = mChosepoint.getLatitude();
        mDistinLongitude = mChosepoint.getLongitude();
        getAddress(mChosepoint);
        if (mSelectPoiMode ==1){
            if (mCurPointMarker!=null){
                mCurPointMarker.destroy();
                mCurPointMarker=null;
            }
        }
        if (mChosePointMarker!=null){
            mChosePointMarker.destroy();
        }
        mChosePointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi))
                .position(latLng));
        mChosePointMarker.showInfoWindow();
        mChosePointMarker.setToTop();}
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                RegeocodeAddress address = result.getRegeocodeAddress();
                mDesAdressName = address.getFormatAddress();
                mDistinLatitude = mChosepoint.getLatitude();
                mDistinLongitude = mChosepoint.getLongitude();
                String addressRange = address.getProvince()+address.getCity()+address.getDistrict()
                        +address.getTownship();
                markAddr = mDesAdressName + "," + mChosepoint.getLatitude() + "," + mChosepoint.getLongitude();
                if (mChosepoint!=null){
                    mGeoResultlayout.setVisibility(View.VISIBLE);
                    Trace.Debug("####visible");
                }
                enableGuide();
                String building = mDesAdressName.replace(addressRange,"");
                if(building == null || building.length()<=1){
                    building = mDesAdressName;
                }
                mGeoDescribe.setText(addressRange);
                mGeoBuildings.setText(building);
                mDistanceView.setVisibility(View.VISIBLE);
                mDistanceView.setText(String.format(mContext.getString(R.string.map_distance), Utils.getDistance(new LatLng(mCurLatitude,mCurLongitude),mChooseLatLng)));
                if (mPoishowLayout.getVisibility() == View.VISIBLE) {
                    mPoiShowTitleTextview.setText(building);
                }
            } else {
                ToastUtil.show(mContext, R.string.str_no_result);
            }
        } else if (rCode == 27) {
            ToastUtil.show(mContext,R.string.net_erro_toast);
        } else if (rCode == 32) {
            ToastUtil.show(mContext, R.string.str_key_erro);
            //mLocationDesTextView.setText("key无效");
        } else {
            ToastUtil.show(mContext,R.string.net_erro_toast);
            //mLocationDesTextView.setText(rCode);
        }


    }
    private void setCarUpMode() {
        if (mCurMapLocation==null)
            return;
        aMap.moveCamera(CameraUpdateFactory.changeBearing(360-mAngle));
        aMap.moveCamera(CameraUpdateFactory.changeTilt(30));
        currentZoom=18;
        aMap.moveCamera(CameraUpdateFactory.zoomTo(currentZoom));
        if (mCurPointMarker==null){
            mCurPointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_3d))
                    .position(new LatLng(mCurMapLocation.getLatitude(),mCurMapLocation.getLongitude())));
        }else{
            mCurPointMarker.setPosition(new LatLng(mCurMapLocation.getLatitude(),mCurMapLocation.getLongitude()));
            mCurPointMarker.setRotateAngle(0);
            mCurPointMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_3d));
        }
    }

    private void setNorthUpMode() {
        if (mCurMapLocation==null)
            return;
        aMap.moveCamera(CameraUpdateFactory.changeBearing(0));
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        currentZoom=14;
        aMap.moveCamera(CameraUpdateFactory.zoomTo(currentZoom));
        Trace.Debug("##### angle ="+mAngle+ "  "+aMap.getCameraPosition().bearing);

        if (mCurPointMarker==null) {
            mCurPointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked))
                    .position(new LatLng(mCurMapLocation.getLatitude(), mCurMapLocation.getLongitude())).rotateAngle(mAngle + aMap.getCameraPosition().bearing));
        }else {
            mCurPointMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked));
            mCurPointMarker.setPosition(new LatLng(mCurMapLocation.getLatitude(), mCurMapLocation.getLongitude()));
            mCurPointMarker.setRotateAngle(mAngle + aMap.getCameraPosition().bearing);

        }

    }

    @Override
    public void setRotateAngle(float angle) {
        mAngle=angle;
        if (mSelectPoiMode ==0||mSelectPoiMode ==2){
            if (mInitCarMode==0){
                if (mCurPointMarker!=null){
                    Trace.Debug("##### angle ="+angle+ "  "+aMap.getCameraPosition().bearing);
                    mCurPointMarker.setRotateAngle(angle+aMap.getCameraPosition().bearing);
                }
            }else {
                aMap.animateCamera(CameraUpdateFactory.changeBearing(360-angle));
            }
        }

    }
    private float mAngle;
    private void setCurLocationMaker(AMapLocation aMapLocation) {
        if (mCurPointMarker!=null){
//            mCurPointMarker.destroy();
            if (mInitCarMode==0){

                mCurPointMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked));
                mCurPointMarker.setPosition(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
                mCurPointMarker.setRotateAngle(mAngle+aMap.getCameraPosition().bearing);
            }else {
//                mCurPointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
//                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_3d))
//                        .position(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude())));

                mCurPointMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_3d));
                mCurPointMarker.setPosition(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
                mCurPointMarker.setRotateAngle(0);
            }

        }else {
            if (mInitCarMode==0){
                mCurPointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked))
                        .position(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude())).rotateAngle(mAngle+aMap.getCameraPosition().bearing));
            }else {
                mCurPointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_3d))
                        .position(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude())));
            }
        }





    }
    @Override
    public void onCameraChange(CameraPosition cameraPosition){
        if (mCurMapLocation==null){
            return;
        }
        if (mSelectPoiMode ==0){
            if (mInitCarMode==0){
                if (mCurPointMarker!=null){
                    Trace.Debug("##### angle ="+mAngle+ "  "+aMap.getCameraPosition().bearing);
                    mCurPointMarker.setRotateAngle(mAngle+aMap.getCameraPosition().bearing);
                }
            }
        }
        LatLng cameraLatLng= new LatLng(Utils.getSixDouble(cameraPosition.target.latitude),Utils.getSixDouble(cameraPosition.target.longitude));
        LatLng curLatLng= new LatLng(Utils.getSixDouble(mCurMapLocation.getLatitude()),Utils.getSixDouble(mCurMapLocation.getLongitude()));

        if (cameraLatLng.latitude!=curLatLng.latitude||cameraLatLng.longitude!=curLatLng.longitude){
            Trace.Debug("#####camera "+cameraLatLng.latitude+"  "+cameraLatLng.longitude);
            Trace.Debug("#####curLatLng "+curLatLng.latitude+"  "+curLatLng.longitude);
            mCarMode=2;
            mLocationView.setImageResource(R.mipmap.localization);
            if (mSelectPoiMode ==0){
                if (mChosepoint==null){
                    mGeoResultlayout.setVisibility(View.GONE);
                    Trace.Debug("******* gone");}}
        }

    }
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition){
//        Trace.Debug("#####  camera finish  ");

        if(currentZoom >= maxZoom){
            addzoom.setImageResource(R.mipmap.map_zoom_out_grey);
        }
        else if(currentZoom <= minZoom){
            reducezoom.setImageResource(R.mipmap.map_zoom_in_grey);
        }
        else{
            addzoom.setImageResource(R.mipmap.map_zoom_out);
            reducezoom.setImageResource(R.mipmap.map_zoom_in);
        }



    }



    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {

    }

    private void  disableGuide(){
        divider_line.setVisibility(View.GONE);
        mAddView.setVisibility(View.GONE);
        tv_guide.setVisibility(View.GONE);
        mGeoResultlayout.setOnClickListener(null);
    }

    private void  enableGuide(){
        divider_line.setVisibility(View.VISIBLE);
        mAddView.setVisibility(View.VISIBLE);
        if (mSelectPoiMode ==1){
            tv_guide.setVisibility(View.GONE);
        }else{
            tv_guide.setVisibility(View.VISIBLE);
        }

        mGeoResultlayout.setOnClickListener(this);
    }

    @Override
    public void update(Observable observable, final Object data) {
        setNightForThincar();
        setThincarMapModeDelay();
    }

    /**
     * 设置地图类型
     * @param mapType
     */
    public void setMapType(final int mapType){
        if(handler!=null){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(aMap!=null){
                        aMap.setMapType(mapType);
                    }
                }
            }, 0);
        }
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
    public void onCalculateRouteSuccess() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

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
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

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
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    private static class MapDownloadReceiver extends BroadcastReceiver{
        WeakReference<MapFragment> ref;

        public MapDownloadReceiver(MapFragment mapFragment) {
            this.ref = new WeakReference<MapFragment>(mapFragment);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SettingCfg.MAP_DOWNLOAD)){
                Trace.Debug("##### MapDownloadReceiver ");
                if (ref==null){
                    return;
                }

                MapFragment mapFragment=ref.get();
                if(mapFragment!=null){
                    mapFragment.setDownloadLayoutGone();
                }
            }

        }
    }
    private void setDownloadLayoutGone(){
        mDownloadLayout.setVisibility(View.GONE);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEvent(Integer i) {

        switch(i) {
            case Constant.SHOW_MAP_TOPVIEW:
                if (GlobalCfg.IS_POTRAIT) {

                    ThincarUtils.getInstance(mContext).cameraPosition(aMap);
                    aMap.setOnMapClickListener(this);//添加map点击
//                if (isMapClick) {
//                    mGeoResultlayout.setVisibility(View.VISIBLE);
//                }
                }
                break;
            case Constant.HIDE_MAP_TOPVIEW:
                if (GlobalCfg.IS_POTRAIT) {
                    aMap.stopAnimation();

                    mGeoResultlayout.setVisibility(View.GONE);
                    Trace.Debug("******* gone");
                    aMap.setOnMapClickListener(new AMap.OnMapClickListener() {//点击map后进入导航页面
                        @Override
                        public void onMapClick(LatLng lng) {
                            EventBus.getDefault().post(Constant.NOTIFY_MAINACT_SHOWMAP);//通知
                        }
                    });

                }
                break;
            case Constant.MAP_HALF_SCREEN:
                halfScreen();
                break;
            case Constant.MAP_RESTORE_SCREEN:
                GlobalCfg.mNeedPlayAnim = true;
                mapView.setVisibility(View.VISIBLE);
                other_icon_layout.setVisibility(View.VISIBLE);
                restoreScreen();
                break;
            case Constant.POI_SELECT_HOME_ADDRESS:
                mDistanceView.setText("");
                mShouldPoilayoutRestore = false;
                replaceFragmentByPoiSearch(true);
                break;
            case Constant.POI_SELECT_WORK_ADDRESS:
                mDistanceView.setText("");
                mShouldPoilayoutRestore = false;
                replaceFragmentByPoiSearch(false);
                break;
            case Constant.DRIVING:
                toyotaRule(true);
                break;
            case Constant.NO_DRIVE:
                toyotaRule(false);
                break;
        }
    }

    public void startNaviFromThincar(String naviType) {
        String target = null;
        if (naviType.equals(NaviBarSendHelp.HOME_TAG)) {
            target =  CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HOME_ADDR, null);
        } else if(naviType.equals(NaviBarSendHelp.WORK_TAG)) {
            target =  CacheUtils.getInstance(mContext).getString(Constant.SpConstant.WORK_ADDR, null);
        }
        if (target == null) {
            return;
        }
        Bundle nBundle = new Bundle();
        nBundle.putString(RoutePlanFragment.ROUTEPLAN_START_ADDRESS, myAddr);
        nBundle.putString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS, target);
        nBundle.putString(RoutePlanFragment.LAUNCH_FRAGMENT, RoutePlanFragment.MAP);
        RoutePlanFragment secondFragment = RoutePlanFragment.getInstance(nBundle);
        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment,RoutePlanFragment.class.getSimpleName()).commitAllowingStateLoss();
        ((HomeActivity) mContext).isNavigating = true;
        ((HomeActivity) mContext).isInMapFragment = true;
        ((HomeActivity) mContext).isInEasyStop = false;
    }

    private void restorePointMarker() {
        if (mChooseLatLng != null) {
            mChosePointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi))
                    .position(mChooseLatLng));
            mChosePointMarker.showInfoWindow();
            mChosePointMarker.setToTop();
        } else {
            if (mSelectPoiMode ==1 && mCurMapLocation != null){
                mCurPointMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi))
                        .position(new LatLng(mCurMapLocation.getLatitude(),mCurMapLocation.getLongitude())));
            }
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

        aMap.setCustomMapStylePath(filePath + "/" + styleName);
        aMap.setMapCustomEnable(true);

    }

    private void setNightForThincar() {
        if (aMap != null) {
//            setThincarMapMode();
            aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    setThincarMapMode();
                    if (mTrafficOpened==0){
                        aMap.setTrafficEnabled(true);
                    }else{
                        aMap.setTrafficEnabled(false);
                    }
                }
            });
        }
    }

    private void setThincarMapMode() {
        if(HomeActivity.isThinCar){
            aMap.setMapType(AMap.MAP_TYPE_NIGHT);
        }else{
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        }
    }

    private void setThincarMapModeDelay() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(HomeActivity.isThinCar){
                    aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                }else{
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                }
            }
        }, 2000);
    }
    public void setZoomOut(boolean b) {
        Trace.Debug("map"," zoom out "+b);
        if (aMap!=null){
            if (b){
                aMap.animateCamera(CameraUpdateFactory.zoomOut());
            }else {
                aMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        }
    }

    private void notifyCurrentThirdAppPage() {
        GlobalCfg.IS_THIRD_APP_STATE = true;
        GlobalCfg.isCarResumed = false;
        DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                ThinCarDefine.PageIndexDefine.THIRAD_APP_PAGE,0);
    }

    /**
     *
     * 丰田致炫的走行规制
     *   行驶过程中，地图页面按钮不能触控
     * @param tag true表示在行驶中, false表示非行驶
     */
    private void toyotaRule(boolean tag){
        if(tag){
            view.findViewById(R.id.title).setVisibility(View.GONE);
            addzoom.setVisibility(View.GONE);
            reducezoom.setVisibility(View.GONE);
            mTrafficView.setVisibility(View.GONE);
            mLocationView.setVisibility(View.GONE);
        }else{
            view.findViewById(R.id.title).setVisibility(View.VISIBLE);
            addzoom.setVisibility(View.VISIBLE);
            reducezoom.setVisibility(View.VISIBLE);
            mTrafficView.setVisibility(View.VISIBLE);
            mLocationView.setVisibility(View.VISIBLE);
        }
    }
}