package com.fournodes.ud.pranky.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.utils.FontManager;

/**
 * Created by Usman on 11/21/2015.
 */
public class CustomTextView extends TextView {


    public CustomTextView(Context context) {
        super(context);


        setTypeface(FontManager.getTypeFace(context, SharedPrefs.DEFAULT_FONT));
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(FontManager.getTypeFace(context, SharedPrefs.DEFAULT_FONT));

    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(FontManager.getTypeFace(context, SharedPrefs.DEFAULT_FONT));

    }


    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }
}
