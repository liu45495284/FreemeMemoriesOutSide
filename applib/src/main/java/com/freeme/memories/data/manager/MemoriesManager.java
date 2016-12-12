package com.freeme.memories.data.manager;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.format.DateFormat;
import android.util.Log;

import com.freeme.memories.R;
import com.freeme.memories.base.AppImpl;
import com.freeme.memories.base.IMemoriesApp;
import com.freeme.memories.constant.Global;
import com.freeme.memories.data.bucket.MemoryBucket;
import com.freeme.memories.pool.Future;
import com.freeme.memories.utils.AppUtil;
import com.freeme.memories.utils.DateUtil;
import com.freeme.memories.utils.LogUtil;
import com.freeme.provider.AddressObject;
import com.freeme.provider.GalleryStore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Description: 全局唯一回忆相册的管理类
 * Author: connorlin
 * Date: Created on 2016-10-21.
 */
public class MemoriesManager implements IContentDataNotifier {
    public static final String TAG = "MemoriesManager";

    private Map<Integer, MemoryBucket> mMemoryBucketList = new HashMap<>();
    private IMemoriesApp mIMemoriesApp;
    private Resources mRes;
    private Future<List<MemoryBucket>> mLoadTask;
    private static long sVersionSerial = 0;
    private HashMap<Uri, DataNotifyBroker> mNotifierMap =
            new HashMap<Uri, DataNotifyBroker>();

    private DataNotifyBroker mBroker = null;
    private ContentResolver mResolver;

    public static synchronized long nextVersionNumber() {
        return ++sVersionSerial;
    }

    public static MemoriesManager getInstance() {
        return Singleton.instance;
    }

    public void init(IMemoriesApp app) {
        mResolver = app.getContentResolver();
        mIMemoriesApp = app;
        mRes = app.getResources();
        AppImpl.getApp().registerChangeNotifier(GalleryStore.Files.getContentUri(LocalImageHelper
                .EXTERNAL_MEDIA), this);
        //loadMemories();
    }

    public void loadMemories() {
        mMemoryBucketList.clear();
        int[] random = AppUtil.noRepeatRandom(5);
        for (int index : random) {
            LogUtil.i("index = " + index);
            loadMemory(index);
        }
    }

    private void loadMemory(int type) {
        switch (type) {
            case Global.MEMORIES_TYPE_LAST_WEEK:
                loadLastWeekMemory();
                break;

            case Global.MEMORIES_TYPE_LAST_YEAR_TODAY:
                loadLastYearTodayMemory();
                break;

            case Global.MEMORIES_TYPE_LAST_MONTH:
                loadLastMonthMemory();
                break;

            case Global.MEMORIES_TYPE_LOCATION:
                loadLocationMemory();
                break;

            case Global.MEMORIES_TYPE_DATE:
                loadDateMemory();
                break;
        }
    }

    private void loadLastWeekMemory() {
        // 最近一周
        String today = DateUtil.getCurrentDate(DateUtil.DATE_FORMAT_YMD);
        String start = DateUtil.getCurrentDateByOffset(DateUtil.DATE_FORMAT_YMD, Calendar.DATE,
                Global.LAST_WEEK_DAY_NUM);
        MemoryBucket lastweek = new MemoryBucket.Builder()
                .setDescription(mRes.getString(R.string.memory_last_week_description))
                .setSummary(start + Global.SEPARATOR + today)
                .setMemoryType(Global.MEMORIES_TYPE_LAST_WEEK)
                .build();

        MemoryBucketManager bucketManager =
                new MemoryBucketManager(mIMemoriesApp, lastweek, Global.MEMORIES_TYPE_LAST_WEEK,
                        null, 0);
        bucketManager.loadImages();

    }

    private void loadLastYearTodayMemory() {
        // 去年今天
        String date = DateUtil.getCurrentDateByOffset(DateUtil.DATE_FORMAT_YMD, Calendar.YEAR, -1);
        MemoryBucket lastYearToday = new MemoryBucket.Builder()
                .setDescription(mRes.getString(R.string.memory_last_year_today_description))
                .setSummary(date)
                .setMemoryType(Global.MEMORIES_TYPE_LAST_YEAR_TODAY)
                .build();

        MemoryBucketManager bucketManager = new MemoryBucketManager(mIMemoriesApp, lastYearToday,
                Global.MEMORIES_TYPE_LAST_YEAR_TODAY, null, 0);
        bucketManager.loadImages();

    }

    private void loadLastMonthMemory() {
        // 过去三个月
        String today = DateUtil.getCurrentDate(DateUtil.DATE_FORMAT_YMD);
        String start = DateUtil.getCurrentDateByOffset(DateUtil.DATE_FORMAT_YMD, Calendar.MONTH,
                Global.LAST_MONTH_NUM);
        MemoryBucket lastweek = new MemoryBucket.Builder()
                .setDescription(mRes.getString(R.string.memory_last_month_description))
                .setSummary(start + Global.SEPARATOR + today)
                .setMemoryType(Global.MEMORIES_TYPE_LAST_MONTH)
                .build();

        MemoryBucketManager bucketManager =
                new MemoryBucketManager(mIMemoriesApp, lastweek, Global.MEMORIES_TYPE_LAST_MONTH,
                        null, 0);
        bucketManager.loadImages();
    }

    private void loadDateMemory() {
        LogUtil.d(TAG, "Kathy - loadDateMemory");
        final HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        Handler mHandler = new Handler(handlerThread.getLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String where = "cameradate > 0)"
                        + " group by cameradate having count(cameradate) > 10"
                        + " and ( datetaken > 0";
                Cursor cursor = mResolver.query(
                        GalleryStore.Files.getContentUri("external"),
                        new String[]{GalleryStore.Images.ImageColumns.DATE_TAKEN
                                + "," + "datetaken/24/60/60 as cameradate "}, where, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    for (int index = 0; index < cursor.getCount(); index++) {
                        int intDateTaken = cursor.getInt(cursor.getColumnIndex(GalleryStore
                                .Images.ImageColumns.DATE_TAKEN));
                        doAddDateTypeBucket(intDateTaken);
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
                handlerThread.quit();
            }
        });
    }

    private void doAddDateTypeBucket(int intDateTaken) {
        LogUtil.d(TAG, "Kathy - doAddDateTypeBucket - datetaken = " + intDateTaken);

        long longDateTaken = (long)(intDateTaken) * 1000;
        String strDate = null;
        if (longDateTaken != 0) {
            strDate = DateFormat.format("yyyy-MM-dd", longDateTaken).toString();
        }

        MemoryBucket location = new MemoryBucket.Builder()
                .setDescription(mRes.getString(R.string.diary) + " • " + strDate )
                .setMemoryType(Global.MEMORIES_TYPE_DATE)
                .build();
        LogUtil.d(TAG, "Kathy - doAddDateTypeBucket - strDate = " + strDate);

        MemoryBucketManager bucketManager =
                new MemoryBucketManager(mIMemoriesApp, location, Global.MEMORIES_TYPE_DATE,
                        null, intDateTaken);
        bucketManager.loadImages();
    }

    private void loadLocationMemory() {
        LogUtil.d(TAG, "Kathy - loadLocationMemory");
        //筛选出所有照片的城市(去重)且存入list数组中,按照list的个数增加相应城市MemooryBucket
        final HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        Handler mHandler = new Handler(handlerThread.getLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //根据经纬度逆向解析地址,只筛选那些经纬度信息存在的,且地址信息未被解析过的
                String where = GalleryStore.Images.ImageColumns.COUNTRY + " IS NOT NULL"
                        + " AND " + GalleryStore.Images.ImageColumns.CITY + " IS NOT NULL ";

                Cursor cursor = mResolver.query(
                        GalleryStore.Files.getContentUri("external"),
                        new String[]{"DISTINCT " + GalleryStore.Images.ImageColumns.COUNTRY,
                                GalleryStore.Images.ImageColumns.CITY}, where, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    for (int index = 0; index < cursor.getCount(); index++) {
                        String strCountry = cursor.getString(cursor.getColumnIndex(GalleryStore
                                .Images
                                .ImageColumns.COUNTRY));
                        String strCity = cursor.getString(cursor.getColumnIndex(GalleryStore.Images
                                .ImageColumns.CITY));
                        //检测到一个城市,添加一个bucket条目
                        doAddMemoryBucket(new AddressObject(null, strCountry, strCity));
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
                handlerThread.quit();
            }
        });
    }


    private void doAddMemoryBucket(AddressObject addressObject) {
        LogUtil.d(TAG, "Kathy - doAddMemoryBucket - addressObject = " + addressObject.toString());
        MemoryBucket location = new MemoryBucket.Builder()
                .setDescription(mRes.getString(R.string.memory_travel_description) + " • " +
                        addressObject.getCity())
                .setMemoryType(Global.MEMORIES_TYPE_LOCATION)
                .build();

        MemoryBucketManager bucketManager =
                new MemoryBucketManager(mIMemoriesApp, location, Global.MEMORIES_TYPE_LOCATION,
                        addressObject, 0);
        bucketManager.loadImages();
    }

    public List<MemoryBucket> getMemoryBucketList() {
        Set<Integer> keys = mMemoryBucketList.keySet();
        List<MemoryBucket> list = new ArrayList<>();
        Iterator<Integer> iterator = keys.iterator();
        while (iterator.hasNext()) {
            int key = iterator.next();
            list.add(mMemoryBucketList.get(key));
        }
        return list;
    }

    public void addBucket(MemoryBucket bucket) {
        nextVersionNumber();
        Log.d(TAG, "sVersionSerial " + sVersionSerial + " MemoryBucket" + bucket.getMemoryType());
        int bucketId = bucket.getBucketId();
        if (bucket.getLocalImages().size() != 0) {

            mMemoryBucketList.put(bucketId, bucket);
        } else {
            mMemoryBucketList.remove(bucketId);
        }
        if (mBroker != null) {
            mBroker.onChange(bucket.getBucketId());
        }
    }

    public void removeBucket(MemoryBucket bucket) {
        mMemoryBucketList.remove(bucket.getBucketId());
    }

    public MemoryBucket getMemoryBucketByKey(int key) {
        return mMemoryBucketList.get(key);
    }

    @Override
    public void notifyContentChanged() {
        loadMemories();
    }

    public void registerChangeNotifier(IMemoriesDataNotifier notifier) {
        if (mBroker == null) {
            mBroker = new DataNotifyBroker();
        }
        mBroker.registerNotifier(notifier);
    }

    public void unRegisterChangeNotifier(IMemoriesDataNotifier notifier) {
        if (mBroker != null) {
            mBroker.unRegisterNotifiere(notifier);
        }
    }


    private static class Singleton {
        private static MemoriesManager instance = new MemoriesManager();
    }

    public long getVersionSionSerial() {
        return sVersionSerial;
    }

    private static class DataNotifyBroker {
        private WeakHashMap<IMemoriesDataNotifier, Object> mNotifiers =
                new WeakHashMap<IMemoriesDataNotifier, Object>();

        public synchronized void registerNotifier(IMemoriesDataNotifier notifier) {
            mNotifiers.put(notifier, null);
        }

        public synchronized void unRegisterNotifiere(IMemoriesDataNotifier notifier) {
            mNotifiers.remove(notifier);
        }

        public synchronized void onChange(int type) {
            for (IMemoriesDataNotifier notifier : mNotifiers.keySet()) {
                notifier.notifyContentChanged(type);
            }
        }
    }
}
