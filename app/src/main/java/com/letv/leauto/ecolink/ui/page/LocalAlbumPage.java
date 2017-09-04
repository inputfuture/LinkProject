package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.fragment.LocalAlbumListFragment;
import com.letv.leauto.ecolink.ui.fragment.NewAlbumFragment;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.mobile.core.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/5.
 * 本地歌单界面
 */
public class LocalAlbumPage extends BasePage implements AdapterView.OnItemClickListener,OnItemLongClickListener{
    @Bind(R.id.local_album_grid)
    GridView mGridView;
    List<LeAlbumInfo> mLeAlbumInfos=new ArrayList<>();
    private LocalAlbumAdapter mAdpter;


    private HomeActivity act;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MessageTypeCfg.MSG_SUBITEMS_OBTAINED:
                    LeObject<LeAlbumInfo> albumResult= (LeObject<LeAlbumInfo>) msg.obj;
                    List<LeAlbumInfo> leAlbumInfos=albumResult.list;
                    mLeAlbumInfos.clear();
                    mLeAlbumInfos.addAll(leAlbumInfos);
                    LeAlbumInfo addLeAlbumInfo=new LeAlbumInfo();
                    addLeAlbumInfo.ALBUM_ID="add+";
                    addLeAlbumInfo.NAME="add+";
                    mLeAlbumInfos.add(addLeAlbumInfo);
                    mAdpter.notifyDataSetChanged();


                    break;
            }
        }
    };

    public LocalAlbumPage(Context context) {
        super(context);
        act=(HomeActivity)context;
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.page_local_album_p, null);
        } else {
            view = inflater.inflate(R.layout.page_local_album, null);
        }

        ButterKnife.bind(this,view);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);


        return view;
    }

    @Override
    public void initData() {
        if (mAdpter==null) {
            mAdpter = new LocalAlbumAdapter(ct, mLeAlbumInfos);
            mGridView.setAdapter(mAdpter);
        }
        mLeAlbumInfos.clear();
        DataUtil.getInstance().getAlbumFromDB(mHandler,SortType.SORT_LOCAL_NEW,null);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdpter.setSeclection(position);
        if (position==mLeAlbumInfos.size()-1){
            FragmentManager manager = ((HomeActivity) ct).getSupportFragmentManager();
            FragmentTransaction transaction= manager.beginTransaction();
            NewAlbumFragment newAlbumFragment=new NewAlbumFragment();

            Fragment localMusicFragment= manager.findFragmentByTag("LocalMusicFragment");
            if (localMusicFragment!=null){
                transaction.hide(localMusicFragment);
            }
            transaction.add(R.id.local_music_frame, newAlbumFragment, "NewAlbumFragment").commitAllowingStateLoss();
        }else{
            FragmentManager manager = ((HomeActivity) ct).getSupportFragmentManager();
            FragmentTransaction transaction= manager.beginTransaction();
            LocalAlbumListFragment localAlbumListFragment=new LocalAlbumListFragment();

            Bundle bundle=new Bundle();
            bundle.putParcelable(Constants.CHANNEL_FOCUS,mLeAlbumInfos.get(position));
            localAlbumListFragment.setArguments(bundle);


            Fragment localMusicFragment= manager.findFragmentByTag("LocalMusicFragment");
            if (localMusicFragment!=null){
                transaction.hide(localMusicFragment);
            }
            transaction.add(R.id.local_music_frame, localAlbumListFragment, "LocalAlbumListFragment").commitAllowingStateLoss();

        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        mAdpter.setSeclection(position);
        if (position!=mLeAlbumInfos.size()-1){
            NetworkConfirmDialog networkConfirmDialog=new NetworkConfirmDialog(ct,R.string.delete_album,R.string.ok,R.string.cancel);
            networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                @Override
                public void onConfirm(boolean checked) {
                    LeAlbumInfo leAlbumInfo=mLeAlbumInfos.get(position);
                    mLeAlbumInfos.remove(leAlbumInfo);
                    MediaOperation.getInstance().delelteAlbumInfo(SortType.SORT_LOCAL_NEW,leAlbumInfo);
                    mAdpter.notifyDataSetChanged();
                }

                @Override
                public void onCancel() {


                }
            });
            networkConfirmDialog.show();

        }else{
//            FragmentManager manager = ((HomeActivity) ct).getSupportFragmentManager();
//            FragmentTransaction transaction= manager.beginTransaction();
//            LocalAlbumListFragment localAlbumListFragment=new LocalAlbumListFragment();
//
//            Bundle bundle=new Bundle();
//            bundle.putParcelable(Constants.CHANNEL_FOCUS,mLeAlbumInfos.get(position));
//            localAlbumListFragment.setArguments(bundle);
//
//
//            Fragment localMusicFragment= manager.findFragmentByTag("LocalMusicFragment");
//            if (localMusicFragment!=null){
//                transaction.hide(localMusicFragment);
//            }
//            transaction.add(R.id.local_music_frame, localAlbumListFragment, "LocalAlbumListFragment").commitAllowingStateLoss();

        }
        return true;
    }

    class LocalAlbumAdapter extends BaseAdapter{
        Context mContext;
        List<LeAlbumInfo> mAlbumLists;
        LayoutInflater mInflater;
        private int mIndex=-1;

        public LocalAlbumAdapter(Context context, List<LeAlbumInfo> mAlbumLists) {
            this.mContext = context;
            this.mAlbumLists = mAlbumLists;
            mInflater=LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            Trace.Debug("### getcount");
            return mAlbumLists==null?0:mAlbumLists.size();
        }

        @Override
        public Object getItem(int position) {
            return mAlbumLists==null?null:mAlbumLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder=null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_loacl_album, null);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.addImage = (ImageView) convertView.findViewById(R.id.image_add);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (GlobalCfg.IS_POTRAIT){
                int width= (DensityUtils.getScreenWidth(mContext)-mContext.getResources().getDimensionPixelSize(R.dimen.size_40dp)*2
                        -mContext.getResources().getDimensionPixelSize(R.dimen.size_40dp))/2;
                FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) viewHolder.image.getLayoutParams();
                layoutParams.width=width;
                layoutParams.height=width;
                viewHolder.image.setLayoutParams(layoutParams);


            }else{
                int width= (DensityUtils.getScreenWidth(mContext)-mContext.getResources().getDimensionPixelSize(R.dimen.size_60dp)*2
                        -mContext.getResources().getDimensionPixelSize(R.dimen.size_40dp)*3)/4;
                FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) viewHolder.image.getLayoutParams();
                layoutParams.width=width;
                layoutParams.height=width;
                viewHolder.image.setLayoutParams(layoutParams);
            }
            final LeAlbumInfo item = mAlbumLists.get(position);

            viewHolder.name.setText((item.NAME == null ? "未命名" : item.NAME));
            final ImageView finalImageView=viewHolder.image;
            String imageUrl = item.IMG_URL;
            if (item.ALBUM_ID.equals("add+")){
                viewHolder.name.setText("新建歌单");
                finalImageView.setImageResource(R.color.transparent_5);
                finalImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                viewHolder.addImage.setVisibility(View.VISIBLE);
            }else {
                viewHolder.addImage.setVisibility(View.GONE);
                finalImageView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                finalImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                if (imageUrl != null && !"".equals(imageUrl.trim())) {
                    //需要进行取缓存操作

                    Glide.with(mContext).load(imageUrl)
                            .asBitmap()
                            .skipMemoryCache(true)
                            .placeholder(R.mipmap.ic_defult)
                            .error(R.mipmap.ic_defult)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new BitmapImageViewTarget(finalImageView) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    finalImageView.setImageBitmap(resource);
                                }
                            });
                } else {
                    finalImageView.setImageResource(R.mipmap.ic_defult);
                }}
//             点击改变选中listItem的背景色
            if (mIndex == position) {
                //finalImageView.setBackgroundResource(R.drawable.gridview_item_image_bg);
                //viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.green_color));
            } else {
                viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                finalImageView.setBackgroundResource(R.color.transparent);
            }
            return convertView;
        }

        //标识选择的Item
        public void setSeclection(int position) {
            mIndex = position;
            notifyDataSetChanged();
        }
        class ViewHolder{
            ImageView image;
            TextView name;
            ImageView addImage;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
