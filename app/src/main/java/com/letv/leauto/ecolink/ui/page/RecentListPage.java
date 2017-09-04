package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.AlbumListAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.EnvStatus;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.LeSortInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.leplayer.model.OnStatusChangedListener;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 用于展示历史记录最近播放的曲子
 *
 */
public class RecentListPage extends BasePage implements OnStatusChangedListener,View.OnClickListener /*, PullToRefreshBase.OnRefreshListener2<ListView>*/{

    @Bind(R.id.radio_album_list)
    ListView mRadioAlbumList;
    @Bind(R.id.album_name)
    TextView mAlbumNameView;
    @Bind(R.id.nodate)
    TextView mNodateView;
    private AlbumListAdapter radiaoAlumAdapter;
    private ArrayList<MediaDetail> mediaDetails;
    private LeSortInfo mLeSortInfo;
    public static final int RENCENT=0;
    public static final int MUSIC_PLAY=1;
    private int mCurrentPage=RENCENT;
    private boolean isRecent =true;
    @Bind(R.id.clear_history)
    TextView mHistoryView;
    HomeActivity homeActivity;
    private ScrollView mScrollView;
    @Bind(R.id.recent_diver)
    LinearLayout mDiverView;



    public RecentListPage(Context context, LeSortInfo leSortInfo, ScrollView scrollView) {
        super(context);
        mLeSortInfo=leSortInfo;
        mScrollView=scrollView;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageTypeCfg.MSG_FROM_RECENT: {
                    LeObject<MediaDetail> result = (LeObject<MediaDetail>) msg.obj;
                    updateAlbumInfo(result.list);}
                break;

            }
        }
    };



    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT){
            view = inflater.inflate(R.layout.page_music_recent, null);
        }else{
            view = inflater.inflate(R.layout.page_music_recent_1, null);
        }
        homeActivity = (HomeActivity) ct;
        ButterKnife.bind(this, view);
        lePlayer.setOnStatusChangedListener(this);
        mAlbumNameView.setVisibility(View.GONE);
        mHistoryView.setOnClickListener(this);
        mediaDetails=new ArrayList<>();
        radiaoAlumAdapter =new AlbumListAdapter( mediaDetails,ct,lePlayer,isRecent);
        mRadioAlbumList.setAdapter(radiaoAlumAdapter);
        mRadioAlbumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mediaDetails != null && mediaDetails.size() > 0) {
                    lePlayer.setPlayerList(mediaDetails);
                    LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
                    leAlbumInfo.TYPE=mediaDetails.get(0).TYPE;
                    if (position >= 0 && position < mediaDetails.size()) {
                        lePlayer.TYPE=1;
                        lePlayer.setAlbumInfo(leAlbumInfo);
                        lePlayer.playList(position);
                        homeActivity.changeToPlayMusic();
                        GlobalCfg.MUSIC_TYPE = Constant.TAG_LERADIO;
                        radiaoAlumAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        return view;
    }


    @Override
    public void initData() {

        LeSortInfo curSort = mLeSortInfo;
        EnvStatus.Sort_Id = curSort.SORT_ID;
        EnvStatus.Sort_Type = curSort.TYPE;
        EnvStatus.Album_Id = DataUtil.ALBUM_LIST + "_" + curSort.SORT_ID;

        DataUtil.getInstance().getMediaListFromDB(handler, curSort.TYPE,"0", curSort.SORT_ID);
    }
    private void updateAlbumInfo(ArrayList<MediaDetail> list) {
        if (list != null && list.size() > 0) {
            mHistoryView.setVisibility(View.VISIBLE);
            mediaDetails.clear();
            mediaDetails.addAll(list);
            Trace.Debug("#### recent ="+mediaDetails.toString());
            radiaoAlumAdapter.notifyDataSetChanged();
            mNodateView.setVisibility(View.GONE);
//            mScrollView.smoothScrollTo(0,0);
            mRadioAlbumList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
//                    mScrollView.smoothScrollTo(0,0);
                    mRadioAlbumList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            mDiverView.setVisibility(View.VISIBLE);

        }else {
            mDiverView.setVisibility(View.GONE);
            mNodateView.setVisibility(View.VISIBLE);
            mNodateView.setText(R.string.str_not_listen);
            mHistoryView.setVisibility(View.GONE);

        }
    }


    @Override
    public void onProgressChanged(long progress, long duration) {

    }

    @Override
    public void onVolumeChanged(float leftVolume, float rightVolume) {

    }

    @Override
    public void onSongChanged(String url, int position) {
        radiaoAlumAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(int errCode, int extra) {

    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear_history:
                NetworkConfirmDialog networkConfirmDialog=new NetworkConfirmDialog(ct,R.string.delete_history,R.string.ok,R.string.cancel);
                networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                    @Override
                    public void onConfirm(boolean checked) {
                      //  MediaOperation.getInstance().deleteMediasByType(SortType.SORT_RECENT);
                        MediaOperation.getInstance().deleteAllRecentItems();
                        mHistoryView.setVisibility(View.GONE);
                        mediaDetails.clear();
                        radiaoAlumAdapter.notifyDataSetChanged();
                        mNodateView.setVisibility(View.VISIBLE);
                        mNodateView.setText(R.string.str_not_listen);
                        mDiverView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                networkConfirmDialog.show();

                break;
        }
    }

  /*  @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }*/


    @Override
    public void destory() {
        super.destory();

    }
}
