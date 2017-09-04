package com.letv.leauto.ecolink.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.lemap.navi.Utils;
import com.letv.leauto.ecolink.lemap.navi.routebean.StrategyBean;
import com.letv.leauto.ecolink.lemap.navi.routebean.StrategyStateBean;
import com.letv.leauto.ecolink.ui.dialog.StrategyChooseAdapter;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NaviSettingDialog extends Dialog {

    @Bind(R.id.close)
    ImageView mCloseBtn;
    @Bind(R.id.ok)
    TextView mSureButton;

    @Bind(R.id.day_mode)
    Button mDayModeBtn;
    @Bind(R.id.night_mode)
    Button mNightModeBtn;
    @Bind(R.id.auto_mode)
    Button mAutoModeBtn;
    @Bind(R.id.speak_mode)
    CheckBox mSpeakModeCheckBox;
    @Bind(R.id.scale_check)
    CheckBox mScaleCheckBox;


    @Bind(R.id.eye)
    CheckBox eyeCB;
    @Bind(R.id.traffic)
    CheckBox trafficCB;
    @Bind(R.id.navi)
    CheckBox naviCB;



    private List<Button> mMapModeButtons = new ArrayList<Button>();

    private int mMapMode;
    private int mTrafficOnOff;
    private int mSpeakerMode;
    private int mSpeakerContent;
    private Context mContext;
    private boolean mScaleOpen;
    NaviSettingChangeListener mSettingChangeListener;

    private boolean congestion, cost, hightspeed, avoidhightspeed;
    List<StrategyStateBean> mStrategys = new ArrayList<StrategyStateBean>();
    @Bind(R.id.strategy_list)
    GridView mStrategyChooseListView;
    private StrategyChooseAdapter mStrategyAdapter;
    private StrategyChangeListener mListener;
    private final  static int CLOSE=0X11;

    private int mCloseTime=10*1000;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==CLOSE){
                dismiss();
            }
        }
    };

    public NaviSettingDialog(Context context) {
        super(context);
        mContext=context;
    }

    public NaviSettingDialog(Context context, int theme) {
        super(context, theme);
        mContext=context;
    }

    protected NaviSettingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext=context;
    }

    public void setSettingChangeListener(NaviSettingChangeListener settingChangeListener) {
        this.mSettingChangeListener = settingChangeListener;
    }

    public void setSpeakContent(int speakMode) {
        if (speakMode==0){
            naviCB.setEnabled(false);
            eyeCB.setEnabled(false);
            trafficCB.setEnabled(false);

        }else{
            naviCB.setEnabled(true);
            eyeCB.setEnabled(true);
            trafficCB.setEnabled(true);
        }

    }
    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mSpeakerMode = 1;
//        mMapMode = 2;
//    }



    public  interface NaviSettingChangeListener{
        void settingChange();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (GlobalCfg.IS_POTRAIT) {
            setContentView(R.layout.dialog_navi_setting);
        } else {
            setContentView(R.layout.dialog_navi_setting_l);

        }
        ButterKnife.bind(this);
        if (GlobalCfg.IS_POTRAIT) {
            getWindow().setWindowAnimations(R.style.bottom_in); //设置窗口弹出动画
        }else {
            getWindow().setWindowAnimations(R.style.left_in); //设置窗口弹出动画
        }

        ;

        if (GlobalCfg.IS_POTRAIT) {
            getWindow().setWindowAnimations(R.style.bottom_in); //设置窗口弹出动画
            getWindow().setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = DensityUtils.getScreenWidth(mContext);
            params.height=mContext.getResources().getDimensionPixelSize(R.dimen.size_580dp);
            params.y=mContext.getResources().getDimensionPixelSize(R.dimen.size_48dp);
            getWindow().setAttributes(params);

        }else {
            getWindow().setWindowAnimations(R.style.right_in); //设置窗口弹出动画
            getWindow().setGravity(Gravity.BOTTOM|Gravity.LEFT);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = DensityUtils.getScreenWidth(mContext)-mContext.getResources().getDimensionPixelSize(R.dimen.size_120dp);
            params.height=DensityUtils.getScreenHeight(mContext)-mContext.getResources().getDimensionPixelSize(R.dimen.size_48dp);
            params.x=mContext.getResources().getDimensionPixelSize(R.dimen.size_120dp);
            params.y=mContext.getResources().getDimensionPixelSize(R.dimen.size_48dp);
            getWindow().setAttributes(params);
        }
        mMapModeButtons.add(mDayModeBtn);
        mMapModeButtons.add(mNightModeBtn);
        mMapModeButtons.add(mAutoModeBtn);
        initSetting();
        mHandler.sendEmptyMessageDelayed(CLOSE,mCloseTime);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        mHandler.removeMessages(CLOSE);
        mHandler.sendEmptyMessageDelayed(CLOSE,mCloseTime);
        return super.dispatchTouchEvent(ev);
    }


    @OnClick({R.id.close, R.id.eye, R.id.traffic, R.id.navi, R.id.day_mode, R.id.night_mode,
            R.id.auto_mode, R.id.speak_mode,R.id.ok,R.id.atm_layout,R.id.wc_layout,R.id.gas_layout,R.id.maintenance_layout,R.id.scale_check})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.close:

                dismiss();

//                setResult(NaviFragment.NAVI_SETTING_REQUST);
//                finish();
//                ((Activity) mContext).overridePendingTransition(0, 0);
                break;
            case R.id.ok:
                dismiss();
                saveSetting();
                if (mSettingChangeListener!=null){
                    mSettingChangeListener.settingChange();
                }
                setResultIntent(-1);
                break;
            case R.id.eye:
                if(!eyeCB.isChecked()){
                    SpeakerContent.getInstance().removeEyeMsg();
                }
                else {
                    SpeakerContent.getInstance().setEyeMsg();
                }
                break;
            case R.id.traffic:
                if(!trafficCB.isChecked()){
                    SpeakerContent.getInstance().removeTrafficMsg();;
                }
                else {
                    SpeakerContent.getInstance().setTrafficMsg();
                }
                break;
            case R.id.navi:
                if(!naviCB.isChecked()){
                    SpeakerContent.getInstance().removeNaviMsg();
                }
                else {
                    SpeakerContent.getInstance().setNaviMsg();
                }
                break;
            case R.id.day_mode:
                setButtonSelected(mMapModeButtons, 0);
                mMapMode = 0;
                break;
            case R.id.night_mode:
                setButtonSelected(mMapModeButtons, 1);
                mMapMode = 1;
                break;
            case R.id.auto_mode:
                setButtonSelected(mMapModeButtons, 2);
                mMapMode = 2;
                break;
            case R.id.speak_mode:
                if (mSpeakModeCheckBox.isChecked()){
                    mSpeakerMode=0;
                }else {
                    mSpeakerMode=1;
                }
                setSpeakContent(mSpeakerMode);
                break;
            case R.id.maintenance_layout:
                dismiss();
                setResultIntent(MapCfg.MAINTENCE);
                break;
            case R.id.wc_layout:
                dismiss();
                setResultIntent(MapCfg.WC);
                break;
            case  R.id.gas_layout:
                dismiss();
                setResultIntent(MapCfg.GAS);
                break;
            case R.id.atm_layout:
                dismiss();
                setResultIntent(MapCfg.ATM);
                break;
            case R.id.scale_check:
                if (mScaleCheckBox.isChecked()){
                    mScaleOpen=true;
                }else{
                    mScaleOpen=false;
                }

                break;


        }
    }

    private void setButtonSelected(List<Button> list, int index) {
        for (Button bt : list) {
            bt.setSelected(false);
        }

        list.get(index).setSelected(true);
    }

    private void initSetting() {
        mMapMode = CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_MAP_MODE, 2);
        mMapModeButtons.get(mMapMode).setSelected(true);

        mScaleOpen=CacheUtils.getInstance(mContext).getBoolean(SettingCfg.NAVI_SCALE_OPEN,true);
        if (mScaleOpen){
            mScaleCheckBox .setChecked(true);
        }else{
            mScaleCheckBox.setChecked(false);
        }

        mSpeakerMode = CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_SPEAKER, 1);
        if (mSpeakerMode==1){
            mSpeakModeCheckBox.setChecked(false);
        }else {
            mSpeakModeCheckBox.setChecked(true);
        }
        mSpeakerContent = CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_SPEAKER_CONTENT, 0x07);
        SpeakerContent.getInstance().setMsgContent(mSpeakerContent);
        if(SpeakerContent.getInstance().hasElectricEyeMsg()){
            eyeCB.setChecked(true);
        }
        else{
            eyeCB.setChecked(false);
        }

        if(SpeakerContent.getInstance().hasTrafficMsg()){
            trafficCB.setChecked(true);
        }
        else{
            trafficCB.setChecked(false);
        }

        if(SpeakerContent.getInstance().hasNaviMsg()){
            naviCB.setChecked(true);
        }
        else{
            naviCB.setChecked(false);
        }
        setSpeakContent(mSpeakerMode);

        congestion=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_CONGESTION,false);
        cost=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_COST,false);
        avoidhightspeed=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_HIGHSPEED,false);
        hightspeed=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_PRIORITY_HIGHSPEED,false);
        mStrategys.add(new StrategyStateBean(Utils.AVOID_CONGESTION, congestion));
        mStrategys.add(new StrategyStateBean(Utils.AVOID_COST, cost));
        mStrategys.add(new StrategyStateBean(Utils.AVOID_HIGHSPEED, avoidhightspeed));
        mStrategys.add(new StrategyStateBean(Utils.PRIORITY_HIGHSPEED, hightspeed));
        mStrategyAdapter = new StrategyChooseAdapter(mContext, mStrategys);


        mStrategyChooseListView.setAdapter(mStrategyAdapter);

    }

    private void saveSetting(){
        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_MAP_MODE, mMapMode);
        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_TRAFFIC_ON_OFF, mTrafficOnOff);
        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_SPEAKER, mSpeakerMode);
        CacheUtils.getInstance(mContext).putInt(SettingCfg.NAVI_SPEAKER_CONTENT, SpeakerContent.getInstance().getSpeakercotent());
        CacheUtils.getInstance(mContext).putBoolean(SettingCfg.NAVI_SCALE_OPEN,mScaleOpen);
    }
    private void setResultIntent(int type){
        StrategyBean strategyBean=new StrategyBean();
        for (StrategyStateBean bean : mStrategys) {
            if (bean.getStrategyCode() == Utils.AVOID_CONGESTION) {
                CacheUtils.getInstance(mContext).putBoolean(Utils.INTENT_NAME_AVOID_CONGESTION, bean.isOpen());
                strategyBean.setCongestion(bean.isOpen());
            }

            if (bean.getStrategyCode() == Utils.AVOID_COST) {
                CacheUtils.getInstance(mContext).putBoolean(Utils.INTENT_NAME_AVOID_COST, bean.isOpen());
                strategyBean.setCost(bean.isOpen());
            }

            if (bean.getStrategyCode() == Utils.AVOID_HIGHSPEED) {
                CacheUtils.getInstance(mContext).putBoolean(Utils.INTENT_NAME_AVOID_HIGHSPEED, bean.isOpen());
                strategyBean.setAvoidhightspeed(bean.isOpen());
            }

            if (bean.getStrategyCode() == Utils.PRIORITY_HIGHSPEED) {
                CacheUtils.getInstance(mContext).putBoolean(Utils.INTENT_NAME_PRIORITY_HIGHSPEED, bean.isOpen());
                strategyBean.setHightspeed(bean.isOpen());
            }
        }
        if (mListener!=null){
            mListener.getCurrentStrategy(strategyBean,type);
        }

    }
    public void setStrategyChangeListener(StrategyChangeListener listener) {
        mListener=listener;
    }
    public interface StrategyChangeListener{
        void getCurrentStrategy(StrategyBean bean,int type);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(this);
            mHandler=null;
        }
    }

    public static class SpeakerContent{
        private static int mContent;
        private static SpeakerContent mSpeakerContent;

        public static SpeakerContent getInstance(){
            if(mSpeakerContent == null){
                mSpeakerContent = new SpeakerContent(0x07);
            }

            return mSpeakerContent;
        }

        public void setMsgContent(int content){
            mContent = content;
        }

        private SpeakerContent(int content){
            mContent = content;
        }

        public boolean hasElectricEyeMsg(){
            return (mContent & 0x01) == 0x01;
        }

        public boolean hasTrafficMsg(){
            return (mContent & 0x02) == 0x02;
        }

        public boolean hasNaviMsg(){
            return (mContent & 0x04) == 0x04;
        }

        public void setEyeMsg(){
            mContent = mContent | 0x01;
        }

        public void setTrafficMsg(){
            mContent = mContent | 0x02;
        }

        public void setNaviMsg(){
            mContent = mContent | 0x04;
        }

        public void removeEyeMsg(){
            mContent = mContent & ~0x01;
        }

        public void removeTrafficMsg(){
            mContent = mContent & ~0x02;
        }

        public void removeNaviMsg(){
            mContent = mContent & ~0x04;
        }

        public int getSpeakercotent(){
            return mContent;
        }
    }


}