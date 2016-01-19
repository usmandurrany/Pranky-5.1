package com.fournodes.ud.pranky;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by Usman on 8/1/2016.
 */

public class GetContacts{
    Context context;

    public GetContacts(Context context) {
        this.context = context;
    }

    public JSONArray checkConctactVer() {
        String id;
        String version;

        AppDB prankyDB = new AppDB(context);

        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        ArrayList<JSONObject> contactNumbers = new ArrayList<>();
        Integer contactsCount = cursor.getCount();
        if (contactsCount > 0)
        {
            while(cursor.moveToNext())
            {
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Map<String,String> contact = new HashMap<>();
                    id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    //Log.e("While Loop",id);
                    Cursor ver =  context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, new String[] {ContactsContract.RawContacts.VERSION}, ContactsContract.RawContacts.CONTACT_ID+"=?", new String[] {id}, null);
                    try {
                        ver.moveToNext();
                        version = ver.getString(ver.getColumnIndex(ContactsContract.RawContacts.VERSION));
                        //Log.e("While Loop Version",version);
                   if (prankyDB.isUpdated(id,version)){

                       Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                               new String[]{id}, null);
                       // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list
                       contact.put("id",String.valueOf(id));
                       int i=0;
                       while (pCursor.moveToNext())
                       {
                            String phoneNo 	= pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("-","").replace(")","").replace("(","").replace(" ","");
                           contact.put("number"+(i+1), URLEncoder.encode(phoneNo,"UTF-8"));
                          //Log.e("Inner Loop",phoneNo);
                           i++;
                       }
                       contactNumbers.add(new JSONObject(contact));
                       pCursor.close();
                       //String[] contactNumArray = new String[contactNumbers.size()];
                       //contactNumbers.toArray(contactNumArray);
                      // allNumbers.add(contactNumArray);
                   }
                    }catch (CursorIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    ver.close();

                }


            }
            cursor.close();

        }

        return new JSONArray(contactNumbers);
    }
    public Map ReadPhoneContacts() //This Context parameter is nothing but your Activity class's Context
    {
        String id;
        String contactName;
        String version;


        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Integer contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list
        Map<String, String[]> result = new HashMap<>();
        if (contactsCount > 0)
        {
            while(cursor.moveToNext())
            {
                LinkedHashSet<String> contactNumbers = new LinkedHashSet<>();
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                   // Log.e("While Loop",contactName);
                    contactNumbers.add(id);
                    Cursor ver = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, new String[]{ContactsContract.RawContacts.VERSION}, ContactsContract.RawContacts.CONTACT_ID + "=?", new String[]{id}, null);
                    try {
                    ver.moveToNext();
                    version = ver.getString(ver.getColumnIndex(ContactsContract.RawContacts.VERSION));
                    //Log.e("While Loop",version);
                    contactNumbers.add(version);
                }catch (CursorIndexOutOfBoundsException e){
                       Log.e("Contact Name",contactName);
                        contactNumbers.add("0");
                    }
                    ver.close();
                    //the below cursor will give you details for multiple contacts
                    Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list

                    while (pCursor.moveToNext())
                    {
                        String phoneNo 	= pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactNumbers.add(phoneNo.replace("-","").replace(")","").replace("(","").replace(" ",""));
                    }
                    pCursor.close();
                    String[] contactNumArray = new String[contactNumbers.size()];
                    contactNumbers.toArray(contactNumArray);
                    result.put(contactName, contactNumArray);
                }


            }
            cursor.close();

        }
        return result;
    }

}
