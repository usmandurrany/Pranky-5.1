package com.fournodes.ud.pranky.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.util.Log;

import com.fournodes.ud.pranky.BackgroundMusic;
import com.fournodes.ud.pranky.CameraControls;
import com.fournodes.ud.pranky.PreviewMediaPlayer;
import com.fournodes.ud.pranky.SharedPrefs;

import static com.fournodes.ud.pranky.PreviewMediaPlayer.getInstance;

public class PlayPrank extends BroadcastReceiver implements MediaPlayer.OnCompletionListener {

    private int counter = 1;
    private int sysSound;
    private String cusSound;
    private int repeatCount;
    private int volume;
    private PreviewMediaPlayer playSound;
    private boolean bgMusicPlay;
    private CameraControls cameraControls;
    private AudioManager audioManager;
    private int currVol;

    public PlayPrank() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        playSound = getInstance(context);
        cameraControls = CameraControls.getInstance(context);
        Log.e("Prank Count",String.valueOf(SharedPrefs.getPrankCount()));
        sysSound = intent.getIntExtra("sysSound", -1);
        //     Log.e("System Sound",item);
        cusSound = intent.getStringExtra("cusSound");
        Log.e("Custom Sound", String.valueOf(cusSound));
        repeatCount = intent.getIntExtra("repeatCount", 1);
        Log.e("Repeat Count", String.valueOf(repeatCount));
        volume = intent.getIntExtra("volume", 1);
        Log.e("Sound Volume", String.valueOf(volume));

        playSound.release();
        cameraControls.releaseCamera();

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        currVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        if (("raw.vibrate_hw").equals(cusSound)) {
            Log.w("Vibrate", "SUCCESS");
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
                    .vibrate(new long[]{0, 2000, 1000, 2000, 1000, 2000}, -1);

        } else if (("raw.message").equals(cusSound)) {
            playSound.create(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        } else if (("raw.ringtone").equals(cusSound)) {
            playSound.create(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        } else if (("raw.flash").equals(cusSound)) {
            cameraControls.turnFlashOn(10);

        } else if (("raw.flash_blink").equals(cusSound)) {
            cameraControls.blinkFlash(10);

        } else {
            if (sysSound != -1)
                playSound.create(sysSound, this);
            else {
                playSound.create(cusSound, this);

            }

            try {
                if (BackgroundMusic.mp.isPlaying()) {
                    BackgroundMusic.pause();
                    bgMusicPlay = true;
                }
            } catch (Exception e) {
                Log.e("Play Sound", e.toString());
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (counter == repeatCount) {
            playSound.release();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVol, 0);
            try {
                if (bgMusicPlay == true) {
                    BackgroundMusic.play();
                    bgMusicPlay = false;
                }
            } catch (Exception e) {
                Log.e("Play Sound", e.toString());
            }
        } else
            playSound.mp.start();
        counter++;
    }
}
