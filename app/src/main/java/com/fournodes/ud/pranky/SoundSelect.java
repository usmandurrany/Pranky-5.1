package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by Usman-Durrani on 10-Nov-15.
 */
public class SoundSelect extends Activity implements FileChooser.FileSelectedListener {
    private Dialog dialog;
    protected View decorView;
    PrankyDB prankyDB;
    int cusIconID;
    File soundFile;
    String fileName;
    String fileExt;
    ImageView btnsave;
    ImageView custom;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_soundselect,
                null);
        setContentView(rootView);

        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        prankyDB = new PrankyDB(SoundSelect.this);
        final EditText txtselsound = (EditText) findViewById(R.id.txtSelSound);
        txtselsound.setTypeface(FontManager.getTypeFace(this, "grinched-regular"));
        ImageView btnselsound = (ImageView) findViewById(R.id.btnMusicToggle);
        ImageView btndiagclose = (ImageView) findViewById(R.id.btnDiagClose);
        btnsave = (ImageView) findViewById(R.id.btnSave);
        btnsave.setEnabled(false);

        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnselsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooser filech = new FileChooser(SoundSelect.this);
                filech.setFileListener(new FileChooser.FileSelectedListener() {
                                           @Override
                                           public void fileSelected(File file) {
                                               fileName = file.getName();
                                               fileExt = fileName.substring(fileName.lastIndexOf("."));

                                               Toast.makeText(SoundSelect.this, fileExt, Toast.LENGTH_SHORT).show();

                                               if (fileExt.equals(".mp3") || fileExt.equals(".wav") || fileExt.equals(".3gp") || fileExt.equals(".ogg")) {

                                                   soundFile = file;
                                                   txtselsound.setText(fileName);

                                               } else
                                                   Toast.makeText(SoundSelect.this, "Invalid file! Select another", Toast.LENGTH_SHORT).show();

                                           }
                                       }

                )
                        .

                                showDialog();


            }
        });


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cusIconID != 0) {
                    copyfile(soundFile.getAbsolutePath(), soundFile.getName());
                    // Gets the data repository in write mode
                    SQLiteDatabase db = prankyDB.getWritableDatabase();

// Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(PrankyDB.COLUMN_PIC_LOC, cusIconID);
                    values.put(PrankyDB.COLUMN_PIC_ALIAS, soundFile.getName());
                    values.put(PrankyDB.COLUMN_SOUND_LOC, soundFile.getAbsolutePath());
                    values.put(PrankyDB.COLUMN_REPEAT_COUNT, 1);
                    values.put(PrankyDB.COLUMN_SOUND_VOL, 1);


// Insert the new row, returning the primary key value of the new row
                    long newRowId;
                    newRowId = db.insert(
                            PrankyDB.TABLE_USER_SOUNDS,
                            "null",
                            values);
                    Toast.makeText(SoundSelect.this, String.valueOf(newRowId), Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(SoundSelect.this, "Pick an Icon", Toast.LENGTH_SHORT).show();


                Log.d("sender", "Broadcasting message");
                Intent intent = new Intent("main-activity-broadcast");
                // You can also include some extra data.
                intent.putExtra("message", "custom-sound-added");
                LocalBroadcastManager.getInstance(SoundSelect.this).sendBroadcast(intent);
                finish();
            }
        });


        //Set the dialog to not focusable (makes navigation ignore us adding the window)


    }

    public void iconClick(View v) {
        if (custom != null)
            custom.setImageResource(0);
        custom = (ImageView) findViewById(v.getId());
        String imgName = getResources().getResourceEntryName(v.getId());
        try {
            cusIconID = R.mipmap.class.getField(imgName).getInt(null);
            int custom_hover = R.mipmap.class.getField(imgName + "_hover").getInt(null);
            custom.setBackgroundResource(custom_hover);
            custom.setImageResource(R.drawable.gridselectedanim);
            AnimationDrawable boxsel = (AnimationDrawable) custom.getDrawable();
            boxsel.start();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        btnsave.setEnabled(true);

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

    public void copyfile(String srcPath, String fileName) {
        //String sourcePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TongueTwister/tt_temp.3gp";
        File source = new File(srcPath);

        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pranky/" + fileName;
        File destination = new File(destinationPath);
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        unbindDrawables(rootView);
        rootView = null;
        System.gc();

    }

}
