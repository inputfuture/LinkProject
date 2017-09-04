package com.letv.leauto.ecolink.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.leauto.link.lightcar.LogUtils;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.Trace;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/10/31.
 */
public class PathService extends Service implements AMapLocationListener,RouteSearch.OnRouteSearchListener{
    public static final String startService = "start";
    public static final String EndService = "end";
    private static final String TAG = "PathService";
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private RouteSearch routeSearch;
    private RouteSearch.DriveRouteQuery query ;
    private LatLonPoint startPoint = null;
    private LatLonPoint endPoint = null;
    private SimpleDateFormat format=new SimpleDateFormat("HH:mm");

    @Override
    public void onCreate() {
        super.onCreate();
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(30000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位

        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            LogUtils.i(TAG,"onStartCommand intent is null,just return!!");
            return super.onStartCommand(intent, flags, startId);
        }
        String str=intent.getStringExtra("loacal");
        String endString = intent.getStringExtra("endPoint");
        Trace.Info(TAG, "onStartCommand: "+str);
        if(str!=null && str.equals(startService)){
            if(endString!=null){
                String[] stStrs = endString.split(",");
                endPoint=new LatLonPoint(Double.parseDouble(stStrs[1]),Double.parseDouble(stStrs[2]));
                mlocationClient.startLocation();//开始新的定位
            }
        }

        if(str!=null && str.equals(EndService)){
            mlocationClient.stopLocation();
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(AMapLocation location) {

        if(location!=null){
            if(location.getErrorCode()==0){
                //定位成功回调信息，设置相关消息
//                location.getLatitude();//获取纬度
//                location.getLongitude();//获取经度
                startPoint=new LatLonPoint(location.getLatitude(),location.getLongitude());
                RouteSearch.FromAndTo fromAndTo=new RouteSearch.FromAndTo(startPoint,endPoint);
                query=new RouteSearch.DriveRouteQuery(fromAndTo,RouteSearch.DrivingDefault,null,null, "");
                routeSearch.calculateDriveRouteAsyn(query);
                EcoApplication.getInstance().setCurrentLoaction(location);

            }
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        //解析result获取算路结果

        if (rCode == 1000) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                DrivePath drivePath = result.getPaths().get(0);
                Trace.Info(TAG, "onDriveRouteSearched: "+ DensityUtils.convertMeter2KM(drivePath.getDistance()));
                Trace.Info(TAG, "onDriveRouteSearched: "+DensityUtils.convertSec2Min(drivePath.getDuration()));
                long time = System.currentTimeMillis() + drivePath.getDuration()*1000;
                Trace.Info(TAG, "onDriveRouteSearched:tiem"+format.format(time));

            }
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

}
