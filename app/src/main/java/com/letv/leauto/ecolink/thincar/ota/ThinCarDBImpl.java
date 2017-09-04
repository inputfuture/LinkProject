package com.letv.leauto.ecolink.thincar.ota;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.leauto.link.lightcar.LogUtils;
import com.letv.leauto.ecolink.database.manager.DetailSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */
public class ThinCarDBImpl implements ThincarDBDao {

    private ThinCarDBHelper mDBHelper;


    private ThinCarDBImpl(Context context) {
        mDBHelper = ThinCarDBHelper.getInstance(context);
    }

    private static ThinCarDBImpl thincarDB = null;

    public static ThinCarDBImpl getInstance(Context context) {
        if (thincarDB == null) {
            synchronized (ThinCarDBImpl.class) {
                if (thincarDB == null) {
                    thincarDB = new ThinCarDBImpl(context);
                }
            }
        }
        return thincarDB;
    }

    @Override
    public synchronized void insertOtaEntity(OtaEntity entity) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ThinCarDBHelper._CARMAC, entity.getCarMac());
        cv.put(ThinCarDBHelper._CARVERSION, entity.getCarVersion());
        cv.put(ThinCarDBHelper._CARMODLE, entity.getCarModle());
        cv.put(ThinCarDBHelper._DOWNSTATUS, entity.getDownStatus());
        cv.put(ThinCarDBHelper._DOWNURL, entity.downUrl);
        cv.put(ThinCarDBHelper._FILENAME, entity.getFileName());
        cv.put(ThinCarDBHelper._FILEPATH, entity.getFilePath());
        cv.put(ThinCarDBHelper._MD5, entity.getMd5());
        cv.put(ThinCarDBHelper._UNZIPSTATUS, entity.getUnzipStatus());
        cv.put(ThinCarDBHelper._PKGZISE,entity.getPkgSize());
        cv.put(ThinCarDBHelper._MESSAGE,entity.getMessage());
        cv.put(ThinCarDBHelper._RELEASETIME,entity.getTime());
        cv.put(ThinCarDBHelper._PROGRESS,entity.getProgress());
        long id = db.insert(ThinCarDBHelper.DB_TABLE, null, cv);

        if (id == -1) {
            LogUtils.d("TAG", "--->insert to DB fail");
        } else {
            LogUtils.d("TAG", "--->insert to DB ok");
        }

        db.close();

    }

    @Override
    public synchronized void deleteOtaEntity(String version) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String str = "delete form " + ThinCarDBHelper.DB_TABLE + " where " + ThinCarDBHelper._CARVERSION + "= ?";
        db.execSQL(str, new Object[]{version});
        db.close();

    }

    @Override
    public synchronized void updataOtaEntity(OtaEntity entity) {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ThinCarDBHelper._CARVERSION, entity.getCarVersion());
        cv.put(ThinCarDBHelper._CARMODLE, entity.getCarModle());
        cv.put(ThinCarDBHelper._DOWNSTATUS, entity.getDownStatus());
        cv.put(ThinCarDBHelper._DOWNURL, entity.downUrl);
        cv.put(ThinCarDBHelper._FILENAME, entity.getFileName());
        cv.put(ThinCarDBHelper._FILEPATH, entity.getFilePath());
        cv.put(ThinCarDBHelper._MD5, entity.getMd5());
        cv.put(ThinCarDBHelper._UNZIPSTATUS, entity.getUnzipStatus());
        cv.put(ThinCarDBHelper._PKGZISE,entity.getPkgSize());
        cv.put(ThinCarDBHelper._MESSAGE,entity.getMessage());
        cv.put(ThinCarDBHelper._RELEASETIME,entity.getTime());
        cv.put(ThinCarDBHelper._PROGRESS,entity.getProgress());
        db.update(ThinCarDBHelper.DB_TABLE, cv, ThinCarDBHelper._CARVERSION + " = ?", new String[]{entity.getCarVersion()});
        db.close();

    }

    @Override
    public List<OtaEntity> getOtaEntityFromDB() {

        List<OtaEntity> entities = new ArrayList<>();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.query(ThinCarDBHelper.DB_TABLE, null, null, null, null, null, ThinCarDBHelper.ID + "  desc");
        while (cursor.moveToNext()) {
            OtaEntity entity = new OtaEntity();
            entity.setCarMac(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._CARMAC)));
            entity.setCarModle(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._CARMODLE)));
            entity.setCarVersion(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._CARVERSION)));
            entity.setDownStatus(cursor.getInt(cursor.getColumnIndex(ThinCarDBHelper._DOWNSTATUS)));
            entity.setDownUrl(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._DOWNURL)));
            entity.setFileName(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._FILENAME)));
            entity.setFilePath(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._FILEPATH)));
            entity.setMd5(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._MD5)));
            entity.setUnzipStatus(cursor.getInt(cursor.getColumnIndex(ThinCarDBHelper._UNZIPSTATUS)));
            entity.setPkgSize(cursor.getInt(cursor.getColumnIndex(ThinCarDBHelper._PKGZISE)));
            entity.setMessage(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._MESSAGE)));
            entity.setProgress(cursor.getInt(cursor.getColumnIndex(ThinCarDBHelper._PROGRESS)));
            entity.setTime(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._RELEASETIME)));
            entities.add(entity);
        }
        db.close();
        cursor.close();
        return entities;
    }

    @Override
    public List<OtaEntity> isExists(String versionCode) {
        List<OtaEntity> entities = new ArrayList<>();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.query(ThinCarDBHelper.DB_TABLE,null, ThinCarDBHelper._CARVERSION + " =?", new String[]{versionCode},null,null,ThinCarDBHelper.ID + "  desc");
        if (cursor == null || cursor.getCount() <= 0) {
            return entities;
        }
        while (cursor.moveToNext()){
            OtaEntity entity = new OtaEntity();
            entity.setCarMac(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._CARMAC)));
            entity.setCarModle(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._CARMODLE)));
            entity.setCarVersion(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._CARVERSION)));
            entity.setDownStatus(cursor.getInt(cursor.getColumnIndex(ThinCarDBHelper._DOWNSTATUS)));
            entity.setDownUrl(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._DOWNURL)));
            entity.setFileName(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._FILENAME)));
            entity.setFilePath(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._FILEPATH)));
            entity.setMd5(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._MD5)));
            entity.setUnzipStatus(cursor.getInt(cursor.getColumnIndex(ThinCarDBHelper._UNZIPSTATUS)));
            entity.setPkgSize(cursor.getInt(cursor.getColumnIndex(ThinCarDBHelper._PKGZISE)));
            entity.setMessage(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._MESSAGE)));
            entity.setProgress(cursor.getInt(cursor.getColumnIndex(ThinCarDBHelper._PROGRESS)));
            entity.setTime(cursor.getString(cursor.getColumnIndex(ThinCarDBHelper._RELEASETIME)));
            entities.add(entity);
        }

        db.close();
        cursor.close();
        return entities;
    }
}
