package com.example.knightjeffrey_ce07_canvasdrawing.models;

public class Item {
    private final String name;
    private final int value;
    private final int imgId;


    public Item(String name, int value, int imgId) {
        this.name = name;
        this.value = value;
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getImgId() {
        return imgId;
    }
}
