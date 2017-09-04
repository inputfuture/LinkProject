package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.KuwoDownLoadSuccessAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.download.DownloadManager;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.kuwodownload.CheckInterface;
import com.letv.leauto.ecolink.ui.kuwodownload.Watcher;
import com.letv.leauto.ecolink.ui.view.DeleteDataDialog;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.leauto.ecolink.utils.WaitingAnimationDialog;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DownLoadCompletePage extends BasePage implements View.OnClickListener, CheckInterface,AdapterView.OnItemClickListener,LePlayer.ListViewItemStateListener,DeleteDataDialog.DeleteDataInterface{

    private KuwoDownLoadSuccessAdapter myAdapter;//适配器
    @Bind(R.id.lv_listview)
    ReWriteListView lv_listview;
    @Bind(R.id.tv_guanli)
    TextView tv_guanli;
    @Bind(R.id.tv_complete)
    TextView tv_complete;
    @Bind(R.id.tv_line)
    TextView tv_line;
    @Bind(R.id.tv_select_all)
    TextView tv_select_all;

    @Bind(R.id.ll_delete)
    LinearLayout ll_delete;
    @Bind(R.id.ll_title)
    LinearLayout ll_title;
    @Bind(R.id.cb_select_all)
    ImageView cb_select_all;
    @Bind(R.id.ll_playAll)
    LinearLayout ll_playAll;
    @Bind(R.id.ll_selectAll)
    LinearLayout ll_selectAll;
    @Bind(R.id.tv_play_or_pause)
    TextView tv_play_or_pause;
    @Bind(R.id.iv_play)
    ImageView iv_play;
    @Bind(R.id.tv_no_downloaded)
    TextView mTvNoDownloaded;
    public Boolean selectAll = false;
    private boolean mIsDelete = false;
    private String jsonString;
    private HomeActivity homeActivity;
    public DownLoadCompletePage(Context context) {
        super(context);
    }
    public DeleteDataDialog mDeletaDialog;
    ArrayList<MediaDetail> mediaDetails = new ArrayList<>();
    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessageTypeCfg.MSG_FROM_LOCAL:
                    WaitingAnimationDialog.close();
                    LeObject<MediaDetail> result = (LeObject<MediaDetail>) msg.obj;
                    if (mediaDetails.size() != 0) {
                        mediaDetails.clear();
                    }
                    mediaDetails.addAll(result.list);
                    if (mediaDetails.size()==0){
                        cb_select_all.setImageResource(R.mipmap.song_not_selected);
                        ll_title.setVisibility(View.GONE);
                        tv_line.setVisibility(View.GONE);
                        mTvNoDownloaded.setVisibility(View.VISIBLE);
                    }else {
                        ll_title.setVisibility(View.VISIBLE);
                        tv_line.setVisibility(View.VISIBLE);
                        mTvNoDownloaded.setVisibility(View.GONE);
                    }
                    setAdapter();
                    break;
                case MessageTypeCfg.MSG_REFRESH_COMPLETED:

                    //删除完成,重新获取
                    DataUtil.getInstance().getMediaListFromDB(mHandler, SortType.SORT_KUWO_LOCAL, "all_local", "media");
                    break;
                case MessageTypeCfg.MUSICSTART:
                    if (mediaDetails!=null&&lePlayer.getIndex()<mediaDetails.size()&&lePlayer.getPlayerList()!=null&&mCurrentIndex<lePlayer.getPlayerList().size()){
                        if(lePlayer.getPlayerList() != null && lePlayer.getPlayerList().size() != 0) {
                            if (lePlayer.getPlayerList().get(lePlayer.getIndex()).NAME.equals(mediaDetails.get(lePlayer.getIndex()).NAME)) {
                                iv_play.setImageResource(R.mipmap.button_pause);
                                tv_play_or_pause.setText(R.string.str_all_pause);
                                Trace.Error("==ddd2s", "dd");
                            }
                        }
                    }
                    break;
                case MessageTypeCfg.MUSICINDEX:
                    if (mediaDetails!=null&&mCurrentIndex<mediaDetails.size()&&lePlayer.getPlayerList()!=null&&mCurrentIndex<lePlayer.getPlayerList().size()){
                        if (lePlayer.getPlayerList().get(mCurrentIndex).NAME.equals(mediaDetails.get(mCurrentIndex).NAME)){
                            Trace.Error("==ddds","dd");
                            setAdapter();
                            lv_listview.setSelection(mCurrentIndex);
                            lv_listview.smoothScrollToPosition(mCurrentIndex);
                        }
                    }
                    break;
                case MessageTypeCfg.MUSICSTROP:
                    if (mediaDetails!=null&&lePlayer.getIndex()<mediaDetails.size()&&lePlayer.getPlayerList()!=null&&mCurrentIndex<lePlayer.getPlayerList().size()){
                        if(lePlayer.getPlayerList() != null) {

                            if (lePlayer.getPlayerList().get(lePlayer.getIndex()).NAME.equals(mediaDetails.get(lePlayer.getIndex()).NAME)) {
                                iv_play.setImageResource(R.mipmap.music_play_simle);
                                tv_play_or_pause.setText("播放全部");
                                Trace.Error("==ddd2s", "dd");
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    //观察者模式
    private Watcher watcher = new Watcher() {

        @Override
        public void ontifyDownloadDataChange() {
            Trace.Error("====Watcher===", "====="+System.currentTimeMillis());
            //从数据库中查找酷我数据
            DataUtil.getInstance().getMediaListFromDB(mHandler, SortType.SORT_KUWO_LOCAL, "all_local", "media");
        }
    };
    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.kowo_download_complete_p, null);
        } else {
            view = inflater.inflate(R.layout.kowo_download_complete, null);
        }
        ButterKnife.bind(this, view);
        homeActivity = (HomeActivity) ct;
        lv_listview.setFocusable(false);
        tv_guanli.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
        ll_delete.setOnClickListener(this);
        ll_selectAll.setOnClickListener(this);
        ll_playAll.setOnClickListener(this);
        lv_listview.setOnItemClickListener(this);
        return view;
    }

    public void initData() {

        lePlayer.setListViewItemStateListener(this);
        //从数据库中查找酷我数据
        DataUtil.getInstance().getMediaListFromDB(mHandler, SortType.SORT_KUWO_LOCAL, "all_local", "media");

        if (myAdapter==null){
            Trace.Error("=====1122","");
            myAdapter = new KuwoDownLoadSuccessAdapter(ct, mediaDetails,lePlayer);
            lv_listview.setAdapter(myAdapter);
            myAdapter.setCheckInterface(this);
        }else {
            setAdapter();
        }
        Trace.Error("=====112233","mIsDelete="+mIsDelete);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_guanli:
                mIsDelete = true;
                setDeleteAble(mIsDelete);
                ll_selectAll.setVisibility(View.VISIBLE);
                ll_delete.setVisibility(View.VISIBLE);
                tv_guanli.setVisibility(View.GONE);
                ll_playAll.setVisibility(View.GONE);
                tv_complete.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_complete:
                mIsDelete = false;
                setDeleteAble(mIsDelete);
                ll_selectAll.setVisibility(View.GONE);
                ll_delete.setVisibility(View.GONE);
                tv_guanli.setVisibility(View.VISIBLE);
                tv_complete.setVisibility(View.GONE);
                ll_playAll.setVisibility(View.VISIBLE);
                break;
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
            case R.id.ll_delete:
                if (myAdapter.getNowMedials().size()!=0){
                    if (mDeletaDialog==null){
                        mDeletaDialog=new DeleteDataDialog((HomeActivity)ct,null);
                        mDeletaDialog.setInterface(this);
                        mDeletaDialog.show();
                    }else {
                        mDeletaDialog.show();
                    }
                }

                break;
            case R.id.ll_playAll:
                if (mediaDetails.size() != 0) {
                    if (lePlayer.getIndex()<mediaDetails.size()&&lePlayer.getPlayerList()!=null&&lePlayer.getPlayerList().size()!=0){
                        if (lePlayer.getPlayerList().get(lePlayer.getIndex()).NAME.equals(mediaDetails.get(lePlayer.getIndex()).NAME)&&!lePlayer.getCurrentStatus().isPlaying){
                            HomeActivity.isStoped=false;
                            lePlayer.startPlay();
                        }else if (lePlayer.getPlayerList().get(lePlayer.getIndex()).NAME.equals(mediaDetails.get(lePlayer.getIndex()).NAME)&&lePlayer.getCurrentStatus().isPlaying){
                            HomeActivity.isStoped=true;
                            lePlayer.stopPlay();
                        }else {
                            LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
                            leAlbumInfo.TYPE= SortType.SORT_KUWO_LOCAL;
                            lePlayer.TYPE=2;
                            lePlayer.setAlbumInfo(leAlbumInfo);
                            lePlayer.setPlayerList(mediaDetails);
                            lePlayer.playList(0);
                            homeActivity.changeToPlayMusic();
                            GlobalCfg.MUSIC_TYPE = Constant.TAG_KUWO;
                        }
                    }else {
                        LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
                        leAlbumInfo.TYPE= SortType.SORT_KUWO_LOCAL;
                        lePlayer.TYPE=2;
                        lePlayer.setAlbumInfo(leAlbumInfo);
                        lePlayer.setPlayerList(mediaDetails);
                        lePlayer.playList(0);
                        homeActivity.changeToPlayMusic();
                        GlobalCfg.MUSIC_TYPE = Constant.TAG_KUWO;
                    }

                }
                break;

        }
    }

    private void setDeleteAble(boolean argBoolean) {
        if (myAdapter != null) {
            myAdapter.setDelete(argBoolean);
        }
    }

    private void setAdapter() {
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
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
        if (mediaDetails.size() != 0&&!mIsDelete) {
            KuwoDownLoadSuccessAdapter.ViewHolder holder = (KuwoDownLoadSuccessAdapter.ViewHolder) view.getTag();
            LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
            leAlbumInfo.TYPE= SortType.SORT_KUWO_LOCAL;
            lePlayer.TYPE=2;
            lePlayer.setAlbumInfo(leAlbumInfo);
            lePlayer.setPlayerList(mediaDetails);
            lePlayer.playList(position);
            homeActivity.changeToPlayMusic();
            GlobalCfg.MUSIC_TYPE = Constant.TAG_KUWO;
        }
    }

    @Override
    public void musicStart() {
        mHandler.sendEmptyMessage(MessageTypeCfg.MUSICSTART);

    }

    @Override
    public void musicStop() {
        mHandler.sendEmptyMessage(MessageTypeCfg.MUSICSTROP);

    }
    public int mCurrentIndex;
    @Override
    public void musicIndex(int index) {
        mCurrentIndex=index;
        mHandler.sendEmptyMessage(MessageTypeCfg.MUSICINDEX);



    }

    @Override
    public void delete() {
        ArrayList<MediaDetail> arrayList = (ArrayList<MediaDetail>) myAdapter.getNowMedials();
        if (arrayList != null && arrayList.size() != 0) {
            WaitingAnimationDialog.show(homeActivity);
            DownloadManager manager = DownloadManager.getInstance();
            manager.deleteDownloadFile(arrayList, mHandler);
        }
    }


    @Override
    public void destory() {
        super.destory();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }

    }
}
