package com.letv.leauto.ecolink.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.letv.leauto.ecolink.http.host.LetvAutoHosts;
import com.letv.leauto.ecolink.ui.leradio_interface.data.NowPlayingProgramme;
import com.letv.leauto.ecolink.utils.DeviceUtils;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by lww on 2015/6/26.
 */
public class MediaDetail implements Parcelable,Cloneable {

    public String CREATE_TIME;//创建时间
    public String AUDIO_ID="";//歌曲id
    public String AUTHOR;//作者
    public String NAME="";//歌曲名称
    public String ALBUM;//专辑名称
    public String ALBUM_ID;//专辑id
    public String TYPE;//自己区分歌曲的类型，酷我还是乐听
    public String SONG_LYRIC;//歌词
    private int duration;//歌曲的时间,单位s
    public Long START_TIME;//开始时间（直播用）
    public Long END_TIME;//结束时间（直播用）
    public String playType;//乐听用，区分音频7,10还是视频1,2
    public String IMG_URL;//图片的Url
    public String SOURCE_URL;//歌曲的Url
    public boolean fileIfExist;//判断是否有本地音乐
    private String sourceName;//资源名称
    public String channelType;/*用于记录乐听中 栏目名称*/
    public String XIA_MI_ID = "";
    public String LE_SOURCE_VID="";//
    public String LE_SOURCE_MID="";//
    public String SOURCE_CP_ID="";//
    public String CREATE_USER;//
    public String UPDATE_USER;//
    //用于标记下载状态
    public int DOWNLOAD_FLAG;
    public String DOWNLOAD_ID;
    //用于记录下载时间 recycle 7天自动清除
    public String DOWNLOAD_TIME;
    public String PATH="";
    public String ARTIST;
    /*文件总长度*/
    public long mLength;
    /*当前下载长度*/
    public long mCurrentlen;
    /*保存路径*/
    public String mPath;
    /*下载速度*/
    private String mSpeed="0";
    /**/
   // private RandomAccessFile mOutput;
    private String mDownLoadSourceURL = "";

    public boolean isFileIfExist() {
        return fileIfExist;
    }
    public void setFileIfExist(boolean fileIfExist) {
        this.fileIfExist = fileIfExist;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getPlayType() {
        return playType;
    }
    public void setPlayType(String playType) {
        this.playType = playType;
    }
    public String getSourceName() {
        return sourceName;
    }
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    private static  final String TAG = MediaDetail.class.getSimpleName();



    public MediaDetail() {
        String path = DeviceUtils.getMusicCachePath();
        setPath(path);
        DOWNLOAD_FLAG = State.STATE_QUEUED;
    }

    public MediaDetail(String url, String id, String path, String name, String resource, String author) {
        SOURCE_URL = url;
        AUDIO_ID = id;
        AUTHOR = author;
        setPath(path);
        NAME = name;
        TYPE = resource;
        DOWNLOAD_FLAG = State.STATE_QUEUED;
        mLength = 0;
    }

    public void setPath(String path) {
        mPath = path;
        mkdirIfneed(mPath);
    }

    /**
     * 获取播放地址
     */
    public String getFile() {
        return mPath + NAME + ".mp3";
    }

    public String getSpeed() {
        return mSpeed;
    }

    public void setSpeed(String speed) {
        mSpeed = speed;
    }

    public void setCurrentlen(long currentlen) {
        if (mLength < currentlen) {
            mLength = currentlen;
        }
        mCurrentlen = currentlen;
    }

    public void remove() {
        File file = new File(mPath, NAME + ".mp3");
        if (file.exists()) {
            file.delete();
        }
    }

//    public RandomAccessFile getOutStream() {
//        if (mOutput != null) {
//            return mOutput;
//        }
//        try {
//            mOutput = new RandomAccessFile(new File(mPath, NAME + ".mp3"), "rw");
//            Log.d(TAG, "--->file: " + (mPath + NAME));
//            return mOutput;
//        } catch (Exception e) {
//            Log.d(TAG, "--->e:" + e.getMessage());
//        }
//        return null;
//    }

    private void mkdirIfneed(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    @Override
    public String toString() {
        return "MediaDetail{" +
                "AUTHOR='" + AUTHOR + '\'' +
                ", IMG_URL='" + IMG_URL + '\'' +
                ", CREATE_TIME='" + CREATE_TIME + '\'' +
                ", AUDIO_ID='" + AUDIO_ID + '\'' +
                ", SOURCE_URL='" + SOURCE_URL + '\'' +
                ", LE_SOURCE_MID='" + LE_SOURCE_MID + '\'' +
                ", SOURCE_CP_ID='" + SOURCE_CP_ID + '\'' +
                ", NAME='" + NAME + '\'' +
                ", ALBUM_ID='" + ALBUM_ID + '\'' +
                ", LE_SOURCE_VID='" + LE_SOURCE_VID + '\'' +
                ", CREATE_USER='" + CREATE_USER + '\'' +
                ", UPDATE_USER='" + UPDATE_USER + '\'' +
                ", START_TIME=" + START_TIME +
                ", END_TIME=" + END_TIME +
                ", SONG_LYRIC='" + SONG_LYRIC + '\'' +
                ", DOWNLOAD_FLAG='" + DOWNLOAD_FLAG + '\'' +
                ", DOWNLOAD_ID='" + DOWNLOAD_ID + '\'' +
                ", DOWNLOAD_TIME='" + DOWNLOAD_TIME + '\'' +
                ", TYPE='" + TYPE + '\'' +
                ", ARTIST='" + ARTIST + '\'' +
                ", ALBUM='" + ALBUM + '\'' +
                ", fileIfExist=" + fileIfExist +
                ", duration=" + duration +
                ", playType='" + playType + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", IMG_URL='" + IMG_URL + '\'' +
                ", SOURCE_URL='" + SOURCE_URL + '\'' +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaDetail that = (MediaDetail) o;

        if (AUTHOR != null ? !AUTHOR.equals(that.AUTHOR) : that.AUTHOR != null) return false;
        if (IMG_URL != null ? !IMG_URL.equals(that.IMG_URL) : that.IMG_URL != null) return false;
        if (CREATE_TIME != null ? !CREATE_TIME.equals(that.CREATE_TIME) : that.CREATE_TIME != null)
            return false;
        if (AUDIO_ID != null ? !AUDIO_ID.equals(that.AUDIO_ID) : that.AUDIO_ID != null)
            return false;
        if (LE_SOURCE_MID != null ? !LE_SOURCE_MID.equals(that.LE_SOURCE_MID) : that.LE_SOURCE_MID != null)
            return false;
        if (SOURCE_CP_ID != null ? !SOURCE_CP_ID.equals(that.SOURCE_CP_ID) : that.SOURCE_CP_ID != null)
            return false;
        if (NAME != null ? !NAME.equals(that.NAME) : that.NAME != null) return false;
        if (ALBUM_ID != null ? !ALBUM_ID.equals(that.ALBUM_ID) : that.ALBUM_ID != null)
            return false;
        return LE_SOURCE_VID != null ? LE_SOURCE_VID.equals(that.LE_SOURCE_VID) : that.LE_SOURCE_VID == null;

    }

    @Override
    public int hashCode() {
        int result = AUTHOR != null ? AUTHOR.hashCode() : 0;
        result = 31 * result + (IMG_URL != null ? IMG_URL.hashCode() : 0);
        result = 31 * result + (CREATE_TIME != null ? CREATE_TIME.hashCode() : 0);
        result = 31 * result + (AUDIO_ID != null ? AUDIO_ID.hashCode() : 0);
        result = 31 * result + (LE_SOURCE_MID != null ? LE_SOURCE_MID.hashCode() : 0);
        result = 31 * result + (SOURCE_CP_ID != null ? SOURCE_CP_ID.hashCode() : 0);
        result = 31 * result + (NAME != null ? NAME.hashCode() : 0);
        result = 31 * result + (ALBUM_ID != null ? ALBUM_ID.hashCode() : 0);
        result = 31 * result + (LE_SOURCE_VID != null ? LE_SOURCE_VID.hashCode() : 0);
        return result;
    }

    public String getRealImgUrl() {
        String url = null;
        if(this.IMG_URL !=null) {
            if (this.IMG_URL.startsWith("http://")) {
                url = this.IMG_URL;
            } else if (this.IMG_URL.startsWith("/data")) {
                url = LetvAutoHosts.HOST_URL + "img" + this.IMG_URL.substring(5);
            } else {
                url = LetvAutoHosts.HOST_URL + "img/uploadfile/" + this.IMG_URL;
            }
        }
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.AUTHOR);
        dest.writeString(this.SOURCE_URL);

        dest.writeString(this.NAME);
        dest.writeString(this.IMG_URL);
        dest.writeString(this.CREATE_TIME);
        dest.writeString(this.SOURCE_CP_ID);
        dest.writeString(this.LE_SOURCE_MID);
        dest.writeString(this.LE_SOURCE_VID);
        dest.writeString(this.AUDIO_ID);
        dest.writeString(this.CREATE_USER);
        dest.writeString(this.ALBUM_ID);
        dest.writeString(this.UPDATE_USER);
        dest.writeInt(this.DOWNLOAD_FLAG);
        dest.writeString(this.DOWNLOAD_TIME);
        dest.writeString(this.TYPE);
    }

    private MediaDetail(Parcel dest) {
        this.AUTHOR = dest.readString();
        this.SOURCE_URL = dest.readString();
        this.NAME = dest.readString();
        this.IMG_URL = dest.readString();
        this.CREATE_TIME = dest.readString();
        this.SOURCE_CP_ID = dest.readString();
        this.LE_SOURCE_MID = dest.readString();
        this.LE_SOURCE_VID = dest.readString();
        this.AUDIO_ID = dest.readString();
        this.CREATE_USER = dest.readString();
        this.ALBUM_ID = dest.readString();
        this.UPDATE_USER = dest.readString();

        this.DOWNLOAD_FLAG = dest.readInt();
        this.DOWNLOAD_ID = dest.readString();
        this.DOWNLOAD_TIME = dest.readString();
        this.TYPE = dest.readString();
    }

    public static final Creator<MediaDetail> CREATOR = new Creator<MediaDetail>() {
        public MediaDetail createFromParcel(Parcel in) {
            return new MediaDetail(in);
        }

        public MediaDetail[] newArray(int size) {
            return new MediaDetail[size];
        }
    };


    public static class State {
        public static final int STATE_QUEUED = 0;
        public static final int STATE_DOWNLOADING = 1;
        public static final int STATE_PAUSED = 2;
        public static final int STATE_FAILED = 3;
        public static final int STATE_FINISH = 4;
        public static final int STATE_NONE = 5;
    }

    public String getmDownLoadSourceURL() {
        return mDownLoadSourceURL;
    }


    public void setmDownLoadSourceURL(String mDownLoadSourceURL) {
        this.mDownLoadSourceURL = mDownLoadSourceURL;
    }
}
