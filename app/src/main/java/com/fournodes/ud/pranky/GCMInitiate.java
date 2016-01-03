package com.fournodes.ud.pranky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Usman on 12/30/2015.
 */
public class GCMInitiate {
    Context context;
    RegisterOnServer rs;

    public GCMInitiate(Context context) {
        this.context = context;
    }


    public void run() {
        try {
            // Convert the expDate in shared prefs to CALENDAR type for comparison
            Calendar exp = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
            exp.setTime(sdf.parse(SharedPrefs.getExpDate()));

            // Get current Time from device for comparison
            Calendar today = Calendar.getInstance(TimeZone.getDefault());


            // Perform Checks for true
            //serverState is 1 and myAppID has expired but myGcmID is set
            if (SharedPrefs.getServerState() == 1 && exp.before(today) && SharedPrefs.getMyGcmID() != null) {
                // Request new appID from server

                // Initialize the RegisterOnServer class
                rs = new RegisterOnServer(context);
                SharedPrefs.setMyAppID(""); // First clear the myAppID on device
                // Send myGcmID but empty myAppID
                String[] args = {SharedPrefs.getMyGcmID(), SharedPrefs.getMyAppID()};
                rs.execute(args);
            }
            // serverState is 1 and myGcmId is not set
            else if (SharedPrefs.getServerState() == 1 && SharedPrefs.getMyGcmID() == null && SharedPrefs.getMyGcmID() == null) {
                if (checkPlayServices()) {
                    Intent register = new Intent(context, GCMRegistrationService.class);
                    context.startService(register);
                }


            }


        } catch (Exception e) {
            Log.e("GCM Register", e.toString());
        }
    }


     private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog((Activity) context, resultCode, 9000)
                        .show();
                Log.w("Play Services", "Play Services Not Found");
            } else {
                Log.i("Play Services", "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}

