package com.fournodes.ud.pranky;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        List<String[]> allNumbers = new ArrayList<>();
        Integer contactsCount = cursor.getCount();
        if (contactsCount > 0)
        {
            while(cursor.moveToNext())
            {
                ArrayList<String> contactNumbers = new ArrayList<>();
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    Cursor ver =  context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, new String[] {ContactsContract.RawContacts.VERSION}, ContactsContract.RawContacts.CONTACT_ID+"=?", new String[] {id}, null);
                    try {
                        ver.moveToNext();
                        version = ver.getString(ver.getColumnIndex(ContactsContract.RawContacts.VERSION));

                   if (prankyDB.isUpdated(id,version)){

                       Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                               new String[]{id}, null);
                       // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list
                       while (pCursor.moveToNext())
                       {
                           // int phoneType 		= pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                           //String isStarred 		= pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));
                           String phoneNo 	= pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("-","").replace(")","").replace("(","").replace(" ","");
                           // Log.e("Inner Loop",phoneNo);

                           //you will get all phone numbers according to it's type as below switch case.
                           //Logs.e will print the phone number along with the name in DDMS. you can use these details where ever you want.
                           contactNumbers.add(phoneNo);
                       }
                       pCursor.close();
                       String[] contactNumArray = new String[contactNumbers.size()];
                       contactNumbers.toArray(contactNumArray);
                       allNumbers.add(contactNumArray);
                   }
                    }catch (CursorIndexOutOfBoundsException e){e.printStackTrace();}
                    ver.close();

                }


            }
            cursor.close();

        }

        return new JSONArray(allNumbers);
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
                ArrayList<String> contactNumbers = new ArrayList<>();
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
                    // Log.e("While Loop",version);
                    contactNumbers.add(version);
                }catch (CursorIndexOutOfBoundsException e){
                        Log.e("Contact Name",contactName);}
                    ver.close();
                    //the below cursor will give you details for multiple contacts
                    Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list
                    while (pCursor.moveToNext())
                    {
                        // int phoneType 		= pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        //String isStarred 		= pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));
                        String phoneNo 	= pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("-","").replace(")","").replace("(","").replace(" ","");

                       // Log.e("Inner Loop",phoneNo);

                        //you will get all phone numbers according to it's type as below switch case.
                        //Logs.e will print the phone number along with the name in DDMS. you can use these details where ever you want.
                       contactNumbers.add(phoneNo);
                    }
                    pCursor.close();
                    String[] contactNumArray = new String[contactNumbers.size()];
                    contactNumbers.toArray(contactNumArray);
                    result.put(contactName, contactNumArray);
                }


            }
            cursor.close();
           // ver.close();

        }
        //Log.e("ayy map: ", result.toString());
        return result;
    }

}
