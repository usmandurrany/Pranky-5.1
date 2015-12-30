package com.fournodes.ud.pranky;

import android.content.Context;
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
public class UpdateServerState extends AsyncTask<String, String, String> {
    private Context context;
    private HttpGet httpget;
    String result;


    public UpdateServerState(Context context){
        this.context=context;
    }


    @Override
    protected String doInBackground(String... strings) {
        String app_id= SharedPrefs.getMyAppID();
        String gcm_id= SharedPrefs.getMyGcmID();
        String frnd_id = strings[0]; // id of the prankster to return response to
        String serverState = strings[1]; // 0 for not prankable, 1 for allowed

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        httpget = new HttpGet("http://pranky.four-nodes.com/setState.php?friend_id=" + frnd_id +"&app_id="+app_id+"&gcm_id="+gcm_id+"&state="+serverState);

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
        return result;
    }

}
