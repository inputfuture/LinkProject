package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.Contact;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * 联系人列表适配器。
 *
 * @author
 */
public class ContactAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<Contact> mList;
    String previewStr;
    int mPosition;

    public ContactAdapter(Context context, ArrayList<Contact> objects) {
        this.mList = objects;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = View.inflate(mContext, R.layout.item_contact, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) view.findViewById(R.id.name);
            holder.tv_phone = (TextView) view.findViewById(R.id.phone);
            holder.tv_alpha = (TextView) view.findViewById(R.id.sort_key);
            holder.alpha = (LinearLayout) view.findViewById(R.id.sort_key_layout);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        mPosition = position;

        holder.tv_name.setText(mList.get(position).getName());
        holder.tv_phone.setText(mList.get(position).getNumber().replace(" ", ""));
        String currentStr = getAlpha(position);
        previewStr = (position - 1) >= 0 ? getAlpha(position - 1) : " ";
        if (!previewStr.equals(currentStr)) {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.tv_alpha.setText(currentStr);
        } else {
            holder.alpha.setVisibility(View.GONE);
        }


        return view;

    }

    static class ViewHolder {
        public LinearLayout alpha;
        public TextView tv_name;
        public TextView tv_phone;
        public TextView tv_alpha;
    }

    public void setContactList(ArrayList<Contact> list) {
        this.mList = list;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    //TODO:
    public int getPosition() {
        return mPosition;
    }

    //TODO:
    public String getCurrentAlpha() {
        return previewStr;
    }

    private String getAlpha(int position) {
        Contact contact = mList.get(position);
        if (contact == null) {
            return "#";
        }

        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(contact.getSortKey()).matches()) {
            return contact.getSortKey();
        } else {
            return "#";
        }
    }

}
