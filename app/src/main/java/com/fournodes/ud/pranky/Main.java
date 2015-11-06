package com.fournodes.ud.pranky;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main extends FragmentActivity implements SoundSelectListener {
    protected   View decorView;

    public me.relex.circleindicator.CircleIndicator mIndicator;
    protected ViewPager awesomePager;
    protected PagerAdapter pm;
    protected ImageView clock;
    protected ImageView timer;
    private int sound;



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


        awesomePager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (me.relex.circleindicator.CircleIndicator) findViewById(R.id.pagerIndicator);

        ArrayList<Integer> a = new ArrayList<Integer>();

        //Category m = new Category();

        for(int i = 0; i < images.length; i++) {
            a.add(i, images[i]);
            //.resid = a.get(i);
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




}
