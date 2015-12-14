package com.fournodes.ud.pranky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

import static com.fournodes.ud.pranky.PreviewMediaPlayer.getInstance;

public class PlaySound extends BroadcastReceiver {
    private PreviewMediaPlayer playSound = getInstance();
    int counter = 1;
    int sysSound;
    String cusSound;
    int repeatCount;
    int volume;

    public PlaySound() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sysSound = intent.getIntExtra("sysSound", -1);
        //     Log.e("System Sound",sound);
        cusSound = intent.getStringExtra("cusSound");
        Log.e("Custom Sound", String.valueOf(cusSound));
        repeatCount = intent.getIntExtra("repeatCount", 1);
        Log.e("Repeat Count", String.valueOf(repeatCount));
        volume = intent.getIntExtra("volume", 1);
        Log.e("Sound Volume", String.valueOf(volume));
        try {
            if (playSound.mp.isPlaying()) {
                playSound.mp.stop();
                playSound.mp.release();
                playSound.mp = null;

            }
        } catch (Exception e) {
            Log.e("Preview MediaPlayer", e.toString());
        }
            if (sysSound != -1)
                playSound.mp = MediaPlayer.create(context, sysSound);
            else {
                playSound.mp = new MediaPlayer();
                try {
                    playSound.mp.setDataSource(cusSound);
                    playSound.mp.setVolume(0, sysSound);
                    playSound.mp.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            final int currVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        playSound.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        playSound.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    playSound.mp.start();
                }
            });
        playSound.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (counter == repeatCount) {
                        mp.release();
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVol, 0);
                    } else
                        playSound.mp.start();
                    counter++;
                }
            });


    }
}
