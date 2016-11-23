package com.freeme.memories.pool;

import android.content.ContentResolver;

import com.freeme.memories.data.entity.LocalImage;
import com.freeme.memories.data.manager.LocalImageHelper;
import com.freeme.memories.utils.LogUtil;
import com.freeme.provider.AddressObject;

import java.util.ArrayList;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public class MemoriesLoader implements ThreadPool.Job<ArrayList<LocalImage>> {

    private ContentResolver mContentResolver;
    private int mLoadType;
    private AddressObject mAddressObject;

    public MemoriesLoader(ContentResolver resolver) {
        mContentResolver = resolver;
    }

    public MemoriesLoader(ContentResolver resolver, int type, AddressObject addressObject) {
        mContentResolver = resolver;
        mLoadType = type;
        mAddressObject = addressObject;
    }

    @Override
    public ArrayList<LocalImage> run(ThreadPool.JobContext jc) {
        LogUtil.i("MemoriesLoader running...");
        return LocalImageHelper.loadImageFromFilesTable(jc, mContentResolver, mLoadType, mAddressObject);
    }
}
