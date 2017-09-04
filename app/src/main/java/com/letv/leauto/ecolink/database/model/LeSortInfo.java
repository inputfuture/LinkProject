package com.letv.leauto.ecolink.database.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaochao on 2015/7/2.
 */
public class LeSortInfo implements Parcelable {

    public String SOURCE_CP_ID;
    public String SRC_IMG_URL;
    public String NAME;
    public int SOURCE_ID;
    public String IMG_URL;
    public String CREATE_TIME;
    public String DESCRIPTION;
    public String SORT_ID;
    public int LEVEL;
    public String UPDATE_TIME;
    public String STATUS;
    public String ICON_URL;
    public String PLAYCOUNT;
    public String COMPERE;
    public String SRC_ICON_URL;
    public String CREATE_USER;
    public String UPDATE_USER;
    public String POSITION;
    public String SRC_PLAYCOUNT;
    //标记专辑类型，如收藏，本地，最近，下载
    public String TYPE;
    public LeSortInfo() {}

    public LeSortInfo(String NAME, String TYPE, String SORT_ID) {
        this.NAME = NAME;
        this.SORT_ID = SORT_ID;
        this.TYPE = TYPE;
    }
    @Override
    public String toString() {
        return "LeSortInfo{" +
                "SOURCE_CP_ID='" + SOURCE_CP_ID + '\'' +
                ", SRC_IMG_URL='" + SRC_IMG_URL + '\'' +
                ", NAME='" + NAME + '\'' +
                ", SOURCE_ID=" + SOURCE_ID +
                ", IMG_URL='" + IMG_URL + '\'' +
                ", CREATE_TIME='" + CREATE_TIME + '\'' +
                ", DESCRIPTION='" + DESCRIPTION + '\'' +
                ", PAGE_ID='" + SORT_ID + '\'' +
                ", LEVEL=" + LEVEL +
                ", UPDATE_TIME='" + UPDATE_TIME + '\'' +
                ", STATUS='" + STATUS + '\'' +
                ", ICON_URL='" + ICON_URL + '\'' +
                ", PLAYCOUNT='" + PLAYCOUNT + '\'' +
                ", COMPERE='" + COMPERE + '\'' +
                ", SRC_ICON_URL='" + SRC_ICON_URL + '\'' +
                ", CREATE_USER='" + CREATE_USER + '\'' +
                ", UPDATE_USER='" + UPDATE_USER + '\'' +
                ", POSITION='" + POSITION + '\'' +
                ", SRC_PLAYCOUNT='" + SRC_PLAYCOUNT + '\'' +
                ", TYPE='" + TYPE + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeSortInfo that = (LeSortInfo) o;

        if (NAME != null ? !NAME.equals(that.NAME) : that.NAME != null) return false;
        if (SORT_ID != null ? !SORT_ID.equals(that.SORT_ID) : that.SORT_ID != null) return false;
        return TYPE != null ? TYPE.equals(that.TYPE) : that.TYPE == null;

    }

    @Override
    public int hashCode() {
        int result = NAME != null ? NAME.hashCode() : 0;
        result = 31 * result + (SORT_ID != null ? SORT_ID.hashCode() : 0);
        result = 31 * result + (TYPE != null ? TYPE.hashCode() : 0);
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.SOURCE_CP_ID);
        dest.writeString(this.SRC_IMG_URL);
        dest.writeString(this.NAME);
        dest.writeInt(this.SOURCE_ID);
        dest.writeInt(this.LEVEL);
        dest.writeString(this.IMG_URL);
        dest.writeString(this.CREATE_TIME);
        dest.writeString(this.DESCRIPTION);
        dest.writeString(this.SORT_ID);
        dest.writeString(this.UPDATE_TIME);
        dest.writeString(this.STATUS);
        dest.writeString(this.ICON_URL);
        dest.writeString(this.PLAYCOUNT);
        dest.writeString(this.COMPERE);
        dest.writeString(this.SRC_ICON_URL);
        dest.writeString(this.CREATE_USER);
        dest.writeString(this.UPDATE_USER);
        dest.writeString(this.POSITION);
        dest.writeString(this.SRC_PLAYCOUNT);
        dest.writeString(this.TYPE);
    }

    private LeSortInfo(Parcel dest)
    {
        this.SOURCE_CP_ID = dest.readString();
        this.SRC_IMG_URL = dest.readString();
        this.NAME = dest.readString();
        this.SOURCE_ID = dest.readInt();
        this.LEVEL = dest.readInt();
        this.IMG_URL = dest.readString();
        this.CREATE_TIME = dest.readString();
        this.DESCRIPTION = dest.readString();
        this.SORT_ID = dest.readString();
        this.UPDATE_TIME = dest.readString();
        this.STATUS = dest.readString();
        this.ICON_URL = dest.readString();
        this.PLAYCOUNT = dest.readString();
        this.COMPERE = dest.readString();
        this.SRC_ICON_URL = dest.readString();
        this.CREATE_USER = dest.readString();
        this.UPDATE_USER = dest.readString();
        this.POSITION = dest.readString();
        this.SRC_PLAYCOUNT = dest.readString();
        this.TYPE = dest.readString();
    }

    public static final Creator<LeSortInfo> CREATOR = new Creator<LeSortInfo>()
    {
        public LeSortInfo createFromParcel(Parcel in)
        {
            return new LeSortInfo(in);
        }

        public LeSortInfo[] newArray(int size)
        {
            return new LeSortInfo[size];
        }
    };
}
