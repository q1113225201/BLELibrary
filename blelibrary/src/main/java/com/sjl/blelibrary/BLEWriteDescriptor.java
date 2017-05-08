package com.sjl.blelibrary;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.sjl.blelibrary.util.BLELogUtil;

import java.util.UUID;

/**
 * BLEWriteDescriptor
 *
 * @author SJL
 * @date 2017/5/3
 */

public class BLEWriteDescriptor {
    private static final String TAG = "BLEWriteDescriptor";

    public BLEWriteDescriptor() {
    }

    public boolean writeDescriptor(BluetoothGatt gatt, String uuidDescriptorService, String uuidDescriptorCharacteristic, String uuidDescriptor) {
        //获取GATT服务
        BluetoothGattService bluetoothGattService = gatt.getService(UUID.fromString(uuidDescriptorService));
        if (bluetoothGattService == null) {
            BLELogUtil.e(TAG, "writeDescriptor getService failure");
            return false;
        }
        //获取特性
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(uuidDescriptorCharacteristic));
        if (bluetoothGattCharacteristic == null) {
            BLELogUtil.e(TAG, "writeDescriptor getCharacteristic failure");
            return false;
        }
        //设置蓝牙返回数据提醒
        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(uuidDescriptor));
        if (bluetoothGattDescriptor == null) {
            BLELogUtil.e(TAG, "writeDescriptor bluetoothGattDescriptor == null");
            return false;
        }
        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        if (!gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true)) {
            BLELogUtil.e(TAG, "writeDescriptor setCharacteristicNotification failure");
            return false;
        }

        if (!gatt.writeDescriptor(bluetoothGattDescriptor)) {
            BLELogUtil.e(TAG, "writeDescriptor writeDescriptor failure");
            return false;
        }
        return true;
    }
}
