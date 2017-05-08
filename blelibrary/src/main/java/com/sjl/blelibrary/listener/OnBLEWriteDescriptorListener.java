package com.sjl.blelibrary.listener;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.sjl.blelibrary.BLEException;

/**
 * OnBLEWriteDescriptorListener
 *
 * @author SJL
 * @date 2017/5/3
 */

public interface OnBLEWriteDescriptorListener {
    void onWriteDescriptorSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

    void onWriteDescriptorFailure(BLEException bleException);
}
