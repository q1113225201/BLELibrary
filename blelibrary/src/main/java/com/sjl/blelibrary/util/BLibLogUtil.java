package com.sjl.blelibrary.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * BLibLogUtil
 *
 * @author 林zero
 * @date 2017/3/24
 */
public class BLibLogUtil {
    private static final String TAG = "BLibLogUtil";
    public static Boolean DEBUG = false; // 日志控制总开关 true 在开发工具后台打印日志 false 不打印日志
    private static char LOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息

    public static void w(Object msg) { // 警告信息
        log(TAG, msg.toString(), 'w');
    }

    public static void e(Object msg) { // 错误信息
        log(TAG, msg.toString(), 'e');
    }

    public static void d(Object msg) {// 调试信息
        log(TAG, msg.toString(), 'd');
    }

    public static void i(Object msg) {//
        log(TAG, msg.toString(), 'i');
    }

    public static void v(Object msg) {
        log(TAG, msg.toString(), 'v');
    }

    public static void w(String text) {
        log(TAG, text, 'w');
    }

    public static void e(String text) {
        log(TAG, text, 'e');
    }

    public static void d(String text) {
        log(TAG, text, 'd');
    }

    public static void i(String text) {
        log(TAG, text, 'i');
    }

    public static void v(String text) {
        log(TAG, text, 'v');
    }

    public static void w(String tag, Object msg) { // 警告信息
        log(tag, msg.toString(), 'w');
    }

    public static void e(String tag, Object msg) { // 错误信息
        log(tag, msg.toString(), 'e');
    }

    public static void d(String tag, Object msg) {// 调试信息
        log(tag, msg.toString(), 'd');
    }

    public static void i(String tag, Object msg) {//
        log(tag, msg.toString(), 'i');
    }

    public static void v(String tag, Object msg) {
        log(tag, msg.toString(), 'v');
    }

    public static void w(String tag, String text) {
        log(tag, text, 'w');
    }

    public static void e(String tag, String text) {
        log(tag, text, 'e');
    }

    public static void d(String tag, String text) {
        log(tag, text, 'd');
    }

    public static void i(String tag, String text) {
        log(tag, text, 'i');
    }

    public static void v(String tag, String text) {
        log(tag, text, 'v');
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     * @return void
     * @since v 1.0
     */
    private static void log(String tag, String msg, char level) {
        if (DEBUG) {
            if ('e' == level && 'v' == LOG_TYPE) { // 输出错误信息
                Log.e(tag, msg);
            } else if ('w' == level && 'v' == LOG_TYPE) {
                Log.w(tag, msg);
            } else if ('d' == level && 'v' == LOG_TYPE) {
                Log.d(tag, msg);
            } else if ('i' == level && 'v' == LOG_TYPE) {
                Log.i(tag, msg);
            } else {
                Log.d(tag, msg);
            }
        }
    }
}
