package com.letv.leauto.ecolink.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;

/**
 * Created by chenchunyu on 16/4/22.
 */
public class EcoDialogTwo extends Dialog implements View.OnClickListener{
    private Context mContext;
    private ICallDialogCallBack listener;
    private RelativeLayout mContentView;
    private View mOK;
    private View mCancel;
    private TextView mMessage;
    public TextView mOKDesc;
    public TextView mCancelDesc;
    private String mRight;
    private String mLeft;
    private String message;
    public EcoDialogTwo(Context context, String message) {
        super(context);
        mContext = context;
        this.message = message;
        init();

    }
    public EcoDialogTwo(Context context, int theme, String message, String left, String right) {
        super(context, theme);
        mContext = context;
        this.message = message;
        mRight=right;
        mLeft=left;
        init();
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = (RelativeLayout) inflater.inflate(
                R.layout.exit_navi_dialog, null);
        setContentView(mContentView);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) mContext.getResources().getDimension(
                R.dimen.dialog_layout_width);
        params.height = (int) mContext.getResources().getDimension(
                R.dimen.dialog_layout_height);
        getWindow().setAttributes(params);

        mOK = mContentView.findViewById(R.id.ok);
        mOKDesc = (TextView) mContentView.findViewById(R.id.mOKDesc);
        mOKDesc.setText(mLeft);
        mCancel = mContentView.findViewById(R.id.cancel);
        mCancelDesc= (TextView) mContentView.findViewById(R.id.mCancelDesc);
        mCancelDesc.setText(mRight);
        mCancel.setOnClickListener(this);
        mMessage = (TextView) mContentView.findViewById(R.id.message);
        mMessage.setText(message);
        mOK.setOnClickListener(this);
        mOK.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mOK.setBackgroundResource(R.drawable.shape_dialog_left_press);
                        mOKDesc.setTextColor(Color.WHITE);
                        break;
                    case MotionEvent.ACTION_UP:
                        mOK.setBackgroundResource(R.drawable.shape_dialog_left_default);
                        mOKDesc.setTextColor(Color.BLACK);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        mCancel.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mCancel.setBackgroundResource(R.drawable.shape_dialog_right_press);
                        mCancelDesc.setTextColor(Color.WHITE);
                        break;
                    case MotionEvent.ACTION_UP:
                        mCancel.setBackgroundResource(R.drawable.shape_dialog_right_default);
                        mCancelDesc.setTextColor(Color.BLACK);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }
    public void setListener(ICallDialogCallBack listener) {
        this.listener = listener;
    }

    public interface ICallDialogCallBack {
        public void onConfirmClick(EcoDialogTwo currentDialog);

        public void onCancelClick(EcoDialogTwo currentDialog);

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok) {
//            dismiss();
			listener.onConfirmClick(this);

        } else if (v.getId() == R.id.cancel) {
//            dismiss();
			listener.onCancelClick(this);

        }
    }
}
