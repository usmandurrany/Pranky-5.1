package com.fournodes.ud.pranky.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.fournodes.ud.pranky.DatabaseHelper;
import com.fournodes.ud.pranky.ContactDetails;
import com.fournodes.ud.pranky.CustomToast;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.enums.ActionType;
import com.fournodes.ud.pranky.interfaces.OnCompleteListener;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.fournodes.ud.pranky.network.ContactsAsync;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Usman on 12/1/2016.
 */
public class DisplayContactsDialog implements OnCompleteListener {

    private Context context;
    private ArrayList<ContactDetails> contList;
    private Dialog dialog;
    private int conNamePOS;
    private SwipeRefreshLayout refreshList;
    private ImageView close;
    private ListView lstContacts;
    private WaitDialog wait;
    private DatabaseHelper prankyDB;
    private int curCount;
    private int newCount;

    public DisplayContactsDialog(Context context){
        this.context=context;
        prankyDB = new DatabaseHelper(context);
        curCount = prankyDB.conRegCount();

    }

    public void show() {

        dialog = new Dialog(context, R.style.ClockDialog);
        dialog.setContentView(R.layout.dialog_contact_names);
        refreshList = (SwipeRefreshLayout) dialog.findViewById(R.id.refreshList);
        refreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList.setRefreshing(false);
                wait = new WaitDialog(context);
                wait.setWaitText("R e f r e s h i n g ...");
                wait.show();
                DatabaseHelper prankyDB= new DatabaseHelper(context);
                prankyDB.nuke(DatabaseHelper.TABLE_CONTACTS);
                ContactsAsync syncContacts = new ContactsAsync(context);
                syncContacts.delegate = DisplayContactsDialog.this;
                syncContacts.execute();
            }
        });
        close = (ImageView) dialog.findViewById(R.id.close);
        lstContacts =  (ListView) dialog.findViewById(R.id.lstConNames);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




        contList = prankyDB.getAllContacts();
        // contArr = new ContactDetails[contList.size()];
        // contList.toArray(contArr);

        String[] names = new String[contList.size()];
        for (int i = 0; i < contList.size(); i++) {
            names[i] = contList.get(i).getName();
        }
        final ArrayAdapter<String> conAdapter = new ArrayAdapter<String>(context,
                    R.layout.contacts_row, names);
        lstContacts.setAdapter(conAdapter);
        lstContacts.setEmptyView(dialog.findViewById(R.id.emptyElement));


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
                                AppServerConn appServerConn = new AppServerConn(context,
                                        ActionType.RetrieveFriendId,
                                        contList.get(DisplayContactsDialog.this.conNamePOS).getNumIDs()[conNumPOS],
                                        contList.get(DisplayContactsDialog.this.conNamePOS).getRegNumbers()[conNumPOS]);
                                appServerConn.showWaitDialog("Fetching ID ...");
                                appServerConn.execute();
                            }
                        });

                    }else{
                        AppServerConn appServerConn = new AppServerConn(context,
                                ActionType.RetrieveFriendId,
                                contList.get(conNamePOS).getNumIDs()[0],
                                contList.get(conNamePOS).getRegNumbers()[0]);

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


    }

    @Override
    public void onComplete() {

    }

    @Override
    public void conSyncComplete() {
        //refreshList.setRefreshing(false);

        newCount=prankyDB.conRegCount();
        if(newCount-curCount > 0){
            CustomToast cToast = new CustomToast(context, newCount-curCount+" contacts added");
            cToast.show();
        }else if(newCount-curCount < 0){
            CustomToast cToast = new CustomToast(context, ((newCount-curCount)*(-1))+" contacts removed");
            cToast.show();
        }else{
            CustomToast cToast = new CustomToast(context, "No change");
            cToast.show();
        }


        if (wait != null)
            wait.dismiss();
        DatabaseHelper prankyDB = new DatabaseHelper(context);
        contList = prankyDB.getAllContacts();

        String[] names = new String[contList.size()];
        for (int i = 0; i < contList.size(); i++) {
            names[i] = contList.get(i).getName();
        }
        if (names.length > 0) {
            final ArrayAdapter<String> conAdapter = new ArrayAdapter<>(context,
                    R.layout.contacts_row, names);
            lstContacts.setAdapter(conAdapter);
            lstContacts.invalidate();
        }
    }
}
