package com.letv.leauto.ecolink.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.voicehelp.LeVoicePopupWindow;
import com.letv.voicehelp.manger.command.LeVoiceCommandManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 2017/3/7.
 */
public class NewHelpFragment extends BaseFragment implements View.OnClickListener{
    @Bind(R.id.lyt_voice)
    RelativeLayout mVoiceLayout;
    @Bind(R.id.lyt_introduce)
    RelativeLayout mIntroduceLayout;
    @Bind(R.id.lyt_connect)
    RelativeLayout mConnectLayout;
    @Bind(R.id.tv_voice)
    TextView mVoiceTextView;
    @Bind(R.id.tv_introduce)
    TextView mIntroduce;
    @Bind(R.id.tv_connect)
    TextView mConnectTextView;
    @Bind(R.id.iv_back)
    ImageView mBackView;
    @Override
    protected View initView(LayoutInflater inflater) {
        View  view = inflater.inflate(R.layout.fragment_new_help, null);
        ButterKnife.bind(this, view);
        mConnectLayout.setOnClickListener(this);
        mVoiceLayout.setOnClickListener(this);
        mIntroduceLayout.setOnClickListener(this);
        mBackView.setOnClickListener(this);
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingAboutFragment()).commitAllowingStateLoss();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.lyt_voice:
                new LeVoicePopupWindow(mContext).justForShowHelp(mVoiceLayout);
//                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new VoiceHelpIntroFragment()).commitAllowingStateLoss();
                break;
            case R.id.lyt_connect:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new ConnectHelpFragment()).commitAllowingStateLoss();
                break;
            case R.id.lyt_introduce:
                Intent intent=new Intent(mContext,IntroduceActivity.class);
                startActivity(intent);

//                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new IntroduceActivity()).commitAllowingStateLoss();
                break;
            case R.id.iv_back:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingAboutFragment()).commitAllowingStateLoss();
                break;

        }
    }
}
