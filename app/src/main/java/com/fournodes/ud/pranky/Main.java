package com.fournodes.ud.pranky;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.fournodes.ud.pranky.AppBGMusic.getInstance;

public class Main extends FragmentActivity implements SoundSelectListener {
    protected   View decorView;

    public me.relex.circleindicator.CircleIndicator mIndicator;
    protected ViewPager awesomePager;
    protected PagerAdapter pm;
    protected ImageView clock;
    protected ImageView timer;
    protected ImageView settings;
    private int sound;
    AppBGMusic mService;
    boolean mBound;
    private AppBGMusic player =getInstance();
    private int pageNo=0;



    int images[] = {
            R.mipmap.annoyed, R.mipmap.vibrate,
            R.mipmap.bee, R.mipmap.cat,
            R.mipmap.current, R.mipmap.hammer,
            R.mipmap.glassbreak, R.mipmap.gun,
            R.mipmap.farting, R.mipmap.watertap,
            R.mipmap.cricket, R.mipmap.bomb,
            R.mipmap.footsteps, R.mipmap.mosquito,
            R.mipmap.airhorn,R.mipmap.elecrazor,
            R.mipmap.nail,R.mipmap.scratch,
            R.mipmap.bird,R.mipmap.windblow,
            R.mipmap.door,R.mipmap.spoon,
            R.mipmap.paper,R.mipmap.waterdrop};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        awesomePager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (me.relex.circleindicator.CircleIndicator) findViewById(R.id.pagerIndicator);

        ArrayList<Integer> a = new ArrayList<Integer>();

        //Category m = new Category();

        for(int i = 0; i < images.length; i++) {
            a.add(i, images[i]);
            //.resid = a.get(i);
        }

        PrankyDB prankyDB = new PrankyDB(this);
        SQLiteDatabase db = prankyDB.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PrankyDB.COLUMN_PIC_LOC
        };

        Cursor c = db.rawQuery("SELECT * FROM usr_sounds",null);

        if (c.moveToFirst()) {

            while (c.isAfterLast() == false) {
                String name = c.getString(c.getColumnIndex(PrankyDB.COLUMN_PIC_LOC));

                a.add(Integer.valueOf(name));
                c.moveToNext();
            }
        }




        //codeCategory = new ArrayList<Category>();
        //codeCategory.add(m);

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
            if (!it.hasNext()){
                GridItems itmLst = new GridItems(i, R.mipmap.addmore);
                imLst.add(itmLst);

            }
            GridItems[] gp = {};
            GridItems[] gridPage = imLst.toArray(gp);
            GridFragment Gfrag = new GridFragment(gridPage, Main.this);
            Gfrag.setRetainInstance(true);
            gridFragments.add(Gfrag);
        }


        pm = new PagerAdapter(getSupportFragmentManager(), gridFragments);
        awesomePager.setAdapter(pm);
        awesomePager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        awesomePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {



                Log.e("POSITION", String.valueOf(position));

                Fragment fragment = (Fragment) pm.instantiateItem(awesomePager, pageNo);
                if (fragment instanceof IGridFragment) {
                    ((IGridFragment) fragment).pageScrolled();
                }

                pageNo = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mIndicator.setViewPager(awesomePager);

        clock = (ImageView) findViewById(R.id.clock_btn);
        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sound == 0)
                    Toast.makeText(Main.this, "Please select a sound first.", Toast.LENGTH_SHORT).show();
                else {
                    ClockDialog cDialog = new ClockDialog(Main.this, sound);
                    cDialog.show();
                }
            }
        });

        timer = (ImageView) findViewById(R.id.timer_btn);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sound == 0)
                    Toast.makeText(Main.this, "Please select a sound first.", Toast.LENGTH_SHORT).show();
                else {
                    TimerDialog tDialog = new TimerDialog(Main.this, sound);
                    tDialog.show();
                }
            }
        });
        settings = (ImageView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsDialog sett = new SettingsDialog(Main.this);
                sett.show();
            }
        });



    }



    @Override
    public void selectedSound(int sound) {
        Toast.makeText(Main.this, String.valueOf(sound), Toast.LENGTH_SHORT).show();
        this.sound=sound;
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
    public void onDestroy(){
        super.onDestroy();
        if(player.mp!=null) {
            if (player.mp.isPlaying()) {
                player.mp.stop();
                player.mp.release();
            }
        }
    }



}
