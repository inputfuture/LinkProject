package com.letv.leauto.ecolink.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.letv.leauto.ecolink.http.host.LetvAutoHosts;


/**
 * Created by zhaochao on 2015/7/2.
 */
public class LeAlbumInfo implements Parcelable {
    public String ALBUM_TYPE_ID="";//用于区分视频还是音频
    public String source="";//
    public String NAME="";//专辑名称
    public String AUTHER="";//专辑作者
    public String DIRECTORY;
    public String IMG_URL="";//图片
    public String ALBUM_ID="";//专辑id
    public String RATING;
    public String PAGE_ID="";//
    public String SOURCENAME="";
    public String SOURCEID="";


    //// TODO: 2016/11/8 以下在乐听中没有数据
    public String SOURCE_CP_ID="";
    public String SRC_IMG_URL="";
    public String CREATE_TIME="";
    public String DESCRIPTION="";
    public String SORT_ID="";
    public String UPDATE_TIME="";
    public String ORDER="";
    public String DISPLAY_NAME="";
    public String DISPLAY_SOURCE_URL="";
    public String DISPLAY_LE_SOURCE_VID="";
    public String DISPLAY_LE_SOURCE_MID="";
    public String DISPLAY_ID="";
    public String DISPLAY_IMG_URL="";
    public String channelType="";

    public String PLAYCOUNT="";
    //标记专辑类型，如收藏，本地，最近，下载
    public String TYPE; /*用于标记栏目名称，热门，我的音乐等等*/
    //用于标记音乐或者非音乐

    public String CREATE_USER="";
    public String KUWO_BILL_ID="";//酷我歌单id
    //用于标记下载状态
    public String DOWNLOAD_FLAG="";
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }





    @Override
    public String toString() {
        return "{" +
                "SOURCE_CP_ID='" + SOURCE_CP_ID + '\'' +
                ", SRC_IMG_URL='" + SRC_IMG_URL + '\'' +
                ", NAME='" + NAME + '\'' +
                ", AUTHER='" + AUTHER + '\'' +
                ", IMG_URL='" + IMG_URL + '\'' +
                ", CREATE_TIME='" + CREATE_TIME + '\'' +
                ", DESCRIPTION='" + DESCRIPTION + '\'' +
                ", PAGE_ID='" + SORT_ID + '\'' +
                ", UPDATE_TIME='" + UPDATE_TIME + '\'' +
                ", ORDER='" + ORDER + '\'' +
                ", DISPLAY_NAME='" + DISPLAY_NAME + '\'' +
                ", DISPLAY_SOURCE_URL='" + DISPLAY_SOURCE_URL + '\'' +
                ", DISPLAY_LE_SOURCE_VID='" + DISPLAY_LE_SOURCE_VID + '\'' +
                ", DISPLAY_LE_SOURCE_MID='" + DISPLAY_LE_SOURCE_MID + '\'' +
                ", DISPLAY_ID='" + DISPLAY_ID + '\'' +
                ", DISPLAY_IMG_URL='" + DISPLAY_IMG_URL + '\'' +
                ", PLAYCOUNT='" + PLAYCOUNT + '\'' +
                ", TYPE='" + TYPE + '\'' +
                ", ALBUM_TYPE_ID='" + ALBUM_TYPE_ID + '\'' +
                ", CREATE_USER='" + CREATE_USER + '\'' +
                ", ALBUM_ID='" + ALBUM_ID + '\'' +
                ", DOWNLOAD_FLAG='" + DOWNLOAD_FLAG + '\'' +
                '}';
    }

    public LeAlbumInfo() {}

    public String getRealImgUrl()
    {
        String url = null;
        if(this.IMG_URL==null || this.IMG_URL.length()<=0) {
            url = this.SRC_IMG_URL;
        } else if(this.IMG_URL.startsWith("http://")) {
            url = this.IMG_URL;
        } else if(this.IMG_URL.startsWith("/data")) {
            url = LetvAutoHosts.HOST_URL + "img" + this.IMG_URL.substring(5);
        } else {
            url = LetvAutoHosts.HOST_URL + "img/uploadfile/" + this.IMG_URL;
        }
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.SOURCE_CP_ID);
        dest.writeString(this.SRC_IMG_URL);
        dest.writeString(this.NAME);
        dest.writeString(this.DISPLAY_NAME);
        dest.writeString(this.AUTHER);
        dest.writeString(this.IMG_URL);
        dest.writeString(this.CREATE_TIME);
        dest.writeString(this.DESCRIPTION);
        dest.writeString(this.SORT_ID);
        dest.writeString(this.UPDATE_TIME);
        dest.writeString(this.PLAYCOUNT);
        dest.writeString(this.TYPE);
        dest.writeString(this.ALBUM_TYPE_ID);
        dest.writeString(this.CREATE_USER);
        dest.writeString(this.ALBUM_ID);
        dest.writeString(this.DOWNLOAD_FLAG);
        dest.writeString(this.ORDER);

    }

    private LeAlbumInfo(Parcel dest)
    {
        this.SOURCE_CP_ID = dest.readString();
        this.SRC_IMG_URL = dest.readString();
        this.NAME = dest.readString();
        this.DISPLAY_NAME = dest.readString();
        this.AUTHER = dest.readString();
        this.IMG_URL = dest.readString();
        this.CREATE_TIME = dest.readString();
        this.DESCRIPTION = dest.readString();
        this.SORT_ID = dest.readString();
        this.UPDATE_TIME = dest.readString();
        this.PLAYCOUNT = dest.readString();
        this.TYPE = dest.readString();
        this.ALBUM_TYPE_ID = dest.readString();
        this.CREATE_USER = dest.readString();
        this.ALBUM_ID = dest.readString();
        this.DOWNLOAD_FLAG = dest.readString();
        this.DOWNLOAD_FLAG = dest.readString();
        this.ORDER =  dest.readString();
        this.DISPLAY_SOURCE_URL = dest.readString();
        this.DISPLAY_LE_SOURCE_VID = dest.readString();
        this.DISPLAY_LE_SOURCE_MID = dest.readString();
        this.DISPLAY_ID =  dest.readString();
    }


    public static final Creator<LeAlbumInfo> CREATOR = new Creator<LeAlbumInfo>()
    {
        public LeAlbumInfo createFromParcel(Parcel in)
        {
            return new LeAlbumInfo(in);
        }

        public LeAlbumInfo[] newArray(int size)
        {
            return new LeAlbumInfo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeAlbumInfo albumInfo = (LeAlbumInfo) o;

        if (ALBUM_TYPE_ID != null ? !ALBUM_TYPE_ID.equals(albumInfo.ALBUM_TYPE_ID) : albumInfo.ALBUM_TYPE_ID != null)
            return false;
        if (NAME != null ? !NAME.equals(albumInfo.NAME) : albumInfo.NAME != null) return false;
        return ALBUM_ID != null ? ALBUM_ID.equals(albumInfo.ALBUM_ID) : albumInfo.ALBUM_ID == null;

    }

    @Override
    public int hashCode() {
        int result = ALBUM_TYPE_ID != null ? ALBUM_TYPE_ID.hashCode() : 0;
        result = 31 * result + (NAME != null ? NAME.hashCode() : 0);
        result = 31 * result + (ALBUM_ID != null ? ALBUM_ID.hashCode() : 0);
        return result;
    }
}
