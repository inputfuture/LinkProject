package com.letv.leauto.ecolink.leplayer.mixMedia;

/**
 * Created by zhaochao on 2015/8/10.
 */
public interface IMListener {
    void onCompletion(IMPlayer player);
    void onPrepared(IMPlayer player);
    boolean onError(IMPlayer player, int what, int extra);
}
