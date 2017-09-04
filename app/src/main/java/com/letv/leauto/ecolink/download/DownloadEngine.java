package com.letv.leauto.ecolink.download;

import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.utils.Constants;
import com.letv.leauto.ecolink.utils.TimeUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;

/**
 * Created by zhaotongkai on 2016/10/18.
 */
public class DownloadEngine implements Callable<Long> {
    private static final String TAG = DownloadEngine.class.getSimpleName();

    public static final int ERR_CODE_SUCCESS = 0;
    public static final int ERR_CODE_CONNECT_FAIL = 1;
    public static final int ERR_CODE_TIMEOUT = 2;
    public static final int ERR_CODE_READ_FAIL = 3;
    public static final int ERR_CODE_INTERRUPTED = 4;
    public static final int ERR_CODE_CREATEFILE_FAIL = 5;


    private long startTime;
    private MediaDetail mTask;
    private OkHttpClient mOkHttpClient;
    private boolean mRunning;
    private RandomAccessFile mOutStream;
    private Thread mThread;
    Future<Long> future;
    private DownloadListener mListener;
    public ExecutorService mThreadPool = Executors.newFixedThreadPool(1);
    public static ExecutorService mOpThread = Executors.newFixedThreadPool(2);
    public HttpHandler<File> fileHttpHandler;

    public DownloadEngine() {

    }

    public void setTask(MediaDetail task) {
        mTask = task;
//        mOutStream = mTask.getOutStream();
    }

    public MediaDetail getTask() {
        return mTask;
    }

    public void setListener(DownloadListener listener) {
        mListener = listener;
    }

    @Override
    public Long call() {
        final long[] startTime = {0};
        mRunning = true;
        HttpUtils http = new HttpUtils();
        http.configRequestThreadPoolSize(1);
        fileHttpHandler = http.download(mTask.getmDownLoadSourceURL(), mTask.SOURCE_URL, true, false, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                mTask.DOWNLOAD_FLAG = DownloadTask.State.STATE_FINISH;
                if (mTask != null && mTask.getPlayType() == null) {
                    mTask.setPlayType(Constants.TYPE_AUDIO);
                }

                MediaOperation.getInstance().updateDetailDownloadStateId(mTask.TYPE, mTask.SOURCE_URL, mTask.NAME, String.valueOf(MediaDetail.State.STATE_FINISH));
                mRunning = false;
                mListener.onSuccess(mTask);
                fileHttpHandler = null;
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mRunning = false;
                mListener.onFail(mTask, 0);
                fileHttpHandler = null;

            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                mRunning = false;
                mListener.onPause(mTask);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                mRunning = true;
                mTask.setCurrentlen(current);
                mTask.mLength = total;
                if (mListener != null) {
                    mListener.onProgress(mTask, current);
                }
                long curTime = System.currentTimeMillis();
                //计算下载速度
                String speed = TimeUtils.getSize(startTime[0], curTime, current);
                mTask.setSpeed(speed);

            }

            @Override
            public void onStart() {
                super.onStart();
                mRunning = true;
                mListener.onStart(mTask);
                startTime[0] = System.currentTimeMillis();
            }
        });


        return Long.valueOf(mTask.mLength);
    }


    public void start(MediaDetail task) {
        setTask(task);
        if (mTask == null /*|| mOutStream == null*/) {
            onFail(ERR_CODE_CREATEFILE_FAIL);
            return;
        }
        mTask.DOWNLOAD_FLAG = MediaDetail.State.STATE_DOWNLOADING;
        mRunning = true;
//        Trace.Debug("#### mrunning ="+mRunning);
        future = mThreadPool.submit(this);
    }

    public void finish() {
        if (fileHttpHandler != null) {
            fileHttpHandler.cancel();
        }

        mRunning = false;
        if (mTask != null) {
            Trace.Debug(TAG, "--->finish:" + mTask.NAME + "   state=" + mTask.DOWNLOAD_FLAG);
        }
        if (future != null) {
            future.cancel(true);
        }
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void onFail(int code) {
        mTask.DOWNLOAD_FLAG = DownloadTask.State.STATE_FAILED;
        mRunning = false;
        if (mListener != null) {
            mListener.onFail(mTask, code);
        }
    }

    public void resume() {
        mRunning = true;
        future = mThreadPool.submit(this);
//        if (fileHttpHandler!=null){
//            fileHttpHandler.resume();
//        }
    }

    /**
     * 状态监听只支持单接口模式，即只能有一个监听者存在
     * 如果使用我们的DownloadManager管理下载任务，DownloadManager就是监听者
     * 请不要再重置新监听者
     * 如果直接使用DownloadEngine，可以设置监听者
     */
    public interface DownloadListener {
        public void onSuccess(MediaDetail task);

        public void onStart(MediaDetail task);

        public void onPause(MediaDetail task);

        public void onFail(MediaDetail task, int code);

        public void onProgress(MediaDetail task, float percent);

        public void remove();

        public void add();
    }
}
