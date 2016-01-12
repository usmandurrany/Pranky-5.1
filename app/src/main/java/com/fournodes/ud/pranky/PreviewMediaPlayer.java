package com.fournodes.ud.pranky;

import android.media.MediaPlayer;

/**
 * Created by Usman on 11/14/2015.
 */
public class PreviewMediaPlayer {
    public static  MediaPlayer mp;
    private static volatile PreviewMediaPlayer instance = null;

    private PreviewMediaPlayer() {
    }

    public static PreviewMediaPlayer getInstance() {
        if (instance == null) {
            synchronized (PreviewMediaPlayer.class) {
                if (instance == null) {
                    instance = new PreviewMediaPlayer();
                }
            }
        }

        return instance;
    }
}
