package com.fournodes.ud.pranky.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fournodes.ud.pranky.BackgroundMusic;
import com.fournodes.ud.pranky.DatabaseHelper;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.Tutorial;
import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.utils.Cleaner;
import com.fournodes.ud.pranky.utils.FileChooser;
import com.fournodes.ud.pranky.utils.FontManager;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by Usman-Durrani on 10-Nov-15.
 */
public class AddSoundDialogActivity extends Activity {

    private View decorView;
    private DatabaseHelper prankyDB;
    private int cusIconID;
    private File soundFile;
    private String fileName;
    private String fileExt;
    private ImageView btnsave;
    private ImageView custom;
    private View rootView;
    private EditText txtselsound;
    private ImageView btndiagclose;
    private ImageView btnselsound;
    private ImageView customImage;
    private Tutorial mTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_dialog_addsound,
                null);
        setContentView(rootView);
        onWindowFocusChanged(true);

        prankyDB = new DatabaseHelper(AddSoundDialogActivity.this);
        txtselsound = (EditText) findViewById(R.id.txtSelSound);
        txtselsound.setTypeface(FontManager.getTypeFace(this, SharedPrefs.DEFAULT_FONT));
        btnselsound = (ImageView) findViewById(R.id.btnMusicToggle);
        btndiagclose = (ImageView) findViewById(R.id.btnDiagClose);
        customImage = (ImageView) findViewById(R.id.custom2);
        btnsave = (ImageView) findViewById(R.id.btnSave);

        btnsave.setEnabled(false);

        if (SharedPrefs.isAddmoreFirstLaunch()) {
            mTutorial = new Tutorial(this, Type.AddSoundDialogActivity);
            mTutorial.show(new ViewTarget(btnselsound),
                    "Select a sound",
                    "Tap on the icon to pick a sound from your phone");
        }

        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnselsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooser filech = new FileChooser(AddSoundDialogActivity.this);
                filech.setFileListener(
                        new FileChooser.FileSelectedListener() {
                            @Override
                            public void fileSelected(File file) {
                                fileName = file.getName();
                                fileExt = fileName.substring(fileName.lastIndexOf("."));
                                //Toast.makeText(AddSoundDialogActivity.this,
                                //fileExt, Toast.LENGTH_SHORT).show();

                                if (fileExt.equals(".mp3") ||
                                        fileExt.equals(".wav") ||
                                        fileExt.equals(".3gp") ||
                                        fileExt.equals(".ogg")) {
                                    soundFile = file;
                                    txtselsound.setText(fileName);
                                    if (mTutorial != null)
                                        mTutorial.moveToNext(new ViewTarget(customImage),
                                                "Pick an icon",
                                                "Choose an icon for your sound");

                                } else
                                    Toast.makeText(AddSoundDialogActivity.this,
                                            "Invalid file! Select another", Toast.LENGTH_SHORT).show();

                            }
                        }

                ).showDialog();
             }
        });


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cusIconID != 0) {
                    copyFile(soundFile.getAbsolutePath(), soundFile.getName());
                    // Gets the data repository in write mode
                    SQLiteDatabase db = prankyDB.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_ITEM_IMG_LOC, cusIconID);
                    values.put(DatabaseHelper.COLUMN_ITEM_NAME, soundFile.getName());
                    values.put(DatabaseHelper.COLUMN_ITEM_SOUND_LOC, soundFile.getAbsolutePath());
                    values.put(DatabaseHelper.COLUMN_REPEAT_COUNT, 1);
                    values.put(DatabaseHelper.COLUMN_SOUND_VOL, 1);
                    values.put(DatabaseHelper.COLUMN_ITEM_CATEGORY, "custom");


                    db.insert(DatabaseHelper.TABLE_ITEMS,"null",values);

                } else
                    Toast.makeText(AddSoundDialogActivity.this, "Pick an Icon", Toast.LENGTH_SHORT).show();


                SharedPrefs.setCusSoundAdded(true);
                if (mTutorial != null)
                    mTutorial.end();
                finish();
            }
        });


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
        if (soundFile != null)
            btnsave.setEnabled(true);
        if (soundFile != null && mTutorial != null) {
            mTutorial.moveToNext(new ViewTarget(btnsave),
                    "Save your sound", "Tap on save to add your sound to the list");
        }
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


    public void copyFile(String srcPath, String fileName) {
        //String sourcePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TongueTwister/tt_temp.3gp";
        File source = new File(srcPath);

        String destinationPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath() + "/Pranky/" + fileName;

        File destination = new File(destinationPath);
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefs.setBgMusicPlaying(false);
        Cleaner.unbindDrawables(rootView);
        rootView = null;

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (BackgroundMusic.mp != null) {
                BackgroundMusic.pause();
            }
        } catch (Exception e) {
            Log.e("BG Music Pause", e.toString());
        }
    }

    @Override
    protected void onResume() {
        if (SharedPrefs.prefs == null)
            SharedPrefs.setContext(this);

        super.onResume();
        try {
            if (BackgroundMusic.mp != null) {
                BackgroundMusic.play();
            }
        } catch (Exception e) {
            Log.e("BG Music Pause", e.toString());
        }
    }

}
