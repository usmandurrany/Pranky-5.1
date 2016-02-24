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
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.enums.Type;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

/**
 * Created by Usman on 11/6/2015.
 */
public class TimerDialogActivity extends Activity{
    private View decorView;
    private SetPrank scheduler;
    private Tutorial mTutorial;
    private AbstractWheel duration;

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

       /* duration = (SeekBar) findViewById(R.id.sliderDuration);
        final CustomTextView durationCounter = (CustomTextView) findViewById(R.id.txtDurationCounter);

        duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                durationCounter.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
*/
        //Configure Hours Column
        duration = (AbstractWheel) findViewById(R.id.whlDuration);
        ArrayWheelAdapter<String> durationAdapter =
                new ArrayWheelAdapter<>(this, new String[] {"05", "10", "15","20","25","30","35","40","45","50","55","60"});
        //NumericWheelAdapter durationAdapter = new NumericWheelAdapter(TimerDialogActivity.this, 0, 60,"%02d");
        durationAdapter.setItemResource(R.layout.wheel_item_duration);
        durationAdapter.setItemTextResource(R.id.wheel_item);
        duration.setViewAdapter(durationAdapter);

        final AbstractWheel hour = (AbstractWheel) findViewById(R.id.timerhour);

        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(TimerDialogActivity.this, 0, 12,"%02d");
        hourAdapter.setItemResource(R.layout.wheel_item_number_left);
        hourAdapter.setItemTextResource(R.id.wheel_item);
        hour.setViewAdapter(hourAdapter);

        //Configure Minutes Column
        final AbstractWheel min = (AbstractWheel) findViewById(R.id.timerminute);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(TimerDialogActivity.this, 0, 59,"%02d");
        minAdapter.setItemResource(R.layout.wheel_item_number_center);
        minAdapter.setItemTextResource(R.id.wheel_item);
        min.setViewAdapter(minAdapter);

        //Configure Seconds Marker Column
        final AbstractWheel sec = (AbstractWheel) findViewById(R.id.timersec);
        NumericWheelAdapter secAdapter = new NumericWheelAdapter(TimerDialogActivity.this, 0, 59,"%02d");
        secAdapter.setItemResource(R.layout.wheel_item_number_right);
        secAdapter.setItemTextResource(R.id.wheel_item);
        sec.setViewAdapter(minAdapter);
        final ImageView set = (ImageView) findViewById(R.id.timerset);

        sec.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {}

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
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
                                        getString(R.string.tut_time_attack_title),
                                        getString(R.string.tut_time_attack_desc));
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
                            Type.TimerDialogActivity,calcDuration());

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
                                getString(R.string.toast_min_time_limit)).show();

                    }
                }
            }


        });




    if (SharedPrefs.isTimerFirstLaunch()) {
        mTutorial = new Tutorial(this, Type.TimerDialogActivity);
        mTutorial.show(new ViewTarget(sec),
                getString(R.string.tut_time_attack_title),
                getString(R.string.tut_time_attack_desc2));
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

    public int calcDuration(){
        if(duration.getCurrentItem() == 0){
            return 5;
        }else
            return (duration.getCurrentItem()*5);
    }
}


