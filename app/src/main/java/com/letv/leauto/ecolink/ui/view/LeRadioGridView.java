package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by why on 2016/8/8.
 */
public class LeRadioGridView extends GridView {
    private float xPosition;
    private float yPosition;


    public LeRadioGridView(Context context) {
        super(context);
    }

    public LeRadioGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeRadioGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                xPosition=ev.getX();
//                yPosition=ev.getY();
//                Trace.Debug("#### xposition="+ev.getX());
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (Math.abs(ev.getX()-xPosition)>Math.abs(ev.getY()-yPosition)){
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                    return true;
//                }
//                break;
//        }
//        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(ev);
    }
}
