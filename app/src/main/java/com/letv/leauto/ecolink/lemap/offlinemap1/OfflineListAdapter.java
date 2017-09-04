package com.letv.leauto.ecolink.lemap.offlinemap1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/8/4.
 */
public class OfflineListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener {

    private static final String TAG = "OfflineListAdapter";
    private boolean[] isOpen;// 记录一级目录是否打开

    private List<String> names;

    private List<OfflineMapProvince> provinceList = null;

    private OfflineMapManager amapManager;
    private Context mContext;
    List<OfflineMapCity> downloadOfflineMapCityList;

    public OfflineListAdapter(List<OfflineMapProvince> provinceList,

                              OfflineMapManager amapManager, Context mContext) {
        this.provinceList = provinceList;

        this.amapManager = amapManager;
        this.mContext = mContext;
        names = Arrays.asList(mContext.getString(R.string.str_Whole_country),mContext.getString(R.string.str_directly_Municipality),mContext.getString(R.string.str_hm),mContext.getString(R.string.str_Province));

        isOpen = new boolean[provinceList.size()];
//        this.downloadOfflineMapCityList = getOfflineDownloadCityList();
//        Trace.Error("oialkdjf", downloadOfflineMapCityList.toString());
    }




    @Override
    public int getGroupCount() {
        return provinceList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (isNormalProvinceGroup(groupPosition)) {
            // 普通省份的第一个位置放省份
            return provinceList.get(groupPosition).getCityList().size() + 1;
        }


        return provinceList.get(groupPosition).getCityList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return provinceList.get(groupPosition).getProvinceName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        TextView group_text;
        ImageView group_image;
        if (convertView == null) {
            if (GlobalCfg.IS_POTRAIT) {
                convertView = (RelativeLayout) RelativeLayout.inflate(
                        mContext, R.layout.offlinemap_group_p, null);
            } else {
                convertView = (RelativeLayout) RelativeLayout.inflate(
                        mContext, R.layout.offlinemap_group, null);
            }
        }

        group_text = (TextView) convertView.findViewById(R.id.group_text);
        group_image = (ImageView) convertView
                .findViewById(R.id.group_image);
        String provinceName = provinceList.get(groupPosition).getProvinceName();
        group_text.setText(provinceName);
        group_image.setTag(provinceName);


        if (names.contains(provinceName) && group_image.getTag().equals(provinceName)) {
            group_image.setVisibility(View.INVISIBLE);
        } else {
            group_image.setVisibility(View.VISIBLE);
        }

        if (isNormalProvinceGroup(groupPosition)) {
           group_text.setTextSize(16);
        } else {
            group_text.setTextSize(12);
        }
        if (isOpen[groupPosition]) {
            group_image.setImageDrawable(mContext.getResources().getDrawable(
                    R.mipmap.offmap_up));
        } else {
            group_image.setImageDrawable(mContext.getResources().getDrawable(
                    R.mipmap.offmap_down));
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            OfflineChildProvince offLineChild = new OfflineChildProvince(mContext, amapManager, false);
            convertView = offLineChild.getOffLineChildView();
            viewHolder.mOfflineChild = offLineChild;
            convertView.setTag(viewHolder);
        }
        OfflineMapCity mapCity = null;

        viewHolder.mOfflineChild.setProvince(false);

        if (isNormalProvinceGroup(groupPosition)) {
            if (isProvinceItem(groupPosition, childPosition)) {
                // 如果是省份，为了方便，进行一下处理
                mapCity = getCicy(provinceList.get(groupPosition));
                viewHolder.mOfflineChild.setProvince(true);
                viewHolder.mOfflineChild.setDownLoadVisble(false);
                viewHolder.mOfflineChild.getOffLineChildView().setEnabled(false);

            } else {
                // 减1处理，第一个被放置了省份
                mapCity = provinceList.get(groupPosition).getCityList().get(childPosition - 1);
                viewHolder.mOfflineChild.setDownLoadVisble(true);
                viewHolder.mOfflineChild.getOffLineChildView().setEnabled(true);
            }
        } else {
            mapCity = provinceList.get(groupPosition).getCityList().get(childPosition);
        }
        Trace.Error(TAG, "cityName : " + mapCity.getCity() + "--- state : " + mapCity.getState() + " --- completeCode : " + mapCity.getcompleteCode());
        viewHolder.mOfflineChild.setOffLineCity(mapCity);

        return convertView;
    }


    public final class ViewHolder {
        public OfflineChildProvince mOfflineChild;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onGroupCollapse(int groupPosition) {

        if (groupPosition < 3) {
            isOpen[groupPosition] = true;
        } else {
            isOpen[groupPosition] = false;
        }
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        isOpen[groupPosition] = true;
    }

    /**
     * 是否为普通省份
     * 不是直辖市，概要图，港澳，省份
     *
     * @param groupPosition
     * @return
     */
    private boolean isNormalProvinceGroup(int groupPosition) {
//		return groupPosition != 0 && groupPosition != 1 && groupPosition != 2;
        return groupPosition > 3;
    }


    private boolean isProvinceItem(int groupPosition, int childPosition) {
        // 不是特殊省份，而且子栏目中第一栏
        return isNormalProvinceGroup(groupPosition) && childPosition == 0;
    }

    /**
     * 把一个省的对象转化为一个市的对象
     */
    public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
        OfflineMapCity aMapCity = new OfflineMapCity();
        aMapCity.setCity(aMapProvince.getProvinceName());
        aMapCity.setSize(aMapProvince.getSize());
        aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
        aMapCity.setState(aMapProvince.getState());
        aMapCity.setUrl(aMapProvince.getUrl());
        return aMapCity;
    }


}
