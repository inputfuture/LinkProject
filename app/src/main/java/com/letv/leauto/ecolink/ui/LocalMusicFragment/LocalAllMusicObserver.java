package com.letv.leauto.ecolink.ui.LocalMusicFragment;

import android.app.Activity;

import com.letv.leauto.ecolink.adapter.KuwoDownLoadingAdapter;
import com.letv.leauto.ecolink.database.model.MediaDetail;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by fuqinqin on 2016/9/8.
 */
public class LocalAllMusicObserver implements Observer {
    private WeakReference<KuwoDownLoadingAdapter> mAdapter;
    private List<MediaDetail> mList;
    private Activity mActivity;
    public LocalAllMusicObserver(Activity argActitity,WeakReference<KuwoDownLoadingAdapter> adapter){
        mAdapter = adapter;
        mList = new ArrayList<>();
        mActivity=argActitity;
    }

    @Override
    public void update(Observable observable, final Object data) {
        if(data instanceof List){
            if(mAdapter != null) {
                final KuwoDownLoadingAdapter adapter = mAdapter.get();
                if(adapter != null) {
                    mList.clear();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (this) {
                                List<MediaDetail> list= (List<MediaDetail>) data;
                                Iterator<MediaDetail> iterator = list.iterator();
                                while(iterator.hasNext()){
                                    MediaDetail downloadInfo= iterator.next();
                                   if(downloadInfo.DOWNLOAD_FLAG != MediaDetail.State.STATE_FINISH) {
                                        mList.add(0,downloadInfo);
                                    }
                                }

                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

                }
            }
        }
    }
}
