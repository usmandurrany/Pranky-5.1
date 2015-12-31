package com.fournodes.ud.pranky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;

import static com.fournodes.ud.pranky.PreviewMediaPlayer.getInstance;

public class PlaySound extends BroadcastReceiver {
    int counter = 1;
    int sysSound;
    String cusSound;
    int repeatCount;
    int volume;
    Camera cam;
    Camera.Parameters params;
    boolean isLighOn;
    private PreviewMediaPlayer playSound = getInstance();
    private boolean bgMusicPlay;

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

        if (("raw.vibrate_hw").equals(cusSound)) {
            Log.w("Vibrate", "SUCCESS");
            long[] pattern = {0, 2000, 1000, 2000, 1000, 2000};
            ((Vibrator) context.getSystemService(context.VIBRATOR_SERVICE)).vibrate(pattern, -1);

        } else if (("raw.message").equals(cusSound)) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (("raw.ringtone").equals(cusSound)) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (("raw.flash").equals(cusSound)) {
            Log.w("Flash", "SUCCESS");
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        cam.stopPreview();
                        cam.release();

                    }
                }, repeatCount * 1000);

            }

        } else if (("raw.flash_blink").equals(cusSound)) {
            Log.w("Blink", "SUCCESS");
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {


                Runnable flashBlinkRunnable = new Runnable() {
                    @Override
                    public void run() {
                        cam = Camera.open();
                        params = cam.getParameters();
                        try {
                            cam.setPreviewTexture(new SurfaceTexture(0));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cam.startPreview();

                        for (int i = 0; i < 10; i++) {
                            try {
                                flipFlash();
                                Thread.sleep(100);
                                flipFlash();
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        cam.stopPreview();
                        cam.release();
                        //handler.post(flashBlinkRunnable);

                    }
                };

                new Thread(flashBlinkRunnable).start();

            }
        } else {
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
                    try{
                        if (BackgroundMusic.mp.isPlaying()){
                            BackgroundMusic.pause();
                            bgMusicPlay=true;
                        }
                    }catch (Exception e){
                        Log.e("Play Sound",e.toString());
                    }
                    playSound.mp.start();
                }
            });
            playSound.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (counter == repeatCount) {
                        mp.release();
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVol, 0);
                        try{
                            if (bgMusicPlay == true) {
                                BackgroundMusic.play();
                                bgMusicPlay = false;
                            }
                        }catch (Exception e){
                            Log.e("Play Sound",e.toString());
                        }
                    } else
                        playSound.mp.start();
                    counter++;
                }
            });


        }


    }

    private void flipFlash() {
        if (isLighOn) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cam.setParameters(params);
            isLighOn = false;
        } else {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(params);
            isLighOn = true;
        }
    }
}
