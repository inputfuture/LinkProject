package com.letv.leauto.ecolink;

import android.content.Context;

import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.qplay.QPlayer;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.controller.EcoTTSController;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by zhaochao on 2015/7/15.
 * 用于要全局使用的对象，统一放在此类中。此类在应用中唯一
 */
public class LeGlob {

    private Context ctx;
    private LePlayer lePlayer;
    private EcoTTSController ttsHandlerController;
    private CacheUtils cacheUtils;
    private QPlayer qPlayer;


    public LeGlob(Context context) {
        this.ctx = context.getApplicationContext();
    }

    public LePlayer getPlayer() {
        if (this.lePlayer == null) {
            this.lePlayer = new LePlayer(this.ctx);
        }
        return this.lePlayer;
    }


    public QPlayer getqPlayer(){
//        if (this.qPlayer == null) {
//            this.qPlayer = new QPlayer(this.ctx);
////            qPlayer.openServiceIfNeed();
//            Trace.Debug("####### openservice");
//        }
//        return this.qPlayer;
        return  null;
    }
    public CacheUtils getCache() {
        if (this.cacheUtils == null) {
            this.cacheUtils = cacheUtils.getInstance(this.ctx);
        }
        return this.cacheUtils;
    }

    public EcoTTSController getTtsController() {
        if (this.ttsHandlerController == null) {
            this.ttsHandlerController = EcoTTSController.getInstance(this.ctx);
        }
        return this.ttsHandlerController;
    }

}
