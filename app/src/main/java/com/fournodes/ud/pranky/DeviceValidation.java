package com.fournodes.ud.pranky;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
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

/**
 * Created by Usman on 12/25/2015.
 */
public class DeviceValidation extends AsyncTask<String,String,String> {

    private String result;
    private WaitDialog wDialog;
    private Context context;
    private Intent intent;

    private HttpURLConnection conn;
    private URL url;

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

        try {
            url = new URL(SharedPrefs.APP_SERVER_ADDR+"index.php?app_id=" + strings[0]);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            result = convertStreamToString(conn.getInputStream());



        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
            intent.putExtra("message", "network-error");

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

        String line;
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
