package com.letv.leauto.ecolink.net;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by tianwei on 16/3/22.
 */
public interface PostCallBack {
    void onFailure(Call call, IOException e);

    void onResponse(Call call, Response response) throws IOException;

    void onError(int errorCode);
}
