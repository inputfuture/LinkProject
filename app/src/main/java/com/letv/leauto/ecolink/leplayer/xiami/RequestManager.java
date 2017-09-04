package com.letv.leauto.ecolink.leplayer.xiami;

import com.google.gson.Gson;
import com.google.gson.JsonElement;


public class RequestManager {

    public RequestManager() {
        mGson = new Gson();
    }

    private static RequestManager instance;

    public static RequestManager getInstance() {
        if (instance == null) {
            instance = new RequestManager();
        }
        return instance;
    }

    private Gson mGson;

    public Gson getGson() {
        return mGson;
    }

    public boolean isResponseValid(XiamiApiResponse response) {
        if (response == null) return false;
        int state = response.getState();
        if (state == 0) {
            JsonElement element = response.getData();
            return !(element == null || element.isJsonNull());
        } else {
            return false;
        }
    }


}
