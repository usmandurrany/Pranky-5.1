package com.fournodes.ud.pranky;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Usman on 11/19/2015.
 */
public class CustomViewPager extends ViewPager {


    OnSwipeOutListener mListener;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mListener.onSwipeOutAtEnd(ev.getX());
        }

        return super.onTouchEvent(ev);
    }


    public interface OnSwipeOutListener {
        public void onSwipeOutAtEnd(float x);
    }
}