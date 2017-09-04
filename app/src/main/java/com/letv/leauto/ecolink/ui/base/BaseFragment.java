package com.letv.leauto.ecolink.ui.base;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.csr.gaia.library.Gaia;
import com.csr.gaia.library.GaiaError;
import com.csr.gaia.library.GaiaLink;
import com.csr.gaia.library.GaiaPacket;
import com.letv.auto.keypad.service.KeyEventManager;
import com.leauto.link.lightcar.ThinCarDefine;
import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.controller.EcoTTSController;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.leplayer.LePlayer;
import com.letv.leauto.ecolink.receiver.BroadcastReceiverExtended;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.thincar.ThincarGestureProcessor;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.ui.fragment.SettingFragment;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.NetUtils;
import com.letv.leauto.ecolink.utils.Trace;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.letv.leauto.ecolink.utils.ContextProvider.getApplicationContext;

public abstract class BaseFragment extends Fragment implements BroadcastReceiverExtended.BroadcastReceiverListener {

    private final static String TAG = "BaseFragment";
    protected Context mContext;
    public View rootView;
    protected EcoTTSController ttsHandlerController;
    protected LePlayer lePlayer;
    public Boolean isNetConnect = false;
    public static boolean isAlum = true;
    //添加轻车机相关功能参数,begin
    private KeyEventManager mKeyEventManager;
    private BroadcastReceiverExtended mBroadcastReceiver;
    public GaiaLink mGaiaLink;
    public BluetoothAdapter mBtAdapter;
    private BluetoothDevice mDevice;
    private GaiaLink.Transport mTransport;
    private int nbAttemptConnection = 0;
    private static final int NB_ATTEMPTS_CONNECTION_MAX = 2;
    private BluetoothDevice[] mListDevices;
    public byte[] data;
    ComponentName bluetoothReceiver;
    public AudioManager audioManager;
    public boolean mIsCurrentConn = false;
    public boolean mCurrentActivityFlag = true;
    private boolean mScanDeviceFlag = false;
    protected int mScreenWidth;
    protected double mPhoneCarRate;
    protected  int mScreenHeight;
    protected ThincarGestureProcessor mThincarGestureProcessor;
    public NetworkConfirmDialog mNoNetDialog;
    KeypadStateReceiver mKeypadStateReceiver;

    //添加轻车机相关功能参数,end
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
//        LetvReportUtils.recordActivityStart(this.getClass().getSimpleName());
    }


   public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        mScreenWidth = DensityUtils.getScreenWidth(mContext);
        mScreenHeight = DensityUtils.getScreenHeight(mContext);
        mPhoneCarRate = (double) mScreenWidth / (double) ThinCarDefine.FULL_CAR_WIDTH;
        ttsHandlerController = EcoApplication.LeGlob.getTtsController();
        isNetConnect = NetUtils.isConnected(mContext);
        lePlayer = EcoApplication.LeGlob.getPlayer();
        lePlayer.openServiceIfNeed();
        mBtAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        mBroadcastReceiver = new BroadcastReceiverExtended(this);
        audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        getCache();
        mKeypadStateReceiver=new KeypadStateReceiver(this);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = initView(inflater);
        Trace.Debug("####onCreateView");
        return rootView;
    }

    public View getRootView() {
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        initReceiver(this.mContext);
        mCurrentActivityFlag = true;
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.mContext.registerReceiver(mBroadcastReceiver, filter);
        if (null == mGaiaLink) {
            mGaiaLink = GaiaLink.getInstance();
        }
        mGaiaLink.setReceiveHandler(this.getGaiaHandler());
        if (!mGaiaLink.isConnected()) {
            startDev();
        } else {
            getInformation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            mBroadcastReceiver.destory();
            mBroadcastReceiver=null;

        }
        if (mKeypadStateReceiver != null) {
            mKeypadStateReceiver=null;
        }
        if (null != mGaiaLink) {
            mGaiaLink.setReceiveHandler(null);
            mGaiaLink = null;
        }
        if (handler!=null){
            handler.removeCallbacksAndMessages(this);
            handler=null;
        }
        Trace.Debug("onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurrentActivityFlag = false;
        this.mContext.unregisterReceiver(mBroadcastReceiver);
        this.mContext.unregisterReceiver(mKeypadStateReceiver);
        if (mGaiaLink.isConnected()) {
//            mGaiaLink.cancelNotification(Gaia.EventId.CHARGER_CONNECTION);
//            mGaiaLink = null;
            Trace.Debug("cancelNotification");
        }
    }

    protected abstract View initView(LayoutInflater inflater);

    protected abstract void initData(Bundle savedInstanceState);

    public void handlePacket(Message msg) {
        GaiaPacket packet = (GaiaPacket) msg.obj;
        Gaia.Status status = packet.getStatus();
        switch (packet.getCommand()) {
            case Gaia.COMMAND_GET_FM_FREQ_CONTROL:
                receivePacketGetFMFREQControl(packet);
                break;
            case Gaia.COMMAND_EVENT_NOTIFICATION:
                handleNotification(packet);
                break;
            case Gaia.COMMAND_VM_UPGRADE_CONNECT:
                if (!status.equals(Gaia.Status.NOT_SUPPORTED)) {
                    receivePacketUpdate();
                }
                break;
            default:
                break;
        }

    }

    public void receivePacketUpdate() {
    }

    public void receivePacketGetFMFREQControl(GaiaPacket packet) {
        data = packet.getPayload();
    }

    public void handleNotification(GaiaPacket packet) {
        Gaia.EventId event = packet.getEvent();
        Trace.Debug("#####event:" + event);
        switch (event) {
            case CHARGER_CONNECTION:
                //此处处理接收到的key
                //   Toast.makeText(BaseActivity.this, "检测到KEY值：" + packet.getPayload()[1], Toast.LENGTH_SHORT).show();
                int eventNum = packet.getPayload()[1];
                notificationEvent(eventNum);
                break;

            default:
                break;
        }
    }

    protected void notificationEvent(int keyCode) {

        Trace.Debug("notificationEvent->keyCode:" + keyCode);
        ArrayList<MediaDetail> playerList = lePlayer.getPlayerList();
        switch (keyCode) {
            case Constant.KEYCODE_DPAD_LEFT:
//                if (playerList != null && playerList.size() > 0) {
//                    lePlayer.playPrev();
//                }
                break;
            case Constant.KEYCODE_DPAD_RIGHT:
//                if (playerList != null && playerList.size() > 0) {
//                    lePlayer.playNext(false);
//                }
                break;
            case Constant.KEYCODE_PAUSE_PALY:
                Trace.Debug("notificationEvent->keyCode:" + "KEYCODE_PAUSE_PALY");
//                if (playerList != null && playerList.size() > 0) {
//                    if (null != lePlayer.getCurrentStatus()) {
//                        if (lePlayer.getCurrentStatus().isPlaying == true) {
//                            lePlayer.stopPlay();
//                        } else {
//                            lePlayer.startPlay();
//                        }
//                    } else {
//
//                    }
//                }
                break;
            case Constant.KEYCODE_DPAD_BACK:
                Trace.Debug("notificationEvent->keyCode:" + "KEYCODE_DPAD_BACK");
                getActivity().onBackPressed();
                if(((HomeActivity)getActivity()).isPopupWindowShow){
                    ((HomeActivity)getActivity()).PopWindowDismiss();
                }
                break;
            case Constant.KEYCODE_VOICE:
                //shimeng add for network is disconnected when enter to VoiceRecognitionActivity,20160329,begin
                Trace.Debug("notificationEvent->mContext:" + mContext);
                ((HomeActivity)getActivity()).startVoiceSearch();
                break;
            case Constant.KEYCODE_DPAD_LEFT_LONG:
                if (null != lePlayer && null != lePlayer.getCurrentStatus()) {
                    lePlayer.forwordOrRewind(false);
                }
                break;
            case Constant.KEYCODE_DPAD_RIGHT_LONG:
                if (null != lePlayer && null != lePlayer.getCurrentStatus()) {
                    lePlayer.forwordOrRewind(true);
                }
                break;
            case Constant.KEYCODE_DPAD_UP_LONG:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                break;
            case Constant.KEYCODE_DPAD_DOWN_LONG:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                break;
            case Constant.KEYCODE_DPAD_UP:
//                new Thread() {
//                    public void run() {
//                        try {
//                            Instrumentation inst = new Instrumentation();
//                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
//                        } catch (Exception e) {
//                            Trace.Debug("notificationEvent->onKeyUp:"+e.toString());
//                        }
//                    }
//                }.start();
                break;
            case Constant.KEYCODE_DPAD_DOWN:
//                new Thread() {
//                    public void run() {
//                        try {
//                            Instrumentation inst = new Instrumentation();
//                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
//                        } catch (Exception e) {
//                            Trace.Debug("notificationEvent->onKeyDown:"+ e.toString());
//                        }
//                    }
//                }.start();
                break;
            case Constant.KEYCODE_DPAD_CENTER:

                break;
            default:
                break;
        }
    }

//    //添加轻车机乐车盒相关功能
//    private Runnable mRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (getKeypadState() != BluetoothProfile.STATE_CONNECTED) {
//                mKeyEventManager.disconnect(mDevice);
//            }
//        }
//    };

    private int getKeypadState() {
        if (mKeyEventManager == null)
            mKeyEventManager = KeyEventManager.getKeyEventManager(getApplicationContext());
        if (mKeyEventManager != null) {
            return mKeyEventManager.getConnectionState();
        }
        return -1;
    }
    public static class KeypadStateReceiver extends BroadcastReceiver{
        WeakReference<BaseFragment> reference;

        public KeypadStateReceiver(BaseFragment baseFragment) {
            reference=new WeakReference<BaseFragment>(baseFragment);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            if (action.equals(KeyEventManager.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = bundle.getInt(BluetoothProfile.EXTRA_STATE);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                     reference.get().mIsCurrentConn= true;
                } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    reference.get().mIsCurrentConn = false;
                }
//                ttsHandlerController.startSpeek(new String[]{"乐键已连接"});
            } else if (action.equals(KeyEventManager.ACTION_SCAN_FOUND)) {
                if (! reference.get().mCurrentActivityFlag ||  reference.get().mKeyEventManager == null) return;
                BluetoothDevice mDevice = (BluetoothDevice) bundle.getParcelable(BluetoothDevice.EXTRA_DEVICE);
                if (!TextUtils.isEmpty( reference.get().mKeyEventManager.getAutoConnectDeviceAddress())) {
                    if ( reference.get().mKeyEventManager.getAutoConnectDeviceAddress().equals(mDevice.getAddress())) {
                        reference.get().mScanDeviceFlag = true;
                        reference.get().mKeyEventManager.scanDevice(false);
                        reference.get().mKeyEventManager.connect(mDevice);
                    }
                }
            } else if (action.equals(KeyEventManager.ACTION_SCAN_COMPLETE)) {
                if (! reference.get().mCurrentActivityFlag) return;
                if ( reference.get().mScanDeviceFlag ||  reference.get().getKeypadState() == BluetoothProfile.STATE_CONNECTED) {
                    reference.get().mScanDeviceFlag = false;
                    reference.get(). mIsCurrentConn = false;
                } else {
                    if ( reference.get().mIsCurrentConn) {
                        reference.get().mIsCurrentConn = false;
                    }
                }
            }
        }
    };

    private void initReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(KeyEventManager.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(KeyEventManager.ACTION_SCAN_COMPLETE);
        filter.addAction(KeyEventManager.ACTION_SCAN_FOUND);
        if (mKeypadStateReceiver!=null){
            context.registerReceiver(mKeypadStateReceiver, filter);}
        Trace.Debug( "initReceiver");

    }

    public void startDev() {
        updateListDevices();
    }

    public void getInformation() {
        mGaiaLink.registerNotification(Gaia.EventId.CHARGER_CONNECTION);
        Trace.Debug("mGaiaLink " + "getInformation");
    }

    private String devAddress;
    public CacheUtils cacheUtils;

    public CacheUtils getCache() {
        if (this.cacheUtils == null) {
            this.cacheUtils = cacheUtils.getInstance(mContext);
        }
        return this.cacheUtils;
    }

    public void updateListDevices() {
        if (mBtAdapter != null && mBtAdapter.isEnabled()) {

            mListDevices = mBtAdapter.getBondedDevices().toArray(new BluetoothDevice[mBtAdapter.getBondedDevices().size()]);
            if (mListDevices.length > 0) {
                for (int i = 0; i < mListDevices.length; i++) {
                    String devName = mListDevices[i].getName();
                    Trace.Debug("devName"+ devName);
                    if ((!TextUtils.isEmpty(devName)&&devName.contains("Autokit") )|| (!TextUtils.isEmpty(devName)&&devName.contains("AutoKit"))) {
                        connect(GaiaLink.Transport.BT_GAIA, mListDevices[i]);
                        devAddress = mListDevices[i].getAddress().replace(":", "");
                        cacheUtils.putString("devAddress", devAddress);
                        Trace.Debug("devAddress"+ devAddress);
                    } else {
                        Trace.Debug( "mListDevices------null1");
                    }
                }
            } else {
//                Toast.makeText(mContext, "未与车盒配对", Toast.LENGTH_LONG).show();
            }
        } else {
            Trace.Debug( "mListDevices------null2");
        }
    }

    private void onConnect() {
        Trace.Debug("mListDevices------onConnect");
        if (null == mGaiaLink) {
            mGaiaLink = GaiaLink.getInstance();
        }
        getInformation();
        //ttsHandlerController.startSpeek(new String[]{"乐车盒已连接"});
    }

    public void connect(GaiaLink.Transport transport, BluetoothDevice device) {
        mDevice = device;
        mTransport = transport;

        Trace.Debug("#####connect="+ mGaiaLink.isConnected() + "");

        if (mGaiaLink.isConnected()) {
//            mWaitingForConnection = true;
//            disconnectDevice();
        } else {
            connectDevice();
        }
    }

    public void disconnectDevice() {
        mGaiaLink.disconnect();
    }

    public void connectDevice() {
        mGaiaLink.connect(mDevice, mTransport);
    }

    private static final int REQUEST_ENABLE_BT = 1;

    public void checkEnableBt() {
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            onBluetoothEnabled();
        }
    }


    public  class GaiaHandler extends Handler {

        final WeakReference<Context> mActivity;

        public GaiaHandler(Context activity) {
            super();
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage( Message msg) {
            Context parentActivity = mActivity.get();
            switch (GaiaLink.Message.valueOf(msg.what)) {
                case PACKET:
                    if(!GlobalCfg.isThincarConnect){
                        GlobalCfg.isThincarConnect = true;
                    }
                    handlePacket(msg);
                    break;
                case CONNECTED:
                    GlobalCfg.isThincarConnect = true;
                    Intent connIntent = new Intent(SettingFragment.ACTION_THINCAR_CONNECTED);
                    LocalBroadcastManager.getInstance(parentActivity).sendBroadcast(connIntent);
                    onConnect();
                    break;
                case DISCONNECTED:
                    Intent unconnIntent = new Intent(SettingFragment.ACTION_THINCAR_DISCONNECTED);
                    LocalBroadcastManager.getInstance(parentActivity).sendBroadcast(unconnIntent);
                    GlobalCfg.isThincarConnect = false;
                    connectDevice();
                    break;
                case ERROR:
                    GaiaError error = (GaiaError) msg.obj;
                    handleError(error);
                    break;
                case STREAM:
                    break;
                default:
                    break;
            }
        }
    }

    protected Handler getGaiaHandler() {
        return new GaiaHandler(this.mContext);
    }


    public void onBluetoothEnabled() {
        updateListDevices();
    }

    public void onBluetoothDisabled() {
        checkEnableBt();
    }


    private void handleError(GaiaError error) {

        Trace.Debug("handleError=" + error.getType() + "");

        switch (error.getType()) {
            case SENDING_FAILED:
                String message;
                if (error.getCommand() > 0) {
                    message = "Send command " + error.getCommand() + " failed";

                } else {
                    message = "Send command failed";
                }
                Toast.makeText(mContext, "车盒未连接,请重试", Toast.LENGTH_SHORT).show();
            case UNSUPPORTED_TRANSPORT:
                break;
            case ILLEGAL_ARGUMENT:
                if (mDevice == null) {
                } else if (mTransport == null) {
                } else {
                }
                break;
            case DEVICE_UNKNOWN_ADDRESS:
                break;
            case CONNECTION_FAILED:
                break;
            case ALREADY_CONNECTED:
                if (nbAttemptConnection < NB_ATTEMPTS_CONNECTION_MAX) {
                    nbAttemptConnection++;
                    this.disconnectDevice();
                } else {
                    nbAttemptConnection = 0;
                }
                break;
            case BLUETOOTH_NOT_SUPPORTED:
                break;
        }
    }


    protected void showNoNetDialog(){
        Trace.Debug("****** 无网络弹窗");
        if (mNoNetDialog==null){
            mNoNetDialog=new NetworkConfirmDialog(mContext, R.string.no_net_message,R.string.ok, R.string.i_know);
            mNoNetDialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                @Override
                public void onConfirm(boolean checked) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                    mNoNetDialog=null;
                }

                @Override
                public void onCancel() {
                    mNoNetDialog=null;


                }
            });
            mNoNetDialog.setCancelable(false);
            mNoNetDialog.show();}else{
            if (!mNoNetDialog.isShowing()){
                mNoNetDialog.show();
            }
        }
    }

    public EcoApplication getEcoApplication(){
        Activity activity = getActivity();
        if(activity!=null){
            return (EcoApplication) activity.getApplication();
        }
        return null;
    }
}
