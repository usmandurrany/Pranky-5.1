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
    private int day,hr,min,sec,ampm;
    private int sound;

    public SoundScheduler(Context context) {
        this.context=context;

    }
    public SoundScheduler(Context context, int day, int hr, int min, int ampm,int sound){
        this.context=context;
        this.day =day;
        this.hr=hr;
        this.min=min;
        this.ampm=ampm;
        this.sound=sound;
    }
    public SoundScheduler(Context context, int hr, int min, int sec,int sound){
        this.context=context;
        this.hr=hr;
        this.min=min;
        this.sec=sec;
        this.sound=sound;
    }

    public Calendar clockSchedule(){

        schAlarm = Calendar.getInstance(TimeZone.getDefault());


    schAlarm.setTimeInMillis(System.currentTimeMillis());

    schAlarm.set(Calendar.HOUR, (hr + 1));
    schAlarm.set(Calendar.MINUTE, min);
        schAlarm.set(Calendar.SECOND, 00);

        schAlarm.set(Calendar.AM_PM, ampm);
    schAlarm.set(Calendar.DAY_OF_MONTH, (schAlarm.get(Calendar.DAY_OF_MONTH) + day));
    Log.d("SCHEDULED FOR", String.valueOf(schAlarm.get(Calendar.YEAR)) + " " + String.valueOf(schAlarm.get(Calendar.MONTH)) + " " + String.valueOf(schAlarm.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(schAlarm.get(Calendar.HOUR)) + " " + String.valueOf(schAlarm.get(Calendar.MINUTE)) + " " + String.valueOf(schAlarm.get(Calendar.AM_PM)));



        return schAlarm;




    }

    public Calendar timerSchedule(){
        schAlarm = Calendar.getInstance(TimeZone.getDefault());

        schAlarm.setTimeInMillis(System.currentTimeMillis());
        Log.e("Calendar Hour", String.valueOf(schAlarm.get(Calendar.HOUR) + hr));
        schAlarm.set(Calendar.HOUR, (schAlarm.get(Calendar.HOUR) + hr));
        schAlarm.set(Calendar.MINUTE, (schAlarm.get(Calendar.MINUTE)+ min));
        schAlarm.set(Calendar.SECOND, (schAlarm.get(Calendar.SECOND) + sec));
        Log.e("SCHEDULED FOR", String.valueOf(schAlarm.get(Calendar.YEAR)) + " " + String.valueOf(schAlarm.get(Calendar.MONTH)) + " " + String.valueOf(schAlarm.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(schAlarm.get(Calendar.HOUR)) + " " + String.valueOf(schAlarm.get(Calendar.MINUTE)) + " " + String.valueOf(schAlarm.get(Calendar.SECOND)));

        return schAlarm;
    }

    public void ScheduleSoundPlayback(String type, Calendar schAlarm){
        Intent intent = new Intent(context, PlaySound.class);
        intent.putExtra("Sound", context.getResources().getResourceEntryName(sound));
        Toast.makeText(context, context.getResources().getResourceName(sound), Toast.LENGTH_SHORT).show();
        final int _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id,
                intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        if (type=="clock")
            alarmManager.set(AlarmManager.RTC_WAKEUP,schAlarm.getTimeInMillis() , pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, schAlarm.getTimeInMillis(), pendingIntent);

        Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();

    }


}
