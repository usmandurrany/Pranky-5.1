package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Usman on 11/6/2015.
 */
public class PrankDialog{
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

        frndID.setText(SharedPrefs.getFrndAppID());
        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frndID.getText()!= null){
                    new DeviceValidation(new DeviceValidation.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            if (output.equals("1"))
                            {
                                SharedPrefs.setFrndAppID(frndID.getText().toString());
                                dialog.dismiss();
                            }else if (output.equals("0")){
                                CustomToast cToast = new CustomToast(context, "Your friend is not prankable at the moment");
                                cToast.show();
                            }else if (output.equals("-10")){ //Server Unreachable
                                CustomToast cToast = new CustomToast(context, "Can't connect to server");
                                cToast.show();

                            }else if (output.equals("-20")){//Network Unavailable
                                CustomToast cToast = new CustomToast(context, "Network Unavailable");
                                cToast.show();
                            }
                            else{
                                CustomToast cToast = new CustomToast(context, "Invalid ID");
                                cToast.show();
                            }
                        }
                    }).execute(frndID.getText().toString());


                   }else{
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
