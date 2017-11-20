package com.sjl.blelibrary.listener;

import android.bluetooth.BluetoothGatt;

/**
 * OnBLEConnectListener
 *
 * @author SJL
 * @date 2017/5/3
 */

public interface OnBLibConnectListener {
    void onConnectSuccess(BluetoothGatt gatt, int status, int newState);

    void onConnectFailure(BluetoothGatt gatt, int code);

    void onServicesDiscovered(BluetoothGatt gatt, int status);
}
