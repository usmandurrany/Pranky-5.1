package com.fournodes.ud.pranky;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class UserRegisterationActivity extends AppCompatActivity {

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registeration);
        onWindowFocusChanged(true);
        final AutoCompleteTextView country = (AutoCompleteTextView) findViewById(R.id.usrCountry);
        ImageView signupfinish = (ImageView) findViewById(R.id.signUp);
        final EditText countryCode = (EditText) findViewById(R.id.countryCode);
        final EditText number = (EditText) findViewById(R.id.usrNumber);

        final String[] countries = getResources().getStringArray(R.array.countries);
        final String[] cc = getResources().getStringArray(R.array.cc);


        final String[] cCodes = getResources().getStringArray(R.array.countries);
        ArrayAdapter<String> ccAdapter = new  ArrayAdapter<>(this,R.layout.spinner_row,R.id.countryCode,countries);
        //ccAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        country.setThreshold(1);
        country.setAdapter(ccAdapter);

        country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                for(int i=0;i<countries.length;i++)
                {
                    if (cc[i].contains(country.getText())){
                        String[] temp = cc[i].split(",");
                        countryCode.setText(temp[1]);
                        break;
                    }
                }
               number.requestFocus();
            }
        });



        signupfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(UserRegisterationActivity.this, MainActivity.class);
                startActivity(main);
                finish();
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
}
