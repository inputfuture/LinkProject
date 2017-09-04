package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.Contact;
import com.letv.leauto.ecolink.manager.ContactManager;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.callbean.CharacterParser;
import com.letv.leauto.ecolink.ui.callbean.SideBar;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.fragment.SelectNumFragment;
import com.letv.leauto.ecolink.ui.view.SelectNumDialog;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lonng on 15/12/8.
 */
public class CallContactPage extends BasePage implements View.OnClickListener, SelectNumFragment.NumChangeListener {

    @Bind(R.id.contact_null)
    LinearLayout contact_null;
    @Bind(R.id.iv_search)
    ImageView iv_search;
    @Bind(R.id.contact_page)
    RelativeLayout contact_page;
    @Bind(R.id.contacts_list_view)
    ListView contacts_list_view;
    @Bind(R.id.contact_null_text)
    TextView contact_null_text;
    @Bind(R.id.create)
    Button mCreateContactBtn;
    //    @Bind(R.id.sidrbar_addmember)
//    SideBar sidrbar_addmember;
    private ArrayList<Contact> contactList=new ArrayList<>();
    //    private ContactAdapter mContactAdapter;
//    private HashMap<String, Integer> alphaIndexer;
    private CharacterParser characterParser;
    private NetworkConfirmDialog mNoPermissionDialog;

    public CallContactPage(Context context) {
        super(context);
    }
    private ArrayList<String> listSortLetters=new ArrayList<>();
    private List<ArrayList<Contact>> mShowListContacts;
    private AddMemberAdapter adapter;
    private boolean mIsInite;
    private Handler handler = new Handler() {


        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 10:
                    if (mShowListContacts !=null && mShowListContacts.size() > 0) {
                        adapter = new AddMemberAdapter();
                        contacts_list_view.setAdapter(adapter);
                    }
                    break;
                case MessageTypeCfg.MSG_SUBITEMS_OBTAINED:
                    ArrayList<Contact> contacts= (ArrayList<Contact>) msg.obj;
                    contactList.addAll(contacts);
                    if (contactList.size() > 0) {
                        mIsInite=true;
                        contact_null.setVisibility(View.GONE);
                        contact_page.setVisibility(View.VISIBLE);
                        setdata();
                        if (mNoPermissionDialog!=null){
                            mNoPermissionDialog.dismiss();
                            mNoPermissionDialog=null;
                        }
                    } else {
                        mIsInite=false;
                        Trace.Debug("#####nodate");
                        contact_null.setVisibility(View.VISIBLE);
                        contact_null_text.setText("通讯录未同步");
//                        contact_null_text.setText("");
                        contact_page.setVisibility(View.GONE);
                        popNoPermissionDialog();

                    }
                    break;
                case MessageTypeCfg.MSG_NO_CONNECTS_PERMISSION:
                    mIsInite=false;
                    Trace.Debug("#####nodate");
                    contact_null.setVisibility(View.VISIBLE);
                    contact_null_text.setText("通讯录未同步");
                    contact_page.setVisibility(View.GONE);
                    popNoPermissionDialog();
                    break;
                default:
                    break;
            }
        }
    };

    private void popNoPermissionDialog() {
        Trace.Debug("##### popNoPermissionDialog");
        if (mNoPermissionDialog==null){

            mNoPermissionDialog=new NetworkConfirmDialog(ct,"请检查通讯录权限", R.string.ok,R.string.cancel);
            mNoPermissionDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                @Override
                public void onConfirm(boolean checked) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    ct.startActivity(intent);
                    mNoPermissionDialog=null;

                }

                @Override
                public void onCancel() {
                    mNoPermissionDialog=null;
                }
            });
            Trace.Debug("##### popNoPermissionDialog");
            mNoPermissionDialog.setCancelable(false);
            mNoPermissionDialog.show();
        }
    }

    private void setdata() {
        new Thread(){
            @Override
            public void run() {
                List<Contact> lists = new ArrayList<Contact>();//设置完成setsortters
                mShowListContacts = new ArrayList<ArrayList<Contact>>();
                characterParser = new CharacterParser();
                // TODO: 2016/8/16
                if (contactList.size() > 0) {
                    //把首个字母改成大小写
                    for (int i = 0; i < contactList.size(); i++) {
                        Contact conts = contactList.get(i);
                        String pinyin = characterParser.getSelling(conts.getName());
                        String sortString = pinyin.substring(0, 1).toUpperCase();
                        Trace.Info("TAG", sortString);
                        if (sortString.matches("[A-Z]")) {
                            conts.setSortLetters(sortString.toUpperCase());
                        } else {
                            conts.setSortLetters("#");
                        }
                        lists.add(conts);
                    }
                    if (lists.size() > 0) {
                        //筛选字头相同的联系人
                        for (int i = 0; i < SideBar.b.length; i++) {
                            ArrayList<Contact> list2 = new ArrayList<Contact>();
                            String str = SideBar.b[i];
                            int count=0;
                            for (int j = 0; j < lists.size(); j++) {
                                Contact cont1 = lists.get(j);
                                if (cont1.getSortLetters().equals(str)) {
                                    list2.add(cont1);
                                }
                            }
                            if (list2.size() != 0) {
                                mShowListContacts.add(list2);//把首字母相同的添加到一起放到数组中
                            }
                        }
                    }
                    handler.sendEmptyMessage(10);
                }
            }
        }.start();



    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.page_contact_l, null);
        ButterKnife.bind(this, view);

        return view;
    }

    ContactManager contactManager;
    @Override
    public void initData() {
        if (!mIsInite) {


            if (GlobalCfg.IS_POTRAIT) {
                iv_search.setImageResource(R.mipmap.az_port);
            } else {
                iv_search.setImageResource(R.mipmap.az_land);
            }
            iv_search.setOnClickListener(this);
            mCreateContactBtn.setOnClickListener(this);
            contactManager = ContactManager.getInstance(ct);
            contactManager.getLocalContactsTwo(handler);
            mIsInite=true;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                SelectNumFragment selectNumFragment = new SelectNumFragment();
                selectNumFragment.setNumChangeListener(this);
                Bundle bundle=new Bundle();
                listSortLetters.clear();
                for (int i = 0; i< mShowListContacts.size(); i++){
                    listSortLetters.add(mShowListContacts.get(i).get(0).getSortLetters());
                }
                bundle.putSerializable("list",listSortLetters);
                selectNumFragment.setArguments(bundle);
                FragmentManager manager = ((HomeActivity) ct).getSupportFragmentManager();
                Fragment callFragment = manager.findFragmentByTag("CallFragment");
                FragmentTransaction transaction = manager.beginTransaction();
                if (callFragment != null) {
                    transaction.hide(callFragment);
                }
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).add(R.id.call_frame, selectNumFragment, "SelectNumFragment").commitAllowingStateLoss();

                break;
            case R.id.create:

                break;
        }
    }

    @Override
    public void numChange(String argString) {
        if (mShowListContacts != null && mShowListContacts.size() > 0) {
            int position = adapter.getPositionForSection(argString.charAt(0));
            if (position != -1) {
                contacts_list_view.setSelection(position);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void destory() {
        super.destory();
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
        if (mShowListContacts!=null){
            for (ArrayList<Contact> mShowListContact : mShowListContacts) {
                mShowListContact.clear();
            }
            mShowListContacts.clear();
        }
        if (contactList != null) {
            contactList.clear();
        }
        if (listSortLetters != null) {
            listSortLetters.clear();
        }
    }

    class AddMemberAdapter extends BaseAdapter implements SectionIndexer {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mShowListContacts.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return mShowListContacts.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
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
            }
            viewHolder = (ViewHolder) contentView.getTag();

            final ArrayList<Contact> cont = mShowListContacts.get(position);
            int section = getSectionForPosition(position);

            if (position == getPositionForSection(section)) {
                viewHolder.rl_catalog.setVisibility(View.VISIBLE);
                viewHolder.tv_Letter.setText(cont.get(0).getSortLetters());
            } else {
                viewHolder.rl_catalog.setVisibility(View.GONE);
            }
            if (GlobalCfg.IS_POTRAIT) {
                viewHolder.gridView.setNumColumns(2);
            } else {
                viewHolder.gridView.setNumColumns(3);
            }
            viewHolder.gridView.setAdapter(new GridAdapter(ct, cont));

            viewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    if (cont.get(position).getNumList().size()>1) {
                        SelectNumDialog selectNumDialog=new SelectNumDialog(ct,cont.get(position));
                        selectNumDialog.show();
                    }else if (cont.get(position).getNumber() != null) {
                        callPhone(cont.get(position).getNumber());
                    }
                }
            });
            return contentView;
        }

        public int getSectionForPosition(int position) {
            return mShowListContacts.get(position).get(0).getSortLetters().charAt(0);
        }


        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = mShowListContacts.get(i).get(0).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }

            return -1;
        }


        private String getAlpha(String str) {
            String sortStr = str.trim().substring(0, 1).toUpperCase();
            // ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
            if (sortStr.matches("[A-Z]")) {
                return sortStr;
            } else {
                return "#";
            }
        }

        @Override
        public Object[] getSections() {
            return null;
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
        public View getView(int position, View argconvertView, ViewGroup parent) {
            ViewHoldertwo viewHolder = null;
            if (argconvertView == null) {
                viewHolder = new ViewHoldertwo();
                argconvertView = LayoutInflater.from(ct).inflate(R.layout.myfamily_member_gv_item, null);
                viewHolder.tv_teltphone_name = (TextView) argconvertView.findViewById(R.id.tv_teltphone_name);
                argconvertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHoldertwo) argconvertView.getTag();
            }

            if (contacts.get(position).getName() != null) {
                viewHolder.tv_teltphone_name.setText(contacts.get(position).getName());
            } else {
                viewHolder.tv_teltphone_name.setText(contacts.get(position).getNumber());
            }
            return argconvertView;
        }

        class ViewHoldertwo {
            TextView tv_teltphone_name;
        }
    }

    private void callPhone(String str) {
//            if (android.os.Build.MODEL.contains("vivo") || android.os.Build.MODEL.contains("coolpad")) {
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + str));
//                ct.startActivity(intent);
//
//            } else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + str));
                ct.startActivity(intent);
//            }

    }

}
