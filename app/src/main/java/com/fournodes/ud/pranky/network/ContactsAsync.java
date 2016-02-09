package com.fournodes.ud.pranky.network;

import android.content.Context;
import android.os.AsyncTask;

import com.fournodes.ud.pranky.DatabaseHelper;
import com.fournodes.ud.pranky.Contacts;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.dialogs.WaitDialog;
import com.fournodes.ud.pranky.interfaces.OnCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

/**
 * Created by Usman on 8/1/2016.
 */
public class ContactsAsync extends AsyncTask<JSONArray, String, String> {
    public OnCompleteListener delegate = null;
    private String result;
    private Context context;
    private WaitDialog wDiag;

    public ContactsAsync(Context context) {
        this.context = context;
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

    public void init(String text) {
        wDiag = new WaitDialog(context);
        wDiag.setWaitText(text);
        wDiag.show();
    }

    @Override
    protected String doInBackground(JSONArray... jsonArrays) {
        try {
            Contacts contacts = new Contacts(context);
            DatabaseHelper prankyDB = new DatabaseHelper(context);
            if (jsonArrays.length <= 0) {
                prankyDB.storeContacts(contacts.getAll());
            }

            if (SharedPrefs.prefs == null)
                SharedPrefs.setContext(context);
            String url = SharedPrefs.APP_SERVER_ADDR + "number_func.php?country_code=" + SharedPrefs.getUserCountryCode() +"&locale=" + SharedPrefs.getLocale();
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);
            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            con.setRequestProperty("Accept", "*/*");

            String urlParameters;
            if (jsonArrays.length>0) {
                urlParameters = "payload=" + jsonArrays[0].toString();
            } else {
                urlParameters = "payload=" + prankyDB.contactDetails().toString();
            }


            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            result = convertStreamToString(con.getInputStream());


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Log.e("Server Resp",result);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (wDiag != null)
            wDiag.dismiss();
        DatabaseHelper prankyDB = new DatabaseHelper(context);
        try {

            JSONArray resp = new JSONArray(result);
            for (int i = 0; i < resp.length(); i++) {
                JSONObject contactDetails = resp.getJSONObject(i);
                HashSet<String> numbers = new HashSet<>();
                HashSet<String> numIDs = new HashSet<>();
                String id = contactDetails.getString("id");
                for (int j = 1; j < contactDetails.length(); j++) {
                    if (contactDetails.has("number" + j)) {
                        numIDs.add(contactDetails.getString("num" + j + "_id"));
                        numbers.add(contactDetails.getString("number" + j));
                    }
                }
                String[] numbersArr = new String[numbers.size()];
                String[] numIDsArr = new String[numIDs.size()];
                numbers.toArray(numbersArr);
                numIDs.toArray(numIDsArr);

                prankyDB.storeRegisteredContact(id, numIDsArr, numbersArr);

            }
            if (delegate!=null)
            delegate.onCompleteContactSync();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }
}
