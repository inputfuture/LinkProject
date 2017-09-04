package com.letv.leauto.ecolink.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.LePlayer;

import java.util.ArrayList;
import java.util.List;


public class KuWoDownLoadAdapter extends BaseAdapter {
    private List<MediaDetail> mList;
    private List<MediaDetail> mNowDownDetails;
    private Context context;
    public ImageView mIvMusic;
    /**
     * 贝塞尔曲线中间过程的点的坐标
     */
    private float[] mCurrentPosition = new float[2];
    public KuWoDownLoadAdapter(Context context, List<MediaDetail> mList) {
        mNowDownDetails = new ArrayList<>();
        this.mList = mList;
        this.context = context;
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
            if (GlobalCfg.IS_POTRAIT) {
                convertView = View.inflate(context, R.layout.fragment_kuwo_musiclist_item_p, null);
            } else {
                convertView = View.inflate(context, R.layout.fragment_kuwo_musiclist_item, null);
            }
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_auther = (TextView) convertView.findViewById(R.id.tv_auther);
            holder.iv_select_item = (ImageView) convertView.findViewById(R.id.iv_select_item);
            holder.iv_move = (ImageView) convertView.findViewById(R.id.iv_move);
            holder.iv_existing = (ImageView) convertView.findViewById(R.id.iv_existing);
            holder.rl_layout = (RelativeLayout) convertView.findViewById(R.id.rl_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MediaDetail mediaDetail = mList.get(position);
        if (GlobalCfg.IS_POTRAIT) {
            holder.tv_name.setText(mediaDetail.NAME);
            if(!mediaDetail.AUTHOR.equals("未知作者")) {
                holder.tv_auther.setText(mediaDetail.AUTHOR);
            }
        } else {
            holder.tv_name.setText(mediaDetail.NAME);
            if(!mediaDetail.AUTHOR.equals("未知作者")) {
                holder.tv_auther.setText(" — " + mediaDetail.AUTHOR);
            }
        }
            holder.iv_select_item.setVisibility(View.VISIBLE);
            holder.iv_existing.setVisibility(View.GONE);
//            if (GlobalCfg.IS_POTRAIT) {
//                holder.tv_auther.setVisibility(View.GONE);
//            } else {
//                holder.tv_auther.setVisibility(View.VISIBLE);
//            }
            if (mNowDownDetails.contains(mediaDetail)) {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.green_color));
                holder.tv_auther.setTextColor(context.getResources().getColor(R.color.green_color));
                holder.iv_select_item.setImageResource(R.mipmap.song_selected);
            } else {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.transparent_60));
                holder.tv_auther.setTextColor(context.getResources().getColor(R.color.transparent_60));
                holder.iv_select_item.setImageResource(R.mipmap.song_not_selected);
            }
            holder.rl_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNowDownDetails.contains(mediaDetail)) {
                        mNowDownDetails.remove(mediaDetail);
                        holder.tv_name.setTextColor(context.getResources().getColor(R.color.transparent_60));
                        holder.tv_auther.setTextColor(context.getResources().getColor(R.color.transparent_60));
                        holder.iv_select_item.setImageResource(R.mipmap.song_not_selected);
                    } else {
                        mNowDownDetails.add(mediaDetail);
                        holder.tv_name.setTextColor(context.getResources().getColor(R.color.green_color));
                        holder.tv_auther.setTextColor(context.getResources().getColor(R.color.green_color));
                        holder.iv_select_item.setImageResource(R.mipmap.song_selected);
                    }
                    if (mNowDownDetails.size()==mList.size()){
                        checkInterface.checkAll();
                    }else {
                        checkInterface.unCheckAll();
                    }
                }
            });
        return convertView;
    }
    public void setIsAllDownLoad(boolean mIsAll){
        if (mIsAll){
            mNowDownDetails.clear();
            mNowDownDetails.addAll(mList);
        }else {
            mNowDownDetails.clear();
        }
        notifyDataSetChanged();
    }
    public List<MediaDetail> getNowMedials() {
        return mNowDownDetails;
    }
    public static class ViewHolder {
        public TextView tv_name;
        public TextView tv_auther;
        public ImageView iv_select_item;
        public ImageView iv_move;
        public ImageView iv_existing;
        public RelativeLayout rl_layout;
    }
    //回调选中的状态
    public interface  CheckInterface{
        public abstract void checkAll();
        public abstract void unCheckAll();
    }
    public CheckInterface checkInterface;
    public void setCheckInterface(CheckInterface argCheckInterface){
        checkInterface=argCheckInterface;
    }
}
