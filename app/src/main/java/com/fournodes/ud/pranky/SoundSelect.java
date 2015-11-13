package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


/**
 * Created by Usman-Durrani on 10-Nov-15.
 */
public class SoundSelect extends Activity implements FileChooser.FileSelectedListener {
    private Dialog dialog;
    protected   View decorView;
    PrankyDB prankyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundselect);

        prankyDB = new PrankyDB(SoundSelect.this);


        ImageView btnselsound = (ImageView) findViewById(R.id.btnMusicToggle);
        ImageView btnselpic = (ImageView) findViewById(R.id.btnSelPic);
        ImageView btndiagclose = (ImageView) findViewById(R.id.btnDiagClose);
        ImageView btnsoundset = (ImageView) findViewById(R.id.btnSoundSet);


        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundSelect.this.finish();
            }
        });


        btnselsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooser filech = new FileChooser(SoundSelect.this);
                filech.setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) {
                        Toast.makeText(SoundSelect.this, file.getName(), Toast.LENGTH_SHORT).show();
                        copyfile(file.getAbsolutePath(), file.getName());

                        // Gets the data repository in write mode
                        SQLiteDatabase db = prankyDB.getWritableDatabase();

// Create a new map of values, where column names are the keys
                        ContentValues values = new ContentValues();
                        values.put(PrankyDB.COLUMN_PIC_LOC, R.mipmap.waterdrop);
                        values.put(PrankyDB.COLUMN_PIC_ALIAS, file.getName());
                        values.put(PrankyDB.COLUMN_SOUND_LOC, file.getAbsolutePath());
                        values.put(PrankyDB.COLUMN_REPEAT_COUNT, 1);


// Insert the new row, returning the primary key value of the new row
                        long newRowId;
                        newRowId = db.insert(
                                PrankyDB.TABLE_USER_SOUNDS,
                                "null",
                                values);
                        Toast.makeText(SoundSelect.this, String.valueOf(newRowId), Toast.LENGTH_SHORT).show();

                    }
                })
                .showDialog();



            }
        });

        btnselpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        btnsoundset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.d("sender", "Broadcasting message");
                Intent intent = new Intent("custom-sound-added");
                // You can also include some extra data.
                intent.putExtra("message", "This is my message!");
                LocalBroadcastManager.getInstance(SoundSelect.this).sendBroadcast(intent);
                finish();
            }
        });




        //Set the dialog to not focusable (makes navigation ignore us adding the window)



    }






    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void fileSelected(File file) {
    }

    public void copyfile(String srcPath,String fileName){
        //String sourcePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TongueTwister/tt_temp.3gp";
        File source = new File(srcPath);

        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pranky/"+fileName;
        File destination = new File(destinationPath);
        try
        {
            FileUtils.copyFile(source, destination);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
