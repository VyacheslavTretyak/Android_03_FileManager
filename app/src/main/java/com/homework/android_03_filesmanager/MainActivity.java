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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private AlertDialog dialog;
    private int curItem = -1;
    private View curView = null;
    private FileAdapter adapter;
    private ArrayList<FileView> listFiles;
    private File path;
    private File copiedFile;
    private AlertDialog dialogYesNo;
    private AlertDialog dialogCreate;
    private AlertDialog dialogRename;
    private File esMainDir;

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
        builder.setTitle("Create directory");
        LayoutInflater inflater	= this.getLayoutInflater();
        final View view	= inflater.inflate(R.layout.create_dir_layout,null,false);
        builder.setView(view);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                EditText editText = view.findViewById(R.id.edit_create_dir);
                File f = new File(path.getPath()+"//"+editText.getText());
                f.mkdir();
                adapter.add(new FileView(f, directoryPic));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogCreate = builder.create();

        builder = new	AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setTitle("Rename file");
        LayoutInflater inflater1	= this.getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.create_dir_layout,null,false);
        builder.setView(view);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                EditText editText = view.findViewById(R.id.edit_create_dir);
                File f = adapter.getItem(curItem).file;
                File newfile = new File(path, editText.getText().toString());
                f.renameTo(newfile);
                LoadFiles(path);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogRename = builder.create();



        builder = new	AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setMessage("Are you sure?");
        builder.setTitle("Warning");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                Delete(adapter.getItem(curItem).file);
                LoadFiles(path);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
       dialogYesNo = builder.create();
       //dialog.show();

        esMainDir = null;
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
        Button button = findViewById(R.id.button_back);
        if(file.getPath().equals(esMainDir.getPath())){
            button.setEnabled(false);
        }else{
            button.setEnabled(true);
        }
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

    private void Paste(File file, File path) throws IOException {
        if(file == null){
            return;
        }
        if(file.isDirectory()){
            File d = new File(path.getPath()+"//"+file.getName());
            d.mkdir();
            File[] files = file.listFiles();
            for(File f:files) {
                Paste(f, d);
            }
        } else {
            Copy(file, path);
        }
    }

    public void Copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
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

    private void Delete(File file){
        //adapter.remove(adapter.getItem(curItem));
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files.length < 1){
                file.delete();
            }
            for(File f:files){
                Delete(f);
                f.delete();
            }
        }
        file.delete();
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
                dialogCreate.show();
                break;
            case R.id.action_copy:
                copiedFile = adapter.getItem(curItem).file;
                break;
            case R.id.action_paste:
                try {
                    Paste(copiedFile, path);
                    LoadFiles(path);
                    adapter.notifyDataSetChanged();
                }catch (Exception ex){
                    Toast.makeText(this, ex.getMessage(),Toast.LENGTH_SHORT);
                }
                break;
            case R.id.action_delete:
                dialogYesNo.show();
                break;
            case R.id.action_rename:
                dialogRename.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}