package com.letv.leauto.ecolink.database.manager;


public class LTRecentItemSchema {
    public final static String TABLE_NAME = "recent_item";

    public final static String ID = "id";
    public final static String URl = "url";
    public final static String XMID = "xmid";
    public final static String AUDID = "audioid";
    public final static String CPNAME = "cpname";
    public final static String TYPE = "type";
    public final static String SOURCE = "source";
    public final static String TITLE = "title";
    public final static String PROGRESS = "progress";
    public final static String DUARATION = "duration";
    public final static String PLAYTIME = "playtime";
    public final static String AUTHOR = "author";
    public final static String IMG_URL = "imageUrl";
    public final static String MID = "mid";
    public final static String VID = "vid";
    public final static String CPID = "cpid";


    public final static String createsql = "CREATE TABLE " + "IF NOT EXISTS   "+TABLE_NAME + " ("
            + ID + " INTEGER primary key" + ", "
            + URl + " TEXT" + ", "
            + SOURCE +" TEXT" + ", "
            + TYPE +" TEXT" + ", "
            + AUDID + " TEXT" + ", "
            + XMID + " TEXT" + ", "
            + CPNAME + " TEXT" + ", "
            + TITLE + " TEXT" + ", "
            + PROGRESS + " TEXT"+ ", "
            +DUARATION + " TEXT" +", "
            +AUTHOR + " TEXT" +", "
            +IMG_URL + " TEXT" +", "
            +MID + " TEXT" +", "
            +VID + " TEXT" +", "
            + CPID + " TEXT" + ", "
            + PLAYTIME + " TEXT"+");";
    public final static String dropsql = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
