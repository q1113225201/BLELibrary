package com.sjl.blelibrary.util;

import android.annotation.SuppressLint;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;

/**
 * BLibUtil
 *
 * @author 林zero
 * @date 2019/6/11
 */
@SuppressLint("NewApi")
public class BLibUtil {
    private static final String TAG = "BLibUtil";

    /**
     * 构建广告设置
     * @param advertiseMode {@link AdvertiseSettings.Builder#setAdvertiseMode}
     * @param txPowerLevel {@link AdvertiseSettings.Builder#setTxPowerLevel}
     * @param connectable
     * @param timeout 0<=timeout<=180000
     * @return
     */
    public static AdvertiseSettings buildAdvertiseSettings(int advertiseMode, int txPowerLevel, boolean connectable, int timeout) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(advertiseMode);
        builder.setTxPowerLevel(txPowerLevel);
        builder.setConnectable(connectable);
        builder.setTimeout(timeout);
        AdvertiseSettings advertiseSettings = builder.build();
        BLibLogUtil.d(TAG, advertiseSettings);
        return advertiseSettings;
    }
}
