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
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import android.os.Handler;
import java.util.logging.LogRecord;

public class Main extends FragmentActivity {

    private View rootView;
    private View decorView;
    private ViewPager mGridPager;
    private PagerAdapter pm;
    private ImageView clock;
    private ImageView timer;
    private ImageView settings;
    private ImageView prankbtn;
    private ShowcaseView showcaseView;
    private CustomToast cToast;
    private ObjectAnimator anim;
    private RelativeLayout sideMenu;
    private ImageView smInfo;
    private ImageView smHelp;
    private ImageView smTerms;
    private me.relex.circleindicator.CircleIndicator mIndicator;

    private int itemsOnPage = 9;
    private int pageNo = 0;
    private int steps = 2;
    private boolean open = false;
    private boolean timerLaunch = false;
    private Handler smClose;
    private android.support.v4.app.Fragment currPage;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            switch (message) {
                case "prank-sent":
                    break;

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

                case "network-error": {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Network  or server unavailable ");
                    cToast.show();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(rootView);
        onWindowFocusChanged(true);

        /*********************************** Initializations ***************************************/

        mIndicator = (me.relex.circleindicator.CircleIndicator) findViewById(R.id.pagerIndicator);
        mGridPager = (ViewPager) findViewById(R.id.pager);
        prankbtn = (ImageView) findViewById(R.id.prankit);
        timer = (ImageView) findViewById(R.id.timer_btn);
        sideMenu = (RelativeLayout) findViewById(R.id.sideMenu);
        smInfo = (ImageView) findViewById(R.id.smInfo);
        smHelp = (ImageView) findViewById(R.id.smHelp);
        smTerms = (ImageView) findViewById(R.id.smTerms);
        clock = (ImageView) findViewById(R.id.clock_btn);
        settings = (ImageView) findViewById(R.id.settings);

        GCMRegister gcmReg = new GCMRegister(this);
        gcmReg.run();
        createFragments();
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

                if (fragment instanceof IFragment)
                    ((IFragment) fragment).pageScrolled();

                pageNo = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                currPage = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, mGridPager.getCurrentItem());

                if (mGridPager.getCurrentItem() == pm.getCount() - 1 && state == 0) { //State 0 = page has settled
                    android.support.v4.app.Fragment frag = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, pm.getCount() - 1);
                    ((IFragment) frag).pageLast();
                }
            }
        });
        mIndicator.setViewPager(mGridPager);

        /*********************************** Click Listeners ******************************************/

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Sound.sysSound == -1 && Sound.cusSound == null) {
                    cToast = new CustomToast(getApplicationContext(), "Select  a  sound  first");
                    cToast.show();
                    if (currPage!=null)
                        ((IFragment) currPage).animateIcon();
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
                    if (currPage!=null)
                        ((IFragment) currPage).animateIcon();
                } else {
                    SharedPrefs.setBgMusicPlaying(true);
                    timerLaunch = true;
                    Intent timerDialog = new Intent(Main.this, TimerDialog.class);
                    startActivity(timerDialog);
                }
            }
        });
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
                    if (currPage!=null)
                        ((IFragment) currPage).animateIcon();
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

        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!open) {

                    sideMenu.setBackgroundResource(R.drawable.sm_hide);
                    anim = ObjectAnimator.ofFloat(sideMenu, "translationX", 0, dipsToPixels(130));
                    open = true;

                   smClose = new Handler();
                    smClose.postDelayed(new Runnable() {
                        public void run() {
                            anim = ObjectAnimator.ofFloat(sideMenu, "translationX", dipsToPixels(130), 0);
                            open = false;
                            sideMenu.setBackgroundResource(R.drawable.sm_show);
                            anim.setDuration(500);
                            anim.start();
                            smClose.removeCallbacks(this);
                        }
                    }, 3000);


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

        currPage = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, mGridPager.getCurrentItem());

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
                            Log.e("Step",String.valueOf(steps));
                            if (Sound.sysSound != -1 || Sound.cusSound != null || steps > 2 ) {
                                if (steps == 3 && !timerLaunch) { //Dont proceed untill timer dialog has been launched once
                                    Animation grow = AnimationUtils.loadAnimation(Main.this, R.anim.grow_bounce);
                                    grow.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            Animation shrink = AnimationUtils.loadAnimation(Main.this, R.anim.shrink);
                                            timer.startAnimation(shrink);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                    timer.startAnimation(grow);
                                    steps = 2;
                                }
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

                                   /* case 4:
                                        showcaseView.setShowcase(new ViewTarget(settings), true);
                                        showcaseView.setContentTitle("App settings");
                                        showcaseView.setContentText("Tap on the settings icon to view application settings");
                                        steps++;
                                        break;*/
                                    case 4:
                                        showcaseView.setShowcase(new ViewTarget(prankbtn), true);
                                        showcaseView.setContentTitle("Prank a friend");
                                        showcaseView.setContentText("Pick a sound then press 'Prank' to send it directly to your friend's phone");
                                        showcaseView.setStyle(R.style.CustomShowcaseTheme3);
                                        steps++;
                                        break;

                                    case 5:
                                        showcaseView.hide();
                                        SharedPrefs.setAppFirstLaunch(false);
                                        break;
                                 /*   case 6:
                                        showcaseView.hide();
                                        mGridPager.setCurrentItem(pm.getCount() - 1);
                                        break;*/


                                }
                            }

                        }
                    })
                    .build();
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
    protected void onResume() {
        super.onResume();
        if (showcaseView != null && timerLaunch && SharedPrefs.isAppFirstLaunch()) {
            showcaseView.hide();
        new CountDownTimer(5000,1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
            //showTutorial();
                showcaseView.show();
                showcaseView.setShowcase(new ViewTarget(clock), true);
                showcaseView.setContentTitle("Set playback time");
                showcaseView.setContentText("You can also schedule the playback by tapping on the clock button");
                steps++;

            }
        }.start();
            /*showcaseView.setShowcase(new ViewTarget(clock), true);
            showcaseView.setContentTitle("Set playback time");
            showcaseView.setContentText("You can also schedule the playback by tapping on the clock button");
            steps++;*/
        }

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

        Cleaner.unbindDrawables(rootView);
        rootView = null;
    }
}

