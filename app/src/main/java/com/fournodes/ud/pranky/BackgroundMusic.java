package com.fournodes.ud.pranky;

import android.media.MediaPlayer;

public class BackgroundMusic {
    MediaPlayer mp;
    private static volatile BackgroundMusic instance = null;

    private BackgroundMusic() {
    }

    public static BackgroundMusic getInstance() {
        if (instance == null) {
            synchronized (BackgroundMusic.class) {
                if (instance == null) {
                    instance = new BackgroundMusic();
                }
            }
        }

        return instance;
    }
}
