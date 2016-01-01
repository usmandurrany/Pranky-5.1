package com.fournodes.ud.pranky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Home on 12/14/2015.
 */
public class NetworkConnectivityCheck extends BroadcastReceiver {

    Context context;
    CustomToast cToast;
    RegisterOnServer rs;

    @Override
    public void onReceive(Context context, Intent intent) {

       /* this.context = context;

        ConnectivityManager conn =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (SharedPrefs.prefs == null) {
            SharedPrefs SP = new SharedPrefs(context);
            SP.initAllPrefs();
        }

        // Checks the user prefs and the network connection. Based on the result, decides whether
        // to refresh the display or keep the current display.
        // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
        if ( networkInfo != null && networkInfo.isConnected() ) {



            //SharedPrefs.setPrankBtnEnabled(true);

            SharedPrefs.setNetworkAvailable(true);



            if (SharedPrefs.isPrankable()) {

                try {
                    // Convert the expDate in shared prefs to CALENDAR type for comparision
                    Calendar exp = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                    exp.setTime(sdf.parse(SharedPrefs.getExpDate()));

                    // Get current Time from device for comparison
                    Calendar today= Calendar.getInstance(TimeZone.getDefault());


                    // Perform Checks for true
                    //serverState is 1 and myAppID has expired but myGcmID is set
                    if(SharedPrefs.getServerState() == 1 && exp.before(today) && SharedPrefs.getMyGcmID()!=null){
                        // Request new appID from server

                        // Initialize the RegisterOnServer class
                        rs = new RegisterOnServer(context);
                        SharedPrefs.setMyAppID(""); // First clear the myAppID on device
                        // Send myGcmID but empty myAppID
                        String[] args = {SharedPrefs.getMyGcmID(),SharedPrefs.getMyAppID()};
                        rs.execute(args);
                    }
                    // serverState is 1 and myGcmId is not set
                    else if (SharedPrefs.getServerState() == 1 && SharedPrefs.getMyGcmID() == null && SharedPrefs.getMyGcmID() == null)
                    {
                        if(checkPlayServices()) {
                            Intent register = new Intent(context, RegistrationIntentService.class);
                            context.startService(register);
                        }


                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }



            // Otherwise, the app can't download content--either because there is no network
            // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
            // is no Wi-Fi connection.
            // Sets refreshDisplay to false.
        } else {
           // SharedPrefs.setPrankBtnEnabled(false);
            SharedPrefs.setNetworkAvailable(false);


        }

        Intent prankBtnEnable = new Intent();
        prankBtnEnable.setAction("main-activity-broadcast");
        prankBtnEnable.putExtra("message", "network-changed");
        LocalBroadcastManager.getInstance(context).sendBroadcast(prankBtnEnable);*/

    }

}
