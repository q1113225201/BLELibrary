package com.sjl.blelibrary.core;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;

import com.sjl.blelibrary.constant.BLibCode;
import com.sjl.blelibrary.util.BLibLogUtil;

/**
 * BLibAdvertiser
 *
 * @author 林zero
 * @date 2019/6/10
 */
@SuppressLint("NewApi")
public class BLibAdvertiser {
    private static final String TAG = "BLibAdvertiser";

    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private OnBLEAdvertisingListener onBLEAdvertisingListener;
    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            BLibLogUtil.i(TAG, "onStartSuccess:" + settingsInEffect);
            if (onBLEAdvertisingListener != null) {
                onBLEAdvertisingListener.onStartSuccess(settingsInEffect);
            }
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            int error = BLibCode.ER_ADVERTISE_INTERNAL_ERROR;
            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                error = BLibCode.ER_ADVERTISE_DATA_TOO_LARGE;
                BLibLogUtil.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                error = BLibCode.ER_ADVERTISE_TOO_MANY_ADVERTISERS;
                BLibLogUtil.e(TAG, "Failed to start advertising because no advertising instance is available.");
            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                error = BLibCode.ER_ADVERTISE_ALREADY_STARTED;
                BLibLogUtil.e(TAG, "Failed to start advertising as the advertising is already started");
            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                error = BLibCode.ER_ADVERTISE_INTERNAL_ERROR;
                BLibLogUtil.e(TAG, "Operation failed due to an internal error");
            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                error = BLibCode.ER_ADVERTISE_FEATURE_UNSUPPORTED;
                BLibLogUtil.e(TAG, "This feature is not supported on this platform");
            }
            if (onBLEAdvertisingListener != null) {
                onBLEAdvertisingListener.onStartFailure(error);
            }
        }
    };

    public BLibAdvertiser(OnBLEAdvertisingListener onBLEAdvertisingListener) {
        this.onBLEAdvertisingListener = onBLEAdvertisingListener;
        init();
    }

    private void init() {
        if (bluetoothLeAdvertiser == null) {
            bluetoothLeAdvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        }
    }

    /**
     * 开始广告
     * @param settings
     * @param advertiseData
     */
    public void startAdvertising(AdvertiseSettings settings, AdvertiseData advertiseData) {
        startAdvertising(settings, advertiseData, null);
    }

    /**
     * 开始广告
     * @param settings
     * @param advertiseData
     * @param scanResponse
     */
    public void startAdvertising(AdvertiseSettings settings, AdvertiseData advertiseData, AdvertiseData scanResponse) {
        init();
        bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, scanResponse, advertiseCallback);
    }

    /**
     * 停止广告
     */
    public void stopAdvertising() {
        init();
        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
    }

    public interface OnBLEAdvertisingListener {
        void onStartSuccess(AdvertiseSettings settingsInEffect);

        void onStartFailure(int errorCode);
    }
}
