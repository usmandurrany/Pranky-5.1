package com.fournodes.ud.pranky;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Usman on 11/24/2015.
 */
public class RemoteServer extends AsyncTask<String, String, String>{
    String result;
    String model;
    Context context;
    public RemoteServer(Context context){
       this.context=context;
    }
    @Override
    protected String doInBackground(String... strings) {
        model = Build.MODEL;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefs.SHARED_PREF_FILE, 0);
        String gcm_id = sharedPreferences.getString(SharedPrefs.REGISTRATION_TOKEN,null);

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet("http://pranky.four-nodes.com/index.php?model="+model+"&gcm_id="+gcm_id);

        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.e("ServerResponse", response.toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
                // now you have the string representation of the HTML request
                instream.close();
            }


        } catch (Exception e) {}




        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        SharedPreferences prefs = context.getSharedPreferences(SharedPrefs.SHARED_PREF_FILE,0);
        Log.e("Server", s);
        try {
            JSONObject resp= new JSONObject(s);
            Log.e("ServerRespnse", resp.getString("result"));
            Log.e("ServerRespnse", resp.getString("app_id"));
            Log.e("ServerRespnse", resp.getString("time"));
            if  (resp.getString("result") == "1"){
                prefs.edit().putString(SharedPrefs.APP_ID,resp.getString("app_id")).apply();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onPostExecute(s);
    }

    private static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
