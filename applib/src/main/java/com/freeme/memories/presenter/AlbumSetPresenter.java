package com.freeme.memories.presenter;

import android.content.Context;

import com.freeme.memories.activity.AlbumSetActivity;
import com.freeme.memories.base.BasePresenter;
import com.freeme.memories.base.IHandle;
import com.freeme.memories.utils.LogUtil;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-20.
 */
public class AlbumSetPresenter extends BasePresenter {

    public AlbumSetPresenter(IHandle handle) {
        setHandle(handle);
    }

//    public void onItemClick(Context context, int position) {
//        LogUtil.i("onItemClick");
//        onHandle(AlbumSetActivity.MSG_MEMORY_ITEM, position);
//    }

    public void onItemClick(Context context, int position,int buckid) {
        LogUtil.i("onItemClick");
        onHandle(AlbumSetActivity.MSG_MEMORY_ITEM, buckid);
    }
}
