package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.LePlayer;

import java.util.ArrayList;

/**
 * Created by why on 2016/7/25.
 * 直播专辑列表
 */
public class LiveAlbumAdapter extends BaseAdapter {

    private ArrayList<LeAlbumInfo> mAlbumList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int clickTemp = -1;
    private LePlayer lePlayer;


    public LiveAlbumAdapter(Context context, ArrayList<LeAlbumInfo> leAlbumInfos) {
        mContext=context;
        mInflater=LayoutInflater.from(mContext);
        mAlbumList=leAlbumInfos;
        lePlayer= EcoApplication.LeGlob.getPlayer();
    }

    @Override
    public int getCount() {
        return mAlbumList.size();
    }
    //标识选择的Item
    public void setSeclection(int position) {
        clickTemp = position;
    }
    @Override
    public Object getItem(int position) {
        return mAlbumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public Object getItem(int position) {
//        Trace.Debug("####getitem");
//        return mAlbumList==null?null:mAlbumList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (GlobalCfg.IS_POTRAIT) {
                convertView = mInflater.inflate(R.layout.item_radio_album_live_p, null);
            }else {
                convertView = mInflater.inflate(R.layout.item_radio_album_live, null);
            }
            viewHolder.iv_move= (ImageView) convertView.findViewById(R.id.iv_move);

            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final LeAlbumInfo item = mAlbumList.get(position);
        if (lePlayer!=null) {
            ArrayList<MediaDetail> mediaDetails=lePlayer.getPlayerList();
            if (mediaDetails!=null){
                int  index=lePlayer.getIndex();
                if (index>=0&&index<mediaDetails.size()){
                    MediaDetail lemediaDetail1=mediaDetails.get(index);
                    if (lemediaDetail1.NAME.equals(item.NAME)){

                        viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.green));

                    }else{
                        viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));

                    }

                }
            }else{
                viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));

            }

        } else {
//            holder.tv_name.setTextSize(25);
            viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));

        }

        item.TYPE= SortType.SORT_LIVE;
        String unName = mContext.getString(R.string.un_define_name);
        viewHolder.name.setText((item.NAME == null ? unName : item.NAME));

        return convertView;
    }


    public static class ViewHolder{
        public ImageView iv_move;
        TextView name;
    }
}
