package com.sjl.blelibrary;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.os.Looper;

import com.sjl.blelibrary.util.BLELogUtil;

/**
 * BLEScanner
 *
 * @author SJL
 * @date 2017/5/3
 */
@SuppressLint("NewApi")
public class BLEScanner extends ScanCallback {
    private static final String TAG = "BLEScanner";

    private static BluetoothLeScanner bluetoothLeScanner;
    private boolean isScanning = false;
    //超时时间
    private int timeout = 5000;
    //超时处理
    private Handler timeoutHandler = new Handler(Looper.getMainLooper());
    //扫描回调
    private OnBLEScanListener onBLEScanListener;

    public interface OnBLEScanListener {
        void onScanResult(BluetoothDevice device, int rssi, byte[] scanRecord);

        void onScanFailed(BLEException bleException);
    }

    public BLEScanner(OnBLEScanListener onBLEScanListener) {
        this.onBLEScanListener = onBLEScanListener;
        init();
    }

    public BLEScanner(int timeout, OnBLEScanListener onBLEScanListener) {
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
        init();
        bluetoothLeScanner.startScan(this);
        isScanning = true;
        if (timeout > 0) {
            timeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanTimeOut();
                }
            }, timeout);
        }
    }

    /**
     * 扫描超时
     */
    private void scanTimeOut() {
        if (isScanning) {
            BLELogUtil.e(TAG, "scanTimeOut");
            stopScan();
            onBLEScanListener.onScanFailed(new BLEException(BLEException.SCAN_TIMEOUT));
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        init();
        isScanning = false;
        bluetoothLeScanner.stopScan(this);
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);

        BLELogUtil.e(TAG, "ScanResult:" + result);
        onBLEScanListener.onScanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
    }

    public OnBLEScanListener getOnBLEScanListener() {
        return onBLEScanListener;
    }

    public void setOnBLEScanListener(OnBLEScanListener onBLEScanListener) {
        this.onBLEScanListener = onBLEScanListener;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
