package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ListView;

import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by qu on 16/7/26.
 */
public class ReWriteListView extends ListView {

    public ReWriteListView(Context context) {
        super(context);
    }
    private int  expandSpec;

    public ReWriteListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
//    @Override
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//      int  curExpandSpec = MeasureSpec.makeMeasureSpec(
//                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//        Trace.Debug("##### curExpandSpec"+curExpandSpec);
//        super.onMeasure(widthMeasureSpec, curExpandSpec);
////        if (curExpandSpec!=expandSpec){
////            super.onMeasure(widthMeasureSpec, curExpandSpec);
////            expandSpec=curExpandSpec;
////        }else{
////            super.onMeasure(widthMeasureSpec, curExpandSpec);
////        }
//
//    }

}
