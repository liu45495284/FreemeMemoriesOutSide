package com.freeme.memories.base;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Looper;

import com.freeme.memories.data.manager.IContentDataNotifier;
import com.freeme.memories.extra.bitmappool.ImageCacheService;
import com.freeme.memories.pool.ThreadPool;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public interface IMemoriesApp {

    ImageCacheService getImageCacheService();

    ThreadPool getThreadPool();

    Context getAndroidContext();

    Looper getMainLooper();

    ContentResolver getContentResolver();

    Resources getResources();

    void registerChangeNotifier(Uri uri, IContentDataNotifier notifier);

    void unRegisterChangeNotifier(Uri uri, IContentDataNotifier notifier);
}
