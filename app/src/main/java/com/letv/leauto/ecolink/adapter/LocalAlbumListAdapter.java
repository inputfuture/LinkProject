package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.MediaDetail;

import java.util.List;

/**
 * Created by why on 2016/11/22.
 */
public class LocalAlbumListAdapter extends BaseAdapter {
    List<MediaDetail> mMediaDetails;
    List<MediaDetail> mSelectMediaDetails;
    Context mContext;
    LayoutInflater mInflater;
    private boolean mIsSelect;/*是否是选择模式*/
    private boolean mIsAll;/*是否是全选模式*/

    public LocalAlbumListAdapter(Context mContext, List<MediaDetail> mMediaDetails,List<MediaDetail> selectMediaDetails,boolean isSelect) {
        this.mContext = mContext;
        this.mMediaDetails = mMediaDetails;
        this.mSelectMediaDetails=selectMediaDetails;
        mInflater=LayoutInflater.from(mContext);
        mIsSelect=isSelect;
    }
    public void setIsSelect(boolean isSelect){
        mIsSelect=isSelect;
        notifyDataSetChanged();
    }

    public boolean isSelect() {
        return mIsSelect;
    }

    @Override
    public int getCount() {
        return mMediaDetails==null?0:mMediaDetails.size();
    }

    public void setIsAll(boolean isAll){
        mIsAll=isAll;
    }

    public boolean ismIsAll() {
        return mIsAll;
    }

    @Override
    public Object getItem(int position) {
        return mMediaDetails==null?null:mMediaDetails.get(position);
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
        final MediaDetail mediaDetail=mMediaDetails.get(position);
        holder.tv_name.setText(mediaDetail.NAME);
        if (mIsSelect){
            holder.iv_select.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.VISIBLE);
            if (mSelectMediaDetails.contains(mediaDetail)) {
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

                    if (mSelectMediaDetails.contains(mediaDetail)) {
                        mSelectMediaDetails.remove(mediaDetail);
                        holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                        holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                        holder.iv_select.setImageResource(R.mipmap.song_not_selected);

                    } else {
                        mSelectMediaDetails.add(mediaDetail);
                        holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.green_color));
                        holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.green_color));
                        holder.iv_select.setImageResource(R.mipmap.song_selected);
                    }
                    if (mSelectMediaDetails.size()==mMediaDetails.size()){
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
        }else{
            holder.iv_select.setVisibility(View.GONE);
            holder.layout.setVisibility(View.GONE);
            holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
            holder.tv_author.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
        }
        return convertView;
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
