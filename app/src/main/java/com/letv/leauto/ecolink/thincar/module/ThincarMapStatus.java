package com.letv.leauto.ecolink.thincar.module;

import com.autonavi.ae.route.model.TmcBarItem;

/**
 * Created by Administrator on 2017/7/25.
 */

public class ThincarMapStatus {
    private int mStatus;
    private int mLength;

    public ThincarMapStatus(int status,int length) {
        this.mStatus = status;
        this.mLength = length;
    }

    public int getStatus() {
        return this.mStatus;
    }

    void setStatus(int status) {
        this.mStatus = status;
    }

    public int getLength() {
        return this.mLength;
    }

    void setLength(int length) {
        this.mLength = length;
    }
}
