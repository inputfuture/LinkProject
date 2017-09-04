package com.letv.leauto.ecolink.database.manager;

/**
 * Created by why on 2017/3/9.
 */

public class PoiHistoryScheme {
    public static final String TABLE_NAME = "poi_history";

    public final static String ID = "id";
    public static final String LAT= "lat";
    public static final String LON = "lon";
    public static final String ADDRESS_NAME = "address";
    public static final String DISTRICT = "district";
    public static final String DISTANCE="distance";
    public static final String TYPE = "type";


    public final static String createsql = "CREATE TABLE " +  "IF NOT EXISTS   "+TABLE_NAME + " ("
            + ID + " INTEGER primary key" + ", "
            + LAT + " TEXT" + ", "
            + LON + " TEXT" + ", "
            + ADDRESS_NAME + " TEXT" + ", "
            + DISTRICT + " TEXT" + ", "
            + DISTANCE + " TEXT"  + ", "
            + TYPE +"  TEXT"+");";

    public final static String dropsql = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public final static String addTye="ALTER TABLE   "+TABLE_NAME+" ADD "+ TYPE +" TEXT DEFAULT '1'";

}
