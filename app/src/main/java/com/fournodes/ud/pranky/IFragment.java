package com.fournodes.ud.pranky;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Usman on 11/10/2015.
 */
public interface IFragment {
    public void pageScrolled();
    public void pageLast();

    public void TutImageClick();

    public void TutImageTouch(View view, MotionEvent motionEvent);
}
