package com.letv.leauto.ecolink.qplay;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.Trace;
import com.tencent.qplayauto.device.QPlayAutoArguments;
import com.tencent.qplayauto.device.QPlayAutoDeviceInfos;
import com.tencent.qplayauto.device.QPlayAutoJNI;
import com.tencent.qplayauto.device.QPlayAutoSongListItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by 轻e租 on 2016/12/14.
 */

public class QPlayMusicLibFragment extends BaseFragment {


    @Bind(R.id.music_playing_stop)
    ImageView mPlayBtn;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private List<QPlayAutoSongListItem> mSongListItems = new ArrayList<>();
    private RootRecyclerViewAdapter adapter;

    //断开连接时的对话框
    private Dialog mDisconnectDialog;

    private Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            Trace.Debug("#####qplay handler message what="+msg.what);


            //接收到连接信息,msg.what = 4;
            if (msg.what== QPlayAutoJNI.MESSAGE_RECEIVE_CONNECT){
                switch (msg.arg1){
                    case QPlayAutoJNI.CONNECT_STATE_SUCCESS:
                        Toast.makeText(mContext, "连接成功", Toast.LENGTH_SHORT).show();
                        if (mDisconnectDialog!=null){
                            if (mDisconnectDialog.isShowing()){
                                mDisconnectDialog.dismiss();
                            }
                            mDisconnectDialog=null;
                        }


                        break;

                    case QPlayAutoJNI.CONNECT_STATE_FAIL:
                        Toast.makeText(mContext, "连接失败,请重新连接", Toast.LENGTH_SHORT).show();
                        QPlayAutoJNI.Stop();

                        ShowConnectDialog();
                        break;

                    case  QPlayAutoJNI.CONNECT_STATE_INTERRUPT:
                        Toast.makeText(mContext, "连接断开,请重新连接", Toast.LENGTH_SHORT).show();
                        QPlayAutoJNI.Stop();

                        ShowConnectDialog();
                        break;
                }
            }

            //接收到命令,msg.what == 1;
            else if ( msg.what == QPlayAutoJNI.MESSAGE_RECEIVE_COMM ){
                //接收到请求设备信息的命令
                if (msg.arg1 == QPlayAutoArguments.REQUEST_DEVICE_INFOS){
                    QPlayAutoDeviceInfos DeviceInfos = new QPlayAutoDeviceInfos();
                    DeviceInfos.AppVer = getAppVersion();
                    DeviceInfos.Network = 1;
                    DeviceInfos.Brand = "车机品牌";
                    DeviceInfos.Models = "车机型号";
                    DeviceInfos.OS = "Android";
                    DeviceInfos.OSVer = android.os.Build.VERSION.RELEASE;
                    DeviceInfos.Ver = "1.3";
                    DeviceInfos.LRCBufSize = 200*1024;
                    DeviceInfos.PCMBufSize = 1024*1024;
                    DeviceInfos.PICBufSize = 500*1024;

                    QPlayAutoJNI.ResponseDeviceInfos(msg.arg2,DeviceInfos);

                    Trace.Debug( "######RegisterPlayState:====" + QPlayAutoJNI.RequestRegisterPlayState(0));
                    QPlayAutoJNI.RequestPlayList(QPlayAutoJNI.SONG_LIST_ROOT_ID,0, QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);
                }
                //接收到请求歌单的响应信息
                else  if (msg.arg1 == QPlayAutoArguments.RESPONSE_PLAY_LIST){
                    Toast.makeText(mContext, "请求数据成功", Toast.LENGTH_SHORT).show();
                    QPlayAutoArguments.ResponsePlayList playList = (QPlayAutoArguments.ResponsePlayList) msg.obj;
                    QPlayAutoSongListItem[] songItems = playList.playList;
                    Trace.Debug("##### 接收到歌单信息，数据量："+songItems.length);
                    for (int i = 0; i <songItems.length; i++) {
                        if (songItems[i].Name.equals("我的歌单")){
                            songItems[i].Name="我喜欢";
                            songItems[i].ParentID=null;
                            songItems[i].Type=2;
                            songItems[i].ID="MY_FOLDER:{\"KEY_ID\":201,\"KEY_TYPE\":101,\"KEY_NAME\":\"我喜欢\"}";
                            mSongListItems.add(songItems[i]);


                        }else if (songItems[i].Name.equals("本地歌曲")){
                            songItems[i].Name="本地下载";
                            mSongListItems.add(songItems[i]);

                        }else if (songItems[i].Name.equals("最近播放")||songItems[i].Name.equals("在线电台")
                                ||songItems[i].Name.equals("在线歌单")||songItems[i].Name.equals("分类")){

                        }else if (songItems[i].Name.equals("排行榜")){
                            QPlayAutoJNI.RequestPlayList(songItems[i].ID, 0, 20);

                        }else{
//                            if (songItems[i].Name.startsWith("巅峰榜·")){
//                                String name=songItems[i].Name;
//                                songItems[i].Name=name.substring(name.lastIndexOf("·")+1,name.length());
//                            }
                            mSongListItems.add(songItems[i]);

                        }


                    }
                    adapter.notifyDataSetChanged();
                }
            }

        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();
        if (!GlobalCfg.QQ_CONNECT){
            if (mDisconnectDialog==null) {
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayerFragment(), QPlayerFragment.class.getSimpleName()).commitAllowingStateLoss();
            }
        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view= inflater.inflate(R.layout.fragment_qplay_music_lib,null);
        ButterKnife.bind(this,view);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int playIndex = QPlayHelper.getInstance(getActivity()).getPlayIndex();
                if (playIndex==-1){
                    Toast.makeText(mContext, "请选择需要播放的音乐~~~", Toast.LENGTH_SHORT).show();
                }else {
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayerFragment(),QPlayerFragment.class.getSimpleName()).commitAllowingStateLoss();
                }
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        //根据屏幕的不同方向来设置recyclerView的布局方式
        if (GlobalCfg.IS_POTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
        }else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        }


        adapter = new RootRecyclerViewAdapter(mContext,mSongListItems);
        //为Recyclerview的适配器设置点击事件
        adapter.setOnRecyclerViewListener(new RootRecyclerViewAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {

                if (mSongListItems !=null&& mSongListItems.size()!=0){
                    QPlayHelper.getInstance(getActivity()).setSongItem(mSongListItems.get(position));
                    QPlayHelper.getInstance(getActivity()).setSourceStr(mSongListItems.get(position).Name);
                    Trace.Debug( "######onItemClick"+ mSongListItems.get(position).Name+",歌曲名："+ mSongListItems.get(position).Name);
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayMusicListFragment()).commitAllowingStateLoss();
                }
            }

            //暂时不响应长按事件
            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        mRecyclerView.setAdapter(adapter);
        EcoApplication.LeGlob.getqPlayer().setHandler(mHandler);//设置消息处理类
        QPlayAutoJNI.RequestPlayList(QPlayAutoJNI.SONG_LIST_ROOT_ID,0, QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);

        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

   /* @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            QPlayAutoJNI.SetHandler(mHandler);//设置消息处理类
            QPlayAutoJNI.RequestPlayList(QPlayAutoJNI.SONG_LIST_ROOT_ID,0,QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);
        }
        super.onHiddenChanged(hidden);
    }*/


    /**
     * 显示重连对话框
     */
    private void ShowConnectDialog() {
        if (mDisconnectDialog ==null){
        //断开连接时弹出重连对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.disconnect_dialog_layout,null);
        TextView affirmTv = (TextView) view.findViewById(R.id.affirm);
        affirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flag = QPlayAutoJNI.Start(QPlayAutoJNI.DEVICE_TYPE_AUTO, QPlayAutoJNI.CONNECT_TYPE_WIFI, "BMV", "X5");
                if (flag>=0){
                    //重连成功
                    mDisconnectDialog.dismiss();
                    QPlayAutoJNI.RequestPlayList(QPlayAutoJNI.SONG_LIST_ROOT_ID,0, QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);
                }else {
                    QPlayAutoJNI.Stop();
                    Toast.makeText(mContext, "重连失败!请确认已安装QQ音乐,并且车机已经连接网络!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view).setCancelable(false);
        mDisconnectDialog = builder.create();
        mDisconnectDialog.show();
        }else {
            if (!mDisconnectDialog.isShowing()){
                mDisconnectDialog.show();
            }

        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler !=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler =null;
        }
        if (mSongListItems != null) {
            mSongListItems.clear();
        }
    }

    /**
     * 获取app的版本
     * @return
     */
    private String getAppVersion()
    {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        }
        catch (Exception err)
        {
            return "";
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("switchover","switchover");
    }
}
