package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LeTouchView extends View {

    public static final int FLAG_DIRECT_LEFT = 1;
    public static final int FLAG_DIRECT_UP = 2;
    public static final int FLAG_DIRECT_RIGHT = 3;
    public static final int FLAG_DIRECT_DOWN = 4;

    private MotionEvent downEvent;
    private View viewH, viewV;
    private int flagScroll;
    private int flagDistance = 100;

    private boolean isMoved;


    public LeTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
    }

    public void setTouchTarget(View viewHorizontal, View viewVertical) {
        this.viewH = viewHorizontal;
        this.viewV = viewVertical;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        processMotionEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.flagDistance = (right - left) / 10;
    }

    private void processMotionEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downEvent = MotionEvent.obtain(event);
                break;
            case MotionEvent.ACTION_MOVE:

                if (downEvent != null) {
                    if (flagScroll == 0) {
                        float deltaX = event.getX() - downEvent.getX();
                        float deltaY = event.getY() - downEvent.getY();
                        if (Math.abs(deltaX) > flagDistance && this.viewH != null) {
                            flagScroll = deltaX > 0 ? FLAG_DIRECT_RIGHT : FLAG_DIRECT_LEFT;
                            this.viewH.dispatchTouchEvent(downEvent);
                        } else if (Math.abs(deltaY) > flagDistance && this.viewV != null) {
                            flagScroll = deltaY < 0 ? FLAG_DIRECT_UP : FLAG_DIRECT_DOWN;
                            this.viewV.dispatchTouchEvent(downEvent);
                        }
                    } else {
                        isMoved = true;
                        if ((flagScroll == 1 || flagScroll == 3) && this.viewH != null) {
                            this.viewH.dispatchTouchEvent(event);
                        } else if ((flagScroll == 2 || flagScroll == 4) && this.viewV != null) {
                            this.viewV.dispatchTouchEvent(event);
                        }
                    }
                } else {
                    flagScroll = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (downEvent != null) {
                    if (isMoved) {
                        if ((flagScroll == 1 || flagScroll == 3) && this.viewH != null && event != null) {
                            this.viewH.dispatchTouchEvent(event);
                        } else if ((flagScroll == 2 || flagScroll == 4) && this.viewV != null) {
                            this.viewV.dispatchTouchEvent(event);
                        }
                        downEvent = null;
                        flagScroll = 0;
                    } else {
                        if( this.viewV != null) {
                            this.viewV.dispatchTouchEvent(downEvent);
                            this.viewV.dispatchTouchEvent(event);
                        }
                    }
                }
                isMoved = false;
                break;
        }
    }
}
