//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.amap.api.navi.model.AMapTrafficStatus;
import com.letv.leauto.ecolink.R;

import java.util.List;

public class MyTrafficBar extends ImageView {
    private int left;
    private int right;
    private int progressBarHeight;
    private Paint paint;
    private Bitmap displayingBitmap;
    private Bitmap tmcBarBitmapPortrait;
    private Bitmap tmcBarBitmapLandscape;
    private List<AMapTrafficStatus> mTmcSections;
    private int tmcBarTopMargin = 0;
    private Bitmap rawBitmap;
    private int totalDis = 0;
    private RectF colorRectF;
    private int tmcBarBgHeight = 0;
    private int unknownTrafficColor;
    private int smoothTrafficColor;
    private int slowTrafficColor;
    private int jamTrafficColor;
    private int veryJamTrafficColor;

    public MyTrafficBar(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.initResource();
    }

    public MyTrafficBar(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.initResource();
    }

    public MyTrafficBar(Context var1) {
        super(var1);
        this.initResource();
    }

    public Bitmap getDisplayingBitmap() {
        return this.displayingBitmap;
    }

    private void initResource() {
        this.rawBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.navigation_tmcbar_cursor);
        this.displayingBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.navigation_light_bg);
        this.progressBarHeight=rawBitmap.getHeight()*9;
        this.tmcBarTopMargin= (int) (rawBitmap.getHeight()*0.5f);
        this.paint = new Paint();
        paint.setAntiAlias(true);
        this.colorRectF = new RectF();
        this.unknownTrafficColor = Color.parseColor("#B3CCDD");
        this.smoothTrafficColor = Color.parseColor("#05C300");
        this.slowTrafficColor = Color.parseColor("#FFD615");
        this.jamTrafficColor = Color.argb(255, 255, 93, 91);
        this.veryJamTrafficColor = Color.argb(255, 179, 17, 15);
    }


    int mRemainDis;


    public void update(List<AMapTrafficStatus> var1, int totalDis, int remainDis) {
        this.mTmcSections = var1;
        this.totalDis = totalDis;
        mRemainDis=remainDis;
        Bitmap bitmap = this.produceFinalBitmap();
        if(bitmap != null) {
            this.setImageBitmap(bitmap);
        }

    }

    public int getUnknownTrafficColor() {
        return this.unknownTrafficColor;
    }

    public void setUnknownTrafficColor(int var1) {
        this.unknownTrafficColor = var1;
    }

    public int getSmoothTrafficColor() {
        return this.smoothTrafficColor;
    }

    public void setSmoothTrafficColor(int var1) {
        this.smoothTrafficColor = var1;
    }

    public int getSlowTrafficColor() {
        return this.slowTrafficColor;
    }

    public void setSlowTrafficColor(int var1) {
        this.slowTrafficColor = var1;
    }

    public int getJamTrafficColor() {
        return this.jamTrafficColor;
    }

    public void setJamTrafficColor(int var1) {
        this.jamTrafficColor = var1;
    }

    public int getVeryJamTrafficColor() {
        return this.veryJamTrafficColor;
    }

    public void setVeryJamTrafficColor(int var1) {
        this.veryJamTrafficColor = var1;
    }

    Bitmap produceFinalBitmap() {
        if(this.mTmcSections == null) {
            return null;
        } else {

            Bitmap bitmap = Bitmap.createBitmap(rawBitmap.getWidth(),rawBitmap.getHeight()*11,Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            this.paint.setStyle(Style.FILL);
            left= (int) (rawBitmap.getWidth()*0.2f);
            right= (int) (rawBitmap.getWidth()*0.8f);
            float remainDis = (float)this.mRemainDis;
            paint.setColor(getResources().getColor(R.color.transparent_black_60));
            paint.setStrokeCap(Paint.Cap.ROUND);

            this.colorRectF.set(this.left,
                    this.progressBarHeight *  remainDis / (float)this.totalDis + this.tmcBarTopMargin,this.right,
                    this.progressBarHeight  + this.tmcBarTopMargin);
            canvas.drawRect(this.colorRectF, this.paint);
            float overDistance=totalDis-mRemainDis;
            float overStatus=0;
            int index = 0;

            for(int i = 0; i < this.mTmcSections.size(); ++i) {
                AMapTrafficStatus trafficStatus = mTmcSections.get(i);
                switch(trafficStatus.getStatus()) {
                    case 0:
                        this.paint.setColor(this.unknownTrafficColor);
                        break;
                    case 1:
                        this.paint.setColor(this.smoothTrafficColor);
                        break;
                    case 2:
                        this.paint.setColor(this.slowTrafficColor);
                        break;
                    case 3:
                        this.paint.setColor(this.jamTrafficColor);
                        break;
                    case 4:
                        this.paint.setColor(this.veryJamTrafficColor);
                        break;
                    default:
                        this.paint.setColor(this.unknownTrafficColor);
                }
                overStatus=overStatus+trafficStatus.getLength();

                if (overStatus<=overDistance){
                    /*不再绘制*/
                    index=i;

                }else{
                    if ((overStatus-trafficStatus.getLength())>overDistance){

                        this.colorRectF.set(this.left,
                                this.progressBarHeight * (totalDis - overStatus) / (float)this.totalDis + this.tmcBarTopMargin,
                                this.right, this.progressBarHeight * (totalDis-overStatus+trafficStatus.getLength()) / (float)this.totalDis + this.tmcBarTopMargin);
                        canvas.drawRect(this.colorRectF, this.paint);
                    }else{
                        this.colorRectF.set(this.left,
                                this.progressBarHeight * (totalDis - overStatus) / (float)this.totalDis + this.tmcBarTopMargin,
                                this.right, this.progressBarHeight * (totalDis-overDistance) / (float)this.totalDis + this.tmcBarTopMargin);
                        canvas.drawRect(this.colorRectF, this.paint);
                    }

                }

            }

            Rect rect = new Rect(left, (int) (tmcBarTopMargin-rawBitmap.getWidth()*0.05f), right, (int) (tmcBarTopMargin+progressBarHeight+rawBitmap.getWidth()*0.1f));
            paint.setStrokeWidth(rawBitmap.getWidth()*0.12f);
            paint.setStyle(Style.STROKE);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(new RectF(rect),15,15,paint);
            Rect border = new Rect((int) (left-rawBitmap.getWidth()*0.06f), (int) (tmcBarTopMargin-rawBitmap.getWidth()*0.05f-rawBitmap.getWidth()*0.06f), (int)(right+rawBitmap.getWidth()*0.06f), (int) (tmcBarTopMargin+progressBarHeight+rawBitmap.getWidth()*0.1f+rawBitmap.getWidth()*0.06f));
            paint.setStrokeWidth(1);
            paint.setStyle(Style.STROKE);
            paint.setColor(getResources().getColor(R.color.traffic_border));
            canvas.drawRoundRect(new RectF(border),15,15,paint);
            canvas.drawBitmap(rawBitmap,0,this.progressBarHeight *  mRemainDis / (float)this.totalDis+tmcBarTopMargin-rawBitmap.getWidth()*0.1f,paint);
            return bitmap;
        }
    }



    public void setTmcBarHeightWhenLandscape(double var1) {
        if(var1 > 1.0D) {
            var1 = 1.0D;
        } else if(var1 < 0.1D) {
            var1 = 0.1D;
        }

        this.tmcBarBitmapLandscape = Bitmap.createScaledBitmap(this.rawBitmap, this.rawBitmap.getWidth(), (int)((double)this.rawBitmap.getHeight() * var1), true);
    }




    public int getTmcBarBgHeight() {
        return this.tmcBarBgHeight;
    }

    public void recycleResource() {
        if(this.displayingBitmap != null) {
            this.displayingBitmap.recycle();
            this.displayingBitmap = null;
        }

        if(this.tmcBarBitmapPortrait != null) {
            this.tmcBarBitmapPortrait.recycle();
            this.tmcBarBitmapPortrait = null;
        }

        if(this.tmcBarBitmapLandscape != null) {
            this.tmcBarBitmapLandscape.recycle();
            this.tmcBarBitmapLandscape = null;
        }

    }
}
