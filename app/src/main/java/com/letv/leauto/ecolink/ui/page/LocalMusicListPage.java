package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/5.
 */
public class LocalMusicListPage extends BasePage implements RadioGroup.OnCheckedChangeListener{

    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    private HomeActivity act;

    public LocalMusicListPage(Context context) {
        super(context);
        act=(HomeActivity)context;
    }

    @Override
    protected View initView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.loacl_allmusic,null);
        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.btn_download_complete:


                break;
            case R.id.btn_downloading:


                break;
        }
    }



}
