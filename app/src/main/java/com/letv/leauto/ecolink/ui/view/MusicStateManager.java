package com.letv.leauto.ecolink.ui.view;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.leplayer.model.PlayItem;
import com.letv.leauto.ecolink.ui.HomeActivity;


/**
 * Created by fuqinqin on 2017/1/3.
 */
public class MusicStateManager implements View.OnClickListener, LePlayer.IconStateListener {
    private ImageView mSateIamgeView;
    private AnimationDrawable mStatAnimation;
    private HomeActivity mContext;
    private PlayItem mLastItem;
    private static MusicStateManager instance;
    protected LePlayer lePlayer;
    private static final int HAVE_NOT_PLAY = -1;
    private static final int IS_PLAYING = 0;
    private static final int IS_PAUSED = 1;

    private static final int START = 100;
    private static final int PAUSE = 101;

    private int mIsPlaying = HAVE_NOT_PLAY;
    private boolean mEnable = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLastItem = MediaOperation.getInstance().getHistoryLastItem();
            switch (msg.what) {
                case START:
                    setEnable(true);
                    setDynamicIcon();
                    break;
                case PAUSE:
                    if (mLastItem == null) {
                        setEnable(false);
                    } else {
                        setEnable(true);
                        setStaticIcon();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private MusicStateManager(){
    }

    public static MusicStateManager getInstance(){
        if(instance == null){
            instance = new MusicStateManager();
        }
        return instance;
    }

    public void init(Activity context, ImageView imageView){
        mSateIamgeView = imageView;
        enableAnimation(false);
        mContext = (HomeActivity)context;
        if(mIsPlaying == HAVE_NOT_PLAY) {
            mLastItem = MediaOperation.getInstance().getHistoryLastItem();
            if (mLastItem == null) {
                setEnable(false);
            } else {
                setEnable(true);
            }
            lePlayer = EcoApplication.getInstance().LeGlob.getPlayer();
            lePlayer.openServiceIfNeed();
            lePlayer.setIconStateListener(this);

        }
        else{
            if(mIsPlaying == IS_PLAYING){
                if(mSateIamgeView != null){
                     //Glide.with(context).load(R.drawable.music_green).asGif().into(mSateIamgeView);
                    mSateIamgeView.setImageResource(R.drawable.playing_anim);
                    mStatAnimation = (AnimationDrawable) mSateIamgeView.getDrawable();
                    mStatAnimation.start();
                }
            }

            else{
                if(mSateIamgeView != null){
                    mSateIamgeView.setImageResource(R.mipmap.music_play_green);
                }
            }
        }
        mSateIamgeView.setOnClickListener(this);
    }

    public PlayItem getLastItem(){
        return mLastItem;
    }

    private void setEnable(boolean enable){
        if(enable) {
            mSateIamgeView.setImageResource(R.mipmap.music_play_green);
            mEnable = true;
        }
        else {
            mSateIamgeView.setImageResource(R.mipmap.music_play_white);
            mEnable = false;
        }
        enableAnimation(false);
    }

    @Override
    public void onClick(View view) {
        if(mEnable) {
            mContext.changeToPlayMusic();
        }
    }

    @Override
    public void musicStart() {
        mHandler.sendEmptyMessage(START);
    }

    @Override
    public void musicStop() {
        mHandler.sendEmptyMessage(PAUSE);
    }

    private void setDynamicIcon(){
        if(mSateIamgeView != null){
            if(!mContext.isDestroyed()) {
                 //Glide.with(mContext).load(R.drawable.music_green).asGif().into(mSateIamgeView);
                mSateIamgeView.setImageResource(R.drawable.playing_anim);
                mStatAnimation = (AnimationDrawable) mSateIamgeView.getDrawable();
                mStatAnimation.start();
            }
        }
        mIsPlaying = IS_PLAYING;
        mEnable = true;
    }

    private void setStaticIcon(){
        if(mSateIamgeView != null){
            mSateIamgeView.setImageResource(R.mipmap.music_play_green);
            enableAnimation(false);
        }
        mIsPlaying = IS_PAUSED;
        mEnable = true;
    }
    public void changeImage(ImageView iv){
        mSateIamgeView = iv;
    }

    private void enableAnimation(boolean enable) {
        if (mStatAnimation == null) {
            return;
        }
        if (enable) {
            mStatAnimation.start();
        } else {
            mStatAnimation.stop();
        }
    }
}
