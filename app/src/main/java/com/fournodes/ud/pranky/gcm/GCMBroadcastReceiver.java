package com.fournodes.ud.pranky.gcm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.ItemSelected;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.activities.SplashActivity;
import com.fournodes.ud.pranky.enums.ActionType;
import com.fournodes.ud.pranky.enums.ClassType;
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.fournodes.ud.pranky.receivers.PlayPrank;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Usman on 11/23/2015.
 */
public class GCMBroadcastReceiver extends GcmListenerService {
    private static final String TAG = "GCMBroadcastReceiver";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    private AppServerConn appServerConn;



    @Override
    public void onMessageReceived(String from, Bundle data) { // Prank received will trigger this
        if (SharedPrefs.prefs == null) {
            SharedPrefs SP = new SharedPrefs(getApplicationContext());
            SP.initAllPrefs();
        }
        Log.e("Message received",data.getString("message"));
        Log.e("Sender Name",data.getString("sender_name"));

        switch(ActionType.valueOf(data.getString("message"))) {
            case PlayPrank:

                if (SharedPrefs.isPrankable()) {
                    String sound = data.getString("item");

                    String soundRep = data.getString("repeat_count");
                    String soundVol = data.getString("volume");

                    Intent intent = new Intent(getApplicationContext(), PlayPrank.class);

                    if (sound.equals("raw.flash")
                            || sound.equals("raw.flash_blink")
                            || sound.equals("raw.vibrate_hw")
                            || sound.equals("raw.message")
                            || sound.equals("raw.ringtone")) {

                        intent.putExtra("sysSound", -1);
                        intent.putExtra("cusSound", sound);

                    }
                    else
                    {
                        intent.putExtra("sysSound", ItemSelected.getSoundRes(sound));
                        intent.putExtra("cusSound", "");
                    }


                    intent.putExtra("repeatCount", Integer.valueOf(soundRep));
                    intent.putExtra("volume", Integer.valueOf(soundVol));


                    final int _id = (int) System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id,
                            intent, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);

                    SharedPrefs.setFrndAppID(data.getString("sender_id")); // Temporarily save the senders ID as frndsAppID
                    appServerConn = new AppServerConn(ActionType.NotifyPrankSuccessful);
                    appServerConn.execute();
                    sendNotification(data.getString("sender_name"));


                } else {
                    SharedPrefs.setServerState(0); // Set serverState to 0
                    // Send request to app server to remove myAppID from database untill serverState becomes 1
                    appServerConn = new AppServerConn(ActionType.UpdateAvailability);
                    appServerConn.execute();
                     // Params (myGcmID, myAppId, serverState(fom stored prefs))
                    // Once myAppID has been removed from the db on the server, generate a response for the sender
                    SharedPrefs.setFrndAppID(data.getString("sender_id")); // Temporarily save the senders ID as frndsAppID
                    appServerConn = new AppServerConn(ActionType.NotifyPrankFailed);
                    appServerConn.execute();

                }
                break;

            case NotifyPrankFailed:
                // Broadcast the response to Main activity to display a toast
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
                        new Intent(String.valueOf(ClassType.MainActivity))
                                .putExtra(String.valueOf(ActionType.Broadcast),
                                        String.valueOf(Message.PrankFailed)));

                break;
            case NotifyPrankSuccessful:
                // Broadcast the response to Main activity to display a toast
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
                        new Intent(String.valueOf(ClassType.MainActivity))
                                .putExtra(String.valueOf(ActionType.Broadcast),
                                        String.valueOf(Message.PrankSuccessful)));

                break;
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
     * @param name GCM message received.
     */
    private void sendNotification(String name) {
        String message;
        if  (!name.equals("null")) {
             message = "You have been pranked by " + name;
        }else {
            message = "You have been pranked";
        }
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_16)
                .setLargeIcon(((BitmapDrawable)getApplicationContext().getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap())  .setColor(Color.parseColor("#fff2c305"))
                .setContentTitle("Pranked!")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int)System.currentTimeMillis(), notificationBuilder.build());
    }
}
