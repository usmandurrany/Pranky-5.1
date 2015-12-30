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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by Usman on 11/6/2015.
 */
public class PrankDialog extends Activity{
    private Context context;
    private Dialog dialog;
    View decorView;
    ShowcaseView showcaseView;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            if (message.equals("not-prankable")){
                CustomToast cToast = new CustomToast(PrankDialog.this, "Your friend is not prankable at the moment");
                cToast.show();

            } else if (message.equals("server-unreachable")){
                CustomToast cToast = new CustomToast(PrankDialog.this, "Can't connect to server");
                cToast.show();

            } else if (message.equals("network-unavailable")) {
                CustomToast cToast = new CustomToast(PrankDialog.this, "Network Unavailable");
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
        setContentView(R.layout.dialog_prank);

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
        final EditText frndID = (EditText) findViewById(R.id.txtfrndID);
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
                    new DeviceValidation(new DeviceValidation.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            Intent intent = new Intent("prank-dialog-activity-broadcast");
                            if (output.equals("1"))
                            {
                                intent.putExtra("message", "default");
                                SharedPrefs.setFrndAppID(frndID.getText().toString());
                                finish();
                            }else if (output.equals("0")){

                                // You can also include some extra data.
                                intent.putExtra("message", "not-prankable");


                            }else if (output.equals("-10")){ //Server Unreachable
                                // You can also include some extra data.
                                intent.putExtra("message", "server-unreachable");


                            }else if (output.equals("-20")){//Network Unavailable
                                intent.putExtra("message", "network-unavailable");



                            }
                            else{

                                intent.putExtra("message", "invalid-id");


                            }
                            LocalBroadcastManager.getInstance(PrankDialog.this).sendBroadcast(intent);
                        }
                    }).execute(frndID.getText().toString());


                }else{
                    CustomToast cToast = new CustomToast(PrankDialog.this, "Enter friends ID");
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
                    .setContentText("Enter your 'Frieend's ID' found in the settings popup of your friends phone")
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
