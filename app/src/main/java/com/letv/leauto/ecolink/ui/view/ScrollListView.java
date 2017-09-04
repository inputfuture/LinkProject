package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.letv.leauto.ecolink.utils.DensityUtils;

import java.util.List;

/**
 * Created by why on 2016/8/8.
 */
public class ScrollListView extends ListView {
    private float xPosition;
    private float distance;
    private float yPosition;

    public ScrollListView(Context context) {
        super(context);
        distance = DensityUtils.dp2px(context, 3f);
    }

    public ScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        distance = DensityUtils.dp2px(context, 3f);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
