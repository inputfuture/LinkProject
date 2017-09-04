package com.letv.leauto.ecolink.qplay;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.receiver.BluetoothReceiver;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.view.EcoSeekBar;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.TimeUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.tencent.qplayauto.device.QPlayAutoJNI;
import com.tencent.qplayauto.device.QPlayAutoSongListItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 轻e租 on 2016/12/14.
 */

public class QPlayerFragment extends BaseFragment implements View.OnClickListener,QPlayer.MusicStateListener {

    private static final String TAG = "QPlayerFragment";

    public final static int START=0x31;
    public final static int STOP=0x32;
    private static final int UPDATE = 0X33;
    @Bind(R.id.album_picture)
    ImageView albumImg;
    @Bind(R.id.play_pause)
    ImageView mPlayPauseView;
    @Bind(R.id.play_mode)
    ImageView mPlayModeView;
    @Bind(R.id.play_pre)
    ImageView mPlayPreView;
    @Bind(R.id.play_next)
    ImageView mPlayNextView;
    @Bind(R.id.app_back)
    ImageView mBackView;
    @Bind(R.id.song_name)
    TextView mSongNameView;
    @Bind(R.id.artist)
    TextView mArtistView;
    @Bind(R.id.album)
    TextView mAlbumNameView;
    @Bind(R.id.curr_time)
    TextView currTime;
    @Bind(R.id.total_time)
    TextView totalTime;
    @Bind(R.id.seek_bar)
    EcoSeekBar mSeekBar;
    private SharedPreferences sp;
    //当前播放歌曲的名字，艺术家，专辑
    private String currSongName;
    private String currSongArtist;
    private String currSongAlbum;

    private QPlayAutoSongListItem mCurSongItem;
    //播放PCM音频的类对象
    private QPlayer mPlayer = null;
    //记录当前歌单的列表
    private ArrayList<QPlayAutoSongListItem> mSongItems = new ArrayList<>();
    //断开连接对话框
    private Dialog mDisconnectDialog;
    private int TIME = 1000;

    private int mIndex;


    //记录当前播放的是歌曲还是电台
    private volatile int PlayType = 0;

    //处理接收到的移动设备消息
    private Handler
            mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Trace.Debug("#######处理消息 msg what=" + msg.what);
            switch (msg.what) {
                //命令消息
                case QPlayAutoJNI.MESSAGE_RECEIVE_COMM:
//                    parseMobileCommand(msg.arg1, msg.arg2, msg.obj);
                    break;

                //二进制消息,这里只处理专辑图消息
                case QPlayAutoJNI.MESSAGE_RECEIVE_DATA:
                    Trace.Error(TAG, "handleMessage:====接收到图片信息");
                    if (msg.arg1 == QPlayAutoJNI.BIN_DATA_TYPE_PIC)//专辑图数据
                    {
                        Bitmap bmp = BytesToBitmap((byte[]) msg.obj);
                        if (bmp != null) {
                            albumImg.setImageBitmap(bmp);//显示歌曲专辑图片
                        }
                    }
                    break;

                //与移动设备的连接消息
                case QPlayAutoJNI.MESSAGE_RECEIVE_CONNECT:
                    if (msg.arg1 == QPlayAutoJNI.CONNECT_STATE_SUCCESS) {
                        //与手机QQ音乐连接成功，正在获取移动设备信息...
                        QPlayAutoJNI.RequestMobileDeviceInfos();
                        mPlayer.releasePlay();
                        mPlayer.playIndex(mIndex);
                        if (mDisconnectDialog!=null){
                            if (mDisconnectDialog.isShowing()){
                        mDisconnectDialog.dismiss();
                            mDisconnectDialog=null;}
                        }

                    } else if (msg.arg1 == QPlayAutoJNI.CONNECT_STATE_FAIL) {
                        Toast.makeText(mContext, "连接失败,请重新连接", Toast.LENGTH_SHORT).show();
                        ShowConnectDialog();
                    } else if (msg.arg1 == QPlayAutoJNI.CONNECT_STATE_INTERRUPT) {
                        Toast.makeText(mContext, "连接断开,请重新连接", Toast.LENGTH_SHORT).show();
                        ShowConnectDialog();
                    }
                    break;



                case QPlayAutoJNI.MESSAGE_RECEIVE_PLAY_BUFF:
                    Trace.Debug("###### 正在缓冲数据 ");
                    QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, "正在缓冲数据...");
                    break;

                case QPlayAutoJNI.MESSAGE_RECEIVE_ERROR:
                    QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, "出现错误:" + msg.obj.toString());
                    break;
                case START:
                    mPlayPauseView.setImageResource(R.mipmap.music_play);

                    break;
                case STOP:

                    mPlayPauseView.setImageResource(R.mipmap.music_pause);
//                    if (mPlayer.isPlay()){
//                        mPlayPauseView.setImageResource(R.mipmap.music_play);
//                        mPlayer.requestAudioFocus();
//                    }
                    break;
                case UPDATE:
                    if (mPlayer != null) {
                        int total = mPlayer.getTotalTimes();
                        int curr = mPlayer.getPlayPosition();
                        mSeekBar.setMax(total);
                        mSeekBar.setProgress(curr);
                        totalTime.setText(TimeUtils.secToTime(total+""));
                        currTime.setText(TimeUtils.secToTime(curr+""));
                    }
                    mHandler.sendEmptyMessageDelayed(UPDATE,TIME);
                    break;

            }
        }
    };


    /**
     * 将字节数组转为Bitmap对象
     *
     * @param b
     * @return
     */
    public static Bitmap BytesToBitmap(byte[] b) {
        if (b.length != 0)
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        else
            return null;
    }

    /**
     * 显示重连对话框
     */
    private void ShowConnectDialog() {
        if (mDisconnectDialog==null){
            //断开连接时弹出重连对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            View view = View.inflate(mContext, R.layout.disconnect_dialog_layout, null);
            TextView affirmTv = (TextView) view.findViewById(R.id.affirm);
            affirmTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int flag = EcoApplication.LeGlob.getqPlayer().startConnect();
                    if (flag >= 0) {
                        //重连成功
                        mDisconnectDialog.dismiss();
                        QPlayAutoJNI.RequestPlayList(QPlayAutoJNI.SONG_LIST_ROOT_ID, 0, QPlayAutoJNI.PLAY_LIST_REQUEST_PRE_COUNT);
                    } else {
                        QPlayAutoJNI.Stop();
                        Toast.makeText(mContext, "重连失败!请确认已安装QQ音乐,并且车机已经连接网络!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setView(view).setCancelable(false);
            mDisconnectDialog = builder.create();
            mDisconnectDialog.show();
        }else{
            if (!mDisconnectDialog.isShowing()){
                mDisconnectDialog.show();
            }
        }
    }




    /**
     * 初始化控件
     */
    private void initView() {
        mPlayPauseView.setOnClickListener(this);
        mPlayModeView.setOnClickListener(this);
        mPlayPreView.setOnClickListener(this);
        mPlayNextView.setOnClickListener(this);
        mBackView.setOnClickListener(this);
        mSeekBar.setCanSeek(false);
    }

    /**
     * 音乐播放初始化
     */
    private void initMusic() {

        mPlayer.setHandler(mHandler);
        mIndex=mPlayer.getIndex();
        mSongItems.clear();
        mSongItems.addAll(mPlayer.getPlayList());
        mCurSongItem =mSongItems.get(mIndex);
        currSongName = mCurSongItem.Name;
        currSongArtist = mCurSongItem.Artist;
        currSongAlbum = mCurSongItem.Album;
        mPlayer.setMusicStateListener(this);
        mPlayPauseView.setImageResource(R.mipmap.music_play);
        if (currSongName != null) {
            mSongNameView.setText(currSongName);
        }
        if (currSongArtist != null) {
            mArtistView.setText(currSongArtist);
        }
        if (currSongAlbum != null) {
            mAlbumNameView.setText(currSongAlbum);
        }

        QPlayAutoJNI.GetSongPicture(mCurSongItem.ID);
        //每隔1s执行一次(读取当前播放时间)
        if (mPlayer != null) {
            int total = mPlayer.getTotalTimes();
            int curr = mPlayer.getPlayPosition();
            mSeekBar.setMax(total);
            mSeekBar.setProgress(curr);
            totalTime.setText(TimeUtils.secToTime(total+""));
            currTime.setText(TimeUtils.secToTime(curr+""));
        }
        mHandler.sendEmptyMessageDelayed(UPDATE, TIME);
    }



    /**
     * 播放/暂停
     */
    public void playPause() {

        if (mPlayer.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mPlayer.pasuseByUser();

            mPlayPauseView.setImageResource(R.mipmap.music_pause);
        } else {
            mPlayer.play();
            mPlayPauseView.setImageResource(R.mipmap.music_play);
        }
    }




    /**
     * 停止播放
     */
    public void StopPlayClick() {

        QPlayAutoJNI.StopPlay();
        mPlayer.stop();
    }

    /**
     * 初始化播放模式模块
     */
    private void initPlayMode() {
        int mode = CacheUtils.getInstance(mContext).getInt(SettingCfg.QPLAY_MODE, 1);
        setMediaModeView(mode);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!GlobalCfg.QQ_CONNECT){
            if (mDisconnectDialog==null) {
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayerFragment(), QPlayerFragment.class.getSimpleName()).commitAllowingStateLoss();
            } }
    }

/*    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            QPlayAutoJNI.SetHandler(mHandler,mPlayer.PlayHandler);
            mCurSongItem = ((HomeActivity)getActivity()).getCurrSong();
            initMusic();
        }
        super.onHiddenChanged(hidden);
    }*/

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {

            view = inflater.inflate(R.layout.fragment_qplay_player, null);
        } else {
            view = inflater.inflate(R.layout.fragment_qplay_player_land, null);
        }
        ButterKnife.bind(this, view);
        mPlayer = EcoApplication.LeGlob.getqPlayer();
        initView();
        initPlayMode();
        initMusic();

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
        if (mDisconnectDialog!=null){
            mDisconnectDialog.dismiss();
            mDisconnectDialog=null;
        }


    }

    /**
     * 该页面的点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.app_back:

                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.qplay_container, new QPlayMusicListFragment()).commitAllowingStateLoss();
                break;

            case R.id.play_pre:
//                playPre();
                mPlayer.playPre();
                break;

            case R.id.play_pause:
                playPause();
                break;

            case R.id.play_next:
//                playNext(1);
                mPlayer.playNext();

                break;

            //切换播放模式
            case R.id.play_mode:
                changeMusicPalyMode();

                break;

            default:
                break;
        }
    }

    private void changeMusicPalyMode() {
        int mode = CacheUtils.getInstance(mContext).getInt(SettingCfg.QPLAY_MODE, 1);
        switch (mode) {
            case 1:/*顺序播放*/
                mode = 2;
                break;
            case 2:/*单曲循环*/
                mode = 3;
                break;
            case 3: /*随机播放*/
                mode = 1;
                break;
            default:
                mode = 1;
                break;
        }
        setMediaModeView(mode);
        CacheUtils.getInstance(mContext).putInt(SettingCfg.QPLAY_MODE, mode);


    }


    private void setMediaModeView(int mode) {
        mPlayer.setPlayMode(mode);
        switch (mode) {
            case PlayPCM.MODE_CYCLE:
                mPlayModeView.setImageResource(R.mipmap.music_mode_order);
                break;
            case PlayPCM.MODE_CYCLE_SINGLE:
                mPlayModeView.setImageResource(R.mipmap.music_mode_loops);
                break;
            case PlayPCM.MODE_RANDOM:
                mPlayModeView.setImageResource(R.mipmap.music_mode_random);
                break;
            default:
                mPlayModeView.setImageResource(R.mipmap.music_mode_order);
                break;

        }
    }

    @Override
    public void musicStart() {
        if (mHandler!=null){
            mHandler.sendEmptyMessage(START);}

    }

    @Override
    public void musicStop() {
        Trace.Debug("*****stop");
        if (mHandler!=null){
            mHandler.sendEmptyMessage(STOP);
        }






    }

    @Override
    public void musicIndex(int index) {
        mIndex=index;
        mCurSongItem =mSongItems.get(mIndex);
        currSongName = mCurSongItem.Name;
        currSongArtist = mCurSongItem.Artist;
        currSongAlbum = mCurSongItem.Album;
        if (currSongName != null) {
            mSongNameView.setText(currSongName);
        }
        if (currSongArtist != null) {
            mArtistView.setText(currSongArtist);
        }
        if (currSongAlbum != null) {
            mAlbumNameView.setText(currSongAlbum);
        }
        QPlayAutoJNI.GetSongPicture(mCurSongItem.ID);

    }
}
