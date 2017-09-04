package com.letv.leauto.ecolink.update;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 2016/9/1.
 */
public class CustomDialog extends Dialog implements View.OnClickListener{
    @Bind(R.id.title)
    TextView mTitleView;
    @Bind(R.id.message)
    TextView mMessageView;
    @Bind(R.id.negativeButton)
    TextView mCancelView;
    @Bind(R.id.positiveButton)
    TextView mOkbutton;
    private Context mContext;
    @Bind(R.id.update_not_mind)
    CheckBox mNotUpdateBox;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog_custom);
        ButterKnife.bind(this);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) mContext.getResources().getDimension(
                R.dimen.dialog_update_width);
        params.height = (int) mContext.getResources().getDimension(
                R.dimen.dialog_update_height);
        getWindow().setAttributes(params);
        mCancelView.setOnClickListener(this);
        mOkbutton.setOnClickListener(this);
        mNotUpdateBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mClickListener!=null){
                    mClickListener.notUpdateChecked(isChecked);
                }

            }
        });

    }

    public void setMessage(String s){
        if (s!=null){
        mMessageView.setText(s+"");}
    }
    public void setTitle(String s){
        mTitleView.setText(s+"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.negativeButton:
                if (mClickListener!=null){
                    mClickListener.negativeClick();
                }
                dismiss();
                break;
            case R.id.positiveButton:
                if (mClickListener!=null){
                    mClickListener.positiveClick();
                }
                dismiss();
                break;
        }
    }

    private ClickListener mClickListener;

    public void setClickListener(ClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    interface ClickListener{
         void positiveClick();
        void negativeClick();
        void notUpdateChecked(boolean check);
    }
}
