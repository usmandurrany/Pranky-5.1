package com.fournodes.ud.pranky.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fournodes.ud.pranky.BackgroundMusic;
import com.fournodes.ud.pranky.CustomToast;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SetPrank;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.Tutorial;
import com.fournodes.ud.pranky.enums.Action;
import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.enums.Message;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created by Usman on 11/6/2015.
 */
public class TimerDialogActivity extends Activity{
    private View decorView;
    private SetPrank scheduler;
    private Tutorial mTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_timer);

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

        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(TimerDialogActivity.this, 0, 12);
        hourAdapter.setItemResource(R.layout.wheel_picker_item);
        hourAdapter.setItemTextResource(R.id.time_item);
        hour.setViewAdapter(hourAdapter);

        //Configure Minutes Column
        final WheelView min = (WheelView) findViewById(R.id.timerminute);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(TimerDialogActivity.this, 00, 59);
        minAdapter.setItemResource(R.layout.wheel_picker_item);
        minAdapter.setItemTextResource(R.id.time_item);
        min.setViewAdapter(minAdapter);

        //Configure Seconds Marker Column
        final WheelView sec = (WheelView) findViewById(R.id.timersec);
        NumericWheelAdapter secAdapter = new NumericWheelAdapter(TimerDialogActivity.this, 00, 59);
        secAdapter.setItemResource(R.layout.wheel_picker_item);
        secAdapter.setItemTextResource(R.id.time_item);
        sec.setViewAdapter(minAdapter);
        final ImageView set = (ImageView) findViewById(R.id.timerset);

        sec.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {}

            @Override
            public void onScrollingFinished(WheelView wheel) {
                if (SharedPrefs.isTimerFirstLaunch()) {
                    if (sec.getCurrentItem() > 5)
                        sec.setCurrentItem(5);
                    new CountDownTimer(500, 500) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            if (mTutorial != null && sec.getCurrentItem() == 5) {
                                mTutorial.moveToNext(new ViewTarget(set),
                                        "Time Attack",
                                        "Tap on the set button to set the timer and wait for your sound to play");
                            }
                        }
                    }.start();
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
                if (SharedPrefs.getPranksLeft()<=0){
                    startActivity(new Intent(TimerDialogActivity.this, GetPremiumDialogActivity.class));
                    finish();
                }else {
                    scheduler = new SetPrank(TimerDialogActivity.this,
                            0,
                            hour.getCurrentItem(),
                            min.getCurrentItem(),
                            sec.getCurrentItem(),
                            0,
                            Type.TimerDialogActivity);

                    if (scheduler.validateTime(scheduler.timerSchedule())) {

                        scheduler.ScheduleSoundPlayback(scheduler.timerSchedule());

                        if (mTutorial != null) {
                            mTutorial.end();
                        }
                        LocalBroadcastManager.getInstance(TimerDialogActivity.this)
                                .sendBroadcast(new Intent(String.valueOf(Type.InterActivityBroadcast))
                                        .putExtra(String.valueOf(Action.Broadcast),
                                                String.valueOf(Message.ShowPranksLeft)));
                        finish();
                    } else {
                        new CustomToast(TimerDialogActivity.this,
                                "Minimum  time  is  5  seconds").show();

                    }
                }
            }


        });




    if (SharedPrefs.isTimerFirstLaunch()) {
        mTutorial = new Tutorial(this, Type.TimerDialogActivity);
        mTutorial.show(new ViewTarget(sec),
                "Time Attack",
                "Select the timer to 5 seconds by moving the wheel");
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


