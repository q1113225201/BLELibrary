package com.sjl.blelibrary.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.sjl.blelibrary.constant.BLibCode;
import com.sjl.blelibrary.listener.OnBLibWriteDataListener;
import com.sjl.blelibrary.util.BLibByteUtil;
import com.sjl.blelibrary.util.BLibLogUtil;

import java.util.UUID;

/**
 * BLibWriteData
 *
 * @author 林zero
 * @date 2017/5/3
 */

public class BLibWriteData {
    private static final String TAG = "BLibWriteData";
    public static final long WAIT_TIME = 70;//两组数据之间的时间间隔
    public static final int MAX_BYTES = 20;// 蓝牙发送数据分包，每个包的最大长度为20个字节
    private int currentPosition = 0;
    private OnBLibWriteDataListener onBLEWriteDataListener;

    public BLibWriteData(OnBLibWriteDataListener onBLEWriteDataListener) {
        this.onBLEWriteDataListener = onBLEWriteDataListener;
    }

    /**
     * 写数据
     *
     * @param gatt
     * @param uuidWriteService
     * @param uuidWriteCharacteristics
     * @param data
     */
    public void writeData(BluetoothGatt gatt, BLibGattCallback gattCallback, String uuidWriteService, String uuidWriteCharacteristics, byte[] data) {
        BluetoothGattService bluetoothGattService = gatt.getService(UUID.fromString(uuidWriteService));
        if (bluetoothGattService == null) {
            BLibLogUtil.d(TAG, "writeData getService failure");
            if (onBLEWriteDataListener != null) {
                onBLEWriteDataListener.onWriteDataFailure(BLibCode.ER_WRITE_DATA_GET_SERVICE);
            }
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(uuidWriteCharacteristics));
        if (bluetoothGattCharacteristic == null) {
            BLibLogUtil.d(TAG, "writeData getCharacteristic failure");
            if (onBLEWriteDataListener != null) {
                onBLEWriteDataListener.onWriteDataFailure(BLibCode.ER_WRITE_DATA_GET_CHARACTERISTIC);
            }
            return;
        }
        //分包写数据
        writeOneSet(gatt, gattCallback, bluetoothGattCharacteristic, data, 0);
    }

    /**
     * 写一组数据
     *
     * @param gatt
     * @param gattCallback
     * @param bluetoothGattCharacteristic
     * @param data
     * @param position
     */
    private void writeOneSet(final BluetoothGatt gatt, final BLibGattCallback gattCallback, final BluetoothGattCharacteristic bluetoothGattCharacteristic, final byte[] data, int position) {
        currentPosition = position;
        if (position == 0) {
            //第一组数据
            gattCallback.setOnBLEWriteDataListener(new OnBLibWriteDataListener() {
                @Override
                public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    if ((currentPosition + 1) * MAX_BYTES >= data.length) {
                        //数据完全写完
                        if (onBLEWriteDataListener != null) {
                            onBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status);
                        }
                        return;
                    }
                    writeOneSet(gatt, gattCallback, bluetoothGattCharacteristic, data, currentPosition + 1);
                }

                @Override
                public void onWriteDataFailure(int code) {
                    if (onBLEWriteDataListener != null) {
                        onBLEWriteDataListener.onWriteDataFailure(code);
                    }
                }
            });
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //两包数据间间隔一定时间
                    Thread.sleep(WAIT_TIME);
                    int sendLength = data.length - currentPosition * MAX_BYTES;
                    sendLength = sendLength > MAX_BYTES ? MAX_BYTES : sendLength;
                    byte[] sendValue = BLibByteUtil.subBytes(data, currentPosition * MAX_BYTES, sendLength);
                    BLibLogUtil.d(TAG, String.format("position=%d,%s", currentPosition, BLibByteUtil.bytesToHexString(sendValue)));
                    if (!bluetoothGattCharacteristic.setValue(sendValue)) {
                        BLibLogUtil.d(TAG, "writeOneSet setValue failure");
                        if (onBLEWriteDataListener != null) {
                            onBLEWriteDataListener.onWriteDataFailure(BLibCode.ER_WRITE_DATA_WRITE_DATA);
                        }
                        return;
                    }
                    if (!gatt.writeCharacteristic(bluetoothGattCharacteristic)) {
                        BLibLogUtil.d(TAG, "writeOneSet writeCharacteristic failure");
                        if (onBLEWriteDataListener != null) {
                            onBLEWriteDataListener.onWriteDataFailure(BLibCode.ER_WRITE_DATA_WRITE_CHARACTERISTIC);
                        }
                        return;
                    }
                } catch (Exception e) {
                    BLibLogUtil.d(TAG, "writeOneSet e:" + e.getMessage());
                }
            }
        }).start();
    }

    public void setOnBLEWriteDataListener(OnBLibWriteDataListener onBLEWriteDataListener) {
        this.onBLEWriteDataListener = onBLEWriteDataListener;
    }
}
