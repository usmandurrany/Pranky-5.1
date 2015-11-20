package com.fournodes.ud.pranky;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Usman on 11/20/2015.
 */
public class CustomToast {
    Context context;
    String ToastText;

    LayoutInflater inflater;
    View customToast;
    TextView toastText;
    Toast toast;

    public CustomToast(Context context, String toastText) {
        this.context = context;
        ToastText = toastText;

  }

    public void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        customToast = inflater.inflate(R.layout.toast,null);
        toastText = (TextView) customToast.findViewById(R.id.toastText);
        toastText.setText(ToastText);

        toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 220);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(customToast);
    }

public void show(){
    init();
    toast.show();


}



}