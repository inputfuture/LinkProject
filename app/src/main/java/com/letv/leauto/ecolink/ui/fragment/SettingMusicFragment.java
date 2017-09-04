package com.letv.leauto.ecolink.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.cfg.SettingMusicCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by lww on 9/22/15.
 */
public class SettingMusicFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.lyt_download)
    RelativeLayout downloadSetting;
    @Bind(R.id.lyt_play)
    RelativeLayout cleanSetting;

    @Bind(R.id.iv_download_select)
    ImageView iv_download;

    @Bind(R.id.iv_play)
    ImageView iv_play;

    @Bind(R.id.iv_back)
    ImageView iv_back;

    private NetworkConfirmDialog mConfirmDialog;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.setting_music, null);
        } else {
            view = inflater.inflate(R.layout.setting_music_l, null);
        }
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void initView() {
        downloadSetting.setOnClickListener(this);
        cleanSetting.setOnClickListener(this);

        iv_back.setOnClickListener(this);
        //shimeng fix for bug332,20160516,begin
//        if(MusicDownloadManager.isDownloadFileMaximum(EcoApplication.instance)){
//            tv_size.setText("500M");
//        }else {
//            tv_size.setText(MusicDownloadManager.getCacheSize());
//        }
        //shimeng fix for bug332,20160516,end
        setDownLoadState();
        setPlayState();


    }

    private void setPlayState() {
        if (CacheUtils.getInstance(mContext)
                .getBoolean(SettingMusicCfg.USER_MOBILE_NET_PLAY, false)) {
            iv_play
                    .setImageResource(R.mipmap.switch_on);
        } else {
            iv_play
                    .setImageResource(R.mipmap.switch_off);
        }
    }

    private void setDownLoadState() {
        if (CacheUtils.getInstance(mContext)
                .getBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, false)) {
            iv_download
                    .setImageResource(R.mipmap.switch_on);
        } else {
            iv_download
                    .setImageResource(R.mipmap.switch_off);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyt_download:

                if (!CacheUtils.getInstance(mContext)
                        .getBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, false)) {
                    mConfirmDialog = new NetworkConfirmDialog(mContext, R.string.mobile_download, R.string.str_open, R.string.cancel);
                    mConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                                                   @Override
                                                   public void onConfirm(boolean checked) {
                                                       CacheUtils.getInstance(mContext).putBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, true);
                                                       iv_download.setImageResource(R.mipmap.switch_on);
                                                   }

                                                   @Override
                                                   public void onCancel() {

                                                   }
                                               }

                    );

                    mConfirmDialog.show();
                } else {
                    CacheUtils.getInstance(mContext).putBoolean(SettingMusicCfg.USER_MOBILE_NET_DOWNED, false);
                    iv_download.setImageResource(R.mipmap.switch_off);
                }

                break;
            case R.id.lyt_play:
                if (CacheUtils.getInstance(mContext).getBoolean(SettingMusicCfg.USER_MOBILE_NET_PLAY, false)) {
                    CacheUtils.getInstance(mContext).putBoolean(SettingMusicCfg.USER_MOBILE_NET_PLAY, false);
                    iv_play.setImageResource(R.mipmap.switch_off);
                } else {
                    mConfirmDialog = new NetworkConfirmDialog(mContext, R.string.mobile_play, R.string.str_open, R.string.cancel);
                    mConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                        @Override
                        public void onConfirm(boolean checked) {
                            CacheUtils.getInstance(mContext).putBoolean(SettingMusicCfg.USER_MOBILE_NET_PLAY, true);
                            iv_play.setImageResource(R.mipmap.switch_on);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    mConfirmDialog.show();
                }


                break;
            case R.id.iv_back:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFragment()).commitAllowingStateLoss();
                break;

            case R.id.tv_cancle:
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
        MobclickAgent.onPageStart("SettingMusicFragment");
    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd("SettingMusicFragment");
    }

    IntentFilter intentFilter;
    DownLoadSwicherBroadCast mBroadCast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentFilter = new IntentFilter();
        intentFilter.addAction(SettingCfg.BROADCAST_DOWNlOAD_SWICH);
        intentFilter.addAction(SettingCfg.BROADCAST_2G_PLAY_SWICH);
        mBroadCast = new DownLoadSwicherBroadCast();
        mContext.registerReceiver(mBroadCast, intentFilter);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mBroadCast != null) {
            mContext.unregisterReceiver(mBroadCast);
            mBroadCast = null;
        }
    }

    class DownLoadSwicherBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == SettingCfg.BROADCAST_DOWNlOAD_SWICH) {
                setDownLoadState();
            } else if (intent.getAction() == SettingCfg.BROADCAST_2G_PLAY_SWICH) {
                setPlayState();
            }

        }
    }


}
