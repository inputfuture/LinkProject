package com.letv.leauto.ecolink.lemap.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.lemap.entity.SearchPoi;
import com.letv.leauto.ecolink.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchPoiAdapter extends BaseAdapter implements Filterable {
    private ArrayFilter mFilter;
    private List<SearchPoi> mList;
    private Context context;
    private ArrayList<SearchPoi> mUnfilteredData;
    private String mprefix;
    private boolean isAdd;
    private boolean mNeedSort;
    private int type;

    public SearchPoiAdapter(List<SearchPoi> mList, Context context, int type) {
        this.mList = mList;
//        sortList(this.mList);
        this.context = context;
        this.type=type;
        if (type == MapCfg.SEARCH_TYPE_ADD) {
            this.isAdd = true;
        } else {
            this.isAdd = false;
        }

    }

    public void setType(int type) {
        this.type = type;
        if (type == MapCfg.SEARCH_TYPE_ADD) {
            this.isAdd = true;
        } else {
            this.isAdd = false;
        }
    }

    public void setmList(List<SearchPoi> mList, String pre, boolean needSort) {
        this.mprefix = pre;
        this.mList = mList;
        this.mNeedSort = needSort;
        if(needSort) {
            sortList(this.mList);
        }
    }

    public void setPrefix(String pre){
        this.mprefix = pre;
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
            if (GlobalCfg.IS_POTRAIT) {
                convertView = View.inflate(context, R.layout.search_poi_item, null);
            } else {
                convertView = View.inflate(context, R.layout.search_poi_item_l, null);
            }


            holder.addressName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.addressDistrict = (TextView) convertView.findViewById(R.id.tv_district);
            holder.distance = (TextView) convertView.findViewById(R.id.tv_distance);
            holder.rightIcon = (ImageView) convertView.findViewById(R.id.iv_arraw);
            holder.addressLayout= (RelativeLayout) convertView.findViewById(R.id.lyt_address);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        if (isAdd) {
            holder.rightIcon.setImageResource(R.mipmap.map_ic_add);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener!=null){
                        mClickListener.itemClick(position);
                    }
                }
            });
        } else {
            holder.rightIcon.setImageResource(R.mipmap.location_go);
            holder.addressLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.addressClick(position);
                    }

                }
            });
            holder.rightIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.iconClick(position);

                }
            });
        }
        if(mNeedSort){
            holder.distance.setVisibility(View.VISIBLE);
        }else{
            holder.distance.setVisibility(View.INVISIBLE);
        }
        String addr = mList.get(position).getAddrname();
        String district = mList.get(position).getDistrict();
        double distance = mList.get(position).getDistance();
        SpannableStringBuilder style1 = new SpannableStringBuilder(addr);
        if (mprefix != null) {
            int start, end;
            start = addr.indexOf(mprefix);
            if (start != -1) {
                end = start + mprefix.length();
                style1.setSpan(new ForegroundColorSpan(context
                                .getResources().getColor(R.color.green_color)), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }
        Log.d("KeySearchFragment","add="+addr+", district="+district);
        holder.addressName.setText(style1);
        if(TextUtils.isEmpty(district)){
            holder.addressDistrict.setText(style1);
        }else{
            holder.addressDistrict.setText(district);
        }

        String distanceString = DistanceUtil.getDistanceStr(distance);
        holder.distance.setText(distanceString);
        return convertView;
    }

    static class ViewHolder {
        public ImageView rightIcon;
        public TextView addressName;
        public TextView addressDistrict;
        public TextView distance;
        public RelativeLayout addressLayout;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            try {
                mprefix = prefix.toString();
            } catch (Exception e) {
                mprefix = "";
            }

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<SearchPoi>(mList);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<SearchPoi> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<SearchPoi> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<SearchPoi> newValues = new ArrayList<SearchPoi>(count);

                for (int i = 0; i < count; i++) {
                    SearchPoi pc = unfilteredValues.get(i);
                    if (pc != null && pc.getAddrname().startsWith(prefixString)) {
                        newValues.add(pc);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            //noinspection unchecked
            mList = (List<SearchPoi>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }

    private void sortList(List<SearchPoi> list){
        if(list == null || list.size() <= 1  ){
            return;
        }

        Collections.sort(list, new Comparator<SearchPoi>(){
            @Override
            public int compare(SearchPoi lhs, SearchPoi rhs) {
                Double dLhs = Double.valueOf(lhs.getDistance());
                Double dRhs = Double.valueOf(rhs.getDistance());
                return dLhs.compareTo(dRhs);

            }

        });
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
