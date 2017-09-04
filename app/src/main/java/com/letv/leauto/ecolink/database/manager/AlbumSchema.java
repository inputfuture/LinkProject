package com.letv.leauto.ecolink.database.manager;

/**
 * Created by liweiwei1 on 2015/7/15.
 */
public class AlbumSchema {
    public final static String TABLE_NAME = "media_album";

    public final static String ID = "id";
    public final static String SOURCE_CP_ID = "source_cp_id";
    public final static String SRC_IMG_URL = "src_img_url";
    public final static String AUTHER = "auther";
    public final static String NAME = "name";
    public final static String IMG_URL = "img_url";
    public final static String CREATE_TIME = "create_time";
    public final static String DESCRIPTION = "description";
    public final static String SORT_ID = "sort_id";
    public final static String UPDATE_TIME = "update_time";
    public final static String ICON_URL = "icon_url";
    public final static String PLAYCOUNT = "playcount";
    public final static String ALBUM_TYPE_ID = "album_type_id";
    public final static String TYPE = "type";
    public final static String CREATE_USER = "create_user";
    public final static String ALBUM_ID = "album_id";
    public final static String DISPLAY_NAME = "display_name";
    public final static  String DISPLAY_SOURCE_URL = "display_source_url";
    public final static  String DISPLAY_LE_SOURCE_VID = "display_le_source_vid";
    public final static  String DISPLAY_LE_SOURCE_MID = "display_le_source_mid";
    public final static  String DISPLAY_ID = "display_id";
    public final static  String DISPLAY_IMG_URL = "display_img_url";
    public final static String CHANNEL_NAME="channel_name";

    //用于标记是否下载完成
    public final static String DOWNLOAD_FLAG = "download_flag";

    public final static String createsql = "CREATE TABLE " + TABLE_NAME + " ("
            + ID + " INTEGER primary key" + ", "
            + SOURCE_CP_ID + " TEXT" + ", " + SRC_IMG_URL + " TEXT" + ", "
            + AUTHER + " TEXT" + ", "
            + NAME + " TEXT" + ", "+ DISPLAY_NAME + " TEXT" + ", "
            + DISPLAY_SOURCE_URL + " TEXT" + ", "+ DISPLAY_IMG_URL + " TEXT" + ", "
            + DISPLAY_LE_SOURCE_MID + " TEXT" + ", "+ DISPLAY_LE_SOURCE_VID + " TEXT" + ", "
            + DISPLAY_ID + " TEXT" + ", "
            + IMG_URL + " TEXT" + ", " + CREATE_TIME + " TEXT" + ", "
            + DESCRIPTION + " TEXT" + ", " + SORT_ID + " TEXT" + ", "
            + UPDATE_TIME + " TEXT" + ", "
            + ICON_URL + " TEXT" + ", " + PLAYCOUNT + " TEXT" + ", "
            + TYPE + " TEXT" + ", "
            + ALBUM_TYPE_ID + " TEXT" + ", " + CREATE_USER + " TEXT " + ", "
            + ALBUM_ID + " TEXT" + ", "
            + CHANNEL_NAME + " TEXT" + ", "
            + DOWNLOAD_FLAG + " TEXT" + ");";

    public final static String dropsql = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public final static String addcolumnsql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + DISPLAY_NAME + " TEXT";

}
