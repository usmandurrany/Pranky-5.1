package com.fournodes.ud.pranky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created by Usman on 11/6/2015.
 */
public class TimerDialog {

    private Context context;
    private Dialog dialog;
    private int sound;
    private String soundCus;


    public TimerDialog(Context context, int sound) {
        this.context = context;
        this.sound=sound;
    }

    public TimerDialog(Context context, String soundCus) {
        this.context=context;
        this.soundCus=soundCus;
    }

    public void show(){

                dialog = new Dialog(context, R.style.ClockDialog);
                dialog.setContentView(R.layout.dialog_timer);


                //Configure Hours Column
                final WheelView hour = (WheelView) dialog.findViewById(R.id.timerhour);
                NumericWheelAdapter hourAdapter = new NumericWheelAdapter(context, 0, 12);
                hourAdapter.setItemResource(R.layout.wheel_item_time);
                hourAdapter.setItemTextResource(R.id.time_item);
                hour.setViewAdapter(hourAdapter);

                //Configure Minutes Column
                final WheelView min = (WheelView) dialog.findViewById(R.id.timerminute);
                NumericWheelAdapter minAdapter = new NumericWheelAdapter(context, 00, 59);
                minAdapter.setItemResource(R.layout.wheel_item_time);
                minAdapter.setItemTextResource(R.id.time_item);
                min.setViewAdapter(minAdapter);

                //Configure Seconds Marker Column
                final WheelView sec = (WheelView) dialog.findViewById(R.id.timersec);
                NumericWheelAdapter secAdapter = new NumericWheelAdapter(context, 00, 59);
                secAdapter.setItemResource(R.layout.wheel_item_time);
                secAdapter.setItemTextResource(R.id.time_item);
                sec.setViewAdapter(minAdapter);
                ImageView close = (ImageView) dialog.findViewById(R.id.timerclose);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                ImageView set = (ImageView) dialog.findViewById(R.id.timerset);
                set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SoundScheduler scheduler = new SoundScheduler(context,hour.getCurrentItem(),min.getCurrentItem(),sec.getCurrentItem(),sound,soundCus);

                        if (scheduler.validateTime(scheduler.timerSchedule(),"dialog_timer")){
                            scheduler.ScheduleSoundPlayback("dialog_timer", scheduler.timerSchedule());
                            dialog.dismiss();
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


    }


