package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created by Usman on 11/6/2015.
 */
public class ClockDialog {
    private Context context;
    private Dialog dialog;
    private int clockDay, clockHour,clockMin,clockampm; //0 for am 1 for pm
    private int sound;
    private String soundCus;
    public int soundVol;
    public int soundRepeat;


    public ClockDialog(Context context, int sound,String soundCus, int soundRepeat,int soundVol ){
        this.context=context;
        this.sound=sound;
        this.soundCus=soundCus;
        this.soundRepeat=soundRepeat;
        this.soundVol=soundVol;
    }


    public void show(){


                dialog = new Dialog(context, R.style.ClockDialog);
                dialog.setContentView(R.layout.dialog_clock);

                //Array for the am/pm marker column
                String[] ampmArray = {"AM","PM"};
                //With a custom method I get the next following 10 days from now
                ArrayList<Date> days = getNextNumberOfDays(new Date(), 10);


                //Configure Days Column
                final WheelView day = (WheelView) dialog.findViewById(R.id.day);
                day.setViewAdapter(new DayWheelAdapter(context, days));

                //Configure Hours Column
                final WheelView hour = (WheelView) dialog.findViewById(R.id.hour);
                NumericWheelAdapter hourAdapter = new NumericWheelAdapter(context, 1, 12);
                hourAdapter.setItemResource(R.layout.wheel_item_time);
                hourAdapter.setItemTextResource(R.id.time_item);
                hour.setViewAdapter(hourAdapter);

                //Configure Minutes Column
                final WheelView min = (WheelView) dialog.findViewById(R.id.minute);
                NumericWheelAdapter minAdapter = new NumericWheelAdapter(context, 00, 59);
                minAdapter.setItemResource(R.layout.wheel_item_time);
                minAdapter.setItemTextResource(R.id.time_item);
                min.setViewAdapter(minAdapter);

                //Configure am/pm Marker Column
                final WheelView ampm = (WheelView) dialog.findViewById(R.id.ampm);
                ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(context, ampmArray);
                ampmAdapter.setItemResource(R.layout.wheel_item_time);
                ampmAdapter.setItemTextResource(R.id.time_item);
                ampm.setViewAdapter(ampmAdapter);

                ImageView close = (ImageView) dialog.findViewById(R.id.close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                ImageView clockset = (ImageView) dialog.findViewById(R.id.set);
                clockset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, String.valueOf(ampm.getCurrentItem()), Toast.LENGTH_SHORT).show();
                        clockDay = day.getCurrentItem();
                        clockHour = hour.getCurrentItem();
                        clockMin = min.getCurrentItem();
                        clockampm = ampm.getCurrentItem();
                        SoundScheduler scheduler = new SoundScheduler(context, clockDay, clockHour, clockMin, clockampm, sound,soundCus,soundRepeat,soundVol);
                        if (scheduler.validateTime(scheduler.clockSchedule(),"dialog_clock")){
                            scheduler.ScheduleSoundPlayback("dialog_clock", scheduler.clockSchedule());
                        dialog.dismiss();
                    }
                        else {
                            CustomToast cToast = new CustomToast(context,"Selected time has passed.");
                            cToast.show();
                        }

                    }
                });

        //Set the dialog to not focusable (makes navigation ignore us adding the window)
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Show the dialog!
        dialog.show();

        //Set the dialog to immersive
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                ((Activity) context).getWindow().getDecorView().getSystemUiVisibility());

        //Clear the not focusable flag from the window
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


    }

    public static ArrayList<Date> getNextNumberOfDays(Date originalDate,int days){
        ArrayList<Date> dates = new ArrayList<Date>();
        long offset;
        for(int i= 0; i<= days; i++){
            offset = 86400 * 1000L * i;
            Date date = new Date( originalDate.getTime()+offset);
            dates.add(date);
        }
        return dates;
    }


}
