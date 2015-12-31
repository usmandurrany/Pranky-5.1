package com.fournodes.ud.pranky;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.List;

public class Main extends FragmentActivity {

    private me.relex.circleindicator.CircleIndicator mIndicator;
    private View decorView;
    private ViewPager mGridPager;
    private PagerAdapter pm;
    private ImageView clock;
    private ImageView timer;
    private ImageView settings;
    private ImageView prankbtn;
    private View rootView;
    private ShowcaseView showcaseView;
    private CustomToast cToast;
    private ObjectAnimator anim;

    private int itemsOnPage = 9;
    private int pageNo = 0;
    private int steps = 2;
    private boolean open = false;
    private boolean timerLaunch = false;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            switch (message) {
                case "custom-sound-added":
                    createFragments();
                    mGridPager.setCurrentItem(pm.getCount() - 1);
                    break;
                case "prank-response-received": {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Your  friend  is  unavailable ");
                    cToast.show();
                    break;
                }
                case "prank-successful": {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Your  friend  has  been  pranked ");
                    cToast.show();
                    break;
                }
                case "server-not-found": {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Can't  connect  to  server ");
                    cToast.show();
                    break;
                }
                case "network-not-available": {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Network  unavailable ");
                    cToast.show();
                    break;
                }
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
        final ImageView smTerms = (ImageView) findViewById(R.id.smTerms);

        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!open) {

                    sideMenu.setBackgroundResource(R.drawable.sm_hide);
                    anim = ObjectAnimator.ofFloat(sideMenu, "translationX", 0, dipsToPixels(130));
                    open = true;


                } else {
                    anim = ObjectAnimator.ofFloat(sideMenu, "translationX", dipsToPixels(130), 0);
                    open = false;
                    sideMenu.setBackgroundResource(R.drawable.sm_show);
                }

                anim.setDuration(500);
                anim.start();

            }

        });

        smInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoDialog infoDialog = new InfoDialog(Main.this);
                infoDialog.show();
            }
        });

        smHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.setAppFirstLaunch(true);
                SharedPrefs.setTimerFirstLaunch(true);
                SharedPrefs.setClockFirstLaunch(true);
                SharedPrefs.setAddmoreFirstLaunch(true);
                SharedPrefs.setSettingsFirstLaunch(true);
                SharedPrefs.setRemotePrankFirstLaunch(true);
                steps = 2;
                showTutorial();
            }
        });

        smTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent terms = new Intent(Intent.ACTION_VIEW);
                terms.setData(Uri.parse("http://pranky.four-nodes.com/terms.html"));
                startActivity(terms);
            }
        });

        showTutorial();


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
                if (mGridPager.getCurrentItem() == pm.getCount() - 1 && state == 0) {
                    android.support.v4.app.Fragment frag = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, pm.getCount() - 1);
                    ((IFragment) frag).pageLast();
                }
            }
        });


        GCMRegister gcmReg = new GCMRegister(this);
        gcmReg.run();


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

                    Intent clockDialog = new Intent(Main.this, ClockDialog.class);
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
                    timerLaunch=true;
                    Intent timerDialog = new Intent(Main.this, TimerDialog.class);
                    startActivity(timerDialog);
                }
            }
        });
        settings = (ImageView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.setBgMusicPlaying(true);
                Intent settingsDialog = new Intent(Main.this, SettingsDialog.class);
                startActivity(settingsDialog);

            }
        });

        prankbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SharedPrefs.setBgMusicPlaying(true);
                Intent prankDialog = new Intent(Main.this, PrankDialog.class);
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
                    Intent prankDialog = new Intent(Main.this, PrankDialog.class);
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

        if (SharedPrefs.isCusSoundAdded()) {
            createFragments();
            mGridPager.setCurrentItem(pm.getCount() - 1);

        }
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
        Log.e("Main", "Paused");
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
        if (SharedPrefs.prefs == null)
            new SharedPrefs(this);
        SharedPrefs.setCusSoundAdded(false);
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


    private int dipsToPixels(int dips) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dips * scale + 0.5f);
    }

    public void showTutorial() {

        if (SharedPrefs.isAppFirstLaunch()) {
            ViewTarget target = new ViewTarget(R.id.pager, this);
            showcaseView = new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(target)
                    .setContentTitle("Pick a sound")
                    .setContentText("Tap on the image to preview sound and select it")
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Sound.sysSound != -1 || Sound.cusSound != null) {
                                if (steps == 3 && !timerLaunch) //Dont proceed untill timer dialog has been launched once
                                    steps=2;
                                switch (steps) {
                                    case 2:
                                        showcaseView.setShowcase(new ViewTarget(timer), true);
                                        showcaseView.setContentTitle("Set playback time");
                                        showcaseView.setContentText("Tap on the timer icon to play the sound after a specified interval");
                                        steps++;
                                        break;
                                    case 3:
                                        showcaseView.setShowcase(new ViewTarget(clock), true);
                                        showcaseView.setContentTitle("Set playback time");
                                        showcaseView.setContentText("You can also schedule the playback by tapping on the clock button");
                                        steps++;
                                        break;

                                    case 4:
                                        showcaseView.setShowcase(new ViewTarget(settings), true);
                                        showcaseView.setContentTitle("App settings");
                                        showcaseView.setContentText("Tap on the settings icon to view application settings");
                                        steps++;
                                        break;
                                    case 5:
                                        showcaseView.setShowcase(new ViewTarget(prankbtn), true);
                                        showcaseView.setContentTitle("Prank a friend");
                                        showcaseView.setContentText("Pick a sound then press 'Prank' to send it directly to your friend's phone");
                                        steps++;
                                        break;
                                    case 6:
                                        showcaseView.hide();
                                        mGridPager.setCurrentItem(pm.getCount() - 1);
                                        break;


                                }
                            }

                        }
                    })
                    .build();
        }
    }
}

