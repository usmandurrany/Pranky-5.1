package com.fournodes.ud.pranky.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.enums.Action;
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.enums.Type;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by Usman on 11/23/2015.
 */
public class GCMRegistrationService extends IntentService {

    private static final String TAG = "RegIntentService";

    public GCMRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            Log.w("SenderID", getString(R.string.gcm_defaultSenderId));
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM ID: " + token);
            SharedPrefs.setMyGcmID(token);


        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            SharedPrefs.setSentGcmIDToServer(false);
        }

        switch (Type.valueOf(intent.getStringExtra(String.valueOf(Action.Callback)))) {
            case UserRegistrationActivity:
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(
                        String.valueOf(Type.UserRegistrationActivity)).
                        putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.TokenGenerated)));
                break;

            case MainActivity:
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(
                        String.valueOf(Type.MainActivity)).
                        putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.TokenGenerated)));
                break;
        }

    }

}
