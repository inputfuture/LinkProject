package com.letv.leauto.ecolink.lemap.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.lemap.entity.SearchPoi;

import java.util.List;


public class HistoryAdapter extends BaseAdapter {
    private List<SearchPoi> mList;
    private Context context;
    private boolean isAddPoi;

    public HistoryAdapter(List<SearchPoi> mList, Context context, boolean isSearchView) {
        this.mList = mList;
        this.context = context;
        this.isAddPoi = isSearchView;
    }

    public void setmList(List<SearchPoi> mList) {
        this.mList = mList;
    }

    public void setIsAddPoi(boolean addPoi) {
        isAddPoi = addPoi;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if(GlobalCfg.IS_POTRAIT){
                convertView = View.inflate(context, R.layout.history_lv_item, null);
            }else {
                convertView = View.inflate(context, R.layout.history_lv_item_l, null);
            }

            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_left = (ImageView) convertView.findViewById(R.id.type);
            holder.rightLayout = (LinearLayout) convertView.findViewById(R.id.right);
            holder.rightIcon= (ImageView) convertView.findViewById(R.id.img_arraw);
            holder.toHere= (TextView) convertView.findViewById(R.id.to_here);


            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        SearchPoi searchPoi=mList.get(position);

        if (isAddPoi) {
            holder.iv_left.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightIcon.setImageResource(R.mipmap.map_ic_add);
            holder.toHere.setVisibility(View.GONE);
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.half_white));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.itemClick(position);
                    }
                }
            });


        } else {


            holder.iv_left.setVisibility(View.VISIBLE);
            if (searchPoi.getType().equals(SearchPoi.SEARCH)){
                holder.iv_left.setImageResource(R.mipmap.history_search);
                holder.rightLayout.setVisibility(View.GONE);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.itemClick(position);
                        }
                    }
                });

            }else{

                holder.iv_left.setImageResource(R.mipmap.history_navi);
                holder.rightIcon.setImageResource(R.mipmap.navi_to_here);
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.toHere.setVisibility(View.VISIBLE);
                holder.tv_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.addressClick(position);
                        }

                    }
                });
                holder.rightLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.iconClick(position);
                        }
                    }
                });
            }



        }
        String addr = mList.get(position).getAddrname();
        holder.tv_name.setText(addr);

        return convertView;
    }

    static class ViewHolder {
        public TextView tv_name;
        public  ImageView iv_left;
        public LinearLayout rightLayout;
        public ImageView rightIcon;
        public TextView toHere;
      ;

    }

ClickListener mClickListener;

    public void setClickListener(ClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface ClickListener{
        void itemClick(int position);
        void addressClick(int position);
        void iconClick(int position);
    }

}
