package com.sjl.blelibrary.base;

import android.app.Application;

/**
 * BLELibraryInit
 *
 * @author SJL
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

    public void init(Application application) {
        BLibInit.application = application;
        //初始化错误码列表
        BLibCode.init(application);
    }
}
