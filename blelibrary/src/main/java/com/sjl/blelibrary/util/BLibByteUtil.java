package com.sjl.blelibrary.util;

import java.util.Locale;

/**
 * BLibByteUtil
 *
 * @author 林zero
 * @date 2017/4/21
 */

public class BLibByteUtil {

    //以十六进制字符输出字节数组到日志,每两位字符进行分隔
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            if (i == src.length - 1) {
                stringBuilder.append(hv.toUpperCase(Locale.US));
            } else {
                stringBuilder.append(hv.toUpperCase(Locale.US)).append(" ");
            }
        }
        return stringBuilder.toString();
    }

    //截取一部分字节
    public static byte[] subBytes(byte[] bytes, int start, int len) {
        if (bytes.length < len || len == 0) {
            return null;
        } else if (bytes.length == len) {
            return bytes;
        }
        byte[] bs = new byte[len];
        for (int i = 0; i < len; i++) {
            bs[i] = bytes[start++];
        }
        return bs;
    }
}
