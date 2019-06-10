package com.sjl.blelibrary.listener;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * OnBLEWriteDataListener
 *
 * @author 林zero
 * @date 2017/5/3
 */

public interface OnBLibWriteDataListener {
    void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onWriteDataFailure(int code);
}
