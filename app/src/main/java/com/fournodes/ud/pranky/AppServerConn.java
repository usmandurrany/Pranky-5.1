package com.fournodes.ud.pranky;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Usman on 1/4/2016.
 */
public class AppServerConn extends AsyncTask<String,String,String> {
    private String TAG = "AppServerConn";
    private Context context;
    private ActionType type;
    private URL url;
    private HttpURLConnection conn;
    private String result;
    private String validate_id;
    private Intent broadcastResult;
    private JSONObject resp;
    private WaitDialog wDiag;
    private String selectedItem;

    public AppServerConn(ActionType type){
        this.type=type;
        initialize();
    }

    public AppServerConn(Context context, ActionType type){
        this.context = context;
        this.type=type;
        initialize();
    }

    public AppServerConn(Context context, ActionType type, String validate_id){
        this.context=context;
        this.type = type;
        this.validate_id=validate_id;
        initialize();
    }

    private void initialize(){
        try {
            switch (type) {
                case DEVICE_VALIDATE:
                    broadcastResult = new Intent("prank-dialog-activity-broadcast");
                    url = new URL(SharedPrefs.APP_SERVER_ADDR + "index.php?app_id=" + URLEncoder.encode(validate_id,"UTF-8"));
                    break;
                case UPDATE_STATE:
                case REGISTER_APP_ID:
                    url = new URL(SharedPrefs.APP_SERVER_ADDR+"index.php?model="+URLEncoder.encode(Build.MODEL,"UTF-8")+"&gcm_id="+SharedPrefs.getMyGcmID()+"&app_id="+SharedPrefs.getMyAppID()+"&state="+SharedPrefs.getServerState());
                    break;
                case PRANK:
                    if (SelectedItem.sysSound == -2) // condition for HW fucntions to be treated as a special type of sysSound
                        selectedItem = SelectedItem.cusSound;
                    else
                        selectedItem = SelectedItem.soundName;

                    broadcastResult = new Intent("main-activity-broadcast");
                    url = new URL(SharedPrefs.APP_SERVER_ADDR+"sendmsg.php?friend_id=" + SharedPrefs.getFrndAppID() + "&app_id=" + SharedPrefs.getMyAppID() + "&sound=" + selectedItem + "&soundRep=" + SelectedItem.repeatCount + "&soundVol=" + (int) SelectedItem.volume + "&type="+ActionType.PRANK+"&currenttime="+System.currentTimeMillis());

                    break;
                case RESPONSE:
                    url = new URL(SharedPrefs.APP_SERVER_ADDR + "sendmsg.php?friend_id=" + SharedPrefs.getFrndAppID() + "&app_id=" + SharedPrefs.getMyAppID() + "&sound=0&soundRep=0&soundVol=0&type="+type.RESPONSE);
                    break;
                case PRANK_SUCCESSFUL:
                    url = new URL(SharedPrefs.APP_SERVER_ADDR + "sendmsg.php?friend_id=" + SharedPrefs.getFrndAppID() + "&app_id=" + SharedPrefs.getMyAppID() + "&sound=0&soundRep=0&soundVol=0&type="+type.PRANK_SUCCESSFUL);
                    break;

            }
        }catch (MalformedURLException e){e.printStackTrace();}
        catch (UnsupportedEncodingException e){e.printStackTrace();}
    }

    public void showWaitDialog(String text){
        wDiag = new WaitDialog(context);
        wDiag.setWaitText(text);
        wDiag.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        try{
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            result = convertStreamToString(conn.getInputStream());

        }catch (IOException e){
            e.printStackTrace();
            broadcastResult.putExtra("message", "network-error");
        }


        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (wDiag!=null)
            wDiag.dismiss();

        try {
            resp = new JSONObject(result);

            switch (type) {
                case DEVICE_VALIDATE:
                    if  (resp.getString("result").equals("1")){ // Device exists and IS prankable

                        Log.e(TAG, "Device exists and IS prankable");
                        broadcastResult.putExtra("message", "valid-id");


                    } else if (resp.getString("result").equals("0")){ // Device exists and is NOT prankable

                        Log.e(TAG,"Device exists and is NOT prankable");
                        broadcastResult.putExtra("message", "not-prankable");


                    }else { // Device does not exist

                        Log.e(TAG,"Device does n ot exist");
                        broadcastResult.putExtra("message", "invalid-id");


                    }
                    break;
                case UPDATE_STATE:
                case REGISTER_APP_ID:
                    Calendar exp_date = Calendar.getInstance(TimeZone.getDefault());
                    exp_date.set(Calendar.HOUR, (exp_date.get(Calendar.HOUR) + 24));
                    Log.e("ExpiryDate", exp_date.getTime().toString());
                    if  (resp.getString("result").equals("1")){
                        Log.e(TAG, resp.getString("app_id"));
                        Log.e(TAG, resp.getString("time"));


                        SharedPrefs.setMyAppID(resp.getString("app_id")); // Store the recieved myAppID in shared prefs
                        SharedPrefs.setSentGcmIDToServer(true);// GCM ID has been sent successfully
                        SharedPrefs.setExpDate(exp_date.getTime().toString());// Expiry date for myAppId is saved
                    } else if (resp.getString("result").equals("2")){
                        Log.e(TAG,"Server State Updated");
                    }
                    break;
                case PRANK:
                    broadcastResult.putExtra("message", "prank-sent");

                    Log.e("Success", resp.getString("success"));
                    Log.e("Failure", resp.getString("failure"));
                    break;
                case RESPONSE:
                    Log.e(TAG,"Response generated.");
                break;


            }
        }catch (JSONException e){e.printStackTrace();}
        catch (NullPointerException e){e.printStackTrace();}

        if (broadcastResult!=null)
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastResult);

        super.onPostExecute(result);
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
