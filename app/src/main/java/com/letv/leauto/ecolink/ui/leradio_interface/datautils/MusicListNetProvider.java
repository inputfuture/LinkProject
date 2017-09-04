package com.letv.leauto.ecolink.ui.leradio_interface.datautils;


import android.content.Context;

import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicDetailModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicListItem;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicListModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicUrlInfo;
import com.letv.leauto.ecolink.ui.leradio_interface.data.PositiveSeries;
import com.letv.leauto.ecolink.ui.leradio_interface.data.VideoListModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.VideoListResponseModel;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.GetMusicDetailDynamicParameter;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.GetPlayListDynamicParameter;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.VideoListParameter;
import com.letv.leauto.ecolink.ui.leradio_interface.request.GetMusicURLDynamicRequest;
import com.letv.leauto.ecolink.ui.leradio_interface.request.GetPlayListDynamicRequest;
import com.letv.leauto.ecolink.ui.leradio_interface.request.VideoListRequest;
import com.letv.mobile.async.TaskCallBack;
import com.letv.mobile.core.utils.ContextProvider;
import com.letv.mobile.http.bean.CommonResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.letv.leauto.ecolink.utils.CdeUtil;
//import com.letv.mobile.core.utils.CdeUtil;

/**
 * Created by Han on 16/7/16.
 */
public class MusicListNetProvider {

    //private static MusicListNetProvider mMusicListNetProvider;
    private static final String TAG = "MusicListNetProvider";
    public MusicListNetProvider() {
    }

//    public synchronized static MusicListNetProvider getInstance() {
//        if (mMusicListNetProvider == null) {
//            mMusicListNetProvider = new MusicListNetProvider();
//        }
//        return mMusicListNetProvider;
//    }

    //请求音频专辑列表
    public void getAudioList(String pid, int page,final OnAudioResponseCallback callback, boolean isSync) {
        GetPlayListDynamicParameter params = new GetPlayListDynamicParameter();
        Map<String, String> queris = new HashMap<>();
        queris.put("albumId", pid);
        queris.put("page", String.valueOf(page));
        params.setQueris(queris);
        // getPlayListDynamicRequest(ContextProvider.getApplicationContext(), callback, isSync).execute(params.combineParams());
    }

    public static final GetPlayListDynamicRequest getPlayListDynamicRequest(Context ctx, final OnAudioResponseCallback callback, boolean isSync) {
        return new GetPlayListDynamicRequest(ctx, new TaskCallBack() {
            @Override
            public void callback(int code, String msg,
                                 String errorCode, Object object) {
                if (code == TaskCallBack.CODE_OK) {
                    CommonResponse<MusicListModel> response = (CommonResponse<MusicListModel>) object;
                    MusicListModel musicListModel = response.getData();
                    if (musicListModel != null) {
                        callback.onGetAudioListSuccess(musicListModel);
                    }
                } else {
                    callback.onGetAudioListSuccess(null);
                }
            }

        }, isSync);
    }


    //请求播放地址
    public void getAudioDetail(String detailId, String type, final OnGetURLResponseCallback callback, boolean isSync) {
        GetMusicDetailDynamicParameter params = new GetMusicDetailDynamicParameter(detailId,type);
        Map<String, String> queris = new HashMap<>();
        queris.put("detailId", detailId);
        queris.put("type", type);
        params.setQueris(queris);
//        getMusicDetailDynamicRequest(ContextProvider.getApplicationContext(), callback
//                , isSync).execute(params.combineParams());

    }

    public static final GetMusicURLDynamicRequest getMusicDetailDynamicRequest(Context ctx, final OnGetURLResponseCallback callback, boolean isSync) {
        return new GetMusicURLDynamicRequest(ctx, new TaskCallBack() {
            @Override
            public void callback(int code, String s, String s1, Object object) {
                CommonResponse<MusicUrlInfo> response = (CommonResponse<MusicUrlInfo>) object;
                if (response != null) {
                    MusicUrlInfo model = response.getData();
                    callback.onGetURLDetailSuccess(model);
                } else {
                    callback.onGetURLDetailSuccess(null);
                }
            }
        }, isSync);
    }

    //请求视频专辑列表
    public void getVideoList(String albumId, String page, final OnVideoResponseCallback callback, boolean isSync) {
        VideoListParameter params = new VideoListParameter(albumId, "0", page);
        VideoListRequest request = videoListRequest(ContextProvider.getApplicationContext(), callback, isSync);
        // Trace.Debug("--->videolist url: " + request.getRequestUrl(params.combineParams()).buildUrl());
        // request.execute(params.combineParams());
    }
    public static final VideoListRequest videoListRequest(Context ctx, final OnVideoResponseCallback callback, boolean isSync) {
        return new VideoListRequest(ctx, new TaskCallBack() {
            @Override
            public void callback(int code, String msg,
                                 String errorCode, Object object) {
                CommonResponse<VideoListResponseModel> response = (CommonResponse<VideoListResponseModel>) object;
                if (response != null) {
                    VideoListResponseModel model = response.getData();
                    callback.onGetVideoListSuccess(model);
                } else {
                    callback.onGetVideoListSuccess(null);
                }

            }
        }, isSync);

    }


    public interface OnAudioResponseCallback {
        void onGetAudioListSuccess(MusicListModel musicListModel);
    }

    public interface OnVideoResponseCallback {
        void onGetVideoListSuccess(VideoListResponseModel model);
    }

    public interface OnAudioDetailResponseCallback {
        void onGetAudioDetailSuccess(MusicDetailModel musicDetailModel);
    }

    public interface OnGetURLResponseCallback {
        void onGetURLDetailSuccess(MusicUrlInfo musicDetailModel);
    }

    public interface OnTopicListResponseCallback {
        void onGetTopicListSuccess(List<MediaDetail> modelList);
    }


    public interface OnGetPlayInfoCallback {
//        void onGetFirstPlayItemSuccess(List<MediaDetail> mediaList);

        void onGetPlayListSuccess(List<MediaDetail> mediaList);

        void onGetVideoListSuccess(List<PositiveSeries> positiveSeries);

        void onGetDataError();
    }
    //需要起播pagenum,获取视频列表
    public void getVideoInfoAndList(final LeAlbumInfo focus, final int page, final OnGetPlayInfoCallback callback){
        final List<PositiveSeries> positiveSeries=new ArrayList<>();
        final String pid = focus.ALBUM_ID;
//        getAudioDetail("", "", pid, "", new MusicListNetProvider.OnAudioDetailResponseCallback() {
//            @Override
//            public void onGetAudioDetailSuccess(final MusicDetailModel musicDetailModel) {
//                if (musicDetailModel == null) {
//                    callback.onGetDataError();
//                    return;
//                }
//                final int pageNum = musicDetailModel.getPageNum();
//
//            }
//        },false);
        // final int pageNum = 0;
        getVideoList(focus.ALBUM_ID, String.valueOf(page), new OnVideoResponseCallback() {
            @Override
            public void onGetVideoListSuccess(VideoListResponseModel responseModel) {
                if (responseModel != null) {
                    VideoListModel[] seriesModel = responseModel.getAlbums();
                    if (seriesModel != null) {
                        if (seriesModel[0] == null) {
                            PositiveSeries pPositiveSeries=new PositiveSeries();
                            pPositiveSeries.setPage(page);
                            pPositiveSeries.setPageSize(1);
                            List<MediaDetail> modelList = new ArrayList<>();
                            String playType = focus.ALBUM_TYPE_ID;
                            MediaDetail info = new MediaDetail();
                            // info.NAME=(musicDetailModel.getName());//歌曲名称
                            // info.AUDIO_ID= musicDetailModel.getId()+"";//歌曲id
                            info.IMG_URL=(focus.IMG_URL);//图片的Url
                            info.ALBUM_ID=focus.ALBUM_ID;////专辑id
                            info.setPlayType(playType);//区分视频，音频
                            info.TYPE= SortType.SORT_LE_RADIO;//自己区分歌曲的类型
                            info.ALBUM=(focus.NAME);//专辑名称
                            info.channelType=focus.channelType;
                            // info.setDuration(musicDetailModel.getDuration()/1000);//音乐的时间
                            // info.SOURCE_URL= CmfHelper.getInstance().getLinkShellUrl(musicDetailModel.getPlayUrl(),true);
                            modelList.add(info);
                            pPositiveSeries.setPositivieSeries(modelList);
                            positiveSeries.add(pPositiveSeries);
                        } else {
                            for (int i=0;i<seriesModel.length;i++){
                                PositiveSeries pPositiveSeries=new PositiveSeries();
                                pPositiveSeries.setPage(seriesModel[i].getPage());
                                // pPositiveSeries.setPageSize(seriesModel[i].getPageSize());
                                if (seriesModel[i] !=null){
                                    pPositiveSeries.setPositivieSeries(changeToMediaInfo(seriesModel, focus));
                                }
                                positiveSeries.add(pPositiveSeries);
                            }

                        }
                    }else {
                        PositiveSeries pPositiveSeries=new PositiveSeries();
                        pPositiveSeries.setPage(page);
                        pPositiveSeries.setPageSize(1);
                        List<MediaDetail> modelList = new ArrayList<>();
                        String playType = focus.ALBUM_TYPE_ID;
                        MediaDetail info = new MediaDetail();
                        // info.NAME=(musicDetailModel.getName());//歌曲名称
                        // info.AUDIO_ID= musicDetailModel.getId()+"";//歌曲id
                        info.IMG_URL=(focus.IMG_URL);//图片的Url
                        info.ALBUM_ID=focus.ALBUM_ID;////专辑id
                        info.setPlayType(playType);//区分视频，音频
                        info.TYPE= SortType.SORT_LE_RADIO;//自己区分歌曲的类型
                        info.ALBUM=(focus.NAME);//专辑名称
                        // info.setDuration(musicDetailModel.getDuration()/1000);//音乐的时间
                        // info.SOURCE_URL= CmfHelper.getInstance().getLinkShellUrl(musicDetailModel.getPlayUrl(),true);
                        modelList.add(info);
                        pPositiveSeries.setPositivieSeries(modelList);
                        positiveSeries.add(pPositiveSeries);
                    }
                }
                callback.onGetVideoListSuccess(positiveSeries);
            }
        },false);

    }
    //获取视频列表，分页加载
    public void getVideoInfoAndListTwo(final LeAlbumInfo focus, final OnGetPlayInfoCallback callback,String argPage){
        final List<PositiveSeries> positiveSeries=new ArrayList<>();
        getVideoList(focus.ALBUM_ID, argPage, new OnVideoResponseCallback() {
            @Override
            public void onGetVideoListSuccess(VideoListResponseModel responseModel) {
                if (responseModel != null) {
                    VideoListModel[] seriesModel = responseModel.getAlbums();
                    if (seriesModel != null) {
                        if (seriesModel[0] == null) {
                            return;
                        } else {
                            for (int i=0;i<seriesModel.length;i++){
                                PositiveSeries pPositiveSeries=new PositiveSeries();
                                pPositiveSeries.setPage(seriesModel[i].getPage());
                                // pPositiveSeries.setPageSize(seriesModel[i].getPageSize());
                                if (seriesModel!=null){
                                    pPositiveSeries.setPositivieSeries(changeToMediaInfo(seriesModel, focus));
                                }
                                positiveSeries.add(pPositiveSeries);
                            }

                        }
                    }
                }
                callback.onGetVideoListSuccess(positiveSeries);
            }
        },false);

    }
    private List<MediaDetail> changeToMediaInfo(VideoListModel[] models, LeAlbumInfo focus) {
        List<MediaDetail> modelList = new ArrayList<>();
        String playType = focus.ALBUM_TYPE_ID;
        if (models != null) {
            for (int i = 0; i < models.length; i++) {

                VideoListModel item = models[i];
                MediaDetail info = new MediaDetail();
                info.NAME=(item.getName());//歌曲名称
                info.AUDIO_ID=item.getDetailId();//歌曲id
                info.IMG_URL=(item.getImg());//图片的Url
                info.ALBUM_ID=focus.ALBUM_ID;////专辑id
                info.setPlayType(playType);//区分视频，音频
                info.TYPE= SortType.SORT_LE_RADIO;//自己区分歌曲的类型
                info.setDuration(item.getDuration());//歌曲的时长，单位s
                info.ALBUM=(focus.NAME);//专辑名称               info.AUTHOR = item.getAuthor();
                modelList.add(info);
          }
        }
        return modelList;
    }

    public void getAudioInfoAndList(final LeAlbumInfo focus, final int page, final OnGetPlayInfoCallback callback){
        final List<MediaDetail> queue = new ArrayList<>();
        getAudioList(focus.ALBUM_ID, page, new OnAudioResponseCallback() {
            @Override
            public void onGetAudioListSuccess(MusicListModel musicListModel) {
                if (musicListModel != null) {
                    ArrayList<MusicListItem> musicListItems = (ArrayList<MusicListItem>) musicListModel.getAudios();
                    if (musicListItems != null) {
                        for (int i = 0; i < musicListItems.size(); i++) {
                            MusicListItem item = musicListItems.get(i);
                            MediaDetail model = new MediaDetail();
                            model.NAME=(item.getName());//歌曲名称
                            model.setDuration(Integer.parseInt(item.getDuration())/1000);//歌曲的时间
//                            model.AUDIO_ID=String.valueOf(item.getAudioid());//歌曲id
//                            model.ALBUM_ID=String.valueOf(item.getOstid());//专辑id
                            model.IMG_URL=(focus.IMG_URL);//图片的Url
                            model.TYPE= SortType.SORT_LE_RADIO;//自己区分歌曲的类型
                            model.ALBUM=(focus.NAME);//专辑名称
                            model.setPlayType(focus.ALBUM_TYPE_ID);//区分视频，音频
                            model.setSourceName(item.getSourceName());//资源名称
//                            model.AUTHOR=item.getSingerName()+"";//作者名称
                            queue.add(model);

                        }
                    }
                }

                callback.onGetPlayListSuccess(queue);

            }
        },false);

    }

}
