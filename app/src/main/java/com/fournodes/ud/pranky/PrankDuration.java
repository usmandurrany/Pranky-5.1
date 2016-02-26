package com.fournodes.ud.pranky;

import android.app.Activity;
import android.content.Context;

import java.util.Locale;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

/**
 * Created by Usman on 26/2/2016.
 */
public class PrankDuration extends Activity {
    private ArrayWheelAdapter<String> durationAdapter;
    private AbstractWheel duration;


    public void setDurationWheel(Context context, AbstractWheel duration, int durInMillis) {
        this.duration = duration;
        int i;
        int durInSec = durInMillis / 1000;
        int firstVal = ((int) Math.ceil((double) durInSec / 5)) * 5;
        String[] values = new String[((60 - firstVal) / 5) + 1];
        values[0] = String.format(Locale.US, "%02d", firstVal);
        for (i = 1; i < values.length; i++) {
            values[i] = String.format(Locale.US, "%02d", Integer.parseInt(values[i - 1]) + 5);
        }
        durationAdapter = new ArrayWheelAdapter<>(context, values);
        //NumericWheelAdapter durationAdapter = new NumericWheelAdapter(ClockDialogActivity.this, 0, 60,"%02d");
        durationAdapter.setItemResource(R.layout.wheel_item_duration);
        durationAdapter.setItemTextResource(R.id.wheel_item);
        duration.setViewAdapter(durationAdapter);

    }


    public int getDuration() {
        return Integer.parseInt(String.valueOf(durationAdapter.getItemText(duration.getCurrentItem())));
    }
}
