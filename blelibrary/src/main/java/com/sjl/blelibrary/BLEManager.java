package com.sjl.blelibrary;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.sjl.blelibrary.listener.OnBLEConnectListener;
import com.sjl.blelibrary.listener.OnBLEReceiveDataListener;
import com.sjl.blelibrary.listener.OnBLEWriteDataListener;
import com.sjl.blelibrary.listener.OnBLEWriteDescriptorListener;
import com.sjl.blelibrary.util.BLELogUtil;

/**
 * XiaoDiManager
 *
 * @author SJL
 * @date 2017/5/3
 */

public class BLEManager {
    private static final String TAG = "XiaoDiManager";
    private Application application;
    private String mac;
    private BLEBluetoothGattPool bleBluetoothGattPool;
    private static BLEManager bleManager;

    public static BLEManager getInstance(Application application) {
        if (bleManager == null) {
            synchronized (TAG) {
                if (bleManager == null) {
                    bleManager = new BLEManager(application);
                }
            }
        }
        return bleManager;
    }

    private BLEManager(Application application) {
        this.application = application;
        initPool();
    }

    private void initPool() {
        bleBluetoothGattPool = new BLEBluetoothGattPool();
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
    public void enableBluetooth(Context context) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

    private BLEScanner bleScanner;

    /**
     * 扫描设备
     *
     * @param onBLEScanListener
     */
    public void startScan(BLEScanner.OnBLEScanListener onBLEScanListener) {
        //默认设置5秒
        startScan(5000, onBLEScanListener);
    }

    /**
     * 扫描设备
     *
     * @param timeout           扫描时长
     * @param onBLEScanListener
     */
    public void startScan(int timeout, BLEScanner.OnBLEScanListener onBLEScanListener) {
        if (bleScanner == null) {
            bleScanner = new BLEScanner(timeout, onBLEScanListener);
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
    public void connect(final String mac, final OnBLEConnectListener onBLEConnectListener) {
        BLEGattCallback bleGattCallback = bleBluetoothGattPool.getBluetoothGattCallback(mac);
        if (bleGattCallback == null) {
            bleGattCallback = new BLEGattCallback();
        }
        bleGattCallback.setOnBLEConnectListener(new OnBLEConnectListener() {
            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status, int newState) {
                onBLEConnectListener.onConnectSuccess(gatt, status, newState);
            }

            @Override
            public void onConnectFailure(BluetoothGatt gatt, BLEException bleException) {
                if (gatt != null && gatt.getDevice().getAddress().toUpperCase().equals(mac.toUpperCase())) {
                    BLELogUtil.e(TAG, "onConnectFailure");
                    onBLEConnectListener.onConnectFailure(gatt, bleException);
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
            bluetoothGatt = new BLEConnect().connect(application, mac, bleGattCallback);
        }
        if (bluetoothGatt == null) {
            onBLEConnectListener.onConnectFailure(null, new BLEException(BLEException.CONNECT_FAILURE));
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
    public void writeDescriptor(String mac, String uuidDescriptorService, String uuidDescriptorCharacteristic, String uuidDescriptor, OnBLEWriteDescriptorListener onBLEWriteDescriptorListener) {
        BLEGattCallback bleGattCallback = bleBluetoothGattPool.getBluetoothGattCallback(mac);
        if(bleGattCallback==null){
            onBLEWriteDescriptorListener.onWriteDescriptorFailure(new BLEException(BLEException.WRITE_DESCRIPTOR_FAILURE));
            return;
        }
        bleGattCallback.setOnBLEWriteDescriptorListener(onBLEWriteDescriptorListener);
        if (!new BLEWriteDescriptor().writeDescriptor(bleBluetoothGattPool.getBluetoothGatt(mac), uuidDescriptorService, uuidDescriptorCharacteristic, uuidDescriptor)) {
            bleGattCallback.setOnBLEConnectListener(null);
            onBLEWriteDescriptorListener.onWriteDescriptorFailure(new BLEException(BLEException.WRITE_DESCRIPTOR_FAILURE));
        }
    }

    private BLEWriteData bleWriteData;

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
    public void writeData(String mac, String uuidWriteService, String uuidWriteCharacteristics, byte[] data, OnBLEWriteDataListener onBLEWriteDataListener, OnBLEReceiveDataListener onBLEReceiveDataListener) {
        BLEGattCallback bleGattCallback = bleBluetoothGattPool.getBluetoothGattCallback(mac);
        bleGattCallback.setOnBLEWriteDescriptorListener(null);
        bleGattCallback.setOnBLEWriteDataListener(onBLEWriteDataListener);
        bleGattCallback.setOnBLEReceiveDataListener(onBLEReceiveDataListener);
        if (bleWriteData == null) {
            bleWriteData = new BLEWriteData(onBLEWriteDataListener);
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
