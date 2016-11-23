package com.freeme.memories.data.bucket;

import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.freeme.memories.BR;
import com.freeme.memories.base.BaseBucket;
import com.freeme.memories.data.entity.LocalImage;
import com.freeme.memories.utils.AppUtil;
import com.freeme.memories.utils.LogUtil;

import java.util.ArrayList;

/**
 * Description: 回忆相册类
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public class MemoryBucket extends BaseBucket {

    private String mCoverPath;
    private LocalImage mCoverImage;

    @NonNull
    private String mDescription;
    private String mSummary;
    private int mMemoryType;
    private ArrayList<LocalImage> mLocalImages = new ArrayList<>();
    private int bucketId;

    public int getBucketId() {
        return bucketId = mDescription.hashCode();
    }

    private MemoryBucket(Builder builder) {
        this.mCoverPath = builder.mCoverPath;
        this.mCoverImage = builder.mCoverImage;
        this.mDescription = builder.mDescription;
        this.mSummary = builder.mSummary;
        this.mMemoryType = builder.mMemoryType;
    }

    @Bindable
    public LocalImage getCoverImage() {
        return mCoverImage;
    }

    public void setCoverImage(LocalImage coverImage) {
        mCoverImage = coverImage;
        notifyPropertyChanged(BR.coverImage);
    }

    @Bindable
    public String getCoverPath() {
        return mCoverPath;
    }

    public void setCoverPath(String coverPath) {
        mCoverPath = coverPath;
        notifyPropertyChanged(BR.coverPath);
    }

    @Override
    public void onContentDirty() {
        super.onContentDirty();
    }

    @Bindable
    @Override
    public int getMemoryType() {
        return mMemoryType;
    }

    public void setMemoryType(int memoryType) {
        mMemoryType = memoryType;
        notifyPropertyChanged(BR.memoryType);
    }

    @Bindable
    @Override
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    @Override
    public String getSummary() {
        return mSummary;
    }

    @Override
    public void setSummary(String summary) {
        mSummary = summary;
        notifyPropertyChanged(BR.summary);
    }

    @Bindable
    public ArrayList<LocalImage> getLocalImages() {
        return mLocalImages;
    }

    public void setLocalImages(ArrayList<LocalImage> localImages,int type) {
        if (localImages == null) {
            return;
        }

        mLocalImages = localImages;
        notifyPropertyChanged(BR.localImages);

        // 添加附加信息
        setLocalImageExtraInfo(localImages);
    }

    private void setLocalImageExtraInfo(ArrayList<LocalImage> localImages) {
        // 封面
        setExtraCover(localImages);
    }

    /**
     * 设置相册封面
     * @param localImages
     */
    private void setExtraCover(ArrayList<LocalImage> localImages) {
        LogUtil.i("mLocalImages size = " + mLocalImages.size());
        if (localImages.size() > 0) {
            int random = (int) (Math.random() * (localImages.size() - 1));
            mCoverImage = localImages.get(random);

            setCoverPath(AppUtil.getDataPath(mCoverImage.getData()));
            //MemoriesManager.getInstance().addBucket(this);
        }
    }

    public static class Builder {
        private String mCoverPath;
        private LocalImage mCoverImage;
        private String mDescription;
        private String mSummary;
        private int mMemoryType;

        public Builder setMemoryType(int memoryType) {
            mMemoryType = memoryType;
            return this;
        }

        public Builder setCoverImage(LocalImage coverImage) {
            mCoverImage = coverImage;
            return this;
        }

        public Builder setDescription(String description) {
            mDescription = description;
            return this;
        }

        public Builder setSummary(String summary) {
            mSummary = summary;
            return this;
        }

        public Builder setCoverPath(String coverPath) {
            mCoverPath = coverPath;
            return this;
        }

        public MemoryBucket build() {
            return new MemoryBucket(this);
        }
    }

    @Override
    public String toString() {
        return  " mDescription = " + mDescription
                + " mSummary = " + mSummary
                + " mMemoryType = " + mMemoryType;
    }
}
