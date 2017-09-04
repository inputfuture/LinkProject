package com.letv.leauto.ecolink.thincar.ota;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/30.
 */
public class ThinCarDBHelper extends SQLiteOpenHelper{
    public static final String ID="id";

    private  static final String THINCARDB_NAME="thincar.db";
    private static final int THINCARDB_VERSION=1;
    public static final String DB_TABLE="ota_info";
    public static final String _CARMAC="carMac";
    public static final String _CARVERSION="carVersion";
    public static final String _CARMODLE="carModle";
    public static final String _DOWNSTATUS="downStatus";
    public static final String _UNZIPSTATUS="unzipStatus";
    public static final String _MD5="md5";
    public static final String _FILEPATH="filePath";
    public static final String _FILENAME="fileName";
    public static final String _DOWNURL="downUrl";
    public static final String _PKGZISE="pkgsize";
    public static final String _RELEASETIME="release_time";
    public static final String _PROGRESS="progress";
    public static final String _MESSAGE="message";


    private String CREATE_DL_TABLE="create table "+DB_TABLE +
            "("+ID+"  integer primary key,"
            +_CARVERSION +" varchar(256), " +
            _CARMAC +" varchar(128), "+
            _CARMODLE +" varchar(64), "+
            _DOWNSTATUS+" integer, "+
            _UNZIPSTATUS+" integer, "+
            _PKGZISE+" integer, "+
            _DOWNURL+" varchar(256), "+
            _FILENAME+" varchar(128), "+
            _FILEPATH+" varchar(128), "+
            _MD5+" varchar(64)," +
            _RELEASETIME+" text,"+
            _PROGRESS+" integer,"+
            _MESSAGE+" text"+
            ")";

    private  static ThinCarDBHelper dbHelper=null;

    public static ThinCarDBHelper getInstance(Context context){
        if(dbHelper==null){
            synchronized (ThinCarDBHelper.class){
                if(dbHelper==null){
                    dbHelper=new ThinCarDBHelper(context.getApplicationContext());
                }
            }
        }
        return dbHelper;
    }



    private ThinCarDBHelper(Context context) {
        super(context, THINCARDB_NAME, null, THINCARDB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
