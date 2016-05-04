package com.fournodes.ud.pranky.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.Tutorial;
import com.fournodes.ud.pranky.custom.CustomToast;
import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.mediaplayers.BackgroundMusic;
import com.fournodes.ud.pranky.mediaplayers.PreviewMediaPlayer;
import com.fournodes.ud.pranky.utils.DatabaseHelper;
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

    private DatabaseHelper db;
    private int cusIconID;
    private File fileSelected;
    private String fileName;
    private String fileExt;
    private ImageView buttonSave;
    private ImageView iconSelected;
    private EditText eTextFileName;
    private ImageView iconCustom;
    private Tutorial mTutorial;
    private PreviewMediaPlayer previewMediaPlayer;
    private int durInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_addsound);
        onWindowFocusChanged(true);
        previewMediaPlayer = PreviewMediaPlayer.getInstance(this);
        int color = Color.parseColor("#f27d13");
        db = new DatabaseHelper(AddSoundDialogActivity.this);
        eTextFileName = (EditText) findViewById(R.id.txtSelSound);
        eTextFileName.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        eTextFileName.setTypeface(FontManager.getTypeFace(this, SharedPrefs.DEFAULT_FONT));
        ImageView buttonBrowse = (ImageView) findViewById(R.id.btnMusicToggle);
        ImageView buttonClose = (ImageView) findViewById(R.id.btnClose);
        iconCustom = (ImageView) findViewById(R.id.custom2);
        buttonSave = (ImageView) findViewById(R.id.btnSave);

        buttonSave.setEnabled(false);

        if (SharedPrefs.isAddmoreFirstLaunch()) {
            mTutorial = new Tutorial(this, Type.AddSoundDialogActivity);
            mTutorial.show(new ViewTarget(buttonBrowse),
                    getString(R.string.tut_select_sound_title),
                    getString(R.string.tut_select_sound_desc));
        }

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooser mFileChooser = new FileChooser(AddSoundDialogActivity.this);
                mFileChooser.setFileListener(
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

                                    fileSelected = file;
                                    previewMediaPlayer.getDurInMills(fileSelected.getAbsolutePath(),
                                            new MediaPlayer.OnPreparedListener() {
                                                @Override
                                                public void onPrepared(MediaPlayer mp) {
                                                    durInMillis = mp.getDuration();
                                                    Log.e("Add Sound", "Sound Duration " + String.valueOf(durInMillis));
                                                    if (durInMillis < 60000) {
                                                        eTextFileName.setText(fileName);
                                                        if (mTutorial != null)
                                                            mTutorial.moveToNext(new ViewTarget(iconCustom),
                                                                    getString(R.string.tut_pick_icon_title),
                                                                    getString(R.string.tut_pick_icon_desc));
                                                    } else {
                                                        Toast.makeText(AddSoundDialogActivity.this,
                                                                R.string.toast_sound_duration, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else
                                    Toast.makeText(AddSoundDialogActivity.this,
                                            R.string.toast_invalid_file, Toast.LENGTH_SHORT).show();

                            }
                        }

                ).showDialog();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cusIconID != 0) {
                    if (mTutorial != null)
                        mTutorial.end();

                    String newFilePath = copyFile(fileSelected.getAbsolutePath(), fileSelected.getName());
                    // Gets the data repository in write mode
                    SQLiteDatabase databse = db.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_ITEM_IMG_LOC, cusIconID);
                    values.put(DatabaseHelper.COLUMN_ITEM_NAME, fileSelected.getName());
                    values.put(DatabaseHelper.COLUMN_ITEM_SOUND_LOC, newFilePath);
                    values.put(DatabaseHelper.COLUMN_REPEAT_COUNT, 1);
                    values.put(DatabaseHelper.COLUMN_SOUND_VOL, 1);
                    values.put(DatabaseHelper.COLUMN_ITEM_CATEGORY, 8);//Custom


                    databse.insert(DatabaseHelper.TABLE_ITEMS, "null", values);

                } else
                    new CustomToast(AddSoundDialogActivity.this, getString(R.string.tut_pick_icon_title)).show();

                SharedPrefs.setCusSoundAdded(true);
                Log.e("Sound Added", String.valueOf(SharedPrefs.isCusSoundAdded()));
                finish();
            }
        });


    }

    public void iconClick(View v) {
        if (iconSelected != null)
            iconSelected.setImageResource(0);
        iconSelected = (ImageView) findViewById(v.getId());
        String imgName = getResources().getResourceEntryName(v.getId());
        try {
            cusIconID = R.mipmap.class.getField(imgName).getInt(null);
            int custom_hover = R.mipmap.class.getField(imgName + "_hover").getInt(null);
            iconSelected.setBackgroundResource(custom_hover);
            iconSelected.setImageResource(R.drawable.item_selected_animation);
            AnimationDrawable boxsel = (AnimationDrawable) iconSelected.getDrawable();
            boxsel.start();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (fileSelected != null)
            buttonSave.setEnabled(true);
        if (fileSelected != null && mTutorial != null) {
            mTutorial.moveToNext(new ViewTarget(buttonSave),
                    getString(R.string.tut_save_sound_title), getString(R.string.tut_save_sound_desc));
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    public String copyFile(String srcPath, String fileName) {
        //String sourcePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TongueTwister/tt_temp.3gp";
        File source = new File(srcPath);

        String destinationPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath() + "/Pranky/" + fileName;

        File destination = new File(destinationPath);
        try {
            FileUtils.copyFile(source, destination);
            return destinationPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefs.setBgMusicPlaying(false);
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
