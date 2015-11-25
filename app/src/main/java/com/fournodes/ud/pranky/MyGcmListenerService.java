package com.fournodes.ud.pranky;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Usman on 11/23/2015.
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        SharedPreferences prefs = getSharedPreferences(SharedPrefs.SHARED_PREF_FILE, 0);
        String prankResp = data.getString("response");
        String sound = data.getString("sound");
        String soundRep = data.getString("soundRep");
        String soundVol = data.getString("soundVol");

        if (prefs.getBoolean(SharedPrefs.PRANKABLE,true) && sound != "0") {




            Intent intent = new Intent(getApplicationContext(), PlaySound.class);

            intent.putExtra("Sound", sound);
            intent.putExtra("SoundCus", "");
            intent.putExtra("SoundRepeat", soundRep);
            intent.putExtra("SoundVol", soundVol);

//        Toast.makeText(getApplicationContext(), String.valueOf(soundRepeat), Toast.LENGTH_SHORT).show();


            final int _id = (int) System.currentTimeMillis();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id,
                    intent, PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);


            // Toast.makeText(getApplicationContext(), "Alarm set", Toast.LENGTH_LONG).show();


            String message = data.getString("message");


            Log.d(TAG, "From: " + from);
            Log.d(TAG, "Message: " + message);

            if (from.startsWith("/topics/")) {
                // message received from some topic.
            } else {
                // normal downstream message.
            }
        }else if (prankResp == "disabled" && sound == "0"){
            Log.e("RESPONSE","YOUR FRIEND IS NOT PRANKABLE AT THE MOMENT");
        }

        else{
            SendPrank notPrankable = new SendPrank(getApplicationContext());
            notPrankable.execute();
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
       // sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.face_logo)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
