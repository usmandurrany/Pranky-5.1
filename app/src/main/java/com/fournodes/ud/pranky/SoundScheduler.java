package com.fournodes.ud.pranky;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Usman on 11/5/2015.
 */
public class SoundScheduler {
    private Context context;
    Calendar schAlarm;

    public SoundScheduler(Context context) {
        this.context=context;

    }

    public Calendar clockSchedule(int d, int h, int m,int ampm){

        schAlarm = Calendar.getInstance(TimeZone.getDefault());


    schAlarm.setTimeInMillis(System.currentTimeMillis());

    schAlarm.set(Calendar.HOUR, (h+1));
    schAlarm.set(Calendar.MINUTE, m);
    schAlarm.set(Calendar.AM_PM, ampm);
    schAlarm.set(Calendar.DAY_OF_MONTH, (schAlarm.get(Calendar.DAY_OF_MONTH) + d));
    Log.d("SCHEDULED FOR", String.valueOf(schAlarm.get(Calendar.YEAR))+" " + String.valueOf(schAlarm.get(Calendar.MONTH))+" "+ String.valueOf(schAlarm.get(Calendar.DAY_OF_MONTH))+" "+String.valueOf(schAlarm.get(Calendar.HOUR))+" "+String.valueOf(schAlarm.get(Calendar.MINUTE))+" "+String.valueOf(schAlarm.get(Calendar.AM_PM)));



        return schAlarm;




    }

    public Calendar timerSchedule(int h, int m, int s){
        schAlarm = Calendar.getInstance(TimeZone.getDefault());

        schAlarm.setTimeInMillis(System.currentTimeMillis());
        Log.e("Calendar Hour", String.valueOf(schAlarm.get(Calendar.HOUR) + h));
        schAlarm.set(Calendar.HOUR, (schAlarm.get(Calendar.HOUR) + h));
        schAlarm.set(Calendar.MINUTE, (schAlarm.get(Calendar.MINUTE)+ m));
        schAlarm.set(Calendar.SECOND, (schAlarm.get(Calendar.SECOND) + s));
        Log.e("SCHEDULED FOR", String.valueOf(schAlarm.get(Calendar.YEAR)) + " " + String.valueOf(schAlarm.get(Calendar.MONTH)) + " " + String.valueOf(schAlarm.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(schAlarm.get(Calendar.HOUR)) + " " + String.valueOf(schAlarm.get(Calendar.MINUTE)) + " " + String.valueOf(schAlarm.get(Calendar.SECOND)));

        return schAlarm;
    }
}
