package com.freeme.memories.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.droi.adcontrol.banner.AdControlBanner;
import com.droi.adcontrol.banner.BannerListener;
import com.freeme.memories.R;
import com.freeme.memories.actionbar.entity.ActionBarConfig;
import com.freeme.memories.adapter.AlbumSetAdapter;
import com.freeme.memories.base.AppImpl;
import com.freeme.memories.base.BaseActivity;
import com.freeme.memories.constant.Global;
import com.freeme.memories.data.bucket.MemoryBucket;
import com.freeme.memories.data.manager.IMemoriesDataNotifier;
import com.freeme.memories.data.manager.MemoriesManager;
import com.freeme.memories.databinding.ActivityAlbumSetBinding;
import com.freeme.memories.presenter.AlbumSetPresenter;
import com.freeme.memories.ui.RecyclerViewItemDecoration;
import com.freeme.memories.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class AlbumSetActivity extends BaseActivity implements IMemoriesDataNotifier {
    public static final int MSG_MEMORY_ITEM = 100;
    public static final int MSG_MEMORY_ITEM_TYPE = 0x0101;
    public static final String TAG = "AlbumSetActivity";

    private ActivityAlbumSetBinding mActivityAlbumSetBinding;
    private ActionBarConfig mActionBarConfig;
    private AlbumSetAdapter mAdapter;

    private List<MemoryBucket> mMemoryList = new ArrayList<>();
    private long mDataVersion = -1;

    private String bannerPosition = "banner_01";
    private AdControlBanner banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "Kathy onCreate");
        mActivityAlbumSetBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_album_set);
        initBinding();
        createBannerAd();
    }

    private void createBannerAd() {
        banner = new AdControlBanner(this, bannerPosition);
        banner.setListener(new BannerListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailed() {
                Log.d(TAG, "onAdFailed");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }
        });
        mActivityAlbumSetBinding
                .adContainer.addView(banner, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void setUpActionBar() {
        super.setUpActionBar();
        mActionBarConfig = new ActionBarConfig();
        mActionBarConfig.setTitle(getString(R.string.app_name));
    }

    @Override
    protected void onResume() {
        if (banner != null) {
            banner.resume();
        }
        super.onResume();
        MobclickAgent.onResume(this);
        LogUtil.d(TAG, "Kathy - AlbumSetActivity - onResume - AppImpl.isShowEmpty = " + AppImpl
                .isShowEmpty);
        MemoriesManager.getInstance().registerChangeNotifier(this);
        if (mDataVersion != MemoriesManager.getInstance().getVersionSionSerial()) {
            updateContent();
        }

        if (AppImpl.isShowEmpty && mMemoryList.size() == 0) {
            mActivityAlbumSetBinding.emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        if (banner != null) {
            banner.pause();
        }
        super.onPause();
        MobclickAgent.onPause(this);
        LogUtil.d(TAG, "Kathy - AlbumSetActivity - onPause");
        MemoriesManager.getInstance().unRegisterChangeNotifier(this);
    }

    @Override
    protected void onDestroy() {
        if (banner != null) {
            banner.destroy();
            banner = null;
        }
        super.onDestroy();
    }

    @Override
    public void onHandle(int type, Object object) {
        switch (type) {
            case MSG_MEMORY_ITEM:
                LogUtil.i("MSG_MEMORY_ITEM");
                Intent intent = new Intent(AlbumSetActivity.this, AlbumActivity.class);
                intent.putExtra(Global.CLICK_BUCKETID_INDEX, (int) object);
//                Bundle bundle = new Bundle();
//                LogUtil.i("(MemoryBucket) object = " + ((MemoryBucket) object).getLocalImages());
//                LogUtil.i("(MemoryBucket) object size = " + ((MemoryBucket) object)
// .getLocalImages().size());
//                bundle.putParcelable(Global.EXTRA_MEMORY_BUCKET, (MemoryBucket) object);
//                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private void initBinding() {
        mActivityAlbumSetBinding.setActionbarconfig(mActionBarConfig);
        mActivityAlbumSetBinding.recycler.setHasFixedSize(true);
        mActivityAlbumSetBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        int space = (int) getResources().getDimension(R.dimen.global_margin_6dp);
        mActivityAlbumSetBinding.recycler.addItemDecoration(
                new RecyclerViewItemDecoration(1, space, false));
        mActivityAlbumSetBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        AlbumSetPresenter presenter = new AlbumSetPresenter(this);

        List<MemoryBucket> list = MemoriesManager.getInstance().getMemoryBucketList();
        mMemoryList.addAll(list);
        updateDataVersion();

        mAdapter = new AlbumSetAdapter(presenter, mMemoryList);
        mActivityAlbumSetBinding.recycler.setAdapter(mAdapter);
    }

    private void updateContent() {

        List<MemoryBucket> list = MemoriesManager.getInstance().getMemoryBucketList();
        mMemoryList.clear();
        if (list != null && list.size() != 0) {
            mActivityAlbumSetBinding.emptyView.setVisibility(View.GONE);
            AppImpl.isShowEmpty = false;
        } else {
            mActivityAlbumSetBinding.emptyView.setVisibility(View.VISIBLE);
            AppImpl.isShowEmpty = true;
        }
        LogUtil.d(TAG, "Kathy - AlbumSetActivity - updateContent - isShowEmpty = " + AppImpl
                .isShowEmpty);

        mMemoryList.addAll(list);
        updateDataVersion();
        mAdapter.notifyDataSetChanged();
    }

    private void updateDataVersion() {
        mDataVersion = MemoriesManager.getInstance().getVersionSionSerial();
    }

    @Override
    public void notifyContentChanged(int type) {
        LogUtil.d(TAG, "Kathy - AlbumSetActivity - notifyContentChanged");
        updateContent();
    }
}