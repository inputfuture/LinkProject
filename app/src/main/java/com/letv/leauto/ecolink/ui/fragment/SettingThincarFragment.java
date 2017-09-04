package com.letv.leauto.ecolink.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guo on 2017/7/6.
 */

public class SettingThincarFragment extends BaseFragment implements View.OnClickListener{

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.btn_buy)
    Button btnBuy;
    @Bind(R.id.btn_link)
    Button btnLink;
    @Bind(R.id.btn_thincar_help)
    Button btnHelp;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if(GlobalCfg.IS_POTRAIT){
            view = inflater.inflate(R.layout.fragment_thincar_unconn, null);
        }else {
            view = inflater.inflate(R.layout.fragment_thincar_unconn_l, null);
        }
        ButterKnife.bind(this, view);
        ivBack.setOnClickListener(this);
        btnLink.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnBuy.setOnClickListener(this);
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFragment()).commitAllowingStateLoss();
                break;
            case R.id.btn_link:
                try {
                    Intent bt_intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(bt_intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_thincar_help:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingHelpFragment(), "SettingHelpFragment").addToBackStack(null).commitAllowingStateLoss();
                break;
            case R.id.btn_buy:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingBuyFragment(), "SettingBuyFragment").addToBackStack(null).commitAllowingStateLoss();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
