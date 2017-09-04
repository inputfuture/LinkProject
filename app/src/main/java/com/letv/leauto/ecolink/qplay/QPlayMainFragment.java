package com.letv.leauto.ecolink.qplay;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.PackageUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.tencent.qplayauto.device.QPlayAutoJNI;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 轻e租 on 2016/12/14.
 */

public class QPlayMainFragment extends BaseFragment implements View.OnClickListener{

    @Bind(R.id.sycn_btn)
    TextView mScanBtn;
    @Bind(R.id.warn_text)
    TextView mWarnText;
    private static final int JUMP=0x92;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QPlayAutoJNI.MESSAGE_RECEIVE_CONNECT:
                    switch (msg.arg1) {
                        case QPlayAutoJNI.CONNECT_STATE_SUCCESS:
                            Toast.makeText(mContext, "连接成功", Toast.LENGTH_SHORT).show();
                            Trace.Debug("#####连接成功");
                            mHandler.sendEmptyMessageDelayed(JUMP,1000);


                            break;

                        case QPlayAutoJNI.CONNECT_STATE_FAIL:
                            Toast.makeText(mContext, "连接失败,请重新连接", Toast.LENGTH_SHORT).show();
                            QPlayAutoJNI.Stop();
                            break;

                        case QPlayAutoJNI.CONNECT_STATE_INTERRUPT:


                            break;
                    }
                    break;
                case JUMP:
                    ActivityManager mAm = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                    //获得当前运行的task
                    List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
                    for (ActivityManager.RunningTaskInfo rti : taskList) {
                        //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                        if(rti.topActivity.getPackageName().equals(mContext.getPackageName())) {
                            mAm.moveTaskToFront(rti.id,0);
                            ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayMusicLibFragment(),QPlayMusicLibFragment.class.getSimpleName()).commitAllowingStateLoss();
                            return;
                        }
                    }
                    break;

            }



        }
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (PackageUtil.ApkIsInstall(mContext,"com.tencent.qqmusic")){
            mWarnText.setText(mContext.getString(R.string.qq_use_warn));
            mScanBtn.setVisibility(View.VISIBLE);
            mScanBtn.setOnClickListener(this);
        }else{
            mWarnText.setText(mContext.getString(R.string.qq_install_warn));
            mScanBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_qplay_main,null);
        ButterKnife.bind(this,view);
        if (EcoApplication.LeGlob.getqPlayer() != null) {
            EcoApplication.LeGlob.getqPlayer().setHandler(mHandler);
        }

        boolean first = cacheUtils.getBoolean(SettingCfg.QPLAY_FIRST_LOGIN,true);
        //如果不是第一次连接，则直接自动同步QQ音乐
        if (!first){
            connectQPlay();
        }
        if (PackageUtil.ApkIsInstall(mContext,"com.tencent.qqmusic")){
            mWarnText.setText(mContext.getString(R.string.qq_use_warn));
            mScanBtn.setVisibility(View.VISIBLE);
            mScanBtn.setOnClickListener(this);
        }else{
            mWarnText.setText(mContext.getString(R.string.qq_install_warn));
            mScanBtn.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sycn_btn:

                connectQPlay();
                break;
        }

    }

    private void connectQPlay() {
        if (PackageUtil.ApkIsInstall(mContext,"com.tencent.qqmusic")){
            if (GlobalCfg.QQ_CONNECT){
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayMusicLibFragment(),QPlayMusicLibFragment.class.getSimpleName()).commitAllowingStateLoss();
            }else {
                mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage("com.tencent.qqmusic"));
                int flag = EcoApplication.LeGlob.getqPlayer().startConnect();
                if (flag >= 0) {
                    Toast.makeText(mContext, "正在连接手机QQ音乐并请求数据，请稍等...", Toast.LENGTH_SHORT).show();
                   cacheUtils.putBoolean(SettingCfg.QPLAY_FIRST_LOGIN,false);

//                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayMusicLibFragment(),QPlayMusicLibFragment.class.getSimpleName()).commitAllowingStateLoss();
                } else {
                    QPlayAutoJNI.Stop();
                    Toast.makeText(mContext, "启动失败!请确认已安装QQ音乐,并且车机已经连接网络!", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(mContext, "请安装最新版QQ音乐", Toast.LENGTH_SHORT).show();
        }
    }
}
