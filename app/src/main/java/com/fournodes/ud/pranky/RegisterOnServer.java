package com.fournodes.ud.pranky;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Usman on 11/24/2015.
 */
public class RegisterOnServer extends AsyncTask<String, String, String>{
    String result;
    String model;
    Context context;
    private HttpURLConnection conn;
    private URL url;
    public RegisterOnServer(Context context){
       this.context=context;
    }


    @Override
    protected String doInBackground(String... strings) {
        model = Build.MODEL;
        String gcm_id = strings[0];
        String app_id = strings[1];
        int state = SharedPrefs.getServerState();


        try {
            url = new URL(SharedPrefs.APP_SERVER_ADDR+"index.php?model="+model+"&gcm_id="+gcm_id+"&app_id="+app_id+"&state="+String.valueOf(state));
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            result = convertStreamToString(conn.getInputStream());



        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();

        }


        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        Calendar exp_date = Calendar.getInstance(TimeZone.getDefault());
        exp_date.set(Calendar.HOUR, (exp_date.get(Calendar.HOUR) + 24));
        Log.e("ExpiryDate", exp_date.getTime().toString());
        //Log.e("Server", s);
        try {
            JSONObject resp= new JSONObject(s);
            Log.e("ServerRespnse", resp.getString("result"));

            if  (resp.getString("result").equals("1")){
                Log.e("ServerRespnse", resp.getString("app_id"));
                Log.e("ServerRespnse", resp.getString("time"));


                SharedPrefs.setMyAppID(resp.getString("app_id")); // Store the recieved myAppID in shared prefs
                SharedPrefs.setSentGcmIDToServer(true);// GCM ID has been sent successfully
                SharedPrefs.setExpDate(exp_date.getTime().toString());// Expiry date for myAppId is saved
            } else if (resp.getString("result").equals("2")){
                Log.e("Response","Server State Updated");
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
