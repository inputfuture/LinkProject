package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.Contact;

import java.util.ArrayList;


public class SameSearchAdapter extends BaseAdapter {


    private ArrayList<Contact> searchList;
    private Context mcontext;


    public SameSearchAdapter(Context context, ArrayList<Contact> objects) {
        super();
        searchList = objects;
        mcontext = context;
    }

    @Override
    public int getCount() {
        return searchList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mcontext, R.layout.item_samesearchnum, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.num = (TextView) convertView
                    .findViewById(R.id.num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(searchList.get(position).getName());
        holder.num.setText(searchList.get(position).getNumber());
        return convertView;
    }


    static class ViewHolder {
        TextView name;
        TextView num;
    }

}
