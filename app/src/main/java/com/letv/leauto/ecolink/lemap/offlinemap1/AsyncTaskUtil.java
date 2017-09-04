package com.letv.leauto.ecolink.lemap.offlinemap1;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;


/**
 * Created by tianwei on 16/6/7.
 */
public class AsyncTaskUtil {
    private static final int MESSAGE_POST_PROGRESS = 0x1;

    private AsyncTaskUtil() {
    }

    private InternalHandler sHandler;
    private AsynchTask mAsynchTask;
    private AsyncTaskListener mAsyncTaskListener;


    public static AsyncTaskUtil newInstance() {
        return new AsyncTaskUtil();
    }

    public AsyncTaskListener getAsyncTaskListener() {
        return mAsyncTaskListener;
    }

    /**
     * 异步执行任务
     *
     * @param params
     * @param asyncTaskListener
     */
    @MainThread
    public synchronized void execute(AsyncTaskListener asyncTaskListener, Object... params) {
        mAsyncTaskListener = asyncTaskListener;
        mAsynchTask = new AsynchTask();
        mAsynchTask.execute(params);
    }

    public void cancel() {
        if (mAsynchTask != null) {
            mAsynchTask.cancel(true);
            mAsynchTask = null;
        }
    }

    public void publishProgress(int progress) {
        if (!mAsynchTask.isCancelled()) {
            getHandler().obtainMessage(MESSAGE_POST_PROGRESS,
                    progress).sendToTarget();
        }
    }

    public class AsynchTask extends AsyncTask<Object, Integer, Object> {

        @Override
        protected void onPreExecute() {
            mAsyncTaskListener.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            return mAsyncTaskListener.doInBackground(params);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
//            mAsyncTaskListener.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Object result) {
            mAsyncTaskListener.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            mAsyncTaskListener.onCancelled();
        }

        @Override
        protected void onCancelled(Object result) {
            mAsyncTaskListener.onCancelled(result);
        }

    }


    public interface AsyncTaskListener {
        void onPreExecute();

        Object doInBackground(Object... params);

        //主线程
        void onProgressUpdate(int progress);

        void onPostExecute(Object result);

        void onCancelled();

        void onCancelled(Object result);
    }

    private Handler getHandler() {
        synchronized (AsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    private class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_POST_PROGRESS:
                    if (mAsyncTaskListener != null)
                        mAsyncTaskListener.onProgressUpdate((Integer) msg.obj);
                    break;
            }
        }
    }


}
