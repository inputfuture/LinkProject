package com.letv.leauto.ecolink.thincar.ota;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.ota.OtaThincarUtils;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.utils.HashUtils;
import com.letv.leauto.ecolink.utils.LeSignature;
import com.letv.leauto.ecolink.utils.Trace;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/30.
 */
public class OtaUtils {

    public static final String FilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.ecolink.ota/";
    //OTAcheck路径
    private static final String OTA_CHECK_UPDATA = "http://test.tvapi.letv.com/api/v2/lecar/upgradeProfile";
    public static final String _AK = "ak_j7riPS3hvMxzApPjCXol";
    public static final String _SK = "sk_kIo27lGerVXS7t8KL5lN";

    private static File Otafile;

    public static OtaEntity mOtaEntity;
    private static long startTime;

    //车机OTA数据库对应的数据
    //checkOTA(carDB, context,mac, version, mdole);
    public static void checkOTA(final Context context, final String carSN, final String modle, final String versionCode) {

        Map<String, String> maps = new HashMap<>();
        maps.put("deviceId", carSN);//设备mac地址
        maps.put("deviceType", "lecar");
        maps.put("model", modle);//设备型号
        maps.put("versionCode", versionCode);//设备当前版本号
        maps.put("versionType", "0");//0位测试版本，1位开发版
        maps.put("language", "CN");//语言
        long time = System.currentTimeMillis();

        String url = OTA_CHECK_UPDATA +
                "?_ak=" + _AK +
                "&deviceId=" + carSN +
                "&deviceType=" + "lecar" +
                "&model=" + modle +
                "&_sign=" + LeSignature.getSignature(_AK, _SK, maps, time) +
                "&_time=" + time +
                "&versionCode=" + versionCode +
                "&versionType=" + "0" +
                "&language=" + "CN";

        Trace.Debug("thincar",url);

        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int value) {
                String vercode="V5101RCN01C001001B03271S";
                final ThinCarDBImpl db = ThinCarDBImpl.getInstance(context);
                final List<OtaEntity> otaBeans = db.isExists(vercode);
                if (otaBeans.size()==0){

                    Trace.Debug("thincar", "ota网络请求失败：" + e.toString());
                    mOtaEntity  = new OtaEntity();
                    mOtaEntity.setCarVersion("V5101RCN01C001001B03271S");
                    mOtaEntity.setDownUrl("http://g3.letv.cn/267/47/26/scloud_beta/0/upload/tmp/scloud_ota_1499744949_18957320.zip?b=123456&platid=5&splatid=500");
                    mOtaEntity.setMd5("49ab6b5cc16620c285933e7053e935c6");
                    mOtaEntity.setFilePath(FilePath);
                    mOtaEntity.setFileName("001.001_S0327");
                    mOtaEntity.setDownStatus(0);
                    mOtaEntity.setPkgSize(74489782);
                    mOtaEntity.setMessage("\"更新说明:1.更新了导航；\",\n" +
                            "            \"2.优化了LeRadio;\",\n" +
                            "            \"3.优化了UI;\",\n" +
                            "            \"4.更新了蓝牙;\"");
                    mOtaEntity.setTime("2017-03-27");
                    db.insertOtaEntity(mOtaEntity);

                    sendOtaUpdata(mOtaEntity,context,true);
                }else{
                    mOtaEntity=otaBeans.get(0);
                    File file=new File(mOtaEntity.filePath,mOtaEntity.fileName);
                    if (file.exists()){
                        sendOtaUpdata(mOtaEntity,context,true);
                    }else {
                        db.deleteOtaEntity(mOtaEntity.getCarVersion());
                        mOtaEntity  = new OtaEntity();
                        mOtaEntity.setCarVersion("V5101RCN01C001001B03271S");
                        mOtaEntity.setDownUrl("http://g3.letv.cn/267/47/26/scloud_beta/0/upload/tmp/scloud_ota_1499744949_18957320.zip?b=123456&platid=5&splatid=500");
                        mOtaEntity.setMd5("49ab6b5cc16620c285933e7053e935c6");
                        mOtaEntity.setFilePath(FilePath);
                        mOtaEntity.setFileName("001.001_S0327");
                        mOtaEntity.setDownStatus(0);
                        mOtaEntity.setPkgSize(74489782);
                        mOtaEntity.setMessage("\"更新说明:1.更新了导航；\",\n" +
                                "            \"2.优化了LeRadio;\",\n" +
                                "            \"3.优化了UI;\",\n" +
                                "            \"4.更新了蓝牙;\"");
                        mOtaEntity.setTime("2017-03-27");
                        db.insertOtaEntity(mOtaEntity);
                        sendOtaUpdata(mOtaEntity,context,false);
                    }

                }
            }

            @Override
            public void onResponse(String response,int value) {
                Trace.Debug("thincar", "网络请求成功返回的信息：" + response);
                JSONObject data = JSON.parseObject(response);
                int code = data.getInteger("errno");
                if (code == 10000) {
                    final JSONObject jsonObject = data.getJSONObject("data");
                    String vercode = jsonObject.getString("versionCode");
                    final ThinCarDBImpl db = ThinCarDBImpl.getInstance(context);
                    final List<OtaEntity> otaBeans = db.isExists(vercode);

                    Trace.Debug("thincar",otaBeans.size()+";"+vercode);
                    if (otaBeans.size() == 0) {//有升级包，但是本地还没有

                        mOtaEntity = new OtaEntity();
                        mOtaEntity.setCarVersion(vercode);
                        mOtaEntity.setDownUrl(jsonObject.getString("pkgUrl"));
                        mOtaEntity.setMd5(jsonObject.getString("pkgMd5"));
                        mOtaEntity.setFilePath(FilePath);
                        mOtaEntity.setFileName(jsonObject.getString("title"));
                        mOtaEntity.setDownStatus(0);
                        mOtaEntity.setPkgSize(jsonObject.getInteger("pkgsize"));
                        LogUtils.i("TAG", mOtaEntity.toString());
                        db.insertOtaEntity(mOtaEntity);
                    } else {//升级包已有
                        mOtaEntity=otaBeans.get(0);

                    }

                } else {
                    Trace.Debug("thincar", "网络请求成功返回的消息：" + data.getString("errmsg"));
                }

            }
        });
    }


    public static void startOtaDownload(final Context context){
        Trace.Debug("thincar","entity "+mOtaEntity.toString());
        if (mOtaEntity.getDownStatus() == DownType.DOWN_SUCCESS) {
            sendOtaUpdata(mOtaEntity,context,true);

        }else{


            DownFile(mOtaEntity, context);

        }
    }
    public static void DownFile(final OtaEntity entity, final Context context) {

        startTime=System.currentTimeMillis();

        OkHttpUtils.get().url(entity.getDownUrl()).build().execute(new FileCallBack(entity.getFilePath(), entity.getFileName()) {
            @Override
            public void inProgress(float progress, long total , int id) {


                long endTime=System.currentTimeMillis();
                if ((endTime-startTime)>1000){
                    int finalProgress= (int) (100*progress);

                    startTime=endTime;
                    Trace.Debug("thincar"," inprogress="+finalProgress);
                    entity.setDownStatus(DownType.DOWN_DOWNING);
                    entity.setProgress(finalProgress);
                    ThinCarDBImpl db = ThinCarDBImpl.getInstance(context);
                    db.updataOtaEntity(entity);

                    sendOtaUpdata(entity,context,false);

                }
            }

            @Override
            public void onError(Call call, Exception e,int code) {

            }

            @Override
            public void onResponse(File response,int code) {
                LogUtils.i("TAG", "File: " + response.toString());

                try {
                    String md5 = HashUtils.getMd5ByFile(response);
                    if (md5.equals(entity.getMd5())) {
                        entity.setDownStatus(DownType.DOWN_SUCCESS);
                        entity.setProgress(100);
                        ThinCarDBImpl db = ThinCarDBImpl.getInstance(context);
                        db.updataOtaEntity(entity);
                    }
                    if(HomeActivity.isThinCar){
                        sendOtaUpdata(entity, context,false);
                    }
                    LogUtils.i("TAG", "下载数据库内容: " + entity.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //发送升级列表
    public static void sendOtaUpdata(OtaEntity entity, Context context,boolean first) {
        Map<String, Object> map = new HashMap<>();
        map.put("NAME", entity.getFileName());
        map.put("VERSION", entity.getCarVersion());
        map.put("START", 0);
        map.put("COUNT", entity.getPkgSize());
        if (entity.getDownStatus()==DownType.UN_DOWNLOAD )
        {
            map.put("NAME", entity.getFileName());
            map.put("VERSION", entity.getCarVersion());
            map.put("START", 0);
            map.put("COUNT", entity.getPkgSize());
            map.put("RFLAG",2);
            map.put("UAPDATE_CONTENTS",entity.message);
            map.put("DATE",entity.time);
            map.put("STATUS",entity.downStatus);
            map.put("DOWNLOAD_PROGRESS",entity.progress);
        }else if (entity.getDownStatus()==DownType.DOWN_FAIL){
            map.put("RFLAG",2);
            map.put("STATUS",entity.downStatus);
        }else if (entity.getDownStatus()==DownType.DOWN_DOWNING){

           if (first){
               map.put("NAME", entity.getFileName());
               map.put("VERSION", entity.getCarVersion());
               map.put("START", 0);
               map.put("COUNT", entity.getPkgSize());
               map.put("UAPDATE_CONTENTS",entity.message);
               map.put("DATE",entity.time);
           }
            map.put("RFLAG",1);
            map.put("STATUS",entity.downStatus);
            map.put("DOWNLOAD_PROGRESS",entity.progress);
        }else if (entity.getDownStatus()==DownType.DOWN_SUCCESS){
            if (first){
                map.put("NAME", entity.getFileName());
                map.put("VERSION", entity.getCarVersion());
                map.put("START", 0);
                map.put("COUNT", entity.getPkgSize());
                map.put("UAPDATE_CONTENTS",entity.message);
                map.put("DATE",entity.time);
            }
            map.put("RFLAG",1);
            map.put("STATUS",entity.downStatus);
            map.put("DOWNLOAD_PROGRESS",entity.progress);
        }

        JSONObject jsonObject = new JSONObject(map);

        JSONArray array=new JSONArray();
        array.add(jsonObject);
        String content = array.toString();

        DataSendManager.getInstance().sendToCarInfo(OtaThincarUtils.sendOtaList(content.getBytes()));
    }

    //大小转化
    private static String Size2M(int size) {

        DecimalFormat df=new DecimalFormat(".##");
        float str = size / 1024f;
        if (str >= 1024) {
            return df.format(str /1024f) + "M";
        }
        return df.format(str /1024f) + "K";
    }

    //获取包名
    private static String getOtaName(String url) {
        int index = url.lastIndexOf("/");
        int index1 = url.lastIndexOf("?");
        return url.substring(index + 1, index1);
    }


    private static boolean isread = false;

    //给车机发送Ota文件
    public static void sendOtaZip(final Context context,final String version, final int start, final int count) {
        new Thread() {

            @Override
            public void run() {
                Trace.Debug("thincar","发送数据");
                RandomAccessFile raf;
                if (Otafile == null) {
                    ThinCarDBImpl db = ThinCarDBImpl.getInstance(context);
                    List<OtaEntity> _otaentity = db.isExists(version);
                    OtaEntity entity = _otaentity.get(0);
                    Otafile = new File(entity.getFilePath(), entity.getFileName());
                }

                if (!Otafile.exists()) {
                    LogUtils.i("TAG", "升级文件不存在");
                    return;
                }

                if (Otafile == null || !Otafile.exists()) {
                    LogUtils.i("TAG", "OTAFAIL文件不存在");
                    ThinCarDBImpl db = ThinCarDBImpl.getInstance(context);//删除数据库记录
                    db.deleteOtaEntity(version);
                    return;
                }
                try {

                    raf = new RandomAccessFile(Otafile, "r");
                    byte[] buff = new byte[count];
                    raf.seek((long) start);
                    raf.read(buff);
                    raf.close();
//            dealWith(buff);
                    subpackageSendData(context,buff);
//                    LogUtils.i("TAG","传输文件成功");
//                    limitNextBytes(buff.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 内部分包发送数据
     *
     */
    public static void subpackageSendData(Context context,byte[] data) {
        int sendblockMax = 4096 - 21 - 24;
        int sendRequestSize = 0;
        int leftBytes = 0;
        Trace.Debug("thincar","发送数据");
        if (data.length <= sendblockMax) {
            DataSendManager.getInstance().sendToCarInfo(OtaThincarUtils.sendOtaZip(data,data.length));
            try {
                Thread.sleep(50);
            } catch (Exception e) {

            }
        } else {
            leftBytes = data.length;
            while (leftBytes > 0) {
                if (leftBytes > sendblockMax) {
                    sendRequestSize = sendblockMax;
                } else {
                    sendRequestSize = leftBytes;
                }

                byte[] buff = new byte[sendRequestSize];
                System.arraycopy(data, data.length - leftBytes, buff, 0, sendRequestSize);
                DataSendManager.getInstance().sendToCarInfo(OtaThincarUtils.sendOtaZip(buff,buff.length));
                leftBytes = leftBytes - sendRequestSize;
                try {
                    Thread.sleep(50);
                } catch (Exception e) {

                }
            }
        }
    }

    public static void sendNoWifiState(Context context) {
        Map<String, Object> map = new HashMap<>();

        map.put("RFLAG",2);

        JSONObject jsonObject = new JSONObject(map);
        JSONArray array=new JSONArray();
        array.add(jsonObject);
        String content = array.toString();

        DataSendManager.getInstance().sendToCarInfo(OtaThincarUtils.sendWifiReq(content.getBytes()));

    }
}
