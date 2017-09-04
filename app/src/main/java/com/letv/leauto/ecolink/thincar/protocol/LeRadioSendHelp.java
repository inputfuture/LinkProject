package com.letv.leauto.ecolink.thincar.protocol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.AlbumListAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.http.leradio.AlbumListLoader;
import com.letv.leauto.ecolink.http.leradio.AudioListLoader;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.leplayer.model.LTStatus;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.leauto.ecolink.ui.leradio_interface.data.PositiveSeries;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2016/11/1.
 */
public class LeRadioSendHelp {
    private static final String TAG = "LeRadioSendHelp";
    private static final String PIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String FILE_FORMAT = ".png";
    private static LeRadioSendHelp ourInstance = new LeRadioSendHelp();
    private static final int PLAYING_STATE = 1;//播放中
    private static final int WAITING_STATE = 2;//等待中

    private static final int PLAY = 1;//播放，继续
    private static final int PAUSE = 2;//暂停
    private static final int PRE = 3;//上一曲
    private static final int NEXT = 4;//下一曲

    private static final int IMAGE_WIDTH = 176;
    private static final int IMAGE_HEIGHT = 176;

    private static final int EACH_PAGE_COUNT = 20;

    /**
     * 保存最后一次发送播放状态
     */
    private int mCurrentPlayStatus = WAITING_STATE;

    /**
     * 保存最后一次发送播放歌曲
     */
    private String mCurrentPlayAlbumId = "";

    /**
     * 保存最后一次发送播放歌曲
     */
    private String mCurrentPlaySongId = "";

    /**
     * 保存正在播放歌曲在专辑位置
     */
    private int mCurrentPlaySongIndex = 0;

    private ExecutorService mThreadPool;

    private Context mContext;

    private LeAlbumInfo mAlbumInfo;

    private int mCurrentPageIndex = 1;

    /**
     * 是不是手机主动申请
     */
    private boolean isPhoneRequest;

    private List<PositiveSeries> mPositiveSeries;

    /**
     * 当前专辑所有已经申请到的数据
     */
    ArrayList<MediaDetail> mCurrentMediaDetails = new ArrayList<>();

    /**
     * 用于保存当前选择的Channel,不是正在播放的Channel.
     */
    private Channel mCurrentChooseChannel;

    /**
     * 保存当前播放歌曲所在Channel
     */
    private Channel mCurrentPlayChannel;

    /**
     * 当前频道所有专辑
     */
    ArrayList<LeAlbumInfo> mCurrentAlbumInfoList = new ArrayList<>();

    private int mCurrentAlbumIndex = 1;

    private RequestPlayerRunnable mRequestPlayerRunnable;

    /**
     * 保存最后一次请求播放专辑ID
     */
    private String mLatestRequestPlayAlbumId = "";

    /**
     * 暂时保存当前频道所有专辑
     */
    ArrayList<LeAlbumInfo> mTempAlbumInfoList = new ArrayList<>();

    private LoadingAlubState mLoadingAlubState = LoadingAlubState.END;

    public void initLeRadioSendHelp(Context context) {
        mContext = context.getApplicationContext();
    }

    public static LeRadioSendHelp getInstance() {
        return ourInstance;
    }

    private LeRadioSendHelp() {
        mThreadPool = Executors.newFixedThreadPool(1);
    }

    /**
     * 手机返回歌曲列表信息
     */
    public void requestAlbumList() {
        LePlayer player = EcoApplication.LeGlob.getPlayer();
        LeAlbumInfo album = player.getLeAlbumInfo();

//        if (mCurrentPlayStatus == PLAYING_STATE) {
//            notifyPlayStatus(album.ALBUM_ID, mCurrentPlayStatus);
//        }

        //没有歌曲列表，请求默认歌曲
        if (mCurrentAlbumInfoList.size() == 0) {
            isPhoneRequest = true;
            mCurrentChooseChannel = new Channel("1003436801", "热门", "", "1", "http://d.itv.letv.com/mobile/channel/data.json?pageid=1003436801", "14021151", "2");
            setCurrentPlayChannel(mCurrentChooseChannel);

            loadAlumData();
        } else {
            int currentIndex = findAlbumIndex(album);
            LeAlbumInfo detail = mCurrentAlbumInfoList.get(currentIndex);
            notifyPlayStatusToCar(detail, null, WAITING_STATE);
        }
    }

    private int findAlbumIndex(LeAlbumInfo album) {
        int index = 0;
        int size = mCurrentAlbumInfoList.size();
        for (int i = 0; i < size; i++) {
            LeAlbumInfo info = mCurrentAlbumInfoList.get(i);
            if (album != null && album.ALBUM_ID != null && info != null && info.ALBUM_ID != null
                    && album.ALBUM_ID.trim().equals(info.ALBUM_ID.trim())) {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * 封装待发送歌曲列表
     *
     * @param albumList    正在播放歌曲列表
     * @param currentIndex 正播放歌曲列表中的位置
     */
    private void sendAlbumList(final List<LeAlbumInfo> albumList, final int currentIndex) {
        new Thread() {

            @Override
            public void run() {
                ArrayList<LeAlbumInfo> sendList = new ArrayList<LeAlbumInfo>();

                Map<String, Object> map = new HashMap<>();
                map.put("Type", "Interface_Response");
                map.put("Method", "RequestSongsInfo");
                List<Map<String, Object>> list = new ArrayList<>();
                int i = -4;
                while (currentIndex + i < 0) {
                    i++;
                }

                for (; i <= 4 && (currentIndex + i) < albumList.size(); i++) {
//                    LeAlbumInfo detail = songList.get(currentIndex + i);
//                    Map<String, Object> songMap = new HashMap<>();
//                    songMap.put("name", detail.NAME);
//                    songMap.put("songid", detail.ALBUM_ID);
//                    songMap.put("position", i);
//                    list.add(songMap);
//                    sendList.add(detail);
                    if (i == 0) {
                        LeAlbumInfo detail = albumList.get(currentIndex + i);
                        notifyPlayStatusToCar(detail, null, WAITING_STATE);
                        break;
                    }
                }

//                Map<String, Object> songs = new HashMap<>();
//                songs.put("songs", list);
//
//                map.put("Parameter", songs);
//                JSONObject jsonObject = (JSONObject) JSON.toJSON(map);
//
//                DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.LE_RADIO_APPID,jsonObject);
//
//                for (LeAlbumInfo detail : sendList) {
//                    requestSongImage(detail);
//                }

            }
        }.start();
    }

    /**
     * 手机通知车机当前乐听播放状态
     */
    public void notifyPlayStatus(boolean isPlaying, int index) {
        LePlayer player = EcoApplication.LeGlob.getPlayer();

        mCurrentPlaySongIndex = index;

        int playStatus = isPlaying ? PLAYING_STATE : WAITING_STATE;
        LeAlbumInfo albumInfo = player.getLeAlbumInfo();
        ArrayList<MediaDetail> songList = player.getPlayerList();
        if (albumInfo != null) {
            String albumId = albumInfo.ALBUM_ID;

            if (playStatus == PLAYING_STATE && !TextUtils.isEmpty(mLatestRequestPlayAlbumId) && !mLatestRequestPlayAlbumId.equals(albumId)) {
                return;
            }

            MediaDetail songInfo = null;
            if (songList != null && index < songList.size()) {
                songInfo = songList.get(index);
            }

            if (mCurrentPlayAlbumId == null || mCurrentPlaySongId == null) {
                return;
            }

            if (mCurrentPlayStatus != playStatus || !mCurrentPlayAlbumId.equals(albumId)
                    || (songInfo != null && songInfo.AUDIO_ID != null && !mCurrentPlaySongId.equals(songInfo.AUDIO_ID))) {//播放状态和当前保存状态不一致时才发送
                mCurrentPlayStatus = playStatus;
                mCurrentPlayAlbumId = albumId;
                if (songInfo != null) {
                    mCurrentPlaySongId = songInfo.AUDIO_ID;
                }

                notifyPlayStatusToCar(albumInfo, songInfo, mCurrentPlayStatus);
                clearLatestRequestPlayAlbumId();
            }
        }
    }

    /**
     * 车机根据songId请求图片。
     */
    public void requestImageById(String albumId) {
        for (LeAlbumInfo detail : mCurrentAlbumInfoList) {
            if (detail.ALBUM_ID.equalsIgnoreCase(albumId)) {
                requestAlbumImage(detail);
            }
        }
    }

    /**
     * 车机请求歌曲信息
     */
    public void requestAlbumInfo(final String albumId, final String positon) {
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                int index = Integer.parseInt(positon);
                int target = 0;
                for (int i = 0; i < mCurrentAlbumInfoList.size(); i++) {
                    LeAlbumInfo detail = mCurrentAlbumInfoList.get(i);
                    if (detail.ALBUM_ID.equalsIgnoreCase(albumId)) {
                        target = i + index;
                        if (0 <= target && target < mCurrentAlbumInfoList.size()) {
                            sendSingleAlbum(detail, mCurrentAlbumInfoList.get(i + index), index);
                        }

                        break;
                    }
                }

               checkNeedLoadAlbum(target);
            }
        });
    }

    private synchronized void checkNeedLoadAlbum(int index) {
        int size = mCurrentAlbumInfoList.size();
        if (size == 1) {//语音或者本地昨时专辑不触发分布加载
            LeAlbumInfo albumInfo = mCurrentAlbumInfoList.get(0);
            if (albumInfo.TYPE.equals(SortType.SORT_LOCAL) || albumInfo.TYPE.equals(SortType.SORT_LE_RADIO_LOCAL)
                    || albumInfo.TYPE.equals(SortType.SORT_LOCAL_ALL) || albumInfo.TYPE.equals(SortType.SORT_VOICE)) {
                return;
            }
        }
        if (index + 10 >= size && mLoadingAlubState.equals(LoadingAlubState.END)) {
            int lastPageCount = size % 20;
            LogUtils.i("checkNeedLoadAlbum","mCurrentAlbumInfoList size:" + size);
            if (lastPageCount == 0) {
                mCurrentAlbumIndex = size / 20 + 1;
            } else {
                mCurrentAlbumIndex = size / 20 + 2;
            }

            LogUtils.i("checkNeedLoadAlbum","mCurrentAlbumInfoList mCurrentAlbumIndex:" + mCurrentAlbumIndex);
            loadAlumData();
        }
    }

    /**
     * 发送单一歌曲信息给车机
     *
     * @param centerAlbumDetail 中心歌曲信息
     * @param sendAlbumDetail   请求的歌曲信息
     * @param positon           请求歌曲位置偏移
     */
    private void sendSingleAlbum(LeAlbumInfo centerAlbumDetail, LeAlbumInfo sendAlbumDetail, int positon) {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", "RequestAlbumInfo");
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> centerSongMap = new HashMap<>();
        centerSongMap.put("albumname", centerAlbumDetail.NAME);
        centerSongMap.put("albumid", centerAlbumDetail.ALBUM_ID);
        centerSongMap.put("position", 0);
        list.add(centerSongMap);

        Map<String, Object> sendSongMap = new HashMap<>();
        sendSongMap.put("albumname", sendAlbumDetail.NAME);
        sendSongMap.put("albumid", sendAlbumDetail.ALBUM_ID);
        sendSongMap.put("position", positon);
        list.add(sendSongMap);

        Map<String, Object> songs = new HashMap<>();
        songs.put("albums", list);

        map.put("Parameter", songs);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.LE_RADIO_APPID, jsonObject);

        requestAlbumImage(sendAlbumDetail);
    }

    /**
     * 从网络请求图片数据并发送
     */
    private void requestAlbumImage(LeAlbumInfo Album) {
        httpRequest(Album.getRealImgUrl(), Album);
    }

    private void httpRequest(String imageUrl, LeAlbumInfo album) {
        if (null == imageUrl || "".equals(imageUrl)) {
            return;
        }

        try {
            OkHttpClient client = new OkHttpClient();

            //获取请求对象
            Request request = new Request.Builder().url(imageUrl).build();
            //获取响应体
            ResponseBody body = client.newCall(request).execute().body();
            //获取流
            InputStream in = body.byteStream();
            //转化为bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            Bitmap newBitmap = Utils.centerSquareScaleBitmap(bitmap, IMAGE_WIDTH);
            saveAndSendPic(newBitmap, album.ALBUM_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 车机端请求手机操作
     */
    public void requestPlayerAction(String albumId, String action) {
        LogUtils.i(TAG, "requestPlayerAction albumId:" + albumId + "   action:" + action);
        mLatestRequestPlayAlbumId = albumId;
        if (mRequestPlayerRunnable != null) {
            handler.removeCallbacks(mRequestPlayerRunnable);
        }
        mRequestPlayerRunnable = new RequestPlayerRunnable(albumId, action);
        handler.postDelayed(mRequestPlayerRunnable, 100);
    }

    private void playAlbum(LeAlbumInfo album) {
        mCurrentMediaDetails.clear();
        mCurrentPageIndex = 1;
        loadCurrenAudioData(album);
    }

    /**
     * 返回当前乐听播放状态
     *
     * @param
     */
    private void notifyPlayStatusToCar(LeAlbumInfo albumInfo, MediaDetail songInfo, int status) {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Notify");
        map.put("Method", "NotifyPlayerStatus");

        Map<String, Object> item = new HashMap<>();
        item.put("albumid", albumInfo.ALBUM_ID);
        item.put("albumname", albumInfo.NAME);
        if (songInfo != null) {
            item.put("songname", songInfo.NAME);
        } else {
            item.put("songname", "");
        }
        item.put("status", status);

        map.put("Parameter", item);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.LE_RADIO_APPID, jsonObject);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MessageTypeCfg.MSG_SUBITEMS_OBTAINED:
                    LogUtils.i(TAG, "requestLeRadionData MSG_SUBITEMS_OBTAINED");
                    LeObject<LeAlbumInfo> result = (LeObject<LeAlbumInfo>) msg.obj;
                    List<LeAlbumInfo> list = null;
                    if (result != null) {
                        list = result.list;
                    }

                    if (list != null && list.size() > 0 && !mCurrentAlbumInfoList.containsAll(list)) {
                        mCurrentAlbumInfoList.addAll(list);

                        if (isPhoneRequest) {
                            isPhoneRequest = false;
                            LeAlbumInfo detail = list.get(2);
                            notifyPlayStatusToCar(detail, null, WAITING_STATE);
                        }
                    }

                    mLoadingAlubState = LoadingAlubState.END;
//                    mCurrentPageIndex++;
//                    loadAlumData();
                    break;
                case MessageTypeCfg.MSG_GET_AUDIOLIST:
                    LogUtils.i(TAG, "requestLeRadionData MSG_GET_AUDIOLIST");
                    ArrayList<MediaDetail> sonResult = (ArrayList<MediaDetail>) msg.obj;
                    if (sonResult != null && sonResult.size() > 0) {
                        playSongList(sonResult);
                    }
                    break;
                case MessageTypeCfg.NO_MORE_DATA:
                    mLoadingAlubState = LoadingAlubState.ALUBMEND;
                    break;
                case MessageTypeCfg.MSG_GETDATA_EXCEPTION:
                    mLoadingAlubState = LoadingAlubState.END;
                    break;
                case MessageTypeCfg.MSG_GETDATA_FAILED:
                    mLoadingAlubState = LoadingAlubState.ALUBMEND;
                    break;
                case MessageTypeCfg.MSG_GET_VIDEOLIST:
                    mPositiveSeries = (List<PositiveSeries>) msg.obj;
                    if (mPositiveSeries != null && mPositiveSeries.size() > 0) {
                        playSongList(mPositiveSeries.get(0).getPositivieSeries());
                    }
                    break;
            }
        }
    };

    private void playSongList(List<MediaDetail> sonResult) {
        if (!mLatestRequestPlayAlbumId.equals(sonResult.get(0).ALBUM_ID)) {
            return;
        }
        mCurrentMediaDetails.addAll(sonResult);
        LePlayer lePlayer = EcoApplication.LeGlob.getPlayer();
        lePlayer.TYPE = 1;
        lePlayer.setPlayerList(mCurrentMediaDetails);
        lePlayer.setAlbumInfo(mAlbumInfo);
        lePlayer.playList(0);
    }

    private void saveAndSendPic(Bitmap bitmap, String albumId) {
        File sysFile = new File(PIC_PATH);
        File filePic = new File(sysFile, albumId + FILE_FORMAT);

        LogUtils.i(TAG, "responsAlbumPic file name:" + filePic.getName());
        if (filePic.exists()) {
            filePic.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.flush();
            out.close();

            byte[] picData = Utils.convertFileToBytes(filePic);

            byte[] sendData = new byte[picData.length + 64];
            byte[] albumIdData = albumId.getBytes();

            System.arraycopy(albumIdData, 0, sendData, 0, albumIdData.length);
            System.arraycopy(picData, 0, sendData, 64, picData.length);

            DataSendManager.getInstance().sendPicDataToCar(ThinCarDefine.ProtocolAppId.LE_RADIO_APPID, sendData, DataSendManager.DATA_TYPE_PICTURE);
            filePic.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCurrenAudioData(LeAlbumInfo albuminfo) {
        LogUtils.i(TAG, "loadCurrenAudioData mCurrentPageIndex:" + mCurrentPageIndex);
        AudioListLoader loader = new AudioListLoader(mContext, handler, albuminfo);
        loader.load(mCurrentPageIndex);
    }

    public void setCurrentChooseChannel(Channel channel) {
        mCurrentChooseChannel = channel;
    }

    public Channel getCurrentChooseChannel() {
        return mCurrentChooseChannel;
    }

    public void setCurrentPlayChannel(Channel channel) {
        boolean isVoiceOrLocal = isVoiceOrLocal();
        if (isVoiceOrLocal || mCurrentPlayChannel == null || !mCurrentPlayChannel.equals(channel)) {/* 频道有变化，请求频道中的专辑数据 **/
            clearLatestRequestPlayAlbumId();
            mCurrentAlbumInfoList.clear();
            mCurrentAlbumInfoList.addAll(mTempAlbumInfoList);
            mCurrentAlbumIndex = 1;
            mCurrentPlayChannel = channel;
            //loadAlumData();
        }
    }

    private void loadAlumData() {
        mLoadingAlubState = LoadingAlubState.LOADING;
        AlbumListLoader loader = new AlbumListLoader(mContext, handler, mCurrentPlayChannel);
        loader.load(mCurrentPlayChannel.getPageId(), mCurrentAlbumIndex);
    }

    public void requestPlayerStatus() {
        LePlayer player = EcoApplication.LeGlob.getPlayer();
        LeAlbumInfo album = player.getLeAlbumInfo();

        if (album != null) {/** 播放过歌曲*/
            ArrayList<MediaDetail> songList = player.getPlayerList();
            MediaDetail songInfo = null;
            if (songList != null && mCurrentPlaySongIndex < songList.size()) {
                songInfo = songList.get(mCurrentPlaySongIndex);
            }
            notifyPlayStatusToCar(album, songInfo, mCurrentPlayStatus);
        } else {/** 没有播放过歌曲*/
            if (mCurrentAlbumInfoList.size() == 0) {/** 没有请求过数据 */
                isPhoneRequest = true;
                mCurrentChooseChannel = new Channel("1003436801", "热门", "", "1", "http://d.itv.letv.com/mobile/channel/data.json?pageid=1003436801", "14021151", "2");
                setCurrentPlayChannel(mCurrentChooseChannel);

                loadAlumData();
            } else {/** 请求过数据 */
                int currentIndex = 0;
                if (mCurrentAlbumInfoList.size() > 2) {
                    currentIndex = 2;
                }
                LeAlbumInfo detail = mCurrentAlbumInfoList.get(currentIndex);
                notifyPlayStatusToCar(detail, null, WAITING_STATE);
            }
        }
    }

    public void setTempAlbumList(List<LeAlbumInfo> list) {
        mTempAlbumInfoList.clear();
        mTempAlbumInfoList.addAll(list);
    }

    public void updateCurrentAlbumList(Channel channel, ArrayList<LeAlbumInfo> albums) {
        if (channel != null && mCurrentPlayChannel != null && channel.equals(mCurrentPlayChannel)) {
            if (!mCurrentAlbumInfoList.containsAll(albums)) {
                mCurrentAlbumInfoList.addAll(albums);
            }
        }
    }

    public void setLocalRadioAlbum(LeAlbumInfo info) {
        clearLatestRequestPlayAlbumId();
        mCurrentAlbumInfoList.clear();
        mCurrentAlbumInfoList.add(info);
        LogUtils.i("lishixing","setLocalRadioAlbum size:" + mCurrentAlbumInfoList.size());
    }

    public class RequestPlayerRunnable implements Runnable {
        private String albumId;
        private String action;

        public RequestPlayerRunnable(String id, String act) {
            albumId = id;
            action = act;
        }

        @Override
        public void run() {
            if (mCurrentAlbumInfoList.size() == 0) {
                return;
            }

            int value = PLAY;
            try {
                value = Integer.parseInt(action);
            } catch (Exception e) {
                e.printStackTrace();
            }
            LePlayer player = EcoApplication.LeGlob.getPlayer();
            player.openServiceIfNeed();
            player.TYPE = 1;

            int index = 0;
            for (int i = 0; i < mCurrentAlbumInfoList.size(); i++) {
                if (mCurrentAlbumInfoList.get(i).ALBUM_ID.equalsIgnoreCase(albumId)) {
                    index = i;
                }
            }

            LeAlbumInfo request = mCurrentAlbumInfoList.get(index);
            LeAlbumInfo current = player.getLeAlbumInfo();
            mAlbumInfo = request;
            boolean isSameAlbum = false;
            if (current != null && request != null && request.ALBUM_ID != null && current.ALBUM_ID != null
                    && current.ALBUM_ID.equalsIgnoreCase(request.ALBUM_ID)) {
                isSameAlbum = true;
            }

            switch (value) {
                case PLAY:
                    BaseActivity.isStoped = false;
                    if (!player.getCurrentStatus().isPlaying) {//没有在播放
                        if (isSameAlbum) {
                            setPlayerType(player);
                            player.startPlay();
                        } else {
                            playAlbum(request);
                        }
                    } else {//已经在播放
                        if (!isSameAlbum) {
                            playAlbum(request);
                        }
                    }
                    break;
                case PAUSE:
                    player.stopPlay();
                    BaseActivity.isStoped = true;
                    break;
                case NEXT:
                    BaseActivity.isStoped = false;
                    setPlayerType(player);
                    player.playNext(false);
                    break;
                case PRE:
                    BaseActivity.isStoped = false;
                    setPlayerType(player);
                    player.playPrev();
                    break;
            }

        }
    }

    public void clearLatestRequestPlayAlbumId() {
        mLatestRequestPlayAlbumId = "";
    }

    /**
     * 保存当前请求专辑状态：
     * LOADING：表示正在加载专辑
     * END：表示加载结束或者没有加载
     * ALUBMEND：表这个Channel所有专辑加载完毕
     */
    public enum LoadingAlubState {
        LOADING, END, ALUBMEND
    }

    public void setPlayerType(LePlayer player) {
        LeAlbumInfo albumInfo = player.getLeAlbumInfo();
        if (!BaseActivity.isStoped && !TextUtils.isEmpty(albumInfo.TYPE)) {
            if (albumInfo.TYPE.equals(SortType.SORT_LOCAL) || albumInfo.TYPE.equals(SortType.SORT_LE_RADIO_LOCAL) || albumInfo.TYPE.equals(SortType.SORT_LOCAL_ALL)) {
                GlobalCfg.MUSIC_TYPE = Constant.TAG_LOCAL;
                player.OPEN_LOCAL = true;
                player.TYPE = 3;
            } else if (albumInfo.TYPE.equals(SortType.SORT_LE_RADIO)) {
                GlobalCfg.MUSIC_TYPE = Constant.TAG_LERADIO;
                player.OPEN_LERADIO = true;
                player.TYPE = 1;
            } else {
                GlobalCfg.MUSIC_TYPE = Constant.TAG_LERADIO;
                player.OPEN_LERADIO = true;
                player.TYPE = 1;
            }
        }
    }

    /**
     * 判断当前正在播放的是不是本地或者语音搜索专辑
     * @return
     */
    private boolean isVoiceOrLocal() {
        int size = mCurrentAlbumInfoList.size();
        if (size == 1) {//语音或者本地昨时专辑不触发分布加载
            LeAlbumInfo albumInfo = mCurrentAlbumInfoList.get(0);
            if (albumInfo.TYPE.equals(SortType.SORT_LOCAL) || albumInfo.TYPE.equals(SortType.SORT_LE_RADIO_LOCAL)
                    || albumInfo.TYPE.equals(SortType.SORT_LOCAL_ALL) || albumInfo.TYPE.equals(SortType.SORT_VOICE)) {
                return true;
            }
        }
        return false;
    }
}