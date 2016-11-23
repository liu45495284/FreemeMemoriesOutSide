package com.freeme.memories.base;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-20.
 */
public class BasePresenter {
    private IHandle mHandle;

    public IHandle getHandle() {
        return mHandle;
    }

    public void setHandle(IHandle handle) {
        mHandle = handle;
    }

    public void onHandle(int msgId) {
        onHandle(msgId, null);
    }

    public void onHandle(int msgId, Object object) {
        if (mHandle != null) {
            mHandle.onHandle(msgId, object);
        }
    }
}
