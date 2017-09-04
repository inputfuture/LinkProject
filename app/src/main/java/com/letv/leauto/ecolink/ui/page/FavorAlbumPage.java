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
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.RadiaoAlbumGridAdapter;
import com.letv.leauto.ecolink.cfg.EnvStatus;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.LeReqTag;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.LeSortInfo;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.fragment.AlbumListFragment;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.mobile.core.utils.Constants;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * gridView 形式的
 */
public class FavorAlbumPage extends BasePage implements AdapterView.OnItemClickListener,View.OnClickListener{

    @Bind(R.id.radio_album_list)
    GridView mAlbumGridView;
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
    private LeSortInfo mLeSortInfo;
    private boolean mIsInit;
    private HomeActivity homeActivity;
    private ScrollView mScrollView;


    public FavorAlbumPage(Context context, LeSortInfo leSortInfo, ScrollView scrollView) {
        super(context);
        mLeSortInfo=leSortInfo;
        mScrollView=scrollView;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MessageTypeCfg.MSG_SUBITEMS_OBTAINED: {
                    mAlbumGridView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mNoDateView.setVisibility(View.GONE);
                    mConnectView.setVisibility(View.GONE);
                    LeObject<LeAlbumInfo> result = (LeObject<LeAlbumInfo>) msg.obj;
                    updateAlbumInfo(result.list);
                }
                break;


            }
        }
    };

    @Override
    protected View initView(LayoutInflater inflater) {
        View  view = inflater.inflate(R.layout.favor_radio_album, null);
        ButterKnife.bind(this, view);
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mAlbumGridView.getLayoutParams();
        if (GlobalCfg.IS_POTRAIT) {
            mAlbumGridView.setNumColumns(2);
            params.leftMargin=ct.getResources().getDimensionPixelSize(R.dimen.size_40dp);
            params.rightMargin=ct.getResources().getDimensionPixelSize(R.dimen.size_40dp);
        } else {
            mAlbumGridView.setNumColumns(4);
            params.leftMargin=ct.getResources().getDimensionPixelSize(R.dimen.size_60dp);
            params.rightMargin=ct.getResources().getDimensionPixelSize(R.dimen.size_60dp);
        }
        mAlbumGridView.setLayoutParams(params);
        mRefreshView.setOnClickListener(this);
        mLocalToseeView.setOnClickListener(this);
        homeActivity = (HomeActivity) ct;
        mAlbumList=new ArrayList<>();
        radiaoAlumAdapter =new RadiaoAlbumGridAdapter(ct,mAlbumList);
        mAlbumGridView.setAdapter(radiaoAlumAdapter);
        mWaitView.setVisibility(View.VISIBLE);
        Glide.with(ct).load(R.drawable.loading_gif).into(mWaitView);
        mNoDateView.setVisibility(View.GONE);
        mAlbumGridView.setOnItemClickListener(this);
        mConnectView.setVisibility(View.GONE);
        return view;
    }

    private void refreshView() {
        Trace.Debug("####getfrom db");
        DataUtil.getInstance().getAlbumFromDB(handler, curSort.TYPE, curSort.SORT_ID);
        mNoDateView.setVisibility(View.GONE);
        mConnectView.setVisibility(View.GONE);



    }

    LeSortInfo curSort;
    @Override
    public void initData() {
        curSort = mLeSortInfo;
        EnvStatus.Sort_Id = curSort.SORT_ID;
        EnvStatus.Sort_Type = curSort.TYPE;
        EnvStatus.Album_Id = DataUtil.ALBUM_LIST + "_" + curSort.SORT_ID;

        DataUtil.getInstance().getAlbumFromDB(handler, curSort.TYPE, LeReqTag.TAG_LOCAL);



    }
    private void updateAlbumInfo(ArrayList<LeAlbumInfo> albums) {
        mAlbumList.clear();
        if (albums != null && albums.size() > 0) {
            mAlbumList.clear();
            for(int i =albums.size()-1;i>=0 ; i--){
                mAlbumList.add(albums.get(i));
            }
            //mAlbumList.addAll(albums);
            radiaoAlumAdapter.notifyDataSetChanged();
            mAlbumGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
//                    mScrollView.smoothScrollTo(0,0);
                    mAlbumGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
//            mScrollView.smoothScrollTo(0,0);
        }else {

            mNoDateView.setVisibility(View.VISIBLE);
            mNoDateView.setText(R.string.str_not_collection);

        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler=null;

        }
        mAlbumList.clear();
    }
}
