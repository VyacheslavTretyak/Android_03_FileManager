package com.homework.android_03_filesmanager;

import android.graphics.Bitmap;

import java.io.File;

public class FileView {
    public String name;
    public File file;
    public Bitmap tile;

    public FileView(File file, Bitmap tile){
        this.name = file.getName();
        this.file = file;
        this.tile =tile;
    }
}
