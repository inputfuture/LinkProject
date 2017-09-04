package com.letv.leauto.ecolink.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;

/**
 * Created by zhaotongkai on 2016/10/8.
 *
 */
public class NetworkConfirmDialog extends Dialog implements TextView.OnClickListener{

    public TextView mNotice;
    public TextView mConfirm;
    public TextView mCancel;
    private CheckBox mCheckBox;

    public String mMessage;
    public String mOkStr;
    public String mCancleStr;
    private boolean mVisibleCheckBox;


    private OnClickListener mListener;
    private boolean mIsChecked=true;


    public NetworkConfirmDialog(Context context, int notice, int confirm, int cancel, boolean isVisbleCheckBox) {
        super(context);

        if (notice != 0) {
            mMessage = context.getString(notice);
        }
        if (confirm != 0) {
            mOkStr = context.getString(confirm);
        }
        if (cancel != 0) {
            mCancleStr = context.getString(cancel);
        }
        mVisibleCheckBox = isVisbleCheckBox;
    }

    public NetworkConfirmDialog(Context context, String notice, int confirm, int cancel, boolean isVisbleCheckBox) {
        super(context);

        if (!TextUtils.isEmpty(notice)) {
            mMessage = notice;
        }
        if (confirm != 0) {
            mOkStr = context.getString(confirm);
        }
        if (cancel != 0) {
            mCancleStr = context.getString(cancel);
        }
        mVisibleCheckBox = isVisbleCheckBox;
    }

    public NetworkConfirmDialog(Context context, int notice, int confirm) {
        this(context, notice, confirm, 0, false);
    }

    public NetworkConfirmDialog(Context context, int notice, int confirm, int cancel) {
        this(context, notice, confirm, cancel, false);
    }

    public NetworkConfirmDialog(Context context, int notice, int confirm, int cancel, boolean visible, boolean checkde) {
        this(context, notice, confirm, cancel, visible);
        mIsChecked=checkde;
    }

    public NetworkConfirmDialog(Context context, String notice, int confirm, int cancel) {
        this(context, notice, confirm, cancel, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.net_confirm_dialog);
        setupView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    private void setupView() {
        mNotice = (TextView) findViewById(R.id.notice);
        mConfirm = (TextView) findViewById(R.id.confirm);
        mCancel = (TextView) findViewById(R.id.cancel);
        mCheckBox = (CheckBox) findViewById(R.id.never_mind);

        mNotice.setText(mMessage);
        mConfirm.setText(mOkStr);
        if (mCancleStr == null) {
            mCancel.setVisibility(View.GONE);
        } else {
            mCancel.setText(mCancleStr);
        }

        if (mVisibleCheckBox) {
            mCheckBox.setVisibility(View.VISIBLE);
        } else {
            mCheckBox.setVisibility(View.GONE);
        }
        if (mIsChecked){
            mCheckBox.setChecked(true);
        }else{
            mCheckBox.setChecked(false);
        }
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    public void setListener(OnClickListener listener) {
        mListener = listener;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                dismiss();
                if (mListener != null) {
                    mListener.onConfirm(mCheckBox.isChecked());
                }
                break;
            case R.id.cancel:
                dismiss();
                if (mListener != null) {
                    mListener.onCancel();
                }
                break;
        }
    }

    public  interface OnClickListener{
        void onConfirm(boolean checked);
        void onCancel();
    }
}
