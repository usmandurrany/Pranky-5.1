package com.fournodes.ud.pranky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class PlaySound extends BroadcastReceiver {
    MediaPlayer player = null;
    int counter = 1;
    String sound;
    String soundCus;
    String soundRepeat;
    String soundVol;

    public PlaySound() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sound = intent.getStringExtra("Sound");
        //     Log.e("System Sound",sound);
        soundCus = intent.getStringExtra("SoundCus");
        Log.e("Custom Sound", soundCus);
        soundRepeat = intent.getStringExtra("SoundRepeat");
        Log.e("Repeat Count", soundRepeat);
        soundVol = intent.getStringExtra("SoundVol");
        Log.e("Sound Volume", soundVol);

        try {

            if (sound != null)
                player = MediaPlayer.create(context, R.raw.class.getField(sound).getInt(null));
            else {
                player = new MediaPlayer();
                try {
                    player.setDataSource(soundCus);
                    player.setVolume(0, Integer.valueOf(soundVol));
                    player.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            final int currVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    player.start();
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (counter == Integer.valueOf(soundRepeat)) {
                        mp.release();
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVol, 0);
                    } else
                        player.start();
                    counter++;
                }
            });

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


    }
}
