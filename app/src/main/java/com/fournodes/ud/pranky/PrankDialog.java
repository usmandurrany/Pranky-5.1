package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import static com.fournodes.ud.pranky.AppBGMusic.getInstance;

/**
 * Created by Usman on 11/6/2015.
 */
public class PrankDialog {
    private Context context;
    private Dialog dialog;



    public PrankDialog(Context context) {
        this.context = context;
    }

    public void show() {


        dialog = new Dialog(context, R.style.ClockDialog);
        dialog.setContentView(R.layout.dialog_prank);
        ImageView btndiagclose = (ImageView) dialog.findViewById(R.id.close);
        ImageView btnset = (ImageView) dialog.findViewById(R.id.set);
        final EditText frndID = (EditText) dialog.findViewById(R.id.txtfrndID);
        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final SharedPreferences prefs = context.getSharedPreferences(SharedPrefs.SHARED_PREF_FILE,0);
        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frndID.getText()!= null){
                    prefs.edit().putString(SharedPrefs.FRIENDS_ID,frndID.getText().toString()).apply();
                    dialog.dismiss();
                }
                else{
                    CustomToast cToast = new CustomToast(context, "Enter friends ID");
                    cToast.show();
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



}
