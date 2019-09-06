package com.homework.android_03_filesmanager;

import android.graphics.Bitmap;

public class FileView {
    public String name;
    public Bitmap tile;

    public FileView(String name, Bitmap tile){
        this.name = name;
        this.tile =tile;
    }
}
