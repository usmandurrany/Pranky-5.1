package com.fournodes.ud.pranky;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

/**
 * Created by Usman on 11/24/2015.
 */
public class SendPrank extends AsyncTask<String, String, String> {
    Context context;
    int sound;
    int soundRep;
    int soundVol;
    HttpGet httpget;

    public SendPrank(Context context, int sound, int soundRep, int soundVol){
        this.context=context;
        this.sound=sound;
        this.soundRep=soundRep;
        this.soundVol=soundVol;
    }
    public SendPrank(Context context)
    {
        this.context=context;
        this.sound=0;
        this.soundRep=0;
        this.soundVol=0;
    }

    @Override
    protected String doInBackground(String... strings) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefs.SHARED_PREF_FILE, 0);
        String frnd_id = sharedPreferences.getString(SharedPrefs.FRIENDS_ID, null);
        String app_id = sharedPreferences.getString(SharedPrefs.APP_ID, null);
        String prankableResp = sharedPreferences.getString(SharedPrefs.PRANKABLE_RESP, "enabled");
        boolean prankable = sharedPreferences.getBoolean(SharedPrefs.PRANKABLE, true);

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object

        if (sound==0){
           httpget = new HttpGet("http://motasimhussain.koding.io/usman/sendmsg.php?friend_id=" + frnd_id + "&prank_id=" + app_id + "&sound=" + String.valueOf(sound) + "&soundRep=" + String.valueOf(soundRep) + "&soundVol=" + String.valueOf(soundVol) + "&response=" + prankableResp + "&prankable" + String.valueOf(prankable));
        }
        else {
            httpget = new HttpGet("http://motasimhussain.koding.io/usman/sendmsg.php?friend_id=" + frnd_id + "&prank_id=" + app_id + "&sound=" + context.getResources().getResourceEntryName(sound) + "&soundRep=" + String.valueOf(soundRep) + "&soundVol=" + String.valueOf(soundVol) + "&response=" + prankableResp + "&prankable" + String.valueOf(prankable));
        }
        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("ServerResponse", response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                //result= convertStreamToString(instream);
                // now you have the string representation of the HTML request
                instream.close();
            }


        } catch (Exception e) {}
        return null;
    }
}
