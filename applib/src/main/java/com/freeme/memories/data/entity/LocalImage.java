package com.freeme.memories.data.entity;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Description: 单张图片类
 * Author: connorlin
 * Date: Created on 2016-10-19.
 */
public class LocalImage {
    private Long id;
    private String data;
    private Integer size;
    private String display_name;
    private String title;
    private Integer date_added;
    private Integer date_modified;
    private String mime_type;
    private Integer width;
    private Integer height;

    private String description;
    private Double latitude;
    private Double longitude;
    private Integer datetaken;
    private Integer orientation;
    private Integer mini_thumb_magic;
    private String bucket_id;
    private String bucket_display_name;

    private Integer duration;
    private String artist;
    private String album;
    private String resolution;

    private Integer media_type;
    private Uri uri;

    private Bitmap bitmap;

    private LocalImage(Builder builder) {
        this.id = builder.id;
        this.data = builder.data;
        this.size = builder.size;
        this.display_name = builder.display_name;
        this.title = builder.title;
        this.date_added = builder.date_added;
        this.date_modified = builder.date_modified;
        this.mime_type = builder.mime_type;
        this.width = builder.width;
        this.height = builder.height;

        this.description = builder.description;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.datetaken = builder.datetaken;
        this.orientation = builder.orientation;
        this.mini_thumb_magic = builder.mini_thumb_magic;
        this.bucket_id = builder.bucket_id;
        this.bucket_display_name = builder.bucket_display_name;

        this.duration = builder.duration;
        this.artist = builder.artist;
        this.album = builder.album;
        this.resolution = builder.resolution;

        this.media_type = builder.media_type;

        this.uri = builder.uri;
    }

    @Deprecated
    public Bitmap getBitmap() {
//        if (bitmap == null) {
//            ImageSize size =
//                    new ImageSize(Config.ALBUM_THUMBNAIL_WIDTH, Config.ALBUM_THUMBNAIL_HEIGHT);
//            bitmap = ImageLoadManager.getInstance().loadImageSync(AppUtil.getDataPath(data), size);
//        }
//        return bitmap;
        return null;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getDisplayName() {
        return display_name;
    }

    public void setDisplayName(String display_name) {
        this.display_name = display_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDateAdded() {
        return date_added;
    }

    public void setDateAdded(Integer date_added) {
        this.date_added = date_added;
    }

    public String getMimeType() {
        return mime_type;
    }

    public void setMimeType(String mime_type) {
        this.mime_type = mime_type;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getDatetaken() {
        return datetaken;
    }

    public void setDatetaken(Integer datetaken) {
        this.datetaken = datetaken;
    }

    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public Integer getMiniThumbMagic() {
        return mini_thumb_magic;
    }

    public void setMiniThumbMagic(Integer mini_thumb_magic) {
        this.mini_thumb_magic = mini_thumb_magic;
    }

    public String getBucketId() {
        return bucket_id;
    }

    public void setBucketId(String bucket_id) {
        this.bucket_id = bucket_id;
    }

    public String getBucketDisplayName() {
        return bucket_display_name;
    }

    public void setBucketDisplayName(String bucket_display_name) {
        this.bucket_display_name = bucket_display_name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Integer getMediaType() {
        return media_type;
    }

    public void setMediaType(Integer media_type) {
        this.media_type = media_type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Integer getDateModified() {
        return date_modified;
    }

    public void setDateModified(Integer date_modified) {
        this.date_modified = date_modified;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static class Builder {
        private Long id;
        private String data;
        private Integer size;
        private String display_name;
        private String title;
        private Integer date_added;
        private Integer date_modified;
        private String mime_type;
        private Integer width;
        private Integer height;

        private String description;
        private Double latitude;
        private Double longitude;
        private Integer datetaken;
        private Integer orientation;
        private Integer mini_thumb_magic;
        private String bucket_id;
        private String bucket_display_name;

        private Integer duration;
        private String artist;
        private String album;
        private String resolution;

        private Integer media_type;

        private Uri uri;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public Builder setSize(Integer size) {
            this.size = size;
            return this;
        }

        public Builder setDisplay_name(String display_name) {
            this.display_name = display_name;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDateAdded(Integer date_added) {
            this.date_added = date_added;
            return this;
        }

        public Builder setDateModified(Integer date_modified) {
            this.date_modified = date_modified;
            return this;
        }

        public Builder setMimeType(String mime_type) {
            this.mime_type = mime_type;
            return this;
        }

        public Builder setWidth(Integer width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(Integer height) {
            this.height = height;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setDateTaken(Integer datetaken) {
            this.datetaken = datetaken;
            return this;
        }

        public Builder setOrientation(Integer orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setMiniThumbMagic(Integer mini_thumb_magic) {
            this.mini_thumb_magic = mini_thumb_magic;
            return this;
        }

        public Builder setBucketId(String bucket_id) {
            this.bucket_id = bucket_id;
            return this;
        }

        public Builder setBucketDisplayName(String bucket_display_name) {
            this.bucket_display_name = bucket_display_name;
            return this;
        }

        public Builder setDuration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public Builder setArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder setAlbum(String album) {
            this.album = album;
            return this;
        }

        public Builder setResolution(String resolution) {
            this.resolution = resolution;
            return this;
        }

        public Builder setMediaType(Integer media_type) {
            this.media_type = media_type;
            return this;
        }

        public Builder setUri(Uri uri) {
            this.uri = uri;
            return this;
        }

        public LocalImage build() {
            return new LocalImage(this);
        }
    }
}
