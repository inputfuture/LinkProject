package com.letv.leauto.ecolink.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.lemap.offlinemap1.OfflineMapFragment;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liweiwei on 16/1/25.
 */
public class SettingNaviFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "SettingNaviFragment";

    @Bind(R.id.lyt_setting_work)

    RelativeLayout rl_setNavi;

    @Bind(R.id.lyt_offline_map)
    RelativeLayout lyt_offline_map;

    @Bind(R.id.iv_back)
    ImageView iv_back;



    public static SettingNaviFragment getInstance(Bundle bundle) {
        SettingNaviFragment mFragment = new SettingNaviFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.setting_navi, null);
        } else {
            view = inflater.inflate(R.layout.setting_navi_l, null);
        }
        ButterKnife.bind(this, view);
        initView();

        return view;
    }

    private void initView() {

        lyt_offline_map.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        rl_setNavi.setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyt_setting_work:

                ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame,new SelectNaviFragment()).commitAllowingStateLoss();

                break;
            case R.id.lyt_offline_map:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new OfflineMapFragment(),"OfflineMapFragment").commitAllowingStateLoss();
                break;
            case R.id.iv_back:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFragment()).commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFragment()).commitAllowingStateLoss();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingNaviFragment");

    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        MobclickAgent.onPageEnd("SettingNaviFragment");

    }

}
