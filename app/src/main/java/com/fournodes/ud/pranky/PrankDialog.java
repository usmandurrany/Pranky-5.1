package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Usman on 11/6/2015.
 */
public class PrankDialog extends Activity{
    private Context context;
    private Dialog dialog;
    View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_prank);

        Log.e("Prank Dialog","Created");


        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        ImageView btndiagclose = (ImageView) findViewById(R.id.close);
        ImageView btnset = (ImageView) findViewById(R.id.set);
        final EditText frndID = (EditText) findViewById(R.id.txtfrndID);
        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        frndID.setText(SharedPrefs.getFrndAppID());
        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frndID.getText()!= null){
                    new DeviceValidation(new DeviceValidation.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            if (output.equals("1"))
                            {
                                SharedPrefs.setFrndAppID(frndID.getText().toString());
                                finish();
                            }else if (output.equals("0")){
                                CustomToast cToast = new CustomToast(PrankDialog.this, "Your friend is not prankable at the moment");
                                cToast.show();
                            }else if (output.equals("-10")){ //Server Unreachable
                                CustomToast cToast = new CustomToast(PrankDialog.this, "Can't connect to server");
                                cToast.show();

                            }else if (output.equals("-20")){//Network Unavailable
                                CustomToast cToast = new CustomToast(PrankDialog.this, "Network Unavailable");
                                cToast.show();
                            }
                            else{
                                CustomToast cToast = new CustomToast(PrankDialog.this, "Invalid ID");
                                cToast.show();
                            }
                        }
                    }).execute(frndID.getText().toString());


                }else{
                    CustomToast cToast = new CustomToast(PrankDialog.this, "Enter friends ID");
                    cToast.show();
                }

            }
        });



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
        super.onResume();
        try {
            if (BackgroundMusic.mp != null) {
                BackgroundMusic.play();
            }
        } catch (Exception e) {
            Log.e("BG Music Pause", e.toString());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefs.setBgMusicPlaying(false);
        System.gc();

    }
}
