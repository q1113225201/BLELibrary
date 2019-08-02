package com.sjl.blelibrary.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BLibByteUtil
 *
 * @author 林zero
 * @date 2017/4/21
 */

public class BLibByteUtil {
    private static final String TAG = "BLibByteUtil";

    /**
     * 十六进制数组转换成空格分割字符串
     * @param src
     * @return
     */
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

    /**
     * 将以空格分割的每两位相连的指定进制字(10进制或16进制)符串转换成字节数组
     * @param src
     * @param radix
     * @return
     */
    public static byte[] radixStringToBytes(String src, int radix) {
        if(radix!=10&&radix!=16){
            throw new IllegalArgumentException("不合法参数");
        }
        if (src == null || src.length() <= 0) {
            return null;
        }
        String[] srcArr;
        try {
            srcArr = src.split(" ");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (srcArr == null || srcArr.length == 0) {
            return null;
        }
        for (int i = 0; i < srcArr.length; i++) {
            if (srcArr[i].length() > 2) {
                return null;
            }
            if (srcArr[i].length() == 1) {
                srcArr[i] = 0 + srcArr[i];
            }
            if (radix == 16 && !regexCheck(srcArr[i], "^[0-9a-fA-F]{2}$")) {
                return null;
            } else if (radix == 10 && !regexCheck(srcArr[i], "^[0-9]{2}$")) {
                return null;
            }
        }
        byte[] resultBytes = new byte[srcArr.length];
        for (int i = 0; i < srcArr.length; i++) {
            resultBytes[i] = (byte) Integer.parseInt(srcArr[i], radix);
        }
        return resultBytes;
    }
    /**
     * 截取一部分字节
     * @param bytes
     * @param start
     * @param len
     * @return
     */
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


    //正则通用验证
    public static boolean regexCheck(String res, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(res);
        return matcher.matches();
    }
}
