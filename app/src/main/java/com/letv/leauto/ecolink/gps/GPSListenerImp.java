package com.letv.leauto.ecolink.gps;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;

import java.lang.ref.WeakReference;
import java.util.Iterator;


/**
 * Created by fuqinqin on 2016/10/26.
 */
public class GPSListenerImp implements GPSLocationListener {
    private WeakReference<Context> mContext;
    private ImageView mImageView;
    private TextView mTextView;
    private ImageView mCrossImageView;
    private  TextView mCrossTextView;
    private LocationManager mLocationManager;

    public GPSListenerImp(Context context) {
        this.mContext = new WeakReference<>(context);
        if (mContext.get() != null) {
            mLocationManager = (LocationManager) (mContext.get().getSystemService(Context.LOCATION_SERVICE));
        }

    }

    public void setImageView(ImageView iv, TextView tv) {
        mImageView = iv;
        mTextView = tv;
    }

    public void setCrossImageView(ImageView crossImageView,TextView mTextView){
        mCrossImageView=crossImageView;
        mCrossTextView=mTextView;

    }

    @Override
    public void updateLocation(Location location) {

    }

    @Override
    public void updateStatus(String provider, int status, Bundle extras) {

    }

    @Override
    public void updateGPSProviderStatus(int gpsStatus) {

    }

    @Override
    public void onGpsSatelliteStatusChanged(int event) {
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            if (mLocationManager != null && mImageView != null && mTextView != null) {
                GpsStatus status = mLocationManager.getGpsStatus(null); // 取当前状态
                int maxSatellites = status.getMaxSatellites();
                Iterator<GpsSatellite> it = status.getSatellites().iterator();
                int count = 0;
                while (it.hasNext() && count <= maxSatellites) {
                    GpsSatellite s = it.next();
                    count++;
                }
                if (count < 4) {
                    mImageView.setImageResource(R.mipmap.navi_start_satellite_weak);
                    mCrossImageView.setImageResource(R.mipmap.navi_start_satellite_weak);
                    mTextView.setText("弱");
                    mCrossTextView.setText("弱");
                } else if (count < 10 && count >= 4) {
                    mImageView.setImageResource(R.mipmap.navi_start_satellite);
                    mCrossImageView.setImageResource(R.mipmap.navi_start_satellite);
                    mTextView.setText("中");
                    mCrossTextView.setText("中");
                } else if (count >= 10) {
                    mImageView.setImageResource(R.mipmap.navi_start_satellite_strong);
                    mCrossImageView.setImageResource(R.mipmap.navi_start_satellite_strong);
                    mTextView.setText("强");
                    mCrossTextView.setText("强");
                }
//                mTextView.setText(count+"");
            }
        }
    }
}
