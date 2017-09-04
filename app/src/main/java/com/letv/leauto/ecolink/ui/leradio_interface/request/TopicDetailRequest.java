package com.letv.leauto.ecolink.ui.leradio_interface.request;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.letv.leauto.ecolink.ui.leradio_interface.data.TopicDetailModel;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.mobile.async.TaskCallBack;
import com.letv.mobile.core.utils.ContextProvider;
import com.letv.mobile.core.utils.SystemUtil;
import com.letv.mobile.http.bean.CommonResponse;
import com.letv.mobile.http.bean.LetvBaseBean;
import com.letv.mobile.http.builder.DynamicUrlBuilder;
import com.letv.mobile.http.builder.LetvHttpBaseUrlBuilder;
import com.letv.mobile.http.parameter.LetvBaseParameter;
import com.letv.mobile.http.request.LetvHttpDynamicRequest;

import java.util.HashMap;

public class TopicDetailRequest extends
        LetvHttpDynamicRequest<TopicDetailModel> {

    private boolean isSync;

    public TopicDetailRequest(Context context, TaskCallBack callback, boolean isSync) {
        super(context, callback);
        this.isSync = isSync;
    }

    @Override
    protected boolean isSync() {
        return isSync;
    }

    @Override
    public LetvHttpBaseUrlBuilder getRequestUrl(LetvBaseParameter params) {
        return new DynamicUrlBuilder(GlobalHttpPathConfig.PATH_TOPIC_DETAIL,
                params);
    }

    @Override
    protected LetvBaseBean<TopicDetailModel> parse(String sourceData)
            throws Exception {
        CommonResponse<TopicDetailModel> data;
        data = JSON.parseObject(sourceData,
                new TypeReference<CommonResponse<TopicDetailModel>>() {
                });
        return data;
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
