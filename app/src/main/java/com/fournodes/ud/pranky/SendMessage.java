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

import java.io.IOException;

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
    ProgressDialog pdialog;
    WaitDialog wDialog;




  public SendMessage(Context context){
      this.context = context;
  }


public void initDialog(){
/*

    pdialog = new ProgressDialog(context);
    pdialog.setMessage("Please wait..");
    pdialog.setIndeterminate(true);
    pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    pdialog.setCancelable(false);
    pdialog.show();
*/

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

            HttpClient httpclient = new DefaultHttpClient();

            // Prepare a request object

            httpget = new HttpGet(SharedPrefs.APP_SERVER_ADDR + "sendmsg.php?friend_id=" + frnd_id + "&app_id=" + app_id + "&sound=" + soundName + "&soundRep=" + String.valueOf(soundRep) + "&soundVol=" + String.valueOf(soundVol) + "&type=" + type +"&currenttime="+System.currentTimeMillis());

            // Execute the request
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);
                // Examine the response status
                Log.i("ServerResponse", response.getStatusLine().toString());

            } catch (HttpHostConnectException e) {
                Log.e("Send Prank", e.toString());
                Intent intent = new Intent("main-activity-broadcast");
                intent.putExtra("message", "server-not-found");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            } catch (IOException e) {
                Log.e("Send Prank", e.toString());
                Intent intent = new Intent("main-activity-broadcast");
                intent.putExtra("message", "network-not-available");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


            }

    return null;
    }

    @Override
    protected void onPostExecute(String s) {
                super.onPostExecute(s);
        if (wDialog != null)
        wDialog.dismiss();

    }
}
