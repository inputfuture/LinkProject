package com.letv.leauto.ecolink.thincar;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.amap.api.navi.view.NextTurnTipView;

/**
 * Created by Administrator on 2017/6/20.
 */

public class CustomNextTurnTipView extends NextTurnTipView {
    public CustomNextTurnTipView(Context context, AttributeSet attr, int def) {
        super(context, attr, def);
    }

    public CustomNextTurnTipView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public CustomNextTurnTipView(Context context) {
        super(context);
    }

    /**
     * 重写主要为防止数组越界
     * @param var1
     */
    public void setIconType(int var1) {
        int[] customDrawables = this.getCustomIconTypeDrawables();
        if (customDrawables != null && var1 >= customDrawables.length) {
            return;
        }
        super.setIconType(var1);
    }
}
