package com.letv.leauto.ecolink.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.database.model.ParkingDetail;
import com.letv.leauto.ecolink.json.ParkingParse;
import com.letv.leauto.ecolink.lemap.entity.SearchPoi;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.page.EasyStopPage;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.DistanceUtil;
import com.letv.leauto.ecolink.utils.EasyStopDetailThread;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class EasyStopFragment extends BaseFragment implements LocationSource,
        AMapLocationListener, View.OnClickListener, View.OnLongClickListener, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnCameraChangeListener{
    @Bind(R.id.easy_stop_map)
    MapView mapView;

    @Bind(R.id.easy_stop_viewpager)
    ViewPager easy_stop_viewpager;

    @Bind(R.id.rlt_teach)
    RelativeLayout rlt_teach;

    @Bind(R.id.iv_layout)
    RelativeLayout iv_layout;

    @Bind(R.id.iv_teach_confirm)
    ImageView iv_teach_confirm;

    @Bind(R.id.iv_addzoom)
    ImageView addzoom;

    @Bind(R.id.iv_reducezoom)
    ImageView reducezoom;

    @Bind(R.id.iv_localization)
    ImageView iv_loca;




    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    Bundle savedInstanceState;
    String city = null;
    String myAddr = null;
    String enAddr = null;
    String homeAddr = null;
    String workAddr = null;
    List<SearchPoi> mHisList = new ArrayList<SearchPoi>();
    //选择的图标
    private LatLonPoint chosepoint;
    private GeocodeSearch geocoderSearch;
    private Marker locationMarker;
    private String markAddr;//选择的地点信息
    private String addressName;
    private double la;
    private double lo;
    private String cur_add;
    public static final int RECEIVE_DETAIL = 1;
    public static final int SET_POINT_TO_CENTER = 2;
    private Marker lastMarker;
    private ArrayList<ParkingDetail> mEasyStopMarkerDetailList;
    private ArrayList<EasyStopPage> mEasyStopPages = new ArrayList<>();
    private ArrayList<Marker> parkList;
    private int current_marker_position = 0;
    private int scope = 1000;

    private float currentZoom=14;


    private  double lat;
    private  double lnt;

    private Handler mEasyStopHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RECEIVE_DETAIL:
                    String result = String.valueOf(msg.obj);
                    getMarkerDetailList(result);
                    if(scope != 3000){
                        if(mEasyStopMarkerDetailList == null || mEasyStopMarkerDetailList.size()<10){
                            if(mEasyStopMarkerDetailList != null){
                                mEasyStopMarkerDetailList.clear();
                            }
                            scope = 3000;
                            EasyStopDetailThread.getInstance().startSearchPark(la, lo, scope);
                            Toast.makeText(mContext, R.string.str_station_limit_toast, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    displayParkinglots();
                    voiceSpeak(mContext.getString(R.string.str_more_stop));
                    break;
                case SET_POINT_TO_CENTER:
                    LatLng ll = new LatLng(chosepoint.getLatitude(), chosepoint.getLongitude());
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, currentZoom));
                    break;
                default:
                    break;
            }
        }
    };

    public static EasyStopFragment getInstance(Bundle bundle) {
        EasyStopFragment mFragment = new EasyStopFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_easy_stop, null);
        } else {
            view = inflater.inflate(R.layout.fragment_easy_stop_l, null);
        }
        ButterKnife.bind(this, view);
//        mMapSearchTextView.setText(getResources().getString(R.string.click_start_seatch));
        if (isNetConnect) {
            setUpMap();
        } else {
            ToastUtil.show(mContext, getResources().getString(R.string.check_network));
            EcoApplication.isLocation = false;
        }
        iv_teach_confirm.setClickable(true);
        iv_teach_confirm.setOnClickListener(this);
        //初始化地理编码服务
        geocoderSearch = new GeocodeSearch(getActivity());
        geocoderSearch.setOnGeocodeSearchListener(this);

        intTypeFace();
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
//        initHomeAndWorkAddrs();

        //历史记录
//        initHistoryData();
//        hisAdapter = new HistoryAdapter(mHisList, mContext, false);
//        mHistoryListView.setAdapter(hisAdapter);
//        mHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mEndAddr = mHisList.get(position).getAddrname() + "," + mHisList.get(position).getLatitude() + "," + mHisList.get(position).getLongitude();
//                replaceFragmentByRoutePlan();
//            }
//        });
    }

    private void intTypeFace() {

//        mHomeLayout.setOnClickListener(this);
//        mCompanyLayout.setOnClickListener(this);
//        searchView.setOnClickListener(this);
//        mHomeLayout.setOnLongClickListener(this);
//        mCompanyLayout.setOnLongClickListener(this);
        addzoom.setOnClickListener(this);
        reducezoom.setOnClickListener(this);
        iv_loca.setOnClickListener(this);

//        nav_park.setOnClickListener(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
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
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        Trace.Debug("EasyStopFragment", "onDestroy");
        super.onDestroy();
        ttsHandlerController.stop();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        mEasyStopHandler.removeMessages(RECEIVE_DETAIL);
        mEasyStopHandler.removeMessages(SET_POINT_TO_CENTER);
        mEasyStopHandler = null;
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mapView.onCreate(savedInstanceState);// 此方法必须重写
        }

        // 自定义系统定位蓝点
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setOnCameraChangeListener(this);

        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setScaleControlsEnabled(true);

        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        //设置定位园的颜色为透明色
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.radiusFillColor(getResources().getColor(R.color.transparent));
        myLocationStyle.strokeColor(getResources().getColor(R.color.transparent));
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_3d));

        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setOnMapClickListener(this);//添加map点击

        if (GlobalCfg.IS_POTRAIT) {
            aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        }

        aMap.setTrafficEnabled(true);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {

            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                String errText = mContext.getString(R.string.str_location_success) + "," + aMapLocation.getLocationDetail() + ": " + aMapLocation.getLongitude()
                        + ": " + aMapLocation.getLatitude();
                lo = aMapLocation.getLongitude();
                la = aMapLocation.getLatitude();

                locali_do();
                scope = 1000;
                EasyStopDetailThread.getInstance().setThreadHandler(mEasyStopHandler);
                EasyStopDetailThread.getInstance().startSearchPark(la, lo, scope);
                city = aMapLocation.getCity();

                cur_add=aMapLocation.getAddress();

                lat=aMapLocation.getLatitude();
                lnt=aMapLocation.getLongitude();

                myAddr = aMapLocation.getAddress() + "," + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude();
//                mGeoDescribe.setText(aMapLocation.getAddress());

                EcoApplication.isLocation = true;
                Trace.Error("Location", errText);
            } else {
                String errText = mContext.getString(R.string.str_location_faild) + "," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Trace.Error("AmapErr", errText);
                EcoApplication.isLocation = false;
            }
        }
        mlocationClient.stopLocation();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mContext);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setNeedAddress(true);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    private void initHomeAndWorkAddrs() {
//        mHomeAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HOME_ADDR, null);
//        if (mHomeAddr == null) {
//            mHomeImgView.setBackgroundResource(R.mipmap.map_ic_add);
//            mHomeLayout.setBackgroundResource(R.drawable.item_unselect_selector);
//        } else {
//            mHomeImgView.setBackgroundResource(R.mipmap.map_ic_home);
//            mHomeLayout.setBackgroundResource(R.drawable.item_select_selector);
//        }
//        mCompanyAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.WORK_ADDR, null);
//        if (mCompanyAddr == null) {
//            mCompanyImgView.setBackgroundResource(R.mipmap.map_ic_add);
//            mCompanyLayout.setBackgroundResource(R.drawable.item_unselect_selector);
//        } else {
//            mCompanyImgView.setBackgroundResource(R.mipmap.map_ic_office);
//            mCompanyLayout.setBackgroundResource(R.drawable.item_select_selector);
//        }
//
//        if (mHomeAddr != null || mCompanyAddr != null) {
//
//            if (CacheUtils.getInstance(mContext).getBoolean(Constant.IS_FIRST_TIME_MAP, true)) {
//                CacheUtils.getInstance(mContext).putBoolean(Constant.IS_FIRST_TIME_MAP, false);
//                rlt_teach.bringToFront();
//                rlt_teach.setVisibility(View.VISIBLE);
//                rlt_teach.setOnClickListener(this);
//
//
//            } else {
//                rlt_teach.setVisibility(View.GONE);
//                iv_teach_confirm.setClickable(false);
//            }
//
//        }

    }

    /**
     * 获取缓存的搜索历史数据
     */
    /**
     private void initHistoryData() {
     String result = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HISTORY_SEARCHKEY, null);
     if (result == null) {
     mHistoryListView.setVisibility(View.GONE);
     if (!GlobalCfg.IS_POTRAIT) {
     mHistoryEmptyView.setVisibility(View.VISIBLE);
     }
     return;
     }

     String[] array = result.split(";");

     SearchPoi poi;
     mHisList.clear();
     for (int i = array.length - 1; i >= 0; i--) {

     String[] items = array[i].split(",");
     poi = new SearchPoi();
     poi.setAddrname(items[0]);
     poi.setLatitude(items[1]);
     poi.setLongitude(items[2]);
     mHisList.add(poi);
     }

     if (mHisList.size() > 15) {
     String strs = "";
     String str = "";
     for (int i = 14; i >= 0; i--) {
     str = mHisList.get(i).getAddrname() + "," + mHisList.get(i).getLatitude() + "," + mHisList.get(i).getLongitude();
     if (i == 0) {
     strs = strs + str;
     } else {
     strs = strs + str + ";";
     }
     }
     CacheUtils.getInstance(mContext).putString(Constant.SpConstant.HISTORY_SEARCHKEY, strs);
     }
     }
     */
    /**
     * 直接点击搜索
     */
    private void replaceFragmentByKeySearch() {
        Bundle nBundle = new Bundle();
        nBundle.putString(RoutePlanFragment.ROUTEPLAN_START_ADDRESS, myAddr);
        nBundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_MAP);
        KeySearchFragment secondFragment = KeySearchFragment.getInstance(nBundle);
        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.easy_stop_frame, secondFragment).commitAllowingStateLoss();
    }

    public void startToNavi(){
        if (TextUtils.isEmpty(markAddr)) {
            enAddr = myAddr;
        } else {
            enAddr = markAddr;
        }

        replaceFragmentByRoutePlan();
    }
    /**
     * 去路径规划页面
     */
    private void replaceFragmentByRoutePlan() {

        Bundle nBundle = new Bundle();
        nBundle.putString(RoutePlanFragment.ROUTEPLAN_START_ADDRESS, myAddr);
        nBundle.putString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS, enAddr);
        nBundle.putString(RoutePlanFragment.LAUNCH_FRAGMENT, RoutePlanFragment.EASY_STOP);
        RoutePlanFragment secondFragment = RoutePlanFragment.getInstance(nBundle);
        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.easy_stop_frame, secondFragment).commitAllowingStateLoss();
        ((HomeActivity) mContext).isNavigating = true;
        ((HomeActivity) mContext).isInMapFragment = false;
        ((HomeActivity) mContext).isInEasyStop = true;
    }

    /**
     * 添加家或者公司
     *
     * @param isHome
     */
    private void replaceFragmentByPoiSearch(Boolean isHome) {
        Bundle nBundle = new Bundle();
//        nBundle.putBoolean(PoiSearchFragment.IS_HOME_ADDRESS, isHome);
//        nBundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_MAP);
//        PoiSearchFragment secondFragment = PoiSearchFragment.getInstance(nBundle);
//        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment).commitAllowingStateLoss();
    }

    @Override
    public boolean onLongClick(View v) {
//        switch (v.getId()) {
//            case R.id.rlt_home:
//                replaceFragmentByPoiSearch(true);
//                break;
//            case R.id.rlt_work:
//                replaceFragmentByPoiSearch(false);
//                break;
//            default:
//                break;
//        }
        return false;
    }

    @Override
    public void onClick(View v) {
        isNetConnect = NetUtils.isConnected(mContext);
        if (isNetConnect) {
            switch (v.getId()) {
                case R.id.iv_teach_confirm:
                    rlt_teach.setVisibility(View.GONE);
                    break;
                case R.id.iv_addzoom:
                    if(currentZoom<18) {
                        currentZoom = currentZoom + 1;
                        aMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom));
                    }
                    break;
                case R.id.iv_reducezoom:
                    if(currentZoom>3){
                        currentZoom = currentZoom - 1;
                        aMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom));
                    }
                    break;
                case R.id.iv_localization:
                    locali_do();
                    lat=la;
                    lnt=lo;
//                    mGeoResultlayout.setVisibility(View.VISIBLE);
//                    if(!TextUtils.isEmpty(cur_add)){
//                        mGeoDescribe.setText(cur_add);
//                    }
                    break;
                default:
                    break;
            }
        } else {
            ToastUtil.show(mContext, getResources().getString(R.string.check_network));
        }
    }

    final String[] mItems = {"ecolink","高德","百度"};

    private void ShowDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("请选择");

        builder.setItems(mItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //点击后弹出窗口选择了第几项
//               ToastUtil.show(mContext,mItems[which]);
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
        });
        builder.create().show();
    }

    private void startBaiDuMap() {
        Intent intent;
        if(DeviceUtils.isAvilible(mContext,"com.baidu.BaiduMap")){

            try {
//                          intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving®ion=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                intent = Intent.getIntent("intent://map/direction?" +
                        //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
                        "destination=latlng:"+lat+","+lnt+"|name:我的目的地"+        //终点
                        "&mode=driving&" +          //导航路线方式
                        "region=北京" +           //
                        "&src=ecolink#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                //intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&src=thirdapp.navi.yourCompanyName.yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");


                mContext.startActivity(intent); //启动调用
            } catch (URISyntaxException e) {
                Trace.Error("intent", e.getMessage());
            }
        }else{//未安装
            //market为路径，id为包名
            //显示手机上所有的market商店
            ToastUtil.show(mContext, R.string.str_install_baidu_map);
//            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }


    }

    private void startGaoDeMap() {
        if(DeviceUtils.isAvilible(mContext,"com.autonavi.minimap")){

            try{
                Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=ecolink&poiname=我的目的地&lat=" + lat + "&lon=" + lnt + "&dev=1&style=2");
                mContext.startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.show(mContext, R.string.str_install_gaode_map);
//            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }

    private void locali_do() {
        currentZoom=14;
//        mAMap.clear();
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(la, lo), currentZoom));
//        locationMarker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker))
//                .position(new LatLng(la, lo)));
//        locationMarker.showInfoWindow();
//        locationMarker.setToTop();
    }

    private void getMarkerDetailList(String result){
        mEasyStopMarkerDetailList = ParkingParse.parseParkingResult(result);
    }

    private void displayParkinglots(){
        if(mEasyStopMarkerDetailList!=null && mEasyStopMarkerDetailList.size()>0){
            initPageView();
            drawOnMap();
        }
    }

    private void voiceSpeak(String txt){
        if(mEasyStopMarkerDetailList!=null && mEasyStopMarkerDetailList.size()>1){
            ttsHandlerController.speak(txt);
        }
    }

    private void initPageView(){
        mEasyStopPages.clear();
        for(int i=0;i<mEasyStopMarkerDetailList.size();i++){
            EasyStopPage stopPage = new EasyStopPage(mContext,this);
            mEasyStopPages.add(stopPage);
        }
        //TODO add page adapter..
        easy_stop_viewpager.setAdapter(new EasyStopPageAdapter(mContext,mEasyStopPages));
        easy_stop_viewpager.setOffscreenPageLimit(10);
        easy_stop_viewpager.setPageMargin(20);
        easy_stop_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //TODO add marker synchronization with viewpager
                current_marker_position = position;
                Marker marker = parkList.get(current_marker_position);
                if(lastMarker == null){
                    drawCoordinate(marker,true);
                }else{
                    LatLng ll = lastMarker.getPosition();
                    LatLng lk = marker.getPosition();
                    if(ll.latitude!=lk.latitude || ll.longitude!=lk.longitude){
                        drawCoordinate(lastMarker,false);
                        drawCoordinate(marker,true);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void drawOnMap(){
        aMap.clear();
        final ArrayList<MarkerOptions> parkOptionList = new ArrayList<>();
        for(int i=0;i<mEasyStopMarkerDetailList.size();i++){
            ParkingDetail detail = mEasyStopMarkerDetailList.get(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.map_marker,null);
            TextView text = (TextView) view.findViewById(R.id.map_marker_text);
            ImageView img = (ImageView) view.findViewById(R.id.map_marker_img);
            text.setText(String.valueOf(detail.resetCount));
            text.setTextColor(Color.parseColor("#2DB8A0"));
            img.setBackgroundResource(R.mipmap.ic_map_navigation_coordinate);
            MarkerOptions mo = new MarkerOptions().anchor(0.5f, 1)
                    .icon(BitmapDescriptorFactory.fromView(view))
                    .position(new LatLng(detail.latitude,detail.longtitude));
            parkOptionList.add(mo);
        }
        parkList = aMap.addMarkers(parkOptionList,true);
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                current_marker_position = parkList.indexOf(marker);
                if(lastMarker == null){
                    drawCoordinate(marker,true);
                }else{
                    LatLng ll = lastMarker.getPosition();
                    LatLng lk = marker.getPosition();
                    if(ll.latitude!=lk.latitude || ll.longitude!=lk.longitude){
                        drawCoordinate(lastMarker,false);
                        drawCoordinate(marker,true);
                    }else{
                        easy_stop_viewpager.setVisibility(View.VISIBLE);
                        iv_layout.setVisibility(View.GONE);
                    }
                }
                easy_stop_viewpager.setCurrentItem(current_marker_position);
                return true;
            }
        });
        for (Marker m:parkList) {
            m.showInfoWindow();
            m.setToTop();
        }
        locationMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_3d))
                .position(new LatLng(la, lo)));
        locationMarker.showInfoWindow();
        locationMarker.setToTop();
    }
    private String getParkRestCount(LatLng latLng){
        for(int i=0;i<mEasyStopMarkerDetailList.size();i++){
            ParkingDetail detail = mEasyStopMarkerDetailList.get(i);
            if(latLng.latitude == detail.latitude && latLng.longitude == detail.longtitude){
                return String.valueOf(detail.resetCount);
            }
        }
        return "0";
    }
    private int getParkPosition(double latitude, double longtitude){
        for(int i=0;i<mEasyStopMarkerDetailList.size();i++){
            ParkingDetail detail = mEasyStopMarkerDetailList.get(i);
            if(latitude == detail.latitude && longtitude == detail.longtitude){
                return i;
            }
        }
        return 0;
    }
    private void drawCoordinate(Marker marker, boolean isSelected){
        if(isSelected){
            int position = parkList.indexOf(marker);
            LatLng ll = marker.getPosition();
            parkList.remove(marker);
            chosepoint = new LatLonPoint(ll.latitude, ll.longitude);
             mEasyStopHandler.sendEmptyMessage(SET_POINT_TO_CENTER);
            getAddress(chosepoint);
            View view = LayoutInflater.from(mContext).inflate(R.layout.map_marker,null);
            TextView text = (TextView) view.findViewById(R.id.map_marker_text);
            ImageView img = (ImageView) view.findViewById(R.id.map_marker_img);
            text.setText(getParkRestCount(ll));
            text.setTextColor(Color.parseColor("#E47A30"));
            img.setBackgroundResource(R.mipmap.ic_map_navigation_coordinate_sel);
            marker.remove();
            locationMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                    .icon(BitmapDescriptorFactory.fromView(view))
                    .position(new LatLng(ll.latitude, ll.longitude)));
            locationMarker.showInfoWindow();
            locationMarker.setToTop();
            lastMarker = locationMarker;
            parkList.add(position,locationMarker);
        }else{
            int position = parkList.indexOf(marker);
            LatLng ll = marker.getPosition();
            parkList.remove(marker);
            View view = LayoutInflater.from(mContext).inflate(R.layout.map_marker,null);
            TextView text = (TextView) view.findViewById(R.id.map_marker_text);
            ImageView img = (ImageView) view.findViewById(R.id.map_marker_img);
            text.setText(getParkRestCount(ll));
            text.setTextColor(Color.parseColor("#2DB8A0"));
            img.setBackgroundResource(R.mipmap.ic_map_navigation_coordinate);
            marker.remove();
            locationMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                    .icon(BitmapDescriptorFactory.fromView(view))
                    .position(new LatLng(ll.latitude, ll.longitude)));
            locationMarker.showInfoWindow();
            locationMarker.setToTop();
            parkList.add(position,locationMarker);
        }
    }
    //map 点击图标
    @Override
    public void onMapClick(LatLng latLng) {
        easy_stop_viewpager.setVisibility(View.GONE);
        iv_layout.setVisibility(View.VISIBLE);
//        mAMap.clear();
//        chosepoint = new LatLonPoint(latLng.latitude, latLng.longitude);
//        getAddress(chosepoint);
//        locationMarker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi))
//                .position(latLng));
//        locationMarker.showInfoWindow();
//        locationMarker.setToTop();
//
//
//        locationMarker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker))
//                .position(new LatLng(la, lo)));
//        locationMarker.showInfoWindow();
//        locationMarker.setToTop();




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
                addressName = result.getRegeocodeAddress().getFormatAddress();

                lat=chosepoint.getLatitude();
                lnt=chosepoint.getLongitude();


                markAddr = addressName + "," + chosepoint.getLatitude() + "," + chosepoint.getLongitude();
                int position = getParkPosition(lat, lnt);
                ParkingDetail detail = mEasyStopMarkerDetailList.get(position);
                if(detail!=null){
                    double distance = DistanceUtil.DistanceOfTwoPoints(la, lo, lat, lnt)/1000;
                    easy_stop_viewpager.setVisibility(View.VISIBLE);
                    iv_layout.setVisibility(View.GONE);
                    mEasyStopPages.get(position).initData(detail,distance);
                }
//                mGeoResultlayout.setClickable(true);
//                mGeoResultlayout.setOnClickListener(this);
            } else {
                ToastUtil.show(mContext, R.string.str_no_result);
                //mLocationDesTextView.setText("没有结果");
            }
        } else if (rCode == 27) {
            ToastUtil.show(mContext, R.string.net_erro_toast);
            //mLocationDesTextView.setText("网络错误");
        } else if (rCode == 32) {
            ToastUtil.show(mContext, R.string.str_key_erro);
            //mLocationDesTextView.setText("key无效");
        } else {
            ToastUtil.show(mContext, R.string.net_erro_toast);
            //mLocationDesTextView.setText(rCode);
        }


    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                String addressName = address.getFormatAddress();

            } else {

            }
        } else {

        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        currentZoom = cameraPosition.zoom;
    }

    class EasyStopPageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<EasyStopPage> mPages;

        public EasyStopPageAdapter(Context context, ArrayList<EasyStopPage> pages){
            this.mContext = context;
            this.mPages = pages;
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mPages.get(position).getContentView(), 0);
            return mPages.get(position).getContentView();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mPages.get(position).getContentView());
        }
    }
}
