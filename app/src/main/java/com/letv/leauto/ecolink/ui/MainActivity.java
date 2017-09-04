package com.letv.leauto.ecolink.ui;

import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.ScreenRecordActivity;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.Trace;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @Bind(R.id.default_img)
    LinearLayout mDefaultImg;
    private final static int DELAY = 1000;
    private final static int ENTER = 0X99;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ENTER) {
                checkStatement();
            }
        }
    };

    @Override
    protected void initView() {
        setContentView(R.layout.activity_guid);
        ButterKnife.bind(this);
        Trace.Debug("#### restart");
        LogUtils.i("MainActivity","initView mIsRestart:" + EcoApplication.mIsRestart);
        if (EcoApplication.mIsRestart) {
            startHomeActivity();
            mDefaultImg.setBackground(null);
          //  finish();

        } else {
            if (GlobalCfg.IS_POTRAIT) {
                mDefaultImg.setBackgroundResource(R.mipmap.welcom_port);

            } else {
                mDefaultImg.setBackgroundResource(R.mipmap.welcom_land);
            }
            mHandler.sendEmptyMessageDelayed(ENTER, DELAY);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("MainActivity","onResume intent:" + this.getIntent());
    }

    @Override
    public void onClick(View v) {

    }

    private void checkStatement() {
        boolean isThincar = isThinCar(this.getIntent());
        if (isThincar) {
            CacheUtils.getInstance(mContext).putBoolean(DisclaimerActivity.NEVER_MIND, true);
        }
        boolean mind = CacheUtils.getInstance(mContext).getBoolean(DisclaimerActivity.NEVER_MIND, false);
        if (!mind && DisclaimerActivity.firstBoot && !isThincar) {
            Intent intent = new Intent(this, DisclaimerActivity.class);
            startActivity(intent);
            mDefaultImg.setBackground(null);
        } else {
           // startRecordActivity(isThincar);
            startHomeActivity();
            mDefaultImg.setBackground(null);
        }
      //  finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;

        }
    }

    private void startRecordActivity(boolean isThincar) {
        Intent intent = new Intent(mContext, ScreenRecordActivity.class);
        if (isThincar) {
            intent.setAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        } else {
            intent.setAction("com.letv.leauto.ecolink.adb.launch");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setAction(this.getIntent().getAction());
        startActivity(intent);
    }
}