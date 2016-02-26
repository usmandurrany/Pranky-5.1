package com.fournodes.ud.pranky.mediaplayers;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by Usman on 11/14/2015.
 */
public class PreviewMediaPlayer {
    public MediaPlayer mp;
    public Ringtone ringtone;
    private static volatile PreviewMediaPlayer instance = null;
    private Context context;

    private PreviewMediaPlayer(Context context) {
        this.context = context;
    }

    public static PreviewMediaPlayer getInstance(Context context) {
        if (instance == null) {
            synchronized (PreviewMediaPlayer.class) {
                if (instance == null) {
                    instance = new PreviewMediaPlayer(context);
                }
            }
        }

        return instance;
    }

    public void create(Uri tone) {
        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(context, tone);
            ringtone.play();
        } else {
            if (release())
                create(tone);
        }
    }

    public void create(int itemSound, MediaPlayer.OnCompletionListener onComplete) {
        if (mp == null) {
            mp = MediaPlayer.create(context, itemSound);
            mp.setVolume(100, 100);
            mp.setOnCompletionListener(onComplete);
            mp.start();
        } else {
            if (release())
                create(itemSound, onComplete);
        }
    }

    public void create(String item, MediaPlayer.OnCompletionListener onComplete) {
        if (mp == null) {
            try {
                mp = new MediaPlayer();
                mp.setDataSource(item);
                mp.setVolume(100, 100);
                mp.prepare();
                mp.setOnCompletionListener(onComplete);
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (release())
                create(item, onComplete);
        }
    }


    public void getDurInMills(String item, MediaPlayer.OnPreparedListener onPreparedListener) {
        if (mp == null) {
            try {
                mp = new MediaPlayer();
                mp.setDataSource(item);
                mp.setOnPreparedListener(onPreparedListener);
                mp.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (release())
                getDurInMills(item, onPreparedListener);
        }

    }


    public boolean release() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            return true;
        } else if (ringtone != null) {
            ringtone.stop();
            ringtone = null;
            return true;
        } else
            return false;
    }

}
