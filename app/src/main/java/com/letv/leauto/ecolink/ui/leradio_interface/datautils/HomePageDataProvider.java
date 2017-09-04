package com.letv.leauto.ecolink.ui.leradio_interface.datautils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.letv.leauto.ecolink.ui.leradio_interface.dataprovider.DataProvider;
import com.letv.leauto.ecolink.ui.leradio_interface.dataprovider.Scene;
import com.letv.leauto.ecolink.ui.leradio_interface.request.HomePageDynamicRequest;
import com.letv.leauto.ecolink.ui.leradio_interface.response.HomePageResponse;
import com.letv.mobile.async.TaskCallBack;


/**
 * Created by Han on 16/7/16.
 */
public class HomePageDataProvider extends DataProvider<HomePageResponse> {

    private final String CACHE_FILE_NAME = "lemusic_homepage_data_provider.cache";
    private final String NAME = "lemusic_HomePageDataProvider";
    private final long UPDATE_DURATION = 2 * 60 * 1000;

    public static HomePageDataProvider getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public FetchResultWrap fetchData(boolean forceRefresh, String param) {
        final FetchResultWrap wrap = new FetchResultWrap();
//        HomePageDynamicParameter params = new HomePageDynamicParameter();
//        homePageDynamicRequest(ContextProvider.getApplicationContext(), new TaskCallBack() {
//            @Override
//            public void callback(int code, String msg,
//                                 String errorCode, Object object) {
//                if (code == TaskCallBack.CODE_OK) {
//                    CommonResponse commonResponse = (CommonResponse) object;
//                    if (commonResponse.getData()!= null){
//                        HomePageResponse response = (HomePageResponse) commonResponse.getData();
//                        wrap.setData(response);
//                        wrap.setSuccess(true);
//                        wrap.setDataTime(HomePageDataProvider.this
//                                .getCurrentTime());
//                        wrap.setBackFlags(new GetDataBackFlags(code,
//                                msg, errorCode));
//                    }else {
//                        wrap.setSuccess(false);
//                        wrap.setBackFlags(new GetDataBackFlags(code,
//                                msg, errorCode));
//                    }
//
//                }else {
//                    wrap.setSuccess(false);
//                    wrap.setBackFlags(new GetDataBackFlags(code, msg,
//                            errorCode));
//                }
//            }
//        },true).execute(params.combineParams());

        return wrap;
    }

    public static final HomePageDynamicRequest homePageDynamicRequest(Context ctx, TaskCallBack callBack, boolean isSync) {
        return new HomePageDynamicRequest(ctx, callBack, isSync);
    }

    @Override
    protected Scene[] generateScenes() {
        return new Scene[0];
    }

    @Override
    protected long generateValidDuration(long defaultValue) {
        return defaultValue;
    }

    @Override
    protected long generateUpdateDuration(long defaultValue) {
        return UPDATE_DURATION;
    }

    @Override
    protected String getProviderName() {
        return NAME;
    }

    @Override
    protected String getCacheFileNamePrefix() {
        return CACHE_FILE_NAME;
    }

    @Override
    protected HomePageResponse parseData(String dataString) {
        return JSON.parseObject(dataString,
                new TypeReference<HomePageResponse>(){
                });
    }

    @Override
    protected int getVersion() {
        return 0;
    }

    private static class InstanceHolder {
        static HomePageDataProvider instance = new HomePageDataProvider();
    }
}
