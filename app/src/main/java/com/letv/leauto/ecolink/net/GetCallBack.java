package com.letv.leauto.ecolink.net;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Http请求回调
 * Created by tianwei1 on 2016/3/4.
 */
public interface GetCallBack {
    void onFailure(Call call, IOException e);

    void onResponse(Call call,Response response) throws IOException;

    void onError(Response response);
}
