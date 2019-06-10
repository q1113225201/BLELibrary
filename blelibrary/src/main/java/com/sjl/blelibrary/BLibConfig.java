package com.sjl.blelibrary;

import com.sjl.blelibrary.util.BLibLogUtil;

/**
 * BLibConfig
 *
 * @author 林zero
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
