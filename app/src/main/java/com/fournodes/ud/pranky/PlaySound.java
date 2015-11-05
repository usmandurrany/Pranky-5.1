package com.fournodes.ud.pranky;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

public class PlaySound extends BroadcastReceiver {
    public PlaySound() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Uri sound  = Uri.parse("android.resource://com.fournodes.ud.pranky/raw/waterdrop");


        MediaPlayer player = MediaPlayer.create(context, sound);
        player.start();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }
}
