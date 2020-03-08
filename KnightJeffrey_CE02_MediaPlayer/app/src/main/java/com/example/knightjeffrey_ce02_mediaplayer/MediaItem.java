// Jeffrey knight
// MDF3
// CE02 Media player
package com.example.knightjeffrey_ce02_mediaplayer;

import android.net.Uri;


class MediaItem  {
    private String title;
    private final Uri mp3Asset;
    private byte[] albumArt;
    private final Double duration;

    public MediaItem(String title, Uri mp3Asset, byte[] albumArt, Double duration) {
        this.title = title;
        this.mp3Asset = mp3Asset;
        this.albumArt = albumArt;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public Uri getMp3Asset() {
        return mp3Asset;
    }

    public byte[] getAlbumArt() {
        return albumArt;
    }

    public Double getDuration() {
        return duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setAlbumArt(byte[] albumArt) {
        this.albumArt = albumArt;
    }

}
