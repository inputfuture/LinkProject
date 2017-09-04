package com.letv.leauto.ecolink.update; /**
 * Created by liushengli on 2016/4/11.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CustomProgressDialog extends Dialog implements View.OnClickListener{
    @Bind(R.id.progress_horizontal)
    ProgressBar mProgressBar;
    @Bind(R.id.negativeButton)
    TextView mNegativeTextView;
    @Bind(R.id.positiveButton)
    TextView mPositiveView;
    @Bind(R.id.title)
    TextView mTitleView;
    @Bind(R.id.message)
    TextView mMessageView;
    @Bind(R.id.textPercentage)
    TextView mPercentView;
    private Context mContext;


    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext=context;
    }

    public CustomProgressDialog(Context context) {
        super(context);
    }
    public void setTitle(String title){
        mTitleView.setText(title);
    }
    public void setMessageView(String message){
        mMessageView.setText(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog_custom);
        ButterKnife.bind(this);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) mContext.getResources().getDimension(
                R.dimen.dialog_layout_width);
        params.height = (int) mContext.getResources().getDimension(
                R.dimen.dialog_layout_height);
        getWindow().setAttributes(params);

        mNegativeTextView.setOnClickListener(this);
        mPositiveView.setOnClickListener(this);
    }

    public void setProgress(final int cur){
        mProgressBar.setProgress(cur);
        mPercentView.post(new Runnable() {
            @Override
            public void run() {
                mPercentView.setText(cur + "%");
            }
        });
    }
    public void setMax(int max) {
        mProgressBar.setMax(max);
    }

    public void setIndeterminate(boolean indeterminate) {

        mProgressBar.setIndeterminate(indeterminate);

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
    }

}