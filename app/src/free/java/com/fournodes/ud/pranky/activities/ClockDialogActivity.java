package com.fournodes.ud.pranky.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fournodes.ud.pranky.mediaplayers.BackgroundMusic;
import com.fournodes.ud.pranky.custom.CustomToast;
import com.fournodes.ud.pranky.mediaplayers.PreviewMediaPlayer;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SetPrank;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.adapters.DayWheelAdapter;
import com.fournodes.ud.pranky.enums.Action;
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.enums.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

/**
 * Created by Usman on 11/6/2015.
 */
public class ClockDialogActivity extends Activity{
    private AbstractWheel duration;
    private PreviewMediaPlayer previewMediaPlayer;
    private int durInMillis= 5000;
    private String[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_clock);
        onWindowFocusChanged(true);
        previewMediaPlayer = PreviewMediaPlayer.getInstance(this);
        duration = (AbstractWheel) findViewById(R.id.whlDuration);

        if (getIntent().getStringExtra("ItemType").equals("CustomSound")) {
            durInMillis = getIntent().getIntExtra("Duration", 5000);
        }else if (getIntent().getStringExtra("ItemType").equals("SystemSound")){
            durInMillis = getIntent().getIntExtra("Duration", 5000);
        }else if (getIntent().getStringExtra("ItemType").equals("HardwareFunc")){
            duration.setEnabled(false);
        }

        int i;
        int durInSec = durInMillis/1000;
        int firstVal = ((int) Math.ceil((double)durInSec/5))*5;
        values = new String[((60-firstVal)/5)+1];
        values[0]=String.format(Locale.US,"%02d",firstVal);
        for ( i = 1;i<values.length;i++){
            values[i]=String.format(Locale.US,"%02d",Integer.parseInt(values[i-1])+5);
        }


        //Array for the am/pm marker column
        String[] ampmArray = {"AM", "PM"};
        //With a custom method I get the next following 10 days from now
        ArrayList<Date> days = getNextNumberOfDays(new Date(), 10);
        final ArrayWheelAdapter<String> durationAdapter =
                new ArrayWheelAdapter<>(this, values);
        //NumericWheelAdapter durationAdapter = new NumericWheelAdapter(ClockDialogActivity.this, 0, 60,"%02d");
        durationAdapter.setItemResource(R.layout.wheel_item_duration);
        durationAdapter.setItemTextResource(R.id.wheel_item);
        duration.setViewAdapter(durationAdapter);

        //Configure Days Column
        final AbstractWheel day = (AbstractWheel) findViewById(R.id.day);
        day.setViewAdapter(new DayWheelAdapter(ClockDialogActivity.this, days));

        //Configure Hours Column
        final AbstractWheel hour = (AbstractWheel) findViewById(R.id.hour);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(ClockDialogActivity.this, 1, 12,"%02d");
        hourAdapter.setItemResource(R.layout.wheel_item_number_center);
        hourAdapter.setItemTextResource(R.id.wheel_item);
        hour.setViewAdapter(hourAdapter);

        //Configure Minutes Column
        final AbstractWheel min = (AbstractWheel) findViewById(R.id.minute);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(ClockDialogActivity.this, 0, 59,"%02d");
        minAdapter.setItemResource(R.layout.wheel_item_number_center);
        minAdapter.setItemTextResource(R.id.wheel_item);
        min.setViewAdapter(minAdapter);

        //Configure am/pm Marker Column
        final AbstractWheel ampm = (AbstractWheel) findViewById(R.id.ampm);
        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(ClockDialogActivity.this, ampmArray);
        ampmAdapter.setItemResource(R.layout.wheel_item_number_right);
        ampmAdapter.setItemTextResource(R.id.wheel_item);
        ampm.setViewAdapter(ampmAdapter);

        ImageView close = (ImageView) findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView clockset = (ImageView) findViewById(R.id.set);
        clockset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getPranksLeft()<=0){
                    startActivity(new Intent(ClockDialogActivity.this, GetPremiumDialogActivity.class));
                    finish();
                }else {
                    SetPrank scheduler = new SetPrank(ClockDialogActivity.this,
                            day.getCurrentItem(),
                            hour.getCurrentItem(),
                            min.getCurrentItem(),
                            0,
                            ampm.getCurrentItem(),
                            Type.ClockDialogActivity,
                            Integer.parseInt(String.valueOf(durationAdapter.getItemText(duration.getCurrentItem()))));

                    if (scheduler.validateTime(scheduler.clockSchedule())) {

                        scheduler.ScheduleSoundPlayback(scheduler.clockSchedule());

                        LocalBroadcastManager.getInstance(ClockDialogActivity.this)
                                .sendBroadcast(new Intent(String.valueOf(Type.InterActivityBroadcast))
                                        .putExtra(String.valueOf(Action.Broadcast),
                                                String.valueOf(Message.ShowPranksLeft)));
                        finish();
                    } else {
                        new CustomToast(ClockDialogActivity.this,
                                getString(R.string.toast_time_passed)).show();

                    }
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
        View decorView = getWindow().getDecorView();

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefs.setBgMusicPlaying(false);
    }


}
