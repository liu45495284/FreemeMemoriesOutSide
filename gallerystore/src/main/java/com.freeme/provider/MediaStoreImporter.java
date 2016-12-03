package com.freeme.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import com.freeme.Address.AddressItem;
import com.freeme.Address.AnalyzeAddressTask;
import com.freeme.Address.AnalyzeCallback;
import com.freeme.Address.CodeUtil;
import com.freeme.Address.Config;
import com.freeme.memories.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class MediaStoreImporter {
    private static final String TAG = "MediaStoreImporter";
    private static final int TAG_HANDLER_ADD_FILE = 1;
    private static final int TAG_HANDLER_DELETE_FILE = 2;

    private static GalleryFilesDao galleryFilesDao = null;
    private ContentResolver mResolver;
    private Context mContext;

    public static MediaStoreImporter getInstance() {
        return Singleton.instance;
    }

    public void doImport(final Context context) {
        mResolver = context.getContentResolver();
        mContext = context;
        galleryFilesDao = GalleryDBManager.getInstance().getGalleryFilesDao();

        final HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        Handler mHandler = new Handler(handlerThread.getLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                importData();
                notifyUriChange("images");
                com.freeme.provider.MediaStoreImporter.getInstance().doAnalysisAddress(context);
                handlerThread.quit();
            }
        });
    }

    private void importData() {
        LogUtil.i("import Data");
        List<GalleryFiles> addFilesList = new ArrayList<>();
        addFilesList.clear();
        List<GalleryFiles> updateFilesList = new ArrayList<>();
        updateFilesList.clear();

        List<Long> containIds = getContainIds();

        String where = "media_type =" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR " + "media_type =" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Cursor cursor = mResolver.query(MediaStore.Files.getContentUri("external"),
                GalleryStore.PROJECTION, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                long _id = cursor.getLong(0);
                GalleryFiles galleryFiles = getGalleryFiles(_id);
                setGalleryFile(galleryFiles, cursor, _id);
                if (containIds.contains(_id)) {
                    updateFilesList.add(galleryFiles);
                } else {
                    addFilesList.add(galleryFiles);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

        try {
            galleryFilesDao.insertInTx(addFilesList);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            LogUtil.i("SQLiteConstraintException : " + e);
        }
        galleryFilesDao.updateInTx(updateFilesList);
    }

    private List<Long> getContainIds() {
        List<Long> containIds = new ArrayList<>();
        Cursor cursor = mResolver.query(
                GalleryStore.Files.getContentUri("external"),
                new String[]{BaseColumns._ID}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            for (int index = 0; index < cursor.getCount(); index++) {
                containIds.add(cursor.getLong(0));
                cursor.moveToNext();
            }
            cursor.close();
        }

        return containIds;
    }

    private GalleryFiles getGalleryFiles(long id) {
        return GalleryFiles.getGalleryFiles(galleryFilesDao, id);
    }

    private void setGalleryFile(GalleryFiles galleryFiles, Cursor cursor, long _id) {
        String tmpString;
        int index = 0;

        // _id
        galleryFiles.setId(_id);

        // _data
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setData(tmpString);
        }

        // _size
        index++;
        galleryFiles.setSize(cursor.getInt(index));

        // media_type
        index++;
        galleryFiles.setMedia_type(cursor.getInt(index));

        // _display_name
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setDisplay_name(tmpString);
        }

        // mime_type
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setMime_type(tmpString);
        }

        // title
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setTitle(tmpString);
        }

        // date_added
        index++;
        galleryFiles.setDate_added(cursor.getInt(index));

        // date_modified
        index++;
        galleryFiles.setDate_modified(cursor.getInt(index));

        // description
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setDescription(tmpString);
        }

        // picasa_id
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setDescription(tmpString);
        }

        // duration
        index++;
        galleryFiles.setDuration(cursor.getInt(index));

        // artist
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setArtist(tmpString);
        }

        // album
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setAlbum(tmpString);
        }

        // resolution
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setResolution(tmpString);
        }

        // width
        index++;
        galleryFiles.setWidth(cursor.getInt(index));

        // height
        index++;
        galleryFiles.setHeight(cursor.getInt(index));

        // latitude
        index++;
        galleryFiles.setLatitude(cursor.getDouble(index));

        // longitude
        index++;
        galleryFiles.setLongitude(cursor.getDouble(index));

        // datetaken
        index++;
        //LogUtil.i("datetaken = " + cursor.getLong(index));
        galleryFiles.setDatetaken((int) (cursor.getLong(index) / 1000));

        // orientation
        index++;
        galleryFiles.setOrientation(cursor.getInt(index));

        // mini_thumb_magic
        index++;
        galleryFiles.setMini_thumb_magic(cursor.getInt(index));

        // bucket_id
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setBucket_id(tmpString);
        }

        // bucket_display_name
        index++;
        tmpString = cursor.getString(index);
        if (tmpString != null) {
            galleryFiles.setBucket_display_name(tmpString);
        }
    }

    public void addFile(String type, long selectionId) {
        LogUtil.i("addfiles = " + type + " , " + selectionId);

        mHandler.removeMessages(TAG_HANDLER_ADD_FILE);
        Message msg = mHandler.obtainMessage();
        msg.what = TAG_HANDLER_ADD_FILE;
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putLong("selectionId", selectionId);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, 5000);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case TAG_HANDLER_ADD_FILE:
                    Log.d(TAG, "Kathy - addFile - handleMessage: ");
                    Bundle bundle = msg.getData();
                    String type = bundle.getString("type");
                    long selectionId = bundle.getLong("selectionId");

                    List<Long> containIds = getContainIds();
                    String where = "(media_type =" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                            + " OR " + "media_type =" + MediaStore.Files.FileColumns
                            .MEDIA_TYPE_VIDEO
                            + ") AND " + "_id = " + selectionId;

                    Cursor cursor = mResolver.query(MediaStore.Files.getContentUri("external"),
                            GalleryStore.PROJECTION, where, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        long _id = cursor.getLong(0);
                        if (!containIds.contains(_id)) {
                            GalleryFiles galleryFiles = getGalleryFiles(_id);
                            setGalleryFile(galleryFiles, cursor, _id);
                            galleryFilesDao.insertInTx(galleryFiles);
                            notifyUriChange(type);
                        } else {
                            updateFiles(type);
                        }
                        cursor.close();
                    }

                    break;

                case TAG_HANDLER_DELETE_FILE:
                    List<Long> containIds1 = getContainIds();
                    List<Long> mediastoreIds = new ArrayList<>();

                    String where1 = "(media_type =" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                            + " OR " + "media_type =" + MediaStore.Files.FileColumns
                            .MEDIA_TYPE_VIDEO
                            + ")";
                    Cursor cursor1 = mResolver.query(
                            MediaStore.Files.getContentUri("external"),
                            new String[]{BaseColumns._ID}, where1, null, null);
                    if (cursor1 != null && cursor1.moveToFirst()) {
                        for (int index = 0; index < cursor1.getCount(); index++) {
                            mediastoreIds.add(cursor1.getLong(0));
                            cursor1.moveToNext();
                        }
                        cursor1.close();
                    }

                    for (Long id : containIds1) {
                        if (!mediastoreIds.contains(id)) {
                            LogUtil.i("Community", "Kathy - delete id = " + id);
                            galleryFilesDao.deleteByKey(id);
                        }
                    }

                    for (Long id : mediastoreIds) {
                        if (!containIds1.contains(id)) {
                            galleryFilesDao.deleteByKey(id);
                        }
                    }
                    updateFiles("images");
                    updateFiles("video");
                    break;

                default:
                    break;
            }

        }
    };

    private void notifyUriChange(String type) {
        if ("images".equals(type)) {
//            mResolver.notifyChange(GalleryStore.Images.Media.EXTERNAL_CONTENT_URI, null);
            mResolver.notifyChange(GalleryStore.Files.getContentUri("external"), null);
        } else {
            //mResolver.notifyChange(GalleryStore.Video.Media.EXTERNAL_CONTENT_URI, null);
        }
    }

    public void updateFiles(String type) {
        importData();
        notifyUriChange(type);
    }

    public void deleteFiles() {
        LogUtil.i("Community", "Kathy - removeMessages");
        mHandler.removeMessages(TAG_HANDLER_DELETE_FILE);
        Message msg = mHandler.obtainMessage();
        msg.what = TAG_HANDLER_DELETE_FILE;
        mHandler.sendMessageDelayed(msg, 5000);
        LogUtil.i("Community", "Kathy - sendMessageDelayed");
    }

    private static class Singleton {
        private static MediaStoreImporter instance = new MediaStoreImporter();
    }

    public void doAnalysisAddress(Context context) {

        final HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        Handler mHandler = new Handler(handlerThread.getLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //根据经纬度逆向解析地址,只筛选那些经纬度信息存在的,且地址信息未被解析过的
                String where = "(media_type = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + ")" + " AND ( " + GalleryStore.Images.ImageColumns.LATITUDE + " > 0 AND "
                        + GalleryStore.Images.ImageColumns.LONGITUDE + " > 0 )" + " AND "
                        + "( " + GalleryStore.Images.ImageColumns.COUNTRY + " IS NULL"
                        + " AND " + GalleryStore.Images.ImageColumns.CITY + " IS NULL )";

                Cursor cursor = mResolver.query(
                        GalleryStore.Files.getContentUri("external"),
                        new String[]{GalleryStore.Images.ImageColumns._ID, GalleryStore.Images
                                .ImageColumns.LATITUDE, GalleryStore
                                .Images.ImageColumns.LONGITUDE}, where, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    for (int index = 0; index < cursor.getCount(); index++) {
                        long id = cursor.getLong(cursor.getColumnIndex(GalleryStore.Images
                                .ImageColumns._ID));
                        Double latitude = cursor.getDouble(cursor.getColumnIndex(GalleryStore.Images
                                .ImageColumns.LATITUDE));
                        Double longtitude = cursor.getDouble(cursor.getColumnIndex(GalleryStore
                                .Images.ImageColumns.LONGITUDE));
                        analyzeAddress(id, latitude, longtitude);
                        cursor.moveToNext();
                    }
                    cursor.close();
                    //地址解析完后更新数据库数据,将country和city存入数据库
                }
                handlerThread.quit();
            }
        });
    }

    private void doUpdate(AddressObject addressObject) {
        LogUtil.d(TAG, "Kathy - doUpdate = " + addressObject.getId());
        //将对应id获取到的地址信息存入gallery.db
        ContentValues updateValues = new ContentValues();
        updateValues.put(GalleryStore.Images.ImageColumns.COUNTRY, addressObject.getCountry());
        updateValues.put(GalleryStore.Images.ImageColumns.CITY, addressObject.getCity());
        String where = "( " + MediaStore.Files.FileColumns._ID + " = " + addressObject.getId()
                + ")";
        mResolver.update(GalleryStore.Files.getContentUri
                ("external"), updateValues, where, null);
    }

    private void analyzeAddress(final Long id, Double latitude, Double longtitude) {
        String url = String.format(Config.GOOGLE_MAP_API, latitude, longtitude);
        new AnalyzeAddressTask(new AnalyzeCallback() {

            @Override
            public void onSuccess(AddressItem[] data) {
                doUpdate(breakAddress(data, id));
            }

            @Override
            public void onFailure() {
            }
        }).execute(url);
    }

    private AddressObject breakAddress(AddressItem[] data, long id) {
        List<String> tmpList;
        List<String> addressList = new ArrayList<>();
        for (AddressItem aData : data) {
            tmpList = Arrays.asList(aData.getTypes());
            if (tmpList.contains(Config.COUNTRY)) {
                addressList.add(CodeUtil.decode(aData.getLong_name()));
            } else if (tmpList.contains(Config.COUNTRY_AREA_LEVEL_1)) {
                addressList.add(CodeUtil.decode(aData.getLong_name()));
            } else if (tmpList.contains(Config.COUNTRY_AREA_LEVEL_2)) {
                addressList.add(CodeUtil.decode(aData.getLong_name()));
            } else if (tmpList.contains(Config.LOCALITY)) {
                addressList.add(CodeUtil.decode(aData.getLong_name()));
            } else if (tmpList.contains(Config.SUBLOCALITY_LEVEL_1)) {
                addressList.add(CodeUtil.decode(aData.getLong_name()));
            } else if (tmpList.contains(Config.SUBLOCALITY_LEVEL_2)) {
                addressList.add(CodeUtil.decode(aData.getLong_name()));
            } else if (tmpList.contains(Config.ROUTE)) {
                addressList.add(CodeUtil.decode(aData.getLong_name()));
            } else if (tmpList.contains(Config.STREET_NUMBER)) {
                addressList.add(CodeUtil.decode(aData.getLong_name()));
            }
        }
        addressList = reverseList(addressList);
        String country;
        String city;

        if (addressList.size() > 3) {
            country = addressList.get(1);
            city = addressList.get(2);
        } else {
            return null;
        }

        AddressObject addressObject = new AddressObject();
        addressObject.setId(id);
        addressObject.setCountry(country);
        addressObject.setCity(city);
        return addressObject;
    }

    private List<String> reverseList(List<String> address) {
        Stack<String> stack = new Stack<>();
        for (String str : address) {
            stack.push(str);
        }

        address.clear();
        while (!stack.empty()) {
            address.add(stack.pop());
        }
        return address;
    }
}
