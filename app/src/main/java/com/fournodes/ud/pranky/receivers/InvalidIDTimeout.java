package com.fournodes.ud.pranky.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fournodes.ud.pranky.SharedPrefs;

/**
 * Created by Usman on 1/6/2016.
 */
public class InvalidIDTimeout extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (SharedPrefs.prefs == null) {
            SharedPrefs sharedprefs = new SharedPrefs(context);
        }

        SharedPrefs.setInvalidIDCount(0);
        Log.e("Reset ID",String.valueOf(SharedPrefs.getInvalidIDCount()));

    }
}
