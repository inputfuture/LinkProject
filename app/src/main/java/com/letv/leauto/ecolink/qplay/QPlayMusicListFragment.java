package com.letv.leauto.ecolink.qplay;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.library.PullToRefreshBase;
import com.letv.leauto.ecolink.library.PullToRefreshListView;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.Trace;
import com.tencent.qplayauto.device.QPlayAutoArguments;
import com.tencent.qplayauto.device.QPlayAutoJNI;
import com.tencent.qplayauto.device.QPlayAutoSongListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.tencent.qplayauto.device.QPlayAutoJNI.SONG_ITEM_TYPE_LIST;
import static com.tencent.qplayauto.device.QPlayAutoJNI.SONG_ITEM_TYPE_RADIO;

/**
 * Created by 轻e租 on 2016/12/14.
 */

public class QPlayMusicListFragment extends BaseFragment implements View.OnClickListener,PullToRefreshBase.OnRefreshListener2<ListView>{

    @Bind(R.id.app_back)
    ImageView mAppBackView;
    @Bind(R.id.music_playing_stop)
    ImageView mMusicPlayView;
    @Bind(R.id.song_list)
    PullToRefreshListView mListView;
    @Bind(R.id.folder_name)
    TextView sourceName;
    @Bind(R.id.unLogin_tv)
    TextView unLoginTv;
    //断开连接时的对话框
    private Dialog mDisConnectDialog;
    //记录用户查询的路径
    private Stack<String> mSongStack = new Stack<String>();
    //ListView的数据源，用于记录当前歌单的列表
    private ArrayList<QPlayAutoSongListItem> mSongItems = new ArrayList();
    private QPlayMusicListAdapter mAdapter;

    //当前请求数据的页数
    private int mPageIndex;
    //当前请求数据的总页数
    private int mPageTotal;

    //该页面当前歌单item项以及父ID
    private QPlayAutoSongListItem mListItem;
    private String mSongId;
    //标题名字
    private String mSongname;
    private static final int COMPLETE=0X91;
    private QPlayer qPlayer;
    @Bind(R.id.empty_mess)
    TextView mEmptyView;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Trace.Debug("####hand what=" + msg.what + "  arg1=" + msg.arg1);
            switch (msg.what) {
                case QPlayAutoJNI.MESSAGE_RECEIVE_CONNECT:
                    switch (msg.arg1) {
                        case QPlayAutoJNI.CONNECT_STATE_SUCCESS:
                            Toast.makeText(mContext, "连接成功", Toast.LENGTH_SHORT).show();
                            Trace.Debug("###### 连接成功");
                            ActivityManager mAm = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                            //获得当前运行的task
                            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
                            for (ActivityManager.RunningTaskInfo rti : taskList) {
                                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                                if(rti.topActivity.getPackageName().equals(mContext.getPackageName())) {
                                    mAm.moveTaskToFront(rti.id,0);
                                    return;
                                }
                            }
                            if (mDisConnectDialog!=null){
                                if (mDisConnectDialog.isShowing()){
                                    mDisConnectDialog.dismiss();
                                    mDisConnectDialog=null;}
                            }

                            break;

                        case QPlayAutoJNI.CONNECT_STATE_FAIL:
                            Toast.makeText(mContext, "连接失败,请重新连接", Toast.LENGTH_SHORT).show();
                            QPlayAutoJNI.Stop();
                            showConnectDialog();
                            break;

                        case QPlayAutoJNI.CONNECT_STATE_INTERRUPT:
                            Toast.makeText(mContext, "连接断开,请重新连接", Toast.LENGTH_SHORT).show();
                            QPlayAutoJNI.Stop();
                            showConnectDialog();
                            break;
                    }
                    break;
                case QPlayAutoJNI.MESSAGE_RECEIVE_COMM: {
                    switch (msg.arg1) {
                        case QPlayAutoArguments.RESPONSE_PLAY_LIST:
                            QPlayAutoArguments.ResponsePlayList playList = (QPlayAutoArguments.ResponsePlayList) msg.obj;
                            QPlayAutoSongListItem[] songItems = playList.playList;
                            mPageIndex = playList.pageIndex;
                            mPageTotal = playList.count / QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT + (playList.count % QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT == 0 ? 0 : 1);
                            mSongId = playList.parentID;

                            for (int i = 0; i < songItems.length; i++) {
                                if (songItems[i].Type==SONG_ITEM_TYPE_RADIO||songItems[i].Type==SONG_ITEM_TYPE_LIST){
                                    mPageIndex=0;
                                    QPlayAutoJNI.RequestPlayList(songItems[i].ID, mPageIndex, QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);
                                    return;

                                }
                                mSongItems.add(songItems[i]);

                            }
                            if (mSongItems.size()<=0){
                                mEmptyView.setText("当前专辑没有曲目");
                                mEmptyView.setVisibility(View.VISIBLE);

                            }else {
                                mEmptyView.setVisibility(View.GONE);
                            }
                            mAdapter.notifyDataSetChanged();
                            mListView.onRefreshComplete();
                            break;
                        case QPlayAutoArguments.REQUEST_DISCONNECT:
                            break;
                        case QPlayAutoArguments.RESPONSE_ERROR:
                        case QPlayAutoArguments.REQUEST_ERROR: {
                            QPlayAutoArguments.CommandError error = (QPlayAutoArguments.CommandError) msg.obj;
                            if (error.ErrorNo == QPlayAutoJNI.ERROR_PROTOCOL_NOT_LOGIN) {
                                Toast.makeText(mContext, "QQ音乐没有登录，请到QQ音乐登录!", Toast.LENGTH_LONG).show();
                                unLoginTv.setVisibility(View.VISIBLE);
                            } else{
//                                Toast.makeText(mContext, "出现错误:" + error.ErrorNo, Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                    }
                }
                break;
                case COMPLETE:
                    mListView.onRefreshComplete();
                    break;
            }

        }
    };

    /**
     * 显示重连对话框
     */
    private void showConnectDialog() {
        if (mDisConnectDialog==null){
            //断开连接时弹出重连对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            View view = View.inflate(mContext, R.layout.disconnect_dialog_layout,null);
            TextView affirmTv = (TextView) view.findViewById(R.id.affirm);
            affirmTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int flag = EcoApplication.LeGlob.getqPlayer().startConnect();
                    if (flag>=0){
                        //重连成功
                        mDisConnectDialog.dismiss();
                        QPlayAutoJNI.RequestPlayList(QPlayAutoJNI.SONG_LIST_ROOT_ID,0, QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);
                    }else {
                        QPlayAutoJNI.Stop();
                        Toast.makeText(mContext, "重连失败!请确认已安装QQ音乐,并且车机已经连接网络!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setView(view).setCancelable(false);
            mDisConnectDialog = builder.create();
            mDisConnectDialog.show();
        }else {
            if (!mDisConnectDialog.isShowing()){
                mDisConnectDialog.show();
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (!GlobalCfg.QQ_CONNECT){
            if (mDisConnectDialog==null) {
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayerFragment(), QPlayerFragment.class.getSimpleName()).commitAllowingStateLoss();
            }
        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_qplay_music_list,null);
        ButterKnife.bind(this,view);
        qPlayer=EcoApplication.LeGlob.getqPlayer();
        mListItem = QPlayHelper.getInstance(getContext()).getSongItem();
        String name  =QPlayHelper.getInstance(getContext()).getSourceStr();
        String id =null;
        if (mListItem !=null){
            id = mListItem.ID;
        }
        if (name!=null){
            this.mSongname = name;
        }
        if (id!=null){
            this.mSongId = id;
        }
        unLoginTv.setVisibility(View.GONE);
        EcoApplication.LeGlob.getqPlayer().setHandler(mHandler);
        if (mSongname !=null){
            sourceName.setText(mSongname);
        }
        if (mSongId !=null){
            mSongItems.clear();
            QPlayAutoJNI.RequestPlayList(mSongId,mPageIndex, QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);
        }

        mAppBackView.setOnClickListener(this);
        mMusicPlayView.setOnClickListener(this);
        if (GlobalCfg.IS_POTRAIT){
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.leftMargin= (int) mContext.getResources().getDimension(R.dimen.size_10dp);
            mListView.setLayoutParams(params);
        }else{
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.leftMargin= (int) mContext.getResources().getDimension(R.dimen.size_60dp);
            params.rightMargin= (int) mContext.getResources().getDimension(R.dimen.size_60dp);
            mListView.setLayoutParams(params);
        }

        //根据屏幕不同方向，为Listview设置适配器
        mAdapter = new QPlayMusicListAdapter(mContext, mSongItems);
        mListView.setAdapter(mAdapter);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mListView.setOnRefreshListener(this);
        //为Listview设置监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position >= 1 && position - 1 < mSongItems.size()) {
                    QPlayAutoSongListItem songItem = mSongItems.get(position-1);
                    int songType = songItem.Type;
                    switch (songType) {
                        //如果是歌曲，则跳转到音乐播放页面
                        case QPlayAutoJNI.SONG_ITEM_TYPE_SONG:
                            QPlayHelper.getInstance(getContext()).setPlayIndex(position-1);
                            QPlayHelper.getInstance(getContext()).setCurrSong(songItem);
                            QPlayHelper.getInstance(getContext()).setRequstID(mSongId);
                            qPlayer.setPlayList(mSongItems);
                            qPlayer.playIndex(position-1);
                            ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayerFragment(),QPlayerFragment.class.getSimpleName()).commitAllowingStateLoss();
                            break;

                        //如果是电台目录,直接走 case2 的逻辑
                        case SONG_ITEM_TYPE_RADIO:

                            //如果是歌曲目录，并且含有子目录，则获取下一级列表，更新Listview数据源，进行展示
                        case SONG_ITEM_TYPE_LIST:
                            mSongItems.clear();
                            mPageIndex=0;
                            QPlayHelper.getInstance(getContext()).setSongItem(songItem);
                            QPlayAutoJNI.RequestPlayList(songItem.ID, mPageIndex, QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);
                            if (mSongStack.size() == 0) {
                                mSongStack.push(mSongId);
                            }
                            mSongId = songItem.ID;
                            break;

                        default:
                            break;
                    }
                }
            }
        });

        //初始化刷新布局



        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    /**
     * 该页面的部分点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        switch (viewID){
            //返回上一层页面，如果已经是最上层，则返回音乐库，关闭该页面；
            case R.id.app_back:
                Toast.makeText(mContext, "正在返回", Toast.LENGTH_SHORT).show();
                mSongItems.clear();

                if (mSongStack.size()>0){
                    mSongId = mSongStack.pop();
                    if (mSongId!=null){
                        mPageIndex=0;
                        QPlayAutoJNI.RequestPlayList(mSongId,0,15);
                    }else{
                        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayMusicLibFragment(),QPlayMusicLibFragment.class.getSimpleName()).commitAllowingStateLoss();
                    }
                }else {
                    //发消息给Activity，切换Fragment到QQ音乐库页面
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayMusicLibFragment(),QPlayMusicLibFragment.class.getSimpleName()).commitAllowingStateLoss();
                }

                break;

            //跳转到音乐播放页面
            case R.id.music_playing_stop:
                if (QPlayHelper.getInstance(getContext()).getPlayIndex()==-1){
                    Toast.makeText(mContext, "请选择需要播放的音乐~~~", Toast.LENGTH_SHORT).show();
                }else {
                    //发消息给Activity，切换Fragment到播放页面
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayerFragment(),QPlayerFragment.class.getSimpleName()).commitAllowingStateLoss();
                }
                break;

            default:
                break;

        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mPageIndex +1< mPageTotal) {
            //请求下一页
            QPlayAutoJNI.RequestPlayList(mSongId, mPageIndex +1, QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);
        } else{
            Toast.makeText(mContext, "没有更多数据可以加载", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(COMPLETE);

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
//        if (mSongItems!=null){
//            mSongItems.clear();
//        }
        if (mSongStack!=null){
            mSongStack.clear();
        }
    }
}
