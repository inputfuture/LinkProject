package com.letv.leauto.ecolink.database.model;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by Administrator on 2016/9/6.
 */
public class AppInfo {
    private Drawable appIcon;
    private String appName;
    private String appPackagename;
    private String activityName;
    private boolean couldDelete;
    private int type;
    private int status;
    private String  apkUrl;
    private String imageUrl;
    private double progress;
    private double totalSize;

    public AppInfo(Drawable icon, String label, String packageName, String activityName, boolean couldDelete) {
        this.appIcon = icon;
        this.appName = label;
        this.appPackagename = packageName;
        this.activityName = activityName;
        this.couldDelete = couldDelete;

    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getAppPackagename() {
        return this.appPackagename;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public boolean getCouldDelete() {
        return this.couldDelete;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppPackagename(String appPackagename) {
        this.appPackagename = appPackagename;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public boolean isCouldDelete() {
        return couldDelete;
    }

    public void setCouldDelete(boolean couldDelete) {
        this.couldDelete = couldDelete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppInfo appInfo = (AppInfo) o;

        return appPackagename != null ? appPackagename.equals(appInfo.appPackagename) : appInfo.appPackagename == null;

    }

    @Override
    public int hashCode() {
        return appPackagename != null ? appPackagename.hashCode() : 0;
    }


    public void recycle(){
        if (appIcon != null) {
            appIcon.setCallback(null);
            Trace.Debug("消除icon引用");
        }
    }
}