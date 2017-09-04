package com.letv.leauto.ecolink.net;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Gson 工具类
 * Created by tianwei1 on 2016/3/5.
 */
public class GsonUtils {
    private static Gson mGson;
    private static final String TAG = "GsonUtils";

    /**
     * 实例化Gson
     */
    public static void newInstance() {
        if (mGson == null) {
            synchronized (GsonUtils.class) {
                if (mGson == null) {
                    mGson = new Gson();
                }
            }
        }
    }

    /**
     * 获取Gson单例
     *
     * @return
     */
    public static Gson getDefault() {
        return mGson;
    }

    /**
     * 将Json转化为Bean
     *
     * @param json
     * @param clazz
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonSyntaxException {
        if (mGson != null) {
            T t = mGson.fromJson(json, clazz);
            return t;
        }
        return null;
    }

    /**
     * 将Object转化为Json
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        if (mGson != null) {
            return mGson.toJson(obj);
        }
        return null;
    }
}
