package com.freeme.memories.data.manager;

import android.os.Handler;

import com.freeme.memories.base.BaseMemoriesManager;
import com.freeme.memories.base.IMemoriesApp;
import com.freeme.memories.data.bucket.MemoryBucket;
import com.freeme.memories.data.entity.LocalImage;
import com.freeme.memories.pool.Future;
import com.freeme.memories.pool.FutureListener;
import com.freeme.memories.pool.MemoriesLoader;
import com.freeme.provider.AddressObject;

import java.util.ArrayList;

/**
 * Description: 相册数据加载管理类
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public class MemoryBucketManager extends BaseMemoriesManager
        implements FutureListener<ArrayList<LocalImage>> {

    private final Handler mHandler;
    private IMemoriesApp mApp;
    private Future<ArrayList<LocalImage>> mLoadTask;
    private MemoryBucket mMemoryBucket;
    private int mMemoryType;
    private AddressObject mAddressObject;
    private int mDateTaken;

    public MemoryBucketManager(IMemoriesApp app, MemoryBucket bucket, int type, AddressObject
            addressObject, int dateTaken) {
        mApp = app;
        mMemoryBucket = bucket;
        mMemoryType = type;
        mHandler = new Handler(app.getMainLooper());
        mAddressObject = addressObject;
        mDateTaken = dateTaken;
    }

    public void loadImages() {
        cancelLoadTask();
        mLoadTask = mApp.getThreadPool().submit(
                new MemoriesLoader(mApp.getContentResolver(), mMemoryType, mAddressObject, mDateTaken), this);
    }

    private void cancelLoadTask() {
        if (mLoadTask != null) {
            mLoadTask.cancel();
        }
    }

    @Override
    public void onFutureDone(Future<ArrayList<LocalImage>> future) {
        if (mLoadTask != future) {
            return;
        }
        mMemoryBucket.setLocalImages(future.get(), mMemoryType);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //notifyContentChanged();
                MemoriesManager.getInstance().addBucket(mMemoryBucket);
            }
        });
    }
}
