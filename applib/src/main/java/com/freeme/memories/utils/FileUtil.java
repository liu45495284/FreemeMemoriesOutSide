package com.freeme.memories.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public class FileUtil {

    /**
     * 描述：通过文件的本地地址从SD卡读取图片.
     *
     * @param file          the file
     * @param type          图片的处理类型（剪切或者缩放到指定大小，参考AbConstant类）
     *                      如果设置为原图，则后边参数无效，得到原图
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmapFromSD(File file, int type, int desiredWidth, int desiredHeight) {
        Bitmap bitmap = null;
        try {
            //SD卡是否存在
            if (!isCanUseSD()) {
                return null;
            }

            //文件是否存在
            if (!file.exists()) {
                return null;
            }

            //文件存在
            if (type == ImageUtil.CUTIMG) {
                //bitmap = ImageUtil.getCutBitmap(file, desiredWidth, desiredHeight);
            } else if (type == ImageUtil.SCALEIMG) {
                bitmap = ImageUtil.getScaleBitmap(file, desiredWidth, desiredHeight);
            } else {
                //bitmap = ImageUtil.getBitmap(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 描述：SD卡是否能用.
     *
     * @return true 可用,false不可用
     */
    public static boolean isCanUseSD() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
