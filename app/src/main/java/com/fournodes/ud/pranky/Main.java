package com.fournodes.ud.pranky;

import android.app.Dialog;
import android.os.Bundle;
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
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public class Main extends FragmentActivity {

    public me.relex.circleindicator.CircleIndicator mIndicator;
    private ViewPager awesomePager;
    private PagerAdapter pm;
    ImageView clock;
    ImageView timer;

    ArrayList<Category> codeCategory;

    int images[] = { R.mipmap.bee, R.mipmap.cat,
            R.mipmap.current, R.mipmap.knock,
            R.mipmap.glassbreak, R.mipmap.gun,
            R.mipmap.farting, R.mipmap.waterdrop,
            R.mipmap.cricket, R.mipmap.hammer,
            R.mipmap.bomb, R.mipmap.footsteps };

    String deviceNames[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
            "X", "Y", "Z" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                String[] ampmArray = new String[]{"am", "pm"};

//With a custom method I get the next following 10 days from now
                ArrayList<Date> days = getNextNumberOfDays(new Date(), 10);


//Configure Days Column
                WheelView day = (WheelView) dialog.findViewById(R.id.day);
                day.setViewAdapter(new DayWheelAdapter(Main.this, days));

//Configure Hours Column
                WheelView hour = (WheelView) dialog.findViewById(R.id.hour);
                NumericWheelAdapter hourAdapter = new NumericWheelAdapter(Main.this, 1, 12);
                hourAdapter.setItemResource(R.layout.wheel_item_time);
                hourAdapter.setItemTextResource(R.id.time_item);
                hour.setViewAdapter(hourAdapter);

//Configure Minutes Column
                WheelView min = (WheelView) dialog.findViewById(R.id.minute);
                NumericWheelAdapter minAdapter = new NumericWheelAdapter(Main.this, 00, 59);
                minAdapter.setItemResource(R.layout.wheel_item_time);
                minAdapter.setItemTextResource(R.id.time_item);
                min.setViewAdapter(minAdapter);

//Configure am/pm Marker Column
                WheelView ampm = (WheelView) dialog.findViewById(R.id.ampm);
                ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(Main.this, ampmArray);
                ampmAdapter.setItemResource(R.layout.wheel_item_time);
                ampmAdapter.setItemTextResource(R.id.time_item);
                ampm.setViewAdapter(ampmAdapter);

                dialog.show();


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
                WheelView hour = (WheelView) dialog.findViewById(R.id.timerhour);
                NumericWheelAdapter hourAdapter = new NumericWheelAdapter(Main.this, 1, 12);
                hourAdapter.setItemResource(R.layout.wheel_item_time);
                hourAdapter.setItemTextResource(R.id.time_item);
                hour.setViewAdapter(hourAdapter);

//Configure Minutes Column
                WheelView min = (WheelView) dialog.findViewById(R.id.timerminute);
                NumericWheelAdapter minAdapter = new NumericWheelAdapter(Main.this, 00, 59);
                minAdapter.setItemResource(R.layout.wheel_item_time);
                minAdapter.setItemTextResource(R.id.time_item);
                min.setViewAdapter(minAdapter);

//Configure am/pm Marker Column
                WheelView sec = (WheelView) dialog.findViewById(R.id.timersec);
                NumericWheelAdapter secAdapter = new NumericWheelAdapter(Main.this, 00, 59);
                secAdapter.setItemResource(R.layout.wheel_item_time);
                secAdapter.setItemTextResource(R.id.time_item);
                sec.setViewAdapter(minAdapter);

                dialog.show();






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
}
