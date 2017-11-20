package com.sjl.blelibrary.core;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.sjl.blelibrary.base.BLibCode;
import com.sjl.blelibrary.base.BLibInit;
import com.sjl.blelibrary.listener.OnBLibConnectListener;
import com.sjl.blelibrary.listener.OnBLibReceiveDataListener;
import com.sjl.blelibrary.listener.OnBLibWriteDataListener;
import com.sjl.blelibrary.listener.OnBLibWriteDescriptorListener;
import com.sjl.blelibrary.util.BLibLogUtil;

/**
 * XiaoDiManager
 *
 * @author SJL
 * @date 2017/5/3
 */

public class BLibManager {
    private static final String TAG = "XiaoDiManager";
    private Application application;
    private String mac;
    private BLibGattPool bleBluetoothGattPool;
    private static BLibManager bleManager;

    public static BLibManager getInstance() {
        if (bleManager == null) {
            synchronized (TAG) {
                if (bleManager == null) {
                    bleManager = new BLibManager();
                }
            }
        }
        return bleManager;
    }

    private BLibManager() {
        this.application = BLibInit.application;
        if(this.application==null){
            throw new NullPointerException();
        }
        initPool();
    }

    private void initPool() {
        bleBluetoothGattPool = new BLibGattPool();
    }

    /**
     * 是否支持蓝牙
     *
     * @return
     */
    public boolean isSupportBluetooth() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    /**
     * 是否支持BLE
     *
     * @return
     */
    public boolean isSupportBLE() {
        return application.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 蓝牙是否可用
     *
     * @return
     */
    public boolean isBluetoothEnable() {
        if (isSupportBluetooth()) {
            return BluetoothAdapter.getDefaultAdapter().isEnabled();
        }
        return false;
    }

    /**
     * 开启蓝牙
     */
    public void enableBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BLibInit.application.startActivity(intent);
    }

    /**
     * 是否已连接
     *
     * @param mac
     * @return
     */
    public boolean isConnect(String mac) {
        setMac(mac);
        return bleBluetoothGattPool.isConnect(mac);
    }

    private BLibScanner bleScanner;

    /**
     * 扫描设备
     *
     * @param onBLEScanListener
     */
    public void startScan(BLibScanner.OnBLEScanListener onBLEScanListener) {
        //默认设置5秒
        startScan(5000, onBLEScanListener);
    }

    /**
     * 扫描设备
     *
     * @param timeout           扫描时长
     * @param onBLEScanListener
     */
    public void startScan(int timeout, BLibScanner.OnBLEScanListener onBLEScanListener) {
        if (bleScanner == null) {
            bleScanner = new BLibScanner(timeout, onBLEScanListener);
        }
        bleScanner.setOnBLEScanListener(onBLEScanListener);
        bleScanner.setTimeout(timeout);
        bleScanner.startScan();
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (bleScanner != null) {
            bleScanner.stopScan();
        }
    }

    /**
     * 连接设备
     *
     * @param mac
     * @param onBLEConnectListener
     */
    public void connect(final String mac, final OnBLibConnectListener onBLEConnectListener) {
        BLibGattCallback bleGattCallback = bleBluetoothGattPool.getBluetoothGattCallback(mac);
        if (bleGattCallback == null) {
            bleGattCallback = new BLibGattCallback();
        }
        bleGattCallback.setOnBLEConnectListener(new OnBLibConnectListener() {
            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status, int newState) {
                onBLEConnectListener.onConnectSuccess(gatt, status, newState);
            }

            @Override
            public void onConnectFailure(BluetoothGatt gatt,int code) {
                if (gatt != null && gatt.getDevice().getAddress().toUpperCase().equals(mac.toUpperCase())) {
                    BLibLogUtil.e(TAG, "onConnectFailure");
                    onBLEConnectListener.onConnectFailure(gatt, code);
                }
//                disconnectGatt(mac);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                onBLEConnectListener.onServicesDiscovered(gatt, status);
            }
        });
        setMac(mac);
        BluetoothGatt bluetoothGatt = bleBluetoothGattPool.getBluetoothGatt(mac);
        if (bluetoothGatt == null) {
            bluetoothGatt = new BLibConnect().connect(application, mac, bleGattCallback);
        }
        if (bluetoothGatt == null) {
            onBLEConnectListener.onConnectFailure(null, BLibCode.ER_CONNECTED);
        }
        bleBluetoothGattPool.setBluetoothGatt(mac, bluetoothGatt, bleGattCallback);
    }

    /**
     * 写特征值
     *
     * @param mac
     * @param uuidDescriptorService
     * @param uuidDescriptorCharacteristic
     * @param uuidDescriptor
     * @param onBLEWriteDescriptorListener
     */
    public void writeDescriptor(String mac, String uuidDescriptorService, String uuidDescriptorCharacteristic, String uuidDescriptor, OnBLibWriteDescriptorListener onBLEWriteDescriptorListener) {
        BLibGattCallback bleGattCallback = bleBluetoothGattPool.getBluetoothGattCallback(mac);
        if(bleGattCallback==null){
            onBLEWriteDescriptorListener.onWriteDescriptorFailure(BLibCode.ER_WRITEDESC);
            return;
        }
        bleGattCallback.setOnBLEWriteDescriptorListener(onBLEWriteDescriptorListener);
        int result = new BLibWriteDescriptor().writeDescriptor(bleBluetoothGattPool.getBluetoothGatt(mac), uuidDescriptorService, uuidDescriptorCharacteristic, uuidDescriptor);
        if (result<0) {
            bleGattCallback.setOnBLEConnectListener(null);
            onBLEWriteDescriptorListener.onWriteDescriptorFailure(result);
        }
    }

    private BLibWriteData bleWriteData;

    /**
     * 写数据
     *
     * @param mac
     * @param uuidWriteService
     * @param uuidWriteCharacteristics
     * @param data
     * @param onBLEWriteDataListener
     * @param onBLEReceiveDataListener
     */
    public void writeData(String mac, String uuidWriteService, String uuidWriteCharacteristics, byte[] data, OnBLibWriteDataListener onBLEWriteDataListener, OnBLibReceiveDataListener onBLEReceiveDataListener) {
        BLibGattCallback bleGattCallback = bleBluetoothGattPool.getBluetoothGattCallback(mac);
        bleGattCallback.setOnBLEWriteDescriptorListener(null);
        bleGattCallback.setOnBLEWriteDataListener(onBLEWriteDataListener);
        bleGattCallback.setOnBLEReceiveDataListener(onBLEReceiveDataListener);
        if (bleWriteData == null) {
            bleWriteData = new BLibWriteData(onBLEWriteDataListener);
        }
        bleWriteData.setOnBLEWriteDataListener(onBLEWriteDataListener);
        bleWriteData.writeData(bleBluetoothGattPool.getBluetoothGatt(mac), bleGattCallback, uuidWriteService, uuidWriteCharacteristics, data);
    }

    private void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * 断开Gatt连接
     *
     * @param mac
     */
    public synchronized void disconnectGatt(String mac) {
        bleBluetoothGattPool.disconnectGatt(mac);
    }
}
