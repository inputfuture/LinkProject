package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.base.BasePage;

import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 2017/3/7.
 */
public class IntroducePage extends BasePage {
    private boolean mIsInit;
    private int mIndex;
    @Bind(R.id.image)
    ImageView mImageView;
    private Bitmap mBitmap;

    public IntroducePage(Context context, int i) {
        super(context);
        mIndex=i;
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View  view = inflater.inflate(R.layout.page_introduce_help, null);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        if (!mIsInit) {
            if (mIndex == 0) {

                if (GlobalCfg.IS_POTRAIT){
                    setImageResource(R.raw.introduce1);

                }else{
                    setImageResource(R.raw.introduce1_land);
                }


            } else if (mIndex == 1) {
                if (GlobalCfg.IS_POTRAIT){
                    setImageResource(R.raw.introduce2);
                }else{
                    setImageResource(R.raw.introduce2_land);
                }


            }else if (mIndex==2){
                if (GlobalCfg.IS_POTRAIT){
                    setImageResource(R.raw.introduce3);
                }else {
                    setImageResource(R.raw.introduce3_land);
                }
            }

            mIsInit=true;
        }

    }

    private void setImageResource(int id){
        InputStream is = ct.getResources().openRawResource(id);

        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
//width,hight设为原来的十分一
        options.inSampleSize = 3;
       mBitmap =BitmapFactory.decodeStream(is,null,options);
        mImageView.setImageBitmap(mBitmap);

    }

    @Override
    public void destory() {
        super.destory();
        if (mBitmap!=null){
        mBitmap.recycle();
        mBitmap=null;
        System.gc();}
    }
}
