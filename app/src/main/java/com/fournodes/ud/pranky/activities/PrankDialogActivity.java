package com.fournodes.ud.pranky.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.fournodes.ud.pranky.BackgroundMusic;
import com.fournodes.ud.pranky.CustomToast;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.Selection;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.dialogs.DisplayContactsDialog;
import com.fournodes.ud.pranky.enums.ActionType;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.fournodes.ud.pranky.receivers.InvalidIDTimeout;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by Usman on 11/6/2015.
 */
public class PrankDialogActivity extends Activity{

    private View decorView;
    private ShowcaseView showcaseView;
    private EditText frndID;
    private AppServerConn appServerConn;
    private int invIDCount=0;



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            if (message.equals("valid-id")){
                SharedPrefs.setFrndAppID(frndID.getText().toString());
                AppServerConn appServerConn= new AppServerConn(PrankDialogActivity.this, ActionType.PRANK);
                appServerConn.showWaitDialog("P r a n k i n g ...");
                appServerConn.execute();
                finish();
            }else if (message.equals("not-prankable")){
                CustomToast cToast = new CustomToast(PrankDialogActivity.this, "Your friend is unavailable");
                cToast.show();

            }else if (message.equals("fetch-id")){
                frndID.setText(SharedPrefs.getFrndAppID());
                if (Selection.itemSound == -1 && Selection.itemCustomSound == null) {
                    CustomToast cToast = new CustomToast(PrankDialogActivity.this, "Select  a  sound  first");
                    cToast.show();
                }else{
                    AppServerConn appServerConn= new AppServerConn(PrankDialogActivity.this, ActionType.PRANK);
                    appServerConn.showWaitDialog("P r a n k i n g ...");
                    appServerConn.execute();
                }
                finish();

            }
            else if (message.equals("network-error")) {
                CustomToast cToast = new CustomToast(PrankDialogActivity.this, "Network or server unavailable");
                cToast.show();
            }
            else if (message.equals("invalid-id")){
                if (invIDCount==3){
                    SharedPrefs.setInvalidIDCount(invIDCount);

                    Intent timeout = new Intent(PrankDialogActivity.this,InvalidIDTimeout.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,
                            timeout, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, pendingIntent);
                    CustomToast cToast = new CustomToast(getApplicationContext(), "Please wait 60 seconds before trying again");
                    cToast.show();
                    finish();
                    Log.e("Invalid ID Count",String.valueOf(invIDCount));

                }
                CustomToast cToast = new CustomToast(getApplicationContext(), "Invalid ID");
                cToast.show();
                invIDCount++;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_prank);

        Log.e("Prank Dialog","Created");


        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        ImageView btndiagclose = (ImageView) findViewById(R.id.close);
        final ImageView btnset = (ImageView) findViewById(R.id.set);
        frndID = (EditText) findViewById(R.id.txtfrndID);
        ImageView btnShowContacts = (ImageView) findViewById(R.id.btnShowContacts);
        btnShowContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (SharedPrefs.isSignUpComplete()) {
                    DisplayContactsDialog diag = new DisplayContactsDialog(PrankDialogActivity.this);
                    diag.show();
                }else{
                    startActivity(new Intent(PrankDialogActivity.this,UserRegisterationActivity.class));
                    overridePendingTransition(R.anim.slide_in_form_top, R.anim.fade_out);
                }
            }
        });
        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        frndID.setText(SharedPrefs.getFrndAppID());
        frndID.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (frndID.getText().toString().trim().length() == 4 && showcaseView!=null){
                    showcaseView.setShowcase(new ViewTarget(btnset),true);
                    showcaseView.setStyle(R.style.CustomShowcaseTheme3);
                }
                return false;
            }
        });
        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frndID.getText().toString().trim().length() > 0){
                    if (showcaseView != null){
                        showcaseView.hide();
                        showcaseView=null;
                        SharedPrefs.setTimerFirstLaunch(false);

                    }
                    appServerConn = new AppServerConn(PrankDialogActivity.this, ActionType.DEVICE_VALIDATE,frndID.getText().toString());
                    appServerConn.showWaitDialog("P a i r i n g ...");
                    appServerConn.execute();
                }else{
                    CustomToast cToast = new CustomToast(PrankDialogActivity.this, "Enter friends ID");
                    cToast.show();
                }

            }
        });
        if (SharedPrefs.isRemotePrankFirstLaunch()) {
            ViewTarget target = new ViewTarget(frndID);
            showcaseView = new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(target)
                    .setContentTitle("Prank a friend")
                    .setContentText("Enter your 'Friend's ID' found in the settings popup of your friends phone")
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .hideOnTouchOutside()
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showcaseView.hide();
                            showcaseView=null;
                            SharedPrefs.setRemotePrankFirstLaunch(false);
                        }
                    })
                    .build();
        }

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
    protected void onPause() {
        super.onPause();
        try {
            if (BackgroundMusic.mp != null) {
                BackgroundMusic.pause();
            }
        } catch (Exception e) {
            Log.e("BG Music Pause", e.toString());
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

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
            Log.e("BG Music Pause", e.toString());
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("prank-dialog-activity-broadcast"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefs.setBgMusicPlaying(false);
        System.gc();

    }
}
