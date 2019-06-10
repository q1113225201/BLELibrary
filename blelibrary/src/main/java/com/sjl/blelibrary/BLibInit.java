package com.sjl.blelibrary;

import android.app.Application;

import com.sjl.blelibrary.constant.BLibCode;

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

    public void init(Application application) {
        BLibInit.application = application;
        //初始化错误码列表
        BLibCode.init(application);
    }
}
