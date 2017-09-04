package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.utils.DensityUtils;


/**
 * Created by fuqinqin on 2016/10/17.
 */
public class BackView extends RelativeLayout {

    private ImageView backIcon;

    public BackView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public BackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_back_view, this);
        backIcon=(ImageView) findViewById(R.id.back_icon);
        if(!GlobalCfg.IS_POTRAIT){
            LayoutParams params = (LayoutParams) backIcon.getLayoutParams();
            int px = DensityUtils.dp2px(context,10);
            params.setMargins(px,0,px,0);
            backIcon.setLayoutParams(params);
        }
        else {

            LayoutParams params = (LayoutParams) backIcon.getLayoutParams();
            int px = DensityUtils.dp2px(context,4);
            params.setMargins(px,0,px,0);
            backIcon.setLayoutParams(params);
        }
    }
}
