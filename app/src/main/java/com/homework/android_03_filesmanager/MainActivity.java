package com.homework.android_03_filesmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private AlertDialog dialog;
    private int curItem = -1;
    private View curView = null;
    private FileAdapter adapter;
    private ArrayList<FileView> listFiles;
    private File path;

    public Bitmap directoryPic;
    public Bitmap filePic;
    public Bitmap txtFilePic;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager AM = this.getAssets();
        try {
            InputStream IS = AM.open("directory.png");
            directoryPic = BitmapFactory.decodeStream(IS);
            IS = AM.open("file.png");
            filePic = BitmapFactory.decodeStream(IS);
            IS = AM.open("txtfile.png");
            txtFilePic = BitmapFactory.decodeStream(IS);
        } catch (IOException ioe) {
            Log.println(Log.ERROR, "error", ioe.getMessage());
        }

        gridView = this.findViewById(R.id.grid_view);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                FileView selected = adapter.getItem(i);
                path = selected.file;
                if(path.isDirectory()){
                    LoadFiles(path);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        AlertDialog.Builder builder = new	AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setMessage("Are you sure?");
        builder.setTitle("Warning");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File f = path.getParentFile();
                if(f != null){
                    path = f;
                }
            }
        });
       AlertDialog dialog = builder.create();
       dialog.setCancelable(false);
       //dialog.show();

        File esMainDir = null;
        if	(this.isExternalStorageWritable())  {
            esMainDir = Environment.getExternalStorageDirectory();
        }

        listFiles = new ArrayList<>();
        if(esMainDir!= null){
            LoadFiles(esMainDir);
        }  else  {
            Toast.makeText(this,"Directory is empty!",	Toast.LENGTH_SHORT).show();
        }

        adapter = new FileAdapter(this, R.layout.file_layout, listFiles);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (MainActivity.this.curItem != -1) {
                    MainActivity.this.curView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
                MainActivity.this.curItem = position;
                MainActivity.this.curView = view;
                MainActivity.this.curView.setBackgroundColor(Color.parseColor("#aEEEaE"));
            }
        });
    }

    private void LoadFiles(File file){
        listFiles.clear();
        File[] arrFiles = file.listFiles();
        for (File f : arrFiles){
            if (f.isDirectory()) {
                listFiles.add(new FileView(f, directoryPic));
            } else if(getFileExtension(f).equals("txt")){
                listFiles.add(new FileView(f, txtFilePic));
            }else{
                listFiles.add(new FileView(f, filePic));
            }
        }
    }

    public void backOnClick(View view){
        try {
            path = path.getParentFile();
            if(path!= null){
                LoadFiles(path);
                adapter.notifyDataSetChanged();
            }
        }catch (Exception ex){

        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    private	boolean	isExternalStorageWritable()	{
        String state = Environment.getExternalStorageState();
        return (state.equals(Environment.MEDIA_MOUNTED));
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_create_directory:

                break;
            case R.id.action_copy:

                break;
            case R.id.action_paste:
                break;
            case R.id.action_delete:
                break;
            case R.id.action_rename:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}