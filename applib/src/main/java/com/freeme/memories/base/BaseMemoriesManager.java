package com.freeme.memories.base;

import java.util.WeakHashMap;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public class BaseMemoriesManager {

    private WeakHashMap<IContentListener, Object> mListeners = new WeakHashMap<>();

    public void addContentListener(IContentListener listener) {
        mListeners.put(listener, null);
    }

    public void removeContentListener(IContentListener listener) {
        mListeners.remove(listener);
    }

    public void notifyContentChanged() {
        for (IContentListener listener : mListeners.keySet()) {
            listener.onContentDirty();
        }
    }
}
