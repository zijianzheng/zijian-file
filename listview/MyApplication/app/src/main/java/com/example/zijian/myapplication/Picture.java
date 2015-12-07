package com.example.zijian.myapplication;

/**
 * Created by Zijian on 15-11-27.
 */
public class Picture {

    private String picturename;
    private String image;

    public Picture() {
        // TODO Auto-generated constructor stub
    }

    public Picture(String picturename, String image) {
        super();
        this.picturename = picturename;
        this.image = image;
    }


    public String getPictureName() {
        return picturename;
    }

    public void setPictureName(String name) {
        this.picturename = picturename;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
