package com.freeme.memories.base;

import android.databinding.BaseObservable;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public class BaseBucket extends BaseObservable implements IContentListener {

    public BaseBucket() {
    }

    public void setCoverPath(String coverPath) {
    }

    @Override
    public void onContentDirty() {

    }

    public int getMemoryType() {
        return -1;
    }

    public String getDescription() {
        return null;
    }

    public void setDescription(String description) {
    }

    public String getSummary() {
        return null;
    }

    public void setSummary(String summary) {
    }

    public void reload(){

    }
}
