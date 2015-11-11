package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URISyntaxException;


/**
 * Created by Usman-Durrani on 10-Nov-15.
 */
public class SoundSelect extends Activity {
    private Dialog dialog;
    protected   View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundselect);



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
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 0);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(SoundSelect.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }


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


            }
        });




        //Set the dialog to not focusable (makes navigation ignore us adding the window)



    }




    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    File myFile = new File(uri.getPath());
                    Log.d("LOC", "File Uri: " + myFile.getAbsolutePath().toString());
                    // Get the path
                    String path = null;

                        path = myFile.getAbsolutePath();

                    Log.e("FILE", "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
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

}
