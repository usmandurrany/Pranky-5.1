package com.fournodes.ud.pranky;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class UserRegisterationActivity extends Activity {

    private View decorView;
    private EditText name;
    private AutoCompleteTextView country;
    private EditText countryCode;
    private EditText number;
    private ImageView btnDone;

    private String[] cArray;
    private String[] ccArray;

    private AppServerConn appServerConn;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            switch (message) {
                case "register-successful":
                    Intent main = new Intent(UserRegisterationActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                    break;


                case "network-error": {
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Network  or server unavailable ");
                    cToast.show();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registeration);
        onWindowFocusChanged(true);

        name = (EditText) findViewById(R.id.usrName);
        country = (AutoCompleteTextView) findViewById(R.id.usrCountry);
        countryCode = (EditText) findViewById(R.id.countryCode);
        number = (EditText) findViewById(R.id.usrNumber);
        btnDone = (ImageView) findViewById(R.id.signUp);

        cArray = getResources().getStringArray(R.array.countries);
        ccArray = getResources().getStringArray(R.array.cc);


        ArrayAdapter<String> ccAdapter = new  ArrayAdapter<>(this,R.layout.spinner_row,R.id.countryCode,cArray);
        country.setThreshold(1);
        country.setAdapter(ccAdapter);

        country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                for(int i=0;i<ccArray.length;i++)
                {
                    if (ccArray[i].contains(country.getText())){
                        String[] temp = ccArray[i].split(",");
                        countryCode.setText(temp[1]);
                        break;
                    }
                }
               number.requestFocus();
            }
        });



        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(UserRegisterationActivity.this, MainActivity.class);
                startActivity(main);
                finish();

                SharedPrefs.setUserName(name.getText().toString());
                SharedPrefs.setUserCountry(country.getText().toString());
                SharedPrefs.setUserCountryCode(countryCode.getText().toString());
                SharedPrefs.setUserPhoneNumber(number.getText().toString());


                appServerConn = new AppServerConn(UserRegisterationActivity.this, ActionType.SIGN_UP);
                appServerConn.showWaitDialog("R e g i s t r i n g ...");
                appServerConn.execute();



            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("user-register-activity-broadcast"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
