package com.homework.android_03_filesmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FileAdapter extends ArrayAdapter<FileView> {
    private MainActivity mainActivity;
    private int resourceId;
    private List<FileView> files;
    public FileAdapter(Context context, int resource, List<FileView> objects) {
        super(context, resource, objects);
        this.mainActivity = (MainActivity)context;
        files = objects;
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        FileView file = files.get(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView tile = (ImageView) view.findViewById(R.id.image_view);
        TextView name = (TextView) view.findViewById(R.id.file_name);
        tile.setImageBitmap(file.tile);
        name.setText(file.name);
        return view;
    }
}
