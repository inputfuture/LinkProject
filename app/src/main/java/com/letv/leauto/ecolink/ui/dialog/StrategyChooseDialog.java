package com.letv.leauto.ecolink.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.lemap.navi.Utils;
import com.letv.leauto.ecolink.lemap.navi.routebean.StrategyBean;
import com.letv.leauto.ecolink.lemap.navi.routebean.StrategyStateBean;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 驾车偏好设置
 */
public class StrategyChooseDialog extends Dialog implements View.OnClickListener {
    private boolean congestion, cost, hightspeed, avoidhightspeed;
    List<StrategyStateBean> mStrategys = new ArrayList<StrategyStateBean>();
    private GridView mStrategyChooseListView;
    private ImageView mCloseImageview;
    private TextView mUpdateButton;
    private StrategyChooseAdapter mStrategyAdapter;

    private Context mContext;
    private  StrategyChangeListener mListener;

    public StrategyChooseDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
//        super(context);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        congestion=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_CONGESTION,false);
        cost=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_COST,false);
        avoidhightspeed=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_AVOID_HIGHSPEED,false);
        hightspeed=CacheUtils.getInstance(mContext).getBoolean(Utils.INTENT_NAME_PRIORITY_HIGHSPEED,false);
        mStrategys.add(new StrategyStateBean(Utils.AVOID_CONGESTION, congestion));
        mStrategys.add(new StrategyStateBean(Utils.AVOID_COST, cost));
        mStrategys.add(new StrategyStateBean(Utils.AVOID_HIGHSPEED, avoidhightspeed));
        mStrategys.add(new StrategyStateBean(Utils.PRIORITY_HIGHSPEED, hightspeed));
        if (GlobalCfg.IS_POTRAIT){
            setContentView(R.layout.dialog_strategy_choose);
        }else{
            setContentView(R.layout.dialog_strategy_choose1);
        }

        if (GlobalCfg.IS_POTRAIT) {
            getWindow().setWindowAnimations(R.style.bottom_in); //设置窗口弹出动画
            getWindow().setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = DensityUtils.getScreenWidth(mContext);
            params.height=mContext.getResources().getDimensionPixelSize(R.dimen.size_270dp);
            params.y=mContext.getResources().getDimensionPixelSize(R.dimen.size_48dp);
            getWindow().setAttributes(params);

        }else {
            getWindow().setWindowAnimations(R.style.left_in); //设置窗口弹出动画
            getWindow().setGravity(Gravity.BOTTOM|Gravity.LEFT);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = mContext.getResources().getDimensionPixelSize(R.dimen.size_300dp);
            params.height=DensityUtils.getScreenHeight(mContext)-mContext.getResources().getDimensionPixelSize(R.dimen.size_48dp);
            params.y=mContext.getResources().getDimensionPixelSize(R.dimen.size_48dp);
            getWindow().setAttributes(params);
        }

        mStrategyAdapter = new StrategyChooseAdapter(mContext, mStrategys);
        mStrategyChooseListView = (GridView) findViewById(R.id.strategy_choose_list);
        mCloseImageview= (ImageView) findViewById(R.id.close);
        mUpdateButton= (TextView) findViewById(R.id.update_route);
        mCloseImageview.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);
        mStrategyChooseListView.setAdapter(mStrategyAdapter);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResultIntent();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setResultIntent(){
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
            mListener.getCurrentStrategy(strategyBean);
        }

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                dismiss();
                break;
            case R.id.update_route:
                setResultIntent();
                dismiss();
                break;
        }

    }

    public void setStrategyChangeListener(StrategyChangeListener listener) {
        mListener=listener;
    }

    public interface StrategyChangeListener{
        void getCurrentStrategy(StrategyBean bean);

    }
}
