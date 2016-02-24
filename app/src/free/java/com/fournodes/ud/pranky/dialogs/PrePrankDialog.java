package com.fournodes.ud.pranky.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fournodes.ud.pranky.CustomTextView;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.enums.Action;
import com.fournodes.ud.pranky.interfaces.Messenger;
import com.fournodes.ud.pranky.network.AppServerConn;

/**
 * Created by Usman on 30/1/2016.
 */
public class PrePrankDialog {
    public Messenger delegate = null;

    private Context context;
    private Dialog dialog;
    private ImageView waitAnim;
    private Bitmap bitmap;
    private Canvas canvas;
    private int height;
    private int width;
    private CountDownTimer prankCountDown;
    private Paint backgroundPaint;
    private Paint progressPaint;

    private long startTime;
    private long currentTime;
    private long maxTime;

    private long progressMillisecond;
    private double progress;

    private RectF circleBounds;
    private float radius;
    private float handleRadius;


    public PrePrankDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.ClockDialog);
        dialog.setContentView(R.layout.dialog_pre_prank);
    }


    public void show() {


        ImageView wait = (ImageView) dialog.findViewById(R.id.imgWait);
        AnimationDrawable boxsel = (AnimationDrawable) wait.getDrawable();
        boxsel.start();

        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        //container.addView(new CircularCountdown(context));

        ImageView cancel = (ImageView) dialog.findViewById(R.id.btnCancelPrank);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                prankCountDown.cancel();
            }
        });
        waitAnim=(ImageView) dialog.findViewById(R.id.waitAnim);
        waitAnim.post(new Runnable() {
            @Override
            public void run() {
                height = waitAnim.getDrawable().getBounds().height();
                width = waitAnim.getDrawable().getBounds().width();
                Log.e("Logo","Width "+ String.valueOf(width)+" Height "+String.valueOf(height));
                drawCircle();


            }
        });

        dialog.setCancelable(false);

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

    public void setFriendName(String s){
        CustomTextView friendName = (CustomTextView) dialog.findViewById(R.id.txtFriendName);
        friendName.setText(s);
    }


    public void drawCircle(){


        // used to fit the circle into
        circleBounds = new RectF();

        // size of circle and handle
        radius = dipsToPixels(100);
        handleRadius = 10;

        // limit the counter to go up to maxTime ms
        maxTime = 3000;

        // start and current time
        startTime = System.currentTimeMillis();
        currentTime = startTime;


        // the style of the background
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStrokeWidth(15);
        backgroundPaint.setStrokeCap(Paint.Cap.SQUARE);
        backgroundPaint.setColor(Color.parseColor("#4D4D4D"));  // dark gray

        // the style of the 'progress'
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(10);
        progressPaint.setStrokeCap(Paint.Cap.SQUARE);
        progressPaint.setColor(Color.parseColor("#bf071a"));

        // the style for the text in the middle
        Paint textPaint = new TextPaint();
        textPaint.setTextSize(radius / 2);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // text attributes
        float textHeight = textPaint.descent() - textPaint.ascent();
        float textOffset = (textHeight / 2) - textPaint.descent();


        prankCountDown =  new CountDownTimer(3000,16) {
            @Override
            public void onTick(long l) {
                currentTime = System.currentTimeMillis();

                // get elapsed time in milliseconds and clamp between <0, maxTime>
                progressMillisecond = (currentTime - startTime) % maxTime;

                // get current progress on a range <0, 1>
                progress = (double) progressMillisecond / maxTime;
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);


                canvas = new Canvas(bitmap);        // get the center of the view
                float centerWidth = canvas.getWidth() / 2;
                float centerHeight = canvas.getHeight() / 2;


                // set bound of our circle in the middle of the view
                circleBounds.set(centerWidth - radius,
                        centerHeight - radius,
                        centerWidth + radius,
                        centerHeight + radius);


                // draw background circle
                canvas.drawCircle(centerWidth, centerHeight, radius, backgroundPaint);

                // we want to start at -90°, 0° is pointing to the right
                canvas.drawArc(circleBounds, -90, (float)(progress*360), false, progressPaint);

                // display text inside the circle
               /* canvas.drawText((double)(progressMillisecond/100)/10 + "s",
                        centerWidth,
                        centerHeight + textOffset,
                        textPaint);*/

                // draw handle or the circle
                canvas.drawCircle((float)(centerWidth  + (Math.sin(progress * 2 * Math.PI) * radius)),
                        (float)(centerHeight - (Math.cos(progress * 2 * Math.PI) * radius)),
                        handleRadius,
                        progressPaint);


                waitAnim.setImageBitmap(bitmap);


            }

            @Override
            public void onFinish() {
                AppServerConn appServerConn = new AppServerConn(context, Action.PlayPrank);
                appServerConn.execute();
                //((MainActivity)context).showAd();
                delegate.showPranksLeft();
                dialog.dismiss();

            }
        }.start();


    }

    private int dipsToPixels(int dips) {
        final float scale = context.getResources().getDisplayMetrics().density;
        //Toast.makeText(MainActivity.this, String.valueOf(scale),Toast.LENGTH_SHORT).show();
        return (int) (dips * scale + 0.5f);
    }



}
