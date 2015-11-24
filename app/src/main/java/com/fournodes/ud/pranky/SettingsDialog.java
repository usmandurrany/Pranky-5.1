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
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import static com.fournodes.ud.pranky.AppBGMusic.getInstance;

/**
 * Created by Usman on 11/6/2015.
 */
public class SettingsDialog {
    private Context context;
    private Dialog dialog;
    private int clockDay, clockHour, clockMin, clockampm; //0 for am 1 for pm
    private int sound;
    private AppBGMusic player = getInstance();
    private boolean playMusic = true;


    public SettingsDialog(Context context) {
        this.context = context;
    }

    public void show() {


        dialog = new Dialog(context, R.style.ClockDialog);
        dialog.setContentView(R.layout.dialog_settings);
        Switch btnmusic = (Switch) dialog.findViewById(R.id.btnMusicToggle);
        ImageView btndiagclose = (ImageView) dialog.findViewById(R.id.btnDiagClose);
        TextView bgmusic = (TextView) dialog.findViewById(R.id.txtBGMusic);
        TextView myid = (TextView) dialog.findViewById(R.id.txtmyID);
        bgmusic.setTypeface(FontManager.getTypeFace(context, "grinched-regular"));
        SharedPreferences settings = context.getSharedPreferences("PrankySharedPref", 0);

        myid.setText(settings.getString(SharedPrefs.APP_ID,null));

        playMusic = settings.getBoolean("PlayBGMusic", true);
        btnmusic.setChecked(playMusic);

        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final SharedPreferences.Editor editor = settings.edit();

        btnmusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    playMusic = true;


                } else {
                    playMusic = false;
                }

                editor.putBoolean("PlayBGMusic", playMusic);
                editor.commit();
                if (playMusic) {
                    player.mp = MediaPlayer.create(context, R.raw.app_bg);
                    // player.mp.setLooping(true);
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
