package com.sjl.blelibrary.listener;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * OnBLEWriteDescriptorListener
 *
 * @author 林zero
 * @date 2017/5/3
 */

public interface OnBLibWriteDescriptorListener {
    void onWriteDescriptorSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

    void onWriteDescriptorFailure(int code);
}
