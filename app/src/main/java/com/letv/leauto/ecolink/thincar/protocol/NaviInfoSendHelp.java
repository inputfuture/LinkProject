package com.letv.leauto.ecolink.thincar.protocol;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.navi.enums.IconType;
import com.amap.api.navi.model.NaviInfo;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.ui.HomeActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/7.
 */
public class NaviInfoSendHelp {
    private static NaviInfoSendHelp ourInstance = new NaviInfoSendHelp();

    public static NaviInfoSendHelp getInstance() {
        return ourInstance;
    }

    private Set<Integer> mKeyNaviSet = new HashSet<Integer>();
    private Set<Integer> mKeyDistanceSet = new HashSet<Integer>();

    /** 当前关键路段就不应该显示*/
    private boolean shouldCurrentShow = true;

    /** 保存上次传过的关键转折点距离*/
    private int mLastDistance;

    private int[] KEY_NAVI_DISTANCE = new int[] {
            300, 500
    };

    private int[] KEY_NAVI_ICON_TYPE = new int[] {
            IconType.ARRIVED_DESTINATION,
            IconType.ARRIVED_WAYPOINT,
            IconType.ENTER_ROUNDABOUT,
            IconType.LEFT,
            IconType.LEFT_BACK,
            IconType.LEFT_FRONT,
            IconType.LEFT_TURN_AROUND,
            IconType.NONE,
            IconType.OUT_ROUNDABOUT,
            IconType.RIGHT,
            IconType.RIGHT_BACK,
            IconType.RIGHT_FRONT,
            IconType.STRAIGHT
    };

    private NaviInfoSendHelp() {
        initKeyNaviSet();
        initKeyDistanceSet();
    }

    private void initKeyNaviSet() {
        for (int value:KEY_NAVI_ICON_TYPE) {
            mKeyNaviSet.add(value);
        }
    }

    private void initKeyDistanceSet() {
        for (int value : KEY_NAVI_DISTANCE) {
            mKeyDistanceSet.add(value);
        }
    }

    /**
     * 发送导航信息给车机
     */
    public void sendHudInfoToCar(NaviInfo naviInfo) {
        if (HomeActivity.isThinCar) {
            sendThincarHudInfo(naviInfo);
        } else {
            int currentDistance = naviInfo.getCurStepRetainDistance();
            sendHudInfo(naviInfo, currentDistance);
        }
    }

    private void sendThincarHudInfo(NaviInfo naviInfo) {
        if (mKeyNaviSet.contains(naviInfo.getIconType())) {
            int currentDistance = naviInfo.getCurStepRetainDistance();
            if (mLastDistance < currentDistance) {
                shouldCurrentShow = true;
            }
            if ((isFirstShow(currentDistance) || isSecondShow(currentDistance))) {
                sendHudInfo(naviInfo, mLastDistance);
                mLastDistance = naviInfo.getCurStepRetainDistance();
                return;
            }

            if (naviInfo.getCurStepRetainDistance() <= 100) {
                sendHudInfo(naviInfo, currentDistance);
            }
            mLastDistance = naviInfo.getCurStepRetainDistance();
        }
    }

    private void sendHudInfo(NaviInfo naviInfo,int distance) {
        if (!shouldCurrentShow) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Notify");
        map.put("Method", "NotifyNaVStatus");

        Map<String, Object> content = new HashMap<>();
        content.put("turnId", naviInfo.getIconType());
        //content.put("segmentRemainDistance",naviInfo.getCurStepRetainDistance());
        content.put("segmentRemainDistance", distance);
        content.put("currRoad", naviInfo.getCurrentRoadName());
        content.put("nextRoad", naviInfo.getNextRoadName());
        content.put("segmentRemainTime", naviInfo.getCurStepRetainTime());
        content.put("routeRemainDistance", naviInfo.getPathRetainDistance());
        content.put("routeRamainTime", naviInfo.getPathRetainTime());

        map.put("Parameter", content);
        JSONObject object = (JSONObject) JSON.toJSON(map);
        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.NAVI_APPID, object);
    }

    /** 判断是不是500米*/
    private boolean isFirstShow(int distance) {
        if (distance < 500 && 400<= distance && mLastDistance >= 500) {
            return true;
        }

        return false;
    }

    /** 判断是不是300米*/
    private boolean isSecondShow(int distance) {
        if (distance < 300 && 200<= distance && mLastDistance >= 300) {
            return true;
        }

        return false;
    }

    public void requestHudAction() {
        shouldCurrentShow = false;
    }
}