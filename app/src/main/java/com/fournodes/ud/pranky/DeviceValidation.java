package com.fournodes.ud.pranky;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Usman on 12/25/2015.
 */
public class DeviceValidation extends AsyncTask<String,String,String> {

    private HttpGet httpget;
    private HttpClient httpclient;
    private String result;

    public interface AsyncResponse {
       void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public DeviceValidation(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... strings) {

        httpget = new HttpGet(SharedPrefs.APP_SERVER_ADDR+"index.php?app_id=" + strings[0]); // Friends APP ID for verification

        // Execute the request
        HttpResponse response;
        try {
            httpclient = new DefaultHttpClient();
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
                result= convertStreamToString(instream);
                // now you have the string representation of the HTML request
                instream.close();
            }


        }catch (HttpHostConnectException e) {
            Log.e("Send Prank", e.toString());
            delegate.processFinish("-10");
          /*  Intent intent = new Intent("main-activity-broadcast");
            intent.putExtra("message", "server-not-found");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/

        } catch (IOException e) {
            Log.e("Send Prank", e.toString());
            delegate.processFinish("-20");
          /*  Intent intent = new Intent("main-activity-broadcast");
            intent.putExtra("message", "network-not-available");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/


        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {

        try {
            JSONObject resp= new JSONObject(s);
            Log.e("ServerRespnse", resp.getString("result"));

            if  (resp.getString("result").equals("1")){ // Device exists and IS prankable

                Log.e("ServerRespnse", "Device exists and IS prankable");
               // SharedPrefs.setFrndAppID(resp.getString("app_id")); // Store the recieved AppID AS FRIENDS ID in shared prefs
                delegate.processFinish(resp.getString("result"));

            } else if (resp.getString("result").equals("0")){ // Device exists and is NOT prankable

                Log.e("Device Validation","Device exists and is NOT prankable");
                delegate.processFinish(resp.getString("result"));

            }else { // Device does not exist

                Log.e("Device Validation","Device does not exist");
                delegate.processFinish(resp.getString("result"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){}

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
