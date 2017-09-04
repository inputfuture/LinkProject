package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;

import java.util.ArrayList;
import java.util.List;

public class SortInfoAdapter extends PagerAdapter {
    private Context mct;
    private ArrayList<BasePage> pages;
    private List<Channel> mChannels;

    public SortInfoAdapter(Context ct, ArrayList<BasePage> pages, List<Channel> sortInfos) {
        mChannels =sortInfos;
        this.mct = ct;
        this.pages = pages;

    }

    public SortInfoAdapter(Context mct, ArrayList<BasePage> pages) {
        this.mct = mct;
        this.pages = pages;
    }



    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pages.get(position).getContentView(), 0);
        return pages.get(position).getContentView();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position>=0&&position<pages.size()){
            container.removeView(pages.get(position).getContentView());}
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return   mChannels ==null?null: mChannels.get(position).getName();

    }
}