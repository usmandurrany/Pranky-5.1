package com.fournodes.ud.pranky;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.models.ItemSelected;
import com.fournodes.ud.pranky.receivers.PlayPrank;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Usman on 11/5/2015.
 */
public class SetPrank {
    Calendar schAlarm;
    private Context context;
    private int day, hr, min, sec, ampm;
    private Type type;
    private int duration;

    public SetPrank(Context context, int day, int hr, int min, int sec, int ampm, Type type,int duration) {
        this.context = context;
        this.day = day;
        this.min = min;
        this.sec=sec;
        this.ampm = ampm;
        this.type=type;
        this.duration=duration;
        if (type == Type.ClockDialogActivity)
            this.hr = (hr + 1);
        else
            this.hr=hr;
        Log.e("Set Prank",String.valueOf(duration));
    }

    public void get24HrTime() {
        if (ampm == 0) {
            if (hr == 12)
                hr = 0;
        } else {
            if (hr == 12)
                hr = 12;
            else
                hr = hr + 12;
        }

    }


    public Calendar clockSchedule() {

        get24HrTime();
        schAlarm = Calendar.getInstance(TimeZone.getDefault());


        //schAlarm.setTimeInMillis(System.currentTimeMillis());
        schAlarm.set(Calendar.DAY_OF_MONTH, (schAlarm.get(Calendar.DAY_OF_MONTH) + day));
        schAlarm.set(Calendar.HOUR_OF_DAY, hr);
        schAlarm.set(Calendar.MINUTE, min);
        schAlarm.set(Calendar.SECOND, 0);

        // schAlarm.set(Calendar.AM_PM, ampm);

        Log.d("SCHEDULED FOR",
                String.valueOf(schAlarm.get(Calendar.YEAR)) +
                        " " + String.valueOf(schAlarm.get(Calendar.MONTH)) +
                        " " + String.valueOf(schAlarm.get(Calendar.DAY_OF_MONTH)) +
                        " " + String.valueOf(schAlarm.get(Calendar.HOUR)) +
                        " " + String.valueOf(schAlarm.get(Calendar.MINUTE)) +
                        " " + String.valueOf(schAlarm.get(Calendar.AM_PM)));


        return schAlarm;


    }

    public Calendar timerSchedule() {
        schAlarm = Calendar.getInstance(TimeZone.getDefault());

        schAlarm.setTimeInMillis(System.currentTimeMillis());
        Log.e("Calendar Hour", String.valueOf(schAlarm.get(Calendar.HOUR) + hr));
        schAlarm.set(Calendar.HOUR, (schAlarm.get(Calendar.HOUR) + hr));
        schAlarm.set(Calendar.MINUTE, (schAlarm.get(Calendar.MINUTE) + min));
        schAlarm.set(Calendar.SECOND, (schAlarm.get(Calendar.SECOND) + sec));

        Log.e("SCHEDULED FOR",
                String.valueOf(schAlarm.get(Calendar.YEAR)) +
                        " " + String.valueOf(schAlarm.get(Calendar.MONTH)) +
                        " " + String.valueOf(schAlarm.get(Calendar.DAY_OF_MONTH)) +
                        " " + String.valueOf(schAlarm.get(Calendar.HOUR)) +
                        " " + String.valueOf(schAlarm.get(Calendar.MINUTE)) +
                        " " + String.valueOf(schAlarm.get(Calendar.SECOND)));

        return schAlarm;
    }

    public void ScheduleSoundPlayback(Calendar schAlarm) {
        Intent intent = new Intent(context, PlayPrank.class);
        if (ItemSelected.itemSound != 0)
            intent.putExtra("sysSound", ItemSelected.itemSound);
        intent.putExtra("cusSound", ItemSelected.itemCustomSound);
        intent.putExtra("repeatCount", ItemSelected.itemRepeatCount);
        intent.putExtra("volume", (int) ItemSelected.itemVolume);
        intent.putExtra("duration", duration);

       // Toast.makeText(context, String.valueOf(Sound.itemRepeatCount), Toast.LENGTH_SHORT).show();


        final int _id = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id,
                intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, schAlarm.getTimeInMillis(), pendingIntent);

        SharedPrefs.setPranksLeft(SharedPrefs.getPranksLeft()-1);

        //Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();

    }

    public boolean validateTime(Calendar cal) {
        if (type == Type.ClockDialogActivity) {
            Calendar current = Calendar.getInstance(TimeZone.getDefault());
            Log.d("Current Time", current.getTime().toString());
            Log.d("SCH Time", cal.getTime().toString());

            return current.before(cal);

        } else if (type == Type.TimerDialogActivity) {
            return !(hr == 0 && min == 0 && sec < 5);
        }

        return false;
    }


}
