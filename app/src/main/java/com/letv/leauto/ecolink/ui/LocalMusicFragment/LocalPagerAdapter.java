package com.letv.leauto.ecolink.ui.LocalMusicFragment;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.letv.leauto.ecolink.ui.base.BasePage;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/5.
 */
public class LocalPagerAdapter extends PagerAdapter{

    private Context mct;
    private ArrayList<BasePage> pages;
    private ArrayList<String> List;

    public LocalPagerAdapter(Context mct,ArrayList<BasePage> pages,ArrayList<String> list){
        this.mct=mct;
        this.pages=pages;
        this.List=list;
    }


    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pages.get(position).getContentView(),0);
        return  pages.get(position).getContentView();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pages.get(position).getContentView());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return List.get(position);
    }
}
