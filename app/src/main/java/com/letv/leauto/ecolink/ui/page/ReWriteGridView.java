package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by qu on 16/7/26.
 */
public class ReWriteGridView extends GridView {

    public ReWriteGridView(Context context) {
        super(context);
    }

    public ReWriteGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
