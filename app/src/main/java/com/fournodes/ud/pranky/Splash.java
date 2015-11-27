package com.fournodes.ud.pranky;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import static com.fournodes.ud.pranky.BackgroundMusic.getInstance;

public class Splash extends AppCompatActivity {

    // Time for this splash activity
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    // Loop delay for BG Music
    private final int BG_MUSIC_DELAY = 2000;
    // Static class to play BG Music
    private BackgroundMusic player = getInstance();
    // Intent to launch the next activity
    private Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Cache font in Memory
        FontManager.createTypeFace(Splash.this, "grinched-regular");

        // Initialize SharedPrefs will default values
        new SharedPrefs(Splash.this).initAllPrefs();

        // Check if BG Music is enabled in shared prefs
        if (SharedPrefs.isBgMusicEnabled()) {
            player.mp = MediaPlayer.create(getApplicationContext(), R.raw.app_bg);
            player.mp.setVolume(0, (float) 0.2);
            player.mp.start();

            // Loop the music after a short delay once it has finished
            player.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                player.mp.start();
                            } catch (IllegalStateException e) {
                            }
                        }
                    },BG_MUSIC_DELAY);
                }
            });
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
                Animation rotateAnim = AnimationUtils.loadAnimation(getApplication(), R.anim.logo_rotate);
                logo_face.startAnimation(rotateAnim);
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
                // Check if app is launched for the first time
                if (SharedPrefs.isAppFirstLaunch())
                // Launch tutorial activity if true
                    mainIntent = new Intent(Splash.this, TutorialActivity.class);
                else
                // Launch main activity if false
                    mainIntent = new Intent(Splash.this, Main.class);

                Splash.this.startActivity(mainIntent);
                Splash.this.finish();

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


}
