package com.fournodes.ud.pranky;

import android.media.MediaPlayer;

public class AppBGMusic {
    MediaPlayer mp;
    private static volatile AppBGMusic instance = null;

    private AppBGMusic() {
    }

    public static AppBGMusic getInstance() {
        if (instance == null) {
            synchronized (AppBGMusic.class) {
                if (instance == null) {
                    instance = new AppBGMusic();
                }
            }
        }

        return instance;
    }
}
