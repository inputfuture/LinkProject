package com.letv.leauto.ecolink.leplayer.xiami;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiami.core.exceptions.AuthExpiredException;
import com.xiami.core.exceptions.ResponseErrorException;
import com.xiami.sdk.XiamiSDK;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by shizhao.czc on 2015/5/6.
 */
public abstract class RequestSongTask extends AsyncTask<HashMap<String, Object>, Long, OnlineSong> {

    private RequestManager requestManager;
    private Context context;

    public RequestSongTask(Context context) {
        this.context = context;
        requestManager = RequestManager.getInstance();
    }

    public abstract void postInBackground(OnlineSong onlineSong);

    @Override
    public OnlineSong doInBackground(HashMap<String, Object>... params) {
        try {
            HashMap<String, Object> param = params[0];
            String result = XiamiSDK.xiamiSDKRequest(RequestMethods.METHOD_SONG_DETAIL, param);
            if (!TextUtils.isEmpty(result)) {
                Gson gson = requestManager.getGson();
                XiamiApiResponse response = gson.fromJson(result, XiamiApiResponse.class);
                if (requestManager.isResponseValid(response)) {
                    OnlineSong onlineSong = gson.fromJson(response.getData(), OnlineSong.class);
                    if (onlineSong != null) {
                        postInBackground(onlineSong);
                        return onlineSong;
                    } else {
                        return null;
                    }
                } else return null;
            } else {
                return null;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (AuthExpiredException e) {
            e.printStackTrace();
            return null;
        } catch (ResponseErrorException e) {
            return null;
        }
    }

}
