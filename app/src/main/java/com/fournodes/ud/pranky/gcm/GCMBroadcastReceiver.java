package com.fournodes.ud.pranky.gcm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.enums.Action;
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.models.ContactSelected;
import com.fournodes.ud.pranky.models.ItemSelected;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.fournodes.ud.pranky.receivers.PlayPrank;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Usman on 11/23/2015.
 */
public class GCMBroadcastReceiver extends GcmListenerService {
    private static final String TAG = "GCMBroadcastReceiver";


    @Override
    public void onMessageReceived(String from, Bundle data) { // Prank received will trigger this
        if (SharedPrefs.prefs == null) {
            SharedPrefs SP = new SharedPrefs(getApplicationContext());
            SP.initAllPrefs();
        }
        Log.e("Message received", data.getString("message"));
        Log.e("Sender Name", data.getString("sender_name"));
        ContactSelected.setName(data.getString("sender_name"));

        switch (Action.valueOf(data.getString("message"))) {
            case PlayPrank:

                AppServerConn appServerConn;
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

                    } else {
                        intent.putExtra("sysSound", ItemSelected.getSoundRes(sound));
                        intent.putExtra("cusSound", "");
                    }


                    intent.putExtra("repeatCount", Integer.valueOf(soundRep));
                    intent.putExtra("volume", Integer.valueOf(soundVol));
                    intent.putExtra("notify", data.getString("notify"));
                    intent.putExtra("sender", data.getString("sender_name"));


                    final int _id = (int) System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id,
                            intent, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);

                    SharedPrefs.setFrndAppID(data.getString("sender_id")); // Temporarily save the senders ID as frndsAppID
                    appServerConn = new AppServerConn(Action.NotifyPrankSuccessful);
                    appServerConn.execute();

                } else {
                    SharedPrefs.setServerState(0); // Set serverState to 0
                    // Send request to app server to remove myAppID from database untill serverState becomes 1
                    appServerConn = new AppServerConn(Action.UpdateAvailability);
                    appServerConn.execute();
                    // Params (myGcmID, myAppId, serverState(fom stored prefs))
                    // Once myAppID has been removed from the db on the server, generate a response for the sender
                    SharedPrefs.setFrndAppID(data.getString("sender_id")); // Temporarily save the senders ID as frndsAppID
                    appServerConn = new AppServerConn(Action.NotifyPrankFailed);
                    appServerConn.execute();

                }
                break;

            case NotifyPrankFailed:
                // Broadcast the response to Main activity to display a toast
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
                        new Intent(String.valueOf(Type.MainActivity))
                                .putExtra(String.valueOf(Action.Broadcast),
                                        String.valueOf(Message.PrankFailed)));

                break;
            case NotifyPrankSuccessful:
                // Broadcast the response to Main activity to display a toast
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
                        new Intent(String.valueOf(Type.MainActivity))
                                .putExtra(String.valueOf(Action.Broadcast),
                                        String.valueOf(Message.PrankSuccessful)));

                break;
        }

    }


}
