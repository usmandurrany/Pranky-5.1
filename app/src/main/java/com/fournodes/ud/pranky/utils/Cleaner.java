package com.fournodes.ud.pranky.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

/**
 * Created by Usman on 1/2/2016.
 */
public class Cleaner {

    public Cleaner(){}

    public static void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }

        }

    }
}
