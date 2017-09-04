package com.letv.leauto.ecolink.database.manager;

/**
 * Created by liweiwei1 on 2015/7/14.
 */
public class DetailSchema {
    public final static String TABLE_NAME = "media_detail";

    public final static String ID = "id";
    public final static String AUTHOR = "author";
    public final static String SOURCE_URL = "source_url";
    public final static String NAME = "name";
    public final static String IMG_URL = "img_url";
    public final static String CREATE_TIME = "create_time";
    public final static String SOURCE_CP_ID = "source_cp_id";
    public final static String LE_SOURCE_MID = "le_source_mid";
    public final static String LE_SOURCE_VID = "le_source_vid";
    public final static String AUDIO_ID = "audio_id";
    public final static String CREATE_USER = "create_user";
    public final static String UPDATE_USER = "update_user";
    public final static String ALBUM_ID = "album_id";
    //用于标记是否下载完成
    public final static String DOWNLOAD_FLAG = "download_flag";
    public final static String DOWNLOAD_ID = "download_id";
    public final static String DOWNLOAD_TIME = "download_time";

    public final static String TYPE = "type";
    public final static String CHANNEL_NAME = "channel_name";
    //  public final static String MEDIA_ID = "media_id";

    // public final static String IS_TOP = "is_top";
    // public final static String TIME = "time";
    // public final static String LOCAL_URL = "local_url";
    //public final static String SOURCE_ICON_URL = "source_icon_url";
    // public final static String UPDATE_TIME = "update_time";
    // public final static String STATUS = "status";
    // public final static String ICON_URL = "icon_url";

    //    public final static String LE_SOURCE_ID = "le_source_id";
    // public final static String PLAYCOUNT = "playcount";
    // public final static String SOURCE_IMG_URL = "source_img_url";

    //public final static String IS_STAR = "is_star";
    //  public final static String SIZE = "size";
    //  public final static String DURATION = "duration";



    public final static String createsql = "CREATE TABLE " + TABLE_NAME + " ("
            + ID + " INTEGER primary key" + ", "
            + AUTHOR + " TEXT" + ", " + SOURCE_URL + " TEXT" + ", "
            + NAME + " TEXT" + ", "
            + IMG_URL + " TEXT" + ", " + CREATE_TIME + " TEXT" + ", "
            + SOURCE_CP_ID + " TEXT" + ", "
            + LE_SOURCE_MID + " TEXT" + ", " + LE_SOURCE_VID + " TEXT" + ", "
            + AUDIO_ID + " TEXT" + ", "
            + CHANNEL_NAME + " TEXT" + ", "
            + CREATE_USER + " TEXT" + ", "
            + ALBUM_ID + " TEXT" + ", " + UPDATE_USER + " TEXT" + ", "
            + DOWNLOAD_FLAG + " TEXT" + ", " + TYPE + " TEXT" + ", "
            + DOWNLOAD_TIME + " TEXT" + ", "
            + DOWNLOAD_ID + " TEXT" + ");";

    public final static String dropsql = "DROP TABLE IF EXISTS " + TABLE_NAME;
}