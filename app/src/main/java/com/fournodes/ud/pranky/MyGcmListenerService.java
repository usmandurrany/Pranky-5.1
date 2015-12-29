package com.fournodes.ud.pranky;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

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
    public void onMessageReceived(String from, Bundle data) { // Prank received will trigger this
        String type = data.getString("type");
        String senderAppID= data.getString("app_id");

        Log.e("Sender ID",senderAppID);
        Log.e("Type",type);

        if (SharedPrefs.prefs == null) {
            SharedPrefs SP = new SharedPrefs(getApplicationContext());
            SP.initAllPrefs();
        }


        switch(type) {
            case "prank":

                if (SharedPrefs.isPrankable()) {
                    String sound = data.getString("sound");

                    String soundRep = data.getString("soundRep");
                    String soundVol = data.getString("soundVol");

                    Intent intent = new Intent(getApplicationContext(), PlaySound.class);
                    if (sound.equals("raw.flash") || sound.equals("raw.flash_blink") || sound.equals("raw.vibrate_hw") || sound.equals("raw.message")){
                        intent.putExtra("sysSound", -1);
                        intent.putExtra("cusSound", sound.toString());

                    }
                    else
                    {
                        intent.putExtra("sysSound", Sound.getSoundRes(sound));
                        intent.putExtra("cusSound", "");
                    }


                    intent.putExtra("repeatCount", Integer.valueOf(soundRep));
                    intent.putExtra("volume", Integer.valueOf(soundVol));


                    final int _id = (int) System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id,
                            intent, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);

                    SharedPrefs.setFrndAppID(senderAppID); // Temporarily save the senders ID as frndsAppID
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                           HttpGet httpget = new HttpGet(SharedPrefs.APP_SERVER_ADDR + "sendmsg.php?friend_id=" + SharedPrefs.getFrndAppID() + "&app_id=" + SharedPrefs.getMyAppID() + "&sound=" + String.valueOf(0) + "&soundRep=" + String.valueOf(0) + "&soundVol=" + String.valueOf(0) + "&type=success");
                            try{
                                HttpClient httpclient = new DefaultHttpClient();
                                HttpResponse response = httpclient.execute(httpget);

                            }catch (IOException e){}
                        }
                    });
                    thread.start();


                } else {
                    SharedPrefs.setServerState(0); // Set serverState to 0
                    // Send request to app server to remove myAppID from database untill serverState becomes 1
                    RegisterOnServer rs = new RegisterOnServer(getApplicationContext());
                    String[] args = {SharedPrefs.getMyGcmID(), SharedPrefs.getMyAppID()};
                    rs.execute(args); // Params (myGcmID, myAppId, serverState(fom stored prefs))
                    // Once myAppID has been removed from the db on the server, generate a response for the sender
                    SharedPrefs.setFrndAppID(senderAppID); // Temporarily save the senders ID as frndsAppID

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpGet httpget = new HttpGet(SharedPrefs.APP_SERVER_ADDR + "sendmsg.php?friend_id=" + SharedPrefs.getFrndAppID() + "&app_id=" + SharedPrefs.getMyAppID() + "&sound=" + String.valueOf(0) + "&soundRep=" + String.valueOf(0) + "&soundVol=" + String.valueOf(0) + "&type=response");
                            try{
                                HttpClient httpclient = new DefaultHttpClient();
                                HttpResponse response = httpclient.execute(httpget);

                            }catch (IOException e){}
                        }
                    });
                    thread.start();
                }
                break;

            case "response":
                // Broadcast the response to Main activity to display a toast
                Intent intent = new Intent("main-activity-broadcast");
                intent.putExtra("message", "prank-response-received");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                break;
            case "success":
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
        Intent intent = new Intent(this, Main.class);
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
