package com.freeme.memories.base;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.freeme.memories.data.manager.IContentDataNotifier;
import com.freeme.memories.data.manager.MemoriesManager;
import com.freeme.memories.extra.bitmappool.ImageCacheService;
import com.freeme.memories.pool.ThreadPool;
import com.freeme.memories.utils.LogUtil;
import com.freeme.provider.GalleryDBManager;
import com.freeme.utils.AppUtil;

import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * ClassName: AppImpl
 * Description:
 * Author: connorlin
 * Date: Created on 2016/10/8.
 */
public class AppImpl implements IMemoriesApp {

    private ThreadPool mThreadPool;
    private Object mLock = new Object();
    private ImageCacheService mImageCacheService;

    private Context mContext = null;

    private static IMemoriesApp mApp;

    private HashMap<Uri, NotifyBroker> mNotifierMap =
            new HashMap<Uri, NotifyBroker>();
    // 首页按back键后重新进入并不会重新updateContent,在无照片时,会显示空白.
    // isShowEmpty用于记录是否显示emptyView
    public static boolean isShowEmpty = false;

    private static AppImpl instance = null;

    private AppImpl(){

    }

    public static AppImpl getInstance(){
        if(instance == null){
            instance = new AppImpl();
        }
        return instance;
    }

    public void init(Context context){
        if (!AppUtil.isInGalleryProcess(context)) {
            return;
        }
        mContext = context;
        //*/ Added by droi Linguanrong for freeme gallery db, 16-1-19
        GalleryDBManager.getInstance().initDB(context, "gallery.db");
        mApp = this;
        MemoriesManager.getInstance().init(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        context.registerReceiver(mReceiver, filter);
    }


    @Override
    public ImageCacheService getImageCacheService() {
        // This method may block on file I/O so a dedicated lock is needed here.
        synchronized (mLock) {
            if (mImageCacheService == null) {
                mImageCacheService = new ImageCacheService(getAndroidContext());
            }
            return mImageCacheService;
        }
    }

    @Override
    public synchronized ThreadPool getThreadPool() {
        if (mThreadPool == null) {
            mThreadPool = new ThreadPool();
        }
        return mThreadPool;
    }

    @Override
    public Context getAndroidContext() {
        return mContext;
    }

    @Override
    public Looper getMainLooper() {
        return Looper.getMainLooper();
    }

    @Override
    public ContentResolver getContentResolver() {
        return mContext.getContentResolver();
    }

    @Override
    public Resources getResources() {
        return mContext.getResources();
    }

    public static IMemoriesApp getApp() {
        return mApp;
    }

    public void registerChangeNotifier(Uri uri, IContentDataNotifier notifier) {
        NotifyBroker broker = null;
        synchronized (mNotifierMap) {
            broker = mNotifierMap.get(uri);
            if (broker == null) {
                broker = new NotifyBroker(new Handler());
                AppImpl.getApp().getContentResolver()
                        .registerContentObserver(uri, true, broker);
                mNotifierMap.put(uri, broker);
            }
        }
        broker.registerNotifier(notifier);
    }

    public void unRegisterChangeNotifier(Uri uri, IContentDataNotifier notifier) {
        NotifyBroker broker = null;
        synchronized (mNotifierMap) {
            broker = mNotifierMap.get(uri);
            if (broker != null) {
                broker.unRegisterNotifiere(notifier);
            }
        }
    }

    private static class NotifyBroker extends ContentObserver {
        private WeakHashMap<IContentDataNotifier, Object> mNotifiers =
                new WeakHashMap<IContentDataNotifier, Object>();

        public NotifyBroker(Handler handler) {
            super(handler);
        }

        public synchronized void registerNotifier(IContentDataNotifier notifier) {
            mNotifiers.put(notifier, null);
        }

        public synchronized void unRegisterNotifiere(IContentDataNotifier notifier) {
            mNotifiers.remove(notifier);
        }

        @Override
        public synchronized void onChange(boolean selfChange) {
            for (IContentDataNotifier notifier : mNotifiers.keySet()) {
                notifier.notifyContentChanged();
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d("Kathy", "Kathy - AppImpl - onReceive: intent = " + intent.toString());
            if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED) ||
                    intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                MemoriesManager.getInstance().loadMemories();
            }
        }
    };

    public void destory() {
        if(mContext != null){
            mContext.unregisterReceiver(mReceiver);
        }
    }
}
