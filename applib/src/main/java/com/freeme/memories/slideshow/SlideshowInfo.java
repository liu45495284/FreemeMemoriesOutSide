// SlideshowInfo.java
// Stores the data for a single slideshow.
package com.freeme.memories.slideshow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SlideshowInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name; // name of this slideshow
    private int memoryType;
    private List<MediaItem> mediaItemList; // this slideshow's images
    private String musicPath; // location of music to play
    private int slideBucketId;

    public int getSlideBucketId() {
        return slideBucketId;
    }

    public void setSlideBucketId(int slideBucketId) {
        this.slideBucketId = slideBucketId;
    }

    public SlideshowInfo(String slideshowName) {
        name = slideshowName; // set the slideshow name
        mediaItemList = new ArrayList<MediaItem>();
        musicPath = null; // currently there is no music for the slideshow
    }

    // return this slideshow's name
    public String getName() {
        return name;
    }

    // return List of MediaItems pointing to the slideshow's images
    public List<MediaItem> getMediaItemList() {
        return mediaItemList;
    }

    // add a new MediaItem
    public void addMediaItem(int type, String path) {
        mediaItemList.add(new MediaItem(type, path));
    }

    // add a new MediaItem
    public void clearMedia() {
        mediaItemList.clear();
    }

    // return MediaItem at position index
    public MediaItem getMediaItemAt(int index) {
        if (index >= 0 && index < mediaItemList.size())
            return mediaItemList.get(index);
        else
            return null;
    }

    // return this slideshow's music
    public String getMusicPath() {
        return musicPath;
    }

    // set this slideshow's music
    public void setMusicPath(String path) {
        musicPath = path;
    }

    // return number of images/videos in the slideshow
    public int size() {
        return mediaItemList.size();
    }

    public int getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(int memoryType) {
        this.memoryType = memoryType;
    }
}

