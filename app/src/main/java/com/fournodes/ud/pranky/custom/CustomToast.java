package com.fournodes.ud.pranky.custom;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fournodes.ud.pranky.R;

/**
 * Created by Usman on 11/20/2015.
 */
public class CustomToast {
    Context context;
    String ToastText;

    View customToast;
    TextView toastText;
    Toast toast;

    public CustomToast(Context context, String toastText) {
        this.context = context;
        ToastText = toastText;

    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        customToast = inflater.inflate(R.layout.custom_toast, null);
        toastText = (TextView) customToast.findViewById(R.id.toastText);
        toastText.setText(ToastText);
        toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, dipsToPixels(75));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(customToast);
    }

    private int dipsToPixels(int dips) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dips * scale + 0.5f);
    }

    public void show() {
        init();
        toast.show();


    }


}
