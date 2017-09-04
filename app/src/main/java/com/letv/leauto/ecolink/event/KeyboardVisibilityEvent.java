package com.letv.leauto.ecolink.event;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

/**
 * 监听软键盘显示与隐藏事件
 * Created by Jerome on 2017/4/26.
 */

public class KeyboardVisibilityEvent {

    private Activity mActivity;
    private KeyboardVisibilityEventListener mListener;

    private View mActivityRoot;

    private int visibleThreshold;

    public KeyboardVisibilityEvent(Activity activity){
        mActivity = activity;
        visibleThreshold = Math.round(
                convertDpToPx(mActivity, 100));
    }

    public interface KeyboardVisibilityEventListener {
        void onVisibilityChanged(boolean isOpen);
    }


    private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                private final Rect r = new Rect();
                private boolean wasOpened = false;
                @Override
                public void onGlobalLayout() {
                    mActivityRoot.getWindowVisibleDisplayFrame(r);

                    int heightDiff = mActivityRoot.getRootView().getHeight() - r.height();

                    boolean isOpen = heightDiff > visibleThreshold;
                    if (isOpen == wasOpened) {
                        // keyboard state has not changed
                        return;
                    }
                    wasOpened = isOpen;
                    mListener.onVisibilityChanged(isOpen);
                }
            };

    public void register(final KeyboardVisibilityEventListener listener) {
        mListener = listener;

        if (mActivity == null) {
            throw new NullPointerException("activity must not be null");
        }

        int softInputMethod = mActivity.getWindow().getAttributes().softInputMode;
        if (WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE != softInputMethod &&
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED != softInputMethod) {
            throw new IllegalArgumentException("activity window SoftInputMethod is not ADJUST_RESIZE");
        }

        if (listener == null) {
            throw new NullPointerException("listener must not be null");
        }

        mActivityRoot = getActivityRoot(mActivity);
        mActivityRoot.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
    }

    /**
     * 注销
     */
    public void unRegister() {
        if (mActivity != null && mActivityRoot != null) {
            mActivityRoot.getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutListener);
        }
    }

    static View getActivityRoot(Activity activity) {
        return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    float convertDpToPx(Context context, float dp) {
        Resources res = context.getResources();
        return dp * (res.getDisplayMetrics().densityDpi / 160f);
    }
}
