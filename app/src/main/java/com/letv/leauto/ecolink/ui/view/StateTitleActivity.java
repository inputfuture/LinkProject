package com.letv.leauto.ecolink.ui.view;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.EnvStatus;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.WeatherInfo;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.manager.LocationManager;
import com.letv.leauto.ecolink.manager.WeatherIconManager;
import com.letv.leauto.ecolink.service.LeBluetoothService;
import com.letv.leauto.ecolink.thincar.protocol.DeviceInfoNotifyHelp;
import com.letv.leauto.ecolink.utils.BluetoothManager;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.SpUtils;
import com.letv.leauto.ecolink.utils.TimeUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.leauto.favorcar.exInterface.LocationInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class StateTitleActivity extends NotifyAppStateActivity implements ServiceConnection {

    @Bind(R.id.ll_title)
    public LinearLayout ll_title;
    @Bind(R.id.tv_detail_time)
    TextView tv_detail_time;
    @Bind(R.id.tv_temp)
    TextView tv_temp;
    @Bind(R.id.tv_city)
    TextView tv_city;
    @Bind(R.id.iv_weather)
    ImageView iv_weather;
    @Bind(R.id.iv_bluetooth)
    ImageView iv_bluetooth;
    @Bind(R.id.iv_power)
    ImageView iv_power;
    @Bind(R.id.iv_xinhao)
    ImageView iv_xinhao;
    @Bind(R.id.iv_wifi)
    ImageView iv_wifi;
    @Bind(R.id.rest_time)
    TextView tv_time;

    protected String quality;
    //    private Timer timer;
    protected static final int UPDATE_TIME = 0x188;
    private  static  final  int UPDATE_LOCATION=0X19;
    private boolean isBinded = false;
    private LeBluetoothService.BluetoothBinder mLeBluetoothService;
    WeatherInfo weatherInfo;
    String restriction;
    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessageTypeCfg.MSG_INIT_LOCATION:
                    AMapLocation aMapLocation = (AMapLocation) msg.obj;
                    setCurrentLocationInfo(aMapLocation);
                    LatLonPoint poiCenter = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    EnvStatus.savePoi(poiCenter);
                    DataUtil.getInstance().getWeatherInfo(aMapLocation.getProvince() + aMapLocation.getCity(), mHandler);
                    if (!GlobalCfg.IS_ELECTRIC_CAR) {
                        DataUtil.count=0;
                        DataUtil.getInstance().getTrafficControls(aMapLocation.getCity(), mHandler);
                    }
                    break;
                case MessageTypeCfg.MSG_GET_WEATHER:
                    weatherInfo = (WeatherInfo) msg.obj;
                    CacheUtils.getInstance(mContext).putString(Constant.SUNRISE,weatherInfo.sunrise);
                    CacheUtils.getInstance(mContext).putString(Constant.SUNSET,weatherInfo.sunset);

                    if (weatherInfo!=null){
                        initWeather(weatherInfo);
                    }
                case MessageTypeCfg.MSG_GET_WEATHER_FAIL:

                    break;
                case MessageTypeCfg.MSG_GET_TRAFFIC:
                    restriction = (String) msg.obj;
                    Trace.Info("TAG", "handleMessage: " + restriction);
                    Trace.Info("TAG", "handleMessage: " + quality);
                    DeviceInfoNotifyHelp.getInstance().notifyLimitedNumber(restriction);
                    if (restriction.equals(R.string.str_unlimit_city)){
                        if (weatherInfo!=null){
                            initRoadState(weatherInfo.pm25+" "+weatherInfo.quality);
                        }
                    }else if(restriction.equals(R.string.str_unlimit_today)){
                        initRoadState(restriction);
                    }else {
                        initRoadState(mContext.getString(R.string.str_limit_today)+restriction);
                    }

                    break;
                case MessageTypeCfg.MSG_GET_TRAFFIC_FAIL:
                    tv_time.setText("无法获取数据");
                    break;
                case 0:
                    iv_wifi.setImageResource(R.mipmap.wifi4);

                    break;
                case 1:
                    iv_wifi.setImageResource(R.mipmap.wifi3);

                    break;
                case 2:
                    iv_wifi.setImageResource(R.mipmap.wifi2);

                    break;
                case 3:
                    iv_wifi.setImageResource(R.mipmap.wifi1);

                    break;
                case 4:
                    iv_wifi.setImageResource(R.mipmap.wifi0);
                    break;
                case UPDATE_LOCATION:
                    mHandler.removeMessages(UPDATE_LOCATION);
                    LocationManager.getInstance().initLocationTwo(mHandler);
                    mHandler.sendEmptyMessageDelayed(UPDATE_LOCATION,20*1000*60);
                    break;
                default:
                    hadleMessages(msg);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        mHandler.sendEmptyMessageDelayed(UPDATE_LOCATION,1000);
        initTime();
//        getCachedWeather();
//        initRoadState();
        //TODO 开启监听蓝牙的服务
//        Intent intent = new Intent(this, LeBluetoothService.class);
//        if (isBinded == false) {
//            this.bindService(intent, this, Context.BIND_AUTO_CREATE);
//        }
        //监听蓝牙，电量
        mContext.registerReceiver(mReceiver, makeFilter());
    }
    String roadstate;

    private void initRoadState(String argState) {
        tv_time.setText(argState);

    }
    @Override
    protected void onResume() {
        super.onResume();
        //检查蓝牙的链接状态
        if (BluetoothManager.isBluetoothEnabled()) {
            iv_bluetooth.setVisibility(View.VISIBLE);
        } else {
            iv_bluetooth.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            // unbindService(this);
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception e) {
        }

        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
        super.onDestroy();
    }

    protected abstract void initView();

    @Override
    public void onClick(View v) {

    }

    /**
     * 初始化时间
     */
    protected void initTime() {
        if(tv_detail_time != null) {
            tv_detail_time.setText(TimeUtils.getHourMin());
        }
        mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 30 * 1000);
    }

//    protected void getCachedWeather() {
//        String weather = CacheUtils.getInstance(EcoApplication.getInstance()).getString(Constant.WEATHER_INFO, null);
//        if (weather != null) {
//            WeatherInfo info = WeatherInfoParse.parseWeatherInfo(weather);
//            initWeather(info);
//        }
//    }

    /**
     * 初始化天气
     *
     * @param info
     */
    protected void initWeather(WeatherInfo info) {
        if (info.weather!=null){
            iv_weather.setBackgroundResource(WeatherIconManager.getInstance().getWeatherIcon(info.weather));
        }
        iv_weather.setVisibility(View.VISIBLE);
        if (info.temp!=null){
            tv_temp.setText(info.temp);
            DeviceInfoNotifyHelp.getInstance().notifyWeather(info.temp);
        }
        if (info.city!=null){
            tv_city.setText(info.city);
            SpUtils.putString(mContext,"location",info.city);
        }

    }

    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    //监听蓝牙是否开启的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        iv_bluetooth.setVisibility(View.VISIBLE);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        iv_bluetooth.setVisibility(View.GONE);
                        break;
                }
            } else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                Trace.Debug("##### level="+level+"  scale="+scale);
                int health = intent.getIntExtra("health", -1);
                int status = intent.getIntExtra("status", -1);

                int curPower =-1;
                if (level >= 0 && scale > 0) {
                    curPower = ((level * 100) / scale)/25;
                }
                curPower=curPower+1;
                DeviceInfoNotifyHelp.getInstance().notifyPhoneBattery(level * 100 / scale);

                switch (curPower) {
                    case 0:
                        iv_power.setImageResource(R.mipmap.battery_charging);
                        break;
                    case 1:
                        iv_power.setImageResource(R.mipmap.battery1);
                        break;
                    case 2:
                        iv_power.setImageResource(R.mipmap.battery2);
                        break;
                    case 3:
                        iv_power.setImageResource(R.mipmap.battery3);
                        break;
                    case 4:
                        iv_power.setImageResource(R.mipmap.battery4);
                    case 5:
                        iv_power.setImageResource(R.mipmap.battery4);
                        break;
                }
                DeviceInfoNotifyHelp.getInstance().notifyPhoneBattery(level * 100 / scale);


            } else if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                //signal strength changed
            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
               Trace.Error("====", "1");
                System.out.println("网络状态改变");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    iv_wifi.setVisibility(View.GONE);

                    System.out.println("wifi网络连接断开");
                   Trace.Error("====", "2");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    iv_wifi.setVisibility(View.VISIBLE);
                   Trace.Error("====", "3");
                    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                    // 使用定时器,每隔5秒获得一次信号强度值


                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (mHandler == null||wifiInfo==null) {
                        return;
                    }
                    //获得信号强度值
                    int level = wifiInfo.getRssi();
                    //根据获得的信号强度发送信息
                    if (level <= 0 && level >= -50) {
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    } else if (level < -50 && level >= -70) {
                        Message msg = new Message();
                        msg.what = 2;
                        mHandler.sendMessage(msg);
                    } else if (level < -70 && level >= -80) {
                        Message msg = new Message();
                        msg.what = 3;
                        mHandler.sendMessage(msg);
                    } else if (level < -80 && level >= -100) {
                        Message msg = new Message();
                        msg.what = 4;
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = 5;
                        mHandler.sendMessage(msg);
                    }


                }

            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi设置打开与否，不一定连接上
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
               Trace.Error("====", "4");
                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    System.out.println("系统关闭wifi");
                   Trace.Error("====", "5");
                } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                    System.out.println("系统开启wifi");
                   Trace.Error("====", "6");
                }
            }
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Trace.Debug("mark", "网络状态已经改变");
                connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if(info != null && info.isAvailable()) {
                    LocationManager.getInstance().initLocationTwo(mHandler);
//                    String name = info.getTypeName();
//                    Trace.Debug("mark", "当前网络名称：" + name);
                } else {
                    mHandler.sendEmptyMessage(MessageTypeCfg.MSG_GET_TRAFFIC_FAIL);
//                    Trace.Debug("mark", "没有可用网络");
                }
            }

        }
    };

    protected void hadleMessages(Message msg) {
    }

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return filter;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mLeBluetoothService = ((LeBluetoothService.BluetoothBinder) service);
        isBinded = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLeBluetoothService = null;
        isBinded = false;
    }

    public void setCurrentLocationInfo(AMapLocation curMapLocation) {
    }
}
