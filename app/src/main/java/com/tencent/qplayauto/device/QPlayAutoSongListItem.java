package com.tencent.qplayauto.device;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by agaochen on 2016/9/26.
 * 注意:Native 调用类，不要随便更改此类的名称及成员名称
 */

public class QPlayAutoSongListItem implements Parcelable {

    //目录或者歌曲的 ID
    public String ID;
    //目录名或者歌曲名
    public String Name;
    //演唱者，如果是目录，则为空
    public String Artist;
    //专辑名，如果是目录，则为空
    public String Album;
    //是否有子目录：1——有；0——没有；
    public int HasChild;
    //是否为歌曲：0——歌曲；
    public int IsSong;
    //歌单父ID
    public String ParentID;
    //Item类型：1——歌曲；2——目录；3——电台(也属于目录)；
    public int Type;

    public QPlayAutoSongListItem() {
    }

    protected QPlayAutoSongListItem(Parcel in) {
        ID = in.readString();
        Name = in.readString();
        Artist = in.readString();
        Album = in.readString();
        HasChild = in.readInt();
        IsSong = in.readInt();
        ParentID = in.readString();
        Type = in.readInt();
    }

    public static final Creator<QPlayAutoSongListItem> CREATOR = new Creator<QPlayAutoSongListItem>() {
        @Override
        public QPlayAutoSongListItem createFromParcel(Parcel in) {
            return new QPlayAutoSongListItem(in);
        }

        @Override
        public QPlayAutoSongListItem[] newArray(int size) {
            return new QPlayAutoSongListItem[size];
        }
    };

    @Override
    public String toString() {
        return "歌名："+this.Name+",歌曲ID："+this.ID+",艺术家："+this.Artist+",专辑："+this.Album+",父ID："+this.ParentID+",是否是歌曲："+this.IsSong+",歌曲类型："+this.Type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ID);
        parcel.writeString(Name);
        parcel.writeString(Artist);
        parcel.writeString(Album);
        parcel.writeInt(HasChild);
        parcel.writeInt(IsSong);
        parcel.writeString(ParentID);
        parcel.writeInt(Type);
    }
}
