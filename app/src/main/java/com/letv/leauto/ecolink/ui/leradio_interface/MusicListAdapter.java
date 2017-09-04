package com.letv.leauto.ecolink.ui.leradio_interface;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.LePlayer;

import java.util.ArrayList;
import java.util.List;


public class MusicListAdapter extends BaseAdapter {
    private List<MediaDetail> mList;
    private List<MediaDetail> mNowDownDetails;
    private Context context;
    private LePlayer lePlayer;
    public MusicListAdapter(Context context, List<MediaDetail> mList, LePlayer argLePlayer) {
        mNowDownDetails = new ArrayList<>();
        this.mList = mList;
        this.context = context;
        lePlayer=argLePlayer;
    }
    public void setmList(List<MediaDetail> mList) {
        this.mList = mList;
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
            convertView = View.inflate(context, R.layout.fragment_kuwo_musiclist_item, null);

            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_select_item = (ImageView) convertView.findViewById(R.id.iv_select_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MediaDetail mediaDetail = mList.get(position);
        holder.tv_name.setText(mediaDetail.NAME);


        return convertView;
    }
    public boolean mIsDownLoad=false;
    public void setDownLoad(boolean visibily) {
        mIsDownLoad=visibily;
        notifyDataSetChanged();
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
    public static class ViewHolder {
        public TextView tv_name;
        public ImageView iv_select_item;
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
