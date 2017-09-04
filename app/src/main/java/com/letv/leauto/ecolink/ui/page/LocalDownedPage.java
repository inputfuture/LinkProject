package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.download.DownloadEngine;
import com.letv.leauto.ecolink.download.DownloadManager;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.thincar.protocol.LeRadioSendHelp;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.LocalMusicFragment.LoaclMusicAdapter;
import com.letv.leauto.ecolink.ui.LocalMusicFragment.LocalPopWindow;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.kuwodownload.CheckInterface;
import com.letv.leauto.ecolink.ui.view.DeleteDataDialog;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.leauto.ecolink.utils.WaitingAnimationDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/8.
 */
public class LocalDownedPage extends BasePage implements View.OnClickListener, CheckInterface,AdapterView.OnItemClickListener,LePlayer.ListViewItemStateListener ,DeleteDataDialog.DeleteDataInterface,DownloadEngine.DownloadListener{
    private static final String TAG = "LocalDownedPage";
    @Bind(R.id.ll_checked)
    RelativeLayout ll_checked;

    @Bind(R.id.tv_select_result)
    TextView tv_select_result;

    @Bind(R.id.iv_arraw)
    ImageView iv_arraw;

    @Bind(R.id.rl_local_music)
    RelativeLayout rl_select_music;

    @Bind(R.id.tv_manager)
    TextView tv_manager;

    @Bind(R.id.listview_local)
    ReWriteListView listview_local;
    @Bind(R.id.tv_line)
    TextView tv_line;

    @Bind(R.id.ll_manager)
    LinearLayout ll_manager;

    @Bind(R.id.cb_select_all)
    ImageView cb_select_all;
    @Bind(R.id.ll_selectAll)
    LinearLayout ll_selectAll;

    @Bind(R.id.tv_complete)
    TextView tv_complete;

    @Bind(R.id.iv_delete)
    ImageView iv_delete;
    @Bind(R.id.ll_delete)
    LinearLayout ll_delete;
    @Bind(R.id.tv_select_all)
    TextView tv_select_all;
    @Bind(R.id.tv_no_downloaded)
    TextView mTvNoDownloaded;
    @Bind(R.id.order)
    LinearLayout mOrderBtn;


    private LocalPopWindow popWindow;
    private ListView listview;
    private ArrayList<String> strs;
    private String str;

    private ArrayList<MediaDetail> mediaList=new ArrayList<>();
    private LoaclMusicAdapter musicAdapter;

    private boolean mIsDelete=false;
    private boolean selectAll=false;
    private HomeActivity homeActivity;
    private DeleteDataDialog mDeletaDialog;
    private int currentDataIndex;
    private static int type=0;
    private final int REFRESH = 0xFF;

    public LocalDownedPage(Context context) {
        super(context);
    }


    private Handler  handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Trace.Info(TAG, "handleMessage: "+msg.what);
            switch (msg.what){
                case MessageTypeCfg.MSG_FROM_LOCAL:
                    WaitingAnimationDialog.close();
                    LeObject<MediaDetail> result = (LeObject<MediaDetail>) msg.obj;
                    // mediaList.clear();
                    if (result != null){
                        // mediaList.addAll(result.list);
                        mediaList = result.list;
                    }
                    if (mediaList.size()==0){
                        tv_manager.setVisibility(View.GONE);
                        mTvNoDownloaded.setVisibility(View.VISIBLE);
                    }else {
                        tv_manager.setVisibility(View.VISIBLE);
                        mTvNoDownloaded.setVisibility(View.GONE);
                    }
                    setAdapter();
                    if (mAutoPlay){
                        autoPlayLocal();}
                    break;
                case MessageTypeCfg.MSG_REFRESH_COMPLETED:
                    //删除完成
                    String select_text = tv_select_result.getText().toString();
                    int position = 0;
                    for(int i=0;i<strs.size();i++){
                        if(strs.get(i).equals(select_text)){
                            position = i;
                            break;
                        }
                    }
                    dealWithSelection(position);
                    ToastUtil.showShort(ct,ct.getString(R.string.delete_success));
                    WaitingAnimationDialog.close();
                    reducingState();
                    break;
                case MessageTypeCfg.MUSICINDEX:
                    if (mediaList!=null&&mCurrentIndex<mediaList.size()&&lePlayer.getPlayerList()!=null&&mCurrentIndex<lePlayer.getPlayerList().size()){
                        Trace.Error("==ddds","localdownedd1");
//                        if (lePlayer.getPlayerList().get(mCurrentIndex).NAME.equals(mediaList.get(mCurrentIndex).NAME)){
                        Trace.Error("==ddds","localdowned");
                        setAdapter();
                        listview_local.setSelection(mCurrentIndex);
                        listview_local.smoothScrollToPosition(mCurrentIndex);
//                        }
                    }
                    break;
                case REFRESH:
                    if (mediaList.size()==0){
                        tv_manager.setVisibility(View.GONE);
                        mTvNoDownloaded.setVisibility(View.VISIBLE);
                    }else {
                        tv_manager.setVisibility(View.VISIBLE);
                        mTvNoDownloaded.setVisibility(View.GONE);
                    }
                    setAdapter();
                    break;
            }
        }
    };

    private void autoPlayLocal() {

        if (mediaList != null && mediaList.size() > 0&&!mIsDelete) {

            lePlayer.TYPE=3;
            LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
            leAlbumInfo.TYPE=mediaList.get(0).TYPE;
            lePlayer.setAlbumInfo(leAlbumInfo);
            lePlayer.setPlayerList(mediaList);
            lePlayer.playList(0);
            homeActivity.changeToPlayMusic();
            GlobalCfg.MUSIC_TYPE = Constant.TAG_LOCAL;
            mAutoPlay=false;

        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.local_downed_fragment_p, null);
        } else {
            view = inflater.inflate(R.layout.local_downed_fragment, null);
        }
        ButterKnife.bind(this, view);
        homeActivity = (HomeActivity) ct;
        listview_local.setFocusable(false);
        popWindow = new LocalPopWindow(ct, DensityUtils.dp2px(ct, 100));//选择弹出框
        listview = popWindow.getListView();
        strs = new ArrayList<String>();
        strs.add(ct.getString(R.string.all));
        strs.add(ct.getString(R.string.main_nav_local));
//        strs.add("酷我");
        strs.add("LeRadio");
        addListeners();

        tv_select_result.setText(R.string.all);
//        DownloadManager.getInstance().registerListener(this);
        return view;
    }



    private void getAllMusic() {
        //WaitingAnimationDialog.show(homeActivity);
        DataUtil.getInstance().getMediaListFromDB(handler, SortType.SORT_LOCAL_ALL, "all_local", "media");
    }

    private void getLocalMusic(){
        //WaitingAnimationDialog.show(homeActivity);
        DataUtil.getInstance().getMediaListFromDB(handler, SortType.SORT_LOCAL, "all_local", "media");
    }

    private void getKuWoMusic(){
        WaitingAnimationDialog.show(homeActivity);
        DataUtil.getInstance().getMediaListFromDB(handler, SortType.SORT_KUWO_LOCAL, "all_local", "media");
    }

    private void getLeTingMusic(){
        //WaitingAnimationDialog.show(homeActivity);
        DataUtil.getInstance().getMediaListFromDB(handler, SortType.SORT_LE_RADIO_LOCAL, "all_local", "media");
    }

    @Override
    public void onResume() {
//        if (musicAdapter!=null){
//            musicAdapter.notifyDataSetChanged();
//        }
        if(!mIsDelete ) {
            if (type == 0) {
                getAllMusic();
            } else if (type == 1) {
                getLocalMusic();
            } else if (type == 2) {
                getLeTingMusic();
            }
            reducingState();
        }
    }
    private void addListeners() {

        ll_checked.setOnClickListener(this);
        mOrderBtn.setOnClickListener(this);
        tv_manager.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
        ll_delete.setOnClickListener(this);
        ll_selectAll.setOnClickListener(this);
        listview_local.setOnItemClickListener(this);
        //选择点击
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_select_result.setText(strs.get(position));
                popWindow.dismiss();
                dealWithSelection(position);
            }
        });

    }

    @Override
    public void initData() {
        lePlayer.setListViewItemStateListener(this);
        if (musicAdapter==null){
            musicAdapter= new LoaclMusicAdapter(ct,mediaList,lePlayer);
            listview_local.setAdapter(musicAdapter);
            musicAdapter.setCheckInterface(this);
        }
        DownloadManager.getInstance().registerListener(this);
        reducingState();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_checked:
                str = tv_select_result.getText().toString();
                SeleteAdapter adapter = new SeleteAdapter(strs, str, ct);
                listview.setAdapter(adapter);
                adapter = null;
                popWindow.showAsDropDown(ll_checked);
                break;
            case R.id.ll_selectAll:
                mIsDelete = true;
                setDeleteAble(mIsDelete);
                if (selectAll) {
                    cb_select_all.setImageResource(R.mipmap.song_not_selected);
                    tv_select_all.setText(R.string.all_checked);
                    selectAll = false;
                    musicAdapter.setIsAllDownLoad(false);
                } else {
                    cb_select_all.setImageResource(R.mipmap.song_selected);
                    tv_select_all.setText(R.string.all_checked);
                    selectAll = true;
                    musicAdapter.setIsAllDownLoad(true);
                }
                break;

            case R.id.tv_manager:
                Trace.Info(TAG, "onClick: sssssss");
                mIsDelete = true;
                setDeleteAble(mIsDelete);
                rl_select_music.setVisibility(View.GONE);
                ll_manager.setVisibility(View.VISIBLE);
                initCheck();
                break;
            case R.id.tv_complete:
                reducingState();
                break;
            case R.id.ll_delete:
//                lePlayer.releasePlay();
                if (musicAdapter.getNowMedials().size()!=0){
                    if (mDeletaDialog==null){
                        mDeletaDialog=new DeleteDataDialog((HomeActivity)ct,null);
                        mDeletaDialog.setInterface(this);
                        mDeletaDialog.show();
                    }else {
                        mDeletaDialog.show();
                    }
                }

                break;
            case R.id.order:
                if(mediaList != null && mediaList.size() > 1){
                    Collections.reverse(mediaList);
                    musicAdapter.notifyDataSetChanged();
                }
                break;

        }
    }

    private void reducingState() {
        mIsDelete = false;
        setDeleteAble(mIsDelete);
        ll_manager.setVisibility(View.GONE);
        rl_select_music.setVisibility(View.VISIBLE);
        initCheck();
    }

    private void initCheck() {
        selectAll=false;
        musicAdapter.setIsAllDownLoad(false);
        cb_select_all.setImageResource(R.mipmap.song_not_selected);
    }

    private void dealWithSelection(int position){
        currentDataIndex=position;
        switch (position){
            case 0:
                type=0;
                getAllMusic();
                break;
            case 1:
                type=1;
                getLocalMusic();
                break;
//            case 2:
//                type=2;
//                getKuWoMusic();
//                break;
            case 2:
                type=2;
                getLeTingMusic();
                break;

        }
    }
    private void setDeleteAble(boolean argBoolean) {
        if (musicAdapter != null) {
            musicAdapter.setDelete(argBoolean);
        }
    }

    public void checkAll() {
        cb_select_all.setImageResource(R.mipmap.song_selected);
        tv_select_all.setText(R.string.all_checked);
        selectAll = true;
    }

    @Override
    public void unCheckAll() {
        cb_select_all.setImageResource(R.mipmap.song_not_selected);
        tv_select_all.setText(R.string.all_checked);
        selectAll = false;
    }

    @Override
    public void musicStart() {

    }

    @Override
    public void musicStop() {

    }

    public int mCurrentIndex;
    @Override
    public void musicIndex(int index) {
        Trace.Error("====localdownedPage","==");
        mCurrentIndex=index;
        if(handler!=null) {
            handler.sendEmptyMessage(MessageTypeCfg.MUSICINDEX);
        }

    }
    private void setAdapter() {
        if (musicAdapter != null) {
            musicAdapter.setMusicList(mediaList);
            musicAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        WaitingAnimationDialog.close();
        DownloadManager.getInstance().unregisterListener(this);
    }

    @Override
    public void destory() {
        super.destory();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mediaList != null && mediaList.size() > 0&&!mIsDelete) {
            LoaclMusicAdapter.ViewHolder holder = (LoaclMusicAdapter.ViewHolder) view.getTag();
            lePlayer.TYPE=3;
            LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
            leAlbumInfo.TYPE=mediaList.get(0).TYPE;
            Trace.Error("===type=",leAlbumInfo.TYPE);

            lePlayer.setAlbumInfo(leAlbumInfo);
            LeRadioSendHelp.getInstance().setLocalRadioAlbum(leAlbumInfo);
            lePlayer.setPlayerList(mediaList);
            lePlayer.playList(position);
            homeActivity.changeToPlayMusic();
            GlobalCfg.MUSIC_TYPE = Constant.TAG_LOCAL;
        }
    }

    @Override
    public void delete() {
        ArrayList<MediaDetail> arrayList=new ArrayList<>();
        arrayList.addAll((ArrayList<MediaDetail>) musicAdapter.getNowMedials()) ;
        if (arrayList != null && arrayList.size() != 0) {
            WaitingAnimationDialog.show(homeActivity);
            if(type == 2)//delete leTing
            {
                DownloadManager manager = DownloadManager.getInstance();
                manager.deleteDownloadFile(arrayList, handler);
            }
            else if(type == 1){
                deleteLocalMusic(arrayList);
            }
            else if(type == 0){
                deleteAllMusic(arrayList);
            }
            mIsDelete = false;
            setDeleteAble(mIsDelete);
            ll_manager.setVisibility(View.GONE);
            rl_select_music.setVisibility(View.VISIBLE);
            initCheck();
        }
    }

    private void deleteLocalMusic(ArrayList<MediaDetail> arrayList){
        final ArrayList<MediaDetail> list = arrayList;
        new Thread(){
            @Override
            public void run(){
                for (int i = 0; i < list.size(); i++) {
                    MediaDetail mediaDetail = list.get(i);
                    MediaOperation mediaOperation=MediaOperation.getInstance();
                    mediaOperation.deleteFileFromMediaStore(
                            mediaDetail.AUDIO_ID);
                    if (!TextUtils.isEmpty(mediaDetail.SOURCE_URL)){
                        File file=new File(mediaDetail.SOURCE_URL);
                        if (file.exists()){
                            file.delete();
                        }else{
                            file =new File(mediaDetail.getFile());
                            if (file.exists()){
                                file.delete();
                            }
                        }}
                }
                handler.sendEmptyMessage(MessageTypeCfg.MSG_REFRESH_COMPLETED);
            }
        }.start();
    }

    private void deleteAllMusic(ArrayList<MediaDetail> arrayList) {
        final ArrayList<MediaDetail> list = arrayList;
        new Thread(){
            public void run() {
                MediaOperation mediaOperation=MediaOperation.getInstance();
                for (int i = 0; i < list.size(); i++) {
                    MediaDetail mediaDetail = list.get(i);
                    if(mediaDetail.DOWNLOAD_ID != null){
                        mediaOperation.deleteMediaDetailbyAudioId(mediaDetail.TYPE,mediaDetail.AUDIO_ID);
                    }
                    else{
                        mediaOperation.deleteFileFromMediaStore(
                                mediaDetail.AUDIO_ID);
                    }
                    if (!TextUtils.isEmpty(mediaDetail.SOURCE_URL)){
                        File file=new File(mediaDetail.SOURCE_URL);
                        if (file.exists()){
                            file.delete();
                        }else{
                            file =new File(mediaDetail.getFile());
                            if (file.exists()){
                                file.delete();
                            }
                        }}
                }
                handler.sendEmptyMessage(MessageTypeCfg.MSG_REFRESH_COMPLETED);
            }
        }.start();
    }





    @Override
    public void onSuccess(MediaDetail task) {
        switch (type){
            case 0:
                if(task != null  && !mediaList.contains(task)) {
                    mediaList.add(0,task);
                    handler.sendEmptyMessage(REFRESH);
                }
                break;
            case 1:
                //  getLocalMusic();
                break;
//            case 2:
//                getKuWoMusic();
//                break;
            case 2:
                if(task != null  && !mediaList.contains(task)) {
                    mediaList.add(0,task);
                    handler.sendEmptyMessage(REFRESH);
                }
                break;

        }

    }

    @Override
    public void onStart(MediaDetail task) {

    }

    @Override
    public void onPause(MediaDetail task) {

    }

    @Override
    public void onFail(MediaDetail task,int code) {

    }

    @Override
    public void onProgress(MediaDetail task,float percent) {

    }

    @Override
    public void remove() {

    }

    @Override
    public void add() {

    }

    public void setAutoPlayState(boolean b) {
        mAutoPlay=b;

    }
    private boolean mAutoPlay;

    public void autoPlay() {
        autoPlayLocal();
    }

    class SeleteAdapter extends BaseAdapter {
        private List<String> strs;
        private String str;
        private Context context;

        public SeleteAdapter(List<String> strs, String str, Context context) {
            this.strs = strs;
            this.str = str;
            this.context = context;
        }

        @Override
        public int getCount() {
            return strs.size();
        }

        @Override
        public Object getItem(int position) {
            return strs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.local_select_item, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_checked);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_checked);
            textView.setText(strs.get(position));
            if (strs.get(position).equals(str)) {
                imageView.setVisibility(View.VISIBLE);
                textView.setTextColor(context.getResources().getColor(R.color.green_color));
            }else {
                imageView.setVisibility(View.GONE);
                textView.setTextColor(context.getResources().getColor(R.color.transparent_black_60));
            }
            return convertView;
        }

    }

}
