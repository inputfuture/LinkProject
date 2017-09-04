package com.letv.leauto.ecolink.qplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.tencent.qplayauto.device.QPlayAutoSongListItem;

import java.util.List;

/**
 * Created by 轻e租 on 2016/11/23.
 */

public class QPlayMusicListAdapter extends BaseAdapter {

    private Context context;
    private List<QPlayAutoSongListItem> songList;
    private LayoutInflater inflater;

    public QPlayMusicListAdapter(Context context, List<QPlayAutoSongListItem> songList) {
        this.context = context;
        this.songList = songList;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int i) {
        return songList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolderTwo viewHolder;
        if (view==null){
            view = this.inflater.inflate(R.layout.item_qplay_list,null);

            viewHolder = new ViewHolderTwo();
            viewHolder.songName = (TextView) view.findViewById(R.id.song_name);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolderTwo) view.getTag();
        }
        QPlayAutoSongListItem songItem = songList.get(i);
        String theName = songItem.Name;
        String theArtist = songItem.Artist;

        if (theName!=null){
            viewHolder.songName.setText(theName);
        }
        if (GlobalCfg.IS_POTRAIT){
            viewHolder.artist.setVisibility(View.VISIBLE);
            if (theArtist!=null){
                viewHolder.artist.setText(theArtist);
            }
        }else{
            viewHolder.artist.setVisibility(View.GONE);
        }


        return view;
    }
    class ViewHolderTwo {
        TextView songName;
        TextView artist;

    }

}
