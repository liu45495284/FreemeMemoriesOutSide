package com.freeme.memories.extra.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;

import com.freeme.memories.constant.Global;
import com.freeme.memories.extra.bitmappool.BytesBufferPool;
import com.freeme.memories.pool.ThreadPool;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-22.
 */
public abstract class MediaObject {

    private static int sMicrothumbnailTargetSize = 200;
    private static int sThumbnailTargetSize = 640;

    private static final int BYTESBUFFE_POOL_SIZE = 4;
    private static final int BYTESBUFFER_SIZE     = 200 * 1024;

    private static final BytesBufferPool sMicroThumbBufferPool     =
            new BytesBufferPool(BYTESBUFFE_POOL_SIZE, BYTESBUFFER_SIZE);

    public static int getTargetSize(int type) {
        switch (type) {
            case Global.TYPE_THUMBNAIL:
                return sThumbnailTargetSize;

            case Global.TYPE_MICROTHUMBNAIL:
                return sMicrothumbnailTargetSize;

            default:
                throw new RuntimeException(
                        "should only request thumb/microthumb from cache");
        }
    }

    public abstract ThreadPool.Job<Bitmap> requestImage(int type);

    public abstract ThreadPool.Job<BitmapRegionDecoder> requestLargeImage();

    public static BytesBufferPool getBytesBufferPool() {
        return sMicroThumbBufferPool;
    }
}
