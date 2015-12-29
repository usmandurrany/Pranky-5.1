package com.fournodes.ud.pranky;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.formats.NativeAd;
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

public class Main extends FragmentActivity implements SoundSelectListener, View.OnClickListener {
    public me.relex.circleindicator.CircleIndicator mIndicator;
    protected View decorView;
    View rootView;
    protected ViewPager mGridPager;
    protected PagerAdapter pm;
    protected ImageView clock;
    protected ImageView timer;
    protected ImageView settings;
    protected ImageView prankbtn;
    ShowcaseView showcaseView;
    int itemsOnPage = 9;
    int soundRep;
    int soundVol;
    CustomToast cToast;
    private int sound = -1;
    private String soundCus = null;
//    private BackgroundMusic player = getInstance(getApplicationContext());
    private int pageNo = 0;
    RegisterOnServer rs;
    int steps=1;
    boolean open = false;
    ObjectAnimator anim;

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
            } else if (message.equals("server-not-found")){
                CustomToast cToast = new CustomToast(getApplicationContext(), "Cant connect to server");
                cToast.show();
            } else if (message.equals("network-not-available")){
                CustomToast cToast = new CustomToast(getApplicationContext(), "Network is unavailable");
                cToast.show();
            }else if (message.equals("get-id")){
                initGCM();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        setContentView(rootView);

        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        mGridPager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (me.relex.circleindicator.CircleIndicator) findViewById(R.id.pagerIndicator);

        prankbtn = (ImageView) findViewById(R.id.prankit);
        timer = (ImageView) findViewById(R.id.timer_btn);
        final RelativeLayout sideMenu = (RelativeLayout) findViewById(R.id.sideMenu);
        final ImageView smInfo = (ImageView) findViewById(R.id.smInfo);
        final ImageView smHelp = (ImageView) findViewById(R.id.smHelp);

        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!open) {

                    sideMenu.setBackgroundResource(R.drawable.sm_hide);
                    anim = ObjectAnimator.ofFloat(sideMenu, "translationX", 0, dipsToPixels(80));
                    open=true;


                }else{
                    anim = ObjectAnimator.ofFloat(sideMenu, "translationX", dipsToPixels(80), 0);
                    open=false;
                    sideMenu.setBackgroundResource(R.drawable.sm_show);
          }

                anim.setDuration(500);
                anim.start();

                }

        });


        ViewTarget target = new ViewTarget(R.id.pager,this);
        /*Target target = new Target() {
            @Override
            public Point getPoint() {
                // Get approximate position of overflow action icon's center

                float centreX=prankbtn.getX() + prankbtn.getWidth()  / 2;
                float centreY=prankbtn.getY() + prankbtn.getHeight() / 2;


                return new Point((int) centreX, (int) centreY +125);
            }
        };*/
        mGridPager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mGridPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                Log.e("POSITION", String.valueOf(position));

                android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, pageNo);
                //ImageView test = (ImageView) fragment.getView().findViewById(R.id.grid_item_image);


                if (fragment instanceof IFragment) {
                    ((IFragment) fragment).pageScrolled();
                }

                pageNo = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
if (mGridPager.getCurrentItem() == pm.getCount()-1 && state == 0) {
    android.support.v4.app.Fragment frag = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, pm.getCount() - 1);
    ((IFragment) frag).pageLast();
}
            }
        });

    if (SharedPrefs.isAppFirstLaunch()) {

        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(target)
                .setContentTitle("Pick a sound")
                .setContentText("Tap on the image to preview sound and select it")
                .setStyle(R.style.CustomShowcaseTheme2)
                .hideOnTouchOutside()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        steps++;
                        switch (steps) {
                            case 2:
                                showcaseView.setShowcase(new ViewTarget(timer), true);
                                showcaseView.setContentTitle("Set playback time");
                                showcaseView.setContentText("Tap on the timer icon to play the sound after a specified interval");
                                break;
                            case 3:
                                showcaseView.setShowcase(new ViewTarget(clock), true);
                                showcaseView.setContentTitle("Set playback time");
                                showcaseView.setContentText("You can also schedule the playback by tapping on the clock button");
                                break;

                            case 4:
                                showcaseView.setShowcase(new ViewTarget(settings), true);
                                showcaseView.setContentTitle("App settings");
                                showcaseView.setContentText("Tap on the settings icon to view application settings");
                                break;
                            case 5:
                                showcaseView.setShowcase(new ViewTarget(prankbtn), true);
                                showcaseView.setContentTitle("Prank a friend");
                                showcaseView.setContentText("Pick a sound then press 'Prank' to send it directly to your friend's phone");
                                break;
                            case 6:
                                showcaseView.hide();
                                mGridPager.setCurrentItem(pm.getCount() - 1);

                                break;


                        }


                    }
                })
                .build();
    }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        float scaleFactor = metrics.density;
//        Toast.makeText(Main.this, String.valueOf(scaleFactor), Toast.LENGTH_SHORT).show();
/*
        Intent intent = new Intent("CONNECTIVITY_CHECK");
        sendBroadcast(intent);*/


        initGCM();




        createFragments();



        mIndicator.setViewPager(mGridPager);

        clock = (ImageView) findViewById(R.id.clock_btn);
        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Sound.sysSound == -1 && Sound.cusSound == null) {
                    cToast = new CustomToast(getApplicationContext(), "Select  a  sound  first");
                    cToast.show();
                } else {
                    SharedPrefs.setBgMusicPlaying(true);

                    Intent clockDialog = new Intent(Main.this,ClockDialog.class);
                    startActivity(clockDialog);
                }
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Sound.sysSound == -1 && Sound.cusSound == null) {
                    cToast = new CustomToast(getApplicationContext(), "Select  a  sound  first");
                    cToast.show();
                } else {
                    SharedPrefs.setBgMusicPlaying(true);

                    Intent timerDialog = new Intent(Main.this,TimerDialog.class);
                    startActivity(timerDialog);
                }
            }
        });
        settings = (ImageView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.setBgMusicPlaying(true);
                Intent settingsDialog = new Intent(Main.this,SettingsDialog.class);
                startActivity(settingsDialog);

            }
        });

        prankbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SharedPrefs.setBgMusicPlaying(true);
                Intent prankDialog = new Intent(Main.this,PrankDialog.class);
                startActivity(prankDialog);

                return false;
            }
        });
        prankbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (Sound.sysSound == -1 && Sound.cusSound == null) {
                        cToast = new CustomToast(Main.this, "Select  a  sound  first");
                        cToast.show();
                    } else if (Sound.sysSound == -1 && Sound.cusSound != null) {

                        cToast = new CustomToast(Main.this, "A  non-custom  sound  should  be  selected");
                        cToast.show();
                    } else if (SharedPrefs.getFrndAppID() == null) {
                        SharedPrefs.setBgMusicPlaying(true);
                        Intent prankDialog = new Intent(Main.this,PrankDialog.class);
                        startActivity(prankDialog);

                    } else {
                        SendMessage sendMessage = new SendMessage(Main.this);
                        sendMessage.initDialog();
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
        Log.e("Main","Paused");
        try {
            if (BackgroundMusic.mp != null && !SharedPrefs.isBgMusicPlaying()) {
                BackgroundMusic.pause();
            }
        } catch (Exception e) {
            Log.e("BG Music Pause", e.toString());
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (BackgroundMusic.mp != null) {
                BackgroundMusic.stop();
            }
        } catch (Exception e) {
            Log.e("Main Destroy", e.toString());
        }


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


public void initGCM() {
    try {
        // Convert the expDate in shared prefs to CALENDAR type for comparision
        Calendar exp = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        exp.setTime(sdf.parse(SharedPrefs.getExpDate()));

        // Get current Time from device for comparison
        Calendar today = Calendar.getInstance(TimeZone.getDefault());


        // Perform Checks for true
        //serverState is 1 and myAppID has expired but myGcmID is set
        if (SharedPrefs.getServerState() == 1 && exp.before(today) && SharedPrefs.getMyGcmID() != null) {
            // Request new appID from server

            // Initialize the RegisterOnServer class
            rs = new RegisterOnServer(Main.this);
            SharedPrefs.setMyAppID(""); // First clear the myAppID on device
            // Send myGcmID but empty myAppID
            String[] args = {SharedPrefs.getMyGcmID(), SharedPrefs.getMyAppID()};
            rs.execute(args);
        }
        // serverState is 1 and myGcmId is not set
        else if (SharedPrefs.getServerState() == 1 && SharedPrefs.getMyGcmID() == null && SharedPrefs.getMyGcmID() == null) {
            if (checkPlayServices()) {
                Intent register = new Intent(Main.this, RegistrationIntentService.class);
                Main.this.startService(register);
            }


        }


    } catch (Exception e) {
        Log.e("Init GCM", e.toString());
    }
}

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(Main.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
               apiAvailability.getErrorDialog(Main.this, resultCode, 9000)
                .show();
                Log.w("Play Services","Play Services Not Found");
            } else {
                Log.i("Play Services", "This device is not supported.");
            }
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View view) {
     //showcaseView.setShowcase(new ViewTarget(settings), true);
    }

    private int dipsToPixels(int dips)
    {
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(dips * scale + 0.5f);
    }
}

