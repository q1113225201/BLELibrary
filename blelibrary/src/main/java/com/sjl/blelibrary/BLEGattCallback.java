package com.sjl.blelibrary;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

import com.sjl.blelibrary.listener.OnBLEConnectListener;
import com.sjl.blelibrary.listener.OnBLEReceiveDataListener;
import com.sjl.blelibrary.listener.OnBLEWriteDataListener;
import com.sjl.blelibrary.listener.OnBLEWriteDescriptorListener;
import com.sjl.blelibrary.util.BLELogUtil;

/**
 * BLEGattCallback
 *
 * @author SJL
 * @date 2017/2/8
 */

public class BLEGattCallback extends BluetoothGattCallback {
    private static final String TAG = "BLEGattCallback";

    private OnBLEConnectListener onBLEConnectListener;
    private OnBLEWriteDescriptorListener onBLEWriteDescriptorListener;
    private OnBLEWriteDataListener onBLEWriteDataListener;
    private OnBLEReceiveDataListener onBLEReceiveDataListener;

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            super.onConnectionStateChange(gatt, status, newState);
        //连接状态改变
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //连接gatt server
                BLELogUtil.i(TAG, "onConnectionStateChange state connect");
                if (onBLEConnectListener != null) {
                    onBLEConnectListener.onConnectSuccess(gatt, status, newState);
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //断开连接gatt server
                BLELogUtil.e(TAG, "onConnectionStateChange state disconnected");
                if (onBLEConnectListener != null) {
                    onBLEConnectListener.onConnectFailure(gatt, new BLEException(BLEException.DISCONNECT));
                }
            } else {
                BLELogUtil.e(TAG, "onConnectionStateChange status=" + status + ",newState=" + newState);
            }
        } else {
            BLELogUtil.e(TAG, "onConnectionStateChange status=" + status);
            if (onBLEConnectListener != null) {
                onBLEConnectListener.onConnectFailure(gatt, new BLEException(BLEException.CONNECT_STATE_CHANGE));
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BLELogUtil.i(TAG, "onServicesDiscovered gatt success");
            if (onBLEConnectListener != null) {
                onBLEConnectListener.onServicesDiscovered(gatt, status);
            }
        } else {
            BLELogUtil.e(TAG, "onServicesDiscovered status=" + status);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BLELogUtil.i(TAG, "onCharacteristicWrite gatt success");
            if (onBLEWriteDataListener != null) {
                onBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status);
            }
        } else {
            BLELogUtil.e(TAG, "onCharacteristicWrite status=" + status + ",characteristic uuid=" + characteristic.getUuid().toString());
            if (onBLEWriteDataListener != null) {
                onBLEWriteDataListener.onWriteDataFailure(new BLEException(BLEException.WRITE_DATA_FAILURE));
            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);
        BLELogUtil.i(TAG, "onCharacteristicRead");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            super.onCharacteristicChanged(gatt, characteristic);
        BLELogUtil.i(TAG, "onCharacteristicChanged receiverData");

        if (onBLEReceiveDataListener != null) {
            onBLEReceiveDataListener.onReceiveData(characteristic.getValue());
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//            super.onDescriptorWrite(gatt, descriptor, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BLELogUtil.i(TAG, "onDescriptorWrite gatt success");
            if (onBLEWriteDescriptorListener != null) {
                onBLEWriteDescriptorListener.onWriteDescriptorSuccess(gatt, descriptor, status);
            }
        } else {
            BLELogUtil.e(TAG, "onDescriptorWrite status=" + status);
            if (onBLEWriteDescriptorListener != null) {
                onBLEWriteDescriptorListener.onWriteDescriptorFailure(new BLEException(BLEException.WRITE_DESCRIPTOR_FAILURE));
            }
        }
    }

    public void setOnBLEConnectListener(OnBLEConnectListener onBLEConnectListener) {
        this.onBLEConnectListener = onBLEConnectListener;
    }

    public void setOnBLEWriteDescriptorListener(OnBLEWriteDescriptorListener onBLEWriteDescriptorListener) {
        this.onBLEWriteDescriptorListener = onBLEWriteDescriptorListener;
    }

    public void setOnBLEWriteDataListener(OnBLEWriteDataListener onBLEWriteDataListener) {
        this.onBLEWriteDataListener = onBLEWriteDataListener;
    }

    public void setOnBLEReceiveDataListener(OnBLEReceiveDataListener onBLEReceiveDataListener) {
        this.onBLEReceiveDataListener = onBLEReceiveDataListener;
    }
}
