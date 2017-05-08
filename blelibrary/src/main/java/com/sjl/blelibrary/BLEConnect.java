package com.sjl.blelibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;

import com.sjl.blelibrary.util.BLELogUtil;

/**
 * BLEConnect
 *
 * @author SJL
 * @date 2017/5/3
 */

public class BLEConnect {
    private static final String TAG = "BLEConnect";

    public BLEConnect() {
    }

    public BluetoothGatt connect(Context context,String mac,BluetoothGattCallback gattCallback){
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
        if (bluetoothDevice == null) {
            BLELogUtil.e(TAG, "connect getRemoteDevice failure");
            return null;
        }
        BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(context, false, gattCallback);
        if (bluetoothGatt == null) {
            BLELogUtil.e(TAG, "connect connectGatt failure");
            return null;
        }
        return bluetoothGatt;
    }
}
