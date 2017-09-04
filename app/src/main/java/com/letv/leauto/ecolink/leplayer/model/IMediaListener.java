package com.letv.leauto.ecolink.leplayer.model;

/**
 * Created by zhaochao on 2015/8/10.
 */
public interface IMediaListener {
    void onCompletion(ILePlayer player);
    void onPrepared(ILePlayer player);
    boolean onError(ILePlayer player, int what, int extra);
}
