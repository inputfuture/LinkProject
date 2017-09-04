package com.letv.leauto.ecolink.database.manager;

/**
 * Created by liweiwei1 on 2015/7/15.
 */
public class ChannelInfoSchema {
    public final static String TABLE_NAME = "channels";

    public final static String ID = "id";
    public final static String PAGE_ID = "pageid";
    public final static String NAME = "name";
    public final static String TYPE = "type";
    public final static String DATA_URL = "dataUrl";
    public final static String CMS_ID = "cmsID";
    public final static String SKIP_ID = "skipType";
    public final static String MZ_CID = "mzcId";

    public final static String createsql = "CREATE TABLE "  + "IF NOT EXISTS   "+TABLE_NAME + " ("
            + ID + " INTEGER primary key" + ", "
            + NAME + " TEXT" + ", "
            + PAGE_ID + " TEXT" + ", "
            + DATA_URL + " TEXT" + ", "
            + CMS_ID + " TEXT" + ", "
            + SKIP_ID + " TEXT" + ", "
            + MZ_CID + " TEXT" + ", "

            + TYPE + " TEXT" + ");";
    public final static String dropsql = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
