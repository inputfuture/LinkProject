package com.letv.leauto.ecolink.ui.fragment;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.csr.gaia.library.Gaia;
import com.csr.gaia.library.GaiaLink;
import com.leauto.sdk.NotifyCommand;
import com.leauto.sdk.SdkManager;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.SeconPageAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.model.AppInfo;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.manager.ChoosedAppManager;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.page.HomeCommonPage;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainFragment extends BaseFragment {

    @Bind(R.id.iv_logo_link)
    ImageView iv_logo;
    @Bind(R.id.ll_dot)
    LinearLayout ll_dot;//获取轮播图片的点的parent，用于动态添加要显示的点

    @Bind(R.id.home_pager)
    ViewPager mHomePager;

    private ChoosedAppManager mAppManager;
    private int mCurrentIndex;
//    private HomeActivity homeActivity;
    private static final int UPDATE_TIME = 0x168;
    public static String HIDE = "HIDELOGO";
    public static String SHOW = "SHOWLOG";
    private ArrayList<BasePage> pages = new ArrayList<BasePage>();
    private static final int EACH_PAGE_APP_COUNT = 8;
    //添加的所有应用
    protected List<AppInfo> mAppList = new ArrayList<AppInfo>();
    private HomePageAdapter mHomePageAdapter;

    private String quality;

    //切出去时是否需要暂停状态判断
    Boolean shouldResetPlayerState = false;

    private String radomApkNum;
    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private static int currentIndex;

    public static final int CHOOSE_APP_CODE = 0X94;
    public static final int REFRESH=0X90;

//    private Handler mHandler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case  REFRESH:
//                    refreshPages();
//                    break;
//            }
//        }
//    };

    private MyHandler myHandler;

    private static class MyHandler extends Handler{
        private WeakReference<MainFragment> mainFragment;

        public MyHandler(MainFragment mainFragment) {
            this.mainFragment = new WeakReference<MainFragment>(mainFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case  REFRESH:
                   mainFragment.get().refreshPages();
                    break;
            }
        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = initOrietantion(inflater);
        ButterKnife.bind(this, view);
        myHandler=new MyHandler(this);

        showLogo();
        if (null == mGaiaLink) {
            mGaiaLink = GaiaLink.getInstance();
        }
        mGaiaLink.setReceiveHandler(this.getGaiaHandler());
        if (!mGaiaLink.isConnected()) {
            startDev();
        } else {
            getInformation();
        }
        completeReceiver = new CompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainFragment.HIDE);
        intentFilter.addAction(MainFragment.SHOW);
        intentFilter.addAction("XIANSHILOGO");
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(completeReceiver, intentFilter);
        return view;
    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    ((HomeActivity) mContext).onBackPressed();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    /**
     * 横竖屏初始化
     *
     * @param inflater
     * @return
     */
    private View initOrietantion(LayoutInflater inflater) {
        View view;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_main, null);
        } else {
            view = inflater.inflate(R.layout.fragment_main_l, null);
        }
        DataUtil.getInstance().getCpName(null, "tag");
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        mAppManager= ChoosedAppManager.getInstance(mContext);
        initHomePage();


        for (int i = 0; i < pages.size(); i++) {
            ll_dot.addView(View.inflate(mContext, R.layout.advertisement_board_dot, null));
        }
        initDots(ll_dot);
        mHomePageAdapter = new HomePageAdapter(mContext, pages);
        mHomePager.setAdapter(mHomePageAdapter);
        mHomePager.setOffscreenPageLimit(0);
        pages.get(mHomePager.getCurrentItem()).initData();
        mHomePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BasePage page = pages.get(position);
                page.initData();
                setCurrentDot(position);
                mCurrentIndex=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > pages.size() - 1
                || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }

    private void initDots(LinearLayout ll_dot) {
        dots = new ImageView[pages.size()];

        // 循环取得小点图片
        for (int i = 0; i < pages.size(); i++) {
            dots[i] = (ImageView) ll_dot.getChildAt(i);
            dots[i].setEnabled(true);// 都设为灰色
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);// 设置为选中状态
    }



    private void initHomePage() {
        mAppList.clear();
        mAppList.addAll(mAppManager.getSavedApps(false));
        if (mAppList.size() < GlobalCfg.EXPENDSIZE) {
            mAppList.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_add)
                    , mContext.getResources().getString(R.string.add_app), Constant.HomeMenu.ADD, null, false));
        }
        for (BasePage page : pages) {
            page.destory();
        }
        pages.clear();
        if (mAppList.size() % EACH_PAGE_APP_COUNT == 0) {
            int count = mAppList.size() / EACH_PAGE_APP_COUNT;
            for (int i = 0; i < count; i++) {
                if (i <= 4) {
                    List<AppInfo> appInfos = new ArrayList<>();
                    appInfos.addAll(mAppList.subList(i * EACH_PAGE_APP_COUNT, i * EACH_PAGE_APP_COUNT + EACH_PAGE_APP_COUNT));
                    HomeCommonPage homeSecondPage = new HomeCommonPage(mContext, appInfos, this, i + 1);
                    pages.add(homeSecondPage);
                }
            }
        } else {
            int count = mAppList.size() / EACH_PAGE_APP_COUNT + 1;
            if (count <= 5) {
                for (int i = 0; i < count - 1; i++) {
                    List<AppInfo> appInfos = new ArrayList<>();
                    appInfos.addAll(mAppList.subList(i * EACH_PAGE_APP_COUNT, i * EACH_PAGE_APP_COUNT + EACH_PAGE_APP_COUNT));
                    HomeCommonPage homeSecondPage = new HomeCommonPage(mContext, appInfos, this, i + 1);
                    pages.add(homeSecondPage);
                }
                List<AppInfo> appInfos = new ArrayList<>();
                appInfos.addAll(mAppList.subList((count - 1) * EACH_PAGE_APP_COUNT, mAppList.size()));
                HomeCommonPage homeSecondPage = new HomeCommonPage(mContext, appInfos, this, count);
                pages.add(homeSecondPage);
            } else {
                for (int i = 0; i < 5; i++) {
                    List<AppInfo> appInfos = new ArrayList<>();
                    appInfos.addAll(mAppList.subList(i * EACH_PAGE_APP_COUNT, i * EACH_PAGE_APP_COUNT + EACH_PAGE_APP_COUNT));
                    HomeCommonPage homeSecondPage = new HomeCommonPage(mContext, appInfos, this, i + 1);
                    pages.add(homeSecondPage);
                }
            }

        }
    }

    /**
     * 显示最后一次播放的音乐图片
     */
    public void showLogo() {
        if (GlobalCfg.CAR_FACTORY_NAME != null && GlobalCfg.CAR_FACTORY_NAME.toLowerCase().contains("beiqi".toLowerCase()) && iv_logo != null) {
//            ToastUtil.showShort(mContext,"显示");
            iv_logo.setVisibility(View.VISIBLE);
        } else {
            iv_logo.setVisibility(View.GONE);
        }
    }


    /**
     * 显示最后一次播放的音乐图片
     */
    public void hideLogo() {
//        ToastUtil.showShort(mContext,"隐藏");
        iv_logo.setVisibility(View.GONE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Trace.Debug("######onActivityResult");
        switch (resultCode){
            case CHOOSE_APP_CODE:
                HomeCommonPage.mCanDelete=false;
                SeconPageAdapter.isDelete=false;
//                if (DeviceUtils.getDeviceName().contains("HM NOTE")){
//                    Trace.Debug("#####红米手机 ");
                    myHandler.sendEmptyMessageDelayed(REFRESH,1000);
                GlobalCfg.isChooseAppState = false;
//                }else {
//
//                refreshPages();}

                break;
        }


    }

    public void refreshPages() {
        initHomePage();
        ll_dot.removeAllViews();

        for (int i = 0; i < pages.size(); i++) {
            ll_dot.addView(View.inflate(mContext, R.layout.advertisement_board_dot, null));
        }
        Trace.Debug("#### mAppList size"+mAppList.size());
        initDots(ll_dot);
        mHomePageAdapter = new HomePageAdapter(mContext, pages);
        mHomePager.setAdapter(mHomePageAdapter);
        mHomePager.setOffscreenPageLimit(0);
        if (mCurrentIndex<pages.size()){
            mHomePager.setCurrentItem(mCurrentIndex);
        }else{
            mHomePager.setCurrentItem(pages.size()-1);
        }
        pages.get(mHomePager.getCurrentItem()).initData();
        mHomePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BasePage page = pages.get(position);
                page.initData();
                setCurrentDot(position);
                mCurrentIndex=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 车机链接时打开悬浮按钮
     */
    private void commandShowPop() {
        SdkManager.getInstance(mContext).notifyCommand(
                NotifyCommand.COMMAND_SHOW_POP, 0, 0);
    }


    /**
     * 关闭悬浮按钮
     */
    private void commandHidePop() {
        SdkManager.getInstance(mContext).notifyCommand(
                NotifyCommand.COMMAND_HIDE_POP, 0, 0);
    }





    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainFragment");
        if (!GlobalCfg.IS_POTRAIT) {
            if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        commandHidePop();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd("MainFragment");


    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (this.completeReceiver != null) {
            mContext.unregisterReceiver(completeReceiver);
            completeReceiver = null;
        }
        if (mGaiaLink!=null && mGaiaLink.isConnected()) {
            mGaiaLink.cancelNotification(Gaia.EventId.CHARGER_CONNECTION);
            mGaiaLink = null;
            Trace.Debug("cancelNotification");
        }

        if (mAppList != null) {
            mAppList.clear();
            mAppList=null;
        }
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler=null;
        }
    }

    /**
     * 语音搜索后跳转
     */
    public void replaceFragmentByVoiceMusic(String intention, String keyword) {
//        homeActivity.changeBottom(Constant.TAG_LERADIO);
//        FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
//        Fragment fragment = manager.findFragmentByTag("LeRadioAlumFragment");
//        if (fragment != null) {
//            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.remove(fragment);
//            transaction.commitAllowingStateLoss();
//        }
//        fragment = manager.findFragmentByTag("LeMusicFragment");
//        if (fragment != null) {
//            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.remove(fragment);
//            transaction.commitAllowingStateLoss();
//        }
//        Bundle nBundle = new Bundle();
//        nBundle.putString(VoiceCfg.RESULT_KEYWORD, keyword);
//        LeMusicFragment secondFragment = LeMusicFragment.getInstance(nBundle, true);
//
//        manager.beginTransaction().replace(R.id.music_play, secondFragment, "LeMusicFragment").commitAllowingStateLoss();
    }


    private CompleteReceiver completeReceiver;

    public void initMusic() {
    }


    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Toast.makeText(context, R.string.download_comlitely, Toast.LENGTH_SHORT);
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                installFile(id);

            }
            if (intent.getAction().equals(MainFragment.SHOW)) {
                showLogo();
            }
            if (intent.getAction().equals(MainFragment.HIDE)) {
                hideLogo();
            }
        }
    }

    DownloadManager downloadManager;

    //安装应用，指定位置
    private void installFile(long id) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        downloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
        File apkFile = queryDownloadedApk(id);
        if (apkFile != null && apkFile.length() > 0 && apkFile.exists() && apkFile.isFile() && apkFile.getName().contains(".apk")) {
            install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(install);
        }

    }

    //通过downLoadId查询下载的apk，解决6.0以后安装的问题
    public File queryDownloadedApk(long id) {
        File targetApkFile = null;
        downloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cur = downloadManager.query(query);
        if (cur != null) {
            if (cur.moveToFirst()) {
                String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (!TextUtils.isEmpty(uriString)) {
                    targetApkFile = new File(Uri.parse(uriString).getPath());
                    Trace.Error("===========", targetApkFile.getAbsolutePath());
                }
            }
            cur.close();
        }

        return targetApkFile;
    }


    class HomePageAdapter extends PagerAdapter {
        private Context mct;
        private ArrayList<BasePage> pages;

        public HomePageAdapter(Context ct, ArrayList<BasePage> pages) {
            this.mct = ct;
            this.pages = pages;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Trace.Error("==instantiateItem==", position + "");
            container.addView(pages.get(position).getContentView(), 0);
            return pages.get(position).getContentView();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position>=0&&position<pages.size())
                container.removeView(pages.get(position).getContentView());
        }
    }


    public void refreshAllPage() {
        for(BasePage page: pages) {
            page.refresh();
        }
    }



}
