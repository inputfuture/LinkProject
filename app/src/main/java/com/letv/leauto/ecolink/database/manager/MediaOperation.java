package com.letv.leauto.ecolink.database.manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.database.field.DownLoadState;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.LeSortInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.model.PlayItem;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.Trace;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liweiwei1 on 2015/7/13.
 */
public class MediaOperation {
    private static MediaOperation instance;
    private SQLiteDatabase db;

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static MediaOperation getInstance() {
        if (instance == null) {
            synchronized (MediaOperation.class) {
                if (instance == null) {
                    instance = new MediaOperation();
                }
            }
        }
        return instance;
    }

    public MediaOperation() {
        if (this.db == null) {
            this.db = EcoApplication.getModeDb(1);
        }
    }

    /**
     * 获取所有专辑
     *
     * @return
     */
    public ArrayList<LeAlbumInfo> getAlbumList(String albumType) {
        ArrayList<LeAlbumInfo> albumList = new ArrayList<LeAlbumInfo>();
        switch (albumType) {
            case SortType.SORT_LOCAL_NEW:

            case SortType.SORT_FAVOR:
            case SortType.SORT_RECENT:
                String selection = AlbumSchema.TYPE + " =? ";
                String[] selectionArgs = new String[]{albumType};
                Cursor cursor = db.query(AlbumSchema.TABLE_NAME, null, selection,
                        selectionArgs, null, null, null);
                LeAlbumInfo album;
                while (cursor.moveToNext()) {
                    album = new LeAlbumInfo();
                    album.SOURCE_CP_ID = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.SOURCE_CP_ID));
                    album.SRC_IMG_URL = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.SRC_IMG_URL));
                    album.NAME = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.NAME));
                    album.AUTHER = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.AUTHER));
                    album.IMG_URL = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.IMG_URL));
                    album.CREATE_TIME = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.CREATE_TIME));
                    album.DESCRIPTION = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.DESCRIPTION));
                    album.UPDATE_TIME = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.UPDATE_TIME));
                    album.PLAYCOUNT = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.PLAYCOUNT));
                    //标记专辑类型，如收藏，本地，最近，下载
                    album.TYPE = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.TYPE));
                    album.ALBUM_TYPE_ID = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.ALBUM_TYPE_ID));
                    album.CREATE_USER = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.CREATE_USER));
                    album.ALBUM_ID = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.ALBUM_ID));
                    album.DISPLAY_NAME = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.DISPLAY_NAME));
                    album.DISPLAY_LE_SOURCE_VID = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.DISPLAY_LE_SOURCE_VID));
                    album.DISPLAY_LE_SOURCE_MID = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.DISPLAY_LE_SOURCE_MID));
                    album.DISPLAY_ID = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.DISPLAY_ID));
                    album.DISPLAY_IMG_URL = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.DISPLAY_IMG_URL));
                    album.DISPLAY_SOURCE_URL = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.DISPLAY_SOURCE_URL));

                    album.DOWNLOAD_FLAG = cursor.getString(cursor
                            .getColumnIndexOrThrow(AlbumSchema.DOWNLOAD_FLAG));


                    albumList.add(album);
                }
                cursor.close();
                break;
            case SortType.SORT_LOCAL:
                ContentResolver contentResolver = EcoApplication.instance.getContentResolver();
                Cursor albumCursor = contentResolver.query(
                        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null,
                        MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
                LeAlbumInfo albumInfo;
                //添加全部音乐的专辑
                albumInfo = new LeAlbumInfo();
                albumInfo.NAME = "全部本地音频";
                albumInfo.ALBUM_ID = SortType.SORT_LOCAL;
                albumInfo.TYPE = SortType.SORT_LOCAL;
                albumInfo.SRC_IMG_URL = "";
                albumInfo.DISPLAY_NAME = "";
                //albumInfo.SOURCE_CP_ID = CpCfg.CP_LOCAL;
                int ddd = albumCursor.getCount();
                //将空列表过滤
                if (getMusicList(SortType.SORT_LOCAL, albumInfo.ALBUM_ID).size() > 0) {
                    MediaDetail firstMedia = getMusicList(SortType.SORT_LOCAL, albumInfo.ALBUM_ID).get(0);
                    albumInfo.DISPLAY_NAME = firstMedia.NAME;
                    albumInfo.DISPLAY_SOURCE_URL = firstMedia.SOURCE_URL;
                    albumList.add(albumInfo);
                }

                while (albumCursor.moveToNext()) {
                    String artist = albumCursor.getString((albumCursor
                            .getColumnIndex(MediaStore.Audio.Artists.ARTIST))); // 艺术家
                    albumInfo = new LeAlbumInfo();
                    albumInfo.NAME = artist;
                    albumInfo.ALBUM_ID = artist;
                    albumInfo.TYPE = SortType.SORT_LOCAL;
                    albumInfo.SRC_IMG_URL = "";
                    //albumInfo.SOURCE_CP_ID = CpCfg.CP_LOCAL;

                    //将空列表过滤
                    if (getMusicList(SortType.SORT_LOCAL, albumInfo.ALBUM_ID).size() > 0) {
                        MediaDetail firstMedia = getMusicList(SortType.SORT_LOCAL, albumInfo.ALBUM_ID).get(0);
                        albumInfo.DISPLAY_NAME = firstMedia.NAME;
                        albumInfo.DISPLAY_SOURCE_URL = firstMedia.SOURCE_URL;
                        albumList.add(albumInfo);
                    }
                }
                albumCursor.close();
                break;
            default:
                break;
        }

        return albumList;
    }


    /**
     * 清除本地数据库里面的列表
     *
     * @param timeLimit
     * @return
     */
    public long deleteDownloadMusicListByTime(long timeLimit) {
        ContentResolver contentResolver = EcoApplication.instance.getContentResolver();
        String selection = "";
        String[] selectionArgs;
        if (timeLimit == -1) {
            selection = DetailSchema.DOWNLOAD_FLAG + " =?  AND " + DetailSchema.TYPE + " =? ";
            //清除所有下载完成的数据
            selectionArgs = new String[]{DownLoadState.DOWNLOADED, SortType.SORT_DOWNLOAD};
        } else {
            selection = DetailSchema.DOWNLOAD_TIME + " <=?  AND " + DetailSchema.TYPE + " =? ";
            //只取下载完成的数据清除7天前的
            selectionArgs = new String[]{(Long.toString(System.currentTimeMillis() -
                    timeLimit)), SortType.SORT_DOWNLOAD};
        }
        Cursor detailCursor = db.query(DetailSchema.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);
        String mSdcardRootPath = DeviceUtils.getMusicCachePath();
        while (detailCursor.moveToNext()) {
            String name = detailCursor.getString(detailCursor
                    .getColumnIndexOrThrow(DetailSchema.NAME));
            File file = new File(mSdcardRootPath + name);
            if (!file.exists()) {
                continue;
            }
            boolean isdelete = file.delete();
            Trace.Error("====s=", contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Audio.Media.DISPLAY_NAME + "=?", new String[]{file.getName()}) + "");
        }
        detailCursor.close();
        return db.delete(DetailSchema.TABLE_NAME, selection, selectionArgs);

    }

    public ArrayList<MediaDetail> getAllDownloads() {
        ArrayList<MediaDetail> argList = new ArrayList<>();
        String selection = DetailSchema.TYPE + " =?  or " + DetailSchema.TYPE + " =? ";
        //清除所有下载完成的数据
        String[] selectionArgs = new String[]{SortType.SORT_LE_RADIO_LOCAL, SortType.SORT_KUWO_LOCAL};
        Cursor cursor = db.query(DetailSchema.TABLE_NAME, null, selection,
                selectionArgs, null, null, DetailSchema.DOWNLOAD_TIME + "  desc");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                MediaDetail mediaDetail = new MediaDetail();
                mediaDetail.ALBUM_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.ALBUM_ID));
                mediaDetail.AUDIO_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.AUDIO_ID));
                mediaDetail.AUTHOR = cursor.getString(cursor.getColumnIndex(DetailSchema.AUTHOR));
                mediaDetail.CREATE_TIME = cursor.getString(cursor.getColumnIndex(DetailSchema.CREATE_TIME));
                mediaDetail.CREATE_USER = cursor.getString(cursor.getColumnIndex(DetailSchema.CREATE_USER));
                mediaDetail.LE_SOURCE_MID = cursor.getString(cursor.getColumnIndex(DetailSchema.LE_SOURCE_MID));
                mediaDetail.LE_SOURCE_VID = cursor.getString(cursor.getColumnIndex(DetailSchema.LE_SOURCE_VID));
                mediaDetail.SOURCE_CP_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.SOURCE_CP_ID));
                mediaDetail.NAME = cursor.getString(cursor.getColumnIndex(DetailSchema.NAME));
                mediaDetail.IMG_URL = cursor.getString(cursor.getColumnIndex(DetailSchema.IMG_URL));
                mediaDetail.SOURCE_URL = cursor.getString(cursor.getColumnIndex(DetailSchema.SOURCE_URL));
                mediaDetail.UPDATE_USER = cursor.getString(cursor.getColumnIndex(DetailSchema.UPDATE_USER));
                mediaDetail.DOWNLOAD_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_ID));
                mediaDetail.DOWNLOAD_TIME = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_TIME));
                String flag = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_FLAG));
                if (flag != null) {
                    mediaDetail.DOWNLOAD_FLAG = Integer.parseInt(flag);
                }
                mediaDetail.TYPE = cursor.getString(cursor.getColumnIndex(DetailSchema.TYPE));
                argList.add(mediaDetail);
            }
            cursor.close();
        }
        return argList;
    }

    /**
     * 根据专辑类型或ID获取该专辑下的所有本地音乐
     * 当专辑为收藏
     */
    public ArrayList<MediaDetail> getMusicList(String type, String albumId) {
        ArrayList<MediaDetail> localList = new ArrayList<MediaDetail>();
        switch (type) {
            case SortType.SORT_RECENT:
                String selectionRecent = DetailSchema.TYPE + " =? ";
                String[] selectionArgsRecent = new String[]{type};
                Cursor recentCursor = db.query(DetailSchema.TABLE_NAME, null, selectionRecent,
                        selectionArgsRecent, null, null, DetailSchema.ID + " DESC");
                MediaDetail detailRecent;
                while (recentCursor.moveToNext()) {
                    detailRecent = convertToDetail(recentCursor);
                    localList.add(detailRecent);
                }
                recentCursor.close();
                break;

            case SortType.SORT_FAVOR:
                String selectionFavor = DetailSchema.TYPE + " =? AND " + DetailSchema.ALBUM_ID
                        + " =? ";
                //只取下载完成的数据
                String[] selectionArgsFavor = new String[]{type, albumId};
                Cursor favorCursor = db.query(DetailSchema.TABLE_NAME, null, selectionFavor,
                        selectionArgsFavor, null, null, null);

                MediaDetail detailFavor;
                while (favorCursor.moveToNext()) {
                    detailFavor = convertToDetail(favorCursor);
                    localList.add(detailFavor);
                }
                favorCursor.close();
                break;

            case SortType.SORT_LOCAL_ALL:
                localList.clear();
                getMediaTypeList(SortType.SORT_LE_RADIO_LOCAL, localList);
                queryDetails(SortType.SORT_LOCAL_ALL, localList, false);
                break;
            case SortType.SORT_LOCAL:
                queryDetails(SortType.SORT_LOCAL, localList);

                break;
            case SortType.SORT_KUWO_LOCAL:
                queryDetails(SortType.SORT_KUWO_LOCAL, localList);
                break;
            case SortType.SORT_LE_RADIO_LOCAL:
                getMediaTypeList(SortType.SORT_LE_RADIO_LOCAL, localList);
                break;
            default:
                break;
        }
        return localList;
    }


    private void queryDetails(String argType, ArrayList<MediaDetail> localList, boolean clear) {
        if (clear) {
            localList.clear();

        }
        queryDetails(argType, localList);

    }

    private void queryDetails(String argType, ArrayList<MediaDetail> localList) {
        ContentResolver contentResolver = EcoApplication.instance.getContentResolver();
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()) {
            MediaDetail mediaDetail = new MediaDetail();
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
            if (isMusic == 1) { // 只把音乐添加到集合当中
                String url = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径

                String id = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media._ID));   //音乐id
                String title = cursor.getString((cursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE))); // 音乐标题
                String artist = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 艺术家
                String album_id = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                mediaDetail.AUDIO_ID = id;
                mediaDetail.AUTHOR = artist;
                mediaDetail.NAME = title;
                mediaDetail.SOURCE_URL = url;
                mediaDetail.ALBUM_ID = album_id;
                mediaDetail.TYPE = argType;
                mediaDetail.DOWNLOAD_FLAG = MediaDetail.State.STATE_NONE;

                boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(artist);
                if (ISO) {
                    try {
                        mediaDetail.AUTHOR = new String(artist.getBytes("iso-8859-1"), "gbk");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    File file = new File(url);
                    String name = file.getName();
                    if (name.indexOf(".mp3") != -1) {
                        name = name.substring(0, name.indexOf(".mp3"));
                    } else if (name.indexOf(".wav") != -1) {
                        name = name.substring(0, name.indexOf(".wav"));
                    }
                    mediaDetail.NAME = name;
                    if (file.exists()) {
                        mediaDetail.setFileIfExist(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!localList.contains(mediaDetail)) {
                    localList.add(mediaDetail);
                }

            }
        }
        cursor.close();
    }

    public void deleteFileFromMediaStore(String mediaId) {
        ContentResolver contentResolver = EcoApplication.instance.getContentResolver();
        String[] selectionArgs = {mediaId};

        contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Audio.Media._ID + "=?", selectionArgs);
    }

    /**
     * 删除选定音乐
     **/
    public void deleteMusicList(ArrayList<MediaDetail> targetList, HashMap<Integer, Boolean> isSelected) {
        if (targetList != null) {
            ContentResolver contentResolver = EcoApplication.instance.getContentResolver();
            File file;
            boolean delFlag = false;
            for (int i = 0; i < targetList.size(); i++) {
                if (isSelected.get(i)) {
                    file = new File(targetList.get(i).SOURCE_URL);
                    if (file.exists() && file.isFile()) {
                        delFlag = file.delete();
                    }
                    if (delFlag) {
                        String where = MediaStore.Audio.Media._ID + " = ?";
                        String[] whereValue = {targetList.get(i).AUDIO_ID};
                        contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, where, whereValue);
                    }
                }
            }
        }
    }

    /**
     * 增加专辑
     *
     * @param albumInfo
     * @return
     */
    public long insertAlbumInfo(String type, LeAlbumInfo albumInfo) {
        long row = -1;
        ContentValues cv;
        cv = album2cv(type, albumInfo);
        if (!hasSavedAlbum(type, albumInfo)) {
            row = db.insert(AlbumSchema.TABLE_NAME, null, cv);
        }
        return row;
    }

    /**
     * 插入专辑时查重
     *
     * @param type
     * @param albumInfo
     * @return
     */
    public boolean hasSavedAlbum(String type, LeAlbumInfo albumInfo) {
        String selectionFavor = AlbumSchema.TYPE + " =? AND " + AlbumSchema.ALBUM_ID + " =? AND " + AlbumSchema.NAME + " =?";
        String[] selectionArgsFavor = new String[]{type, albumInfo.ALBUM_ID, albumInfo.NAME};
        Cursor cursor = db.query(AlbumSchema.TABLE_NAME, null, selectionFavor,
                selectionArgsFavor, null, null, null);

        if (cursor.getCount() < 1) {
            return false;
        }
        cursor.close();
        return true;
    }

    public synchronized void insertLTItem(MediaDetail detail) {
        PlayItem item = new PlayItem();
        item.setUrl(detail.SOURCE_URL);
        item.setId(detail.AUDIO_ID);
        item.setXmid(detail.XIA_MI_ID);
        item.setCpName(detail.getSourceName());
        item.setTitle(detail.NAME);
        item.setSource(detail.TYPE);
        item.setDuration(detail.getDuration());
        item.setPlayType(detail.getPlayType());
        item.setMid(detail.LE_SOURCE_MID);
        item.setVid(detail.LE_SOURCE_VID);
        item.setCpid(detail.SOURCE_CP_ID);
        item.setAuthor(detail.AUTHOR);
        item.setImageUrl(detail.IMG_URL);
        insertLTItem(item);
    }

    /**
     * @param item
     * @return 插入音频记忆功能
     */
    public synchronized void insertLTItem(PlayItem item) {
        long row = -1;
        long time = System.currentTimeMillis();
        item.setPlayTime(time);
        ContentValues cv;
        cv = ltitem2cv(item);
        String condition;
        String[] selectionArgsFavor;
        Trace.Debug("##### insertLTItem:item=" + item.toString());
        /*if (item.isLocalItem()) {
            condition = LTRecentItemSchema.URl + " =? ";
            selectionArgsFavor = new String[]{item.getUrl()};
        } else*/ if (item.isXiaMiItem()) {
            condition = LTRecentItemSchema.XMID + " =? ";
            selectionArgsFavor = new String[]{item.getXmid()};
        } else if (item.isLiveItem()) {
            condition = LTRecentItemSchema.MID + " =? and " + LTRecentItemSchema.VID + " =? and " + LTRecentItemSchema.CPID + " =? ";
            selectionArgsFavor = new String[]{item.getMid(), item.getVid(), item.getCpid()};
        } else {
            condition = LTRecentItemSchema.TYPE + " =? and " + LTRecentItemSchema.AUDID + " =? ";
            if(item.getPlayType()==null){
                item.setPlayType("0");
            }
            selectionArgsFavor = new String[]{item.getPlayType(), item.getId()};
            Trace.Debug("##### insertLTItem:selectionArgsFavor="+selectionArgsFavor[0]+","+selectionArgsFavor[1]);
        }
        if (!hasSavedLTItem(item)) {
            deleteItems();
            row = db.insert(LTRecentItemSchema.TABLE_NAME, null, cv);
        } else {
            if(item.isLocalItem()) {
                condition = LTRecentItemSchema.AUDID + " =? ";
                selectionArgsFavor = new String[]{item.getId()};
                db.delete(LTRecentItemSchema.TABLE_NAME, condition, selectionArgsFavor);
                row = db.update(LTRecentItemSchema.TABLE_NAME, cv, condition, selectionArgsFavor);
            }
        }

    }

    /**
     * @param item
     * @return 插入音频记忆功能
     */
    public synchronized void deleteLTItem(PlayItem item) {

        String condition;
        String[] selectionArgsFavor;

        if (item.isLocalItem()) {
            condition = LTRecentItemSchema.URl + " =? ";
            selectionArgsFavor = new String[]{item.getUrl()};
        } else if (item.isXiaMiItem()) {
            condition = LTRecentItemSchema.XMID + " =? ";
            selectionArgsFavor = new String[]{item.getXmid()};
        } else if (item.isLiveItem()) {
            condition = LTRecentItemSchema.MID + " =? and " + LTRecentItemSchema.VID + " =? and " + LTRecentItemSchema.CPID + " =? ";
            selectionArgsFavor = new String[]{item.getMid(), item.getVid(), item.getCpid()};
        } else {
            condition = LTRecentItemSchema.TYPE + " =? and " + LTRecentItemSchema.AUDID + " =? ";
            if(item.getPlayType()==null){
                item.setPlayType("0");
            }
            selectionArgsFavor = new String[]{item.getPlayType(), item.getId()};

        }
        db.delete(LTRecentItemSchema.TABLE_NAME, condition, selectionArgsFavor);

    }

    public void deleteAllRecentItems() {
        db.delete(LTRecentItemSchema.TABLE_NAME, null, null);
    }

    public ArrayList<MediaDetail> getRecentList() {
        ArrayList<MediaDetail> list = new ArrayList<MediaDetail>();
        Cursor cursor = db.query(LTRecentItemSchema.TABLE_NAME, null, null, null, null, null, LTRecentItemSchema.PLAYTIME + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MediaDetail item = new MediaDetail();
                item.AUDIO_ID = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.AUDID));
                item.NAME = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.TITLE));
                item.TYPE = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.SOURCE));
                item.SOURCE_URL = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.URl));
                item.XIA_MI_ID = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.XMID));
                item.LE_SOURCE_MID = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.MID));
                item.AUTHOR = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.AUTHOR));
                item.LE_SOURCE_VID = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.VID));
                item.LE_SOURCE_MID = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.MID));
                item.IMG_URL = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.IMG_URL));
                item.SOURCE_CP_ID = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.CPID));
                item.setPlayType(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.TYPE)));
                File file = new File(item.getFile());
                if (file.exists()) {
                    item.setFileIfExist(true);
                }
                list.add(item);
            }
        }
        cursor.close();
        return list;
    }


    private void deleteItems() {
        int index = 0;

        Cursor cursor = db.query(LTRecentItemSchema.TABLE_NAME, null, null,
                null, null, null, LTRecentItemSchema.PLAYTIME + " DESC");

        if (cursor.getCount() > 100) {
            if (cursor.moveToLast()) {
                index = cursor.getInt(0);
            }

            db.delete(LTRecentItemSchema.TABLE_NAME,
                    LTRecentItemSchema.ID + "=?", new String[]{index + ""});
        }
        cursor.close();
    }

    public long insertLeSortInfo(LeSortInfo leSortInfo) {
        long row = -1;
        ContentValues cv;

        cv = sortInfo2cv(leSortInfo);
        String condition = ChannelInfoSchema.NAME + " =?  ";
        String[] selectionArgsFavor = new String[]{leSortInfo.NAME};
        if (!hasSavedSortInfo(leSortInfo)) {
            row = db.insert(ChannelInfoSchema.TABLE_NAME, null, cv);
        } else {
            db.update(ChannelInfoSchema.TABLE_NAME, cv, condition, selectionArgsFavor);
        }
        return row;

    }


    public long insertLeSortInfo(final ArrayList<LeSortInfo> leSortInfos) {
        new Thread() {
            @Override
            public void run() {
                db.delete(ChannelInfoSchema.TABLE_NAME, null, null);
                long row = -1;
                for (int i = 0; i < leSortInfos.size(); i++) {
                    LeSortInfo leSortInfo = leSortInfos.get(i);
                    insertLeSortInfo(leSortInfo);
                }
            }


        }.start();

        return 0;
    }

    public long deleteLeSortInfo(LeSortInfo leSortInfo) {
        long row = -1;
        ContentValues cv;
        String condition = ChannelInfoSchema.NAME + " =?  ";
        String[] selectionArgsFavor = new String[]{leSortInfo.NAME};
        db.delete(ChannelInfoSchema.TABLE_NAME, condition, selectionArgsFavor);
        return row;

    }

    public ArrayList<LeSortInfo> getSortInfos() {
        ArrayList<LeSortInfo> leSortInfos = new ArrayList<>();
        Cursor cursor = db.query(ChannelInfoSchema.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LeSortInfo sortInfo = new LeSortInfo();
                sortInfo.NAME = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.NAME));
                sortInfo.SORT_ID = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.PAGE_ID));
                sortInfo.TYPE = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.TYPE));
                leSortInfos.add(sortInfo);

            }
        }
        cursor.close();
        return leSortInfos;

    }

    public synchronized ProgressStatus getLTITem(PlayItem item) {
        if (item.isLiveItem()) {
            return new ProgressStatus(0, 0);
        }

        String condition;
        String[] selectionArgsFavor;
        if (item.getUrl() != null && !item.getUrl().trim().equals("") && item.getUrl().startsWith("/")) {
            condition = LTRecentItemSchema.URl + " =? ";
            selectionArgsFavor = new String[]{item.getUrl()};
        } else if (item.getXmid() != null && !item.getXmid().trim().equals("")) {
            condition = LTRecentItemSchema.XMID + " =? ";
            selectionArgsFavor = new String[]{item.getXmid()};
        } else if (item.isLiveItem()) {
            condition = LTRecentItemSchema.MID + " =? and " + LTRecentItemSchema.VID + " =? and " + LTRecentItemSchema.CPID + " =? ";
            selectionArgsFavor = new String[]{item.getMid(), item.getVid(), item.getCpid()};
        } else {
            condition = LTRecentItemSchema.TYPE + " =? and " + LTRecentItemSchema.AUDID + " =? ";
            if(item.getPlayType()==null){
                item.setPlayType("0");
            }
            selectionArgsFavor = new String[]{item.getPlayType(), item.getId()};
            Trace.Debug("##### getLTITem:selectionArgsFavor="+selectionArgsFavor[0]+","+selectionArgsFavor[1]);
        }
        if(selectionArgsFavor[0] == null){
            return new ProgressStatus(0, 0);
        }
        Cursor cursor = db.query(LTRecentItemSchema.TABLE_NAME, null, condition,
                selectionArgsFavor, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String p = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.PROGRESS));
                String d = cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.DUARATION));
                if (p != null && d != null) {
                    return new ProgressStatus(Long.parseLong(p), Long.parseLong(d));
                }
            }
            cursor.close();

        }
        return new ProgressStatus(0, 0);
    }

    public PlayItem getHistoryLastItem() {
        PlayItem result = new PlayItem();
        Cursor cursor = db.query(LTRecentItemSchema.TABLE_NAME, null, null,
                null, null, null, LTRecentItemSchema.PLAYTIME + " DESC");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.setUrl(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.URl)));
                result.setId(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.ID)));
                result.setImageUrl(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.IMG_URL)));
                result.setMid(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.MID)));
                result.setVid(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.VID)));
                result.setCpid(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.CPID)));
                result.setAuthor(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.AUTHOR)));
                result.setPlayType(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.TYPE)));
                result.setCpName(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.CPNAME)));
                result.setSource(cursor.getString(cursor.getColumnIndex(LTRecentItemSchema.SOURCE)));
                cursor.close();
                return result;
            }


        }
        cursor.close();
        return null;
    }

    public boolean hasSavedLTItem(PlayItem item) {
        String condition;
        String[] selectionArgsFavor;
        /*if (item.isLocalItem()) {
            condition = LTRecentItemSchema.URl + " =? ";
            selectionArgsFavor = new String[]{item.getUrl()};
        } else */if (item.isXiaMiItem()) {
            condition = LTRecentItemSchema.XMID + " =? ";
            selectionArgsFavor = new String[]{item.getXmid()};
        } else if (item.isLiveItem()) {
            condition = LTRecentItemSchema.MID + " =? and " + LTRecentItemSchema.VID + " =? and " + LTRecentItemSchema.CPID + " =? ";
            selectionArgsFavor = new String[]{item.getMid(), item.getVid(), item.getCpid()};
        } else {

            if(item.getPlayType()==null){
                item.setPlayType("0");
            }
            condition = LTRecentItemSchema.TYPE + " =? and " + LTRecentItemSchema.AUDID + " =? ";
            selectionArgsFavor = new String[]{item.getPlayType(), item.getId()};
            Trace.Debug("##### hasSavedLTItem:selectionArgsFavor="+selectionArgsFavor[0]+","+selectionArgsFavor[1]);

        }
        if(selectionArgsFavor[0] == null && selectionArgsFavor[1] == null){
            return false;
        }
        Cursor cursor = db.query(LTRecentItemSchema.TABLE_NAME, null, condition,
                selectionArgsFavor, null, null, /*LTRecentItemSchema.PLAYTIME*/null);

        if (cursor.getCount() < 1) {
            cursor.close();
            Trace.Debug("##### hasSavedLTItem:return false");
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean hasSavedSortInfo(LeSortInfo sortInfo) {
        String selectionFavor = ChannelInfoSchema.NAME + " =?  ";
        String[] selectionArgsFavor = new String[]{sortInfo.NAME};
        Cursor cursor = db.query(ChannelInfoSchema.TABLE_NAME, null, selectionFavor,
                selectionArgsFavor, null, null, null);

        if (cursor.getCount() < 1) {
            return false;
        }
        cursor.close();
        return true;

    }

    private ContentValues ltitem2cv(PlayItem ltItem) {
        final PlayItem item = ltItem;
        ContentValues cv = new ContentValues();
        cv.put(LTRecentItemSchema.URl, item.getUrl());
        cv.put(LTRecentItemSchema.XMID, item.getXmid());
        cv.put(LTRecentItemSchema.CPNAME, item.getCpName());
        cv.put(LTRecentItemSchema.AUDID, item.getId());
        cv.put(LTRecentItemSchema.SOURCE, item.getSource());
        cv.put(LTRecentItemSchema.TYPE, item.getPlayType());
        cv.put(LTRecentItemSchema.TITLE, item.getTitle());
        cv.put(LTRecentItemSchema.PROGRESS, ltItem.getProgress());
        cv.put(LTRecentItemSchema.DUARATION, ltItem.getDuration());
        cv.put(LTRecentItemSchema.PLAYTIME, ltItem.getPlayTime());
        cv.put(LTRecentItemSchema.IMG_URL, ltItem.getImageUrl());
        cv.put(LTRecentItemSchema.AUTHOR, ltItem.getAuthor());
        cv.put(LTRecentItemSchema.CPID, ltItem.getCpid());
        cv.put(LTRecentItemSchema.MID, ltItem.getMid());
        cv.put(LTRecentItemSchema.VID, ltItem.getVid());
        return cv;
    }


    /**
     * 更新Detail下载路径和时间
     */
    public long updateDetailDownloadStatePath(String downloadId, String downloadPath) {

        String where = DetailSchema.DOWNLOAD_ID + " =? ";
        String[] whereValue = new String[]{downloadId};
        ContentValues cv = new ContentValues();
        cv.put(DetailSchema.SOURCE_URL, downloadPath);
        String currentTime = Long.toString(System.currentTimeMillis());
        cv.put(DetailSchema.DOWNLOAD_TIME, currentTime);
        cv.put(DetailSchema.DOWNLOAD_FLAG, DownLoadState.DOWNLOADED);
        long id = db.update(DetailSchema.TABLE_NAME, cv, where, whereValue);

        return id;
    }


    public boolean isCollectionAlbum(LeAlbumInfo leAlbumInfo) {
        String selection = AlbumSchema.TYPE + " =? AND " + AlbumSchema.ALBUM_ID + " =? ";
        String[] selectionArgs = new String[]{SortType.SORT_FAVOR, leAlbumInfo.ALBUM_ID};
        Cursor cursor = db.query(AlbumSchema.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);

        if (cursor.getCount() < 1) {
            return false;
        }
        cursor.close();
        return true;

    }


    /**
     * 判断单条音乐是否下载
     *
     * @param mediaDetail 查询detail唯一标识符MediaID
     * @return
     */
    public boolean isDownLoadMusic(String Type, MediaDetail mediaDetail) {

        String selection = DetailSchema.TYPE + "= ? and " + DetailSchema.AUDIO_ID + "= ? and " + DetailSchema.DOWNLOAD_FLAG + " =?";

        String[] selectionArgs = new String[]{Type, mediaDetail.AUDIO_ID, MediaDetail.State.STATE_FINISH + ""};
        Cursor cursor = db.query(DetailSchema.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;

    }

    public boolean isDownLoadMusic(String id) {
        String selection = DetailSchema.TYPE + "= ? and " + DetailSchema.AUDIO_ID + "= ? and " + DetailSchema.DOWNLOAD_FLAG + " =?";

        String[] selectionArgs = new String[]{SortType.SORT_LE_RADIO_LOCAL, id, MediaDetail.State.STATE_FINISH + ""};
        Cursor cursor = db.query(DetailSchema.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * 删除专辑
     *
     * @param type
     * @param albumInfo
     * @return
     */
    public long delelteAlbumInfo(String type, LeAlbumInfo albumInfo) {
        long row = -1;
        String selectionFavor = AlbumSchema.TYPE + " =? AND " + AlbumSchema.ALBUM_ID + " =? AND " + AlbumSchema.NAME + " =? ";
        String[] selectionArgs = new String[]{type, albumInfo.ALBUM_ID, albumInfo.NAME};
        if (hasSavedAlbum(type, albumInfo)) {
            row = db.delete(AlbumSchema.TABLE_NAME, selectionFavor, selectionArgs);
        }

        return row;
    }

    /**
     * 删除单条音乐
     *
     * @param type
     */

    public long deleteMediasByType(String type) {
        long id = -1;
        String selectionFavor = DetailSchema.TYPE + " =?";
        String[] selectionArgsFavor = new String[]{type,};
        id = db.delete(DetailSchema.TABLE_NAME, selectionFavor, selectionArgsFavor);

        return id;
    }


    public void beginTransaction() {
        db.beginTransaction();
    }

    public void endTransaction() {
        db.endTransaction();
    }

    /**
     * 增加
     *
     * @param mediaDetail
     * @return
     */
    public long insertMediaDetail(String type, MediaDetail mediaDetail) {
        long id = -1;
        ContentValues cv = detail2cv(type, mediaDetail);
        if (!hasSavedDetail(type, mediaDetail)) {
            id = db.insert(DetailSchema.TABLE_NAME, null, cv);
        }
        return id;
    }

    public long deleteMediaDetailbyAudioId(String type, String audioId) {
        long id = -1;
        String selectionFavor = DetailSchema.TYPE + " =? AND " + DetailSchema.AUDIO_ID + " =? ";
        String[] selectionArgsFavor = new String[]{type, audioId};
        id = db.delete(DetailSchema.TABLE_NAME, selectionFavor, selectionArgsFavor);

        return id;
    }

    /**
     * @param albumId
     * @return
     * @ why
     */
    public ArrayList<MediaDetail> getDownlodMusics(String albumId, String type) {
        ArrayList<MediaDetail> mediaDetails = new ArrayList<>();
//        String selection = DetailSchema.ALBUM_ID + " =? AND (" + DetailSchema.DOWNLOAD_FLAG + "= ? OR " + DetailSchema.DOWNLOAD_FLAG + "= ?)";
        String selection = DetailSchema.ALBUM_ID + " =? AND " + DetailSchema.TYPE + "= ?";
        String[] selectionArgs = new String[]{albumId, type};
        Cursor cursor = db.query(DetailSchema.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                MediaDetail mediaDetail = new MediaDetail();
                mediaDetail.ALBUM_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.ALBUM_ID));
                mediaDetail.AUDIO_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.AUDIO_ID));
                mediaDetail.AUTHOR = cursor.getString(cursor.getColumnIndex(DetailSchema.AUTHOR));
                mediaDetail.CREATE_TIME = cursor.getString(cursor.getColumnIndex(DetailSchema.CREATE_TIME));
                mediaDetail.CREATE_USER = cursor.getString(cursor.getColumnIndex(DetailSchema.CREATE_USER));
                mediaDetail.LE_SOURCE_MID = cursor.getString(cursor.getColumnIndex(DetailSchema.LE_SOURCE_MID));
                mediaDetail.LE_SOURCE_VID = cursor.getString(cursor.getColumnIndex(DetailSchema.LE_SOURCE_VID));
                mediaDetail.SOURCE_CP_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.SOURCE_CP_ID));
                mediaDetail.NAME = cursor.getString(cursor.getColumnIndex(DetailSchema.NAME));
                mediaDetail.IMG_URL = cursor.getString(cursor.getColumnIndex(DetailSchema.IMG_URL));
                mediaDetail.SOURCE_URL = cursor.getString(cursor.getColumnIndex(DetailSchema.SOURCE_URL));
                mediaDetail.UPDATE_USER = cursor.getString(cursor.getColumnIndex(DetailSchema.UPDATE_USER));
                mediaDetail.DOWNLOAD_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_ID));
                mediaDetail.DOWNLOAD_TIME = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_TIME));
                String flag = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_FLAG));
                mediaDetail.DOWNLOAD_FLAG = Integer.parseInt(flag);
                mediaDetails.add(mediaDetail);

            }
            cursor.close();
        }

        return mediaDetails;

    }

    /**
     * @param type
     * @return
     * @ why
     */
    public ArrayList<MediaDetail> getMediaTypeList(String type, ArrayList<MediaDetail> argList) {
        String selection = DetailSchema.TYPE + " =? AND " + DetailSchema.DOWNLOAD_FLAG + " =?";
        String[] selectionArgs = new String[]{type, String.valueOf(MediaDetail.State.STATE_FINISH)};
        Cursor cursor = db.query(DetailSchema.TABLE_NAME, null, selection,
                selectionArgs, null, null, DetailSchema.DOWNLOAD_TIME + "  desc");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                MediaDetail mediaDetail = new MediaDetail();
                mediaDetail.ALBUM_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.ALBUM_ID));
                mediaDetail.AUDIO_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.AUDIO_ID));
                mediaDetail.AUTHOR = cursor.getString(cursor.getColumnIndex(DetailSchema.AUTHOR));
                mediaDetail.CREATE_TIME = cursor.getString(cursor.getColumnIndex(DetailSchema.CREATE_TIME));
                mediaDetail.CREATE_USER = cursor.getString(cursor.getColumnIndex(DetailSchema.CREATE_USER));
                mediaDetail.LE_SOURCE_MID = cursor.getString(cursor.getColumnIndex(DetailSchema.LE_SOURCE_MID));
                mediaDetail.LE_SOURCE_VID = cursor.getString(cursor.getColumnIndex(DetailSchema.LE_SOURCE_VID));
                mediaDetail.SOURCE_CP_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.SOURCE_CP_ID));
                mediaDetail.NAME = cursor.getString(cursor.getColumnIndex(DetailSchema.NAME));
                mediaDetail.IMG_URL = cursor.getString(cursor.getColumnIndex(DetailSchema.IMG_URL));
                mediaDetail.SOURCE_URL = cursor.getString(cursor.getColumnIndex(DetailSchema.SOURCE_URL));
                mediaDetail.UPDATE_USER = cursor.getString(cursor.getColumnIndex(DetailSchema.UPDATE_USER));
                mediaDetail.DOWNLOAD_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_ID));
                mediaDetail.DOWNLOAD_TIME = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_TIME));
                String flag = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_FLAG));
                if (flag != null) {
                    mediaDetail.DOWNLOAD_FLAG = Integer.parseInt(flag);
                }
                mediaDetail.TYPE = cursor.getString(cursor.getColumnIndex(DetailSchema.TYPE));
                File file = new File(mediaDetail.getFile());
                if (file.exists()) {
                    mediaDetail.setFileIfExist(true);
                    argList.add(mediaDetail);

                } else {
                    deleteMediaDetailbyAudioId(type, mediaDetail.AUDIO_ID);
                }


            }
            cursor.close();
        }

        return argList;

    }

    /**
     * 插入单条曲目时查重
     *
     * @param type
     * @param mediaDetail
     * @return
     */
    private boolean hasSavedDetail(String type, MediaDetail mediaDetail) {
        try {
            String selectionFavor = DetailSchema.TYPE + " =? AND " + DetailSchema.AUDIO_ID + " =? ";
            String[] selectionArgsFavor = new String[]{type, mediaDetail.AUDIO_ID};
            Cursor cursor = db.query(DetailSchema.TABLE_NAME, null, selectionFavor,
                    selectionArgsFavor, null, null, null);

            if (cursor.getCount() < 1) {
                return false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 更新Detail下载状态
     */
    public long updateDetailDownloadStateId(MediaDetail detail) {
        long id = -1;
        String where = DetailSchema.TYPE + " =? AND " + DetailSchema.AUDIO_ID + " =? ";
        String[] whereValue = new String[]{detail.TYPE, detail.AUDIO_ID};
        ContentValues cv = new ContentValues();
        cv.put(DetailSchema.DOWNLOAD_FLAG, String.valueOf(detail.DOWNLOAD_FLAG));

        id = db.update(DetailSchema.TABLE_NAME, cv, where, whereValue);
        return id;
    }

    public long updateDetailDownloadStateId(String type, String audioId, String title, String state) {
        long id = -1;
        String where = DetailSchema.TYPE + " =? AND " + DetailSchema.AUDIO_ID + " =? AND " + DetailSchema.NAME + " =? ";
        String[] whereValue = new String[]{type, audioId, title};
        ContentValues cv = new ContentValues();
        cv.put(DetailSchema.DOWNLOAD_FLAG, state);
        cv.put(DetailSchema.DOWNLOAD_TIME, System.currentTimeMillis() + "");
        id = db.update(DetailSchema.TABLE_NAME, cv, where, whereValue);
        return id;
    }

    private MediaDetail convertToDetail(Cursor favorCursor) {
        MediaDetail detailFavor = new MediaDetail();
        detailFavor.AUTHOR = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.AUTHOR));
        detailFavor.SOURCE_URL = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.SOURCE_URL));
        detailFavor.NAME = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.NAME));
        detailFavor.IMG_URL = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.IMG_URL));
        detailFavor.CREATE_TIME = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.CREATE_TIME));
        detailFavor.AUDIO_ID = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.AUDIO_ID));
        detailFavor.SOURCE_CP_ID = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.SOURCE_CP_ID));
        detailFavor.LE_SOURCE_MID = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.LE_SOURCE_MID));
        detailFavor.LE_SOURCE_VID = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.LE_SOURCE_VID));

        detailFavor.AUDIO_ID = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.AUDIO_ID));
        detailFavor.CREATE_USER = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.CREATE_USER));
        detailFavor.ALBUM_ID = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.ALBUM_ID));
        detailFavor.UPDATE_USER = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.UPDATE_USER));
        String flag = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.DOWNLOAD_FLAG));
        detailFavor.DOWNLOAD_FLAG = Integer.parseInt(flag);
        detailFavor.DOWNLOAD_ID = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.DOWNLOAD_ID));
        detailFavor.DOWNLOAD_TIME = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.DOWNLOAD_TIME));
        detailFavor.TYPE = favorCursor.getString(favorCursor
                .getColumnIndexOrThrow(DetailSchema.TYPE));
        return detailFavor;
    }

    /**
     * @param albumInfo
     * @return
     */
    private ContentValues album2cv(String type, LeAlbumInfo albumInfo) {
        ContentValues cv = new ContentValues();
        cv.put(AlbumSchema.SOURCE_CP_ID, albumInfo.SOURCE_CP_ID);
        cv.put(AlbumSchema.SRC_IMG_URL, albumInfo.SRC_IMG_URL);
        cv.put(AlbumSchema.NAME, albumInfo.NAME);
        cv.put(AlbumSchema.IMG_URL, albumInfo.IMG_URL);
        cv.put(AlbumSchema.CREATE_TIME, albumInfo.CREATE_TIME);
        cv.put(AlbumSchema.DESCRIPTION, albumInfo.DESCRIPTION);
        cv.put(AlbumSchema.UPDATE_TIME, albumInfo.UPDATE_TIME);
        cv.put(AlbumSchema.PLAYCOUNT, albumInfo.PLAYCOUNT);
        cv.put(AlbumSchema.TYPE, type);
        cv.put(AlbumSchema.ALBUM_TYPE_ID, albumInfo.ALBUM_TYPE_ID);
        cv.put(AlbumSchema.CREATE_USER, albumInfo.CREATE_USER);
        cv.put(AlbumSchema.ALBUM_ID, albumInfo.ALBUM_ID);
        cv.put(AlbumSchema.DISPLAY_NAME, albumInfo.DISPLAY_NAME);
        cv.put(AlbumSchema.DISPLAY_ID, albumInfo.DISPLAY_ID);
        cv.put(AlbumSchema.DISPLAY_SOURCE_URL, albumInfo.DISPLAY_SOURCE_URL);
        cv.put(AlbumSchema.DISPLAY_LE_SOURCE_VID, albumInfo.DISPLAY_LE_SOURCE_VID);
        cv.put(AlbumSchema.DISPLAY_LE_SOURCE_MID, albumInfo.DISPLAY_LE_SOURCE_MID);
        cv.put(AlbumSchema.DISPLAY_IMG_URL, albumInfo.DISPLAY_IMG_URL);
        cv.put(AlbumSchema.CHANNEL_NAME, albumInfo.channelType);
        //TODO:
        cv.put(AlbumSchema.DOWNLOAD_FLAG, albumInfo.DOWNLOAD_FLAG);

        return cv;
    }


    private ContentValues sortInfo2cv(LeSortInfo sortInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ChannelInfoSchema.NAME, sortInfo.NAME);
        contentValues.put(ChannelInfoSchema.PAGE_ID, sortInfo.SORT_ID);
        contentValues.put(ChannelInfoSchema.TYPE, sortInfo.TYPE);
        return contentValues;
    }

    /**
     * @param detail
     * @return
     */
    private ContentValues detail2cv(String type, MediaDetail detail) {
        ContentValues cv = new ContentValues();
        cv.put(DetailSchema.ALBUM_ID, detail.ALBUM_ID);
        //cv.put(DetailSchema.ID, detail.AUDIO_ID);
        cv.put(DetailSchema.AUTHOR, detail.AUTHOR);
        cv.put(DetailSchema.CREATE_TIME, detail.CREATE_TIME);
        cv.put(DetailSchema.CREATE_USER, detail.CREATE_USER);
        cv.put(DetailSchema.IMG_URL, detail.IMG_URL);
        cv.put(DetailSchema.SOURCE_CP_ID, detail.SOURCE_CP_ID);
        cv.put(DetailSchema.LE_SOURCE_MID, detail.LE_SOURCE_MID);
        cv.put(DetailSchema.LE_SOURCE_VID, detail.LE_SOURCE_VID);
        cv.put(DetailSchema.AUDIO_ID, detail.AUDIO_ID);
        cv.put(DetailSchema.NAME, detail.NAME);
        cv.put(DetailSchema.SOURCE_URL, detail.SOURCE_URL);
        cv.put(DetailSchema.UPDATE_USER, detail.UPDATE_USER);
        //开始放进去的时候将状态改为未标记
//        if (type != null) {
//            if (type.equals(SortType.SORT_DOWNLOAD)) {
//                detail.DOWNLOAD_FLAG = DownLoadState.UNDOWNLOAD;
//            }
//        }
        cv.put(DetailSchema.DOWNLOAD_FLAG, detail.DOWNLOAD_FLAG + "");
        cv.put(DetailSchema.DOWNLOAD_ID, detail.DOWNLOAD_ID);
        cv.put(DetailSchema.DOWNLOAD_TIME, detail.DOWNLOAD_TIME);
        cv.put(DetailSchema.TYPE, type);
        return cv;
    }

    public ArrayList<Channel> getChannelsList() {
        ArrayList<Channel> channels = new ArrayList<>();
        Cursor cursor = db.query(ChannelInfoSchema.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Channel channel = new Channel();
                channel.name = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.NAME));
                channel.pageId = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.PAGE_ID));
                channel.type = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.TYPE));
                channel.dataUrl = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.DATA_URL));
                channel.cmsID = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.CMS_ID));
                channel.mzcId = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.MZ_CID));
                channel.skipType = cursor.getString(cursor.getColumnIndex(ChannelInfoSchema.SKIP_ID));
                channels.add(channel);

            }
            cursor.close();
        }
        return channels;

    }

    public long insertChannelList(final ArrayList<Channel> channelsList) {
        new Thread() {
            @Override
            public void run() {
                db.delete(ChannelInfoSchema.TABLE_NAME, null, null);
                long row = -1;
                for (int i = 0; i < channelsList.size(); i++) {
                    Channel channel = channelsList.get(i);
                    insertChannel(channel);
                }
            }


        }.start();

        return 0;

    }

    public void removeChannelAll() {
        db.delete(ChannelInfoSchema.TABLE_NAME, null, null);
    }

    public long insertChannel(Channel channel) {
        long row = -1;
        ContentValues cv;

        cv = channel2cv(channel);
        String condition = ChannelInfoSchema.NAME + " =?  ";
        String[] selectionArgsFavor = new String[]{channel.name};
        if (!hasSavedChannel(channel)) {
            row = db.insert(ChannelInfoSchema.TABLE_NAME, null, cv);
        } else {
            db.update(ChannelInfoSchema.TABLE_NAME, cv, condition, selectionArgsFavor);
        }
        return row;
    }

    private boolean hasSavedChannel(Channel channel) {
        String selectionFavor = ChannelInfoSchema.NAME + " =?  ";
        String[] selectionArgsFavor = new String[]{channel.name};
        Cursor cursor = db.query(ChannelInfoSchema.TABLE_NAME, null, selectionFavor,
                selectionArgsFavor, null, null, null);

        if (cursor.getCount() < 1) {
            return false;
        }
        cursor.close();
        return true;
    }

    private ContentValues channel2cv(Channel channel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ChannelInfoSchema.NAME, channel.name);
        contentValues.put(ChannelInfoSchema.PAGE_ID, channel.pageId);
        contentValues.put(ChannelInfoSchema.TYPE, channel.type);
        contentValues.put(ChannelInfoSchema.DATA_URL, channel.dataUrl);
        contentValues.put(ChannelInfoSchema.CMS_ID, channel.cmsID);
        contentValues.put(ChannelInfoSchema.MZ_CID, channel.mzcId);
        contentValues.put(ChannelInfoSchema.SKIP_ID, channel.skipType);
        return contentValues;
    }

    public ArrayList<MediaDetail> getLocalMusicByAlbum(LeAlbumInfo leAlbumInfo) {
        ArrayList<MediaDetail> mediaDetails = new ArrayList<>();
//        String selection = DetailSchema.ALBUM_ID + " =? AND (" + DetailSchema.DOWNLOAD_FLAG + "= ? OR " + DetailSchema.DOWNLOAD_FLAG + "= ?)";
        String selection = DetailSchema.TYPE + " =? AND " + DetailSchema.ALBUM_ID + "= ?";
        String[] selectionArgs = new String[]{leAlbumInfo.TYPE, leAlbumInfo.ALBUM_ID};
        Cursor cursor = db.query(DetailSchema.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                MediaDetail mediaDetail = new MediaDetail();
                mediaDetail.ALBUM_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.ALBUM_ID));
                mediaDetail.AUDIO_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.AUDIO_ID));
                mediaDetail.AUTHOR = cursor.getString(cursor.getColumnIndex(DetailSchema.AUTHOR));
                mediaDetail.CREATE_TIME = cursor.getString(cursor.getColumnIndex(DetailSchema.CREATE_TIME));
                mediaDetail.CREATE_USER = cursor.getString(cursor.getColumnIndex(DetailSchema.CREATE_USER));
                mediaDetail.LE_SOURCE_MID = cursor.getString(cursor.getColumnIndex(DetailSchema.LE_SOURCE_MID));
                mediaDetail.LE_SOURCE_VID = cursor.getString(cursor.getColumnIndex(DetailSchema.LE_SOURCE_VID));
                mediaDetail.SOURCE_CP_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.SOURCE_CP_ID));
                mediaDetail.NAME = cursor.getString(cursor.getColumnIndex(DetailSchema.NAME));
                mediaDetail.IMG_URL = cursor.getString(cursor.getColumnIndex(DetailSchema.IMG_URL));
                mediaDetail.SOURCE_URL = cursor.getString(cursor.getColumnIndex(DetailSchema.SOURCE_URL));
                mediaDetail.UPDATE_USER = cursor.getString(cursor.getColumnIndex(DetailSchema.UPDATE_USER));
                mediaDetail.DOWNLOAD_ID = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_ID));
                mediaDetail.DOWNLOAD_TIME = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_TIME));
                mediaDetail.TYPE = cursor.getString(cursor.getColumnIndex(DetailSchema.TYPE));
//                mediaDetail.ALBUM=cursor.getString(cursor.getColumnIndex(leAlbumInfo.NAME));
                String flag = cursor.getString(cursor.getColumnIndex(DetailSchema.DOWNLOAD_FLAG));
                mediaDetail.DOWNLOAD_FLAG = Integer.parseInt(flag);
                mediaDetails.add(mediaDetail);


            }
            cursor.close();
        }
        return mediaDetails;
    }

    public void insertLocalAlbumDetail(final List<MediaDetail> mediaDetails) {
        new Thread() {
            @Override
            public void run() {
                db.beginTransaction();

                for (int i = 0; i < mediaDetails.size(); i++) {
                    MediaDetail mediaDetail = mediaDetails.get(i);
                    insertMediaDetail(SortType.SORT_LOCAL_NEW, mediaDetail);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }


        }.start();

    }

    public void deleteLocalAlbumDetail(final List<MediaDetail> deleteMediaDetails) {
        new Thread() {
            @Override
            public void run() {
                db.beginTransaction();


                for (int i = 0; i < deleteMediaDetails.size(); i++) {
                    MediaDetail mediaDetail = deleteMediaDetails.get(i);
                    deleteMediaDetailbyAudioId(mediaDetail);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }


        }.start();

    }

    public long deleteMediaDetailbyAudioId(MediaDetail mediaDetail) {
        long id = -1;
        String selectionFavor = DetailSchema.TYPE + " =? AND " + DetailSchema.SOURCE_URL + " =? AND " + DetailSchema.ALBUM_ID + " =? ";
        String[] selectionArgsFavor = new String[]{mediaDetail.TYPE, mediaDetail.SOURCE_URL, mediaDetail.ALBUM_ID};
        id = db.delete(DetailSchema.TABLE_NAME, selectionFavor, selectionArgsFavor);

        return id;
    }

    public static class ProgressStatus {
        public long progress;
        public long duration;

        public ProgressStatus(long progress, long duration) {
            this.progress = progress;
            this.duration = duration;
        }
    }
}