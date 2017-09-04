package com.letv.leauto.ecolink.download;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.http.leradio.DetailLoader;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MediaInfoModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicDetailModel;
import com.letv.leauto.ecolink.utils.Constants;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.Trace;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by zhaotongkai on 2016/10/20.
 */
public class DownloadManager implements DownloadEngine.DownloadListener{

    private LinkedList<MediaDetail> mDownloadingTasks;
    private LinkedList<MediaDetail> mDownloadedTasks;
    private DownloadEngine mDownloadEngine;
    private static DownloadManager mInstance = null;
    private volatile boolean mStopAll;
    private MediaDetail mTask;

    private List<DownloadEngine.DownloadListener> mListeners;

    private static final String TAG = DownloadManager.class.getSimpleName();

    private HandlerThread handlerThread;
    private MyHandler mHandler;

    public static DownloadManager getInstance() {
        if (mInstance == null) {
            mInstance = new DownloadManager();
        }
        return mInstance;
    }
    private DownloadManager() {
        mDownloadingTasks = new LinkedList<>();
        mDownloadedTasks = new LinkedList<>();
        mDownloadEngine = new DownloadEngine();
        mDownloadEngine.setListener(this);
        mListeners = new ArrayList<>();
        handlerThread = new HandlerThread("myHandlerThread");
        handlerThread.start();
        mHandler = new MyHandler(handlerThread.getLooper());
        loadFromDb();
        if (mDownloadingTasks != null && mDownloadingTasks.size() > 0) {
            schedule();
        }
    }

    public void loadFromDb() {
        MediaOperation op = MediaOperation.getInstance();
        List<MediaDetail> list = op.getAllDownloads();
        for (MediaDetail t : list) {
            Trace.Debug(TAG, "--->task: " + t.NAME + "  " + t.NAME+"，t.DOWNLOAD_FLAG="+t.DOWNLOAD_FLAG);
            if (t.DOWNLOAD_FLAG != MediaDetail.State.STATE_FINISH) {
                mDownloadingTasks.add(t);
            }
        }
    }

    /*调度下载任务轮转*/
    public void schedule() {
        if (mStopAll){
            return;
        }
//       Trace.Debug( "#####schecule");
        if (mDownloadEngine.isRunning()) {
            return ;
        }
        if (mDownloadingTasks != null && mDownloadingTasks.size() > 0) {

            for (int i=0; i < mDownloadingTasks.size(); i++) {
                MediaDetail task = mDownloadingTasks.get(i);
                if (task != null && (task.DOWNLOAD_FLAG == MediaDetail.State.STATE_QUEUED ||
                        task.DOWNLOAD_FLAG == MediaDetail.State.STATE_PAUSED )) {
//                    Trace.Debug( "#####start tast ="+task.toString());
                    mTask = task;
                    DetailLoader loader = new DetailLoader(EcoApplication.getInstance(), mHandler);
                    if(mTask!=null && mTask.getPlayType()==null){
                        mTask.setPlayType(Constants.TYPE_AUDIO);
                    }
                    loader.load(task.AUDIO_ID, task.getPlayType());
                    if(task != null && (task.DOWNLOAD_FLAG == MediaDetail.State.STATE_QUEUED)){
                        LetvReportUtils.reportDownloadMusic(task.ALBUM_ID, task);
                    }
                    break;
                }
            }
        }
    }

    public void stopAll() {

        mStopAll = true;
        if (mDownloadEngine != null) {
            mDownloadEngine.finish();
        }
    }

    public boolean getStopState(){
        return mStopAll;
    }

    public void start(MediaDetail task) {
        MediaDetail t = mDownloadEngine.getTask();
        if (mDownloadEngine.isRunning()) {
            mDownloadEngine.finish();
            try {
                Thread.sleep(500);
            } catch(Exception e) {}
        }
        if (t != null) {
            t.DOWNLOAD_FLAG = MediaDetail.State.STATE_PAUSED;
        }

        mTask = task;
        if(mTask!=null && mTask.getPlayType()==null){
            mTask.setPlayType(Constants.TYPE_AUDIO);
        }
        DetailLoader loader = new DetailLoader(EcoApplication.getInstance(), mHandler);
        loader.load(task.AUDIO_ID, task.getPlayType());

    }
    public void stop() {
        MediaDetail t = mDownloadEngine.getTask();
        mDownloadEngine.finish();
        t.DOWNLOAD_FLAG = MediaDetail.State.STATE_PAUSED;
    }

    public void resumeAll() {
        mStopAll=false;
        if (mDownloadEngine.fileHttpHandler!=null){
            mDownloadEngine.resume();
        }else {
            schedule();
        }
    }

    public void add(MediaDetail task, String type) {
        Trace.Debug(TAG, "--->add task: " + task.NAME);
        task.DOWNLOAD_FLAG = MediaDetail.State.STATE_QUEUED;
        /*写数据库要开事务，提高效率*/
        MediaOperation op = MediaOperation.getInstance();
        String path = DeviceUtils.getMusicCachePath();
        task.setPath(path);
        task.TYPE = type;
        task.SOURCE_URL = task.getFile();
        mDownloadingTasks.add(task);
        op.insertMediaDetail(SortType.SORT_LE_RADIO_LOCAL, task);
        task.SOURCE_URL = "";
       /* if (!mDownloadEngine.isRunning()) {
            mTask = task;
            DetailLoader loader = new DetailLoader(EcoApplication.getInstance(), mHandler);
            loader.load(task.AUDIO_ID, task.getPlayType());
        }*/
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.add();
        }
        schedule();
    }

    /**
     * 批量添加下载任务
     * @param medias 歌曲信息
     * @param sourceType 来源；LeRedio，Kuwo等
     */
    public void addAll(Handler handler,List<MediaDetail> medias, String sourceType) {
        if (medias == null || medias.size() <= 0) {
            return;
        }

        String path = DeviceUtils.getMusicCachePath();

        /*写数据库要开事务，提高效率*/
        MediaOperation op = MediaOperation.getInstance();
        // op.beginTransaction();
        for (MediaDetail detail : medias) {
            detail.DOWNLOAD_FLAG = MediaDetail.State.STATE_QUEUED;
            mDownloadingTasks.addLast(detail);
            detail.setPath(path);
            detail.TYPE = sourceType;
            detail.SOURCE_URL = detail.getFile();
            op.insertMediaDetail(SortType.SORT_LE_RADIO_LOCAL, detail);
            detail.SOURCE_URL = "";
        }
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.add();
        }
        handler.sendEmptyMessage(MessageTypeCfg.MSG_REFRESH_COMPLETED);
        /*写数据库，关闭事务*/
        // op.endTransaction();
        /* start download */
        schedule();
    }

    public void remove(final List<MediaDetail> list, final Handler handler) {
        if (list == null || list.size() <= 0) {
            return ;
        }
        DownloadEngine.mOpThread.execute(new Runnable() {
            @Override
            public void run() {
                /*写数据库要开事务，提高效率*/
                MediaOperation op = MediaOperation.getInstance();
                op.beginTransaction();
                ListIterator lit = list.listIterator();
                MediaDetail task = new MediaDetail();
                while (lit.hasNext()) {
                    task = (MediaDetail)lit.next();
                    MediaDetail t = mDownloadEngine.getTask();
                    if (mDownloadEngine.isRunning() && t == task) {
                        mDownloadEngine.finish();
                    }
                    mDownloadingTasks.remove(task);
                    task.remove();
                    lit.remove();
                    op.deleteMediaDetailbyAudioId(task.TYPE, task.AUDIO_ID);
                }

                /*写数据库，关闭事务*/
                 op.endTransaction();
                /**/
                for (DownloadEngine.DownloadListener listener : mListeners) {
                    listener.remove();
                }
                schedule();
                handler.sendEmptyMessage(MessageTypeCfg.MSG_REFRESH_COMPLETED);
            }
        });
    }

    public void deleteDownloadFile(final List<MediaDetail> list, final Handler handler){
        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    MediaDetail mediaDetail=list.get(i);
                    MediaOperation mediaOperation=MediaOperation.getInstance();
                    mediaOperation.deleteMediaDetailbyAudioId(mediaDetail.TYPE,mediaDetail.AUDIO_ID);
                    if (!TextUtils.isEmpty(mediaDetail.SOURCE_URL)){
                        File file=new File(mediaDetail.SOURCE_URL);
                        if (file.exists()){
                            file.delete();
                        }else{
                            file =new File(mediaDetail.getFile());
                            if (file.exists()){
                                file.delete();
                            }
                        }}
                }
                handler.sendEmptyMessage(MessageTypeCfg.MSG_REFRESH_COMPLETED);
            }
        }.start();
    }

    public void remove(MediaDetail task) {
        if (task == null) {
            return ;
        }
        MediaOperation op = MediaOperation.getInstance();
        op.deleteMediaDetailbyAudioId(task.TYPE, task.AUDIO_ID);
        mDownloadingTasks.remove(task);
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.remove();
        }

        task.remove();
        schedule();
    }

    public List<MediaDetail> getDownloadings() {
        return mDownloadingTasks;
    }

    public List<MediaDetail> getDownloadeds() {
        return mDownloadedTasks;
    }

    @Override
    public void onSuccess(MediaDetail task) {
       // MediaDetail task = mDownloadEngine.getTask();
        MediaOperation op = MediaOperation.getInstance();
        Trace.Debug("#### download success");
        /*把已下载的任务转移到已下载队列*/
        mDownloadingTasks.remove(task);
        task.DOWNLOAD_FLAG = MediaDetail.State.STATE_FINISH;
        op.updateDetailDownloadStateId(task);
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.onSuccess(task);
        }
        schedule();
    }

    @Override
    public void onStart(MediaDetail task) {
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.onStart(task);
        }
    }

    @Override
    public void onPause(MediaDetail task) {
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.onStart(task);
        }
        schedule();
    }
    @Override
    public void onFail(MediaDetail task,int code) {
       // MediaDetail task = mDownloadEngine.getTask();
        if(task != null) {
            mDownloadingTasks.remove(task);
            MediaOperation op = MediaOperation.getInstance();
            Trace.Debug("#### download onFail");
        /*把已下载的任务转移到已下载队列*/
            mDownloadingTasks.remove(task);
            task.DOWNLOAD_FLAG = MediaDetail.State.STATE_FAILED;
            op.updateDetailDownloadStateId(task);
            mDownloadingTasks.addLast(task);
        }
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.onFail(task,code);
        }
        schedule();
    }

    @Override
    public void onProgress(MediaDetail task,float percent) {
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.onProgress(task,percent);
        }
    }

    @Override
    public void remove() {
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.remove();
        }
    }

    @Override
    public void add() {
        for (DownloadEngine.DownloadListener listener : mListeners) {
            listener.add();
        }
    }

    public void registerListener(DownloadEngine.DownloadListener listener) {
       mListeners.add(listener);
    }

    public void unregisterListener(DownloadEngine.DownloadListener listener) {
        mListeners.remove(listener);
    }
    public void deleteFile(){}



    class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MessageTypeCfg.MSG_GET_MUSIC_URL:
                        MusicDetailModel model = (MusicDetailModel) msg.obj;
                        mTask.setmDownLoadSourceURL(model.playUrl);
                        mTask.SOURCE_URL = mTask.getFile();
                        mDownloadEngine.start(mTask);
                        break;
                    case  MessageTypeCfg.MSG_GETDATA_EXCEPTION:
                        onFail(mTask,1);
                        break;
                    default:
                        break;
                }
        }
    }


    }
