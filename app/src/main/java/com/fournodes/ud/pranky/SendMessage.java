package com.fournodes.ud.pranky;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Usman on 11/24/2015.
 */
public class SendMessage extends AsyncTask<String, String, String> {
    private Context context;
    private int sound=0;
    private int soundRep=0;
    private int soundVol=0;
    private String soundName="";
    private WaitDialog wDialog;
    private HttpURLConnection conn;
    private URL url;
    private Intent intent;
    private String result;


  public SendMessage(Context context){
      this.context = context;
  }


public void initDialog(){
    intent = new Intent("main-activity-broadcast");
    wDialog = new WaitDialog(context);
    wDialog.show();
}


    @Override
    protected String doInBackground(String... strings) {

        this.sound = Sound.sysSound;
        this.soundRep = Sound.repeatCount;
        this.soundVol = (int) Sound.volume;

        String app_id = SharedPrefs.getMyAppID();
            String frnd_id = SharedPrefs.getFrndAppID();
            String type = strings[0];

            if (sound == -2) // condition for HW fucntions to be treated as a special type of sysSound
                soundName = Sound.cusSound;
            else
                soundName = Sound.soundName;

        try {
            Log.e("Friend ID",String.valueOf(frnd_id));
            Log.e("APP ID",String.valueOf(app_id));
            Log.e("Sound Name",String.valueOf(sound));
            Log.e("Sound Repeat",String.valueOf(soundRep));
            Log.e("Sound Volume",String.valueOf(soundVol));
            Log.e("Type",String.valueOf(type));
            Log.e("Time Param",String.valueOf(System.currentTimeMillis()));

            url = new URL(SharedPrefs.APP_SERVER_ADDR+"sendmsg.php?friend_id=" + frnd_id + "&app_id=" + app_id + "&sound=" + soundName + "&soundRep=" + String.valueOf(soundRep) + "&soundVol=" + String.valueOf(soundVol) + "&type=" + type +"&currenttime="+System.currentTimeMillis());
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            result = convertStreamToString(conn.getInputStream());


            intent.putExtra("message", "prank-sent");

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
       super.onPostExecute(s);
        if (wDialog != null)
        wDialog.dismiss();
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        try {
            JSONObject resp= new JSONObject(s);
            Log.e("Success", resp.getString("success"));
            Log.e("Failure", resp.getString("failure"));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){}


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
