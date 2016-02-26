package com.fournodes.ud.pranky.custom;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by Usman on 27/1/2016.
 */
public class CustomEditText extends EditText {

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CustomEditText(Context context) {
        super(context);

    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            ((Activity)getContext()).onWindowFocusChanged(true);
            //Log.e("edittext","key down");
            //dispatchKeyEvent(event);
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
