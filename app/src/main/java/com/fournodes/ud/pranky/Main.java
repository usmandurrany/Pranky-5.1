package com.fournodes.ud.pranky;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public class Main extends FragmentActivity implements SoundSelectListener {
    private   View decorView;

    public me.relex.circleindicator.CircleIndicator mIndicator;
    private ViewPager awesomePager;
    private PagerAdapter pm;
    ImageView clock;
    ImageView timer;
    int sound;

    int clockDay, clockHour,clockMin,clockampm; //0 for am 1 for pm
    int timerHour, timerMin, timerSec;
    //String ampm;

    ArrayList<Category> codeCategory;

    int images[] = { R.mipmap.bee, R.mipmap.cat,
            R.mipmap.current, R.mipmap.knock,
            R.mipmap.glassbreak, R.mipmap.gun,
            R.mipmap.farting, R.mipmap.waterdrop,
            R.mipmap.cricket, R.mipmap.hammer,
            R.mipmap.bomb, R.mipmap.footsteps };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Calendar c = Calendar.getInstance();
//        Toast.makeText(Main.this, c.get(Calendar.AM_PM),Toast.LENGTH_SHORT).show();
       // Toast.makeText(Main.this,getResources().getDisplayMetrics().densityDpi,Toast.LENGTH_LONG);

        awesomePager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (me.relex.circleindicator.CircleIndicator) findViewById(R.id.pagerIndicator);

        ArrayList<Integer> a = new ArrayList<Integer>();

        Category m = new Category();

        for(int i = 0; i < images.length; i++) {
            a.add(i, images[i]);
            m.resid = a.get(i);
        }

        codeCategory = new ArrayList<Category>();
        codeCategory.add(m);

        Iterator<Integer> it = a.iterator();

        List<GridFragment> gridFragments = new ArrayList<GridFragment>();
        it = a.iterator();

        int i = 0;
        while(it.hasNext()) {
            ArrayList<GridItems> imLst = new ArrayList<GridItems>();

            GridItems itm = new GridItems(0, it.next());
            imLst.add(itm);
            i = i + 1;

            if(it.hasNext()) {
                GridItems itm1 = new GridItems(1, it.next());
                imLst.add(itm1);
                i = i + 1;
            }

            if(it.hasNext()) {
                GridItems itm2 = new GridItems(2, it.next());
                imLst.add(itm2);
                i = i + 1;
            }

            if(it.hasNext()) {
                GridItems itm3 = new GridItems(3, it.next());
                imLst.add(itm3);
                i = i + 1;
            }

            if(it.hasNext()) {
                GridItems itm4 = new GridItems(4, it.next());
                imLst.add(itm4);
                i = i + 1;
            }

            if(it.hasNext()) {
                GridItems itm5 = new GridItems(5, it.next());
                imLst.add(itm5);
                i = i + 1;
            }

            if(it.hasNext()) {
                GridItems itm6 = new GridItems(6, it.next());
                imLst.add(itm6);
                i = i + 1;
            }

            if(it.hasNext()) {
                GridItems itm7 = new GridItems(7, it.next());
                imLst.add(itm7);
                i = i + 1;
            }

            if(it.hasNext()) {
                GridItems itm8 = new GridItems(8, it.next());
                imLst.add(itm8);
                i = i + 1;
            }

            GridItems[] gp = {};
            GridItems[] gridPage = imLst.toArray(gp);
            gridFragments.add(new GridFragment(gridPage, Main.this));
        }

        pm = new PagerAdapter(getSupportFragmentManager(), gridFragments);
        awesomePager.setAdapter(pm);
        mIndicator.setViewPager(awesomePager);

        clock = (ImageView) findViewById(R.id.clock_btn);
        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(Main.this, R.style.ClockDialog);
                dialog.setContentView(R.layout.clock);
                dialog.setTitle("Title...");

                //Array for the am/pm marker column
               String[] ampmArray = {"am","pm"};
//With a custom method I get the next following 10 days from now
                ArrayList<Date> days = getNextNumberOfDays(new Date(), 10);


//Configure Days Column
                final WheelView day = (WheelView) dialog.findViewById(R.id.day);
                day.setViewAdapter(new DayWheelAdapter(Main.this, days));

//Configure Hours Column
                final WheelView hour = (WheelView) dialog.findViewById(R.id.hour);
                NumericWheelAdapter hourAdapter = new NumericWheelAdapter(Main.this, 1, 12);
                hourAdapter.setItemResource(R.layout.wheel_item_time);
                hourAdapter.setItemTextResource(R.id.time_item);
                hour.setViewAdapter(hourAdapter);

//Configure Minutes Column
                final WheelView min = (WheelView) dialog.findViewById(R.id.minute);
                NumericWheelAdapter minAdapter = new NumericWheelAdapter(Main.this, 00, 59);
                minAdapter.setItemResource(R.layout.wheel_item_time);
                minAdapter.setItemTextResource(R.id.time_item);
                min.setViewAdapter(minAdapter);

//Configure am/pm Marker Column
                final WheelView ampm = (WheelView) dialog.findViewById(R.id.ampm);
                ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(Main.this, ampmArray);
                ampmAdapter.setItemResource(R.layout.wheel_item_time);
                ampmAdapter.setItemTextResource(R.id.time_item);
                ampm.setViewAdapter(ampmAdapter);

                ImageView close = (ImageView) dialog.findViewById(R.id.close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                ImageView clockset = (ImageView) dialog.findViewById(R.id.set);
                clockset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Main.this, String.valueOf(ampm.getCurrentItem()), Toast.LENGTH_SHORT).show();
                        clockDay=day.getCurrentItem();
                        clockHour=hour.getCurrentItem();
                        clockMin=min.getCurrentItem();
                        clockampm=ampm.getCurrentItem();
                        ScheduleSoundPlayback("clock",sound);

                    }
                });

                //Here's the magic..
//Set the dialog to not focusable (makes navigation ignore us adding the window)
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

//Show the dialog!
                dialog.show();

//Set the dialog to immersive
                dialog.getWindow().getDecorView().setSystemUiVisibility(
                        Main.this.getWindow().getDecorView().getSystemUiVisibility());

//Clear the not focusable flag from the window
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                // custom dialog


                // set the custom dialog components - text, image and button
                //TextView text = (TextView) dialog.findViewById(R.id.text);
                //text.setText("Android custom dialog example!");
                //ImageView image = (ImageView) dialog.findViewById(R.id.image);
                // image.setImageResource(R.drawable.ic_launcher);

                // Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                //dialogButton.setOnClickListener(new OnClickListener() {
                //     @Override
                //    public void onClick(View v) {
                //         dialog.dismiss();
                //     }
                //  });
                // dialog.show();
            }

        });








        timer = (ImageView) findViewById(R.id.timer_btn);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(Main.this, R.style.ClockDialog);
                dialog.setContentView(R.layout.timer);
                dialog.setTitle("Title...");

                //Array for the am/pm marker column

//Configure Hours Column
                final WheelView hour = (WheelView) dialog.findViewById(R.id.timerhour);
                NumericWheelAdapter hourAdapter = new NumericWheelAdapter(Main.this, 0, 12);
                hourAdapter.setItemResource(R.layout.wheel_item_time);
                hourAdapter.setItemTextResource(R.id.time_item);
                hour.setViewAdapter(hourAdapter);

//Configure Minutes Column
                final WheelView min = (WheelView) dialog.findViewById(R.id.timerminute);
                NumericWheelAdapter minAdapter = new NumericWheelAdapter(Main.this, 00, 59);
                minAdapter.setItemResource(R.layout.wheel_item_time);
                minAdapter.setItemTextResource(R.id.time_item);
                min.setViewAdapter(minAdapter);

//Configure am/pm Marker Column
               final WheelView sec = (WheelView) dialog.findViewById(R.id.timersec);
                NumericWheelAdapter secAdapter = new NumericWheelAdapter(Main.this, 00, 59);
                secAdapter.setItemResource(R.layout.wheel_item_time);
                secAdapter.setItemTextResource(R.id.time_item);
                sec.setViewAdapter(minAdapter);
                ImageView close = (ImageView) dialog.findViewById(R.id.timerclose);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                ImageView set = (ImageView) dialog.findViewById(R.id.timerset);
                set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        timerHour=hour.getCurrentItem();
                        timerMin=min.getCurrentItem();
                        timerSec=sec.getCurrentItem();

                        ScheduleSoundPlayback("timer",sound);                    }
                });

//Here's the magic..
//Set the dialog to not focusable (makes navigation ignore us adding the window)
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

//Show the dialog!
                dialog.show();

//Set the dialog to immersive

                dialog.getWindow().getDecorView().setSystemUiVisibility(
                        Main.this.getWindow().getDecorView().getSystemUiVisibility());

//Clear the not focusable flag from the window
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);





                // custom dialog


                // set the custom dialog components - text, image and button
                //TextView text = (TextView) dialog.findViewById(R.id.text);
                //text.setText("Android custom dialog example!");
                //ImageView image = (ImageView) dialog.findViewById(R.id.image);
                // image.setImageResource(R.drawable.ic_launcher);

                // Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                //dialogButton.setOnClickListener(new OnClickListener() {
                //     @Override
                //    public void onClick(View v) {
                //         dialog.dismiss();
                //     }
                //  });
                // dialog.show();
            }

        });
    }

    @Override
    public void selectedSound(int sound) {
        Toast.makeText(Main.this, String.valueOf(sound), Toast.LENGTH_SHORT).show();
        this.sound=sound;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private List<GridFragment> fragments;

        public PagerAdapter(FragmentManager fm, List<GridFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int pos) {
            return this.fragments.get(pos);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }


    public static ArrayList getNextNumberOfDays(Date originalDate,int days){
        ArrayList dates = new ArrayList();
        long offset;
        for(int i= 0; i<= days; i++){
            offset = 86400 * 1000L * i;
            Date date = new Date( originalDate.getTime()+offset);
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

    public void ScheduleSoundPlayback(String type, int sound){
        SoundScheduler scheduler=new SoundScheduler(Main.this);
        Intent intent = new Intent(this, PlaySound.class);
        intent.putExtra("Sound",getResources().getResourceEntryName(sound));
        Toast.makeText(Main.this, getResources().getResourceName(sound), Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (type=="clock")
        alarmManager.set(AlarmManager.RTC_WAKEUP,scheduler.clockSchedule(clockDay,clockHour,clockMin,clockampm).getTimeInMillis() , pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, scheduler.timerSchedule(timerHour, timerMin, timerSec).getTimeInMillis(), pendingIntent);

        Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show();

    }


}
