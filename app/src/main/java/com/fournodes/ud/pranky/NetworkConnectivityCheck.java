package com.fournodes.ud.pranky;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Home on 12/14/2015.
 */
public class NetworkConnectivityCheck extends BroadcastReceiver {

    Context context;
    CustomToast cToast;
    RegisterOnServer rs;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        ConnectivityManager conn =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        // Checks the user prefs and the network connection. Based on the result, decides whether
        // to refresh the display or keep the current display.
        // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
        if ( networkInfo != null && networkInfo.isConnected() ) {

            SharedPrefs.setPrankBtnEnabled(true);



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
            SharedPrefs.setPrankBtnEnabled(false);

            cToast = new CustomToast(context, "No network connectivity");
            cToast.show();
        }

        Intent prankBtnEnable = new Intent();
        prankBtnEnable.setAction("main-activity-broadcast");
        prankBtnEnable.putExtra("message", "network-changed");
        LocalBroadcastManager.getInstance(context).sendBroadcast(prankBtnEnable);

    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog((Activity)context, resultCode, 9000)
                        .show();
            } else {
                Log.i("Play Services", "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
