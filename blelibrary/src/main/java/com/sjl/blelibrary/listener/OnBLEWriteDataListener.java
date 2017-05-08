package com.sjl.blelibrary.listener;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.sjl.blelibrary.BLEException;

/**
 * OnBLEWriteDataListener
 *
 * @author SJL
 * @date 2017/5/3
 */

public interface OnBLEWriteDataListener {
    void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onWriteDataFailure(BLEException exception);
}
