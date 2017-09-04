package com.letv.leauto.ecolink.manager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.manager.ChoosedAppColumns;
import com.letv.leauto.ecolink.database.model.AppInfo;
import com.letv.leauto.ecolink.utils.PackageUtil;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.utils.Trace;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/9/7.
 */
public class ChoosedAppManager {
    private static ChoosedAppManager instance;
    private SQLiteDatabase db;
    //数据库中存放的app

    private PackageManager pm;
    private Context mContext;
    List<AppInfo> mSavedAppInfos=new ArrayList<>();

    private ChoosedAppManager(Context context) {
        pm = context.getPackageManager();
        if (this.db == null) {
            this.db = EcoApplication.getModeDb(1);
            mContext=context.getApplicationContext();
        }
    }


    public static ChoosedAppManager getInstance(Context context) {
        if (null == instance) {
            synchronized (ChoosedAppManager.class) {
                if (instance == null) {
                    instance = new ChoosedAppManager(context);
                }
            }

        }
        return instance;
    }



    public void saveChoosedAppToDB(final List<AppInfo> appInfos) {
        new Thread(){
            @Override
            public void run() {
                db.beginTransaction();
                for (AppInfo info : appInfos) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ChoosedAppColumns.APP_ICON, convertDrawableToByte(info.getAppIcon()));
                    contentValues.put(ChoosedAppColumns.APP_NAME, info.getAppName());
                    contentValues.put(ChoosedAppColumns.APP_PACKAGE_NAME, info.getAppPackagename());
                    contentValues.put(ChoosedAppColumns.ACTIVITY_NAME, info.getActivityName());
                    db.insert(ChoosedAppColumns.TABLE_NAME, null, contentValues);
                }
                db.setTransactionSuccessful();
                db.endTransaction();

            }
        }.start();

    }


    /**
     * @param isFromThincar 是不是瘦车机需要获取app数据
     * @return
     */
    public List<AppInfo> getSavedApps(boolean isFromThincar) {
        List<AppInfo> appInfos = new ArrayList<>();
        /** 瘦车机连接情况下，不显示这些应用*/
        if (GlobalCfg.IS_POTRAIT) {
            if (!isFromThincar) {
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_map), mContext.getString(R.string.menu_map), Constant.HomeMenu.NAVI, Constant.TAG_MAP, false));
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_leradio), mContext.getString(R.string.menu_leradio), Constant.HomeMenu.LERAIDO, Constant.TAG_LERADIO, false));
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_localmusic), mContext.getString(R.string.menu_local_music), Constant.HomeMenu.LOCAL_MUCIC, Constant.TAG_LOCAL, false));
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_set), mContext.getString(R.string.menu_set), Constant.HomeMenu.SET, Constant.TAG_SETTING, false));
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_phone), mContext.getString(R.string.menu_phone), Constant.HomeMenu.PHONE, Constant.TAG_CALL, false));
            }

//                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.qq_music_icon),mContext.getString(R.string.menu_qplay),Constant.HomeMenu.QPLAY,null,false));
            appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.lecs_default_car), mContext.getString(R.string.menu_favorcar), Constant.HomeMenu.FAVORCAR, Constant.TAG_FAVOR_CAR, false));
            if (PackageUtil.ApkIsInstall(mContext, Constant.HomeMenu.WECHAT)) {
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_wechat), mContext.getString(R.string.menu_wechat), Constant.HomeMenu.WECHAT, Constant.TAG_WEIXIN, false));
            }
            if (isFromThincar)  {
                if (PackageUtil.ApkIsInstall(mContext, Constant.HomeMenu.LEVEDIO)) {
                    appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_levideo), mContext.getString(R.string.menu_levedio), Constant.HomeMenu.LEVEDIO, Constant.TAG_LE_VIDEO, false));
                }
            } else {
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_levideo), mContext.getString(R.string.menu_levedio), Constant.HomeMenu.LEVEDIO, Constant.TAG_LE_VIDEO, false));
            }

            if (PackageUtil.ApkIsInstall(mContext, Constant.HomeMenu.GAODE)) {
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_gaode), mContext.getString(R.string.menu_gaode), Constant.HomeMenu.GAODE, Constant.TAG_GAODE_MAP, false));
            }
            if (PackageUtil.ApkIsInstall(mContext, Constant.HomeMenu.BAIDU)) {
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_baidu), mContext.getString(R.string.menu_baidu), Constant.HomeMenu.BAIDU, Constant.TAG_BAIDU_MAP, false));
            }


        } else {
            if (!isFromThincar) {
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_map), mContext.getString(R.string.menu_map), Constant.HomeMenu.NAVI, Constant.TAG_MAP, false));
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_leradio), mContext.getString(R.string.menu_leradio), Constant.HomeMenu.LERAIDO, Constant.TAG_LERADIO, false));
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_localmusic), mContext.getString(R.string.menu_local_music), Constant.HomeMenu.LOCAL_MUCIC, Constant.TAG_LOCAL, false));
//                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_live), mContext.getString(R.string.menu_live), Constant.HomeMenu.LIVE, Constant.TAG_LE_VIDEO, false));
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_phone), mContext.getString(R.string.menu_phone), Constant.HomeMenu.PHONE, Constant.TAG_CALL, false));
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_set), mContext.getString(R.string.menu_set), Constant.HomeMenu.SET, Constant.TAG_SETTING, false));
            }

//                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.qq_music_icon),mContext.getString(R.string.menu_qplay),Constant.HomeMenu.QPLAY,null,false));
            appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.lecs_default_car), mContext.getString(R.string.menu_favorcar), Constant.HomeMenu.FAVORCAR, Constant.TAG_FAVOR_CAR, false));
            if (PackageUtil.ApkIsInstall(mContext, Constant.HomeMenu.WECHAT)) {
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_wechat), mContext.getString(R.string.menu_wechat), Constant.HomeMenu.WECHAT, Constant.TAG_WEIXIN, false));
            }
            if (PackageUtil.ApkIsInstall(mContext, Constant.HomeMenu.GAODE)) {
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_gaode), mContext.getString(R.string.menu_gaode), Constant.HomeMenu.GAODE, Constant.TAG_GAODE_MAP, false));
            }
            if (PackageUtil.ApkIsInstall(mContext, Constant.HomeMenu.BAIDU)) {
                appInfos.add(new AppInfo(mContext.getResources().getDrawable(R.mipmap.menu_icon_baidu), mContext.getString(R.string.menu_baidu), Constant.HomeMenu.BAIDU, Constant.TAG_BAIDU_MAP, false));
            }
        }

        Cursor cursor = null;
        try {
            cursor = db.query(ChoosedAppColumns.TABLE_NAME, null, null,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                appInfos.add(new AppInfo(
                        convertByteToDrawable(cursor.getBlob(cursor.getColumnIndexOrThrow(ChoosedAppColumns.APP_ICON)))
                        ,cursor.getString(cursor.getColumnIndexOrThrow(ChoosedAppColumns.APP_NAME))
                        ,cursor.getString(cursor.getColumnIndexOrThrow(ChoosedAppColumns.APP_PACKAGE_NAME))
                        ,cursor.getString(cursor.getColumnIndexOrThrow(ChoosedAppColumns.ACTIVITY_NAME))
                        ,true) );
            }
        } catch (SQLiteException exception) {
            exception.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return appInfos;

    }

    //获取手机中安装的所有应用

    public List<AppInfo> getInstalledApp() {
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        if (resolveInfos != null) {
            for (ResolveInfo info : resolveInfos) {
                if ( !info.activityInfo.packageName.equals("com.autonavi.minimap")&&
                        !info.activityInfo.packageName.equals("com.baidu.BaiduMap")&&
                        !info.activityInfo.packageName.equals("com.tencent.mm")&&
                        !info.activityInfo.packageName.equals("com.letv.android.letvlive")&&
                        !info.activityInfo.packageName.equals("com.letv.android.client")&&
                        !info.activityInfo.packageName.equals("com.letv.leauto.ecolink"))
                    appInfoList.add(
                            new AppInfo(info.loadIcon(pm)
                                    ,info.loadLabel(pm).toString()
                                    ,info.activityInfo.packageName
                                    ,info.activityInfo.name
                                    ,true)

                    );
            }
        }
        return appInfoList;

    }


    public List<AppInfo> getShowAllAPP() {
        List<AppInfo> appInfos = new ArrayList<>();
        try {
            appInfos.addAll(getInstalledApp());
//            appInfos.removeAll(getSavedApps());
        } catch (Exception e) {
            Trace.Error("==getShowAllAPP", "e:" + e);
        }
        return appInfos;
    }


    public void deleteAppFromDB(AppInfo info) {
        db.delete(ChoosedAppColumns.TABLE_NAME, ChoosedAppColumns.APP_PACKAGE_NAME + "=?",
                new String[]{info.getAppPackagename()});
    }

    private byte[] convertDrawableToByte(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bs);

        return bs.toByteArray();
    }

    private Drawable convertByteToDrawable(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        Drawable drawable = bitmapDrawable;
        return drawable;
    }
}