package com.letv.leauto.ecolink.http.leradio;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.http.host.LetvAutoHosts;
import com.letv.leauto.ecolink.http.model.LeCpReqData;
import com.letv.leauto.ecolink.leplayer.model.PlayItem;
import com.letv.leauto.ecolink.ui.leradio_interface.data.LeRadioBaseModel;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * File description
 * Created by @author${shimeng}  on @date14/5/23.
 */

public class LiveDetailLoader extends BaseLoader {
    private int mNetworkType;
    private Context ctx;

    public LiveDetailLoader(Context ctx, Handler handler) {
        super(ctx, handler);
    }

    public void load(int tagNum, String type, PlayItem song) {
        String url = LetvAutoHosts.LERADIO_LIVE_DETAIL;
        Trace.Debug("load--->url=" + url);
        Trace.Debug("tagnum=" + tagNum + ",type=" + type + ",song=" + song);
        this.mNetworkType = NetUtils.getNetworkType(this.ctx);
        LeCpReqData reqData = LeCpReqData.create(song, tagNum, this.mNetworkType == 3);
        String body = reqData.toLeCpRequestBody();
        Trace.Debug("body=" + body);
        RequestCall call = postStringRequest(url, body);
        call.execute(this);

    }

    @Override
    public void onError(Call call, Exception e, int id) {
        Trace.Debug("##### e" + e.toString());
        Message message = mHandler.obtainMessage();
        message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
        mHandler.sendMessage(message);
    }

    @Override
    public void onResponse(final String response, int id) {
        Trace.Debug("response=" + response);
        String linkshellUrl = null;
        Message message = mHandler.obtainMessage();
        message.what = MessageTypeCfg.MSG_GET_MUSIC_URL;

        try {
            JSONObject json = new JSONObject(response);
            Trace.Debug("--->json=" + json);
            if (!json.has(LeRadioBaseModel.MEDIA_LIST_DATA)) {
                message.what = MessageTypeCfg.MSG_NODATA_GET;
                Trace.Debug("--->no valible data");
            } else {
                JSONObject dataReturn = json.optJSONObject("data");
                if (dataReturn != null) {
                    String urlSrc = dataReturn.optString("streamUrl", null);
                    if (urlSrc == null || urlSrc.length() < 2) {
                        urlSrc = dataReturn.optString("mainUrl", null);
                    }
                    if (urlSrc != null && urlSrc.length() > 0) {
                        linkshellUrl = urlSrc;
                    } else {
                        message.what = MessageTypeCfg.MSG_NODATA_GET;
                        Trace.Debug("response=" + "LesongCallback:error");
                    }
                }

                Trace.Debug("--->real url: " + linkshellUrl);
            }
        } catch (Exception e) {
            message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
        }
        message.obj = linkshellUrl;
        mHandler.sendMessage(message);

    }
}
