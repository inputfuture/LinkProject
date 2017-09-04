package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.LeSortInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.model.OnStatusChangedListener;
import com.letv.leauto.ecolink.ui.LocalMusicFragment.LoaclMusicAdapter;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * 2 用于展示 播放界面音乐列表
 */
public class MusicListPage extends BasePage implements OnStatusChangedListener,View.OnClickListener {

    private int mCurrentIndex;
    private LeAlbumInfo mAlbumInfo;
    @Bind(R.id.radio_album_list)
    ListView mRadioAlbumList;
    @Bind(R.id.album_name)
    TextView mAlbumNameView;
    private LoaclMusicAdapter loaclMusicAdapter;
    private ArrayList<MediaDetail> mediaDetails=new ArrayList<>();
    private MediaDetail LastPlayMedia = null;
    private LeSortInfo mLeSortInfo;
    private HomeActivity homeActivity;
    private boolean mIsMusicPlayPage;
    public static final int RENCENT=0;
    public static final int MUSIC_PLAY=1;
    private int mCurrentPage=RENCENT;
    @Bind(R.id.clear_history)
    TextView mHistoryView;




    public MusicListPage(Context mContext, LeAlbumInfo albumInfo, ArrayList<MediaDetail> mediaList, int index) {
        super(mContext);
        mIsMusicPlayPage=true;
        mAlbumInfo=albumInfo;
        mediaDetails=mediaList;
        mCurrentIndex=index;

    }

    @Override
    public void destory() {
        super.destory();
        if (mediaDetails != null) {
            mediaDetails.clear();
        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT){
            view = inflater.inflate(R.layout.page_music_list, null);
        }else{
            view = inflater.inflate(R.layout.page_music_list_1, null);
        }



        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void initData() {
        lePlayer.setOnStatusChangedListener(this);

        mediaDetails=lePlayer.getPlayerList();
        int index=lePlayer.getIndex();
        loaclMusicAdapter =new LoaclMusicAdapter( ct,mediaDetails,lePlayer);
        mRadioAlbumList.setAdapter(loaclMusicAdapter);
        mRadioAlbumList.setSelection(index);
        mRadioAlbumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mediaDetails != null && mediaDetails.size() > 0) {
                    if (position >= 0 && position < mediaDetails.size()) {
                        if(LastPlayMedia!=mediaDetails.get(position)&&(position!=lePlayer.getIndex()) ){
                            lePlayer.playList(position);
                            LastPlayMedia = mediaDetails.get(position);
                        }
                        loaclMusicAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

        if (mAlbumInfo.NAME!=null&&!mAlbumInfo.NAME.toLowerCase().equals("null")){
            mAlbumNameView.setVisibility(View.VISIBLE);
            mAlbumNameView.setText(mAlbumInfo.NAME);

        }else{
           // mAlbumNameView.setVisibility(View.GONE);
            mAlbumNameView.setText(R.string.play_list);
        }
        mAlbumNameView.setText(R.string.play_list);


    }
    private void updateAlbumInfo(ArrayList<MediaDetail> list) {
        if (list != null && list.size() > 0) {
            mHistoryView.setVisibility(View.VISIBLE);
            mediaDetails.addAll(list);
            Trace.Debug("#### recent ="+mediaDetails.toString());
            loaclMusicAdapter.notifyDataSetChanged();

        }else {
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
        loaclMusicAdapter.notifyDataSetChanged();
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
                MediaOperation.getInstance().deleteMediasByType(SortType.SORT_RECENT);
                mHistoryView.setVisibility(View.GONE);
                mediaDetails.clear();
                loaclMusicAdapter.notifyDataSetChanged();
                break;
        }
    }
}
