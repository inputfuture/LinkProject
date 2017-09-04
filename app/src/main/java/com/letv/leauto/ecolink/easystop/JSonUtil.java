package com.letv.leauto.ecolink.easystop;

import com.letv.leauto.ecolink.utils.Trace;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * JSON工具类
 */
public class JSonUtil {

    /**
     * JAVA数组对象转换成JSON字符串
     *
     * @param list JAVA数组对象
     * @return JSON字符串
     * @throws Exception
     */
    public static String obj2Json(List<Class<?>> list) throws Exception {
        if (list == null || list.size() == 0) {
            return "{}";
        }
        StringBuilder jsonString = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                jsonString.append(",");
            }
            Class<?> cla = list.get(i);
            jsonString.append(cla);
        }
        return jsonString.toString();
    }

    /**
     * JAVA集合对象转换成JSON字符串
     *
     * @param map JAVA集合对象
     * @return JSON字符串
     * @throws Exception
     */
    public static JSONObject obj2Json(Map<String, String> map) throws Exception {
        if (map == null || map.size() == 0) {
            return null;
        }
        JSONObject queryper = new JSONObject();
        StringBuilder jsonString = new StringBuilder();
        Set<String> keySet = map.keySet();
        boolean isFirst = true;
        for (String key : keySet) {
            Trace.Debug("JSonUtil", "key :=" + key);
            queryper.put(key, map.get(key));
        }
        return queryper;
    }

}