package com.sjl.blelibrary.constant;

import android.content.Context;

import com.sjl.blelibrary.R;

import java.util.HashMap;
import java.util.Map;

/**
 * BLibCode
 *
 * @author 林zero
 * @date 2017/11/20
 */

public class BLibCode {
    public static final int ER_DISCONNECT = -1000;//断开连接
    //扫描的错误
    public static final int ER_DEVICE_NOT_FOUND = -1100;//未发现设备
    //连接时的错误
    public static final int ER_CONNECTED = -1200;//连接错误
    public static final int ER_CONNECTED_MAX = -1201;//蓝牙连接数上限
    public static final int ER_GATT_CONN_TIMEOUT = -1202;//GATT连接超时
    public static final int ER_GATT_NO_RESOURCES = -1203;//设备主动断开连接
    public static final int ER_DISCONNECT_BY_DEVICE = -1204;//设备主动断开连接
    //写描述符的错误
    public static final int ER_WRITE_DESC = -1300;//写描述符非法
    public static final int ER_WRITE_DESC_GET_SERVICE = -1301;//获取写描述符服务错误
    public static final int ER_WRITE_DESC_GET_CHARACTERISTIC = -1302;//获取写描述符特性错误
    public static final int ER_WRITE_DESC_ENABLE_NOTIFICATION = -1303;//写描述符时使能通知错误
    public static final int ER_WRITE_DESC_GET_DESC = -1304;//获取描述符错误
    public static final int ER_WRITE_DESC_WRITE_DESC = -1305;//写描述符错误
    public static final int ER_WRITE_DESC_CALLBACK = -1306;//写描述符回调失败
    //写数据的错误
    public static final int ER_WRITE_DATA_GET_SERVICE = -1401;//获取写数据服务错误
    public static final int ER_WRITE_DATA_GET_CHARACTERISTIC = -1402;//获取写数据特性错误
    public static final int ER_WRITE_DATA_WRITE_DATA = -1403;//写数据错误
    public static final int ER_WRITE_DATA_WRITE_CHARACTERISTIC = -1403;//写特征值错误
    public static final int ER_WRITE_DATA_CALLBACK = -1405;//写数据回调失败
    //广告错误
    public static final int ER_ADVERTISE_DATA_TOO_LARGE = -1500;//广告数据超过31位
    public static final int ER_ADVERTISE_TOO_MANY_ADVERTISERS = -1501;//无广告实例
    public static final int ER_ADVERTISE_ALREADY_STARTED = -1502;//广告已开始
    public static final int ER_ADVERTISE_INTERNAL_ERROR = -1503;//内部错误
    public static final int ER_ADVERTISE_FEATURE_UNSUPPORTED = -1504;//平台不支持该功能

    private static Map<Integer, String> erMap = null;
    private static Map<Integer, Integer> codeMap = new HashMap<>();

    static {
        codeMap.put(8, BLibCode.ER_GATT_CONN_TIMEOUT);
        codeMap.put(19, BLibCode.ER_DISCONNECT_BY_DEVICE);
        codeMap.put(128, BLibCode.ER_GATT_NO_RESOURCES);
        codeMap.put(133, BLibCode.ER_CONNECTED_MAX);
    }

    public static void init(Context context) {
        erMap = new HashMap<>();
        erMap.put(ER_DISCONNECT, context.getString(R.string.ER_DISCONNECT));

        erMap.put(ER_DEVICE_NOT_FOUND, context.getString(R.string.ER_DEVICE_NOT_FOUND));

        erMap.put(ER_CONNECTED, context.getString(R.string.ER_CONNECTED));
        erMap.put(ER_CONNECTED_MAX, context.getString(R.string.ER_CONNECTED_MAX));
        erMap.put(ER_GATT_CONN_TIMEOUT, context.getString(R.string.ER_GATT_CONN_TIMEOUT));
        erMap.put(ER_GATT_NO_RESOURCES, context.getString(R.string.ER_GATT_NO_RESOURCES));
        erMap.put(ER_DISCONNECT_BY_DEVICE, context.getString(R.string.ER_DISCONNECT_BY_DEVICE));

        erMap.put(ER_WRITE_DESC, context.getString(R.string.ER_WRITE_DESC));
        erMap.put(ER_WRITE_DESC_GET_SERVICE, context.getString(R.string.ER_WRITE_DESC_GET_SERVICE));
        erMap.put(ER_WRITE_DESC_GET_CHARACTERISTIC, context.getString(R.string.ER_WRITE_DESC_GET_CHARACTERISTIC));
        erMap.put(ER_WRITE_DESC_ENABLE_NOTIFICATION, context.getString(R.string.ER_WRITE_DESC_ENABLE_NOTIFICATION));
        erMap.put(ER_WRITE_DESC_GET_DESC, context.getString(R.string.ER_WRITE_DESC_GET_DESC));
        erMap.put(ER_WRITE_DESC_WRITE_DESC, context.getString(R.string.ER_WRITE_DESC_WRITE_DESC));
        erMap.put(ER_WRITE_DESC_CALLBACK, context.getString(R.string.ER_WRITE_DESC_CALLBACK));
        erMap.put(ER_WRITE_DESC_CALLBACK, context.getString(R.string.ER_WRITE_DESC_CALLBACK));

        erMap.put(ER_WRITE_DATA_GET_SERVICE, context.getString(R.string.ER_WRITE_DATA_GET_SERVICE));
        erMap.put(ER_WRITE_DATA_GET_CHARACTERISTIC, context.getString(R.string.ER_WRITE_DATA_GET_CHARACTERISTIC));
        erMap.put(ER_WRITE_DATA_WRITE_DATA, context.getString(R.string.ER_WRITE_DATA_WRITE_DATA));
        erMap.put(ER_WRITE_DATA_WRITE_CHARACTERISTIC, context.getString(R.string.ER_WRITE_DATA_WRITE_CHARACTERISTIC));
        erMap.put(ER_WRITE_DATA_CALLBACK, context.getString(R.string.ER_WRITE_DATA_CALLBACK));

        erMap.put(ER_ADVERTISE_DATA_TOO_LARGE, context.getString(R.string.ER_ADVERTISE_DATA_TOO_LARGE));
        erMap.put(ER_ADVERTISE_TOO_MANY_ADVERTISERS, context.getString(R.string.ER_ADVERTISE_TOO_MANY_ADVERTISERS));
        erMap.put(ER_ADVERTISE_ALREADY_STARTED, context.getString(R.string.ER_ADVERTISE_ALREADY_STARTED));
        erMap.put(ER_ADVERTISE_INTERNAL_ERROR, context.getString(R.string.ER_ADVERTISE_INTERNAL_ERROR));
        erMap.put(ER_ADVERTISE_FEATURE_UNSUPPORTED, context.getString(R.string.ER_ADVERTISE_FEATURE_UNSUPPORTED));
    }

    /**
     * 获取错误信息
     *
     * @param code
     * @return
     */
    public static String getError(int code) {
        return erMap.get(code)==null?("error code "+code):erMap.get(code);
    }

    /**
     * 错误码匹配
     *
     * @param status
     * @return
     */
    public static int matchCode(int status) {
        Integer result = codeMap.get(status);
        result = result == null ? status : result;
        return result;
    }
}
