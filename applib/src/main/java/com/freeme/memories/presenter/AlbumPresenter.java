package com.freeme.memories.presenter;

import android.content.Context;
import android.view.View;

import com.freeme.memories.activity.AlbumActivity;
import com.freeme.memories.base.BasePresenter;
import com.freeme.memories.base.IHandle;
import com.freeme.memories.utils.LogUtil;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-20.
 */
public class AlbumPresenter extends BasePresenter {

    public AlbumPresenter(IHandle handle) {
        setHandle(handle);
    }

    public void onHeaderClick(View view) {
        LogUtil.i("onHeaderClick");
        onHandle(AlbumActivity.MSG_HEADER);
    }

    public void onItemClick(Context context, int position) {
        LogUtil.i("onItemClick");
        onHandle(AlbumActivity.MSG_IMAGE_ITEM, position);
    }
}
