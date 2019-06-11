package com.sjl.blelibrary.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;

import com.sjl.blelibrary.util.BLibLogUtil;

/**
 * BLibConnect
 *
 * @author 林zero
 * @date 2017/5/3
 */

public class BLibConnect {
    private static final String TAG = "BLibConnect";

    public BLibConnect() {
    }

    public BluetoothGatt connect(Context context,String mac,BluetoothGattCallback gattCallback){
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
        if (bluetoothDevice == null) {
            BLibLogUtil.d(TAG, "connect getRemoteDevice failure");
            return null;
        }
        BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(context, false, gattCallback);
        if (bluetoothGatt == null) {
            BLibLogUtil.d(TAG, "connect connectGatt failure");
            return null;
        }
        return bluetoothGatt;
    }
}
