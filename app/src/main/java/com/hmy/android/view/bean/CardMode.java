package com.hmy.android.view.bean;

/**
 * Created by Shall on 2015-06-23.
 */
public class CardMode {
    private String name;
    private int year;
    private String images;

    public CardMode(String name, int year, String images) {
        this.name = name;
        this.year = year;
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
