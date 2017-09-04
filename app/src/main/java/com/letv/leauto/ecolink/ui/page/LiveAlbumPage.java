package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.LiveAlbumAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.leplayer.model.OnStatusChangedListener;
import com.letv.leauto.ecolink.library.PullToRefreshBase;
import com.letv.leauto.ecolink.library.PullToRefreshListView;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wanghuayan
 */
public class LiveAlbumPage extends BasePage implements OnStatusChangedListener,PullToRefreshBase.OnRefreshListener2<ListView>,View.OnClickListener{

    @Bind(R.id.radio_album_list)
    PullToRefreshListView mListView;
    @Bind(R.id.wait_view)
    ImageView mWaitView;
    @Bind(R.id.nodate)
    TextView mNoDateView;
    @Bind(R.id.connect_view)
    RelativeLayout mConnectView;
    @Bind(R.id.bt_refresh)
    TextView mRefreshView;
    @Bind(R.id.bt_local_see)
    TextView mLocalToseeView;
    private LiveAlbumAdapter mLiveAlbumAdapter;
    private ArrayList<LeAlbumInfo> mAlbumList;
    private Channel mLeSortInfo;
    private ArrayList<MediaDetail> mediaDetails;
    HomeActivity homeActivity;



    public LiveAlbumPage(Context context, Channel leSortInfo) {
        super(context);
        mLeSortInfo=leSortInfo;
        lePlayer.setOnStatusChangedListener(this);
//        sorts.add(new LeSortInfo("直播", null, "2400"));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageTypeCfg.MSG_SUBITEMS_OBTAINED: {
                    mListView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mNoDateView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mListView.onRefreshComplete();
                    LeObject<LeAlbumInfo> result = (LeObject<LeAlbumInfo>) msg.obj;
                    updateAlbumInfo(result.list);}
                break;
                case MessageTypeCfg.MSG_GETDATA_FAILED:
                    mListView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mNoDateView.setVisibility(View.VISIBLE);
                    mWaitView.setImageBitmap(null);
                    mListView.onRefreshComplete();
                    mNoDateView.setText(R.string.no_data_toast);
                    break;
                case MessageTypeCfg.MSG_GETDATA_EXCEPTION:
                    mListView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mNoDateView.setVisibility(View.VISIBLE);
                    mListView.onRefreshComplete();
                    mNoDateView.setText(R.string.net_erro_toast);
                    break;
                case MessageTypeCfg.MSG_LIVE_MEDIALST_OBTAINED:
                    mediaDetails= (ArrayList<MediaDetail>) msg.obj;
                    break;

            }
        }
    };

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT){
            view = inflater.inflate(R.layout.page_live_album, null);
        }else{
            view = inflater.inflate(R.layout.page_live_album_1, null);
        }
        homeActivity = (HomeActivity) ct;
        ButterKnife.bind(this, view);
        mListView.setOnRefreshListener(this);

        mRefreshView.setOnClickListener(this);
        mLocalToseeView.setOnClickListener(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mediaDetails != null && mediaDetails.size() > 0) {
                    if (position >= 0 && position <=mediaDetails.size()) {
                        lePlayer.TYPE=1;
                        lePlayer.setPlayerList(mediaDetails);
                        LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
                        leAlbumInfo.TYPE= SortType.SORT_LIVE;
                        leAlbumInfo.NAME=ct.getString(R.string.musice_live_list);
                        lePlayer.setAlbumInfo(leAlbumInfo);
                        lePlayer.playList(position-1);
                        homeActivity.changeToPlayMusic();
                        GlobalCfg.MUSIC_TYPE = Constant.TAG_LERADIO;
                    }
                }

            }
        });



        return view;
    }
    private boolean mIsInit;
    Channel mCurSort;
    @Override
    public void initData() {
        if (!mIsInit) {
            mCurSort = mLeSortInfo;

            Glide.with(ct).load(R.drawable.loading_gif).into(mWaitView);
            mAlbumList = new ArrayList<>();
            mLiveAlbumAdapter = new LiveAlbumAdapter(ct, mAlbumList);
            mListView.setAdapter(mLiveAlbumAdapter);
//            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    mLiveAlbumAdapter.setSeclection(position);
//                    mLiveAlbumAdapter.notifyDataSetChanged();
//                }
//            });

            if (NetUtils.isConnected(ct)){
                mConnectView.setVisibility(View.GONE);
                DataUtil.getInstance().getAlbumList(handler,null, "2400");
                mIsInit = true;
            }else{
                mListView.setVisibility(View.GONE);
                mNoDateView.setVisibility(View.GONE);
                mWaitView.setVisibility(View.GONE);
                mConnectView.setVisibility(View.VISIBLE);

            }
        }
    }
    private void updateAlbumInfo(ArrayList<LeAlbumInfo> albums) {
        mAlbumList.clear();
        if (albums != null && albums.size() > 0) {
            Trace.Debug("LiveAlbumPage"," #### live "+albums.toString());
            mAlbumList.addAll(albums);
            mLiveAlbumAdapter.notifyDataSetChanged();
            DataUtil.getInstance().requestLiveMedialist(handler,"tag",mAlbumList,null);
        }
    }



    private void refreshView() {
        if (NetUtils.isConnected(ct)){
            mConnectView.setVisibility(View.GONE);
            DataUtil.getInstance().getAlbumList(handler, null,"2400");

        }else{
            mListView.setVisibility(View.GONE);
            mNoDateView.setVisibility(View.GONE);
            mWaitView.setVisibility(View.GONE);
            mConnectView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        refreshView();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_refresh:
                refreshView();
                break;
            case R.id.bt_local_see:
                homeActivity.changeToLocal();
                break;
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
        if (mLiveAlbumAdapter!=null){
        mLiveAlbumAdapter.notifyDataSetChanged();}
    }

    @Override
    public void onError(int errCode, int extra) {

    }

    @Override
    public void onPrepared() {

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
