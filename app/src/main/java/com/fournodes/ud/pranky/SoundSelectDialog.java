package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created by Usman-Durrani on 10-Nov-15.
 */
public class SoundSelectDialog {
    private Context context;
    private Dialog dialog;



    public SoundSelectDialog(Context context) {
        this.context=context;
    }

    public void show(){


        dialog = new Dialog(context, R.style.ClockDialog);
        dialog.setContentView(R.layout.dialog_soundselect);
        ImageView btnselsound = (ImageView) dialog.findViewById(R.id.btnSelSound);
        ImageView btnselpic = (ImageView) dialog.findViewById(R.id.btnSelPic);
        ImageView btndiagclose = (ImageView) dialog.findViewById(R.id.btnDiagClose);
        ImageView btnsoundset = (ImageView) dialog.findViewById(R.id.btnSoundSet);



        btndiagclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        btnselsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

        btnselpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

        btnsoundset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



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
