package com.sjl.blelibrary;

/**
 * BLEException
 *
 * @author SJL
 * @date 2017/5/3
 */

public class BLEException {
    public static final String SCAN_TIMEOUT = "扫描超时";
    public static final String SCAN_FAILURE = "扫描失败";
    public static final String CONNECT_FAILURE = "连接失败";
    public static final String DISCONNECT = "断开连接";
    public static final String CONNECT_STATE_CHANGE = "连接数上限";
    public static final String WRITE_DESCRIPTOR_FAILURE = "写特征失败";
    public static final String WRITE_DATA_CHECK_FAILURE = "验证写数据特征失败";
    public static final String WRITE_DATA_FAILURE = "写数据失败";
    public static final String RECEIVE_DATA_FAILURE = "接收数据异常";

    private String message;

    public BLEException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
