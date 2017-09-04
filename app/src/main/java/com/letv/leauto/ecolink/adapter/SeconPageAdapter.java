package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.model.AppInfo;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.List;

/**
 * Created by lishixing on 2016/9/6.
 */
public class SeconPageAdapter extends BaseAdapter {
    private List<AppInfo> mAppInfoList;
    private LayoutInflater mLayoutInflater;
    public static boolean isDelete=false;
    Context mContext;


    public SeconPageAdapter(List<AppInfo> list, Context context) {
        mAppInfoList = list;
        mLayoutInflater = LayoutInflater.from(context);
        mContext=context;
//        mContext.getResources().updateConfiguration();

    }
    deleteAPPListener mDeleteAPPListener;

    public void setDeleteAPPListener(deleteAPPListener mDeleteAPPListener) {
        this.mDeleteAPPListener = mDeleteAPPListener;
    }
    //    public void setDataList(List<AppInfo> list) {
//        //mAppInfoList = list;
//        this.notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        return mAppInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder;
        int parentHeight;
        int parentWidth;
        if (GlobalCfg.IS_POTRAIT){
            parentHeight= DensityUtils.getScreenHeight(mContext)-DensityUtils.dp2px(mContext,40)-DensityUtils.dp2px(mContext,14)-DensityUtils.dp2px(mContext,49)-DensityUtils.dp2px(mContext,20)-mContext.getResources().getDrawable(R.mipmap.player_page_nor).getMinimumHeight();
            parentWidth= DensityUtils.getScreenWidth(mContext)-DensityUtils.dp2px(mContext,40)-DensityUtils.dp2px(mContext,14)-DensityUtils.dp2px(mContext,49)-DensityUtils.dp2px(mContext,20)-mContext.getResources().getDrawable(R.mipmap.player_page_nor).getMinimumHeight();
        }else {
            parentHeight= DensityUtils.getScreenHeight(mContext)-DensityUtils.dp2px(mContext,40)-DensityUtils.dp2px(mContext,20)-DensityUtils.dp2px(mContext,49)-DensityUtils.dp2px(mContext,20)-mContext.getResources().getDrawable(R.mipmap.player_page_nor).getMinimumHeight();
            parentWidth= DensityUtils.getScreenWidth(mContext)-DensityUtils.dp2px(mContext,40)-DensityUtils.dp2px(mContext,20)-DensityUtils.dp2px(mContext,49)-DensityUtils.dp2px(mContext,20)-mContext.getResources().getDrawable(R.mipmap.player_page_nor).getMinimumHeight();
        }

        if (convertView == null) {
            holder = new Holder();
            if (GlobalCfg.IS_POTRAIT){
                int itemheight=parentHeight/4;
                convertView = mLayoutInflater.inflate(R.layout.added_app_gridview_item, null);
                if (itemheight>0){
                    AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemheight);
                    convertView.setLayoutParams(param);
                }

            }else {
                int itemheight;
                if (parentHeight<parentWidth){
                     itemheight=parentHeight/2;
                }else{
                 itemheight=parentWidth/2;
                }
                convertView = mLayoutInflater.inflate(R.layout.added_app_gridview_item_1, null);
                if (itemheight>0){
                    AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemheight);
                    convertView.setLayoutParams(param);}else{
                    AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    convertView.setLayoutParams(param);
                }
            }

            holder.imageView = (ImageView) convertView.findViewById(R.id.item_img);
            holder.textView = (TextView) convertView.findViewById(R.id.item_tv);
            holder.deleteView = (ImageView) convertView.findViewById(R.id.item_delete_img);


            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.imageView.setImageDrawable(mAppInfoList.get(position).getAppIcon());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteAPPListener!=null){
                    mDeleteAPPListener.imageClick(position);
                }
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mDeleteAPPListener!=null){
                    mDeleteAPPListener.imageLongClick(position);
                }
                return true;
            }
        });

        holder.textView.setText(mAppInfoList.get(position).getAppName());
        if (isDelete){
            holder.deleteView.setVisibility(View.VISIBLE);
            holder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteAPPListener!=null){
                        mDeleteAPPListener.deleteClick(position);
                    }

                }
            });
            if (!mAppInfoList.get(position).getCouldDelete()) {
                holder.deleteView.setVisibility(View.INVISIBLE);
            }
        }else{
            holder.deleteView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void destroy() {
        mContext=null;
    }

    public interface  deleteAPPListener{
        void deleteClick(int position);
        void imageClick(int position);
        void imageLongClick(int position);
    }

    private static class Holder {
        public ImageView imageView;
        public TextView textView;
        public ImageView deleteView;
    }

    public void removeApp(int position) {
        mAppInfoList.remove(position);
        this.notifyDataSetChanged();
    }

    public void addApp(AppInfo appInfo) {
        mAppInfoList.add(appInfo);
        this.notifyDataSetChanged();
    }
    public void setDelete(boolean delete){
        isDelete=delete;
    }


}
