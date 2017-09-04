package com.letv.leauto.ecolink.ui.leradio_interface.dataprovider;

import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.letv.leauto.ecolink.cfg.LeTVConfig;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.mobile.core.utils.FileUtils;
import com.letv.mobile.core.utils.GlobalThreadPool;

import java.io.File;

/**
 * 保存数据，提前获取数据（例如开机就获取数据）<br/>
 * 数据格式变更以后需要修改版本号(mVersion)，以免读取错误的数据引起崩溃
 *
 * @author baiwenlong
 */
public abstract class DataProvider<T> {

    public static final int DATA_FROM_MEMORY_CACHE = 0;// 内存存储
    public static final int DATA_FROM_FILE_CACHE = 1;// 文件存储
    public static final int DATA_FROM_SERVER = 2;// 网络读取

    /**
     * 主versionCode
     */
    private static final int MAIN_VERSION_CODE = 0;
    /**
     * 子versionCode
     */
    protected int mSubVersion;
    private final String TAG = "DataProvider";
    private final String KEY_DATA_TIME = "data_time";
    private final String KEY_DATA = "data";
    private final String KEY_BACK_FLAGS = "back_flags";
    private final Scene[] mScenes;// 需要加载或检查数据的场景
    private long VALID_DURATION = Integer.MAX_VALUE;// 有效的缓存时间，超过缓存时间数据就失效了，就不能返回了
    private long UPDATE_DURATION = 1000 * 60 * 10;// 需要更新数据的时间间隔
    private final String mProviderName;
    private long mDataTime;// 获取到缓存的时间（以开机的时间为标准）
    private Object mData;// 缓存的数据
    private GetDataBackFlags mBackFlags;// 获取数据返回的标志
    private boolean mIsFetchingData = false;// 正在获取数据
    private boolean mIsFetchingFromServer = false;// 正在从服务器获取数据
    private DataProviderInterface mDataListener;
    private final String mLock = new String("lock");
    private final Handler mHandler = new Handler();

    protected DataProvider() {
        this.mScenes = this.generateScenes();
        this.mSubVersion = this.getVersion();
        this.VALID_DURATION = this.generateValidDuration(this.VALID_DURATION);
        this.UPDATE_DURATION = this
                .generateUpdateDuration(this.UPDATE_DURATION);
        this.mProviderName = this.getProviderName();
    }

    /**
     * 供外部调用获取数据的方法
     *
     * @param listener
     */
    public void getData(DataProviderInterface listener) {
        this.getData(listener, false, null);
    }

    public void getData(DataProviderInterface listener, String param) {
        this.getData(listener, false, param);
    }

    public void getData(DataProviderInterface listener, boolean forceRefresh) {
        this.getData(listener, forceRefresh, null);
    }

    /**
     * 供外部调用获取数据的方法
     *
     * @param listener
     */
    public void getData(DataProviderInterface listener, boolean forceRefresh, String param) {
        Trace.Debug(TAG, this.mProviderName
                        + " getData: forceRefresh = " + forceRefresh);
        synchronized (this.mLock) {
            if (this.mIsFetchingData) {// 正在获取数据：直接返回
                Trace.Debug(TAG,
                                this.mProviderName + " is fetching data, will return");
                this.mDataListener = listener;
                if (this.mDataListener != null && this.isIsFetchingFromServer()) {
                    this.mDataListener.onStartGetFromServer();
                }
                return;
            }
            this.mDataListener = null;
            if (!forceRefresh && this.isDataValid()) {// 数据有效：回调返回数据
                Trace.Debug(TAG,
                                this.mProviderName
                                        + " data is valid, will return directly");
                listener.onGetData(this.mData, this.mDataTime,
                        DATA_FROM_MEMORY_CACHE, this.mBackFlags);
            } else {// 数据无效，开始获取数据
                this.startFetchData(listener, forceRefresh, param);
            }
        }
    }

    /**
     * 取消获取数据
     */
    public void cancelGet() {
        synchronized (this.mLock) {
            this.mDataListener = null;
        }
    }



    /**
     * 开始获取数据
     *
     * @param listener
     */
    private void startFetchData(DataProviderInterface listener,
                                final boolean forceRefresh, final String param) {
        Trace.Debug(TAG,
                        this.mProviderName + " startFetchData");
        synchronized (this.mLock) {
            if (this.mIsFetchingData) {
                Trace.Debug(TAG,
                                this.mProviderName + " "
                                        + "startFetchData: is fetching, will return");
                return;
            }
            this.mDataListener = listener;
            this.mIsFetchingData = true;
        }
        GlobalThreadPool.getGlobalCachedPool().execute(new Runnable() {

            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                FetchResultWrap result = null;
                if (!forceRefresh) {// 读取缓存
                    result = DataProvider.this.readFromCache();
                }

                final int dataSource;// 数据来源

                if (result != null && result.isSuccess()) {// 有缓存
                    dataSource = DATA_FROM_FILE_CACHE;
                    long endTime = System.currentTimeMillis();
                    Trace.Debug(TAG,
                                    DataProvider.this.mProviderName + " "
                                            + "fetch data from cache use time "
                                            + (endTime - startTime) + "ms");
                } else {// 无缓存，从网络读取
                    Trace.Debug(TAG,
                                    DataProvider.this.mProviderName
                                            + " will read from server");
                    DataProvider.this.setIsFetchingFromServer(true);
                    synchronized (DataProvider.this.mLock) {
                        final DataProviderInterface listener = DataProvider.this.mDataListener;// 调用从网络读取数据的回调
                        if (listener != null) {
                            DataProvider.this.mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    listener.onStartGetFromServer();
                                }
                            });
                        }
                    }
                    dataSource = DATA_FROM_SERVER;
                    result = DataProvider.this.fetchData(forceRefresh, param);
                    Trace.Debug(TAG,
                                    DataProvider.this.mProviderName
                                            + "result = " + result.isSuccess());
                    if (result != null && result.isSuccess()) {
                        DataProvider.this.saveToCache(result);
                    }
                    DataProvider.this.setIsFetchingFromServer(false);
                    long endTime = System.currentTimeMillis();
                    Trace.Debug(TAG,
                                    DataProvider.this.mProviderName
                                            + " fetch data from server use time "
                                            + (endTime - startTime) + "ms");
                }

                if (result == null) {
                    Trace.Debug(TAG,
                                    DataProvider.this.mProviderName
                                            + " result is null, will create one");
                    result = new FetchResultWrap();
                    result.success = false;
                }

                // 执行结束回调
                synchronized (DataProvider.this.mLock) {
                    final DataProviderInterface listener = DataProvider.this.mDataListener;
                    DataProvider.this.mDataListener = null;
                    DataProvider.this.mIsFetchingData = false;
                    if (result.success) {// 成功
                        Trace.Debug(TAG,
                                        DataProvider.this.mProviderName
                                                + " fetch data success");
                        DataProvider.this.mData = result.data;
                        DataProvider.this.mDataTime = result.dataTime;
                        DataProvider.this.mBackFlags = result.backFlags;
                        if (listener != null) {
                            DataProvider.this.mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    listener.onGetData(DataProvider.this.mData,
                                            DataProvider.this.mDataTime,
                                            dataSource,
                                            DataProvider.this.mBackFlags);
                                }
                            });
                        }
                    } else {// 失败
                        if (result.backFlags == null) {
                            result.backFlags = new GetDataBackFlags();
                        }
                        Trace.Debug(TAG,
                                        DataProvider.this.mProviderName
                                                + " fetch data failed: failedReason = "
                                                + result.backFlags);
                        final GetDataBackFlags failedReason = result.backFlags;
                        if (listener != null) {
                            DataProvider.this.mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    listener.onFailed(failedReason);
                                }
                            });
                        }
                    }

                }
            }
        });
    }

    private void saveToCache(FetchResultWrap data) {
        Trace.Debug(TAG,
                        this.mProviderName + " saveToCache");
        try {
            long startTime = System.currentTimeMillis();
            String filePath = LeTVConfig.getNoSdCardPath()
                    + this.getCacheFileName();
            JSONObject jObj = new JSONObject();
            jObj.put(this.KEY_DATA_TIME, data.getDataTime());
            jObj.put(this.KEY_DATA, data.getData());
            jObj.put(this.KEY_BACK_FLAGS, data.getBackFlags());
            FileUtils.write(jObj.toJSONString(), filePath);
            Trace.Debug(TAG,
                            this.mProviderName + " saveToCache success, use time "
                                    + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            Trace.Debug(TAG,
                            this.mProviderName + " saveToCache failed: " + e);
            e.printStackTrace();
        }
    }

    private FetchResultWrap readFromCache() {
        Trace.Debug(TAG,
                        this.mProviderName + " readFromCache");
        long startTime = System.currentTimeMillis();
        String filePath = LeTVConfig.getNoSdCardPath()
                + this.getCacheFileName();
        File cacheFile = new File(filePath);
        if (cacheFile.exists() && cacheFile.length() > 0) {
            try {
                String content = FileUtils.read(cacheFile.getAbsolutePath());
                JSONObject jObj = JSON.parseObject(content);
                long dataTime = jObj.getLongValue(this.KEY_DATA_TIME);
                T dataReaded = this.parseData(jObj.getString(this.KEY_DATA));
                GetDataBackFlags backFlags = jObj.getObject(
                        this.KEY_BACK_FLAGS, GetDataBackFlags.class);
                if (dataReaded != null) {
                    FetchResultWrap wrap = new FetchResultWrap();
                    wrap.setData(dataReaded);
                    wrap.setDataTime(dataTime);
                    wrap.setSuccess(true);
                    wrap.setBackFlags(backFlags);
                    Trace.Debug(TAG,
                                    this.mProviderName
                                            + " readFromCache success, use time "
                                            + (System.currentTimeMillis() - startTime)
                                            + "ms");
                    return wrap;
                }
            } catch (Exception e) {
                Trace.Debug(TAG,
                                this.mProviderName + " readFromCache failed: " + e);
                e.printStackTrace();
            }
        } else {
            Trace.Debug(TAG,
                            this.mProviderName
                                    + " readFromCache: no cache file or file.length = 0");
        }
        return null;
    }

    private String getCacheFileName() {
        return this.getCacheFileNamePrefix() + "_" + MAIN_VERSION_CODE + "_"
                + this.mSubVersion;
    }

    /**
     * 获取数据（从网络或者文件等）<br/>
     * NOTE(baiwenlong):这里不可以新起线程执行
     */
    public abstract FetchResultWrap fetchData(boolean forceRefresh, String param);

    /**
     * 看是否有匹配的scene
     *
     * @param scene
     * @return
     */
    public boolean matchScene(Scene scene) {
        if (this.mScenes != null) {
            for (Scene t : this.mScenes) {
                if (t == scene) {
                    return true;
                }
            }
        }
        return false;
    }

    public void checkToAutoLoadData(Scene scene) {
        synchronized (this.mLock) {
            if (this.isNeedUpdate()) {
                this.startFetchData(null, this.mData != null, null);
            }
        }
    }

    protected abstract Scene[] generateScenes();

    protected abstract long generateValidDuration(long defaultValue);

    protected abstract long generateUpdateDuration(long defaultValue);

    protected abstract String getProviderName();

    protected abstract String getCacheFileNamePrefix();

    protected abstract T parseData(String dataString);

    protected abstract int getVersion();

    private boolean isDataValid() {
        return this.mData != null
                && this.getCurrentTime() - this.mDataTime < this.VALID_DURATION;
    }

    /**
     * 是否需要更新数据
     *
     * @return
     */
    public boolean isNeedUpdate() {
        return this.mData == null
                || (this.getCurrentTime() - this.mDataTime > this.UPDATE_DURATION);
    }

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }

    protected static class FetchResultWrap {
        Object data;
        long dataTime;
        boolean success;
        GetDataBackFlags backFlags;

        public FetchResultWrap() {
            super();
        }

        public Object getData() {
            return this.data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public long getDataTime() {
            return this.dataTime;
        }

        public void setDataTime(long dataTime) {
            this.dataTime = dataTime;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public GetDataBackFlags getBackFlags() {
            return this.backFlags;
        }

        public void setBackFlags(GetDataBackFlags backFlags) {
            this.backFlags = backFlags;
        }

    }

    public boolean isIsFetchingFromServer() {
        synchronized (this.mLock) {
            return this.mIsFetchingFromServer;
        }
    }

    public void setIsFetchingFromServer(boolean isFetchingFromServer) {
        synchronized (this.mLock) {
            this.mIsFetchingFromServer = isFetchingFromServer;
        }
    }
}
