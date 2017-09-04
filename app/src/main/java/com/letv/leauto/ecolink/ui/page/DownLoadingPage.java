package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.KuwoDownLoadingAdapter;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.download.DownloadEngine;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.kuwodownload.CheckInterface;
import com.letv.leauto.ecolink.download.DownloadManager;
import com.letv.leauto.ecolink.utils.WaitingAnimationDialog;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qu on 16/7/26.
 */
public class DownLoadingPage extends BasePage implements View.OnClickListener, CheckInterface, AdapterView.OnItemClickListener,KuwoDownLoadingAdapter.MusicCountInterface {


    private DownloadManager downloadManager;
    private KuwoDownLoadingAdapter myAdapter;//适配器
    @Bind(R.id.lv_listview)
    ListView lv_listview;
    @Bind(R.id.ll_pauseAll)
    LinearLayout ll_pauseAll;
    @Bind(R.id.ll_playAll)
    LinearLayout ll_playAll;
    @Bind(R.id.ll_selectAll)
    LinearLayout ll_selectAll;
    @Bind(R.id.ll_delete)
    LinearLayout ll_delete;
    @Bind(R.id.ll_startOrpauseAll)
    LinearLayout ll_startOrpauseAll;
    @Bind(R.id.tv_complete)
    TextView tv_complete;
    @Bind(R.id.tv_guanli)
    TextView tv_guanli;
    @Bind(R.id.cb_select_all)
    ImageView cb_select_all;
    @Bind(R.id.ll_title)
    LinearLayout ll_title;
    @Bind(R.id.tv_line)
    TextView tv_line;
    @Bind(R.id.tv_size)
    TextView mTvSize;
    @Bind(R.id.tv_select_all)
    TextView tv_select_all;
    @Bind(R.id.tv_no_downloading)
    TextView mTvNoDownloading;
    private boolean mIsDelete;
    private boolean selectAll=false;
    private boolean mIsFirst;
    public DownLoadingPage(Context context) {
        super(context);

    }
    private int mType;
    public KuwoDownLoadingAdapter getAdapter(){
        return myAdapter;
    }

    /* progress refresh - 1s once*/
    private static final int MSG_PROGRESS_REFRESH = 10;
    /* trigger when one task download state change */
    private static final int MSG_LIST_REFRESH = 11;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_REFRESH:
                    notifyDataChange();
                    break;
                case MessageTypeCfg.MSG_REFRESH_COMPLETED:
                    WaitingAnimationDialog.close();
                    myAdapter.initData();
                    if(myAdapter != null && myAdapter.getCount() == 0){
                        reducingState();
                        DownloadManager.getInstance().resumeAll();
                    }
                    myAdapter.notifyDataSetChanged();
                    break;
                case MSG_LIST_REFRESH:
                    handler.removeMessages(MSG_PROGRESS_REFRESH);
                    myAdapter.initData();
                    WaitingAnimationDialog.close();
                    handler.sendEmptyMessageDelayed(MSG_PROGRESS_REFRESH, 2000);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    public void setType(int type){
        mType = type;
    }




    @Override
    protected View initView(LayoutInflater inflater){
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.kowo_downloading_p, null);
        } else {
            view = inflater.inflate(R.layout.kowo_downloading, null);
        }
        ButterKnife.bind(this, view);
        lv_listview.setFocusable(false);
        ll_pauseAll.setOnClickListener(this);
        ll_selectAll.setOnClickListener(this);
        ll_playAll.setOnClickListener(this);
        ll_delete.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
        tv_guanli.setOnClickListener(this);

        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadManager.unregisterListener(mListener);

    }

    public void initData() {
        downloadManager = DownloadManager.getInstance();
        downloadManager.registerListener(mListener);

        if (myAdapter == null) {
            myAdapter = new KuwoDownLoadingAdapter(ct, downloadManager, mType);
            myAdapter.setCheckInterface(this);
            myAdapter.setMusicCount(this);
            myAdapter.initData();
            lv_listview.setAdapter(myAdapter);
            lv_listview.setOnItemClickListener(this);

        } else {
            myAdapter.initData();
        }
        mIsFirst = true;
        reducingState();

    }

    public void notifyDataChange(){
        if(myAdapter != null && myAdapter.getCount() > 0){
            myAdapter.notifyDataSetChanged();
            handler.sendEmptyMessageDelayed(MSG_PROGRESS_REFRESH, 1000);
        }
    }
public boolean initState;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //管理
            case R.id.tv_guanli:
                mIsDelete = true;
                initState=downloadManager.getStopState();
                downloadManager.stopAll();
                setDeleteAble(mIsDelete);
                ll_pauseAll.setVisibility(View.GONE);
                ll_playAll.setVisibility(View.VISIBLE);
                ll_selectAll.setVisibility(View.VISIBLE);
                ll_delete.setVisibility(View.VISIBLE);
                tv_guanli.setVisibility(View.GONE);
                tv_complete.setVisibility(View.VISIBLE);
                mTvSize.setVisibility(View.GONE);
                ll_startOrpauseAll.setVisibility(View.GONE);

                break;
            //完成
            case R.id.tv_complete:
                reducingState();
//                mIsDelete = false;
                if (!initState && downloadManager.getDownloadings().size() != 0) {
                    downloadManager.resumeAll();
                    ll_pauseAll.setVisibility(View.VISIBLE);
                    ll_playAll.setVisibility(View.GONE);
                }
                break;
            //全部暂停
            case R.id.ll_pauseAll:
                downloadManager.stopAll();
                myAdapter.notifyDataSetChanged();
                ll_pauseAll.setVisibility(View.GONE);
                ll_playAll.setVisibility(View.VISIBLE);
                break;
            //全部开始
            case R.id.ll_playAll:
                downloadManager.resumeAll();
                myAdapter.notifyDataSetChanged();
                ll_pauseAll.setVisibility(View.VISIBLE);
                ll_playAll.setVisibility(View.GONE);
                break;
            //全部选中
            case R.id.ll_selectAll:
                mIsDelete = true;
                setDeleteAble(mIsDelete);
                if (selectAll) {
                    cb_select_all.setImageResource(R.mipmap.song_not_selected);
                    tv_select_all.setText(R.string.all_checked);
                    selectAll = false;
                    myAdapter.setIsAllDownLoad(false);
                } else {
                    cb_select_all.setImageResource(R.mipmap.song_selected);
                    tv_select_all.setText(R.string.all_checked);
                    selectAll = true;
                    myAdapter.setIsAllDownLoad(true);
                }
                break;
            //删除
            case R.id.ll_delete:
                if (myAdapter.getNowMedials().size()!=0){
                    NetworkConfirmDialog networkConfirmDialog=new NetworkConfirmDialog(ct,R.string.delete_download,R.string.ok,R.string.cancel);
                    networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                        @Override
                        public void onConfirm(boolean checked) {
                            downloadManager.stopAll();
                            downloadManager.remove(myAdapter.getNowMedials(), handler);
                            WaitingAnimationDialog.show(ct);
                            unCheckAll();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    networkConfirmDialog.show();
                    networkConfirmDialog.setCancelable(false);
                }

                break;

        }
    }

    private void reducingState() {
        mIsDelete = false;
        setDeleteAble(mIsDelete);
        ll_selectAll.setVisibility(View.GONE);
        ll_delete.setVisibility(View.GONE);
        tv_guanli.setVisibility(View.VISIBLE);
        tv_complete.setVisibility(View.GONE);
        mTvSize.setVisibility(View.VISIBLE);
        ll_startOrpauseAll.setVisibility(View.VISIBLE);
    }

    private void setDeleteAble(boolean argBoolean) {
        if (myAdapter != null) {
            myAdapter.setDelete(argBoolean);
        }
    }

    public void checkAll() {
        cb_select_all.setImageResource(R.mipmap.song_selected);
        tv_select_all.setText(R.string.all_checked);
        selectAll = true;
    }

    @Override
    public void unCheckAll() {
        cb_select_all.setImageResource(R.mipmap.song_not_selected);
        tv_select_all.setText(R.string.all_checked);
        selectAll = false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!mIsDelete){
            myAdapter.setIndex(position);
        }
    }

    @Override
    public void musicSize(int argSize) {
        if (argSize==0){
            ll_title.setVisibility(View.GONE);
            tv_line.setVisibility(View.GONE);
            mTvNoDownloading.setVisibility(View.VISIBLE);
        }else {
            ll_title.setVisibility(View.VISIBLE);
            tv_line.setVisibility(View.VISIBLE);
            mTvNoDownloading.setVisibility(View.GONE);
        }
        mTvSize.setText("("+argSize+")");
    }



    private DownloadEngine.DownloadListener mListener = new DownloadEngine.DownloadListener() {
        @Override
        public void onSuccess(MediaDetail task) {
            handler.removeMessages(MSG_LIST_REFRESH);
            handler.sendEmptyMessageDelayed(MSG_LIST_REFRESH,200);
        }

        @Override
        public void onStart(MediaDetail task) {

        }

        @Override
        public void onPause(MediaDetail task) {

        }

        @Override
        public void onFail(MediaDetail task,int code) {
//            handler.sendEmptyMessage(MSG_LIST_REFRESH);
            handler.removeMessages(MSG_LIST_REFRESH);
            handler.sendEmptyMessageDelayed(MSG_LIST_REFRESH,200);
        }

        @Override
        public void onProgress(MediaDetail task,float percent) {
//            handler.sendEmptyMessage(MSG_LIST_REFRESH);
            handler.removeMessages(MSG_LIST_REFRESH);
            handler.sendEmptyMessage(MSG_LIST_REFRESH);

        }

        @Override
        public void remove() {
            handler.removeMessages(MSG_LIST_REFRESH);
            handler.sendEmptyMessageDelayed(MSG_LIST_REFRESH,200);

        }

        @Override
        public void add() {
            handler.removeMessages(MSG_LIST_REFRESH);
            handler.sendEmptyMessageDelayed(MSG_LIST_REFRESH,200);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(!mIsDelete) {
            reducingState();
        }
        if(mIsFirst){
            mIsFirst = false;
        }
        else{
            handler.removeMessages(MSG_LIST_REFRESH);
            handler.sendEmptyMessage(MSG_LIST_REFRESH);
        }
    }


    @Override
    public void destory() {
        super.destory();
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
    }
}

