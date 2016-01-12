package com.fournodes.ud.pranky.gcm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.fournodes.ud.pranky.enums.ActionType;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.fournodes.ud.pranky.receivers.PlayPrank;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.Selection;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.activities.MainActivity;
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
    private String message;



    @Override
    public void onMessageReceived(String from, Bundle data) { // Prank received will trigger this
        if (SharedPrefs.prefs == null) {
            SharedPrefs SP = new SharedPrefs(getApplicationContext());
            SP.initAllPrefs();
        }

        message = data.getString("message");
      //  SharedPrefs.setFrndAppID(data.getString("sender_id"));

        switch(ActionType.valueOf(message)) {
            case PRANK:

                if (SharedPrefs.isPrankable()) {
                    String sound = data.getString("item");

                    String soundRep = data.getString("repeat_count");
                    String soundVol = data.getString("volume");

                    Intent intent = new Intent(getApplicationContext(), PlayPrank.class);
                    if (sound.equals("raw.flash") || sound.equals("raw.flash_blink") || sound.equals("raw.vibrate_hw") || sound.equals("raw.message")|| sound.equals("raw.ringtone")){
                        intent.putExtra("sysSound", -1);
                        intent.putExtra("cusSound", sound.toString());

                    }
                    else
                    {
                        intent.putExtra("sysSound", Selection.getSoundRes(sound));
                        intent.putExtra("cusSound", "");
                    }


                    intent.putExtra("repeatCount", Integer.valueOf(soundRep));
                    intent.putExtra("volume", Integer.valueOf(soundVol));


                    final int _id = (int) System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id,
                            intent, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);

                    SharedPrefs.setFrndAppID(data.getString("sender_id")); // Temporarily save the senders ID as frndsAppID
                    appServerConn = new AppServerConn(ActionType.PRANK_SUCCESSFUL);
                    appServerConn.execute();


                } else {
                    SharedPrefs.setServerState(0); // Set serverState to 0
                    // Send request to app server to remove myAppID from database untill serverState becomes 1
                    appServerConn = new AppServerConn(ActionType.UPDATE_STATE);
                    appServerConn.execute();
                     // Params (myGcmID, myAppId, serverState(fom stored prefs))
                    // Once myAppID has been removed from the db on the server, generate a response for the sender
                    SharedPrefs.setFrndAppID(data.getString("sender_id")); // Temporarily save the senders ID as frndsAppID
                    appServerConn = new AppServerConn(ActionType.PRANK_FAILED);
                    appServerConn.execute();

                }
                break;

            case PRANK_FAILED:
                // Broadcast the response to Main activity to display a toast
                Intent intent = new Intent("main-activity-broadcast");
                intent.putExtra("message", "prank-response-received");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                break;
            case PRANK_SUCCESSFUL:
                // Broadcast the response to Main activity to display a toast
                Intent succ = new Intent("main-activity-broadcast");
                succ.putExtra("message", "prank-successful");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(succ);

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
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.facelogo_1)
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
