package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.amap.api.navi.view.ZoomButtonView;
import com.letv.leauto.ecolink.R;

/**
 * Created by fuqinqin on 2016/10/11.
 */
public class EcoZoomButtonView extends ZoomButtonView {

    private ImageButton zoomOutBtn;
    private ImageButton zoomInBtn;

    public EcoZoomButtonView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        init();

    }

    public EcoZoomButtonView(Context var1, AttributeSet var2) {
        super(var1, var2);
        init();
    }

    public ImageButton getZoomOutBtn() {
        return zoomOutBtn;
    }

    public ImageButton getZoomInBtn() {
        return zoomInBtn;
    }

    private void init(){
        super.removeAllViews();
        LayoutInflater inflater=(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View var1 = inflater.inflate(R.layout.navi_zoombar, this);
        zoomOutBtn = (ImageButton)var1.findViewById(R.id.zoom_reduce);
        zoomInBtn = (ImageButton)var1.findViewById(R.id.zoom_add);
    }
}
