package com.letv.leauto.ecolink.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;


import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.cfg.SettingMusicCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;

import com.letv.leauto.ecolink.download.DownloadManager;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liweiwei1 on 2015/7/22.
 */
public class MusicDownloadManager {
    //可以收藏的CP
    static String[] SOURCE_CP_ID_ARRAY_FAVOR = {
            "7d38fe21-0a0d-4426-b88c-fafad72a7650"/*多听*/,
            "55b2de5e-5271-4c56-8f79-5a6fc84ccdae"/*蜻蜓*/,
            "964c31d3-8ffb-4656-ace6-1602d5befe12"/*考拉*/,
            "667969a6-2bbc-11e5-b07a-fa163e6f7961"/*听头条*/,
            "c923fb1a-20a0-11e5-b63e-fa163e6f7961"/*乐视车联*/,
            "cc431a6a-3590-11e5-b07a-fa163e6f7961"/*cms发布到乐听库的虾米音乐*/,
            "05d039c8-4d30-11e5-b785-fa163e6f7961"/*乐视点播*/,
            "c9e0c736-3590-11e5-b07a-fa163e6f7961"/*乐视体育点播*/,
            "b2aaef5e-3f24-11e5-b43b-fa163e6f7961"/*乐视音乐点播*/,
            "3434be20-424e-11e5-b43b-fa163e6f7961"/*广播*/
    };
    //可以下载的CP
    static String[] SOURCE_CP_ID_ARRAY_DOWNLOAD = {
            "7d38fe21-0a0d-4426-b88c-fafad72a7650"/*多听*/,
            "55b2de5e-5271-4c56-8f79-5a6fc84ccdae"/*蜻蜓*/,
            "667969a6-2bbc-11e5-b07a-fa163e6f7961"/*听头条*/,
            "c923fb1a-20a0-11e5-b63e-fa163e6f7961"/*乐视车联*/,
            "964c31d3-8ffb-4656-ace6-1602d5befe12"/*考拉*/};





    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        ContentResolver contentResolver = EcoApplication.instance.getContentResolver();
        Trace.Error("====filePath=", filePath);
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            //扫描文件,不然不能及时扫描出来
//            ScanLocalFile.scanDirAsync(EcoApplication.getInstance().getApplicationContext(),filePath);
            boolean state=file.delete();
            if (state){
                contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Audio.Media.DATA + "=?", new String[]{file.getAbsolutePath()});
                return state;
            }
//            if (contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    MediaStore.Audio.Media.DATA + "=?", new String[]{file.getAbsolutePath()}) == 1) {
//                Log.e("====d=", "删除" + file.getAbsolutePath() + "成功");
//            }
        }else {
            contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Audio.Media.DATA + "=?", new String[]{file.getAbsolutePath()});
        }
        return false;
    }


    private static void removeAllDownloadFiles() {
        try {
            ContentResolver contentResolver = EcoApplication.instance.getContentResolver();
            String mPath = DeviceUtils.getMusicCachePath();
            File downRoot = new File(mPath);
            if (!downRoot.exists()) {
                return;
            }
            if (downRoot.isFile()) {
                downRoot.delete();
                downRoot.mkdirs();
                return;
            }
            File[] files = downRoot.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            for (File f : files) {
                Trace.Error("====d=" ,contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Audio.Media.DISPLAY_NAME+"=?",new String[]{f.getName()})+"");

                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否大于500M
     *
     * @param context
     */
    public static boolean isDownloadFileMaximum(Context context) {
        String mPath = DeviceUtils.getMusicCachePath();
        File file = new File(mPath);
        long fileSize = 0;
        try {
            fileSize = getFileSize(file);
        } catch (Exception e) {
        }
        if (fileSize > Constant.MAXIMUM_CAPACITY) {
            return true;
        } else {
            return false;
        }
    }
    public static void setMaxCacheSize(Context context,int size) {
        CacheUtils.getInstance(context).putInt(SettingMusicCfg.DOWNLOAD_SIZE_SELECTE,size);
    }
    public static int getMaxCacheSize(Context context) {
        int size = CacheUtils.getInstance(context).getInt(SettingMusicCfg.DOWNLOAD_SIZE_SELECTE,500);
        return size;
    }
    public static String getCacheSize() {
        String mPath = DeviceUtils.getMusicCachePath();
        File file = new File(mPath);
        String fileSizeString = "0M";
        long fileS = 0;
        DecimalFormat df = new DecimalFormat("#.00");
        try {
            fileS = getFileSize(file);
            if (fileS < 1024) {
                fileSizeString = 0 + "M";
            } else if (fileS < 1073741824) {
                fileSizeString = df.format((double) fileS / 1048576) + "M";
            } else {
                fileSizeString = df.format((double) fileS / 1073741824) + "G";
            }
            return fileSizeString;
        } catch (Exception e) {
        }
        return fileSizeString;
    }

    private static long getFileSize(File f) throws Exception {
        long size = 0;
        File[] flist = f.listFiles();
        if (flist != null && flist.length > 0) {
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        }
        Trace.Debug("FileSize", size + "");
        return size;
    }

    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    public static void startDownloadLeradioWithServer(final Handler handler, final LeAlbumInfo argAlbumInfo, List<MediaDetail> mediaDetails){

        Trace.Debug("#####startDownloading with Server" +mediaDetails.size());
        if (mediaDetails == null || mediaDetails.size() == 0) {
            return;
        }
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.addAll(handler,mediaDetails, SortType.SORT_LE_RADIO_LOCAL);
    }
    //酷我下载
    public static void startDownloadKuwoWithServer(final Handler handler, List<MediaDetail> detailList){
        final List<MediaDetail> mediaDetails=new ArrayList<>();
        mediaDetails.addAll(detailList);
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.addAll(handler,mediaDetails, SortType.SORT_KUWO_LOCAL);


    }


    /**
     * @param detail
     * 下载单曲
     */
    public static void startDownloadLeradio(final MediaDetail detail){
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.add(detail, SortType.SORT_LE_RADIO);
        /*new Thread(){
            @Override
            public void run() {
                Context context = EcoApplication.instance;
                Trace.Debug("#####startDownloading");
                if (detail == null) {
                    return;
                }
                android.app.DownloadManager downloadManager = (android.app.DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                String downloadUrl = detail.SOURCE_URL;
                Trace.Debug("#####downloadUrl = "+ downloadUrl);
                Uri uri = Uri.parse(downloadUrl);
                if (downloadUrl == null || downloadUrl.equals("")) {
                    return;
                }
                android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(uri);
                //只允许在wifi状态下载
//        request.setAllowedNetworkTypes(android.app.DownloadManager.Request.NETWORK_WIFI);
                request.setNotificationVisibility(View.GONE);
                request.setShowRunningNotification(false);
                String name = DeviceUtils.getMusicCachePath();;
                File folder = new File(name);
                if (!(folder.exists() && folder.isDirectory())){
                    folder.mkdirs();
                }
                File file=new File(folder.getPath()+File.separator+detail.NAME+".mp3");
                if (file.exists()){
                    detail.DOWNLOAD_FLAG = MediaDetail.State.STATE_FINISH;
                    detail.TYPE = SortType.SORT_DOWNLOAD;
                    //更新下载ID和下载状态
                    MediaOperation.getInstance().updateDetailDownloadStateId(detail);
                    return;
                }
                //存储于根目录LeAuto文件夹里面
                request.setDestinationInExternalPublicDir("Ecolink/Music/", detail.NAME+".mp3");
                //返回的唯一值
                long downloadId = downloadManager.enqueue(request);

                //不显示下载界面
                request.setVisibleInDownloadsUi(false);
                detail.DOWNLOAD_FLAG = MediaDetail.State.STATE_DOWNLOADING;
                detail.DOWNLOAD_ID = Long.toString(downloadId);
                detail.TYPE = SortType.SORT_DOWNLOAD;
                //更新下载ID和下载状态
                MediaOperation.getInstance().updateDetailDownloadStateId(detail);

            }
        }.start();
*/
    }

    /**
     * 把需要下载的链接添加到下载列表
     *
     * @param detail
     */


    /**
     * 删除选中的歌曲，酷我
     *
     */
//    artPath=DeviceUtils.getMusicCachePath()
    public static void deleteKuwoMediaList(final Handler handler, final List<MediaDetail> argList) {
//       mediaDetail.SOURCE_URL= /storage/emulated/0/Ecolink/KuWoMusic/Pride.mp3
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<argList.size();i++){
                    if (deleteFile(argList.get(i).SOURCE_URL)) {
                        Trace.Error("=deleteFile=", "删除成功0");
                    } else {
                        Trace.Error("=deleteFile=", "删除失败");
                    }
                }
//                for (MediaDetail mediaDetail : argList) {
//
//                }
                Message message = Message.obtain();
                message.what = MessageTypeCfg.MSG_REFRESH_COMPLETED;
                handler.sendMessage(message);
            }
        }).start();

    }

}
