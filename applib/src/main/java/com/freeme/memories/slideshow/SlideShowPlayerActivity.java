// SlideshowPlayer.java
// Plays the selected slideshow that's passed as an Intent extra
package com.freeme.memories.slideshow;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.freeme.memories.R;
import com.freeme.memories.base.AppImpl;
import com.freeme.memories.constant.Global;
import com.freeme.memories.data.bucket.MemoryBucket;
import com.freeme.memories.data.entity.LocalImage;
import com.freeme.memories.data.manager.IMemoriesDataNotifier;
import com.freeme.memories.data.manager.MemoriesManager;
import com.freeme.memories.utils.DisplayUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class SlideShowPlayerActivity extends Activity implements IMemoriesDataNotifier {
    private static final String TAG = "SlideShowPlayerActivity";

    private static final int DURATION = 3000; // 5 seconds per slide

    private ImageView mImageView; // displays the current image
    private VideoView mVideoView; // displays the current video
    private ImageView mImgPauseButton;

    private String slideshowName; // name of current slideshow
    private SlideshowInfo mSlideshowInfo; // slideshow being played
    private Handler handler; // used to update the slideshow
    private int nextItemIndex; // index of the next image to display
    private int mediaTime; // time in ms from which media should play
    private MediaPlayer mMediaPlayer; // plays the background music, if any
    private int mMediaResId;
    private long mDataVersion;
    private boolean mPaused = false;
    private int mPausedIndex = 0;
    private int mScreeneWidth = 700;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不自动锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager
                .LayoutParams.FLAG_KEEP_SCREEN_ON);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.slideshow_player);

        if (savedInstanceState == null) {
            // get slideshow name from Intent's extras
            slideshowName = getIntent().getStringExtra(Constants.SLIDE_SHOW_NAME);
            mediaTime = 0; // position in media clip
            nextItemIndex = 0; // start from first image

        } else {
            // get the play position that was saved when config changed
            mediaTime = savedInstanceState.getInt(Constants.MEDIA_TIME);

            // get index of image that was displayed when config changed
            nextItemIndex = savedInstanceState.getInt(Constants.IMAGE_INDEX);

            // get name of slideshow that was playing when config changed
            slideshowName = savedInstanceState.getString(Constants.SLIDESHOW_NAME);
        }
        initView();
        getSlideShowInfo();
        initMediaPlayer();

        handler = new Handler(); // create handler to control slideshow
    }

    private void initView() {

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击暂停,再点击播放
                if (mPaused) {
                    mMediaPlayer.start();
                    nextItemIndex = mPausedIndex;
                    handler.post(updateSlideshow);
                    mImgPauseButton.setVisibility(View.GONE);
                    mPaused = false;
                } else {
                    mMediaPlayer.pause();
                    handler.removeCallbacks(updateSlideshow);
                    mPausedIndex = nextItemIndex - 1;
                    mImgPauseButton.setVisibility(View.VISIBLE);
                    mPaused = true;
                }
            }
        });
        mImgPauseButton = (ImageView) findViewById(R.id.pause_button);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        // set video completion handler
        mVideoView.setOnCompletionListener(
                new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        handler.post(updateSlideshow); // update the slideshow
                    }
                }
        );
        DisplayMetrics disPlayMetrics = DisplayUtil.getDisplayMetrics(this);
        mScreeneWidth = disPlayMetrics.widthPixels;
    }

    private void getSlideShowInfo() {
        mSlideshowInfo = (SlideshowInfo) getIntent().getSerializableExtra(Constants
                .SLIDE_SHOW_INFO);
        mDataVersion = MemoriesManager.getInstance().getVersionSionSerial();
        getSlideMemoryType(mSlideshowInfo.getMemoryType());
    }

    private void getSlideMemoryType(int memoryType) {
        switch (memoryType) {
            case Global.MEMORIES_TYPE_LAST_WEEK:
                mMediaResId = R.raw.lastweek;
                break;

            case Global.MEMORIES_TYPE_LAST_YEAR_TODAY:
                mMediaResId = R.raw.lastyeartoday;
                break;

            case Global.MEMORIES_TYPE_LAST_MONTH:
                mMediaResId = R.raw.lastmonth;
                break;

            case Global.MEMORIES_TYPE_LOCATION:
                mMediaResId = R.raw.location;
                break;
            default:
                mMediaResId = R.raw.lastweek;
                break;
        }
    }

    private void initMediaPlayer() {
        // try to create a MediaPlayer to play the music
        try {
            mMediaPlayer = MediaPlayer.create(this, mMediaResId);
            //mMediaPlayer.prepare(); // prepare the MediaPlayer to play
            mMediaPlayer.setLooping(true); // loop the music
            mMediaPlayer.seekTo(mediaTime); // seek to mediaTime
        } catch (IllegalStateException e) {
            e.getStackTrace();
        }

        //设置选中音乐循环播放
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.start();
                mMediaPlayer.setLooping(true);
            }
        });
    }

    // called after onCreate and sometimes onStop
    @Override
    protected void onStart() {
        super.onStart();
        if (!mPaused) {
            handler.post(updateSlideshow); // post updateSlideshow to execute
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MemoriesManager.getInstance().unRegisterChangeNotifier(this);
        if (mMediaPlayer != null)
            mMediaPlayer.pause(); // pause playback
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (!mPaused) {
            if (mMediaPlayer != null) {
                mMediaPlayer.start(); // resume playback
            }
        }
        MemoriesManager.getInstance().registerChangeNotifier(this);
        if (mDataVersion != MemoriesManager.getInstance().getVersionSionSerial()) {
            updateContent();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        // prevent slideshow from operating when in background
        handler.removeCallbacks(updateSlideshow);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null)
            mMediaPlayer.release(); // release MediaPlayer resources
    }

    // save slideshow state so it can be restored in onCreate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // if there is a mMediaPlayer, store media's current position
        if (mMediaPlayer != null)
            outState.putInt(Constants.MEDIA_TIME, mMediaPlayer.getCurrentPosition());

        // save nextItemIndex and slideshowName
        outState.putInt(Constants.IMAGE_INDEX, nextItemIndex - 1);
        outState.putString(Constants.SLIDESHOW_NAME, slideshowName);
    }

    // anonymous inner class that implements Runnable to control slideshow
    private Runnable updateSlideshow = new Runnable() {
        @Override
        public void run() {
            synchronized (mSlideshowInfo) {
                if (nextItemIndex >= mSlideshowInfo.size()) {
                    nextItemIndex = nextItemIndex % (mSlideshowInfo.size());
                }

                MediaItem item = mSlideshowInfo.getMediaItemAt(nextItemIndex);
                if (item.getType() == Global.MEDIA_TYPE_IMAGE) {
                    mImageView.setVisibility(View.VISIBLE); // show mImageView
                    mVideoView.setVisibility(View.INVISIBLE); // hide mVideoView
                    loadImage(item.getPath());
                } else {
                    mImageView.setVisibility(View.INVISIBLE); // hide mImageView
                    mVideoView.setVisibility(View.VISIBLE); // show mVideoView
                    playVideo(Uri.parse(item.getPath())); // plays the video
                }
                ++nextItemIndex;
            }
        }

        public void loadImage(String path) {
            Uri uri = Uri.parse(path);
            Glide
                    .with(AppImpl.getApp().getAndroidContext())
                    .load(uri)
                    .override(mScreeneWidth, 16 * mScreeneWidth / 9)
                    .dontAnimate()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<?
                                super GlideDrawable> glideAnimation) {
                            if (isDestroyed() || isFinishing()) {
                                return;
                            }
                            Drawable previous = mImageView.getDrawable();
                            if (previous instanceof TransitionDrawable)
                                previous = ((TransitionDrawable) previous).getDrawable(1);

                            if (previous == null)
                                mImageView.setImageDrawable(resource);
                            else {
                                Drawable[] drawables = {previous, resource};
                                TransitionDrawable transition =
                                        new TransitionDrawable(drawables);
                                mImageView.setImageDrawable(transition);
                                transition.startTransition(1000);
                            }

                            handler.postDelayed(updateSlideshow, DURATION);
                        }
                    });

        }

        // play a video
        private void playVideo(Uri videoUri) {
            // configure the video view and play video
            mVideoView.setVideoURI(videoUri);
            mVideoView.setMediaController(
                    new MediaController(SlideShowPlayerActivity.this));
            mVideoView.start(); // start the video
        }
    };

    public void updateContent() {
        synchronized (mSlideshowInfo) {
            MemoryBucket memoryBucket = MemoriesManager.getInstance().getMemoryBucketByKey
                    (mSlideshowInfo.getSlideBucketId());
            List<LocalImage> imageList = new ArrayList<>();
            if (null != memoryBucket) {
                imageList = memoryBucket.getLocalImages();
            }
            if (imageList.isEmpty()) {
                finish();
                return;
            }
            mSlideshowInfo.clearMedia();

            mDataVersion = MemoriesManager.getInstance().getVersionSionSerial();
            if (imageList == null) {
                finish();
            }
            if (null != imageList && imageList.size() > 0) {
                for (int i = 0; i < imageList.size(); i++) {
                    LocalImage localImage = imageList.get(i);
                    if (null != localImage) {
                        //media or video
                        int type = localImage.getMediaType();
                        String strUri = localImage.getUri().toString();
                        mSlideshowInfo.addMediaItem(type, strUri);
                    }
                }
            }
        }
    }

    @Override
    public void notifyContentChanged(int type) {
        if (type == mSlideshowInfo.getSlideBucketId()) {
            updateContent();
        }
    }
}

