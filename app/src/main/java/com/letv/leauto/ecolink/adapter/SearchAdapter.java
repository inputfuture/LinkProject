package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.Contact;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;


public class SearchAdapter extends BaseAdapter {


    private ArrayList<Contact> searchList;
//    private ArrayList<Contact> mUnfilteredData;
    private Context mcontext;
    private String mprefix;
//    private ArrayFilter mFilter;
    private CharSequence str;

    public SearchAdapter(CharSequence s,Context context, ArrayList<Contact> objects) {
        super();
        searchList = objects;
        mcontext = context;
        str = s;
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
            convertView = View.inflate(mcontext, R.layout.item_searchnum, null);
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
        String num = searchList.get(position).getNumber();
        SpannableStringBuilder style1 = new SpannableStringBuilder(num);
        mprefix = str.toString();
        Trace.Debug("ccy", "getView: "+mprefix);
        if (mprefix != null) {
            int start, end;
            start = num.indexOf(mprefix);
            if (start != -1) {
                end = start + mprefix.length();
                style1.setSpan(new ForegroundColorSpan(mcontext
                                .getResources().getColor(R.color.green_color)), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }
        holder.num.setText(style1);
//        holder.num.setText(searchList.get(position).getNumber());
        return convertView;
    }


    static class ViewHolder {
        TextView name;
        TextView num;
    }

}
