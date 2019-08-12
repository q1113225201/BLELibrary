package com.sjl.blelibrary.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Handler;

import com.sjl.blelibrary.util.BLibLogUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BLibGattPool
 *
 * @author 林zero
 * @date 2017/5/4
 */

public class BLibGattPool {
    private static final String TAG = "BLibGattPool";
    private Map<String, BluetoothGattItem> gattMap = new HashMap<>();
    private boolean terminalDelete = false;

    //定期删除缓冲池数据
    private Handler handler = new Handler();
    private int time = 1000 * 20;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            List<String> macList = new ArrayList<>();
            for (String macItem : gattMap.keySet()) {
                macList.add(macItem);
            }
            for (String macItem : macList) {
                if (System.currentTimeMillis() - gattMap.get(macItem).time > time) {
                    if (terminalDelete) {
                        BLibLogUtil.d(TAG, macItem + " timeout");
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

    public BLibGattPool() {
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
    public synchronized BLibGattCallback getBluetoothGattCallback(String mac) {
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
    public synchronized void setBluetoothGatt(String mac, BluetoothGatt bluetoothGatt, BLibGattCallback bleGattCallback) {
        BluetoothGattItem bluetoothGattItem = gattMap.get(mac);
        if (bluetoothGattItem == null) {
            bluetoothGattItem = new BluetoothGattItem();
        }
        bluetoothGattItem.time = System.currentTimeMillis();
        if (bluetoothGatt != null) {
            bluetoothGattItem.bluetoothGatt = bluetoothGatt;
        }
        if (bleGattCallback != null) {
            bluetoothGattItem.bleGattCallback = bleGattCallback;
        }
        gattMap.put(mac, bluetoothGattItem);
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
     */
    public synchronized void disconnectGatt(String mac) {
        BluetoothGatt bluetoothGatt = getBluetoothGatt(mac);
        BLibLogUtil.d(TAG, "disconnectGatt:" + mac + "," + (bluetoothGatt != null));
        removeBluetoothGatt(mac);
        removeDevice(mac);
        disconnectGatt(bluetoothGatt);
    }

    /**
     * 断开Gatt连接
     */
    public synchronized void disconnectGatt(BluetoothGatt bluetoothGatt) {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
        if (bluetoothGatt != null) {
            clearCacheDevice(bluetoothGatt);
        }
        if (bluetoothGatt != null) {
            removeBluetoothGatt(bluetoothGatt.getDevice().getAddress());
            removeDevice(bluetoothGatt.getDevice().getAddress());
            bluetoothGatt.close();
        }
    }

    /**
     * 删除已配对设备
     */
    private void removeDevice(String mac) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
                if (device.getAddress().equalsIgnoreCase(mac)) {
                    try {
                        Method m = device.getClass()
                                .getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                    } catch (Exception e) {
                        BLibLogUtil.e(TAG, e.getMessage());
                    }
                }
            }
        }
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
        BLibGattCallback bleGattCallback;
        long time;
    }
}
