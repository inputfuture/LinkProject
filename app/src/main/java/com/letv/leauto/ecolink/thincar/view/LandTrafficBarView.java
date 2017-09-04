package com.letv.leauto.ecolink.thincar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.amap.api.navi.model.AMapTrafficStatus;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.ThinCarDefine;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.thincar.module.ThincarMapStatus;
import com.letv.leauto.ecolink.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/9.
 */
public class LandTrafficBarView extends ImageView {
    private int left;
    private int right;
    private int progressBarWidth;
    private Paint paint;
    private Bitmap displayingBitmap;
    private Bitmap tmcBarBitmapPortrait;
    private Bitmap tmcBarBitmapLandscape;
    private List<ThincarMapStatus> mTmcSections = new ArrayList<>();
    private int tmcBarTopMargin = 30;
    private Bitmap rawBitmap;
    private int totalDis = 0;
    private RectF colorRectF;
    private int drawTmcBarBgX;
    private int drawTmcBarBgY;
    private int tmcBarBgWidth;
    private int tmcBarBgHeight = 0;
    private int noTrafficColor;
    private int unknownTrafficColor;
    private int smoothTrafficColor;
    private int slowTrafficColor;
    private int jamTrafficColor;
    private int veryJamTrafficColor;
    private Bitmap progressBitMap;

    private int progressTop;

    public LandTrafficBarView(Context context, AttributeSet attr, int value) {
        super(context, attr, value);
        this.initResource(context);
    }

    public LandTrafficBarView(Context context, AttributeSet attr) {
        super(context, attr);
        this.initResource(context);
    }

    public LandTrafficBarView(Context context) {
        super(context);
        this.initResource(context);
    }

    public Bitmap getDisplayingBitmap() {
        return this.displayingBitmap;
    }

    private void initResource(Context context) {
        int mScreenHeight = DensityUtils.getScreenHeight(context);
        double mPhoneCarRate = (double) mScreenHeight / (double) ThinCarDefine.FULL_CAR_HEIGHT;
        this.rawBitmap = Bitmap.createBitmap(DensityUtils.getScreenWidth(context), (int) (20 * mPhoneCarRate), Bitmap.Config.ARGB_8888);
        progressBitMap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.navi_progress);
        this.tmcBarBitmapPortrait = this.rawBitmap;
        this.left = this.tmcBarBitmapPortrait.getHeight() * 20 / 100;
        this.right = this.tmcBarBitmapPortrait.getHeight() * 80 / 100;

        int progressHeight = this.right - this.left;
        progressTop = this.left + (progressHeight - progressBitMap.getHeight()) / 2;
        //this.progressBarWidth = (int)((double)tmcBarBitmapPortrait.getWidth() * 1.0D);
        this.tmcBarBgWidth = tmcBarBitmapPortrait.getWidth();
        this.tmcBarBgHeight = tmcBarBitmapPortrait.getHeight();
        progressBarWidth = tmcBarBitmapPortrait.getWidth() - progressBitMap.getWidth();
        this.paint = new Paint();
        if (Build.VERSION.SDK_INT >= 11) {
            this.tmcBarTopMargin = Math.abs(this.progressBarWidth - tmcBarBitmapPortrait.getWidth() / 4 - (int) ((double) this.progressBarWidth * 0.017D));
        } else {
            this.tmcBarTopMargin = Math.abs(this.progressBarWidth - tmcBarBitmapPortrait.getWidth() / 4 - 3);
        }

        //this.setTmcBarHeightWhenLandscape(0.6666666666666666D);
        this.displayingBitmap = this.tmcBarBitmapPortrait;
        this.colorRectF = new RectF();
        this.noTrafficColor = Color.parseColor("#808080");
        this.unknownTrafficColor = Color.parseColor("#14afe5");
        this.smoothTrafficColor = Color.parseColor("#9cfba7");
        this.slowTrafficColor = Color.parseColor("#e6b940");
        this.jamTrafficColor = Color.parseColor("#ee4e5c");
        this.veryJamTrafficColor = Color.parseColor("#a61225");
    }

    public void onConfigurationChanged(boolean var1) {
        if (var1) {
            this.displayingBitmap = this.tmcBarBitmapLandscape;
        } else {
            this.displayingBitmap = this.tmcBarBitmapPortrait;
        }

        this.setProgressBarSize(var1);
    }

    private void setProgressBarSize(boolean value) {
        this.progressBarWidth = (int) ((double) this.displayingBitmap.getWidth() * 1.0D);
        this.tmcBarBgWidth = this.displayingBitmap.getWidth();
        this.tmcBarBgHeight = this.displayingBitmap.getHeight();
        if (!value) {
            if (Build.VERSION.SDK_INT >= 11) {
                this.tmcBarTopMargin = Math.abs(this.progressBarWidth - this.displayingBitmap.getWidth()) / 4 - (int) ((double) this.progressBarWidth * 0.017D);
            } else {
                this.tmcBarTopMargin = Math.abs(this.progressBarWidth - this.displayingBitmap.getWidth()) / 4 - 4;
            }
        } else {
            this.tmcBarTopMargin = Math.abs(this.progressBarWidth - this.displayingBitmap.getWidth()) / 4 - (int) ((double) this.progressBarWidth * 0.017D);
        }

    }

    public void update(List<AMapTrafficStatus> status, int totalDistance, int remainDistance) {
        mTmcSections.clear();
        int total = 0;
        for (int i = status.size() - 1; i >= 0; --i) {
            AMapTrafficStatus item = (AMapTrafficStatus) status.get(i);
            if (total + item.getLength() <= remainDistance) {
                mTmcSections.add(new ThincarMapStatus(item.getStatus(),item.getLength()));
                total += item.getLength();
            } else {
                int tempLen = remainDistance - total;
                mTmcSections.add(new ThincarMapStatus(item.getStatus(),tempLen));
                break;
            }
        }

        this.totalDis = totalDistance;
        Bitmap bitmap = this.produceFinalBitmap();
        if (bitmap != null) {
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

    public void setSlowTrafficColor(int color) {
        this.slowTrafficColor = color;
    }

    public int getJamTrafficColor() {
        return this.jamTrafficColor;
    }

    public void setJamTrafficColor(int color) {
        this.jamTrafficColor = color;
    }

    public int getVeryJamTrafficColor() {
        return this.veryJamTrafficColor;
    }

    public void setVeryJamTrafficColor(int color) {
        this.veryJamTrafficColor = color;
    }

    Bitmap produceFinalBitmap() {
        if (this.mTmcSections == null) {
            return null;
        } else {
            Bitmap bitmap = Bitmap.createBitmap(this.displayingBitmap.getWidth(), this.displayingBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            this.paint.setStyle(Paint.Style.FILL);
            float drawLen = 0.0F;

            for (int i = 0; i <= this.mTmcSections.size() - 1; ++ i) {
                ThincarMapStatus status = (ThincarMapStatus) this.mTmcSections.get(i);
                switch (status.getStatus()) {
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

                if (drawLen + (float) status.getLength() < (float) totalDis) {
                    this.colorRectF.set((float) progressBarWidth - (float) this.progressBarWidth * (drawLen + status.getLength()) / (float) this.totalDis + progressBitMap.getWidth(), (float) this.left, (float) progressBarWidth - (float) this.progressBarWidth * drawLen / (float) this.totalDis + progressBitMap.getWidth(), (float) this.right);
                } else {
                    this.colorRectF.set((float) progressBarWidth - (float) this.progressBarWidth + progressBitMap.getWidth(), (float) this.left, (float) progressBarWidth - (float) this.progressBarWidth * drawLen / (float) this.totalDis + progressBitMap.getWidth(), (float) this.right);
                }

                canvas.drawRect(this.colorRectF, this.paint);
                drawLen += (float) status.getLength();
            }

            this.paint.setColor(this.noTrafficColor);
            this.colorRectF.set((float) progressBarWidth - (float) this.progressBarWidth, (float) this.left, ((float) totalDis - drawLen) * progressBarWidth / totalDis, (float) this.right);
            canvas.drawRect(this.colorRectF, this.paint);
            canvas.drawBitmap(progressBitMap, tmcBarBgWidth - drawLen * progressBarWidth / totalDis - progressBitMap.getWidth(), (float) this.progressTop, null);
            canvas.drawBitmap(this.displayingBitmap, 0.0F, 0.0F, (Paint) null);
            return bitmap;
        }
    }

    public void setTmcBarPosition(int left, int top, int right, int bottom, boolean value) {
        this.setTmcBarHeightWhenLandscape(0.6666666666666666D * (double) top / (double) right);
        this.setTmcBarHeightWhenPortrait(1.0D * (double) top / (double) right);
        bottom = bottom * top / right;
        this.onConfigurationChanged(value);
        if (value) {
            this.drawTmcBarBgX = Math.abs(left - (int) (1.3D * (double) this.tmcBarBgWidth));
            this.drawTmcBarBgY = (top - this.tmcBarBgHeight / 2) * 6 / 10;
        } else {
            this.drawTmcBarBgX = Math.abs(left - (int) (1.3D * (double) this.tmcBarBgWidth));
            this.drawTmcBarBgY = (int) ((double) top - 1.5D * (double) bottom - (double) this.tmcBarBgHeight);
        }

    }

    public void setTmcBarHeightWhenLandscape(double height) {
        if (height > 1.0D) {
            height = 1.0D;
        } else if (height < 0.1D) {
            height = 0.1D;
        }

        this.tmcBarBitmapLandscape = Bitmap.createScaledBitmap(this.rawBitmap, 781, 81, true);
    }

    public void setTmcBarHeightWhenPortrait(double height) {
        if (height > 1.0D) {
            height = 1.0D;
        } else if (height < 0.1D) {
            height = 0.1D;
        }

        this.tmcBarBitmapPortrait = Bitmap.createScaledBitmap(this.rawBitmap, this.rawBitmap.getWidth(), (int) ((double) this.rawBitmap.getHeight() * height), true);
        this.displayingBitmap = this.tmcBarBitmapPortrait;
        this.setProgressBarSize(false);
    }

    public int getTmcBarBgPosX() {
        return this.drawTmcBarBgX;
    }

    public int getTmcBarBgPosY() {
        return this.drawTmcBarBgY;
    }

    public int getTmcBarBgWidth() {
        return this.tmcBarBgWidth;
    }

    public int getTmcBarBgHeight() {
        return this.tmcBarBgHeight;
    }

    public void recycleResource() {
        if (this.displayingBitmap != null) {
            this.displayingBitmap.recycle();
            this.displayingBitmap = null;
        }

        if (this.tmcBarBitmapPortrait != null) {
            this.tmcBarBitmapPortrait.recycle();
            this.tmcBarBitmapPortrait = null;
        }

        if (this.tmcBarBitmapLandscape != null) {
            this.tmcBarBitmapLandscape.recycle();
            this.tmcBarBitmapLandscape = null;
        }

    }
}