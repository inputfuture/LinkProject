package com.letv.leauto.ecolink.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;

/**
 * Created by zhaotongkai on 2016/9/29.
 */
public class StatementDialog extends Dialog implements View.OnClickListener{

    public OnClickListener mClickListener;

    private TextView mAccess;

    public static StatementDialog create(Context ctx, OnClickListener listener) {

        return new StatementDialog(ctx, listener);
    }
    public StatementDialog(Context ctx, OnClickListener listener) {
        super(ctx, R.style.Dialog);
        this.setCanceledOnTouchOutside(false);
        mClickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statement_dialog);
        mAccess = (TextView) findViewById(R.id.btn_access);
        mAccess.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

    public interface OnClickListener {
        public void onAccess();
    }
}
