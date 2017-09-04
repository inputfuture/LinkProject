package com.letv.leauto.ecolink.http.leradio;

import android.content.Context;
import android.os.Handler;

import com.letv.leauto.ecolink.http.CommonHeader;
import com.letv.leauto.ecolink.ui.leradio_interface.baseparameter.HttpBaseParameter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by kevin zhao on 2016/12/16.
 * notice：暂时不可用
 * 该类负责加载leradio的相关信息，包括：栏目列表，栏目内专辑列表
 *      专辑内音乐、视频列表及节目详情
 */
public class BaseLoader extends StringCallback {

    public static final String RESPOSE_CODE = "code";
    public static final String RESPOSE_DATA = "data";

    /*请求返回成功*/
    public static final int CODE_SUCCESS = 10000;
    /*请求返回失败*/
    public static final int CODE_FAILURE = 20000;


    /* 回调handler消息处理 */
    protected Handler mHandler;
    protected Context mContext;


    /* 获取所有栏目列表 */
    private static final int MESSAGE_GET_COLUMNS_LIST = 0x101;

    /* 获取栏目下专辑列表 */
    private static final int MESSAGE_GE_ALBUM_LIST = 0x102;

    /* 获取视频剧集列表 */
    private static final int MESSAGE_GET_VIDEO_LIST = 0x103;

    /* 获取音频专辑内容列表 */
    private static final int MESSAGE_GET_AUDIO_LIST = 0x104;

    /* 获取播放链接 */
    private static final int MESSAGE_GET_DETAIL = 0x105;

    /* 获取查询结果链接 */
    private static final int MESSAGE_GET_SEARCH = 0x106;

    public BaseLoader() {

    }

    public BaseLoader(Context ctx, Handler handler) {
        mContext = ctx;
        mHandler = handler;
    }
    protected RequestCall postStringRequest(String url, String parameters) {

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        return OkHttpUtils.postString()
                .url(url)
                .tag(this)
                .mediaType(mediaType)
                .headers(new CommonHeader(mContext))
                .content(parameters)
                .build();
    }

    protected RequestCall postStringRequest(String url, HttpBaseParameter parameters) {

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        return OkHttpUtils.postString()
                .url(url)
                .tag(this)
                .mediaType(mediaType)
                .headers(new CommonHeader(mContext))
                .content(parameters.toJSONString())
                .build();
    }

    protected RequestCall getRequest(String url, Map<String, String> parameter) {
        return OkHttpUtils.get()
                .url(url)
                .tag(this)
                .headers(new CommonHeader(mContext))
                .params(parameter)
                .build();
    }


    @Override
    public void onError(Call call, Exception e,int id) {

    }

    @Override
    public void onResponse(final String response,int id) {

    }

}
