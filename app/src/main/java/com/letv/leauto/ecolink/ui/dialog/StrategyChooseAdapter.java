package com.letv.leauto.ecolink.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.lemap.navi.Utils;
import com.letv.leauto.ecolink.lemap.navi.routebean.StrategyStateBean;

import java.util.List;

/**
 * 驾车偏好设置adapter，高速优先与不走高速、避免收费互斥
 * Created by ligen on 16/10/28.
 */
public class StrategyChooseAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private List<StrategyStateBean> mStrategys;

    public StrategyChooseAdapter(Context context, List<StrategyStateBean> strategys) {
        mContext = context;
        mStrategys = strategys;
    }

    @Override
    public int getCount() {
        return mStrategys.size();
    }

    @Override
    public StrategyStateBean getItem(int i) {
        return mStrategys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_strategy_choose, null);
            holder.mStrategyNameText = (TextView) view.findViewById(R.id.strategy_name);
            holder.mStrategyChooseFlagImage = (ImageView) view.findViewById(R.id.strategy_choose_flag);
            holder.mItem= (RelativeLayout) view.findViewById(R.id.strategy_item);
            holder.mIconView= (ImageView) view.findViewById(R.id.strategy_check_icon);
            holder.mStrategyBean = mStrategys.get(i);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        view.setOnClickListener(this);
        StrategyStateBean strategyBean = mStrategys.get(i);
        if (strategyBean != null) {
            int strategyCode = strategyBean.getStrategyCode();
            String strategyName = getStrategyName(strategyCode);
            holder.mStrategyNameText.setText(strategyName);

        }
        if (holder != null && holder.mStrategyBean != null) {
            boolean isOpen = holder.mStrategyBean.isOpen();
            if (isOpen) {
                holder.mStrategyChooseFlagImage.setImageResource(getStrategyOpenResource(holder.mStrategyBean.getStrategyCode()));
                holder.mItem.setBackgroundColor(mContext.getResources().getColor(R.color.green_color));
                holder.mStrategyNameText.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mIconView.setVisibility(View.VISIBLE);


            } else {
                holder.mStrategyChooseFlagImage.setImageResource(getStrategyCloseResource(holder.mStrategyBean.getStrategyCode()));
                holder.mItem.setBackgroundColor(mContext.getResources().getColor(R.color.black_10));
                holder.mStrategyNameText.setTextColor(mContext.getResources().getColor(R.color.black_60));
                holder.mIconView.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private int getStrategyCloseResource(int strategyCode) {
        int resourceId = 0;
        switch (strategyCode) {
            case Utils.AVOID_CONGESTION:
                resourceId = R.mipmap.avoid_congestion;
                break;
            case Utils.AVOID_COST:
                resourceId = R.mipmap.avoid_cost;
                break;
            case Utils.AVOID_HIGHSPEED:
                resourceId = R.mipmap.avoid_highway;
                break;
            case Utils.PRIORITY_HIGHSPEED:
                resourceId = R.mipmap.highway_first;
                break;
            default:
                break;
        }
        return resourceId;
    }

    private int getStrategyOpenResource(int strategyCode) {
        int resourceId = 0;
        switch (strategyCode) {
            case Utils.AVOID_CONGESTION:
                resourceId = R.mipmap.avoid_congestion_sel;
                break;
            case Utils.AVOID_COST:
                resourceId = R.mipmap.avoid_cost_sel;
                break;
            case Utils.AVOID_HIGHSPEED:
                resourceId = R.mipmap.avoid_highway_sel;
                break;
            case Utils.PRIORITY_HIGHSPEED:
                resourceId = R.mipmap.highway_first_sel;
                break;
            default:
                break;
        }
        return resourceId;
    }

    private String getStrategyName(int strategyCode) {
        String strategyName = "";
        switch (strategyCode) {
            case Utils.AVOID_CONGESTION:
                strategyName = mContext.getString(R.string.congestion);
                break;
            case Utils.AVOID_COST:
                strategyName = mContext.getString(R.string.cost);
                break;
            case Utils.AVOID_HIGHSPEED:
                strategyName = mContext.getString(R.string.avoidhightspeed);
                break;
            case Utils.PRIORITY_HIGHSPEED:
                strategyName = mContext.getString(R.string.hightspeed);
                break;
            default:
                break;
        }
        return strategyName;
    }

    @Override
    public void onClick(View view) {
        Holder holder = (Holder) view.getTag();
        if (holder == null || holder.mStrategyBean == null) {
            return;
        }
        if (holder.mStrategyBean.isOpen()) {
            holder.mStrategyBean.setOpen(false);
        } else {
            holder.mStrategyBean.setOpen(true);
        }

        int strategyCode = holder.mStrategyBean.getStrategyCode();

        if (strategyCode == Utils.PRIORITY_HIGHSPEED) {
            for (StrategyStateBean bean : mStrategys) {
                if (bean.getStrategyCode() == Utils.AVOID_COST || bean.getStrategyCode() == Utils.AVOID_HIGHSPEED) {
                    bean.setOpen(false);
                }
            }
        }

        if (strategyCode == Utils.AVOID_COST || strategyCode == Utils.AVOID_HIGHSPEED) {
            for (StrategyStateBean bean : mStrategys) {
                if (bean.getStrategyCode() == Utils.PRIORITY_HIGHSPEED) {
                    bean.setOpen(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class Holder {
        public TextView mStrategyNameText;
        public ImageView mStrategyChooseFlagImage;
        public StrategyStateBean mStrategyBean;
        public RelativeLayout mItem;
        public ImageView mIconView;
    }
}
