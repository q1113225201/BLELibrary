package com.sjl.blelibrary.base;

import com.sjl.blelibrary.util.BLibLogUtil;

/**
 * BLELibraryConfig
 *
 * @author SJL
 * @date 2017/11/20
 */

public class BLibConfig {
    private static BLibConfig bleLibraryConfig = new BLibConfig();
    //是否调试模式
    private static boolean debug = false;

    public static boolean isDebug() {
        return debug;
    }

    public static BLibConfig setDebug(boolean debug) {
        BLibConfig.debug = debug;
        //日志打印开关
        BLibLogUtil.DEBUG = debug;
        return bleLibraryConfig;
    }
}
