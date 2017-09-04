package com.letv.leauto.ecolink.thincar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.TextureMapView;
import com.leauto.link.lightcar.ThinCarDefine;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.utils.Utils;

/**
 * Created by Administrator on 2016/12/7.
 */
public class MapAnimation {

    private RelativeLayout mViewGroup;
    private Context mContext;
    private TextureMapView mMapView;
    private RelativeLayout mOtherIconGroup;
    private Bitmap mGaodeBitmap;
    private double mPhoneCarRate;
    private Handler mHandler = new Handler();


    public interface GetMapShotFinish {
        void onFinished();
    }

    public MapAnimation(RelativeLayout viewGroup, Context context, TextureMapView mapView,RelativeLayout other_icon_layout,double phoneCarRate) {
        mViewGroup = viewGroup;
        mContext = context;
        mMapView = mapView;
        mOtherIconGroup = other_icon_layout;
        mPhoneCarRate = phoneCarRate;
    }

    public void playReally() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMapView.setVisibility(View.VISIBLE);
                mOtherIconGroup.setVisibility(View.VISIBLE);
            }
        },1900);
        mMapView.setVisibility(View.INVISIBLE);
        mOtherIconGroup.setVisibility(View.INVISIBLE);

        Bitmap one = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.welcome_anim_one);
        Bitmap two = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.welcome_anim_two);

        final ImageView gaodeImageView = new ImageView(mContext);
        RelativeLayout.LayoutParams paramsGaode = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.MATCH_PARENT);
        mViewGroup.addView(gaodeImageView, paramsGaode);
        gaodeImageView.setImageBitmap(mGaodeBitmap);
        gaodeImageView.setVisibility(View.INVISIBLE);

        final ImageView twoImageView = new ImageView(mContext);
        RelativeLayout.LayoutParams paramsTwo = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.MATCH_PARENT);
        mViewGroup.addView(twoImageView, paramsTwo);
        twoImageView.setVisibility(View.INVISIBLE);
        twoImageView.setImageBitmap(two);

        final ImageView oneImageView = new ImageView(mContext);
        RelativeLayout.LayoutParams paramsOne = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.MATCH_PARENT);
        mViewGroup.addView(oneImageView, paramsOne);
        oneImageView.setImageBitmap(one);
        playPicOne(oneImageView, twoImageView, gaodeImageView);
    }

    private void playPicOne(final ImageView oneImageView, final ImageView twoImageView, final ImageView gaodeImageView) {
        ObjectAnimator scaleXOne = ObjectAnimator.ofFloat(oneImageView, "scaleX", 1f, 3f);
        ObjectAnimator scaleYOne = ObjectAnimator.ofFloat(oneImageView, "scaleY", 1f, 3f);
        final ObjectAnimator alphaOne = ObjectAnimator.ofFloat(oneImageView, "alpha", 1.0f, 0f);
        alphaOne.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!HomeActivity.isThinCar) {
                    alphaOne.cancel();
                    return;
                }
                float value = (float) animation.getAnimatedValue("alpha");
                if (0.3f <= value && value <= 0.4f) {
                    playPicTwo(twoImageView, gaodeImageView);
                }
            }
        });

        alphaOne.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewGroup.removeView(oneImageView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet animSetOne = new AnimatorSet();
        animSetOne.play(scaleXOne).with(scaleYOne).with(alphaOne);
        animSetOne.setDuration(300).start();
    }

    private void playPicTwo(final ImageView view, final ImageView gaodeImageView) {
        ObjectAnimator scaleXTwo = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f, 0.6f, 2f);
        final ObjectAnimator scaleYTwo = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f, 0.6f, 2f);
        ObjectAnimator alphaTwo = ObjectAnimator.ofFloat(view, "alpha", 0.3f, 1f, 0.6f, 0f);

        alphaTwo.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewGroup.removeView(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scaleYTwo.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!HomeActivity.isThinCar) {
                    scaleYTwo.cancel();
                    return;
                }
                float value = (float) animation.getAnimatedValue("scaleY");
                if (1.5f <= value && value <= 1.6f) {
                    playPicThree(gaodeImageView);
                }
            }
        });

        AnimatorSet animSetTwo = new AnimatorSet();
        animSetTwo.play(scaleXTwo).with(scaleYTwo).with(alphaTwo);
        animSetTwo.setDuration(1000).start();
        view.setVisibility(View.VISIBLE);
    }

    private void playPicThree(final ImageView gaodeImageView) {
        ObjectAnimator scaleXThree = ObjectAnimator.ofFloat(gaodeImageView, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleYThree = ObjectAnimator.ofFloat(gaodeImageView, "scaleY", 0.5f, 1f);
        final ObjectAnimator alphaThree = ObjectAnimator.ofFloat(gaodeImageView, "alpha", 0f, 1f);

        AnimatorSet animSetTwo = new AnimatorSet();
        animSetTwo.play(scaleXThree).with(scaleYThree).with(alphaThree);
        animSetTwo.setDuration(500).start();
        gaodeImageView.setVisibility(View.VISIBLE);

        alphaThree.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewGroup.removeView(gaodeImageView);
                mMapView.setVisibility(View.VISIBLE);
                mOtherIconGroup.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        alphaThree.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!HomeActivity.isThinCar) {
                    alphaThree.cancel();
                }
            }
        });
    }

    public void getMapScreenShot(final GetMapShotFinish listener) {
        AMap aMap = mMapView.getMap();
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {
            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {
//                mGaodeBitmap = Utils.resizeImage(bitmap,mMapView.getWidth(), (int)((double) ThinCarDefine.HALF_CAR_HEIGHT * mPhoneCarRate));
//                mMapView.setVisibility(View.INVISIBLE);
                listener.onFinished();
            }
        });
    }
}