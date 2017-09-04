package com.letv.leauto.ecolink.thincar.protocol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.userinfo.LoginManager;
import com.letv.leauto.ecolink.utils.Utils;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.favorcar.contract.LoginContract;
import com.letv.loginsdk.bean.DataHull;
import com.letv.loginsdk.bean.PersonalInfoBean;
import com.letv.loginsdk.network.task.GetResponseTask;
import com.letv.loginsdk.network.volley.VolleyRequest;
import com.letv.loginsdk.network.volley.VolleyResponse;
import com.letv.loginsdk.network.volley.toolbox.SimpleResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2016/11/2.
 */
public class DeviceInfoNotifyHelp {
    private static final String TAG = "DeviceInfoNotifyHelp";
    private static final String PIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String FILE_FORMAT = ".png";
    private static DeviceInfoNotifyHelp ourInstance = new DeviceInfoNotifyHelp();
    private Context mContext;

    //用户保存手机电池电量
    private int phoneBattery = 0;
    private String weathreInfo = "";
    private String limitedNumber = "";
    private String userName = "";

    public static DeviceInfoNotifyHelp getInstance() {
        return ourInstance;
    }

    public void initDeviceInfo(Context context) {
        mContext = context.getApplicationContext();
    }

    private DeviceInfoNotifyHelp() {
    }

    /**
     * 发送蓝牙状态
     */
    public void notifyBlueToothStatus(int value) {
        notifyInformation("NotifyBTStatus","BTStatus",value + "");
    }

    /**
     * 发送网络类型
     */
    public void notifyNetWorkType(String type) {
        notifyInformation("NotifyNetwork","Network",type);
    }

    /**
     * 发送信号强度
     */
    public void notifySignal(int value) {
        notifyInformation("NotifySignal","Signal",value+"");
    }

    /**
     * 发送手机型号
     */
    public void notifyPhoneType(String type) {
        notifyInformation("NotifyPhone","Phone",type);
    }


    /**
     * 收到车机请求电池电量请求
     */
    public void requesetPhoneBattery() {
        sendPhoneBattery();
    }

    /**
     * 发送手机电量
     */
    public void notifyPhoneBattery(int value) {
        if (phoneBattery == 0) {//第一次发送电量
            phoneBattery = value;
            sendPhoneBattery();
            return;
        }
        if (Math.abs(phoneBattery - value) >= 10) {
            phoneBattery = value;
            sendPhoneBattery();
            //notifyInformation("NotifyBattery","Battery",phoneBattery / 10);
        }
    }

    private void sendPhoneBattery() {
        Map<String,Object> map = new HashMap<>();
        map.put("Type", ThinCarDefine.Interface_Notify);
        map.put("Method","NotifyBattery");

        Map<String,Object> btstatus = new HashMap<>();
        btstatus.put("Battery",phoneBattery / 10);

        map.put("Parameter",btstatus);
        JSONObject object = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.DEVICE_INFO_APPID,object);;
    }

    /**
     * 收到车机请求欢迎页信息
     */
    public void requestWelcomInfo() {
        requestAccountInfo();
        Map<String,Object> map = new HashMap<>();
        map.put("Type", "Interface_Notify");
        map.put("Method","NotifyWelcomeInfo");

        Map<String,Object> content = new HashMap<>();
        content.put("Weather", weathreInfo);
        if (limitedNumber.equals(R.string.str_unlimit_city)){//不限行城市，不发送

        } else if(limitedNumber.equals(R.string.str_unlimit_today)){//今日不限行，发送
            content.put("LimitedNumber","");
        }else {//限行，发送
            content.put("LimitedNumber",limitedNumber);
        }
        //content.put("UserName",userName);

        map.put("Parameter",content);
        JSONObject object = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.WELCOME_PAGE_APPID,object);
    }

    /**
     * 发送天气信息
     */
    public void notifyWeather(String value) {
        weathreInfo = value;
        //notifyInformation("NotifyWeather","Weather",value);
    }

    /**
     * 发送限号信息
     */
    public void notifyLimitedNumber(String value) {
        limitedNumber = value;
        //notifyInformation("NotifyLimitedNumber","LimitedNumber",value);
    }

    /**
     * 发送帐户信息
     */
    public void requestAccountInfo() {
        if(LoginContract.isLogin()) {
            String userNickName = LoginContract.getNickname();
            if (!TextUtils.isEmpty(userNickName)) {
                notifyAccountInfo();
                sendAccountPic();
            } else {
                checkUpdate(mContext);
            }
        }
    }

    public void notifyAccountInfo() {
        String userNickName = LoginContract.getNickname();
        if (!TextUtils.isEmpty(userNickName)) {
            LogUtils.i(TAG,"notifyAccountInfo userNickName:" + userNickName);
            userName = userNickName;
            notifyUserName(userNickName);
        }
    }

    private void sendAccountPic() {
        new Thread() {

            @Override
            public void run() {
                String headPicUrl = LoginContract.getHeadPicUrl();
                if (!TextUtils.isEmpty(headPicUrl.trim())) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        LogUtils.i(TAG,"notifyAccountInfo headPicUrl:" + headPicUrl);
                        Request request = new Request.Builder().url(headPicUrl).build();
                        ResponseBody body = client.newCall(request).execute().body();
                        InputStream in = body.byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        saveAndSendPic(Utils.resizeImage(bitmap,40,40));
                    } catch (Exception e) {
                        LogUtils.i(TAG,"notifyAccountInfo exception happened:" + e);
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

    private void saveAndSendPic(Bitmap bitmap) {
        File sysFile = new File(PIC_PATH);
        File filePic = new File(sysFile, "1" + FILE_FORMAT);

        LogUtils.i(TAG, "responsAlbumPic file name:" + filePic.getName());
        if (filePic.exists()) {
            filePic.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.flush();
            out.close();

            responsAlbumPic(filePic);
            filePic.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回图片数据
     */
    private void responsAlbumPic(final File file) {
        try {
            String filename = file.getName().replace(FILE_FORMAT, "");
            long name = Long.parseLong(filename);
            DataSendManager.getInstance().subpackageSendData(ThinCarDefine.ProtocolAppId.USER_ACCOUNT_APPID,Utils.convertFileToBytes(file),
                    DataSendManager.DATA_TYPE_PICTURE,name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知车机用户退出登录
     */
    public void notifyUserLoginOut() {
        Map<String,Object> map = new HashMap<>();
        map.put("Type", "Interface_Notify");
        map.put("Method", "NotifyUserLogout");
        map.put("Parameter",null);

        JSONObject object = (JSONObject) JSON.toJSON(map);
        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.USER_ACCOUNT_APPID,object);
    }

    /**
     * 发送用户名
     */
    private void notifyUserName(String name) {
        Map<String,Object> map = new HashMap<>();
        map.put("Type","Interface_Notify");
        map.put("Method","NotifyUserName ");

        Map<String,Object> content = new HashMap<>();
        content.put("UserName",name);

        map.put("Parameter",content);
        JSONObject object = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.USER_ACCOUNT_APPID,object);
    }

    private void notifyInformation(String method,String parameter,String value) {
        Map<String,Object> map = new HashMap<>();
        map.put("Type", ThinCarDefine.Interface_Notify);
        map.put("Method",method);

        Map<String,Object> content = new HashMap<>();
        content.put(parameter,value);

        map.put("Parameter",content);
        JSONObject object = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.DEVICE_INFO_APPID,object);
    }

    /**
     * 发送日期时间给车机
     */
    public void notifyCurrentTime() {
        Map<String,Object> map = new HashMap<>();
        map.put("Type","Interface_Notify");
        map.put("Method","NotifyTime");

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        Map<String,Object> content = new HashMap<>();
        content.put("Year",c.get(Calendar.YEAR));
        content.put("Month",c.get(Calendar.MONTH) + 1);
        content.put("Day",c.get(Calendar.DAY_OF_MONTH));
        content.put("Hour",c.get(Calendar.HOUR_OF_DAY));
        content.put("Minute",c.get(Calendar.MINUTE));
        content.put("Second",c.get(Calendar.SECOND));

        map.put("Parameter",content);
        JSONObject object = (JSONObject) JSON.toJSON(map);
        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.DEVICE_INFO_APPID,object);
    }

    private void checkUpdate(final Context context) {
        GetResponseTask.getGetResponseTaskInstance().getUserInfoByUid(LoginManager.getUid(context), new SimpleResponse<PersonalInfoBean>() {
            @Override
            public void onCacheResponse(VolleyRequest<PersonalInfoBean> request, PersonalInfoBean result, DataHull hull, VolleyResponse.CacheResponseState state) {
                if (state == VolleyResponse.CacheResponseState.SUCCESS) {
                    String nickName = result.getNickname();
                    String headPicUrl = result.getPicture200x200();

                    if (!nickName.equals(LoginContract.getNickname())) {
                        LoginContract.setNickName(nickName);
                    }

                    notifyAccountInfo();
                    if (!headPicUrl.equals(LoginContract.getHeadPicUrl())) {
                        LoginContract.setHeadPicUrl(headPicUrl);
                    }

                   sendAccountPic();
                }
            }
        });
    }
}