package com.letv.leauto.ecolink.easystop;

import android.util.Log;

import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by shimeng on 14/5/10.
 */
public class SignUtil {
    private static String TAG = "SignUtil";

    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     * 待签名字符串直接拼接秘钥
     */
    public static String md5Sign(Map<String, String> params, String key, String encode) {
        String signStr = getSignStr(params) + key;//待签名字符串
        String sign = MD5Crypto.MD5Encode(signStr, encode);
        Trace.Debug("创建Md5签名，待签名字符串：{}", signStr+",sign:{}"+ sign);
        return sign;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * 空值、sign和sign_type不参与签名
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String getSignStr(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (/*StringUtils.isBlank(value) || */key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("signType")) {
                continue;
            }
            sb.append(key).append("=").append(value).append("&");
        }
        sb.deleteCharAt(sb.lastIndexOf("&"));
        return sb.toString();
    }



}
