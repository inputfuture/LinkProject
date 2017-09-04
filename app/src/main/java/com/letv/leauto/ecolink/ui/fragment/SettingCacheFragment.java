package com.letv.leauto.ecolink.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.view.DeleteDataDialog;
import com.letv.leauto.ecolink.utils.AppCacheConfig;
import com.letv.leauto.ecolink.utils.Trace;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/1.
 */
public class SettingCacheFragment extends BaseFragment implements View.OnClickListener, DeleteDataDialog.DeleteDataInterface {

    public static final String TAG = "SettingCacheFragment";
    @Bind(R.id.lyt_offline_map)
    RelativeLayout rl_clear;

    @Bind(R.id.tv_cache)
    TextView tv_cache;

    @Bind(R.id.lyt_setting_work)
    RelativeLayout rl_setting_level;

    @Bind(R.id.iv_back)
    ImageView iv_back;
    private String mCache;
    private DeleteDataDialog mDeletaDialog;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.settingcachefragment_p, null);
        } else {
            view = inflater.inflate(R.layout.settingcachefragment, null);
        }
        ButterKnife.bind(this, view);
        addListeners();
        return view;
    }

    public void setCacheSring(String cache) {
        mCache = cache;
    }

    private void addListeners() {
        rl_setting_level.setOnClickListener(this);
        rl_clear.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Trace.Error(TAG, "mCache: " + mCache);
        if (mCache == null) {
            try {
                mCache = AppCacheConfig.getTotalCacheSize(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                mCache = "0.0KB";
            }

            tv_cache.setText(mCache);
        } else {
            tv_cache.setText(mCache);
        }
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
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFragment()).commitAllowingStateLoss();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingFragment()).commitAllowingStateLoss();
                break;
            case R.id.lyt_offline_map:
                if (mDeletaDialog == null) {
                    mDeletaDialog = new DeleteDataDialog((HomeActivity) mContext, "cach");
                    mDeletaDialog.setInterface(this);
                    mDeletaDialog.show();
                } else {
                    mDeletaDialog.show();
                }

                break;
            case R.id.lyt_setting_work:

                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingSelectCacheFragment()).commitAllowingStateLoss();


                break;

        }
    }

    @Override
    public void delete() {
        new MyTask().execute();
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected Void doInBackground(Void... params) {
            try {
                AppCacheConfig.clearAllCache(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Void result) {
            tv_cache.setText("0.0KB");
        }

    }

}
