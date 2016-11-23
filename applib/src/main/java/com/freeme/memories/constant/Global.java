package com.freeme.memories.constant;

import android.provider.MediaStore.Files.FileColumns;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public class Global {
    // 回忆类型-最近一周
    public static final int MEMORIES_TYPE_LAST_WEEK = 0;
    public static final int MEMORIES_TYPE_LAST_YEAR_TODAY = 1;
    public static final int MEMORIES_TYPE_LAST_MONTH = 2;
    public static final int MEMORIES_TYPE_LOCATION = 3;

    public static final String FILE_PREX = "file://";

    public static final String EXTRA_MEMORY_ITEM = "memory_item";
    public static final String EXTRA_MEMORY_BUCKET = "memory_bucket";
    public static final String EXTRA_MEMORY_TYPE = "memory_type";

    public static final int MEDIA_TYPE_IMAGE = FileColumns.MEDIA_TYPE_IMAGE;
    public static final int MEDIA_TYPE_VIDEO = FileColumns.MEDIA_TYPE_VIDEO;

    public static final String SEPARATOR = " ~ ";
    public static String CLICK_BUCKETID_INDEX = "bucket_id";
    public static int LAST_WEEK_DAY_NUM = -7;       // 最近一周, 按天数算
    public static int LAST_MONTH_NUM = -3;       // 过去X个月, 按月数算
    public static int LAST_YEAR_NUM = -1;       // 去年今天, 按年数算

    public static final int TYPE_THUMBNAIL      = 1;
    public static final int TYPE_MICROTHUMBNAIL = 2;
    public static final String EXTRA_MEMORY_BUCKET_INDEX = "memory_bucket_index";
    public static final String EXTRA_PHOTO_ITEM_INDEX = "memory_photo_index";
}
