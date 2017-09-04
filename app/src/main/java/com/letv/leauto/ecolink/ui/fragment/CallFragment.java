package com.letv.leauto.ecolink.ui.fragment;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.page.CallContactPage;
import com.letv.leauto.ecolink.ui.page.CallKeyPadPage;
import com.letv.leauto.ecolink.ui.page.CommonPagerAdapter;
import com.letv.leauto.ecolink.ui.page.YellowPage;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CallFragment extends BaseFragment implements View.OnClickListener ,ViewPager.OnPageChangeListener {

    private static final int CALL_UPDATE = 1;
    @Bind(R.id.vp_viewPager)
    ViewPager vpViewPager;
    @Bind(R.id.ll_call)
    LinearLayout mLlCall;
    @Bind(R.id.ll_book)
    LinearLayout mLlBook;
    @Bind(R.id.tv_call)
    TextView mTvCall;
    @Bind(R.id.tv_book)
    TextView mTvBook;
    @Bind(R.id.ll_yellow)
    LinearLayout mYellowLayout;
    @Bind(R.id.tv_yellow)
    TextView mYellowTextView;
    private ArrayList<String> mTitleLists; //tab名称列表
    private ArrayList<BasePage> mPages;
    private CallContactPage callContactPage;
    private CallKeyPadPage callKeyPadPage;
    private CommonPagerAdapter callAdapter;
    private HomeActivity homeActivity;
    private YellowPage mYellowPage;
    private ArrayList<TextView> mTextViewList;

    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            BasePage page = mPages.get(0);
            if (page != null) {

                page.initData();
            }

        }
    };
    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);
        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) homeActivity).isPopupWindowShow) {
                    homeActivity.changeToHome();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }
    @Override
    protected View initView(LayoutInflater inflater) {
        View  view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_call, null);
        } else {
            view = inflater.inflate(R.layout.fragment_call, null);
        }
        ButterKnife.bind(this, view);
        homeActivity = (HomeActivity)mContext;
        mContext.getContentResolver().registerContentObserver(
                CallLog.Calls.CONTENT_URI, true, mObserver);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager telephony = (TelephonyManager)mContext.getSystemService(
                mContext.TELEPHONY_SERVICE);
        telephony.listen(new OnePhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.getContentResolver().unregisterContentObserver(mObserver);
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
        for (int i = 0; i < mPages.size(); i++) {
            BasePage basePage=mPages.get(i);
            basePage.destory();
        }
        mPages.clear();
        mTitleLists.clear();
        mTextViewList.clear();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mPages =new ArrayList<BasePage>();
        mTitleLists =new ArrayList<String>();

        callKeyPadPage = new CallKeyPadPage(mContext);
        callContactPage = new CallContactPage(mContext);
        mYellowPage=new YellowPage(mContext);
        mTextViewList=new ArrayList<>();
        mTextViewList.add(mTvCall);
        mTextViewList.add(mTvBook);
        mTextViewList.add(mYellowTextView);

        mPages.add(callKeyPadPage);
        mPages.add(callContactPage);
        mPages.add(mYellowPage);
        mTitleLists.add(mContext.getString(R.string.str_dail));
        mTitleLists.add(mContext.getString(R.string.str_contact));
        mTitleLists.add(mContext.getString(R.string.yellow_page));

        callAdapter = new CommonPagerAdapter(mContext, mPages, mTitleLists);
        vpViewPager.setAdapter(callAdapter);
        vpViewPager.setOffscreenPageLimit(0);
        vpViewPager.setOnPageChangeListener(this);
        mLlCall.setOnClickListener(this);
        mLlBook.setOnClickListener(this);
        mYellowLayout.setOnClickListener(this);
        BasePage page = mPages.get(0);
        page.initData();
        selectPage(0);
    }
    private void selectPage(int position) {
        vpViewPager.setCurrentItem(position);
        for (int i = 0; i < mTextViewList.size(); i++) {
            TextView textView=mTextViewList.get(i);
            if (i==position){
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                textView.setBackgroundResource(R.drawable.radiobutton_bg);
                textView.setTextColor(mContext.getResources().getColor(
                        R.color.white));
                TextPaint bill = textView.getPaint();
                bill.setFakeBoldText(true);
            }else {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                textView.setBackgroundResource(R.color.transparent);
                textView.setTextColor(mContext.getResources().getColor(
                        R.color.transparent_60));
                TextPaint my = textView.getPaint();
                my.setFakeBoldText(false);
            }
        }


    }
    @Override
    public void onResume(){
        super.onResume();
        MobclickAgent.onPageStart("CallFragment");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_call:{
                BasePage page = mPages.get(0);
                page.initData();
                selectPage(0);}
            break;
            case R.id.ll_book:{
                BasePage page = mPages.get(1);
                page.initData();
                selectPage(1);}
            break;
            case R.id.ll_yellow:{
                BasePage page = mPages.get(2);
                page.initData();
                selectPage(2);}
            break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position==1){
            callContactPage.initData();
        }else if (position==2){
            mYellowPage.initData();
        }

        selectPage(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private boolean mCallLogUpdate=false;

    //
    private ContentObserver mObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            Trace.Debug("####通讯录更新");
            mHandler.removeMessages(CALL_UPDATE);
            mHandler.sendEmptyMessageDelayed(CALL_UPDATE,700);

        }
    };


    /**
     * 电话状态监听.
     * @author stephen
     *
     */
    class OnePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged( int state, String incomingNumber) {

            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:


                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (!TextUtils.isEmpty(incomingNumber)){
                        if (!TextUtils.isEmpty(callKeyPadPage.getNumText())&&incomingNumber.contains(callKeyPadPage.getNumText())){
                            callKeyPadPage.clearNumEdit();


                        }
                    }


                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        MobclickAgent.onPageEnd("CallFragment");
    }


    public static CallFragment getInstance(Bundle nBundle, boolean b) {
        CallFragment mFragment = new CallFragment();
        mFragment.setArguments(nBundle);
        return mFragment;
    }





}
