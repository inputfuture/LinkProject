package com.letv.leauto.ecolink.utils;

import android.os.Handler;
import android.util.Log;

import com.letv.leauto.ecolink.easystop.JSonUtil;
import com.letv.leauto.ecolink.easystop.LeRequest;
import com.letv.leauto.ecolink.easystop.SignUtil;
import com.letv.leauto.ecolink.ui.fragment.EasyStopFragment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by zhuyanbo on 16-8-22.
 */
public class EasyStopDetailThread {

    private final String TAG = "EasyStopThread";
    private volatile static EasyStopDetailThread sEasyStopDetailThread;
    private EasyStopThread mThread;
    private Handler handler;

    private EasyStopDetailThread(){
        if(mThread == null){
            mThread = new EasyStopThread();
        }
    }

    public static EasyStopDetailThread getInstance(){
        if(sEasyStopDetailThread == null){
            synchronized (EasyStopDetailThread.class){
                if(sEasyStopDetailThread == null){
                    sEasyStopDetailThread = new EasyStopDetailThread();
                }
            }
        }
        return sEasyStopDetailThread;
    }

    public void setThreadHandler(Handler handler){
        this.handler = handler;
    }

    public void startSearchPark(double latitude, double longtitude, int scope){
        if(mThread!=null){
            mThread = null;
        }
        mThread = new EasyStopThread();
        mThread.setLocation(latitude, longtitude, scope);
        mThread.start();
    }


    class EasyStopThread extends Thread{


        private double latitude,longtitude;
        private int scope = 1000;

        void setLocation(double latitude, double longtitude, int scope){
            this.latitude = latitude;
            this.longtitude = longtitude;
            this.scope = scope;
        }

        @Override
        public void run() {
            try{
                String result = getPostResData();
                if(handler!=null){
                    handler.obtainMessage(EasyStopFragment.RECEIVE_DETAIL, result).sendToTarget();
                }
            }catch (Exception ex){

            }
        }

        private String getPostResData() {
            String key = "86db820d50434248ad98a31965554bcf";  //--开发者秘钥
            SortedMap<String, String> map = new TreeMap<String, String>();
            map.put("signType", "md5");
            map.put("partner", "6620efbbab0f42a4b12dec251537e257");
            map.put("service", "mgr.park.getParkInfoList");
            map.put("charset", "utf-8");
            map.put("version", "1.0");
            map.put("scope", ""+scope);
            map.put("longitude", ""+longtitude);
            map.put("latitude", ""+latitude);
//            map.put("limit", "2");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String buildTimeStr = sdf.format(new Date());
            map.put("timestamp", buildTimeStr);
        /*签名*/
            SignUtil signUtil = new SignUtil();
            String sign = signUtil.md5Sign(map, key, "UTF-8");
            Trace.Info(TAG, "sign=" + sign);
            String url = "http://api.tingjiandan.com/openapi/gateway"; //--测试服务器开发平台访问地址
            map.put("sign", sign);
            LeRequest httpClient = new LeRequest();
            String result = null;
            try {
                Trace.Info(TAG, "map=" + map);
                JSONObject jsonObject= JSonUtil.obj2Json(map);
                Trace.Info(TAG, "jsonObject=" + jsonObject);
                result = httpClient.requestByHttpPost(url, jsonObject.toString());//   sendRequestByPost(url, JsonUtil.objectToJson(map));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Trace.Info(TAG, "result=" + result);
            return result;
        }
    }
}
