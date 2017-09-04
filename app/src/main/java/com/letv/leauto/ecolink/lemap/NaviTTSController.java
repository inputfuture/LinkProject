package com.letv.leauto.ecolink.lemap;

import android.content.Context;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.NaviTTSType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.ae.guide.model.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.controller.EcoTTSController;
import com.letv.leauto.ecolink.ui.NaviSettingDialog;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * 语音播报组件
 */
public class NaviTTSController implements AMapNaviListener {

    public static NaviTTSController ttsManager;
    // 合成对象.
    EcoTTSController mController;
    private Context mContext;


    NaviTTSController(Context context) {
        initSpeechSynthesizer();
        mContext=context.getApplicationContext();
    }

    public static NaviTTSController getInstance(Context context) {
        if (ttsManager == null) {
            ttsManager = new NaviTTSController(context);
        }
        return ttsManager;
    }

    /**
     *
     * @param
     */
    public void playText(String playText) {
        if(BaseActivity.isVoice)
            return;
        if (CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_SPEAKER,1)==0){
            return;
        }
        Trace.Debug("###### playtext  "+playText);
        if (playText.contains("准备出发,")){
           playText= playText.replace("准备出发,","");
        }
        mController.speak(playText);
    }


    /**
     *
     * @param
     */
    public void stop() {
        mController.stop();
    }

    public  void restart(){ mController.resume();}


    private void initSpeechSynthesizer() {
        mController = EcoApplication.LeGlob.getTtsController();
    }

    @Override
    public void onArriveDestination() {
        this.playText(EcoApplication.getInstance().getString(R.string.map_arrived));
    }

    @Override
    public void onArrivedWayPoint(int arg0) {
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        Trace.Debug("######## size="+arg0);
        this.playText(EcoApplication.getInstance().getString(R.string.map_get_way_faild));
    }

    @Override
    public void onCalculateRouteSuccess() {
        String calculateResult = EcoApplication.getInstance().getString(R.string.map_get_way_ready);
        this.playText(calculateResult);
    }

    @Override
    public void onEndEmulatorNavi() {
        this.playText(EcoApplication.getInstance().getString(R.string.map_navi_end));

    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {
        Trace.Debug("### type"+arg0 +"  "+arg1);
        if (!NaviSettingDialog.SpeakerContent.getInstance().hasNaviMsg()){
            if (arg0== NaviTTSType.NAVIINFO_TEXT){
                return;
            }
        }
        this.playText(arg1);
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        this.playText(EcoApplication.getInstance().getString(R.string.map_change_way));
    }

    @Override
    public void onReCalculateRouteForYaw() {
        this.playText(EcoApplication.getInstance().getString(R.string.map_naviway_dispach));
    }

    @Override
    public void onStartNavi(int arg0) {
        this.playText("准备出发");

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

//    /**
//     * @param aMapNaviCameraInfos
//     * 导航过程中摄像头信息回调
//     */
//    @Override
//    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {
//
//    }
//
//    /**
//     * @param aMapServiceAreaInfos
//     * 导航过程中服务区信息回调
//     */
//    @Override
//    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {
//
//    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

//    /**
//     * @param i
//     * 导航过程中“叮”的回调函数
//     * EPlay_NULL 0 无
//    EPLay_Reroute 1 偏航重算
//    EPlay_Ding 100 马上到转向路口的时候发的提示音
//    EPlay_Dong 101 导航状态下测速电子眼通过音
//    EPlay_Elec_Ding 102 巡航状态下电子眼（所有类型）通过音
//     */
//    @Override
//    public void onPlayRing(int i) {
//
//    }
}
