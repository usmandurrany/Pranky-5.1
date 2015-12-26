package com.fournodes.ud.pranky;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

public class BackgroundMusic {
    static MediaPlayer mp;
    static Context context;
    private static volatile BackgroundMusic instance = null;
    // Loop delay for BG Music
    private static final int BG_MUSIC_DELAY = 2000;

    private BackgroundMusic() {
    }


    public static BackgroundMusic getInstance(Context context) {

        BackgroundMusic.context = context;

        if (instance == null) {
            synchronized (BackgroundMusic.class) {
                if (instance == null) {
                    instance = new BackgroundMusic();
                }
            }
        }
        return instance;
    }

    public static void setContext(Context context) {
        BackgroundMusic.context = context;
        mp = MediaPlayer.create(context, R.raw.app_bg);
        mp.setVolume(0, (float) 0.1);

        // Loop the music after a short delay once it has finished
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            mp.start();
                        } catch (IllegalStateException e) {
                        }
                    }
                }, BG_MUSIC_DELAY);
            }
        });
    }

    public static void play(){
        mp.start();
    }
    public static void pause(){
        mp.pause();
    }
    public static void stop(){
        mp.stop();
        mp.release();
    }



}
