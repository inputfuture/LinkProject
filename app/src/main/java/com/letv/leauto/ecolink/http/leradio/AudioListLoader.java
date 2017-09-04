package com.letv.leauto.ecolink.http.leradio;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.ui.leradio_interface.data.LeRadioBaseModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicListItem;
import com.letv.leauto.ecolink.ui.leradio_interface.data.MusicListModel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.PositiveSeries;
import com.letv.leauto.ecolink.ui.leradio_interface.globalHttpPathConfig.GlobalHttpPathConfig;
import com.letv.leauto.ecolink.ui.leradio_interface.parameter.VideoListParameter;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.mobile.core.utils.Constants;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 加载LeRadio专辑下的节目列表
 * Created by kevin on 2016/12/21.
 */
public class AudioListLoader extends BaseLoader {

    public LeAlbumInfo mAlbum;

    public AudioListLoader(Context ctx, Handler handler, LeAlbumInfo album) {
        super(ctx, handler);
        mAlbum = album;
    }

    public void load(int page) {
        String url = null;
//        switch (mAlbum.ALBUM_TYPE_ID) {
//            case Constants.MEDIA_PLAY_TYPE_VIDEO://视频
//            case Constants.MEDIA_PLAY_TYPE_VIDEOLIST:
//            case Constants.MEDIA_PLAY_TYPE_VIDEO1:
//                url = GlobalHttpPathConfig.BASE_URL + GlobalHttpPathConfig.GET_VIDEO_LIST;
//                break;
//            case Constants.MEDIA_PLAY_TYPE_AUDIO://音频
//            case Constants.MEDIA_PLAY_TYPE_AUDIOLIST:
//                url = GlobalHttpPathConfig.BASE_URL + GlobalHttpPathConfig.GET_MUSIC_LIST;
//                break;
//            default:
//                break;
//
//        }
        url = GlobalHttpPathConfig.BASE_URL + GlobalHttpPathConfig.GET_MUSIC_LIST;
        if (TextUtils.isEmpty(url)) {
            return;
        }
        VideoListParameter parameter = new VideoListParameter(mAlbum.ALBUM_ID, mAlbum.ALBUM_TYPE_ID, String.valueOf(page));
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
        Message message = Message.obtain();
        message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
        List<MediaDetail> details = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(response);
            int code = json.getInt(RESPOSE_CODE);
            if (code != CODE_SUCCESS) {
                message.what = MessageTypeCfg.MSG_GETDATA_FAILED;
            } else {
                MusicListModel list = MusicListModel.parse(json.getJSONObject(LeRadioBaseModel.MEDIA_LIST_DATA));
                for (MusicListItem item : list.getAudios()) {
                    MediaDetail detail = new MediaDetail();
                    detail.NAME=(item.getName());//歌曲名称
                    if(item.getDuration()!=null) {
                        detail.setDuration(Integer.parseInt(item.getDuration()) / 1000);//歌曲的时间
                    }
                    detail.AUDIO_ID=String.valueOf(item.getmediaId());//歌曲id
                    detail.ALBUM_ID=String.valueOf(item.getAlbumId());//专辑id
                    detail.IMG_URL= item.getImg();//图片的Url
                    detail.TYPE= SortType.SORT_LE_RADIO;//自己区分歌曲的类型
                    detail.AUTHOR = item.getsinger();
                    detail.ALBUM = mAlbum.NAME;//专辑名称
                    detail.setPlayType(item.getmediaType());//区分视频，音频
                    detail.setSourceName(item.getSourceName());//资源名称
                    detail.AUTHOR=item.getsinger();//作者名称
                    details.add(detail);
                }

                switch (mAlbum.ALBUM_TYPE_ID) {
                    case Constants.MEDIA_PLAY_TYPE_VIDEO://视频
                    case Constants.MEDIA_PLAY_TYPE_VIDEOLIST:
                    case Constants.MEDIA_PLAY_TYPE_VIDEO1:
                        /*TO-DO 视频列表的层级太深，后续需要做成跟音频一致 */
                        if (list.getAudios().size() > 0) {
                            PositiveSeries pPositiveSeries = new PositiveSeries();
                            List<PositiveSeries> positiveSeries=new ArrayList<>();
                            pPositiveSeries.setPage(list.getAudios().get(0).getPage());
                            pPositiveSeries.setPositivieSeries(details);
                            positiveSeries.add(pPositiveSeries);
                            message.what = MessageTypeCfg.MSG_GET_VIDEOLIST;
                            message.obj = positiveSeries;
                        }else{
                            message.what = MessageTypeCfg.MSG_GET_VIDEOLIST;
                            message.obj = null;
                        }

                        break;
                    case Constants.MEDIA_PLAY_TYPE_AUDIO://音频
                    case Constants.MEDIA_PLAY_TYPE_AUDIOLIST:
                        if (list.getAudios().size() > 0) {
                            mAlbum.PAGE_ID = String.valueOf(list.getAudios().get(0).getPage());
                            message.what = MessageTypeCfg.MSG_GET_AUDIOLIST;
                            message.obj = details;
                        }else{
                            message.what = MessageTypeCfg.MSG_GET_AUDIOLIST;
                            message.obj = null;
                        }

                        break;
                    default:
                        break;

                }

            }
        }catch (Exception e) {
            Trace.Debug("--->e: " + e.getMessage());
        }

        mHandler.sendMessage(message);

    }


}
