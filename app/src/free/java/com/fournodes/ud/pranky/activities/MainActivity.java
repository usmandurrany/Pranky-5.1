package com.fournodes.ud.pranky.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.Tutorial;
import com.fournodes.ud.pranky.adapters.PagerAdapter;
import com.fournodes.ud.pranky.custom.CustomToast;
import com.fournodes.ud.pranky.dialogs.InfoDialog;
import com.fournodes.ud.pranky.dialogs.PrePrankDialog;
import com.fournodes.ud.pranky.enums.Action;
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.fragments.GridFragment;
import com.fournodes.ud.pranky.gcm.GCMInitiate;
import com.fournodes.ud.pranky.interfaces.IFragment;
import com.fournodes.ud.pranky.interfaces.Messenger;
import com.fournodes.ud.pranky.mediaplayers.BackgroundMusic;
import com.fournodes.ud.pranky.mediaplayers.PreviewMediaPlayer;
import com.fournodes.ud.pranky.models.ContactSelected;
import com.fournodes.ud.pranky.models.GridItem;
import com.fournodes.ud.pranky.models.ItemCategory;
import com.fournodes.ud.pranky.models.ItemSelected;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.fournodes.ud.pranky.utils.Analytics;
import com.fournodes.ud.pranky.utils.Cleaner;
import com.fournodes.ud.pranky.utils.DatabaseHelper;
import com.fournodes.ud.pranky.utils.FontManager;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends FragmentActivity implements Messenger {

    private View rootView;
    private ViewPager mGridPager;
    private PagerAdapter pm;
    private ImageView timer;
    private ImageView prankbtn;
    private CustomToast cToast;
    private ObjectAnimator anim;
    private RelativeLayout sideMenu;
    private int itemsOnPage = 9;
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
    private TextSwitcher prankCount;
    private LinearLayout smButtonGroup;
    private LinearLayout smPrankLeft;
    private Tracker mTracker;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            switch (Message.valueOf(intent.getStringExtra(String.valueOf(Action.Broadcast)))) {
                case PrankSent:
                    Log.e("Prank Sent", "Successfully");
                    break;

                case SoundAdded:
                    createFragments();
                    mGridPager.setCurrentItem(pm.getCount() - 1);
                    break;
                case PrankFailed: {
                    CustomToast cToast = new CustomToast(getApplicationContext(), getString(R.string.toast_friend_unavailable));
                    cToast.show();
                    break;
                }
                case PrankSuccessful: {
                    CustomToast cToast = new CustomToast(getApplicationContext(), getString(R.string.toast_prank_success));
                    cToast.show();
                    break;
                }

                case NetworkError: {
                    CustomToast cToast = new CustomToast(getApplicationContext(), getString(R.string.toast_network_unavailable));
                    cToast.show();
                    break;
                }
                case UserUnregistered: {
                    CustomToast cToast = new CustomToast(getApplicationContext(), getString(R.string.notification_logged_out));
                    cToast.show();
                    break;
                }
                case UserRegistered: {

                    break;
                }
                case TokenGenerated: {
                    AppServerConn appServerConn = new AppServerConn(Action.RegisterDevice);
                    appServerConn.execute();
                    break;
                }
                case ShowPranksLeft: {
                    showPranksLeft();
                    break;
                }
            }
        }
    };
    private BroadcastReceiver interActivityMessenger = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            switch (Message.valueOf(intent.getStringExtra(String.valueOf(Action.Broadcast)))) {
                case ShowPranksLeft: {
                    showPranksLeft();
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
        /*********************************** SavedInstance Checks***********************************/

        if (SharedPrefs.prefs == null) {
            new SharedPrefs(this).initAllPrefs();

        }
        if (FontManager.getTypeFace(this, SharedPrefs.DEFAULT_FONT) == null) {
            FontManager.createTypeFace(this, SharedPrefs.DEFAULT_FONT);

        }

        /*************************************** App Config **************************************/

        // Obtain the shared Tracker instance.
        Analytics application = (Analytics) getApplication();
        mTracker = application.getDefaultTracker();

        prankCount = (TextSwitcher) findViewById(R.id.smPrankCountText);
        prankCount.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView smPrankCountNew = new TextView(MainActivity.this);
                smPrankCountNew.setTextColor(Color.WHITE);
                smPrankCountNew.setGravity(Gravity.CENTER);
                smPrankCountNew.setTextSize(18);
                smPrankCountNew.setTypeface(Typeface.DEFAULT_BOLD);
                return smPrankCountNew;
            }
        });
        prankCount.setInAnimation(MainActivity.this, R.anim.text_flip_in);
        prankCount.setOutAnimation(MainActivity.this, R.anim.text_flip_out);
        prankCount.setCurrentText(String.valueOf(SharedPrefs.getPranksLeft()));


        /*********************************** Initializations ***************************************/

        previewSound = PreviewMediaPlayer.getInstance(this);
        me.relex.circleindicator.CircleIndicator mIndicator = (me.relex.circleindicator.CircleIndicator) findViewById(R.id.pagerIndicator);
        mGridPager = (ViewPager) findViewById(R.id.pager);
        prankbtn = (ImageView) findViewById(R.id.prankit);
        timer = (ImageView) findViewById(R.id.timer_btn);
        sideMenu = (RelativeLayout) findViewById(R.id.sideMenu);
        ImageView smInfo = (ImageView) findViewById(R.id.smInfo);
        ImageView smHelp = (ImageView) findViewById(R.id.smHelp);
        ImageView smTerms = (ImageView) findViewById(R.id.smTerms);
        ImageView clock = (ImageView) findViewById(R.id.clock_btn);
        ImageView settings = (ImageView) findViewById(R.id.settings);
        smButtonGroup = (LinearLayout) findViewById(R.id.smButtonGroup);
        smPrankLeft = (LinearLayout) findViewById(R.id.smPrankLeft);
        ImageView smBlankButton = (ImageView) findViewById(R.id.smBlankButton);


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
                Log.i(ItemCategory.getCategory(MainActivity.this, (position)), "Setting screen name");
                mTracker.setScreenName("Screen-" + ItemCategory.getCategory(MainActivity.this, (position)));
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());

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
                timerLaunch = false;
                if (ItemSelected.itemSound == -1 && ItemSelected.itemCustomSound == null) {
                    cToast = new CustomToast(getApplicationContext(), getString(R.string.toast_select_sound));
                    cToast.show();
                    if (currPage != null)
                        ((IFragment) currPage).shakeIcons();
                } else
                    startActivity(new Intent(MainActivity.this, ClockDialogActivity.class));

            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ItemSelected.itemSound == -1 && ItemSelected.itemCustomSound == null) {
                    cToast = new CustomToast(getApplicationContext(), getString(R.string.toast_select_sound));
                    cToast.show();
                    if (currPage != null)
                        ((IFragment) currPage).shakeIcons();
                } else
                    startActivity(new Intent(MainActivity.this, TimerDialogActivity.class));

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
                    cToast = new CustomToast(MainActivity.this, getString(R.string.toast_friend_id_timeout));
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
                    cToast = new CustomToast(MainActivity.this, getString(R.string.toast_select_sound));
                    cToast.show();
                    if (currPage != null)
                        ((IFragment) currPage).shakeIcons();
                } else if (ItemSelected.itemSound == -1 && ItemSelected.itemCustomSound != null) {

                    cToast = new CustomToast(MainActivity.this, getString(R.string.toast_select_non_custom_sound));
                    cToast.show();
                } else if (SharedPrefs.getFrndAppID() == null) {
                    if (SharedPrefs.getInvalidIDCount() < 3) {
                        SharedPrefs.setBgMusicPlaying(true);
                        Intent prankDialog = new Intent(MainActivity.this, PrankDialogActivity.class);
                        startActivityForResult(prankDialog, 1);
                    } else {
                        cToast = new CustomToast(MainActivity.this, getString(R.string.toast_friend_id_timeout));
                        cToast.show();
                    }
                } else if (SharedPrefs.getPranksLeft() <= 0) {
                    startActivity(new Intent(MainActivity.this, GetPremiumDialogActivity.class));
                } else {
                    PrePrankDialog prePrankDiag = new PrePrankDialog(MainActivity.this);
                    prePrankDiag.delegate = MainActivity.this;
                    if (ContactSelected.getName() != null)
                        prePrankDiag.setFriendName(ContactSelected.getName());
                    else
                        prePrankDiag.setFriendName(ContactSelected.getApp_id());

                    prePrankDiag.show();
                }

            }

        });

        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!open) {
                    /*************************** Menu Opened **************************************/
                    sideMenu.setBackgroundResource(R.drawable.sm_hide);
                    anim = ObjectAnimator.ofFloat(sideMenu, "translationX", 0, dipsToPixels(146));
                    open = true;

                    smClose = new Handler();
                    smClose.postDelayed(new Runnable() {
                        public void run() {
                            if (open) {
                                anim = ObjectAnimator.ofFloat(sideMenu, "translationX", dipsToPixels(146), 0);
                                open = false;
                                sideMenu.setBackgroundResource(R.drawable.sm_show);
                                anim.setDuration(500);
                                anim.start();
                                smClose.removeCallbacks(this);
                            }
                        }
                    }, 3000);


                } else {
                    /*************************** Menu Closed **************************************/

                    anim = ObjectAnimator.ofFloat(sideMenu, "translationX", dipsToPixels(146), 0);
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

        smBlankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GetPremiumDialogActivity.class));
            }
        });

        startTutorial();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        animate(prankbtn, 500, 0);
        //animPrankButton();
    }

    public void createFragments() {
        SQLiteDatabase db = prankyDB.getReadableDatabase();
        String[] categories = ItemCategory.getCategories(MainActivity.this);
        int id = 0;
        int i;
        boolean lastItemAdded = false;

        /* List of all fragments */
        List<GridFragment> pageList = new ArrayList<>();


        for (int j = 0; j < categories.length; j++) {

            /* Query the database, Select * WHERE category = categories[i] */
            Cursor c = db.query(DatabaseHelper.TABLE_ITEMS,
                    null, DatabaseHelper.COLUMN_ITEM_CATEGORY + " = ?",
                    new String[]{String.valueOf(j + 1)},
                    null, null, null);


            int count = c.getCount();

            /* List of individual item in the grid */
            ArrayList<GridItem> itemsList = new ArrayList<>();


            /* Move cursor to 0 (initially at -1) */
            i = 1;
            while (c.moveToNext() || (categories[j].equals("custom") && !lastItemAdded)) {
                if (!c.isAfterLast() && i <= itemsOnPage) {
                    id = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_ID));
                    Integer image = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_ITEM_IMG_LOC));
                    String sound = c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_ITEM_SOUND_LOC));
                    Integer soundRepeat = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_REPEAT_COUNT));
                    Integer soundVol = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_SOUND_VOL));

                    /* Create a new object for the current item the add to the list */
                    GridItem items = new GridItem(id, image, sound, soundRepeat, soundVol);
                    itemsList.add(items);
                    i++;

                } else if (((c.getPosition() == count) || c.getPosition() == count - 1 || count == 0)
                        && i <= itemsOnPage && categories[j].equals("custom")) {

                    GridItem lstItem = new GridItem(id, R.mipmap.addmore, "addSound");
                    itemsList.add(lstItem);
                    lastItemAdded = true;
                    //int itemCount = c.getCount();
                    addSoundLoc = addSoundLocation(count);

                }

                /* Create new fragment, bundle objects list and pass, add fragment to fragment list */
                if (i > itemsOnPage || (c.getPosition() == count || (c.getPosition() == count - 1
                        && !categories[j].equals("custom")) || count == 0)) {

                    Bundle args = new Bundle();
                   /* GridItem[] itemsArray = new GridItem[itemsList.size()];
                    itemsList.toArray(itemsArray);*/
                    args.putString("category", categories[j]);
                    args.putParcelableArrayList("icons", itemsList);
                    GridFragment page = new GridFragment();
                    page.setArguments(args);
                    pageList.add(page);
                    // Log.e("Page Break At", String.valueOf(i));
                    i = 1;
                }
            }
            c.close();

        }

        pm = new PagerAdapter(getSupportFragmentManager(), pageList);
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
        View decorView = getWindow().getDecorView();

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
        Log.e("Main Activity", "Resumed");

        pingServer();


        if (SharedPrefs.prefs == null) {
            SharedPrefs.setContext(this);
        }

        if (SharedPrefs.isSignUpComplete() && SharedPrefs.getMyAppID() != null && SharedPrefs.getAppServerID() != null) {
            AppServerConn appServerConn = new AppServerConn(this, Action.VerifyUserRegistration);
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
                mMessageReceiver, new IntentFilter(String.valueOf(Type.MainActivity)));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                interActivityMessenger, new IntentFilter(String.valueOf(Type.MainActivity)));


    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Main Activity", "Paused");
        try {
            if (BackgroundMusic.mp != null && !SharedPrefs.isBgMusicPlaying()) {
                BackgroundMusic.pause();
            }
        } catch (Exception e) {
            Log.e("BG Music Pause", e.toString());
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        previewSound.release();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                interActivityMessenger, new IntentFilter(String.valueOf(Type.InterActivityBroadcast)));


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

        mTutorial.moveToNext(new ViewTarget(timer), getString(R.string.tut_set_playback_time_title), getString(R.string.tut_set_playback_time_desc));

        Animation grow = AnimationUtils.loadAnimation(MainActivity.this, R.anim.grow_to_1_3);
        grow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation shrink = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shring_form_1_3);
                timer.startAnimation(shrink);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        grow.setStartOffset(500);
        timer.startAnimation(grow);
    }

    public void animate(final View view, final int duration, final int repeatCount) {
        Animation grow = AnimationUtils.loadAnimation(MainActivity.this, R.anim.grow_to_1_1);
        grow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation shrink = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shrink_form_1_1);
                view.startAnimation(shrink);
                if (repeatCount > 0) {
                    shrink.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            animate(view, duration, repeatCount - 1);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        grow.setDuration(duration);
        view.startAnimation(grow);
    }

    public void pingServer() {
        if (SharedPrefs.getPingServerDate() != null) {
            try {
                // Convert the expDate in shared prefs to CALENDAR type for comparison
                Calendar pingServerDate = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                pingServerDate.setTime(sdf.parse(SharedPrefs.getPingServerDate()));

                // Get current Time from device for comparison
                Calendar today = Calendar.getInstance(TimeZone.getDefault());
                if (pingServerDate.before(today)) {
                    new AppServerConn(MainActivity.this, Action.PingServer).execute();
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void showPranksLeft() {

        sideMenu.setBackgroundResource(R.drawable.sm_hide);
        smButtonGroup.setVisibility(View.GONE);
        smPrankLeft.setVisibility(View.VISIBLE);
        anim = ObjectAnimator.ofFloat(sideMenu, "translationX", 0, dipsToPixels(146));
        anim.setDuration(500).start();
        open = true;
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Handler delayedSetText = new Handler();
                delayedSetText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (prankCount != null)
                            prankCount.setText(String.valueOf(SharedPrefs.getPranksLeft()));
                    }
                }, 1000);

                smClose = new Handler();
                smClose.postDelayed(new Runnable() {
                    public void run() {
                        if (open) {
                            anim = ObjectAnimator.ofFloat(sideMenu, "translationX", dipsToPixels(146), 0);
                            open = false;
                            sideMenu.setBackgroundResource(R.drawable.sm_show);
                            anim.setDuration(500);
                            anim.start();
                            smClose.removeCallbacks(this);
                            anim.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    smPrankLeft.setVisibility(View.GONE);
                                    smButtonGroup.setVisibility(View.VISIBLE);
                                    // animate(prankCount,500,5);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {
                                }
                            });

                        }
                    }
                }, 3000);


            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });


    }

}


