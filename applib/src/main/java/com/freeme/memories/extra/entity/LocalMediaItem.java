package com.freeme.memories.extra.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Path;

import com.freeme.memories.base.IMemoriesApp;
import com.freeme.memories.extra.bitmappool.ImageCacheRequest;
import com.freeme.memories.pool.ThreadPool;
import com.freeme.memories.pool.ThreadPool.JobContext;
import com.freeme.memories.extra.utils.DecodeUtils;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-22.
 */
public class LocalMediaItem extends MediaObject {

    @Override
    public ThreadPool.Job<Bitmap> requestImage(int type) {
        return null;
    }

    @Override
    public ThreadPool.Job<BitmapRegionDecoder> requestLargeImage() {
        return null;
    }

    public static class LocalImageRequest extends ImageCacheRequest {
        private String mLocalFilePath;

        LocalImageRequest(IMemoriesApp application, Path path, long timeModified,
                int type, String localFilePath) {
            super(application, path, timeModified, type, getTargetSize(type));
            mLocalFilePath = localFilePath;
        }

        @Override
        public Bitmap onDecodeOriginal(JobContext jc, final int type) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            int targetSize = getTargetSize(type);

            // try to decode from JPEG EXIF
//            if (type == Global.TYPE_MICROTHUMBNAIL) {
//                ExifInterface exif = new ExifInterface();
//                byte[] thumbData = null;
//                try {
//                    exif.readExif(mLocalFilePath);
//                    thumbData = exif.getThumbnail();
//                } catch (FileNotFoundException e) {
//                    Log.w(TAG, "failed to find file to read thumbnail: " + mLocalFilePath);
//                } catch (IOException e) {
//                    Log.w(TAG, "failed to get thumbnail from: " + mLocalFilePath);
//                }
//                if (thumbData != null) {
//                    Bitmap bitmap = DecodeUtils.decodeIfBigEnough(
//                            jc, thumbData, options, targetSize);
//                    if (bitmap != null) return bitmap;
//                }
//            }

            return DecodeUtils.decodeThumbnail(jc, mLocalFilePath, options, targetSize, type);
        }
    }
}
