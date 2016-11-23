package com.freeme.memory;

import android.app.Application;

import com.freeme.memories.base.AppImpl;
import com.freeme.updateself.update.UpdateMonitor;

/**
 * Created by server on 16-11-18.
 */
public class MemoryApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        AppImpl.getInstance().init(this);
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
