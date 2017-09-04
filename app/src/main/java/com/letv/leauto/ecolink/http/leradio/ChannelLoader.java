package com.letv.leauto.ecolink.http.leradio;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.ui.leradio_interface.data.ChannelListModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.LeRadioBaseModel;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.ChannelDynamicParameter;
import com.letv.leauto.ecolink.utils.Trace;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * 加载所有栏目
 * Created by kevin on 2016/12/21.
 */
public class ChannelLoader extends BaseLoader {

    public ChannelLoader(Context ctx, Handler handler) {
        super(ctx, handler);
    }

    public void load() {
        ChannelDynamicParameter parameter = new ChannelDynamicParameter();
        RequestCall call = getRequest(GlobalHttpPathConfig.BASE_URL + GlobalHttpPathConfig.GET_CHANNEL, parameter.combineParams());
        call.execute(this);
    }

    @Override
    public void onError(Call call, Exception e,int id) {
        Trace.Debug("##### e" + e.toString());
        Message message=mHandler.obtainMessage();
        message.what = MessageTypeCfg.MSG_CHANNEL;
        mHandler.sendMessage(message);
    }

    @Override
    public void onResponse(final String response,int id) {
        Trace.Debug("response=" + response);
        Message message = mHandler.obtainMessage();
        message.what = MessageTypeCfg.MSG_CHANNEL;

        try {
            JSONObject json = new JSONObject(response);
            int code = json.getInt(RESPOSE_CODE);
            if (code != CODE_SUCCESS) {
                message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
            } else if (!json.has(LeRadioBaseModel.MEDIA_LIST_DATA)) {

            } else {
                JSONObject object = json.getJSONObject(LeRadioBaseModel.MEDIA_LIST_DATA);
                ChannelListModel list = ChannelListModel.parse(object);
                message.obj = list.channels;
            }
        }catch (Exception e) {

        }
        mHandler.sendMessage(message);

    }
}
