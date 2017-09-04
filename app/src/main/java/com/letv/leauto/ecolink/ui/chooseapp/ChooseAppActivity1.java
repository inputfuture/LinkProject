package com.letv.leauto.ecolink.ui.chooseapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.database.model.AppInfo;
import com.letv.leauto.ecolink.manager.ChoosedAppManager;
import com.letv.leauto.ecolink.ui.fragment.MainFragment;
import com.letv.leauto.ecolink.ui.view.StateTitleActivity;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseAppActivity1 extends StateTitleActivity implements OnItemClickListener{

    @Bind(R.id.gridview)
    GridView mGridView;
    @Bind(R.id.iv_back)
    ImageView mImageBack;
    @Bind(R.id.recycleView)
    RecyclerView mRecyclerView;
    ChooseAppAdapter mAppAdapter;
    private List<AppInfo> mAllAppInfos;
    private List<AppInfo> mChooseAppinfos;
    private ChoosedAppManager mAppManager;
    private AddAppAdapter mAddAppAdapter;
    @Bind(R.id.btn_sure)
    Button mSureButton;
    @Bind(R.id.add_empty)
    ImageView mImageAddEmpty;
    private volatile int mSavedCount;
    private List<AppInfo> mSaveAppInfos;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        if (GlobalCfg.IS_POTRAIT){
            setContentView(R.layout.activity_choose_app);

        }else {
            setContentView(R.layout.activity_choose_app);
        }


        ButterKnife.bind(this);
        if (GlobalCfg.IS_POTRAIT){
            mGridView.setNumColumns(2);
        }
        mAppManager=ChoosedAppManager.getInstance(mContext);
        mSaveAppInfos=mAppManager.getSavedApps(false);
        mSavedCount=mSaveAppInfos.size();
        mAllAppInfos=mAppManager.getShowAllAPP();
        mChooseAppinfos=new ArrayList<>();
        mAddAppAdapter=new AddAppAdapter(mContext,mChooseAppinfos);
        mImageBack.setOnClickListener(this);
        mSureButton.setOnClickListener(this);
        mAppAdapter = new ChooseAppAdapter(mAllAppInfos, mChooseAppinfos,mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAddAppAdapter);
        mGridView.setAdapter(mAppAdapter);
        mGridView.setOnItemClickListener(this);
        mImageAddEmpty.setVisibility(View.VISIBLE);
        mAddAppAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appInfo=mChooseAppinfos.get(position);
                mChooseAppinfos.remove(appInfo);
                mSavedCount--;
//                mAllAppInfos.add(appInfo);
                mAppAdapter.notifyDataSetChanged();
                mAddAppAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mChooseAppinfos.size()-1);
                if (mChooseAppinfos.size()>0){
                    mImageAddEmpty.setVisibility(View.GONE);
                }else{
                    mImageAddEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (CacheUtils.getInstance(EcoApplication.getInstance()).getBoolean(SettingCfg.SCREEN_LIGHT_OPEN, true)) {
            //常亮
            openLight();
        } else {
            //关闭常亮
            closeLight();
        }
    }
    public void openLight() {
        //常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void closeLight() {
        //关闭常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mSavedCount >= GlobalCfg.EXPENDSIZE) {
            ToastUtil.show(mContext, R.string.str_app_limit);
        } else {
            AppInfo appInfo = mAllAppInfos.get(position);
//            mAllAppInfos.remove(appInfo);
            if (!mSaveAppInfos.contains(appInfo)){
                if (mChooseAppinfos.contains(appInfo)){
                    mChooseAppinfos.remove(appInfo);
                    mSavedCount--;
                }else {
                    mChooseAppinfos.add(appInfo);
                    mSavedCount++;
                }

                mAppAdapter.notifyDataSetChanged();
                mAddAppAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mChooseAppinfos.size() - 1);
                if (mChooseAppinfos.size()>0){
                    mImageAddEmpty.setVisibility(View.GONE);
                }else{
                    mImageAddEmpty.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //homeActivity.setIsFromChooseApp(true);
                this.finish();
                break;
            case R.id.btn_sure:
                mAppManager.saveChoosedAppToDB(mChooseAppinfos);
                setResult(MainFragment.CHOOSE_APP_CODE);
                finish();
                break;

            default:
                break;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        GlobalCfg.isChooseAppState = false;
        if (mSaveAppInfos != null) {
            mSaveAppInfos.clear();
            mSaveAppInfos=null;
        }
        if (mChooseAppinfos==null){
            mChooseAppinfos.clear();
            mChooseAppinfos=null;
        }
        if (mAddAppAdapter != null) {
            mAddAppAdapter.destroy();
            mAddAppAdapter=null;
        }
        if (mAppAdapter != null) {
            mAppAdapter.destroy();
            mAppAdapter=null;
        }
        if (mAppManager!=null){
            mAppManager=null;
        }
    }




    class  AddAppAdapter extends RecyclerView.Adapter<AddAppAdapter.ViewHolder> {
        private List<AppInfo>  mAppInfos;

        private  Context mContext;
        LayoutInflater mInflater;
        public AddAppAdapter(Context context,List<AppInfo> appInfos) {
            mContext=context;
            mAppInfos=appInfos;
            mInflater=LayoutInflater.from(mContext);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mInflater.inflate(R.layout.item_add_app, null, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.imageView.setImageDrawable(mAppInfos.get(position).getAppIcon());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener!=null){
                        onItemClickListener.onItemClick(null,holder.imageView,position,position);
                    }


                }
            });
            Trace.Debug("###### onBindViewHolder"+position);
        }

        @Override
        public int getItemCount() {
            return mAppInfos.size();
        }

        public void destroy() {
            mContext=null;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;

            public ViewHolder(View v) {
                super(v);
                imageView = (ImageView) v.findViewById(R.id.image);
            }
        }

        OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
    }

}
