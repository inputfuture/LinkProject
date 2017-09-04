package com.letv.leauto.ecolink.ui.view;

/**
 * Created by fuqinqin on 2016/10/13.
 */
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PressedImageView extends ImageView {

    public PressedImageView(Context context) {
        super(context);
    }

    public PressedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

      /*  if(getDrawable() == null)
            return;

        if(pressed) {
            setImageAlpha(0x80);
            invalidate();
        }
        else {
            setImageAlpha(0xFF);
            invalidate();
        }*/
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

    }

    private void setGrey(){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        setColorFilter(filter);
    }

    private void recover(){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(1);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        setColorFilter(filter);
    }
}
