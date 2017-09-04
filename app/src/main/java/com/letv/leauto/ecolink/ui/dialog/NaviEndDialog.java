package com.letv.leauto.ecolink.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isnc.facesdk.view.FaceRegistView;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.lemap.navi.Utils;
import com.letv.leauto.ecolink.lemap.navi.routebean.StrategyBean;
import com.letv.leauto.ecolink.lemap.navi.routebean.StrategyStateBean;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 驾车偏好设置
 */
public class NaviEndDialog extends Dialog {
    private static final int CLOSE = 0X11;
    @Bind(R.id.close_layout)
    LinearLayout mEndLayout;
    @Bind(R.id.distance)
    TextView mDistanceView;
    @Bind(R.id.all_time)
    TextView mAllTimeView;
    @Bind(R.id.average_speed)
    TextView mAverageSpeedView;
    @Bind(R.id.hight_speed)
    TextView mHightSpeedView;
    @Bind(R.id.time)
    TextView mDelaytimeView;
    private Context mContext;
    private CloseListener mListener;


    private String mDistance,mAllTime,mAverageSpeed,mHightSpeed;
    private int mDelayTime=10;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==CLOSE){
                mDelayTime--;
                mDelaytimeView.setText(mDelayTime+"");
                if (mHandler==null){
                    return;
                }
                mHandler.sendEmptyMessageDelayed(CLOSE,1000);
                if (mDelayTime==0){

                    if (mListener!=null){
                        mListener.close();
                    }
                }
            }
        }
    };

    public NaviEndDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        mContext=context;
    }

    public void setDistance(String mDistance) {
        this.mDistance = mDistance;
        if (mDistanceView != null) {
            mDistanceView.setText(mDistance);
        }
    }

    public void setAllTime(String mAllTime) {
        this.mAllTime = mAllTime;
        if (mAllTimeView != null) {
            mAllTimeView.setText(mAllTime);
        }
    }

    public void setAverageSpeed(String mAverageSpeed) {
        this.mAverageSpeed = mAverageSpeed;
        if (mAverageSpeedView != null) {
            mAverageSpeedView.setText(mAverageSpeed);
        }
    }

    public void setHightSpeed(String mHightSpeed) {
        this.mHightSpeed = mHightSpeed;
        if (mHightSpeedView != null) {
            mHightSpeedView.setText(mHightSpeed);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GlobalCfg.IS_POTRAIT){
            setContentView(R.layout.dialog_navi_end);
        }else{
            setContentView(R.layout.dialog_navi_end1);
        }
        ButterKnife.bind(this);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = DensityUtils.getScreenWidth(mContext);
        params.height = DensityUtils.getScreenHeight(mContext);
        getWindow().setAttributes(params);
        mDelaytimeView.setText(mDelayTime+"");
        mHandler.sendEmptyMessageDelayed(CLOSE,1000);



    }





    @OnClick({R.id.close_layout})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_layout:
                dismiss();
                if (mListener!=null){
                    mListener.close();
                }
                break;

        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(this);
            mHandler=null;
        }
    }

    public void setCloseListener(CloseListener listener) {
        mListener=listener;

    }
    public  interface CloseListener{
        void close();
    }
}
