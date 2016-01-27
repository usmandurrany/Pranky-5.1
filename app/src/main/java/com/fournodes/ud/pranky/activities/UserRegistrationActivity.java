package com.fournodes.ud.pranky.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.fournodes.ud.pranky.BackgroundMusic;
import com.fournodes.ud.pranky.Country;
import com.fournodes.ud.pranky.CustomEditText;
import com.fournodes.ud.pranky.CustomToast;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.adapters.CountryAdapter;
import com.fournodes.ud.pranky.dialogs.WaitDialog;
import com.fournodes.ud.pranky.enums.ActionType;
import com.fournodes.ud.pranky.enums.ClassType;
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.gcm.GCMRegistrationService;
import com.fournodes.ud.pranky.interfaces.OnCompleteListener;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.fournodes.ud.pranky.network.ContactsAsync;
import com.fournodes.ud.pranky.services.MonitorContacts;

import java.util.ArrayList;
import java.util.Arrays;

public class UserRegistrationActivity extends Activity implements View.OnKeyListener, OnCompleteListener{

    private View decorView;
    private CustomEditText name;
    private AutoCompleteTextView country;
    private EditText countryCode;
    private CustomEditText number;
    private ImageView btnDone;
    private ImageView btnSkip;


    private String[] ctyXmlArray;

    private AppServerConn appServerConn;

    private WaitDialog wait;
    private int userCountryAtIndex;
    private String userCountryShortCode;
    private boolean listItemClicked;

    ContactsAsync syncContacts;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            switch (Message.valueOf(intent.getStringExtra("Broadcast"))) {
                case UserRegistered:
                    Intent main = new Intent(UserRegistrationActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                    break;


                case NetworkError: {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Network  or server unavailable ");
                    cToast.show();
                    break;
                }
                case TokenGenerated:
                    // GCM id has been generated
                    wait.dismiss();
                    registerUser();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registeration);
        onWindowFocusChanged(true);

        syncContacts = new ContactsAsync(this);
        syncContacts.delegate=this;

        ctyXmlArray = getResources().getStringArray(R.array.countries);

        final ArrayList<Country> countryList = new ArrayList<>();

        final TelephonyManager teleMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (teleMgr != null){
            userCountryShortCode = teleMgr.getSimCountryIso();
            if (userCountryShortCode ==null || ("").equals(userCountryShortCode)){
                userCountryShortCode = teleMgr.getNetworkCountryIso();
            }
             Log.e("Country Code SIM", teleMgr.getSimCountryIso());
            Log.e("Country Code NWK", teleMgr.getNetworkCountryIso());
        }


        int i = 0;
        for (String value : ctyXmlArray) {

            String[] countryDetails = value.split(",");
            Log.e("country", Arrays.toString(countryDetails));
            if (countryDetails[0].equals(userCountryShortCode))
                userCountryAtIndex=i;
            countryList.add(new Country(i,countryDetails[0],countryDetails[1],countryDetails[2]));
            i++;
        }




        final CountryAdapter cAdapter = new CountryAdapter(this,R.layout.spinner_row,countryList);
        int color = Color.parseColor("#f27d13");
        name = (CustomEditText) findViewById(R.id.usrName);
        name.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        name.setOnKeyListener(this);
        country = (AutoCompleteTextView) findViewById(R.id.usrCountry);
        country.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        country.setOnKeyListener(this);
        country.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                onWindowFocusChanged(true);
                if (!view.hasFocus() && country.getText().length()>0 && !listItemClicked) {
                    if (cAdapter.getFilterResultSize() >1 || cAdapter.getFilterResultSize() == 0)
                        countryCode.setText(null);
                    else{
                        countryCode.setText(cAdapter.getItem(0).getCountryCode());
                        country.setText(cAdapter.getItem(0).getCountryName());
                    }
                }else if(!view.hasFocus() && country.getText().length()>0){
                    listItemClicked = false;
                }
            }
        });
        countryCode = (EditText) findViewById(R.id.countryCode);
        countryCode.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        //countryCode.setOnKeyListener(this);
        countryCode.setEnabled(false);
        number = (CustomEditText) findViewById(R.id.usrNumber);
        number.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        number.setOnKeyListener(this);
        btnDone = (ImageView) findViewById(R.id.signUp);
        btnSkip = (ImageView) findViewById(R.id.skip);


        //Log.e("Country Array", Arrays.toString(ccArray));

        //ArrayAdapter<String> ccAdapter = new  ArrayAdapter<>(this,R.layout.spinner_row,R.id.countryCode,cArray);
        country.setThreshold(1);
        country.setAdapter(cAdapter);
        country.setText(cAdapter.getCountryName(userCountryAtIndex));
        countryCode.setText(cAdapter.getItem(userCountryAtIndex).getCountryCode());


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.setSignUpSkipped(true);
                if(SharedPrefs.isAppFirstLaunch())
                {startActivity(new Intent(UserRegistrationActivity.this, MainActivity.class));}
                finish();

            }
        });

        country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
/*                Country data = cAdapter.getItem(pos);
                int realPosition = countryList.indexOf(data);

                Log.e("Item real pos",String.valueOf(realPosition));*/
                listItemClicked=true;
                countryCode.setText(cAdapter.getCountryCode(pos));
               number.requestFocus();
            }
        });


        btnDone.setEnabled(false);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    SharedPrefs.setSignUpComplete(true);
                    SharedPrefs.setUserName(name.getText().toString());
                    SharedPrefs.setUserCountry(country.getText().toString());
                    SharedPrefs.setUserCountryCode(countryCode.getText().toString());
                    SharedPrefs.setUserPhoneNumber(number.getText().toString());

                    /******************************** Contacts sync testing code **********************************/

                if(!SharedPrefs.isSentGcmIDToServer()) {
                    wait= new WaitDialog(UserRegistrationActivity.this);
                    wait.setWaitText("P l e a s e   W a i t ...");
                    wait.show();
                    UserRegistrationActivity.this.startService(new Intent(
                            UserRegistrationActivity.this,GCMRegistrationService.class)
                            .putExtra(String.valueOf(ActionType.Callback), String.valueOf(ClassType.UserRegistrationActivity)));
                }
                else
                 registerUser();

            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Log.e("focus","changed");

        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        if (SharedPrefs.prefs == null)
            SharedPrefs.setContext(this);
        super.onResume();
        try {
            if (BackgroundMusic.mp != null) {
                BackgroundMusic.play();
            }
        } catch (Exception e) {
            Log.e("BG Music Resume", e.toString());
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(String.valueOf(ClassType.UserRegistrationActivity)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        try {
            if (BackgroundMusic.mp != null) {
                BackgroundMusic.pause();
            }
        } catch (Exception e) {
            Log.e("BG Music Resume", e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (name.getText().length() > 0
                && country.getText().length() > 0
                && countryCode.getText().length() > 0
                && number.getText().length() == 10) {

            btnDone.setEnabled(true);
        }else{
            btnDone.setEnabled(false);
        }
        return false;
    }

    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_form_top);
        super.finish();
    }


    @Override
    public void onComplete() {
        if (SharedPrefs.isAppFirstLaunch()){
            startActivity(new Intent(UserRegistrationActivity.this, MainActivity.class));
            ContactsAsync conSync = new ContactsAsync(UserRegistrationActivity.this);
            conSync.execute();
            finish();
        }else {
            wait = new WaitDialog(UserRegistrationActivity.this);
            wait.setWaitText("S y n c i n g   C o n t a c t s ...");
            wait.show();
            ContactsAsync conSync = new ContactsAsync(UserRegistrationActivity.this);
            conSync.delegate = UserRegistrationActivity.this;
            conSync.execute();
        }
    }

    @Override
    public void conSyncComplete() {
        wait.dismiss();
        startService(new Intent(UserRegistrationActivity.this, MonitorContacts.class));
        finish();
    }

    public void registerUser(){
        appServerConn = new AppServerConn(UserRegistrationActivity.this, ActionType.RegisterUser);
        appServerConn.delegate=this;
        appServerConn.showWaitDialog("R e g i s t e r i n g ...");
        appServerConn.execute();
    }

}
