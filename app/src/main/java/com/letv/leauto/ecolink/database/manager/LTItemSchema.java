package com.letv.leauto.ecolink.database.manager;

/**
 * Created by why on 2016/7/27.
 */
public class LTItemSchema {
    public final static String TABLE_NAME = "song_item";

    public final static String ID = "id";
    public final static String URl = "url";
    public final static String MID = "mid";
    public final static String VID = "vid";
    public final static String AUIDOID="audioid";

    public final static String CPID = "cpid";
    public final static String TITLE = "title";

    public final static String PROGRESS = "progress";
    public final static String DUARATION = "duration";
    public final static String FINISH="finish";


    public final static String createsql = "CREATE TABLE " + "IF NOT EXISTS   "+TABLE_NAME + " ("
            + ID + " INTEGER primary key" + ", "
            + URl + " TEXT" + ", "
            +AUIDOID+" TEXT" + ", "
            + MID + " TEXT" + ", "
            + VID + " TEXT" + ", "
            + CPID + " TEXT" + ", "
            + TITLE + " TEXT" + ", "
            + PROGRESS + " TEXT"+ ", "
            +DUARATION + " TEXT" +", "
            + FINISH + " TEXT"+");";
    public final static String dropsql = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
