package com.fournodes.ud.pranky.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fournodes.ud.pranky.CustomTextView;
import com.fournodes.ud.pranky.R;

/**
 * Created by Usman-Durrani on 28-Dec-15.
 */
public class WaitDialog {
    Context context;
    Dialog dialog;

    public WaitDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.ClockDialog);
        dialog.setContentView(R.layout.dialog_wait);
    }


    public void show() {


        ImageView wait = (ImageView) dialog.findViewById(R.id.imgWait);
        AnimationDrawable boxsel = (AnimationDrawable) wait.getDrawable();
        boxsel.start();


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
    public void dismiss(){
        dialog.dismiss();
    }

    public void setWaitText(String s){
        CustomTextView waitText = (CustomTextView) dialog.findViewById(R.id.waitText);
        waitText.setText(s);
    }

    public void setCancelable(Boolean cancelable){
        dialog.setCancelable(cancelable);
    }

}
