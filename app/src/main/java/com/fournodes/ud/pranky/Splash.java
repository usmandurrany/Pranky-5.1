package com.fournodes.ud.pranky;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import static com.fournodes.ud.pranky.AppBGMusic.getInstance;

public class Splash extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private AppBGMusic player =getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences settings = getSharedPreferences("PrankySharedPref", 0);



        if (settings.getBoolean("PlayBGMusic",true)){
            player.mp = MediaPlayer.create(getApplicationContext(), R.raw.app_bg);

            // player.mp.setLooping(true);
            player.mp.setVolume(0, (float) 0.2);
            player.mp.start();
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
                    }, 2000);
                }
            });
        }
        ImageView logo = (ImageView)findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_bounce);
        logo.startAnimation(animation);

        final ImageView logo_face= (ImageView)findViewById(R.id.logo_face);
        final Animation animation2 = AnimationUtils.loadAnimation(getApplication(),R.anim.logo_drop);
        logo_face.startAnimation(animation2);

        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            Animation animation3 = AnimationUtils.loadAnimation(getApplication(),R.anim.logo_rotate);
                logo_face.startAnimation(animation3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

                Intent mainIntent = new Intent(Splash.this, Main.class);
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
