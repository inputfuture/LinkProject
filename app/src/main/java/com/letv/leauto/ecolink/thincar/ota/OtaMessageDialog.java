package com.letv.leauto.ecolink.thincar.ota;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.tracker.env.Hardware;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 2017/7/17.
 */

public class OtaMessageDialog extends Dialog implements View.OnClickListener{
    @Bind(R.id.version)
    TextView mVersionView;
    @Bind(R.id.size)
    TextView mSizeView;

    @Bind(R.id.time)
    TextView mTimeView;
    @Bind(R.id.content)
    TextView mContentView;
    @Bind(R.id.update)
    Button mUpdateButton;
    @Bind(R.id.progress)
    ProgressBar mProgressBar;
    @Bind(R.id.ok)
    Button mSureButton;
    @Bind(R.id.type0)
    RelativeLayout mRelativeLayout0;
    @Bind(R.id.type1)
    RelativeLayout mRelativeLayout1;
    @Bind(R.id.type2)
    RelativeLayout mRelativeLayout2;
    @Bind(R.id.icon)
    ImageView mStateIconView;

    @Bind(R.id.close)
    ImageView mColoseView;
    @Bind(R.id.progress_txt)
    TextView mProgressText;


    private int mType=0;
    UpdateClickListener mUpdateClickListener;
    private OtaEntity mOtaEntity;
    private Context mContext;

    public OtaMessageDialog(@NonNull Context context) {

        super(context);
    }

    public OtaMessageDialog(@NonNull Context context, @StyleRes int themeResId,int type) {
        super(context, themeResId);
        mContext=context;
        mType=type;
    }

    protected OtaMessageDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Trace.Debug("thincar","oncreate");
        super.onCreate(savedInstanceState);
        if (GlobalCfg.IS_POTRAIT){
            setContentView(R.layout.dialog_ota_message);
        }else{
            setContentView(R.layout.dialog_ota_message_land);
        }
        ButterKnife.bind(this);

        getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (GlobalCfg.IS_POTRAIT){
            params.width = mContext.getResources().getDimensionPixelSize(R.dimen.size_280dp);
            params.height=mContext.getResources().getDimensionPixelSize(R.dimen.size_480dp);
        }else{
            params.width = mContext.getResources().getDimensionPixelSize(R.dimen.size_520dp);
            params.height=mContext.getResources().getDimensionPixelSize(R.dimen.size_280dp);
        }

        getWindow().setAttributes(params);
        setlayoutByType(mType);
        mUpdateButton.setOnClickListener(this);
        mSureButton.setOnClickListener(this);
        mColoseView.setOnClickListener(this);
    }

    public void setlayoutByType(int mType) {
        switch (mType){
            case 0:
                mRelativeLayout0.setVisibility(View.VISIBLE);
                mRelativeLayout1.setVisibility(View.GONE);
                mRelativeLayout2.setVisibility(View.GONE);
                mStateIconView.setImageResource(R.mipmap.ota_update);
                break;
            case 1:
                mRelativeLayout0.setVisibility(View.GONE);
                mRelativeLayout1.setVisibility(View.VISIBLE);
                mRelativeLayout2.setVisibility(View.GONE);
                mStateIconView.setImageResource(R.mipmap.ota_downloading);
                break;
            case 2:
                mRelativeLayout0.setVisibility(View.GONE);
                mRelativeLayout1.setVisibility(View.GONE);
                mRelativeLayout2.setVisibility(View.VISIBLE);
                mStateIconView.setImageResource(R.mipmap.ota_download_finish);
                break;

        }
    }


    public void setProgress(int progress){
        mProgressBar.setProgress(progress);
        mProgressText.setText(progress+"%");

    }

    public void setUpdateClickListener(UpdateClickListener mUpdateClickListener) {
        this.mUpdateClickListener = mUpdateClickListener;
    }

    public void setOtaEntity(OtaEntity otaEntity) {
        this.mOtaEntity = otaEntity;
        mVersionView.setText("版本："+mOtaEntity.getCarVersion());
        mSizeView.setText("大小："+mOtaEntity.getPkgSize());
        mTimeView.setText("日期：");
        mContentView.setText(otaEntity.getMessage());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update:
                if (mUpdateClickListener!=null){
                    mUpdateClickListener.onClick();
                }
                break;
            case R.id.ok:
                dismiss();

                break;
            case R.id.close:
                dismiss();
                break;
        }

    }

    public interface UpdateClickListener{
        void  onClick();
    }
}
