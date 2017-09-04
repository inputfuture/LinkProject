package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.Contact;

import java.util.ArrayList;


public class CallLogAdapter extends BaseAdapter {


    private ArrayList<Contact> callLogList;
    private Context mcontext;


    public CallLogAdapter(Context context, ArrayList<Contact> objects) {
        super();
        callLogList = objects;
        mcontext = context;
    }

    @Override
    public int getCount() {
        return callLogList.size();
    }

    @Override
    public Object getItem(int position) {
        return callLogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mcontext, R.layout.item_calllog, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.time = (TextView) convertView
                    .findViewById(R.id.time);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        if (null != callLogList.get(position).getName() && !"".equals(callLogList.get(position).getName())) {
//            holder.name.setText(callLogList.get(position).getName()+"("+callLogList.get(position).getNum()+")");
//        } else {
//            holder.name.setText(callLogList.get(position).getNumber()+"("+callLogList.get(position).getNum()+")");
//        }
        if(callLogList.get(position).getNum() == 1){
            holder.name.setText(callLogList.get(position).getName());
        }else {
            holder.name.setText(callLogList.get(position).getName()+"("+callLogList.get(position).getNum()+")");
        }

        if (callLogList.get(position).getType() == 3) {
//            holder.imageView.setVisibility(View.VISIBLE);
            holder.name.setTextColor(Color.parseColor("#f00f00"));
//            holder.imageView.setImageResource(R.mipmap.phone_in);
        }else if (callLogList.get(position).getType() == 1){
            holder.name.setTextColor(mcontext.getResources().getColor(R.color.transparent_60));
//            holder.imageView.setVisibility(View.VISIBLE);
//            holder.imageView.setImageResource(R.mipmap.phone_in);
        }else {
            holder.name.setTextColor(mcontext.getResources().getColor(R.color.transparent_60));
//            holder.imageView.setVisibility(View.VISIBLE);
//            holder.imageView.setImageResource(R.mipmap.phone_out);
        }
        holder.time.setText(callLogList.get(position).getTime());
        return convertView;
    }


    static class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView time;
    }

}
