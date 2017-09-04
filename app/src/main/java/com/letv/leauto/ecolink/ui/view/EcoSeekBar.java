package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.letv.leauto.ecolink.cfg.GlobalCfg;

/**
 * Created by why on 2016/7/12.
 */
public class EcoSeekBar extends SeekBar {
    public EcoSeekBar(Context context) {
        super(context,null);
    }

    public EcoSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EcoSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private boolean mCanSeek=true;

    public void setCanSeek(boolean canSeek) {
        this.mCanSeek = canSeek;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (GlobalCfg.IS_DONGFEN||!mCanSeek){
            return false;
        }else{
        return super.dispatchTouchEvent(event);}
    }
}
