package com.letv.leauto.ecolink.ui.leradio_interface;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.ui.leradio_interface.data.ChannelFocus;
import com.letv.leauto.ecolink.utils.DensityUtils;

import java.util.ArrayList;

/**
 * Created by why on 2016/7/25.
 */
public class RadiaoAlbumGridAdapter extends BaseAdapter {

    private ArrayList<LeAlbumInfo> mChannelFocusList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int mIndex = -1;
    private Glide mGlide;

    public RadiaoAlbumGridAdapter(Context context, ArrayList<LeAlbumInfo> argChannelFocus) {
        mContext=context;
        mInflater=LayoutInflater.from(mContext);
        mChannelFocusList=argChannelFocus;
        mGlide = Glide.get(mContext);
    }

    @Override
    public int getCount() {
        return mChannelFocusList.size();
    }
    //标识选择的Item
    public void setSeclection(int position) {
        mIndex = position;
        notifyDataSetChanged();
    }
    @Override
    public Object getItem(int position) {
        return mChannelFocusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_radio_album_grid, null);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (GlobalCfg.IS_POTRAIT){
            int width= (DensityUtils.getScreenWidth(mContext)-mContext.getResources().getDimensionPixelSize(R.dimen.size_40dp)*2
                    -mContext.getResources().getDimensionPixelSize(R.dimen.size_40dp))/2;
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) viewHolder.image.getLayoutParams();
            layoutParams.width=width;
            layoutParams.height=width;
            viewHolder.image.setLayoutParams(layoutParams);


        }else{
            int width= (DensityUtils.getScreenWidth(mContext)-mContext.getResources().getDimensionPixelSize(R.dimen.size_60dp)*2
                    -mContext.getResources().getDimensionPixelSize(R.dimen.size_40dp)*3)/4;
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) viewHolder.image.getLayoutParams();
            layoutParams.width=width;
            layoutParams.height=width;
            viewHolder.image.setLayoutParams(layoutParams);
        }
        final LeAlbumInfo item = mChannelFocusList.get(position);
        viewHolder.name.setText((item.NAME == null ? "未命名" : item.NAME));
        final ImageView finalImageView=viewHolder.image;
        String imageUrl = item.IMG_URL;
        if (imageUrl != null && !"".equals(imageUrl.trim())) {
            //需要进行取缓存操作

            mGlide.with(mContext).load(imageUrl)
                    .asBitmap()
                    .skipMemoryCache(true)
                    .placeholder(R.mipmap.ic_defult)
                    .error(R.mipmap.ic_defult)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new BitmapImageViewTarget(finalImageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            finalImageView.setImageBitmap(resource);
                        }
                    });
        } else {
            finalImageView.setImageResource(R.mipmap.ic_defult);
        }
        // 点击改变选中listItem的背景色
        if (mIndex == position) {
            //finalImageView.setBackgroundResource(R.drawable.gridview_item_image_bg);
            //viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.green_color));
        } else {
            viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
            finalImageView.setBackgroundResource(R.color.transparent);
        }
        return convertView;
    }


    class ViewHolder{
        ImageView image;
        TextView name;
    }
}
