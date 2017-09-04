package com.letv.leauto.ecolink.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.http.leradio.ChannelLoader;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AlbumEidtFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.back)
    ImageView mBackImageButton;
    @Bind(R.id.complete)
    TextView mCompleteButton;
    @Bind(R.id.eidt_gridview)
    GridView mEditGridView;
    private AlbumEditAdapter mAdapter;
    private String TAG=AlbumEidtFragment.class.getSimpleName();
    private MediaOperation mediaOperation;
    private boolean isSortCached;
    private ArrayList<Channel> mChannelInfos =new ArrayList<>();
    private ArrayList<Channel> mSelectChannels;
    private ArrayList<Channel> mOriginSelectChannelsInfos;
    SortChangeListener msSortChangeListener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MessageTypeCfg.MSG_CHANNEL:
                    List<Channel> list= (List<Channel>) msg.obj;
                    if (list!=null){
                        updateChannels(list);
                    }

                    break;

                case MessageTypeCfg.MSG_GETDATA_COMPLETED: {
//                    rlWait.setVisibility(View.GONE);
                }
                break;
                case MessageTypeCfg.MSG_GETDATA_EXCEPTION: {
                }
                break;
            }
        }
    };

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);
        FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
        Fragment kuwoMusicMainFragment= manager.findFragmentByTag("LeRadioAlumFragment");
        FragmentTransaction transaction= manager.beginTransaction();
        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                    if (kuwoMusicMainFragment != null) {
                        transaction.show(kuwoMusicMainFragment);
                    }
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }
    @Override
    protected View initView(LayoutInflater inflater) {
        Trace.Debug("##### initView");
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_album_eidt, null);
        } else {
            view = inflater.inflate(R.layout.fragment_album_eidt, null);
        }
        ButterKnife.bind(this, view);
        if (GlobalCfg.IS_POTRAIT) {
            mEditGridView.setNumColumns(3);
            int padding= (int) mContext.getResources().getDimension(R.dimen.size_14dp);

            mBackImageButton.setPadding(padding,padding,padding,padding);

        } else {
            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) mBackImageButton.getLayoutParams();
            layoutParams.leftMargin= (int) mContext.getResources().getDimension(R.dimen.size_6dp);
            layoutParams.rightMargin= (int) mContext.getResources().getDimension(R.dimen.size_6dp);
            mBackImageButton.setLayoutParams(layoutParams);
            mEditGridView.setNumColumns(4);

            LinearLayout.LayoutParams gridParams= (LinearLayout.LayoutParams) mEditGridView.getLayoutParams();
            gridParams.leftMargin=(int) mContext.getResources().getDimension(R.dimen.size_60dp);
            gridParams.rightMargin=(int) mContext.getResources().getDimension(R.dimen.size_60dp);
            mEditGridView.setLayoutParams(gridParams);


            RelativeLayout.LayoutParams completeParams= (RelativeLayout.LayoutParams) mCompleteButton.getLayoutParams();
            completeParams.rightMargin=(int) mContext.getResources().getDimension(R.dimen.size_35dp);
            mCompleteButton.setLayoutParams(completeParams);
        }
        mBackImageButton.setOnClickListener(this);
        mCompleteButton.setOnClickListener(this);
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mediaOperation=MediaOperation.getInstance();
        // DataUtil.getInstance().getChannels(this.handler, mContext );
        ChannelLoader loader = new ChannelLoader(mContext, this.handler);
        loader.load();
        mSelectChannels =mediaOperation.getChannelsList();
        for (Channel channel : mSelectChannels) {
            if ("我的".equals(channel.name)) {
                mSelectChannels.remove(channel);
                break;
            }
        }
        mOriginSelectChannelsInfos =new ArrayList<>();
        mOriginSelectChannelsInfos.addAll(mSelectChannels);
        mAdapter=new AlbumEditAdapter(getActivity(), mChannelInfos, mSelectChannels);
        mEditGridView.setAdapter(mAdapter);
    }


    private void updateChannels(List<Channel> sorts) {
        mChannelInfos.clear();
        mChannelInfos.addAll(sorts);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onClick(View v) {
        FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
        Fragment kuwoMusicMainFragment= manager.findFragmentByTag("LeRadioAlumFragment");
        FragmentTransaction transaction= manager.beginTransaction();
        switch (v.getId()){
            case R.id.back:
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right,R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                if (kuwoMusicMainFragment!=null){
                    transaction.show(kuwoMusicMainFragment);
                }

                break;
            case R.id.complete:
                if (mSelectChannels !=null&& mSelectChannels != mOriginSelectChannelsInfos) {
                    mediaOperation.insertChannelList(mSelectChannels);
                    if (msSortChangeListener!=null){
                        msSortChangeListener.sortChange(mSelectChannels);
                    }
                }
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right,R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                if (kuwoMusicMainFragment!=null){
                    transaction.show(kuwoMusicMainFragment);
                }
                break;
        }

    }
    public void setSortChangeListener(SortChangeListener sortChangeListener){
        msSortChangeListener=sortChangeListener;
    }

    interface SortChangeListener{
        void sortChange(ArrayList<Channel> channelsList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
        if (mOriginSelectChannelsInfos!=null){
            mOriginSelectChannelsInfos.clear();
        }
        if (mChannelInfos!=null){
            mChannelInfos.clear();
        }
        if (mSelectChannels!=null){
            mSelectChannels.clear();
        }
    }

    static class AlbumEditAdapter extends BaseAdapter{
        private ArrayList<Channel> mLeSortInfos;
        private ArrayList<Channel> mSelectSortInfos;
        private Context mContext;
        private LayoutInflater mInflater;

        public AlbumEditAdapter(Context mContext, ArrayList<Channel> mLeSortInfos, ArrayList<Channel> selectSortInfos) {
            this.mContext = mContext;
            this.mLeSortInfos = mLeSortInfos;
            mSelectSortInfos=selectSortInfos;
            mInflater=LayoutInflater.from(mContext);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLeSortInfos==null?0:mLeSortInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mLeSortInfos==null?null:mLeSortInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView==null){
                viewHolder=new ViewHolder();
                convertView=mInflater.inflate(R.layout.item_album_edit,null);
                viewHolder.item= (TextView) convertView.findViewById(R.id.album_edit_item);
                convertView.setTag(viewHolder);

            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            final Channel leSortInfo=mLeSortInfos.get(position);


            if (mSelectSortInfos.contains(leSortInfo)) {
                viewHolder.item.setBackground(mContext.getResources().getDrawable(R.drawable.album_eidt_hightlight_selector));
                viewHolder.item.setTextColor(mContext.getResources().getColor(R.color.green_color));

            } else {
                viewHolder.item.setBackground(mContext.getResources().getDrawable(R.drawable.album_eidt_custom_selector));
                viewHolder.item.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
            }

            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectSortInfos.contains(leSortInfo)) {
                        mSelectSortInfos.remove(leSortInfo);
//                            mediaOperation.deleteLeSortInfo(leSortInfo);
                        viewHolder.item.setBackground(mContext.getResources().getDrawable(R.drawable.album_eidt_custom_selector));
                        viewHolder.item.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                    } else {
                        mSelectSortInfos.add(leSortInfo);
//                            mediaOperation.insertLeSortInfo(leSortInfo);
                        viewHolder.item.setBackground(mContext.getResources().getDrawable(R.drawable.album_eidt_hightlight_selector));
                        viewHolder.item.setTextColor(mContext.getResources().getColor(R.color.green_color));
                    }
                }
            });

            viewHolder.item.setText(mLeSortInfos.get(position).getName());
            return convertView;
        }

        class ViewHolder{
            TextView item;
        }
    }
}
