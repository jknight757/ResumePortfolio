//Jeffrey Knight
// Java 2 1911
//CE07
package com.example.knightjeffrey_ce07_newsreader;

import java.io.Serializable;

class Article implements Serializable {

    private final String title;
    private final String imgUrl;
    private final String webUrl;
    private byte[] image;



    public Article(String title, String imgUrl, String webUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.webUrl = webUrl;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

}
