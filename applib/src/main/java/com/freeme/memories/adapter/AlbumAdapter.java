package com.freeme.memories.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.freeme.memories.R;
import com.freeme.memories.base.AppImpl;
import com.freeme.memories.data.entity.LocalImage;
import com.freeme.memories.databinding.AlbumItemBinding;
import com.freeme.memories.presenter.AlbumPresenter;

import java.util.ArrayList;
import java.util.List;

//import com.freeme.memories.utils.FrescoUtils;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016/10/3.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.BindingHolder> {

    private AlbumPresenter mAlbumPresenter;
    private List<LocalImage> mLocalImages = new ArrayList<>();
    private int mExpectItemPictureSize = 200;

    public AlbumAdapter(AlbumPresenter presenter, List<LocalImage> localImages) {
        mAlbumPresenter = presenter;
        mLocalImages = localImages;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AlbumItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.album_item, parent, false);
        binding.setPresenter(mAlbumPresenter);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        holder.getBinding().setPosition(position);
        holder.getBinding().setItem(mLocalImages.get(position));
        String path = mLocalImages.get(position).getUri().toString();
        Uri uri = Uri.parse(path);
        Glide
                .with(AppImpl.getApp().getAndroidContext())
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(mExpectItemPictureSize,mExpectItemPictureSize)
                .centerCrop()
                .thumbnail(0.1f)
                .into(holder.getBinding().itemThumbnail);
    }

    @Override
    public int getItemCount() {
        return mLocalImages.size();
    }

    public class BindingHolder extends RecyclerView.ViewHolder {
        private AlbumItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
        }

        public AlbumItemBinding getBinding() {
            return binding;
        }

        public void setBinding(AlbumItemBinding binding) {
            this.binding = binding;
        }
    }

    public void setExpectItemPicWidth(int width){
        mExpectItemPictureSize = width;
    }

}
