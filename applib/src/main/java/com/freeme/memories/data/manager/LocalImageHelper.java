package com.freeme.memories.data.manager;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.freeme.memories.constant.Global;
import com.freeme.memories.data.entity.LocalImage;
import com.freeme.memories.pool.ThreadPool.JobContext;
import com.freeme.memories.utils.DateUtil;
import com.freeme.memories.utils.LogUtil;
import com.freeme.provider.AddressObject;
import com.freeme.provider.GalleryStore;
import com.freeme.provider.GalleryStore.MediaColumns;
import com.freeme.provider.GalleryStore.Images.ImageColumns;
import com.freeme.provider.GalleryStore.Video.VideoColumns;
import com.freeme.provider.GalleryStore.Files.FileColumns;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Description: 数据加载实现类
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public class LocalImageHelper {

    public static final String[] PROJECTION = {
            MediaColumns._ID,                   // 0
            MediaColumns.DATA,                  // 1
            MediaColumns.SIZE,                  // 2
            MediaColumns.DISPLAY_NAME,          // 3
            MediaColumns.TITLE,                 // 4
            MediaColumns.DATE_ADDED,            // 5
            MediaColumns.DATE_MODIFIED,         // 6
            MediaColumns.MIME_TYPE,             // 7
            MediaColumns.WIDTH,                 // 8
            MediaColumns.HEIGHT,                // 9

            ImageColumns.DESCRIPTION,           // 10
            ImageColumns.LATITUDE,              // 11
            ImageColumns.LONGITUDE,             // 12
            ImageColumns.DATE_TAKEN,            // 13
            ImageColumns.ORIENTATION,           // 14
            ImageColumns.MINI_THUMB_MAGIC,      // 15
            ImageColumns.BUCKET_ID,             // 16
            ImageColumns.BUCKET_DISPLAY_NAME,   // 17

            VideoColumns.DURATION,              // 18
            VideoColumns.ARTIST,                // 19
            VideoColumns.ALBUM,                 // 20
            VideoColumns.RESOLUTION,            // 21

            FileColumns.MEDIA_TYPE              // 22
    };

    private static final int INDEX_ID = 0;
    private static final int INDEX_DATA = 1;
    private static final int INDEX_SIZE = 2;
    private static final int INDEX_DISPLAY_NAME = 3;
    private static final int INDEX_TITLE = 4;
    private static final int INDEX_DATE_ADDED = 5;
    private static final int INDEX_DATE_MODIFIED = 6;
    private static final int INDEX_MIME_TYPE = 7;
    private static final int INDEX_WIDTH = 8;
    private static final int INDEX_HEIGHT = 9;

    private static final int INDEX_DESCRIPTION = 10;
    private static final int INDEX_LATITUDE = 11;
    private static final int INDEX_LONGITUDE = 12;
    private static final int INDEX_DATE_TAKEN = 13;
    private static final int INDEX_ORIENTATION = 14;
    private static final int INDEX_MINI_THUMB_MAGIC = 15;
    private static final int INDEX_BUCKET_ID = 16;
    private static final int INDEX_BUCKET_DISPLAY_NAME = 17;

    private static final int INDEX_DURATION = 18;
    private static final int INDEX_ARTIST = 19;
    private static final int INDEX_ALBUM = 20;
    private static final int INDEX_RESOLUTION = 21;
    private static final int INDEX_MEDIA_TYPE = 22;

    public static final String EXTERNAL_MEDIA = "external";
    //private static final String PURE_BUCKET_GROUP_BY = ") GROUP BY 1,(2";
    private static final String VIDEO_IMAGE_CLAUSE =
            "(" + MediaColumns.DATA + " not like " + "'%Screenshot%'" + ")"
                    + " AND " + "(" + MediaColumns.DATA + " not like '%截%'" + ")"
                    + " AND " + "(" + GalleryStore.Files.FileColumns.MEDIA_TYPE
                    + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + ")"
                    + " AND " + "(" + MediaColumns.SIZE
                    + ">" + 1024 * 200 + ")";

    public static ArrayList<LocalImage> loadImageFromFilesTable(
            JobContext jc, ContentResolver resolver, int type, AddressObject addressObject) {

        String where = null;
        switch (type) {
            case Global.MEMORIES_TYPE_LAST_WEEK:
                LogUtil.i("type MEMORIES_TYPE_LAST_WEEK");
                long lastWeek = DateUtil.getCurrentDateMidnightByOffset(Calendar.DATE,
                        Global.LAST_WEEK_DAY_NUM);
                where = ImageColumns.DATE_TAKEN + " > " + lastWeek / 1000;
                LogUtil.i("MEMORIES_TYPE_LAST_WEEK where = " + where);
                break;

            case Global.MEMORIES_TYPE_LAST_YEAR_TODAY:
                LogUtil.i("type MEMORIES_TYPE_LAST_YEAR_TODAY");
                long start = DateUtil.getCurrentDateMidnightByOffset(Calendar.YEAR,
                        Global.LAST_YEAR_NUM);
                long end = start + DateUtil.DAY_MILLIS;
                where = ImageColumns.DATE_TAKEN + ">" + start / 1000 + " AND "
                        + ImageColumns.DATE_TAKEN + "<" + end / 1000;
//                where = "(" + ImageColumns.DATE_TAKEN + " > " + start + ")";
                LogUtil.i("MEMORIES_TYPE_LAST_YEAR_TODAY where = " + where);
                break;

            case Global.MEMORIES_TYPE_LAST_MONTH:
                LogUtil.i("type MEMORIES_TYPE_LAST_MONTH");
                //去除最近一周
                long lastMonth = DateUtil.getCurrentDateMidnightByOffset(Calendar.MONTH,
                        Global.LAST_MONTH_NUM);
                long exceptlastWeek = DateUtil.getCurrentDateMidnightByOffset(Calendar.DATE,
                        Global.LAST_WEEK_DAY_NUM);
                where = ImageColumns.DATE_TAKEN + " > " + lastMonth / 1000 + " AND "
                        + ImageColumns.DATE_TAKEN + " < " + exceptlastWeek / 1000;
                LogUtil.i("MEMORIES_TYPE_LAST_MONTH where = " + where);
                break;

            case Global.MEMORIES_TYPE_LOCATION:
                LogUtil.i("type MEMORIES_TYPE_LOCATION - addressObject = " + addressObject
                        .toString());

                if (null != addressObject) {
                    where = ImageColumns.LATITUDE + " > 0 AND " + ImageColumns.LONGITUDE + "> 0 "
                            + "AND " + ImageColumns.COUNTRY + " = " + "'" + addressObject.getCountry() + "'" +
                            " AND " + ImageColumns.CITY + " = " + "'" +addressObject.getCity()+ "'";
                }
                LogUtil.i("MEMORIES_TYPE_LOCATION where = " + where);
                break;
        }

        String whereGroup;
        if (where == null) {
            whereGroup = VIDEO_IMAGE_CLAUSE;
        } else {
            whereGroup = VIDEO_IMAGE_CLAUSE + " AND (" + where + ") ";
        }
        LogUtil.i("end whereGroup = " + whereGroup);

        Uri uri = getFilesContentUri();
        Cursor cursor = resolver.query(uri, PROJECTION, whereGroup, null, "_size DESC LIMIT 30");
        LogUtil.i("cursor = " + cursor);
        if (cursor == null) {
            return null;
        }

        ArrayList<LocalImage> localImageList = new ArrayList<>();

        int typeBits = 0;
        typeBits |= (1 << FileColumns.MEDIA_TYPE_IMAGE);
        typeBits |= (1 << FileColumns.MEDIA_TYPE_VIDEO);
        try {
            while (cursor.moveToNext()) {
                if ((typeBits & (1 << cursor.getInt(INDEX_MEDIA_TYPE))) != 0) {
                    localImageList.add(buildImage(cursor));

                    if (jc.isCancelled()) {
                        return null;
                    }
                }
            }
        } finally {
            closeSilently(cursor);
        }

        return localImageList;
    }

    private static Uri getFilesContentUri() {
        return GalleryStore.Files.getContentUri(EXTERNAL_MEDIA);
    }

    private static LocalImage buildImage(Cursor cursor) {
        int mediaType = cursor.getInt(INDEX_MEDIA_TYPE);
        long id = cursor.getLong(INDEX_ID);
        Uri uri = null;
        switch (mediaType) {
            case FileColumns.MEDIA_TYPE_IMAGE:
                uri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" + id);
                break;

            case FileColumns.MEDIA_TYPE_VIDEO:
                uri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI + "/" + id);
                break;
        }

        return new LocalImage.Builder()
                .setId(id)
                .setData(cursor.getString(INDEX_DATA))
                .setSize(cursor.getInt(INDEX_SIZE))
                .setDisplay_name(cursor.getString(INDEX_DISPLAY_NAME))
                .setTitle(cursor.getString(INDEX_TITLE))
                .setDateAdded(cursor.getInt(INDEX_DATE_ADDED))
                .setDateModified(cursor.getInt(INDEX_DATE_MODIFIED))
                .setMimeType(cursor.getString(INDEX_MIME_TYPE))
                .setWidth(cursor.getInt(INDEX_WIDTH))
                .setHeight(cursor.getInt(INDEX_HEIGHT))
                .setDescription(cursor.getString(INDEX_DESCRIPTION))
                .setLatitude(cursor.getDouble(INDEX_LATITUDE))
                .setLongitude(cursor.getDouble(INDEX_LONGITUDE))
                .setDateTaken(cursor.getInt(INDEX_DATE_TAKEN))
                .setOrientation(cursor.getInt(INDEX_ORIENTATION))
                .setMiniThumbMagic(cursor.getInt(INDEX_MINI_THUMB_MAGIC))
                .setBucketId(cursor.getString(INDEX_BUCKET_ID))
                .setBucketDisplayName(cursor.getString(INDEX_BUCKET_DISPLAY_NAME))
                .setDuration(cursor.getInt(INDEX_DURATION))
                .setArtist(cursor.getString(INDEX_ARTIST))
                .setAlbum(cursor.getString(INDEX_ALBUM))
                .setResolution(cursor.getString(INDEX_RESOLUTION))
                .setMediaType(mediaType)
                .setUri(uri)
                .build();
    }

    public static void closeSilently(Cursor cursor) {
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable t) {
            LogUtil.i("fail to close" + t);
        }
    }
}
