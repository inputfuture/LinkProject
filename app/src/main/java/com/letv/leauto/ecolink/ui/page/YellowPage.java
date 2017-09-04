package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.model.Contact;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.view.SelectNumDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 2017/3/3.
 */
public class YellowPage extends BasePage {

    private SparseArray<List> sparseArraySparseArray = new SparseArray<>();
    @Bind(R.id.list)
    ListView mListView;
    private boolean mIsInit;

    public YellowPage(Context context) {
        super(context);

        ArrayList<Contact> arrayList = new ArrayList();
        arrayList.add(new Contact("事故122", "122", "0"));
        arrayList.add(new Contact("急救120", "120", "0"));
        arrayList.add(new Contact("匪警110", "110", "0"));
        arrayList.add(new Contact("火警119", "119", "0"));
        sparseArraySparseArray.put(0, arrayList);
        ArrayList<Contact> routehelp = new ArrayList();

        routehelp.add(new Contact("太平洋车险免费救援\n95500转3转3", "95500", "1"));
        routehelp.add(new Contact("人保车险免费救援\n95518转9", "95518", "1"));
        routehelp.add(new Contact("大陆汽车救援\n400-818-1010", "400-818-1010", "1"));
        routehelp.add(new Contact("平安车险免费救援\n95511转5转2", "95511", "1"));
        routehelp.add(new Contact("中石化免费救援\n95105988转7", "95105988", "1"));
        routehelp.add(new Contact("中路车盟道路救援\n400-810-8208", "400-810-8208", "1"));
        sparseArraySparseArray.put(1, routehelp);

        ArrayList<Contact> insurance = new ArrayList();
        insurance.add(new Contact("平安汽车保险\n95512", "95512", "2"));
        insurance.add(new Contact("中国人保汽车保险\n95518", "95518", "2"));
        insurance.add(new Contact("太平洋汽车保险\n95500", "95500", "2"));
        insurance.add(new Contact("中华联合汽车保险\n95585", "95585", "2"));
        insurance.add(new Contact("天安汽车保险\n95505", "95505", "2"));
        insurance.add(new Contact("阳光汽车保险\n95510", "95510", "2"));
        insurance.add(new Contact("大地汽车保险\n95590", "95590", "2"));
        insurance.add(new Contact("中国人寿财险\n95519", "95519", "2"));
        insurance.add(new Contact("太平洋汽车保险\n95589", "95589", "2"));
        insurance.add(new Contact("都邦汽车保险\n95586", "95586", "2"));
        insurance.add(new Contact("天平汽车保险\n95550", "95550", "2"));


        insurance.add(new Contact("永安汽车保险\n95502", "95502", "2"));
        insurance.add(new Contact("安邦汽车保险\n95569", "95569", "2"));
        insurance.add(new Contact("永城汽车保险\n95552", "95552", "2"));
        insurance.add(new Contact("华泰汽车保险\n4006095509", "4006095509", "2"));
        insurance.add(new Contact("渤海汽车保险\n4006116666", "4006116666", "2"));
        insurance.add(new Contact("大众汽车保险\n95507", "95507", "2"));
        insurance.add(new Contact("民安汽车保险\n95506", "95506", "2"));
        insurance.add(new Contact("华安汽车保险\n95556", "95556", "2"));
        insurance.add(new Contact("安诚汽车保险\n4000500000", "4000500000", "2"));
        sparseArraySparseArray.put(2, insurance);


    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.page_yellow, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void initData() {
        if (!mIsInit) {
            mListView.setAdapter(new ListAdapter(ct, sparseArraySparseArray));
            mIsInit = true;
        }
    }


    class ListAdapter extends BaseAdapter {
        SparseArray<List> mList;
        Context mContext;

        public ListAdapter(Context context, SparseArray<List> list) {
            mContext = context;
            mList = list;

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

        class ViewHolder {
            TextView tv_Letter;
            ReWriteGridView gridView;
            RelativeLayout rl_catalog;
        }


        @Override
        public View getView(int position, View contentView, ViewGroup arg2) {
            ViewHolder viewHolder = null;
            if (contentView == null) {
                viewHolder = new ViewHolder();
                contentView = LayoutInflater.from(ct).inflate(R.layout.addmemberfromlocal, null);
                viewHolder.rl_catalog = (RelativeLayout) contentView.findViewById(R.id.rl_catalog_addmember);
                viewHolder.gridView = (ReWriteGridView) contentView.findViewById(R.id.gv_addmember_loacl);
                viewHolder.tv_Letter = (TextView) contentView.findViewById(R.id.catalog_addmember);
                contentView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) contentView.getTag();
            }

            final ArrayList<Contact> cont = (ArrayList<Contact>) mList.get(position);


//            if (position == getPositionForSection(section)) {
//                viewHolder.rl_catalog.setVisibility(View.VISIBLE);
//                viewHolder.tv_Letter.setText(cont.get(0).getSortLetters());
//            } else {
//                viewHolder.rl_catalog.setVisibility(View.GONE);
//            }
            switch (position) {
                case 0:
                    viewHolder.tv_Letter.setText(mContext.getString(R.string.common_tel));
                    break;
                case 1:
                    viewHolder.tv_Letter.setText(mContext.getString(R.string.route_help_tel));
                    break;
                case 2:
                    viewHolder.tv_Letter.setText(mContext.getString(R.string.insurance_tel));
                    break;
            }
            if (GlobalCfg.IS_POTRAIT) {
                viewHolder.gridView.setNumColumns(2);
            } else {
                viewHolder.gridView.setNumColumns(3);
            }
            viewHolder.gridView.setAdapter(new GridAdapter(ct, cont));

//            viewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//                    if (cont.get(position).getNumList().size()>1) {
//                        SelectNumDialog selectNumDialog=new SelectNumDialog(ct,cont.get(position));
//                        selectNumDialog.show();
//                    }else if (cont.get(position).getNumber() != null) {
//                        callPhone(cont.get(position).getNumber());
//                    }
//                }
//            });
            return contentView;
        }


    }

    class GridAdapter extends BaseAdapter {
        Context ct;
        ArrayList<Contact> contacts;

        public GridAdapter(Context ct, ArrayList<Contact> cont) {
            this.ct = ct;
            contacts = cont;

        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View argconvertView, ViewGroup parent) {
            ViewHoldertwo viewHolder = null;
            if (argconvertView == null) {
                viewHolder = new ViewHoldertwo();
                argconvertView = LayoutInflater.from(ct).inflate(R.layout.myfamily_member_gv_item, null);
                viewHolder.tv_teltphone_name = (TextView) argconvertView.findViewById(R.id.tv_teltphone_name);
                viewHolder.number= (TextView) argconvertView.findViewById(R.id.number);
                argconvertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHoldertwo) argconvertView.getTag();
            }

            if (contacts.get(position).getName() != null) {
                String name=contacts.get(position).getName();
                if (name.contains("\n")){
                    String[] names=name.split("\n");
                    String  html="<font size=\"14\">" + names[0] + "</font>"+"<br>"+"<font size=\"20\">" + names[1] + "</font>"+"</br>";
                    viewHolder.tv_teltphone_name.setText(names[0]);
                    viewHolder.number.setText(names[1]);
                    viewHolder.number.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.tv_teltphone_name.setText(contacts.get(position).getName());
                    viewHolder.number.setText("");
                    viewHolder.number.setVisibility(View.GONE);}
            } else {
                viewHolder.tv_teltphone_name.setText(contacts.get(position).getNumber());
                viewHolder.number.setText("");
                viewHolder.number.setVisibility(View.GONE);
            }
            argconvertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkConfirmDialog networkConfirmDialog=new NetworkConfirmDialog(ct,contacts.get(position).getNumber(),R.string.call,R.string.cancel);
                    networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                        @Override
                        public void onConfirm(boolean checked) {
                            String number = contacts.get(position).getNumber();
//                            if (android.os.Build.MODEL.contains("vivo") || android.os.Build.MODEL.contains("coolpad")) {
//                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
//                                ct.startActivity(intent);
//
//                            } else {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                                ct.startActivity(intent);
//                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    networkConfirmDialog.setCancelable(false);
                    networkConfirmDialog.show();


                }
            });


            return argconvertView;
        }

        class ViewHoldertwo {
            TextView tv_teltphone_name;
            TextView number;
        }
    }
}
