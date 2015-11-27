package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
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
    private BackgroundMusic player = getInstance();
    private boolean playMusic = true;
    private  RemoteServer rs;

    public SettingsDialog(Context context) {
        this.context = context;
    }

    public void show() {
        // Send the stored GCM ID/Token to the server
        rs = new RemoteServer(context);

        dialog = new Dialog(context, R.style.ClockDialog);
        dialog.setContentView(R.layout.dialog_settings);

        Switch btnmusic = (Switch) dialog.findViewById(R.id.btnMusicToggle);
        Switch remoteprank = (Switch) dialog.findViewById(R.id.swtRemotePrank);
        ImageView btndiagclose = (ImageView) dialog.findViewById(R.id.btnDiagClose);
        TextView bgmusic = (TextView) dialog.findViewById(R.id.txtBGMusic);
        final LinearLayout remotePrankID  = (LinearLayout) dialog.findViewById(R.id.layoutRemoteID);
        final TextView myid = (TextView) dialog.findViewById(R.id.txtmyID);

        // Get the cached font and apply it
        bgmusic.setTypeface(FontManager.getTypeFace(context, "grinched-regular"));

        // Get myAppID form shared prefs and display it in the dialog
        myid.setText(SharedPrefs.getMyAppID());

        // Initialize the switchs on the dialog by checking shared prefs
        if (SharedPrefs.isBgMusicEnabled()) {
            btnmusic.setChecked(playMusic);
        }
        if (SharedPrefs.isPrankable()){
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
                    player.mp = MediaPlayer.create(context, R.raw.app_bg);
                    player.mp.setVolume(0, (float) 0.2);
                    player.mp.start();
                    player.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    try {
                                        player.mp.start();
                                    } catch (IllegalStateException e) {
                                    }
                                }
                            }, 2000);
                        }
                    });
                } else {
                    player.mp.stop();
                    player.mp.release();
                }
            }
        });


        remoteprank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    try {
                        // Convert the expDate in shared prefs to CALENDAR type for comparision
                        Calendar exp = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                        exp.setTime(sdf.parse(SharedPrefs.getExpDate()));

                        // Get current Time from device for comparision
                        Calendar today= Calendar.getInstance(TimeZone.getDefault());

                        // Perform Checks for true

                        // serverState is 0 and myAppId has not expired
                        if (SharedPrefs.getServerState() == 0 && exp.after(today)){
                            // Resend the stored myAppID to server

                            //Send the GCM id and the myAppID as args
                            String[] args = {SharedPrefs.getMyGcmID(),SharedPrefs.getMyAppID()};
                            rs.execute(args);
                            SharedPrefs.setServerState(1);
                        }
                        // serverState is 0 and myAppId has expired
                        else if(SharedPrefs.getServerState() == 0 && exp.before(today)){
                            // Request new appID from server
                            //Send the GCM id and the myAppID as args
                            String[] args = {SharedPrefs.getMyGcmID(),""};
                            rs.execute(args);
                            SharedPrefs.setServerState(1);

                        }
                        // serverState is 1 and myGcmId is not set or expDate is not set or expDate has passed
                        else if (SharedPrefs.getServerState() == 1 && (SharedPrefs.getMyGcmID()==null || SharedPrefs.getExpDate().equals("null")|| exp.before(today)))
                        {
                            // Run the method present in the Main activity
                            ((Main) context).GCMRegister();
                        }
                        remotePrankID.setVisibility(View.VISIBLE);

                        // Set prankable to true in Shared Prefs
                        SharedPrefs.setPrankable(true);
                        SharedPrefs.setPrankableResp("enabled"); // String that will go to the server


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                else {
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
