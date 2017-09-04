package com.letv.leauto.ecolink.qplay;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.utils.Trace;
import com.tencent.qplayauto.device.QPlayAutoSongListItem;

import java.util.List;

/**
 * Created by 轻e租 on 2016/11/23.
 */

public class RootRecyclerViewAdapter extends RecyclerView.Adapter {
    private Context mContext;

    public RootRecyclerViewAdapter(Context context, List<QPlayAutoSongListItem> songListItems) {
        mContext=context;
        list=songListItems;
    }

    private int [] colors=new int[]{R.color.q_color1,R.color.q_color2,R.color.q_color3,
            R.color.q_color4,
            R.color.q_color5,R.color.q_color6,R.color.q_color7,
            R.color.q_color8,
            R.color.q_color9,R.color.q_color10,R.color.q_color11,
            R.color.q_color12};
    public  interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<QPlayAutoSongListItem> list;

    public RootRecyclerViewAdapter(List<QPlayAutoSongListItem> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_layout,null);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SongViewHolder viewHolder = (SongViewHolder) holder;
        viewHolder.position = position;
        QPlayAutoSongListItem songName = list.get(position);
        viewHolder.image.setBackgroundColor(mContext.getResources().getColor(colors[position%12]));
        if (position == 0) {
            viewHolder.iconView.setBackgroundResource(R.mipmap.download_icon);
            viewHolder.iconView.setText("");
            viewHolder.iconViewText.setVisibility(View.GONE);

        } else if (position == 1) {
            viewHolder.iconView.setBackgroundResource(R.mipmap.music_favor_no);
            viewHolder.iconView.setText("");
            viewHolder.iconViewText.setVisibility(View.GONE);

        }else{
            viewHolder.iconView.setBackgroundResource(0);
            if (songName.Name!=null){
                String  name=songName.Name;
                if (name.contains("·")||name.contains(" ")){
                    String[] names= name.split("·| ");

                    String html="";
                    for (int i = 0; i < names.length-1; i++) {
                        html=html+names[i]+"\n";
                    }


                    html=html.substring(0,html.length()-1);
                    viewHolder.iconViewText.setVisibility(View.VISIBLE);
                    viewHolder.iconViewText.setText(html);
                    viewHolder.iconView.setText( names[names.length-1]);

                }else{
                    viewHolder.iconViewText.setVisibility(View.GONE);
                    viewHolder.iconView.setText(songName.Name);
                }

            }
        }
        if (songName.Name!=null){
            viewHolder.rootName.setText(songName.Name);}
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        public View rootView;
        public ImageView image;
        public TextView rootName;
        public TextView iconView;
        public int position;
        public TextView iconViewText;

        public SongViewHolder(View itemView) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.image);
            this.rootName = (TextView) itemView.findViewById(R.id.name);
            this.rootView = itemView.findViewById(R.id.root_view);
            this.iconView= (TextView) itemView.findViewById(R.id.icon);
            this.iconViewText= (TextView) itemView.findViewById(R.id.icon_text);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(null != onRecyclerViewListener){
                return onRecyclerViewListener.onItemLongClick(position);
            }
            return false;
        }
    }
}
