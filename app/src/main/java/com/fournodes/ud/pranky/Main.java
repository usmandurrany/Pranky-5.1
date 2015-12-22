package com.fournodes.ud.pranky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.fournodes.ud.pranky.BackgroundMusic.getInstance;

public class Main extends FragmentActivity implements SoundSelectListener {
    public me.relex.circleindicator.CircleIndicator mIndicator;
    protected View decorView;
    View rootView;
    protected ViewPager mGridPager;
    protected PagerAdapter pm;
    protected ImageView clock;
    protected ImageView timer;
    protected ImageView settings;
    protected ImageView prankbtn;

    int itemsOnPage = 9;
    int soundRep;
    int soundVol;
    CustomToast cToast;
    private int sound = -1;
    private String soundCus = null;
//    private BackgroundMusic player = getInstance(getApplicationContext());
    private int pageNo = 0;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            if (message.equals("custom-sound-added")){
                createFragments();
                mGridPager.setCurrentItem(pm.getCount() - 1);
            } else if (message.equals("prank-response-received")){
                CustomToast cToast = new CustomToast(getApplicationContext(), "Your Friend is not prankable at the moment");
                cToast.show();
            } else if (message.equals("network-changed")) {
                prankbtn.setEnabled(SharedPrefs.isPrankBtnEnabled());
            }
            else if (message.equals("prank-successful")){
                CustomToast cToast = new CustomToast(getApplicationContext(), "Your Friend has been successfully pranked");
                cToast.show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        setContentView(rootView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        float scaleFactor = metrics.density;
//        Toast.makeText(Main.this, String.valueOf(scaleFactor), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent("CONNECTIVITY_CHECK");
        sendBroadcast(intent);

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
                if (Sound.sysSound == -1 && Sound.cusSound == null) {
                    cToast = new CustomToast(getApplicationContext(), "Select  a  sound  first");
                    cToast.show();
                } else {
                    ClockDialog cDialog = new ClockDialog(Main.this);
                    cDialog.show();
                }
            }
        });

        timer = (ImageView) findViewById(R.id.timer_btn);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Sound.sysSound == -1 && Sound.cusSound == null) {
                    cToast = new CustomToast(getApplicationContext(), "Select  a  sound  first");
                    cToast.show();
                } else {
                    TimerDialog tDialog = new TimerDialog(Main.this);
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

        prankbtn = (ImageView) findViewById(R.id.prankit);
        prankbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Sound.sysSound == -1 && Sound.cusSound == null) {
                    cToast = new CustomToast(Main.this, "Select  a  sound  first");
                    cToast.show();
                } else if(Sound.sysSound == -1 && Sound.cusSound != null) {

                    cToast = new CustomToast(Main.this, "A  non-custom  sound  should  be  selected");
                    cToast.show();
                } else if(SharedPrefs.getFrndAppID()== null) {
                    PrankDialog pDialog = new PrankDialog(Main.this);
                    pDialog.show();
                } else{
                    SendMessage sendMessage = new SendMessage(getApplicationContext(), true);
                    sendMessage.execute("prank");

                }
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
        try {
            if (BackgroundMusic.mp != null) {
                BackgroundMusic.play();
            }
        } catch (Exception e) {
            Log.e("BG Music Resume", e.toString());
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("main-activity-broadcast"));
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
    public void onDestroy() {
        super.onDestroy();
        try {
            if (BackgroundMusic.mp != null) {
                BackgroundMusic.stop();
            }
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {
            Log.e("Main Destroy", e.toString());
        }
        SharedPrefs.setFrndAppID(null);


        unbindDrawables(rootView);
        rootView = null;
        System.gc();
    }

    public void createFragments() {
        int id = 0;
        int i = 1;
        boolean lastItemAdded = false;
        List<GridFragment> gridGridFragments = new ArrayList<GridFragment>();


        PrankyDB prankyDB = new PrankyDB(this);
        SQLiteDatabase db = prankyDB.getReadableDatabase();


        Cursor c = db.rawQuery("SELECT * FROM usr_sounds", null);

        if (c.moveToFirst()) {

            while (!c.isAfterLast() || !lastItemAdded) {
                ArrayList<GridItems> imLst = new ArrayList<GridItems>();
                if (!c.isAfterLast()) {
                    for (i = 1; (i <= itemsOnPage) && (!c.isAfterLast()); i++) {
                        id = c.getInt(c.getColumnIndex(PrankyDB.COLUMN_ID));
                        Integer image = c.getInt(c.getColumnIndex(PrankyDB.COLUMN_PIC_LOC));
                        String sound = c.getString(c.getColumnIndex(PrankyDB.COLUMN_SOUND_LOC));
                        Integer soundRepeat = c.getInt(c.getColumnIndex(PrankyDB.COLUMN_REPEAT_COUNT));
                        Integer soundVol = c.getInt(c.getColumnIndex(PrankyDB.COLUMN_SOUND_VOL));

                        GridItems items = new GridItems(id, image, sound, soundRepeat, soundVol);
                        imLst.add(items);
                        c.moveToNext();

                    }
                }
                if (c.isAfterLast() && i < itemsOnPage) {

                    GridItems lstItem = new GridItems(id, R.mipmap.addmore, "addmore");
                    imLst.add(lstItem);
                    lastItemAdded = true;

                }
                GridItems[] gp = {};
                GridItems[] gridPage = imLst.toArray(gp);
                GridFragment Gfrag = new GridFragment(gridPage, Main.this);
                Gfrag.setRetainInstance(true);
                gridGridFragments.add(Gfrag);
                i = 1;

            }
        }
        c.close();


        pm = new PagerAdapter(getSupportFragmentManager(), gridGridFragments, getApplicationContext());
        mGridPager.setAdapter(pm);
    }


    @Override
    public void selectedSound(int sound, String soundStr, int SoundRep, int Soundvol) {
        this.sound = sound;
        this.soundCus = soundStr;
        this.soundRep = SoundRep;
        this.soundVol = Soundvol;



    }




    protected void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }

        }

    }


//    public boolean isNetworkAvailable() {
//        final ConnectivityManager connectivityManager = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE));
//        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
//    }
}
