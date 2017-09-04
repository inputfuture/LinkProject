package com.letv.leauto.ecolink.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.Contact;
import com.letv.leauto.ecolink.utils.PhoneNumberUtil;
import com.letv.leauto.ecolink.utils.TimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 王华燕 on 16/4/22.
 */
public class SelectNumDialog extends Dialog implements AdapterView.OnItemClickListener {
    private Context mContext;
    private RelativeLayout mContentView;
    private Contact mContact;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.listview)
    ListView mListview;


    public SelectNumDialog(Context context, Contact argContact) {
        super(context, R.style.Dialog);
        mContext = context;
        mContact = argContact;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_num_layout);
        ButterKnife.bind(this);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) mContext.getResources().getDimension(
                R.dimen.dialog_update_width);
        params.height = (int) mContext.getResources().getDimension(
                R.dimen.dialog_update_height);
        getWindow().setAttributes(params);
        mListview.setOnItemClickListener(this);
        mTvName.setText(mContact.getName());
        MyAdapter myAdapter = new MyAdapter();
        mListview.setAdapter(myAdapter);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            callPhone(mContact.getNumList().get(position));
            dismiss();

    }

    private void callPhone(String str) {
//        if (android.os.Build.MODEL.contains("vivo") || android.os.Build.MODEL.contains("coolpad")) {
//            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + str));
//            mContext.startActivity(intent);
//
//        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + str));
            mContext.startActivity(intent);
//        }
    }

    class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mContact.getNumList().size();
        }

        @Override
        public Object getItem(int position) {
            return mContact.getNumList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View argconvertView, ViewGroup parent) {
            ViewHoldertwo viewHolder = null;
            if (argconvertView == null) {
                viewHolder = new ViewHoldertwo();
                argconvertView = LayoutInflater.from(mContext).inflate(R.layout.select_num_item_layout, null);
                viewHolder.tv_num = (TextView) argconvertView.findViewById(R.id.tv_num);
                viewHolder.tv_num_type = (TextView) argconvertView.findViewById(R.id.tv_num_type);
                argconvertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHoldertwo) argconvertView.getTag();
            }

            String phoneNumber = mContact.getNumList().get(position);
            viewHolder.tv_num.setText(phoneNumber);
            if (position == 0) {
                //setPhoneNumberLocation(viewHolder.tv_num_type, phoneNumber);
                int type =  PhoneNumberUtil.getNumberType(mContext,phoneNumber);
                viewHolder.tv_num_type.setText(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(type));
            } else {
               int type =  PhoneNumberUtil.getNumberType(mContext,phoneNumber);
                viewHolder.tv_num_type.setText(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(type));
            }

            return argconvertView;
        }

        private void setPhoneNumberLocation(final TextView view, final String number) {
            new Thread() {
                @Override
                public void run() {
                    final String str = PhoneNumberUtil.getNumberLocation(number);
                    Activity activity = (Activity) mContext;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String array[] = str.split(" ");
                            if (array.length >=3 ) {
                                view.setText(PhoneNumberUtil.filteNumber(array[2]));
                            }
                        }
                    });
                }
            }.start();
        }

        class ViewHoldertwo {
            TextView tv_num;
            TextView tv_num_type;
        }
    }
}
