package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.Trace;

public class DialView extends View {

    Context mContext;
    private static final int MSG_LISTENER = 198;
    private static final int MSG_FLY = 199;
    private static final int FLY_MAX_NUM = 10;

    private Paint paint1, paint2;
    private boolean isUserRadius;
    private boolean isUserBitmap;
    private int Radius_Circle;
    private int Resolution_Size;
    private int Far_Distance;
    private Bitmap[] images;
    private Rect[] imagesRect;
    private double[] angles;
    private double[] centers;
    private double[] textSize;
    private RectF[] rectFs;

    private String[] strs;
    private float fontScale;

    private double angleOffset;
    private double lastDelta;
    private int visibleSize;
    private int Current_Index;
    private int listCount;

    private int flyCount;
    private double flyDelta;
    private double targetDelta;

    private OnIndexChangedListener listener;

    public interface OnIndexChangedListener {
        public void onIndexChanged(int position);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FLY:
                    startFly();
                    break;
                case MSG_LISTENER:
                    if (listener != null) {
                        listener.onIndexChanged(Current_Index);
                    }
                    break;
            }
        }
    };

    public DialView(Context context) {
        super(context);
        mContext = context;
        initDialView();
    }

    public DialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initDialView();
    }

    public DialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initDialView();
    }

    private void initDialView() {
        paint1 = new Paint();
        paint1.setColor(Color.RED);
        paint1.setAntiAlias(true);

        paint2 = new Paint();
        paint2.setColor(Color.WHITE);
        paint2.setTextSize(33f);
        paint2.setStrokeWidth(0.5f);
        paint2.setDither(true);
        paint2.setAntiAlias(true);
        paint2.setTextAlign(Paint.Align.CENTER);

        this.isUserRadius = false;
        this.isUserBitmap = false;
        this.angles = new double[]{-1 / 2f, -1 / 3f, -1 / 6f, 0f, 1 / 6f, 1 / 3f, 1 / 2f};
        this.centers = new double[7];
        this.textSize = new double[7];
        this.rectFs = new RectF[7];
        this.fontScale = 1.0f;
    }

    public void setOnIndexChangedListener(OnIndexChangedListener listener) {
        this.listener = listener;
    }

    public void setShowStyle(double angleOffset, int iconSize, int radius, int visibleSize, int farDistance, float fontScale) {

        try {
            this.fontScale = fontScale;
            this.isUserRadius = true;
            this.angleOffset = angleOffset;
            this.Resolution_Size = iconSize;
            this.Radius_Circle = radius;
            this.visibleSize = visibleSize;
            this.Far_Distance = farDistance;

            this.angles = new double[this.visibleSize];
            double start_Angle = -angleOffset * (this.visibleSize - 1) / 2;
            for (int i = 0; i < this.visibleSize; i++) {
                this.angles[i] = (start_Angle + angleOffset * i) / 180f;
            }
            this.centers = new double[this.visibleSize];
            this.textSize = new double[this.visibleSize];
            this.rectFs = new RectF[this.visibleSize];
        } catch (Exception e) {
            Trace.Error("zhao111", "setShowStyle:" + e.getLocalizedMessage());
        }
    }

    public void setStringList(String[] strs, int currentIndex) {
        if (strs == null || strs.length == 0) {
            this.strs = null;
            this.listCount = 0;
        } else {
            this.strs = new String[strs.length];
            for (int i = 0; i < strs.length; i++) {
                this.strs[i] = strs[i];
            }
            this.listCount = this.strs.length;
        }
        this.Current_Index = currentIndex;
        this.sendListenerMessage(MSG_LISTENER, Current_Index);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            Trace.Info("zhao111", "onDraw: " + this.getWidth());
            if (!this.isUserBitmap) {
                for (int i = 0; i < this.angles.length; i++) {
                    //canvas.drawArc(rectFs[i], 0, 360, true, paint1);
                    int ii = (this.Current_Index - this.angles.length / 2 + i + this.strs.length) % this.strs.length;
                    paint2.setTextSize((float) this.textSize[i]);
                    if (this.listCount == 1) {
                        if (i == this.visibleSize / 2) {
                            canvas.drawText(this.strs[0], (float) centers[i], (float) (rectFs[i].top + rectFs[i].bottom + this.textSize[i] / 2 + 7 * this.fontScale) / 2, paint2);
                        }
                    } else {
                        canvas.drawText(this.strs[ii], (float) centers[i], (float) (rectFs[i].top + rectFs[i].bottom + this.textSize[i] / 2 + 7 * this.fontScale) / 2, paint2);
                    }
                }
            } else {
                for (int i = 0; i < this.angles.length; i++) {
                    int ii = (this.Current_Index - this.angles.length / 2 + i + this.images.length) % this.images.length;
                    if (rectFs[i].right > 0 && rectFs[i].left < this.getWidth()) {
                        canvas.drawBitmap(this.images[ii], this.imagesRect[ii], rectFs[i], paint1);
                    }
                }
            }
        } catch (Exception e) {
            Trace.Error("zhao111", e.toString());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!this.isUserRadius) {
            this.Radius_Circle = (right - left) * 2 / 5;
            this.Resolution_Size = (right - left) / 14;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Trace.Info("zhao111", "----onSizeChanged:" + w + ", " + oldw);
        measureLocations(0f);
    }

    private void sendListenerMessage(int what, int current_Index) {
        Message msg = Message.obtain();
        msg.what = MSG_LISTENER;
        msg.arg1 = current_Index;
        this.handler.sendMessage(msg);
    }

    private void measureLocations(double angleDelta) {
        Trace.Info("zhao111", "measureLocations: " + this.getWidth());
        for (int i = 0; i < this.angles.length; i++) {
            this.centers[i] = this.getWidth() / 2f + Radius_Circle * Math.sin((this.angles[i] + angleDelta) * Math.PI) * this.fontScale;
            double distance = Radius_Circle - Radius_Circle * Math.cos((this.angles[i] + angleDelta) * Math.PI);
            //double radius = Resolution_Size * (1 - 0.375 * distance / Radius_Circle);
            double radius = (Far_Distance - distance) * Resolution_Size / Far_Distance;
            double fm = this.getWidth() / 2f;

            this.textSize[i] = (1 - Math.abs(this.centers[i] - fm) / fm / 2) * 33 * this.fontScale;
            if (radius <= 0) {
                radius = 0;
            }
            rectFs[i] = new RectF();
            rectFs[i].left = (float) (centers[i] - radius);
            rectFs[i].top = (float) (this.getHeight() / 2f - radius);
            rectFs[i].right = (float) (centers[i] + radius);
            rectFs[i].bottom = (float) (this.getHeight() / 2f + radius);
        }
        this.invalidate();
    }

    public void setOffset(double delta) {
        this.lastDelta = delta;
        double angleDelta = delta * this.angleOffset / 180f;
        this.measureLocations(angleDelta);
    }

    public void setTarget(int step) {
        if (this.listCount <= 1) {
            return;
        }
        if (step >= 0) {
            this.targetDelta = -step;
            this.flyDelta = (step - this.lastDelta) / FLY_MAX_NUM;
        } else {
            this.targetDelta = -step;
            this.flyDelta = -(-step + this.lastDelta) / FLY_MAX_NUM;
        }
        this.startFly();
    }

    private void startFly() {
        this.flyCount++;
        if (this.flyCount < FLY_MAX_NUM) {
            this.measureLocations((this.lastDelta + this.flyCount * this.flyDelta) * this.angleOffset / 180f);
            this.handler.sendEmptyMessageDelayed(MSG_FLY, 10);
        } else {
            this.Current_Index += this.targetDelta;
            this.Current_Index = (this.Current_Index + this.listCount) % this.listCount;
            //this.measureLocations(this.targetDelta * this.angleOffset / 180f);
            this.measureLocations(0);
            this.flyCount = 0;
            this.sendListenerMessage(MSG_LISTENER, Current_Index);
        }
    }

    public void setAngles(double proportion) {
        this.lastDelta = proportion;
        this.measureLocations(proportion / 8);
        this.invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (strs!=null&&strs.length==1){
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    private MotionEvent downEvent;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downEvent = MotionEvent.obtain(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (downEvent != null) {
                    setAngles((event.getX() - downEvent.getX()) * 1.5f / this.getWidth());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (downEvent != null) {
                    setAngles((event.getX() - downEvent.getX()) * 1.5f / this.getWidth());
                    if (event.getX() - downEvent.getX() > 50) {
                        setTarget(1);
                    } else if (downEvent.getX() - event.getX() > 50) {
                        setTarget(-1);
                    } else {

                        int step = 0;

                        if (GlobalCfg.IS_POTRAIT) {
                            float interval = DeviceUtils.getScreenWidth(mContext) / 5;

                            if (0 < event.getX() && event.getX() < interval) {
                                step = 2;
                            } else if (interval < event.getX() && event.getX() < 2 * interval) {
                                step = 1;
                            } else if (2 * interval < event.getX() && event.getX() < 3 * interval) {
                                step = 0;
                            } else if (3 * interval < event.getX() && event.getX() < 4 * interval) {
                                step = -1;
                            } else if (4 * interval < event.getX() && event.getX() < 5 * interval) {
                                step = -2;
                            }
                        } else {
                            float interval_h = DeviceUtils.getScreenWidth(mContext) / 7;

                            if (0 < event.getX() && event.getX() < interval_h) {
                                step = 3;
                            } else if (interval_h < event.getX() && event.getX() < 2 * interval_h) {
                                step = 2;
                            } else if (2 * interval_h < event.getX() && event.getX() < 3 * interval_h) {
                                step = 1;
                            } else if (3 * interval_h < event.getX() && event.getX() < 4 * interval_h) {
                                step = 0;
                            } else if (4 * interval_h < event.getX() && event.getX() < 5 * interval_h) {
                                step = -1;
                            } else if (5 * interval_h < event.getX() && event.getX() < 6 * interval_h) {
                                step = -2;
                            } else if (6 * interval_h < event.getX() && event.getX() < 7 * interval_h) {
                                step = -3;
                            }
                        }
                        if (step != 0) {
                            setTarget(step);
                        }

                  /*  if (event.getX() < DeviceUtils.getScreenWidth(mContext) / 2) {
                        setTarget(1);
                    } else {
                        setTarget(-1);
                    }*/
                    }
                }
                downEvent = null;
                break;
        }
        return true;
    }

}
