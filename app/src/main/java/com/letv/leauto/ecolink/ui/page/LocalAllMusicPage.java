package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.utils.Trace;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/5.
 */
public class LocalAllMusicPage extends BasePage implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "LocalAllMusicPage";
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    private HomeActivity act;
    @Bind(R.id.ll_content)
    LinearLayout m_llContent;        //切换的中间内容
    private LocalDownedPage downedPage;
    // private LocalDowningPage downingPage;

    private DownLoadingPage downLoadingPage;
    private HomeActivity homeActivity;
    private RelativeLayout.LayoutParams pLayoutParams;

    public LocalAllMusicPage(Context context) {
        super(context);
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.loacl_allmusic_p, null);
        } else {
            view = inflater.inflate(R.layout.loacl_allmusic, null);
        }
        ButterKnife.bind(this, view);
        homeActivity = (HomeActivity) ct;
        return view;
    }

    @Override
    public void initData() {
        radioGroup.setOnCheckedChangeListener(this);
        pLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
        selectPage(0);
    }

    private void selectPage(int position) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton child = (RadioButton) radioGroup.getChildAt(i);
            child.setChecked(false);
            child.setTextColor(ct.getResources().getColor(
                    R.color.transparent_60));
            TextPaint tp = child.getPaint();
            tp.setFakeBoldText(false);
        }
        RadioButton select = (RadioButton) radioGroup.getChildAt(position);
        select.setChecked(true);
        select.setTextColor(ct.getResources().getColor(
                R.color.white));
        TextPaint tp = select.getPaint();
        tp.setFakeBoldText(true);
    }



    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.btn_download_complete:
                selectPage(0);
                if (downedPage==null){
                    downedPage = new LocalDownedPage(ct);
                    if (mAutoPlay){
                        downedPage.setAutoPlayState(true);
                        mAutoPlay=false;
                    }
                    downedPage.initData();
                }
                else{
                    downedPage.onResume();
                }
                m_llContent.removeAllViews();
                m_llContent.addView(downedPage.getContentView(),pLayoutParams);
                break;
            case R.id.btn_downloading:
                selectPage(1);
                if (downLoadingPage==null){
                    downLoadingPage = new DownLoadingPage(ct);
                    downLoadingPage.setType(1);
                    downLoadingPage.initData();
                }
                else{
                    downLoadingPage.onResume();
                }


                downLoadingPage.notifyDataChange();
                m_llContent.removeAllViews();
                m_llContent.addView(downLoadingPage.getContentView(),pLayoutParams);

                break;
        }
    }

    @Override
    public void onResume(){
        if(downLoadingPage != null){
            downLoadingPage.onResume();
        }
        if (downedPage!=null){
            downedPage.onResume();
        }
    }

    @Override
    public void destory() {
        super.destory();
        if (downedPage != null) {
            downedPage.destory();
        }
        if (downLoadingPage != null) {
            downLoadingPage.destory();
        }
    }
    private boolean mAutoPlay;
    public void setAutoPlay(boolean b) {
        mAutoPlay=b;

    }

    public void autoPlay() {
        if (downedPage!=null){
            downedPage.autoPlay();
            mAutoPlay=false;
        }

    }
}
