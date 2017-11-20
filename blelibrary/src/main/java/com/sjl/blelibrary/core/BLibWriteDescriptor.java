package com.sjl.blelibrary.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.sjl.blelibrary.base.BLibCode;
import com.sjl.blelibrary.util.BLibLogUtil;

import java.util.UUID;

/**
 * BLEWriteDescriptor
 *
 * @author SJL
 * @date 2017/5/3
 */

public class BLibWriteDescriptor {
    private static final String TAG = "BLEWriteDescriptor";

    public BLibWriteDescriptor() {
    }

    public int writeDescriptor(BluetoothGatt gatt, String uuidDescriptorService, String uuidDescriptorCharacteristic, String uuidDescriptor) {
        //获取GATT服务
        BluetoothGattService bluetoothGattService = gatt.getService(UUID.fromString(uuidDescriptorService));
        if (bluetoothGattService == null) {
            BLibLogUtil.e(TAG, "writeDescriptor getService null");
            return BLibCode.ER_WRITEDESC_GET_SERVICE;
        }

        //获取特性
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(uuidDescriptorCharacteristic));
        if (bluetoothGattCharacteristic == null) {
            BLibLogUtil.e(TAG, "writeDescriptor getCharacteristic null");
            return BLibCode.ER_WRITEDESC_GET_CHARACTERISTIC;
        }
        if (!gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true)) {
            BLibLogUtil.e(TAG, "writeDescriptor setCharacteristicNotification null");
            return BLibCode.ER_WRITEDESC_ENABLE_NOTIFICATION;
        }
        //设置蓝牙返回数据提醒
        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(uuidDescriptor));
        if (bluetoothGattDescriptor == null) {
            BLibLogUtil.e(TAG, "writeDescriptor getDescriptor null");
            return BLibCode.ER_WRITEDESC_GET_DESC;
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
            return BLibCode.ER_WRITEDESC_WRITE_DESC;
        }
        return 1;
    }
}
