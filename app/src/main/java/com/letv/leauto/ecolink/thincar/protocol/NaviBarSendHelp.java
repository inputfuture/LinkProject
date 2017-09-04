package com.letv.leauto.ecolink.thincar.protocol;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.manager.LocationManager;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.fragment.NaviFragment;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/5.
 */
public class NaviBarSendHelp {
    public static final String HOME_TAG = "0";
    public static final String WORK_TAG = "1";
    private static final int NOT_IN_NAVING = 0;//非导航中
    private static final int IN_NAVING = 1;//导航中

    private static final int NOT_IN_PREVIEWING = 0;//非预览中
    private static final int IN_PREVIEWING = 1;//预览中

    public static final String QUICK_SEARCH_PARKING = "parking";
    public static final String QUICK_SEARCH_GASSTATION = "gasstation";
    public static final String QUICK_SEARCH_TIOLET = "toilet";
    public static final String QUICK_SEARCH_FOOD = "food";

    private static NaviBarSendHelp ourInstance = new NaviBarSendHelp();

    private String homeEndAddr = null;
    private String workEndAddr = null;
    private String startAddr = "乐视大厦,39.933542,116.494108";
    private LatLonPoint startPoint = null;
    private LatLonPoint endHomePoint = null;
    private LatLonPoint endWorkPoint = null;
    private Context mContext;
    private RouteSearch.DriveRouteQuery homeQueryShort;
    private RouteSearch.DriveRouteQuery workQueryShort;

    private String homeRemainingDistance;//公里为单位
    private String homeRemainingTime;//分钟为单位

    private String workRemainingDistance;//公里为单位
    private String workRemainingTime;//分钟为单位

    private int requestItem = 0;
    private int SEND_NAVI_INTERVAL = 30 * 1000;//导航发送信息间隔间
    private long lastSendTime = 0;//最后一次发送导航信息时间
    private String mRemainingDistance = "";//保存导航中还剩下的距离
    private String mRemainingTime = "";//保存导航中还剩下的时间

    private boolean isRequestNaviInfo = false;

    private Runnable mQequestDistanceRunnable = new Runnable() {

        @Override
        public void run() {
            resetSearchValue();
            homeEndAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HOME_ADDR, "");
            workEndAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.WORK_ADDR, "");
            if (TextUtils.isEmpty(homeEndAddr) && TextUtils.isEmpty(workEndAddr)) {
                responseNotNaviingBarInfo();
                return;
            }

          LocationManager.getInstance().initLocation(mHandler);
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageTypeCfg.MSG_INIT_LOCATION:
                    AMapLocation aMapLocation = (AMapLocation) msg.obj;
                    if (aMapLocation.getErrorCode() == 0) {
                        startAddr = aMapLocation.getAddress() + "," + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude();
                        initPointData();
                        searchRouteResult();
                    }
                    break;
            }
        }
    };

    public void initNaviBar(Context context) {
        mContext = context;
    }

    public static NaviBarSendHelp getInstance() {
        return ourInstance;
    }

    private NaviBarSendHelp() {
    }

    /**
     * 向车机发送当前是否处于导航中
     */
    public void sendIsInNaving() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", "ResponseIsNaving");

        Map<String, Object> content = new HashMap<>();
        content.put("IsNaving", MapCfg.mNaAciFragmentIsNaVi ? IN_NAVING : NOT_IN_NAVING);

        map.put("Parameter", content);

        sendDataInMap(map);
    }

    /**
     * 车机请求NaviBar信息
     */
    public void requestNaviBarInfo() {
        if (!MapCfg.mNaAciFragmentIsNaVi) {//如果没有在导航中，返回离家或者公司的距离
            mHandler.removeCallbacks(mQequestDistanceRunnable);
            mHandler.postDelayed(mQequestDistanceRunnable,200);
        } else {
            responseNaviingBarInfo();
        }

    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult() {
        if (endHomePoint != null) {
            final RouteSearch.FromAndTo homeFromAndTo = new RouteSearch.FromAndTo(
                    startPoint, endHomePoint);
            homeQueryShort = new RouteSearch.DriveRouteQuery(homeFromAndTo, RouteSearch.DrivingShortDistance,
                    null, null, "");
            requestItem++;
        }

        if (endWorkPoint != null) {
            final RouteSearch.FromAndTo workFromAndTo = new RouteSearch.FromAndTo(
                    startPoint, endWorkPoint);
            workQueryShort = new RouteSearch.DriveRouteQuery(workFromAndTo, RouteSearch.DrivingShortDistance,
                    null, null, "");
            requestItem++;
        }

        LogUtils.i("NaviBarSendHelp","searchRouteResult requestItem:"+requestItem);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (homeQueryShort != null) {
                    RouteSearch homeRouteSearch = new RouteSearch(mContext);
                    homeRouteSearch.setRouteSearchListener(new NaviRouteSearchListener());
                    homeRouteSearch.calculateDriveRouteAsyn(homeQueryShort);
                }

                if (workQueryShort != null) {
                    RouteSearch workRouteSearch = new RouteSearch(mContext);
                    workRouteSearch.setRouteSearchListener(new NaviRouteSearchListener());
                    workRouteSearch.calculateDriveRouteAsyn(workQueryShort);
                }
            }
        }).start();
    }

    private void resetSearchValue() {
        requestItem = 0;
        homeQueryShort = null;
        workQueryShort = null;
        homeRemainingDistance = "";
        homeRemainingTime = "";
        workRemainingDistance = "";
        workRemainingTime = "";
        endHomePoint = null;
        endWorkPoint = null;
    }

    /**
     * 加速状态变化，通知车机当前没有导航中
     */
    public void responseNotNaviingDirect() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", "ResponseNaviBarInfo");

        Map<String, Object> content = new HashMap<>();
        content.put("IsNaving", NOT_IN_NAVING);

        map.put("Parameter", content);
        sendDataInMap(map);
    }

    /**
     * 返回非导航中导航条信息
     */
    public void responseNotNaviingBarInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", "ResponseNaviBarInfo");

        Map<String, Object> content = new HashMap<>();
        content.put("IsNaving", NOT_IN_NAVING);
        if (endHomePoint != null) {
            content.put("HomeRemainingDistance", homeRemainingDistance);
            content.put("HomeRemainingTime", homeRemainingTime);
        }

        if (endWorkPoint != null) {
            content.put("WorkRemainingDistance", workRemainingDistance);
            content.put("WorkRemainingTime", workRemainingTime);
        }

        map.put("Parameter", content);
        sendDataInMap(map);
    }

    /**
     * 车机请求手机开始导航
     */
    public void requestStartNavi(String naviType) {
        if (MapCfg.mNaAciFragmentIsNaVi) {
            return;
        }

        HomeActivity activity = (HomeActivity)mContext;
        activity.startNaviForThinCar(naviType);
    }

    /**
     * 车机请求手机结束导航
     */
    public void requestStopNavi() {
        HomeActivity activity = (HomeActivity)mContext;
        activity.stopNaviForThinCar();
    }

    /**
     * 导航中时时更新导航信息
     * @param naviInfo
     */
    public void updateNaviInfo(NaviInfo naviInfo) {
        String distance = DensityUtils.convertMeter2KMNoUnit(naviInfo.getPathRetainDistance());
        String time = naviInfo.getPathRetainTime() / 60 + "";
        if (HomeActivity.isThinCar&&distance.equalsIgnoreCase(mRemainingDistance) && time.equalsIgnoreCase(mRemainingTime)) {
            return;
        }
        mRemainingDistance = distance;
        mRemainingTime = time;
        responseNaviingBarInfo();
    }


    /**
     * 返回导航中导航条信息
     */
    public void responseNaviingBarInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", "ResponseNaviBarInfo");

        Map<String, Object> content = new HashMap<>();
        content.put("IsNaving", IN_NAVING);
        content.put("RemainingDistance", mRemainingDistance);
        content.put("RemainingTime", mRemainingTime);
        NaviFragment fragment = NaviFragment.getThis();
        if (fragment != null) {
            content.put("IsPreviewing",fragment.isPreview() ? IN_PREVIEWING : NOT_IN_PREVIEWING);
        } else {
            content.put("IsPreviewing",NOT_IN_PREVIEWING);
        }

        LogUtils.i("NaviBarSendHelp","NaviingBarInfo mRemainingDistance:"+mRemainingDistance);
        LogUtils.i("NaviBarSendHelp","NaviingBarInfo mRemainingTime:"+mRemainingTime);
        map.put("Parameter", content);
        sendDataInMap(map);
    }

    public void notifySetting() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Notify");
        map.put("Method", "NotifySetting");
        map.put("Parameter", null);

        sendDataInMap(map);
    }

    /**
     * 手机通知车机，开始预览
     */
    public void notifyStartPreview() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Notify");
        map.put("Method", "NotifyStartPreview");
        map.put("Parameter", null);

        sendDataInMap(map);
    }

    /**
     * 手机通知车机，结束预览
     */
    public void notifyStopPreview() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Notify");
        map.put("Method", "NotifyStopPreview");
        map.put("Parameter", null);

        sendDataInMap(map);
    }

    private void sendDataInMap(Map<String, Object> map) {
        JSONObject obj = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.NAVI_BAR_APPID,obj);
    }

    private void initPointData() {
        String[] stStrs = startAddr.split(",");
        if (stStrs.length > 0) {
            startPoint = new LatLonPoint(Double.parseDouble(stStrs[1]), Double.parseDouble(stStrs[2]));
        }

        if (homeEndAddr != null) {
            String[] homeStrs = homeEndAddr.split(",");
            if (homeStrs.length >= 2) {
                endHomePoint = new LatLonPoint(Double.parseDouble(homeStrs[1]), Double.parseDouble(homeStrs[2]));
            }
        }

        if (workEndAddr != null) {
            String[] homeStrs = workEndAddr.split(",");
            if (homeStrs.length >= 2) {
                endWorkPoint = new LatLonPoint(Double.parseDouble(homeStrs[1]), Double.parseDouble(homeStrs[2]));
            }
        }
    }

    private class NaviRouteSearchListener implements RouteSearch.OnRouteSearchListener {
        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
            if (rCode == 1000) {
                if (result != null && result.getPaths() != null
                        && result.getPaths().size() > 0) {
                    result.getDriveQuery();
                    DrivePath drivePath = result.getPaths().get(0);
                    if (homeQueryShort != null && homeQueryShort.equals(result.getDriveQuery())) {
                        requestItem--;
                        homeRemainingDistance = DensityUtils.convertMeter2KMNoUnit(drivePath.getDistance());
                        homeRemainingTime = drivePath.getDuration() / 60 + "";
                    } else if (workQueryShort != null && workQueryShort.equals(result.getDriveQuery())) {
                        requestItem--;
                        workRemainingDistance = DensityUtils.convertMeter2KMNoUnit(drivePath.getDistance());
                        workRemainingTime = drivePath.getDuration() / 60 + "";
                    } else {
                        ToastUtil.show(mContext, R.string.map_no_result);
                    }

                    if (requestItem < 0) {
                        requestItem = 0;
                    }
                    if (requestItem == 0) {
                        responseNotNaviingBarInfo();
                        isRequestNaviInfo = false;
                    }
                }
            }
        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }
    }
}