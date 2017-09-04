package com.letv.leauto.ecolink.event;

import java.util.Observable;

/**
 * Created by Jerome on 2017/4/27.
 * 互联车机 连接状态观察者
 */

public class LinkCarConnectStatusObservable extends Observable {

    public final static int CONNECT = 0x01;
    public final static int DIS_CONNECT = 0x02;

    public void setLinkConnected(int connectStatus){
        setChanged();
        notifyObservers(connectStatus);
    }

}
