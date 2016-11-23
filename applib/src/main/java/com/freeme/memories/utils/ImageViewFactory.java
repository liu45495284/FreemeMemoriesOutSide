package com.freeme.memories.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.freeme.memories.R;

import java.io.File;

/**
 * ClassName: ImageViewFactory
 * Description:
 * Author: connorlin
 * Date: Created on 2016-9-1.
 */
public class ImageViewFactory {

    public static ImageView getImageView(Context context, String url) {
        ImageView imageView = (ImageView) LayoutInflater.from(context)
                .inflate(R.layout.imageview_factory, null);

//        ImageLoadManager.getInstance().displayImage(url, imageView);
//        imageView.setImageBitmap(getExistBitmap(url));
//        imageView.setTag(url);


        return imageView;
    }

    public static ImageView getImageView(Context context) {
        ImageView imageView = (ImageView) LayoutInflater.from(context)
                .inflate(R.layout.imageview_factory, null);

        return imageView;
    }

    private static Bitmap getExistBitmap(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        return ImageUtil.getScaleBitmap(file, 320, 480);
    }
}

