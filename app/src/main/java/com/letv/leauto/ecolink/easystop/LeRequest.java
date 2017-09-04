package com.letv.leauto.ecolink.easystop;

import android.util.Log;

import com.letv.leauto.ecolink.utils.Trace;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhaochao on 2015/7/10.
 */
public class LeRequest {

    public static String requestByHttpPost(String httpUrl, String body) throws IOException {
        byte[] postData = null;
        Trace.Debug("TAG_POST", "body："+body);
        if(body!=null && body.length()>0) {
            postData = body.getBytes();
        }

        // 新建一个URL对象
        URL url = new URL(httpUrl);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        if(postData!=null) {
            urlConn.setDoInput(true);
            // Post请求必须设置允许输出
            urlConn.setDoOutput(true);
            // Post请求不能使用缓存
            //shimeng add for cach creat time,20160402,begin
            //urlConn.setUseCaches(false);
            urlConn.setUseCaches(true);
            //shimeng add for cach creat time,20160402,end
            // 设置为Post请求
            urlConn.setRequestMethod("POST");
            // 配置请求Content-Type
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        } else {
            urlConn.setRequestMethod("GET");
        }
        urlConn.setInstanceFollowRedirects(true);
        try {
            if(postData!=null) {
                Trace.Debug("TAG_POST", "urlConn："+urlConn);
                // 开始连接
                urlConn.connect();
                // 发送请求参数
                DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
                dos.write(postData);
                dos.flush();
                dos.close();
            }
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                String data = readStream(urlConn.getInputStream(), "UTF-8");
                Trace.Debug("TAG_POST", "Post请求方式成功，返回数据如下：");
                Trace.Debug("TAG_POST", data);
                return data;
            } else {
                Trace.Debug("TAG_POST", "Post方式请求失败");
                return null;
            }
        } catch (Exception e) {
            Trace.Error("TAG_POST", e.toString());
        }
        return null;
    }


    public static String requestForBSN(String httpUrl, String body) throws IOException {
        byte[] postData = null;
        if(body!=null && body.length()>0) {
            postData = body.getBytes();
        }
        // 新建一个URL对象
        URL url = new URL(httpUrl);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        if(postData!=null) {
            // Post请求必须设置允许输出
            urlConn.setDoOutput(true);
            // Post请求不能使用缓存
            //shimeng add for cach creat time,20160402,begin
            //urlConn.setUseCaches(false);
            urlConn.setUseCaches(true);
            //shimeng add for cach creat time,20160402,end
            // 设置为Post请求
            urlConn.setRequestMethod("POST");
            // 配置请求Content-Type
            //urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");
            urlConn.setRequestProperty("X-Token", "j2M9jSWe.4016.zOzzggX3EMCG");
        } else {
            urlConn.setRequestMethod("GET");
        }
        urlConn.setInstanceFollowRedirects(true);
        try {
            if(postData!=null) {
                // 开始连接
                urlConn.connect();
                // 发送请求参数
                DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
                dos.write(postData);
                dos.flush();
                dos.close();
            }
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                String data = readStream(urlConn.getInputStream(), "UTF-8");
                Trace.Debug("TAG_POST", "Post请求方式成功，返回数据如下：");
                Trace.Debug("TAG_POST", data);
                return data;
            } else {
                Trace.Debug("TAG_POST", "Post方式请求失败");
                return null;
            }
        } catch (Exception e) {
            Trace.Error("TAG_POST", e.toString());
        }
        return null;
    }

    public static String readStream(InputStream is, String charsetName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString(charsetName);
    }
}
