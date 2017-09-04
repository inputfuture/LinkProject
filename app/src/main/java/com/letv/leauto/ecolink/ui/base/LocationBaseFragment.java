package com.letv.leauto.ecolink.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by why on 2017/4/25.
 */

public class LocationBaseFragment extends BaseFragment implements AMapLocationListener{

    public AMapLocationClient mlocationClient;
    public AMapLocationClientOption mLocationOption;
    @Override
    protected View initView(LayoutInflater inflater) {
        return null;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null) {
            Trace.Debug("#####permission stop location");
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

    }
}
