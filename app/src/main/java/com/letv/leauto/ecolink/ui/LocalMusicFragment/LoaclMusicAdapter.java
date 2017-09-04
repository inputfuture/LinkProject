package com.letv.leauto.ecolink.ui.LocalMusicFragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.ui.kuwodownload.CheckInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/9.+
 */
public class LoaclMusicAdapter extends BaseAdapter{

    private List<MediaDetail> mList;
    private List<MediaDetail> mNowDownDetails;
    private Context context;
    protected LePlayer lePlayer;
    public LoaclMusicAdapter(Context context, List<MediaDetail> mList,LePlayer argLePlayer) {
        mNowDownDetails = new ArrayList<>();
        this.mList = mList;
        this.context = context;
        lePlayer = argLePlayer;
    }

    public void setMusicList(List<MediaDetail> list){
        mList = list;
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
        ViewHolder holder = null;
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
            holder.layoutLocal= (LinearLayout) convertView.findViewById(R.id.item_local);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position>=mList.size()&&position<0)
            return convertView;
        final MediaDetail mediaDetail = mList.get(position);
        if (TextUtils.isEmpty(mediaDetail.AUTHOR)){
            mediaDetail.AUTHOR=context.getResources().getString(R.string.str_unkhow_author);
        }
        if (GlobalCfg.IS_POTRAIT) {
            holder.tv_name.setText(mediaDetail.NAME);
            if(!mediaDetail.AUTHOR.equals(context.getResources().getString(R.string.str_unkhow_author))) {
                holder.tv_auther.setText(mediaDetail.AUTHOR);
            }
            else{
                holder.tv_auther.setText("");
            }
        } else {
            holder.tv_name.setText(mediaDetail.NAME);
            if(!(mediaDetail.AUTHOR.equals(context.getResources().getString(R.string.str_unkhow_author))||mediaDetail.AUTHOR.contains(context.getResources().getString(R.string.str_unkhow)))) {
                holder.tv_auther.setText(" - " + mediaDetail.AUTHOR);
            }
            else{
                holder.tv_auther.setText("");
            }
        }
        if (mIsDelete){
            holder.iv_select_item.setVisibility(View.VISIBLE);
            holder.iv_existing.setVisibility(View.GONE);
            holder.layoutLocal.setClickable(true);
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
            final ViewHolder finalHolder = holder;
            holder.layoutLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNowDownDetails.contains(mediaDetail)) {
                        mNowDownDetails.remove(mediaDetail);
                        finalHolder.tv_name.setTextColor(context.getResources().getColor(R.color.transparent_60));
                        finalHolder.tv_auther.setTextColor(context.getResources().getColor(R.color.transparent_60));
                        finalHolder.iv_select_item.setImageResource(R.mipmap.song_not_selected);
                    } else {
                        mNowDownDetails.add(mediaDetail);
                        finalHolder.tv_name.setTextColor(context.getResources().getColor(R.color.green_color));
                        finalHolder.tv_auther.setTextColor(context.getResources().getColor(R.color.green_color));
                        finalHolder.iv_select_item.setImageResource(R.mipmap.song_selected);
                    }
                    if (mNowDownDetails.size()==mList.size()){
                        checkInterface.checkAll();
                    }else {
                        checkInterface.unCheckAll();
                    }
                }
            });
        }else {
            holder.iv_select_item.setVisibility(View.GONE);
            holder.tv_auther.setVisibility(View.VISIBLE);
            holder.layoutLocal.setClickable(false);
            if (MediaOperation.getInstance().isDownLoadMusic(SortType.SORT_KUWO_LOCAL,mediaDetail)||MediaOperation.getInstance().isDownLoadMusic(SortType.SORT_LE_RADIO_LOCAL,mediaDetail)||mediaDetail.fileIfExist){
                holder.iv_existing.setVisibility(View.VISIBLE);
            }else {
                holder.iv_existing.setVisibility(View.GONE);
            }


            if (lePlayer.getPlayerList()!=null&&lePlayer.getIndex()<lePlayer.getPlayerList().size()&&(lePlayer.getPlayerList().get(lePlayer.getIndex()).NAME+lePlayer.getPlayerList().get(lePlayer.getIndex()).AUTHOR).equals(mediaDetail.NAME+mediaDetail.AUTHOR)&&mList.size()==lePlayer.getPlayerList().size()){
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.green_color));
                holder.tv_auther.setTextColor(context.getResources().getColor(R.color.green_color));
            }
            else {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.transparent_60));
                holder.tv_auther.setTextColor(context.getResources().getColor(R.color.transparent_60));
            }
        }

        return convertView;
    }
    public boolean mIsDelete=false;
    public void setDelete(boolean visibily) {
        mIsDelete=visibily;
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
    public List<MediaDetail> getNowMedials() {
        return mNowDownDetails;
    }
    public static class ViewHolder {
        public TextView tv_name;
        public ImageView iv_select_item;
        public ImageView iv_move;
        public TextView tv_auther;
        public ImageView iv_existing;
        public LinearLayout layoutLocal;
    }

    public CheckInterface checkInterface;
    public void setCheckInterface(CheckInterface argCheckInterface){
        checkInterface=argCheckInterface;
    }

}
