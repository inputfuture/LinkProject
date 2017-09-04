package com.letv.leauto.ecolink.manager;

import android.os.Handler;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.utils.Trace;


/**
 * Created by liweiwei on 16/4/1.
 */
public class LocationManager implements AMapLocationListener {

    private static LocationManager instance;
    private AMapLocation aMapLocation;

    //定位
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    Handler mHandler;
    Handler mHandlerTwo;

    public static LocationManager getInstance() {
        if (instance == null) {
            synchronized (LocationManager.class) {
                if (instance == null) {
                    instance = new LocationManager();

                }
            }
        }
        return instance;
    }

    /**
     * 开启一次定位服务
     */
    public void initLocation(Handler handler) {
        mHandler = handler;

        locationClient = new AMapLocationClient(EcoApplication.instance);
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationOption.setOnceLocation(true);
        // 设置定位监听
        locationClient.setLocationListener(this);
        locationClient.setLocationOption(locationOption);

        // 启动定位
        locationClient.startLocation();
    }

    public void initLocationTwo(Handler handler) {
        mHandlerTwo = handler;

        locationClient = new AMapLocationClient(EcoApplication.instance);
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationOption.setOnceLocation(true);
        // 设置定位监听
        locationClient.setLocationListener(this);
        locationClient.setLocationOption(locationOption);

        // 启动定位
        locationClient.startLocation();
    }




    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        this.aMapLocation=aMapLocation;
        Trace.Error("=====location","1");
        if (aMapLocation.getErrorCode() == 0) {
            EcoApplication.getInstance().setCity(aMapLocation.getCity());
            EcoApplication.getInstance().setProvince(aMapLocation.getProvince());
            EcoApplication.getInstance().setAddress(aMapLocation.getAddress());
            EcoApplication.getInstance().setLatitude(aMapLocation.getLatitude());
            EcoApplication.getInstance().setLongitude(aMapLocation.getLongitude());
            EcoApplication.getInstance().setCurrentLoaction(aMapLocation);
            if (mHandler!=null){
                Trace.Error("=====location","2");
                Message message = new Message();
                message.obj = aMapLocation;
                message.what = MessageTypeCfg.MSG_INIT_LOCATION;
                mHandler.sendMessage(message);
                mHandler=null;
            }
            if (mHandlerTwo!=null){
                Trace.Error("=====location","3");
                Message message = new Message();
                message.obj = aMapLocation;
                message.what = MessageTypeCfg.MSG_INIT_LOCATION;
                mHandlerTwo.sendMessage(message);
                mHandlerTwo=null;
            }
        }

    }
}
