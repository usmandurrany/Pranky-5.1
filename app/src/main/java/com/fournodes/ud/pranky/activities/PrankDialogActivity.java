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
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.Tutorial;
import com.fournodes.ud.pranky.dialogs.DisplayContactsDialog;
import com.fournodes.ud.pranky.enums.Action;
import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.enums.Message;
import com.fournodes.ud.pranky.network.AppServerConn;
import com.fournodes.ud.pranky.receivers.InvalidIDTimeout;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by Usman on 11/6/2015.
 */
public class PrankDialogActivity extends Activity {

    private View decorView;
    private EditText frndID;
    private AppServerConn appServerConn;
    private int invIDCount = 0;
    private Tutorial mTutorial;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            switch (Message.valueOf(intent.getStringExtra(String.valueOf(Action.Broadcast)))) {
                case ValidId: {
                    SharedPrefs.setFrndAppID(frndID.getText().toString());
                    finish();
                    break;
                }
                case UserUnavailable: {
                    CustomToast cToast = new CustomToast(PrankDialogActivity.this, getString(R.string.toast_friend_unavailable));
                    cToast.show();
                    break;
                }
                case RetrievedFriendId: {
                    frndID.setText(SharedPrefs.getFrndAppID());
                    finish();
                    break;
                }
                case NetworkError: {
                    CustomToast cToast = new CustomToast(PrankDialogActivity.this, getString(R.string.toast_network_unavailable));
                    cToast.show();
                    break;
                }
                case InvalidId: {
                    if (invIDCount == 3) {
                        SharedPrefs.setInvalidIDCount(invIDCount);

                        Intent timeout = new Intent(PrankDialogActivity.this, InvalidIDTimeout.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                                timeout, PendingIntent.FLAG_ONE_SHOT);

                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, pendingIntent);
                        CustomToast cToast = new CustomToast(getApplicationContext(), getString(R.string.toast_friend_id_timeout));
                        cToast.show();
                        finish();
                        Log.e("Invalid ID Count", String.valueOf(invIDCount));

                    }
                    CustomToast cToast = new CustomToast(getApplicationContext(), getString(R.string.toast_invalid_id));
                    cToast.show();
                    invIDCount++;
                    break;
                }

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_prank);

        Log.e("Prank Dialog", "Created");


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
                } else {
                    startActivity(new Intent(PrankDialogActivity.this, UserRegistrationActivity.class));
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
                if (frndID.getText().toString().trim().length() == 4 && mTutorial != null) {
                    mTutorial.moveToNext(new ViewTarget(btnset), getString(R.string.tut_prank_friend_title), getString(R.string.tut_prank_friend_desc));
                }
                return false;
            }
        });
        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frndID.getText().toString().trim().length() > 0) {
                    if (mTutorial != null) {
                        mTutorial.end();

                    }
                    appServerConn = new AppServerConn(PrankDialogActivity.this, Action.ValidateId, frndID.getText().toString());
                    appServerConn.showWaitDialog(getString(R.string.paring_spaced));
                    appServerConn.execute();
                } else {
                    CustomToast cToast = new CustomToast(PrankDialogActivity.this, getString(R.string.toast_enter_friend_id));
                    cToast.show();
                }

            }
        });
        if (SharedPrefs.isRemotePrankFirstLaunch()) {
            mTutorial = new Tutorial(this, Type.PrankDialogActivity);
            mTutorial.show(new ViewTarget(frndID), getString(R.string.tut_prank_friend_title), getString(R.string.tut_prank_friend_desc2));
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
                mMessageReceiver, new IntentFilter(String.valueOf(Type.PrankDialogActivity))
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPrefs.setBgMusicPlaying(false);
        System.gc();

    }
}
