package com.fournodes.ud.pranky.mediaplayers;

import android.content.Context;
import android.media.MediaPlayer;

import com.fournodes.ud.pranky.R;

public class BackgroundMusic {
    public static MediaPlayer mp;
    static Context context;
    private static volatile BackgroundMusic instance = null;
    // Loop delay for BG Music

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
        mp = MediaPlayer.create(context, R.raw.app_bg_delay);
        mp.setVolume(0, (float) 0.1);
        mp.setLooping(true);

    }

    public static void play() {
        mp.start();
    }

    public static void pause() {
        mp.pause();
    }

    public static void stop() {
        mp.stop();
        mp.release();
    }


}
