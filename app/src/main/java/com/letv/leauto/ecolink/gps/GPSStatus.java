package com.letv.leauto.ecolink.gps;

import android.location.GpsSatellite;
import android.location.GpsStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuqinqin on 2016/10/26.
 */
public class GPSStatus implements GpsStatus.Listener{

    private GPSLocationListener mGpsLocationListener;
    private List<GpsSatellite> mSatelliteList = new ArrayList<GpsSatellite>();

    public GPSStatus(GPSLocationListener gpsLocationListener) {
        this.mGpsLocationListener = gpsLocationListener;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event){
            case GpsStatus.GPS_EVENT_STARTED:
                mGpsLocationListener.onGpsSatelliteStatusChanged(event);
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                mGpsLocationListener.onGpsSatelliteStatusChanged(event);
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                mGpsLocationListener.onGpsSatelliteStatusChanged(event);
                 break;
            case GpsStatus.GPS_EVENT_STOPPED:
                mGpsLocationListener.onGpsSatelliteStatusChanged(event);
                break;

        }

    }
}
