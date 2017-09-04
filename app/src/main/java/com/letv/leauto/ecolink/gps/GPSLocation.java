package com.letv.leauto.ecolink.gps;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * 类描述：实现LocationListener的子类，同时实现自己的接口调用(具体观察者)
 */
public class GPSLocation implements LocationListener{
    private GPSLocationListener mGpsLocationListener;

    public GPSLocation(GPSLocationListener gpsLocationListener) {
        this.mGpsLocationListener = gpsLocationListener;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mGpsLocationListener.updateLocation(location);
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        mGpsLocationListener.updateStatus(provider, status, extras);
        switch (status) {
            case LocationProvider.AVAILABLE:
                mGpsLocationListener.updateGPSProviderStatus(GPSProviderStatus.GPS_AVAILABLE);
                break;
            case LocationProvider.OUT_OF_SERVICE:
                mGpsLocationListener.updateGPSProviderStatus(GPSProviderStatus.GPS_OUT_OF_SERVICE);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                mGpsLocationListener.updateGPSProviderStatus(GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE);
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        mGpsLocationListener.updateGPSProviderStatus(GPSProviderStatus.GPS_ENABLED);
    }

    @Override
    public void onProviderDisabled(String provider) {
        mGpsLocationListener.updateGPSProviderStatus(GPSProviderStatus.GPS_DISABLED);
    }

}
