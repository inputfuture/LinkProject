package com.letv.leauto.ecolink.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.Trace;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/1.
 */
public class SettingSelectCacheFragment extends BaseFragment implements View.OnClickListener{

    @Bind(R.id.rl_ecolink)
    RelativeLayout rl_ecolink;
    @Bind(R.id.iv_ecolink)
    ImageView iv_ecolink;
    @Bind(R.id.rl_gaode)
    RelativeLayout rl_gaode;
    @Bind(R.id.iv_gaode_map)
    ImageView iv_gaode_map;
    @Bind(R.id.rl_baidu)
    RelativeLayout rl_baidu;
    @Bind(R.id.iv_baidu_map)
    ImageView iv_baidu_map;
    @Bind(R.id.rl_800m)
    RelativeLayout rl_800m;
    @Bind(R.id.iv_800M)
    ImageView iv_800M;
    @Bind(R.id.rl_1g)
    RelativeLayout rl_1g;
    @Bind(R.id.iv_1g)
    ImageView iv_1g;
    @Bind(R.id.iv_back)
    ImageView iv_back;

    private int key;


    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_select_cache_setting_p, null);
        } else {
            view = inflater.inflate(R.layout.fragment_select_cache_setting, null);
        }
        ButterKnife.bind(this, view);
        addListeners();

        key = CacheUtils.getInstance(mContext).getInt(SettingCfg.CACHE_LEVEL, 2);

        choseItem(key);
        return view;
    }

    private void addListeners() {
        rl_baidu.setOnClickListener(this);
        rl_ecolink.setOnClickListener(this);
        rl_gaode.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        rl_800m.setOnClickListener(this);
        rl_1g.setOnClickListener(this);

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_ecolink:
                choseItem(0);

                break;
            case R.id.rl_gaode:
                choseItem(1);

                break;
            case R.id.rl_baidu:
                choseItem(2);

                break;

            case R.id.rl_800m:

                choseItem(3);

                break;
            case R.id.rl_1g:

                choseItem(4);

                break;

            case R.id.iv_back:

                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingCacheFragment(),SettingCacheFragment.TAG ).commitAllowingStateLoss();
                break;

        }
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
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingCacheFragment(), SettingCacheFragment.TAG).commitAllowingStateLoss();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    public void choseItem(int i) {
        resetImgs();
        key = i;
        CacheUtils.getInstance(mContext).putInt(SettingCfg.CACHE_LEVEL, key);
        switch (i) {
            case 0:
                iv_ecolink.setImageResource(R.mipmap.set_naviset_sel);
                break;
            case 1:
                iv_gaode_map.setImageResource(R.mipmap.set_naviset_sel);
                break;
            case 2:
                iv_baidu_map.setImageResource(R.mipmap.set_naviset_sel);
                break;
            case 3:
                iv_800M.setImageResource(R.mipmap.set_naviset_sel);
                break;
            case 4:
                iv_1g.setImageResource(R.mipmap.set_naviset_sel);
                break;

        }
    }

    private void resetImgs() {
        iv_baidu_map.setImageResource(R.mipmap.song_not_selected);
        iv_ecolink.setImageResource(R.mipmap.song_not_selected);
        iv_gaode_map.setImageResource(R.mipmap.song_not_selected);
        iv_800M.setImageResource(R.mipmap.song_not_selected);
        iv_1g.setImageResource(R.mipmap.song_not_selected);

    }
}
