//Jeffrey Knight
//Java2 1911
// CE04 contacts
package com.example.knightjeffrey_ce04_java2;

import java.io.Serializable;
import java.util.ArrayList;

public class Contact implements Serializable {
    private final String name;
    private final ArrayList<String> nums;
    private final String imgPath;

    public Contact(String name, ArrayList<String> nums, String imgPath) {
        this.name = name;
        this.nums = nums;
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getNums() {
        return nums;
    }

    public String getImgPath() {
        return imgPath;
    }
}
