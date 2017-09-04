package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by why on 2016/8/8.
 */
public class LeRadioViewpager extends ViewPager {
    private float xPosition;
    private float distance;
    private float yPosition;

    public LeRadioViewpager(Context context) {
        super(context);
        distance = DensityUtils.dp2px(context, 3f);
    }

    public LeRadioViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
        distance = DensityUtils.dp2px(context, 3f);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 先保存手指按下的x轴的坐标
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            xPosition = ev.getX();
            yPosition = ev.getY();
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            /*
             计算手指移动时的坐标跟按下的坐标之间的绝对值，如果超过给定的值，
             就认为viewpager需要滚动。通过调节distance的大小，可以改变滑动
             灵敏度
              */
            if (Math.abs(ev.getX() - xPosition) > Math.abs(ev.getY() - yPosition)+distance)
                return true;
            else// 意思就是：touch事件已经被PeopleViewPager自己消费了，不会传递到子控件
                return false;
        }
        // 其他情况，依旧保持默认的处理方法
        return super.onInterceptTouchEvent(ev);
    }
}
