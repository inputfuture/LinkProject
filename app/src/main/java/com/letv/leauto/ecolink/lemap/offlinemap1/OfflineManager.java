package com.letv.leauto.ecolink.lemap.offlinemap1;

import android.content.Context;

import com.amap.api.maps.offlinemap.OfflineMapManager;

/**
 * Created by Administrator on 2016/11/24.
 */
public class OfflineManager implements OfflineMapManager.OfflineMapDownloadListener {

    private  volatile  static OfflineManager mthis;

    private OfflineMapManager mManager;

    private  MyOfflineMapDownloadListener mListener;

    private void setListener(MyOfflineMapDownloadListener listener){
        this.mListener=listener;
    }

    private  OfflineManager(Context context){
        mManager=new OfflineMapManager(context,this);
    }

    public synchronized static OfflineManager getInstance(Context context, MyOfflineMapDownloadListener listener){
        if(mthis==null){
            mthis=new OfflineManager(context);
        }
        mthis.setListener(listener);

        return mthis;
    }

    public OfflineMapManager getManager(){
            return  mManager;
    }


    @Override
    public void onDownload(int i, int i1, String s) {
        if(mListener!=null)
        mListener.onDownload(i,i1,s);
    }

    @Override
    public void onCheckUpdate(boolean b, String s) {
        if(mListener!=null)
        mListener.onCheckUpdate(b,s);
    }

    @Override
    public void onRemove(boolean b, String s, String s1) {
        if(mListener!=null)
        mListener.onRemove(b,s,s1);
    }


    public interface MyOfflineMapDownloadListener {
        void onDownload(int var1, int var2, String var3);

        void onCheckUpdate(boolean var1, String var2);

        void onRemove(boolean var1, String var2, String var3);
    }
}
