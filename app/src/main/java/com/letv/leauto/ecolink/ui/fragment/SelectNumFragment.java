package com.letv.leauto.ecolink.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SelectNumFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.gridview)
    GridView mGridView;
    @Bind(R.id.iv_back)
    ImageView iv_back;
    public ArrayList<String> mArrayList;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_selectnum_p, null);
        } else {
            view = inflater.inflate(R.layout.fragment_selectnum, null);
        }
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ArrayList<String> listSortLetters = new ArrayList<>();

    @Override
    protected void initData(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        mArrayList = new ArrayList<>();
        mArrayList = (ArrayList<String>) bundle.getSerializable("list");
        MyAdapter myAdapter = new MyAdapter();
        mGridView.setAdapter(myAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mNumChangeListener.numChange(mArrayList.get(position));
                remove();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
            }
        });
    }

    private void remove() {
        FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
        Fragment callFragment = manager.findFragmentByTag("CallFragment");
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
        if (callFragment != null) {
            transaction.show(callFragment);
        }
    }

    @Override
    public void onResume() {
        Trace.Debug("why", "#####onresume");
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                if(!((HomeActivity) mContext).isPopupWindowShow) {
                    remove();
                }
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onClick(View v) {

    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrayList.get(position);
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
                argconvertView = LayoutInflater.from(mContext).inflate(R.layout.myfamily_member_gv_item, null);
                viewHolder.tv_teltphone_name = (TextView) argconvertView.findViewById(R.id.tv_teltphone_name);
                argconvertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHoldertwo) argconvertView.getTag();
            }
            viewHolder.tv_teltphone_name.setText(mArrayList.get(position));

            return argconvertView;
        }

        class ViewHoldertwo {
            TextView tv_teltphone_name;
        }
    }

    NumChangeListener mNumChangeListener;

    public void setNumChangeListener(NumChangeListener numChangeListener) {
        mNumChangeListener = numChangeListener;
    }

    public interface NumChangeListener {
        void numChange(String argString);
    }
}
