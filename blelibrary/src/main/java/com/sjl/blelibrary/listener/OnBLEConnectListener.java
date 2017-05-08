package com.sjl.blelibrary.listener;

import android.bluetooth.BluetoothGatt;

import com.sjl.blelibrary.BLEException;

/**
 * OnBLEConnectListener
 *
 * @author SJL
 * @date 2017/5/3
 */

public interface OnBLEConnectListener {
    void onConnectSuccess(BluetoothGatt gatt, int status, int newState);

    void onConnectFailure(BluetoothGatt gatt,BLEException bleException);

    void onServicesDiscovered(BluetoothGatt gatt, int status);
}
