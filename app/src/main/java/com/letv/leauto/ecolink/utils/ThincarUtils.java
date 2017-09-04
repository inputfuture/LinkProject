package com.letv.leauto.ecolink.utils;

import android.content.Context;
import android.location.Location;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ThincarUtils {

    private static ThincarUtils thincarUtils=null;

    private Context mContext;

    private ThincarUtils(Context context) {
       this.mContext = context;
    }

    public static ThincarUtils getInstance(Context context){

        if(thincarUtils==null){
            synchronized (ThincarUtils.class){
                if(thincarUtils==null){
                    thincarUtils=new ThincarUtils(context);
                }
            }
        }
        return thincarUtils;
    }

    /**
     * 可视区为中心点
     */
    public void cameraPosition(AMap aMap){
        Location lactaion = aMap.getMyLocation();
        if (lactaion == null) {
            return;
        }
        CameraPosition position = aMap.getCameraPosition();
        LatLng mLatlng = new LatLng(lactaion.getLatitude(), lactaion.getLongitude());
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatlng, position.zoom));
    }


}
