package com.sjl.blelibrary.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

import com.sjl.blelibrary.base.BLibCode;
import com.sjl.blelibrary.listener.OnBLibConnectListener;
import com.sjl.blelibrary.listener.OnBLibReceiveDataListener;
import com.sjl.blelibrary.listener.OnBLibWriteDataListener;
import com.sjl.blelibrary.listener.OnBLibWriteDescriptorListener;
import com.sjl.blelibrary.util.BLibLogUtil;

/**
 * BLibGattCallback
 *
 * @author SJL
 * @date 2017/2/8
 */

public class BLibGattCallback extends BluetoothGattCallback {
    private static final String TAG = "BLibGattCallback";

    private OnBLibConnectListener onBLEConnectListener;
    private OnBLibWriteDescriptorListener onBLEWriteDescriptorListener;
    private OnBLibWriteDataListener onBLEWriteDataListener;
    private OnBLibReceiveDataListener onBLEReceiveDataListener;

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            super.onConnectionStateChange(gatt, status, newState);
        //连接状态改变
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //连接gatt server
                BLibLogUtil.d(TAG, "onConnectionStateChange state connect");
                if (onBLEConnectListener != null) {
                    onBLEConnectListener.onConnectSuccess(gatt, status, newState);
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //断开连接gatt server
                BLibLogUtil.e(TAG, "onConnectionStateChange state disconnected");
                if (onBLEConnectListener != null) {
                    onBLEConnectListener.onConnectFailure(gatt, BLibCode.ER_DISCONNECT);
                }
            } else {
                BLibLogUtil.e(TAG, "onConnectionStateChange status=" + status + ",newState=" + newState);
            }
        } else {
            BLibLogUtil.e(TAG, "onConnectionStateChange status=" + status);
            if (onBLEConnectListener != null) {
                onBLEConnectListener.onConnectFailure(gatt, BLibCode.matchCode(status));
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BLibLogUtil.d(TAG, "onServicesDiscovered gatt success");
            if (onBLEConnectListener != null) {
                onBLEConnectListener.onServicesDiscovered(gatt, status);
            }
        } else {
            BLibLogUtil.e(TAG, "onServicesDiscovered status=" + status);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        //super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BLibLogUtil.d(TAG, "onCharacteristicWrite gatt success");
            if (onBLEWriteDataListener != null) {
                onBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status);
            }
        } else {
            BLibLogUtil.e(TAG, "onCharacteristicWrite status=" + status + ",characteristic uuid=" + characteristic.getUuid().toString());
            if (onBLEWriteDataListener != null) {
                onBLEWriteDataListener.onWriteDataFailure(BLibCode.ER_WRITEDATA_CALLBACK);
            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);
        BLibLogUtil.d(TAG, "onCharacteristicRead");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            super.onCharacteristicChanged(gatt, characteristic);
        BLibLogUtil.d(TAG, "onCharacteristicChanged receiverData");

        if (onBLEReceiveDataListener != null) {
            onBLEReceiveDataListener.onReceiveData(characteristic.getValue());
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//            super.onDescriptorWrite(gatt, descriptor, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BLibLogUtil.d(TAG, "onDescriptorWrite gatt success");
            if (onBLEWriteDescriptorListener != null) {
                onBLEWriteDescriptorListener.onWriteDescriptorSuccess(gatt, descriptor, status);
            }
        } else {
            BLibLogUtil.e(TAG, "onDescriptorWrite status=" + status);
            if (onBLEWriteDescriptorListener != null) {
                onBLEWriteDescriptorListener.onWriteDescriptorFailure(BLibCode.ER_WRITEDESC_CALLBACK);
            }
        }
    }

    public void setOnBLEConnectListener(OnBLibConnectListener onBLEConnectListener) {
        this.onBLEConnectListener = onBLEConnectListener;
    }

    public void setOnBLEWriteDescriptorListener(OnBLibWriteDescriptorListener onBLEWriteDescriptorListener) {
        this.onBLEWriteDescriptorListener = onBLEWriteDescriptorListener;
    }

    public void setOnBLEWriteDataListener(OnBLibWriteDataListener onBLEWriteDataListener) {
        this.onBLEWriteDataListener = onBLEWriteDataListener;
    }

    public void setOnBLEReceiveDataListener(OnBLibReceiveDataListener onBLEReceiveDataListener) {
        this.onBLEReceiveDataListener = onBLEReceiveDataListener;
    }
}
