package com.freeme.memories.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.droi.adcontrol.banner.AdControlBanner;
import com.droi.adcontrol.banner.BannerListener;
import com.freeme.memories.actionbar.entity.ActionBarConfig;
import com.freeme.memories.actionbar.presenter.ActionBarPresenter;
import com.freeme.memories.R;
import com.freeme.memories.adapter.AlbumAdapter;
import com.freeme.memories.base.AppImpl;
import com.freeme.memories.base.BaseActivity;
import com.freeme.memories.constant.Config;
import com.freeme.memories.constant.Global;
import com.freeme.memories.data.bucket.MemoryBucket;
import com.freeme.memories.data.entity.LocalImage;
import com.freeme.memories.data.manager.IMemoriesDataNotifier;
import com.freeme.memories.data.manager.MemoriesManager;
import com.freeme.memories.databinding.ActivityAlbumBinding;
import com.freeme.memories.presenter.AlbumPresenter;
import com.freeme.memories.slideshow.Constants;
import com.freeme.memories.slideshow.MediaItem;
import com.freeme.memories.slideshow.SlideShowPlayerActivity;
import com.freeme.memories.slideshow.SlideshowInfo;
import com.freeme.memories.ui.RecyclerViewItemDecoration;
import com.freeme.memories.utils.DisplayUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends BaseActivity implements IMemoriesDataNotifier {

    public static final String TAG = "AlbumActivity";
    public static final int MSG_HEADER = 100;
    public static final int MSG_IMAGE_ITEM = 101;

    private ActivityAlbumBinding mActivityAlbumBinding;
    private MemoryBucket mMemoryBucket;
    private ActionBarConfig mActionBarConfig;

    //SlideShow
    private SlideshowInfo mSlideShowInfo;
    private Handler handler; // used to update the slideshow
    private int nextItemIndex; // index of the next image to display
    private static final int DURATION = 3000;

    private int mBucketId = -1;
    private ArrayList<LocalImage> mLocalImages = new ArrayList<>();
    private AlbumAdapter mAdapter;
    private long mDataVersion;
    private boolean isInitData = false;
    private int mScreeneWidth = 700 ;

    private String bannerPosition = "banner_02";
    private AdControlBanner banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAlbumBinding = DataBindingUtil.setContentView(this, R.layout.activity_album);
        initData();
        initSlideShowInfo();
        initSlidePreview(savedInstanceState);
        initBinding();
        createBannerAd();
    }

    private void createBannerAd(){
        banner = new AdControlBanner(this,bannerPosition);
        banner.setListener(new BannerListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG,"onAdLoaded");
            }

            @Override
            public void onAdFailed() {
                Log.d(TAG,"onAdFailed");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG,"onAdClicked");
            }
        });

        mActivityAlbumBinding.adContainer.addView(banner,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
    }



    private void initSlideShowInfo() {
        if (mMemoryBucket != null) {
            //slideName
            mSlideShowInfo = new SlideshowInfo(mMemoryBucket.getDescription());
            mActionBarConfig.setTitle(mMemoryBucket.getDescription());
            mSlideShowInfo.setMemoryType(mMemoryBucket.getMemoryType());
            List<LocalImage> imageList = mMemoryBucket.getLocalImages();
            mSlideShowInfo.setSlideBucketId(mBucketId);
            if (null != imageList && imageList.size() > 0) {
                for (int i = 0; i < imageList.size(); i++) {
                    LocalImage localImage = imageList.get(i);
                    if (null != localImage) {
                        //media or video
                        int type = localImage.getMediaType();
                        String strUri = localImage.getUri().toString();
                        mSlideShowInfo.addMediaItem(type, strUri);
                    }
                }
            }
        }
    }

    private void initSlidePreview(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            nextItemIndex = 0; // start from first image
        } else {
            // get index of image that was displayed when config changed
            nextItemIndex = savedInstanceState.getInt(Constants.IMAGE_INDEX);
        }

        handler = new Handler(); // create handler to control slideshow
    }

    @Override
    public void setUpActionBar() {
        super.setUpActionBar();

        mActionBarConfig = new ActionBarConfig();
        mActionBarConfig.setActionBarPresenter(new ActionBarPresenter(this));
        mActionBarConfig.setShowHomeAsUp(true);
        //mActionBarConfig.setTitle("");
    }

    @Override
    public void onHandle(int type, Object object) {
        super.onHandle(type, object);

        switch (type) {
            case MSG_HEADER:
                // 启动幻灯片播放
                Intent playSlideshow =
                        new Intent(AlbumActivity.this, SlideShowPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SLIDE_SHOW_INFO,
                        mSlideShowInfo);
                playSlideshow.putExtras(bundle);
                // include the slideshow's name as an extra
                playSlideshow.putExtra(
                        Constants.SLIDE_SHOW_NAME, mMemoryBucket
                                .getDescription());
                startActivity(playSlideshow);
                break;

            case MSG_IMAGE_ITEM:
                Intent photo = new Intent(AlbumActivity.this, PhotoActivity.class);
                photo.putExtra(Global.EXTRA_MEMORY_BUCKET_INDEX, mBucketId);
                photo.putExtra(Global.EXTRA_PHOTO_ITEM_INDEX, (int) object);
                startActivity(photo);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save nextItemIndex and slideshowName
        outState.putInt(Constants.IMAGE_INDEX, nextItemIndex - 1);
    }

    // called after onCreate and sometimes onStop
    @Override
    protected void onStart() {
        super.onStart();
       // post updateSlideshow to execute
    }

    @Override
    protected void onResume() {
        if(banner != null){
            banner.resume();
        }
        super.onResume();
        MobclickAgent.onResume(this);
        MemoriesManager.getInstance().registerChangeNotifier(this);
        if (mDataVersion != MemoriesManager.getInstance().getVersionSionSerial()) {
            updateContent();
        }
        handler.post(updateSlideshow);
    }

    @Override
    protected void onPause() {
        if(banner != null){
            banner.pause();
        }
        super.onPause();
        MobclickAgent.onPause(this);
        MemoriesManager.getInstance().unRegisterChangeNotifier(this);
        handler.removeCallbacks(updateSlideshow);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // prevent slideshow from operating when in background

    }

    @Override
    protected void onDestroy() {
        if(banner != null){
            banner.destroy();
            banner = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBack() {
        super.onBack();
        finish();
    }

    private void initData() {
        mBucketId = getIntent().getIntExtra(Global.CLICK_BUCKETID_INDEX, -1);
        if (mBucketId != -1) {
            mMemoryBucket = MemoriesManager.getInstance().getMemoryBucketByKey(mBucketId);
            updateDataVersion();
        }
//        mMemoryBucket = getIntent().getParcelableExtra(Global.EXTRA_MEMORY_BUCKET);
        mActionBarConfig.setTitle("");
    }

    private void initBinding() {
        if (mMemoryBucket == null) {
            return;
        }
        mActivityAlbumBinding.setActionbarConfig(mActionBarConfig);

        AlbumPresenter presenter = new AlbumPresenter(this);
        mActivityAlbumBinding.setPresenter(presenter);
        mActivityAlbumBinding.setBucket(mMemoryBucket);

        mActivityAlbumBinding.recycler.setHasFixedSize(true);
        mActivityAlbumBinding.recycler.setLayoutManager(
                new GridLayoutManager(this, Config.ALBUM_COLUMN));
        int space = (int) getResources().getDimension(R.dimen.global_margin_2dp);
        mActivityAlbumBinding.recycler.addItemDecoration(
                new RecyclerViewItemDecoration(Config.ALBUM_COLUMN, space, false));
//        mActivityAlbumBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mLocalImages.addAll(mMemoryBucket.getLocalImages());

        DisplayMetrics disPlayMetrics = DisplayUtil.getDisplayMetrics(this);
        mScreeneWidth = disPlayMetrics.widthPixels;

        mAdapter = new AlbumAdapter(presenter, mLocalImages);
        mAdapter.setExpectItemPicWidth(mScreeneWidth / 4);

        if (mLocalImages.size() <= 4) {
            mActivityAlbumBinding.header.setVisibility(View.GONE);
            mActivityAlbumBinding.recycler.setLayoutManager(
                    new GridLayoutManager(this, Config.ALBUM_COLUMN_TWO));
            int spaceDivider = (int) getResources().getDimension(R.dimen.global_margin_6dp);
            mActivityAlbumBinding.recycler.addItemDecoration(
                    new RecyclerViewItemDecoration(Config.ALBUM_COLUMN_TWO, spaceDivider, false));
        }

        mActivityAlbumBinding.recycler.setAdapter(mAdapter);

        mActivityAlbumBinding.header.attachTo(mActivityAlbumBinding.recycler);

        mActivityAlbumBinding.headerVideo.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        handler.post(updateSlideshow); // update the slideshow
                    }
                }
        );
        isInitData = true;
    }

    // anonymous inner class that implements Runnable to control slideshow
    private Runnable updateSlideshow = new Runnable() {
        @Override
        public void run() {
            if (mSlideShowInfo != null) {
                if (nextItemIndex >= mSlideShowInfo.size()) {
                    nextItemIndex = nextItemIndex % (mSlideShowInfo.size());
                }
                MediaItem item = mSlideShowInfo.getMediaItemAt(nextItemIndex);
                if (item.getType() == Global.MEDIA_TYPE_IMAGE) {
                    mActivityAlbumBinding.headerImage.setVisibility(View.VISIBLE); // show
                    mActivityAlbumBinding.headerVideo.setVisibility(View.INVISIBLE); // hide
                    loadImage(item.getPath());
                } else {
                    mActivityAlbumBinding.headerImage.setVisibility(View.INVISIBLE); // hide
                    mActivityAlbumBinding.headerVideo.setVisibility(View.VISIBLE); // show
                    playVideo(Uri.parse(item.getPath())); // plays the video
                }
                ++nextItemIndex;
            } else {
                finish();
            }

        }


        public void loadImage(String path) {

            Uri uri = Uri.parse(path);
            Glide
                    .with(AppImpl.getApp().getAndroidContext())
                    .load(uri)
                    .override(mScreeneWidth,4*mScreeneWidth/7)
                    .dontAnimate()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<?
                                super GlideDrawable> glideAnimation) {
                            Drawable previous = mActivityAlbumBinding.headerImage.getDrawable();
                            if (previous instanceof TransitionDrawable)
                                previous = ((TransitionDrawable) previous).getDrawable(1);

                            if (previous == null) {
                                mActivityAlbumBinding.headerImage.setImageDrawable(resource);
                            }
                            else {
                                Drawable[] drawables = {previous, resource};
                                TransitionDrawable transition =
                                        new TransitionDrawable(drawables);
                                mActivityAlbumBinding.headerImage.setImageDrawable(transition);
                                transition.startTransition(1000);
                            }
                            handler.postDelayed(updateSlideshow, DURATION);
                        }
                    });
        }

        // play a video
        private void playVideo(Uri videoUri) {
            // configure the video view and play video
            mActivityAlbumBinding.headerVideo.setVideoURI(videoUri);
            mActivityAlbumBinding.headerVideo.setMediaController(
                    new MediaController(AlbumActivity.this));
            mActivityAlbumBinding.headerVideo.start(); // start the video
        }
    };

    @Override
    public void notifyContentChanged(int bucket) {
        if (bucket == mBucketId) {
            updateContent();
        }
    }

    private void updateContent() {
        if (!isInitData) {
            initBinding();
            return;
        }
        if (mBucketId != -1) {
            mMemoryBucket = MemoriesManager.getInstance().getMemoryBucketByKey(mBucketId);
            mActivityAlbumBinding.setBucket(mMemoryBucket);

            ArrayList<LocalImage> list = new ArrayList<LocalImage>();
            if (null != mMemoryBucket) {
                list = mMemoryBucket.getLocalImages();
            }

            if (list.isEmpty()) {
                finish();
                return;
            }
            mLocalImages.clear();
            mLocalImages.addAll(mMemoryBucket.getLocalImages());
            mAdapter.notifyDataSetChanged();
            updateDataVersion();
        }
    }

    private void updateDataVersion() {
        mDataVersion = MemoriesManager.getInstance().getVersionSionSerial();
    }
}
