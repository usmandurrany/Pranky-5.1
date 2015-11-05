package com.fournodes.ud.pranky;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kankan.wheel.widget.StrokeTextView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;


/**
 * Created by Usman-Durrani on 03-Nov-15.
 */ public class DayWheelAdapter extends AbstractWheelTextAdapter {

    ArrayList<Date> dates;

    //An object of this class must be initialized with an array of Date type
    protected DayWheelAdapter(Context context, ArrayList<Date> dates) {
        //Pass the context and the custom layout for the text to the super method
        super(context, R.layout.wheel_item_time);
        this.dates = dates;
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        StrokeTextView weekday = (StrokeTextView) view.findViewById(R.id.time_item);

        //Format the date (Name of the day / number of the day)
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd");
        //Assign the text
        weekday.setStrokeColor(0xFF1B640D);
        weekday.setStrokeWidth(10);
        weekday.setText(dateFormat.format(dates.get(index)));

        if (index == 0) {
            //If it is the first date of the array, set the color blue
            weekday.setText("Today");
            //weekday.setTextColor(0xFF0000F0);
        }
        else{
            //If not set the color to black
            //weekday.setTextColor(0xFF111111);
        }

        return view;
    }

    @Override
    public int getItemsCount() {
        return dates.size();
    }

    @Override
    protected CharSequence getItemText(int index) {
        return "";
    }


}