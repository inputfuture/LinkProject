package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.http.leradio.AlbumListLoader;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.library.PullToRefreshBase;
import com.letv.leauto.ecolink.library.PullToRefreshGridView;
import com.letv.leauto.ecolink.thincar.protocol.LeRadioSendHelp;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.fragment.AlbumListFragment;
import com.letv.leauto.ecolink.ui.leradio_interface.RadiaoAlbumGridAdapter;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.mobile.core.utils.Constants;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * gridView 形式的
 */
public class RadioAlbumPage extends BasePage implements AdapterView.OnItemClickListener,View.OnClickListener{

    @Bind(R.id.radio_album_list)
    PullToRefreshGridView mAlbumGridView;
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
    private RadiaoAlbumGridAdapter radiaoAlumAdapter;
    private ArrayList<LeAlbumInfo> mAlbumList;
    private boolean mIsInit;
    private HomeActivity homeActivity;
    private int mIndex = 1;//当前刷新页面的索引
    private Channel mChannel;
    public RadioAlbumPage(Context context, Channel argChannels) {
        super(context);
        mChannel = argChannels;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MessageTypeCfg.MSG_SUBITEMS_OBTAINED:
                    mIndex++;
                    mAlbumGridView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mNoDateView.setVisibility(View.GONE);
                    mConnectView.setVisibility(View.GONE);
                    mAlbumGridView.onRefreshComplete();
                    LeObject<LeAlbumInfo> result = (LeObject<LeAlbumInfo>) msg.obj;
                    if (result !=null && result.list!=null){
                        updateAlbumInfo(result.list);
                    }else{
                        ToastUtil.show(ct, R.string.no_more_data);
                    }

                    break;
                case MessageTypeCfg.MSG_GETDATA_FAILED:
                    mAlbumGridView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mAlbumGridView.onRefreshComplete();
                    mWaitView.setImageBitmap(null);
                    mNoDateView.setVisibility(View.VISIBLE);
                    mConnectView.setVisibility(View.GONE);
                    mNoDateView.setText("没有数据");

                    break;
                case MessageTypeCfg.MSG_GETDATA_EXCEPTION:
                    mAlbumGridView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mConnectView.setVisibility(View.GONE);
                    mAlbumGridView.onRefreshComplete();
                    mNoDateView.setVisibility(View.VISIBLE);
                    mNoDateView.setText("网络出现错误");
                    break;
            }
        }
    };
    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.page_radio_album, null);

        } else {
            view = inflater.inflate(R.layout.page_radio_album_1, null);

        }
        ButterKnife.bind(this, view);
        mRefreshView.setOnClickListener(this);
        mLocalToseeView.setOnClickListener(this);
        mAlbumGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mAlbumGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                Trace.Debug("######onPullDownToRefresh");
                refreshView();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                Trace.Debug("######onPullUpToRefresh");
                refreshView();

            }
        });
        homeActivity = (HomeActivity) ct;
        return view;
    }

    private void refreshView() {
        if (NetUtils.isConnected(ct)) {
            // DataUtil.getInstance().getNewLeAlbumList(handler,ct, mChannel);
            AlbumListLoader loader = new AlbumListLoader(ct, handler, mChannel);
            loader.load(mChannel.getPageId(),mIndex);
            mNoDateView.setVisibility(View.GONE);
            mConnectView.setVisibility(View.GONE);
        } else {
            mNoDateView.setVisibility(View.GONE);
            mAlbumGridView.setVisibility(View.GONE);
            mWaitView.setVisibility(View.GONE);
            mConnectView.setVisibility(View.VISIBLE);
        }
    }


    private void updateAlbumInfo(ArrayList<LeAlbumInfo> albums) {
        //mAlbumList.clear();
        if (mAlbumList!=null && albums != null && albums.size() > 0) {
            mAlbumList.addAll(albums);
            radiaoAlumAdapter.notifyDataSetChanged();
            LeRadioSendHelp.getInstance().setTempAlbumList(mAlbumList);
            LeRadioSendHelp.getInstance().updateCurrentAlbumList(mChannel,albums);
        }else {
            if(mAlbumList==null || (mAlbumList!=null && mAlbumList.size()==0)) {
                mNoDateView.setVisibility(View.VISIBLE);
                mNoDateView.setText("没有数据");
            }

        }
    }

    @Override
    public void initData() {
        if (!mIsInit){
            LeRadioSendHelp.getInstance().setCurrentChooseChannel(mChannel);
            mAlbumList=new ArrayList<>();
            radiaoAlumAdapter = new RadiaoAlbumGridAdapter(ct, mAlbumList);
            mAlbumGridView.setAdapter(radiaoAlumAdapter);
            mWaitView.setVisibility(View.VISIBLE);
            Glide.with(ct).load(R.drawable.loading_gif).into(mWaitView);
            mNoDateView.setVisibility(View.GONE);
            mAlbumGridView.setOnItemClickListener(this);
            mConnectView.setVisibility(View.GONE);
            if (NetUtils.isConnected(ct)){
                // DataUtil.getInstance().getNewLeAlbumList(handler,ct, mChannel);
                AlbumListLoader loader = new AlbumListLoader(ct, handler, mChannel);
                loader.load(mChannel.getPageId(),mIndex);
                mIsInit=true;
            }else{
                mNoDateView.setVisibility(View.GONE);
                mAlbumGridView.setVisibility(View.GONE);
                mWaitView.setVisibility(View.GONE);
                mConnectView.setVisibility(View.VISIBLE);

            }
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        FragmentManager manager = ((HomeActivity) ct).getSupportFragmentManager();
//        FragmentTransaction transaction= manager.beginTransaction();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(Constants.CHANNEL_FOCUS, mAlbumList.get(position));
//        musicListFragment.setArguments(bundle);
//        Fragment leRadioAlumFragment= manager.findFragmentByTag("LeRadioAlumFragment");
//        if (leRadioAlumFragment!=null){
//            transaction.hide(leRadioAlumFragment);
//        }
//        ((HomeActivity) ct).getSupportFragmentManager().beginTransaction().add(R.id.music_frame, musicListFragment, "MusicListFragment").commitAllowingStateLoss();

        radiaoAlumAdapter.setSeclection(position);
        LeAlbumInfo item = mAlbumList.get(position);

        FragmentManager manager = ((HomeActivity) ct).getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        AlbumListFragment albumListFragment=new AlbumListFragment();
        Bundle bundle=new Bundle();
        bundle.putParcelable(Constants.CHANNEL_FOCUS,item);
        albumListFragment.setArguments(bundle);
        Fragment leRadioAlumFragment= manager.findFragmentByTag("LeRadioAlumFragment");
        if (leRadioAlumFragment!=null){
            transaction.hide(leRadioAlumFragment);
        }
        transaction.add(R.id.music_frame, albumListFragment, "AlbumListFragment").commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_local_see:
                homeActivity.changeToLocal();
                break;
            case R.id.bt_refresh:
                refreshView();
                break;
        }
    }

    @Override
    public void destory() {
        super.destory();
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
        if (mAlbumList!=null){
            mAlbumList.clear();
        }
        radiaoAlumAdapter=null;
    }
}
