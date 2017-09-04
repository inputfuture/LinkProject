package com.letv.leauto.ecolink.leplayer;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.mixMedia.AudioManagerHelper;
import com.letv.leauto.ecolink.leplayer.mixMedia.LeService;
import com.letv.leauto.ecolink.leplayer.model.LTStatus;
import com.letv.leauto.ecolink.leplayer.model.OnStatusChangedListener;
import com.letv.leauto.ecolink.leplayer.model.PlayItem;
import com.letv.leauto.ecolink.receiver.BluetoothReceiver;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.umeng.AnalyzeManager;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.TelephonyUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaochao on 2015/6/9.
 */
public class LePlayer implements ServiceConnection, LeService.IndexChangeListener {

    public static final int MODE_ORDER = 1;
    public static final int MODE_SINGLE = 2;
    public static final int MODE_RANDOM = 3;
    private final AudioManager mAudioManager;
    private final AudioManagerHelper mAudioManagerHelper;
    private final ComponentName mBlueToothComponent;
    private Context context;
    private ArrayList<OnStatusChangedListener> statusListener;
    private MusicStateListener musicStateListener;
    private ListViewItemStateListener listViewItemStateListener;
    private IconStateListener mIconStateListener;
    private LeService service;
    private boolean isBinded;
    private volatile ArrayList<MediaDetail> mediaDetails;
    public static LTStatus mPreLtItem;
    private volatile int mIndex;
    private CacheUtils spUtils;
    private LeAlbumInfo mAlbumInfo;
    public static volatile int TYPE = -1; /*1为乐听，2为酷我，3为本地，*/
    public static boolean OPEN_LERADIO = false;
    public static boolean OPEN_KUWO;
    public static boolean OPEN_LOCAL;
    public static ArrayList<MediaDetail> leMediaDetails = new ArrayList<>();
    public static ArrayList<MediaDetail> kuwoMediaDetails = new ArrayList<>();
    public static ArrayList<MediaDetail> localMediaDetails = new ArrayList<>();
    public static volatile int LE_INDEX = -1;
    public static volatile int KUWO_INDEX = -1;
    public static volatile int LOCAL_INDEX = -1;
    public static volatile LeAlbumInfo LE_ALBUMINFO;
    public static volatile LeAlbumInfo KUWO_ALBUMINFO;
    public static volatile LeAlbumInfo LOCAL_ALBUMINFO;
    private Activity activity;
    private RemoteControlClient mRemoteControlClient;
    private boolean mFocusLosed;


    //记录酷我信息
    public LePlayer(Context context) {
        this.context = context;
        spUtils = CacheUtils.getInstance(context);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mBlueToothComponent = new ComponentName(context.getPackageName(),BluetoothReceiver.class.getName());
        mAudioManagerHelper = new AudioManagerHelper();
//        mAudioManagerHelper.setHasAudioFocus(requestAudioFocus());
        statusListener = new ArrayList<>();
    }

    public void onDuckBegin() {
        this.service.onDuckBegin();
    }

    public void onDuckEnd() {
        this.service.onDuckEnd();
    }
    private boolean requestAudioFocus() {;
        int result = mAudioManager.requestAudioFocus(audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            Trace.Debug("#####","....requestAudioFocus =false");
            // lePlayer.stopPlay();
            return false;
        } else {
            Trace.Debug("#####","....requestAudioFocus = true ");
            return true;
        }
    }

    public synchronized void openServiceIfNeed() {
        if (this.context != null) {
            Intent service = new Intent(this.context, LeService.class);
            this.context.startService(service);
            if (this.isBinded) {
                this.context.unbindService(this);
                this.isBinded = false;
            }
            this.context.bindService(service, this, Context.BIND_AUTO_CREATE);
        }
    }

    public synchronized void setActivity(Activity activity) {
        this.activity = activity;
        if (this.service != null) {
            service.setActivity(this.activity);
        }
    }

    public synchronized void saveCurrentItem() {
        mPreLtItem = getCurrentStatus();
        if (mPreLtItem != null && mPreLtItem.currentItem != null) {

            boolean isSavedItem = MediaOperation.getInstance().hasSavedLTItem(mPreLtItem.currentItem);
            Trace.Debug("LePlayer","####saveCurrentItem#### isSavedItem=" + isSavedItem);
            if(!isSavedItem) {
                MediaOperation.getInstance().insertLTItem(mPreLtItem.currentItem);
            }
        }
    }

    /**
     * 存储最后播放的一首音乐
     */
    private synchronized void saveLastMedia() {
        if (mAlbumInfo != null) {
            if (mIndex < 0) {
                mIndex = 0;
            }
            if (mediaDetails.size() > 0 && mIndex < mediaDetails.size()) {

                String playurl = mediaDetails.get(mIndex).SOURCE_URL;
                if (!TextUtils.isEmpty(playurl) && playurl.startsWith("/")) {
                    spUtils.putString(Constant.Radio.LAST_MUSIC_LOCAL_URL, playurl);
                } else {
                    spUtils.putString(Constant.Radio.LAST_MUSIC_URL, mediaDetails.get(mIndex).AUDIO_ID);
                }

                spUtils.putInt(Constant.Radio.LAST_POSITION, mIndex);
                Trace.Debug("LePlayer","####saveLastMedia#### mAlbumInfo=" + mAlbumInfo.toString());
                mAlbumInfo.ALBUM_ID = mediaDetails.get(mIndex).ALBUM_ID;
                mAlbumInfo.SORT_ID = mediaDetails.get(mIndex).channelType;
                mAlbumInfo.PAGE_ID = mediaDetails.get(mIndex).channelType;
                spUtils.putString(Constant.Radio.LAST_ALBUM, mAlbumInfo.toString());
                spUtils.putString(Constant.Radio.LAST_ALBUM_NAME, mAlbumInfo.NAME);
                spUtils.putString(Constant.Radio.LAST_IMG_URL, mAlbumInfo.IMG_URL);
                if (mPreLtItem != null && mPreLtItem.currentItem != null && mPreLtItem.currentItem.isLiveItem()) {
                    spUtils.putInt(Constant.Radio.LAST_POSITION, -1);
                }
//                    if (mAlbumInfo.PAGE_ID!=null)
                spUtils.putString(Constant.Radio.LAST_SORT_ID, mediaDetails.get(mIndex).channelType);
                //保存每首歌的id
//                    if (mediaDetails.get(mIndex).AUDIO_ID!=null)
                spUtils.putString(Constant.Radio.AUDIO_ID, mediaDetails.get(mIndex).AUDIO_ID);
                if((mediaDetails.get(mIndex).getSourceName()!=null&&mediaDetails.get(mIndex).getSourceName().contains("虾米")&&mediaDetails.get(mIndex).getSourceName().contains("XiaMi"))){
                    spUtils.putString(Constant.Radio.LAST_TYPE, SortType.SORT_VOICE);
                    mediaDetails.get(mIndex).XIA_MI_ID = mediaDetails.get(mIndex).SOURCE_CP_ID;
                }else {
                    spUtils.putString(Constant.Radio.LAST_TYPE, mediaDetails.get(mIndex).TYPE);
                }
                if(mediaDetails!= null) {
                    for (int i = 0; i < mediaDetails.size(); i++) {
                        if ((mediaDetails.get(i).getSourceName() != null && mediaDetails.get(i).getSourceName().contains("虾米") && mediaDetails.get(i).getSourceName().contains("XiaMi"))) {
                            mediaDetails.get(i).XIA_MI_ID = mediaDetails.get(mIndex).SOURCE_CP_ID;
                        }
                    }
                }
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder()
                        //.excludeFieldsWithoutExposeAnnotation() //不导出实体中没有用@Expose注解的属性
                        .enableComplexMapKeySerialization() //
                        .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")//
                        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)//
                        .setPrettyPrinting() //对json结果格式化.
                        .setVersion(1.0).
                                create();

                String LastMediaList = gson.toJson(mediaDetails);
                spUtils.putString(Constant.Radio.TOSTRING, LastMediaList);
                //Trace.Debug("LePlayer","#####Constant.Radio.TOSTRING=" + LastMediaList);
                //如果type=kuwo,LAST_KUWO_NET_OR_LOCAL=net,AUDIO_ID有值,ALBUM_ID=null,那么就是搜索
                //如果type=kuwo,LAST_KUWO_NET_OR_LOCAL=net,AUDIO_ID有值,ALBUM_ID有值,那么就是专辑
                //如果type=kuwo,LAST_KUWO_NET_OR_LOCAL=null,AUDIO_ID=null，ALBUM_ID=null,那么就是本地酷我
                //如果type!=kuwo,那就不是酷我
                //专辑id
//                    if (mAlbumInfo.ALBUM_ID!=null)
                Trace.Debug("LePlayer","####saveLastMedia#### ALBUM_ID=" + mAlbumInfo.ALBUM_ID + ",mediaDetails.get(mIndex).ALBUM_ID=" + mediaDetails.get(mIndex).ALBUM_ID);
                spUtils.putString(Constant.Radio.ALBUM_ID, mediaDetails.get(mIndex).ALBUM_ID);
                //酷我歌单的ID
//                    if (mAlbumInfo.KUWO_BILL_ID!=null)
//                spUtils.putString(Constant.Radio.KUWO_BILL_ID, mAlbumInfo.KUWO_BILL_ID);
//                    if (mediaDetails.get(mIndex).NAME!=null)
                spUtils.putString(Constant.Radio.MUSIC_NAME, mediaDetails.get(mIndex).NAME);
//                    if (mediaDetails.get(mIndex).AUTHOR!=null)
                spUtils.putString(Constant.Radio.AUDIO_AUTHOR, mediaDetails.get(mIndex).AUTHOR);
//                    if (mediaDetails.get(mIndex).ALBUM!=null)
                spUtils.putString(Constant.Radio.AUDIO_DETAIL, mediaDetails.get(mIndex).getSourceName());
                if (mAlbumInfo.TYPE != null && (SortType.SORT_VOICE.equals(mAlbumInfo.TYPE) || SortType.SORT_RECENT.equals(mAlbumInfo.TYPE) || SortType.SORT_FAVOR.equals(mAlbumInfo.TYPE))
                        || SortType.SORT_LOCAL.equals(mAlbumInfo.TYPE) || SortType.SORT_DOWNLOAD.equals(mAlbumInfo.TYPE) || SortType.SORT_KUWO.equals(mAlbumInfo.TYPE)||SortType.SORT_LE_RADIO_LOCAL.equals(mAlbumInfo.TYPE)) {

                } else {
//                    if (mediaDetails != null) {
//                        if (mIndex >= 0 && mIndex < mediaDetails.size()) {
//                            Trace.Debug("LePlayer","##### saveLastMedia");
//                            LTStatus currentStatus = getCurrentStatus();
//                            if (currentStatus != null) {
//                                if (currentStatus.currentItem != null) {
//                                    MediaOperation mediaOperation = MediaOperation.getInstance();
//                                    currentStatus.currentItem.setProgress(getCurrentStatus().progress);
//                                    mediaOperation.insertLTItem(currentStatus.currentItem);
//
//                                }
//                            }
//                        }
//                    }
//                    saveCurrentItem();
                }
                saveCurrentItem();
                sendWidgetBroadvcast();
            }
        }
        Trace.Debug("LePlayer","##### saveLastMedia");
//        homeActivity.initRadio();
    }

    public void closeServiceForce() {
        if (this.context != null) {
            Intent service = new Intent(this.context, LeService.class);
            if (this.isBinded) {
                this.context.unbindService(this);
                this.isBinded = false;
            }
            this.context.stopService(service);
        }
    }

    public synchronized void playNext(boolean delete) {
        mPreLtItem = getCurrentStatus();

        if (this.service != null) {
            requestAudioFocus();
            mPreLtItem = getCurrentStatus();
            if (mPreLtItem.currentItem != null && !delete) {
                MediaOperation.getInstance().insertLTItem(mPreLtItem.currentItem);
            }
            this.service.playNext();
            service.isFromuser = true;
            BaseActivity.isStoped = false;

            if (musicStateListener != null) {
                musicStateListener.musicStart();
            }
            if (mIconStateListener != null) {
                mIconStateListener.musicStart();
            }
            if (listViewItemStateListener != null) {
                listViewItemStateListener.musicStart();
            }
        }
    }

    private void removeMedia() {
        if (mediaDetails.size() > 0) {
            MediaDetail mediaDetail = mediaDetails.get(mIndex);
            if (mediaDetail.TYPE != null && (mediaDetail.TYPE.equals(SortType.SORT_LOCAL) || mediaDetail.TYPE.equals(SortType.SORT_LE_RADIO_LOCAL) || mediaDetail.TYPE.equals(SortType.SORT_KUWO_LOCAL) || mediaDetail.TYPE.equals(SortType.ALL_LOACL) || mediaDetail.TYPE.equals(SortType.SORT_LOCAL_ALL))) {
                if (mediaDetail.SOURCE_URL == null) {
                    mediaDetails.remove(mediaDetail);
                    service.remove(mediaDetail);
                }
            }
        }
    }

    public synchronized void playPrev() {
        for (OnStatusChangedListener onStatusChangedListener : statusListener) {
            onStatusChangedListener.onProgressChanged(0, -1);
        }
        mPreLtItem = getCurrentStatus();
        if (mPreLtItem.currentItem != null) {
            MediaOperation.getInstance().insertLTItem(mPreLtItem.currentItem);
        }
        if (this.service != null) {
            requestAudioFocus();
            service.isFromuser = true;
            this.service.playPrev();

            BaseActivity.isStoped = false;
            if (musicStateListener != null) {
                musicStateListener.musicStart();
            }
            if (mIconStateListener != null) {
                mIconStateListener.musicStart();
            }
            if (listViewItemStateListener != null) {
                listViewItemStateListener.musicStart();
            }

        }
    }

    public AudioManager getAudioManager() {
        return mAudioManager;
    }

    public synchronized void playList(int index)

    {

        Trace.Debug("LePlayer","######playList:index=" + index);
        mIndex = index;
        switch (TYPE) {
            case 1:
                LE_INDEX = index;
                break;
            case 2:
                KUWO_INDEX = index;
                break;
            case 3:
                LOCAL_INDEX = index;
                break;
            default:
        }
        Trace.Debug("LePlayer","######playList:this.service=" + this.service);
        if (this.service != null) {
            service.isFromuser = true;

            if (musicStateListener != null) {
                musicStateListener.musicStart();
            }
            if (mIconStateListener != null) {
                mIconStateListener.musicStart();
            }
            if (listViewItemStateListener != null) {
                listViewItemStateListener.musicStart();
            }
            requestAudioFocus();
            this.service.playList(index);
            mPreLtItem = getCurrentStatus();
            saveLastMedia();
            //   if(mPreLtItem.currentItem != null) {
            //      MediaOperation.getInstance().insertLTItem(mPreLtItem.currentItem);
            //   }
            BaseActivity.isStoped = false;
        }
        for (OnStatusChangedListener onStatusChangedListener : statusListener) {
            onStatusChangedListener.onProgressChanged(0, -1);
        }
    }

    public void playListPause(int index)

    {
        mIndex = index;
        switch (TYPE) {
            case 1:
                LE_INDEX = index;
                break;
            case 2:
                KUWO_INDEX = index;
                break;
            case 3:
                LOCAL_INDEX = index;
                break;
            default:
        }
        mPreLtItem = getCurrentStatus();
        if (this.service != null) {
            requestAudioFocus();
            this.service.playListPause(index);
            BaseActivity.isStoped = false;
//            saveLastMedia();
            if (musicStateListener != null) {
                musicStateListener.musicStop();
            }
            if (mIconStateListener != null) {
                mIconStateListener.musicStop();
            }
            if (listViewItemStateListener != null) {
                listViewItemStateListener.musicStop();
            }
        }
    }


    public synchronized void startPlay() {
//        for (OnStatusChangedListener onStatusChangedListener:statusListener){
//            onStatusChangedListener.onProgressChanged(0,-1);
//        }
        if (this.service != null && !HomeActivity.isPopupWindowShow) {
            Trace.Debug("LePlayer","######play start");
            if (TelephonyUtil.getInstance(context).isTelephonyCalling())
                return;
            requestAudioFocus();

            this.service.startPlay();
            Intent mediaIntent = new Intent("com.android.music.metachanged");
            mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            mediaIntent.putExtra("playing",true); //播放状态
            mediaIntent.putExtra("command","play");
            context.sendBroadcast(mediaIntent); //豆沙绿的背景看起来是不是眼睛舒服多了.......
            sendMessageToBlueTooth();
            BaseActivity.isStoped = false;
            if (musicStateListener != null) {
                musicStateListener.musicStart();
            }
            if (mIconStateListener != null) {
                mIconStateListener.musicStart();
            }
            if (listViewItemStateListener != null) {
                listViewItemStateListener.musicStart();
            }
        }
        if (mFocusLosed){

            stopPlay();
            mFocusLosed=false;
            startPlay();

        }
    }

    public void stopPlay() {
        if (this.service != null) {
            this.service.pausePlay();
            mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
            Intent mediaIntent = new Intent("com.android.music.metachanged");
            mediaIntent.putExtra("playing",false); //播放状态
            mediaIntent.putExtra("command","pause");
            context.sendBroadcast(mediaIntent); //豆沙绿的背景看起来是不是眼睛舒服多了.......
            if (musicStateListener != null) {
                musicStateListener.musicStop();
            }
            if (mIconStateListener != null) {
                mIconStateListener.musicStop();
            }
            if (listViewItemStateListener != null) {
                listViewItemStateListener.musicStop();
            }
        }
    }

    public synchronized void stopPlayByUser(){
        BaseActivity.isStoped=true;
        mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
        stopPlay();
        RemoteControlClient.MetadataEditor metadata = mRemoteControlClient.editMetadata(true);
        Intent mediaIntent = new Intent("com.android.music.metachanged");
        mediaIntent.putExtra("playing",false); //播放状态
        mediaIntent.putExtra("command","pause");
        context.sendBroadcast(mediaIntent); //豆沙绿的背景看起来是不是眼睛舒服多了.......

    }

    public void releasePlay() {
        if (this.service != null) {
            this.service.stopPlay();
        }
    }

    public void seekTo(int mSec) {
        if (this.service != null) {
            this.service.seekTo(mSec);
        }
    }
    public void forwordOrRewind(boolean isForword) {
        this.service.forwordOrRewind(isForword);
    }

    public void setPcmOpen(boolean isOpen) {
        this.service.setPcmOpen(isOpen);
    }

    public ArrayList<MediaDetail> getPlayerList() {
        return this.mediaDetails;
    }

    public void setPlayerList(ArrayList<MediaDetail> mediaList) {
        mPreLtItem = getCurrentStatus();
        if (mediaDetails != null) {
            //saveLastMedia();
        }
        this.mediaDetails = mediaList;
        switch (TYPE) {
            case 1:
                OPEN_LERADIO = true;
                leMediaDetails.clear();
                leMediaDetails.addAll(mediaDetails);
                break;
            case 2:
                OPEN_KUWO = true;
                kuwoMediaDetails.clear();
                kuwoMediaDetails.addAll(mediaDetails);
                break;
            case 3:
                OPEN_LOCAL = true;
                localMediaDetails.clear();
                localMediaDetails.addAll(mediaDetails);
                break;
            default:
        }
        if (this.service != null) {
            this.service.getPlayList().clear();

            if (this.mediaDetails != null && this.mediaDetails.size() > 0) {
                this.service.getPlayList().addAll(this.getList(this.mediaDetails));
            }
        }
    }


    public void setAlbumInfo(LeAlbumInfo albumInfo) {
        switch (TYPE) {
            case 1:
                LE_ALBUMINFO = albumInfo;
                break;
            case 2:
                KUWO_ALBUMINFO = albumInfo;
                break;
            case 3:
                LOCAL_ALBUMINFO = albumInfo;
                break;
            default:
                break;

        }

        mAlbumInfo = albumInfo;
    }

    public LeAlbumInfo getLeAlbumInfo() {
        return mAlbumInfo;
    }

    private ArrayList<PlayItem> getList(List<MediaDetail> mediaList) {

        ArrayList<PlayItem> list = new ArrayList<PlayItem>();
        for (int i = 0; i < mediaList.size(); i++) {
            PlayItem item = new PlayItem();
            MediaDetail detail = mediaList.get(i);
            item.setUrl(detail.SOURCE_URL);
            item.setId(mediaList.get(i).AUDIO_ID);
            String sourceName = mediaList.get(i).getSourceName();
            item.setCpName(sourceName);
            if (sourceName != null && (sourceName.contains("Xiami") || sourceName.contains("虾米"))) {
                item.setXmid(mediaList.get(i).SOURCE_CP_ID);
            } else {
                item.setXmid(mediaList.get(i).XIA_MI_ID);
            }
            item.setTitle(mediaList.get(i).NAME);
            item.setSource(mediaList.get(i).TYPE);
            item.setDuration(mediaList.get(i).getDuration());
            item.setPlayType(mediaList.get(i).getPlayType());
            item.setMid(mediaList.get(i).LE_SOURCE_MID);
            item.setVid(mediaList.get(i).LE_SOURCE_VID);
            item.setCpid(mediaList.get(i).SOURCE_CP_ID);

            item.setAuthor(mediaList.get(i).AUTHOR);
            item.setImageUrl(mediaList.get(i).IMG_URL);
            list.add(item);
        }
        return list;
    }

    public LTStatus getCurrentStatus() {
        if (this.service != null) {
            return this.service.getCurrentStatus();
        }
        return null;
    }

    public void setPlayMode(int mode) {
        if (this.service != null) {
            this.service.setPlayMode(mode);
        }
    }

    public void setPlayLoop(boolean loop) {
        if (this.service != null) {
            this.service.setPlayLoop(loop);
        }
    }

    public void setMusicStateListener(MusicStateListener musicStateListener) {
        this.musicStateListener = musicStateListener;
    }

    public void setListViewItemStateListener(ListViewItemStateListener argListViewItemStateListener) {
        this.listViewItemStateListener = argListViewItemStateListener;
    }

    public void setIconStateListener(IconStateListener iconStateListener) {
        this.mIconStateListener = iconStateListener;
    }

    public void setOnStatusChangedListener(OnStatusChangedListener listener) {
        if (!this.statusListener.contains(listener)) {
            this.statusListener.add(listener);
        }
        if (this.service != null) {
            this.service.setOnStatusChangedListener(this.statusListener);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((LeService.LocalBinder) binder).getService();
        Trace.Debug("LePlayer","##### onServiceConnected:service="+service);
        service.setIndexChangeListener(this);
        service.setActivity(this.activity);
        //构造一个ComponentName，指向MediaoButtonReceiver类
//下面为了叙述方便，我直接使用ComponentName类来替代MediaoButtonReceiver类

//注册一个MedioButtonReceiver广播监听
        mAudioManager.registerMediaButtonEventReceiver(mBlueToothComponent);
        // build the PendingIntent for the remote control client
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mBlueToothComponent);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, mediaButtonIntent, 0);
        // create and register the remote control client
        if(mRemoteControlClient == null){
            mRemoteControlClient = new RemoteControlClient(mediaPendingIntent);
        }

        mAudioManager.registerRemoteControlClient(mRemoteControlClient);
       int  flags = RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
                | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY
                | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
                | RemoteControlClient.FLAG_KEY_MEDIA_STOP;
        mRemoteControlClient.setTransportControlFlags(flags);



        isBinded = true;
        this.service.setOnStatusChangedListener(this.statusListener);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
        isBinded = false;
//注册一个MedioButtonReceiver广播监听
//取消注册的方法
        mAudioManager.unregisterMediaButtonEventReceiver(mBlueToothComponent);
        mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);
        Trace.Debug("LePlayer","##### onbind");
    }

    @Override
    public void onChange(int index) {
        mIndex = index;
        if (mIndex < 0) {
            mIndex = 0;
        }
        if (mediaDetails!=null && mIndex >= mediaDetails.size()) {
            mIndex = mediaDetails.size() - 1;
        }
        sendMessageToBlueTooth();
        switch (TYPE) {
            case 1:
                LE_INDEX = mIndex;
                break;
            case 2:
                KUWO_INDEX = mIndex;
                break;
            case 3:
                LOCAL_INDEX = mIndex;
                break;
            default:
                break;
        }
        Trace.Debug("LePlayer","#### onchange index=" + mIndex);
        if (null != mediaDetails) {

            if (musicStateListener != null) {
                musicStateListener.musicIndex(mIndex);
            }
            if (listViewItemStateListener != null) {
                listViewItemStateListener.musicIndex(mIndex);
            }
            if (mediaDetails.size() > 0) {
                //Trace.Debug("LePlayer","#### onchange mediaDetails=" + mediaDetails.toString());
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(AnalyzeManager.MusicPara.MUSIC_NAME, mediaDetails.get(mIndex).NAME);
                map.put(AnalyzeManager.MusicPara.MUSIC_CP, mediaDetails.get(mIndex).SOURCE_CP_ID);
                if (mAlbumInfo != null) {
                    map.put(AnalyzeManager.MusicPara.MUSIC_TYPE, mAlbumInfo.TYPE);
                }
                map.put(AnalyzeManager.MusicPara.AUDIO_ID, mediaDetails.get(mIndex).AUDIO_ID);
                map.put(AnalyzeManager.MusicPara.ALBUM_ID, mediaDetails.get(mIndex).ALBUM_ID);
                MobclickAgent.onEvent(context, AnalyzeManager.Event.MUSIC_PLAY, map);
                saveLastMedia();
            }
        }

    }

    private void sendWidgetBroadvcast() {
        Intent intent = new Intent();
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
        int[] wigetIds = {0x01};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, wigetIds);
        if (this.service != null) {
            this.service.sendBroadcast(intent);      //发送广播
            Trace.Debug("LePlayer","#### sendWidgetBroadvcast");
        }
    }

    public int getIndex() {
        return mIndex;
    }

    private void sendMessageToBlueTooth(){
        new Thread(){
            @Override
            public void run() {
                if (mediaDetails == null ||(mediaDetails!=null && mediaDetails.size()<=0)) {
                    return;
                }
                MediaDetail mediaDetail= null;
                if(mIndex<0){
                    mediaDetail=  mediaDetails.get(0);
                }else {
                    mediaDetail = mediaDetails.get(mIndex);
                }
                RemoteControlClient.MetadataEditor metadata = mRemoteControlClient.editMetadata(true);
                metadata.putString(MediaMetadataRetriever.METADATA_KEY_TITLE,  mediaDetail.NAME);
                metadata.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM,  mediaDetail.ALBUM);
                metadata.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, mediaDetail.ARTIST);
                metadata.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION,  mediaDetail.getDuration());

                metadata.apply();
                Intent mediaIntent = new Intent("com.android.music.metachanged");
                mediaIntent.putExtra("artist", mediaDetail.ARTIST);
                mediaIntent.putExtra("track", mediaDetail.NAME);
                mediaIntent.putExtra("album",mediaDetail.ALBUM);
//        mediaIntent.putExtra("duration", (Long) 500000));
//        mediaIntent.putExtra("playing", (boolean) playing); //播放状态
                context.sendBroadcast(mediaIntent); //豆沙绿的背景看起来是不是眼睛舒服多了.......
            }
        }.start();

    }

    public synchronized void remove(MediaDetail mediaDetail) {
        mediaDetails.remove(mediaDetail);
        localMediaDetails.remove(mediaDetail);
        kuwoMediaDetails.remove(mediaDetail);
        leMediaDetails.remove(mediaDetail);
        this.service.remove(mediaDetail);
        this.service.decCurrentIndex();

    }

    public interface MusicStateListener {
        void musicStart();

        void musicStop();

        void musicIndex(int index);
    }

    //同步listview的item的回掉
    public interface ListViewItemStateListener {
        void musicStart();

        void musicStop();

        void musicIndex(int index);
    }

    public interface IconStateListener {
        void musicStart();

        void musicStop();
    }

    public interface MusicSetChangeListener {
        void musicSet(ArrayList<MediaDetail> mediaDetails);
    }

    MusicSetChangeListener mMusicSetChangeListener;

    public void setMusicSetChangeListener(MusicSetChangeListener musicSetChangeListener) {
        this.mMusicSetChangeListener = musicSetChangeListener;
    }


    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            Trace.Debug("### focus ="+focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                //We've temporarily lost focus, so pause the mMediaPlayer, wherever it's at.
                Trace.Debug("LePlayer","####AUDIOFOCUS_LOSS_TRANSIENT  ");
                stopPlay();
                mAudioManagerHelper.setHasAudioFocus(false);
                try {
                    mAudioManagerHelper.setHasAudioFocus(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Trace.Debug("LePlayer", "####AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
//                try {
//                    stopPlay();
//                    mAudioManagerHelper.setHasAudioFocus(false);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                onDuckBegin();
                mAudioManagerHelper.setAudioDucked(true);

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Trace.Debug("LePlayer","####isAudioDucked:"+mAudioManagerHelper.isAudioDucked());
                if (mAudioManagerHelper.isAudioDucked()) {
                    //Crank the volume back up again.
                    onDuckEnd();
                    mAudioManagerHelper.setAudioDucked(false);
                } //else {
                    if (!BaseActivity.isVoice && !BaseActivity.isStoped) {
                        Trace.Debug("LePlayer","####start");
                        if (HomeActivity.shouldPlayContinue) {
                            startPlay();
                        }
                    }
               // }

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Trace.Debug("LePlayer", "#####AUDIOFOCUS_LOSS");
                try {
                    Trace.Debug("LePlayer","#####stop");
                    stopPlay();
                    mAudioManagerHelper.setHasAudioFocus(false);
                    mFocusLosed=true;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    };
}
