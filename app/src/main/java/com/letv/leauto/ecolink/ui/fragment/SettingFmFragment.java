package com.letv.leauto.ecolink.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.csr.gaia.library.Gaia;
import com.csr.gaia.library.GaiaPacket;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.StringHexUtil;
import com.letv.leauto.ecolink.utils.Trace;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shimeng on 2017/2/14.
 */
public class SettingFmFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tvTitle)
    TextView tv_title;
    @Bind(R.id.btn_thincar_help)
    Button btn_help;
    @Bind(R.id.tv_refesh_fm)
    TextView tv_refresh_fm;
    @Bind(R.id.tv_fm_title)
    TextView tv_fm_rate;
    @Bind(R.id.iv_fm_left)
    ImageView iv_decrease;
    @Bind(R.id.iv_fm_right)
    ImageView iv_increase;
    @Bind(R.id.seek_bar_fm)
    SeekBar seekbar;
    int mProgress = 0;

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);
        switch (keyCode) {
            case Constant.KEYCODE_DPAD_LEFT:
                seekbar.setProgress(mProgress - 1);
                break;
            case Constant.KEYCODE_DPAD_RIGHT:
                seekbar.setProgress(mProgress + 1);
                break;
            case Constant.KEYCODE_DPAD_CENTER:
                setFMFREQ();
                break;
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
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_fm_setting, null);
        } else {
            view = inflater.inflate(R.layout.fragment_fm_setting_l, null);
        }
        ButterKnife.bind(this, view);
        initView();

        return view;
    }

    private void initView() {
        tv_title.setText(R.string.setting_fm);
        tv_refresh_fm.setOnClickListener(this);
        btn_help.setOnClickListener(this);
        iv_decrease.setOnClickListener(this);
        iv_increase.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        enableSeekPosition();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        askForFMFREQ();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void askForFMFREQ() {
        if (mGaiaLink.isConnected()) {
            sendGaiaPacket(Gaia.COMMAND_GET_FM_FREQ_CONTROL);
        } else {
            Trace.Debug("fmsetting--"+"error");
        }

    }

    private void setFMFREQ() {
        if (mGaiaLink.isConnected()) {
            Trace.Debug("setFMFREQ："+ Integer.toHexString(mProgress + 870));
            String hex = Integer.toHexString(mProgress + 870);
            byte[] tmp = StringHexUtil.HexString2Bytes(Integer.toHexString(mProgress + 870));
            Integer bt1 = Integer.parseInt(hex.substring(0, hex.length() - 2), 16);
            Integer bt2 = Integer.parseInt(hex.substring(hex.length() - 2, hex.length()), 16);
            Trace.Debug("setFMFREQ："+ bt1 + "---" + bt2);
            sendGaiaPacket(Gaia.COMMAND_SET_FM_FREQ_CONTROL, bt1, bt2);
            Toast.makeText(mContext, "调频成功", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(mContext, "车盒未连接,请先连接车盒", Toast.LENGTH_LONG).show();

        }
    }

    private void sendGaiaPacket(int command, int... payload) {
        mGaiaLink.sendCommand(Gaia.VENDOR_CSR, command, payload);
    }

    @Override
    public void receivePacketGetFMFREQControl(GaiaPacket packet) {
        super.receivePacketGetFMFREQControl(packet);
//        showFMFREQ.setText(data[1] + "," + data[2]);
        Trace.Debug("receivePacketGetFMFREQControl："+ data[1] + "," + data[2]);
        mProgress = Integer.valueOf(StringHexUtil.Bytes2HexString(data), 16) - 870;
        upDateFMRate(mProgress);
        seekbar.setProgress(mProgress);
    }


    private void enableSeekPosition() {
        try {
            seekbar.setEnabled(true);
            seekbar.setMax(210);
            seekbar.setProgress(0);
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {

                        Trace.Debug("enableSeekPosition：fromUser = " + fromUser);
                        System.out.println("liweiwei....progress = " + progress);
                        mProgress = progress;
                        // if (fromUser) {
                        // seekbar.setProgress(progress / 1000);
                        upDateFMRate(progress);
                        // }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void upDateFMRate(int progress) {
        Trace.Debug("upDateFMRate："+ progress + "");
        String rate;
        int a = progress / 10 + 87;
        int b = progress % 10;
        rate = a + "." + b;
        tv_fm_rate.setText(rate);
    }

    private void regulateSeekBar(Boolean isIncrease, int progress) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_refesh_fm:
                setFMFREQ();
                break;
            case R.id.iv_back:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFragment()).commitAllowingStateLoss();
                break;
            case R.id.iv_fm_left:
                seekbar.setProgress(mProgress - 1);
                break;
            case R.id.iv_fm_right:
                seekbar.setProgress(mProgress + 1);
                break;
            case R.id.btn_thincar_help:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingHelpFragment(), "SettingHelpFragment").addToBackStack(null).commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

}
