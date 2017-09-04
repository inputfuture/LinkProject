package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by wang.huayan on 2016/6/20.
 */
public class SlideTabStrip extends RelativeLayout {
    private final Paint mDividerPaint;
    private LinearLayout titleLayout;
    private TextView anchorView;
    private static final int DEFAULT_DIVIDER_THICKNESS_DIPS = 1;
    private static final int DIVER_MARGIN=15;
    private int mDiverColor= Color.GREEN;
    private boolean mShowDiver=false;
    private int mDivertopMargin=DIVER_MARGIN;
    private int mDiverBottomMargin=DIVER_MARGIN;



    public void setTitleLayout(LinearLayout titleLayout) {
        this.titleLayout = titleLayout;
    }

    public void setAnchorView(TextView anchorView) {
        this.anchorView = anchorView;
    }

    public void setDiverColor(int mDiverColor) {
        mShowDiver = true;
        this.mDiverColor = mDiverColor;
    }

    public enum GravityType {
        CENTER,
        BOTTOM
    }

    public SlideTabStrip(Context context) {
        this(context, null);
    }




    public SlideTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        final float density = getResources().getDisplayMetrics().density;
        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth((int) (DEFAULT_DIVIDER_THICKNESS_DIPS * density));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowDiver) {
            if (titleLayout != null) {
                for (int i = 0; i < titleLayout.getChildCount() - 1; i++) {
                    View child = titleLayout.getChildAt(i);
                    mDividerPaint.setColor(mDiverColor);
                    if (child != null) {
                        canvas.drawLine(child.getRight(), mDivertopMargin, child.getRight(),
                                getHeight() - mDiverBottomMargin, mDividerPaint);
                    }
                }
            }
        }
    }
}
