package com.letv.leauto.ecolink.http.leradio;

import android.os.Message;

import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.VideoListParameter;
import com.letv.leauto.ecolink.utils.Trace;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * 加载视频专辑下的视频列表
 * Created by kevin on 2016/12/21.
 */
public class VideoListLoader extends BaseLoader{

    /**
     * 加载视频专辑下的视频列表
     * @param albumId 专辑ID
     * @param page    请求的页码
     */
    public void load(String albumId, int page) {
        VideoListParameter parameter = new VideoListParameter(albumId, "0", String.valueOf(page));
        RequestCall call = getRequest(GlobalHttpPathConfig.BASE_URL + GlobalHttpPathConfig.GET_VIDEO_LIST, parameter);
        call.execute(this);
    }

    @Override
    public void onError(Call call, Exception e,int id) {
        Trace.Debug("##### e" + e.toString());
        Message message=mHandler.obtainMessage();
        message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
        mHandler.sendMessage(message);
    }

    @Override
    public void onResponse(final String response,int id) {
        Trace.Debug("response=" + response);
        Message message = Message.obtain();
        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;

        try {
            JSONObject json = new JSONObject(response);
            int code = json.getInt(RESPOSE_CODE);
            if (code != CODE_SUCCESS) {
                message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
            } else {
                message.obj = json.getJSONObject(RESPOSE_DATA);
            }
        }catch (Exception e) {

        }
        mHandler.sendMessage(message);

    }
}
