package com.letv.leauto.ecolink.lemap.offlinemap1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapView;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.fragment.SettingNaviFragment;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/4.
 */
public class OfflineMapFragment extends BaseFragment implements OfflineManager.MyOfflineMapDownloadListener, ViewPager.OnPageChangeListener, View.OnClickListener {
    public static final String TAG = "OfflineMapFragment";
    @Bind(R.id.ll_city_list)
    LinearLayout mLlCityList;
    @Bind(R.id.ll_download_manager)
    LinearLayout mLlDownloadManger;
    @Bind(R.id.download_list_text)
    TextView mDownloadText;
    @Bind(R.id.downloaded_list_text)
    TextView mDownloadedText;
    @Bind(R.id.back_image_view)
    ImageView mBackImage;
    @Bind(R.id.content_viewpage)
    ViewPager mContentViewPage;

    @Bind(R.id.ll_map)
    LinearLayout ll_map;
    @Bind(R.id.include_ll)
    LinearLayout include_ll;

    @Bind(R.id.tv_cancle)
    TextView cancle;

    @Bind(R.id.checkbox)
    CheckBox checkbox;

    @Bind(R.id.tv_delete)
    LinearLayout tv_delete;

    @Bind(R.id.listview_mapdowner)
    ListView mListView_downed_contral;


    private MapView mapView;
    private ExpandableListView mAllOfflineMapList;
    private OfflineMapManager amapManager;
    private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市

    private TextView tv_map_contral;
    private TextView tv_line;
    private ListView mDownLoadedList;
    private OfflineDownloadedAdapter mDownloadedAdapter;

    private OfflineDownloadedAdapter adapter_contral;

    /**
     * 更新所有列表
     */
    private final static int UPDATE_LIST = 0;
    /**
     * 显示toast log
     */
    private final static int SHOW_MSG = 1;

    private final static int DISMISS_INIT_DIALOG = 2;
    private final static int SHOW_INIT_DIALOG = 3;

    //选择是删除的城市
    List<OfflineMapCity> chosedCities;

    private List<OfflineMapCity> downCities;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LIST:

                    if (mContentViewPage.getCurrentItem() == 0) {
                        if (null != adapter) {
                            ((BaseExpandableListAdapter) adapter)
                                    .notifyDataSetChanged();
                        }
                    } else {
                        mDownloadedAdapter.notifyDataChange();
                        mListView_downed_contral.setAdapter(adapter_contral);
                        adapter_contral.notifyDataChange();
                        if (adapter_contral.getChosePosition().size() < adapter_contral.getCities().size()) {
                            checkbox.setChecked(false);
                        } else if (adapter_contral.getChosePosition().size() == adapter_contral.getCities().size()) {
                            checkbox.setChecked(true);
                        }

                        if (mDownloadedAdapter.getCities().size() <= 0) {
                            tv_map_contral.setVisibility(View.GONE);
                            if (tv_line != null) {
                                tv_line.setVisibility(View.GONE);
                            }

                        } else {
                            tv_map_contral.setVisibility(View.VISIBLE);
                            if (tv_line != null) {
                                tv_line.setVisibility(View.VISIBLE);
                            }

                        }

                    }
                    break;
                case SHOW_MSG:
                    Toast.makeText(mContext, (String) msg.obj,
                            Toast.LENGTH_SHORT).show();

                    break;

                case DISMISS_INIT_DIALOG:
//                    initDiaTrace.Debugismiss();
                    handler.sendEmptyMessage(UPDATE_LIST);
                    break;
                case SHOW_INIT_DIALOG:
//                    if (initDialog != null) {
//                        initDialog.show();
//                    }
                    break;

                default:
                    break;
            }
        }

    };
    private OfflineListAdapter adapter;
    private OfflinePagerAdapter mPageAdapter;
    private View view;


    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.offline_map_layout, null);
        } else {
            view = inflater.inflate(R.layout.offline_map_layout_l, null);
        }
        ButterKnife.bind(this, view);
        addListenes();
        return view;

    }

    private void addListenes() {
        cancle.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        mapView = new MapView(mContext);
        chosedCities = new ArrayList<>();
        downCities = new ArrayList<>();
        amapManager=OfflineManager.getInstance(mContext,this).getManager();


        AsyncTaskUtil.newInstance().execute(new AsyncTaskUtil.AsyncTaskListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public Object doInBackground(Object... params) {
                List<OfflineMapCity> list = getOfflineDownloadCityList();
                return list;
            }

            @Override
            public void onProgressUpdate(int progress) {

            }

            @Override
            public void onPostExecute(Object result) {
                Trace.Error(TAG, ((List<OfflineMapCity>) result).toString());
                initAllCityList();
                initDownloadedList();
                mPageAdapter = new OfflinePagerAdapter(mContentViewPage,
                        mAllOfflineMapList, view);

                mContentViewPage.setAdapter(mPageAdapter);
                mContentViewPage.setCurrentItem(0);
                mContentViewPage.setOnPageChangeListener(OfflineMapFragment.this);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onCancelled(Object result) {

            }
        });

    }

    private List<OfflineMapCity> getOfflineDownloadCityList() {
        int executeCount = 0;
        int preSize = 0;
        int currentSize = 0;
        while (executeCount <= 20000 || preSize - currentSize != 0) {
            preSize = currentSize;
            currentSize = amapManager.getDownloadOfflineMapCityList().size();
            executeCount++;
        }
        return amapManager.getDownloadOfflineMapCityList();
    }

    private void initAllCityList() {
        // 扩展列表
        View provinceContainer = LayoutInflater.from(mContext).inflate(R.layout.offline_province_listview, null);
        mAllOfflineMapList = (ExpandableListView) provinceContainer.findViewById(R.id.province_download_list);

        initProvinceListAndCityMap();
        adapter = new OfflineListAdapter(provinceList, amapManager,
                mContext);
        // 为列表绑定数据源
        mAllOfflineMapList.setAdapter(adapter);
        // adapter实现了扩展列表的展开与合并监听
        mAllOfflineMapList.setOnGroupCollapseListener(adapter);
        mAllOfflineMapList.setOnGroupExpandListener(adapter);
        mAllOfflineMapList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition == 0 || groupPosition == 1 || groupPosition == 2 || groupPosition == 3) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        mAllOfflineMapList.setGroupIndicator(null);
        mAllOfflineMapList.expandGroup(0);
        mAllOfflineMapList.expandGroup(1);
        mAllOfflineMapList.expandGroup(2);
    }

    /**
     * sdk内部存放形式为<br>
     * 省份 - 各自子城市<br>
     * 北京-北京<br>
     * ...<br>
     * 澳门-澳门<br>
     * 概要图-概要图<br>
     * <br>
     * 修改一下存放结构:<br>
     * 概要图-概要图<br>
     * 直辖市-四个直辖市<br>
     * 港澳-澳门香港<br>
     * 省份-各自子城市<br>
     */
    private void initProvinceListAndCityMap() {

        List<OfflineMapProvince> lists = amapManager.getOfflineMapProvinceList();

        provinceList.add(null);
        provinceList.add(null);
        provinceList.add(null);
        provinceList.add(null);

        // 添加3个null 以防后面添加出现 index out of bounds

        ArrayList<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
        ArrayList<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
        ArrayList<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图
        for (int i = 0; i < lists.size(); i++) {
            OfflineMapProvince province = lists.get(i);
            if (province.getCityList().size() != 1) {//普通省份
                // 普通省份
                provinceList.add(i + 4, province);
                // cityMap.put(i + 3, cities);

            } else {
                String name = province.getProvinceName();
                if (name.contains("香港")) {
                    gangaoList.addAll(province.getCityList());
                } else if (name.contains("澳门")) {
                    gangaoList.addAll(province.getCityList());
                } else if (name.contains("概要图")) {
                    gaiyaotuList.addAll(province.getCityList());
                } else {
                    // 直辖市
                    cityList.addAll(province.getCityList());
                }

            }

        }

        // 添加，概要图，直辖市，港口
        OfflineMapProvince gaiyaotu = new OfflineMapProvince();
        gaiyaotu.setProvinceName("全国");
        gaiyaotu.setCityList(gaiyaotuList);

        provinceList.set(0, gaiyaotu);// 使用set替换掉刚开始的null

        OfflineMapProvince zhixiashi = new OfflineMapProvince();
        zhixiashi.setProvinceName("直辖市");
        zhixiashi.setCityList(cityList);
        provinceList.set(1, zhixiashi);


        OfflineMapProvince gaogao = new OfflineMapProvince();
        gaogao.setProvinceName("港澳");
        gaogao.setCityList(gangaoList);
        provinceList.set(2, gaogao);

        OfflineMapProvince shengfen = new OfflineMapProvince();
        shengfen.setProvinceName("省份");
        provinceList.set(3, shengfen);
    }

    /**
     * 把一个省的对象转化为一个市的对象
     */
    public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
        OfflineMapCity aMapCity = new OfflineMapCity();
        aMapCity.setCity(aMapProvince.getProvinceName());
        aMapCity.setSize(aMapProvince.getSize());
        aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
        aMapCity.setState(aMapProvince.getState());
        aMapCity.setUrl(aMapProvince.getUrl());
        return aMapCity;
    }

    /**
     * 初始化已下载列表
     */

    private void initDownloadedList() {
        if (GlobalCfg.IS_POTRAIT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.offline_downloaded_list1_p, null);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.offline_downloaded_list1, null);
        }
        tv_map_contral = (TextView) view.findViewById(R.id.tv_map_contral);
        tv_line = (TextView) view.findViewById(R.id.tv_line);

        tv_map_contral.setOnClickListener(this);
        mLlCityList.setOnClickListener(this);
        mLlDownloadManger.setOnClickListener(this);
        mBackImage.setOnClickListener(this);

        mDownLoadedList = (ListView) view.findViewById(R.id.downloaded_map_list);

        mDownloadedAdapter = new OfflineDownloadedAdapter(mContext, amapManager, false);
        adapter_contral = new OfflineDownloadedAdapter(mContext, amapManager, true);

        mDownLoadedList.setAdapter(mDownloadedAdapter);


//        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                List<OfflineMapCity> cities = adapter_contral.getCities();
//                if (isChecked) {
//                    adapter_contral.getChosePosition().clear();
//                    for (int i = 0; i < cities.size(); i++) {
//                        adapter_contral.getIsSelected().put(i, true);
//                        adapter_contral.getChosePosition().add((Integer) i);
//                    }
//
//                    chosedCities.clear();
//                    chosedCities.addAll(cities);
//
//                } else {
//
//                    for (int i = 0; i < cities.size(); i++) {
//                        adapter_contral.getIsSelected().put(i, false);
//                    }
//                    chosedCities.clear();
//                    adapter_contral.getChosePosition().clear();
//                }
//                adapter_contral.notifyDataSetChanged();
//            }
//        });
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OfflineMapCity> cities = adapter_contral.getCities();
                if (checkbox.isChecked()) {
                    adapter_contral.getChosePosition().clear();
                    for (int i = 0; i < cities.size(); i++) {
                        adapter_contral.getIsSelected().put(i, true);
                        adapter_contral.getChosePosition().add((Integer) i);
                    }

                    chosedCities.clear();
                    chosedCities.addAll(cities);

                } else {

                    for (int i = 0; i < cities.size(); i++) {
                        adapter_contral.getIsSelected().put(i, false);
                    }
                    chosedCities.clear();
                    adapter_contral.getChosePosition().clear();
                }
                adapter_contral.notifyDataSetChanged();
            }
        });

        mListView_downed_contral.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                                                //只有当删除键显示的时候才会有点击事件

                                                                OfflineMapCity offlineCity = adapter_contral.getCities().get(position);
                                                                if (offlineCity.getState() == OfflineMapStatus.UNZIP) {
                                                                    ToastUtil.show(mContext, R.string.download_unPackage_cannot_delete);
                                                                } else {
                                                                    OfflineDownloadedAdapter.ViewHolder holder = (OfflineDownloadedAdapter.ViewHolder) view.getTag();
                                                                    holder.checkBox.toggle();
                                                                    adapter_contral.getIsSelected().put(position, holder.checkBox.isChecked());
                                                                    if (holder.checkBox.isChecked()) {
                                                                        if (!chosedCities.contains(offlineCity)) {
                                                                            chosedCities.add(offlineCity);
                                                                        }
                                                                        if (!adapter_contral.getChosePosition().contains((Integer) position)) {
                                                                            adapter_contral.getChosePosition().add((Integer) position);
                                                                        }

                                                                    } else {
                                                                        if (chosedCities.contains(offlineCity)) {
                                                                            chosedCities.remove(offlineCity);
                                                                        }

                                                                        if (adapter_contral.getChosePosition().contains((Integer) position)) {
                                                                            adapter_contral.getChosePosition().remove((Integer) position);
                                                                        }
                                                                    }
                                                                }

                                                                if (adapter_contral.getChosePosition().size() < adapter_contral.getCities().size()) {
                                                                    checkbox.setChecked(false);
                                                                } else if (adapter_contral.getChosePosition().size() == adapter_contral.getCities().size()) {
                                                                    checkbox.setChecked(true);
                                                                }
                                                                adapter_contral.notifyDataSetChanged();
                                                            }
                                                        }

        );
    }

    /**
     * 暂停所有下载和等待
     */

    private void stopAll() {
        if (amapManager != null) {
            amapManager.stop();
        }
    }

    /**
     * 继续下载所有暂停中
     */
    private void startAllInPause() {
        if (amapManager == null) {
            return;
        }

        ArrayList<OfflineMapCity> ss = amapManager.getDownloadingCityList();
       Trace.Debug(TAG, "startAllInPause: "+ss.size());
        amapManager.restart();

        for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
            if (mapCity.getState() == OfflineMapStatus.PAUSE) {
                try {
                    amapManager.downloadByCityName(mapCity.getCity());
                } catch (AMapException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 取消所有<br>
     * 即：删除下载列表中除了已完成的所有<br>
     * 会在OfflineMapDownloadListener.onRemove接口中回调是否取消（删除）成功
     */
    private void cancelAll() {
        if (amapManager == null) {
            return;
        }
        for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
            if (mapCity.getState() == OfflineMapStatus.PAUSE) {
                amapManager.remove(mapCity.getCity());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (amapManager != null) {
//            amapManager.destroy();
//        }
    }


    private void logList() {
        ArrayList<OfflineMapCity> list = amapManager.getDownloadingCityList();

        for (OfflineMapCity offlineMapCity : list) {
           Trace.Debug("amap-city-loading: ", offlineMapCity.getCity() + ","
                    + offlineMapCity.getState());
        }

        ArrayList<OfflineMapCity> list1 = amapManager
                .getDownloadOfflineMapCityList();

        for (OfflineMapCity offlineMapCity : list1) {
           Trace.Debug("amap-city-loaded: ", offlineMapCity.getCity() + ","
                    + offlineMapCity.getState());
        }
    }


    /**
     * 离线地图下载回调方法
     */
    @Override
    public void onDownload(int status, int completeCode, String downName) {

       Trace.Debug(TAG, "onDownload: "+"status"+status+";completeCode"+completeCode+";downName"+downName);
        
        switch (status) {
            case OfflineMapStatus.SUCCESS:
                // changeOfflineMapTitle(OfflineMapStatus.SUCCESS, downName);
                break;
            case OfflineMapStatus.LOADING:
               Trace.Debug("amap-download", "download: " + completeCode + "%" + ","
                        + downName);
                // changeOfflineMapTitle(OfflineMapStatus.LOADING, downName);
                break;
            case OfflineMapStatus.UNZIP:
               Trace.Debug("amap-unzip", "unzip: " + completeCode + "%" + "," + downName);
                // changeOfflineMapTitle(OfflineMapStatus.UNZIP);
                // changeOfflineMapTitle(OfflineMapStatus.UNZIP, downName);
                break;
            case OfflineMapStatus.WAITING:
               Trace.Debug("amap-waiting", "WAITING: " + completeCode + "%" + ","
                        + downName);
                break;
            case OfflineMapStatus.PAUSE:
               Trace.Debug("amap-pause", "pause: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.STOP:
                break;
            case OfflineMapStatus.ERROR:
               Trace.Debug("amap-download", "download: " + " ERROR " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_AMAP:
               Trace.Debug("amap-download", "download: " + " EXCEPTION_AMAP " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
               Trace.Debug("amap-download", "download: " + " EXCEPTION_NETWORK_LOADING "
                        + downName);
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT)
                        .show();
                amapManager.pause();
                break;
            case OfflineMapStatus.EXCEPTION_SDCARD:
                Trace.Error("amap-download", "download: " + " EXCEPTION_SDCARD "
                        + downName);
                break;
            default:
                break;
        }

        // changeOfflineMapTitle(status, downName);
        handler.sendEmptyMessage(UPDATE_LIST);

    }


    @Override
    public void onCheckUpdate(boolean hasNew, String name) {
        // TODO Auto-generated method stub
       Trace.Debug("amap-demo", "onCheckUpdate " + name + " : " + hasNew);
        Message message = new Message();
        message.what = SHOW_MSG;
        message.obj = "CheckUpdate " + name + " : " + hasNew;
        handler.sendMessage(message);
    }


    @Override
    public void onRemove(boolean success, String name, String describe) {
        // TODO Auto-generated method stub
       Trace.Debug("amap-demo", "onRemove " + name + " : " + success + " , "
                + describe);
        handler.sendEmptyMessage(UPDATE_LIST);

        Message message = new Message();
        message.what = SHOW_MSG;
        if (success) {
            message.obj = "删除" + name + "地图成功";
        } else {
            message.obj = "删除" + name + "地图失败";
        }
        handler.sendMessage(message);

    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*if(!((HomeActivity) mContext).isPopupWindowShow) {
                    getFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingNaviFragment()).commitAllowingStateLoss();
                }*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_city_list:
                mContentViewPage.setCurrentItem(0);
                mDownloadText.setTextColor(mContext.getResources().getColor(R.color.white));
                mDownloadText.setTextSize(20);
                mDownloadedText.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                mDownloadedText.setTextSize(16);
                mDownloadedText.setBackgroundResource(R.drawable.transeparent);
                mDownloadText.setBackgroundResource(R.drawable.radiobutton_bg);
                TextPaint tp = mDownloadText.getPaint();
                tp.setFakeBoldText(true);

                notifyDataChange();

                break;
            case R.id.ll_download_manager:
                mContentViewPage.setCurrentItem(1);
                mDownloadText.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                mDownloadText.setTextSize(16);
                mDownloadedText.setTextColor(mContext.getResources().getColor(R.color.white));
                mDownloadedText.setTextSize(20);
                mDownloadText.setBackgroundResource(R.drawable.transeparent);
                mDownloadedText.setBackgroundResource(R.drawable.radiobutton_bg);
                TextPaint tp2 = mDownloadedText.getPaint();
                tp2.setFakeBoldText(true);
                doCancle();

                break;

            case R.id.back_image_view:
                getFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingNaviFragment()).commitAllowingStateLoss();


                break;

            case R.id.tv_map_contral:

                ll_map.setVisibility(View.GONE);
                include_ll.setVisibility(View.VISIBLE);


                break;

            case R.id.tv_cancle:

                ll_map.setVisibility(View.VISIBLE);
                include_ll.setVisibility(View.GONE);

                break;

            case R.id.tv_delete:
                if (chosedCities.size()>0){

                NetworkConfirmDialog networkConfirmDialog=new NetworkConfirmDialog(mContext,R.string.delete_map,R.string.ok,R.string.cancel);
                networkConfirmDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                    @Override
                    public void onConfirm(boolean checked) {
                        boolean deleteAll = false;
                        if (chosedCities.size()==adapter_contral.getCities().size()){
                            deleteAll=true;

                        }
                        StringBuffer buffer = new StringBuffer();
                        for (int i = 0; i < chosedCities.size(); i++) {
                            buffer.append(chosedCities.get(i).getCity() + ",");

                            if (chosedCities.get(i).getState() != OfflineMapStatus.UNZIP) {
                                amapManager.remove(chosedCities.get(i).getCity());
                            } else {
                                ToastUtil.show(mContext, R.string.download_unPackage_cannot_delete);
                            }
                        }

                        chosedCities.clear();
                        adapter_contral.getChosePosition().clear();
                        adapter_contral.notifyDataChange();
                        checkbox.setChecked(false);
                        if (deleteAll){
                            checkbox.setChecked(false);
                            ll_map.setVisibility(View.VISIBLE);
                            include_ll.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancel() {


                    }
                });
                networkConfirmDialog.setCancelable(false);
                networkConfirmDialog.show();
                }

                break;


        }


    }

    private void doCancle() {
        tv_map_contral.setVisibility(View.VISIBLE);//批量管理隐藏
        //把选择的全部设置为false
        for (int i = 0; i < mDownloadedAdapter.getCities().size(); i++) {
            mDownloadedAdapter.getIsSelected().put(i, false);
        }
        //选中的清空
        mDownloadedAdapter.getChosePosition().clear();
        checkbox.setChecked(false);
        mDownloadedAdapter.notifyDataSetChanged();
    }

    private void notifyDataChange() {

        mDownloadedAdapter.notifyDataSetChanged();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mDownloadText.setTextColor(mContext.getResources().getColor(R.color.white));
                TextPaint tp = mDownloadText.getPaint();
                tp.setFakeBoldText(true);
                mDownloadText.setTextSize(20);
                mDownloadedText.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                mDownloadedText.setTextSize(16);
                mDownloadedText.setBackgroundResource(R.drawable.transeparent);
                mDownloadText.setBackgroundResource(R.drawable.radiobutton_bg);
                break;
            case 1:

                mDownloadText.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
                mDownloadedText.setTextColor(mContext.getResources().getColor(R.color.white));
                TextPaint tp2 = mDownloadedText.getPaint();
                tp2.setFakeBoldText(true);
                mDownloadText.setTextSize(16);
                mDownloadedText.setTextSize(20);
                mDownloadText.setBackgroundResource(R.drawable.transeparent);
                mDownloadedText.setBackgroundResource(R.drawable.radiobutton_bg);
                doCancle();

                break;
        }
        handler.sendEmptyMessage(UPDATE_LIST);


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
