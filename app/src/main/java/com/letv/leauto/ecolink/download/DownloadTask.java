package com.letv.leauto.ecolink.download;

import com.letv.leauto.ecolink.utils.Trace;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by zhaotongkai on 2016/10/18.
 */
public class DownloadTask {
    private static final String TAG = DownloadTask.class.getSimpleName();

    /*下载链接*/
    public String mUrl;
    /*节目id*/
    public String mId;
    /*Album ID*/
    public String mAlbumID;
    /*作者*/
    public String mAuthor;
    /*下载状态*/
    public int mState;
    /*文件总长度*/
    public long mLength;
    /*文件已下载长度*/
    public long mCurrentlen;
    /*保存路径*/
    public String mPath;
    /*文件名*/
    public String mName;
    /*下载来源*/
    public String mResource;

    /*图片url*/
    public String mImageurl;
    /**/

    public String mLeSrcID="";
    public String mSrcID="";
    public String mLeSrcVID="";
    /**/
    private RandomAccessFile mOutput;


    public DownloadTask() {

    }

    public DownloadTask(String url, String id, String path, String name, String resource, String author) {
        mUrl = url;
        mId = id;
        mAuthor = author;
        setPath(path);
        mName = name;
        mResource = resource;
        mState = State.STATE_QUEUED;
        mLength = 0;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getImageurl() {
        return mImageurl;
    }

    public void setImageurl(String imageurl) {
        mImageurl = imageurl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getAlbumID() {
        return mAlbumID;
    }

    public void setAlbumID(String albumID) {
        mAlbumID = albumID;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        if (state < State.STATE_QUEUED || state > State.STATE_FINISH) {
            mState = State.STATE_QUEUED;
        }
        mState = state;
    }

    public long getLength() {
        return mLength;
    }

    public void setLength(long length) {
        mLength = length;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
        mkdirIfneed(mPath);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getResource() {
        return mResource;
    }

    public void setResource(String resource) {
        mResource = resource;
    }

    public long getCurrentlen() {
        return mCurrentlen;
    }

    public void setCurrentlen(long currentlen) {
        if (mLength < currentlen) {
            mLength = currentlen;
        }
        mCurrentlen = currentlen;
    }

    public void remove() {
        File file = new File(mPath, mName);
        if (file.exists()) {
            file.delete();
        }
    }

    public RandomAccessFile getOutStream() {
        if (mOutput != null) {
            return mOutput;
        }
        try {
            mOutput = new RandomAccessFile(new File(mPath, mName), "rw");
            Trace.Debug(TAG, "--->file: " + (mPath + mName));
            return mOutput;
        } catch (Exception e) {
            Trace.Debug(TAG, "--->e:" + e.getMessage());
        }
        return null;
    }

    private void mkdirIfneed(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static class State {
        public static final int STATE_QUEUED = 0;
        public static final int STATE_DOWNLOADING = 1;
        public static final int STATE_PAUSED = 2;
        public static final int STATE_FAILED = 3;
        public static final int STATE_FINISH = 4;
    }
}
