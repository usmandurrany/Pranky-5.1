package com.fournodes.ud.pranky;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Usman on 8/1/2016.
 */

public class Contacts {
    private Context context;
    private Cursor cDetails; //Contact name and id
    private Cursor cVersion; //Contact number of times edited
    private Cursor cPhones; //Contact all phone

    private int count;
    private String contactId;
    private String contactName;
    private String contactVersion;
    private String contactNumber;

    private DatabaseHelper prankyDB;

    public Contacts(Context context) {
        this.context = context;
    }

    public HashMap getAll() {
        HashMap<String, String[]> mapContactsAll = new HashMap<>();

        cDetails = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null);


        if (cDetails != null && (count = cDetails.getCount()) > 0) {

            while (cDetails.moveToNext()) {

                if (Integer.parseInt(cDetails.getString(cDetails.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    LinkedHashSet<String> contactDetails = new LinkedHashSet<>();

                    contactId = cDetails.getString(cDetails.getColumnIndex(ContactsContract.Contacts._ID));
                    // Log.e("Contact Id",String.valueOf(contactId));
                    contactName = cDetails.getString(cDetails.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //Log.e("Contact Name",String.valueOf(contactName));

                    /********** Store id of updated contact in the HashMap ****************/
                    contactDetails.add(String.valueOf(contactId));

                    cVersion = context.getContentResolver().query(
                            ContactsContract.RawContacts.CONTENT_URI,
                            new String[]{ContactsContract.RawContacts.VERSION},
                            ContactsContract.RawContacts.CONTACT_ID + "=?",
                            new String[]{contactId}, null);

                    if (cVersion != null && cVersion.moveToNext()) {

                        contactVersion = cVersion.getString(cVersion.getColumnIndex(ContactsContract.RawContacts.VERSION));
                        //Log.e("Contact Version",String.valueOf(contactVersion));

                        /********** Store id of updated contact in the HashMap ****************/
                        contactDetails.add(contactVersion);


                        cPhones = context.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{contactId},
                                null);

                        if (cPhones != null) {
                            while (cPhones.moveToNext()) {

                                contactNumber = cPhones.getString(cPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                //Log.e("Contact Number",contactNumber);


                                /******* Store the number of updated contact in the HashMap ********/
                                contactDetails.add(formatNumber(contactNumber));
                            }

                            cPhones.close();
                        }

                        String[] contactDetailsArray = new String[contactDetails.size()];
                        contactDetails.toArray(contactDetailsArray);
                        //Log.e("Contact Detials Arr", Arrays.toString(contactDetailsArray));
                        mapContactsAll.put(contactName, contactDetailsArray);

                        cVersion.close();
                    }


                }

            }
            cDetails.close();
        }

        return mapContactsAll;
    }


    public JSONArray getUpdated() {
        HashMap<String, String[]> allContacts = getAll();
        prankyDB = new DatabaseHelper(context);
        List<JSONObject> conUpdatedList = new ArrayList<>();
        for (HashMap.Entry<String, String[]> entry : allContacts.entrySet()) {
            String[] contactDetails = entry.getValue();
            if (prankyDB.isUpdated(contactDetails[0], contactDetails[1])) {
                HashMap<String, String> contactUpdated = new HashMap<>();
                contactUpdated.put("id", contactDetails[0]);
                for (int i = 2; i < contactDetails.length; i++) {
                    contactUpdated.put("number" + (i - 1), contactDetails[i]);
                }
                conUpdatedList.add(new JSONObject(contactUpdated));
            }
        }
        return new JSONArray(conUpdatedList);
    }


    public String formatNumber(String contactNumber) {
        try {
            if (contactNumber != null)
                return URLEncoder.encode(
                        contactNumber
                        .replace("-", "")
                        .replace(")", "")
                        .replace("(", "")
                        .replace(" ", ""),
                        "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


}
