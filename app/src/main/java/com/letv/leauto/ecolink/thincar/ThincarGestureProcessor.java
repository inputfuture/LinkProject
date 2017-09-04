package com.letv.leauto.ecolink.thincar;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.autonavi.amap.mapcore.IPoint;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.ThinCarDefine;

/**
 * Created by Administrator on 2017/1/19.
 */
public class ThincarGestureProcessor {

    private AMap aMap;
    private double mPhoneCarRate;

    public ThincarGestureProcessor(AMap map,double rate) {
        aMap = map;
        mPhoneCarRate = rate;
    }

    public void notifyGesterEvent(int event, int x, int y, int parameter) {
        int phoneX = (int) ((double) x * mPhoneCarRate);
        int phoneY = (int) ((double) y * mPhoneCarRate);

        LogUtils.i("MapFragmnt","notifyGesterEvent event:"+event + " x:"+x + " y:"+y + " parameter:"+parameter);
        switch (event) {
            case ThinCarDefine.ProtocolFromCarAction.P0_PHONE_GESTURE_MOVE:
                scrollMap(phoneX,phoneY);
                break;
            case ThinCarDefine.ProtocolFromCarAction.P0_PHONE_GESTURE_SCALE:
                scaleMap(phoneX,phoneY,parameter);
                break;
            case ThinCarDefine.ProtocolFromCarAction.P0_PHONE_GESTURE_ROTATE:
                rotateMap(phoneX,phoneY,parameter);
                break;
        }
    }

    /**
     * 对地图进行移动
     * @param phoneX
     * @param phoneY
     */
    private void scrollMap(int phoneX, int phoneY) {
        if (aMap != null) {
            aMap.animateCamera(CameraUpdateFactory.scrollBy(phoneX,phoneY));
        }
    }

    /**
     * 对地图进行缩放
     * @param phoneX
     * @param phoneY
     * @param parameter
     */
    private void scaleMap(int phoneX, int phoneY, int parameter) {
        float value = (float)parameter/(float)100 - 1;
        if (aMap != null) {
            aMap.animateCamera(CameraUpdateFactory.zoomBy(value * 4));
        }
    }

    /**
     * 对地图进行旋转
     * @param phoneX
     * @param phoneY
     * @param parameter
     */
    private void rotateMap(int phoneX, int phoneY, int parameter) {
        IPoint point = new IPoint(phoneX,phoneY);
        /** 暂时不处理旋转手势 */
        //aMap.animateCamera(CameraUpdateFactory.changeBearingGeoCenter(parameter,point));
//        if (aMap != null) {
//            aMap.animateCamera(CameraUpdateFactory.changeBearing(-parameter));
//        }
    }
}
