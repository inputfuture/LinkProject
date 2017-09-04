package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by zhaochao on 2015/11/18.
 */
public class AlbumListView extends ListView {
  //  private final Camera mCamera = new Camera();
    private final Matrix mMatrix = new Matrix();
    private final int MSG_ON_CENTER = 99;
    private MotionEvent downEvent, upEvent;
    private OnCenterListener listener;
    //private Paint mPaint;
    private int currentPosition;
    private int firstPosition;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ON_CENTER:
                    if(listener!=null) {
                        listener.onCenterMoved(currentPosition - firstPosition);
                    }
                    break;
            }
        }
    };

    public interface OnCenterListener {
        void onCenterMoved(int delta);
    }

    public void setCenterListener(OnCenterListener listener) {
        this.listener = listener;
    }

    public AlbumListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setChildrenDrawingOrderEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected int getChildDrawingOrder (int childCount, int i) {
        //sets order number to each child, so makes overlap and center is always on top
        return i;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if(ev.getAction()==MotionEvent.ACTION_DOWN) {
            downEvent = MotionEvent.obtain(ev);
            firstPosition = this.getFirstVisiblePosition();
        }
        if(ev.getAction()==MotionEvent.ACTION_UP) {
            upEvent = MotionEvent.obtain(ev);
            continueScroll();
        }
        return super.onTouchEvent(ev);
    }

    private void continueScroll() {
        if (downEvent == null || upEvent == null) {
            return;
        }
        float deltaY = upEvent.getY() - downEvent.getY();

        if (deltaY > 100) {
            currentPosition = this.getFirstVisiblePosition();
            this.smoothScrollToPositionFromTop(currentPosition, 0, 300);
        } else if (deltaY < -100) {
            int firstPosition = this.getFirstVisiblePosition();
            currentPosition = firstPosition + 1;
            this.smoothScrollToPositionFromTop(currentPosition, 0, 300);
        } else {
            currentPosition = firstPosition;
        }

        downEvent = null;
        upEvent = null;
        handler.sendEmptyMessageDelayed(MSG_ON_CENTER, 300);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        // get top left coordinates
        final int top = child.getTop();
        Bitmap bitmap = child.getDrawingCache();
        if (bitmap == null) {
            child.setDrawingCacheEnabled(true);
            child.buildDrawingCache();
            bitmap = child.getDrawingCache();
        }
        final int centerY = child.getHeight() / 2;
//        final int centerX = child.getWidth() / 2;
        final int centerX = child.getWidth() / 3;
        float xyScale = ((top + centerY) - getHeight() / 2) * 1.0f / getHeight();
        if (xyScale < 0) {
            xyScale = 0 - xyScale;
        }
        xyScale = (1 - xyScale) * 1.3f;

       Trace.Error("==xyScale===",xyScale+"");
       Trace.Error("==xycenterX===",centerX+"");
       Trace.Error("==xycenterY===",centerY+"");
        mMatrix.setScale(xyScale, xyScale, centerX, centerY);

        if(GlobalCfg.IS_POTRAIT) {
            mMatrix.postTranslate(0f, top);
        }else {
           Trace.Error("==600f * xyScale===",600f * xyScale - 700+"");
//            mMatrix.postTranslate(600f * xyScale-700 , top);
            mMatrix.postTranslate(10, top);
        }
        canvas.drawBitmap(bitmap, mMatrix, null);
        return false;
    }
}
