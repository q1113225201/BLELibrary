package com.sjl.blelibrary.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.sjl.blelibrary.constant.BLibCode;
import com.sjl.blelibrary.util.BLibLogUtil;

import java.util.UUID;

/**
 * BLibWriteDescriptor
 *
 * @author 林zero
 * @date 2017/5/3
 */

public class BLibWriteDescriptor {
    private static final String TAG = "BLibWriteDescriptor";

    public BLibWriteDescriptor() {
    }

    public int writeDescriptor(BluetoothGatt gatt, String uuidDescriptorService, String uuidDescriptorCharacteristic, String uuidDescriptor) {
        //获取GATT服务
        BluetoothGattService bluetoothGattService = gatt.getService(UUID.fromString(uuidDescriptorService));
        if (bluetoothGattService == null) {
            BLibLogUtil.d(TAG, "writeDescriptor getService null");
            return BLibCode.ER_WRITE_DESC_GET_SERVICE;
        }

        //获取特性
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(uuidDescriptorCharacteristic));
        if (bluetoothGattCharacteristic == null) {
            BLibLogUtil.d(TAG, "writeDescriptor getCharacteristic null");
            return BLibCode.ER_WRITE_DESC_GET_CHARACTERISTIC;
        }
        if (!gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true)) {
            BLibLogUtil.d(TAG, "writeDescriptor setCharacteristicNotification null");
            return BLibCode.ER_WRITE_DESC_ENABLE_NOTIFICATION;
        }
        //设置蓝牙返回数据提醒
        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(uuidDescriptor));
        if (bluetoothGattDescriptor == null) {
            BLibLogUtil.d(TAG, "writeDescriptor getDescriptor null");
            return BLibCode.ER_WRITE_DESC_GET_DESC;
        }
        //根据特征属性设置
        if((bluetoothGattCharacteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_NOTIFY)==0) {
            //显示特征
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        }else{
            //显示通知
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        }

        if (!gatt.writeDescriptor(bluetoothGattDescriptor)) {
            BLibLogUtil.e(TAG, "writeDescriptor writeDescriptor false");
            return BLibCode.ER_WRITE_DESC_WRITE_DESC;
        }
        return 1;
    }
}
