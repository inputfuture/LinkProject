package com.letv.leauto.ecolink.ui.chooseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.AppInfo;
import com.letv.leauto.ecolink.manager.ChoosedAppManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lishixing on 2016/9/6.
 */
public class ChooseAppAdapter extends BaseAdapter {
    private List<AppInfo> mAppInfoList;
    private LayoutInflater mLayoutInflater;
    private List<AppInfo> mChooseInfoList;
    Context mContext;
    private List<AppInfo> mSaveInfoList;


    public ChooseAppAdapter(List<AppInfo> list, Context context) {
        mAppInfoList = list;
        mLayoutInflater = LayoutInflater.from(context);
        mContext=context;

    }

    public ChooseAppAdapter(List<AppInfo> list, List<AppInfo> chooseAppinfos, Context context) {
        mAppInfoList = list;
        mChooseInfoList=chooseAppinfos;
        mLayoutInflater = LayoutInflater.from(context);
        mContext=context;
        mSaveInfoList=ChoosedAppManager.getInstance(mContext).getSavedApps(false);


    }

    public void setDataList(List<AppInfo> list) {
        mAppInfoList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAppInfoList==null?0:mAppInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfoList==null?null:mAppInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mLayoutInflater.inflate(R.layout.choose_app_gridview_item,null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_img);
            holder.textView = (TextView) convertView.findViewById(R.id.item_tv);
            holder.checkBox= (CheckBox) convertView.findViewById(R.id.item_check);


            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.imageView.setImageDrawable(mAppInfoList.get(position).getAppIcon());
        holder.textView.setText(mAppInfoList.get(position).getAppName());
        AppInfo appInfo=mAppInfoList.get(position);
        if (mSaveInfoList.contains(appInfo)){
            convertView.setEnabled(false);
            holder.checkBox.setButtonDrawable(R.mipmap.select_disable);

        }else{
            convertView.setEnabled(true);
            holder.checkBox.setButtonDrawable(R.drawable.checkbox_bg);
            if (mChooseInfoList.contains(appInfo)){
                holder.checkBox.setChecked(true);
            }else {
                holder.checkBox.setChecked(false);
            }
        }
        return convertView;
    }

    public void destroy() {
        mContext=null;
    }

    private static class Holder {
        public ImageView imageView;
        public TextView textView;
        public CheckBox checkBox;
    }

//    public void removeApp(int position) {
//        ChoosedAppManager.getInstance(mContext).getInstallAppList().remove(position);
//        this.notifyDataSetChanged();
//    }
//
//    public void addApp(AppInfo appInfo) {
//        ChoosedAppManager.getInstance(mContext).getInstallAppList().add(appInfo);
//        this.notifyDataSetChanged();
//    }
}
