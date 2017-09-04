package com.letv.leauto.ecolink.adapter;


import android.content.Context;
import android.provider.Browser;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.LePlayer;

import java.util.ArrayList;
import java.util.List;


/**
 * 专辑音乐列表界面 下载
 * 用于显示 播放音乐列表界面
 */
public class AlbumListAdapter extends BaseAdapter {
    private List<MediaDetail> mList;
    private Context mContext;
    private boolean mIsDownload; /*是否是下载状态*/
    private boolean isRecent = false;
    private List<MediaDetail> mDownloadDetails;
    private List<MediaDetail> mNowDownDetails;
    public LePlayer lePlayer;
    DownLoadClickListener mDownLoadClickListener;

    public AlbumListAdapter(List<MediaDetail> mList, Context context,LePlayer argLePlayer,Boolean isRecent) {
        this.mList = mList;
        this.mContext = context;
        this.isRecent = isRecent;
        mDownloadDetails = new ArrayList<>();
        lePlayer = argLePlayer;
    }

    public interface DownLoadClickListener{
        void canClick(boolean canClick);
    }

    public void setDownLoadClickListener(DownLoadClickListener mdDownLoadClickListener) {
        this.mDownLoadClickListener = mdDownLoadClickListener;
    }

    public AlbumListAdapter(List<MediaDetail> mList, List<MediaDetail> downloadDetails, Context context, LePlayer argLePlayer) {
        this.mList = mList;
        this.mContext = context;
        mDownloadDetails = downloadDetails;
        mNowDownDetails = new ArrayList<>();
        lePlayer = argLePlayer;
    }
    public void setmList(List<MediaDetail> mList) {
        this.mList = mList;
    }

    public void addItems(List<MediaDetail> list) {
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.mList.clear();
        notifyDataSetChanged();
    }

    public void setIsDownload(boolean isDownload) {
        mIsDownload = isDownload;
        mNowDownDetails.clear();
        if (mDownLoadClickListener !=null){
            mDownLoadClickListener.canClick(!mIsDownload);
        }
        notifyDataSetChanged();
    }


    private boolean mIsAll;

    public void setIsAllDownLoad(boolean mIsAll){
        this.mIsAll=mIsAll;
        mNowDownDetails.clear();
        if (mIsAll){
            for (int i = 0; i < mList.size(); i++) {
                if (!mDownloadDetails.contains(mList.get(i))){
                    mNowDownDetails.add(mList.get(i));
                }
            }}
        if (mNowDownDetails.size()>0){
            if (mDownLoadClickListener !=null){
                mDownLoadClickListener.canClick(true);
            }
        }else{
            if (mDownLoadClickListener !=null){
                mDownLoadClickListener.canClick(true);
            }
        }
        notifyDataSetChanged();
    }
    public boolean getIsAll(){
        return  mIsAll;
    }



    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_album_list, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_music_name);
            holder.tv_author = (TextView) convertView.findViewById(R.id.tv_music_author);
            holder.iv_select = (ImageView) convertView.findViewById(R.id.img_select);
            holder.iv_move = (ImageView) convertView.findViewById(R.id.iv_move);
            holder.layout= (RelativeLayout) convertView.findViewById(R.id.item_lay);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        final MediaDetail mediaDetail = mList.get(position);
        holder.tv_name.setText(mList.get(position).NAME.replace(" ", ""));

        if (null != mList.get(position).AUTHOR && !mList.get(position).AUTHOR.equals("null") && !mList.get(position).AUTHOR.trim().equals("") && !mList.get(position).AUTHOR.trim().equals("未知作者")) {
            if (this.isRecent) {
                holder.tv_author.setText(" — " + mList.get(position).AUTHOR);
            }
        }

        //如果是下载模式，已经下载的显示灰色，复选框选中状态
        if (mIsDownload) {
            holder.iv_select.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.VISIBLE);
            holder.iv_select.setImageResource(R.mipmap.song_not_selected);
            if (mDownloadDetails.contains(mediaDetail)) {
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                holder.iv_select.setImageResource(R.mipmap.song_selected);

            } else {
                if (mNowDownDetails.contains(mediaDetail)) {
                    holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.green_color));
                    holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.green_color));
                    holder.iv_select.setImageResource(R.mipmap.song_selected);
                } else {
                    holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                    holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                    holder.iv_select.setImageResource(R.mipmap.song_not_selected);
                }
                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mNowDownDetails.contains(mediaDetail)) {
                            mNowDownDetails.remove(mediaDetail);
                            holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                            holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                            holder.iv_select.setImageResource(R.mipmap.song_not_selected);

                        } else {
                            mNowDownDetails.add(mediaDetail);
                            holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.green_color));
                            holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.green_color));
                            holder.iv_select.setImageResource(R.mipmap.song_selected);
                        }

                        if (mNowDownDetails.size()>0){
                            if (mDownLoadClickListener !=null){
                                mDownLoadClickListener.canClick(true);
                            }
                        }else{
                            if (mDownLoadClickListener !=null){
                                mDownLoadClickListener.canClick(true);
                            }
                        }
                        if (mNowDownDetails.size()==mList.size()){
                            mIsAll=true;
                            if (mItemAllSelectListener !=null){
                                mItemAllSelectListener.setIsAll(true);
                            }
                        }else{
                            if (mItemAllSelectListener !=null){
                                mItemAllSelectListener.setIsAll(false);
                            }
                            mIsAll=false;
                        }

                    }
                });
            }


        } else {
            holder.iv_select.setImageResource(R.mipmap.downloaded_icon);

            if (lePlayer!=null) {
                ArrayList<MediaDetail> mediaDetails=lePlayer.getPlayerList();
                if (mediaDetails!=null){
                    int  index=lePlayer.getIndex();
                    if (index>=0&&index<mediaDetails.size()){
                        MediaDetail lemediaDetail1=mediaDetails.get(index);
                        if (lemediaDetail1.AUDIO_ID.equals(mediaDetail.AUDIO_ID)&&lemediaDetail1.LE_SOURCE_MID.equals(mediaDetail.LE_SOURCE_MID)){
                            holder.iv_select.setVisibility(View.GONE);
                            holder.layout.setVisibility(View.GONE);
//                Glide.with(mContext).load(R.drawable.musicplay).into(holder.iv_select);
//            holder.tv_name.setTextSize(28);
                            holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.green));
                            holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.green));
                        }else{
                            holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                            holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                            holder.iv_select.setVisibility(View.GONE);
                            holder.layout.setVisibility(View.GONE);
                        }

                    }
                }else{
                    holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                    holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                    holder.iv_select.setVisibility(View.GONE);
                    holder.layout.setVisibility(View.GONE);
                }

            } else {
//            holder.tv_name.setTextSize(25);
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                holder.iv_select.setVisibility(View.GONE);
                holder.layout.setVisibility(View.GONE);
            }
            if (MediaOperation.getInstance().isDownLoadMusic(SortType.SORT_LE_RADIO_LOCAL,mediaDetail)&& mediaDetail.fileIfExist) {
                holder.iv_select.setVisibility(View.VISIBLE);
            } else {

                holder.iv_select.setVisibility(View.GONE);
            }
        }

        return convertView;
    }


    public List<MediaDetail> getNowDownLoadMedials() {
        return mNowDownDetails;
    }

    public class ViewHolder {
        public TextView tv_name;
        public TextView tv_author;
        public ImageView iv_select;
        public ImageView iv_move;
        RelativeLayout layout;
    }

    public void setItemAllSelectListener(ItemAllSelectListener mItemAllSelectListener) {
        this.mItemAllSelectListener = mItemAllSelectListener;
    }

    ItemAllSelectListener mItemAllSelectListener;
    public interface  ItemAllSelectListener{
        void setIsAll(boolean isAll);
    }
}
