package com.freeme.memory;

import android.app.Application;

import com.droi.adcontrol.AdControl;
import com.freeme.memories.base.AppImpl;
import com.freeme.memories.utils.LogUtil;
import com.freeme.updateself.update.UpdateMonitor;

/**
 * Created by Kathy on 16-11-18.
 */
public class MemoryApp extends Application {

    private static final String TAG = "MemoryApp";
    @Override
    public void onCreate() {
        super.onCreate();
        AppImpl.getInstance().init(this);

        /*Add for DroiAD*/
        try {
            LogUtil.d(TAG,"application oncreate");
            AdControl.Init(this)
                    .sync();
            AdControl.setDebugMode(true);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG,"sdk init error");
        }

        UpdateMonitor.Builder
                //*/ init UpdateMonitor
                .getInstance(this)
                //*/ register you Application to obsever
                .registerApplication(this)
                //*/ register you Application is Service or hasEnrtyActivity
                .setApplicationIsServices(false)
                //*/ default notify small icon, ifnot set use updateself_ic_notify_small
//                .setDefaultNotifyIcon(R.drawable.updateself_ic_notify_small)
                .complete();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppImpl.getInstance().destory();
    }
}
