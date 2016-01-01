package com.fournodes.ud.pranky;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
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
    private WaitDialog wDialog;
    private Context context;
    private Intent intent;

    public DeviceValidation(Context context){

        this.context=context;

    }

    public void init(){
        intent = new Intent("prank-dialog-activity-broadcast");
        wDialog = new WaitDialog(context);
        wDialog.setWaitText("P a i r i n g ...");
        wDialog.show();
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
            intent.putExtra("message", "server-unreachable");


        } catch (IOException e) {
            Log.e("Send Prank", e.toString());
            intent.putExtra("message", "network-unavailable");

        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        if (wDialog != null)
            wDialog.dismiss();

        try {
            JSONObject resp= new JSONObject(s);
            Log.e("ServerRespnse", resp.getString("result"));

            if  (resp.getString("result").equals("1")){ // Device exists and IS prankable

                Log.e("ServerRespnse", "Device exists and IS prankable");
                intent.putExtra("message", "valid-id");


            } else if (resp.getString("result").equals("0")){ // Device exists and is NOT prankable

                Log.e("Device Validation","Device exists and is NOT prankable");
                intent.putExtra("message", "not-prankable");


            }else { // Device does not exist

                Log.e("Device Validation","Device does n ot exist");
                intent.putExtra("message", "invalid-id");


            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){}

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
