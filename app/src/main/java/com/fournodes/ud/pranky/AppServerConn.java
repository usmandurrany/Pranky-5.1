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
                    if (Selection.itemSound == -2) // condition for HW fucntions to be treated as a special type of itemSound
                        selectedItem = Selection.itemCustomSound;
                    else
                        selectedItem = Selection.itemName;

                    broadcastResult = new Intent("main-activity-broadcast");
                    url = new URL(SharedPrefs.APP_SERVER_ADDR+"sendmsg.php?receiver_id=" + SharedPrefs.getFrndAppID() + "&sender_id=" + SharedPrefs.getMyAppID() + "&item=" + selectedItem + "&repeat_count=" + Selection.itemRepeatCount + "&volume=" + (int) Selection.itemVolume + "&message="+ActionType.PRANK+"&currenttime="+System.currentTimeMillis());

                    break;
                case PRANK_FAILED:
                case PRANK_SUCCESSFUL:
                    url = new URL(SharedPrefs.APP_SERVER_ADDR + "sendmsg.php?receiver_id=" + SharedPrefs.getFrndAppID() + "&sender_id=" + SharedPrefs.getMyAppID() + "&message="+type);
                    break;
                case SIGN_UP:
                    broadcastResult = new Intent("user-register-activity-broadcast");

                    url = new URL(SharedPrefs.APP_SERVER_ADDR+"index.php?model="+URLEncoder.encode(Build.MODEL,"UTF-8")+"&gcm_id="+SharedPrefs.getMyGcmID()+"&app_id="+SharedPrefs.getMyAppID()+"&state="+SharedPrefs.getServerState()+"&name=" +URLEncoder.encode(SharedPrefs.getUserName(),"UTF-8")+"&country_code="+URLEncoder.encode(SharedPrefs.getUserCountryCode(),"UTF-8")+"&phone="+URLEncoder.encode(SharedPrefs.getUserPhoneNumber(),"UTF-8"));
                    Log.e("SignUp Details", String.valueOf(url));
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
            if (broadcastResult==null)
                broadcastResult = new Intent("main-activity-broadcast");

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
                case SIGN_UP:
                    if  (resp.getString("result").equals("1")){
                        Log.e(TAG, resp.getString("app_id"));
                        Log.e(TAG, resp.getString("time"));

                    } else if (resp.getString("result").equals("2")){
                        Log.e(TAG,"Server State Updated");
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
