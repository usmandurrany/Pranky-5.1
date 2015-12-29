package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static com.fournodes.ud.pranky.BackgroundMusic.getInstance;

/**
 * Created by Usman on 11/6/2015.
 */
public class SettingsDialog extends Activity implements View.OnClickListener{
    private Context context;
    private Dialog dialog;
    private BackgroundMusic player;
    private boolean playMusic = true;
    private RegisterOnServer rs;
    View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_settings);

        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Initialize the switchs on the dialog by checking shared prefs
        SwitchCompat btnmusic = (SwitchCompat) findViewById(R.id.btnMusicToggle);
        SwitchCompat remoteprank = (SwitchCompat) findViewById(R.id.swtRemotePrank);

        btnmusic.setChecked(SharedPrefs.isBgMusicEnabled());


        btnmusic.setOnClickListener(this);
        btnmusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    SharedPrefs.setBgMusicEnabled(true);

                } else {
                    SharedPrefs.setBgMusicEnabled(false);
                }



                if (SharedPrefs.isBgMusicEnabled()) {
                try {
                        BackgroundMusic.setContext(SettingsDialog.this);
                        BackgroundMusic.play();
                     }catch(IllegalStateException e){Log.e("Settings Dialog",e.toString());}
                } else {
                    try
                    {
                        BackgroundMusic.stop();
                    }catch (IllegalStateException e){Log.e("Settings Dialog",e.toString());}

                }
            }
        });

        ImageView btndiagclose = (ImageView) findViewById(R.id.btnDiagClose);
        TextView bgmusic = (TextView) findViewById(R.id.txtBGMusic);
        final LinearLayout remotePrankID = (LinearLayout) findViewById(R.id.layoutRemoteID);
        final TextView myid = (TextView) findViewById(R.id.txtmyID);

        // Get the cached font and apply it
        bgmusic.setTypeface(FontManager.getTypeFace(SettingsDialog.this, SharedPrefs.DEFAULT_FONT));

        // Get myAppID form shared prefs and display it in the dialog
        myid.setText(SharedPrefs.getMyAppID());


        if (SharedPrefs.isPrankable()) {
            remoteprank.setChecked(true);
            remotePrankID.setVisibility(View.VISIBLE);
        }

        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        remoteprank.setOnClickListener(this);

        remoteprank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Initialize the RegisterOnServer class
                rs = new RegisterOnServer(SettingsDialog.this);

                if (isChecked) {
                    try {
                        // Convert the expDate in shared prefs to CALENDAR type for comparision
                        Calendar exp = Calendar.getInstance(TimeZone.getDefault());
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                        exp.setTime(sdf.parse(SharedPrefs.getExpDate()));

                        // Get current Time from device for comparision
                        Calendar today = Calendar.getInstance(TimeZone.getDefault());

                        // Perform Checks for true

                        // serverState is 0 and myAppId has not expired
                        if (SharedPrefs.getServerState() == 0 && exp.after(today)) {
                            Log.e("Condition1", "True");

                            // Resend the stored myAppID to server
                            //Send the GCM id and the myAppID as args
                            SharedPrefs.setServerState(1);
                            String[] args = {SharedPrefs.getMyGcmID(), SharedPrefs.getMyAppID()};
                            rs.execute(args);
                        }
                        // serverState is 0 and myAppId has expired
                        else if (SharedPrefs.getServerState() == 0 && exp.before(today)) {
                            Log.e("Condition2", "True");

                            // Request new appID from server
                            //Send the GCM id and the myAppID as args
                            SharedPrefs.setServerState(1);
                            SharedPrefs.setMyAppID(""); // Clear the myAppID before requesting form the server
                            String[] args = {SharedPrefs.getMyGcmID(), SharedPrefs.getMyAppID()};
                            rs.execute(args);

                        }
                        // serverState is 1 and myGcmId is not set or expDate is not set or expDate has passed
                        else if (SharedPrefs.getServerState() == 1 && (SharedPrefs.getMyGcmID() == null || exp.before(today))) {
                            Log.e("Condition3", "True");

                            //Run the method present in the Main activity

                            Intent intent = new Intent("main-activity-broadcast");
                            // You can also include some extra data.
                            intent.putExtra("message", "get-id");
                            LocalBroadcastManager.getInstance(SettingsDialog.this).sendBroadcast(intent);                            //Intent intent = new Intent("CONNECTIVITY_CHECK");
                            //context.sendBroadcast(intent);

                        }

                        remotePrankID.setVisibility(View.VISIBLE);

                        // Set prankable to true in Shared Prefs
                        SharedPrefs.setPrankable(true);
                        SharedPrefs.setPrankableResp("enabled"); // String that will go to the server


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    remotePrankID.setVisibility(View.INVISIBLE);
                    SharedPrefs.setPrankable(false);
                    SharedPrefs.setPrankableResp("disabled"); // String that will go to the server

                }
            }
        });




    }



    public static ArrayList<Date> getNextNumberOfDays(Date originalDate, int days) {
        ArrayList<Date> dates = new ArrayList<Date>();
        long offset;
        for (int i = 0; i <= days; i++) {
            offset = 86400 * 1000L * i;
            Date date = new Date(originalDate.getTime() + offset);
            dates.add(date);
        }
        return dates;
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
    public void onClick(View view) {
        /*String imgName = getResources().getResourceEntryName(view.getId());
        Toast.makeText(SettingsDialog.this, imgName, Toast.LENGTH_SHORT).show();*/
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
