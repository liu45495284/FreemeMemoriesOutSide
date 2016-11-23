package com.freeme.memories.ui;

/**
 * ClassName: CycleViewPager
 * Description:
 * Author: connorlin
 * Date: Created on 2016-9-1.
 */

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.freeme.memories.R;
import com.freeme.memories.base.AppImpl;
import com.freeme.memories.base.BaseHandler;
import com.freeme.memories.base.BaseViewPager;
import com.freeme.memories.data.entity.LocalImage;
import com.freeme.memories.utils.ImageViewFactory;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewer extends Fragment implements OnPageChangeListener {

    private static final int DURATION = 1000;
    private static final int AUTO_DURATION = 3000;
    private List<ImageView> imageViews = new ArrayList<>();
    private ImageView[] indicators;
    private FrameLayout viewPagerFragmentLayout;
    private LinearLayout indicatorLayout; // 指示器
    private BaseViewPager viewPager;
    private BaseViewPager parentViewPager;
    private ViewPagerAdapter adapter;
    private BaseHandler handler;
    private int time = AUTO_DURATION; // 默认轮播时间
    private int currentPosition = 0; // 轮播当前位置
    private boolean isScrolling = false; // 滚动框是否滚动着
    private boolean isCycle = false; // 是否循环
    private boolean isWheel = false; // 是否轮播
    private boolean showIndicator = true;
    private long releaseTime = 0; // 手指松开、页面不滚动时间，防止手机松开后短时间进行切换
    private int WHEEL = 100; // 转动
    private int WHEEL_WAIT = 101; // 等待
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null && !getActivity().isFinishing() && isWheel) {
                long now = System.currentTimeMillis();
                // 检测上一次滑动时间与本次之间是否有触击(手滑动)操作，有的话等待下次轮播
                if (now - releaseTime > time - DURATION) {
                    handler.sendEmptyMessage(WHEEL);
                } else {
                    handler.sendEmptyMessage(WHEEL_WAIT);
                }
            }
        }
    };
    private ImageCycleViewListener mImageCycleViewListener;

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        viewPager.setCurrentItem(currentPosition, true);
    }

    public void setShowIndicator(boolean showIndicator) {
        this.showIndicator = showIndicator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.view_cycle_viewpager_contet, null);

        viewPager = (BaseViewPager) view.findViewById(R.id.viewPager);
        indicatorLayout = (LinearLayout) view
                .findViewById(R.id.layout_viewpager_indicator);

        viewPagerFragmentLayout = (FrameLayout) view
                .findViewById(R.id.layout_viewager_content);

        handler = new BaseHandler(getActivity()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == WHEEL && isWheel && imageViews.size() > 1) {
                    currentPosition++;
                    if (currentPosition > imageViews.size() - 1) {
                        currentPosition = 0;
                    }
                    viewPager.setCurrentItem(currentPosition, true);

                    releaseTime = System.currentTimeMillis();
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, time);
                    return;
                }

                if (msg.what == WHEEL_WAIT && imageViews.size() != 0) {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, time);
                }
            }
        };

        return view;
    }

    public void setData(List<ImageView> views) {
        setData(views, null);
    }

    public void setData(List<ImageView> views, ImageCycleViewListener listener) {
        setData(views, listener, 0);
    }

    /**
     * 初始化viewpager
     *
     * @param views        要显示的views
     * @param showPosition 默认显示位置
     */
    public void setData(List<ImageView> views, ImageCycleViewListener listener, int showPosition) {
        mImageCycleViewListener = listener;
        this.imageViews.clear();

        if (views.size() == 0) {
            viewPagerFragmentLayout.setVisibility(View.GONE);
            return;
        }

        for (ImageView item : views) {
            this.imageViews.add(item);
        }

        int ivSize = views.size();

        // 设置指示器
        indicators = new ImageView[ivSize];

        if (ivSize < 2) {
            setWheel(false);
            setCycle(false);
        }

        indicatorLayout.removeAllViews();

        if (showIndicator) {
            for (int i = 0; i < indicators.length; i++) {
                View view = LayoutInflater.from(getActivity()).inflate(
                        R.layout.view_cycle_viewpager_indicator, null);
                indicators[i] = (ImageView) view.findViewById(R.id.image_indicator);
                indicatorLayout.addView(view);
            }
        }

        adapter = new ViewPagerAdapter();

        // 默认指向第一项，下方viewPager.setCurrentItem将触发重新计算指示器指向
        setIndicator(0);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
        if (showPosition < 0 || showPosition >= views.size()) {
            showPosition = 0;
        }
        viewPager.setCurrentItem(showPosition);
    }

    /**
     * 设置指示器
     *
     * @param selectedPosition 默认指示器位置
     */
    private void setIndicator(int selectedPosition) {
        if (!showIndicator) {
            return;
        }

        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(R.drawable.indicator_normal);
        }

        if (indicators.length > selectedPosition) {
            indicators[selectedPosition].setBackgroundResource(R.drawable.indicator_focused);
        }
    }

    /**
     * 设置指示器居中，默认指示器在右方
     */
    public void setIndicatorCenter() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        indicatorLayout.setLayoutParams(params);
    }

    /**
     * 是否处于循环状态
     *
     * @return
     */
    public boolean isCycle() {
        return isCycle;
    }

    /**
     * 是否循环，默认不开启，开启前，请将views的最前面与最后面各加入一个视图，用于循环
     *
     * @param isCycle 是否循环
     */
    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    /**
     * 是否处于轮播状态
     *
     * @return
     */
    public boolean isWheel() {
        return isWheel;
    }

    /**
     * 设置是否轮播，默认不轮播,轮播一定是循环的
     *
     * @param isWheel
     */
    public void setWheel(boolean isWheel) {
        this.isWheel = isWheel;
        isCycle = true;
        if (isWheel) {
            handler.postDelayed(runnable, time);
        } else {
            handler.removeCallbacks(runnable);
        }
    }

    /**
     * 释放指示器高度，可能由于之前指示器被限制了高度，此处释放
     */
    public void releaseHeight() {
        getView().getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
        refreshData();
    }

    /**
     * 刷新数据，当外部视图更新后，通知刷新数据
     */
    public void refreshData() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置轮播暂停时间，即没多少秒切换到下一张视图.默认5000ms
     *
     * @param time 毫秒为单位
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * 隐藏CycleViewPager
     */
    public void hide() {
        viewPagerFragmentLayout.setVisibility(View.GONE);
    }

    /**
     * 返回内置的viewpager
     *
     * @return viewPager
     */
    public BaseViewPager getViewPager() {
        return viewPager;
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        setIndicator(arg0);
        currentPosition = arg0;
        releaseTime = System.currentTimeMillis();
        viewPager.setCurrentItem(currentPosition, true);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    /**
     * 设置viewpager是否可以滚动
     *
     * @param enable
     */
    public void setScrollable(boolean enable) {
        viewPager.setScrollable(enable);
    }

    /**
     * 返回当前位置,循环时需要注意返回的position包含之前在views最前方与最后方加入的视图，即当前页面试图在views集合的位置
     *
     * @return
     */
    public int getCurrentPostion() {
        return currentPosition;
    }

    /**
     * 如果当前页面嵌套在另一个viewPager中，为了在进行滚动时阻断父ViewPager滚动，可以 阻止父ViewPager滑动事件
     * 父ViewPager需要实现ParentViewPager中的setScrollable方法
     */
    public void disableParentViewPagerTouchEvent(BaseViewPager parentViewPager) {
        if (parentViewPager != null) {
            parentViewPager.setScrollable(false);
        }
    }

    /**
     * 轮播控件的监听事件
     */
    public interface ImageCycleViewListener {

        /**
         * 单击图片事件
         *
         * @param imageView
         */
        void onImageClick(int postion, View imageView);
    }

    /**
     * 页面适配器 返回对应的view
     */
    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            ImageView v = imageViews.get(position);

            LocalImage image = (LocalImage) v.getTag();

            ImageView imageView = ImageViewFactory.getImageView(getActivity(),image.getData());
//            Uri uri = Uri.parse();

            Glide
                    .with(AppImpl.getApp().getAndroidContext())
                    .load(image.getUri())
//                    .thumbnail(0.1f)
                    .into(imageView);

            if (mImageCycleViewListener != null) {
                imageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mImageCycleViewListener.onImageClick(currentPosition, v);
                    }
                });
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
