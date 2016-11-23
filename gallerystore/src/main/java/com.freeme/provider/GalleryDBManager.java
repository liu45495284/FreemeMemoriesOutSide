package com.freeme.provider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.freeme.memories.utils.LogUtil;
import com.freeme.utils.AppUtil;


public class GalleryDBManager {
    private Context        mContext;
    protected ServiceConnection mediaStoreConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.i("onServiceConnected = " + name + " | " + service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mContext.unbindService(mediaStoreConnection);
            bindServer();
        }
    };
    private SQLiteDatabase db;
    private com.freeme.provider.DaoMaster daoMaster;

    public static GalleryDBManager getInstance() {
        return Singleton.instance;
    }

    public void initDB(Context context, String dbName) {
        mContext = context;
        com.freeme.provider.DaoMaster.DevOpenHelper helper = new com.freeme.provider.DaoMaster.DevOpenHelper(context, dbName, null);
        db = helper.getWritableDatabase();
        daoMaster = new com.freeme.provider.DaoMaster(db);
        if (AppUtil.isInGalleryProcess(mContext)) {
            bindServer();
            com.freeme.provider.MediaStoreImporter.getInstance().doImport(context);
        }
    }

    private void bindServer() {
        Intent intent = new Intent(mContext, com.freeme.provider.MediaStoreImportService.class);
        mContext.bindService(intent, mediaStoreConnection, Context.BIND_AUTO_CREATE);
    }

    public SQLiteDatabase getDataBase() {
        return daoMaster.getDatabase();
    }

    public com.freeme.provider.GalleryFilesDao getGalleryFilesDao() {
        com.freeme.provider.DaoSession session = daoMaster.newSession();
        return session.getGalleryFilesDao();
    }

    private static class Singleton {
        private static GalleryDBManager instance = new GalleryDBManager();
    }
}
