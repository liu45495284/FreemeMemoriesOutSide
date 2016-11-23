package com.freeme.memories.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.freeme.memories.R;
import com.freeme.memories.constant.Global;
import com.freeme.memories.data.bucket.MemoryBucket;
import com.freeme.memories.data.entity.LocalImage;
import com.freeme.memories.data.manager.IMemoriesDataNotifier;
import com.freeme.memories.data.manager.MemoriesManager;
import com.freeme.memories.databinding.ActivityPhotoBinding;
import com.freeme.memories.ui.PhotoViewer;
import com.freeme.memories.utils.ImageViewFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity implements IMemoriesDataNotifier {

    private ActivityPhotoBinding mActivityPhotoBinding;
    private PhotoViewer mPhotoViewer;
    private MemoryBucket mMemoryBucket;
    private long mDataVersion;
    private LocalImage mSelectImage;
    private int mPhotoIndex;
    private int mBucketindex = -1;
    private List<LocalImage> mLocalImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivityPhotoBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MemoriesManager.getInstance().registerChangeNotifier(this);
        if (mDataVersion != MemoriesManager.getInstance().getVersionSionSerial()) {
            updateContent();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MemoriesManager.getInstance().unRegisterChangeNotifier(this);
    }

    private void initView() {
        mPhotoViewer = (PhotoViewer) getFragmentManager().findFragmentById(R.id.viewer);
        mPhotoViewer.setShowIndicator(false);
    }

    private void initData() {
        mBucketindex = getIntent().getIntExtra(Global.EXTRA_MEMORY_BUCKET_INDEX, -1);
        if (mBucketindex == -1) {
            return;
        }

        mPhotoIndex = getIntent().getIntExtra(Global.EXTRA_PHOTO_ITEM_INDEX, 0);

        mMemoryBucket = MemoriesManager.getInstance().getMemoryBucketByKey(mBucketindex);
        mDataVersion = MemoriesManager.getInstance().getVersionSionSerial();
        if (null != mMemoryBucket) {
            mLocalImages = mMemoryBucket.getLocalImages();
        } else {
            return;
        }
        if (mPhotoIndex < mLocalImages.size()) {
            mSelectImage = mLocalImages.get(mPhotoIndex);
        }

        List<ImageView> views = new ArrayList<>();
        for (LocalImage image : mLocalImages) {
            ImageView view = ImageViewFactory.getImageView(this, image.getData());
            view.setTag(image);
            views.add(view);
        }

        mPhotoViewer.setCycle(false);
        mPhotoViewer.setData(views);
        mPhotoViewer.setWheel(false);
        mPhotoViewer.setCurrentPosition(mPhotoIndex);
    }

    public void updateContent() {
        mPhotoIndex = mPhotoViewer.getCurrentPostion();
        if (null != mLocalImages) {
            mSelectImage = mLocalImages.get(mPhotoIndex);
        }

        mMemoryBucket = MemoriesManager.getInstance().getMemoryBucketByKey(mBucketindex);
        if (null != mMemoryBucket) {
            mLocalImages = mMemoryBucket.getLocalImages();
        }
        if (mLocalImages.isEmpty()) {
            finish();
            return;
        }
        mDataVersion = MemoriesManager.getInstance().getVersionSionSerial();
        List<ImageView> views = new ArrayList<>();
        int selectPosition = -1;
        for (int i = 0; i < mLocalImages.size(); i++) {
            LocalImage image = mLocalImages.get(i);
            ImageView view = ImageViewFactory.getImageView(this, image.getData());
            view.setTag(image);
            views.add(view);
            if (null != mSelectImage) {
                if (mSelectImage.getData().equals(image.getData())) {
                    mSelectImage = image;
                    selectPosition = i;
                }
            }
        }
        if (selectPosition != -1) {
            mPhotoIndex = selectPosition;
        } else {
            mPhotoIndex = 0;
        }
        mPhotoViewer.setCycle(false);
        mPhotoViewer.setData(views);
        mPhotoViewer.setWheel(false);
        mPhotoViewer.setCurrentPosition(mPhotoIndex);
    }

    @Override
    public void notifyContentChanged(int type) {
        if (type == mBucketindex) {
            updateContent();
        }
    }
}
