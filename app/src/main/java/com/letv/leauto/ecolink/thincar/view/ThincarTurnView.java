package com.letv.leauto.ecolink.thincar.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;

/**
 * Created by Administrator on 2017/6/14.
 */
public class ThincarTurnView extends RelativeLayout {
    private long mLastIconType = -1L;
    private Resources customRes;
    private int[] customIconTypeDrawables;
    private Bitmap nextTurnBitmap;

    RectF mBackRectF;
    Paint mPaint;

    private ImageView mImageView;
    private TextView mTextView;
    private TextView mUnitView;

    public Resources getCustomResources() {
        return this.customRes;
    }

    public int[] getCustomIconTypeDrawables() {
        return this.customIconTypeDrawables;
    }

    public void setCustomIconTypes(Resources resources, int[] customType) {
        this.customRes = resources;
        this.customIconTypeDrawables = customType;
    }

    public ThincarTurnView(Context context, AttributeSet attr, int def) {
        super(context, attr, def);
        initResource(context);
    }

    public ThincarTurnView(Context context, AttributeSet attr) {
        super(context, attr);
        initResource(context);
    }

    public ThincarTurnView(Context context) {
        super(context);
        initResource(context);
    }

    private void initResource(Context context) {
        mPaint = new Paint();
        mBackRectF = new RectF();

        View view = LayoutInflater.from(context).inflate(R.layout.thicar_turn_view,this);
        mImageView = (ImageView) view.findViewById(R.id.image);
        mTextView = (TextView) view.findViewById(R.id.text);
        mUnitView = (TextView) view.findViewById(R.id.unit);
    }

    public void recycleResource() {
        if(this.nextTurnBitmap != null) {
            this.nextTurnBitmap.recycle();
            this.nextTurnBitmap = null;
        }

    }

    public void setIconType(int type) {
        if(type < customIconTypeDrawables.length && this.mLastIconType != (long)type) {
            this.recycleResource();
            if(this.customIconTypeDrawables != null && this.customRes != null) {
                this.nextTurnBitmap = BitmapFactory.decodeResource(this.customRes, this.customIconTypeDrawables[type]);
            } else {
                // this.nextTurnBitmap = BitmapFactory.decodeResource(ej.a(), this.defaultIconTypes[type]);
            }

            mImageView.setImageBitmap(nextTurnBitmap);
            this.mLastIconType = (long)type;
        }
    }

    public void updateText(String text,String unit) {
        mTextView.setText(text);
        mUnitView.setText(unit);
    }
}