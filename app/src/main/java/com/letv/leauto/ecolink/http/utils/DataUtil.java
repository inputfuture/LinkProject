package com.letv.leauto.ecolink.http.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.CpCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.cfg.VoiceCfg;
import com.letv.leauto.ecolink.database.field.SortIDConfig;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.LeCPDic;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.database.model.VoiceResult;
import com.letv.leauto.ecolink.database.model.WeatherInfo;
import com.letv.leauto.ecolink.event.MusicVoiceEvent;
import com.letv.leauto.ecolink.http.host.LetvAutoHosts;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.model.LeReqData;
import com.letv.leauto.ecolink.http.model.TrafficReq;
import com.letv.leauto.ecolink.http.parameter.ModuleName;
import com.letv.leauto.ecolink.http.parameter.Operation;
import com.letv.leauto.ecolink.json.AlbumParse;
import com.letv.leauto.ecolink.json.ChannelParse;
import com.letv.leauto.ecolink.json.CpParse;
import com.letv.leauto.ecolink.json.MediaParse;
import com.letv.leauto.ecolink.json.TrafficRestrictionParse;
import com.letv.leauto.ecolink.json.VoiceRecgParse;
import com.letv.leauto.ecolink.json.WeatherInfoParse;
import com.letv.leauto.ecolink.ui.leradio_interface.data.AudioSerchDetail;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicUrlInfo;
import com.letv.leauto.ecolink.ui.leradio_interface.data.PositiveSeries;
import com.letv.leauto.ecolink.ui.leradio_interface.data.VideoListModel;
import com.letv.leauto.ecolink.ui.leradio_interface.datautils.MusicListNetProvider;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.HomePageParameter;
import com.letv.leauto.ecolink.utils.ContextProvider;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.mobile.core.utils.Constants;
import com.letv.mobile.core.utils.SystemUtil;
import com.letv.voicehelp.eventbus.EventBusHelper;
import com.letvcloud.cmf.CmfHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by liweiwei on 16/2/24.
 */
public class DataUtil {

    public static final String TAG = "DataUtil";
    public static final String CHANNEL_LIST = "sort_list";
    public static final String ALBUM_LIST = "album_list";
    public static final String MEDIA_LIST = "media_list";
    public static final String CP_LIST = "cp_list";
    private static DataUtil mInstance;
    private static MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static MediaType voiceMediaType = MediaType.parse("application/json");
    public static final int MSG_DELAY = 500;//发送消息延时
    public static DataUtil getInstance() {
        if (mInstance == null) {
            synchronized (DataUtil.class) {
                if (mInstance == null) {
                    mInstance = new DataUtil();
                }
            }
        }
        return mInstance;
    }


    /**
     * 初始化二级菜单数据
     */
    public void getAlbumList(final Handler handler, final String tag, final String sort_id) {

        if (sort_id.equals(SortIDConfig.GUESS_LIKING)) {
        } else {
            requestAlbumListNormal(handler, tag, sort_id);
        }
    }

    /**
     * 初始化音频列表
     *
     * @param
     */
    public void getMediaList(final Handler handler, final String tag, final String sort_id, final LeAlbumInfo curAlbum) {

        if (SortIDConfig.GUESS_LIKING.equals(sort_id)) {
            requestMediaListGuess(handler, tag, curAlbum.ALBUM_ID, curAlbum.ORDER/*"1"*/);
        } else if (sort_id!=null&&sort_id.equals(SortType.SORT_LIVE)) {
            requestMediaListLive(handler, tag, curAlbum.ALBUM_ID, curAlbum.ORDER/*"1"*/);
        } else {

            requestMediaListNormal(handler, tag, curAlbum.ALBUM_ID, curAlbum.ORDER/*"1"*/);
        }

    }

    /**
     * 根据CP_ID获得CP的名称
     */
    public void getCpName(final Handler handler, final String tag) {
        LeReqData leReqData = LeReqData.create(Operation.QUERY, ModuleName.CP_DICTIONARY_QUERY, tag);
        leReqData.addParam("STATUS", 1);
        OkHttpUtils
                .postString()
                .url(LetvAutoHosts.BASE_URL)
                .mediaType(mediaType)
                .content(leReqData.toJSONString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int id) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                try {
                                    LeObject<LeCPDic> leObject = new LeObject<LeCPDic>();
                                    leObject.success = true;
                                    leObject.tag = tag;
                                    String json = EcoApplication.LeGlob.getCache().getString(DataUtil.CP_LIST, null);
                                    if (json != null) {
                                        leObject.list = CpParse.getCPList(new JSONArray(json));
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }

//                                handler.sendMessage(message);
                            }
                        }).start();
                    }

                    @Override
                    public void onResponse(final String response,int id) {
                        Trace.Debug("response", response);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                try {
                                    LeObject<LeCPDic> leCPDics = CpParse.parseCpResp(response);
                                    if (leCPDics.list == null || leCPDics.list.size() <= 0) {
                                        leCPDics.success = true;
                                        leCPDics.tag = tag;
                                        String json = EcoApplication.LeGlob.getCache().getString(DataUtil.CP_LIST, null);
                                        leCPDics.list = CpParse.getCPList(new JSONArray(json));
                                    }
                                    if (leCPDics.list != null && leCPDics.list.size() > 0) {
                                        String cp_forcde = "";
                                        String cp_xiami = "";
                                        String cp_sport = "";
                                        String cp_chelian = "";
                                        if (leCPDics.list != null && leCPDics.list.size() > 0) {
                                            for (int i = 0; i < leCPDics.list.size(); i++) {
                                                LeCPDic dic = leCPDics.list.get(i);

                                                if (CpCfg.CP_XIAMI.equalsIgnoreCase(dic.ALIAS_NAME)) {
                                                    cp_xiami = dic.SOURCE_CP_ID;
                                                }
                                                if (CpCfg.CP_SPORTS.equalsIgnoreCase(dic.ALIAS_NAME)) {
                                                    cp_sport = dic.SOURCE_CP_ID;
                                                }
                                                if (CpCfg.CP_CHELIAN.equalsIgnoreCase(dic.ALIAS_NAME)) {
                                                    cp_chelian = dic.SOURCE_CP_ID;
                                                }
                                                if (CpCfg.CP_CDE_ALIAS.contains(dic.ALIAS_NAME)) {
                                                    cp_forcde = cp_forcde + dic.SOURCE_CP_ID + ",";
                                                }
                                            }
                                            EcoApplication.LeGlob.getCache().putString(CpCfg.CP_FORCDE_CPIDS, cp_forcde);
                                            EcoApplication.LeGlob.getCache().putString(CpCfg.CP_XIAMI_CPID, cp_xiami);
                                            EcoApplication.LeGlob.getCache().putString(CpCfg.CP_SPORTS_CPID, cp_sport);
                                            EcoApplication.LeGlob.getCache().putString(CpCfg.CP_CHELIAN_CPID, cp_chelian);
                                        }

                                        message.obj = leCPDics;
                                        message.what = MessageTypeCfg.MSG_CPLIST_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }
//                                handler.sendMessage(message);

                            }
                        }).start();
                    }
                });


    }


    public ArrayList<Channel> getCacheChannelList() {
        try {
            String json = EcoApplication.LeGlob.getCache().getString(DataUtil.CHANNEL_LIST, null);
            return ChannelParse.getChannelList(new JSONArray(json));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 正常流程获取专辑列表
     */
    private void requestAlbumListNormal(final Handler handler, final String tag, final String sort_id) {
        LeReqData reqData = LeReqData.create(Operation.QUERY, ModuleName.ALBUM_AND_TOP_QUERY, tag);
        reqData.addParam("SORT_ID", sort_id);
        reqData.addParam("STATUS", 1);
        OkHttpUtils
                .postString()
                .url(LetvAutoHosts.BASE_URL)
                .mediaType(mediaType)
                .content(reqData.toJSONString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                try {
                                    LeObject<LeAlbumInfo> leObject = new LeObject<LeAlbumInfo>();
                                    leObject.success = true;
                                    leObject.tag = tag;
                                    String json = EcoApplication.LeGlob.getCache().getString(DataUtil.ALBUM_LIST + "_" + sort_id, null);
                                    Trace.Debug("requestAlbumListNormal", "json:" + json);
                                    if (json != null) {
                                        leObject.list = AlbumParse.getAlbumList(new JSONArray(json));
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }

                                handler.sendMessage(message);
                            }
                        }).start();
                    }

                    @Override
                    public void onResponse(final String response,int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Trace.Debug("response=" + response);
                                Message message = Message.obtain();
                                try {
                                    LeObject<LeAlbumInfo> leObject = AlbumParse.parseAlbumResp(response, sort_id);
                                    if (leObject.list == null || leObject.list.size() <= 0) {
                                        leObject.success = true;
                                        leObject.tag = tag;
                                        String json = EcoApplication.LeGlob.getCache().getString(DataUtil.ALBUM_LIST + "_" + sort_id, null);
                                        leObject.list = AlbumParse.getAlbumList(new JSONArray(json));
                                    }
                                    if (leObject.list != null && leObject.list.size() > 0) {
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }
                                handler.sendMessage(message);
                            }
                        }).start();
                    }
                });
    }

    private void requestMediaListNormal(final Handler handler, final String tag, final String album_id, final String order) {


        LeReqData reqData = LeReqData.create(Operation.QUERY, ModuleName.AUDIO_QUERY, tag);
        String url = GlobalHttpPathConfig.BASE_URL + GlobalHttpPathConfig.GET_MUSIC_LIST;
        reqData.addParam("ALBUM_ID", album_id);
        reqData.addParam("ORDER", order);
        OkHttpUtils
                .postString()
                .url(url)
                .mediaType(mediaType)
                .content(reqData.toJSONString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                try {
                                    LeObject<MediaDetail> leObject = new LeObject<MediaDetail>();
                                    leObject.success = true;
                                    leObject.tag = tag;
                                    String json = EcoApplication.LeGlob.getCache().getString(DataUtil.MEDIA_LIST, null);
                                    Trace.Debug("#####error" + json);
                                    if (json != null) {
                                        // leObject.list = MediaParse.getMediaList(new JSONArray(json), false);
                                        leObject.list = MediaParse.getAudioSerchDetailList(new JSONArray(json));//MediaParse.getMediaList(new JSONArray(json), false);
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_MEDIALST_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }

                                handler.sendMessage(message);
                            }
                        }).start();
                    }

                    @Override
                    public void onResponse(final String response, int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Trace.Debug("#####response" + response);
                                Message message = Message.obtain();
                                try {
                                    LeObject<MediaDetail> leObject = MediaParse.parseMediaResp(response, false);
                                    if (leObject.list == null || leObject.list.size() <= 0) {
                                        leObject.success = true;
                                        leObject.tag = tag;
                                        String json = EcoApplication.LeGlob.getCache().getString(DataUtil.MEDIA_LIST, null);
                                        Trace.Debug("#####json=" + json);
                                        leObject.list = MediaParse.getAudioSerchDetailList(new JSONArray(json));//MediaParse.getMediaList(new JSONArray(json), false);
                                    }
                                    if (leObject.list != null && leObject.list.size() > 0) {
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_MEDIALST_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }
                                handler.sendMessage(message);
                            }
                        }).start();
                    }
                });
    }


    private void requestMediaListLive(final Handler handler, final String tag, final String album_id, final String order) {


        LeReqData reqData = LeReqData.create(Operation.QUERY, ModuleName.AUDIO_QUERY, tag);
        reqData.addParam("ALBUM_ID", album_id);
        reqData.addParam("ORDER", order);
        OkHttpUtils
                .postString()
                .url(LetvAutoHosts.LIVE_DETAIL_URL)
                .mediaType(mediaType)
                .content(reqData.toJSONString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                try {
                                    LeObject<MediaDetail> leObject = new LeObject<MediaDetail>();
                                    leObject.success = true;
                                    leObject.tag = tag;
                                    String json = EcoApplication.LeGlob.getCache().getString(DataUtil.MEDIA_LIST, null);
                                    Trace.Debug("#####error" + json);
                                    if (json != null) {
                                        leObject.list = MediaParse.getMediaList(new JSONArray(json), false);
                                        //leObject.list = MediaParse.getAudioSerchDetailList(new JSONArray(json));//MediaParse.getMediaList(new JSONArray(json), false);
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_MEDIALST_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }

                                handler.sendMessage(message);
                            }
                        }).start();
                    }

                    @Override
                    public void onResponse(final String response,int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Trace.Debug("#####response" + response);
                                Message message = Message.obtain();
                                try {
                                    LeObject<MediaDetail> leObject = MediaParse.parseMediaResp(response, false);
                                    if (leObject.list == null || leObject.list.size() <= 0) {
                                        leObject.success = true;
                                        leObject.tag = tag;
                                        String json = EcoApplication.LeGlob.getCache().getString(DataUtil.MEDIA_LIST, null);
                                        Trace.Debug("#####json=" + json);
                                        leObject.list = MediaParse.getMediaList(new JSONArray(json), false);
                                    }
                                    if (leObject.list != null && leObject.list.size() > 0) {
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_MEDIALST_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }
                                handler.sendMessage(message);
                            }
                        }).start();
                    }
                });
    }

    public void requestLiveMedialist(final Handler handler, final String tag, final ArrayList<LeAlbumInfo> leAlbumInfos, final String order) {
        ArrayList<MediaDetail> mediaDetails = new ArrayList<>();
        for (int i = 0; i < leAlbumInfos.size(); i++) {
            LeAlbumInfo albumInfo = leAlbumInfos.get(i);
            Trace.Debug("#### requestLiveMedialist +" + albumInfo);
            final MediaDetail mediaDetail = new MediaDetail();
            mediaDetail.NAME = albumInfo.NAME;
            mediaDetail.IMG_URL = albumInfo.IMG_URL;
            LeReqData reqData = LeReqData.create(Operation.QUERY, ModuleName.AUDIO_QUERY, tag);
            reqData.addParam("ALBUM_ID", albumInfo.ALBUM_ID);
            OkHttpUtils
                    .postString()
                    .url(LetvAutoHosts.LIVE_DETAIL_URL)
                    .mediaType(mediaType)
                    .content(reqData.toJSONString())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e,int id) {
                            Trace.Debug("#### live onError +" + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(final String response,int id) {
                            new Thread() {
                                @Override
                                public void run() {
                                    Trace.Debug("#### live onresponse +" + response);
                                    LeObject<MediaDetail> leObject = MediaParse.parseMediaResp(response, false);
                                    mediaDetail.channelType = "直播";
                                    mediaDetail.SOURCE_URL = leObject.list.get(0).SOURCE_URL;

                                    mediaDetail.SOURCE_CP_ID = leObject.list.get(0).SOURCE_CP_ID;
                                    mediaDetail.AUDIO_ID = leObject.list.get(0).AUDIO_ID;
                                    mediaDetail.ALBUM_ID = leObject.list.get(0).ALBUM_ID;
                                    mediaDetail.START_TIME = leObject.list.get(0).START_TIME;
                                    mediaDetail.END_TIME = leObject.list.get(0).END_TIME;
                                    mediaDetail.AUTHOR = leObject.list.get(0).AUTHOR;
                                    mediaDetail.CREATE_TIME = leObject.list.get(0).CREATE_TIME;
                                    mediaDetail.LE_SOURCE_MID = leObject.list.get(0).LE_SOURCE_MID;
                                    mediaDetail.LE_SOURCE_VID = leObject.list.get(0).LE_SOURCE_VID;
                                    Trace.Debug("#### live onresponse" + mediaDetail.toString());

                                }
                            }.start();


                        }
                    });

            mediaDetails.add(mediaDetail);

        }
        Message message = Message.obtain();
        message.obj = mediaDetails;
        message.what = MessageTypeCfg.MSG_LIVE_MEDIALST_OBTAINED;
        handler.sendMessage(message);

    }


    private void requestMediaListGuess(final Handler handler, final String tag, final String album_id, final String order) {

        LeReqData reqData = LeReqData.create(Operation.QUERY, ModuleName.MEDIADETAILS_QUERY, tag);
        reqData.addParam("CLASS_ID", album_id);
        OkHttpUtils
                .postString()
                .url(LetvAutoHosts.BASE_URL)
                .mediaType(mediaType)
                .content(reqData.toJSONString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                try {
                                    LeObject<MediaDetail> leObject = new LeObject<MediaDetail>();
                                    leObject.success = true;
                                    leObject.tag = tag;
                                    String json = EcoApplication.LeGlob.getCache().getString(DataUtil.MEDIA_LIST, null);
                                    if (json != null) {
                                        leObject.list = MediaParse.getMediaList(new JSONArray(json), true);
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_MEDIALST_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }
                                handler.sendMessage(message);
                            }
                        }).start();
                    }

                    @Override
                    public void onResponse(final String response,int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Trace.Debug("response", response);
                                Message message = Message.obtain();
                                try {
                                    LeObject<MediaDetail> leObject = MediaParse.parseMediaResp(response, true);
                                    if (leObject.list == null || leObject.list.size() <= 0) {
                                        leObject.success = true;
                                        leObject.tag = tag;
                                        String json = EcoApplication.LeGlob.getCache().getString(DataUtil.MEDIA_LIST, null);
                                        leObject.list = MediaParse.getMediaList(new JSONArray(json), true);
                                    }
                                    if (leObject.list != null && leObject.list.size() > 0) {
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_MEDIALST_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }
                                handler.sendMessage(message);
                            }
                        }).start();
                    }
                });
    }

    /**
     * 本地缓存二级菜单数据
     */
    public void getAlbumFromDB(final Handler handler, final String sort_type, final String tag) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    LeObject<LeAlbumInfo> albumResult = new LeObject<LeAlbumInfo>();
                    albumResult.list = MediaOperation.getInstance().getAlbumList(sort_type);
                    albumResult.tag = tag;
                    Message message = Message.obtain();
                    message.obj = albumResult;
                    message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(MessageTypeCfg.MSG_GETDATA_COMPLETED);
            }
        }.start();
    }

    /**
     * 本地缓存二级菜单数据
     */
    public void getLcoalMusicByAlbum(final Handler handler, final LeAlbumInfo leAlbumInfo) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    ArrayList<MediaDetail> mediaDetails = MediaOperation.getInstance().getLocalMusicByAlbum(leAlbumInfo);
                    Message message = Message.obtain();
                    message.obj = mediaDetails;
                    message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(MessageTypeCfg.MSG_GETDATA_COMPLETED);
            }
        }.start();
    }

    /**
     * 本地缓存二级菜单数据
     */
    public void getMediaListFromDB(final Handler handler, final String sort_type, final String album_id, final String tag) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Message message = Message.obtain();
                    LeObject<MediaDetail> albumResult = new LeObject<MediaDetail>();
                    if (sort_type.equals(SortType.SORT_RECENT)) {
                        albumResult.list = MediaOperation.getInstance().getRecentList();
                        message.what = MessageTypeCfg.MSG_FROM_RECENT;
                    } else {
                        albumResult.list = MediaOperation.getInstance().getMusicList(sort_type, album_id);
                        message.what = MessageTypeCfg.MSG_FROM_LOCAL;
                    }
                    albumResult.tag = tag;
                    message.obj = albumResult;

                    handler.sendMessageDelayed(message,MSG_DELAY);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(MessageTypeCfg.MSG_GETDATA_COMPLETED);
            }
        }.start();
    }

    /**
     * 本地缓存二级菜单数据
     */
    public void getMediaListFromJson(final Handler handler, final String lastMediaList, final String tag) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    LeObject<MediaDetail> albumResult = new LeObject<MediaDetail>();
                    Trace.Debug("#### lastMediaList:" +lastMediaList);
                    albumResult.list = MediaParse.getMediaList(new JSONArray(lastMediaList), false);;
                    Trace.Debug("#### albumResult.list:" + albumResult.list);
                    albumResult.tag = tag;
                    Message message = Message.obtain();
                    message.obj = albumResult;
                    message.what = MessageTypeCfg.MSG_FROM_VOICE;
                    handler.sendMessageDelayed(message,MSG_DELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(MessageTypeCfg.MSG_GETDATA_COMPLETED);
            }
        }.start();
    }

    /**
     * 本地缓存二级菜单数据
     */
    public void getMediaListFromLeradioJson(final Handler handler, final String lastMediaList, final String tag) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    LeObject<MediaDetail> albumResult = new LeObject<MediaDetail>();
                    Trace.Debug("#### lastMediaList:" +lastMediaList);
                    albumResult.list = MediaParse.getMediaList(new JSONArray(lastMediaList), false);;
                    Trace.Debug("#### albumResult.list:" + albumResult.list);
                    albumResult.tag = tag;
                    Message message = Message.obtain();
                    message.obj = albumResult;
                    message.what = MessageTypeCfg.MSG_MEDIALST_OBTAINED;
                    handler.sendMessageDelayed(message,MSG_DELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(MessageTypeCfg.MSG_GETDATA_COMPLETED);
            }
        }.start();
    }
    /**
     * 解析百度语音接口返回数据以及bosonnlp返回的数据
     *
     * @param mHandler
     * @param origin_result
     */

    public void getKeyWordFromVoice(final Handler mHandler, final String origin_result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                final VoiceResult voiceResult = VoiceRecgParse.parseVoiceRecg(origin_result);
                if (voiceResult.key_word != null && !voiceResult.key_word.equals("")) {
                    message.obj = voiceResult;
                    message.what = VoiceCfg.MSG_GET_DATA_SUCCESS;
                    mHandler.sendMessage(message);
                } else {
                    Trace.Debug("voiceResult.raw_text", voiceResult.raw_text);
                    String body = "[\"" + voiceResult.raw_text + "\"]";
                    OkHttpUtils
                            .postString()
                            .url(LetvAutoHosts.VOICE_BSN_URL)
                            .mediaType(voiceMediaType)
                            .addHeader("X-Token", "j2M9jSWe.4016.zOzzggX3EMCG")
                            .addHeader("Accept", "application/json")
                            .content(body)
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e,int id) {
                            Message message = Message.obtain();
                            message.what = VoiceCfg.MSG_GET_DATA_FAILED;
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onResponse(String response,int id) {
                            Trace.Debug("response", response);
                            Message bsnMsg = Message.obtain();
                            try {
                                //波森如果没有解析成功的话,直接返回原始数据
                                VoiceResult bsnResult = VoiceRecgParse.ParseBsn(response);

                                if (bsnResult.key_word == null) {
                                    bsnResult.domain = VoiceCfg.DOMAIN_MUSIC;
                                    bsnResult.intention = "play";
                                    bsnResult.key_word = voiceResult.raw_text;
                                }
                                Trace.Debug("response", bsnResult.key_word);
                                bsnMsg.obj = bsnResult;
                                bsnMsg.what = VoiceCfg.MSG_GET_DATA_SUCCESS;
                                mHandler.sendMessage(bsnMsg);
                            } catch (Exception e) {
                                bsnMsg.what = VoiceCfg.MSG_GET_DATA_FAILED;
                                mHandler.sendMessage(bsnMsg);
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        }).start();
    }

    /**
     * 语音搜索
     *
     * @param handle
     * @param key
     */
    public void getSearchListByVoice(Context ctx, final Handler handle, final String key) {
        LeReqData reqData = LeReqData.create(Operation.QUERY, ModuleName.ALL_KEYWORDS_QUERY, "getSearchListByVoice");
        reqData.addParam("KEYWORDS", key);
        // Context ctx = ContextProvider.getApplicationContext();
        String appVersion = SystemUtil.getVersionName(ctx);
        OkHttpUtils
                .postString()
                .url(GlobalHttpPathConfig.LERADIO_SEARCH)
                .mediaType(mediaType)
                .addHeader("appVersion", appVersion)
                .addHeader("imei", DeviceUtils.getDeviceId(ctx))
                .addHeader("devId", DeviceUtils.getDeviceId(ctx))
                .addHeader("mac", SystemUtil.getMacAddress())
                .content(reqData.toJSONString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();

                                message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                //EcoApplication.LeGlob.getCache().putString(VoiceCfg.VOICE_RECOGNITION, cacheString);
                                handle.sendMessage(message);

                            }
                        }).start();
                    }

                    @Override
                    public void onResponse(final String response,int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Trace.Debug("response", response);
                                Message message = new Message();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject data = jsonObject.optJSONObject("data");

                                    LeObject<MediaDetail> object = new LeObject<MediaDetail>();
                                    //缓存收藏更新数据
                                    String cacheString = "";
                                    if (data != null) {
                                        cacheString = data.optJSONArray("root").toString();
                                        if (cacheString != null && cacheString.length() > 0) {
                                            object.list = MediaParse.getMediaList(new JSONArray(cacheString), false);
                                            object.success = true;
                                            message.obj = object;
                                            message.what = MessageTypeCfg.MSG_SEARCH_BY_VOICE_OBTAINED;
                                        } else {
                                            object.success = false;
                                            message.what = MessageTypeCfg.MSG_SEARCH_BY_VOICE_OBTAINED;
                                        }
                                    } else {
                                        object.success = false;
                                        message.what = MessageTypeCfg.MSG_SEARCH_BY_VOICE_OBTAINED;
                                    }
                                    //EcoApplication.LeGlob.getCache().putString(VoiceCfg.VOICE_RECOGNITION, cacheString);
                                    handle.sendMessage(message);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
    }

    public void getThePlayList(ArrayList<MediaDetail> mediaDetails) {
        EventBusHelper.post(new MusicVoiceEvent(mediaDetails));
    }

    public void getTheAbumList(String voiceResult, final Handler handler) {
        LeObject<LeAlbumInfo> leObject = new LeObject<LeAlbumInfo>();
        Context ctx = ContextProvider.getApplicationContext();
        Message message = new Message();
        try {
            leObject.list = AlbumParse.getAlbumList(new JSONArray(voiceResult));
            if (leObject.list != null && leObject.list.size() > 0) {
                message.obj = leObject;
                message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
            } else {
                message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
            }
            LeAlbumInfo albumItem = leObject.list.get(0);
            DataUtil.getInstance().getMusicDetailListData(handler, ctx, albumItem, albumItem.ALBUM_TYPE_ID, null);
        } catch (Exception e) {
            e.printStackTrace();
            message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
        }
    }

    // 将获取专辑下面的曲目列表转化为播放的列表接口
    private ArrayList<MediaDetail> changeModel2MediaList(ArrayList<VideoListModel> models) {
        ArrayList<MediaDetail> MediaList = new ArrayList<MediaDetail>();
        if (models != null) {
            for (int i = 0; i < models.size(); i++) {
                VideoListModel item = models.get(i);
                MediaDetail info = new MediaDetail();
                info.NAME = (item.getName());//歌曲名称
                info.AUDIO_ID = item.getDetailId();//歌曲id
                info.IMG_URL = (item.getImg());//图片的Url
                info.ALBUM_ID = item.getAlbumId();////专辑id
                info.setPlayType(item.getDetailType());//区分视频，音频
                info.TYPE = SortType.SORT_LE_RADIO;//自己区分歌曲的类型
                info.setDuration(item.getDuration());//歌曲的时长，单位s
                info.ALBUM = item.getName();//专辑名称
                info.AUTHOR = item.getAuthor();
                MediaList.add(info);
            }
        } else {
            MediaList = null;
        }
        return MediaList;
    }


    public void getPlayList(String voiceResult) {
        try {
            JSONObject jsonObject = new JSONObject(voiceResult);
            // JSONObject data = jsonObject.optJSONObject("data");
            Trace.Debug(TAG, "jsonObject:" + jsonObject);
//            ArrayList<VideoAlbums> videoAlbumsList = new ArrayList<VideoAlbums>();
//            ArrayList<AudioAlbums> audioAlbumsList = new ArrayList<AudioAlbums>();
            ArrayList<AudioSerchDetail> serchList = new ArrayList<AudioSerchDetail>();
            if (jsonObject != null) {
                //解析 videoAlbums
//                JSONArray videoAlbums = data.getJSONArray("videoAlbums");
//                Trace.Debug(TAG, "videoAlbums:" + videoAlbums);
//                if (videoAlbums != null && videoAlbums.length() > 0) {
//                    videoAlbumsList = MediaParse.getVideoAlbumsList(videoAlbums);
//                    ArrayList<VideoListModel> videoModels = videoAlbumsList.get(0);
//                    ArrayList<MediaDetail> mediaDetails = changeModel2MediaList(videoModels);
//                    getThePlayList(mediaDetails);
//                } else {
//                    Trace.Debug(TAG, "videoAlbums is null!");
//                }
                //解析 audioAlbums
//                JSONArray audioAlbums = data.getJSONArray("audioAlbums");
//                Trace.Debug(TAG, "audioAlbums:" + audioAlbums);
//                if (audioAlbums != null && videoAlbums.length() > 0) {
//                    audioAlbumsList = MediaParse.getAudioAlbumsList(audioAlbums);
//                    AudioAlbums audioAlbum= audioAlbumsList.get(0);
//                     String audioAlbumId = audioAlbum.getAlbumId();
//
//                    //ArrayList<VideoListModel> aideoModels
////                    ArrayList<MediaDetail> mediaDetails = changeModel2MediaList(aideoModels);
////                    getThePlayList(mediaDetails);
//                } else {
//                    Trace.Debug(TAG, "audioAlbums is null!");
//                }
                //解析audios

                JSONArray audios = jsonObject.getJSONArray("audios");
                Trace.Debug(TAG, "audios:" + audios);
                if (audios != null && audios.length() > 0) {
                    ArrayList<MediaDetail> mediaDetails = MediaParse.getAudioSerchDetailList(audios);
                    //ArrayList<MediaDetail> mediaDetails = changeSearch2MediaList(serchList);
                    getThePlayList(mediaDetails);
                } else {
                    Trace.Debug(TAG, "audios is null!");
                }

            } else {
                Trace.Debug(TAG, "data is null!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWeatherInfo(String city, final Handler handler) {

        OkHttpUtils.get().url(LetvAutoHosts.WEATHER_URL + city).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
                if(handler == null)
                    return;
                handler.sendEmptyMessage(MessageTypeCfg.MSG_GET_WEATHER_FAIL);
            }

            @Override
            public void onResponse(final String response,int id) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(handler == null)
                            return;

                        Trace.Debug("response", response);
                        Message message = new Message();
                        WeatherInfo info = WeatherInfoParse.parseWeatherInfo(response);
                        if (info != null && info.weather != null && handler != null) {
                            message.obj = info;
                            message.what = MessageTypeCfg.MSG_GET_WEATHER;
                            if (handler!=null){
                                handler.sendMessage(message);}
                        }
                    }
                }).start();

            }
        });
    }
    public static int count = 0;
    /**
     * 获取限号
     *
     * @param handler
     */
    public static void getTrafficControls(final String city, final Handler handler) {


        String url = TrafficReq.create(city);
        Trace.Debug("###### traffic " + url);
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
                if (handler==null)
                    return;
                if (count<3){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    getTrafficControls(city, handler);
                    count++;
                    handler.sendEmptyMessage(MessageTypeCfg.MSG_GET_TRAFFIC_FAIL);
                }
            }

            @Override
            public void onResponse(final String response,int id) {

                Trace.Debug("TAG", "onResponseQu2: " + response);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        String res = TrafficRestrictionParse.parseTrafficInfo(response);
//                        CacheUtils.getInstance(EcoApplication.getInstance()).putString(Constant.TRAFFIC_INFO, res);
                        message.obj = res;
                        message.what = MessageTypeCfg.MSG_GET_TRAFFIC;
                        if (handler==null)
                            return;
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }



//    public static void getTrafficControls(String city, final  Handler handler, Context context){
//        String url = TrafficReq.create(city);
//        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
//        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                Message message = new Message();
//                String res = TrafficRestrictionParse.parseTrafficInfo(response);
//                CacheUtils.getInstance(EcoApplication.getInstance()).putString(Constant.TRAFFIC_INFO, res);
//                message.obj = res;
//                message.what = MessageTypeCfg.MSG_GET_TRAFFIC;
//                handler.sendMessage(message);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                handler.sendEmptyMessage( MessageTypeCfg.MSG_GET_TRAFFIC);
//            }
//        });
//
//
//        mRequestQueue.add(stringRequest);
//
//
//    }

    private MusicListNetProvider mNetProvider;

    // 获得标题栏目信息
    public void getChannels(final Handler argHandler, final Context argContext) {
        //如果是true就要开线程
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (NetUtils.isConnected(argContext)){
//                    ChannelDynamicParameter params = new ChannelDynamicParameter();
//                    ChannelDynamicRequest.channelDynamicRequest(argContext, new TaskCallBack() {
//                        @Override
//                        public void callback(int code, String msg,String errorCode, Object object) {
//                            if (code == TaskCallBack.CODE_OK) {
//                                CommonListResponse commonResponse = (CommonListResponse) object;
//                                if (commonResponse.getData()!= null){
//                                    List<ChannelResponse> response = (List<ChannelResponse>) commonResponse.getData();
//                                    List<Channel> channels= response.get(0).getChannels();
//                                    Message message=Message.obtain();
//                                    message.what= MessageTypeCfg.MSG_CHANNEL;
//                                    List<Channel> resultChannels=new ArrayList<Channel>();
//                                    Channel hotChannel = null;
//                                    Channel myChannel = null;
//                                    Channel musicChannel = null;
//                                    Channel liveChannel = null;
//                                    for (int i = 0; i < channels.size(); i++) {
//                                        Channel channel=channels.get(i);
//                                        if (channel.getName().equals("热门")){
//                                            hotChannel=channels.get(i);
//                                        }else if (channel.getName().equals("我的")){
//                                            myChannel=channels.get(i);
//                                        }else  if (channel.getName().equals("音乐")){
//                                            musicChannel=channels.get(i);
//                                        }else if (channel.getName().equals("直播")){
//                                            liveChannel=channels.get(i);
//                                        }else {
//                                            resultChannels.add(channel);
//                                        }
//                                    }
//                                    resultChannels.add(0,liveChannel);
//                                    resultChannels.add(0,musicChannel);
//                                    resultChannels.add(0,myChannel);
//                                    resultChannels.add(0,hotChannel);
//                                    message.obj=resultChannels;
//                                    argHandler.sendMessage(message);
//                                    StringBuilder jsonArray=new StringBuilder();
//                                    jsonArray.append("[");
//                                    for (int i = 0; i < resultChannels.size(); i++) {
//                                        JSONObject jsonObject=new JSONObject();
//                                        Channel channel=channels.get(i);
//                                        try {
//                                            jsonObject.put("pageId",channel.pageId);
//                                            jsonObject.put("name",channel.name);
//                                            jsonObject.put("skipType",channel.skipType);
//                                            jsonObject.put("cmsID",channel.cmsID);
//                                            jsonObject.put("dataUrl",channel.dataUrl);
//                                            jsonObject.put("type",channel.type);
//                                            jsonObject.put("mzcId",channel.mzcId);
//                                            jsonArray.append(jsonObject.toString());
//                                            jsonArray.append(",");
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    jsonArray.deleteCharAt(jsonArray.length()-1);
//                                    jsonArray.append("]");
//                                    Trace.Debug("#### ChannelResponse"+jsonArray.toString());
//                                    CacheUtils.getInstance(argContext).putString(CHANNEL_LIST,jsonArray.toString());
//
//                                }
//                            }else {
//                                Message message=Message.obtain();
//                                message.what= MessageTypeCfg.MSG_CHANNEL;
//                                message.obj=getCacheChannelList();
//                                argHandler.sendMessage(message);
//
//                            }
//                        }
//                    },true).execute(params.combineParams());
//                }else {
//                    Message message=Message.obtain();
//                    message.what= MessageTypeCfg.MSG_CHANNEL;
//                    message.obj=getCacheChannelList();
//                    argHandler.sendMessage(message);
//                }
//            }
//        }).start();
    }

    //    3.1版本获取专辑列表
    public void getNewLeAlbumList(final Handler argHandler, final Context argContext, final Channel argChannels) {

        String appVersion = SystemUtil.getVersionName(argContext);
        OkHttpUtils.get()
                .url(GlobalHttpPathConfig.HOME_DYNAMIC_HOMEPAGE)//
                .tag(this)//
                .addHeader("appVersion", appVersion)
                .addHeader("imei", DeviceUtils.getDeviceId(argContext))
                .addHeader("devId", DeviceUtils.getDeviceId(argContext))
                .addHeader("mac", SystemUtil.getMacAddress())
                .params(new HomePageParameter(argChannels.getPageId(),"1"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        Trace.Debug("##### e" + e.toString());
                        Message message = argHandler.obtainMessage();
                        message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                        argHandler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(final String response,int id) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Trace.Debug("response=" + response);
                                Message message = Message.obtain();
                                try {
                                    LeObject<LeAlbumInfo> leObject = AlbumParse.getLeAlbumInfoList(response, argChannels);

                                    if (leObject.list != null && leObject.list.size() > 0) {
                                        message.obj = leObject;
                                        message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
                                    } else {
                                        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    message.what = MessageTypeCfg.MSG_GETDATA_EXCEPTION;
                                }
                                argHandler.sendMessage(message);
                            }
                        }).start();
                    }
                });


        //如果是true就要开线程
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HomePageDynamicParameter params = new HomePageDynamicParameter();
//                params.setPageid(argChannels.getPageId());
//                HomePageDynamicRequest.homePageDynamicRequest(argContext, new TaskCallBack() {
//                    @Override
//                    public void callback(int code, String msg,String errorCode, Object object) {
//                        if (code == TaskCallBack.CODE_OK) {
//                            CommonResponse commonResponse = (CommonResponse) object;
//                            if (commonResponse.getData()!= null){
//                                HomePageResponse response = (HomePageResponse) commonResponse.getData();
//                                Trace.Error("======getNewLeAlbumList=",response.getBlock().get(0).toString());
//                                Message message=Message.obtain();
//                                message.what= MessageCfg.MSG_HOME_PAGE;
//                                message.obj=response.getBlock();
//                                argHandler.sendMessage(message);
//                            }
//                        }
//                    }
//                },true).execute(params.combineParams());
//            }
//        }).start();
    }

    //获取专辑下的列表
    public void getMusicDetailListData(final Handler mHandler, Context mContext, LeAlbumInfo argLeAlbumInfo, String type, String argPage) {

        // LeRadioController controller = new LeRadioController(mHandler);
        // controller.getVideoListByAlbum(argLeAlbumInfo.ALBUM_ID, 0);
        int page = 0;
        if (mNetProvider == null) {
            mNetProvider = new MusicListNetProvider();
        }
        if (argPage != null) {
            page = Integer.valueOf(argPage);
        }
        switch (type) {
            case Constants.MEDIA_PLAY_TYPE_VIDEO://视频
            case Constants.MEDIA_PLAY_TYPE_VIDEOLIST:
            case Constants.MEDIA_PLAY_TYPE_VIDEO1:

                mNetProvider.getVideoInfoAndList(argLeAlbumInfo, page, new MusicListNetProvider.OnGetPlayInfoCallback() {


                    @Override
                    public void onGetPlayListSuccess(List<MediaDetail> queueList) {
                        Trace.Error("===VList=2=", queueList.toString());


                    }

                    @Override
                    public void onGetVideoListSuccess(List<PositiveSeries> positiveSeries) {
                        if (positiveSeries != null) {
                            Message message = Message.obtain();
                            message.what = MessageTypeCfg.MSG_GET_VIDEOLIST;
                            message.obj = positiveSeries;
                            mHandler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onGetDataError() {
                        Trace.Error("===VList=3=", "NetWork Error");
                    }
                });


                break;

            case Constants.MEDIA_PLAY_TYPE_AUDIO://音频
            case Constants.MEDIA_PLAY_TYPE_AUDIOLIST:
                mNetProvider.getAudioInfoAndList(argLeAlbumInfo, page, new MusicListNetProvider.OnGetPlayInfoCallback() {


                    @Override
                    public void onGetPlayListSuccess(List<MediaDetail> queueList) {
//                        getInfoList(queueList);
                        Trace.Error("===AList=2=", queueList.toString());
                        if (queueList != null) {
                            Message message = Message.obtain();
                            message.what = MessageTypeCfg.MSG_GET_AUDIOLIST;
                            message.obj = queueList;
                            mHandler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onGetVideoListSuccess(List<PositiveSeries> positiveSeries) {

                    }

                    @Override
                    public void onGetDataError() {
//                        handleStopRequest("NetWork Error");
                        Trace.Error("===AList=3=", "NetWork Error");
                    }
                });
                break;
            default:
                break;

        }
    }

    //获取歌曲的Url，每听一次都要请求一下，因为歌曲的地址有实效性，一段时间后地址就会失效
    public void getMusicUrlData(final Handler mHandler, final String mediaId, String type) {
        if (mNetProvider == null) {
            mNetProvider = new MusicListNetProvider();
        }
        switch (type) {
            //当前播放为视频音轨，需要通过videoId和AlbumId(pid)请求视频音轨播放地址
            case Constants.MEDIA_PLAY_TYPE_VIDEO:
            case Constants.MEDIA_PLAY_TYPE_VIDEOLIST:
                mNetProvider.getAudioDetail(mediaId, type, new MusicListNetProvider.OnGetURLResponseCallback() {
                    @Override
                    public void onGetURLDetailSuccess(MusicUrlInfo model) {
                        if (model == null) {
                            return;
                        }
                        Trace.Error("==model=", model.getPlayUrl());
                        Message message = Message.obtain();
                        message.what = MessageTypeCfg.MSG_GET_MUSIC_URL;
                        String linkshellUrl = CmfHelper.getInstance().getLinkShellUrl(model.getPlayUrl(), true);
                        message.obj = linkshellUrl;
                        mHandler.sendMessage(message);
                    }
                }, false);

                break;
            //当前播放为音频，需通过audioId请求音频播放地址
            case Constants.MEDIA_PLAY_TYPE_AUDIO:
            case Constants.MEDIA_PLAY_TYPE_AUDIOLIST:
                mNetProvider.getAudioDetail(mediaId, type, new MusicListNetProvider.OnGetURLResponseCallback() {
                    @Override
                    public void onGetURLDetailSuccess(MusicUrlInfo model) {
                        Message message = Message.obtain();
                        message.what = MessageTypeCfg.MSG_GET_MUSIC_URL;
                        String linkshellUrl = CmfHelper.getInstance().getLinkShellUrl(model.getPlayUrl(), true);
                        message.obj = linkshellUrl;
                        mHandler.sendMessage(message);
                    }
                }, false);
                break;
            default:
                break;

        }
    }

}
