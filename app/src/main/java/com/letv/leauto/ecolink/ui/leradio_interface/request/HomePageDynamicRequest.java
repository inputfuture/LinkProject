package com.letv.leauto.ecolink.ui.leradio_interface.request;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.ui.leradio_interface.response.HomePageResponse;
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

/**
 * 首页数据动态请求
 * Dynamic request for home page
 * @author luoyanfeng@le.com
 */
public class HomePageDynamicRequest
        extends LetvHttpDynamicRequest<HomePageResponse> {

    /**
     * 是否同步执行request
     * Whether run this request in sync
     */
    private boolean isSync = false;

    public HomePageDynamicRequest(Context context, TaskCallBack callback,
            boolean isSync) {
        super(context, callback);
        this.isSync = isSync;
    }
    public static final HomePageDynamicRequest homePageDynamicRequest(Context ctx, com.letv.mobile.async.TaskCallBack callBack, boolean isSync) {
        return new HomePageDynamicRequest(ctx, callBack, isSync);
    }
    @Override
    public LetvHttpBaseUrlBuilder getRequestUrl(LetvBaseParameter params) {
        return new DynamicUrlBuilder(GlobalHttpPathConfig.HOME_DYNAMIC_HOMEPAGE,
                params);
    }

    @Override
    protected LetvBaseBean<HomePageResponse> parse(String sourceData)
            throws Exception {
        return JSON.parseObject(sourceData,
                new TypeReference<CommonResponse<HomePageResponse>>() {
                });
    }

    @Override
    protected boolean isSync() {
        return this.isSync;
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
