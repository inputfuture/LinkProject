package com.letv.leauto.ecolink.lemap.entity;

import com.amap.api.maps.offlinemap.OfflineMapCity;

/**
 * Created by Administrator on 2016/7/6.
 */
public class OfflineMap {
    OfflineMapCity mapCity;
    boolean ischecked =false;//是否选择
    boolean isShow=false;

    public OfflineMapCity getMapCity() {
        return mapCity;
    }
    public void setMapCity(OfflineMapCity mapCity) {
        this.mapCity = mapCity;
    }
    public boolean isIschecked() {
        return ischecked;
    }
    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public boolean isShow() {
        return isShow;
    }
    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

}
