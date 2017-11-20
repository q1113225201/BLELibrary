package com.sjl.blelibrarydemo;

import android.app.Application;

import com.sjl.blelibrary.base.BLibInit;

/**
 * App
 *
 * @author SJL
 * @date 2017/11/20
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BLibInit.getInstance().init(this);
    }
}
