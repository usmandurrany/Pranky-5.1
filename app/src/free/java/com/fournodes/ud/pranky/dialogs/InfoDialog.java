package com.fournodes.ud.pranky.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fournodes.ud.pranky.R;

/**
 * Created by Usman on 12/30/2015.
 */
public class InfoDialog {

    private Context context;
    private Dialog dialog;

    public InfoDialog(Context context) {
        this.context = context;
    }


    public void show() {


        dialog = new Dialog(context, R.style.ClockDialog);
        dialog.setContentView(R.layout.dialog_info);

        ImageView close = (ImageView) dialog.findViewById(R.id.btnClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
