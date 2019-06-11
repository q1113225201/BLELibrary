package com.sjl.blelibrary;

import android.app.Application;

import com.sjl.blelibrary.constant.BLibCode;
import com.sjl.blelibrary.util.BLibLogUtil;

/**
 * BLibInit
 *
 * @author 林zero
 * @date 2017/11/20
 */

public class BLibInit {
    private static final String TAG = "BLibInit";
    private static BLibInit bleLibraryInit;

    public static BLibInit getInstance() {
        if (bleLibraryInit == null) {
            synchronized (TAG) {
                if (bleLibraryInit == null) {
                    bleLibraryInit = new BLibInit();
                }
            }
        }
        return bleLibraryInit;
    }

    public static Application application;

    public BLibInit init(Application application) {
        BLibInit.application = application;
        //初始化错误码列表
        BLibCode.init(application);
        return bleLibraryInit;
    }

    //是否调试模式
    private boolean debug = false;

    public BLibInit setDebug(boolean debug) {
        this.debug = debug;
        //日志打印开关
        BLibLogUtil.DEBUG = debug;
        return bleLibraryInit;
    }

    public boolean isDebug() {
        return debug;
    }
}
