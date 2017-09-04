package com.letv.leauto.ecolink.ui.vehiclebean;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class VehicleSideBar extends View {
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	public static ArrayList<String> index=new ArrayList<String>();
	private int choose = -1;
	private Paint paint = new Paint();
	private TextView mTextDialog;
	int singleHeight;
	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}
	public VehicleSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public VehicleSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VehicleSideBar(Context context) {
		super(context);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight();
		int width = getWidth();
		if(index.size()!=0){
			singleHeight = height / index.size();
		}else{
			singleHeight = 0;
		}


		for (int i = 0; i < index.size(); i++) {
			paint.setColor(Color.parseColor("#60ffffff"));
			// paint.setColor(Color.WHITE);
			//paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(40);
			if (i == choose) {
				paint.setColor(Color.parseColor("#10ffffff"));
				paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(index.get(i)) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(index.get(i), xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * index.size());

		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;//
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			if (oldChoose != c) {
				if (c >= 0 && c <index.size()) {
					if (listener != null) {
						listener.onTouchingLetterChanged(index.get(c));
					}
					if (mTextDialog != null) {
						mTextDialog.setText(index.get(c));
						mTextDialog.setVisibility(View.VISIBLE);
					}
					
					choose = c;
					invalidate();
				}
			}

			break;
		}
		return true;
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}