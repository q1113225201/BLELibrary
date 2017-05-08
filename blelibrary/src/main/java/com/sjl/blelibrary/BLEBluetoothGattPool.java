package com.sjl.blelibrary;

import android.bluetooth.BluetoothGatt;
import android.os.Handler;

import com.sjl.blelibrary.util.BLELogUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BLEBluetoothGattPool
 *
 * @author SJL
 * @date 2017/5/4
 */

public class BLEBluetoothGattPool {
    private static final String TAG = "BLEBluetoothGattPool";
    private Map<String, BluetoothGattItem> gattMap = new HashMap<>();
    private boolean terminalDelete = false;

    //定期删除缓冲池数据
    private Handler handler = new Handler();
    private int time = 1000 * 20;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            BLELogUtil.e(TAG, "runnable");
            List<String> macList = new ArrayList<>();
            for (String macItem : gattMap.keySet()) {
                macList.add(macItem);

            }
            for (String macItem : macList) {
                if (System.currentTimeMillis() - gattMap.get(macItem).time > time) {
                    BLELogUtil.e(TAG, macItem + " timeout");
                    if (terminalDelete) {
                        disconnectGatt(macItem);
                    }
                }
            }
            /*Iterator<String> macs = gattMap.keySet().iterator();
            while (macs.hasNext()) {
                String currentMac = macs.next();
                if (System.currentTimeMillis() - gattMap.get(currentMac).time > TIME) {
                    BLELogUtil.e(TAG, currentMac + " timeout");
                    disconnectGatt(currentMac);
                    //避免java.util.ConcurrentModificationException
                    handler.postDelayed(runnable, 50);
                    return;
                }
            }*/
            initPool();
        }
    };

    public BLEBluetoothGattPool() {
        initPool();
    }

    private void initPool() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, time);
    }

    /**
     * 获取Gatt
     *
     * @param mac
     * @return
     */
    public synchronized BluetoothGatt getBluetoothGatt(String mac) {
        if (isConnect(mac)) {
            setBluetoothGatt(mac, gattMap.get(mac).bluetoothGatt, gattMap.get(mac).bleGattCallback);
            return gattMap.get(mac).bluetoothGatt;
        }
        return null;
    }

    /**
     * 获取Gatt回调
     *
     * @param mac
     * @return
     */
    public synchronized BLEGattCallback getBluetoothGattCallback(String mac) {
        if (isConnect(mac)) {
            setBluetoothGatt(mac, gattMap.get(mac).bluetoothGatt, gattMap.get(mac).bleGattCallback);
            return gattMap.get(mac).bleGattCallback;
        }
        return null;
    }

    /**
     * 设置缓存池数据
     *
     * @param mac
     * @param bluetoothGatt
     */
    public synchronized void setBluetoothGatt(String mac, BluetoothGatt bluetoothGatt, BLEGattCallback bleGattCallback) {
        gattMap.put(mac, new BluetoothGattItem(bluetoothGatt, bleGattCallback, System.currentTimeMillis()));
    }

    /**
     * 删除Gatt
     *
     * @param mac
     */
    public synchronized void removeBluetoothGatt(String mac) {
        if (isConnect(mac)) {
            gattMap.remove(mac);
        }
    }

    /**
     * 是否已连接
     *
     * @param mac
     * @return
     */
    public boolean isConnect(String mac) {
        if (gattMap.containsKey(mac)) {
            return true;
        }
        return false;
    }

    /**
     * 断开Gatt连接
     *
     * @param mac
     */
    public synchronized void disconnectGatt(String mac) {
        BLELogUtil.e(TAG, "disconnectGatt:" + mac);
        BluetoothGatt bluetoothGatt = getBluetoothGatt(mac);
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
        if (bluetoothGatt != null) {
            clearCacheDevice(bluetoothGatt);
        }
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        removeBluetoothGatt(mac);
    }

    /**
     * 清除Gatt缓存
     */
    private void clearCacheDevice(BluetoothGatt bluetoothGatt) {
        try {
            Method method = BluetoothGatt.class.getMethod("refresh");
            method.invoke(bluetoothGatt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setEnableTerminalDelete(boolean enable) {
        this.terminalDelete = enable;
    }

    //每个连接的Gatt
    class BluetoothGattItem {
        BluetoothGatt bluetoothGatt;
        BLEGattCallback bleGattCallback;
        long time;

        public BluetoothGattItem(BluetoothGatt bluetoothGatt, BLEGattCallback bleGattCallback, long time) {
            this.bluetoothGatt = bluetoothGatt;
            this.bleGattCallback = bleGattCallback;
            this.time = time;
        }
    }
}
