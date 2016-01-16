package com.fournodes.ud.pranky.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.fournodes.ud.pranky.AppDB;
import com.fournodes.ud.pranky.ContactDetails;
import com.fournodes.ud.pranky.CustomToast;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.enums.ActionType;
import com.fournodes.ud.pranky.network.AppServerConn;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Usman on 12/1/2016.
 */
public class DisplayContactsDialog {

    ContactDetails[] contArr;
    Context context;
    ArrayList<ContactDetails> contList;
    Dialog dialog;
    int conNamePOS;



    public DisplayContactsDialog(Context context){
    this.context=context;

    }

    public void show() {
        AppDB prankyDB = new AppDB(context);
        contList = prankyDB.getAllContacts();
        // contArr = new ContactDetails[contList.size()];
        // contList.toArray(contArr);
        String[] names = new String[contList.size()];
        for (int i = 0; i < contList.size(); i++) {
            names[i] = contList.get(i).getName();
        }
        if (names.length > 0) {





            dialog = new Dialog(context, R.style.ClockDialog);
            dialog.setContentView(R.layout.dialog_contact_names);

            ImageView close = (ImageView) dialog.findViewById(R.id.close);
            ListView lstContacts =  (ListView) dialog.findViewById(R.id.lstConNames);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            final ArrayAdapter<String> conAdapter = new ArrayAdapter<String>(context,
                    R.layout.contacts_row, names);
            lstContacts.setAdapter(conAdapter);

            lstContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int conNamePOS, long l) {
                    DisplayContactsDialog.this.conNamePOS = conNamePOS;
                    Log.e("ID:", String.valueOf(contList.get(conNamePOS).getId()));
                    Log.e("Name:", contList.get(conNamePOS).getName());
                    Log.e("Numbers:", Arrays.toString(contList.get(conNamePOS).getRegNumbers()));
                    Log.e("NumIDs:", Arrays.toString(contList.get(conNamePOS).getNumIDs()));

                    if (contList.get(conNamePOS).getRegNumbers().length > 1) {

                        Dialog conNumDiag = new Dialog(context, R.style.ClockDialog);
                        conNumDiag.setContentView(R.layout.dialog_contact_numbers);
                        ListView lstConNumbers = (ListView) conNumDiag.findViewById(R.id.lstConNumbers);
                        final ArrayAdapter<String> conNumAdapter = new ArrayAdapter<String>(context,
                                R.layout.contacts_row, contList.get(conNamePOS).getRegNumbers());
                        lstConNumbers.setAdapter(conNumAdapter);
                        lstConNumbers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int conNumPOS, long l) {
                                Log.e("Numbers:", contList.get(DisplayContactsDialog.this.conNamePOS).getRegNumbers()[conNumPOS]);
                                AppServerConn appServerConn = new AppServerConn(context, ActionType.GET_FRIEND_APP_ID, contList.get(DisplayContactsDialog.this.conNamePOS).getNumIDs()[conNumPOS], contList.get(DisplayContactsDialog.this.conNamePOS).getRegNumbers()[conNumPOS]);
                                appServerConn.showWaitDialog("Fetching ID ...");
                                appServerConn.execute();
                            }
                        });

                    }else{
                        AppServerConn appServerConn = new AppServerConn(context, ActionType.GET_FRIEND_APP_ID, contList.get(conNamePOS).getNumIDs()[0], contList.get(conNamePOS).getRegNumbers()[0]);
                        appServerConn.execute();
                    }
                }
            });

            //Set the dialog to not focusable (makes navigation ignore us adding the window)
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            //Show the dialog!
            dialog.show();

            //Set the dialog to immersive
            dialog.getWindow().getDecorView().setSystemUiVisibility(
                    ((Activity) context).getWindow().getDecorView().getSystemUiVisibility());

            //Clear the not focusable flag from the window
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

























/*
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
            builderSingle.setTitle("Select a friend to prank");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1, names);

            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int pos) {
                            // String strName = arrayAdapter.getItem(which);

                            Log.e("ID:", String.valueOf(contList.get(pos).getId()));
                            Log.e("Name:", contList.get(pos).getName());
                            Log.e("Numbers:", Arrays.toString(contList.get(pos).getRegNumbers()));
                            Log.e("NumIDs:", Arrays.toString(contList.get(pos).getNumIDs()));

                            if (contList.get(pos).getRegNumbers().length > 1) {
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom)).setItems(contList.get(pos).getRegNumbers(), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int i) {
                                        // The 'which' argument contains the index position
                                        // of the selected item

                                        Log.e("Numbers:", contList.get(pos).getRegNumbers()[i]);
                                        AppServerConn appServerConn = new AppServerConn(context, ActionType.GET_FRIEND_APP_ID, contList.get(pos).getNumIDs()[i], contList.get(pos).getRegNumbers()[i]);
                                        appServerConn.showWaitDialog("Fetching ID ...");
                                        appServerConn.execute();

                                    }
                                });
                                builderInner.setTitle("Which number?");
                                builderInner.show();
                            }else{
                                AppServerConn appServerConn = new AppServerConn(context, ActionType.GET_FRIEND_APP_ID, contList.get(pos).getNumIDs()[0], contList.get(pos).getRegNumbers()[0]);
                                appServerConn.execute();
                            }
                        }
                    });
            builderSingle.show();*/

        }

        else {
            CustomToast cToast = new CustomToast(context,"Nothing  to  show");
            cToast.show();
        }
    }
}
