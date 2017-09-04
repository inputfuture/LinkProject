package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.letv.leauto.ecolink.R;

/**
 * Created by why on 2016/9/29.
 */
public class ColorView extends View
{
    private static int COLOR=1;
    private int color1=getResources().getColor(R.color.white);
    private int color2=getResources().getColor(R.color.yellow);
    volatile int color=color1;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (color==color1){
                color=color2;
            }else{
                color=color1;
            }
            setBackgroundColor(color);

        }
    };
    public ColorView(Context context) {
        super(context);
    }

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        handler.sendEmptyMessageDelayed(COLOR,500);
    }
}
