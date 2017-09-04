package com.letv.leauto.ecolink.receiver;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;


import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by liweiwei1 on 2015/7/20.
 */
public class NetStatReceiver extends BroadcastReceiver {
    //  private Context mContext;
    private State wifiState = null;
    private State mobileState = null;

    String action = null;

    private DownloadManager downloadManager;
    static DownLoadListener mDownLoadListener;

    public static void setDownloadListener(DownLoadListener downloadListener){
        mDownLoadListener=downloadListener;

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        action = intent.getAction();
        downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        //获取手机的连接服务管理器，这里是连接管理器类
        switch (action) {
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                Query query = new Query();
                query.setFilterById(downloadId);
                Cursor c = downloadManager.query(query);
                Trace.Debug("#####download complete");
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)) {
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        MediaOperation.getInstance().updateDetailDownloadStatePath(Long.toString(downloadId), uriString);
                        Trace.Debug("#####download complete onback");
                        if (mDownLoadListener!=null){
                            mDownLoadListener.downloadComplete();
                        }

                    }
                }
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiNetworkInfo =  cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileNetworkInfo= cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiNetworkInfo!=null){
                wifiState = wifiNetworkInfo.getState();}
                if (mobileNetworkInfo!= null) {
                    mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
                }

                if (wifiState != null && mobileState != null && State.CONNECTED != wifiState && State.CONNECTED == mobileState) {
                   Trace.Info("NetState", "mobileState");
                    GlobalCfg.IS_MOBILE_NET = true;
                } else if (wifiState != null && mobileState != null && State.CONNECTED == wifiState && State.CONNECTED != mobileState) {
                   Trace.Info("NetState", "wifiState");
                    GlobalCfg.IS_MOBILE_NET = false;
                } else if (wifiState != null && mobileState != null && State.CONNECTED != wifiState && State.CONNECTED != mobileState) {
                   Trace.Info("NetState", "noNet");
                }
                break;
            default:
                break;
        }
    }


    public interface DownLoadListener {
        void downloadComplete();
    }
}