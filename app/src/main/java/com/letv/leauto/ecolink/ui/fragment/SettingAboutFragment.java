package com.letv.leauto.ecolink.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.Trace;
import com.tencent.bugly.beta.Beta;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhaotongkai on 2016/9/28.
 */
public class SettingAboutFragment extends BaseFragment implements View.OnClickListener{

    @Bind(R.id.iv_back)
    ImageView mBack;
    @Bind(R.id.lyt_cusser)
    RelativeLayout mCusser;
    @Bind(R.id.lyt_version)
    RelativeLayout mVersion;
    @Bind(R.id.iv_version)
    ImageView mIVversion;
    @Bind(R.id.tv_version_code)
    TextView mVercode;

    @Bind(R.id.tv_notify)
    TextView mTVNotity;
    @Bind(R.id.lyt_newhelp)
    RelativeLayout mNewHelpLayout;

    public static SettingAboutFragment getInstance(Bundle bundle) {
        SettingAboutFragment mFragment = new SettingAboutFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }
    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.setting_about, null);
        } else {
            view = inflater.inflate(R.layout.setting_about_l, null);
        }
        ButterKnife.bind(this, view);
        initView();

        return view;
    }


    @Override
    protected void initData(Bundle savedInstanceState) {


    }

    private void initView() {
        mCusser.setOnClickListener(this);
        mVercode.setText(getVersion());
        mBack.setOnClickListener(this);
        mTVNotity.setOnClickListener(this);
        mNewHelpLayout.setOnClickListener(this);
        mVersion.setOnClickListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lyt_cusser:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingCustomSerFragment(),"SettingCustomSerFragment").commitAllowingStateLoss();
                break;
            case R.id.iv_back:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFragment()).commitAllowingStateLoss();
                break;
            case R.id.tv_notify:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new DisclaimerFragment()).commitAllowingStateLoss();
                break;
            case R.id.lyt_newhelp:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new NewHelpFragment()).commitAllowingStateLoss();
                break;
            case R.id.lyt_version:
                Beta.checkUpgrade();
                break;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.04";
        }
    }

}
