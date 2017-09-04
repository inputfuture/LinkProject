package com.letv.leauto.ecolink.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.controller.EcoTTSController;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.utils.NetUtils;

public abstract class BasePage {
    protected Context ct;
    protected View contentView;
    protected LePlayer lePlayer;
    public View rootView;
    protected EcoTTSController ttsHandlerController;
    public Boolean isNetConnect = false;

    public BasePage(Context context) {
        ct = context;
        ttsHandlerController = EcoApplication.LeGlob.getTtsController();
        isNetConnect = NetUtils.isConnected(ct);
        lePlayer = EcoApplication.LeGlob.getPlayer();
        lePlayer.openServiceIfNeed();
        contentView = initView((LayoutInflater) ct
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE));

    }


    public View getContentView() {
        return contentView;
    }

    protected abstract View initView(LayoutInflater inflater);

    public abstract void initData();

    public void refresh() {

    }

    public void onResume() {

    }

    public void getData() {

    }

    protected void onDestroy() {

    }

    public void destory(){}

}
