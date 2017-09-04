package com.letv.leauto.ecolink.ui.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MapCfg;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.event.KeyboardVisibilityEvent;
import com.letv.leauto.ecolink.lemap.PoiHistoryManager;
import com.letv.leauto.ecolink.lemap.adapter.HistoryAdapter;
import com.letv.leauto.ecolink.lemap.adapter.SearchPoiAdapter;
import com.letv.leauto.ecolink.lemap.entity.SearchPoi;
import com.letv.leauto.ecolink.lemap.navi.Utils;
import com.letv.leauto.ecolink.receiver.ScreenRotationUtil;
import com.letv.leauto.ecolink.thincar.protocol.NaviBarSendHelp;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.LocationBaseFragment;
import com.letv.leauto.ecolink.ui.view.DeleteDataDialog;
import com.letv.leauto.ecolink.umeng.AnalyzeManager;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DeviceUtils;
import com.letv.leauto.ecolink.utils.LetvReportUtils;
import com.letv.leauto.ecolink.utils.MyAnimationDrawable;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class KeySearchFragment extends LocationBaseFragment implements TextWatcher,
        View.OnClickListener ,View.OnLongClickListener,AdapterView.OnItemClickListener{
    private static final String TAG = "KeySearchFragment";
    private String tag = "KeySearchFragment";
    public static String VOICE_SEARCH_KEY = "VOICE_SEARCH_KEY";
    public static String SEARCH_TYPE_NEARBY = "SEARCH_TYPE_NEARBY";
    public static String TCHINCAR_SEARCH_KEY = "TCHINCAR_SEARCH_KEY";
    public static String IS_THINCAR_QUICEK_SEARCH = "IS_THINCAR_QUICK_SEARCH";
    private final static  int COMPANY_ANIM=0;
    private final static int HOME_ANIM=1;

    private boolean isHide=false;
    private DataSendManager mDataSendManager;

    @Bind(R.id.activity_navi)
    RelativeLayout mRootView;

    @Bind(R.id.map_search_text)
    EditText mKeyWordEditView;
    @Bind(R.id.parking_lots_lyt)
    RelativeLayout mParkingLayout;
    @Bind(R.id.gas_station_lyt)
    RelativeLayout mGasLayout;
    @Bind(R.id.toilets_lyt)
    RelativeLayout mToiletLayout;
    @Bind(R.id.food_lyt)
    RelativeLayout mFoodLayout;

    @Bind(R.id.lyt_history)
    RelativeLayout mHistoryLayout;
    @Bind(R.id.history_lv)
    ListView mHistoryListView;
    @Bind(R.id.history_empty_view)
    TextView mHistoryEmptyView;
    @Bind(R.id.search_list_view)
    ListView mSearchListView;

    @Bind(R.id.iv_back)
    RelativeLayout iv_back;
    @Bind(R.id.img_delete)
    ImageView img_delete;
    @Bind(R.id.search_progress)
    ProgressBar search_progress;

    @Bind(R.id.rlt_home)
    RelativeLayout mHomeLayout;
    @Bind(R.id.company_layout)
    RelativeLayout mCompanyLayout;
    @Bind(R.id.home_image)
    ImageView mHomeImgView;
    @Bind(R.id.company_image)
    ImageView mCompanyImgView;

    @Bind(R.id.company_remind)
    ImageView company_remind;

    @Bind(R.id.home_remind)
    ImageView home_remind;

    @Bind(R.id.home_describe)
    TextView mHomeDescribe;
    @Bind(R.id.company_describe)
    TextView mCompanyDescribe;


    //    @Bind(R.id.poi_layout)
//    LinearLayout mPoiLayout;
//    @Bind(R.id.home_company_layout)
//    LinearLayout mHomeCompanyLayout;
    @Bind(R.id.favor_poi_layout)
    RelativeLayout mFavorPoiLayout;



    private String mCityName = "北京";
    //搜索类型,0:从地图主页,1:Point家或公司,2:设置界面,3:语音搜索
    private  int mSearchType = 0;
    private static Boolean isHome = false;
    private Boolean isNearBy = false;
    private String mQuickKeyWord;

    private SearchPoiAdapter mSearchAdapter = null;
    private List<SearchPoi> mPoiList=new ArrayList<>();
    private PoiSearch.Query mPoiQuery;// Poi查询条件类
    private PoiSearch mPoiSearch;// POI搜索
    private  LatLonPoint mCenterPoint = null;
    private List<SearchPoi> mHistoryPoiList = new ArrayList<SearchPoi>();
    private HistoryAdapter mHistoryAdapter = null;
    private boolean cleanListView = false;
    private PoiHistoryManager mHistoryManager;
    String mEndAddr = null;
    String mHomeAddr = null;
    String mCompanyAddr = null;
    private boolean cleanView;
    private KeyboardVisibilityEvent mKeyboardVisibilityEvent;
    public static KeySearchFragment getInstance(Bundle bundle) {
        KeySearchFragment mFragment = new KeySearchFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case COMPANY_ANIM:
                    company_remind.setVisibility(View.GONE);
                    mHandler.removeMessages(COMPANY_ANIM);
                    break;
                case HOME_ANIM:
                    home_remind.setVisibility(View.GONE);
                    mHandler.removeMessages(HOME_ANIM);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_key_search, null);
        } else {
            view = inflater.inflate(R.layout.fragment_key_search_l, null);
        }
        ButterKnife.bind(this, view);
        mHistoryManager=PoiHistoryManager.getInstance(mContext);
        mDataSendManager = DataSendManager.getInstance();
        mCityName = EcoApplication.getInstance().getCity();
        mSearchType = getArguments().getInt(MapCfg.SEARCH_TYPE);
        mKeyWordEditView.requestFocus();
        iv_back.setOnClickListener(this);
        img_delete.setOnClickListener(this);
        company_remind.setClickable(true);
        company_remind.setOnClickListener(this);
        home_remind.setClickable(true);
        home_remind.setOnClickListener(this);

        img_delete.setVisibility(View.INVISIBLE);
        search_progress.setVisibility(View.INVISIBLE);
        mKeyWordEditView.requestFocus();
        /**瘦车机进来，不弹出搜索框*/
        if (mSearchType != MapCfg.SEARCH_TYPE_THINCAR) {
            DeviceUtils.popKeyBoard(mContext);
        }
        mKeyWordEditView.setSelection(0);

        if (isNetConnect) {
            initData();

        } else {
            showNoNetDialog();
            initData();
        }
        mSearchListView.setHeaderDividersEnabled(false);
        return view;
    }


    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    public void setSearchAdapterClick() {
        mSearchAdapter.setClickListener(new SearchPoiAdapter.ClickListener() {
            @Override
            public void itemClick(int position) {
                mEndAddr = mPoiList.get(position).getAddrname() + "," + mPoiList.get(position).getLatitude() + "," + mPoiList.get(position).getLongitude();

                Trace.Debug("######  no address");
                saveAddressData(mEndAddr);
                mSearchType=MapCfg.SEARCH_TYPE_MAP;
                if (mSearchAdapter != null) {
                    mSearchAdapter.setType(mSearchType);
                }
                initHomeAndWorkAddrs();
                if (mHistoryAdapter!=null){
                    mHistoryAdapter.setIsAddPoi(false);
                }
                mPoiList.clear();
                mSearchAdapter.notifyDataSetChanged();
                mKeyWordEditView.setText("");
            }

            @Override
            public void addressClick(int position) {
                DeviceUtils.dropKeyBoard(mContext, mKeyWordEditView);
                if (mPoiList != null && mPoiList.size() > position) {
                    mEndAddr = mPoiList.get(position).getAddrname() + "," + mPoiList.get(position).getLatitude() + "," + mPoiList.get(position).getLongitude();
                    SearchPoi searchPoi= mPoiList.get(position);
                    searchPoi.setType(SearchPoi.SEARCH);
                    replaceMapFragmentByType(searchPoi,MapCfg.MAPMODE_POI);
                }


            }

            @Override
            public void iconClick(int position) {
                DeviceUtils.dropKeyBoard(mContext, mKeyWordEditView);
                if (mPoiList != null && mPoiList.size() > position) {
                    startNavi(mPoiList.get(position));
                }

            }
        });
    }


    private void setHistoryAdapterClick(){
        mHistoryAdapter.setClickListener(new HistoryAdapter.ClickListener() {
            @Override
            public void itemClick(int position) {
                if (mHistoryPoiList != null && mHistoryPoiList.size() > 0) {
                    if (mSearchType == MapCfg.SEARCH_TYPE_ADD) {
                        mEndAddr = mHistoryPoiList.get(position).getAddrname() + "," + mHistoryPoiList.get(position).getLatitude() + "," + mHistoryPoiList.get(position).getLongitude();

                        Trace.Debug("######  no address");
                        saveAddressData(mEndAddr);
                        mSearchType = MapCfg.SEARCH_TYPE_MAP;
                        if (mSearchAdapter != null) {
                            mSearchAdapter.setType(mSearchType);
                        }
                        initHomeAndWorkAddrs();
                        if (mHistoryAdapter != null) {
                            mHistoryAdapter.setIsAddPoi(false);
                        }
                        mKeyWordEditView.setHint(R.string.map_navi_destination);
                        initHistoryList();
                        return;
                    } else {
                        mKeyWordEditView.setText(mHistoryPoiList.get(position).getAddrname());

                    }
                }
            }

            @Override
            public void addressClick(int position) {
                if (mHistoryPoiList != null && mHistoryPoiList.size() > 0) {
                    SearchPoi searchPoi=mHistoryPoiList.get(position);
                    replaceMapFragmentByType(searchPoi,MapCfg.MAPMODE_POI);
                }


            }

            @Override
            public void iconClick(int position) {
                DeviceUtils.dropKeyBoard(mContext, mKeyWordEditView);
                if (mHistoryPoiList != null && mHistoryPoiList.size() > 0) {
                    startNavi(mHistoryPoiList.get(position));
                }

            }
        });
    }
    private void replaceMapFragmentByType(SearchPoi searchPoi, int mapmodePoi) {
        switch (mapmodePoi){
            case  MapCfg.MAPMODE_CUSTOME:
                break;
            case MapCfg.MAPMODE_ADD:{
                Trace.Debug("######  no address");
                saveAddressData(mEndAddr);
                Bundle nBundle=new Bundle();
                nBundle.putInt(MapCfg.MAPMODE,MapCfg.MAPMODE_ADD);
                nBundle.putBoolean(MapFragment.IS_HOME_ADDRESS,isHome);
                nBundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_ADD);
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.map_frame,MapFragment.getInstance(nBundle),MapFragment.class.getSimpleName()).commitAllowingStateLoss();}
            break;
            case MapCfg.MAPMODE_POI:
                searchPoi.setType(SearchPoi.NAVI);
                saveHistorySearch(searchPoi);
                Bundle nBundle=new Bundle();
                nBundle.putInt(MapCfg.MAPMODE,MapCfg.MAPMODE_POI);
                nBundle.putSerializable(MapCfg.POI_LOCATION,searchPoi);
                nBundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_MAP);
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.map_frame,MapFragment.getInstance(nBundle),MapFragment.class.getSimpleName()).commitAllowingStateLoss();
                break;
        }
    }



    private void startNavi(SearchPoi searchPoi){
        mEndAddr = searchPoi.getAddrname() + "," + searchPoi.getLatitude() + "," + searchPoi.getLongitude();
        searchPoi.setType(SearchPoi.NAVI);
        saveHistorySearch(searchPoi);
        chooseMap(CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_SELECT_KYE, 0),searchPoi);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.history_lv:
                if (position == mHistoryPoiList.size()) {
                    showDialog();
                } else {


                }
                break;
        }



    }
    private void initHistoryList(){
        //历史记录
        initHistoryData();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(mHistoryListView.getFooterViewsCount() == 0) {
            View footerview = inflater.inflate(R.layout.clean_listview_footerview, null);
            mHistoryListView.addFooterView(footerview);
        }
        mHistoryAdapter.notifyDataSetChanged();




    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        DeviceUtils.dropKeyBoard(mContext, mKeyWordEditView);
//        if (mKeyboardVisibilityEvent != null) {
//            mKeyboardVisibilityEvent.unRegister();
//            mKeyboardVisibilityEvent = null;
//        }
        //通知车机显示键盘
        DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.CONTROL_LIGHT_AVN_PARAM, ThinCarDefine.ProtocolToCarAction.SHOW_BOTTOM_BAR, 0);
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutListener);
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
        mHistoryPoiList.clear();

    }

    protected void initData() {
        mKeyWordEditView.addTextChangedListener(this);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
        //添加键盘监听
        mKeyWordEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Trace.Debug("*****　actionid ＝"+actionId);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    DeviceUtils.dropKeyBoard(mContext, mKeyWordEditView);
                }else if (actionId==EditorInfo.IME_ACTION_UNSPECIFIED){
                    mKeyWordEditView.setText("");
                }
                return false;
            }
        });
        mKeyWordEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LetvReportUtils.reportMapSearchEvent();
                mKeyWordEditView.setCursorVisible(true);
            }
        });

        mParkingLayout.setOnClickListener(this);
        mGasLayout.setOnClickListener(this);
        mToiletLayout.setOnClickListener(this);
        mFoodLayout.setOnClickListener(this);
//        mSearchListView.setOnItemClickListener(this);

        mSearchListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DeviceUtils.dropKeyBoard(mContext, mKeyWordEditView);
                return false;
            }
        });

        mSearchAdapter = new SearchPoiAdapter(mPoiList, mContext, mSearchType);
        setSearchAdapterClick();
        mSearchListView.setAdapter(mSearchAdapter);
        if (mSearchType==MapCfg.SEARCH_TYPE_ADD){
            mHistoryAdapter = new HistoryAdapter(mHistoryPoiList, mContext,true );
        }else{
            mHistoryAdapter = new HistoryAdapter(mHistoryPoiList, mContext,false );
        }

        mHistoryListView.setAdapter(mHistoryAdapter);
        setHistoryAdapterClick();
        switch (mSearchType) {

            case MapCfg.SEARCH_TYPE_MAP:
//                DeviceUtils.popKeyBoard(mContext);
                mKeyWordEditView.setHint(R.string.map_navi_destination);
                isHome = getArguments().getBoolean(MapFragment.IS_HOME_ADDRESS);
                break;
            case MapCfg.SEARCH_TYPE_ADD:
                mKeyWordEditView.setHint(R.string.map_navi_destination);
                if (getArguments() != null) {
                    isHome = getArguments().getBoolean(MapFragment.IS_HOME_ADDRESS);
                    if (isHome){
                        mKeyWordEditView.setHint(R.string.map_search_home);
                    }else{
                        mKeyWordEditView.setHint(R.string.map_search_company);
                    }
                }
                break;
            case MapCfg.SEARCH_TYPE_THINCAR:
                if (getArguments() != null) {
                    String searchKey = getArguments().getString(TCHINCAR_SEARCH_KEY);
                    isNearBy=true;
                    mSearchType=0;
                    AMapLocation aMapLocation=EcoApplication.getInstance().getCurrentLoaction();
                    mCenterPoint=new LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    if (searchKey.equalsIgnoreCase(NaviBarSendHelp.QUICK_SEARCH_PARKING)) {
                        mQuickKeyWord = mContext.getString(R.string.str_parking_lots);
                        setKeywordEditString(mQuickKeyWord);
                    } else if (searchKey.equalsIgnoreCase(NaviBarSendHelp.QUICK_SEARCH_GASSTATION)) {
                        mQuickKeyWord = mContext.getString(R.string.str_gas_station);
                        setKeywordEditString(mQuickKeyWord);
                    } else if (searchKey.equalsIgnoreCase(NaviBarSendHelp.QUICK_SEARCH_TIOLET)) {
                        mQuickKeyWord = mContext.getString(R.string.str_toilets);
                        setKeywordEditString(mQuickKeyWord);
                    } else if (searchKey.equalsIgnoreCase(NaviBarSendHelp.QUICK_SEARCH_FOOD)) {
                        mQuickKeyWord = mContext.getString(R.string.str_food);
                        setKeywordEditString(mQuickKeyWord);
                    }

                }
                break;
            default:
                break;
        }
        mHistoryListView.setOnItemClickListener(this);
        initHomeAndWorkAddrs();
        initHistoryList();

    }



    private void deleteText(){
        String text = mKeyWordEditView.getText().toString();
        if(text.length()>0){
            mKeyWordEditView.setText("");
            initHistoryList();
        }
    }




    /**
     * 开始进行poi搜索
     */
    protected void setKeywordEditString(String Keywords) {
        CacheUtils.getInstance(mContext).putBoolean(Constant.RECENT_MAP_INFO, true);
        DeviceUtils.dropKeyBoard(mContext, mKeyWordEditView);
        mKeyWordEditView.setText(Keywords);
        mKeyWordEditView.setSelection(Keywords.length());
        mSearchAdapter.setPrefix(Keywords);
    }

    private void initHistoryData() {
        boolean isThincarQuickSearch = getArguments().getBoolean(IS_THINCAR_QUICEK_SEARCH,false);
        mHistoryPoiList.clear();
        mPoiList.clear();
        mSearchAdapter.notifyDataSetChanged();
        Trace.Debug("***** clear data");
        if (mSearchType==MapCfg.SEARCH_TYPE_ADD){
            mHistoryPoiList.addAll(mHistoryManager.getHistoryPoisSType());
            mFavorPoiLayout.setVisibility(View.GONE);
        }else{
            mHistoryPoiList.addAll(mHistoryManager.getHistoryPois());
            if (isThincarQuickSearch) {
                mFavorPoiLayout.setVisibility(View.GONE);
            } else {
                mFavorPoiLayout.setVisibility(View.VISIBLE);
            }
        }
        if (isThincarQuickSearch) {
            mHistoryLayout.setVisibility(View.GONE);
        } else {
            mHistoryLayout.setVisibility(View.VISIBLE);
        }
        if (mHistoryPoiList.size() == 0) {
            mHistoryListView.setVisibility(View.GONE);
            /*if (!GlobalCfg.IS_POTRAIT)*/
            if (isThincarQuickSearch) {
                mHistoryEmptyView.setVisibility(View.GONE);
            } else {
                mHistoryEmptyView.setVisibility(View.VISIBLE);
            }
            return;
        }


        if(isThincarQuickSearch) {
            mHistoryListView.setVisibility(View.GONE);

        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        final String newText = s.toString().trim();
        if (newText == null || newText.equals("")) {
            mKeyWordEditView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            img_delete.setVisibility(View.INVISIBLE);
            search_progress.setVisibility(View.INVISIBLE);
            initHistoryList();
            return;
        } else {
            mKeyWordEditView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            img_delete.setVisibility(View.INVISIBLE);
            search_progress.setVisibility(View.VISIBLE);
        }
        if (newText.contains("厕所")||newText.contains("停车场")
                ||newText.contains("美食")||newText.contains("卫生间")
                ||newText.contains("加油站")||newText.contains("加气站")
                ||newText.contains("卫生间")||newText.contains("洗手间")){
            isNearBy=true;
        }

            doPoiSearch(newText);
//


        //统计搜索关键字
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AnalyzeManager.MapPara.KEY_WORD, s.toString().trim());
        map.put(AnalyzeManager.MapPara.DEVICE_ID, DeviceUtils.getDeviceId(mContext));
        MobclickAgent.onEvent(mContext, AnalyzeManager.Event.MAP, map);
    }

    private void doInputSearch(final String newText) {
        SearchPoi searchPoi=new SearchPoi("0","0",newText);
        searchPoi.setType(SearchPoi.SEARCH);
        saveHistorySearch(searchPoi);
        cleanView = false;
        if (newText.length()<=0){
            return;
        }
        InputtipsQuery inputquery = new InputtipsQuery(newText, mCityName);
        inputquery.setCityLimit(false);



        Inputtips inputTips = new Inputtips(mContext, inputquery);
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> tipList, int rCode) {
                if (tipList==null||tipList.size()==0||rCode!=1000){
                    ToastUtil.show(mContext, R.string.str_nomach_address);
                    img_delete.setVisibility(View.VISIBLE);
                    Trace.Debug("##### VISIBLE");
                    search_progress.setVisibility(View.INVISIBLE);
                    return;
                }

                mPoiList.clear();
                for (int i = 0; i < tipList.size(); i++) {
                    SearchPoi item = new SearchPoi();
                    Tip tip=tipList.get(i);
                    if (null != tip.getName() && null != tip.getDistrict() && null != tip.getPoint()) {

                        item.setAddrname(tip.getName());
                        //添加搜寻的地址街道和于当前的距离,begin
                        item.setDistrict(tip.getDistrict());
                        item.setLatitude(tip.getPoint().getLatitude() + "");
                        item.setLongitude(tip.getPoint().getLongitude() + "");
                        mPoiList.add(item);
                    }
                }
                //shimeng fix for bug1257,20160425,end
                mSearchAdapter.setmList(mPoiList, newText,false);
                mSearchAdapter.notifyDataSetChanged();
                mHistoryLayout.setVisibility(View.GONE);
                if (GlobalCfg.IS_POTRAIT||mSearchType==MapCfg.SEARCH_TYPE_ADD){
                    mFavorPoiLayout.setVisibility(View.GONE);
                }else{
                    mFavorPoiLayout.setVisibility(View.VISIBLE);
                }
                img_delete.setVisibility(View.VISIBLE);
                search_progress.setVisibility(View.INVISIBLE);

            }
        });

        inputTips.requestInputtipsAsyn();
        img_delete.setVisibility(View.INVISIBLE);
        Trace.Debug("##### INVISIBLE");
        search_progress.setVisibility(View.VISIBLE);



    }


    @Override
    public void afterTextChanged(Editable s) {
        final String newText = s.toString().trim();
        if (newText.length() <= 0) {
            img_delete.setVisibility(View.INVISIBLE);
            if (isNearBy ) {
                CacheUtils.getInstance(mContext).putBoolean(Constant.RECENT_MAP_INFO, false);
            } else {
                cleanView = true;
            }
            if (null != mPoiList) {
                mPoiList.clear();
            }
            mSearchAdapter.notifyDataSetChanged();
            isNearBy = false;


        }

    }


    private  void  doPoiSearch(String newText){
        if (newText.length() > 0) {
            if (mCityName==null||mCenterPoint==null){
                ToastUtil.show(mContext, R.string.str_location_faild);
                return;
            }
            SearchPoi searchPoi=new SearchPoi("0","0",newText);
            searchPoi.setType(SearchPoi.SEARCH);
            saveHistorySearch(searchPoi);
            cleanListView = false;
            mQuickKeyWord = newText;
            CacheUtils.getInstance(mContext).putBoolean(Constant.RECENT_MAP_INFO, true);
            if (mCenterPoint != null) {
                mPoiQuery = new PoiSearch.Query(newText, "", mCityName);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
                mPoiQuery.setPageSize(20);
                mPoiQuery.setCityLimit(false);
                mPoiSearch = new PoiSearch(mContext, mPoiQuery);
                mPoiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() { /*** 附近的加油站/银行搜索*/
                @Override
                public void onPoiSearched(PoiResult result, int rCode) {
                    search_progress.setVisibility(View.INVISIBLE);
                    img_delete.setVisibility(View.VISIBLE);
                    isNearBy = false;

                    if (rCode == 1000) {
                        if (result != null && result.getQuery() != null) {// 搜索poi的结果
                            // 取得搜索到的poiitems有多少页
                            List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                            if (poiItems != null && poiItems.size() > 0) {
                                SearchPoi searchPoi;
                                mPoiList.clear();
                                for (int i = 0; i < poiItems.size(); i++) {
                                    PoiItem poiItem=poiItems.get(i);
                                    searchPoi = new SearchPoi();
                                    searchPoi.setAddrname(poiItem.getTitle());
                                    searchPoi.setDistrict(poiItem.getDirection());
                                    searchPoi.setLatitude(poiItem.getLatLonPoint() .getLatitude()+ "");
                                    searchPoi.setLongitude(poiItem.getLatLonPoint().getLongitude() + "");
                                    searchPoi.setDistrict(poiItem.getSnippet());
                                    searchPoi.setDistance(poiItem.getDistance());
                                    mPoiList.add(searchPoi);

                                }
                                mSearchAdapter.notifyDataSetChanged();
                                mHistoryLayout.setVisibility(View.GONE);
                                if (GlobalCfg.IS_POTRAIT||mSearchType==MapCfg.SEARCH_TYPE_ADD){
                                    mFavorPoiLayout.setVisibility(View.GONE);
                                }else {
                                    mFavorPoiLayout.setVisibility(View.VISIBLE);
                                }
                                if (cleanListView) {
                                    mPoiList.clear();
                                    mSearchAdapter.notifyDataSetChanged();

                                }

                            } else {
                                mPoiList.clear();
                                mSearchAdapter.notifyDataSetChanged();
                                ToastUtil.show(mContext, getResources().getString(R.string.map_no_result));
                            }
                        }

                    } else if (rCode == 27) {
                        Trace.Debug("#####"+ mContext.getString(R.string.map_error_network));
                    } else if (rCode == 32) {
                        Trace.Debug("#####"+  mContext.getString(R.string.map_error_network));
                    } else {
                        Trace.Debug("#####"+  mContext.getString(R.string.map_error_network));
                    }

                }

                    @Override
                    public void onPoiItemSearched(PoiItem poiItem, int i) {
                        String address = poiItem.getSnippet();
                    }
                });
                if (isNearBy){
                mPoiSearch.setBound(new PoiSearch.SearchBound(mCenterPoint, 3000, true));}

                // 设置搜索区域为以lp点为圆心，其周围2000米范围
                mPoiSearch.searchPOIAsyn();// 异步搜索
            }
        } else {
            cleanListView = true;
        }
    }
    private void saveAddressData(String data) {
        if (data != null && !data.equals("") && !data.equals("null")) {
            if (isHome) {
                CacheUtils.getInstance(mContext).putString(Constant.SpConstant.HOME_ADDR, data);
            } else {
                CacheUtils.getInstance(mContext).putString(Constant.SpConstant.WORK_ADDR, data);
            }
        } else {
            ToastUtil.show(mContext, R.string.str_noaddress_check_network);
            Trace.Debug("######  no address");
        }
    }

    /**
     * 保存搜索的数据
     * @param searchPoi
     */
    private void saveHistorySearch(SearchPoi searchPoi) {
        mHistoryManager.saveSearchPoi(searchPoi);
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        super.onLocationChanged(aMapLocation);
        mCenterPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        if (mHomeAddr!=null){
            String[] strings=mHomeAddr.split(",");
            String distance=Utils.getDistance(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()),new LatLng(Double.valueOf(strings[1]),Double.valueOf(strings[2])));
            mHomeDescribe.setText("距离"+distance);
        }
        if (mCompanyAddr!=null){
            String[] strings=mCompanyAddr.split(",");
            String distance=Utils.getDistance(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()),new LatLng(Double.valueOf(strings[1]),Double.valueOf(strings[2])));
            mCompanyDescribe.setText("距离"+distance);
        }
        mCityName = aMapLocation.getCity();
    }



    private void replaceFragmentByPoiSearch(boolean isHome) {
        Bundle nBundle=new Bundle();
        nBundle.putInt(MapCfg.MAPMODE,1);
        nBundle.putBoolean(MapFragment.IS_HOME_ADDRESS,isHome);
        nBundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_ADD);
        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().
                replace(R.id.map_frame,MapFragment.getInstance(nBundle),MapFragment.class.getSimpleName()).commitAllowingStateLoss();
    }
    private void replaceFragmentByRoutePlan() {
        Bundle nBundle = new Bundle();
        nBundle.putString(RoutePlanFragment.ROUTEPLAN_END_ADDRESS, mEndAddr);
        nBundle.putString(RoutePlanFragment.LAUNCH_FRAGMENT, RoutePlanFragment.MAP);
        AMapNavi.getInstance(mContext).destroy();
        RoutePlanFragment secondFragment = RoutePlanFragment.getInstance(nBundle);
        ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, secondFragment,RoutePlanFragment.class.getSimpleName()).commitAllowingStateLoss();

    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("KeySearchFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("KeySearchFragment");
        InputMethodManager inputMethodManager =
                (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mKeyWordEditView.getWindowToken(), 0);
    }


    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);
        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*actionBackPress();*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                actionBackPress();
                break;
            case R.id.parking_lots_lyt:
                if (DeviceUtils.isFastClick()) {
                    return;
                }
                isNearBy=true;
                mQuickKeyWord = mContext.getString(R.string.str_parking_lots);
                setKeywordEditString(mQuickKeyWord);

                break;
            case R.id.gas_station_lyt:
                if (DeviceUtils.isFastClick()) {
                    return;
                }
                isNearBy=true;
                mQuickKeyWord = mContext.getString(R.string.str_gas_station);
                setKeywordEditString(mQuickKeyWord);
                break;
            case R.id.toilets_lyt:
                if (DeviceUtils.isFastClick()) {
                    return;
                }
                isNearBy=true;
                mQuickKeyWord = mContext.getString(R.string.str_toilets);
                setKeywordEditString(mQuickKeyWord);
                break;
            case R.id.food_lyt:
                if (DeviceUtils.isFastClick()) {
                    return;
                }
                isNearBy=true;
                mQuickKeyWord = mContext.getString(R.string.str_food);
                setKeywordEditString(mQuickKeyWord);
                break;
            case R.id.clean_recent_list:
                showDialog();
                break;
            case R.id.img_delete:
                deleteText();
                break;

            case R.id.rlt_home:
                mHomeAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HOME_ADDR, null);
                if (mHomeAddr == null) {
                    home_remind.setVisibility(View.GONE);
                    mHandler.removeMessages(HOME_ANIM);
                    replaceFragmentByPoiSearch(true);
                } else {
                    mEndAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HOME_ADDR, "");



                    String[] strings=mEndAddr.split(",");
                    SearchPoi searchPoi=new SearchPoi(strings[1],strings[2],strings[0]);
                    chooseMap(CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_SELECT_KYE,0),searchPoi);
                    home_remind.setVisibility(View.GONE);
                    if (mHandler != null) {
                        mHandler.removeMessages(1);
                    }

                }
                break;
            case R.id.company_layout:
                mCompanyAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.WORK_ADDR, null);
                if (mCompanyAddr == null) {
                    company_remind.setVisibility(View.GONE);
                    mHandler.removeMessages(COMPANY_ANIM);
                    replaceFragmentByPoiSearch(false);
                } else {
                    mEndAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.WORK_ADDR, "");

                    String[] strings=mEndAddr.split(",");
                    SearchPoi searchPoi=new SearchPoi(strings[1],strings[2],strings[0]);
                    chooseMap(CacheUtils.getInstance(mContext).getInt(SettingCfg.NAVI_SELECT_KYE,0),searchPoi);
                    company_remind.setVisibility(View.GONE);
                    if (mHandler != null) {
                        mHandler.removeMessages(COMPANY_ANIM);
                    }

                }
                break;


        }
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.rlt_home:
                home_remind.setVisibility(View.GONE);
                mHandler.removeMessages(HOME_ANIM);
                replaceFragmentByPoiSearch(true);
                break;
            case R.id.company_layout:
                company_remind.setVisibility(View.GONE);
                mHandler.removeMessages(COMPANY_ANIM);
                replaceFragmentByPoiSearch(false);
                break;
            default:
                break;
        }
        return false;
    }


    public void showDialog() {
        HomeActivity act = (HomeActivity)mContext;
        DeleteDataDialog dialog = new DeleteDataDialog(act, "KeySearchFragment");
        // EcoDialog dialog = new EcoDialog(mContext, R.style.Dialog, "清除全部历史记录?");
        dialog.setListener(new DeleteDataDialog.ICallDialogCallBack() {
            @Override
            public void onConfirmClick(DeleteDataDialog currentDialog) {
//                CacheUtils.getInstance(mContext).putString(Constant.SpConstant.HISTORY_SEARCHKEY, null);
                mHistoryManager.deleteAllHistory();
                initHistoryList();
                //     currentDialog.dismiss();
            }

            @Override
            public void onCancelClick(DeleteDataDialog currentDialog) {
                //  currentDialog.dismiss();
            }

        });
        dialog.show();
    }

    private void chooseMap(int which, SearchPoi searchPoi){
        switch (which){
            case  0:
                replaceFragmentByRoutePlan();
                break;
            case 1:

                startGaoDeMap(searchPoi);

                break;
            case 2:

                startBaiDuMap(searchPoi);
                break;
        }
    }
    /**
     *显示导航选择列表
     */
    final String[] mItems = {"ecolink","高德","百度"};


    private void startGaoDeMap(SearchPoi searchPoi) {
        if(DeviceUtils.isAvilible(mContext,"com.autonavi.minimap")){
            notifyCurrentThirdAppPage();
            try{
                Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=ecolink&poiname=我的目的地&lat=" +
                        searchPoi.getLatitude() + "&lon=" + searchPoi.getLongitude() + "&dev=1&style=2");
                mContext.startActivity(intent);
                ScreenRotationUtil.commandShowPop(mContext);
                if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                    ScreenRotationUtil.startLandService(mContext,"com.autonavi.minimap");
                }
            } catch (URISyntaxException e)
            {e.printStackTrace(); }
        }else{
            ToastUtil.show(mContext,R.string.str_install_gaode_map);
//            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }

    private void startBaiDuMap(SearchPoi searchPoi) {
        Intent intent;
        if(DeviceUtils.isAvilible(mContext,"com.baidu.BaiduMap")){
            notifyCurrentThirdAppPage();
            try {
                com.baidu.mapapi.model.LatLng sourceLatLng= new com.baidu.mapapi.model.LatLng(Double.valueOf(searchPoi.getLatitude()),Double.valueOf(searchPoi.getLongitude()));
                CoordinateConverter converter  = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.COMMON);
// sourceLatLng待转换坐标
                converter.coord(sourceLatLng);
                com.baidu.mapapi.model.LatLng desLatLng = converter.convert();
//                          intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving®ion=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                intent = Intent.getIntent("intent://map/direction?" +
                        //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
                        "destination=latlng:"+desLatLng.latitude+","+desLatLng.longitude+"|name:我的目的地"+        //终点
                        "&mode=driving&" +          //导航路线方式
                        "region=" + mCityName +           //
                        "&src=ecolink#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                //intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&src=thirdapp.navi.yourCompanyName.yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");


                mContext.startActivity(intent); //启动调用
                ScreenRotationUtil.commandShowPop(mContext);
                if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                    ScreenRotationUtil.startLandService(mContext,"com.baidu.BaiduMap");
                }
            } catch (URISyntaxException e) {
                Log.e("intent", e.getMessage());
            }
        }else{//未安装
            //market为路径，id为包名
            //显示手机上所有的market商店
            ToastUtil.show(mContext,R.string.str_install_baidu_map);
//            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }


    }

    /**
     * 返回界面跳转逻辑
     */
    private void actionBackPress() {
//        DeviceUtils.dropKeyBoard(mContext, mKeyWordEditView);
        Boolean isIn_recent = CacheUtils.getInstance(mContext).getBoolean(Constant.RECENT_MAP_INFO, false);
        if (isIn_recent) {
            if (mPoiList != null) {
                mPoiList.clear();
            }
            mSearchAdapter.setmList(mPoiList, null, false);

            mSearchAdapter.notifyDataSetChanged();
            mKeyWordEditView.setText(null);
            initHistoryData();

            CacheUtils.getInstance(mContext).putBoolean(Constant.RECENT_MAP_INFO, false);
//            return;
        }
        Bundle nBundle = new Bundle();

        /**
         * jerome 屏蔽的，不知道这段code做什么功能的,
         * 会影响在回到map页面出现闪现输入框
         */
//        if (mSearchListView != null) {
//            ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().
//                    replace(R.id.map_frame, KeySearchFragment.getInstance(nBundle)).commitAllowingStateLoss();
//        }

        switch (mSearchType) {
            case MapCfg.SEARCH_TYPE_THINCAR:
            case MapCfg.SEARCH_TYPE_VOICE:
            case MapCfg.SEARCH_TYPE_MAP:
                MapFragment mFragment = MapFragment.getInstance(nBundle);

                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.map_frame, mFragment).commitAllowingStateLoss();
                break;
            case MapCfg.SEARCH_TYPE_ADD:
                nBundle.putInt(MapCfg.MAPMODE,1);
                nBundle.putBoolean(MapFragment.IS_HOME_ADDRESS,isHome);
                nBundle.putInt(MapCfg.SEARCH_TYPE, MapCfg.SEARCH_TYPE_ADD);
                ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.map_frame,MapFragment.getInstance(nBundle),MapFragment.class.getSimpleName()).commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }



    //添加根部view监听
    ViewTreeObserver.OnGlobalLayoutListener  mLayoutListener=new ViewTreeObserver.OnGlobalLayoutListener() {
        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        public void onGlobalLayout() {

            Rect r=new Rect();
            mRootView.getWindowVisibleDisplayFrame(r);
            int screenHeight= mRootView.getRootView().getHeight();
            int heightDiff = screenHeight - (r.bottom - r.top);
            if(heightDiff<-200){
                return;
            }
            if(heightDiff>300){
                /** 通知车机隐藏键盘 */
                mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.CONTROL_LIGHT_AVN_PARAM,ThinCarDefine.ProtocolToCarAction.HIDE_BOTTOM_BAR,0);
            }else {
                /** 通知车机显示键盘 */
                mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.CONTROL_LIGHT_AVN_PARAM, ThinCarDefine.ProtocolToCarAction.SHOW_BOTTOM_BAR, 0);
            }
        }
    };





    private void initHomeAndWorkAddrs() {
        mHomeAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.HOME_ADDR, null);
        if (mHomeAddr == null) {
            mHomeDescribe.setText("点击添加家地址");
            mHomeLayout.setOnClickListener(this);

        } else {
            mHomeDescribe.setText("在家的附近");
            mHomeLayout.setOnLongClickListener(this);
            mHomeLayout.setOnClickListener(this);
        }
        mCompanyAddr = CacheUtils.getInstance(mContext).getString(Constant.SpConstant.WORK_ADDR, null);
        if (mCompanyAddr == null) {
            mCompanyDescribe.setText("点击添加公司地址");
            mCompanyLayout.setOnClickListener(this);
        } else {
            mCompanyDescribe.setText("在公司附近");
            mCompanyLayout.setOnLongClickListener(this);
            mCompanyLayout.setOnClickListener(this);
        }

        if (mHomeAddr != null || mCompanyAddr != null) {

            if (CacheUtils.getInstance(mContext).getBoolean(Constant.IS_FIRST_TIME_MAP, true)) {
                CacheUtils.getInstance(mContext).putBoolean(Constant.IS_FIRST_TIME_MAP, false);

                if(mHomeAddr != null){
                    playHomeFrameAnimation();
                } else {
                    playCompanyFrameAnimation();
                }
            } else {

                home_remind.setVisibility(View.GONE);
                company_remind.setVisibility(View.GONE);
            }

        }

    }


    /**
     * 播放帧动画
     */
    private void playCompanyFrameAnimation(){
//      company_remind.setImageResource(R.drawable.map_company_remind_animation);
//      AnimationDrawable ad = (AnimationDrawable) company_remind.getDrawable();
//      ad.start();

        company_remind.setVisibility(View.VISIBLE);
        company_remind.bringToFront();
        MyAnimationDrawable.animateRawManuallyFromXML(R.drawable.map_company_remind_animation,
                company_remind, new Runnable() {

                    @Override
                    public void run() {
                        // TODO onStart
                        // 动画开始时回调
                    }
                }, new Runnable() {

                    @Override
                    public void run() {
                        // TODO onComplete
                        // 动画结束时回调
                    }
                });
        mHandler.sendEmptyMessageDelayed(COMPANY_ANIM, 5000);
    }

    /**
     * 播放帧动画
     */
    private void playHomeFrameAnimation(){
//      company_remind.setImageResource(R.drawable.map_company_remind_animation);
//      AnimationDrawable ad = (AnimationDrawable) company_remind.getDrawable();
//      ad.start();

        home_remind.setVisibility(View.VISIBLE);
        home_remind.bringToFront();
        MyAnimationDrawable.animateRawManuallyFromXML(R.drawable.map_company_remind_animation,
                home_remind, new Runnable() {

                    @Override
                    public void run() {
                        // TODO onStart
                        // 动画开始时回调
                    }
                }, new Runnable() {

                    @Override
                    public void run() {
                        // TODO onComplete
                        // 动画结束时回调
                    }
                });
        mHandler.sendEmptyMessageDelayed(HOME_ANIM, 5000);
    }

    private void notifyCurrentThirdAppPage() {
        GlobalCfg.IS_THIRD_APP_STATE = true;
        GlobalCfg.isCarResumed = false;
        mDataSendManager.notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                ThinCarDefine.PageIndexDefine.THIRAD_APP_PAGE,0);
    }
}
