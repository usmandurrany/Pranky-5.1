package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created by Usman on 11/6/2015.
 */
public class TimerDialog extends Activity{


    private Context context;
    private Dialog dialog;
    private int sound;
    private String soundCus;
    public int soundVol;
    public int soundRepeat;
    ShowcaseView showcaseView;
    int steps=2;
    View decorView;
    SoundScheduler scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timer);

        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //Configure Hours Column
        final WheelView hour = (WheelView) findViewById(R.id.timerhour);

        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(TimerDialog.this, 0, 12);
        hourAdapter.setItemResource(R.layout.wheel_item_time);
        hourAdapter.setItemTextResource(R.id.time_item);
        hour.setViewAdapter(hourAdapter);

        //Configure Minutes Column
        final WheelView min = (WheelView) findViewById(R.id.timerminute);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(TimerDialog.this, 00, 59);
        minAdapter.setItemResource(R.layout.wheel_item_time);
        minAdapter.setItemTextResource(R.id.time_item);
        min.setViewAdapter(minAdapter);

        //Configure Seconds Marker Column
        final WheelView sec = (WheelView) findViewById(R.id.timersec);
        NumericWheelAdapter secAdapter = new NumericWheelAdapter(TimerDialog.this, 00, 59);
        secAdapter.setItemResource(R.layout.wheel_item_time);
        secAdapter.setItemTextResource(R.id.time_item);
        sec.setViewAdapter(minAdapter);
        final ImageView set = (ImageView) findViewById(R.id.timerset);

        sec.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                if (showcaseView!=null && sec.getCurrentItem() == 5){
                    showcaseView.setShowcase(new ViewTarget(set), true);
                    showcaseView.setContentTitle("Time Attack");
                    showcaseView.setContentText("Tap on the set button to set the timer and wait for your sound to play");
                    steps++;
                }
            }
        });


        ImageView close = (ImageView) findViewById(R.id.timerclose);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        scheduler = new SoundScheduler(TimerDialog.this, hour.getCurrentItem(), min.getCurrentItem(), sec.getCurrentItem());

                if (scheduler.validateTime(scheduler.timerSchedule(), "dialog_timer")) {
                    scheduler.ScheduleSoundPlayback("dialog_timer", scheduler.timerSchedule());
                    if (showcaseView != null)
                        showcaseView.hide();

                    finish();
                } else {
                    CustomToast cToast = new CustomToast(TimerDialog.this, "Minimum  time  is  5  seconds");
                    cToast.show();
                }
            }


        });




if (SharedPrefs.isTimerFirstLaunch()) {
    ViewTarget target = new ViewTarget(sec);
    showcaseView = new ShowcaseView.Builder(this)
            .withMaterialShowcase()
            .setTarget(target)
            .setContentTitle("Time Attack")
            .setContentText("Select the timer to 5 seconds by moving the wheel")
            .setStyle(R.style.CustomShowcaseTheme2)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sec.getCurrentItem() == 5 || steps ==3) {

                    switch (steps) {
                        case 2:
                            showcaseView.setShowcase(new ViewTarget(set), true);
                            showcaseView.setContentTitle("Time Attack");
                            showcaseView.setContentText("Tap on the set button to set the timer and wait for your sound to play");
                            steps++;

                            break;
                        case 3:
                            showcaseView.hide();
                            SharedPrefs.setTimerFirstLaunch(false);
                            break;
                    }
                }

                }
            })
            .build();

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


