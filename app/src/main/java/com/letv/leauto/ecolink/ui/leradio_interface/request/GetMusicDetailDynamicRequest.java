package com.letv.leauto.ecolink.ui.leradio_interface.request;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicDetailModel;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.mobile.async.TaskCallBack;
import com.letv.mobile.core.utils.ContextProvider;
import com.letv.mobile.core.utils.SystemUtil;
import com.letv.mobile.http.bean.CommonResponse;
import com.letv.mobile.http.bean.LetvBaseBean;
import com.letv.mobile.http.builder.DynamicUrlBuilder;
import com.letv.mobile.http.builder.LetvHttpBaseUrlBuilder;
import com.letv.mobile.http.model.LetvHttpBaseModel;
import com.letv.mobile.http.parameter.LetvBaseParameter;
import com.letv.mobile.http.request.LetvHttpDynamicRequest;
import com.letv.voicehelp.utils.Trace;

import java.util.HashMap;

/**
 * Created by Han on 16/7/15.
 */
public class GetMusicDetailDynamicRequest
        extends LetvHttpDynamicRequest<LetvHttpBaseModel> {
    private boolean isSync;
    public GetMusicDetailDynamicRequest(Context context, TaskCallBack callback,
            boolean isSync) {
        super(context, callback);
        this.isSync = isSync;
    }
    @Override
    public LetvHttpBaseUrlBuilder getRequestUrl(LetvBaseParameter params) {
        Trace.Debug("--->url detail: " + new DynamicUrlBuilder(GlobalHttpPathConfig.GET_MUSIC_DETAIL,
                params).buildUrl());
        return new DynamicUrlBuilder(GlobalHttpPathConfig.GET_MUSIC_DETAIL,
                params);
    }
    @Override
    protected LetvBaseBean<LetvHttpBaseModel> parse(String sourceData)
            throws Exception {
        return JSON.parseObject(sourceData,
                new TypeReference<CommonResponse<LetvHttpBaseModel>>() {
                });
    }
    @Override
    protected boolean isSync() {
        return isSync;
    }

    @Override
    protected HashMap<String, String> getHeader() {
        HashMap<String, String> headers = super.getHeader();
        String appVersion = SystemUtil.getVersionName(ContextProvider
                .getApplicationContext());
        headers.put("appVersion", appVersion);
        headers.put("imei", DeviceUtils.getDeviceId(context));
        headers.put("devId", DeviceUtils.getDeviceId(context));
        headers.put("mac", SystemUtil.getMacAddress());
        return headers;
    }
}
