package com.fournodes.ud.pranky;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.ArrayList;

public class PlaySound extends BroadcastReceiver {
    MediaPlayer player = null;
    public PlaySound() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


             try {
            String sound = intent.getStringExtra("Sound");
            String soundCus = intent.getStringExtra("SoundCus");
            String soundRepeat = intent.getStringExtra("SoundRepeat");
                 String soundVol = intent.getStringExtra("SoundVol");

            if (sound != null)
                player = MediaPlayer.create(context, R.raw.class.getField(sound).getInt(null));
            else{
                player = new MediaPlayer();
            try {
                player.setDataSource(soundCus);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

            final int currVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    player.start();
                }
            });            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVol, 0);
                }
            });

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


    }
}
