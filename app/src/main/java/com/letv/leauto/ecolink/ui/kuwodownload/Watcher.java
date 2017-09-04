package com.letv.leauto.ecolink.ui.kuwodownload;

import java.util.Observable;
import java.util.Observer;

public abstract class Watcher implements Observer {
    @Override
      public void update(Observable observable, Object data) {
        //通知子类观察者数据改变
        ontifyDownloadDataChange();
    }

    public abstract void ontifyDownloadDataChange();
}
