package com.fournodes.ud.pranky.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.fournodes.ud.pranky.AppDB;
import com.fournodes.ud.pranky.ContactDetails;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Usman on 12/1/2016.
 */
public class DisplayContactsDialog {

    ContactDetails[] contArr;
    Context context;
    ArrayList<ContactDetails> contList;
    public DisplayContactsDialog(Context context){
    this.context=context;

    }

    public void show(){
        AppDB prankyDB = new AppDB(context);
        contList = prankyDB.getAllContacts();
       // contArr = new ContactDetails[contList.size()];
       // contList.toArray(contArr);
        String[] name = new String[contList.size()];
        for(int i=0; i<contList.size();i++) {
            name[i]= contList.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)context);
        builder.setTitle("Pick a friend to prank")
                .setItems(name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        Log.e("ID:",String.valueOf(contList.get(pos).getId()));
                        Log.e("Name:",contList.get(pos).getName());
                        Log.e("Numbers:", Arrays.toString(contList.get(pos).getNumIDs()));
                        Log.e("NumIDs:",Arrays.toString(contList.get(pos).getNumIDs()));

                    }
                });

        builder.create();
        builder.show();

    }
}
