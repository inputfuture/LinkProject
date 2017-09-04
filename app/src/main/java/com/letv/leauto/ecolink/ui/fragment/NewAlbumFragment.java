package com.letv.leauto.ecolink.ui.fragment;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewAlbumFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.new_album)
    EditText  mNewEditText;
    @Bind(R.id.back)
    ImageButton mBackButton;
    @Bind(R.id.complete)
    Button mCompleteBtn;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_new_album, null);
        ButterKnife.bind(this,view);
        mBackButton.setOnClickListener(this);
        mCompleteBtn.setOnClickListener(this);
        mNewEditText.requestFocus();
        DeviceUtils.popKeyBoard(mContext);
        mNewEditText.setSelection(0);

        mCompleteBtn.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutListener);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mNewEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    DeviceUtils.dropKeyBoard(mContext, mNewEditText);
                }
                return false;
            }
        });

        mNewEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LetvReportUtils.reportMapSearchEvent();
                mNewEditText.setCursorVisible(true);
            }
        });

    }
    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                    Fragment leRadioAlumFragment = manager.findFragmentByTag("LocalMusicFragment");
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                    if (leRadioAlumFragment != null) {
                        transaction.show(leRadioAlumFragment);
                    }
                    DeviceUtils.dropKeyBoard(mContext, mNewEditText);
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:{
                FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                Fragment leRadioAlumFragment = manager.findFragmentByTag("LocalMusicFragment");
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                if (leRadioAlumFragment != null) {
                    transaction.show(leRadioAlumFragment);
                }
                DeviceUtils.dropKeyBoard(mContext, mNewEditText);
            }
            break;
            case R.id.complete:{
                if (!TextUtils.isEmpty(mNewEditText.getText().toString())){
                    LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
                    leAlbumInfo.NAME=mNewEditText.getText().toString();
                    leAlbumInfo.ALBUM_ID=mNewEditText.getText().toString();
                    leAlbumInfo.TYPE= SortType.SORT_LOCAL_NEW;
                    if (MediaOperation.getInstance().hasSavedAlbum(SortType.SORT_LOCAL_NEW,leAlbumInfo)){
                        ToastUtil.show(mContext,"你已经存在此歌单");
                        return;
                    }else{
                        MediaOperation.getInstance().insertAlbumInfo(SortType.SORT_LOCAL_NEW,leAlbumInfo);
                    }
                }
                DeviceUtils.dropKeyBoard(mContext, mNewEditText);
                FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                Fragment leRadioAlumFragment = manager.findFragmentByTag("LocalMusicFragment");
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                if (leRadioAlumFragment != null) {
                    transaction.show(leRadioAlumFragment);
                }
            }
            break;

        }

    }

    //添加根部view监听
    ViewTreeObserver.OnGlobalLayoutListener  mLayoutListener=new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            Rect r=new Rect();
            getRootView().getWindowVisibleDisplayFrame(r);
            int screenHeight=getRootView().getRootView().getHeight();
            int heightDiff = screenHeight - (r.bottom - r.top);
            if(heightDiff<-200){
                return;
            }
            if (heightDiff > 300) {
                //通知车机隐藏键盘
                DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.CONTROL_LIGHT_AVN_PARAM, ThinCarDefine.ProtocolToCarAction.HIDE_BOTTOM_BAR, 0);
            } else {
                //通知车机显示键盘
                DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.CONTROL_LIGHT_AVN_PARAM, ThinCarDefine.ProtocolToCarAction.SHOW_BOTTOM_BAR, 0);
            }
        }
    };
}
