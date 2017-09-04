package com.letv.leauto.ecolink.thincar.protocol;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.R;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.thirdapp.ThirdAppInfo;
import com.leauto.link.lightcar.thirdapp.ThirdAppModel;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.database.model.AppInfo;
import com.letv.leauto.ecolink.manager.ChoosedAppManager;
import com.letv.leauto.ecolink.thincar.processes.AndroidProcesses;
import com.letv.leauto.ecolink.utils.PackageUtil;
import com.letv.leauto.ecolink.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/27.
 */
public class ThirdAppMsgHelp {
    private static final String TAG = "ThirdAppMsgHelp";
    private static final String PIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String FILE_FORMAT = ".png";
    private static ThirdAppMsgHelp ourInstance = new ThirdAppMsgHelp();
    private static final int IMAGE_WIDTH = 58;
    private static final int IMAGE_HEIGHT = 58;
    private Context mContext;
    private Resources mResources;

    private static final String SPLIT_SIGN = "-";

    private int mFirstHomeAppNum = 0;

    /**
     * 发送的最大最近使用app数量
     */
    private static final int MAX_RECENT_APP_NUM = 8;

    private List<AppInfo> mRecentAppList = new ArrayList<>();

    private List<String> mFirstHomeAppPageList = new ArrayList<>();
    private String[] mFirstHomeAppPageArray = new String[]{
            Constant.TAG_LOCAL, Constant.TAG_LE_VIDEO,
            Constant.TAG_WEIXIN, Constant.TAG_GAODE_MAP,Constant.TAG_BAIDU_MAP
    };

    private List<Integer> mFirstHomeAppNameList = new ArrayList<>();
    private int[] mFirstHomeAppNameArray = new int[]{
            R.string.main_nav_localmusic, R.string.main_nav_levedio,
            R.string.main_nav_wechat, R.string.main_nav_gaode_map,R.string.main_nav_baidu_map
    };

    private List<Integer> mFirstHomeAppPicList = new ArrayList<>();
    private int[] mFirstHomeAppPicArray = new int[]{
            R.mipmap.menu_icon_localmusic, R.mipmap.menu_icon_levideo,
            R.mipmap.menu_icon_wechat, R.mipmap.menu_icon_gaode,R.mipmap.menu_icon_baidu
    };

    public void initThirdAppMsgHelp(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mRecentAppList.clear();
        //getRecentApps(context);
        intFirstHomePageData();

        mFirstHomeAppNum = mFirstHomeAppPageList.size();
    }

    public static ThirdAppMsgHelp getInstance() {
        return ourInstance;
    }

    private ThirdAppMsgHelp() {
    }

    private void intFirstHomePageData() {
        for(int i = 0;i < mFirstHomeAppPageArray.length; i ++) {
            String pageName = mFirstHomeAppPageArray[i];
            if (pageName.equals(Constant.TAG_LE_VIDEO) && !PackageUtil.ApkIsInstall(mContext,Constant.LE_VIDEO_PACKAGE_NAME)) {
                continue;
            }

            if (pageName.equals(Constant.TAG_WEIXIN) && !PackageUtil.ApkIsInstall(mContext,Constant.WEIXIN_PACKAGE_NAME)) {
                continue;
            }

            if (pageName.equals(Constant.TAG_GAODE_MAP) && !PackageUtil.ApkIsInstall(mContext,Constant.GAODE_MAP_PACKAGE_NAME)) {
                continue;
            }

            if (pageName.equals(Constant.TAG_BAIDU_MAP) && !PackageUtil.ApkIsInstall(mContext,Constant.BAIDU_MAP_PACKAGE_NAME)) {
                continue;
            }

            mFirstHomeAppPageList.add(mFirstHomeAppPageArray[i]);
            mFirstHomeAppNameList.add(mFirstHomeAppNameArray[i]);
            mFirstHomeAppPicList.add(mFirstHomeAppPicArray[i]);
        }
    }

    //手机车机连接时，通知手机中已经有的应用
    public void requestAllAppInfo(final Context context) {
        new Thread() {
            @Override
            public void run() {
                List<AppInfo> saveList = ChoosedAppManager.getInstance(context).getSavedApps(true);
                List<AppInfo> list = new ArrayList<AppInfo>();
                for (AppInfo info : saveList) {
                    if (PackageUtil.ApkIsInstall(context,info.getAppPackagename()) || info.getAppPackagename().equals(Constant.HomeMenu.FAVORCAR)) {
                        list.add(info);
                    }
                }

                List<AppInfo> firstHomeList = list;
                if (list.size() >= 8) {
                    firstHomeList = list.subList(0,8);
                }

                sendRecentApp(context,firstHomeList);
                sendThirdApp(context,list,firstHomeList.size());
            }
        }.start();
    }

    public void sendRecentApp(Context context,List<AppInfo> list) {
        ThirdAppModel model = new ThirdAppModel();
        model.Type = ThinCarDefine.Interface_Notify;
        model.Method = "NotifyRecentAppInfo";
        model.Parameter = new ThirdAppModel.Parameter();

//        ThirdAppInfo[] appArray = new ThirdAppInfo[mFirstHomeAppPageList.size()];
//
//        for (int i = 0; i < mFirstHomeAppPageList.size(); i++) {
//            ThirdAppInfo thirdAppInfo = new ThirdAppInfo();
//            thirdAppInfo.name = mContext.getString(mFirstHomeAppNameList.get(i));
//            LogUtils.i(TAG, "sendRecentApp name: " + thirdAppInfo.name);
//            thirdAppInfo.appid = Constant.LOCAL_APP_ID;
//            thirdAppInfo.pageid = mFirstHomeAppPageList.get(i);
//            thirdAppInfo.order = i + 1;
//            thirdAppInfo.iconid = thirdAppInfo.appid +SPLIT_SIGN + thirdAppInfo.pageid;
//            appArray[i] = thirdAppInfo;
//        }


        ThirdAppInfo[] appArray = new ThirdAppInfo[list.size()];

        for (int i = 0; i < list.size(); i++) {
            ThirdAppInfo thirdAppInfo = new ThirdAppInfo();
            thirdAppInfo.name = list.get(i).getAppName();
            LogUtils.i(TAG, "sendThirdApp name: " + thirdAppInfo.name);
            thirdAppInfo.appid = list.get(i).getAppPackagename();
            thirdAppInfo.pageid = list.get(i).getActivityName();
            thirdAppInfo.order = i + 1;
            thirdAppInfo.iconid = thirdAppInfo.appid;
            appArray[i] = thirdAppInfo;
        }

        model.Parameter.appinfos = appArray;
        JSONObject obj = (JSONObject) JSON.toJSON(model);
        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.THIRD_APP_APPID,obj);

        for (int i = 0; i < list.size(); i++) {
            Drawable drawable = list.get(i).getAppIcon();
            Bitmap bitmap = Utils.convertDrawableToBitMap(drawable);
           // Bitmap bitmap = BitmapFactory.decodeResource(mResources, mFirstHomeAppPicList.get(i));
            saveAndSendPic(bitmap, list.get(i).getAppPackagename(),DataSendManager.RECEND_APP_DATA_TYPE_PICTURE);
        }
    }

    private void sendThirdApp(Context context,List<AppInfo> list,int firstHomeSize) {
        //removeUninstallApp(list);

        //ThirdAppInfo[] appArray = addFirstHomeApp(list.size());
        ThirdAppInfo[] appArray = new ThirdAppInfo[list.size()];

        for (int i = 0; i < list.size(); i++) {
            ThirdAppInfo thirdAppInfo = new ThirdAppInfo();
            thirdAppInfo.name = list.get(i).getAppName();
            LogUtils.i(TAG, "sendThirdApp name: " + thirdAppInfo.name);
            thirdAppInfo.appid = list.get(i).getAppPackagename();
            thirdAppInfo.pageid = list.get(i).getActivityName();
            thirdAppInfo.order = i + 1 + firstHomeSize;
            thirdAppInfo.iconid = thirdAppInfo.appid;
            appArray[i] = thirdAppInfo;
        }

        sendTenItemEachTime(appArray,list);
    }

    /**
     * 每次最多发10个应用给车机
     * @param appArray
     */
    private void sendTenItemEachTime(ThirdAppInfo[] appArray,List<AppInfo> appList) {
        int left = appArray.length % 10;
        int time = appArray.length / 10;
        if (left > 0) {
            time = time + 1;
        }

        int length = appArray.length;
        int hasSendData = 0;
        int sendData = 0;
        int leftData = length;

        if (leftData >= 10) {
            sendData = 10;
        } else {
            sendData = leftData;
        }

        for (int i = 1; i <= time;i ++) {
            ThirdAppModel model = new ThirdAppModel();
            model.Type = ThinCarDefine.Interface_Notify;
            model.Method = ThinCarDefine.ProtocolNotifyMethod.METHOD_NOTIFY_APP;
            model.Parameter = new ThirdAppModel.Parameter();

            ThirdAppInfo[] newArray = new ThirdAppInfo[sendData];
            System.arraycopy(appArray, hasSendData, newArray, 0, sendData);
            model.Parameter.appinfos = newArray;
            JSONObject obj = (JSONObject) JSON.toJSON(model);

            LogUtils.i(TAG,"sendTenItemEachTime json:" + obj.toString());
            DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.THIRD_APP_APPID,obj);

            //送图片
            //senFirtHomeAppPic();//先发送首页8个app的图片
            for (int j = hasSendData; j < hasSendData + sendData; j++) {
                Drawable drawable = appList.get(j).getAppIcon();
                Bitmap bitmap = Utils.convertDrawableToBitMap(drawable);
                saveAndSendPic(bitmap, appList.get(j).getAppPackagename(),DataSendManager.DATA_TYPE_PICTURE);
            }
            hasSendData += sendData;
            leftData = length - hasSendData;
            if(leftData >= 10) {
                sendData = 10;
            } else {
                sendData = leftData;
            }
        }
    }

    private void removeUninstallApp(List<AppInfo> list) {
        if (!PackageUtil.ApkIsInstall(mContext,Constant.GAODE_MAP_PACKAGE_NAME)) {
            list.remove(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_gaode),"高德地图",Constant.GAODE_MAP_PACKAGE_NAME,"",false));
        }

        if (!PackageUtil.ApkIsInstall(mContext,Constant.BAIDU_MAP_PACKAGE_NAME)) {
            list.remove(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_baidu),"百度地图",Constant.BAIDU_MAP_PACKAGE_NAME,"",false));
        }
    }

    private void senFirtHomeAppPic() {
        for (int i = 0; i < mFirstHomeAppNum; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(mResources, mFirstHomeAppPicList.get(i));
            saveAndSendPic(bitmap, Constant.LOCAL_APP_ID + SPLIT_SIGN + mFirstHomeAppPageList.get(i),DataSendManager.DATA_TYPE_PICTURE);
        }
    }

    /**
     * 添加的三方app用包名作为唯一的iconid,
     * 本应用内的其它页面用appid + "-" + pageId作为唯一的iconid,
     * @param size
     * @return
     */
    private ThirdAppInfo[] addFirstHomeApp(int size) {
        ThirdAppInfo[] appArray = new ThirdAppInfo[size + mFirstHomeAppNum];

        for (int i = 0; i < mFirstHomeAppPageList.size(); i++) {
            ThirdAppInfo thirdAppInfo = new ThirdAppInfo();
            thirdAppInfo.name = mContext.getString(mFirstHomeAppNameList.get(i));
            thirdAppInfo.appid = Constant.LOCAL_APP_ID;
            thirdAppInfo.pageid = mFirstHomeAppPageList.get(i);
            thirdAppInfo.order = i + 1 + mRecentAppList.size();
            thirdAppInfo.iconid = thirdAppInfo.appid +SPLIT_SIGN + thirdAppInfo.pageid;
            appArray[i] = thirdAppInfo;
        }

        return appArray;
    }

    //根据车机请求信息发送对应应用信息
    public void sendAppIcon(String fileName, Context context) {
        try {
            String packageName = context.getPackageName();
            List<AppInfo> saveApps = ChoosedAppManager.getInstance(context).getSavedApps(true);
            Bitmap bitmap = null;
            if (fileName.contains(packageName)) {//首页8个应用
                String[] array = fileName.split(SPLIT_SIGN);
                if (array.length > 1) {
                    int index = findHomePosition(array[1]);
                    bitmap = BitmapFactory.decodeResource(mResources, mFirstHomeAppPicArray[index]);
                }
            } else {
                int position = findAddAppPositon(saveApps,fileName);
                Drawable drawable = saveApps.get(position).getAppIcon();
                bitmap = Utils.convertDrawableToBitMap(drawable);
            }

            if (bitmap != null) {
                /** 需要调整？？？*/
                saveAndSendPic(bitmap, fileName,DataSendManager.DATA_TYPE_PICTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int findAddAppPositon( List<AppInfo> apps,String fileName) {
        int result = 0;
        for (int i=0;i< apps.size();i++) {
            AppInfo info = apps.get(i);
            if (info.getAppPackagename().equalsIgnoreCase(fileName)) {
                result = i;
            }
        }

        return result;
    }

    private int findHomePosition(String str) {
        int result = 0;

        for (int i =0;i < mFirstHomeAppPageArray.length;i ++) {
            if (mFirstHomeAppPageArray[i].equalsIgnoreCase(str)) {
                result =i;
                break;
            }
        }
        return result;
    }

    private void saveAndSendPic(Bitmap bitmap, String fileName,byte type) {
        if (bitmap == null) {
            return;
        }
        Bitmap resizeBitmap = Utils.resizeImage(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT);
        File sysFile = new File(PIC_PATH);
        if (!sysFile.exists()) {
            sysFile.mkdir();
        }
        File filePic = new File(sysFile, fileName + FILE_FORMAT);

        LogUtils.i(TAG, "saveDrawable file name:" + filePic.getName());
        if (filePic.exists()) {
            filePic.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(filePic);
            resizeBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.flush();
            out.close();

            byte[] picData = Utils.convertFileToBytes(filePic);
            byte[] sendData = new byte[picData.length + 64];
            byte[] songIdData = fileName.getBytes();

            System.arraycopy(songIdData, 0, sendData, 0, songIdData.length);
            System.arraycopy(picData, 0, sendData, 64, picData.length);

            DataSendManager.getInstance().sendPicDataToCar(ThinCarDefine.ProtocolAppId.THIRD_APP_APPID,sendData,type);

            //发送完删除
            filePic.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取手机最近使用的所有应用，不包括桌面和本应用
     * @param context
     * @return
     */
    private void getRecentApps(final Context context) {
        new Thread() {

            @Override
            public void run() {
                List<RunningAppProcessInfo> runningProcessList = AndroidProcesses.getRunningAppProcessInfo(context);
                List<AppInfo> installAppList = ChoosedAppManager.getInstance(context).getInstalledApp();

                List<String> allPackageList = new ArrayList<String>();
                for (RunningAppProcessInfo info : runningProcessList) {
                    allPackageList.add(info.processName);
                    if (info.pkgList != null && info.pkgList.length > 0) {
                        for (String packageName : info.pkgList) {
                            allPackageList.add(packageName);
                        }
                    }
                }

                for (AppInfo info : installAppList) {
                    if (allPackageList.contains(info.getAppPackagename())) {
                        if (!mRecentAppList.contains(info)) {
                            mRecentAppList.add(info);
                        }
                        if (mRecentAppList.size() >= MAX_RECENT_APP_NUM) {
                            break;
                        }
                    }
                }
            }
        }.start();
    }
}