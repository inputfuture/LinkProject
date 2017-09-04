package com.letv.leauto.ecolink.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

public class SettingHelpFragment extends BaseFragment implements View.OnClickListener{

    @Bind(R.id.iv_back)
    ImageView ivBack;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if(GlobalCfg.IS_POTRAIT){
            view = inflater.inflate(R.layout.fragment_thincar_help, null);
        }else {
            view = inflater.inflate(R.layout.fragment_thincar_help_l, null);
        }
        ButterKnife.bind(this, view);
        ivBack.setOnClickListener(this);
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                ((HomeActivity) mContext).getSupportFragmentManager().popBackStack();
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
