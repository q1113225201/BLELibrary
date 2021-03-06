package com.sjl.blelibrary;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.sjl.blelibrary.constant.BLibCode;
import com.sjl.blelibrary.core.BLibAdvertiser;
import com.sjl.blelibrary.core.BLibConnect;
import com.sjl.blelibrary.core.BLibGattCallback;
import com.sjl.blelibrary.core.BLibGattPool;
import com.sjl.blelibrary.core.BLibScanner;
import com.sjl.blelibrary.core.BLibWriteData;
import com.sjl.blelibrary.core.BLibWriteDescriptor;
import com.sjl.blelibrary.listener.OnBLibConnectListener;
import com.sjl.blelibrary.listener.OnBLibReceiveDataListener;
import com.sjl.blelibrary.listener.OnBLibWriteDataListener;
import com.sjl.blelibrary.listener.OnBLibWriteDescriptorListener;
import com.sjl.blelibrary.util.BLibLogUtil;

/**
 * BLibManager
 *
 * @author 林zero
 * @date 2017/5/3
 */
public class BLibManager {
    private static final String TAG = "BLibManager";
    private Application application;
    private BLibGattPool bLibGattPool;
    private static BLibManager bLibManager;

    public static BLibManager getInstance() {
        if (bLibManager == null) {
            synchronized (TAG) {
                if (bLibManager == null) {
                    bLibManager = new BLibManager();
                }
            }
        }
        return bLibManager;
    }

    private BLibManager() {
        this.application = BLibInit.application;
        if (this.application == null) {
            throw new NullPointerException();
        }
        bLibGattPool = new BLibGattPool();
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
     * 是否打开蓝牙
     *
     * @return
     */
    public boolean openBluetooth() {
        if (!isSupportBluetooth()) {
            //不支持蓝牙
            Toast.makeText(application, "不支持蓝牙", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isSupportBLE()) {
            //不支持低功耗蓝牙
            Toast.makeText(application, "不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isBluetoothEnable()) {
            enableBluetooth();
            return false;
        }
        return true;
    }

    /**
     * 是否已连接
     *
     * @param mac
     * @return
     */
    public boolean isConnect(String mac) {
        return bLibGattPool.isConnect(mac);
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
        if (!openBluetooth()) {
            onBLEScanListener.onScanFailed(BLibCode.ER_DISABLE);
            return;
        }
        if (bleScanner == null) {
            bleScanner = new BLibScanner(onBLEScanListener, timeout);
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

    private BLibAdvertiser bLibAdvertiser;

    /**
     * 开始广播
     */
    public void startAdvertising(AdvertiseSettings settings, AdvertiseData advertiseData, BLibAdvertiser.OnBLEAdvertisingListener onBLEAdvertisingListener) {
        startAdvertising(settings, advertiseData, null, onBLEAdvertisingListener);
    }

    /**
     * 开始广播
     *
     * @param settings
     * @param advertiseData
     * @param scanResponse
     * @param onBLEAdvertisingListener
     */
    public void startAdvertising(AdvertiseSettings settings, AdvertiseData advertiseData, AdvertiseData scanResponse, BLibAdvertiser.OnBLEAdvertisingListener onBLEAdvertisingListener) {
        if (!openBluetooth()) {
            onBLEAdvertisingListener.onStartFailure(BLibCode.ER_DISABLE);
            return;
        }
        if (bLibAdvertiser == null) {
            bLibAdvertiser = new BLibAdvertiser(onBLEAdvertisingListener);
        }
        bLibAdvertiser.setOnBLEAdvertisingListener(onBLEAdvertisingListener);
        bLibAdvertiser.startAdvertising(settings, advertiseData, scanResponse);
    }

    /**
     * 停止广播
     */
    public void stopAdvertising() {
        if (bLibAdvertiser != null) {
            bLibAdvertiser.stopAdvertising();
        }
    }

    private static final int MAX_CNT = 3;
    private int connectCnt = -1;

    /**
     * 连接设备
     *
     * @param mac
     * @param onBLEConnectListener
     */
    public void connect(final String mac, final OnBLibConnectListener onBLEConnectListener) {
        BLibLogUtil.i(TAG, "connect");
        if (!openBluetooth()) {
            onBLEConnectListener.onConnectFailure(null, BLibCode.ER_DISABLE);
            return;
        }
        BLibGattCallback bleGattCallback = bLibGattPool.getBluetoothGattCallback(mac);
        if (bleGattCallback == null) {
            bleGattCallback = new BLibGattCallback();
        }
        bleGattCallback.setOnBLEConnectListener(new OnBLibConnectListener() {
            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status, int newState) {
                connectCnt = -1;
                onBLEConnectListener.onConnectSuccess(gatt, status, newState);
            }

            @Override
            public void onConnectFailure(BluetoothGatt gatt, int code) {
                BLibLogUtil.e(TAG, "onConnectFailure:" + connectCnt);
                if (connectCnt == -1 || connectCnt++ > MAX_CNT) {
                    connectCnt = -1;
                    if (gatt != null && gatt.getDevice().getAddress().equalsIgnoreCase(mac)) {
                        onBLEConnectListener.onConnectFailure(gatt, code);
                    }
                    disconnectGatt(mac);
                    return;
                }
                //重连
                disconnectGatt(mac);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connect(mac, onBLEConnectListener);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                connectCnt = -1;
                onBLEConnectListener.onServicesDiscovered(gatt, status);
            }
        });
        BluetoothGatt bluetoothGatt = bLibGattPool.getBluetoothGatt(mac);
        connectCnt = connectCnt == -1 ? 0 : connectCnt;
        //连接计数
        if (bluetoothGatt == null) {
            bluetoothGatt = new BLibConnect().connect(application, mac, bleGattCallback);
        }
        if (bluetoothGatt == null) {
            onBLEConnectListener.onConnectFailure(null, BLibCode.ER_CONNECTED);
        }
        bLibGattPool.setBluetoothGatt(mac, bluetoothGatt, bleGattCallback);
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
        if (!openBluetooth()) {
            onBLEWriteDescriptorListener.onWriteDescriptorFailure(BLibCode.ER_DISABLE);
            return;
        }
        BLibGattCallback bleGattCallback = bLibGattPool.getBluetoothGattCallback(mac);
        if (bleGattCallback == null) {
            onBLEWriteDescriptorListener.onWriteDescriptorFailure(BLibCode.ER_WRITE_DESC);
            return;
        }
        bleGattCallback.setOnBLEWriteDescriptorListener(onBLEWriteDescriptorListener);
        int result = new BLibWriteDescriptor().writeDescriptor(bLibGattPool.getBluetoothGatt(mac), uuidDescriptorService, uuidDescriptorCharacteristic, uuidDescriptor);
        if (result < 0) {
            bleGattCallback.setOnBLEConnectListener(null);
            onBLEWriteDescriptorListener.onWriteDescriptorFailure(result);
        }
    }

    private BLibWriteData bleWriteData;
    private int writeCnt = 0;

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
    public void writeData(final String mac, final String uuidWriteService, final String uuidWriteCharacteristics, final byte[] data, final OnBLibWriteDataListener onBLEWriteDataListener, final OnBLibReceiveDataListener onBLEReceiveDataListener) {
        if (!openBluetooth()) {
            onBLEWriteDataListener.onWriteDataFailure(BLibCode.ER_WRITE_DESC);
            return;
        }
        BLibGattCallback bleGattCallback = bLibGattPool.getBluetoothGattCallback(mac);
        bleGattCallback.setOnBLEWriteDescriptorListener(null);
        bleGattCallback.setOnBLEWriteDataListener(onBLEWriteDataListener);
        bleGattCallback.setOnBLEReceiveDataListener(onBLEReceiveDataListener);
        if (bleWriteData == null) {
            bleWriteData = new BLibWriteData(new OnBLibWriteDataListener() {
                @Override
                public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    writeCnt = 0;
                    if (onBLEWriteDataListener != null) {
                        onBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status);
                    }
                }

                @Override
                public void onWriteDataFailure(int code) {
                    if (writeCnt++ > MAX_CNT) {
                        writeCnt = 0;
                        if (onBLEWriteDataListener != null) {
                            onBLEWriteDataListener.onWriteDataFailure(code);
                        }
                        return;
                    }
                    //重写
                    writeData(mac, uuidWriteService, uuidWriteCharacteristics, data, onBLEWriteDataListener, onBLEReceiveDataListener);
                }
            });
        }
        bleWriteData.setOnBLEWriteDataListener(onBLEWriteDataListener);
        bleWriteData.writeData(bLibGattPool.getBluetoothGatt(mac), bleGattCallback, uuidWriteService, uuidWriteCharacteristics, data);
    }

    /**
     * 断开Gatt连接
     *
     * @param mac
     */
    public synchronized void disconnectGatt(String mac) {
        bLibGattPool.disconnectGatt(mac);
    }
}
