package com.sjl.blelibrary;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.sjl.blelibrary.listener.OnBLEWriteDataListener;
import com.sjl.blelibrary.util.BLEByteUtil;
import com.sjl.blelibrary.util.BLELogUtil;

import java.util.UUID;

/**
 * BLEWriteData
 *
 * @author SJL
 * @date 2017/5/3
 */

public class BLEWriteData {
    private static final String TAG = "BLEWriteData";
    public static final long WAIT_TIME = 70;//两组数据之间的时间间隔
    public static final int MAX_BYTES = 20;// 蓝牙发送数据分包，每个包的最大长度为20个字节
    private int currentPosition = 0;
    private OnBLEWriteDataListener onBLEWriteDataListener;

    public BLEWriteData(OnBLEWriteDataListener onBLEWriteDataListener) {
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
    public void writeData(BluetoothGatt gatt, BLEGattCallback gattCallback, String uuidWriteService, String uuidWriteCharacteristics, byte[] data) {
        BluetoothGattService bluetoothGattService = gatt.getService(UUID.fromString(uuidWriteService));
        if (bluetoothGattService == null) {
            BLELogUtil.e(TAG, "writeData getService failure");
            onBLEWriteDataListener.onWriteDataFailure(new BLEException(BLEException.WRITE_DATA_CHECK_FAILURE));
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(uuidWriteCharacteristics));
        if (bluetoothGattCharacteristic == null) {
            BLELogUtil.e(TAG, "writeData getCharacteristic failure");
            onBLEWriteDataListener.onWriteDataFailure(new BLEException(BLEException.WRITE_DATA_CHECK_FAILURE));
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
    private void writeOneSet(final BluetoothGatt gatt, final BLEGattCallback gattCallback, final BluetoothGattCharacteristic bluetoothGattCharacteristic, final byte[] data, int position) {
        currentPosition = position;
        if (position == 0) {
            //第一组数据
            gattCallback.setOnBLEWriteDataListener(new OnBLEWriteDataListener() {
                @Override
                public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    if ((currentPosition + 1) * MAX_BYTES >= data.length) {
                        //数据完全写完
                        onBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status);
                        return;
                    }
                    writeOneSet(gatt, gattCallback, bluetoothGattCharacteristic, data, currentPosition + 1);
                }

                @Override
                public void onWriteDataFailure(BLEException exception) {
                    onBLEWriteDataListener.onWriteDataFailure(exception);
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
                    byte[] sendValue = BLEByteUtil.getSubbytes(data, currentPosition * MAX_BYTES, sendLength);
                    BLELogUtil.i(TAG, String.format("position=%d,%s", currentPosition, BLEByteUtil.bytesToHexString(sendValue)));
                    if (!bluetoothGattCharacteristic.setValue(sendValue)) {
                        BLELogUtil.e(TAG, "writeOneSet setValue failure");
                        onBLEWriteDataListener.onWriteDataFailure(new BLEException(BLEException.WRITE_DATA_FAILURE));
                        return;
                    }
                    if (!gatt.writeCharacteristic(bluetoothGattCharacteristic)) {
                        BLELogUtil.e(TAG, "writeOneSet writeCharacteristic failure");
                        onBLEWriteDataListener.onWriteDataFailure(new BLEException(BLEException.WRITE_DATA_FAILURE));
                        return;
                    }
                } catch (Exception e) {
                    BLELogUtil.e(TAG, "writeOneSet e:" + e.getMessage());
                }
            }
        }).start();
    }

    public OnBLEWriteDataListener getOnBLEWriteDataListener() {
        return onBLEWriteDataListener;
    }

    public void setOnBLEWriteDataListener(OnBLEWriteDataListener onBLEWriteDataListener) {
        this.onBLEWriteDataListener = onBLEWriteDataListener;
    }
}
