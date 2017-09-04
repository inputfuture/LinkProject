package com.letv.leauto.ecolink.http.leradio;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.json.AlbumParse;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.HomePageParameter;
import com.letv.leauto.ecolink.utils.Trace;
import com.zhy.http.okhttp.request.RequestCall;

import okhttp3.Call;

/**
 * 加载栏目下的所有专辑列表
 * Created by kevin on 2016/12/21.
 */
public class AlbumListLoader extends BaseLoader {

    private Channel mChannel;
    public static final int MSG_DELAY = 500;//发送消息延时
    public AlbumListLoader(Context ctx, Handler handler, Channel chn) {
        super(ctx, handler);
        mChannel = chn;
    }

    public void load(String pageid,int pageIndex) {
        HomePageParameter parameter = new HomePageParameter(pageid,String.valueOf(pageIndex));
        RequestCall call = getRequest(GlobalHttpPathConfig.HOME_DYNAMIC_HOMEPAGE, parameter.combineParams());
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
        Message message = mHandler.obtainMessage();
        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;

        try {
            LeObject<LeAlbumInfo> leObject = AlbumParse.getLeAlbumInfoList(response, mChannel);

            if (leObject.list != null && leObject.list.size() > 0) {
                message.obj = leObject;
                message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
            } else {
                message.obj = null;
                message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
            }
        } catch (Exception e) {
            Trace.Debug("--->e: " + e.getMessage());
            message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
        }
       // message.sendToTarget();
        mHandler.sendMessageDelayed(message,MSG_DELAY);
    }
}
