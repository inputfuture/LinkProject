package com.letv.leauto.ecolink.thincar;


import com.letv.leauto.favorcar.exInterface.IConnectedCarInfoChangedListener;
import com.letv.leauto.favorcar.exInterface.IConnectedStatusChangedListener;
import com.letv.leauto.favorcar.exInterface.ILocationChangedListener;
import com.letv.leauto.favorcar.exInterface.LocationInfo;
import com.letv.leauto.favorcar.exInterface.ThincarInfoInterface;
import com.letv.leauto.favorcar.exInterface.vehicleInfo;

/**
 * Created by Administrator on 2017/6/16.
 */

public class LeThincarInfoInterface implements ThincarInfoInterface {
    private boolean mConnectState;
    private LocationInfo mLocationInfo = new LocationInfo();
    private vehicleInfo mVehicleInfo = new vehicleInfo();

    private IConnectedStatusChangedListener mConnectedStatusChangedListener;
    private ILocationChangedListener mLocationChangedListener;
    private IConnectedCarInfoChangedListener mConnectedCarInfoChangedListener;

    @Override
    public boolean getConnectedStatus(int carType,IConnectedStatusChangedListener listener) {
        mConnectedStatusChangedListener = listener;
        return mConnectState;
    }

    @Override
    public LocationInfo getLocationInfo(ILocationChangedListener listener) {
        mLocationChangedListener = listener;
        return mLocationInfo;
    }

    @Override
    public vehicleInfo getVehicleInfo(IConnectedCarInfoChangedListener listener) {
        mConnectedCarInfoChangedListener = listener;
        return mVehicleInfo;
    }

    public void setThinCarState(boolean value) {
        mConnectState = value;
        if (mConnectedStatusChangedListener != null) {
            mConnectedStatusChangedListener.onConnectedStatusChanged(value);
        }
    }

    public void setLocatinInfo(LocationInfo info) {
        mLocationInfo = info;
        if (mLocationChangedListener != null) {
            mLocationChangedListener.onLocationChanged(info);
        }
    }

    public void setCarVinCode(String code) {
        mVehicleInfo.setCarVINcode(code);
        mVehicleInfo.setPartNum("7900030001HL408");//408
        mVehicleInfo.setModle("150011CG000484");
        mVehicleInfo.setSerialNum("9030572336880000484");
    }
}