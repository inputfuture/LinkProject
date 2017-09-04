package com.letv.leauto.ecolink.qplay;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.utils.Trace;
import com.tencent.qplayauto.device.QPlayAutoArguments;
import com.tencent.qplayauto.device.QPlayAutoArguments.ResponseMediaInfos;
import com.tencent.qplayauto.device.QPlayAutoJNI;
import com.tencent.qplayauto.device.QPlayAutoSongListItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayPCM extends Service {

    //三种播放的模式，分别为循环、单循环、随机；
    public final static int MODE_CYCLE = 1;
    public final static int MODE_CYCLE_SINGLE = 2;
    public final static int MODE_RANDOM = 3;
    private static final String TAG = "PlayPCM";


    private volatile boolean stopPlayFlag = true;//是否停止播放
    private Handler mUiHandler = null;//发送信息给UI处理
    public static boolean PlayExitFlag = false;//播放线程退出标志
    private Thread mPlayThread = null;//播放线程

    private AudioTrack mAudioTrack;//播放器

    private volatile ResponseMediaInfos mCurrentPlaySongInfo;//当前播放信息

    public Queue<String> mPlayQueneId = new LinkedList<String>();//播放队列，记录歌曲id

    public static boolean stopByUser;
    private final LocalBinder binder = new LocalBinder();
    private ArrayList<QPlayAutoSongListItem> playList;
    private int mIndex;
    private int mPlayMode;
    private IndexChangeListener mIndexChangeListener;


    //处理接收到的移动设备消息
    private Handler deviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Trace.Debug(TAG,"#######处理消息 msg what=" + msg.what);
            Message message = mUiHandler.obtainMessage();
            message.what = msg.what;
            message.arg1 = msg.arg1;
            message.obj = msg.obj;
            mUiHandler.sendMessage(message);
            switch (msg.what) {
                //命令消息
                case QPlayAutoJNI.MESSAGE_RECEIVE_COMM:
                    parseMobileCommand(msg.arg1, msg.arg2, msg.obj);
                    break;

                //二进制消息,这里只处理专辑图消息
                case QPlayAutoJNI.MESSAGE_RECEIVE_DATA:
                    Trace.Debug(TAG, "handleMessage:====接收到图片信息");
                    if (msg.arg1 == QPlayAutoJNI.BIN_DATA_TYPE_PIC)//专辑图数据
                    {
//                        Bitmap bmp = BytesToBitmap((byte[]) msg.obj);
//                        if (bmp != null) {
//                            albumImg.setImageBitmap(bmp);//显示歌曲专辑图片
//                        }
                    }
                    break;

                //与移动设备的连接消息
                case QPlayAutoJNI.MESSAGE_RECEIVE_CONNECT:
                    if (msg.arg1 == QPlayAutoJNI.CONNECT_STATE_SUCCESS) {
                        //与手机QQ音乐连接成功，正在获取移动设备信息...
                        QPlayAutoJNI.RequestMobileDeviceInfos();
                        GlobalCfg.QQ_CONNECT = true;
                    } else if (msg.arg1 == QPlayAutoJNI.CONNECT_STATE_FAIL) {
                        QPlayAutoJNI.Stop();
                        GlobalCfg.QQ_CONNECT = false;
                    } else if (msg.arg1 == QPlayAutoJNI.CONNECT_STATE_INTERRUPT) {
                        QPlayAutoJNI.Stop();
                        GlobalCfg.QQ_CONNECT = false;
                        QPlayAutoJNI.Start(QPlayAutoJNI.DEVICE_TYPE_AUTO, QPlayAutoJNI.CONNECT_TYPE_WIFI, "BMV", "X5");

                    }
                    break;

                //播放PCM数据返回信息
                case QPlayAutoJNI.MESSAGE_RECEIVE_PLAY_FINISH:
                    if (msg.arg1 == 0)//播放失败
                    {
                        if (msg.arg2 == 0)//获取PCM数据错误
                        {
                            QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, "无法播放，读取PCM数据错误!");
                            break;
                        } else if (msg.arg2 == 1)//获取歌曲信息错误
                        {

                        } else if (msg.arg2 == 2)//用户主动调用Stop退出播放
                        {
                            break;
                        } else if (msg.arg2 == 105)//歌曲ID不存在
                        {
                            QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, "无法播放，歌曲不存在!");
                            break;
                        } else if (msg.arg2 == 106)//读取数据错误
                        {
                            QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, "无法播放，读取数据错误!");
                            break;
                        } else if (msg.arg2 == 109)//无法播放，没有版权!
                        {
                            QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, "无法播放，没有版权!");
                            break;
                        } else if (msg.arg2 == 110)//QQ音乐没有登录，请先登陆!
                        {
                            QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, "QQ音乐没有登录，请先登陆!");
                            break;
                        }
                    } else if (msg.arg1 == 1) {//播放完成
                        if (msg.arg2 == 0)//正常播放结束
                        {

                        } else if (msg.arg2 == 1)//用户停止播放
                        {
                            break;
                        }
                    }

                    QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, (msg.arg1 == 1 ? "播放完成!" : "播放失败!"));
                    playNext();
                    break;

                case QPlayAutoJNI.MESSAGE_RECEIVE_PLAY_BUFF:
                    Trace.Debug(TAG,"###### 正在缓冲数据 ");
                    QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, "正在缓冲数据...");
                    break;

                case QPlayAutoJNI.MESSAGE_RECEIVE_ERROR:
                    QPlayAutoJNI.SendInfo(QPlayAutoJNI.MESSAGE_INFOS_TYPE_NORMAL, TAG, "出现错误:" + msg.obj.toString());
                    break;


                case QPlayAutoArguments.RESPONSE_MEDIA_INFOS:
                    if (msg.arg2 == QPlayAutoArguments.RESPONSE_MEDIA_INFOS) {
                        mCurrentPlaySongInfo = (ResponseMediaInfos) msg.obj;
                        synchronized (this) {
                            this.notify();
                        }
                    }
                    break;

            }
        }
    };

    private void parseMobileCommand(int commandType, int requestID, Object objValue) {

        Trace.Debug(TAG,"#####commandType=" + commandType);
        switch (commandType) {
            case QPlayAutoArguments.REQUEST_DEVICE_PLAY_PRE://接收到移动设备发送的播放上一个命令
                playPre();
                break;
            case QPlayAutoArguments.REQUEST_DEVICE_PLAY_NEXT://接收到移动设备发送的播放下一个命令
                playNext();
                break;
            case QPlayAutoArguments.REQUEST_DEVICE_PLAY_PLAY://接收到移动设备发送的播放命令
                if (getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
                    startPlay();
                }
                break;
            case QPlayAutoArguments.REQUEST_DEVICE_PLAY_PAUSE://接收到移动设备发送的暂停命令

                if (getPlayState() != AudioTrack.PLAYSTATE_PAUSED) {
                    pausePlay();
                }

                break;
            case QPlayAutoArguments.RESPONSE_ERROR:
            case QPlayAutoArguments.REQUEST_ERROR:
                QPlayAutoArguments.CommandError error = (QPlayAutoArguments.CommandError) objValue;
                if (error.ErrorNo == QPlayAutoJNI.ERROR_PROTOCOL_NOT_LOGIN) {
                    Toast.makeText(getApplicationContext(), "QQ音乐没有登录，请到QQ音乐登录!", Toast.LENGTH_LONG).show();
                } else
//                    Log.e(TAG, "parseMobileCommand: 出现错误=================" + error.ErrorNo);
                    //Toast.makeText(this,"出现错误:" + error.ErrorNo,Toast.LENGTH_LONG).show();

                    break;

        }
    }


    @Override
    public void onCreate() {

        super.onCreate();
        Trace.Debug(TAG,"#####oncrete ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Trace.Debug(TAG,"##### onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Trace.Debug(TAG,"###### onBind");
        mCurrentPlaySongInfo = new ResponseMediaInfos();
        QPlayAutoJNI.SetHandler(deviceHandler);
        startPalyThread();
        return binder;
    }

    public int connect() {
        return QPlayAutoJNI.Start(QPlayAutoJNI.DEVICE_TYPE_AUTO, QPlayAutoJNI.CONNECT_TYPE_WIFI, "BMV", "X5");

    }

    public void playList(int index) {
        EcoApplication.LeGlob.getPlayer().stopPlayByUser();

        mIndex = index;
        QPlayAutoSongListItem item = playList.get(index);
        if (item != null && item.ID != null && mCurrentPlaySongInfo != null && mCurrentPlaySongInfo.songID != null && item.ID.equals(mCurrentPlaySongInfo.songID)) {
            return;
        } else {
            releasePlay();
            playSong(item.ID);
        }
        if (mIndexChangeListener != null) {
            mIndexChangeListener.onChange(mIndex);
        }

    }

    public void playPre() {
        EcoApplication.LeGlob.getPlayer().stopPlayByUser();

        int listSize = this.getPlayList().size();
        if (listSize<=0){
            return;
        }
        if(listSize==1){
            mCurrentPlaySongInfo = null;
        }
        switch (mPlayMode) {
            case MODE_RANDOM:
                if (listSize <= 0) break;
                mIndex = (int) (Math.random() * listSize);
                break;
            case MODE_CYCLE_SINGLE:
                mCurrentPlaySongInfo = null;
                break;
            default:
                mIndex--;
                if (listSize <= 0) {
                    return;
                }
                if (mIndex < 0) {
                    mIndex = (mIndex + listSize * 10) % listSize;
                } else {
                    mIndex = mIndex % listSize;
                }

                break;
        }

        playList(mIndex);


    }

    public void playNext() {
        EcoApplication.LeGlob.getPlayer().stopPlayByUser();
        int listSize = this.getPlayList().size();
        if (listSize<=0){
            return;
        }
        if(listSize==1){
            mCurrentPlaySongInfo = null;
        }

        switch (mPlayMode) {
            case MODE_RANDOM:
                mIndex = (int) (Math.random() * listSize);
                break;
            case MODE_CYCLE_SINGLE:
                mCurrentPlaySongInfo = null;
                break;
            default:
                mIndex++;
                if (mIndex < 0) {
                    mIndex = (mIndex + listSize * 10) % listSize;
                } else {
                    mIndex = mIndex % listSize;
                }

                break;
        }

        playList(mIndex);

    }

    public void setmIndexChangeListener(IndexChangeListener mIndexChangeListener) {
        this.mIndexChangeListener = mIndexChangeListener;
    }

    public void setPlayMode(int mode) {
        mPlayMode = mode;
    }

    public class LocalBinder extends Binder {
        public PlayPCM getService() {
            return PlayPCM.this;
        }
    }

    public void setHandler(Handler UIMessageHandler) {
        mUiHandler = UIMessageHandler;
    }

    private void initPlayInfo(ResponseMediaInfos responseMediaInfos) {
        // 获得构建对象的最小缓冲区大小
        Trace.Debug(TAG,"#####Audio start!!!");

        int minBufSize = AudioTrack.getMinBufferSize(responseMediaInfos.frequency, responseMediaInfos.channel, responseMediaInfos.bit);

        // AudioTrack中有MODE_STATIC和MODE_STREAM两种分类。
        // STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到audiotrack中。
        // 这个和我们在socket中发送数据一样，应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到audiotrack。
        // 这种方式的坏处就是总是在JAVA层和Native层交互，效率损失较大。
        // 而STATIC的意思是一开始创建的时候，就把音频数据放到一个固定的buffer，然后直接传给audiotrack，
        // 后续就不用一次次得write了。AudioTrack会自己播放这个buffer中的数据。
        // 这种方法对于铃声等内存占用较小，延时要求较高的声音来说很适用。
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, responseMediaInfos.frequency, responseMediaInfos.channel, responseMediaInfos.bit, minBufSize, AudioTrack.MODE_STREAM);

        mAudioTrack.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
            @Override
            public void onPeriodicNotification(AudioTrack track) {
                Trace.Debug(TAG,"#####T1");
            }

            @Override
            public void onMarkerReached(AudioTrack track) {
                Trace.Debug(TAG,"T2");
            }
        });
        mAudioTrack.setNotificationMarkerPosition(responseMediaInfos.frequency / 2);// 5秒取一次数据
        mAudioTrack.play();
    }

    /*
     * 读取总时间
     */
    public int getTotalTimes() {
        //Log.e(TAG, "#####获取总时间");
        if (mCurrentPlaySongInfo != null) {
            return mCurrentPlaySongInfo.songDuration;
        } else {
            return 0;
        }
    }

    /*
     * 读取当前播放时间
     */
    public int getPlayPosition() {
        //Log.e(TAG, "#####获取当前播放时间");
        if (mAudioTrack != null && mCurrentPlaySongInfo != null && mCurrentPlaySongInfo.frequency != 0 && mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
            try {
                return mAudioTrack.getPlaybackHeadPosition() / mCurrentPlaySongInfo.frequency;
            } catch (Exception err) {
                err.printStackTrace();
                return 0;
            }
        } else {
            return 0;
        }
    }

    /*
     * 当前是否启动播放
     */
    public boolean IsPlay() {
        Trace.Debug(TAG,"#####判断当前是否已启动播放");

        return !stopPlayFlag;
    }

    public ResponseMediaInfos getCurrentPlaySongInfo() {
        return mCurrentPlaySongInfo;
    }

    /*
     * 暂停播放
     */
    public void pausePlay() {
        if (mAudioTrack != null && mAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            Trace.Debug(TAG,"#####暂停播放");
            mAudioTrack.pause();
        }
    }

    /*
     * 播放
     */
    public void startPlay()

    {
        EcoApplication.LeGlob.getPlayer().stopPlayByUser();
        stopByUser = false;
        if (mAudioTrack != null && mAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            Trace.Debug(TAG,"#####继续播放");
            mAudioTrack.play();
        }

    }

    /*
     * 停止播放
     */
    public void stopPlay() {
        if (mAudioTrack != null && mAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            Trace.Debug(TAG,"#####停止播放");
            mAudioTrack.stop();
        }
    }

    /*
     * 获取播放状态
     */
    public int getPlayState() {
        Trace.Debug(TAG,"#####获取播放状态");
        if (mAudioTrack != null && mAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED)
            return mAudioTrack.getPlayState();
        else
            return -1;
    }

    /*
     * 退出播放线程
     */
    public void ExitPlay() {
        Trace.Debug(TAG,"#####退出播放线程");
        PlayExitFlag = true;
        stopByUser = true;
        releasePlay();
        if (mPlayThread != null) {
            mPlayThread.interrupt();
            mPlayThread = null;
        }

    }

    //停止播放
    public void releasePlay() {
        if (mAudioTrack != null) {
            if (mCurrentPlaySongInfo != null) {
                mCurrentPlaySongInfo.songDuration = 0;
            }
            stopPlayFlag = true;
            if (mAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                //Log.e(TAG,"Audio stop2!!!");
                mAudioTrack.flush();
                mAudioTrack.stop();
            }
            mAudioTrack.release();

        }
        mPlayQueneId.clear();
        QPlayAutoJNI.ClearPCMData();
    }

    /*
     * 判断播放缓冲区是否有足够数据(1000K)
     */
    private boolean needRequestData(ConcurrentLinkedQueue<byte[]> binData) {
        if (binData.size() < 100 && binData.size() % 70 == 0)//PCM数据少于1000,一个包数据为10K
        {
            Object[] data = binData.toArray();
            if (data == null || data.length == 0) {
                Trace.Debug(TAG,"#####1)判断播放缓冲区是否有足够数据（NO）");
                return true;
            } else {
                for (int i = 0; i < data.length; i++)
                    if (((byte[]) (data[i])).length == 0)//已经播放完毕,不需要取数据了
                    {
                        Trace.Debug(TAG,"#####2)判断播放缓冲区是否有足够数据（YES）");
                        return false;
                    }
                Trace.Debug(TAG,"#####3)判断播放缓冲区是否有足够数据（NO）");
                return true;
            }
        } else {
            Trace.Debug(TAG,"#####4)判断播放缓冲区是否有足够数据（YES）");
            return false;
        }

    }

    /*
     * 播放退出
     */
    private void finishPlay(int arg1, int arg2, Object obj) {
        Trace.Debug(TAG,"#####播放退出");
        mCurrentPlaySongInfo.songDuration = 0;
        stopPlayFlag = true;
        QPlayAutoJNI.PCMPackageIndex = -1;
        deviceHandler.obtainMessage(QPlayAutoJNI.MESSAGE_RECEIVE_PLAY_FINISH, arg1, arg2, obj).sendToTarget();//发送消息到UI处理
        return;
    }

    /*
     * 启动播放线程
     */
    public void startPalyThread() {
        Trace.Debug(TAG,"#####启动播放线程");
        mPlayThread = new Thread(new Runnable() {
            byte[] bytes = null;

            public void run() {
                while (!PlayExitFlag) {
                    if (mPlayQueneId.size() < 1) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO 自动生成的 catch 块
                            e.printStackTrace();
                        }
                        continue;
                    }
                    String[] playIdList = new String[mPlayQueneId.size()];
                    mPlayQueneId.toArray(playIdList);
                    mPlayQueneId.clear();
                    if (playIdList == null && playIdList.length < 1)
                        continue;

//                    releasePlay();

                    String songID = playIdList[playIdList.length - 1].toString();
                    int playState = -1;
                    stopPlayFlag = false;

                    QPlayAutoJNI.InitPCMData(songID);//初始化变量

                    QPlayAutoJNI.RequestMediaInfo(songID);

                    try {
                        synchronized (deviceHandler) {
                            deviceHandler.wait(3000);//等待3秒，获取歌曲播放信息
                        }
                    } catch (InterruptedException e) {
                        return;
                    }
                    if (mCurrentPlaySongInfo == null || mCurrentPlaySongInfo.songID == null)
                        continue;

                    initPlayInfo(mCurrentPlaySongInfo);

                    while (!stopPlayFlag) {
                        if (mPlayQueneId.size() > 0)
                            break;

                        //判断是否有足够数据播放，如果不够需要向手机发获取PCM命令
//                        if (needRequestData(QPlayAutoJNI.PcmQueue)) {
                        QPlayAutoJNI.RequestPCMData(songID, QPlayAutoJNI.PCMPackageIndex + 1);
//                            BaseActivity.isStoped = true;
//                            EcoApplication.LeGlob.getPlayer().stopPlay();
                        ;
//                        }

                        int currentState = mAudioTrack.getPlayState();
                        if (currentState != playState) {
                            playState = currentState;
                            mUiHandler.obtainMessage(QPlayAutoJNI.MESSAGE_RECEIVE_PLAY_STATE_CHANGE, playState, 0).sendToTarget();//发送播放状态
                        }

                        if (currentState != AudioTrack.PLAYSTATE_PLAYING) {
                            try {
                                Thread.sleep(500);
                                continue;
                            } catch (InterruptedException e) {
                                Trace.Debug(TAG,"playSong Song:" + songID + " stopPlay!!!");
                                finishPlay(1, 1, songID);//用户停止播放
                                break;
                            }
                        }
                        //Log.d(TAG,"Start play song:" + songID + " Data:" + QPlayAutoJNI.PcmQueue.size());
                        bytes = QPlayAutoJNI.PcmQueue.poll();
                        if (bytes == null)// 没有数据,延时200毫秒后再发请求
                        {
                            mUiHandler.obtainMessage(QPlayAutoJNI.MESSAGE_RECEIVE_PLAY_BUFF, 0, 0, songID).sendToTarget();//正在缓冲数据
                            try {
                                Thread.sleep(1000);
                                continue;
                            } catch (InterruptedException e) {
                                Trace.Debug(TAG,"##### 停止播放 " + e.toString());
                                finishPlay(1, 1, songID);//用户停止播放
                                break;
                            }
                        } else if (bytes.length == 0)// 已经传送数据完成
                        {
                            Trace.Debug(TAG,"playSong Song:" + songID + " finish!!!");
                            finishPlay(1, 0, songID);//播放成功
                            break;
                        } else {
                            playAudioTrack(bytes, 0, bytes.length);// 播放
                            //发送播放缓冲信息
//                            mUiHandler.obtainMessage(QPlayAutoJNI.MESSAGE_RECEIVE_INFOS,QPlayAutoJNI.MESSAGE_INFOS_TYPE_PLAY_BUFF_SIZE, QPlayAutoJNI.PcmQueue.size(),songID).sendToTarget();
                        }
                    }
                    mUiHandler.obtainMessage(QPlayAutoJNI.MESSAGE_RECEIVE_PLAY_FINISH, 0, 2, songID).sendToTarget();//播放退出
                    Trace.Debug(TAG,"##### sonid =" + songID + " end!!!");
                }
            }
        });

        mPlayThread.start();

    }

    /*
     * 播放歌曲，实际上只是加入播入队列，由播放线程每100毫秒检查一次播放队列
     */
    public void playSong(String songID) {
        BaseActivity.isStoped = true;
        EcoApplication.LeGlob.getPlayer().stopPlay();
        Trace.Debug(TAG,"****stop music");
        mPlayQueneId.add(songID);
    }

    /*
     * 写入PCM数据来播放
     */
    public void playAudioTrack(byte[] data, int offset, int length) {
        Trace.Debug(TAG,"#####向播放器写入PCM数据");
        if (data == null || data.length == 0)
            return;
        mAudioTrack.write(data, offset, length);
    }


    public synchronized ArrayList<QPlayAutoSongListItem> getPlayList() {
        if (this.playList == null) {
            this.playList = new ArrayList<QPlayAutoSongListItem>();
        }
        return this.playList;
    }

    public interface IndexChangeListener {
        void onChange(int index);
    }

}
