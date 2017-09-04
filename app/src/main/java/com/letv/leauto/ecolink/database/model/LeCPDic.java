package com.letv.leauto.ecolink.database.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaochao on 2015/7/2.
 */
public class LeCPDic implements Parcelable {

    public String SOURCE_CP_ID;
    public String CREATE_TIME_BAK;
    public String NAME;
    public String ALIAS_NAME;
    public String CREATE_TIME;
    public String DESCRIPTION;
    public String MEDIA_CHANNEL_ID;
    public String MEDIA_SOURCE_ID;
    public String UPDATE_TIME;
    public int STATUS;
    public String PHONE;
    public String API;
    public String LINKMAN;
    public String CREATE_USER;
    public String UPDATE_USER;
    public String UPDATE_TIME_BAK;
    public String MODULE_KEY;

    public LeCPDic() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.SOURCE_CP_ID);
        dest.writeString(this.CREATE_TIME_BAK);
        dest.writeString(this.NAME);
        dest.writeString(this.ALIAS_NAME);
        dest.writeString(this.CREATE_TIME);
        dest.writeString(this.DESCRIPTION);
        dest.writeString(this.MEDIA_CHANNEL_ID);
        dest.writeString(this.MEDIA_SOURCE_ID);
        dest.writeString(this.UPDATE_TIME);
        dest.writeInt(this.STATUS);
        dest.writeString(this.PHONE);
        dest.writeString(this.API);
        dest.writeString(this.LINKMAN);
        dest.writeString(this.CREATE_USER);
        dest.writeString(this.UPDATE_USER);
        dest.writeString(this.UPDATE_TIME_BAK);
        dest.writeString(this.MODULE_KEY);
    }

    private LeCPDic(Parcel dest)
    {
        this.SOURCE_CP_ID = dest.readString();
        this.CREATE_TIME_BAK = dest.readString();
        this.NAME = dest.readString();
        this.ALIAS_NAME = dest.readString();
        this.CREATE_TIME = dest.readString();
        this.DESCRIPTION = dest.readString();
        this.MEDIA_CHANNEL_ID = dest.readString();
        this.MEDIA_SOURCE_ID = dest.readString();
        this.UPDATE_TIME = dest.readString();
        this.STATUS = dest.readInt();
        this.PHONE = dest.readString();
        this.API = dest.readString();
        this.LINKMAN = dest.readString();
        this.CREATE_USER = dest.readString();
        this.UPDATE_USER = dest.readString();
        this.UPDATE_TIME_BAK = dest.readString();
        this.MODULE_KEY = dest.readString();
    }

    public static final Creator<LeCPDic> CREATOR = new Creator<LeCPDic>()
    {
        public LeCPDic createFromParcel(Parcel in)
        {
            return new LeCPDic(in);
        }

        public LeCPDic[] newArray(int size)
        {
            return new LeCPDic[size];
        }
    };
}
