package com.letv.leauto.ecolink.database.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.letv.leauto.ecolink.utils.DatabaseContext;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.leauto.ecolink.utils.Utils;


/**
 * Created by liweiwei1 on 2015/7/13.
 */
public class LeDBHelper extends SQLiteOpenHelper {
    private static final String OLD_DBNAME = "leauto_unity.db";
    private static final String DBNAME = DeviceUtils.getMusicCachePath()+"leauto_unity.db";
    private static final int VERSION = 4;//  3.2添加一个表，数据库版本升级到 3
    static Context mContext;
    private  static LeDBHelper mDatabase;

    public static LeDBHelper getInstance(Context context) {
        //Utils.copyFile(context.getDatabasePath(OLD_DBNAME).getAbsolutePath(),DBNAME);
        if(mDatabase == null){
            int sysVersion = Integer.parseInt(DeviceUtils.getSystemSDKVersion());
            Trace.Debug("####  LeDBHelper", "sysVersion=" + sysVersion);

            if(sysVersion>= Build.VERSION_CODES.KITKAT_WATCH) {
//                boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(
//                        android.os.Environment.getExternalStorageState());
//                if (sdExist){
//                    mDatabase = new LeDBHelper(context, DBNAME);
//                }else{
                    mDatabase = new LeDBHelper(context, OLD_DBNAME);
//                }
            }else{

                mDatabase = new LeDBHelper(context, OLD_DBNAME);
            }
        }
        return mDatabase;
    }

    private LeDBHelper(Context context,String dataBaseName) {
        super(context, dataBaseName, null, VERSION);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Trace.Debug("####  database create");
        db.execSQL(DetailSchema.createsql);
        db.execSQL(AlbumSchema.createsql);
        db.execSQL(LTItemSchema.createsql);
        db.execSQL(ChannelInfoSchema.createsql);
        db.execSQL(ChoosedAppColumns.createsql);
        db.execSQL(LTRecentItemSchema.createsql);
        db.execSQL(PoiHistoryScheme.createsql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Trace.Info( "#### onUpgrade oldVersion="+oldVersion+"  newversion="+newVersion);
        if (oldVersion == 0 && newVersion == 0) {
            db.execSQL(DetailSchema.dropsql);
            onCreate(db);
        }

        if (oldVersion == 1) {
            db.execSQL(LTRecentItemSchema.createsql);
            db.execSQL(ChannelInfoSchema.createsql);
        }
//数据库从低版本升级到3的时候，添加了导航历史记录数据表，如果以前有历史记录存入xml中，转移到数据表中
        if (oldVersion < 3 ) {
            db.execSQL(PoiHistoryScheme.createsql);

        }else if (oldVersion==3){
            db.execSQL(PoiHistoryScheme.addTye);
        }
    }

}
