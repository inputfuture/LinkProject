package com.letv.leauto.ecolink.event;

import com.letv.voicehelp.manger.nav.SearchPoi;

/**
 * Created by lixinlei on 16/11/28.
 */

public class NavEvent {
    /**
     * 打开导航
     */
    public static final int OPEN_NAV = 217;

    /**
     * 关闭导航
     */
    public static final int EXIT_NAV = 425;

    /**
     * 导航去POI
     */
    public static final int GO_TO_NAV = 494;
    /**
     * 重新规划路线
     */
    public static final int NEW_PATH = 495;
    /**
     * 回家
     */
    public static final int GO_HOME = 67;

    /**
     * 去公司
     */
    public static final int GO_WORK = 612;
    public static final int PREVIEW = 613;
    public static final int OPEN_SOUND = 614;
    public static final int CLOSE_SOUND = 615;
    public static final int CAR_UP=616;
    public static final int NORTH_UP=617;

//    head_forward 车头朝上  ,north_forward 正北朝上
    public static final String CAR_UP_STRING="head_forward";
    public static final String NORTH_UP_STRING="north_forward";
    public static final int ZOOM_OUT =618 ;
    public static final int ZOOM_IN = 619;

    private SearchPoi  poi;

    private int type;

    private int strategy;

    public NavEvent(SearchPoi poi , int type) {
        this.poi = poi;
        this.type = type;
    }
    public NavEvent(int  strategy, int type) {
        this.strategy = strategy;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public SearchPoi getPoi() {
        return poi;
    }

    public int getStrategy() {
        return strategy;
    }
}
