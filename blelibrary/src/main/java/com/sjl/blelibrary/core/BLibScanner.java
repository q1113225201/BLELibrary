package com.sjl.blelibrary.core;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.os.Looper;

import com.sjl.blelibrary.constant.BLibCode;
import com.sjl.blelibrary.util.BLibByteUtil;
import com.sjl.blelibrary.util.BLibLogUtil;

import java.util.List;

/**
 * BLibScanner
 *
 * @author 林zero
 * @date 2017/5/3
 */
@SuppressLint("NewApi")
public class BLibScanner {
    private static final String TAG = "BLibScanner";

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean isScanning = false;
    //超时时间
    private int timeout = 5000;
    //超时处理
    private Handler timeoutHandler = new Handler(Looper.getMainLooper());
    //扫描回调
    private OnBLEScanListener onBLEScanListener;
    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BLibLogUtil.d(TAG, "ScanResult:" + result+"\n"+ BLibByteUtil.bytesToHexString(result.getScanRecord().getBytes()));
            if(onBLEScanListener!=null) {
                onBLEScanListener.onScanResult(result.getDevice(), result.getRssi(), result.getScanRecord());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            BLibLogUtil.d(TAG, "onBatchScanResults:" + results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            BLibLogUtil.e(TAG, "onScanFailed:" + errorCode);
        }
    };

    public BLibScanner() {
        this(null);
    }

    public BLibScanner(OnBLEScanListener onBLEScanListener) {
        this(onBLEScanListener, 0);
    }

    public BLibScanner(OnBLEScanListener onBLEScanListener, int timeout) {
        this.timeout = timeout;
        this.onBLEScanListener = onBLEScanListener;
        init();
    }

    private void init() {
        if (bluetoothLeScanner == null) {
            bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        }
    }

    /**
     * 开始扫描
     */
    public void startScan() {
        if(isScanning){
            return;
        }
        init();
        if(bluetoothLeScanner!=null) {
            bluetoothLeScanner.startScan(scanCallback);
            isScanning = true;
        }
        if (timeout > 0) {
            timeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanTimeout();
                }
            }, timeout);
        }
    }

    /**
     * 扫描超时
     */
    private void scanTimeout() {
        BLibLogUtil.d(TAG, "scanTimeout:"+isScanning);
        if (isScanning) {
            stopScan();
            if(onBLEScanListener!=null&&BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                onBLEScanListener.onScanFailed(BLibCode.ER_DEVICE_NOT_FOUND);
            }
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        BLibLogUtil.d(TAG, "stopScan:"+isScanning);
        if (isScanning) {
            if(bluetoothLeScanner!=null&&BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                bluetoothLeScanner.stopScan(scanCallback);
            }
            isScanning = false;
        }
    }

    public void setOnBLEScanListener(OnBLEScanListener onBLEScanListener) {
        this.onBLEScanListener = onBLEScanListener;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public interface OnBLEScanListener {
        void onScanResult(BluetoothDevice device, int rssi, ScanRecord scanRecord);

        void onScanFailed(int code);
    }
}
