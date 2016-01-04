package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created by Usman on 11/6/2015.
 */
public class ClockDialogActivity extends Activity{

    private Context context;
    private Dialog dialog;
    private int clockDay, clockHour, clockMin, clockampm; //0 for am 1 for pm
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_clock);
        onWindowFocusChanged(true);

        //Array for the am/pm marker column
        String[] ampmArray = {"AM", "PM"};
        //With a custom method I get the next following 10 days from now
        ArrayList<Date> days = getNextNumberOfDays(new Date(), 10);


        //Configure Days Column
        final WheelView day = (WheelView) findViewById(R.id.day);
        day.setViewAdapter(new DayWheelAdapter(ClockDialogActivity.this, days));

        //Configure Hours Column
        final WheelView hour = (WheelView) findViewById(R.id.hour);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(ClockDialogActivity.this, 1, 12);
        hourAdapter.setItemResource(R.layout.wheel_picker_item);
        hourAdapter.setItemTextResource(R.id.time_item);
        hour.setViewAdapter(hourAdapter);

        //Configure Minutes Column
        final WheelView min = (WheelView) findViewById(R.id.minute);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(ClockDialogActivity.this, 00, 59);
        minAdapter.setItemResource(R.layout.wheel_picker_item);
        minAdapter.setItemTextResource(R.id.time_item);
        min.setViewAdapter(minAdapter);

        //Configure am/pm Marker Column
        final WheelView ampm = (WheelView) findViewById(R.id.ampm);
        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(ClockDialogActivity.this, ampmArray);
        ampmAdapter.setItemResource(R.layout.wheel_picker_item);
        ampmAdapter.setItemTextResource(R.id.time_item);
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
                Toast.makeText(ClockDialogActivity.this, String.valueOf(ampm.getCurrentItem()), Toast.LENGTH_SHORT).show();
                clockDay = day.getCurrentItem();
                clockHour = hour.getCurrentItem();
                clockMin = min.getCurrentItem();
                clockampm = ampm.getCurrentItem();
                SetPrank scheduler = new SetPrank(ClockDialogActivity.this, clockDay, clockHour, clockMin, clockampm);
                if (scheduler.validateTime(scheduler.clockSchedule(), "dialog_clock")) {
                    scheduler.ScheduleSoundPlayback("dialog_clock", scheduler.clockSchedule());
                    finish();
                } else {
                    CustomToast cToast = new CustomToast(ClockDialogActivity.this, "Selected time has passed.");
                    cToast.show();
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
    }

}