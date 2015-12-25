package com.fournodes.ud.pranky;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends FragmentActivity implements IFragment {
    private View decorView; // To apply the Immersive mode and remove nav/status bars on lower API Levels
    private View rootView;
    private PagerAdapter mPagerAdapter;

    // x coordinates from TouchEvents
    private float mDown; // ImageView in fragment hijacks the ACTION_DOWN touch even so this x coordinate come from the fragment
    private float mUP; // ACTION_UP touch event x coordinate comes form the ViewPager

    // CustomViewPager that overrides the onTouch event used to detect the x coordinate for swipe direction
    private CustomViewPager mTutPager;

    // Images for the fragments
    int[] images = {R.mipmap.help_screen_1, R.mipmap.help_screen_2, R.mipmap.help_screen_3, R.mipmap.help_screen_4, R.mipmap.help_screen_5, R.mipmap.help_screen_6, R.mipmap.help_screen_7, R.mipmap.help_screen_8, R.mipmap.help_screen_9, R.mipmap.help_screen_10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_tutorial,
                null);
        setContentView(rootView);

        // A list of all the fragments
        List<TutorialFragment> TFList = new ArrayList<TutorialFragment>();

        // Loop to create 10 fragments for 10 images and bundle the args
        for (int i = 0; i < 10; i++) {
            TutorialFragment tutFrag = new TutorialFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Image", images[i]);
            tutFrag.setArguments(bundle);
            TFList.add(tutFrag);
        }

        // Send the list of all the fragemtns to the Pager Adapter
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), TutorialActivity.this, TFList);


        // Instantiate a ViewPager and a PagerAdapter.
        mTutPager = (CustomViewPager) findViewById(R.id.pagerTutorial);
        mTutPager.setAdapter(mPagerAdapter);
        mTutPager.setOnSwipeOutListener(new CustomViewPager.OnSwipeOutListener() {
            @Override
            public void onSwipeOutAtEnd(float x) {
                Log.w("UP", String.valueOf(x));
                mUP = x;
                if (mTutPager.getCurrentItem() == (mPagerAdapter.getCount() - 1)) {
                    // If swiped forward on the last image then finish this activity
                    if (mUP < mDown) {  // Check whether the swipe was forward
                        startActivity(new Intent(TutorialActivity.this, Main.class));
                        SharedPrefs.setAppFirstLaunch(false);
                        TutorialActivity.this.finish();
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (mTutPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mTutPager.setCurrentItem(mTutPager.getCurrentItem() - 1);
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

    // Interface IFRAGMENT methods
    @Override
    public void pageScrolled() {

    }

    @Override
    public void TutImageClick() {

    }

    // ACTION_DOWN touch event x coordinates coming from fragments ImageView via Interface IFRAGMENT
    @Override
    public void TutImageTouch(View view, MotionEvent motionEvent) {
        // Check if on the last fragment
        if (mTutPager.getCurrentItem() == (mPagerAdapter.getCount() - 1)) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.w("DOWN", String.valueOf(motionEvent.getX()));
                    // Store the value in the local var
                    mDown = motionEvent.getX();
                    break;

            }
        }
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

        unbindDrawables(rootView);
        rootView = null;
        System.gc();
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

    }

}
