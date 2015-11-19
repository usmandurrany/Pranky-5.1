package com.fournodes.ud.pranky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.fournodes.ud.pranky.AppBGMusic.getInstance;

public class Main extends FragmentActivity implements SoundSelectListener {
    protected   View decorView;

    public me.relex.circleindicator.CircleIndicator mIndicator;
    protected ViewPager mGridPager;
    protected PagerAdapter pm;
    protected ImageView clock;
    protected ImageView timer;
    protected ImageView settings;
    private int sound;
    private String soundCus;
    AppBGMusic mService;
    boolean mBound;
    private AppBGMusic player =getInstance();
    private int pageNo=0;
    int itemsOnPage=9;
    int soundRep;
    int soundVol;
    TextView text;
    View toast;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);


createFragments();
            mGridPager.setCurrentItem(pm.getCount() - 1);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutInflater inflater = getLayoutInflater();
        toast = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toastRoot));

        text = (TextView) toast.findViewById(R.id.toastText);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-sound-added"));

        mGridPager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (me.relex.circleindicator.CircleIndicator) findViewById(R.id.pagerIndicator);


        createFragments();


        mGridPager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mGridPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                Log.e("POSITION", String.valueOf(position));

                android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, pageNo);
                if (fragment instanceof IFragment) {
                    ((IFragment) fragment).pageScrolled();
                }

                pageNo = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mIndicator.setViewPager(mGridPager);

        clock = (ImageView) findViewById(R.id.clock_btn);
        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sound == 0 && soundCus == null) {
                    //Toast.makeText(Main.this, "Please select a sound first.", Toast.LENGTH_SHORT).show();


                    text.setText("Hello! This is a custom toast!");

                    Toast cusToast = new Toast(getApplicationContext());
                    cusToast.setGravity(Gravity.BOTTOM, 0, 220);

                    cusToast.setDuration(Toast.LENGTH_LONG);
                    cusToast.setView(toast);
                    cusToast.show();
                }
               else{
                    ClockDialog cDialog = new ClockDialog(Main.this,sound, soundCus,soundRep,soundVol);
                    cDialog.show();
                }
            }
        });

        timer = (ImageView) findViewById(R.id.timer_btn);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sound == 0 && soundCus == null)
                    Toast.makeText(Main.this, "Please select a sound first.", Toast.LENGTH_SHORT).show();
                 else {
                    TimerDialog tDialog = new TimerDialog(Main.this, sound,soundCus,soundRep,soundVol);
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
    protected void onResume() {
        super.onResume();
        try{if(player.mp!=null) {
            player.mp.start();
        }}catch(Exception e){Log.e("BG Music Resume",e.toString());}
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{if(player.mp!=null) {
            player.mp.pause();
        }}catch(Exception e){Log.e("BG Music Pause",e.toString());}
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try{if(player.mp!=null) {
            player.mp.release();
        }}catch(Exception e){Log.e("BG Music Destroy",e.toString());}
    }
public void createFragments(){
    int id=0;
    int i=1;
    boolean lastItemAdded=false;
    List<GridFragment> gridGridFragments = new ArrayList<GridFragment>();


    PrankyDB prankyDB = new PrankyDB(this);
    SQLiteDatabase db = prankyDB.getReadableDatabase();


    Cursor c = db.rawQuery("SELECT * FROM usr_sounds",null);

    if (c.moveToFirst()) {

        while (!c.isAfterLast() || !lastItemAdded) {
            ArrayList<GridItems> imLst = new ArrayList<GridItems>();
            if (!c.isAfterLast()) {
                for ( i = 1; (i <= itemsOnPage) && (!c.isAfterLast()); i++) {
                    id = c.getInt(c.getColumnIndex(PrankyDB.COLUMN_ID));
                    Integer image = c.getInt(c.getColumnIndex(PrankyDB.COLUMN_PIC_LOC));
                    String sound = c.getString(c.getColumnIndex(PrankyDB.COLUMN_SOUND_LOC));
                    Integer soundRepeat = c.getInt(c.getColumnIndex(PrankyDB.COLUMN_REPEAT_COUNT));
                    Integer soundVol = c.getInt(c.getColumnIndex(PrankyDB.COLUMN_SOUND_VOL));

                    GridItems items = new GridItems(id, image, sound, soundRepeat,soundVol);
                    imLst.add(items);
                    c.moveToNext();

                }
            }
            if (c.isAfterLast() && i < itemsOnPage) {

                GridItems lstItem = new GridItems(id, R.mipmap.addmore);
                imLst.add(lstItem);
                lastItemAdded=true;

            }
            GridItems[] gp = {};
            GridItems[] gridPage = imLst.toArray(gp);
            GridFragment Gfrag = new GridFragment(gridPage, Main.this);
            Gfrag.setRetainInstance(true);
            gridGridFragments.add(Gfrag);
            i=1;

        }
    }
    c.close();


    mGridPager.setAdapter(null);
    pm = new PagerAdapter(getSupportFragmentManager(), gridGridFragments,Main.this);
    mGridPager.setAdapter(pm);
}


    @Override
    public void selectedSound(int sound, String soundStr, int SoundRep, int Soundvol) {
        this.sound=sound;
        this.soundCus=soundStr;
        this.soundRep= SoundRep;
        this.soundVol=Soundvol;

        Toast.makeText(Main.this,"Repeat Count: " + soundRep + " Sound Vol : "+ soundVol, Toast.LENGTH_LONG).show();


    }
}
