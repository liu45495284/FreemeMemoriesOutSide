// MediaItem.java
// Represents an image or video in a slideshow.
package com.freeme.memories.slideshow;

import java.io.Serializable;

public class MediaItem implements Serializable {
    private static final long serialVersionUID = 1L; // class's version #

    /*// constants for media types
    public static enum MediaType {
        IMAGE, VIDEO
    }*/

    private int type; // this MediaItem is an IMAGE or VIDEO
    private String path; // location of this MediaItem

    public MediaItem(int mediaType, String location) {
        type = mediaType;
        path = location;
    }

    // get the MediaType of this image or video
    public int getType() {
        return type;
    }

    // return the description of this image or video
    public String getPath() {
        return path;
    }
}


