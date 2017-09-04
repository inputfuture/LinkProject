package com.letv.leauto.ecolink.lemap.offlinemap1;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/4.
 */
public class OfflineDownloadedAdapter extends BaseAdapter {

    private SparseArray<Boolean> isSelected;
    private OfflineMapManager mOfflineMapManager;

    private List<OfflineMapCity> cities;

    private Context mContext;

    private OfflineDownedChild currentOfflineChild;

    private  List<Integer>  isSelectPosition;

    public  boolean isShowControlTextView=false;
    public boolean isShowCheck;


    public OfflineDownloadedAdapter(Context context, OfflineMapManager offlineMapManager, boolean isShowCheck) {
        this.mContext = context;
        this.mOfflineMapManager = offlineMapManager;
        this.isShowCheck=isShowCheck;
        cities = new ArrayList<OfflineMapCity>();
        isSelected=new SparseArray<>();
        isSelectPosition=new ArrayList<>();
        initCityList();

    }

    public List<Integer> getChosePosition(){
        return  isSelectPosition;
    }


    public  SparseArray<Boolean> getIsSelected() {
        return isSelected;
    }

    public List<OfflineMapCity> getCities(){
        return  cities;
    }

    private void initCityList() {
        if(cities!=null){
            long start = System.currentTimeMillis();
            for (Iterator it = cities.iterator(); it.hasNext();) {
                OfflineMapCity i = (OfflineMapCity) it.next();
                it.remove();
            }
        }
        cities.addAll(mOfflineMapManager.getDownloadOfflineMapCityList());

        for (int i = 0; i <cities.size() ; i++) {
            isSelected.put(i,false);
        }

        for (int i = 0; i <isSelectPosition.size() ; i++) {
            isSelected.put(isSelectPosition.get(i),true);
        }

        if(cities.size()>0){
            isShowControlTextView=true;
        }else{
            isShowControlTextView=false;
        }


        notifyDataSetChanged();
    }

    /**
     * 重新初始化数据加载数据
     */
    public void notifyDataChange() {
        long start = System.currentTimeMillis();
        initCityList();
        Trace.Debug("amap", "Offline Downloading notifyData cost: " + (System.currentTimeMillis() -start));
    }



    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int index) {
        return cities.get(index);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    public  static class ViewHolder {
        public OfflineDownedChild mOfflineChild;
        public  CheckBox checkBox;
        public  TextView name;
        public  TextView name_size;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();

            currentOfflineChild = new OfflineDownedChild(mContext,mOfflineMapManager,true);
            convertView = currentOfflineChild.getOffLineChildView();
            viewHolder.checkBox=(CheckBox)  convertView.findViewById(R.id.offine_checked_item);
            viewHolder.name=(TextView)  convertView.findViewById(R.id.name);
            viewHolder.name_size=(TextView)  convertView.findViewById(R.id.name_size);


            viewHolder.mOfflineChild = currentOfflineChild;
            convertView.setTag(viewHolder);
        }
        OfflineMapCity offlineMapCity = (OfflineMapCity) getItem(position);
        viewHolder.checkBox.setChecked(isSelected.get(position));
        viewHolder.mOfflineChild.setOffLineCity(offlineMapCity);
        if(isShowCheck){
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            if (viewHolder.checkBox.isChecked()){
                viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.green_color));
                viewHolder.name_size.setTextColor(mContext.getResources().getColor(R.color.green_color));
            }else {
                viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                viewHolder.name_size.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
            }
        }else {
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        return convertView;
    }











}
