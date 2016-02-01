package com.fournodes.ud.pranky.activities;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fournodes.ud.pranky.BackgroundMusic;
import com.fournodes.ud.pranky.ContactSelected;
import com.fournodes.ud.pranky.CustomToast;
import com.fournodes.ud.pranky.DatabaseHelper;
import com.fournodes.ud.pranky.GridItem;
import com.fournodes.ud.pranky.ItemSelected;
import com.fournodes.ud.pranky.PreviewMediaPlayer;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.Tutorial;
import com.fournodes.ud.pranky.adapters.PagerAdapter;
import com.fournodes.ud.pranky.dialogs.InfoDialog;
import com.fournodes.ud.pranky.dialogs.PrePrankDialog;
import com.fournodes.ud.pranky.enums.ActionType;
import com.fournodes.ud.pranky.enums.ClassType;
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.fragments.GridFragment;
import com.fournodes.ud.pranky.gcm.GCMInitiate;
import com.fournodes.ud.pranky.interfaces.IFragment;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.fournodes.ud.pranky.utils.Cleaner;
import com.fournodes.ud.pranky.utils.FontManager;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends FragmentActivity {

    private View rootView;
    private View decorView;
    private ViewPager mGridPager;
    private PagerAdapter pm;
    private ImageView clock;
    private ImageView timer;
    private ImageView settings;
    private ImageView prankbtn;
    private CustomToast cToast;
    private ObjectAnimator anim;
    private RelativeLayout sideMenu;
    private ImageView smInfo;
    private ImageView smHelp;
    private ImageView smTerms;
    private me.relex.circleindicator.CircleIndicator mIndicator;

    private int itemsOnPage = 9;
    private int itemCount = 0;
    private int addSoundLoc = 0;


    private int pageNo = 0;
    private boolean open = false;
    private boolean timerLaunch = false;
    private boolean clockLaunch = false;
    private Handler smClose;
    private android.support.v4.app.Fragment currPage;
    private DatabaseHelper prankyDB;
    private Tutorial mTutorial;
    private boolean showTutorial;
    private PreviewMediaPlayer previewSound;


    private InterstitialAd mInterstitialAd;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            switch (Message.valueOf(intent.getStringExtra(String.valueOf(ActionType.Broadcast)))) {
                case PrankSent:
                    Log.e("Prank Sent", "Successfully");
                    break;

                case SoundAdded:
                    createFragments();
                    mGridPager.setCurrentItem(pm.getCount() - 1);
                    break;
                case PrankFailed: {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Your  friend  is  unavailable ");
                    cToast.show();
                    break;
                }
                case PrankSuccessful: {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Your  friend  has  been  pranked ");
                    cToast.show();
                    break;
                }

                case NetworkError: {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Network  or server unavailable ");
                    cToast.show();
                    break;
                }
                case UserUnregistered: {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "You  have  been  logged  out");
                    cToast.show();
                    break;
                }
                case UserRegistered: {

                    break;
                }
                case TokenGenerated: {
                    AppServerConn appServerConn = new AppServerConn(ActionType.RegisterDevice);
                    appServerConn.execute();
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

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                SharedPrefs.setPrankCount(0);
            }
        });

        requestNewInterstitial();


        /*********************************** SavedInstance Checks***********************************/

        if (SharedPrefs.prefs == null) {
            new SharedPrefs(this).initAllPrefs();
            if (FontManager.getTypeFace(this, SharedPrefs.DEFAULT_FONT) == null) {
                FontManager.createTypeFace(this, SharedPrefs.DEFAULT_FONT);

            }
        }

        /*********************************** Initializations ***************************************/

        previewSound = PreviewMediaPlayer.getInstance(this);
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

        prankyDB = new DatabaseHelper(this);
        new GCMInitiate(this).run();


        createFragments();


        mGridPager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mGridPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //Log.e("POSITION", String.valueOf(position));

                android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, pageNo);

                if (fragment instanceof IFragment)
                    ((IFragment) fragment).pageScrolled();

                pageNo = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (showTutorial && state == 0)
                    startTutorial();


                currPage = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, mGridPager.getCurrentItem());

                if (mGridPager.getCurrentItem() == pm.getCount() - 1 && state == 0) { //State 0 = page has settled
                    android.support.v4.app.Fragment frag = (android.support.v4.app.Fragment) pm.instantiateItem(mGridPager, pm.getCount() - 1);
                    ((IFragment) frag).pageLast(addSoundLoc);
                }
            }
        });
        mIndicator.setViewPager(mGridPager);

        /*********************************** Click Listeners ******************************************/

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ItemSelected.itemSound == -1 && ItemSelected.itemCustomSound == null) {
                    cToast = new CustomToast(getApplicationContext(), "Select  a  sound  first");
                    cToast.show();
                    if (currPage != null)
                        ((IFragment) currPage).shakeIcons();
                } else {
                    if (timerLaunch)
                        timerLaunch = false;
                    clockLaunch = true;
                    SharedPrefs.setBgMusicPlaying(true);

                    Intent clockDialog = new Intent(MainActivity.this, ClockDialogActivity.class);
                    startActivity(clockDialog);
                }
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ItemSelected.itemSound == -1 && ItemSelected.itemCustomSound == null) {
                    cToast = new CustomToast(getApplicationContext(), "Select  a  sound  first");
                    cToast.show();
                    if (currPage != null)
                        ((IFragment) currPage).shakeIcons();
                } else {
                    SharedPrefs.setBgMusicPlaying(true);
                    timerLaunch = true;
                    Intent timerDialog = new Intent(MainActivity.this, TimerDialogActivity.class);
                    startActivity(timerDialog);
                }
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.setBgMusicPlaying(true);
                Intent settingsDialog = new Intent(MainActivity.this, SettingsDialogActivity.class);
                startActivity(settingsDialog);

            }
        });

        prankbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (timerLaunch || clockLaunch) {
                    timerLaunch = false;
                    clockLaunch = false;
                }
                if (SharedPrefs.getInvalidIDCount() < 3) {
                    SharedPrefs.setBgMusicPlaying(true);
                    Intent prankDialog = new Intent(MainActivity.this, PrankDialogActivity.class);
                    startActivityForResult(prankDialog, 1);
                } else {
                    cToast = new CustomToast(MainActivity.this, "Please wait 60 seconds before trying again");
                    cToast.show();
                }
                return false;
            }
        });
        prankbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GCMInitiate(MainActivity.this).run();

                if (ItemSelected.itemSound == -1 && ItemSelected.itemCustomSound == null) {
                    cToast = new CustomToast(MainActivity.this, "Select  a  sound  first");
                    cToast.show();
                    if (currPage != null)
                        ((IFragment) currPage).shakeIcons();
                } else if (ItemSelected.itemSound == -1 && ItemSelected.itemCustomSound != null) {

                    cToast = new CustomToast(MainActivity.this, "A  non-custom  sound  should  be  selected");
                    cToast.show();
                } else if (SharedPrefs.getFrndAppID() == null) {
                    if (SharedPrefs.getInvalidIDCount() < 3) {
                        SharedPrefs.setBgMusicPlaying(true);
                        Intent prankDialog = new Intent(MainActivity.this, PrankDialogActivity.class);
                        startActivityForResult(prankDialog, 1);
                    } else {
                        cToast = new CustomToast(MainActivity.this, "Please wait 60 seconds before trying again");
                        cToast.show();
                    }
                } else {
                    PrePrankDialog prePrankDiag = new PrePrankDialog(MainActivity.this);
                    prePrankDiag.setFriendName(ContactSelected.getName());
                    prePrankDiag.show();
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
                            if (open) {
                                anim = ObjectAnimator.ofFloat(sideMenu, "translationX", dipsToPixels(130), 0);
                                open = false;
                                sideMenu.setBackgroundResource(R.drawable.sm_show);
                                anim.setDuration(500);
                                anim.start();
                                smClose.removeCallbacks(this);
                            }
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
                InfoDialog infoDialog = new InfoDialog(MainActivity.this);
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
                SharedPrefs.setLastPageFirstLaunch(true);
                if (mGridPager.getCurrentItem() == 0)
                    startTutorial();
                else
                    mGridPager.setCurrentItem(0, true);
                //startTutorial();
                showTutorial = true;
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

        /******************************** Contacts sync testing code **********************************/
        /*if (SharedPrefs.isSignUpComplete()) {
            Intent monitorContacts = new Intent(this, MonitorContacts.class);
            startService(monitorContacts);
        }*/
        startTutorial();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        animPrankButton();
    }

    public void createFragments() {
        String[] categories = {"horns", "people", "animals", "suspense", "objects", "mobile", "machines", "custom"};
        int id = 0;
        int i;
        boolean lastItemAdded = false;
        List<GridFragment> gridGridFragments = new ArrayList<>();


        SQLiteDatabase db = prankyDB.getReadableDatabase();

        for (int j = 0; j < categories.length; j++) {
            Cursor c = db.query(DatabaseHelper.TABLE_ITEMS, null, DatabaseHelper.COLUMN_ITEM_CATEGORY + " = ?", new String[]{categories[j]}, null, null, null);
            int count = c.getCount();
            ArrayList<GridItem> imLst = new ArrayList<>();
            // Log.e("Category ",categories[j]);
            //Log.e("Count ",String.valueOf(count));

            i = 1;
            while (c.moveToNext() || (categories[j].equals("custom") && !lastItemAdded)) {
                // Log.e("Cursor Position", String.valueOf(c.getPosition()));
                if (!c.isAfterLast() && i <= itemsOnPage) {
                    id = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_ID));
                    Integer image = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_ITEM_IMG_LOC));
                    String sound = c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_ITEM_SOUND_LOC));
                    Integer soundRepeat = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_REPEAT_COUNT));
                    Integer soundVol = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_SOUND_VOL));

                    GridItem items = new GridItem(id, image, sound, soundRepeat, soundVol);
                    imLst.add(items);
                    //  Log.e("Item no.", String.valueOf(i));
                    i++;

                } else if (((c.getPosition() == count) || c.getPosition() == count - 1 || count == 0) && i <= itemsOnPage && categories[j].equals("custom")) {

                    GridItem lstItem = new GridItem(id, R.mipmap.addmore, "addSound");
                    imLst.add(lstItem);
                    lastItemAdded = true;
                    itemCount = c.getCount();
                    addSoundLoc = addSoundLocation(itemCount); //Items count start from 0, func calculates form 1
                    // Log.e("Add Sound Loc", String.valueOf(addSoundLoc));

                }
                if (i > itemsOnPage || (c.getPosition() == count || (c.getPosition() == count - 1 && !categories[j].equals("custom")) || count == 0)) {
                    Bundle args = new Bundle();
                    GridItem[] gItem = new GridItem[imLst.size()];
                    imLst.toArray(gItem);
                    args.putString("category", categories[j]);
                    args.putParcelableArrayList("icons", imLst);
                    GridFragment Gfrag = new GridFragment();
                    Gfrag.setArguments(args);
                    gridGridFragments.add(Gfrag);
                    // Log.e("Page Break At", String.valueOf(i));
                    i = 1;
                }
            }
            c.close();

        }

        pm = new PagerAdapter(getSupportFragmentManager(), gridGridFragments);
        mGridPager.setAdapter(pm);

        if (SharedPrefs.prefs == null)
            new SharedPrefs(this);
        SharedPrefs.setCusSoundAdded(false);

    }


    private int dipsToPixels(int dips) {
        final float scale = getResources().getDisplayMetrics().density;
        //Toast.makeText(MainActivity.this, String.valueOf(scale),Toast.LENGTH_SHORT).show();
        return (int) (dips * scale + 0.5f);
    }


    public void startTutorial() {
        if (SharedPrefs.isAppFirstLaunch()) {
            if (mGridPager.getCurrentItem() != 0)
                mGridPager.setCurrentItem(0, true);

            android.support.v4.app.Fragment firstPage = pm.getItem(0);
            ((IFragment) firstPage).pageFirst();
            showTutorial = false;
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
        showAd();
        pingServer();

        if (SharedPrefs.prefs == null)
            SharedPrefs.setContext(this);

        super.onResume();
        if (SharedPrefs.isSignUpComplete() && SharedPrefs.getMyAppID() != null && SharedPrefs.getAppServerID() != null) {
            AppServerConn appServerConn = new AppServerConn(this, ActionType.VerifyUserRegistration);
            appServerConn.execute();
        }

        if (mTutorial != null && timerLaunch && SharedPrefs.isAppFirstLaunch()) {

            mTutorial.end();


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
                mMessageReceiver, new IntentFilter(String.valueOf(ClassType.MainActivity)));


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
        previewSound.release();


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

    public double totalPages(int itemCount) {
        return itemCount / itemsOnPage;
    }

    public int addSoundLocation(int ItemCount) {
        double minPages = totalPages(ItemCount);
        int minItems = (int) minPages * itemsOnPage;
        return (ItemCount - minItems);
    }

    public void setTutorial(Tutorial mTutorial) {
        this.mTutorial = mTutorial;

        mTutorial.moveToNext(new ViewTarget(timer), "Set playback time", "Tap on the timer icon to play the sound after a specified interval");

        Animation grow = AnimationUtils.loadAnimation(MainActivity.this, R.anim.grow_bounce);
        grow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation shrink = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shrink);
                timer.startAnimation(shrink);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        grow.setStartOffset(500);
        timer.startAnimation(grow);
    }

    public void animPrankButton() {
        Animation grow = AnimationUtils.loadAnimation(MainActivity.this, R.anim.grow_prank_btn);
        grow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation shrink = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shrink_prank_btn);
                prankbtn.startAnimation(shrink);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        grow.setStartOffset(500);
        prankbtn.startAnimation(grow);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("9EA2162DEEE83254D6CB1770786B77EE")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void showAd(){
        if (mInterstitialAd.isLoaded() && SharedPrefs.getPrankCount() >= 3) {
            mInterstitialAd.show();
        }

    }
    public void pingServer(){
        if (SharedPrefs.getPingServerDate() != null) {
            try {
                // Convert the expDate in shared prefs to CALENDAR type for comparison
                Calendar pingServerDate = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                pingServerDate.setTime(sdf.parse(SharedPrefs.getPingServerDate()));

                // Get current Time from device for comparison
                Calendar today = Calendar.getInstance(TimeZone.getDefault());
                if (pingServerDate.before(today)) {

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}


