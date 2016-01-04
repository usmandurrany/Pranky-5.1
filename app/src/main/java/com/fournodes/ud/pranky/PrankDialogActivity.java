package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by Usman on 11/6/2015.
 */
public class PrankDialogActivity extends Activity{
    private Context context;
    private Dialog dialog;
    View decorView;
    ShowcaseView showcaseView;
    EditText frndID;
    private AppServerConn appServerConn;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            if (message.equals("valid-id")){
                SharedPrefs.setFrndAppID(frndID.getText().toString());
                finish();
            }else if (message.equals("not-prankable")){
                CustomToast cToast = new CustomToast(PrankDialogActivity.this, "Your friend is unavailable");
                cToast.show();

            }  else if (message.equals("network-error")) {
                CustomToast cToast = new CustomToast(PrankDialogActivity.this, "Network or server unavailable");
                cToast.show();
            }
            else if (message.equals("invalid-id")){
                CustomToast cToast = new CustomToast(getApplicationContext(), "Invalid ID");
                cToast.show();
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
        ImageView btnset = (ImageView) findViewById(R.id.set);
        frndID = (EditText) findViewById(R.id.txtfrndID);
        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        frndID.setText(SharedPrefs.getFrndAppID());
        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frndID.getText()!= null){
                    appServerConn = new AppServerConn(PrankDialogActivity.this,ActionType.DEVICE_VALIDATE,frndID.getText().toString());
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