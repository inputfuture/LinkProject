package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;

public class LeGestureView extends View implements OnGestureListener, GestureDetector.OnDoubleTapListener {

    public final static int ORIENTATION_LEFT = 10;
    public final static int ORIENTATION_RIGHT = 11;
    public final static int ORIENTATION_UP = 12;
    public final static int ORIENTATION_DOWN = 13;

    private GestureDetector detector;
    // 限制最小移动像素
    private int FLING_MIN_DISTANCE = 110;
    LeGestureListener mGestureListener;


    public LeGestureView(Context context, AttributeSet attrs) {

        super(context, attrs);
        setFocusable(true);
        detector = new GestureDetector(context, this);
    }

    public void setOnGestureClickListener(LeGestureListener mLeGestureListener) {
        this.mGestureListener = mLeGestureListener;

    }

    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒

        if (mGestureListener == null) {
            return false;
        }
        Float move_x, move_y;
        move_x = Math.abs(e1.getX() - e2.getX());
        move_y = Math.abs(e1.getY() - e2.getY());

        if (move_x >= move_y) {
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE) {
                // 向左滑动
                mGestureListener.onFling(ORIENTATION_LEFT, e1, e2, velocityX,
                        velocityY);
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE) {
                // 向右滑动
                mGestureListener.onFling(ORIENTATION_RIGHT, e1, e2, velocityX,
                        velocityY);
            }
        } else {
            if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE) {
                // 向上滑动
                mGestureListener.onFling(ORIENTATION_UP, e1, e2, velocityX,
                        velocityY);
            } else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE) {
                // 向下滑动
                mGestureListener.onFling(ORIENTATION_DOWN, e1, e2, velocityX,
                        velocityY);
            }
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // 单击
        if (mGestureListener != null) {
            mGestureListener.onClick(e);
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (mGestureListener != null) {
            mGestureListener.onLongClick(e);
        }
    }


}
