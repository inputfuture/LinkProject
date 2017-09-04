package com.letv.leauto.ecolink.http.leradio;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.ui.leradio_interface.data.LeRadioBaseModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.SearchListModel;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.SearchParameter;
import com.letv.leauto.ecolink.utils.Trace;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by kevin on 2016/12/22.
 */
public class SearchLoader extends BaseLoader {

    public SearchLoader(Context ctx, Handler handler) {
        super(ctx, handler);
    }

    public void load(String wd) {
        SearchParameter parameter = new SearchParameter(wd).builder(SearchParameter.KEY_DT, "9");
        RequestCall call = postStringRequest(GlobalHttpPathConfig.LERADIO_SEARCH, parameter);
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
                SearchListModel list = SearchListModel.parse(object);
                message.obj = list;
            }
        }catch (Exception e) {

        }
        mHandler.sendMessage(message);

    }
}
