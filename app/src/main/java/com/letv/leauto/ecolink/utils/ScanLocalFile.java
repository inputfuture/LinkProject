package com.letv.leauto.ecolink.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.letv.leauto.ecolink.EcoApplication;

import java.io.File;

/**
 * Created by Administrator on 2016/8/10.
 */
public class ScanLocalFile {
    public static final String TAG = "Tag";
    //在获取数据时，先扫描,删除数据后也扫
//    启动MediaScanner服务，扫描媒体文件：
//    程序通过发送下面的Intent启动MediaScanner服务扫描指定的文件或目录：
//
//    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE：扫描指定文件

    public static void scanDirAsync(Context ctx, String dir) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(dir)));
        ctx.sendBroadcast(scanIntent);

//        MediaScannerConnection.scanFile(ctx, new String[] { DeviceUtils.getKuwoMusicCachePath() }, null, null);
    }

//    private static Uri addImageToMediaStore(ContentResolver resolver,
//                                            String title, long date, Location location, int orientation,
//                                            long jpegLength, String path, int width, int height, String mimeType) {
//        // Insert into MediaStore.
//        ContentValues values = getContentValuesForData(title, date, location,
//                orientation, jpegLength, path, width, height, mimeType);
//
//        Uri uri = null;
//        try {
//            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        } catch (Throwable th) {
//            // This can happen when the external volume is already mounted, but
//            // MediaScanner has not notify MediaProvider to add that volume.
//            // The picture is still safe and MediaScanner will find it and
//            // insert it into MediaProvider. The only problem is that the user
//            // cannot click the thumbnail to review the picture.
//            Trace.Error(TAG, "Failed to write MediaStore" + th);
//        }
//        return uri;
//
//
//    }
//    public static ContentValues getContentValuesForData(String title,
//                                                        long date, Location location, int orientation, long jpegLength,
//                                                        String path, int width, int height, String mimeType) {
//
//        File file = new File(path);
//        long dateModifiedSeconds = TimeUnit.MILLISECONDS.toSeconds(file
//                .lastModified());
//
//        ContentValues values = new ContentValues(11);
//        values.put(MediaStore.Images.ImageColumns.TITLE, title);
//        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, title + JPEG_POSTFIX);
//        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, date);
//        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType);
//        values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateModifiedSeconds);
//        // Clockwise rotation in degrees. 0, 90, 180, or 270.
//        values.put(MediaStore.Images.ImageColumns.ORIENTATION, orientation);
//        values.put(MediaStore.Images.ImageColumns.DATA, path);
//        values.put(MediaStore.Images.ImageColumns.SIZE, jpegLength);
//
//        setImageSize(values, width, height);
//
//        if (location != null) {
//            values.put(MediaStore.Images.ImageColumns.LATITUDE, location.getLatitude());
//            values.put(MediaStore.Images.ImageColumns.LONGITUDE, location.getLongitude());
//        }
//        return values;
//    }
    public static void addImageToMediaStore(final String title, final String path, final String AUTHOR) {
                ContentResolver resolver = EcoApplication.instance.getContentResolver();
                // Insert into MediaStore.
                ContentValues values = getContentValuesForData(title, path, AUTHOR);

                try {
                     resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                } catch (Throwable th) {
                    // This can happen when the external volume is already mounted, but
                    // MediaScanner has not notify MediaProvider to add that volume.
                    // The picture is still safe and MediaScanner will find it and
                    // insert it into MediaProvider. The only problem is that the user
                    // cannot click the thumbnail to review the picture.
                    Trace.Error(TAG, "Failed to write MediaStore" + th);
                }


    }
    public static ContentValues getContentValuesForData(String title,String path,String AUTHOR) {

        File file = new File(path);
//        long dateModifiedSeconds = TimeUnit.MILLISECONDS.toSeconds(file
//                .lastModified());

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, title);//音乐的名字
        values.put(MediaStore.Audio.Media.DATA, path);//音乐的根路径
//        values.put(MediaStore.Audio.Media.DATE_MODIFIED, dateModifiedSeconds);
        values.put(MediaStore.Images.Media.SIZE, file.length());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
//        values.put(MediaStore.Audio.Media._ID,AUDIO_ID);
//        values.put(MediaStore.Audio.Media.ALBUM_ID,ALBUM_ID);
        values.put(MediaStore.Audio.Media.ARTIST,AUTHOR);

        return values;
    }

}
