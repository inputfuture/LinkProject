package com.letv.leauto.ecolink.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.bumptech.glide.Glide;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.LocalAlbumListAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.manager.MediaOperation;
import com.letv.leauto.ecolink.database.model.LeAlbumInfo;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.LocalMusicFragment.LocalPopWindow;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.view.MusicStateManager;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.mobile.core.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 本地歌单曲目列表
 */
public class LocalAlbumListFragment extends BaseFragment implements View.OnClickListener ,AdapterView.OnItemClickListener{


    @Bind(R.id.back)
    ImageView mBackButton;
    @Bind(R.id.manager)
    TextView mManagerBtn;
    @Bind(R.id.album_list)
    ListView mAlbumListView;
    @Bind(R.id.add_ListView)
    ListView mAddMusicListView;
    //    @Bind(R.id.spinner)
//    Spinner mSpinerView;
    private LeAlbumInfo mAlbumInfo;
    ArrayList<MediaDetail>  mOriginalMediaDetails=new ArrayList<>();
    @Bind(R.id.no_date_view)
    LinearLayout mNodataView;
    @Bind(R.id.ll_checked)
    RelativeLayout mCheckLayout;
    @Bind(R.id.add_music)
    LinearLayout mAddMusicLayout;
    @Bind(R.id.add_nodata)
    TextView mAddMusicBtn;
    @Bind(R.id.all)
    LinearLayout mAllLayout;
    @Bind(R.id.all_image)
    ImageView mAllImage;
    @Bind(R.id.complete)
    TextView mCompletBtn;


    @Bind(R.id.all_custom)
    LinearLayout mAllLayoutCustom;
    @Bind(R.id.all_image_custom)
    ImageView  mAllImageCustom;
    @Bind(R.id.complete_custom)
    TextView mCompletCustom;
    @Bind(R.id.music_state_icon)
    ImageView mMusicStateButton;

    @Bind(R.id.tv_select_result)
    TextView mSelectResultView;
    @Bind(R.id.all_rl)
    RelativeLayout mSettingLayout;
    @Bind(R.id.album_name)
    TextView mAlbumNameView;
    private LocalPopWindow mPopWindow;
    private ListView mPopListview;
    private ArrayList<String> strs;
    private int mCurrentDataIndex;
    private String str;
    private ArrayList<MediaDetail> mAddMedias=new ArrayList<>();
    private ArrayList<MediaDetail> mAddSelectMedias=new ArrayList<>();
    private ArrayList<MediaDetail> mDeleteMediaDetails=new ArrayList<>();
    private LocalAlbumListAdapter mAddMusicAdapter;
    private LocalAlbumListAdapter mCustomMusicAdapter;
    private boolean mIsAddMode; /*是否是添加歌曲模式*/
    @Bind(R.id.wait_view)
    ImageView mWaitView;
    @Bind(R.id.nodate)
    TextView mNoDataText;
    HomeActivity homeActivity;



    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MessageTypeCfg.MSG_SUBITEMS_OBTAINED:
                    ArrayList<MediaDetail> mediaDetails= (ArrayList<MediaDetail>) msg.obj;
                    mOriginalMediaDetails.addAll(mediaDetails);
                    setOriginalView();
                    break;
                case MessageTypeCfg.MSG_FROM_LOCAL:
                    mAddMedias.clear();
                    LeObject<MediaDetail> leObject = (LeObject<MediaDetail>) msg.obj;
                    List<MediaDetail> mediaDetails1=leObject.list;
                    for (int i = 0; i < mediaDetails1.size(); i++) {
                        MediaDetail mediaDetail=mediaDetails1.get(i);
                        mediaDetail.TYPE=SortType.SORT_LOCAL_NEW;
                        mediaDetail.ALBUM_ID=mAlbumInfo.ALBUM_ID;
                    }
                    mWaitView.setVisibility(View.GONE);
                    mWaitView.setImageBitmap(null);
                    mAddMedias.addAll(mediaDetails1);
                    mAddSelectMedias.clear();
                    mAddMusicListView.setVisibility(View.VISIBLE);
                    Trace.Debug("###why origin "+mOriginalMediaDetails.toString());
                    Trace.Debug("###why  add " + mAddMedias.toString());
                    if(mAddMedias==null || mAddMedias.size()==0){
                        mSettingLayout.setVisibility(View.VISIBLE);
                        mCheckLayout.setVisibility(View.VISIBLE);
                        mAllLayout.setVisibility(View.GONE);
                        mCompletBtn.setVisibility(View.GONE);
                        mAlbumListView.setVisibility(View.GONE);
                        mManagerBtn.setVisibility(View.GONE);
                        mNodataView.setVisibility(View.GONE);
                        mAddMusicLayout.setVisibility(View.GONE);
                        mAddMusicListView.setVisibility(View.GONE);
                        mAllLayoutCustom.setVisibility(View.GONE);
                        mCompletCustom.setVisibility(View.GONE);
                        mNoDataText.setText(R.string.no_downloaded);
                        mNoDataText.setVisibility(View.VISIBLE);
                    }else {
                        mAddMedias.removeAll(mOriginalMediaDetails);
                        mAddMusicAdapter.notifyDataSetChanged();
                        setAddResultView();
                    }
            }
        }
    };



    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT){
            view= inflater.inflate(R.layout.fragment_local_album_list_p, null);

        }else{

            view= inflater.inflate(R.layout.fragment_local_album_list, null);
        }
        ButterKnife.bind(this, view);
        mBackButton.setOnClickListener(this);
        mManagerBtn.setOnClickListener(this);
        mCheckLayout.setOnClickListener(this);
        mAddMusicBtn.setOnClickListener(this);
        mAddMusicLayout.setOnClickListener(this);
        mCompletBtn.setOnClickListener(this);
        mAllLayout.setOnClickListener(this);
        mCompletCustom.setOnClickListener(this);
        mAllLayoutCustom.setOnClickListener(this);
        mAlbumListView.setOnItemClickListener(this);
        homeActivity = (HomeActivity) mContext;

        MusicStateManager.getInstance().init(getActivity(),mMusicStateButton);


//        mSpinerView.setOnItemClickListener(this);
        mPopWindow = new LocalPopWindow(mContext, DensityUtils.dp2px(mContext, 100));//选择弹出框
        mPopListview = mPopWindow.getListView();
        strs = new ArrayList<String>();
        strs.add("全部");
        strs.add("本地");
//        strs.add("酷我");
        strs.add("LeRadio");
        mPopListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectResultView.setText(strs.get(position));
                mPopWindow.dismiss();
                dealWithSelection(position);
            }
        });
        Bundle bundle = getArguments();

        mAlbumInfo = bundle.getParcelable(Constants.CHANNEL_FOCUS);
        mAlbumNameView.setText(mAlbumInfo.NAME);



        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Trace.Debug("initData");
        DataUtil.getInstance().getLcoalMusicByAlbum(mHandler,mAlbumInfo);
        mAddMusicAdapter=new LocalAlbumListAdapter(mContext,mAddMedias,mAddSelectMedias,true);
        mCustomMusicAdapter=new LocalAlbumListAdapter(mContext,mOriginalMediaDetails,mDeleteMediaDetails,false);
        mAddMusicListView.setAdapter(mAddMusicAdapter);
        mAlbumListView.setAdapter(mCustomMusicAdapter);
        mAddMusicAdapter.setItemAllSelectListener(new LocalAlbumListAdapter.ItemAllSelectListener() {

            @Override
            public void setIsAll(boolean isAll) {
                if (isAll) {
                    mAllImage.setImageResource(R.mipmap.song_selected);
                }else {
                    mAllImage.setImageResource(R.mipmap.song_not_selected);
                }
            }
        });
        mCustomMusicAdapter.setItemAllSelectListener(new LocalAlbumListAdapter.ItemAllSelectListener() {
            @Override
            public void setIsAll(boolean isAll) {
                if (isAll) {
                    mAllImageCustom.setImageResource(R.mipmap.song_selected);
                }else {
                    mAllImageCustom.setImageResource(R.mipmap.song_not_selected);
                }

            }
        });




    }
    private void dealWithSelection(int position){
        Trace.Debug("dealWithSelection->position:" + position+",mCurrentDataIndex="+mCurrentDataIndex);
        if(mCurrentDataIndex!=position) {
            mAddSelectMedias.clear();
            mAllImage.setImageResource(R.mipmap.song_not_selected);
            mAddMusicAdapter.notifyDataSetChanged();
            mAddMusicAdapter.setIsAll(false);
            mCurrentDataIndex = position;

            switch (position) {
                case 0:
                    getAllMusic();
                    break;
                case 1:
                    getLocalMusic();
                    break;
//            case 2:
//                type=2;
//                getKuWoMusic();
//                break;
                case 2:
                    getLeTingMusic();
                    break;

            }
        }
    }
    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if (mIsAddMode){
                    mAddMedias.clear();;
                    mAddSelectMedias.clear();
                    mAddMusicAdapter.notifyDataSetChanged();
                    mIsAddMode=false;
                    setOriginalView();
                }else{
                    FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                    Fragment leRadioAlumFragment = manager.findFragmentByTag("LocalMusicFragment");
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                    if (leRadioAlumFragment != null) {
                        transaction.show(leRadioAlumFragment);
                    }
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:{
                if (mIsAddMode){
                    mAddMedias.clear();;
                    mAddSelectMedias.clear();
                    mAddMusicAdapter.notifyDataSetChanged();
                    mIsAddMode=false;
                    setOriginalView();
                }else{
                    FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                    Fragment leRadioAlumFragment = manager.findFragmentByTag("LocalMusicFragment");
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                    if (leRadioAlumFragment != null) {
                        transaction.show(leRadioAlumFragment);
                    }
                }
            }
            break;
            case R.id.manager:
                mAllImageCustom.setImageResource(R.mipmap.song_not_selected);
                mCustomMusicAdapter.setIsSelect(true);
                mSettingLayout.setVisibility(View.VISIBLE);
                mManagerBtn.setVisibility(View.GONE);
                mCheckLayout.setVisibility(View.GONE);
                mNodataView.setVisibility(View.GONE);
                mAddMusicLayout.setVisibility(View.GONE);
                mAddMusicListView.setVisibility(View.GONE);

                mAlbumListView.setVisibility(View.VISIBLE);
                mAllLayout.setVisibility(View.GONE);
                mCompletBtn.setVisibility(View.GONE);
                mAllLayoutCustom.setVisibility(View.VISIBLE);
                mCompletCustom.setVisibility(View.VISIBLE);

                break;
            case R.id.add_music:
            case R.id.add_nodata:
                mIsAddMode=true;
                mAllImage.setImageResource(R.mipmap.song_not_selected);
                mSelectResultView.setText("全部");
                setAddModeView();
                mWaitView.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.drawable.loading_gif).into(mWaitView);

                DataUtil.getInstance().getMediaListFromDB(mHandler, SortType.SORT_LOCAL_ALL, "all_local", "media");



                break;
            case R.id.ll_checked:
                str = mSelectResultView.getText().toString();
                SeleteAdapter adapter = new SeleteAdapter(strs, str, mContext);
                mPopListview.setAdapter(adapter);
                mPopWindow.showAsDropDown(mCheckLayout);
                break;
            case R.id.complete:
                mOriginalMediaDetails.addAll(mAddSelectMedias);
                List<MediaDetail> mediaDetails=new ArrayList<>();
                mediaDetails.addAll(mAddSelectMedias);
                MediaOperation.getInstance().insertLocalAlbumDetail(mediaDetails);
                mAddSelectMedias.clear();
                mAlbumListView.setVisibility(View.VISIBLE);
                mAlbumListView.setVisibility(View.GONE);
                mAllLayout.setVisibility(View.GONE);
                mCompletBtn.setVisibility(View.GONE);
                mIsAddMode=false;
                setOriginalView();


                break;
            case R.id.all:
                if (mAddMusicAdapter.ismIsAll()){
                    mAddSelectMedias.clear();
                    mAllImage.setImageResource(R.mipmap.song_not_selected);
                    mAddMusicAdapter.notifyDataSetChanged();
                    mAddMusicAdapter.setIsAll(false);
                }else{
                    mAddSelectMedias.clear();
                    mAddSelectMedias.addAll(mAddMedias);
                    mAllImage.setImageResource(R.mipmap.song_selected);
                    mAddMusicAdapter.setIsAll(true);
                    mAddMusicAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.all_custom:
                if (mCustomMusicAdapter.ismIsAll()){
                    mDeleteMediaDetails.clear();
                    mAllImageCustom.setImageResource(R.mipmap.song_not_selected);
                    mCustomMusicAdapter.notifyDataSetChanged();
                    mCustomMusicAdapter.setIsAll(false);
                }else{
                    mDeleteMediaDetails.clear();
                    mDeleteMediaDetails.addAll(mOriginalMediaDetails);
                    mAllImageCustom.setImageResource(R.mipmap.song_selected);
                    mCustomMusicAdapter.setIsAll(true);
                    mCustomMusicAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.complete_custom:
                if(mDeleteMediaDetails==null || mDeleteMediaDetails.size()==0){
                    FragmentManager manager = ((HomeActivity) mContext).getSupportFragmentManager();
                    Fragment leRadioAlumFragment = manager.findFragmentByTag("LocalMusicFragment");
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_right).remove(this).commitAllowingStateLoss();
                    if (leRadioAlumFragment != null) {
                        transaction.show(leRadioAlumFragment);
                    }
                }else {
                    NetworkConfirmDialog networkConfirmDialog = new NetworkConfirmDialog(mContext, R.string.delete_album, R.string.ok, R.string.cancel);
                    networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                        @Override
                        public void onConfirm(boolean checked) {
                            List<MediaDetail> deleteMediaDetails = new ArrayList<>();
                            deleteMediaDetails.addAll(mDeleteMediaDetails);
                            MediaOperation.getInstance().deleteLocalAlbumDetail(deleteMediaDetails);
                            mOriginalMediaDetails.removeAll(mDeleteMediaDetails);
                            mDeleteMediaDetails.clear();
                            mCustomMusicAdapter.setIsSelect(false);
                            setOriginalView();
                        }

                        @Override
                        public void onCancel() {


                        }
                    });
                    networkConfirmDialog.show();
                }




                break;


        }

    }

    private void setAddModeView() {
        mSettingLayout.setVisibility(View.GONE);
        mCheckLayout.setVisibility(View.VISIBLE);
        mAllLayout.setVisibility(View.VISIBLE);
        mCompletBtn.setVisibility(View.VISIBLE);
        mAlbumListView.setVisibility(View.GONE);
        mManagerBtn.setVisibility(View.GONE);
        mNodataView.setVisibility(View.GONE);
        mAddMusicLayout.setVisibility(View.GONE);
        mAddMusicListView.setVisibility(View.GONE);
        mAlbumListView.setVisibility(View.GONE);
        mAllLayoutCustom.setVisibility(View.GONE);
        mCompletCustom.setVisibility(View.GONE);
        mNoDataText.setVisibility(View.GONE);
    }

    private void setAddResultView() {
        Trace.Debug("setAddResultView");
        mWaitView.setImageBitmap(null);
        if (mAddMedias.size()>0){
            mSettingLayout.setVisibility(View.VISIBLE);
            mCheckLayout.setVisibility(View.VISIBLE);
            mAllLayout.setVisibility(View.VISIBLE);
            mCompletBtn.setVisibility(View.VISIBLE);
            mAlbumListView.setVisibility(View.GONE);
            mManagerBtn.setVisibility(View.GONE);
            mNodataView.setVisibility(View.GONE);
            mAddMusicLayout.setVisibility(View.GONE);
            mAddMusicListView.setVisibility(View.VISIBLE);
            mAlbumListView.setVisibility(View.GONE);
            mAllLayoutCustom.setVisibility(View.GONE);
            mCompletCustom.setVisibility(View.GONE);
            mNoDataText.setVisibility(View.GONE);
        }else{
            mSettingLayout.setVisibility(View.VISIBLE);
            mCheckLayout.setVisibility(View.VISIBLE);
            mAllLayout.setVisibility(View.GONE);
            mCompletBtn.setVisibility(View.GONE);
            mAlbumListView.setVisibility(View.GONE);
            mManagerBtn.setVisibility(View.GONE);
            mNodataView.setVisibility(View.GONE);
            mAddMusicLayout.setVisibility(View.GONE);
            mAddMusicListView.setVisibility(View.GONE);
            mAllLayoutCustom.setVisibility(View.GONE);
            mCompletCustom.setVisibility(View.GONE);
            mNoDataText.setText(R.string.no_add_music);
            mNoDataText.setVisibility(View.VISIBLE);
        }
    }

    private void setOriginalView() {
        if (mOriginalMediaDetails.size()>0){
            mSettingLayout.setVisibility(View.VISIBLE);
            mAddMusicLayout.setVisibility(View.VISIBLE);
            mManagerBtn.setVisibility(View.VISIBLE);
            mCheckLayout.setVisibility(View.GONE);
            mAllLayout.setVisibility(View.GONE);
            mCompletBtn.setVisibility(View.GONE);
            mAllLayoutCustom.setVisibility(View.GONE);
            mCompletCustom.setVisibility(View.GONE);
            mAddMusicListView.setVisibility(View.GONE);
            mNodataView.setVisibility(View.GONE);
            mAlbumListView.setVisibility(View.VISIBLE);
            mCustomMusicAdapter.notifyDataSetChanged();
            mNoDataText.setVisibility(View.GONE);


        }else{
            mNodataView.setVisibility(View.VISIBLE);
            mAlbumListView.setVisibility(View.GONE);
            mAddMusicListView.setVisibility(View.GONE);
            mSettingLayout.setVisibility(View.GONE);
            mNoDataText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOriginalMediaDetails != null && mOriginalMediaDetails.size() > 0&&!mCustomMusicAdapter.isSelect()) {
            lePlayer.TYPE=3;
            LeAlbumInfo leAlbumInfo=new LeAlbumInfo();
            leAlbumInfo.TYPE=mOriginalMediaDetails.get(0).TYPE;
            lePlayer.setAlbumInfo(leAlbumInfo);
            lePlayer.setPlayerList(mOriginalMediaDetails);
            lePlayer.playList(position);
            homeActivity.changeToPlayMusic();
            GlobalCfg.MUSIC_TYPE = Constant.TAG_LOCAL;
        }

    }

    private void getAllMusic() {
        mAddMedias.clear();
        mAddSelectMedias.clear();
        mAddMusicAdapter.notifyDataSetChanged();
//        WaitingAnimationDialog.show(mContext);
        mIsAddMode=true;
        setAddModeView();
        mWaitView.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(R.drawable.loading_gif).into(mWaitView);

        DataUtil.getInstance().getMediaListFromDB(mHandler, SortType.SORT_LOCAL_ALL, "all_local", "media");
    }

    private void getLocalMusic(){
        mAddMedias.clear();
        mAddSelectMedias.clear();
        mAddMusicAdapter.notifyDataSetChanged();
        mIsAddMode=true;
        setAddModeView();
        mWaitView.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(R.drawable.loading_gif).into(mWaitView);

//        WaitingAnimationDialog.show(mContext);
        DataUtil.getInstance().getMediaListFromDB(mHandler, SortType.SORT_LOCAL, "all_local", "media");
    }

    private void getKuWoMusic(){
//        WaitingAnimationDialog.show(mContext);
        DataUtil.getInstance().getMediaListFromDB(mHandler, SortType.SORT_KUWO_LOCAL, "all_local", "media");
    }

    private void getLeTingMusic(){
        mAddMedias.clear();
        mAddSelectMedias.clear();
        mAddMusicAdapter.notifyDataSetChanged();
        mIsAddMode=true;
        setAddModeView();
        mWaitView.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(R.drawable.loading_gif).into(mWaitView);

//        WaitingAnimationDialog.show(mContext);
        DataUtil.getInstance().getMediaListFromDB(mHandler, SortType.SORT_LE_RADIO_LOCAL, "all_local", "media");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler!= null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;

        }
        if (mAddMedias != null) {
            mAddMedias.clear();

        }
        if (mAddSelectMedias != null) {
            mAddSelectMedias.clear();
        }

        if (mDeleteMediaDetails != null) {
            mDeleteMediaDetails.clear();
        }
    }
}
