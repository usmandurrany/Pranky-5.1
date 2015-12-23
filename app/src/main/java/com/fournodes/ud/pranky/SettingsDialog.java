package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.fournodes.ud.pranky.BackgroundMusic.getInstance;

/**
 * Created by Usman on 11/6/2015.
 */
public class SettingsDialog {
    private Context context;
    private Dialog dialog;
    private BackgroundMusic player;
    private boolean playMusic = true;
    private RegisterOnServer rs;

    public SettingsDialog(Context context) {
        this.context = context;
    }

    public void show() {
        // Send the stored GCM ID/Token to the server

        dialog = new Dialog(context, R.style.ClockDialog);
        dialog.setContentView(R.layout.dialog_settings);

        SwitchCompat btnmusic = (SwitchCompat) dialog.findViewById(R.id.btnMusicToggle);
        SwitchCompat remoteprank = (SwitchCompat) dialog.findViewById(R.id.swtRemotePrank);
        ImageView btndiagclose = (ImageView) dialog.findViewById(R.id.btnDiagClose);
        TextView bgmusic = (TextView) dialog.findViewById(R.id.txtBGMusic);
        final LinearLayout remotePrankID = (LinearLayout) dialog.findViewById(R.id.layoutRemoteID);
        final TextView myid = (TextView) dialog.findViewById(R.id.txtmyID);

        // Get the cached font and apply it
        bgmusic.setTypeface(FontManager.getTypeFace(context, SharedPrefs.DEFAULT_FONT));

        // Get myAppID form shared prefs and display it in the dialog
        myid.setText(SharedPrefs.getMyAppID());

        // Initialize the switchs on the dialog by checking shared prefs
        if (SharedPrefs.isBgMusicEnabled()) {
            btnmusic.setChecked(playMusic);
        }
        if (SharedPrefs.isPrankable()) {
            remoteprank.setChecked(true);
            remotePrankID.setVisibility(View.VISIBLE);
        }

        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnmusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    SharedPrefs.setBgMusicEnabled(true);

                } else {
                    SharedPrefs.setBgMusicEnabled(false);
                }

                if (SharedPrefs.isBgMusicEnabled()) {
                    BackgroundMusic.setContext(context);

                    BackgroundMusic.play();
                } else {
                    BackgroundMusic.stop();
                }
            }
        });


        remoteprank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Initialize the RegisterOnServer class
                rs = new RegisterOnServer(context);

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

                            // Run the method present in the Main activity
                            //((Main) context).GCMRegister();
                            Intent intent = new Intent("CONNECTIVITY_CHECK");
                            context.sendBroadcast(intent);

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


        //Set the dialog to not focusable (makes navigation ignore us adding the window)
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Show the dialog!
        dialog.show();

        //Set the dialog to immersive
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                ((Activity) context).getWindow().getDecorView().getSystemUiVisibility());

        //Clear the not focusable flag from the window
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


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


}
