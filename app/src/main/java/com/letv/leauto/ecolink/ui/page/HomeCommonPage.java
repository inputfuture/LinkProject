package com.letv.leauto.ecolink.ui.page;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.leauto.sdk.NotifyCommand;
import com.leauto.sdk.SdkManager;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.SeconPageAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.model.AppInfo;
import com.letv.leauto.ecolink.manager.ChoosedAppManager;
import com.letv.leauto.ecolink.receiver.ScreenRotationUtil;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.chooseapp.ChooseAppActivity;
import com.letv.leauto.ecolink.ui.chooseapp.ChooseAppActivity1;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.fragment.MainFragment;
import com.letv.leauto.ecolink.utils.PackageUtil;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shimeng on 16/06/20.
 */

public class HomeCommonPage extends BasePage implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @Bind(R.id.fragment_second_grdiview)
    GridView mSecondFramenGridView;
    protected HomeActivity homeActivity;
    private MainFragment mMainFragment;
    protected List<AppInfo> mAppList;
    public static boolean mCanDelete = false;
    private String radomApkNum;

    protected SeconPageAdapter mAppAdapter;
    private ChoosedAppManager mChoosedAppManager;
    private boolean mIsInit;


    public HomeCommonPage(Context context, List<AppInfo> list,MainFragment fragment,int page) {
        super(context);
        homeActivity = (HomeActivity) ct;
        mAppList=list;
        mMainFragment = fragment;
        mChoosedAppManager=ChoosedAppManager.getInstance(ct);
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = initOrietantion(inflater);
        ButterKnife.bind(this, view);
        Random r = new Random();
        radomApkNum = r.nextInt(100) + "";
        return view;
    }


    /**
     * 横竖屏初始化
     *
     * @param inflater
     * @return
     */
    private View initOrietantion(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_second, null);
        } else {
            view = inflater.inflate(R.layout.fragment_second_l, null);
        }
        return view;
    }


    @Override
    public void initData() {
        if (!mIsInit){

            if (mAppAdapter==null){
                mAppAdapter = new SeconPageAdapter(mAppList, homeActivity);
                mSecondFramenGridView.setAdapter(mAppAdapter);
//            mSecondFramenGridView.setVerticalSpacing(Utils.Dp2Px(homeActivity, 38));
//            mSecondFramenGridView.setOnItemClickListener(this);
//            mSecondFramenGridView.setOnItemLongClickListener(this);
            }else {
                mAppAdapter.notifyDataSetChanged();
            }
            mAppAdapter.setDeleteAPPListener(new SeconPageAdapter.deleteAPPListener() {
                @Override
                public void deleteClick(int position) {
                    deleteApp(position);
                }

            @Override
            public void imageClick(int position) {
                onItemClick(position);
            }

            @Override
            public void imageLongClick(int position) {
                mCanDelete = !mCanDelete;
                changeState(mCanDelete);
            }


            });
            mIsInit=true;}

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return true;
    }

    private void changeState(boolean mCanDelete) {
        mAppAdapter.setDelete(mCanDelete);
        mAppAdapter.notifyDataSetChanged();

    }

    public void onItemClick( final int position) {
        AppInfo appInfo=mAppList.get(position);
        if (appInfo.getAppPackagename().equals(Constant.HomeMenu.NAVI)){
            homeActivity.changeToNavi();

        }else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.LERAIDO)){
            homeActivity.ChangeToLeradio();

        }else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.LOCAL_MUCIC)){
            homeActivity.changeToLocal();

        }else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.LIVE)){
            startLiveApp();

        }else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.LEVEDIO)){
            startLeVedio();

        }else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.PHONE)){
            homeActivity.changeToPhone();

        }else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.SET)){
            homeActivity.changeToSetting();
        }else if(appInfo.getAppPackagename().equals(Constant.HomeMenu.FAVORCAR)){
            homeActivity.changeToFavorCar();
        }else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.WECHAT)){
            startWChartApp();
        }else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.GAODE)){
            startGaoDeApp();
        }else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.BAIDU)){
          startBaidu();

        }else if(appInfo.getAppPackagename().equals(Constant.HomeMenu.ADD)){
            HomeCommonPage.mCanDelete=false;
            SeconPageAdapter.isDelete=false;
            changeState(false);
            if (GlobalCfg.IS_POTRAIT){

                Intent intent = new Intent(homeActivity,ChooseAppActivity.class);
                mMainFragment.startActivityForResult(intent,MainFragment.CHOOSE_APP_CODE);
            }else{
                Intent intent = new Intent(homeActivity,ChooseAppActivity1.class);
                mMainFragment.startActivityForResult(intent,MainFragment.CHOOSE_APP_CODE);

            }

            GlobalCfg.isChooseAppState = true;
        } else if (appInfo.getAppPackagename().equals(Constant.HomeMenu.QPLAY)){
            homeActivity.changeToQPlay();

        }else {
            if (mCanDelete){

            }else {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(mAppList.get(position).getAppPackagename(), mAppList.get(position).getActivityName()));

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ct.startActivity(intent);
                    ScreenRotationUtil.commandShowPop(ct);
                    if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                        ScreenRotationUtil.startLandService(ct,mAppList.get(position).getAppPackagename());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ScreenRotationUtil.commandHidePop(ct);
                }
            }
        }



    }

    private void deleteApp(int position) {
        //应用不能被删除
        if (!mAppList.get(position).getCouldDelete()) {
            return;
        }
        mChoosedAppManager.deleteAppFromDB(mAppList.get(position));
        mMainFragment.refreshPages();
    }


    public void refresh() {
        if (mAppAdapter != null) {
            mAppAdapter.notifyDataSetChanged();
        }
    }



    private void startGaoDeApp() {
        if (PackageUtil.ApkIsInstall(ct, Constant.HomeMenu.GAODE)) {
            ct.startActivity(ct.getPackageManager().getLaunchIntentForPackage(Constant.HomeMenu.GAODE));

            if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                ScreenRotationUtil.startLandService(ct,Constant.HomeMenu.GAODE);
                commandShowPop();
            }
        } else {
            ToastUtil.showShort(ct, "未安装高德地图");
            commandHidePop();
        }
    }
    public void startWChartApp() {
        try {
            if (PackageUtil.ApkIsInstall(ct, Constant.HomeMenu.WECHAT)){

                ct.startActivity(ct.getPackageManager().getLaunchIntentForPackage(Constant.HomeMenu.WECHAT));
                commandShowPop();
                if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                    ScreenRotationUtil.startLandService(ct,Constant.HomeMenu.WECHAT);
                }
            }else{
                Toast.makeText(ct, "您的手机未安装微信", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void startBaidu(){
        if (PackageUtil.ApkIsInstall(ct, Constant.HomeMenu.BAIDU)) {
            try {
                ct.startActivity(ct.getPackageManager().getLaunchIntentForPackage(Constant.HomeMenu.BAIDU));
                ScreenRotationUtil.commandShowPop(ct);
                if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                    ScreenRotationUtil.startLandService(ct,Constant.HomeMenu.BAIDU);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ScreenRotationUtil.commandHidePop(ct);
            }

        } else {
            ToastUtil.showShort(ct, ct.getString(R.string.main_not_install_baidu));
        }
    }



    /**
     * 打开live
     */
    public void startLiveApp() {
        if (PackageUtil.ApkIsInstall(ct,Constant.HomeMenu.LIVE)){
            lePlayer.stopPlay();
            BaseActivity.isStoped=true;
            commandShowPop();
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName("com.letv.android.letvlive", "com.letv.android.letvlive.LiveActivity");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            ct.startActivity(intent);
        }else{
            if (queryDownTaskLive((DownloadManager) ct.getSystemService(ct.DOWNLOAD_SERVICE), DownloadManager.STATUS_RUNNING)) {
                Toast.makeText(ct, "正在下载", Toast.LENGTH_SHORT).show();
            } else {
                showDownloadLiveAPP();
            }

        }

    }


    /**
     * 打开乐视视频
     */
    public void startLeVedio() {
        try {
            if (PackageUtil.ApkIsInstall(ct, Constant.HomeMenu.LEVEDIO)){
                BaseActivity.isStoped=true;
                lePlayer.stopPlay();
                Trace.Debug("###","###stop 3");
                commandShowPop();
                ct.startActivity(ct.getPackageManager().getLaunchIntentForPackage(Constant.HomeMenu.LEVEDIO));
            }else{
                if (queryDownTaskVidio((DownloadManager) ct.getSystemService(ct.DOWNLOAD_SERVICE), DownloadManager.STATUS_RUNNING)) {
                    Toast.makeText(ct, "正在下载", Toast.LENGTH_SHORT).show();
                } else {
                    showDownloadLeVidioAPP();
                    commandHidePop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private boolean queryDownTaskLive(DownloadManager downManager, int status) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(status);
        Cursor cursor = downManager.query(query);
        if (cursor == null) {
            return false;
        }
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            int i = 0;
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                if ("letv_live".equals(title)) {
                    i++;
                }
            }
            if (i != 0) {
                cursor.close();
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean queryDownTaskVidio(DownloadManager downManager, int status) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(status);
        Cursor cursor = downManager.query(query);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {

            int i = 0;
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                if ("letv_client".equals(title)) {
                    i++;
                }
            }
            if (i != 0) {
                cursor.close();
                return true;
            } else {
                return false;
            }

        }
    }


    public void showDownloadLiveAPP() {
        NetworkConfirmDialog networkConfirmDialog=new NetworkConfirmDialog(ct,"是否下载乐视live",R.string.ok,R.string.cancel);
        networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
            @Override
            public void onConfirm(boolean checked) {

                Toast.makeText(ct, "开始下载乐视live", Toast.LENGTH_SHORT).show();
                //修改后台地址
                //download("letv_live", "http://ecolink.leauto.com/com.letv.android.letvlive.apk ");
                download("letv_live", "http://ecolink.leautolink.com/com.letv.android.letvlive.apk");
            }

            @Override
            public void onCancel() {

            }
        });
        networkConfirmDialog.show();
    }

    public void showDownloadLeVidioAPP() {
        NetworkConfirmDialog networkConfirmDialog=new NetworkConfirmDialog(ct,"是否下载乐视视频",R.string.ok,R.string.cancel);
        networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
            @Override
            public void onConfirm(boolean checked) {
                Toast.makeText(ct, "开始下载乐视视频", Toast.LENGTH_SHORT).show();
                //修改后台地址
                //download("letv_client", "http://ecolink.leauto.com/com.letv.android.client.apk");
                download("letv_client", "http://ecolink.leautolink.com/com.letv.android.client.apk");
            }

            @Override
            public void onCancel() {

            }
        });
        networkConfirmDialog.show();
    }

    private static String apkName;

    private void download(String title, String url) {
        Context mContext = EcoApplication.getInstance();
        apkName = title;
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //设置文件存放目录
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, apkName + radomApkNum + ".apk");
        request.allowScanningByMediaScanner();//用于设置是否允许本MediaScanner扫描
        request.setTitle(title);
        request.setDescription("letv desc");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long downloadId = downloadManager.enqueue(request);
    }

    /**
     * 车机链接时打开悬浮按钮
     */
    private void commandShowPop() {
        SdkManager.getInstance(ct).notifyCommand(
                NotifyCommand.COMMAND_SHOW_POP, 0, 0);
    }

    /**
     * 关闭悬浮按钮
     */
    private void commandHidePop() {
        SdkManager.getInstance(ct).notifyCommand(
                NotifyCommand.COMMAND_HIDE_POP, 0, 0);
    }


    @Override
    public void destory() {
        super.destory();

        Trace.Debug("***** home commonpage  destory");
        if (mAppList!=null){

            mAppList.clear();
            mAppList=null;
        }
        homeActivity=null;
        if (mAppAdapter!=null){
            mAppAdapter.destroy();
            mAppAdapter=null;}

    }
}

