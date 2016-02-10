package com.fournodes.ud.pranky.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fournodes.ud.pranky.objects.ContactSelected;
import com.fournodes.ud.pranky.DatabaseHelper;
import com.fournodes.ud.pranky.ItemSelected;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.dialogs.WaitDialog;
import com.fournodes.ud.pranky.enums.Action;
import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.interfaces.OnCompleteListener;

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
public class AppServerConn extends AsyncTask<String, String, String> {
    public OnCompleteListener delegate = null;

    private String TAG = "AppServerConn";
    private Context context;
    private Action type;
    private URL url;
    private String result;
    private String validate_id;
    private Intent broadcastResult;
    private WaitDialog wDiag;
    private String numID;
    private String number;

    public AppServerConn(Action type) {
        this.type = type;
        initialize();
    }

    public AppServerConn(Context context, Action type, String numID, String number) {
        this.context = context;
        this.type = type;
        this.numID = numID;
        this.number = number;
        initialize();
    }

    public AppServerConn(Context context, Action type) {
        this.context = context;
        this.type = type;
        initialize();
    }

    public AppServerConn(Context context, Action type, String validate_id) {
        this.context = context;
        this.type = type;
        this.validate_id = validate_id;
        initialize();
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

    private void initialize() {
            switch (type) {
                case ValidateId:
                    broadcastResult = new Intent(String.valueOf(Type.PrankDialogActivity));
                    
                    url = URLBuilder("index", "app_id="+validate_id);
                    
                    Log.e("VALIDATE DEVICE", String.valueOf(url));

                    break;
                case UpdateAvailability:
                    
                    url = URLBuilder("index",
                            "id=" + SharedPrefs.getAppServerID(),
                            "state=" + SharedPrefs.getServerState());
                    
                    Log.e("UPDATE STATE", String.valueOf(url));

                    break;

                case RenewAppId:
                    if (SharedPrefs.getAppServerID() == null){
                        url = URLBuilder("index",
                                "model=" + Build.MODEL ,
                                "gcm_id=" + SharedPrefs.getMyGcmID() ,
                                "state=" + SharedPrefs.getServerState());
                        Log.e("SEND GCM ID", String.valueOf(url));

                        type= Action.RegisterDevice; //important hack

                    }else {
                        url = URLBuilder("index",
                                "id=" + SharedPrefs.getAppServerID(),
                                "app_id=null");
                        Log.e("RENEW APP ID", String.valueOf(url));
                    }

                    break;

                case RegisterDevice: //Returns app id
                    
                    url = URLBuilder("index",
                            "model=" + Build.MODEL ,
                            "gcm_id=" + SharedPrefs.getMyGcmID() ,
                            "state=" + SharedPrefs.getServerState());
                    
                    Log.e("SEND GCM ID", String.valueOf(url));

                    break;
                case PlayPrank:
                    String selectedItem;
                    if (ItemSelected.itemSound == -2) // condition for HW functions to be treated as a special type of itemSound
                        selectedItem = ItemSelected.itemCustomSound;
                    else
                        selectedItem = ItemSelected.itemName;

                    broadcastResult = new Intent(String.valueOf(Type.MainActivity));
                    
                    url = URLBuilder("sendmsg",
                            "receiver_id=" + SharedPrefs.getFrndAppID() ,
                            "sender_id=" + SharedPrefs.getMyAppID(),
                            "item=" + selectedItem,
                            "repeat_count=" + ItemSelected.itemRepeatCount ,
                            "volume=" + (int) ItemSelected.itemVolume ,
                            "message=" + Action.PlayPrank,
                            "currenttime=" + System.currentTimeMillis());
                    
                    Log.e("PRANK DETAILS", String.valueOf(url));

                    break;
                case NotifyPrankFailed:
                case NotifyPrankSuccessful:
                    
                    url = URLBuilder("sendmsg",
                            "receiver_id=" + SharedPrefs.getFrndAppID(),
                            "sender_id=" + SharedPrefs.getMyAppID(),
                            "message=" + type);

                    Log.e("PRANK SUCCESS/FAILED", String.valueOf(url));

                    break;

                case RegisterUser:
                    //broadcastResult = new Intent(String.valueOf(Type.UserRegistrationActivity));

                    String id = SharedPrefs.getAppServerID();
                    
                    if (id == null) {
                        url = URLBuilder("index",
                                "model=" + Build.MODEL ,
                                "gcm_id=" + SharedPrefs.getMyGcmID(),
                                "state=" + SharedPrefs.getServerState(),
                                "name=" + SharedPrefs.getUserName(),
                                "country_code=" + SharedPrefs.getUserCountryCode(),
                                "phone=" + SharedPrefs.getUserPhoneNumber());
                        
                    } else { //From prank dialog
                        
                        url = URLBuilder("index",
                                "model=" + Build.MODEL,
                                "gcm_id=" + SharedPrefs.getMyGcmID(),
                                "app_id=" + SharedPrefs.getMyAppID(),
                                "id=" + SharedPrefs.getAppServerID(),
                                "name=" + SharedPrefs.getUserName(),
                                "country_code=" + SharedPrefs.getUserCountryCode(),
                                "phone=" + SharedPrefs.getUserPhoneNumber());
                        
                    }
                    
                    Log.e("SignUp Details", String.valueOf(url));
                    break;
                case RetrieveFriendId:
                    
                    broadcastResult = new Intent(String.valueOf(Type.PrankDialogActivity));
                    
                    url = URLBuilder("index",
                            "id=" + numID,
                            "app_id=" + SharedPrefs.getMyAppID(),
                            "phone=" + number.trim());
                    
                    Log.e("GET FRIEND ID", String.valueOf(url));

                    break;

                case VerifyUserRegistration:
                    broadcastResult = new Intent(String.valueOf(Type.MainActivity));

                    url = URLBuilder("index",
                            "id=" + SharedPrefs.getAppServerID(),
                            "app_id=" + SharedPrefs.getMyAppID());
                    
                    Log.e("Registration Check", String.valueOf(url));

                    break;
                case PingServer:
                    url = URLBuilder("index",
                            "id=" + SharedPrefs.getAppServerID(),
                            "app_id=" + SharedPrefs.getMyAppID(),
                            "pingServer=true");

                    Log.e("Ping Server", String.valueOf(url));
                    break;

            }
    }

    public void showWaitDialog(String text) {
        wDiag = new WaitDialog(context);
        wDiag.setWaitText(text);
        wDiag.show();
    }


    @Override
    protected String doInBackground(String... strings) {

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            conn.connect();
            result = convertStreamToString(conn.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
            if (broadcastResult == null)
                broadcastResult = new Intent(String.valueOf(Type.MainActivity));
            if (wDiag != null)
                wDiag.dismiss();

            broadcastResult.putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.NetworkError));
        }


        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (wDiag != null)
            wDiag.dismiss();

        try {

            JSONObject resp = new JSONObject(result);

            switch (type) {
                case ValidateId:
                    if (resp.getString("result").equals("1")) { // Device exists and IS prankable

                        Log.e(TAG, "Device exists and IS prankable");
                        broadcastResult.putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.ValidId));


                    } else if (resp.getString("result").equals("0")) { // Device exists and is NOT prankable

                        Log.e(TAG, "Device exists and is NOT prankable");
                        broadcastResult.putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.UserUnavailable));


                    } else { // Device does not exist

                        Log.e(TAG, "Device does n ot exist");
                        broadcastResult.putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.UserUnavailable));


                    }
                    break;
                case RenewAppId:

                    Log.e(TAG, resp.getString("app_id"));
                    Log.e(TAG, resp.getString("id"));
                    SharedPrefs.setMyAppID(resp.getString("app_id")); // Store the received myAppID in shared prefs

                    Calendar date = Calendar.getInstance(TimeZone.getDefault());
                    date.set(Calendar.HOUR, (date.get(Calendar.HOUR) + 24));
                   //date.set(Calendar.MINUTE, (date.get(Calendar.MINUTE) + 5));
                    SharedPrefs.setExpDate(date.getTime().toString());// Expiry date for myAppId is saved

                    break;

                case UpdateAvailability:
                case RegisterDevice:
                    Calendar exp_date = Calendar.getInstance(TimeZone.getDefault());
                    exp_date.set(Calendar.HOUR, (exp_date.get(Calendar.HOUR) + 24));
                    //exp_date.set(Calendar.MINUTE, (exp_date.get(Calendar.MINUTE) + 5));
                    Calendar serverPingDate = Calendar.getInstance(TimeZone.getDefault());

                    serverPingDate.set(Calendar.DAY_OF_MONTH,
                            (serverPingDate.get(Calendar.DAY_OF_MONTH)+SharedPrefs.PING_SERVER_INTERVAL));



                    Log.e("ExpiryDate", exp_date.getTime().toString());
                    if (resp.getString("result").equals("1")) {
                        Log.e(TAG, resp.getString("app_id"));
                        //Log.e(TAG, resp.getString("time"));

                        SharedPrefs.setPingServerDate(serverPingDate.getTime().toString());
                        SharedPrefs.setAppServerID(resp.getString("id")); // Store the received ServerID in shared prefs
                        SharedPrefs.setMyAppID(resp.getString("app_id")); // Store the received myAppID in shared prefs
                        SharedPrefs.setSentGcmIDToServer(true);// GCM ID has been sent successfully
                        SharedPrefs.setExpDate(exp_date.getTime().toString());// Expiry date for myAppId is saved
                    } else if (resp.getString("result").equals("2")) {
                        Log.e(TAG, "Server State Updated");
                    }
                    break;
                case RegisterUser:
                    if (resp.getString("result").equals("failed")){
                        delegate.onCompleteFailed();
                    }else {
                        Calendar exp = Calendar.getInstance(TimeZone.getDefault());
                        exp.set(Calendar.HOUR, (exp.get(Calendar.HOUR) + 24));


                        Calendar serverPing = Calendar.getInstance(TimeZone.getDefault());

                        serverPing.set(Calendar.DAY_OF_MONTH,
                                (serverPing.get(Calendar.DAY_OF_MONTH)+SharedPrefs.PING_SERVER_INTERVAL));

                        Log.e("ExpiryDate", exp.getTime().toString());
                        Log.e(TAG, resp.getString("app_id"));
                        Log.e(TAG, resp.getString("id"));
                        Log.e(TAG, resp.getString("locale"));
                        SharedPrefs.setLocale(resp.getString("locale")); // Store the received myAppID in shared prefs
                        SharedPrefs.setMyAppID(resp.getString("app_id")); // Store the received myAppID in shared prefs
                        SharedPrefs.setAppServerID(resp.getString("id"));
                        SharedPrefs.setSentGcmIDToServer(true);// GCM ID has been sent successfully
                        SharedPrefs.setExpDate(exp.getTime().toString());// Expiry date for myAppId is saved
                        SharedPrefs.setPingServerDate(serverPing.getTime().toString());

                        delegate.onCompleteSuccess();
                    }
                    break;

                case PlayPrank:
                    broadcastResult.putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.PrankSent));
                    Log.e("Success", resp.getString("success"));
                    Log.e("Failure", resp.getString("failure"));
                    //if (resp.getString("success").equals("1"))
                        SharedPrefs.setPranksLeft(SharedPrefs.getPranksLeft()-1);

                    break;

                case RetrieveFriendId:
                    if (resp.getString("frnd_id").equals("NA"))
                        broadcastResult.putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.UserUnavailable));
                    else {
                        ContactSelected.setApp_id(resp.getString("frnd_id"));
                        Log.e("Friend ID", resp.getString("frnd_id"));
                        Log.e("Friend Device ID", resp.getString("device_id"));
                        SharedPrefs.setFrndAppID(resp.getString("frnd_id")); // Store the received myAppID in shared prefs
                        SharedPrefs.setFriendDeviceId(resp.getString("device_id")); // Store the received myAppID in shared prefs
                        broadcastResult.putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.RetrievedFriendId));
                    }
                    break;
                case VerifyUserRegistration:
                    if (resp.getString("registered").equals("false")) {
                        if (SharedPrefs.prefs == null)
                            new SharedPrefs(context).initAllPrefs();
                        SharedPrefs.setMyAppID(null);
                        SharedPrefs.setSentGcmIDToServer(false);
                        SharedPrefs.setSignUpComplete(false);
                        SharedPrefs.setSignUpSkipped(true);
                        SharedPrefs.setUserCountry(null);
                        SharedPrefs.setUserCountryCode(null);
                        SharedPrefs.setUserName(null);
                        SharedPrefs.setUserPhoneNumber(null);
                        new DatabaseHelper(context).nuke(DatabaseHelper.TABLE_CONTACTS);
                        broadcastResult.putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.UserUnregistered));
                    } else
                        broadcastResult.putExtra(String.valueOf(Action.Broadcast), String.valueOf(Message.UserRegistered));

                    break;
                case PingServer:
                    if (resp.getString("result").equals("8")) {
                        Calendar serverPing = Calendar.getInstance(TimeZone.getDefault());
                        serverPing.set(Calendar.DAY_OF_MONTH, (serverPing.get(Calendar.DAY_OF_MONTH)+6));
                        SharedPrefs.setPingServerDate(serverPing.getTime().toString());
                        Log.e("Server Ping Date",serverPing.getTime().toString());

                    }
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (broadcastResult != null)
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastResult);

        super.onPostExecute(result);
    }


    public URL URLBuilder(String page, String... params) {
        try {
            String url = SharedPrefs.APP_SERVER_ADDR + page + ".php?";

            for (String value : params) {
                String[] param =value.split("=");
                url +=  param[0]+"="+URLEncoder.encode(param[1], "UTF-8") + "&";
            }
            return new URL(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IndexOutOfBoundsException e){e.printStackTrace();}

        return null;
    }
}
