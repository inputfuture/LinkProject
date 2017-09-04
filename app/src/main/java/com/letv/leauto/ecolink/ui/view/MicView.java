package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.utils.DensityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MicView extends View {

    private List<Circle> mCircleList = new ArrayList<Circle>();
    private Bitmap mCenterBitmap;
    private boolean mIsRunning;
    private long mDuration = 2000;
    private Interpolator mInterpolator = new LinearInterpolator();
    private float mInitialRadius;
    private float mMaxRadius;
    private long mLastCreateTime;
    private long mSpeed=800;
    private Paint mPaint;/*外边渐变的 圆环*/
    private Paint mBitmapPaint; /*内部的图标*/
    private Paint mShadePaint;  //*搜索时渐变的圆环*/
    private  Paint mRunBitmapPaint; /*用来绘制小球的画笔*/

    private int mCurrentState=PREPARING;
    public final static  int PREPARING=0; /*准备状态*/
    public final static  int LISTENING=1; /*聆听状态*/
    public final static  int RECOGNISING=2; /*识别状态*/
    public final static  int SEARCHING=3; /*搜索状态*/
    private int mDegree; /*当前旋转角度*/
    private int mPaintWidth; /*外边圆的宽度*/

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEARCHING:
                    mDegree=mDegree+5;
                    if (mDegree>=360){
                        mDegree=0;

                    }
                    invalidate();
                    break;
            }
        }

    };


//    private

    public MicView(Context context) {
        super(context);
        initDialView();
    }

    public MicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initDialView();
    }

    public MicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDialView();
    }

    private void initDialView() {
        mPaintWidth= DensityUtils.dp2px(getContext(),2);
        mBitmapPaint=new Paint();
        mBitmapPaint.setColor(this.getResources().getColor(R.color.mic_back));
        mBitmapPaint.setStyle(Paint.Style.STROKE);
        mBitmapPaint.setStrokeWidth(mPaintWidth);
        mBitmapPaint.setAntiAlias(true);
        mPaint = new Paint();
        mPaint.setColor(this.getResources().getColor(R.color.mic_back));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setAntiAlias(true);

        mShadePaint = new Paint();
        mShadePaint.setColor(this.getResources().getColor(R.color.green));
        mShadePaint.setStyle(Paint.Style.STROKE);
        mShadePaint.setStrokeWidth(mPaintWidth);
        mShadePaint.setAntiAlias(true);

        mRunBitmapPaint = new Paint();
        mRunBitmapPaint.setColor(this.getResources().getColor(R.color.green));
        mRunBitmapPaint.setStyle(Paint.Style.STROKE);
        mRunBitmapPaint.setAntiAlias(true);
        mRunBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        mRunBitmapPaint.setStrokeWidth(mPaintWidth*3);  //设置外边旋转小球的直径为外边圆宽度的3倍

        mCenterBitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_mic).copy(Bitmap.Config.ARGB_8888, true);
        mInitialRadius= (float) (mCenterBitmap.getHeight());
        mMaxRadius=mCenterBitmap.getHeight()*2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width;
        width=widthSize;
        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        } else
        {
            if (mCenterBitmap!=null){
                int bitmapWidth=mCenterBitmap.getHeight()*2*2;
                int desired =  getPaddingLeft() + bitmapWidth + getPaddingRight();
                width = desired;
            }
        }
        setMeasuredDimension(width, width);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        int centerRadius=mCenterBitmap.getHeight();
        int centerX=getWidth()/2;
        canvas.drawBitmap(mCenterBitmap,centerX-mCenterBitmap.getWidth()/2,centerX-mCenterBitmap.getHeight()/2, mBitmapPaint);
        canvas.drawCircle(centerX,centerX,centerRadius,mBitmapPaint);
        switch (mCurrentState){
            case PREPARING:
                break;
            case LISTENING:
            case RECOGNISING:

                Iterator<Circle> iterator = mCircleList.iterator();
                while (iterator.hasNext()) {
                    Circle circle = iterator.next();
                    if (System.currentTimeMillis() - circle.mCreateTime < mDuration) {
                        mPaint.setAlpha(circle.getAlpha());
                        canvas.drawCircle(getWidth() / 2, getHeight() / 2, circle.getCurrentRadius(), mPaint);
                    } else {
                        iterator.remove();
                    }
                }
                if (mCircleList.size() > 0) {
                    postInvalidateDelayed(10);
                }
                break;
            case SEARCHING:
                SweepGradient shader=new SweepGradient(centerX,centerX,getResources().getColor(R.color.transparent),getResources().getColor(R.color.green));
                Matrix matrix=new Matrix();
                matrix.setRotate(mDegree,centerX,centerX);
                shader.setLocalMatrix(matrix);
                mShadePaint.setShader(shader);
                RectF rectF=new RectF(centerX-centerRadius,centerX-centerRadius,centerX+centerRadius,centerX+centerRadius);
                canvas.drawCircle(centerX,centerX,centerRadius,mShadePaint);
                canvas.drawArc(rectF,mDegree, (float) (mPaintWidth*3*Math.PI/centerRadius),false,mRunBitmapPaint);
                handler.sendEmptyMessageDelayed(SEARCHING,800);
                break;
        }
    }


    public void setCurrentState(int state){
        mCurrentState=state;
        switch (mCurrentState){
            case LISTENING:
            case RECOGNISING:
                start();
                break;
            case PREPARING:
                mIsRunning=false;
               break;
            case SEARCHING:
                mIsRunning=false;
                break;
        }
    }

    public void start() {
        if (mCurrentState==LISTENING||mCurrentState==RECOGNISING){
            if (!mIsRunning) {
                mIsRunning = true;
                mCreateCircle.run();
            }
        }
    }



    private Runnable mCreateCircle = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                newCircle();
                postDelayed(mCreateCircle, mSpeed); // 每隔mSpeed毫秒创建一个圆
            }
        }
    };

    private void newCircle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastCreateTime < mSpeed) {
            return;
        }
        Circle circle = new Circle();
        mCircleList.add(circle);
        invalidate();
        mLastCreateTime = currentTime;
    }


    private class Circle{
        private long mCreateTime;
        public Circle() {
            mCreateTime =System.currentTimeMillis();
        }

        public int getAlpha() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) ((1.0f - mInterpolator.getInterpolation(percent)) * 255);
        }

        public float getCurrentRadius() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius);
        }
    }



}
