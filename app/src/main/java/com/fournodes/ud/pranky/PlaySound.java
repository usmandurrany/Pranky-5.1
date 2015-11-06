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


        //Uri sound  = Uri.parse("android.resource://"+intent.getStringExtra("Sound"));


        MediaPlayer player = null;
        try {
            player = MediaPlayer.create(context, R.raw.class.getField(intent.getStringExtra("Sound")).getInt(null));
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


    }
}
