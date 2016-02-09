package com.fournodes.ud.pranky.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fournodes.ud.pranky.BackgroundMusic;
import com.fournodes.ud.pranky.CameraControls;
import com.fournodes.ud.pranky.PreviewMediaPlayer;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.activities.MainActivity;
import com.fournodes.ud.pranky.activities.SplashActivity;

import static com.fournodes.ud.pranky.PreviewMediaPlayer.getInstance;

public class PlayPrank extends BroadcastReceiver implements MediaPlayer.OnCompletionListener {

    private int counter = 1;
    private int sysSound;
    private String cusSound;
    private int repeatCount;
    private int volume;
    private String notify;
    private String sender;
    private PreviewMediaPlayer playSound;
    private boolean bgMusicPlay;
    private CameraControls cameraControls;
    private AudioManager audioManager;
    private int currVol;
    private Context context;

    public PlayPrank() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        playSound = getInstance(context);
        cameraControls = CameraControls.getInstance(context);
        Log.e("Prank Count", String.valueOf(SharedPrefs.getPrankCount()));
        sysSound = intent.getIntExtra("sysSound", -1);
        //     Log.e("System Sound",item);
        cusSound = intent.getStringExtra("cusSound");
        Log.e("Custom Sound", String.valueOf(cusSound));
        repeatCount = intent.getIntExtra("repeatCount", 1);
        Log.e("Repeat Count", String.valueOf(repeatCount));
        volume = intent.getIntExtra("volume", 1);
        Log.e("Sound Volume", String.valueOf(volume));
        notify = intent.getStringExtra("notify");
        sender = intent.getStringExtra("sender");

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
            if (notify != null && notify.equals("true")) {
                new CountDownTimer(5000, 5000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        sendNotification(sender);
                    }
                }.start();
            }
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

    private void sendNotification(String name) {
        String message;
        if (!name.equals("null")) {
            message = "You have been pranked by " + name;
        } else {
            message = "You have been pranked";
        }
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        // Sets up the Snooze and Dismiss action buttons that will appear in the
// big view of the notification.
        Intent dismissIntent = new Intent(context, MainActivity.class);
        PendingIntent piPrankBack = PendingIntent.getActivity(context, 0, dismissIntent, PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo_16)
                .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap()).setColor(Color.parseColor("#fff2c305"))
                .setContentTitle("Pranked!")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .addAction(R.drawable.logo_16, "Prank Back", piPrankBack);


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}
