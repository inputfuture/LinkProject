package com.letv.leauto.ecolink.database.manager;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/9/7.
 */
public class ChoosedAppColumns{
    public static final String TABLE_NAME = "choosed_app";

    public final static String ID = "id";
    public static final String APP_ICON = "app_icon";
    public static final String APP_NAME = "app_name";
    public static final String APP_PACKAGE_NAME = "package_name";
    public static final String ACTIVITY_NAME = "activity_name";
    public static final String TYEP="type";
    public static final String STATUS="status";
    public static final String APK_URL="apl_url";
    public static final String IMG_URL="img_url";
    public static final String TOTAL_SIZE="total_size";
    public static final String PROGRESS="progress";


    public final static String createsql = "CREATE TABLE " +  "IF NOT EXISTS   "+TABLE_NAME + " ("
            + ID + " INTEGER primary key" + ", "
            + APP_ICON + " TEXT" + ", "
            + APP_NAME + " TEXT" + ", "
            + APP_PACKAGE_NAME + " TEXT" + ", "
            + ACTIVITY_NAME + " TEXT"  + ");";

    public final static String dropsql = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
