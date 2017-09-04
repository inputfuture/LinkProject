package com.letv.auto.keypad.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;


import com.letv.auto.keypad.util.LetvLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ZhangHaoyi on 15-4-8.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class KeypadScanner {

    static final private String TAG = "KeypadScanner";

    static final private boolean DBG = LetvLog.DEBUG;

    static final private long DEFAULT_SCAN_TIMEOUT = 4500;

    static final private long MAX_SCAN_TIMEOUT = 30000;

    private BluetoothAdapter mBluetoothAdapter;

    private Context mContext;

    private Set<BluetoothDevice> mScannedDevices = new HashSet<BluetoothDevice>();

    private Handler mHandler;

    private LeScanner mLeScanner;

    private List<LeScanner> mScannerList = new ArrayList<LeScanner>();

    private Runnable mDeferStopScanRunnable = null;

    public interface ScannerCallback {
        boolean onHandle(int status, BluetoothDevice device);
    }

    private void broadcastScanResult(int status, BluetoothDevice device) {
        Intent intent = new Intent();
        if (status == KeyEventManager.SCAN_FOUND) {
            intent.setAction(KeyEventManager.ACTION_SCAN_FOUND);
            intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        } else {
            intent.setAction(KeyEventManager.ACTION_SCAN_COMPLETE);
        }
        mContext.sendBroadcast(intent);
    }

    private interface LeScanner {
        boolean startScan();
        boolean stopScan();
    }

    class NormalScanner implements LeScanner {
        private IntentFilter mFilter;

        final private BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (isMatch(device)) {
                        LetvLog.d(TAG, "Found Valid Device BY NormalScanner");                        
                        broadcastScanResult(KeyEventManager.SCAN_FOUND, device);
                        mScannedDevices.add(device);
                    }
                }
            }
        };

        NormalScanner() {
            mFilter = new IntentFilter();
            mFilter.addAction(BluetoothDevice.ACTION_FOUND);
            mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        }

        @Override
        public boolean startScan() {
            LetvLog.d(TAG,"into startScan BY NormalScanner");
            mContext.registerReceiver(mReceiver, mFilter);
            if (mBluetoothAdapter != null && mBluetoothAdapter.startDiscovery()) {
                return true;
            }
            return false;
        }

        @Override
        public boolean stopScan() {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
            }
            mContext.unregisterReceiver(mReceiver);
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class LeScanLater21 implements LeScanner {

        private BluetoothLeScanner mScanner;

        private final ScanCallback mLeScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                if (isMatch(device)) {
                    LetvLog.d(TAG, "Found Valid Device BY LeScanLater21");                   
                    broadcastScanResult(KeyEventManager.SCAN_FOUND, device);
                    mScannedDevices.add(device);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                for (ScanResult result : results) {
                    this.onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, result);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {                
                LetvLog.e(TAG, "SCAN ERROR:" + errorCode);
                broadcastScanResult(KeyEventManager.SCAN_COMPLETE, null);
            }
        };

        public LeScanLater21() {
            mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

        @Override
        public boolean startScan() {
            if (mScanner != null) {
                mScanner.startScan(mLeScanCallback);
                return true;
            }
            LetvLog.w(TAG, "startScan error is null");
            return false;
        }

        @Override
        public boolean stopScan() {
            if (mScanner != null) {
                try {
                    mScanner.stopScan(mLeScanCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
    }

    class LeScanEarlier21 implements LeScanner {

        private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (isMatch(device)) {
                    LetvLog.d(TAG, "Found Valid Device BY LeScanEarlier21");                    
                    broadcastScanResult(KeyEventManager.SCAN_FOUND, device);
                    mScannedDevices.add(device);
                }
            }
        };

        @Override
        public boolean startScan() {
            LetvLog.d(TAG,"into startScan BY LeScanEarlier21");
            return mBluetoothAdapter.startLeScan(mLeScanCallback);
        }

        @Override
        public boolean stopScan() {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            return true;
        }
    }

    public KeypadScanner(Context context) {
        LetvLog.d(TAG, "into KeypadScanner");
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new Handler(mContext.getMainLooper());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScannerList.add(new LeScanLater21());
        } else {
            mScannerList.add(new LeScanEarlier21());
        }
        mScannerList.add(new NormalScanner());
    }

    private boolean isMatch(BluetoothDevice device) {
        if (checkDeviceValid(device) && !isScanned(device)) {
            final String deviceName = device.getName();
            if (deviceName.contains("LeDrive")
                    || deviceName.contains("Keyboard")
                    || deviceName.contains("LeAuto")) {
                return true;
            }
        }
        return false;
    }

    private boolean isScanned(BluetoothDevice device) {
        synchronized (mScannedDevices) {
            return mScannedDevices.contains(device);
        }
    }

    private boolean checkDeviceValid(BluetoothDevice device) {
        return (device != null && device.getName() != null);
    }

    private void delayStopScan(long timeout) {
        mDeferStopScanRunnable = new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        };
        mHandler.postDelayed(mDeferStopScanRunnable,timeout);
    }

    synchronized public boolean isScanning() {
        return mLeScanner != null;
    }

    synchronized public boolean startScan(long timeout) {
        if (mLeScanner != null) {
            LetvLog.w(TAG, "Is Searching Devices...");
            return true;
        } else {
            mScannedDevices.clear();
            for (LeScanner scanner : mScannerList) {
                if (scanner.startScan()) {
                    mLeScanner = scanner;
                    if (timeout <= 0) {
                        timeout = DEFAULT_SCAN_TIMEOUT;
                    } else if (timeout > MAX_SCAN_TIMEOUT) {
                        timeout = MAX_SCAN_TIMEOUT;
                    }
                    delayStopScan(timeout);
                    return true;
                }
            }
        }
        LetvLog.w(TAG, "Is Scan Failed...");
        broadcastScanResult(KeyEventManager.SCAN_COMPLETE, null);
        return false;
    }

    synchronized public boolean stopScan() {
        if (mLeScanner != null) {
            mLeScanner.stopScan();
            mLeScanner = null;
            if (mDeferStopScanRunnable != null) {
                mHandler.removeCallbacks(mDeferStopScanRunnable);
                mDeferStopScanRunnable = null;
            }
        }
        broadcastScanResult(KeyEventManager.SCAN_COMPLETE, null);
        return true;
    }
}
