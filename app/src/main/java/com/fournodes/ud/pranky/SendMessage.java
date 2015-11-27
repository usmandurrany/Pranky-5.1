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
public class SendMessage extends AsyncTask<String, String, String> {
    Context context;
    int sound=0;
    int soundRep=0;
    int soundVol=0;
    String soundName="";
    HttpGet httpget;

    public SendMessage(Context context, int sound, int soundRep, int soundVol){
        this.context=context;
        this.sound=sound;
        this.soundRep=soundRep;
        this.soundVol=soundVol;
    }
    public SendMessage(Context context){
        this.context=context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String app_id=SharedPrefs.getMyAppID();
        String frnd_id=SharedPrefs.getFrndAppID();
        String type=strings[0];

        if (sound != 0)
        {soundName= context.getResources().getResourceEntryName(sound);}

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object

         httpget = new HttpGet(SharedPrefs.APP_SERVER_ADDR+"sendmsg.php?friend_id=" + frnd_id + "&app_id=" + app_id + "&sound=" + soundName + "&soundRep=" + String.valueOf(soundRep) + "&soundVol=" + String.valueOf(soundVol) + "&type=" + type);

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
