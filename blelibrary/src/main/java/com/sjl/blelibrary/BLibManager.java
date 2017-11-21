package com.sjl.blelibrary;

import com.sjl.blelibrary.core.BLibCoreManager;

/**
 * BLibManager
 *
 * @author SJL
 * @date 2017/11/21
 */

public class BLibManager {
    private static final String TAG = "BLibManager";
    private String uuidDescService = null;
    private String uuidDescCharacteristic = null;
    private String uuidDesc = "00002902-0000-1000-8000-00805f9b34fb";
    private String uuidWriteService = null;
    private String uuidWriteCharacteristics = null;
    private static BLibManager bLibManager;

    public static BLibManager getInstance() {
        if (bLibManager == null) {
            synchronized (TAG) {
                if (bLibManager == null) {
                    bLibManager = new BLibManager();
                }
            }
        }
        return bLibManager;
    }

    private BLibCoreManager bLibCoreManager;

    private BLibManager() {
        bLibCoreManager = BLibCoreManager.getInstance();
    }

    public void init() {

    }
}
