package com.letv.leauto.ecolink.http.leradio;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.ui.leradio_interface.data.LeRadioBaseModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicDetailModel;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.GetMusicDetailDynamicParameter;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.VideoListParameter;
import com.letv.leauto.ecolink.utils.Trace;
import com.letvcloud.cmf.CmfHelper;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * 加载节目详情
 * Created by kevin on 2016/12/21.
 */
public class DetailLoader extends BaseLoader {

    public DetailLoader(Context ctx, Handler handler) {
        super(ctx, handler);
    }

    public void load(String detailId, String type) {
        String url = GlobalHttpPathConfig.BASE_URL + GlobalHttpPathConfig.GET_MUSIC_DETAIL;
        Trace.Debug("load--->url="+url);
        Trace.Debug("detailId="+detailId+",type="+type);
        GetMusicDetailDynamicParameter parameter = new GetMusicDetailDynamicParameter(detailId, type);
        RequestCall call = getRequest(url, parameter.combineParams());
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
        String linkshellUrl = null;
        Message message = mHandler.obtainMessage();
        message.what = MessageTypeCfg.MSG_GET_MUSIC_URL;

        try {
            JSONObject json = new JSONObject(response);
            Trace.Debug("--->json="+json);
            int code = json.getInt(RESPOSE_CODE);
            if (code != CODE_SUCCESS) {
                message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
            } else if (!json.has(LeRadioBaseModel.MEDIA_LIST_DATA)) {
                message.what = MessageTypeCfg.MSG_NODATA_GET;
                Trace.Debug("--->no valible data");
            } else {
                MusicDetailModel detail = MusicDetailModel.parse(json.getJSONObject(LeRadioBaseModel.MEDIA_LIST_DATA));
                linkshellUrl = CmfHelper.getInstance().getLinkShellUrl(detail.getPlayUrl(), true);
                Trace.Debug("--->real url: " + linkshellUrl+",detail.getPlayUrl()="+detail.getPlayUrl());
                detail.playUrl = detail.getPlayUrl();//linkshellUrl;
                message.obj = detail;
            }
        }catch (Exception e) {

        }
        mHandler.sendMessage(message);

    }
}
