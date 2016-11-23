package com.freeme.memories.adapter;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.freeme.memories.R;
import com.freeme.memories.base.AppImpl;
import com.freeme.memories.data.bucket.MemoryBucket;
import com.freeme.memories.databinding.MemoriesItemBinding;
import com.freeme.memories.presenter.AlbumSetPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: connorlin
 * Date: Created on 2016/10/3.
 */
public class AlbumSetAdapter extends RecyclerView.Adapter<AlbumSetAdapter.BindingHolder> {

    List<MemoryBucket> mMemoryBucketList = new ArrayList<>();
    private AlbumSetPresenter mAlbumSetPresenter;

    public AlbumSetAdapter(AlbumSetPresenter presenter, List<MemoryBucket> memoryBuckets) {
        mAlbumSetPresenter = presenter;
        mMemoryBucketList = memoryBuckets;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MemoriesItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.memories_item, parent, false);
        binding.setPresenter(mAlbumSetPresenter);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        holder.getBinding().setBucket(mMemoryBucketList.get(position));
        holder.getBinding().setPosition(position);
//        ImageLoadManager.getInstance()
//                .displayImage(mMemoryBucketList.get(position).getCoverPath(),
//                        holder.getBinding().itemThumbnail);
//        holder.getBinding().executePendingBindings();
        String path = mMemoryBucketList.get(position).getCoverPath();
        if (path == null) {
            return;
        }
        Uri uri = Uri.parse(path);
        Glide
                .with(AppImpl.getApp().getAndroidContext())
                .load(uri)
                .thumbnail(0.1f)
                .into(holder.getBinding().itemThumbnail);
    }

    @Override
    public int getItemCount() {
        return mMemoryBucketList.size();
    }

    public class BindingHolder extends RecyclerView.ViewHolder {
        private MemoriesItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
        }

        public MemoriesItemBinding getBinding() {
            return binding;
        }

        public void setBinding(MemoriesItemBinding binding) {
            this.binding = binding;
        }
    }
}
