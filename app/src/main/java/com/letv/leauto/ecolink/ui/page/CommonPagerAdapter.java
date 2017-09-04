package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.letv.leauto.ecolink.ui.base.BasePage;

import java.util.ArrayList;

/**
 * Created by why on 2017/2/14.
 */

public class CommonPagerAdapter extends PagerAdapter{
    private Context mct;
    private ArrayList<BasePage> pages;
    private ArrayList<String> List;

    public CommonPagerAdapter(Context ct, ArrayList<BasePage> pages, ArrayList<String> argList) {
        this.mct = ct;
        this.pages = pages;
        List=argList;
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
        container.addView(pages.get(position).getContentView(),0);
        return pages.get(position).getContentView();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pages.get(position).getContentView());
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if (List!=null){
            return   List.get(position);
        }else {
            return null;
        }
    }
}
