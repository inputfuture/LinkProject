package com.letv.leauto.ecolink.lemap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.database.manager.PoiHistoryScheme;
import com.letv.leauto.ecolink.lemap.entity.SearchPoi;
import com.letv.leauto.ecolink.utils.CacheUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by why on 2017/3/9.
 */

public class PoiHistoryManager {
    private static PoiHistoryManager instance;
    private SQLiteDatabase db;
    //数据库中存放的app

    private Context mContext;

    private PoiHistoryManager(Context context) {
        if (this.db == null) {
            this.db = EcoApplication.getModeDb(1);
            mContext=context.getApplicationContext();
        saveOldData();
        }
    }


    public static PoiHistoryManager getInstance(Context context) {
        if (null == instance) {
            synchronized (PoiHistoryManager.class) {
                if (instance == null) {
                    instance = new PoiHistoryManager(context);
                }
            }

        }
        return instance;
    }



    public void saveSearchPoi(SearchPoi searchPoi) {
        String where=PoiHistoryScheme.ADDRESS_NAME +"=?";
        String[] strings={searchPoi.getAddrname()};
        db.delete(PoiHistoryScheme.TABLE_NAME,where,strings);
        ContentValues contentValues = new ContentValues();
        contentValues.put(PoiHistoryScheme.LAT, searchPoi.getLatitude());
        contentValues.put(PoiHistoryScheme.LON, searchPoi.getLongitude());
        contentValues.put(PoiHistoryScheme.ADDRESS_NAME, searchPoi.getAddrname());
        contentValues.put(PoiHistoryScheme.DISTRICT,searchPoi.getDistrict());
        contentValues.put(PoiHistoryScheme.TYPE,searchPoi.getType());
        db.insert(PoiHistoryScheme.TABLE_NAME, null, contentValues);
        deleteOldByLimit();
    }

    public List<SearchPoi> getHistoryPois() {
        List<SearchPoi> historyPois=new ArrayList<>();
        Cursor cursor = db.query(PoiHistoryScheme.TABLE_NAME, null, null,
                null, null, null, PoiHistoryScheme.ID + "  desc");

        while (cursor.moveToNext()) {
            SearchPoi searchPoi=new SearchPoi(cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.LON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.ADDRESS_NAME)));
            searchPoi.setDistrict(cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.DISTRICT)));
         String type=cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.TYPE));
            if (type!=null){
                searchPoi.setType(type);
            }else{
                searchPoi.setType(SearchPoi.NAVI);
            }

            historyPois.add(searchPoi);

        }
        cursor.close();
        return historyPois;

    }

    public void deleteAllHistory(){
        db.delete(PoiHistoryScheme.TABLE_NAME,null,null);
    }

    /**
     * 如果软件从3.2以下升级到3.2，存储以前的数据
     */
    public void saveOldData() {
        String result = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HISTORY_SEARCHKEY, null);
        if (result!=null){
            String[] array = result.split(";");
            for (String item:array) {
                String[] items = item.split(",");
                ContentValues contentValues = new ContentValues();
                contentValues.put(PoiHistoryScheme.LAT, items[1]);
                contentValues.put(PoiHistoryScheme.LON, items[2]);
                contentValues.put(PoiHistoryScheme.ADDRESS_NAME, items[0]);

                db.insert(PoiHistoryScheme.TABLE_NAME, null, contentValues);
            }
            CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HISTORY_SEARCHKEY,null);
            deleteOldByLimit();
        }


    }

    /**
     * 删除老的数据，最多存储 15条
     */
    public void deleteOldByLimit(){
        String deleteSql="delete from poi_history where (select count(id) from poi_history)> 15 and id in (select id from poi_history order by id desc limit (select count(id) from poi_history) offset 15 )";
        db.execSQL(deleteSql);
    }

    public  List<SearchPoi> getHistoryPoisSType() {
        List<SearchPoi> historyPois=new ArrayList<>();
        String where=PoiHistoryScheme.TYPE +"=?";
        String[] strings={SearchPoi.NAVI};
        Cursor cursor = db.query(PoiHistoryScheme.TABLE_NAME, null, where,
                strings, null, null, PoiHistoryScheme.ID + "  desc");

        while (cursor.moveToNext()) {
            SearchPoi searchPoi=new SearchPoi(cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.LON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.ADDRESS_NAME)));
            searchPoi.setDistrict(cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.DISTRICT)));
            String type=cursor.getString(cursor.getColumnIndexOrThrow(PoiHistoryScheme.TYPE));
            if (type!=null){
                searchPoi.setType(type);
            }else{
                searchPoi.setType(SearchPoi.NAVI);
            }

            historyPois.add(searchPoi);

        }
        cursor.close();
        return historyPois;

    }
}
