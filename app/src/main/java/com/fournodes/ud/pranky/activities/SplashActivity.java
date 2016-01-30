package com.fournodes.ud.pranky.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.fournodes.ud.pranky.BackgroundMusic;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.utils.FontManager;

public class SplashActivity extends AppCompatActivity {

    // Time for this splash activity
    private final int SPLASH_DISPLAY_LENGTH = 3500;

    // Intent to launch the next activity
    private Intent mainIntent;

    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_splash,
                null);
        setContentView(rootView);


        // Initialize SharedPrefs will default values
        new SharedPrefs(SplashActivity.this).initAllPrefs();


        // Cache font in Memory
        //FontManager.createTypeFace(SplashActivity.this, "grinched-regular");
        FontManager.createTypeFace(SplashActivity.this, SharedPrefs.DEFAULT_FONT);


        // Check if BG Music is enabled in shared prefs
        if (SharedPrefs.isBgMusicEnabled()) {

            // Static class to play BG Music
            BackgroundMusic.setContext(getApplicationContext());
            BackgroundMusic.play();
        }

        // Logo text "PRANKY"
        ImageView logo = (ImageView) findViewById(R.id.logo);
        // Apply the bounch animation
        Animation bounceAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_bounce);
        logo.startAnimation(bounceAnim);

        // Logo icon "Joker Face"
        final ImageView logo_face = (ImageView) findViewById(R.id.logo_face);
        // Apply the first Drop animation
        final Animation dropAnim = AnimationUtils.loadAnimation(getApplication(), R.anim.logo_drop);
        logo_face.startAnimation(dropAnim);

        // Listen for animation complete
        dropAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Apply Rotate Animation

                AnimationDrawable boxsel = (AnimationDrawable) logo_face.getDrawable();
                boxsel.start();
              //  Animation rotateAnim = AnimationUtils.loadAnimation(getApplication(), R.anim.logo_rotate);
               // logo_face.startAnimation(rotateAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        /* New Handler to start the required activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (SharedPrefs.isSignUpComplete()||SharedPrefs.isSignUpSkipped()){
                    mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                }else{
                    mainIntent = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                }
                startActivity(mainIntent);
                finish();

            }
        }, SPLASH_DISPLAY_LENGTH);
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
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(rootView);
        rootView = null;
        System.gc();

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



}
