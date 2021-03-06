package com.fournodes.ud.pranky.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.enums.Action;
import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Usman on 12/30/2015.
 */
public class GCMInitiate {

    private Context context;

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
                SharedPrefs.setMyAppID(null);
                new AppServerConn(context, Action.RenewAppId).execute();

            }
            // serverState is 1 and myGcmId is not set
            else if (SharedPrefs.getServerState() == 1 && SharedPrefs.getMyGcmID() == null) {
                if (checkPlayServices()) {
                    context.startService(new Intent(
                            context, GCMRegistrationService.class)
                            .putExtra(String.valueOf(Action.Callback), String.valueOf(Type.MainActivity)));
                }


            } else if (!SharedPrefs.isSentGcmIDToServer() && SharedPrefs.getMyAppID() == null) {
                new AppServerConn(Action.RegisterDevice).execute();
            }


        } catch (ParseException e) {
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

