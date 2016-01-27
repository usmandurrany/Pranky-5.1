package com.fournodes.ud.pranky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Usman on 11/9/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ITEM_IMG_LOC = "item_img_loc";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_ITEM_SOUND_LOC = "item_sound_loc";
    public static final String COLUMN_REPEAT_COUNT = "repeat_count";
    public static final String COLUMN_SOUND_VOL = "sound_vol";
    public static final String COLUMN_ITEM_CATEGORY = "item_category";
    public String category;


    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_NAME = "contact_name";
    public static final String COLUMN_NUMBER = "contact_number";
    public static final String COLUMN_VERSION= "contact_version";
    public static final String COLUMN_REGISTERED = "contact_registered";
    public static final String COLUMN_NUM_IDS = "server_id";


    private static final String DATABASE_NAME = "pranky.db";
    private static final int DATABASE_VERSION = 36;

    // Database creation sql statement
    private static final String CREATE_PRANK_TABLE = "create table if not exists "
            + TABLE_ITEMS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_ITEM_IMG_LOC
            + " integer not null, " + COLUMN_ITEM_NAME
            + " text not null, " + COLUMN_ITEM_SOUND_LOC
            + " text not null, " + COLUMN_REPEAT_COUNT
            + " integer not null, " + COLUMN_SOUND_VOL
            + " integer not null, "+ COLUMN_ITEM_CATEGORY
            + " text not null);";

    private static final String CREATE_CONTACTS_TABLE = "create table if not exists "
            + TABLE_CONTACTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NAME
            + " text not null, " + COLUMN_NUMBER
            + " text not null, " + COLUMN_VERSION
            + " integer default 0, " + COLUMN_REGISTERED
            + " text not null, "  + COLUMN_NUM_IDS
            + " integer default null);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
        if  (SharedPrefs.prefs == null)
            new SharedPrefs(context).initAllPrefs();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_PRANK_TABLE);
        database.execSQL(CREATE_CONTACTS_TABLE);

        InputStream in = null;
        try {
            String line;
            ContentValues values = new ContentValues();
            in = context.getAssets().open("icons.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while((line=reader.readLine()) != null){
                if (line!=null) {
                    Log.e("LINE", line);
                    String[] itemProp = line.split(",");
                    if(itemProp.length == 1) {
                        category = itemProp[0];
                        continue;
                    }
                    values.put(COLUMN_ITEM_IMG_LOC,R.mipmap.class.getField(itemProp[0]).getInt(null));
                    values.put(COLUMN_ITEM_NAME,itemProp[0]);
                    values.put(COLUMN_ITEM_SOUND_LOC,"raw."+itemProp[0]);
                    values.put(COLUMN_REPEAT_COUNT,itemProp[1]);
                    values.put(COLUMN_SOUND_VOL,itemProp[2]);
                    values.put(COLUMN_ITEM_CATEGORY,category);
                    database.insert(TABLE_ITEMS,null,values);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    /***********************************************************************************************
     * Updates the existing contact with the number sent from server if registered
     ***********************************************************************************************/

    public void storeRegisteredContact(String id,String[] numIDs, String[] number){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REGISTERED, Arrays.toString(number));
        values.put(COLUMN_NUM_IDS, Arrays.toString(numIDs));

        db.update(TABLE_CONTACTS,values,COLUMN_ID+"=?",new String[] {id});
        db.close();
    }

    /***********************************************************************************************
     * Stores all the contacts received in form of a HashMap in the database
     ***********************************************************************************************/

    public JSONArray storeContacts(HashMap<String, String[]> contacts) {

        SQLiteDatabase db = this.getWritableDatabase();
        List<String[]> allNumbers = new ArrayList<>();
        for ( Map.Entry<String, String[]> entry : contacts.entrySet()) {
            List<String> numbersOnly = new ArrayList<>();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, entry.getKey()); // Contact Name
            String[] value = entry.getValue();
            for(int i=0; i<value.length; i++){
                if (i==0){
                    values.put(COLUMN_ID,value[0]);
                }
                else if(i==1) {
                    values.put(COLUMN_VERSION, value[1]); // Contact Name
                }
                else {
                    numbersOnly.add(value[i]);
                }
            }

            // do something with key and/or tab
            String[] numbers = new String[numbersOnly.size()];
            numbersOnly.toArray(numbers);
            allNumbers.add(numbers);
            values.put(COLUMN_NUMBER, Arrays.toString(numbers)); // Contact Name
            //Log.e("123noo: ", Arrays.toString(numbers));
            values.put(COLUMN_REGISTERED, 0); // Contact Name
            //values.put(COLUMN_NUM_IDS, ); // Contact Name
                db.insert(TABLE_CONTACTS, null, values);
        }
        db.close();
        SharedPrefs.setContactsStored(true);
        return new JSONArray(allNumbers);

    }

    /***********************************************************************************************
     *  Returns an array containing all records of the table in an array with each record/contact
     *  being a JSONObject, containing its row ID and all its numbers
     ***********************************************************************************************/

    public JSONArray contactDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS,new String[] {COLUMN_ID, COLUMN_NUMBER},null,null,null,null,null);
        List<JSONObject> allContacts= new ArrayList<>();
        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                Map<String,String> contact = new HashMap<>();
                contact.put("id",cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                String[] numbers = cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)).replace("[","").replace("]","").split(",");
                for(int i=0;i<numbers.length;i++){
                    contact.put("number"+(i+1),numbers[i]);
                }
                allContacts.add(new JSONObject(contact));
            }
        }
        cursor.close();
        db.close();
        return new JSONArray(allContacts);
    }

    /***********************************************************************************************
     *  Checks for the updated contact by comparing the stored version with the received version
     *  using the recieved id to find the contact
     ***********************************************************************************************/

    public boolean isUpdated(String id, String ver){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = db.query(TABLE_CONTACTS,new String[] {COLUMN_NUMBER,COLUMN_VERSION},COLUMN_ID+"=?",new String[] {id},null,null,null);
        if (cursor.getCount() > 0 ) {
            while (cursor.moveToNext()) {
                if (!cursor.getString(cursor.getColumnIndex(COLUMN_VERSION)).equals(ver)) {
                    values.put(COLUMN_VERSION,ver);
                    db.update(TABLE_CONTACTS,values,COLUMN_ID+"=?",new String[] {id});
                    Log.e("isUpdated",id);
                    cursor.close();
                    db.close();
                    return true;
                } else {
                    cursor.close();
                    db.close();
                    return false;
                }
            }
        }
        else{
            //Possibly a new contact
            cursor.close();
            db.close();

        }
        cursor.close();
        db.close();
        return false;
    }

    /***********************************************************************************************
     * Fetches all the contacts form the DB with their IDs, Number IDs and Registered Numbers
     ***********************************************************************************************/

    public ArrayList<ContactDetails> getAllContacts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS,null,COLUMN_NUM_IDS+"!=?",new String[] {""},null,null,null);
        ArrayList<ContactDetails> allContactDetails=new ArrayList<>();
        while (cursor.moveToNext()) {
            ContactDetails contactDetail = new ContactDetails();
            contactDetail.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            contactDetail.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            contactDetail.setRegNumbers(cursor.getString(cursor.getColumnIndex(COLUMN_REGISTERED)));
            contactDetail.setNumIDs(cursor.getString(cursor.getColumnIndex(COLUMN_NUM_IDS)));
            allContactDetails.add(contactDetail);
        }
        cursor.close();
        db.close();
        return allContactDetails;
    }

    public void nuke(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, null, null);
        db.close();
    }

    public int conRegCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =  db.query(TABLE_CONTACTS,new String[] {COLUMN_REGISTERED},COLUMN_REGISTERED+" != ?",new String[] {"0"},null,null,null);
        if (cursor != null)
            return cursor.getCount();
        else
            return -1;
    }
}
