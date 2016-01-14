package com.fournodes.ud.pranky.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;

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
            builderSingle.show();

        }

        else {
            CustomToast cToast = new CustomToast(context,"Nothing  to  show");
            cToast.show();
        }
    }
}
